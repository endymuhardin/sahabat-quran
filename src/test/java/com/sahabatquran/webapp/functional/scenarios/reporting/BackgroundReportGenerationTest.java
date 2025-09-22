package com.sahabatquran.webapp.functional.scenarios.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.StudentReportPage;
import com.sahabatquran.webapp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Simplified Report Generation Tests - Following StudentReportTest Pattern.
 * Tests the simplified report generation architecture with:
 * - Single generate button for all students
 * - Status dashboard with manual refresh (no real-time updates)
 * - Individual regeneration functionality
 * - Pre-generated PDF download system
 *
 * User Role: ACADEMIC_ADMIN, MANAGEMENT
 * Focus: Report generation, status monitoring, PDF verification.
 */
@Slf4j
@DisplayName("BRG: Background Report Generation Scenarios")
class BackgroundReportGenerationTest extends BasePlaywrightTest {

    @Autowired
    private UserRepository userRepository;

    private static final String ADMIN_USERNAME = "academic.admin1";
    private static final String ADMIN_PASSWORD = "Welcome@YSQ2024";
    private static final String ACADEMIC_TERM = "Semester 1 2024/2025";
    private static final String STUDENT_NAME = "Ali Rahman";

    @Test
    @DisplayName("BRG-001: Individual Student Report Generation")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateIndividualStudentBackgroundReport() {
        log.info("🚀 Starting BRG-001: Individual Student Report Generation...");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Access Report Generation Page
        log.info("📝 Bagian 1: Access Report Generation Page");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        page.waitForURL("**/dashboard**");

        // Navigate to Student Reports
        reportPage.navigateToStudentReports();
        page.waitForURL("**/report-cards**");

        // Verify simplified interface
        assertTrue(page.locator("#student-reports").isVisible(), "Student reports page should be accessible");
        assertTrue(page.locator("#term-selector").isVisible(), "Term dropdown should be available");
        assertTrue(page.locator("#btn-generate-all").isVisible(), "Single generate button should be visible");

        // Bagian 2: Generate Reports
        log.info("📝 Bagian 2: Generate Reports");

        // Select term and generate
        page.selectOption("#term-selector", ACADEMIC_TERM);
        page.click("#btn-generate-all");

        // Wait for redirect to status dashboard
        page.waitForURL("**/report-cards/status**");
        assertTrue(page.locator("#status-dashboard").isVisible(), "Should redirect to status dashboard");

        // Bagian 3: Monitor Processing Status
        log.info("📝 Bagian 3: Monitor Processing Status");

        // Wait for generation completion
        reportPage.waitForGenerationCompletion();
        assertTrue(reportPage.isGenerationCompleted(), "Generation should complete successfully");

        // Bagian 4: Verify Results
        log.info("📝 Bagian 4: Verify Results");

        // Verify download links are available
        assertTrue(page.locator(".download-links").first().isVisible(), "Download links should be available");

        log.info("✅ BRG-001 completed successfully");
    }

    @Test
    @DisplayName("BRG-002: Bulk Class Report Generation")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateBulkClassBackgroundReports() {
        log.info("🚀 Starting BRG-002: Bulk Class Report Generation...");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Initiate Bulk Generation
        log.info("📝 Bagian 1: Initiate Bulk Generation");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Select term for bulk generation
        page.selectOption("#term-selector", ACADEMIC_TERM);

        // Generate all reports with single button
        reportPage.generateAllReports();

        // Bagian 2: Monitor Bulk Processing
        log.info("📝 Bagian 2: Monitor Bulk Processing");

        // Verify status dashboard
        page.waitForURL("**/report-cards/status**");
        assertTrue(page.locator("#status-dashboard").isVisible(), "Status dashboard should be visible");

        // Wait for completion
        reportPage.waitForGenerationCompletion();

        // Bagian 3: Verify Results
        log.info("📝 Bagian 3: Verify Results");

        assertTrue(reportPage.isGenerationCompleted(), "Bulk generation should be completed");

        log.info("✅ BRG-002 completed successfully");
    }

    @Test
    @DisplayName("BRG-003: Processing Status Monitoring")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldMonitorBackgroundProcessingStatus() {
        log.info("🚀 Starting BRG-003: Processing Status Monitoring...");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Start Generation
        log.info("📝 Bagian 1: Start Generation");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Start report generation
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();

        // Bagian 2: Monitor Status
        log.info("📝 Bagian 2: Monitor Status");

        // Verify status dashboard components
        page.waitForURL("**/report-cards/status**");
        assertTrue(reportPage.isStatusDashboardVisible(), "Status dashboard should be visible");

        // Verify no real-time updates (simplified architecture)
        assertFalse(page.locator(".real-time-progress").isVisible(), "No real-time progress elements");
        assertFalse(page.locator(".auto-refresh-indicator").isVisible(), "No auto-refresh functionality");

        // Manual refresh to check status
        page.reload();
        assertTrue(reportPage.isStatusDashboardVisible(), "Dashboard should remain accessible after refresh");

        // Bagian 3: Verify Completion
        log.info("📝 Bagian 3: Verify Completion");

        reportPage.waitForGenerationCompletion();
        assertTrue(reportPage.isGenerationCompleted(), "Should detect completion");

        log.info("✅ BRG-003 completed successfully");
    }

