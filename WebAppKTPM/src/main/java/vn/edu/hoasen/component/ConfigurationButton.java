package vn.edu.hoasen.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.server.VaadinSession;
import vn.edu.hoasen.service.MessageService;

@SpringComponent
@UIScope
public class ConfigurationButton extends Button {
    
    @Autowired
    private MessageService messageService;
    
    private Dialog configDialog;

    public ConfigurationButton() {
        setIcon(VaadinIcon.COG.create());
        setText("Configuration");
        addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        
        addClickListener(e -> openConfigDialog());
    }

    private void openConfigDialog() {
        if (configDialog == null) {
            createConfigDialog();
        }
        configDialog.open();
    }

    private void createConfigDialog() {
        configDialog = new Dialog();
        configDialog.setWidth("400px");
        configDialog.setCloseOnEsc(true);
        configDialog.setCloseOnOutsideClick(false);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        H3 title = new H3(messageService.getMessage("config.title"));
        
        // Language selection
        ComboBox<String> languageCombo = new ComboBox<>(messageService.getMessage("config.language"));
        languageCombo.setItems("vi", "en");
        languageCombo.setItemLabelGenerator(lang -> 
            "vi".equals(lang) ? messageService.getMessage("lang.vietnamese") : messageService.getMessage("lang.english"));
        languageCombo.setValue(messageService.getCurrentLanguage());
        
        // Password change form
        FormLayout passwordForm = new FormLayout();
        PasswordField currentPassword = new PasswordField(messageService.getMessage("config.currentPassword"));
        PasswordField newPassword = new PasswordField(messageService.getMessage("config.newPassword"));
        PasswordField confirmPassword = new PasswordField(messageService.getMessage("config.confirmPassword"));
        passwordForm.add(currentPassword, newPassword, confirmPassword);
        
        // Profile form
        FormLayout profileForm = new FormLayout();
        TextField fullName = new TextField(messageService.getMessage("config.fullName"));
        EmailField email = new EmailField(messageService.getMessage("config.email"));
        TextField phone = new TextField(messageService.getMessage("config.phone"));
        profileForm.add(fullName, email, phone);
        
        // Buttons
        Button saveButton = new Button(messageService.getMessage("config.save"), e -> {
            String selectedLanguage = languageCombo.getValue();
            if (selectedLanguage != null && !selectedLanguage.equals(messageService.getCurrentLanguage())) {
                messageService.setLocale(selectedLanguage);
                configDialog.close();
                getUI().ifPresent(ui -> ui.getPage().reload());
            } else {
                configDialog.close();
            }
        });
        
        Button logoutButton = new Button(messageService.getMessage("config.logout"), e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button(messageService.getMessage("config.cancel"), e -> configDialog.close());
        
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        HorizontalLayout logoutLayout = new HorizontalLayout(logoutButton);
        
        layout.add(title, languageCombo, passwordForm, profileForm, buttonLayout, logoutLayout);
        configDialog.add(layout);
    }
}