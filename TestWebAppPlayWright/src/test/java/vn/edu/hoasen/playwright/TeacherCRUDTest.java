package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeacherCRUDTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Navigate to Teachers tab
        page.click("vaadin-side-nav-item:has-text('👨🏫 Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testCreateTeacher() {
        page.locator("vaadin-text-field[label='Name'] input").fill("John Teacher");
        page.locator("vaadin-email-field input").fill("john@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Mathematics");
        page.locator("vaadin-date-picker input").fill("2023-01-15");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("John Teacher"));
    }

    @Test
    void testEditTeacher() {
        // Create teacher first
        page.locator("vaadin-text-field[label='Name'] input").fill("Edit Teacher");
        page.locator("vaadin-email-field input").fill("edit@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0987654321");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Science");
        page.locator("vaadin-date-picker input").fill("2023-02-01");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Edit the teacher
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Edited Teacher");
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Edited Teacher"));
    }

    @Test
    void testDeleteTeacher() {
        // Create teacher first
        page.locator("vaadin-text-field[label='Name'] input").fill("Delete Teacher");
        page.locator("vaadin-email-field input").fill("delete@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Art");
        page.locator("vaadin-date-picker input").fill("2023-03-01");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Delete the teacher
        page.click("vaadin-button:has-text('Delete')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertFalse(page.content().contains("Delete Teacher"));
    }

    @Test
    void testSearchTeacher() {
        // Create multiple teachers
        String[] names = {"Search Teacher 1", "Search Teacher 2", "Different Teacher"};
        for (int i = 0; i < names.length; i++) {
            page.locator("vaadin-text-field[label='Name'] input").fill(names[i]);
            page.locator("vaadin-email-field input").fill("teacher" + i + "@school.com");
            page.locator("vaadin-text-field[label='Phone'] input").fill("012345678" + i);
            page.locator("vaadin-text-field[label='Subject'] input").fill("Subject " + i);
            page.locator("vaadin-date-picker input").fill("2023-01-0" + (i + 1));
            
            page.click("vaadin-button:has-text('Add Teacher')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // Search for specific teachers
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Search Teacher");
        page.click("vaadin-button:has-text('Search')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Search Teacher 1"));
        assertTrue(page.content().contains("Search Teacher 2"));
        assertFalse(page.content().contains("Different Teacher"));
    }

    @Test
    void testInvalidEmail() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Invalid Email Teacher");
        page.locator("vaadin-email-field input").fill("invalid-email");
        page.locator("vaadin-text-field[label='Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Math");
        page.locator("vaadin-date-picker input").fill("2023-01-15");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testInvalidPhone() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Invalid Phone Teacher");
        page.locator("vaadin-email-field input").fill("valid@school.com");
        page.locator("vaadin-text-field[label='Phone'] input").fill("123");
        page.locator("vaadin-text-field[label='Subject'] input").fill("Math");
        page.locator("vaadin-date-picker input").fill("2023-01-15");
        
        page.click("vaadin-button:has-text('Add Teacher')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testMissingRequiredFields() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Incomplete Teacher");
        // Missing subject and hire date
        
        page.click("vaadin-button:has-text('Add Teacher')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }
}