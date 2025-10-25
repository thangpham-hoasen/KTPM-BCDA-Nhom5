package vn.edu.hoasen.config;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocaleConfiguration implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            // Set default locale based on session or default to English
            VaadinSession session = VaadinSession.getCurrent();
            Locale locale = Locale.ENGLISH; // Default
            
            if (session != null) {
                Locale sessionLocale = (Locale) session.getAttribute("locale");
                if (sessionLocale != null) {
                    locale = sessionLocale;
                }
            }
            
            uiEvent.getUI().setLocale(locale);
        });
    }
}