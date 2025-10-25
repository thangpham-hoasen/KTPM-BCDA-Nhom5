package vn.edu.hoasen.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentValidationTest extends BaseTest {

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
    void testValidStudentCreation() {
        page.locator("vaadin-text-field[label='Name'] input").fill("John Doe");
        page.locator("vaadin-date-picker input").fill("2020-01-15");
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
    void testNameTooShort() {
        page.locator("vaadin-text-field[label='Name'] input").fill("J");
        page.locator("vaadin-date-picker input").fill("2020-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testNameTooLong() {
        String longName = "A".repeat(51);
        page.locator("vaadin-text-field[label='Name'] input").fill(longName);
        page.locator("vaadin-date-picker input").fill("2020-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testInvalidPhoneNumber() {
        page.locator("vaadin-text-field[label='Name'] input").fill("John Doe");
        page.locator("vaadin-date-picker input").fill("2020-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("123");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testPhoneWithLetters() {
        page.locator("vaadin-text-field[label='Name'] input").fill("John Doe");
        page.locator("vaadin-date-picker input").fill("2020-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("012345678a");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testStudentTooYoung() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Baby Doe");
        page.locator("vaadin-date-picker input").fill("2023-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testStudentTooOld() {
        page.locator("vaadin-text-field[label='Name'] input").fill("Old Child");
        page.locator("vaadin-date-picker input").fill("2018-01-15");
        page.locator("vaadin-text-field[label='Parent Name'] input").fill("Jane Doe");
        page.locator("vaadin-text-field[label='Parent Phone'] input").fill("0123456789");
        page.locator("vaadin-text-field[label='Class Name'] input").fill("Lớp Mầm");
        
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testMissingRequiredFields() {
        page.click("vaadin-button:has-text('Add Student')");
        
        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }
}