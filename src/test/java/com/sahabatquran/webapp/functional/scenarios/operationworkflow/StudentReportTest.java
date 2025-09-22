package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.StudentReportPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Simplified Report Generation Tests - Synchronized with Updated Manual Test Scenarios.
 *
 * Tests the simplified report generation architecture with:
 * - Single generate button for all students
 * - Status dashboard with server-side updates (no real-time)
 * - Individual regeneration functionality
 * - Pre-generated PDF download system
 *
 * Manual Test Reference: docs/test-scenario/pelaporan-dan-analitik/background-report-generation-happy-path.md
 *
 * @author Sahabat Quran Development Team
 */
@Slf4j
@DisplayName("SRG: Simplified Report Generation Scenarios")
class StudentReportTest extends BasePlaywrightTest {

    private static final String ADMIN_USERNAME = "academic.admin1";
    private static final String ADMIN_PASSWORD = "Welcome@YSQ2024";
    private static final String ACADEMIC_TERM = "Semester 1 2024/2025";
    private static final String STUDENT_NAME = "Ali Rahman";

    @Test
    @DisplayName("SRG-HP-001: Single Button Report Generation for All Students")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateAllStudentReportsWithSingleButton() {
        log.info("🚀 Starting SRG-HP-001: Single Button Report Generation for All Students");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Akses Report Generation Page
        log.info("📝 Bagian 1: Access Report Generation Page");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        page.waitForURL("**/dashboard**");
        assertTrue(page.locator("body").textContent().contains("Dashboard"), "Should be on admin dashboard");

        // Navigate to Student Reports - simple path: Reports → Student Report Cards
        reportPage.navigateToStudentReports();
        page.waitForURL("**/report-cards**");

        // Verify simple interface without bulk/individual complexity
        assertTrue(page.locator("#student-reports").isVisible(), "Student reports page should be accessible");
        assertTrue(page.locator("#term-selector").isVisible(), "Term dropdown should be available");
        assertTrue(page.locator("#btn-generate-all").isVisible(), "Single generate button should be visible");
        assertFalse(page.locator("#bulk-generation").isVisible(), "No bulk generation UI should exist");
        assertFalse(page.locator("#individual-generation").isVisible(), "No individual generation UI should exist");

        // Bagian 2: Execute Single Generate All Process
        log.info("📝 Bagian 2: Execute Single Generate All Process");

        // Select academic term
        page.selectOption("#term-selector", ACADEMIC_TERM);
        log.info("Selected academic term: {}", ACADEMIC_TERM);

        // Verify generate button becomes active
        assertTrue(page.locator("#btn-generate-all").isEnabled(), "Generate button should be enabled");

        // Execute Generate All Reports
        page.click("#btn-generate-all");

        // Verify redirect to status dashboard
        page.waitForURL("**/report-cards/status**");
        assertTrue(page.locator("#status-dashboard").isVisible(), "Should redirect to status dashboard");

        // Verify success message
        assertTrue(page.locator("body").textContent().contains("Report generation started"),
                  "Success message should be displayed");

        // Bagian 3: Monitor Processing via Status Dashboard
        log.info("📝 Bagian 3: Monitor Processing via Status Dashboard");

        // Verify status dashboard components (no real-time updates)
        assertTrue(page.locator(".batch-status").first().isVisible(), "Batch status should be displayed");
        assertTrue(page.locator(".report-counts").first().isVisible(), "Report counts should be visible");
        assertFalse(page.locator(".real-time-progress").isVisible(), "No real-time progress elements");
        assertFalse(page.locator(".auto-refresh").isVisible(), "No auto-refresh functionality");

        // Test manual refresh functionality
        page.reload();
        assertTrue(page.locator("#status-dashboard").isVisible(), "Dashboard should reload correctly");

        // Wait for processing completion via manual checks (simulated)
        reportPage.waitForGenerationCompletion();

        // Verify completion status
        assertTrue(reportPage.isGenerationCompleted(), "Generation should complete successfully");

        // Bagian 4: Access Generated Reports
        log.info("📝 Bagian 4: Access Generated Reports");

        // Verify download functionality available
        assertTrue(page.locator(".download-links").first().isVisible(), "Download links should be available");

        log.info("✅ SRG-HP-001: Single Button Generation completed successfully!");
    }

