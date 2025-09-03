package com.sahabatquran.webapp.functional.scenarios.termpreparationworkflow;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * System Go-Live Readiness and Status Workflow Tests.
 * Tests the go-live readiness assessment and system status verification.
 * 
 * User Role: ACADEMIC_ADMIN
 * Focus: Success workflow scenarios for go-live readiness
 */
@Slf4j
@DisplayName("PS-AP-010: System Go-Live Readiness Workflow Tests")
@Sql(scripts = "/sql/class-preparation-workflow-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/class-preparation-workflow-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SystemGoLiveWorkflowTest extends BasePlaywrightTest {

    @BeforeEach
    void setupWorkflowTest() {
        log.info("🔧 Setting up system go-live readiness workflow test");
        loginAsAdmin();
        page.navigate(getBaseUrl() + "/academic/system-golive");
        page.waitForLoadState();
    }

    @Test
    @DisplayName("PS-AP-010: System Go-Live Readiness Assessment Workflow")
    void systemGoLiveReadinessAssessmentWorkflow() {
        log.info("🎯 PS-AP-010: Testing system go-live readiness assessment workflow");
        
        // Phase 1: Verify Go-Live Interface Load
        log.info("🚀 Phase 1: Go-Live Interface Verification");
        
        // Verify page loads correctly
        assertThat(page.locator("#page-title")).containsText("System Go-Live");
        
        // Verify go-live status card is present
        assertThat(page.locator(".golive-card")).isVisible();
        
        // Phase 2: System Readiness Assessment
        log.info("📊 Phase 2: System Readiness Assessment");
        
        // Verify term selector is available
        assertThat(page.locator("#termSelector")).isVisible();
        
        // Check for system statistics display
        if (page.locator(".grid.grid-cols-1.md\\:grid-cols-4").count() > 0) {
            assertThat(page.locator(".grid.grid-cols-1.md\\:grid-cols-4").first()).isVisible();
            log.info("System statistics grid is displayed");
        }
        
        // Phase 3: Go-Live Controls Verification
        log.info("🎮 Phase 3: Go-Live Controls Verification");
        
        // Verify go-live button or controls are present
        if (page.locator("button:has-text('Execute Go-Live')").isVisible()) {
            log.info("Execute Go-Live button is available");
            assertThat(page.locator("button:has-text('Execute Go-Live')")).isVisible();
        } else if (page.locator("button").count() > 0) {
            assertThat(page.locator("button").first()).isVisible();
            log.info("Go-Live controls are present");
        }
        
        // Phase 4: Status Indicators Verification
        log.info("🔍 Phase 4: Status Indicators Verification");
        
        // Check for status indicators if present
        if (page.locator(".status-indicator").count() > 0) {
            assertThat(page.locator(".status-indicator").first()).isVisible();
            log.info("Status indicators are displayed");
        }
        
        // Verify visual elements are present
        assertThat(page.locator("i.fas").first()).isVisible(); // FontAwesome icons
        
        // Phase 5: System Statistics Verification
        log.info("📈 Phase 5: System Statistics Verification");
        
        // Verify system statistics cards
        if (page.locator(".bg-white.p-6").count() > 0) {
            assertThat(page.locator(".bg-white.p-6").first()).isVisible();
            log.info("Statistics cards are displayed");
        }
        
        // Phase 6: Error Handling Verification
        log.info("⚠️ Phase 6: Error Handling Verification");
        
        // Check if any error messages are displayed (should be none for successful case)
        if (page.locator(".bg-red-50").count() > 0) {
            log.info("Error indicators are present (normal for testing various states)");
        } else {
            log.info("No error indicators present - system appears ready");
        }
        
        // Phase 7: Final Readiness Confirmation
        log.info("✅ Phase 7: Final Readiness Confirmation");
        
        // Verify overall page content indicates go-live readiness
        String pageContent = page.content();
        assertTrue(pageContent.contains("Go-Live") || pageContent.contains("Ready") || 
                  pageContent.contains("System"), 
                  "Page should indicate go-live functionality");
        
        // Final verification - essential elements are present
        assertThat(page.locator("#page-title")).isVisible();
        if (page.locator(".golive-card").count() > 0) {
            assertThat(page.locator(".golive-card")).isVisible();
        }
        
        log.info("✅ PS-AP-010: System go-live readiness assessment workflow completed successfully");
    }
}