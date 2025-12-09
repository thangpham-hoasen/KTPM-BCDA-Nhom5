package vn.edu.hoasen.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.PostConstruct;
import vn.edu.hoasen.controller.AttendanceController;
import vn.edu.hoasen.controller.StudentController;
import vn.edu.hoasen.model.Attendance;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.service.MessageService;
import vn.edu.hoasen.component.ConfigurationButton;

import java.time.LocalDate;
import java.util.List;

@Route(value = "attendance", layout = MainLayout.class)
@PageTitle("Attendance")
public class AttendanceView extends VerticalLayout {
    
    private final AttendanceController attendanceController;
    private final StudentController studentController;
    private final MessageService messageService;
    private final ConfigurationButton configButton;
    
    private Grid<Attendance> grid = new Grid<>(Attendance.class);
    private ComboBox<Student> studentField;
    private DatePicker dateField;
    private ComboBox<Attendance.AttendanceStatus> statusField;
    private TextArea notesField;
    private DatePicker weekStartField;
    private Attendance editingAttendance;
    private Button markButton;
    private Button cancelButton;
    private Button newButton;
    private VerticalLayout formSection;
    
    public AttendanceView(AttendanceController attendanceController, StudentController studentController,
                         MessageService messageService, ConfigurationButton configButton) {
        this.attendanceController = attendanceController;
        this.studentController = studentController;
        this.messageService = messageService;
        this.configButton = configButton;
    }
    
    @PostConstruct
    public void init() {
        initFields();
        setupLayout();
        setupFields();
        showTodayAttendance();
    }
    
    private void initFields() {
        studentField = new ComboBox<>(messageService.getMessage("attendance.student"));
        studentField.setId("student-field");
        dateField = new DatePicker(messageService.getMessage("attendance.date"));
        dateField.setId("date-field");
        statusField = new ComboBox<>(messageService.getMessage("attendance.status"));
        statusField.setId("status-field");
        notesField = new TextArea(messageService.getMessage("attendance.notes"));
        notesField.setId("notes-field");
        weekStartField = new DatePicker(messageService.getMessage("attendance.weekStart"));
        weekStartField.setId("week-start-field");
        
        // Set locale for date pickers
        java.util.Locale currentLocale = messageService.getCurrentLanguage().equals("vi") ? 
            new java.util.Locale("vi", "VN") : java.util.Locale.ENGLISH;
        dateField.setLocale(currentLocale);
        weekStartField.setLocale(currentLocale);
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
        
        H1 title = new H1(messageService.getMessage("attendance.title"));
        title.getStyle().set("margin-bottom", "20px");
        
        weekStartField.setPlaceholder(messageService.getMessage("attendance.weekStart"));
        weekStartField.setWidth("200px");
        Button weeklyReportButton = new Button(messageService.getMessage("attendance.weeklyReport"), e -> showWeeklyReport());
        weeklyReportButton.setId("weekly-report-button");
        weeklyReportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button todayButton = new Button(messageService.getMessage("attendance.todayAttendance"), e -> showTodayAttendance());
        todayButton.setId("today-button");
        
        newButton = new Button(messageService.getMessage("common.addNew"), e -> showForm());
        newButton.setId("new-button");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        
        HorizontalLayout reportLayout = new HorizontalLayout(weekStartField, weeklyReportButton, todayButton, newButton);
        reportLayout.setAlignItems(Alignment.END);
        reportLayout.getStyle().set("margin-bottom", "20px");
        
        FormLayout formLayout = new FormLayout();
        notesField.setHeight("60px");
        formLayout.add(studentField, dateField, statusField, notesField);
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        
        markButton = new Button(messageService.getMessage("attendance.mark"), e -> saveAttendance());
        markButton.setId("mark-button");
        cancelButton = new Button(messageService.getMessage("common.cancel"), e -> cancelEdit());
        cancelButton.setId("cancel-button");
        cancelButton.setVisible(false);
        markButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        markButton.getStyle().set("margin-top", "10px");
        
        HorizontalLayout buttonLayout = new HorizontalLayout(markButton, cancelButton);
        formSection = new VerticalLayout(formLayout, buttonLayout);
        formSection.setSpacing(false);
        formSection.setPadding(true);
        formSection.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                              .set("border-radius", "var(--lumo-border-radius-m)")
                              .set("margin-bottom", "20px");
        formSection.setVisible(false);
        
        setupGrid();
        
        mainContent.add(title, reportLayout, formSection, new Hr(), grid);
        
        // Create horizontal layout with sidebar and main content
        HorizontalLayout mainLayout = new HorizontalLayout(mainContent);
        mainLayout.setSizeFull();
        mainLayout.setSpacing(false);
        
        add(mainLayout, configButton);
    }
    
