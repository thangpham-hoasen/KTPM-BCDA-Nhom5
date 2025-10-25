package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceTest extends BaseTest {

    @BeforeEach
    void loginAndSetup() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Create a student first for attendance testing
        page.locator("vaadin-text-field[label='Name'] input").fill("Attendance Student");
        page.locator("vaadin-date-picker input").fill("2021-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Name");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Navigate to attendance
        page.click("vaadin-side-nav-item:has-text('📋 Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testMarkAttendancePresent() {
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.locator("vaadin-text-area textarea").fill("Student was present and active");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Attendance Student"));
        assertTrue(page.content().contains("Present"));
    }

    @Test
    void testMarkAttendanceAbsentWithPermission() {
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Absent with Permission')");
        
        page.locator("vaadin-text-area textarea").fill("Doctor appointment");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Absent with Permission"));
    }

    @Test
    void testMarkAttendanceAbsentWithoutPermission() {
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Absent without Permission')");
        
        page.locator("vaadin-text-area textarea").fill("No notification from parents");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Absent without Permission"));
    }

    @Test
    void testEditAttendance() {
        // Mark attendance first
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Edit the attendance
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Absent with Permission')");
        
        page.locator("vaadin-text-area textarea").fill("Changed to absent - sick");
        
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Absent with Permission"));
    }

    @Test
    void testCancelEditAttendance() {
        // Mark attendance first
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Start editing and cancel
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Absent without Permission')");
        
        page.click("vaadin-button:has-text('Cancel')");
        
        page.waitForTimeout(500);
        assertTrue(page.content().contains("Present"));
        assertFalse(page.content().contains("Absent without Permission"));
    }

    @Test
    void testWeeklyReport() {
        // Mark some attendance first
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Test weekly report
        page.locator("vaadin-date-picker[label*='Week Start'] input").fill("2024-01-01");
        page.click("vaadin-button:has-text('Weekly Report')");
        
        page.waitForTimeout(1000);
        // Should show attendance data for the week
        assertTrue(page.locator("vaadin-grid").isVisible());
    }

    @Test
    void testTodayAttendance() {
        // Mark attendance for today
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Test today's attendance
        page.click("vaadin-button:has-text('Today Attendance')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Attendance Student"));
    }

    @Test
    void testMissingRequiredFields() {
        // Try to mark attendance without selecting student
        page.click("vaadin-button:has-text('Mark Attendance')");
        
        // Should not proceed without required fields
        page.waitForTimeout(500);
        assertFalse(page.isVisible("vaadin-dialog"));
    }

    @Test
    void testAttendanceWithNotes() {
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Attendance Student')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.locator("vaadin-text-area textarea").fill("Student participated well in activities");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Student participated well"));
    }
}