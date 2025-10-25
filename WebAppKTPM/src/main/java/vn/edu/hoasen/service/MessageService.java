package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.vaadin.flow.server.VaadinSession;

import java.util.Locale;

@Service
public class MessageService {
    
    @Autowired
    private MessageSource messageSource;

    public String getMessage(String key) {
        Locale locale = getCurrentLocale();
        return messageSource.getMessage(key, null, key, locale);
    }

    public String getMessage(String key, Object... args) {
        Locale locale = getCurrentLocale();
        return messageSource.getMessage(key, args, key, locale);
    }
    
    public void setLocale(String language) {
        Locale locale = "vi".equals(language) ? new Locale("vi", "VN") : Locale.ENGLISH;
        java.time.format.DateTimeFormatter formatter = "vi".equals(language) ?
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", locale) :
                java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy", locale);
        VaadinSession.getCurrent().setAttribute("locale", locale);
        VaadinSession.getCurrent().setAttribute("language", language);
        VaadinSession.getCurrent().setAttribute("dtFormatter", formatter);
        
        // Set UI locale for date formatting
        if (com.vaadin.flow.component.UI.getCurrent() != null) {
            com.vaadin.flow.component.UI.getCurrent().setLocale(locale);
        }
    }
    
    public String getCurrentLanguage() {
        if (VaadinSession.getCurrent() != null) {
            String language = (String) VaadinSession.getCurrent().getAttribute("language");
            if (language != null) {
                return language;
            }
        }
        return "en"; // Default to English
    }
    
    public Locale getCurrentLocale() {
        if (VaadinSession.getCurrent() != null) {
            Locale sessionLocale = (Locale) VaadinSession.getCurrent().getAttribute("locale");
            if (sessionLocale != null) {
                return sessionLocale;
            }
        }
        return Locale.ENGLISH; // Default to English
    }

    public java.time.format.DateTimeFormatter getCurrentDateTimeFormatter() {
        if (VaadinSession.getCurrent() != null) {
            java.time.format.DateTimeFormatter sessionDtFormater =
                    (java.time.format.DateTimeFormatter) VaadinSession.getCurrent().getAttribute("dtFormatter");
            if (sessionDtFormater != null) {
                return sessionDtFormater;
            }
        }
        return java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy", java.util.Locale.ENGLISH);
    }

}