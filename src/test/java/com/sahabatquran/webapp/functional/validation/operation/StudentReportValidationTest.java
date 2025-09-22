package com.sahabatquran.webapp.functional.validation.operation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.microsoft.playwright.Page;
import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.StudentReportPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Student Report Validation Tests for Alternate Path scenarios.
 * Tests error handling, missing data validation, and edge cases.
 * 
 * User Role: ACADEMIC_ADMIN
 * Focus: Validation and error scenarios during report generation.
 */
@Slf4j
@DisplayName("LS-AP: Student Report Validation Alternate Path Scenarios")
class StudentReportValidationTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("LS-AP-001: Generate All Reports and Navigate to Status Dashboard")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateAllReportsAndShowStatus() {
        log.info("🚀 Starting LS-AP-001: Generate All Reports with Status Dashboard...");

        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        StudentReportPage reportPage = new StudentReportPage(page);

        loginAsAdmin();

        // Bagian 1: Navigate to Reports Page
        log.info("📝 Bagian 1: Navigate to Reports Page");

        reportPage.navigateToStudentReports();

        // Verify students overview is displayed (simplified UI)
        assertTrue(page.locator(".simple-form").first().isVisible(), "Report generation form should be visible");
        assertTrue(page.locator("#term-selector").isVisible(), "Term selector should be visible");

        // Bagian 2: Generate All Reports
        log.info("📝 Bagian 2: Generate All Reports for Term");

        // Select term and generate all reports
        page.selectOption("#term-selector", ACADEMIC_TERM);
        page.locator("#btn-generate-all").click();

        // Should redirect to status dashboard
        page.waitForURL("**/report-cards/status**", new Page.WaitForURLOptions().setTimeout(10000));
        assertTrue(page.url().contains("/report-cards/status"), "Should redirect to status dashboard");

        // Bagian 3: Verify Status Dashboard
        log.info("📝 Bagian 3: Verify Status Dashboard");

        // Check status dashboard elements
        assertTrue(page.locator("#status-dashboard").isVisible(), "Status dashboard should be visible");
        assertTrue(page.locator("#refresh-button").isVisible(), "Refresh button should be visible");
        assertTrue(page.locator(".back-to-reports").isVisible(), "Back to reports link should be visible");

        // Check for job status display
        if (page.locator(".batch-item").count() > 0) {
            assertTrue(page.locator(".status-badge").first().isVisible(), "Status badge should be visible");
            log.info("✅ Report generation job created and visible in status dashboard");
        } else {
            log.warn("⚠️ No batch items visible - generation may be too fast or not started");
        }

