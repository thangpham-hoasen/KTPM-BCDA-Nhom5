package vn.edu.hoasen.playwright;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;
    protected static final String BASE_URL = "http://localhost:8080";
    protected static Properties authProps;
    
    static {
        authProps = new Properties();
        try (InputStream input = BaseTest.class.getClassLoader().getResourceAsStream("auth.properties")) {
            authProps.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load auth.properties", e);
        }
    }

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(50));
        page = browser.newPage();
        page.setDefaultTimeout(30000);
    }

    @AfterEach
    void tearDown() {
        try {
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    protected void login() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        page.locator("#username-field input").fill(authProps.getProperty("admin.username"));
        page.locator("#password-field input").fill(authProps.getProperty("admin.password"));
        page.click("#login-button");
        page.waitForURL(BASE_URL + "/");
    }
    protected void fillDatePickerByAgeMonths(String datePickerInputSelector, int ageInMonths) {
        LocalDate dob = LocalDate.now().minusMonths(ageInMonths);
        String dateStr = dob.getMonthValue() + "/" + dob.getDayOfMonth() + "/" + dob.getYear();
        page.locator(datePickerInputSelector).click();
        page.locator(datePickerInputSelector).fill(dateStr);
        page.keyboard().press("Enter");
    }
        /** Create a student through UI. className is auto-suggested/read-only in the updated UI. */
    protected void createStudentViaUI(String studentName, int ageInMonths, String parentName, String parentPhone) {
        page.click("#new-button");
        page.waitForTimeout(300);
        page.locator("#name-field input").fill(studentName);
        fillDatePickerByAgeMonths("#birth-date-field input", ageInMonths);
        page.locator("#parent-name-field input").fill(parentName);
        page.locator("#parent-phone-field input").fill(parentPhone);

        // Wait a moment for auto-suggest class name to appear (read-only field)
        page.waitForTimeout(150);

        page.click("#add-button");
        page.waitForTimeout(1200);
    }
}