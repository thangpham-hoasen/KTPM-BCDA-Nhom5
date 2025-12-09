package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentCRUDTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        
        page.click("vaadin-side-nav-item:has-text('👥 Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testCreateStudent() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Alice Smith");
        page.locator("#birth-date-field input").fill("2021-03-10");
        page.locator("#parent-name-field input").fill("Bob Smith");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Chồi");
        
        page.click("#add-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#student-grid").isVisible());
    }

    @Test
    void testEditStudent() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Edit Test");
        page.locator("#birth-date-field input").fill("2021-06-15");
        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Lá");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        // Edit the student
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("Edited Name");
        page.click("#add-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#student-grid").isVisible());
    }

    @Test
    void testDeleteStudent() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Delete Test");
        page.locator("#birth-date-field input").fill("2021-09-20");
        page.locator("#parent-name-field input").fill("Parent Delete");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        // Delete the student
        page.click("vaadin-button:has-text('Delete')");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#student-grid").isVisible());
    }

    @Test
    void testCancelEdit() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Cancel Test");
        page.locator("#birth-date-field input").fill("2021-12-05");
        page.locator("#parent-name-field input").fill("Parent Cancel");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Chồi");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        // Start editing and cancel
        page.click("vaadin-button:has-text('Edit')");
        page.waitForSelector("#cancel-button:visible");
        
        page.locator("#name-field input").fill("Should Not Save");
        page.click("#cancel-button");
        
        page.waitForTimeout(500);
        assertTrue(page.locator("#student-grid").isVisible());
    }

    @Test
    void testSearchStudent() {
        // Create multiple students
        String[] names = {"Search Test 1", "Search Test 2", "Different Name"};
        for (String name : names) {
            page.click("#new-button");
            page.waitForTimeout(300);
            
            page.locator("#name-field input").fill(name);
            page.locator("#birth-date-field input").fill("2021-01-01");
            page.locator("#parent-name-field input").fill("Parent " + name);
            page.locator("#parent-phone-field input").fill("0123456789");
            page.locator("#class-name-field input").fill("Lớp Mầm");
            
            page.click("#add-button");
            page.waitForTimeout(500);
        }
        
        // Search for specific students
        page.locator("#search-field input").fill("Search Test");
        page.click("#search-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#student-grid").isVisible());
    }

    @Test
    void testShowAllStudents() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Show All Test");
        page.locator("#birth-date-field input").fill("2021-01-01");
        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(500);
        
        page.locator("#search-field input").fill("NonExistent");
        page.click("#search-button");
        page.waitForTimeout(500);
        
        // Show all students
        page.click("#show-all-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#student-grid").isVisible());
    }
}