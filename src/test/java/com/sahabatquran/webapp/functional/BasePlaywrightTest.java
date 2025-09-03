package com.sahabatquran.webapp.functional;

import com.microsoft.playwright.*;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for Playwright functional tests.
 * Provides common setup and teardown for Playwright browser automation.
 * 
 * Extends BaseIntegrationTest to reuse PostgreSQL TestContainer setup.
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BasePlaywrightTest extends BaseIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    private static Playwright playwright;
    private static Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    // Recording-related fields
    private java.nio.file.Path currentRecordingPath;
    private String currentRecordingName;
    
    @BeforeAll
    static void setUpPlaywright() {
        playwright = Playwright.create();
        
        // Configuration from system properties
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        boolean recording = Boolean.parseBoolean(System.getProperty("playwright.recording", "false"));
        int slowMo = Integer.parseInt(System.getProperty("playwright.slowmo", "50"));
        
        log.info("🎭 Playwright Configuration:");
        log.info("   Headless: {}", headless);
        log.info("   Recording: {}", recording);
        log.info("   Slow Motion: {}ms", slowMo);
        
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
    void setUpTest(TestInfo testInfo) {
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
            
            // Generate recording name based on test class and method
            String testClassName = testInfo.getTestClass()
                    .map(Class::getSimpleName)
                    .orElse("UnknownClass");
            String testMethodName = testInfo.getTestMethod()
                    .map(method -> method.getName())
                    .orElse("unknownMethod");
            String displayName = testInfo.getDisplayName();
            
            // Create timestamp for uniqueness
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            
            // Build the recording filename
            // Format: ClassName_MethodName_Timestamp
            // If display name is different from method name, include it
            String recordingName;
            if (!displayName.equals(testMethodName + "()")) {
                // Clean display name for file system (remove special chars)
                String cleanDisplayName = displayName
                        .replaceAll("[^a-zA-Z0-9\\s-]", "")
                        .replaceAll("\\s+", "_")
                        .replaceAll("^_+|_+$", ""); // Remove leading/trailing underscores
                recordingName = String.format("%s_%s_%s_%s", 
                        testClassName, testMethodName, cleanDisplayName, timestamp);
            } else {
                recordingName = String.format("%s_%s_%s", 
                        testClassName, testMethodName, timestamp);
            }
            
            // Create subdirectory for this test class if it doesn't exist
            java.nio.file.Path classDir = java.nio.file.Paths.get(recordingDir, testClassName);
            try {
                java.nio.file.Files.createDirectories(classDir);
            } catch (java.io.IOException e) {
                log.warn("Failed to create recording directory: {}", classDir, e);
            }
            
            contextOptions.setRecordVideoDir(classDir)
                          .setRecordVideoSize(1280, 720);
            
            // Store recording information for later use
            currentRecordingPath = classDir;
            currentRecordingName = recordingName;
            
            log.info("📹 Recording enabled for test: {}", recordingName);
            log.info("   📁 Video will be saved to: {}", classDir);
        }
        
        context = browser.newContext(contextOptions);
        page = context.newPage();
        
        // Enable request/response logging for debugging (configurable)
        boolean enableHttpLogging = Boolean.parseBoolean(System.getProperty("playwright.http.logging", "false"));
        if (enableHttpLogging) {
            log.debug("🔍 HTTP request/response logging enabled");
            page.onRequest(request -> log.debug(">> {} {}", request.method(), request.url()));
            page.onResponse(response -> log.debug("<< {} {}", response.status(), response.url()));
        }
        
        // Wait for application to be ready - navigate to login page to verify app is up
        try {
            page.navigate(getBaseUrl() + "/login");
            page.waitForLoadState();
        } catch (Exception e) {
            log.warn("Failed to navigate to application: {}", e.getMessage());
            // Continue anyway - individual tests can handle navigation
        }
    }
    
    @AfterEach
    void tearDownTest(TestInfo testInfo) {
        if (context != null) {
            // Get the video path before closing context
            java.nio.file.Path videoPath = null;
            if (currentRecordingPath != null && currentRecordingName != null && page != null) {
                try {
                    videoPath = page.video().path();
                } catch (Exception e) {
                    log.debug("No video available for this test: {}", e.getMessage());
                }
            }
            
            context.close();
            
            // Rename video file if recording was enabled
            if (videoPath != null && java.nio.file.Files.exists(videoPath)) {
                try {
                    // Determine test result for naming
                    boolean testPassed = !testInfo.getTestMethod()
                            .map(method -> method.isAnnotationPresent(org.junit.jupiter.api.Disabled.class))
                            .orElse(false);
                    
                    String resultSuffix = testPassed ? "PASSED" : "FAILED";
                    String newFileName = String.format("%s_%s.webm", currentRecordingName, resultSuffix);
                    java.nio.file.Path newPath = currentRecordingPath.resolve(newFileName);
                    
                    // Rename the video file
                    java.nio.file.Files.move(videoPath, newPath, 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    log.info("📹 Recording saved: {}", newPath);
                    
                    // Log file size for monitoring
                    long sizeInMB = java.nio.file.Files.size(newPath) / (1024 * 1024);
                    log.info("   📊 Video size: {} MB", sizeInMB);
                    
                } catch (Exception e) {
                    log.warn("Failed to rename video file: {}", e.getMessage());
                }
            }
            
            // Reset recording fields
            currentRecordingPath = null;
            currentRecordingName = null;
        }
    }
    
    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
    
    protected Browser getBrowser() {
        return browser;
    }
    
    protected void loginAsAdmin() {
        // Login as Academic Admin for most test scenarios
        page.navigate(getBaseUrl() + "/login");
        page.fill("#username", "academic.admin1");
        page.fill("#password", "Welcome@YSQ2024");
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
    
    protected void loginAsManagement() {
        page.navigate(getBaseUrl() + "/login");
        page.fill("#username", "management.director");
        page.fill("#password", "Welcome@YSQ2024");
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