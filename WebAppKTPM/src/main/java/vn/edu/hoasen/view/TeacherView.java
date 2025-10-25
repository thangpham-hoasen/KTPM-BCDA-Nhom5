package vn.edu.hoasen.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.PostConstruct;
import vn.edu.hoasen.controller.TeacherController;
import vn.edu.hoasen.model.Teacher;
import vn.edu.hoasen.service.MessageService;
import vn.edu.hoasen.component.ConfigurationButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;

@Route(value = "teachers", layout = MainLayout.class)
@PageTitle("Teachers")
public class TeacherView extends VerticalLayout {
    
    private final TeacherController teacherController;
    private final MessageService messageService;
    private final ConfigurationButton configButton;
    private Grid<Teacher> grid = new Grid<>(Teacher.class);
    private TextField nameField;
    private EmailField emailField;
    private TextField phoneField;
    private TextField subjectField;
    private DatePicker hireDateField;
    private TextField searchField;
    private Teacher editingTeacher;
    private Button addButton;
    private Button cancelButton;
    private Binder<Teacher> binder;
    
    public TeacherView(TeacherController teacherController, MessageService messageService, ConfigurationButton configButton) {
        this.teacherController = teacherController;
        this.messageService = messageService;
        this.configButton = configButton;
    }
    
    @PostConstruct
    public void init() {
        initFields();
        setupLayout();
        refreshGrid();
    }
    
    private void initFields() {
        nameField = new TextField(messageService.getMessage("teacher.name"));
        emailField = new EmailField(messageService.getMessage("teacher.email"));
        phoneField = new TextField(messageService.getMessage("teacher.phone"));
        subjectField = new TextField(messageService.getMessage("teacher.subject"));
        hireDateField = new DatePicker(messageService.getMessage("teacher.hireDate"));
        searchField = new TextField(messageService.getMessage("common.search"));
        
        // Set locale for date picker
        java.util.Locale currentLocale = messageService.getCurrentLocale();
        hireDateField.setLocale(currentLocale);
        
        setupValidation();
    }
    
    private void setupValidation() {
        binder = new Binder<>(Teacher.class);
        
        binder.forField(nameField)
            .withValidator(new StringLengthValidator(messageService.getMessage("validation.name.length"), 2, 50))
            .bind(Teacher::getName, Teacher::setName);
            
        binder.forField(emailField)
            .withValidator(new EmailValidator(messageService.getMessage("validation.email.invalid")))
            .bind(Teacher::getEmail, Teacher::setEmail);
            
        binder.forField(phoneField)
            .withValidator(phone -> phone.matches("^[0-9]{10,11}$"), messageService.getMessage("validation.phone.invalid"))
            .bind(Teacher::getPhone, Teacher::setPhone);
            
        binder.forField(subjectField)
            .asRequired(messageService.getMessage("validation.required"))
            .bind(Teacher::getSubject, Teacher::setSubject);
            
        binder.forField(hireDateField)
            .asRequired(messageService.getMessage("validation.required"))
            .bind(Teacher::getHireDate, Teacher::setHireDate);
    }
    
    private void setupLayout() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        
        // Main content area
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.setPadding(true);
        mainContent.setSpacing(true);
        
        H1 title = new H1(messageService.getMessage("teacher.title"));
        title.getStyle().set("margin-bottom", "20px");
        