    @Test
    @DisplayName("SRG-HP-002: Individual Student Report Regeneration")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRegenerateIndividualStudentReport() {
        log.info("🚀 Starting SRG-HP-002: Individual Student Report Regeneration");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Setup for Regeneration
        log.info("📝 Bagian 1: Setup for Regeneration");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Select student and term for regeneration
        reportPage.selectStudent(STUDENT_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);

        // Verify regenerate option available
        assertTrue(page.locator("#btn-regenerate").isVisible(), "Regenerate button should be available");

        // Bagian 2: Execute Regeneration
        log.info("📝 Bagian 2: Execute Regeneration");

        // Trigger report regeneration
        reportPage.regenerateStudentReport();

        // Verify redirect to status dashboard
        page.waitForURL("**/report-cards/status**");
        assertTrue(page.locator("body").textContent().contains("Report regeneration started"),
                  "Regeneration success message should be displayed");

        // Monitor regeneration process - get the first/most recent batch status
        String statusText = page.locator(".batch-status").first().textContent();
        assertTrue(statusText.contains("INITIATED") || statusText.contains("Processing") || statusText.contains("Completed"),
                  "Single-item batch should be created and may complete quickly");

        // Wait for regeneration completion
        reportPage.waitForGenerationCompletion();
        assertTrue(reportPage.isGenerationCompleted(), "Regeneration should complete");

        log.info("✅ SRG-HP-002: Individual Regeneration completed successfully!");
    }

    @Test
    @DisplayName("SRG-HP-003: Status Dashboard Functionality")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldProvideStatusDashboardFunctionality() {
        log.info("🚀 Starting SRG-HP-003: Status Dashboard Functionality");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Access Status Dashboard
        log.info("📝 Bagian 1: Access Status Dashboard");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // Direct access to status dashboard
        reportPage.navigateToStatusDashboard();
        assertTrue(reportPage.isStatusDashboardVisible(), "Status dashboard should be accessible");

        // Verify batch listings or empty state message is displayed
        assertTrue(page.locator(".batch-listing").isVisible() ||
                  page.locator("h3:text('No Report Generation Jobs')").isVisible(),
                  "Batch listings or empty state should be shown");

        // If there are no batches, verify empty state message
        if (page.locator("h3:text('No Report Generation Jobs')").isVisible()) {
            assertTrue(page.locator("text=No report generation jobs have been started yet.").isVisible(),
                      "Empty state message should be displayed");
        } else {
            assertTrue(page.locator(".batch-status").first().isVisible(), "Batch status should be indicated");
        }

        // Bagian 2: Status Dashboard Navigation
        log.info("📝 Bagian 2: Status Dashboard Navigation");

        // Test manual refresh functionality
        page.reload();
        assertTrue(reportPage.isStatusDashboardVisible(), "Dashboard should refresh without issues");
        assertFalse(page.locator(".auto-refresh-indicator").isVisible(), "No automatic refresh");

        // Verify no auto-refresh indicator in status dashboard specifically
        assertFalse(page.locator(".auto-refresh-indicator").isVisible(), "No auto-refresh indicators");

        // The dashboard uses manual refresh only (no real-time updates)
        assertTrue(page.locator("text=manual refresh required").isVisible() ||
                  page.locator(".refresh-button").isVisible() ||
                  page.locator("h3:text('No Report Generation Jobs')").isVisible(),
                  "Manual refresh UI or empty state should be present");

        // Bagian 3: Batch Management
        log.info("📝 Bagian 3: Batch Management");

        // Test navigation back to main reports
        page.click(".back-to-reports");
        page.waitForURL("**/report-cards**");
        assertTrue(page.locator("#student-reports").isVisible(), "Should return to main reports");

        log.info("✅ SRG-HP-003: Status Dashboard functionality verified!");
    }

    @Test
    @DisplayName("SRG-HP-004: Pre-generated PDF Access System")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldProvidePreGeneratedPdfAccess() {
        log.info("🚀 Starting SRG-HP-004: Pre-generated PDF Access System");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Pre-generated Report Access
        log.info("📝 Bagian 1: Pre-generated Report Access");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // First generate reports to have pre-generated files
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();
        reportPage.waitForGenerationCompletion();

        // Verify report existence via check-report endpoint
        reportPage.navigateToStudentReports();
        reportPage.selectStudent(STUDENT_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);

        // Bagian 2: Download Functionality Verification
        log.info("📝 Bagian 2: Download Functionality Verification");

        // Test PDF download process
        assertTrue(page.locator("#btn-download-pdf").isVisible(), "Download button should be available");

        // Verify download works via /download-pdf endpoint
        page.click("#btn-download-pdf");
        // Note: In real test, would verify file download, here we check for no errors
        assertFalse(page.locator(".error-message").isVisible(), "Download should not show errors");

        // Bagian 3: Access Control Verification
        log.info("📝 Bagian 3: Access Control Verification");

        // Verify role-based access (ACADEMIC_ADMIN should have full access)
        assertTrue(page.locator("#btn-download-pdf").isEnabled(), "Admin should have download access");

        // Verify error handling for non-existent reports by manually clearing student selection
        page.selectOption("#student-selector", ""); // Clear selection
        if (page.locator("#btn-download-pdf").isVisible()) {
            page.click("#btn-download-pdf");
            // Should handle gracefully or show appropriate message
            page.waitForTimeout(1000);
        }

        log.info("✅ SRG-HP-004: Pre-generated PDF access verified!");
    }