    @Test
    @DisplayName("BRG-004: Report Regeneration")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRegenerateReports() {
        log.info("🚀 Starting BRG-004: Report Regeneration...");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Initial Generation
        log.info("📝 Bagian 1: Initial Generation");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Generate initial reports
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();
        reportPage.waitForGenerationCompletion();

        // Bagian 2: Regenerate Individual Report
        log.info("📝 Bagian 2: Regenerate Individual Report");

        // Navigate back to reports
        reportPage.navigateToStudentReports();

        // Select student for regeneration
        reportPage.selectStudent(STUDENT_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);

        // Verify regenerate button available
        assertTrue(page.locator("#btn-regenerate").isVisible(), "Regenerate button should be available");

        // Execute regeneration
        reportPage.regenerateStudentReport();

        // Bagian 3: Verify Regeneration
        log.info("📝 Bagian 3: Verify Regeneration");

        page.waitForURL("**/report-cards/status**");
        reportPage.waitForGenerationCompletion();
        assertTrue(reportPage.isGenerationCompleted(), "Regeneration should complete");

        log.info("✅ BRG-004 completed successfully");
    }

    @Test
    @DisplayName("BRG-005: Error Recovery")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleErrorsAndRecovery() {
        log.info("🚀 Starting BRG-005: Error Recovery...");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Start Processing
        log.info("📝 Bagian 1: Start Processing");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Start generation
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();

        // Bagian 2: Simulate Interruption
        log.info("📝 Bagian 2: Simulate Interruption");

        // Simulate network interruption by refreshing
        page.reload();
        page.waitForLoadState();

        // Bagian 3: Verify Recovery
        log.info("📝 Bagian 3: Verify Recovery");

        // Navigate back to reports
        reportPage.navigateToStudentReports();

        // Verify system remains stable
        assertTrue(page.locator("#student-reports").isVisible(), "Reports page should remain accessible");
        assertTrue(page.locator("#btn-generate-all").isVisible(), "Generate button should be available");

        log.info("✅ BRG-005 completed successfully");
    }

    @Test
    @DisplayName("BRG-006: Main Reports Integration")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldIntegrateWithMainReportsSystem() {
        log.info("🚀 Starting BRG-006: Main Reports Integration...");

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Navigation Integration
        log.info("📝 Bagian 1: Navigation Integration");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to reports
        reportPage.navigateToStudentReports();
        page.waitForURL("**/report-cards**");

        // Verify main reports interface
        assertTrue(page.locator("#student-reports").isVisible(), "Main reports should be accessible");

        // Bagian 2: Status Dashboard Navigation
        log.info("📝 Bagian 2: Status Dashboard Navigation");

        // Navigate to status dashboard
        reportPage.navigateToStatusDashboard();
        assertTrue(reportPage.isStatusDashboardVisible(), "Status dashboard should be accessible");

        // Navigate back to main reports
        page.click(".back-to-reports");
        page.waitForURL("**/report-cards**");
        assertTrue(page.locator("#student-reports").isVisible(), "Should return to main reports");

        // Bagian 3: Data Consistency
        log.info("📝 Bagian 3: Data Consistency");

        // Verify data consistency across navigation
        assertTrue(page.locator("#term-selector").isVisible(), "Term selector should be consistent");
        assertTrue(page.locator("#btn-generate-all").isVisible(), "Generate button should be consistent");

        log.info("✅ BRG-006 completed successfully");
    }

    @Test
    @DisplayName("BRG-007: PDF Content Verification")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateReportWithCorrectPDFContent() {
        log.info("🚀 Starting BRG-007: PDF Content Verification...");

        // Expected data from test setup SQL
        final String EXPECTED_STUDENT_ID = "30000000-0000-0000-0000-000000000001";
        final double EXPECTED_PLACEMENT_SCORE = 85.0;
        final String EXPECTED_PLACEMENT_GRADE = "B+";
        final double EXPECTED_MIDTERM_SCORE = 88.5;
        final String EXPECTED_MIDTERM_GRADE = "A-";
        final double EXPECTED_FINAL_SCORE = 87.5;
        final String EXPECTED_FINAL_GRADE = "A-";

        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);

        // Bagian 1: Verify Test Data
        log.info("📝 Bagian 1: Verify Test Data");

        // Verify student exists in database
        User aliRahman = userRepository.findById(java.util.UUID.fromString(EXPECTED_STUDENT_ID))
                .orElseThrow(() -> new RuntimeException("Test student Ali Rahman not found"));
        assertEquals("Ali Rahman", aliRahman.getFullName(), "Student name should match test data");

        // Bagian 2: Generate Report
        log.info("📝 Bagian 2: Generate Report");

        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        reportPage.navigateToStudentReports();

        // Generate reports
        page.selectOption("#term-selector", ACADEMIC_TERM);
        reportPage.generateAllReports();
        reportPage.waitForGenerationCompletion();

        assertTrue(reportPage.isGenerationCompleted(), "Generation should be completed");

