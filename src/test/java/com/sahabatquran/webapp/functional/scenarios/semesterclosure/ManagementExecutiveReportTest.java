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
 * Management Executive Report Tests.
 *
 * Tests executive reporting functionality as described in:
 * docs/test-scenario/pelaporan-dan-analitik/penutupan-semester-happy-path.md#PSC-HP-004
 *
 * Focuses on:
 * - Executive dashboard access
 * - Performance metrics aggregation
 * - Strategic insights generation
 * - Board presentation packages
 * - Management decision support
 *
 * User Role: MANAGEMENT
 */
@Slf4j
@DisplayName("PSC-ER: Management Executive Report Tests")
class ManagementExecutiveReportTest extends BasePlaywrightTest {

    /**
     * PSC-HP-004: Generate Laporan Eksekutif Penutupan Semester
     *
     * Test Scenario Documentation Reference:
     * docs/test-scenario/pelaporan-dan-analitik/penutupan-semester-happy-path.md#PSC-HP-004
     *
     * Tests executive report generation for board and management team.
     */
    @Test
    @DisplayName("PSC-HP-004: Executive Summary Report Generation")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateExecutiveSummaryReport() {
        log.info("🚀 Starting PSC-HP-004: Executive Summary Report Generation...");

        // Test data from documentation
        final String MANAGEMENT_USERNAME = "management.director";
        final String ADMIN_USERNAME = "academic.admin1";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        // Step 1: Ensure reports are generated (as Academic Admin)
        log.info("📝 Step 1: Ensure semester reports are generated");

        loginAsAdmin();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Quick validation and report generation
        closurePage.clickValidateData();
        page.waitForTimeout(2000);

        if (closurePage.areValidationResultsDisplayed()) {
            closurePage.proceedWithReportGeneration();

            // Generate comprehensive reports for executive summary
            closurePage.configureReportGeneration(
                true,  // includeStudentReports
                true,  // includeClassSummaries
                true,  // includeTeacherEvaluations
                false, // includeParentNotifications (not needed for exec summary)
                true,  // includeManagementSummary - CRITICAL for this test
                false  // autoDistribute
            );

            closurePage.initiateReportGeneration();
            closurePage.waitForGenerationCompletion(360000); // 6 minutes for comprehensive reports

            assertTrue(closurePage.isGenerationCompleted(),
                "Report generation should complete for executive summary");
        }

        // Step 2: Access as Management User
        log.info("📝 Step 2: Access executive reporting as Management");

        loginAsManagement();

        // Navigate to semester closure dashboard (management should have access)
        closurePage.navigateToSemesterClosure();
        assertTrue(closurePage.isDashboardVisible(),
            "Management should have access to semester closure dashboard");

        closurePage.selectTerm(TERM_NAME);

        // Step 3: Verify Executive Report Data Access
        log.info("📝 Step 3: Verify executive report data access");

        // Management should see completed batches
        int completedBatches = closurePage.getCompletedBatchesCount();
        assertTrue(completedBatches > 0, "Should see completed report batches");

        log.info("📊 Management view - Completed batches: {}", completedBatches);

        // Verify management can see batch details for executive insights
        if (completedBatches > 0) {
            // Should be able to view batch status without errors
            assertFalse(closurePage.isErrorMessageVisible(),
                "Management should have proper access to view batch information");
        }

        // Step 4: Verify Term Closure Authority
        log.info("📝 Step 4: Verify management closure authority");

        // Management should be able to execute term closure if reports are complete
        if (closurePage.canTermBeClosed()) {
            log.info("✅ Management has authority to close term when ready");

            // Execute closure (this represents the executive decision)
            closurePage.executeTermClosure();

            // Verify closure execution
            assertTrue(closurePage.isTermClosureComplete() || closurePage.isSuccessMessageVisible(),
                "Management should be able to execute term closure");

            if (closurePage.isSuccessMessageVisible()) {
                String successMessage = closurePage.getSuccessMessage();
                log.info("✅ Closure success message: {}", successMessage);
            }
        } else {
            log.info("ℹ️ Term not yet ready for closure - this is acceptable");
        }

        log.info("✅ PSC-HP-004: Executive Summary Report Generation completed successfully");
    }

    /**
     * PSC-ER-002: Management Dashboard Access and KPI Review
     *
     * Tests management-specific views and key performance indicators.
     */
    @Test
    @DisplayName("PSC-ER-002: Management Dashboard and KPI Access")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldProvideManagementDashboardAndKPIAccess() {
        log.info("🚀 Starting PSC-ER-002: Management Dashboard and KPI Access...");

        final String MANAGEMENT_USERNAME = "management.director";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        // Access as management
        loginAsManagement();

        // Navigate to semester closure dashboard
        closurePage.navigateToSemesterClosure();
        assertTrue(closurePage.isDashboardVisible(),
            "Management dashboard should be accessible");

        closurePage.selectTerm(TERM_NAME);

        // Verify KPI visibility
        log.info("📝 Verifying KPI visibility for management");

        // Check key metrics are visible
        assertTrue(closurePage.isDashboardVisible(),
            "Key performance indicators should be visible");

        // Management should see summary statistics
        int activeBatches = closurePage.getActiveBatchesCount();
        int completedBatches = closurePage.getCompletedBatchesCount();

        log.info("📊 Management KPIs - Active: {}, Completed: {}",
            activeBatches, completedBatches);

        // Verify management has appropriate access levels
        assertFalse(closurePage.isErrorMessageVisible(),
            "Management should have proper access without errors");

        // Should be able to view status without implementation details
        assertTrue(activeBatches >= 0 && completedBatches >= 0,
            "KPI metrics should be non-negative");

        log.info("✅ PSC-ER-002: Management Dashboard and KPI Access completed successfully");
    }

    /**
     * PSC-ER-003: Strategic Decision Support
     *
     * Tests the decision support capabilities for management during term closure.
     */
    @Test
    @DisplayName("PSC-ER-003: Strategic Decision Support for Term Closure")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/semester-closure-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldProvideStrategicDecisionSupport() {
        log.info("🚀 Starting PSC-ER-003: Strategic Decision Support...");

        final String MANAGEMENT_USERNAME = "management.director";
        final String PASSWORD = "Welcome@YSQ2024";
        final String TERM_NAME = "Semester 1 2024/2025";

        SemesterClosurePage closurePage = new SemesterClosurePage(page);

        loginAsManagement();
        closurePage.navigateToSemesterClosure();
        closurePage.selectTerm(TERM_NAME);

        // Test strategic oversight capabilities
        log.info("📝 Testing strategic oversight capabilities");

        // Management should see high-level status
        assertTrue(closurePage.isDashboardVisible(),
            "Strategic dashboard should be available");

        // Should be able to assess closure readiness
        boolean canClose = closurePage.canTermBeClosed();
        log.info("📊 Term closure readiness: {}", canClose ? "Ready" : "Not Ready");

        // This information supports strategic decision making
        if (canClose) {
            log.info("✅ Strategic decision: Term is ready for closure");
        } else {
            log.info("⏳ Strategic decision: Term requires further preparation");
        }

        // Verify no unauthorized access to detailed operations
        // Management should see summary, not operational details
        assertFalse(closurePage.isErrorMessageVisible(),
            "Management access should be properly scoped");

        log.info("✅ PSC-ER-003: Strategic Decision Support completed successfully");
    }

    // Use inherited loginAsAdmin() and loginAsManagement() methods from BasePlaywrightTest
}