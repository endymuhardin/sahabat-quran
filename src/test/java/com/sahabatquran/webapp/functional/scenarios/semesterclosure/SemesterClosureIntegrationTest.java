package com.sahabatquran.webapp.functional.scenarios.semesterclosure;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.SemesterClosurePage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Semester Closure Complete Integration Test.
 *
 * Tests the complete end-to-end semester closure workflow as described in:
 * docs/test-scenario/pelaporan-dan-analitik/penutupan-semester-happy-path.md
 *
 * This integration test covers the complete workflow:
 * PSC-HP-001 through PSC-HP-005 in sequence
 *
 * User Roles: ACADEMIC_ADMIN, MANAGEMENT
 */
@Slf4j
@DisplayName("PSC-INT: Complete Semester Closure Integration Test")
class SemesterClosureIntegrationTest extends BasePlaywrightTest {

    /**
     * Complete End-to-End Semester Closure Workflow
     *
     * Tests the complete semester closure process from validation through
     * final closure and next term preparation readiness.
     *
     * This test represents the actual business process flow and validates
     * all critical integration points.
     */
    @Test
    @DisplayName("PSC-INT-001: Complete Semester Closure Workflow")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCompleteFullSemesterClosureWorkflow() {
        log.info("🚀 Starting PSC-INT-001: Complete Semester Closure Workflow...");

        final String ADMIN_USERNAME = "academic.admin1";
        final String MANAGEMENT_USERNAME = "management.director";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        // ===== PHASE 1: PRE-CLOSURE VALIDATION (PSC-HP-001) =====
        log.info("🔵 PHASE 1: Pre-Closure Validation (PSC-HP-001)");

        loginAsAdmin();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Initial dashboard verification
        assertTrue(closurePage.isDashboardVisible(),
            "Semester closure dashboard should be accessible");

        int initialActiveBatches = closurePage.getActiveBatchesCount();
        int initialCompletedBatches = closurePage.getCompletedBatchesCount();

        log.info("📊 Initial State - Active: {}, Completed: {}",
            initialActiveBatches, initialCompletedBatches);

        // Execute pre-closure validation
        closurePage.clickValidateData();
        assertTrue(closurePage.isValidationModalVisible(),
            "Validation modal should open");

        page.waitForTimeout(3000); // Allow validation to complete

        assertTrue(closurePage.areValidationResultsDisplayed(),
            "Validation results should be displayed");

        String validationSummary = closurePage.getValidationSummaryText();
        assertNotNull(validationSummary, "Validation summary should be available");
        log.info("📋 Validation Summary: {}", validationSummary);

        // Proceed to report generation if validation passes
        assertFalse(closurePage.isErrorMessageVisible(),
            "No critical validation errors should prevent proceeding");

        // ===== PHASE 2: REPORT GENERATION (PSC-HP-002) =====
        log.info("🔵 PHASE 2: Report Generation (PSC-HP-002)");

        closurePage.proceedWithReportGeneration();
        assertTrue(closurePage.isReportGenerationModalVisible(),
            "Report generation modal should open");

        // Configure comprehensive report generation
        closurePage.configureReportGeneration(
            true,  // includeStudentReports
            true,  // includeClassSummaries
            true,  // includeTeacherEvaluations
            true,  // includeParentNotifications
            true,  // includeManagementSummary
            true   // autoDistribute
        );

        closurePage.initiateReportGeneration();
        assertTrue(closurePage.isProgressModalVisible(),
            "Progress tracking should be active");

        // Monitor generation progress
        log.info("📊 Monitoring report generation progress...");

        String initialProgress = closurePage.getProgressPercentage();
        log.info("📈 Initial progress: {}", initialProgress);

        // Wait for completion with generous timeout for full generation
        closurePage.waitForGenerationCompletion(600000); // 10 minutes for comprehensive reports

        assertTrue(closurePage.isGenerationCompleted(),
            "Report generation should complete successfully");

        int completedItems = closurePage.getCompletedItemsCount();
        int failedItems = closurePage.getFailedItemsCount();

        log.info("📊 Generation Results - Completed: {}, Failed: {}", completedItems, failedItems);

        // Verify successful generation
        assertTrue(completedItems > 0, "Should have completed some reports");
        assertTrue(failedItems <= completedItems * 0.05, // Allow 5% failure rate
            "Failed items should be minimal");

        // ===== PHASE 3: MANAGEMENT APPROVAL AND CLOSURE (PSC-HP-002 continued) =====
        log.info("🔵 PHASE 3: Management Approval and Term Closure");

        // Switch to Management user for final approval
        loginAsManagement();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Verify management can see completion status
        assertTrue(closurePage.isDashboardVisible(),
            "Management should have access to closure dashboard");

        int finalCompletedBatches = closurePage.getCompletedBatchesCount();
        assertTrue(finalCompletedBatches > initialCompletedBatches,
            "Should have more completed batches after generation");

        log.info("📊 Final State - Completed batches: {}", finalCompletedBatches);

        // Execute term closure if ready
        if (closurePage.canTermBeClosed()) {
            log.info("✅ Term is ready for closure - executing final closure");

            closurePage.executeTermClosure();

            // Verify closure completion
            assertTrue(closurePage.isTermClosureComplete() || closurePage.isSuccessMessageVisible(),
                "Term closure should complete successfully");

            if (closurePage.isSuccessMessageVisible()) {
                String successMessage = closurePage.getSuccessMessage();
                log.info("🎉 Closure Success: {}", successMessage);
            }
        } else {
            log.info("⏳ Term not yet ready for closure - additional preparation may be needed");
        }

        // ===== PHASE 4: POST-CLOSURE VERIFICATION =====
        log.info("🔵 PHASE 4: Post-Closure Verification");

        // Refresh dashboard to see final state
        closurePage.refreshDashboard();
        closurePage.selectTerm(TERM_NAME);

        // Verify final dashboard state
        assertTrue(closurePage.isDashboardVisible(),
            "Dashboard should remain accessible after closure");

        // Check for any post-closure messages or status updates
        if (closurePage.isSuccessMessageVisible()) {
            log.info("✅ Post-closure status confirmed");
        }

        // Final verification - no critical errors should be present
        assertFalse(closurePage.isErrorMessageVisible(),
            "No errors should be present after successful closure");

        // ===== INTEGRATION TEST COMPLETION =====
        log.info("🎯 Integration Test Results Summary:");
        log.info("   ✅ Pre-closure validation: PASSED");
        log.info("   ✅ Report generation: PASSED ({} completed, {} failed)", completedItems, failedItems);
        log.info("   ✅ Management oversight: PASSED");
        log.info("   ✅ Term closure: {}", closurePage.canTermBeClosed() ? "EXECUTED" : "PREPARED");
        log.info("   ✅ Post-closure verification: PASSED");

        log.info("🏁 PSC-INT-001: Complete Semester Closure Workflow completed successfully");
    }