        // Bagian 3: Locate Generated PDF
        log.info("📝 Bagian 3: Locate Generated PDF");

        // Reports are generated in standard location during tests
        Path reportDir = Paths.get("src/test/resources/reports/students/" + EXPECTED_STUDENT_ID + "/d0000000-0000-0000-0000-000000000001");
        assertTrue(Files.exists(reportDir), "Reports directory should exist for student");

        // Find the PDF file for Ali Rahman
        Path pdfFile = reportDir.resolve("report-card.pdf");

        if (!Files.exists(pdfFile)) {
            // Fallback to searching in /tmp/reports if not in expected location
            Path tmpReportDir = Paths.get("/tmp/reports");
            if (Files.exists(tmpReportDir)) {
                try (Stream<Path> files = Files.list(tmpReportDir)) {
                    List<Path> tmpPdfFiles = files
                        .filter(path -> path.toString().endsWith(".pdf"))
                        .toList();
                    if (!tmpPdfFiles.isEmpty()) {
                        log.warn("PDF not found in expected location. Found PDFs in /tmp/reports:");
                        tmpPdfFiles.forEach(path -> log.warn("  - {}", path));
                    }
                } catch (IOException e) {
                    log.error("Failed to list /tmp/reports", e);
                }
            }
            throw new RuntimeException("PDF file not found for Ali Rahman at: " + pdfFile);
        }

        Path latestPdf = pdfFile;
        log.info("📄 Found generated PDF: {}", latestPdf);

        // Bagian 4: Verify PDF Content
        log.info("📝 Bagian 4: Verify PDF Content");

        String pdfContent = extractPdfContent(latestPdf);
        assertNotNull(pdfContent, "PDF content should not be null");
        assertTrue(pdfContent.length() > 100, "PDF should contain substantial content");

        // Debug: Log PDF content for analysis
        log.info("📝 PDF Content Preview (first 500 characters):");
        log.info(pdfContent.length() > 500 ? pdfContent.substring(0, 500) + "..." : pdfContent);

        // Verify student information
        assertTrue(pdfContent.contains("Ali Rahman"), "PDF should contain student name");
        assertTrue(pdfContent.contains("siswa.ali"), "PDF should contain student ID");
        assertTrue(pdfContent.contains("ali@gmail.com"), "PDF should contain student email");

        // Verify assessment scores
        assertTrue(pdfContent.contains("85") || pdfContent.contains("85.0"),
                "PDF should contain placement score");
        assertTrue(pdfContent.contains("B+"), "PDF should contain placement grade");

        assertTrue(pdfContent.contains("88.5") || pdfContent.contains("88"),
                "PDF should contain midterm score");
        assertTrue(pdfContent.contains("A-"), "PDF should contain midterm grade");

        assertTrue(pdfContent.contains("87.5") || pdfContent.contains("87"),
                "PDF should contain final score");

        // Verify assessment types
        assertTrue(pdfContent.contains("PLACEMENT") || pdfContent.contains("Placement"),
                "PDF should contain placement assessment");
        assertTrue(pdfContent.contains("MIDTERM") || pdfContent.contains("Midterm"),
                "PDF should contain midterm assessment");
        assertTrue(pdfContent.contains("FINAL") || pdfContent.contains("Final"),
                "PDF should contain final assessment");

        // Bagian 5: Verify PDF Properties
        log.info("📝 Bagian 5: Verify PDF Properties");

        File pdfFileObj = latestPdf.toFile();
        assertTrue(pdfFileObj.exists(), "PDF file should exist on filesystem");
        assertTrue(pdfFileObj.length() > 1000, "PDF file should have reasonable size (>1KB)");
        assertTrue(pdfFileObj.length() < 10_000_000, "PDF file should not be excessively large (<10MB)");

        // Verify PDF is valid
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(latestPdf.toString()))) {
            assertTrue(pdfDoc.getNumberOfPages() >= 1, "PDF should have at least 1 page");
            assertTrue(pdfDoc.getNumberOfPages() <= 10, "PDF should not have excessive pages");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF as valid PDF document", e);
        }

        // Bagian 6: Verify Data Consistency
        log.info("📝 Bagian 6: Verify Data Consistency");

        // Verify student data unchanged after processing
        User aliRahmanAfter = userRepository.findById(java.util.UUID.fromString(EXPECTED_STUDENT_ID))
                .orElseThrow(() -> new RuntimeException("Student should still exist after processing"));
        assertEquals("Ali Rahman", aliRahmanAfter.getFullName(),
                "Student name should be unchanged after processing");

        log.info("✅ BRG-007 completed successfully - PDF content verification passed");
        log.info("📊 PDF Summary:");
        log.info("   - File: {}", latestPdf.getFileName());
        log.info("   - Size: {} bytes", pdfFileObj.length());
        log.info("   - Content length: {} characters", pdfContent.length());
    }

    /**
     * Extract text content from PDF file for verification
     */
    private String extractPdfContent(Path pdfPath) {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath.toString()))) {
            StringBuilder content = new StringBuilder();

            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
                content.append(pageText).append("\n");
            }

            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract PDF content", e);
        }
    }
}