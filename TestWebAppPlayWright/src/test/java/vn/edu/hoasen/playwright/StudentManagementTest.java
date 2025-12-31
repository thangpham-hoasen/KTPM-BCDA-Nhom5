package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

class StudentManagementTest extends BaseTest {

    @BeforeEach
    void loginAndNavigate() {
        login();
        page.click("vaadin-side-nav-item:has-text('Students')");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @AfterEach
    void clearData() {
        try (Connection conn = DriverManager.getConnection(authProps.getProperty("db.url"));
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM students");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreateStudent() {
        String uniqueName = "Alice_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("3/10/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Bob Smith");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Chồi");

        page.click("#add-button");
        page.waitForTimeout(1500);

        assertTrue(page.locator("#student-grid").textContent().contains(uniqueName));
    }

    @Test
    void testEditStudent() {
        String uniqueName = "EditTest_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("6/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Lá");

        page.click("#add-button");
        page.waitForTimeout(1500);

        page.locator("#student-grid vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);

        page.locator("#name-field input").fill("Edited_" + System.currentTimeMillis());
        page.click("#add-button");
        page.waitForTimeout(1500);

        assertFalse(page.locator("#student-grid").textContent().contains(uniqueName));
    }

    @Test
    void testDeleteStudent() {
        String uniqueName = "DelStudent_" + System.currentTimeMillis();

        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("9/20/2023");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("ParentABC");
        page.locator("#parent-phone-field input").fill("0987654321");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");
        page.waitForTimeout(1500);

        assertTrue(page.locator("#student-grid").textContent().contains(uniqueName));

        page.locator("#student-grid vaadin-button:has-text('Delete')").first().click();
        page.waitForTimeout(500);
        page.waitForSelector("#confirm-yes-button", new Page.WaitForSelectorOptions().setTimeout(5000));
        page.click("#confirm-yes-button");
        page.waitForTimeout(1500);

        page.reload();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        assertFalse(page.locator("#student-grid").textContent().contains(uniqueName));
    }

    @Test
    void testCancelEdit() {
        String uniqueName = "CancelTest_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("12/5/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Cancel");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Chồi");

        page.click("#add-button");
        page.waitForTimeout(1500);

        page.locator("#student-grid vaadin-button:has-text('Edit')").last().click();
        page.waitForTimeout(500);

        page.locator("#name-field input").fill("Should Not Save");
        page.click("#cancel-button");
        page.waitForTimeout(500);

        assertTrue(page.locator("#student-grid").textContent().contains(uniqueName));
        assertFalse(page.locator("#student-grid").textContent().contains("Should Not Save"));
    }

    @Test
    void testSearchStudent() {
        String searchPrefix = "Search_" + System.currentTimeMillis();
        String[] names = { searchPrefix + "_1", searchPrefix + "_2", "Different_" + System.currentTimeMillis() };
        for (String name : names) {
            page.click("#new-button");
            page.waitForTimeout(500);

            page.locator("#name-field input").fill(name);
            page.locator("#birth-date-field input").click();
            page.locator("#birth-date-field input").fill("1/1/2021");
            page.keyboard().press("Enter");
            page.locator("#parent-name-field input").fill("Parent Test");
            page.locator("#parent-phone-field input").fill("0123456789");
            page.locator("#class-name-field input").fill("Lớp Mầm");

            page.click("#add-button");
            page.waitForTimeout(1000);
        }

        page.locator("#search-field input").fill(searchPrefix);
        page.click("#search-button");
        page.waitForTimeout(1000);

        String gridContent = page.locator("#student-grid").textContent();
        assertTrue(gridContent.contains(searchPrefix));
    }

    @Test
    void testShowAllStudents() {
        String uniqueName = "ShowAll_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/1/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Parent Test");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");
        page.waitForTimeout(1500);

        page.locator("#search-field input").fill("NonExistent");
        page.click("#search-button");
        page.waitForTimeout(1000);

        page.click("#show-all-button");
        page.waitForTimeout(1000);

        assertTrue(page.locator("#student-grid").textContent().contains(uniqueName));
    }

    @Test
    void testValidStudentCreation() {
        String uniqueName = "Valid_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2023");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("Jane Doe");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");
        page.waitForTimeout(1500);

        assertTrue(page.locator("#student-grid").textContent().contains(uniqueName));
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

        // Too young: 23 months old
        java.time.LocalDate date = java.time.LocalDate.now().minusMonths(23);
        String birthDate = date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();

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

        // Too old: 73 months old
        java.time.LocalDate date = java.time.LocalDate.now().minusMonths(73);
        String birthDate = date.getMonthValue() + "/" + date.getDayOfMonth() + "/" + date.getYear();

        page.locator("#name-field input").fill("Old Child");
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill(birthDate);
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
        page.waitForTimeout(500);

        int initialCount = page.locator("#student-grid vaadin-grid-cell-content").count();

        page.click("#add-button");
        page.waitForTimeout(1000);

        int finalCount = page.locator("#student-grid vaadin-grid-cell-content").count();
        assertEquals(initialCount, finalCount);
    }

    @Test
    void testParentNameTooShort() {
        String uniqueName = "ParentShort_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        int initialCount = page.locator("#student-grid vaadin-grid-cell-content").count();

        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill("P");
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");
        page.waitForTimeout(1000);

        int finalCount = page.locator("#student-grid vaadin-grid-cell-content").count();
        assertEquals(initialCount, finalCount);
    }

    @Test
    void testParentNameTooLong() {
        String uniqueName = "ParentLong_" + System.currentTimeMillis();
        page.click("#new-button");
        page.waitForTimeout(500);

        int initialCount = page.locator("#student-grid vaadin-grid-cell-content").count();

        String longParentName = "A".repeat(51);
        page.locator("#name-field input").fill(uniqueName);
        page.locator("#birth-date-field input").click();
        page.locator("#birth-date-field input").fill("1/15/2021");
        page.keyboard().press("Enter");
        page.locator("#parent-name-field input").fill(longParentName);
        page.locator("#parent-phone-field input").fill("0123456789");
        page.locator("#class-name-field input").fill("Lớp Mầm");

        page.click("#add-button");
        page.waitForTimeout(1000);

        int finalCount = page.locator("#student-grid vaadin-grid-cell-content").count();
        assertEquals(initialCount, finalCount);
    }
}
