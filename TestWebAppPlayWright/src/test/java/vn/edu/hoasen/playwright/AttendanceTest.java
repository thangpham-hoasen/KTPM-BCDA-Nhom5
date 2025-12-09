package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceTest extends BaseTest {

    @BeforeEach
    void loginAndSetup() {
        login();
        
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testMarkAttendancePresent() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#notes-field textarea").fill("Student was present and active");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertTrue(page.locator("#attendance-grid").textContent().contains(studentName));
        assertTrue(page.locator("#attendance-grid").textContent().contains("Student was present and active"));
    }

    @Test
    void testMarkAttendanceAbsentWithPermission() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=1");
        
        page.locator("#notes-field textarea").fill("Doctor appointment");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertTrue(page.locator("#attendance-grid").textContent().contains(studentName));
        assertTrue(page.locator("#attendance-grid").textContent().contains("Doctor appointment"));
    }

    @Test
    void testMarkAttendanceAbsentWithoutPermission() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=2");
        
        page.locator("#notes-field textarea").fill("No notification from parents");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertTrue(page.locator("#attendance-grid").textContent().contains(studentName));
        assertTrue(page.locator("#attendance-grid").textContent().contains("No notification from parents"));
    }

    @Test
    void testEditAttendance() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        // Edit the attendance
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=1");
        
        page.locator("#notes-field textarea").fill("Changed to absent - sick");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertTrue(page.locator("#attendance-grid").textContent().contains(studentName));
        assertTrue(page.locator("#attendance-grid").textContent().contains("Changed to absent - sick"));
    }

    @Test
    void testCancelEditAttendance() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#notes-field textarea").fill("Original note");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        String originalContent = page.locator("#attendance-grid").textContent();
        
        // Start editing and cancel
        page.click("vaadin-button:has-text('Edit')");
        page.waitForSelector("#cancel-button");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=2");
        
        page.waitForSelector("#cancel-button:visible");
        page.click("#cancel-button");
        
        page.waitForTimeout(500);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertEquals(originalContent, page.locator("#attendance-grid").textContent());
    }

    @Test
    void testWeeklyReport() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        int initialCount = page.locator("#attendance-grid vaadin-grid-cell-content").count();
        
        // Test weekly report button without setting date
        page.click("#weekly-report-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertTrue(initialCount == 0 || page.locator("#attendance-grid").textContent().contains(studentName));
    }

    @Test
    void testTodayAttendance() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        // Test today's attendance
        page.click("#today-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#attendance-grid").isVisible());
        assertTrue(page.locator("#attendance-grid").textContent().contains(studentName));
    }

    @Test
    void testMissingRequiredFields() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.click("#mark-button");
        page.waitForTimeout(1000);
        // Should not open dialog if required fields are missing
        assertFalse(page.isVisible("vaadin-dialog-overlay"));
    }

    @Test
    void testFormValidation() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.click("#mark-button");
        page.waitForTimeout(1000);
        // Should not proceed without status
        assertFalse(page.isVisible("vaadin-dialog-overlay"));
    }

    @Test
    void testClearFormAfterSave() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        String studentName = page.locator("vaadin-combo-box-item >> nth=0").textContent();
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        
        page.locator("#notes-field textarea").fill("Test notes");
        
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        // Form should be hidden and data saved to grid
        assertFalse(page.locator("#notes-field").isVisible());
        assertTrue(page.locator("#new-button").isVisible());
        assertTrue(page.locator("#attendance-grid").textContent().contains(studentName));
        assertTrue(page.locator("#attendance-grid").textContent().contains("Test notes"));
    }
}