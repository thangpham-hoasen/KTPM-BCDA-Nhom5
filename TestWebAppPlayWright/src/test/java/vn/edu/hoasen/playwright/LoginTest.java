package vn.edu.hoasen.playwright;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {

    @Test
    void testValidLogin() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.locator("vaadin-text-field input").first().fill("admin");
        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");

        page.waitForURL(BASE_URL + "/");
        assertTrue(page.url().equals(BASE_URL + "/"));
    }

    @Test
    void testInvalidLogin() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.locator("vaadin-text-field input").first().fill("invalid");
        page.locator("vaadin-password-field input").fill("invalid");
        page.click("vaadin-button:has-text('Login')");

        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testEmptyCredentials() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.click("vaadin-button:has-text('Login')");

        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testEmptyUsername() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.locator("vaadin-password-field input").fill("admin123");
        page.click("vaadin-button:has-text('Login')");

        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }

    @Test
    void testEmptyPassword() {
        page.navigate(BASE_URL + "/login");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.locator("vaadin-text-field input").first().fill("admin");
        page.click("vaadin-button:has-text('Login')");

        page.waitForSelector("vaadin-notification");
        assertTrue(page.isVisible("vaadin-notification"));
    }
}