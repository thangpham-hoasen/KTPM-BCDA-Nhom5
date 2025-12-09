package vn.edu.hoasen.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.PostConstruct;
import vn.edu.hoasen.model.User;
import vn.edu.hoasen.service.MessageService;
import vn.edu.hoasen.service.UserService;

@Route("login")
public class LoginView extends VerticalLayout {
    
    private final UserService userService;
    private final MessageService messageService;
    
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginView(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostConstruct
    public void init() {
        userService.createDefaultUser(); // Create default admin user
        setupLayout();
    }

    private void setupLayout() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        VerticalLayout loginForm = new VerticalLayout();
        loginForm.setWidth("400px");
        loginForm.setPadding(true);
        loginForm.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)")
                           .set("border-radius", "var(--lumo-border-radius-m)")
                           .set("background", "var(--lumo-base-color)");

        H1 title = new H1(messageService.getMessage("login.title"));
        title.getStyle().set("text-align", "center").set("margin-bottom", "30px");

        usernameField = new TextField(messageService.getMessage("login.username"));
        usernameField.setId("username-field");
        usernameField.setWidth("100%");
        usernameField.setValue("admin"); // Default for demo

        passwordField = new PasswordField(messageService.getMessage("login.password"));
        passwordField.setId("password-field");
        passwordField.setWidth("100%");
        passwordField.setValue("admin123"); // Default for demo

        loginButton = new Button(messageService.getMessage("login.submit"), e -> performLogin());
        loginButton.setId("login-button");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("100%");

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, passwordField);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        loginForm.add(title, formLayout, loginButton);
        add(loginForm);
    }

    private void performLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            Notification.show(messageService.getMessage("login.error.empty"), 3000, Notification.Position.MIDDLE);
            return;
        }

        if (userService.validateLogin(username, password)) {
            User user = userService.findByUsername(username).orElse(null);
            VaadinSession.getCurrent().setAttribute("user", user);
            
            Notification.show(messageService.getMessage("login.success"), 2000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate(StudentView.class));
        } else {
            Notification.show(messageService.getMessage("login.error.invalid"), 3000, Notification.Position.MIDDLE);
        }
    }
}