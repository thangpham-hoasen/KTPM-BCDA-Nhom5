package vn.edu.hoasen.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
}