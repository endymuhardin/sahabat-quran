package com.sahabatquran.webapp.functional;

import com.sahabatquran.webapp.config.SeleniumContainerFactory;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import org.junit.jupiter.api.AfterAll;\nimport org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class BaseSeleniumTest extends BaseIntegrationTest {
    
    
    @LocalServerPort
    protected int port;
    
    private WebDriver webDriver;
    
    // Timeout configurations from properties
    @Value("${selenium.timeout.explicit.seconds:10}")
    private int explicitTimeoutSeconds;
    
    @Value("${selenium.timeout.implicit.seconds:5}")
    private int implicitTimeoutSeconds;
    
    @Value("${selenium.timeout.page-load.seconds:30}")
    private int pageLoadTimeoutSeconds;
    
    @Value("${selenium.timeout.script.seconds:30}")
    private int scriptTimeoutSeconds;
    
    protected String baseUrl;
    
    @BeforeEach
    void setUp() {
        // Create WebDriver instance for this test
        webDriver = SeleniumContainerFactory.createWebDriver();
        
        // Always use host.testcontainers.internal with proper port exposure
        baseUrl = "http://host.testcontainers.internal:" + port;
        
        // Maximize browser window to ensure large viewport
        webDriver.manage().window().maximize();
        
        // Configure timeouts from properties
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeoutSeconds));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeoutSeconds));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeoutSeconds));
    }
    
    @AfterEach
    void tearDown() {
        if (webDriver != null) {
            try {
                // Clear cookies and session data before quitting
                webDriver.manage().deleteAllCookies();
                
                // Navigate to blank page to clear any remaining state
                webDriver.get("about:blank");
                
                // Quit the WebDriver session (but keep container running)
                webDriver.quit();
            } catch (Exception e) {
                // Log but don't fail the test on cleanup errors
                System.err.println("Error during WebDriver cleanup: " + e.getMessage());
            }
        }
    }
    
    protected WebDriver getWebDriver() {
        return webDriver;
    }
    
    protected String getBaseUrl() {
        return baseUrl;
    }
    
    protected Duration getExplicitTimeout() {
        return Duration.ofSeconds(explicitTimeoutSeconds);
    }
    
    @AfterAll
    static void cleanupSeleniumContainer() {
        System.out.println("🧹 Cleaning up Selenium container to finalize recordings...");
        SeleniumContainerFactory.cleanup();
    }
}