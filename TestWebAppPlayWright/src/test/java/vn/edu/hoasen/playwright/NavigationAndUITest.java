package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NavigationAndUITest extends BaseTest {

    @BeforeEach
    public void login() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
    }

    @Test
    void testNavigationToStudents() {
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Student Management"));
        assertTrue(page.isVisible("#new-button"));
    }

    @Test
    void testNavigationToCourses() {
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Course Management"));
        assertTrue(page.isVisible("#new-button"));
    }

    @Test
    void testNavigationToTeachers() {
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Teacher Management"));
        assertTrue(page.isVisible("#new-button"));
    }

    @Test
    void testNavigationToAttendance() {
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Attendance Management"));
        assertTrue(page.isVisible("#new-button"));
    }

    @Test
    void testSidebarNavigation() {
        assertTrue(page.url().equals(BASE_URL + "/"));
        
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
        assertTrue(page.url().contains("/courses"));
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
        assertTrue(page.url().contains("/teachers"));
        
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
        assertTrue(page.url().contains("/attendance"));
    }

    @Test
    void testConfigurationButton() {
        assertTrue(page.isVisible("vaadin-button:has-text('Configuration')") || 
                   page.isVisible("vaadin-menu-bar"));
    }

    @Test
    void testResponsiveLayout() {
        page.setViewportSize(1200, 800);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("#student-grid"));
        
        page.setViewportSize(800, 600);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("#student-grid"));
        
        page.setViewportSize(400, 600);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("#student-grid"));
    }

    @Test
    void testGridVisibility() {
        assertTrue(page.isVisible("vaadin-grid"));
        
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-grid"));
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-grid"));
        
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-grid"));
    }

    @Test
    void testFormLayoutResponsiveness() {
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.setViewportSize(1200, 800);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("#student-grid"));
        
        page.setViewportSize(600, 800);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("#student-grid"));
    }

    @Test
    void testSearchFunctionality() {
        assertTrue(page.isVisible("#search-field"));
        assertTrue(page.isVisible("#search-button"));
        assertTrue(page.isVisible("#show-all-button"));
        
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("#search-field"));
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("#search-field"));
    }

    @Test
    void testButtonStates() {
        assertTrue(page.isVisible("#new-button"));
        assertFalse(page.isVisible("#cancel-button"));
        
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("#new-button"));
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("#new-button"));
        
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("#new-button"));
    }

    @Test
    void testPageTitles() {
        assertTrue(page.content().contains("Student Management"));
        
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Course Management"));
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Teacher Management"));
        
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Attendance Management"));
    }

    @Test
    void testDirectURLAccess() {
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Course Management"));
        
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Teacher Management"));
        
        page.navigate(BASE_URL + "/attendance");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Attendance Management"));
        
        page.navigate(BASE_URL + "/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Student Management"));
    }

    @Test
    void testCompleteNavigation() {
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Students"));
        
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Courses"));
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Teachers"));
        
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Attendance"));
        
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Students"));
    }
}
