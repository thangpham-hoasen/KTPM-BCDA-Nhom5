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
        assertTrue(page.url().contains("login") || page.content().contains("Login"));
    }

    @Test
    void testInvalidURLs() {
        // Test accessing non-existent pages
        page.navigate(BASE_URL + "/nonexistent");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        // Should handle gracefully (redirect to login or show error page)
        assertTrue(page.url().contains("login") || 
                  page.content().contains("404") ||
                  page.content().contains("Not Found") ||
                  page.content().contains("Error") ||
                  !page.url().contains("/nonexistent"));
    }

    @Test
    void testSQLInjectionAttempts() {
        login();

        // Try SQL injection in search field
        page.locator("#search-field input").fill("'; DROP TABLE students; --");
        page.click("#search-button");

        page.waitForTimeout(1000);
        // Application should still be functional
        assertTrue(page.isVisible("#student-grid"));
    }

    @Test
    void testXSSAttempts() {
        login();

        // Try XSS in student name field
        page.click("#new-button");
        page.waitForTimeout(300);

        String xssScript = "<script>alert('XSS')</script>";
        page.locator("#name-field input").fill(xssScript);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Name");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");
        page.waitForTimeout(1000);

        // Application should handle XSS gracefully
        assertTrue(page.isVisible("#student-grid"));
    }

    @Test
    void testSessionTimeout() {
        login();

        // Clear session/cookies to simulate timeout
        page.context().clearCookies();
        
        // Try to perform an action that requires authentication
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        // Should handle session timeout gracefully (redirect to login or show error)
        assertTrue(page.url().contains("login") || 
                  page.content().contains("Login") ||
                  page.content().contains("Unauthorized") ||
                  page.content().contains("Session") ||
                  page.isVisible("vaadin-notification"));
    }

    @Test
    void testConcurrentUserActions() {
        login();

        // Simulate rapid form submissions
        for (int i = 0; i < 3; i++) {
            page.click("#new-button");
            page.waitForTimeout(200);

            page.locator("#name-field input").fill("Concurrent Test " + i);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("1/15/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent " + i);
            page.locator("#parent-phone-field input").fill("012345678" + i);
            page.locator("#class-name-field input").fill("Lớp Mầm");

            page.click("#add-button");
            page.waitForTimeout(300);
        }

        page.waitForTimeout(1000);
        // Application should handle concurrent requests gracefully
        assertTrue(page.isVisible("#student-grid"));
    }

    @Test
    void testInvalidDataTypes() {
        login();

        // Navigate to courses
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.click("#new-button");
        page.waitForTimeout(300);

        // Fill valid fields first
        page.locator("#name-field input").fill("Invalid Duration Course");
        page.locator("#description-field textarea").fill("Test description");
        page.locator("#teacher-name-field input").fill("Test Teacher");
        page.locator("#schedule-field input").fill("Mon 9:00");
        
        // Leave duration field empty (invalid for required integer field)
        // Cannot type text into number input, so test with empty value
        
        page.click("#add-button");

        // Should handle invalid input gracefully (no confirmation dialog due to validation)
        page.waitForTimeout(1000);
        assertFalse(page.isVisible("vaadin-dialog-overlay"));
    }

    @Test
    void testLargeDataInput() {
        login();

        page.click("#new-button");
        page.waitForTimeout(300);

        // Try extremely long input
        String longText = "A".repeat(1000);
        page.locator("#name-field input").fill(longText);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Name");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");

        // Should handle large input gracefully (validation should prevent save)
        page.waitForTimeout(1000);
        assertFalse(page.locator("#student-grid").textContent().contains(longText));
    }

    @Test
    void testNetworkErrorHandling() {
        login();

        page.click("#new-button");
        page.waitForTimeout(300);

        page.locator("#name-field input").fill("Network Test");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Name");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        // Simulate network issues by going offline
        page.context().setOffline(true);

        page.click("#add-button");

        page.waitForTimeout(2000);
        // Should handle network errors gracefully
        assertTrue(page.isVisible("#student-grid"));

        // Restore network
        page.context().setOffline(false);
    }

    @Test
    void testBrowserBackButton() {
        login();

        // Navigate to different pages using direct URLs
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);

        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);

        // Use browser back button
        page.goBack();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
        assertTrue(page.url().contains("/courses"));

        page.goBack();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
        assertTrue(page.url().equals(BASE_URL + "/"));
    }
}