        log.info("✅ LS-AP-001: Generate All Reports with Status Dashboard completed!");
    }
    
    @Test
    @DisplayName("LS-AP-002: Regenerate Individual Student Report")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRegenerateIndividualStudentReport() {
        log.info("🚀 Starting LS-AP-002: Regenerate Individual Student Report...");

        final String STUDENT_NAME = "Ahmad Fauzan";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        StudentReportPage reportPage = new StudentReportPage(page);

        loginAsAdmin();
        reportPage.navigateToStudentReports();

        // Bagian 1: Navigate to Regenerate Section
        log.info("📝 Bagian 1: Navigate to Regenerate Section");

        // Scroll to regenerate section if needed
        page.evaluate("document.querySelector('#btn-regenerate')?.scrollIntoView({behavior: 'smooth', block: 'center'})");
        page.waitForTimeout(500); // Allow smooth scroll

        // Verify regenerate form is visible
        assertTrue(page.locator("#student-selector").isVisible(), "Student selector should be visible");
        assertTrue(page.locator("#regenerate-term-selector").isVisible(), "Term selector for regenerate should be visible");

        // Bagian 2: Select Student and Term for Regeneration
        log.info("📝 Bagian 2: Select Student and Term for Regeneration");

        // Select student
        page.selectOption("#student-selector", STUDENT_NAME);

        // Select term
        page.selectOption("#regenerate-term-selector", ACADEMIC_TERM);

        // Bagian 3: Regenerate Report
        log.info("📝 Bagian 3: Regenerate Report");

        // Click regenerate with confirmation handling
        page.onceDialog(dialog -> {
            log.info("Confirmation dialog: " + dialog.message());
            assertTrue(dialog.message().contains(STUDENT_NAME), "Confirmation should mention student name");
            dialog.accept();
        });

        page.locator("#btn-regenerate").click();

        // Should redirect to status dashboard
        page.waitForURL("**/report-cards/status**", new Page.WaitForURLOptions().setTimeout(10000));
        assertTrue(page.url().contains("/report-cards/status"), "Should redirect to status dashboard after regeneration");

        // Verify status dashboard shows the regeneration job
        assertTrue(page.locator("#status-dashboard").isVisible(), "Status dashboard should be visible");

        log.info("✅ LS-AP-002: Individual Student Report Regeneration completed!");
    }
    
    @Test
    @DisplayName("LS-AP-003: Download Pre-Generated PDF Report")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldDownloadPreGeneratedPdfReport() {
        log.info("🚀 Starting LS-AP-003: Download Pre-Generated PDF Report...");

        final String STUDENT_NAME = "Ahmad Fauzan";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        StudentReportPage reportPage = new StudentReportPage(page);

        loginAsAdmin();
        reportPage.navigateToStudentReports();

        // Bagian 1: Generate PDFs first to ensure they exist
        log.info("📝 Bagian 1: Generate PDFs to ensure they exist");

        // Select term and generate all reports
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();

        // Wait for generation to complete
        page.waitForURL("**/report-cards/status**", new Page.WaitForURLOptions().setTimeout(10000));
        reportPage.waitForGenerationCompletion();

        // Navigate back to reports page for download
        reportPage.navigateToStudentReports();

        // Bagian 2: Navigate to Download Section
        log.info("📝 Bagian 2: Navigate to Download Section");

        // Scroll to download section
        page.evaluate("document.querySelector('#btn-download-pdf')?.scrollIntoView({behavior: 'smooth', block: 'center'})");
        page.waitForTimeout(500);

        // Verify download form is visible
        assertTrue(page.locator("#download-student-selector").isVisible(), "Download student selector should be visible");
        assertTrue(page.locator("#download-term-selector").isVisible(), "Download term selector should be visible");

        // Bagian 3: Select Student and Term for Download
        log.info("📝 Bagian 3: Select Student and Term for Download");

        // Select student for download
        page.selectOption("#download-student-selector", STUDENT_NAME);

        // Select term for download
        page.selectOption("#download-term-selector", ACADEMIC_TERM);

        // Bagian 4: Download PDF
        log.info("📝 Bagian 4: Download PDF Report");

        // Set up download handler
        var download = page.waitForDownload(() -> {
            page.locator("#btn-download-pdf").click();
        });

        // Verify download initiated
        assertNotNull(download, "Download should be initiated");
        String fileName = download.suggestedFilename();
        log.info("Downloaded file: " + fileName);

        // Verify file name contains student info or report identifier
        assertTrue(fileName.endsWith(".pdf"), "Downloaded file should be a PDF");

        // Clean up downloaded file
        download.delete();

        log.info("✅ LS-AP-003: PDF Report Download completed successfully!");
    }
    
    @Test
    @DisplayName("LS-AP-004: Manual Status Refresh on Dashboard")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldManuallyRefreshStatusDashboard() {
        log.info("🚀 Starting LS-AP-004: Manual Status Refresh on Dashboard...");

        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        StudentReportPage reportPage = new StudentReportPage(page);

        loginAsAdmin();

        // Bagian 1: Generate Reports and Go to Status
        log.info("📝 Bagian 1: Generate Reports and Navigate to Status");

        reportPage.navigateToStudentReports();

        // Generate all reports
        page.selectOption("#term-selector", ACADEMIC_TERM);
        page.locator("#btn-generate-all").click();

        // Wait for redirect to status dashboard
        page.waitForURL("**/report-cards/status**", new Page.WaitForURLOptions().setTimeout(10000));

        // Bagian 2: Test Manual Refresh
        log.info("📝 Bagian 2: Test Manual Refresh Functionality");

        // Capture initial state
        int initialBatchCount = page.locator(".batch-item").count();
        log.info("Initial batch count: " + initialBatchCount);

        // Click refresh button
        page.locator("#refresh-button").click();

        // Wait for page reload
        page.waitForLoadState();

        // Verify page reloaded
        assertTrue(page.locator("#status-dashboard").isVisible(), "Status dashboard should still be visible after refresh");
        assertTrue(page.locator("#refresh-button").isVisible(), "Refresh button should still be visible");

        // Bagian 3: Navigate Back to Reports
        log.info("📝 Bagian 3: Navigate Back to Reports");

        // Click back to reports
        page.locator(".back-to-reports").click();

        // Verify we're back on reports page
        page.waitForURL("**/report-cards", new Page.WaitForURLOptions().setTimeout(5000));
        assertTrue(page.url().endsWith("/report-cards"), "Should navigate back to reports page");
        assertTrue(page.locator("#student-reports").isVisible(), "Student reports page should be visible");

        log.info("✅ LS-AP-004: Manual Status Refresh completed successfully!");
    }
    
    @Test
    @DisplayName("LS-AP-005: Handle Missing Student Selection in Forms")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleMissingStudentSelection() {
        log.info("🚀 Starting LS-AP-005: Handle Missing Student Selection in Forms...");

        StudentReportPage reportPage = new StudentReportPage(page);

        loginAsAdmin();
        reportPage.navigateToStudentReports();

        // Bagian 1: Test Regenerate Form Validation
        log.info("📝 Bagian 1: Test Regenerate Form Validation");

        // Scroll to regenerate section
        page.evaluate("document.querySelector('#btn-regenerate')?.scrollIntoView({behavior: 'smooth', block: 'center'})");
        page.waitForTimeout(500);

        // Try to submit without selecting student
        page.selectOption("#regenerate-term-selector", "Semester 1 2024/2025");

        // Check button state (HTML5 form validation should prevent submission)
        boolean isDisabled = page.locator("#btn-regenerate").isDisabled();
        log.info("Button disabled state when form incomplete: " + isDisabled);

        // If browser doesn't disable button, check if form validation works by examining required attributes
        boolean formHasValidation = page.locator("#student-selector[required]").count() > 0 &&
                                  page.locator("#regenerate-term-selector[required]").count() > 0;
        assertTrue(formHasValidation, "Form should have required field validation");

        // Now select a student - get the first non-empty option value
        String firstStudentValue = page.locator("#student-selector option").nth(1).getAttribute("value");
        page.selectOption("#student-selector", firstStudentValue);

        // Verify form can now be completed
        assertFalse(page.locator("#student-selector").evaluate("el => el.value === ''").toString().equals("true"),
                   "Student should be selected");

        // Bagian 2: Test Download Form Validation
        log.info("📝 Bagian 2: Test Download Form Validation");

        // Scroll to download section
        page.evaluate("document.querySelector('#btn-download-pdf')?.scrollIntoView({behavior: 'smooth', block: 'center'})");
        page.waitForTimeout(500);

        // Reset download form
        page.reload();
        page.waitForLoadState();

        // Scroll again after reload
        page.evaluate("document.querySelector('#btn-download-pdf')?.scrollIntoView({behavior: 'smooth', block: 'center'})");
        page.waitForTimeout(500);

        // Try to submit download without selecting student
        page.selectOption("#download-term-selector", "Semester 1 2024/2025");

        // Check download form validation
        boolean downloadFormHasValidation = page.locator("#download-student-selector[required]").count() > 0 &&
                                           page.locator("#download-term-selector[required]").count() > 0;
        assertTrue(downloadFormHasValidation, "Download form should have required field validation");

        // Select a student - get the first non-empty option value
        String firstDownloadStudentValue = page.locator("#download-student-selector option").nth(1).getAttribute("value");
        page.selectOption("#download-student-selector", firstDownloadStudentValue);

        // Verify download form can now be completed
        assertFalse(page.locator("#download-student-selector").evaluate("el => el.value === ''").toString().equals("true"),
                   "Download student should be selected");

        // Bagian 3: Verify Students Overview
        log.info("📝 Bagian 3: Verify Students Overview Section");

        // Scroll to students overview
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(500);

        // Check students are displayed
        if (page.locator(".simple-form").last().locator("div.grid > div").count() > 0) {
            assertTrue(page.locator(".simple-form").last().locator("div.grid > div").first().isVisible(),
                    "At least one student should be visible in overview");
            log.info("Students overview displays students correctly");
        } else {
            // No students case
            assertTrue(page.locator("text=No students found").isVisible(),
                    "No students message should be shown when no students exist");
            log.info("No students message displayed correctly");
        }

        log.info("✅ LS-AP-005: Form Validation and Students Overview completed successfully!");
    }
}
