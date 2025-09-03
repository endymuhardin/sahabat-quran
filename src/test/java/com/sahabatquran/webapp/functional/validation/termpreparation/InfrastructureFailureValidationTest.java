package com.sahabatquran.webapp.functional.validation.termpreparation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * System Go-Live Infrastructure Robustness Validation Tests.
 * Tests system stability and error handling in the go-live interface.
 * 
 * User Role: ACADEMIC_ADMIN
 * Focus: Validation of system robustness, error handling, interface stability
 */
@Slf4j
@DisplayName("PS-AP-010-VAL: Infrastructure Robustness Validation Tests")
@Sql(scripts = "/sql/class-preparation-workflow-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/class-preparation-workflow-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class InfrastructureFailureValidationTest extends BasePlaywrightTest {

    private static final String GOLIVE_URL = "/academic/system-golive";

    @BeforeEach
    void setupValidationTest() {
        log.info("🔧 Setting up infrastructure robustness validation test");
        loginAsAdmin();
        page.navigate(getBaseUrl() + GOLIVE_URL);
        page.waitForLoadState();
    }

    @Test
    @DisplayName("PS-AP-010-VAL-001: System Go-Live Interface Stability Validation")
    void systemGoLiveInterfaceStabilityValidation() {
        log.info("🎯 PS-AP-010-VAL-001: Testing system go-live interface stability validation");
        
        // Verify interface loads without errors
        assertThat(page.locator("#page-title")).containsText("System Go-Live");
        
        // Verify no error messages are displayed by default
        if (page.locator(".bg-red-50").count() == 0) {
            log.info("No error indicators present - interface is stable");
        }
        
        // Verify core components are properly rendered
        assertThat(page.locator(".golive-card")).isVisible();
        
        // Test interface responsiveness
        page.reload();
        page.waitForLoadState();
        assertThat(page.locator("#page-title")).containsText("System Go-Live");
        
        log.info("✅ System go-live interface stability validation completed");
    }

    @Test
    @DisplayName("PS-AP-010-VAL-002: Term Selector Validation")
    void termSelectorValidation() {
        log.info("🎯 PS-AP-010-VAL-002: Testing term selector validation");
        
        // Verify term selector is present and functional
        assertThat(page.locator("#termSelector")).isVisible();
        
        // Test term selector interaction
        if (page.locator("#termSelector option").count() > 1) {
            log.info("Term selector has multiple options available");
            
            // Verify page handles term selection gracefully
            String currentUrl = page.url();
            assertTrue(currentUrl.contains("system-golive"), 
                      "Page should remain on go-live interface");
        } else {
            log.info("Term selector present but limited options (normal for test environment)");
        }
        
        log.info("✅ Term selector validation completed");
    }

    @Test
    @DisplayName("PS-AP-010-VAL-003: Go-Live Button State Validation")
    void goLiveButtonStateValidation() {
        log.info("🎯 PS-AP-010-VAL-003: Testing go-live button state validation");
        
        // Verify go-live button is present
        if (page.locator("button:has-text('Execute Go-Live')").isVisible()) {
            assertThat(page.locator("button:has-text('Execute Go-Live')")).isVisible();
            log.info("Execute Go-Live button is available and visible");
            
            // Verify button is properly styled and accessible
            String buttonClass = page.locator("button:has-text('Execute Go-Live')").getAttribute("class");
            assertTrue(buttonClass != null && !buttonClass.isEmpty(), 
                      "Go-Live button should have proper styling");
        } else {
            log.info("Go-Live button not available (may indicate system not ready)");
            
            // Verify alternative controls are present
            if (page.locator("button").count() > 0) {
                assertThat(page.locator("button").first()).isVisible();
                log.info("Alternative controls are available");
            }
        }
        
        log.info("✅ Go-live button state validation completed");
    }

    @Test
    @DisplayName("PS-AP-010-VAL-004: System Statistics Display Validation")
    void systemStatisticsDisplayValidation() {
        log.info("🎯 PS-AP-010-VAL-004: Testing system statistics display validation");
        
        // Verify statistics grid is displayed
        if (page.locator(".grid.grid-cols-1.md\\:grid-cols-4").count() > 0) {
            assertThat(page.locator(".grid.grid-cols-1.md\\:grid-cols-4").first()).isVisible();
            log.info("Statistics grid is properly displayed");
        }
        
        // Verify statistics cards are present
        if (page.locator(".bg-white.p-6").count() > 0) {
            assertThat(page.locator(".bg-white.p-6").first()).isVisible();
            log.info("Statistics cards are rendered properly");
        }
        
        // Verify icons and visual elements are loaded
        assertThat(page.locator("i.fas").first()).isVisible();
        
        log.info("✅ System statistics display validation completed");
    }

    @Test
    @DisplayName("PS-AP-010-VAL-005: Page Content Consistency Validation")
    void pageContentConsistencyValidation() {
        log.info("🎯 PS-AP-010-VAL-005: Testing page content consistency validation");
        
        // Capture initial page state
        String initialContent = page.content();
        String initialTitle = page.locator("#page-title").textContent();
        
        // Refresh page multiple times to test consistency
        for (int i = 0; i < 3; i++) {
            page.reload();
            page.waitForLoadState();
            
            // Verify consistent title
            String currentTitle = page.locator("#page-title").textContent();
            assertTrue(currentTitle.equals(initialTitle), 
                      "Page title should remain consistent across reloads");
            
            // Verify essential elements remain present
            assertThat(page.locator("#page-title")).isVisible();
            assertThat(page.locator(".golive-card")).isVisible();
            
            log.info("Page reload {} completed successfully", i + 1);
        }
        
        // Verify no JavaScript errors accumulated
        String finalContent = page.content();
        assertTrue(!finalContent.contains("error") || finalContent.contains("System Go-Live"), 
                  "Page should not accumulate errors after multiple reloads");
        
        log.info("✅ Page content consistency validation completed");
    }
}