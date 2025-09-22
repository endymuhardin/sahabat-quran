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
    private java.util.List<String> consoleErrors;
    private java.util.List<String> serverErrors;
    
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
                .setViewportSize(1280, 720)
                .setAcceptDownloads(true); // Enable downloads for PDF testing
                
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
        consoleErrors = new java.util.ArrayList<>();
        serverErrors = new java.util.ArrayList<>();

        // Reset log monitoring for this test
        TestLogMonitor.reset();

        // Capture console errors to fail tests when client-side errors occur
        page.onConsoleMessage(msg -> {
            // Compare by name to be resilient to enum packaging differences across versions
            String typeName = String.valueOf(msg.type());
            if ("error".equalsIgnoreCase(typeName)) {
                consoleErrors.add(msg.text());
            }
        });

        // Capture server-side errors including LazyInitializationException
        page.onResponse(response -> {
            try {
                // Check for server errors in response status or content
                if (response.status() >= 500) {
                    String errorMsg = String.format("Server error: %d %s for %s",
                        response.status(), response.statusText(), response.url());
                    serverErrors.add(errorMsg);
                }

                // Check response body for specific exceptions if it's HTML/text
                String contentType = response.headers().get("content-type");
                if (contentType != null && (contentType.contains("text/html") || contentType.contains("text/plain"))) {
                    String responseText = response.text();

                    // Look for LazyInitializationException in response
                    if (responseText.contains("LazyInitializationException") ||
                        responseText.contains("failed to lazily initialize") ||
                        responseText.contains("could not initialize proxy")) {
                        serverErrors.add("LazyInitializationException detected in response: " + response.url());
                    }

                    // Look for other Hibernate exceptions
                    if (responseText.contains("HibernateException") ||
                        responseText.contains("org.hibernate")) {
                        serverErrors.add("Hibernate exception detected in response: " + response.url());
                    }

                    // Look for Spring transaction exceptions
                    if (responseText.contains("TransactionRequiredException") ||
                        responseText.contains("No Session found for current thread")) {
                        serverErrors.add("Transaction/Session exception detected in response: " + response.url());
                    }
                }
            } catch (Exception e) {
                // Ignore errors in error detection to avoid recursive issues
                log.debug("Error while checking response for server errors: {}", e.getMessage());
            }
        });

        // Set up custom log appender to monitor application logs for exceptions
        setupLogMonitoring();
        
        // Set timeout for navigation operations (configurable, shorter for local dev)
        int navigationTimeout = Integer.parseInt(System.getProperty("playwright.navigation.timeout", "15000")); // 15 seconds default, was 60
        page.setDefaultNavigationTimeout(navigationTimeout);
        log.debug("🕐 Navigation timeout set to: {}ms", navigationTimeout);
        
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

        // Verify we're logged in successfully by checking welcome message element exists
        page.waitForSelector("#welcome-message", new Page.WaitForSelectorOptions().setTimeout(10000));
    }

    // Helper: Navigate and assert HTTP 200 OK. Fails fast on 4xx/5xx and includes a short page content preview.
    protected Response navigateAndAssertOk(String path) {
        Response response = page.navigate(getBaseUrl() + path);
        if (response == null || !response.ok()) {
            String preview;
            try {
                String content = page.content();
                preview = content.substring(0, Math.min(1500, content.length()));
            } catch (Exception e) {
                preview = "<no content available>";
            }
            throw new AssertionError("Navigation failed for " + path +
                ": status=" + (response == null ? "<null>" : response.status()) +
                ", url=" + (response == null ? "<null>" : response.url()) +
                "\nPage preview:\n" + preview);
        }
        // Also assert no console errors so far
        assertNoConsoleErrors();
        return response;
    }

    protected void assertNoConsoleErrors() {
        java.util.List<String> allErrors = new java.util.ArrayList<>();

        if (consoleErrors != null && !consoleErrors.isEmpty()) {
            allErrors.addAll(consoleErrors.stream()
                .map(error -> "Console: " + error)
                .collect(java.util.stream.Collectors.toList()));
        }

        if (serverErrors != null && !serverErrors.isEmpty()) {
            allErrors.addAll(serverErrors.stream()
                .map(error -> "Server: " + error)
                .collect(java.util.stream.Collectors.toList()));
        }

        if (!allErrors.isEmpty()) {
            throw new AssertionError("Errors detected: " + String.join(" | ", allErrors));
        }
    }

    protected void assertNoServerErrors() {
        // Check for logged exceptions before asserting
        checkForLoggedExceptions();

        if (serverErrors != null && !serverErrors.isEmpty()) {
            throw new AssertionError("Server errors detected: " + String.join(" | ", serverErrors));
        }
    }

    private void setupLogMonitoring() {
        // Set up the custom log appender to monitor application logs
        ch.qos.logback.classic.Logger rootLogger =
            (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        TestLogMonitor logMonitor = new TestLogMonitor();
        logMonitor.setName("TestLogMonitor");
        logMonitor.setContext(rootLogger.getLoggerContext());
        logMonitor.start();

        rootLogger.addAppender(logMonitor);
        log.debug("🔍 Test log monitoring enabled");
    }

    protected void checkForLoggedExceptions() {
        // Check if the log monitor detected any exceptions
        if (TestLogMonitor.hasDetectedExceptions()) {
            java.util.List<String> loggedExceptions = TestLogMonitor.getDetectedExceptions();
            serverErrors.addAll(loggedExceptions);
        }
    }
    
    protected void loginAsStudent() {
        page.navigate(getBaseUrl() + "/login");
        page.fill("#username", "siswa.ali");
        page.fill("#password", "Welcome@YSQ2024");
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
}