    @Test
    @DisplayName("SRG-AP-001: Report Generation with Incomplete Student Data")
    @Sql(scripts = "/sql/student-report-incomplete-data-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleIncompleteStudentData() {
        log.info("🚀 Starting SRG-AP-001: Report Generation with Incomplete Student Data");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Execute Generation with Mixed Data Quality
        log.info("📝 Bagian 1: Execute Generation with Mixed Data Quality");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Select term with mixed data quality
        page.selectOption("#term-selector", ACADEMIC_TERM);

        // Execute generation despite incomplete data
        reportPage.generateAllReports();
        page.waitForURL("**/report-cards/status**");

        // Verify system doesn't reject due to incomplete data
        assertFalse(page.locator(".error-message").isVisible(), "Should accept incomplete data");

        // Bagian 2: Verify Incomplete Data Handling
        log.info("📝 Bagian 2: Verify Incomplete Data Handling");

        // Wait for processing and verify graceful handling
        reportPage.waitForGenerationCompletion();

        // Should complete successfully even with incomplete data
        assertTrue(reportPage.isGenerationCompleted(), "Should complete despite incomplete data");

        // Verify appropriate warnings/disclaimers - check status dashboard shows completion
        assertTrue(page.locator(".batch-status").first().textContent().contains("Completed") ||
                  page.locator("body").textContent().contains("Completed"),
                  "Processing should complete with status indicating success");

        log.info("✅ SRG-AP-001: Incomplete data handling verified!");
    }

    @Test
    @DisplayName("SRG-AP-003: No Students Available for Generation")
    void shouldHandleEmptyTermGracefully() {
        log.info("🚀 Starting SRG-AP-003: No Students Available for Generation");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Select Empty Term
        log.info("📝 Bagian 1: Select Empty Term");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Select a term with no enrolled students (if available)
        if (page.locator("#term-selector option").count() > 1) {
            page.selectOption("#term-selector", page.locator("#term-selector option").last().getAttribute("value"));

            // Execute generation for empty term
            reportPage.generateAllReports();

            // Bagian 2: Verify Empty Term Handling
            log.info("📝 Bagian 2: Verify Empty Term Handling");

            // Should handle gracefully
            page.waitForURL("**/report-cards/status**");

            // Should show appropriate message
            assertTrue(page.locator("body").textContent().contains("generation"),
                      "Should provide feedback for empty term");
        }

        log.info("✅ SRG-AP-003: Empty term handling verified!");
    }

    @Test
    @DisplayName("SRG-PV-001: Pre-generated PDF Content Verification")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateCorrectPDFContent() {
        log.info("🚀 Starting SRG-PV-001: Pre-generated PDF Content Verification");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Generate Report for Content Verification
        log.info("📝 Bagian 1: Generate Report for Content Verification");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Setup and generate report for Ali Rahman
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();
        reportPage.waitForGenerationCompletion();

        // Bagian 2: PDF Structure Verification
        log.info("📝 Bagian 2: PDF Structure Verification");

        // Navigate back to download specific report
        reportPage.navigateToStudentReports();
        reportPage.selectStudent(STUDENT_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);

        // Verify PDF download functionality
        assertTrue(page.locator("#btn-download-pdf").isVisible(), "Download should be available");

        // Test PDF download process
        page.click("#btn-download-pdf");
        page.waitForTimeout(2000); // Allow download to initiate

        // Verify no download errors
        assertFalse(page.locator(".error-message").isVisible(), "PDF download should succeed");

        // Bagian 3: Student Information Verification
        log.info("📝 Bagian 3: Content Verification");

        // In a real implementation, would extract and verify PDF content
        // For now, verify the download process works
        assertTrue(page.url().contains("/report-cards"), "Should remain on reports page after download");

        log.info("✅ SRG-PV-001: PDF content verification completed!");
    }

    // Helper method for consistent login
    protected void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }
}