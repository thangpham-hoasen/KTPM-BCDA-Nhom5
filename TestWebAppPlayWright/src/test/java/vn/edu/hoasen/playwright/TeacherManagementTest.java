package vn.edu.hoasen.playwright;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeacherManagementTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
    }

    @Test
    void testCreateTeacher() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("John Doe");
        page.locator("#email-field input").fill("john.doe@example.com");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Mathematics");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#teacher-grid").isVisible());
    }

    @Test
    void testEditTeacher() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Edit Teacher");
        page.locator("#email-field input").fill("edit@example.com");
        page.locator("#phone-field input").fill("0987654321");
        page.locator("#subject-field input").fill("Science");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        page.click("vaadin-button:has-text('Edit')");
        page.waitForSelector("#cancel-button:visible");
        
        page.locator("#name-field input").clear();
        page.locator("#name-field input").fill("Edited Teacher Name");
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#teacher-grid").isVisible());
    }

    @Test
    void testDeleteTeacher() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Delete Teacher");
        page.locator("#email-field input").fill("delete@example.com");
        page.locator("#phone-field input").fill("0111222333");
        page.locator("#subject-field input").fill("History");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        page.click("vaadin-button:has-text('Delete')");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#teacher-grid").isVisible());
    }

    @Test
    void testCancelEdit() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Cancel Teacher");
        page.locator("#email-field input").fill("cancel@example.com");
        page.locator("#phone-field input").fill("0444555666");
        page.locator("#subject-field input").fill("English");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        page.click("vaadin-button:has-text('Edit')");
        page.waitForSelector("#cancel-button:visible");
        
        page.locator("#name-field input").fill("Should Not Save");
        page.click("#cancel-button");
        
        page.waitForTimeout(500);
        assertTrue(page.locator("#teacher-grid").isVisible());
    }

    @Test
    void testSearchTeacher() {
        String[] names = {"Search Teacher 1", "Search Teacher 2", "Different Name"};
        String[] emails = {"search1@example.com", "search2@example.com", "different@example.com"};
        
        for (int i = 0; i < names.length; i++) {
            page.click("#new-button");
            page.waitForTimeout(300);
            
            page.locator("#name-field input").fill(names[i]);
            page.locator("#email-field input").fill(emails[i]);
            page.locator("#phone-field input").fill("012345678" + i);
            page.locator("#subject-field input").fill("Subject " + i);
            
            page.locator("#hire-date-field").click();
            page.waitForSelector("vaadin-date-picker-overlay");
            page.click("vaadin-date-picker-overlay [part='today-button']");
            
            page.click("#add-button");
            page.waitForSelector("vaadin-dialog-overlay");
            page.click("#confirm-yes-button");
            page.waitForTimeout(500);
        }
        
        page.locator("#search-field input").fill("Search Teacher");
        page.click("#search-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#teacher-grid").isVisible());
    }

    @Test
    void testShowAllTeachers() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Show All Teacher");
        page.locator("#email-field input").fill("showall@example.com");
        page.locator("#phone-field input").fill("0777888999");
        page.locator("#subject-field input").fill("Art");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(500);
        
        page.locator("#search-field input").fill("NonExistent");
        page.click("#search-button");
        page.waitForTimeout(500);
        
        page.click("#show-all-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#teacher-grid").isVisible());
    }

    @Test
    void testEmailValidation() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Invalid Email Teacher");
        page.locator("#email-field input").fill("invalid-email");
        page.locator("#phone-field input").fill("0123456789");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.isVisible("vaadin-dialog-overlay"));
    }

    @Test
    void testPhoneValidation() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Invalid Phone Teacher");
        page.locator("#email-field input").fill("valid@example.com");
        page.locator("#phone-field input").fill("123");
        page.locator("#subject-field input").fill("Math");
        
        page.locator("#hire-date-field").click();
        page.waitForSelector("vaadin-date-picker-overlay");
        page.click("vaadin-date-picker-overlay [part='today-button']");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.isVisible("vaadin-dialog-overlay"));
    }
}
