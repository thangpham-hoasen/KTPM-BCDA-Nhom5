package vn.edu.hoasen.playwright;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;

public class StudentAdmissionTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        
        // // Ensure English locale is selected
        // try {
        //      if (page.isVisible("vaadin-button:has-text('Configuration')")) {
        //         page.click("vaadin-button:has-text('Configuration')");
        //         page.waitForSelector("vaadin-dialog-overlay");
                
        //         // Open language combo box (assuming it's the first combo box in dialog)
        //         page.locator("vaadin-combo-box").first().click();
        //         page.waitForSelector("vaadin-combo-box-item");
                
        //         // Select "English"
        //         page.locator("vaadin-combo-box-item:has-text('English')").click(); 
                
        //         // Save (Primary button)
        //         page.locator("vaadin-dialog-overlay vaadin-button[theme~='primary']").click();
                
        //         page.waitForTimeout(1000); 
        //         page.waitForLoadState(LoadState.NETWORKIDLE);
        //     }
        // } catch (Exception e) {
        //     System.out.println("Language switch to English failed or unneeded: " + e.getMessage());
        // }

        // Navigate to Students view
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @AfterEach
    void clearData() {
        try (Connection conn = DriverManager.getConnection(authProps.getProperty("db.url"));
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM students");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Black Box Testing: Equivalence Partitioning & Boundary Value Analysis ---
    // Valid age: 18 - 60 months

    @Test
    void testAdmission_ValidAge_Boundary() {
        // Lower Boundary: 18 months -> Valid
        createStudentWithAge(18, "Valid Child 18m", true);
        assertTrue(page.locator("#student-grid").textContent().contains("Valid Child 18m"));

        // Upper Boundary: 60 months -> Valid
        createStudentWithAge(60, "Valid Child 60m", true);
        assertTrue(page.locator("#student-grid").textContent().contains("Valid Child 60m"));
    }

    @Test
    void testAdmission_InvalidAge_Boundary() {
        // Lower Boundary - 1: 17 months -> Invalid
        createStudentWithAge(17, "Invalid Child 17m", false);
        assertFalse(page.locator("#student-grid").textContent().contains("Invalid Child 17m"));

        // Upper Boundary + 1: 61 months -> Invalid
        createStudentWithAge(61, "Invalid Child 61m", false);
        assertFalse(page.locator("#student-grid").textContent().contains("Invalid Child 61m"));
    }

    @Test
    void testAdmission_ParentPhone_Regex() {
        // Invalid: Contains text
        createStudentWithCustomPhone("Invalid Phone Text", "090abcxyz", false);
        assertFalse(page.locator("#student-grid").textContent().contains("Invalid Phone Text"));

        // Invalid: Start without 0
        createStudentWithCustomPhone("Invalid Phone Start", "1901234567", false);
        assertFalse(page.locator("#student-grid").textContent().contains("Invalid Phone Start"));

        // Valid: 0 + 9 digits
        createStudentWithCustomPhone("Valid Phone", "0987654321", true);
        assertTrue(page.locator("#student-grid").textContent().contains("Valid Phone"));
    }

    @Test
    void testAdmission_ParentName_Mandatory() {
        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill("No Parent Name");
        fillBirthDate(36); 
        page.locator("#birth-date-field input").press("Enter");
        page.locator("#parent-phone-field input").fill("0987654321");
        // Check if class auto-filled
        page.waitForTimeout(500);

        page.click("#add-button");
        page.waitForTimeout(1000);

        assertFalse(page.locator("#student-grid").textContent().contains("No Parent Name"));
    }

    // --- UI Logic: Auto-Class Suggestion & Read-only Field (BR-02 & UI) ---

    @Test
    void testAutoClassSuggestion_Mam() {
        // 18-36 months -> Nursery Class
        page.click("#new-button");
        page.waitForTimeout(500);

        fillBirthDate(20); // 20 months
        page.locator("#birth-date-field input").press("Enter");
        page.waitForTimeout(500);

        assertTrue(page.locator("#class-name-field input").inputValue().contains("Nursery Class"), 
                   "Should suggest 'Nursery Class' for 20 months old");
    }

    @Test
    void testAutoClassSuggestion_Choi() {
        // 37-48 months -> Sprout Class
        page.click("#new-button");
        page.waitForTimeout(500);

        fillBirthDate(40); // 40 months
        page.locator("#birth-date-field input").press("Enter");
        page.waitForTimeout(500);

        assertTrue(page.locator("#class-name-field input").inputValue().contains("Sprout Class"), 
                   "Should suggest 'Sprout Class' for 40 months old");
    }

    @Test
    void testAutoClassSuggestion_La() {
        // 49-60 months -> Leaf Class
        page.click("#new-button");
        page.waitForTimeout(500);

        fillBirthDate(55); // 55 months
        page.locator("#birth-date-field input").press("Enter");
        page.waitForTimeout(500);

        assertTrue(page.locator("#class-name-field input").inputValue().contains("Leaf Class"), 
                   "Should suggest 'Leaf Class' for 55 months old");
    }

    @Test
    void testAutoClassSuggestion_Invalid() {
        // Age < 18 or > 60 -> No suggestion
        page.click("#new-button");
        page.waitForTimeout(500);

        fillBirthDate(10); // 10 months
        page.locator("#birth-date-field input").press("Enter");
        page.waitForTimeout(500);
        assertEquals("", page.locator("#class-name-field input").inputValue(), "Should be empty for 10 months");

        fillBirthDate(70); // 70 months
        page.locator("#birth-date-field input").press("Enter");
        page.waitForTimeout(500);
        assertEquals("", page.locator("#class-name-field input").inputValue(), "Should be empty for 70 months");
    }

    @Test
    void testClassNameField_ReadOnly() {
        page.click("#new-button");
        page.waitForTimeout(500);

        try {
             // Check if editable
             boolean isEditable = page.locator("#class-name-field input").isEditable();
             assertFalse(isEditable, "Class Name field should be read-only");
             
        } catch (Exception e) {
            // Ignored
        }
    }

    // @Test
    // void testClassCapacity_Max25() {
    //     // BR-03: Max 25 students per class.
    //     // Seed 25 students into "class.mam" (Nursery Class)
    //     seedFullClass("class.mam", 25);
        
    //     // Try to add 26th
    //     page.click("#new-button");
    //     page.waitForTimeout(500);

    //     page.locator("#name-field input").fill("Student 26");
    //     fillBirthDate(20); // Nursery Class
    //     page.locator("#birth-date-field input").press("Enter");
        
    //     page.locator("#parent-name-field input").fill("Parent 26");
    //     page.locator("#parent-phone-field input").fill("0987654321");
        
    //     page.click("#add-button");
    //     page.waitForTimeout(1000);

    //     // Verify Error
    //     // English message: "Class is full (25 students)"
    //     boolean isErrorShown = page.locator("vaadin-notification-card").textContent().contains("Class is full");
    //     assertTrue(isErrorShown, "Should show 'Class is full' error");
        
    //     // Close dialog
    //     page.click("#cancel-button");
    //     page.waitForTimeout(500);
        
    //     assertFalse(page.locator("#student-grid").textContent().contains("Student 26"));
    // }
@Test
void testAdmission_ClassCapacityLimit_25_ShouldReject26th() {
    // Arrange: seed 25 học sinh lớp Mầm (18–36). Chọn 24 tháng => class.mam
    seedStudentsInClass("class.mam", 25, 24);

    // Reload UI để grid lấy dữ liệu mới
    page.reload();
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Act: thử thêm học sinh thứ 26 (cũng tuổi 24 tháng => class.mam)
    createStudentViaUI("Child 26 - Full Class", 24, "Parent 26", "0987654321");

    // Assert: không được thêm vào grid
    assertFalse(page.locator("#student-grid").textContent().contains("Child 26 - Full Class"));

    // (Khuyến nghị) assert có thông báo lỗi “đủ sĩ số”
    // Nếu UI có Notification:
    Locator lastNotif = page.locator("vaadin-notification-card").last();
    if (lastNotif.count() > 0) {
        String msg = lastNotif.textContent();
        assertTrue(msg.contains("Class is full") || msg.contains("25"));
    }
}

    // Helper Methods

    private void createStudentWithAge(int ageInMonths, String studentName, boolean expectSuccess) {
        page.click("#new-button");
        page.waitForTimeout(300);

        page.locator("#name-field input").fill(studentName);
        fillBirthDate(ageInMonths);
        page.locator("#birth-date-field input").press("Enter");

        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0987654321");
        
        // Wait for auto-fill if valid age
        page.waitForTimeout(500);

        page.click("#add-button");
        page.waitForTimeout(1000);
        
        if (!expectSuccess) {
             // Verify error notification
             boolean isErrorShown = page.locator("vaadin-notification-card").count() > 0;
             assertTrue(isErrorShown, "Error notification should appear for invalid data");
             
             // Close form (inline) to reset state for next test
             if (page.isVisible("#cancel-button")) {
                 page.click("#cancel-button");
             }
        }
    }

    private void createStudentWithCustomPhone(String studentName, String phone, boolean expectSuccess) {
        page.click("#new-button");
        page.waitForTimeout(300);

        page.locator("#name-field input").fill(studentName);
        fillBirthDate(36); // Valid age (3 years)
        page.locator("#birth-date-field input").press("Enter");

        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill(phone);
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        if (!expectSuccess) {
             // Close form (inline) to reset state for next test
             if (page.isVisible("#cancel-button")) {
                 page.click("#cancel-button");
             }
        }
    }

    private void seedFullClass(String classNameKey, int count) {
        try (Connection conn = DriverManager.getConnection(authProps.getProperty("db.url"));
             Statement stmt = conn.createStatement()) {
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.000");
             String now = LocalDateTime.now().format(dateFormatter);

             for (int i = 1; i <= count; i++) {
                 // Use a fixed birth date approx 20 months ago
                 String birthDate = LocalDate.now().minusMonths(20).atStartOfDay().format(dateFormatter);
                 
                 String sql = String.format("INSERT INTO students (name, birth_date, parent_name, parent_phone, class_name, created_at, updated_at) " +
                         "VALUES ('Student %d', '%s', 'Parent %d', '09000000%02d', '%s', '%s', '%s')", 
                         i, birthDate, i, i, classNameKey, now, now);
                 stmt.addBatch(sql);
             }
            stmt.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to seed database: " + e.getMessage());
        }
    }
    private void seedStudentsInClass(String className, int count, int ageInMonths) {
        LocalDate dob = LocalDate.now().minusMonths(ageInMonths);
        // Ensure consistent date formatting
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.000");
        String dobStr = dob.atStartOfDay().format(dateFormatter);
        
        String now = LocalDateTime.now().format(dateFormatter);

        try (Connection conn = DriverManager.getConnection(authProps.getProperty("db.url"))) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM students");
            }

            String sql = "INSERT INTO students (name, birth_date, class_name, parent_name, parent_phone, is_fully_vaccinated, created_at, updated_at) " +
                         "VALUES (?, ?, ?, ?, ?, 1, ?, ?)";

            try (var ps = conn.prepareStatement(sql)) {
                for (int i = 1; i <= count; i++) {
                    ps.setString(1, "Seed " + i);
                    ps.setString(2, dobStr);
                    ps.setString(3, className);
                    ps.setString(4, "Parent Seed");
                    ps.setString(5, "0987654321");
                    ps.setString(6, now);
                    ps.setString(7, now);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillBirthDate(int ageInMonths) {
        LocalDate dob = LocalDate.now().minusMonths(ageInMonths);
        // English locale: M/d/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String birthDateStr = dob.format(formatter);
        
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill(birthDateStr);
    }
}
