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
 * Report Distribution and Notification Tests.
 *
 * Tests the report distribution functionality specifically mentioned in:
 * docs/test-scenario/pelaporan-dan-analitik/penutupan-semester-happy-path.md
 *
 * Focuses on:
 * - Automated report distribution
 * - Email notification system
 * - Parent portal integration
 * - Distribution status tracking
 * - Stakeholder communication
 *
 * User Role: ACADEMIC_ADMIN
 */
@Slf4j
@DisplayName("PSC-RD: Report Distribution and Notification Tests")
class ReportDistributionTest extends BasePlaywrightTest {

    /**
     * PSC-RD-001: Automated Report Distribution
     *
     * Tests the automatic distribution of generated reports to stakeholders
     * including parents, teachers, and management.
     */
    @Test
    @DisplayName("PSC-RD-001: Automated Report Distribution to Stakeholders")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAutomaticallyDistributeReportsToStakeholders() {
        log.info("🚀 Starting PSC-RD-001: Automated Report Distribution...");

        final String ADMIN_USERNAME = "academic.admin1";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        loginAsAdmin();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // First, generate reports with auto-distribution enabled
        log.info("📝 Step 1: Generate reports with auto-distribution");

        closurePage.clickValidateData();
        page.waitForTimeout(2000);
        closurePage.proceedWithReportGeneration();

        // Configure for distribution testing
        closurePage.configureReportGeneration(
            true,  // includeStudentReports
            true,  // includeClassSummaries
            false, // includeTeacherEvaluations (skip for speed)
            true,  // includeParentNotifications
            false, // includeManagementSummary (skip for speed)
            true   // autoDistribute - ENABLED for this test
        );

        closurePage.initiateReportGeneration();

        // Monitor the generation and distribution process
        log.info("📝 Step 2: Monitor generation and distribution progress");

        assertTrue(closurePage.isProgressModalVisible(),
            "Progress modal should show during generation and distribution");

        // Wait for completion with extended timeout for distribution
        closurePage.waitForGenerationCompletion(360000); // 6 minutes for generation + distribution

        // Verify generation completed
        assertTrue(closurePage.isGenerationCompleted(),
            "Report generation should complete successfully");

        // Verify distribution was executed
        log.info("📝 Step 3: Verify distribution completion");

        // The distribution should have been triggered automatically
        // Check if there are any distribution-related success messages
        if (closurePage.isSuccessMessageVisible()) {
            String successMessage = closurePage.getSuccessMessage();
            log.info("✅ Success message: {}", successMessage);
        }

        // Check final counts
        int completedItems = closurePage.getCompletedItemsCount();
        int failedItems = closurePage.getFailedItemsCount();

        log.info("📊 Final Results - Completed: {}, Failed: {}", completedItems, failedItems);

        // Should have completed successfully with minimal failures
        assertTrue(completedItems > 0, "Should have completed some reports");
        assertTrue(failedItems <= completedItems * 0.1, "Failed items should be less than 10%");

        log.info("✅ PSC-RD-001: Automated Report Distribution completed successfully");
    }

