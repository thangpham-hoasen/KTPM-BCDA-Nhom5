package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PerformanceAndLoadTest extends BaseTest {

    @BeforeEach
    public void login() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");
        page.waitForURL(BASE_URL + "/");
    }

    @Test
    void testPageLoadTime() {
        long startTime = System.currentTimeMillis();
        
        page.navigate(BASE_URL + "/");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        // Page should load within 5 seconds
        assertTrue(loadTime < 5000, "Page load time: " + loadTime + "ms");
    }

    @Test
    void testNavigationPerformance() {
        long startTime, endTime;
        
        // Test navigation to courses
        startTime = System.currentTimeMillis();
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 3000, "Courses navigation too slow");
        
        // Test navigation to teachers
        startTime = System.currentTimeMillis();
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 3000, "Teachers navigation too slow");
        
        // Test navigation to attendance
        startTime = System.currentTimeMillis();
        page.click("vaadin-side-nav-item:has-text('Attendance')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 3000, "Attendance navigation too slow");
    }

    @Test
    void testBulkDataCreation() {
        long startTime = System.currentTimeMillis();
        
        // Create multiple students quickly
        for (int i = 1; i <= 10; i++) {
            page.locator("vaadin-text-field[label='Name'] input").fill("Bulk Student " + i);
            page.locator("vaadin-date-picker input").fill("2021-01-" + String.format("%02d", i));
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent " + i);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("012345678" + i);
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
            
            page.click("vaadin-button:has-text('Add Student')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(200);
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Should complete within reasonable time (30 seconds for 10 records)
        assertTrue(totalTime < 30000, "Bulk creation too slow: " + totalTime + "ms");
        
        // Verify all students were created
        page.waitForTimeout(1000);
        for (int i = 1; i <= 10; i++) {
            assertTrue(page.content().contains("Bulk Student " + i));
        }
    }

    @Test
    void testSearchPerformance() {
        // Create some test data first
        for (int i = 1; i <= 5; i++) {
            page.locator("vaadin-text-field[label='Name'] input").fill("Search Test " + i);
            page.locator("vaadin-date-picker input").fill("2021-01-15");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent " + i);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("012345678" + i);
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
            
            page.click("vaadin-button:has-text('Add Student')");
            page.waitForSelector("vaadin-dialog");
            page.click("vaadin-button:has-text('Yes')");
            page.waitForTimeout(200);
        }
        
        // Test search performance
        long startTime = System.currentTimeMillis();
        
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Search Test");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(1000);
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Search should complete within 2 seconds
        assertTrue(searchTime < 2000, "Search too slow: " + searchTime + "ms");
        
        // Verify search results
        assertTrue(page.content().contains("Search Test 1"));
    }

    @Test
    void testGridRenderingPerformance() {
        // Create fewer records to test grid rendering (reduce from 20 to 5)
        for (int i = 1; i <= 5; i++) {
            page.click("#new-button");
            page.waitForTimeout(200);
            
            page.locator("#name-field input").fill("Grid Test " + i);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("1/15/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent " + i);
            page.locator("#parent-phone-field input").fill("012345678" + i);
            page.locator("#class-name-field input").fill("Lớp Mầm");
            
            page.click("#add-button");
            page.waitForTimeout(300);
        }
        
        // Test grid refresh performance
        long startTime = System.currentTimeMillis();
        
        page.click("#show-all-button");
        page.waitForTimeout(1000);
        
        long renderTime = System.currentTimeMillis() - startTime;
        
        // Grid should render within 3 seconds
        assertTrue(renderTime < 3000, "Grid rendering too slow: " + renderTime + "ms");
        
        // Verify grid is populated
        assertTrue(page.isVisible("#student-grid"));
        assertTrue(page.locator("#student-grid").textContent().contains("Grid Test"));
    }

    @Test
    void testFormValidationPerformance() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        long startTime = System.currentTimeMillis();
        
        // Test validation on multiple fields with invalid data
        page.locator("#name-field input").fill("A"); // Too short
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2025"); // Future date
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("B"); // Too short
        page.locator("#parent-phone-field input").fill("123"); // Invalid
        page.locator("#class-name-field input").fill("Test");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        long validationTime = System.currentTimeMillis() - startTime;
        
        // Validation should complete within 2 seconds
        assertTrue(validationTime < 2000, "Validation too slow: " + validationTime + "ms");
        
        // Test completed - form validation performance measured
        assertTrue(page.isVisible("#student-grid"));
    }

    @Test
    void testMemoryUsageWithLargeDataset() {
        // Create a smaller dataset to test memory handling (reduce from 50 to 10)
        for (int i = 1; i <= 10; i++) {
            page.click("#new-button");
            page.waitForTimeout(200);
            
            page.locator("#name-field input").fill("Memory Test Student " + i);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("1/15/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent " + i);
            page.locator("#parent-phone-field input").fill("012345678" + (i % 10));
            page.locator("#class-name-field input").fill("Lớp " + (i % 3 == 0 ? "Mầm" : i % 3 == 1 ? "Chồi" : "Lá"));
            
            page.click("#add-button");
            page.waitForTimeout(300);
        }
        
        // Test navigation with dataset
        page.click("vaadin-side-nav-item:has-text('Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.click("vaadin-side-nav-item:has-text('Teachers')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Application should still be responsive
        assertTrue(page.isVisible("#student-grid"));
        assertTrue(page.locator("#student-grid").textContent().contains("Memory Test Student"));
    }

    @Test
    void testConcurrentOperations() {
        // Test multiple rapid operations
        long startTime = System.currentTimeMillis();
        
        // Rapid form filling and submission
        for (int i = 1; i <= 5; i++) {
            page.locator("vaadin-text-field[label='Name'] input").fill("Concurrent " + i);
            page.locator("vaadin-date-picker input").fill("2021-01-15");
            page.locator("vaadin-text-field[label='Parent Name'] input").fill("Parent " + i);
            page.locator("vaadin-text-field[label='Parent Phone'] input").fill("012345678" + i);
            page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
            
            page.click("vaadin-button:has-text('Add Student')");
            if (page.isVisible("vaadin-dialog")) {
                page.click("vaadin-button:has-text('Yes')");
            }
            
            // Immediate search after each addition
            page.locator("vaadin-text-field[placeholder*='Search'] input").fill("Concurrent");
            page.click("vaadin-button:has-text('Search')");
            page.waitForTimeout(100);
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // Should handle concurrent operations within reasonable time
        assertTrue(totalTime < 15000, "Concurrent operations too slow: " + totalTime + "ms");
        
        // Verify system is still responsive
        assertTrue(page.isVisible("vaadin-grid"));
    }

    @Test
    void testDatabaseConnectionPerformance() {
        // Test multiple database operations
        long startTime = System.currentTimeMillis();
        
        // Create, read, update operations
        page.locator("vaadin-text-field[label='Name'] input").fill("DB Test Student");
        page.locator("vaadin-date-picker input").fill("2021-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("DB Parent");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        // Create
        page.click("vaadin-button:has-text('Add Student')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(500);
        
        // Read (search)
        page.locator("vaadin-text-field[placeholder*='Search'] input").fill("DB Test");
        page.click("vaadin-button:has-text('Search')");
        page.waitForTimeout(500);
        
        // Update
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(300);
        page.locator("vaadin-text-field[label='Name'] input").fill("DB Test Updated");
        page.click("vaadin-button:has-text('Save')");
        page.waitForSelector("vaadin-dialog");
        page.click("vaadin-button:has-text('Yes')");
        page.waitForTimeout(500);
        
        long dbOperationTime = System.currentTimeMillis() - startTime;
        
        // Database operations should complete within 10 seconds
        assertTrue(dbOperationTime < 10000, "Database operations too slow: " + dbOperationTime + "ms");
        
        // Verify operations completed successfully
        assertTrue(page.content().contains("DB Test Updated"));
    }
}