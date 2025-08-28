package com.sahabatquran.webapp.functional.playwright;

import com.microsoft.playwright.*;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for Playwright functional tests.
 * Provides common setup and teardown for Playwright browser automation.
 * 
 * Extends BaseIntegrationTest to reuse PostgreSQL TestContainer setup.
 * 
 * Comparison with Selenium:
 * - Faster execution and more reliable element detection
 * - Built-in waiting mechanisms 
 * - Better debugging capabilities
 * - Modern async API
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BasePlaywrightTest extends BaseIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    private static Playwright playwright;
    private static Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    @BeforeAll
    static void setUpPlaywright() {
        playwright = Playwright.create();
        
        // Configuration from system properties
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        boolean recording = Boolean.parseBoolean(System.getProperty("playwright.recording", "false"));
        int slowMo = Integer.parseInt(System.getProperty("playwright.slowmo", "50"));
        
        System.out.println("🎭 Playwright Configuration:");
        System.out.println("   Headless: " + headless);
        System.out.println("   Recording: " + recording);
        System.out.println("   Slow Motion: " + slowMo + "ms");
        
        // Use Chromium for consistency, but Playwright supports Firefox and Safari too
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo); // Delay between actions for stability/visibility
                
        browser = playwright.chromium().launch(launchOptions);
    }
    
    @AfterAll
    static void tearDownPlaywright() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    @BeforeEach
    void setUpTest() {
        if (browser == null) {
            throw new IllegalStateException("Playwright browser is not initialized. Make sure @BeforeAll setUpPlaywright() ran successfully.");
        }
        
        // Configuration from system properties
        boolean recording = Boolean.parseBoolean(System.getProperty("playwright.recording", "false"));
        
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1280, 720);
                
        // Enable recording if requested
        if (recording) {
            String recordingDir = System.getProperty("playwright.recording.dir", "target/playwright-recordings");
            contextOptions.setRecordVideoDir(java.nio.file.Paths.get(recordingDir))
                          .setRecordVideoSize(1280, 720);
            System.out.println("📹 Recording enabled - videos will be saved to: " + recordingDir);
        }
        
        context = browser.newContext(contextOptions);
        page = context.newPage();
        
        // Enable request/response logging for debugging
        page.onRequest(request -> System.out.println(">> " + request.method() + " " + request.url()));
        page.onResponse(response -> System.out.println("<< " + response.status() + " " + response.url()));
        
        // Wait for application to be ready - navigate to login page to verify app is up
        try {
            page.navigate(getBaseUrl() + "/login");
            page.waitForLoadState();
        } catch (Exception e) {
            System.err.println("Failed to navigate to application: " + e.getMessage());
            // Continue anyway - individual tests can handle navigation
        }
    }
    
    @AfterEach
    void tearDownTest() {
        if (context != null) {
            context.close();
        }
    }
    
    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
    
    protected void loginAsAdmin() {
        page.navigate(getBaseUrl() + "/login");
        page.fill("#username", "admin");
        page.fill("#password", "AdminYSQ@2024");
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
    
    protected void loginAsInstructor() {
        page.navigate(getBaseUrl() + "/login");
        page.fill("#username", "ustadz.ahmad");
        page.fill("#password", "Welcome@YSQ2024");
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
    
    protected void loginAsStudent() {
        page.navigate(getBaseUrl() + "/login");
        page.fill("#username", "siswa.ali");
        page.fill("#password", "Welcome@YSQ2024");
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
}