    private void setupFields() {
        List<Student> students = studentController.getAllStudents();
        studentField.setItems(students);
        studentField.setItemLabelGenerator(Student::getName);
        
        statusField.setItems(Attendance.AttendanceStatus.values());
        statusField.setItemLabelGenerator(status -> messageService.getMessage(status.getMessageKey()));
        
        dateField.setValue(LocalDate.now());
    }
    
    private void setupGrid() {
        grid.removeAllColumns();
        grid.addColumn(attendance -> attendance.getStudent().getName()).setHeader(messageService.getMessage("attendance.student")).setWidth("150px");
        
        grid.addColumn(attendance -> attendance.getDate() != null ? attendance.getDate().format(messageService.getCurrentDateTimeFormatter()) : "")
            .setHeader(messageService.getMessage("attendance.date")).setWidth("120px");
            
        grid.addColumn(attendance -> messageService.getMessage(attendance.getStatus().getMessageKey())).setHeader(messageService.getMessage("attendance.status")).setWidth("150px");
        grid.addColumn(Attendance::getNotes).setHeader(messageService.getMessage("attendance.notes")).setWidth("200px");
        
        grid.addComponentColumn(attendance -> {
            Button editButton = new Button(messageService.getMessage("common.edit"), e -> editAttendance(attendance));
            editButton.setId("edit-button-" + attendance.getId());
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            return editButton;
        }).setHeader(messageService.getMessage("attendance.actions")).setWidth("100px");
        
        grid.setMinHeight("600px");
        grid.setId("attendance-grid");
    }
    
    private void showTodayAttendance() {
        List<Attendance> todayAttendance = attendanceController.getAttendanceByDate(LocalDate.now());
        grid.setItems(todayAttendance);
    }
    
    private void showWeeklyReport() {
        if (weekStartField.getValue() != null) {
            List<Attendance> weeklyAttendance = attendanceController.getWeeklyAttendance(weekStartField.getValue());
            grid.setItems(weeklyAttendance);
        }
    }
    
    private void refreshGrid() {
        showTodayAttendance();
    }
    
    private void showForm() {
        formSection.setVisible(true);
        newButton.setVisible(false);
        studentField.focus();
    }
    
    private void hideForm() {
        formSection.setVisible(false);
        newButton.setVisible(true);
        clearForm();
    }
    
    private void editAttendance(Attendance attendance) {
        editingAttendance = attendance;
        studentField.setValue(attendance.getStudent());
        dateField.setValue(attendance.getDate());
        statusField.setValue(attendance.getStatus());
        notesField.setValue(attendance.getNotes());
        
        formSection.setVisible(true);
        newButton.setVisible(false);
        markButton.setText(messageService.getMessage("common.save"));
        cancelButton.setVisible(true);
    }
    
    private void cancelEdit() {
        hideForm();
    }
    
    private void clearForm() {
        editingAttendance = null;
        studentField.clear();
        dateField.setValue(LocalDate.now());
        statusField.clear();
        notesField.clear();
        markButton.setText(messageService.getMessage("attendance.mark"));
        cancelButton.setVisible(false);
    }
    
    private void saveAttendance() {
        if (studentField.getValue() != null && dateField.getValue() != null && statusField.getValue() != null) {
            showConfirmDialog(
                messageService.getMessage("confirm.save.title"),
                messageService.getMessage("confirm.save.message"),
                () -> {
                    if (editingAttendance != null) {
                        editingAttendance.setStudent(studentField.getValue());
                        editingAttendance.setDate(dateField.getValue());
                        editingAttendance.setStatus(statusField.getValue());
                        editingAttendance.setNotes(notesField.getValue());
                        attendanceController.updateAttendance(editingAttendance);
                    } else {
                        attendanceController.markAttendance(
                            studentField.getValue(),
                            dateField.getValue(),
                            statusField.getValue(),
                            notesField.getValue()
                        );
                    }
                    refreshGrid();
                    hideForm();
                }
            );
        }
    }
    
    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle(title);
        
        com.vaadin.flow.component.html.Div content = new com.vaadin.flow.component.html.Div();
        content.setText(message);
        dialog.add(content);
        
        Button confirmButton = new Button(messageService.getMessage("common.yes"), e -> {
            onConfirm.run();
            dialog.close();
        });
        confirmButton.setId("confirm-yes-button");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button(messageService.getMessage("common.no"), e -> dialog.close());
        cancelButton.setId("confirm-no-button");
        
        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }
}