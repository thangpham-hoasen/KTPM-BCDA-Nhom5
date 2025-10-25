package vn.edu.hoasen.playwright;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseManagementTest extends BaseTest {

    @Test
    void testAddCourse() {
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Fill course form
        page.locator("vaadin-text-field:has([slot='label']:text('Course Name')) input").fill("Mathematics");
        page.locator("vaadin-text-area:has([slot='label']:text('Description')) textarea").fill("Basic math course");
        page.locator("vaadin-text-field:has([slot='label']:text('Teacher Name')) input").fill("John Smith");
        page.locator("vaadin-integer-field:has([slot='label']:text('Duration (hours)')) input").fill("40");
        page.locator("vaadin-text-field:has([slot='label']:text('Schedule')) input").fill("Mon-Wed-Fri 9AM");
        
        // Click Add Course button
        page.click("text=Add Course");
        
        // Verify course appears in grid
        page.waitForSelector("text=Mathematics");
        assertTrue(page.content().contains("Mathematics"));
    }

    @Test
    void testSearchCourse() {
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Add a course first
        page.locator("vaadin-text-field:has([slot='label']:text('Course Name')) input").fill("Test Course");
        page.locator("vaadin-text-field:has([slot='label']:text('Teacher Name')) input").fill("Test Teacher");
        page.click("text=Add Course");
        
        // Search for the course
        page.locator("vaadin-text-field:has([slot='label']:text('Search')) input").fill("Test");
        page.click("text=Search");
        
        // Verify search results
        page.waitForSelector("text=Test Course");
        assertTrue(page.content().contains("Test Course"));
    }

    @Test
    void testNavigationToTeachers() {
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.click("text=Manage Teachers");
        page.waitForSelector("text=Teacher Management");
        assertTrue(page.content().contains("Teacher Management"));
    }
}