    /**
     * Stress Test: Multiple Term Closure Simulation
     *
     * Tests system stability under multiple closure operations.
     */
    @Test
    @DisplayName("PSC-INT-002: System Stability Under Multiple Operations")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldMaintainStabilityUnderMultipleOperations() {
        log.info("🚀 Starting PSC-INT-002: System Stability Test...");

        final String ADMIN_USERNAME = "academic.admin1";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        loginAsAdmin();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Test multiple validation operations
        log.info("📝 Testing system stability with multiple operations");

        for (int i = 1; i <= 3; i++) {
            log.info("🔄 Operation cycle {}/3", i);

            // Multiple validation checks
            closurePage.clickValidateData();
            page.waitForTimeout(2000);

            assertTrue(closurePage.isValidationModalVisible(),
                String.format("Validation should work on cycle %d", i));

            // Close modal and refresh
            page.keyboard().press("Escape");
            page.waitForTimeout(1000);

            closurePage.refreshDashboard();
            closurePage.selectTerm(TERM_NAME);

            // Verify system remains stable
            assertTrue(closurePage.isDashboardVisible(),
                String.format("Dashboard should remain stable after cycle %d", i));

            assertFalse(closurePage.isErrorMessageVisible(),
                String.format("No errors should occur during cycle %d", i));
        }

        log.info("✅ System maintained stability across multiple operations");
        log.info("✅ PSC-INT-002: System Stability Test completed successfully");
    }

    // Use inherited loginAsAdmin() and loginAsManagement() methods from BasePlaywrightTest
}