package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseCRUDTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Navigate to Courses tab
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testCreateCourse() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Mathematics 101");
        page.locator("vaadin-text-area textarea").fill("Basic mathematics for kindergarten");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Ms. Johnson");
        page.locator("vaadin-integer-field input").fill("45");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Mon-Wed-Fri 9:00-10:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Mathematics 101"));
    }

    @Test
    void testEditCourse() {
        // Create course first
        page.locator("vaadin-text-field[label='Name'] input").fill("Edit Course");
        page.locator("vaadin-text-area textarea").fill("Course to be edited");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Mr. Smith");
        page.locator("vaadin-integer-field input").fill("30");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Tue-Thu 10:00-11:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Edit the course
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Edited Course");
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Edited Course"));
    }

    @Test
    void testDeleteCourse() {
        // Create course first
        page.locator("vaadin-text-field[label='Name'] input").fill("Delete Course");
        page.locator("vaadin-text-area textarea").fill("Course to be deleted");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Ms. Brown");
        page.locator("vaadin-integer-field input").fill("60");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Daily 2:00-3:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Delete the course
        page.click("vaadin-button:has-text('Delete')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertFalse(page.content().contains("Delete Course"));
    }

    @Test
    void testSearchCourse() {
        // Create multiple courses
        String[] names = {"Search Course 1", "Search Course 2", "Different Course"};
        for (int i = 0; i < names.length; i++) {
            page.locator("vaadin-text-field[label='Name'] input").fill(names[i]);
            page.locator("vaadin-text-area textarea").fill("Description " + i);
            page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Teacher " + i);
            page.locator("vaadin-integer-field input").fill(String.valueOf(30 + i * 10));
            page.locator("vaadin-text-field[label='Schedule'] input").fill("Schedule " + i);
            
            page.click("vaadin-button:has-text('Add Course')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // Search for specific courses
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Search Course");
        page.click("vaadin-button:has-text('Search')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Search Course 1"));
        assertTrue(page.content().contains("Search Course 2"));
        assertFalse(page.content().contains("Different Course"));
    }

    @Test
    void testInvalidDuration() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Invalid Duration Course");
        page.locator("vaadin-text-area textarea").fill("Course with invalid duration");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Ms. Test");
        page.locator("vaadin-integer-field input").fill("-10");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Mon 9:00-10:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testZeroDuration() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Zero Duration Course");
        page.locator("vaadin-text-area textarea").fill("Course with zero duration");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Ms. Test");
        page.locator("vaadin-integer-field input").fill("0");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Mon 9:00-10:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testMissingRequiredFields() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Incomplete Course");
        // Missing teacher name and schedule
        
        page.click("vaadin-button:has-text('Add Course')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testCancelEdit() {
        // Create course first
        page.locator("vaadin-text-field[label='Name'] input").fill("Cancel Course");
        page.locator("vaadin-text-area textarea").fill("Course to cancel edit");
        page.locator("vaadin-text-field[label='Teacher Name'] input").fill("Ms. Cancel");
        page.locator("vaadin-integer-field input").fill("45");
        page.locator("vaadin-text-field[label='Schedule'] input").fill("Wed 1:00-2:00");
        
        page.click("vaadin-button:has-text('Add Course')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Start editing and cancel
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Should Not Save");
        page.click("vaadin-button:has-text('Cancel')");
        
        page.waitForTimeout(500);
        assertTrue(page.content().contains("Cancel Course"));
        assertFalse(page.content().contains("Should Not Save"));
    }
}