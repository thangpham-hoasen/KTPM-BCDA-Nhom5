package vn.edu.hoasen.playwright;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseManagementTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        page.click("vaadin-side-nav-item:has-text('📚 Courses')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(500);
    }

    @Test
    void testCreateCourse() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Mathematics 101");
        page.locator("#description-field textarea").fill("Basic mathematics course");
        page.locator("#teacher-name-field input").fill("John Smith");
        page.locator("#duration-field input").fill("40");
        page.locator("#schedule-field input").fill("Mon, Wed, Fri");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertTrue(page.locator("#course-grid").isVisible());
        String gridContent = page.locator("#course-grid").textContent();
        assertTrue(gridContent.contains("Mathematics 101"));
        assertTrue(gridContent.contains("Basic mathematics course"));
        assertTrue(gridContent.contains("John Smith"));
        assertTrue(gridContent.contains("40"));
        assertTrue(gridContent.contains("Mon, Wed, Fri"));
    }

    @Test
    void testEditCourse() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Original Course Name");
        page.locator("#description-field textarea").fill("Course to edit");
        page.locator("#teacher-name-field input").fill("Edit Teacher");
        page.locator("#duration-field input").fill("30");
        page.locator("#schedule-field input").fill("Tue, Thu");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#course-grid").textContent().contains("Original Course Name"));
        
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("");
        page.locator("#name-field input").fill("Updated Course Name");
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        String gridContent = page.locator("#course-grid").textContent();
        assertTrue(gridContent.contains("Updated Course Name"));
        assertTrue(gridContent.contains("Course to edit"));
    }

    @Test
    void testDeleteCourse() {
        String uniqueName = "DeleteCourse_" + System.currentTimeMillis();
        
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill(uniqueName);
        page.locator("#description-field textarea").fill("To be deleted");
        page.locator("#teacher-name-field input").fill("Delete Teacher");
        page.locator("#duration-field input").fill("25");
        page.locator("#schedule-field input").fill("Mon");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);
        
        String beforeDelete = page.locator("#course-grid").textContent();
        assertTrue(beforeDelete.contains(uniqueName));
        
        page.locator("#course-grid vaadin-button:has-text('Delete')").last().click();
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(2000);
        String afterDelete = page.locator("#course-grid").textContent();
        assertFalse(afterDelete.contains(uniqueName));
    }

    @Test
    void testCancelEdit() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Cancel Course");
        page.locator("#description-field textarea").fill("Cancel edit");
        page.locator("#teacher-name-field input").fill("Cancel Teacher");
        page.locator("#duration-field input").fill("35");
        page.locator("#schedule-field input").fill("Wed");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#course-grid").textContent().contains("Cancel Course"));
        
        page.click("vaadin-button:has-text('Edit')");
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("");
        page.locator("#name-field input").fill("Should Not Save");
        page.click("#cancel-button");
        
        page.waitForTimeout(500);
        assertTrue(page.locator("#course-grid").isVisible());
        String gridContent = page.locator("#course-grid").textContent();
        assertTrue(gridContent.contains("Cancel Course"));
        assertFalse(gridContent.contains("Should Not Save"));
        assertFalse(page.locator("#cancel-button").isVisible());
    }

    @Test
    void testSearchCourse() {
        String[] names = {"SearchABC Course 1", "SearchABC Course 2", "Different Name"};
        
        for (int i = 0; i < names.length; i++) {
            page.click("#new-button");
            page.waitForTimeout(300);
            
            page.locator("#name-field input").fill(names[i]);
            page.locator("#description-field textarea").fill("Desc " + (i+1));
            page.locator("#teacher-name-field input").fill("Teacher " + (i+1));
            page.locator("#duration-field input").fill(String.valueOf(20 + i * 5));
            page.locator("#schedule-field input").fill("Schedule " + (i+1));
            
            page.click("#add-button");
            page.waitForSelector("vaadin-dialog-overlay");
            page.click("#confirm-yes-button");
            page.waitForTimeout(1000);
        }
        
        String allContent = page.locator("#course-grid").textContent();
        assertTrue(allContent.contains("SearchABC Course 1"));
        assertTrue(allContent.contains("SearchABC Course 2"));
        assertTrue(allContent.contains("Different Name"));
        
        page.locator("#search-field input").fill("SearchABC");
        page.click("#search-button");
        
        page.waitForTimeout(1000);
        String gridContent = page.locator("#course-grid").textContent();
        assertTrue(gridContent.contains("SearchABC Course 1") || gridContent.contains("SearchABC Course 2"));
    }

    @Test
    void testShowAllCourses() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("UniqueShowAllCourse999");
        page.locator("#description-field textarea").fill("Show all test");
        page.locator("#teacher-name-field input").fill("Show All Teacher");
        page.locator("#duration-field input").fill("45");
        page.locator("#schedule-field input").fill("Fri");
        
        page.click("#add-button");
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);
        
        String initialContent = page.locator("#course-grid").textContent();
        assertTrue(initialContent.contains("UniqueShowAllCourse999"));
        
        page.locator("#search-field input").fill("NonExistentXYZ123");
        page.click("#search-button");
        page.waitForTimeout(1500);
        
        page.click("#show-all-button");
        page.waitForTimeout(1500);
        
        assertTrue(page.locator("#course-grid").isVisible());
        String allContent = page.locator("#course-grid").textContent();
        assertTrue(allContent.contains("UniqueShowAllCourse999"));
    }
}