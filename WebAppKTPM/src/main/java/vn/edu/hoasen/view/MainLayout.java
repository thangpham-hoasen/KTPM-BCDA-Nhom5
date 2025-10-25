package vn.edu.hoasen.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import vn.edu.hoasen.component.ConfigurationButton;
import vn.edu.hoasen.service.MessageService;

public class MainLayout extends AppLayout {

    private final MessageService messageService;
    private final ConfigurationButton configButton;

    public MainLayout(MessageService messageService, ConfigurationButton configButton) {
        this.messageService = messageService;
        this.configButton = configButton;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Kindergarten Management");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

        addToNavbar(new DrawerToggle(), logo, configButton);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("👥 " + messageService.getMessage("nav.students"), StudentView.class));
        nav.addItem(new SideNavItem("📚 " + messageService.getMessage("nav.courses"), CourseView.class));
        nav.addItem(new SideNavItem("👨 " + messageService.getMessage("nav.teachers"), TeacherView.class));
        nav.addItem(new SideNavItem("📋 " + messageService.getMessage("nav.attendance"), AttendanceView.class));

        addToDrawer(new Scroller(nav));
    }
}