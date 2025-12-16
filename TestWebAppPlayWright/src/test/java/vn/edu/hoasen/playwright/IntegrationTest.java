package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest extends BaseTest {

    @BeforeEach
    public void login() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
    }

    @AfterEach
    void clearData() {
        try (Connection conn = DriverManager.getConnection(authProps.getProperty("db.url"));
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM attendances");
            stmt.executeUpdate("DELETE FROM students");
            stmt.executeUpdate("DELETE FROM courses");
            stmt.executeUpdate("DELETE FROM teachers");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCompleteWorkflow() {
        // 1. Create a teacher
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("Ms. Johnson");
        page.locator("#email-field input").fill("johnson@school.com");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Mathematics");
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("1/15/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);
        
        // 2. Create a course
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("Basic Math");
        page.locator("#description-field textarea").fill("Introduction to numbers");
        page.locator("#teacher-name-field input").fill("Ms. Johnson");
        page.locator("#duration-field input").fill("45");
        page.locator("#schedule-field input").fill("Mon-Wed-Fri");
        
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);
        
        // 3. Create students
        page.navigate(BASE_URL + "/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        String[] students = {"Alice Wonder", "Bob Builder", "Charlie Chocolate"};
        for (String student : students) {
            page.click("#new-button");
            page.waitForTimeout(500);
            
            page.locator("#name-field input").fill(student);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("1/15/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent of " + student);
            page.locator("#parent-phone-field input").fill("0123456789");
            page.locator("#class-name-field input").fill("Lớp Mầm");
            
            page.click("#add-button");
            page.waitForTimeout(1500);
        }
        
        // 4. Mark attendance
        page.navigate(BASE_URL + "/attendance");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        for (String student : students) {
            page.click("#new-button");
            page.waitForTimeout(500);
            
            page.locator("#student-field").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item:has-text('" + student + "')");
            
            page.locator("#status-field").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item >> nth=0");
            
            page.locator("#notes-field textarea").fill("Active in " + student);
            
            page.click("#mark-button");
            page.waitForSelector("vaadin-dialog-overlay");
            page.click("#confirm-yes-button");
            page.waitForTimeout(1000);
        }
        
        // 5. Verify
        assertTrue(page.locator("#attendance-grid").textContent().contains("Alice Wonder"));
        
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.locator("#teacher-grid").textContent().contains("Ms. Johnson"));
        
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.locator("#course-grid").textContent().contains("Basic Math"));
    }

    @Test
    void testEditWorkflow() {
        String originalName = "Original " + System.currentTimeMillis();
        String updatedName = "Updated " + System.currentTimeMillis();

        page.click("#new-button");
        page.waitForTimeout(500);
        page.locator("#name-field input").fill(originalName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("5/20/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Original Parent");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        page.click("#add-button");
        page.waitForTimeout(1500);

        page.navigate(BASE_URL + "/attendance");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        page.locator("#student-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item:has-text('" + originalName + "')");
        page.locator("#status-field").click();
        page.waitForSelector("vaadin-combo-box-item");
        page.click("vaadin-combo-box-item >> nth=0");
        page.click("#mark-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);

        page.navigate(BASE_URL + "/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("#student-grid vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);
        page.locator("#name-field input").fill(updatedName);
        page.click("#add-button");
        page.waitForTimeout(1500);

        assertTrue(page.locator("#student-grid").textContent().contains(updatedName));
        page.navigate(BASE_URL + "/attendance");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.locator("#attendance-grid").textContent().contains(updatedName));
    }

    @Test
    void testDataConsistency() {
        String teacherName = "Dr. Smith " + System.currentTimeMillis();
        
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        page.locator("#name-field input").fill(teacherName);
        page.locator("#email-field input").fill("smith@school.com");
        page.locator("#phone-field input").fill("0987654321");
        page.locator("#subject-field input").fill("Science");
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("2/1/2023");
        page.keyboard().press("Enter");
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);

        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        page.locator("#name-field input").fill("Science Exploration");
        page.locator("#description-field textarea").fill("Fun science activities");
        page.locator("#teacher-name-field input").fill(teacherName);
        page.locator("#duration-field input").fill("60");
        page.locator("#schedule-field input").fill("Tue-Thu 2:00-3:00");
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);

        assertTrue(page.locator("#course-grid").textContent().contains(teacherName));
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.locator("#teacher-grid").textContent().contains(teacherName));
    }

    @Test
    void testCrossModuleSearch() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String teacherName = "Wilson" + uniqueId;
        String studentName = "Artist" + uniqueId;

        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        page.locator("#name-field input").fill(teacherName);
        page.locator("#email-field input").fill("wilson@school.com");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Art");
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("3/1/2023");
        page.keyboard().press("Enter");
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);

        page.navigate(BASE_URL + "/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.click("#new-button");
        page.waitForTimeout(500);
        page.locator("#name-field input").fill(studentName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("6/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Artist");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Lá");
        page.click("#add-button");
        page.waitForTimeout(1500);

        page.locator("#search-field input").fill(uniqueId);
        page.click("#search-button");
        page.waitForTimeout(1000);
        assertTrue(page.locator("#student-grid").textContent().contains(studentName));

        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("#search-field input").fill(uniqueId);
        page.click("#search-button");
        page.waitForTimeout(1000);
        assertTrue(page.locator("#teacher-grid").textContent().contains(teacherName));
    }

    @Test
    void testAttendanceReporting() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        String[] students = {"Report1_" + uniqueId, "Report2_" + uniqueId, "Report3_" + uniqueId};

        for (String student : students) {
            page.click("#new-button");
            page.waitForTimeout(500);
            page.locator("#name-field input").fill(student);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("3/10/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent of " + student);
            page.locator("#parent-phone-field input").fill("0123456789");
            page.locator("#class-name-field input").fill("Lớp Chồi");
            page.click("#add-button");
            page.waitForTimeout(1500);
        }

        page.navigate(BASE_URL + "/attendance");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        for (String student : students) {
            page.click("#new-button");
            page.waitForTimeout(500);
            page.locator("#student-field").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item:has-text('" + student + "')");
            page.locator("#status-field").click();
            page.waitForSelector("vaadin-combo-box-item");
            page.click("vaadin-combo-box-item >> nth=0");
            page.locator("#notes-field textarea").fill("Note for " + student);
            page.click("#mark-button");
            page.waitForSelector("vaadin-dialog-overlay");
            page.click("#confirm-yes-button");
            page.waitForTimeout(1000);
        }

        for (String student : students) {
            assertTrue(page.locator("#attendance-grid").textContent().contains(student));
        }
    }

}
