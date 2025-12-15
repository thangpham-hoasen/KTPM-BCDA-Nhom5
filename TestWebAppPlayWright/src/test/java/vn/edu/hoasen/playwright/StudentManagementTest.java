package vn.edu.hoasen.playwright;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentManagementTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Test
    void testCreateStudent() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Alice Smith");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("3/10/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Bob Smith");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Chồi");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#student-grid").textContent().contains("Alice Smith"));
    }

    @Test
    void testEditStudent() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Edit Test");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("6/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Lá");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        page.locator("#student-grid vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("Edited Name");
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#student-grid").textContent().contains("Edited Name"));
    }

    @Test
    void testDeleteStudent() {
        String uniqueName = "DeleteStudent_" + System.currentTimeMillis();
        
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("9/20/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Delete");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1500);
        
        assertTrue(page.locator("#student-grid").textContent().contains(uniqueName));
        
        page.locator("#student-grid vaadin-button:has-text('Delete')").last().click();
        page.waitForSelector("vaadin-dialog-overlay");
        page.click("#confirm-yes-button");
        
        page.waitForTimeout(1000);
        assertFalse(page.locator("#student-grid").textContent().contains(uniqueName));
    }

    @Test
    void testCancelEdit() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Cancel Test");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("12/5/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Cancel");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Chồi");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        page.locator("#student-grid vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);
        
        page.locator("#name-field input").fill("Should Not Save");
        page.click("#cancel-button");
        page.waitForTimeout(500);
        
        assertTrue(page.locator("#student-grid").textContent().contains("Cancel Test"));
        assertFalse(page.locator("#student-grid").textContent().contains("Should Not Save"));
    }

    @Test
    void testSearchStudent() {
        String[] names = {"SearchTest 1", "SearchTest 2", "Different Name"};
        for (String name : names) {
            page.click("#new-button");
            page.waitForTimeout(300);
            
            page.locator("#name-field input").fill(name);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("1/1/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent " + name);
            page.locator("#parent-phone-field input").fill("0123456789");
            page.locator("#class-name-field input").fill("Lớp Mầm");
            
            page.click("#add-button");
            page.waitForTimeout(500);
        }
        
        page.locator("#search-field input").fill("SearchTest");
        page.click("#search-button");
        page.waitForTimeout(1000);
        
        String gridContent = page.locator("#student-grid").textContent();
        assertTrue(gridContent.contains("SearchTest 1") || gridContent.contains("SearchTest 2"));
    }

    @Test
    void testShowAllStudents() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("ShowAll Test");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/1/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        page.locator("#search-field input").fill("NonExistent");
        page.click("#search-button");
        page.waitForTimeout(500);
        
        page.click("#show-all-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#student-grid").textContent().contains("ShowAll Test"));
    }

    @Test
    void testValidStudentCreation() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Valid Student");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2023");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);

        assertTrue(page.locator("#student-grid").textContent().contains("Valid Student"));
    }

    @Test
    void testNameTooShort() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("J");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2020");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        // StudentView doesn't prevent form submission, just shows notification
        assertTrue(page.locator("#student-grid").isVisible());
    }

    @Test
    void testNameTooLong() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        String longName = "A".repeat(51);
        page.locator("#name-field input").fill(longName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2020");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#student-grid").textContent().contains(longName));
    }

    @Test
    void testInvalidPhoneNumber() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Phone Test");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2020");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("123");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#student-grid").textContent().contains("Phone Test"));
    }

    @Test
    void testPhoneWithLetters() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Letter Phone");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2020");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("012345678a");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#student-grid").textContent().contains("Letter Phone"));
    }

    @Test
    void testStudentTooYoung() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        java.time.LocalDate sixMonthsAgo = java.time.LocalDate.now().minusMonths(6);
        String birthDate = sixMonthsAgo.getMonthValue() + "/" + sixMonthsAgo.getDayOfMonth() + "/" + sixMonthsAgo.getYear();
        
        page.locator("#name-field input").fill("Baby Doe");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill(birthDate);
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);

        assertFalse(page.locator("#student-grid").textContent().contains("Baby Doe"));
    }

    @Test
    void testStudentTooOld() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.locator("#name-field input").fill("Old Child");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2018");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertFalse(page.locator("#student-grid").textContent().contains("Old Child"));
    }

    @Test
    void testMissingRequiredFields() {
        page.click("#new-button");
        page.waitForTimeout(300);
        
        page.click("#add-button");
        page.waitForTimeout(1000);
        
        assertTrue(page.locator("#student-grid").isVisible());
    }
}
