package vn.edu.hoasen.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.PostConstruct;
import vn.edu.hoasen.controller.CourseController;
import vn.edu.hoasen.model.Course;
import vn.edu.hoasen.service.MessageService;
import vn.edu.hoasen.component.ConfigurationButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;

@Route(value = "courses", layout = MainLayout.class)
@PageTitle("Courses")
public class CourseView extends VerticalLayout {
    
    private final CourseController courseController;
    private final MessageService messageService;
    private final ConfigurationButton configButton;
    
    private Grid<Course> grid = new Grid<>(Course.class, false);
    private TextField nameField;
    private TextArea descriptionField;
    private TextField teacherNameField;
    private IntegerField durationField;
    private TextField scheduleField;
    private TextField searchField;
    private Course editingCourse;
    private Button addButton;
    private Button cancelButton;
    private Button newButton;
    private VerticalLayout formSection;
    private Binder<Course> binder;
    
    public CourseView(CourseController courseController, MessageService messageService, ConfigurationButton configButton) {
        this.courseController = courseController;
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
        nameField = new TextField(messageService.getMessage("course.name"));
        nameField.setId("name-field");
        descriptionField = new TextArea(messageService.getMessage("course.description"));
        descriptionField.setId("description-field");
        teacherNameField = new TextField(messageService.getMessage("course.teacherName"));
        teacherNameField.setId("teacher-name-field");
        durationField = new IntegerField(messageService.getMessage("course.duration"));
        durationField.setId("duration-field");
        scheduleField = new TextField(messageService.getMessage("course.schedule"));
        scheduleField.setId("schedule-field");
        searchField = new TextField(messageService.getMessage("common.search"));
        searchField.setId("search-field");
        
        setupValidation();
    }
    
    private void setupValidation() {
        binder = new Binder<>(Course.class);
        
        binder.forField(nameField)
            .withValidator(new StringLengthValidator(messageService.getMessage("validation.name.length"), 2, 100))
            .bind(Course::getName, Course::setName);
            
        binder.forField(descriptionField)
            .bind(Course::getDescription, Course::setDescription);
            
        binder.forField(teacherNameField)
            .asRequired(messageService.getMessage("validation.required"))
            .bind(Course::getTeacherName, Course::setTeacherName);
            
        binder.forField(durationField)
            .withValidator(duration -> duration != null && duration > 0, messageService.getMessage("validation.duration.invalid"))
            .bind(Course::getDuration, Course::setDuration);
            
        binder.forField(scheduleField)
            .asRequired(messageService.getMessage("validation.required"))
            .bind(Course::getSchedule, Course::setSchedule);
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
        
        H1 title = new H1(messageService.getMessage("course.title"));
        title.getStyle().set("margin-bottom", "20px");
        
        searchField.setPlaceholder(messageService.getMessage("course.search"));
        searchField.setWidth("300px");
        Button searchButton = new Button(messageService.getMessage("common.search"), e -> searchCourses());
        searchButton.setId("search-button");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button clearSearchButton = new Button(messageService.getMessage("common.showAll"), e -> refreshGrid());
        clearSearchButton.setId("show-all-button");
        
        newButton = new Button(messageService.getMessage("common.addNew"), e -> showForm());
        newButton.setId("new-button");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton, clearSearchButton, newButton);
        searchLayout.setAlignItems(Alignment.END);
        searchLayout.getStyle().set("margin-bottom", "20px");
        
        FormLayout formLayout = new FormLayout();
        descriptionField.setHeight("80px");
        formLayout.add(nameField, descriptionField, teacherNameField, durationField, scheduleField);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        
        addButton = new Button(messageService.getMessage("course.add"), e -> saveCourse());
        addButton.setId("add-button");
        cancelButton = new Button(messageService.getMessage("common.cancel"), e -> cancelEdit());
        cancelButton.setId("cancel-button");
        cancelButton.setVisible(false);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        addButton.getStyle().set("margin-top", "10px");
        
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        formSection = new VerticalLayout(formLayout, buttonLayout);
        formSection.setSpacing(false);
        formSection.setPadding(true);
        formSection.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                              .set("border-radius", "var(--lumo-border-radius-m)")
                              .set("margin-bottom", "20px");
        formSection.setVisible(false);
        