        searchField.setPlaceholder(messageService.getMessage("teacher.search"));
        searchField.setWidth("300px");
        Button searchButton = new Button(messageService.getMessage("common.search"), e -> searchTeachers());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button clearSearchButton = new Button(messageService.getMessage("common.showAll"), e -> refreshGrid());
        
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton, clearSearchButton);
        searchLayout.setAlignItems(Alignment.END);
        searchLayout.getStyle().set("margin-bottom", "20px");
        
        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, emailField, phoneField, subjectField, hireDateField);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2),
            new FormLayout.ResponsiveStep("800px", 3)
        );
        
        addButton = new Button(messageService.getMessage("teacher.add"), e -> saveTeacher());
        cancelButton = new Button(messageService.getMessage("common.cancel"), e -> cancelEdit());
        cancelButton.setVisible(false);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addButton.getStyle().set("margin-top", "10px");
        
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        VerticalLayout formSection = new VerticalLayout(formLayout, buttonLayout);
        formSection.setSpacing(false);
        formSection.setPadding(true);
        formSection.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                              .set("border-radius", "var(--lumo-border-radius-m)")
                              .set("margin-bottom", "20px");
        
        setupGrid();
        
        mainContent.add(title, searchLayout, formSection, new Hr(), grid);
        
        // Create horizontal layout with sidebar and main content
        HorizontalLayout mainLayout = new HorizontalLayout(mainContent);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);
        
        add(mainLayout, configButton);
    }
    
    private void setupGrid() {
        grid.removeAllColumns();
        grid.addColumn(Teacher::getName).setHeader(messageService.getMessage("teacher.name")).setWidth("150px");
        grid.addColumn(Teacher::getEmail).setHeader(messageService.getMessage("teacher.email")).setWidth("180px");
        grid.addColumn(Teacher::getPhone).setHeader(messageService.getMessage("teacher.phone")).setWidth("120px");
        grid.addColumn(Teacher::getSubject).setHeader(messageService.getMessage("teacher.subject")).setWidth("120px");
        
        grid.addColumn(teacher -> teacher.getHireDate() != null ? teacher.getHireDate().format(messageService.getCurrentDateTimeFormatter()) : "")
            .setHeader(messageService.getMessage("teacher.hireDate")).setWidth("120px");
        
        grid.addComponentColumn(teacher -> {
            Button editButton = new Button(messageService.getMessage("common.edit"), e -> editTeacher(teacher));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            
            Button deleteButton = new Button(messageService.getMessage("common.delete"), e -> deleteTeacher(teacher));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(messageService.getMessage("teacher.actions")).setWidth("150px");
        
        grid.setHeight("400px");
    }
    
    private void saveTeacher() {
        try {
            Teacher teacher = editingTeacher != null ? editingTeacher : new Teacher();
            binder.writeBean(teacher);
            
            showConfirmDialog(
                messageService.getMessage("confirm.save.title"),
                messageService.getMessage("confirm.save.message"),
                () -> {
                    try {
                        if (editingTeacher != null) {
                            teacherController.updateTeacher(teacher);
                        } else {
                            teacherController.saveTeacher(teacher);
                        }
                        clearForm();
                        refreshGrid();
                        Notification.show(messageService.getMessage("login.success"), 2000, Notification.Position.MIDDLE);
                    } catch (Exception e) {
                        Notification.show(messageService.getMessage("error.save.failed") + ": " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                    }
                }
            );
        } catch (ValidationException e) {
            Notification.show(messageService.getMessage("error.validation.failed"), 3000, Notification.Position.MIDDLE);
        }
    }
    
    private void editTeacher(Teacher teacher) {
        editingTeacher = teacher;
        binder.readBean(teacher);
        
        addButton.setText(messageService.getMessage("common.save"));
        cancelButton.setVisible(true);
    }
    
    private void cancelEdit() {
        editingTeacher = null;
        clearForm();
        addButton.setText(messageService.getMessage("teacher.add"));
        cancelButton.setVisible(false);
    }
    
    private void deleteTeacher(Teacher teacher) {
        showConfirmDialog(
            messageService.getMessage("confirm.delete.title"),
            messageService.getMessage("confirm.delete.message"),
            () -> {
                try {
                    teacherController.deleteTeacher(teacher.getId());
                    refreshGrid();
                    Notification.show(messageService.getMessage("login.success"), 2000, Notification.Position.MIDDLE);
                } catch (Exception e) {
                    Notification.show(messageService.getMessage("error.delete.failed") + ": " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                }
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
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button(messageService.getMessage("confirm.no"), e -> dialog.close());
        
        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }
    
    private void searchTeachers() {
        grid.setItems(teacherController.searchTeachers(searchField.getValue()));
    }
    
    private void refreshGrid() {
        if (teacherController != null) {
            grid.setItems(teacherController.getAllTeachers());
        }
    }
    
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        subjectField.clear();
        hireDateField.clear();
        nameField.focus();
    }
}