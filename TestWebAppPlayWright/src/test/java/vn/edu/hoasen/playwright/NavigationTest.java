package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NavigationTest extends BaseTest {

    @Test
    void testCompleteNavigation() {
        // Start at Students page
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Student Management"));
        
        // Navigate to Courses
        page.locator("a:has-text('Manage Courses')").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForSelector("h1:has-text('Course Management')", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(page.content().contains("Course Management"));
        
        // Navigate to Teachers
        page.locator("a:has-text('Manage Teachers')").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForSelector("h1:has-text('Teacher Management')", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(page.content().contains("Teacher Management"));
        
        // Navigate back to Students
        page.locator("a:has-text('Manage Students')").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForSelector("h1:has-text('Student Management')", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(page.content().contains("Student Management"));
    }

    @Test
    void testPageTitles() {
        // Test Students page
        page.navigate(BASE_URL);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Student Management"));
        
        // Test Courses page
        page.navigate(BASE_URL + "/courses");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Course Management"));
        
        // Test Teachers page
        page.navigate(BASE_URL + "/teachers");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(page.content().contains("Teacher Management"));
    }
}