        setupGrid();
        
        mainContent.add(title, searchLayout, formSection, new Hr(), grid);
        
        // Create horizontal layout with sidebar and main content
        HorizontalLayout mainLayout = new HorizontalLayout(mainContent);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);
        
        add(mainLayout, configButton);
    }
    
    private void setupGrid() {
        grid.addColumn(Course::getName).setHeader(messageService.getMessage("course.name")).setKey("name").setWidth("150px");
        grid.addColumn(Course::getDescription).setHeader(messageService.getMessage("course.description")).setKey("description").setWidth("200px");
        grid.addColumn(Course::getTeacherName).setHeader(messageService.getMessage("course.teacherName")).setKey("teacherName").setWidth("120px");
        grid.addColumn(Course::getDuration).setHeader(messageService.getMessage("course.duration")).setKey("duration").setWidth("100px");
        grid.addColumn(Course::getSchedule).setHeader(messageService.getMessage("course.schedule")).setKey("schedule").setWidth("120px");
        grid.addColumn(course -> course.getUpdatedAt() != null ? 
            course.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "")
            .setHeader("Last Updated").setKey("updatedAt").setWidth("150px");
        
        grid.addComponentColumn(course -> {
            Button editButton = new Button(messageService.getMessage("common.edit"), e -> editCourse(course));
            editButton.setId("edit-button-" + course.getId());
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            
            Button deleteButton = new Button(messageService.getMessage("common.delete"), e -> deleteCourse(course));
            deleteButton.setId("delete-button-" + course.getId());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(messageService.getMessage("course.actions")).setWidth("150px");
        
        grid.setHeight("400px");
        grid.setId("course-grid");
    }
    
    private void saveCourse() {
        Course course = editingCourse != null ? editingCourse : new Course();
        
        if (binder.writeBeanIfValid(course)) {
            showConfirmDialog(
                messageService.getMessage("confirm.save.title"),
                messageService.getMessage("confirm.save.message"),
                () -> {
                    if (editingCourse != null) {
                        courseController.updateCourse(course);
                    } else {
                        courseController.saveCourse(course);
                    }
                    refreshGrid();
                    hideForm();
                    addButton.setText(messageService.getMessage("course.add"));
                    cancelButton.setVisible(false);
                    editingCourse = null;
                }
            );
        } else {
            Notification.show(messageService.getMessage("validation.error"), 3000, Notification.Position.MIDDLE);
        }
    }
    
    private void showForm() {
        formSection.setVisible(true);
        newButton.setVisible(false);
        nameField.focus();
    }
    
    private void hideForm() {
        formSection.setVisible(false);
        newButton.setVisible(true);
        clearForm();
    }
    
    private void editCourse(Course course) {
        editingCourse = course;
        nameField.setValue(course.getName() != null ? course.getName() : "");
        descriptionField.setValue(course.getDescription() != null ? course.getDescription() : "");
        teacherNameField.setValue(course.getTeacherName() != null ? course.getTeacherName() : "");
        durationField.setValue(course.getDuration());
        scheduleField.setValue(course.getSchedule() != null ? course.getSchedule() : "");
        
        formSection.setVisible(true);
        newButton.setVisible(false);
        addButton.setText(messageService.getMessage("common.save"));
        cancelButton.setVisible(true);
    }
    
    private void cancelEdit() {
        editingCourse = null;
        addButton.setText(messageService.getMessage("course.add"));
        cancelButton.setVisible(false);
        hideForm();
    }
    
    private void deleteCourse(Course course) {
        showConfirmDialog(
            messageService.getMessage("confirm.delete.title"),
            messageService.getMessage("confirm.delete.message"),
            () -> {
                courseController.deleteCourse(course.getId());
                refreshGrid();
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
    
    private void searchCourses() {
        grid.setItems(courseController.searchCourses(searchField.getValue()));
    }
    
    private void refreshGrid() {
        grid.setItems(courseController.getAllCourses());
    }
    
    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        teacherNameField.clear();
        durationField.clear();
        scheduleField.clear();
        nameField.focus();
    }
}