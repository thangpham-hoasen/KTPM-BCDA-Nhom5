package vn.edu.hoasen.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.PostConstruct;
import vn.edu.hoasen.controller.StudentController;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.service.MessageService;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Students")
public class StudentView extends VerticalLayout {
    
    private final StudentController studentController;
    private final MessageService messageService;
    
    private Grid<Student> grid = new Grid<>(Student.class);
    private TextField nameField;
    private DatePicker birthDateField;
    private TextField parentNameField;
    private TextField parentPhoneField;
    private TextField classNameField;
    private TextField searchField;
    private Student editingStudent;
    private Button addButton;
    private Button cancelButton;
    private Button newButton;
    private VerticalLayout formSection;
    private Binder<Student> binder;
    
    public StudentView(StudentController studentController, MessageService messageService) {
        this.studentController = studentController;
        this.messageService = messageService;
    }
    
    @PostConstruct
    public void init() {
        initFields();
        setupLayout();
        refreshGrid();
    }
    
    private void initFields() {
        nameField = new TextField(messageService.getMessage("student.name"));
        nameField.setId("name-field");
        birthDateField = new DatePicker(messageService.getMessage("student.birthDate"));
        birthDateField.setId("birth-date-field");
        parentNameField = new TextField(messageService.getMessage("student.parentName"));
        parentNameField.setId("parent-name-field");
        parentPhoneField = new TextField(messageService.getMessage("student.parentPhone"));
        parentPhoneField.setId("parent-phone-field");
        classNameField = new TextField(messageService.getMessage("student.className"));
        classNameField.setId("class-name-field");
        classNameField.setReadOnly(true);
        searchField = new TextField(messageService.getMessage("common.search"));
        searchField.setId("search-field");
        
        java.util.Locale currentLocale = messageService.getCurrentLanguage().equals("vi") ? 
            new java.util.Locale("vi", "VN") : java.util.Locale.ENGLISH;
        birthDateField.setLocale(currentLocale);

        // Auto suggest class name from birth date (BR-02)
        birthDateField.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                classNameField.clear();
                return;
            }
            try {
                Student tmp = new Student();
                tmp.setBirthDate(e.getValue());
                // suggestClass() returns message key (class.mam/class.choi/class.la)
                String classKey = tmp.suggestClass();
                classNameField.setValue(messageService.getMessage(classKey));
            } catch (Exception ex) {
                // Not eligible => clear so user sees it's invalid
                classNameField.clear();
            }
        });
        
        setupValidation();
    }
    
    private void setupValidation() {
        binder = new Binder<>(Student.class);
        
        binder.forField(nameField)
            .withValidator(new StringLengthValidator(messageService.getMessage("validation.name.length"), 2, 50))
            .bind(Student::getName, Student::setName);
            
        binder.forField(birthDateField)
            .asRequired(messageService.getMessage("validation.required"))
            .bind(Student::getBirthDate, Student::setBirthDate);
            
        binder.forField(parentNameField)
            .withValidator(new StringLengthValidator(messageService.getMessage("validation.name.length"), 2, 50))
            .bind(Student::getParentName, Student::setParentName);
            
        binder.forField(parentPhoneField)
            .withValidator(phone -> phone.matches("^0[0-9]{9,10}$"), messageService.getMessage("validation.phone.invalid"))
            .bind(Student::getParentPhone, Student::setParentPhone);

        // className được gợi ý tự động theo Ngày sinh (BR-02) và được set lại ở backend (StudentService)
        // => field này chỉ để hiển thị, không bind vào model
    }
    
    private void setupLayout() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        H1 title = new H1(messageService.getMessage("student.title"));
        
        searchField.setPlaceholder(messageService.getMessage("student.search"));
        searchField.setWidth("300px");
        Button searchButton = new Button(messageService.getMessage("common.search"), e -> searchStudents());
        searchButton.setId("search-button");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button clearSearchButton = new Button(messageService.getMessage("common.showAll"), e -> refreshGrid());
        clearSearchButton.setId("show-all-button");
        
        newButton = new Button(messageService.getMessage("common.addNew"), e -> showForm());
        newButton.setId("new-button");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton, clearSearchButton, newButton);
        searchLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        searchLayout.setWidthFull();
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, birthDateField, parentNameField, parentPhoneField, classNameField);
        
        addButton = new Button(messageService.getMessage("student.add"), e -> saveStudent());
        addButton.setId("add-button");
        cancelButton = new Button(messageService.getMessage("common.cancel"), e -> cancelEdit());
        cancelButton.setId("cancel-button");
        cancelButton.setVisible(false);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        formSection = new VerticalLayout(formLayout, buttonLayout);
        formSection.setVisible(false);
        
        setupGrid();
        
        add(title, searchLayout, formSection, new Hr(), grid);
    }
    
    private void setupGrid() {
        grid.removeAllColumns();
        
        grid.addColumn(Student::getName).setHeader(messageService.getMessage("student.name"));
        grid.addColumn(student -> student.getBirthDate() != null ? 
            student.getBirthDate().format(messageService.getCurrentDateTimeFormatter()) : "")
            .setHeader(messageService.getMessage("student.birthDate"));
        grid.addColumn(Student::getParentName).setHeader(messageService.getMessage("student.parentName"));
        grid.addColumn(Student::getParentPhone).setHeader(messageService.getMessage("student.parentPhone"));
        grid.addColumn(student -> student.getClassName() != null ?
            messageService.getMessage(student.getClassName()) : "")
            .setHeader(messageService.getMessage("student.className"));
        
        grid.addComponentColumn(student -> {
            Button editButton = new Button(messageService.getMessage("common.edit"), e -> editStudent(student));
            editButton.setId("edit-button-" + student.getId());
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            
            Button deleteButton = new Button(messageService.getMessage("common.delete"), e -> deleteStudent(student));
            deleteButton.setId("delete-button-" + student.getId());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(messageService.getMessage("student.actions"));
        
        grid.setId("student-grid");
    }
    
    private void saveStudent() {
        try {
            Student student = editingStudent != null ? editingStudent : new Student();
            binder.writeBean(student);
            
            if (student.getBirthDate() != null) {
                int ageInMonths = student.getAgeInMonths();
                if (ageInMonths < 18 || ageInMonths > 60) {
                    Notification.show(messageService.getMessage("validation.age.invalid"));
                    return;
                }
            }
            
            if (editingStudent != null) {
                studentController.updateStudent(student);
            } else {
                studentController.saveStudent(student);
            }
            refreshGrid();
            hideForm();
            addButton.setText(messageService.getMessage("student.add"));
            cancelButton.setVisible(false);
            editingStudent = null;
        } catch (ValidationException e) {
            Notification.show(messageService.getMessage("error.validation.failed"));
        } catch (Exception e) {
            Notification.show(e.getMessage()); // Show business error (e.g. Class full)
        }
    }
    
    private void showForm() {
        formSection.setVisible(true);
        newButton.setVisible(false);
        cancelButton.setVisible(true);
        nameField.focus();
    }
    
    private void hideForm() {
        formSection.setVisible(false);
        newButton.setVisible(true);
        clearForm();
    }
    
    private void editStudent(Student student) {
        editingStudent = student;
        binder.readBean(student);

        // className không bind, set thủ công để hiển thị
        if (student.getClassName() != null) {
            classNameField.setValue(messageService.getMessage(student.getClassName()));
        } else {
            classNameField.clear();
        }
        
        formSection.setVisible(true);
        newButton.setVisible(false);
        addButton.setText(messageService.getMessage("common.save"));
        cancelButton.setVisible(true);
    }
    
    private void cancelEdit() {
        editingStudent = null;
        addButton.setText(messageService.getMessage("student.add"));
        cancelButton.setVisible(false);
        hideForm();
    }


    
    private void deleteStudent(Student student) {
        showConfirmDialog(
            messageService.getMessage("confirm.delete.title"),
            messageService.getMessage("confirm.delete.message"),
            () -> {
                studentController.deleteStudent(student.getId());
                refreshGrid();
                Notification.show(messageService.getMessage("login.success"));
            }
        );
    }
    
    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle(title);
        
        com.vaadin.flow.component.html.Div content = new com.vaadin.flow.component.html.Div();
        content.setText(message);
        dialog.add(content);
        
        Button confirmButton = new Button(messageService.getMessage("confirm.yes"), e -> {
            onConfirm.run();
            dialog.close();
        });
        confirmButton.setId("confirm-yes-button");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button(messageService.getMessage("confirm.no"), e -> dialog.close());
        cancelButton.setId("confirm-no-button");
        
        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }
    
    private void searchStudents() {
        grid.setItems(studentController.searchStudents(searchField.getValue()));
    }
    
    private void refreshGrid() {
        if (studentController != null) {
            grid.setItems(studentController.getAllStudents());
        }
    }
    
    private void clearForm() {
        nameField.clear();
        birthDateField.clear();
        parentNameField.clear();
        parentPhoneField.clear();
        classNameField.clear();
        nameField.focus();
    }
}