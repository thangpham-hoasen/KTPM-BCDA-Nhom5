package vn.edu.hoasen.security;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;
import vn.edu.hoasen.view.LoginView;

@Component
public class SecurityConfiguration implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            uiEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!isUserLoggedIn() && !isLoginView(enterEvent.getNavigationTarget())) {
                    enterEvent.rerouteTo(LoginView.class);
                }
            });
        });
    }

    private boolean isUserLoggedIn() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && session.getAttribute("user") != null;
    }

    private boolean isLoginView(Class<?> navigationTarget) {
        return LoginView.class.equals(navigationTarget);
    }
}