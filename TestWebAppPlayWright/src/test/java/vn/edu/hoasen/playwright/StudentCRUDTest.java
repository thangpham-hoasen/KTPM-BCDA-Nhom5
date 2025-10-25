package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentCRUDTest extends BaseTest {

    @BeforeEach
    void login() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
        
        // Navigate to Students tab
        page.click("vaadin-side-nav-item:has-text('👥 Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testCreateStudent() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Alice Smith");
        page.locator("vaadin-date-picker input").fill("2021-03-10");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Bob Smith");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0987654321");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Chồi");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Alice Smith"));
    }

    @Test
    void testEditStudent() {
        // First create a student
        page.locator("vaadin-text-field[label='Name'] input").fill("Edit Test");
        page.locator("vaadin-date-picker input").fill("2021-06-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Test");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Lá");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Edit the student
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Edited Name");
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Edited Name"));
    }

    @Test
    void testDeleteStudent() {
        // First create a student
        page.locator("vaadin-text-field[label='Name'] input").fill("Delete Test");
        page.locator("vaadin-date-picker input").fill("2021-09-20");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Delete");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0987654321");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Delete the student
        page.click("vaadin-button:has-text('Delete')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertFalse(page.content().contains("Delete Test"));
    }

    @Test
    void testCancelEdit() {
        // First create a student
        page.locator("vaadin-text-field[label='Name'] input").fill("Cancel Test");
        page.locator("vaadin-date-picker input").fill("2021-12-05");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Cancel");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Chồi");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Start editing and cancel
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Should Not Save");
        page.click("vaadin-button:has-text('Cancel')");
        
        page.waitForTimeout(500);
        assertTrue(page.content().contains("Cancel Test"));
        assertFalse(page.content().contains("Should Not Save"));
    }

    @Test
    void testSearchStudent() {
        // Create multiple students
        String[] names = {"Search Test 1", "Search Test 2", "Different Name"};
        for (String name : names) {
            page.locator("vaadin-text-field[label='Name'] input").fill(name);
            page.locator("vaadin-date-picker input").fill("2021-01-01");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent " + name);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
            
            page.click("vaadin-button:has-text('Add Student')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // Search for specific students
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Search Test");
        page.click("vaadin-button:has-text('Search')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Search Test 1"));
        assertTrue(page.content().contains("Search Test 2"));
        assertFalse(page.content().contains("Different Name"));
    }

    @Test
    void testShowAllStudents() {
        // Create and search first
        page.locator("vaadin-text-field[label='Name'] input").fill("Show All Test");
        page.locator("vaadin-date-picker input").fill("2021-01-01");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Test");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("NonExistent");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(500);
        
        // Show all students
        page.click("vaadin-button:has-text('Show All')");
        page.waitForTimeout(1000);
        
        assertTrue(page.content().contains("Show All Test"));
    }
}