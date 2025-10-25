package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest extends BaseTest {

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
    void testCompleteWorkflow() {
        // 1. Create a teacher
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Ms. Johnson");
        page.locator("vaadin-email-field input").fill("johnson@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Mathematics");
        page.locator("vaadin-date-picker input").fill("2023-01-15");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // 2. Create a course
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Basic Math");
        page.locator("vaadin-text-area textarea").fill("Introduction to numbers and counting");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Ms. Johnson");
        page.locator("vaadin-integer-field input").fill("45");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Mon-Wed-Fri 9:00-10:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // 3. Create students
        page.click("a[href='/']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        String[] students = {"Alice Wonder", "Bob Builder", "Charlie Chocolate"};
        for (String student : students) {
            page.locator("vaadin-text-field[label='Name'] input").fill(student);
            page.locator("vaadin-date-picker input").fill("2021-01-15");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent of " + student);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
            
            page.click("vaadin-button:has-text('Add Student')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // 4. Mark attendance for students
        page.click("a[href='/attendance']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        for (String student : students) {
            page.locator("vaadin-combo-box[label*='Student'] input").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item:has-text('" + student + "')");
            
            page.locator("vaadin-combo-box[label*='Status'] input").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item:has-text('Present')");
            
            page.locator("vaadin-text-area textarea").fill("Active participation in " + student);
            
            page.click("vaadin-button:has-text('Mark Attendance')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // 5. Verify all data is connected
        assertTrue(page.content().contains("Alice Wonder"));
        assertTrue(page.content().contains("Bob Builder"));
        assertTrue(page.content().contains("Charlie Chocolate"));
        
        // Check teacher exists
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Ms. Johnson"));
        
        // Check course exists
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Basic Math"));
        assertTrue(page.content().contains("Ms. Johnson"));
    }

    @Test
    void testDataConsistency() {
        // Create teacher and course with same teacher name
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Dr. Smith");
        page.locator("vaadin-email-field input").fill("smith@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0987654321");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Science");
        page.locator("vaadin-date-picker input").fill("2023-02-01");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Create course with this teacher
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Science Exploration");
        page.locator("vaadin-text-area textarea").fill("Fun science activities for kids");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Dr. Smith");
        page.locator("vaadin-integer-field input").fill("60");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Tue-Thu 2:00-3:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Verify consistency
        assertTrue(page.content().contains("Dr. Smith"));
        assertTrue(page.content().contains("Science Exploration"));
        
        // Check teacher page still has the teacher
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Dr. Smith"));
        assertTrue(page.content().contains("Science"));
    }

    @Test
    void testCrossModuleSearch() {
        // Create data across modules
        String teacherName = "Prof. Wilson";
        String courseName = "Art & Creativity";
        String studentName = "Emma Artist";
        
        // Create teacher
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill(teacherName);
        page.locator("vaadin-email-field input").fill("wilson@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Art");
        page.locator("vaadin-date-picker input").fill("2023-03-01");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Create course
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill(courseName);
        page.locator("vaadin-text-area textarea").fill("Creative art activities");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill(teacherName);
        page.locator("vaadin-integer-field input").fill("90");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Mon-Fri 1:00-2:30");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Create student
        page.click("a[href='/']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-text-field[label='Name'] input").fill(studentName);
        page.locator("vaadin-date-picker input").fill("2021-06-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Artist");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0987654321");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Lá");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Test search across modules
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Artist");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(1000);
        assertTrue(page.content().contains(studentName));
        
        // Search in courses
        page.click("a[href='/courses']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Art");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(1000);
        assertTrue(page.content().contains(courseName));
        
        // Search in teachers
        page.click("a[href='/teachers']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Wilson");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(1000);
        assertTrue(page.content().contains(teacherName));
    }

    @Test
    void testAttendanceReporting() {
        // Create students for attendance testing
        String[] students = {"Report Student 1", "Report Student 2", "Report Student 3"};
        
        page.click("a[href='/']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        for (String student : students) {
            page.locator("vaadin-text-field[label='Name'] input").fill(student);
            page.locator("vaadin-date-picker input").fill("2021-03-10");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent of " + student);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Chồi");
            
            page.click("vaadin-button:has-text('Add Student')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // Mark different attendance statuses
        page.click("a[href='/attendance']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        String[] statuses = {"Present", "Absent with Permission", "Absent without Permission"};
        
        for (int i = 0; i < students.length; i++) {
            page.locator("vaadin-combo-box[label*='Student'] input").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item:has-text('" + students[i] + "')");
            
            page.locator("vaadin-combo-box[label*='Status'] input").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item:has-text('" + statuses[i] + "')");
            
            page.locator("vaadin-text-area textarea").fill("Note for " + students[i]);
            
            page.click("vaadin-button:has-text('Mark Attendance')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // Test today's attendance report
        page.click("vaadin-button:has-text('Today Attendance')");
        page.waitForTimeout(1000);
        
        // Verify all attendance records are shown
        for (String student : students) {
            assertTrue(page.content().contains(student));
        }
        
        // Test weekly report
        page.locator("vaadin-date-picker[label*='Week Start'] input").fill("2024-01-01");
        page.click("vaadin-button:has-text('Weekly Report')");
        page.waitForTimeout(1000);
        
        assertTrue(page.isVisible("vaadin-grid"));
    }

    @Test
    void testEditWorkflow() {
        // Create initial data
        String originalName = "Original Student";
        String updatedName = "Updated Student";
        
        // Create student
        page.locator("vaadin-text-field[label='Name'] input").fill(originalName);
        page.locator("vaadin-date-picker input").fill("2021-05-20");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Original Parent");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Mark attendance for original student
        page.click("a[href='/attendance']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.locator("vaadin-combo-box[label*='Student'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('" + originalName + "')");
        
        page.locator("vaadin-combo-box[label*='Status'] input").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('Present')");
        
        page.click("vaadin-button:has-text('Mark Attendance')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Edit student name
        page.click("a[href='/']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill(updatedName);
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Verify student name is updated
        assertTrue(page.content().contains(updatedName));
        assertFalse(page.content().contains(originalName));
        
        // Check attendance still exists (should be linked by ID, not name)
        page.click("a[href='/attendance']");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // The attendance record should still exist
        assertTrue(page.isVisible("vaadin-grid"));
    }
}