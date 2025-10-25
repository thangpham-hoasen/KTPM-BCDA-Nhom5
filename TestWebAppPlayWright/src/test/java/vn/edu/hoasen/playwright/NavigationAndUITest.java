package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NavigationAndUITest extends BaseTest {

    @BeforeEach
    void login() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
    }

    @Test
    void testNavigationToStudents() {
        page.click("vaadin-side-nav-item:has-text('👥 Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Student Management"));
        assertTrue(page.isVisible("vaadin-text-field[label='Name']"));
    }

    @Test
    void testNavigationToCourses() {
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Course Management"));
        assertTrue(page.isVisible("vaadin-text-field[label='Name']"));
        assertTrue(page.isVisible("vaadin-text-area"));
    }

    @Test
    void testNavigationToTeachers() {
        page.click("vaadin-side-nav-item:has-text('👨‍🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Teacher Management"));
        assertTrue(page.isVisible("vaadin-email-field"));
    }

    @Test
    void testNavigationToAttendance() {
        page.click("vaadin-side-nav-item:has-text('📋 Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        assertTrue(page.content().contains("Attendance Management"));
        assertTrue(page.isVisible("vaadin-combo-box[label*='Student']"));
    }

    @Test
    void testSidebarNavigation() {
        // Test sidebar navigation links with icons
        page.click("vaadin-side-nav-item:has-text('👥 Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.url().contains("/"));
        
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.url().contains("/courses"));
        
        page.click("vaadin-side-nav-item:has-text('👨‍🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.url().contains("/teachers"));
        
        page.click("vaadin-side-nav-item:has-text('📋 Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.url().contains("/attendance"));
    }

    @Test
    void testConfigurationButton() {
        assertTrue(page.isVisible("vaadin-button:has-text('⚙️ Configuration')"));
        
        page.click("vaadin-button:has-text('⚙️ Configuration')");
        page.waitForTimeout(500);
        
        // Should show language options or configuration dialog
        assertTrue(page.isVisible("vaadin-dialog") || page.isVisible("vaadin-context-menu"));
    }

    @Test
    void testResponsiveLayout() {
        // Test different viewport sizes
        page.setViewportSize(1200, 800);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("vaadin-form-layout"));
        
        page.setViewportSize(800, 600);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("vaadin-form-layout"));
        
        page.setViewportSize(400, 600);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("vaadin-form-layout"));
    }

    @Test
    void testGridVisibility() {
        // Test that grids are visible on each page
        assertTrue(page.isVisible("vaadin-grid"));
        
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-grid"));
        
        page.click("vaadin-side-nav-item:has-text('👨‍🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-grid"));
        
        page.click("vaadin-side-nav-item:has-text('📋 Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-grid"));
    }

    @Test
    void testFormLayoutResponsiveness() {
        page.click("vaadin-side-nav-item:has-text('👥 Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Check form layout exists and is responsive
        assertTrue(page.isVisible("vaadin-form-layout"));
        
        // Test different screen sizes
        page.setViewportSize(1200, 800);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("vaadin-form-layout"));
        
        page.setViewportSize(600, 800);
        page.waitForTimeout(500);
        assertTrue(page.isVisible("vaadin-form-layout"));
    }

    @Test
    void testSearchFunctionality() {
        // Test search on students page
        assertTrue(page.isVisible("vaadin-text-field[placeholder*='Search']"));
        assertTrue(page.isVisible("vaadin-button:has-text('Search')"));
        assertTrue(page.isVisible("vaadin-button:has-text('Show All')"));
        
        // Test search on other pages
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-text-field[placeholder*='Search']"));
        
        page.click("vaadin-side-nav-item:has-text('👨🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-text-field[placeholder*='Search']"));
    }

    @Test
    void testButtonStates() {
        // Test initial button states
        assertTrue(page.isVisible("vaadin-button:has-text('Add Student')"));
        assertFalse(page.isVisible("vaadin-button:has-text('Cancel')"));
        
        // Test on other pages
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-button:has-text('Add Course')"));
        
        page.click("vaadin-side-nav-item:has-text('👨🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-button:has-text('Add Teacher')"));
        
        page.click("vaadin-side-nav-item:has-text('📋 Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.isVisible("vaadin-button:has-text('Mark Attendance')"));
    }

    @Test
    void testPageTitles() {
        // Test page titles/headers
        assertTrue(page.content().contains("Student Management"));
        
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Course Management"));
        
        page.click("vaadin-side-nav-item:has-text('👨🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Teacher Management"));
        
        page.click("vaadin-side-nav-item:has-text('📋 Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Attendance Management"));
    }

    @Test
    void testDirectURLAccess() {
        // Test direct URL access to different pages
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
}