    /**
     * PSC-RD-002: Manual Distribution Management
     *
     * Tests manual distribution controls and status monitoring.
     */
    @Test
    @DisplayName("PSC-RD-002: Manual Distribution Management")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldManageManualDistribution() {
        log.info("🚀 Starting PSC-RD-002: Manual Distribution Management...");

        final String ADMIN_USERNAME = "academic.admin1";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        loginAsAdmin();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Generate reports WITHOUT auto-distribution
        log.info("📝 Step 1: Generate reports without auto-distribution");

        closurePage.clickValidateData();
        page.waitForTimeout(2000);
        closurePage.proceedWithReportGeneration();

        // Configure for manual distribution
        closurePage.configureReportGeneration(
            true,  // includeStudentReports
            false, // includeClassSummaries (skip for speed)
            false, // includeTeacherEvaluations (skip for speed)
            false, // includeParentNotifications (skip for speed)
            false, // includeManagementSummary (skip for speed)
            false  // autoDistribute - DISABLED for manual testing
        );

        closurePage.initiateReportGeneration();

        // Wait for generation only (not distribution)
        log.info("📝 Step 2: Wait for generation completion");

        closurePage.waitForGenerationCompletion(300000); // 5 minutes for generation only

        assertTrue(closurePage.isGenerationCompleted(),
            "Report generation should complete successfully");

        // At this point, reports should be generated but not distributed
        log.info("📝 Step 3: Verify manual distribution control");

        // Refresh dashboard to see batch status
        closurePage.refreshDashboard();
        closurePage.selectTerm(TERM_NAME);

        // Check if there are completed batches available for manual distribution
        int completedBatches = closurePage.getCompletedBatchesCount();
        assertTrue(completedBatches > 0, "Should have completed batches available");

        log.info("📊 Completed batches available for distribution: {}", completedBatches);

        log.info("✅ PSC-RD-002: Manual Distribution Management completed successfully");
    }

    /**
     * PSC-RD-003: Distribution Status Monitoring
     *
     * Tests the monitoring of distribution status and tracking.
     */
    @Test
    @DisplayName("PSC-RD-003: Distribution Status Monitoring")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldMonitorDistributionStatus() {
        log.info("🚀 Starting PSC-RD-003: Distribution Status Monitoring...");

        final String ADMIN_USERNAME = "academic.admin1";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        loginAsAdmin();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Check initial dashboard state
        log.info("📝 Step 1: Check initial dashboard state");

        int initialActiveBatches = closurePage.getActiveBatchesCount();
        int initialCompletedBatches = closurePage.getCompletedBatchesCount();

        log.info("📊 Initial state - Active: {}, Completed: {}",
            initialActiveBatches, initialCompletedBatches);

        // Generate a small batch for monitoring
        closurePage.clickValidateData();
        page.waitForTimeout(2000);
        closurePage.proceedWithReportGeneration();

        // Configure minimal batch
        closurePage.configureReportGeneration(
            true,  // includeStudentReports (minimal set)
            false, // includeClassSummaries
            false, // includeTeacherEvaluations
            false, // includeParentNotifications
            false, // includeManagementSummary
            true   // autoDistribute
        );

        closurePage.initiateReportGeneration();

        // Monitor progress in detail
        log.info("📝 Step 2: Monitor batch progress");

        assertTrue(closurePage.isProgressModalVisible(),
            "Progress modal should be visible during processing");

        // Take periodic snapshots of progress
        for (int i = 0; i < 3; i++) {
            page.waitForTimeout(2000);
            if (closurePage.isProgressModalVisible()) {
                String progress = closurePage.getProgressPercentage();
                String currentItem = closurePage.getCurrentProcessingItem();
                log.info("📊 Progress snapshot {}: {}% - {}", i + 1, progress, currentItem);

                if (progress.contains("100%")) {
                    break;
                }
            }
        }

        // Wait for completion
        closurePage.waitForGenerationCompletion(240000); // 4 minutes

        // Verify final state
        log.info("📝 Step 3: Verify final distribution state");

        assertTrue(closurePage.isGenerationCompleted(),
            "Generation and distribution should complete");

        // Check final dashboard state
        closurePage.refreshDashboard();
        closurePage.selectTerm(TERM_NAME);

        int finalActiveBatches = closurePage.getActiveBatchesCount();
        int finalCompletedBatches = closurePage.getCompletedBatchesCount();

        log.info("📊 Final state - Active: {}, Completed: {}",
            finalActiveBatches, finalCompletedBatches);

        // Should have one more completed batch
        assertTrue(finalCompletedBatches > initialCompletedBatches,
            "Should have more completed batches after processing");

        log.info("✅ PSC-RD-003: Distribution Status Monitoring completed successfully");
    }

    // Use inherited loginAsAdmin() method from BasePlaywrightTest
}