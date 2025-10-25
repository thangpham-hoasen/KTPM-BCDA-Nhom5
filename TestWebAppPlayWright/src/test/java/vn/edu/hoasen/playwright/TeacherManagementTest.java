package vn.edu.hoasen.playwright;

import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TeacherManagementTest extends BaseTest {

    @Test
    void testAddTeacher() {
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Fill teacher form
        page.locator("vaadin-text-field:has([slot='label']:text('Name')) input").fill("Jane Smith");
        page.locator("vaadin-email-field:has([slot='label']:text('Email')) input").fill("jane@school.com");
        page.locator("vaadin-text-field:has([slot='label']:text('Phone')) input").fill("987654321");
        page.locator("vaadin-text-field:has([slot='label']:text('Subject')) input").fill("Mathematics");
        
        // Click Add Teacher button
        page.click("text=Add Teacher");
        
        // Verify teacher appears in grid
        page.waitForSelector("text=Jane Smith");
        assertTrue(page.content().contains("Jane Smith"));
    }

    @Test
    void testSearchTeacher() {
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Add a teacher first
        page.locator("vaadin-text-field:has([slot='label']:text('Name')) input").fill("Test Teacher");
        page.locator("vaadin-email-field:has([slot='label']:text('Email')) input").fill("test@school.com");
        page.locator("vaadin-text-field:has([slot='label']:text('Subject')) input").fill("Test Subject");
        page.click("text=Add Teacher");
        
        // Search for the teacher
        page.locator("vaadin-text-field:has([slot='label']:text('Search')) input").fill("Test");
        page.click("text=Search");
        
        // Verify search results
        page.waitForSelector("text=Test Teacher");
        assertTrue(page.content().contains("Test Teacher"));
    }

    @Test
    void testNavigationToStudents() {
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        page.click("text=Manage Students");
        page.waitForSelector("text=Student Management");
        assertTrue(page.content().contains("Student Management"));
    }
}