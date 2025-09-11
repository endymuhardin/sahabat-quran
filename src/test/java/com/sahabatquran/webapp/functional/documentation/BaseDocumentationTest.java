package com.sahabatquran.webapp.functional.documentation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.sahabatquran.webapp.functional.documentation.template.DocumentationTemplateEngine;
import com.sahabatquran.webapp.functional.documentation.template.WorkflowDocumentation;
import com.sahabatquran.webapp.functional.documentation.template.DocumentationSection;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for Documentation/User Manual generation tests.
 * 
 * These tests are specifically designed to create high-quality screenshots and videos
 * for user documentation, with slower pacing and clear visual demonstrations.
 * 
 * Key differences from regular functional tests:
 * - Much slower execution (2000ms delays) for clear visibility
 * - Automatic screenshot capture at key steps
 * - User-friendly logging and explanations
 * - Focus on complete user workflows rather than technical validation
 * - Optimized video recording for documentation purposes
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseDocumentationTest extends BaseIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    private static Playwright playwright;
    private static Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    // Documentation-specific fields
    private Path documentationDir;
    private String currentTestName;
    private int screenshotCounter;
    protected DocumentationCapture docCapture;
    
    // Template engine support
    protected DocumentationTemplateEngine templateEngine;
    protected WorkflowDocumentation currentWorkflow;
    
    @BeforeAll
    static void setUpPlaywright() {
        playwright = Playwright.create();
        
        log.info("📚 Documentation Test Configuration:");
        log.info("   Mode: Documentation/User Manual Generation");
        log.info("   Slow Motion: 2000ms (for clear visibility)");
        log.info("   Screenshots: Enabled");
        log.info("   Video Recording: Enabled");
        
        // Optimized for documentation: slower, visible, high quality
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(false) // Always visible for documentation
                .setSlowMo(2000); // Very slow for clear demonstration
                
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
    void setUpDocumentationTest(TestInfo testInfo) {
        if (browser == null) {
            throw new IllegalStateException("Playwright browser is not initialized.");
        }
        
        // Setup documentation directory structure
        setupDocumentationDirectory(testInfo);
        
        // Browser context optimized for documentation
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080) // Higher resolution for better screenshots
                .setRecordVideoDir(documentationDir)
                .setRecordVideoSize(1920, 1080); // High quality video
        
        context = browser.newContext(contextOptions);
        page = context.newPage();
        
        // Initialize screenshot counter and documentation capture
        screenshotCounter = 1;
        docCapture = new DocumentationCapture();
        
        // Initialize template engine
        templateEngine = new DocumentationTemplateEngine();
        
        // Start documentation session
        String testDescription = testInfo.getDisplayName();
        docCapture.startSession(currentTestName, testDescription);
        
        // Navigate to application and wait for stability
        try {
            page.navigate(getBaseUrl() + "/login");
            page.waitForLoadState();
            waitForStability();
        } catch (Exception e) {
            log.warn("Failed to navigate to application: {}", e.getMessage());
        }
        
        // Take initial screenshot
        takeScreenshot("00_initial_state", "Initial application state");
        
        log.info("📚 Documentation test setup completed for: {}", currentTestName);
    }
    
    @AfterEach
    void tearDownDocumentationTest(TestInfo testInfo) {
        if (context != null) {
            // Take final screenshot
            takeScreenshot("99_final_state", "Final application state");
            
            // Get video path before closing
            Path videoPath = null;
            try {
                if (page != null) {
                    videoPath = page.video().path();
                }
            } catch (Exception e) {
                log.debug("No video available: {}", e.getMessage());
            }
            
            context.close();
            
            // Rename and organize video file
            if (videoPath != null && Files.exists(videoPath)) {
                organizeVideoFile(videoPath, testInfo);
                // Set video path in documentation capture
                docCapture.setVideoPath(videoPath.toString());
            }
            
            // Save documentation session data
            try {
                Path docDataPath = documentationDir.resolve("documentation-session.json");
                docCapture.saveToFile(docDataPath);
            } catch (Exception e) {
                log.warn("Failed to save documentation session data: {}", e.getMessage());
            }
            
            log.info("📚 Documentation artifacts saved to: {}", documentationDir);
            log.info("   📸 Screenshots: {}", documentationDir.resolve("screenshots"));
            log.info("   📹 Video: {}", documentationDir.resolve("video"));
            log.info("   📄 Session Data: documentation-session.json");
        }
    }
    
    /**
     * Take a screenshot with user-friendly naming and description
     */
    protected void takeScreenshot(String stepName, String description) {
        try {
            String filename = String.format("%02d_%s.png", screenshotCounter++, stepName);
            Path screenshotPath = documentationDir.resolve("screenshots").resolve(filename);
            
            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath));
            
            log.info("📸 Screenshot {}: {} → {}", screenshotCounter - 1, description, filename);
        } catch (Exception e) {
            log.warn("Failed to take screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Wait for page stability (important for clear screenshots)
     */
    protected void waitForStability() {
        try {
            // Use DOMCONTENTLOADED instead of NETWORKIDLE to avoid timeout issues
            // NETWORKIDLE can timeout if there are continuous background requests
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            
            // Wait for any pending navigation to complete
            page.waitForTimeout(2000); // Wait for animations and dynamic content
        } catch (Exception e) {
            log.warn("waitForStability encountered an issue: {}", e.getMessage());
            // Continue anyway - page might still be usable
        }
    }
    
    /**
     * Demonstrate a user action with explanation
     */
    protected void demonstrateAction(String actionDescription, Runnable action) {
        log.info("👤 User Action: {}", actionDescription);
        
        // Execute the action
        action.run();
        
        // Wait for stability and take screenshot
        waitForStability();
        
        // Take screenshot with action description
        String stepName = actionDescription
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "_");
        takeScreenshot(stepName, actionDescription);
        
        // Add to documentation capture
        String screenshotPath = String.format("screenshots/%02d_%s.png", screenshotCounter - 1, stepName);
        docCapture.addAction(actionDescription, screenshotPath);
    }
    
    /**
     * Add explanatory text to logs (will be included in documentation)
     */
    protected void explain(String explanation) {
        log.info("💡 Explanation: {}", explanation);
        docCapture.addExplanation(explanation);
    }
    
    /**
     * Load workflow template data
     */
    protected void loadWorkflowTemplate(String workflowType) {
        try {
            currentWorkflow = templateEngine.loadWorkflowData(workflowType);
            log.info("📋 Loaded workflow template: {}", currentWorkflow.getTitle());
        } catch (Exception e) {
            log.warn("Failed to load workflow template for: {}, falling back to hardcoded text", workflowType);
            currentWorkflow = null;
        }
    }
    
    /**
     * Start section using template data
     */
    protected void startTemplateSection(String sectionId) {
        if (currentWorkflow != null) {
            try {
                DocumentationSection section = templateEngine.getSection(currentWorkflow, sectionId);
                startSection(section.getTitle());
                
                // Add section description
                if (section.getDescription() != null && !section.getDescription().isEmpty()) {
                    explain(section.getDescription());
                }
                
                return;
            } catch (Exception e) {
                log.debug("Section not found in template: {}, using manual approach", sectionId);
            }
        }
        // Fallback to manual section start if template not available
        log.warn("Template section not available: {}, use manual startSection() instead", sectionId);
    }
    
    /**
     * Demonstrate action using template data
     */
    protected void demonstrateTemplateAction(String sectionId, String actionName, Runnable action) {
        String actionDescription = actionName;
        
        if (currentWorkflow != null) {
            try {
                DocumentationSection section = templateEngine.getSection(currentWorkflow, sectionId);
                actionDescription = templateEngine.getActionDescription(section, actionName);
            } catch (Exception e) {
                log.debug("Action not found in template: {}/{}, using provided name", sectionId, actionName);
            }
        }
        
        demonstrateAction(actionDescription, action);
    }
    
    /**
     * Add explanations from template data
     */
    protected void explainFromTemplate(String sectionId) {
        if (currentWorkflow != null) {
            try {
                DocumentationSection section = templateEngine.getSection(currentWorkflow, sectionId);
                String[] explanations = templateEngine.getSectionExplanations(section);
                
                for (String explanation : explanations) {
                    explain(explanation);
                }
                
                // Add tips if available
                if (section.getTips() != null && section.getTips().getItems() != null) {
                    explain("**💡 Tips:**");
                    for (String tip : section.getTips().getItems()) {
                        explain("- " + tip);
                    }
                }
                
                return;
            } catch (Exception e) {
                log.debug("Section explanations not found in template: {}", sectionId);
            }
        }
        
        log.warn("Template explanations not available for section: {}", sectionId);
    }
    
    /**
     * Mark the beginning of a workflow section
     */
    protected void startSection(String sectionName) {
        startSection(sectionName, "");
    }
    
    /**
     * Mark the beginning of a workflow section with description
     */
    protected void startSection(String sectionName, String description) {
        log.info("📖 === {} ===", sectionName.toUpperCase());
        docCapture.startSection(sectionName, description);
        takeScreenshot("section_" + sectionName.toLowerCase().replaceAll("\\s+", "_"), 
                       "Beginning of " + sectionName);
    }
    
    /**
     * Mark the end of a workflow section  
     */
    protected void endSection(String sectionName) {
        takeScreenshot("section_" + sectionName.toLowerCase().replaceAll("\\s+", "_") + "_complete",
                       "Completed " + sectionName);
        docCapture.endSection();
        log.info("✅ Section completed: {}", sectionName);
    }
    
    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
    
    private void setupDocumentationDirectory(TestInfo testInfo) {
        // Create timestamp for this test run
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // Get clean test name
        currentTestName = testInfo.getTestClass()
                .map(Class::getSimpleName)
                .orElse("UnknownTest");
        String methodName = testInfo.getTestMethod()
                .map(method -> method.getName())
                .orElse("unknownMethod");
        
        // Create documentation directory structure
        documentationDir = Paths.get("target/documentation", currentTestName, 
                                    methodName + "_" + timestamp);
        
        try {
            Files.createDirectories(documentationDir.resolve("screenshots"));
            Files.createDirectories(documentationDir.resolve("video"));
        } catch (Exception e) {
            log.warn("Failed to create documentation directories: {}", e.getMessage());
        }
    }
    
    private void organizeVideoFile(Path videoPath, TestInfo testInfo) {
        try {
            String testResult = "SUCCESS"; // Could be enhanced to detect actual test result
            String videoFileName = String.format("%s_%s.webm", currentTestName, testResult);
            Path targetPath = documentationDir.resolve("video").resolve(videoFileName);
            
            Files.move(videoPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Log file info
            long sizeInMB = Files.size(targetPath) / (1024 * 1024);
            log.info("📹 Documentation video saved: {} ({} MB)", targetPath, sizeInMB);
            
        } catch (Exception e) {
            log.warn("Failed to organize video file: {}", e.getMessage());
        }
    }
}