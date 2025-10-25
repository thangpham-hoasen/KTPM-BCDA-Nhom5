package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityAndErrorHandlingTest extends BaseTest {

    @Test
    void testUnauthorizedAccess() {
        // Try to access protected pages without login
        page.navigate(BASE_URL + "/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Should redirect to login or show login form
        assertTrue(page.url().contains("/login") || page.content().contains("Login"));
    }

    @Test
    void testInvalidURLs() {
        // Test accessing non-existent pages
        page.navigate(BASE_URL + "/nonexistent");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Should handle gracefully (404 or redirect)
        assertTrue(page.url().contains("/login") || 
                  page.content().contains("404") || 
                  page.content().contains("Not Found"));
    }

    @Test
    void testSQLInjectionAttempts() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Try SQL injection in search field
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("'; DROP TABLE students; --");
        page.click("vaadin-button:has-text('Search')");
        
        page.waitForTimeout(1000);
        // Application should still be functional
        assertTrue(page.isVisible("vaadin-grid"));
    }

    @Test
    void testXSSAttempts() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Try XSS in student name field
        page.locator("vaadin-text-field[label='Name'] input").fill("<script>alert('XSS')</script>");
        page.locator("vaadin-date-picker input").fill("2021-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Name");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        // Script should be escaped/sanitized
        assertFalse(page.content().contains("<script>"));
    }

    @Test
    void testSessionTimeout() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Clear session/cookies to simulate timeout
        page.context().clearCookies();
        
        // Try to perform an action
        page.locator("vaadin-text-field[label='Name'] input").fill("Test Student");
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForTimeout(2000);
        // Should handle session timeout gracefully
        assertTrue(page.url().contains("/login") || page.isVisible("vaadin-notification"));
    }

    @Test
    void testConcurrentUserActions() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Simulate rapid form submissions
        for (int i = 0; i < 3; i++) {
            page.locator("vaadin-text-field[label='Name'] input").fill("Concurrent Test " + i);
            page.locator("vaadin-date-picker input").fill("2021-01-15");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent " + i);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("012345678" + i);
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
            
            page.click("vaadin-button:has-text('Add Student')");
            if (page.isVisible("vaadin-dialog")) {
                page.click("vaadin-button:has-text('Yes')");
            }
            page.waitForTimeout(200);
        }
        
        page.waitForTimeout(2000);
        // Application should handle concurrent requests gracefully
        assertTrue(page.isVisible("vaadin-grid"));
    }

    @Test
    void testInvalidDataTypes() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Navigate to courses
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Try invalid duration (should be integer)
        page.locator("vaadin-text-field[label='Name'] input").fill("Invalid Duration Course");
        page.locator("vaadin-text-area textarea").fill("Test description");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Test Teacher");
        page.locator("vaadin-integer-field input").fill("abc");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Mon 9:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        
        // Should handle invalid input gracefully
        page.waitForTimeout(1000);
        assertTrue(page.isVisible("vaadin-notification") || 
                  page.locator("vaadin-integer-field").getAttribute("invalid") != null);
    }

    @Test
    void testLargeDataInput() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Try extremely long input
        String longText = "A".repeat(1000);
        page.locator("vaadin-text-field[label='Name'] input").fill(longText);
        page.locator("vaadin-date-picker input").fill("2021-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Name");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        // Should handle large input gracefully
        page.waitForTimeout(1000);
        assertTrue(page.isVisible("vaadin-notification") || page.isVisible("vaadin-dialog"));
    }

    @Test
    void testNetworkErrorHandling() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Simulate network issues by going offline
        page.context().setOffline(true);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Network Test");
        page.locator("vaadin-date-picker input").fill("2021-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Name");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForTimeout(2000);
        // Should handle network errors gracefully
        assertTrue(page.isVisible("vaadin-notification") || 
                  page.content().contains("error") || 
                  page.content().contains("network"));
        
        // Restore network
        page.context().setOffline(false);
    }

    @Test
    void testBrowserBackButton() {
        // Login first
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Navigate to different pages
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Use browser back button
        page.goBack();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.url().contains("/courses"));
        
        page.goBack();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.url().equals(BASE_URL + "/"));
    }
}