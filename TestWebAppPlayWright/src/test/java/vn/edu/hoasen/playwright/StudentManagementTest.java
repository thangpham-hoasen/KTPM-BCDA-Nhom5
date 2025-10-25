package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentManagementTest extends BaseTest {

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
    void testAddValidStudent() {
        page.locator("vaadin-text-field[label='Name'] input").fill("John Doe");
        page.locator("vaadin-date-picker input").fill("2020-01-01");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("John Doe"));
    }

    @Test
    void testSearchStudent() {
        // Add multiple students first
        String[] names = {"Alice Smith", "Bob Johnson", "Charlie Brown"};
        for (String name : names) {
            page.locator("vaadin-text-field[label='Name'] input").fill(name);
            page.locator("vaadin-date-picker input").fill("2020-06-15");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent of " + name);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Chồi");
            
            page.click("vaadin-button:has-text('Add Student')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(500);
        }
        
        // Search for specific student
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Alice");
        page.click("vaadin-button:has-text('Search')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Alice Smith"));
        assertFalse(page.content().contains("Bob Johnson"));
    }

    @Test
    void testEditStudent() {
        // Create student first
        page.locator("vaadin-text-field[label='Name'] input").fill("Edit Test Student");
        page.locator("vaadin-date-picker input").fill("2021-03-10");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Edit Parent");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0987654321");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Lá");
        
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(1000);
        
        // Edit the student
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("vaadin-text-field[label='Name'] input").fill("Edited Student Name");
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        
        page.waitForTimeout(1000);
        assertTrue(page.content().contains("Edited Student Name"));
    }

    @Test
    void testDeleteStudent() {
        // Create student first
        page.locator("vaadin-text-field[label='Name'] input").fill("Delete Test Student");
        page.locator("vaadin-date-picker input").fill("2021-08-20");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Delete Parent");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
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
        assertFalse(page.content().contains("Delete Test Student"));
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
        
        // Search for non-existent student
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("NonExistent");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(500);
        
        // Show all students
        page.click("vaadin-button:has-text('Show All')");
        page.waitForTimeout(1000);
        
        assertTrue(page.content().contains("Show All Test"));
    }

    @Test
    void testAgeValidation() {
        // Test student too young (under 18 months)
        page.locator("vaadin-text-field[label='Name'] input").fill("Too Young");
        page.locator("vaadin-date-picker input").fill("2023-06-01");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent Name");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }
}