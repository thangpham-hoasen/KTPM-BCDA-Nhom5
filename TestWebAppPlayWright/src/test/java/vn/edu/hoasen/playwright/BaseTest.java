package vn.edu.hoasen.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;
    protected static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}