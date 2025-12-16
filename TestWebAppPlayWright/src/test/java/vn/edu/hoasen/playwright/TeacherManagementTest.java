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

class TeacherManagementTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @AfterEach
    void clearData() {
        try (Connection conn = DriverManager.getConnection(authProps.getProperty("db.url"));
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM teachers");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreateTeacher() {
        String uniqueName = "John_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill(uniqueName);
        page.locator("#email-field input").fill("john.doe@example.com");
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
        assertTrue(page.locator("#teacher-grid").textContent().contains(uniqueName));
    }

    @Test
    void testEditTeacher() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Edit Teacher");
        page.locator("#email-field input").fill("edit@example.com");
        page.locator("#phone-field input").fill("0987654321");
        page.locator("#subject-field input").fill("Science");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("2/20/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        page.locator("vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);
        
        page.locator("#name-field input").clear();
        page.locator("#name-field input").fill("Edited Teacher Name");
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#teacher-grid").textContent().contains("Edited Teacher Name"));
    }

    @Test
    void testDeleteTeacher() {
        String uniqueName = "DelTeacher_" + System.currentTimeMillis();
        
        page.click("#new-button");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill(uniqueName);
        page.locator("#email-field input").fill("delete@example.com");
        page.locator("#phone-field input").fill("0111222333");
        page.locator("#subject-field input").fill("History");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("3/15/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);
        
        assertTrue(page.locator("#teacher-grid").textContent().contains(uniqueName));
        
        page.locator("vaadin-button:has-text('Delete')").first().click();
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);

        page.reload();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        assertFalse(page.locator("#teacher-grid").textContent().contains(uniqueName));
    }

    @Test
    void testCancelEdit() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Cancel Teacher");
        page.locator("#email-field input").fill("cancel@example.com");
        page.locator("#phone-field input").fill("0444555666");
        page.locator("#subject-field input").fill("English");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("4/10/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        page.locator("#teacher-grid vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("Should Not Save");
        page.click("#cancel-button");
        page.waitForTimeout(500);
        
        assertTrue(page.locator("#teacher-grid").textContent().contains("Cancel Teacher"));
        assertFalse(page.locator("#teacher-grid").textContent().contains("Should Not Save"));
    }

    @Test
    void testSearchTeacher() {
        String[] names = {"SearchTeacher 1", "SearchTeacher 2", "Different Name"};
        String[] emails = {"search1@example.com", "search2@example.com", "different@example.com"};
        
        for (int i = 0; i < names.length; i++) {
            page.click("#new-button");
            page.waitForTimeout(300);
            
            page.locator("#name-field input").fill(names[i]);
            page.locator("#email-field input").fill(emails[i]);
            page.locator("#phone-field input").fill("012345678" + i);
            page.locator("#subject-field input").fill("Subject " + i);
            
            page.locator("#hire-date-field input").click();
            page.locator("#hire-date-field input").fill("5/" + (i + 1) + "/2024");
            page.keyboard().press("Enter");
            
            page.click("#add-button");
            page.waitForSelector("vaadin-dialog-overlay");
            page.click("#confirm-yes-button");
            page.waitForTimeout(500);
        }
        
        page.locator("#search-field input").fill("SearchTeacher");
        page.click("#search-button");
        page.waitForTimeout(1000);
        
        String gridContent = page.locator("#teacher-grid").textContent();
        assertTrue(gridContent.contains("SearchTeacher 1") || gridContent.contains("SearchTeacher 2"));
    }

    @Test
    void testShowAllTeachers() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("ShowAll Teacher");
        page.locator("#email-field input").fill("showall@example.com");
        page.locator("#phone-field input").fill("0777888999");
        page.locator("#subject-field input").fill("Art");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("6/15/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        page.locator("#search-field input").fill("NonExistent");
        page.click("#search-button");
        page.waitForTimeout(500);
        
        page.click("#show-all-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#teacher-grid").textContent().contains("ShowAll Teacher"));
    }

    @Test
    void testEmailValidation() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Invalid Email Teacher");
        page.locator("#email-field input").fill("invalid-email");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("7/20/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#teacher-grid").textContent().contains("Invalid Email Teacher"));
    }

    @Test
    void testPhoneValidation() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Invalid Phone Teacher");
        page.locator("#email-field input").fill("valid@example.com");
        page.locator("#phone-field input").fill("123");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("8/25/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#teacher-grid").textContent().contains("Invalid Phone Teacher"));
    }

    @Test
    void testNameTooShort() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("J");
        page.locator("#email-field input").fill("short@example.com");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("9/10/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        // Should not show confirmation dialog due to validation error
        assertFalse(page.isVisible("vaadin-dialog-overlay"));
    }

    @Test
    void testNameTooLong() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        String longName = "A".repeat(51);
        page.locator("#name-field input").fill(longName);
        page.locator("#email-field input").fill("long@example.com");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("10/15/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#teacher-grid").textContent().contains(longName));
    }

    @Test
    void testMissingRequiredFields() {
        page.click("#new-button");
        page.waitForTimeout(500);
        
        int initialCount = page.locator("#teacher-grid vaadin-grid-cell-content").count();
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        int finalCount = page.locator("#teacher-grid vaadin-grid-cell-content").count();
        assertEquals(initialCount, finalCount);
    }

    @Test
    void testPhoneWithLetters() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Letter Phone Teacher");
        page.locator("#email-field input").fill("valid@example.com");
        page.locator("#phone-field input").fill("012345678a");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("11/20/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#teacher-grid").textContent().contains("Letter Phone Teacher"));
    }

    @Test
    void testValidTeacherCreation() {
        String uniqueName = "ValidTeacher_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill(uniqueName);
        page.locator("#email-field input").fill("valid.teacher@example.com");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Science");
        
        page.locator("#hire-date-field input").click();
        page.locator("#hire-date-field input").fill("12/1/2024");
        page.keyboard().press("Enter");
        
        page.click("#add-button");
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);

        assertTrue(page.locator("#teacher-grid").textContent().contains(uniqueName));
    }
}
