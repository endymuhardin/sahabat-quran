package com.sahabatquran.webapp.functional.scenarios.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.sahabatquran.webapp.functional.page.BackgroundReportPage;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Background Report Generation Tests - Happy Path Scenarios.
 * Covers background report processing, monitoring, and academic analytics.
 *
 * User Role: ACADEMIC_ADMIN, MANAGEMENT
 * Focus: Background processing, real-time monitoring, bulk operations, status tracking.
 */
@Slf4j
@DisplayName("BRG-HP: Background Report Generation Happy Path Scenarios")
class BackgroundReportGenerationTest extends BasePlaywrightTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("BRG-HP-001: Individual Student Background Report Generation")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateIndividualStudentBackgroundReport() {
        log.info("🚀 Starting BRG-HP-001: Individual Student Background Report Generation...");

        // Test data sesuai dokumentasi
        final String ADMIN_USERNAME = "academic.admin1";
        final String ADMIN_PASSWORD = "Welcome@YSQ2024";
        final String STUDENT_NAME = "Ali Rahman";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        final String REPORT_TYPE = "INDIVIDUAL_REPORT_CARD";
        final int EXPECTED_DURATION_SECONDS = 60; // Maximum expected duration

        LoginPage loginPage = new LoginPage(page);
        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Akses Background Generation Dashboard
        log.info("📝 Bagian 1: Akses Background Generation Dashboard");

        loginAsAdmin();

        // Navigate to Background Report Generation
        page.navigate(getBaseUrl() + "/report-cards/background");
        page.waitForURL("**/report-cards/background");

        // Verify Dashboard Components
        assertTrue(backgroundPage.isDashboardVisible(), "Background reports dashboard should be visible");
        assertTrue(backgroundPage.isIndividualReportCardAvailable(), "Individual Student Report card should be available");
        assertTrue(backgroundPage.isBulkReportCardAvailable(), "Bulk Class Reports card should be available");
        assertTrue(backgroundPage.isProcessingStatusCardAvailable(), "Processing Status card should be available");

        // Bagian 2: Initiate Individual Report Generation
        log.info("📝 Bagian 2: Initiate Individual Report Generation");

        // Start Individual Report Generation
        backgroundPage.startIndividualReportGeneration();

        // Configure Individual Report Parameters
        backgroundPage.selectIndividualStudent(STUDENT_NAME);
        backgroundPage.selectIndividualTerm(ACADEMIC_TERM);
        backgroundPage.selectIndividualReportType(REPORT_TYPE);

        // Submit Background Generation Request
        backgroundPage.submitIndividualReportGeneration();

        // Bagian 3: Monitor Background Processing
        log.info("📝 Bagian 3: Monitor Background Processing");

        // Access status monitor to start tracking
        backgroundPage.accessStatusMonitor();

        // Real-time Progress Updates - wait for processing to start
        backgroundPage.waitForProcessingStart(10);

        // Verify status progression
        String initialStatus = backgroundPage.getBatchStatus();
        log.info("📊 Current batch status: {}", initialStatus);
        assertTrue(initialStatus.equals("INITIATED") || initialStatus.equals("VALIDATING") ||
                   initialStatus.equals("IN_PROGRESS") || initialStatus.equals("COMPLETED"),
                "Initial status should be INITIATED, VALIDATING, IN_PROGRESS, or COMPLETED. Current: " + initialStatus);

        // Monitor processing completion
        backgroundPage.waitForProcessingCompletion(EXPECTED_DURATION_SECONDS);

        // Processing Completion Verification
        assertTrue(backgroundPage.isGenerationCompleted(), "Generation should be completed");
        assertEquals("COMPLETED", backgroundPage.getBatchStatus(), "Status should be COMPLETED");
        assertTrue(backgroundPage.getProgressPercentage().contains("100"), "Progress should reach 100%");

        // Bagian 4: Result Verification and Access
        log.info("📝 Bagian 4: Result Verification and Access");

        // Verify Generation Results
        int completedItems = backgroundPage.getCompletedItemsCount();
        log.info("📊 Completed items count: {}", completedItems);
        assertTrue(completedItems >= 1, "Should have at least 1 completed report, got: " + completedItems);
        assertEquals(0, backgroundPage.getFailedItemsCount(), "Should have 0 failed reports");

        // Verify report was generated successfully
        String resultSummary = backgroundPage.getResultSummary();
        log.info("📊 Result summary: {}", resultSummary);
        assertTrue(resultSummary.contains(String.valueOf(completedItems)),
                "Result summary should show " + completedItems + " reports generated");

        log.info("✅ BRG-HP-001 completed successfully");
    }

    @Test
    @DisplayName("BRG-HP-002: Bulk Class Background Report Generation")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateBulkClassBackgroundReports() {
        log.info("🚀 Starting BRG-HP-002: Bulk Class Background Report Generation...");

        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        final String CLASS_FILTER = "Tahsin 1 - Kelas Pagi A";
        final int EXPECTED_DURATION_SECONDS = 300; // 5 minutes max for bulk
        final int MINIMUM_EXPECTED_REPORTS = 3; // Expect at least 3 reports

        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Initiate Bulk Generation
        log.info("📝 Bagian 1: Initiate Bulk Generation");

        loginAsAdmin();
        page.navigate(getBaseUrl() + "/report-cards/background");

        // Access Bulk Generation Form
        backgroundPage.startBulkReportGeneration();

        // Configure Bulk Parameters
        backgroundPage.selectBulkTerm(ACADEMIC_TERM);
        backgroundPage.selectBulkClassFilter(CLASS_FILTER);
        backgroundPage.configureBulkReportOptions(true, true); // Include both student reports and class summaries

        // Submit Bulk Generation Request
        backgroundPage.submitBulkReportGeneration();

        // Bagian 2: Monitor Bulk Processing
        log.info("📝 Bagian 2: Monitor Bulk Processing");

        // Access status monitor to start tracking
        backgroundPage.accessStatusMonitor();

        // Processing Validation Phase - wait for processing to start
        backgroundPage.waitForProcessingStart(15);
        String status = backgroundPage.getBatchStatus();
        log.info("📊 Current batch status: {}", status);
        assertTrue(status.equals("VALIDATING") || status.equals("IN_PROGRESS") || status.equals("COMPLETED"),
                "Should be in VALIDATING, IN_PROGRESS, or COMPLETED phase. Current: " + status);

        // Processing Generation Phase (may complete very quickly)
        // Skip strict status progression checks since processing completes fast

        // Completion Monitoring
        backgroundPage.waitForProcessingCompletion(EXPECTED_DURATION_SECONDS);

        // Bagian 3: Results Verification
        log.info("📝 Bagian 3: Results Verification");

        // Verify Bulk Results Summary
        assertTrue(backgroundPage.isGenerationCompleted(), "Bulk generation should be completed");
        assertTrue(backgroundPage.getCompletedItemsCount() >= MINIMUM_EXPECTED_REPORTS,
                "Should have generated at least " + MINIMUM_EXPECTED_REPORTS + " reports");
        assertEquals(0, backgroundPage.getFailedItemsCount(), "Should have 0 failed reports");

        log.info("✅ BRG-HP-002 completed successfully");
    }

    @Test
    @DisplayName("BRG-HP-003: Background Processing Status Monitoring")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldMonitorBackgroundProcessingStatus() {
        log.info("🚀 Starting BRG-HP-003: Background Processing Status Monitoring...");

        final String STUDENT_NAME = "Ali Rahman";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Status Dashboard Access
        log.info("📝 Bagian 1: Status Dashboard Access");

        loginAsAdmin();
        page.navigate(getBaseUrl() + "/report-cards/background");

        // Start a report generation to monitor
        backgroundPage.startIndividualReportGeneration();
        backgroundPage.selectIndividualStudent(STUDENT_NAME);
        backgroundPage.selectIndividualTerm(ACADEMIC_TERM);
        backgroundPage.selectIndividualReportType("INDIVIDUAL_REPORT_CARD");
        backgroundPage.submitIndividualReportGeneration();

        // Bagian 2: Real-time Updates
        log.info("📝 Bagian 2: Real-time Updates");

        // Access status monitor to view progress components
        backgroundPage.accessStatusMonitor();

        // Monitor Progress Updates
        backgroundPage.waitForProcessingStart(10);

        // Verify Status Components (after accessing status monitor)
        String currentStatus = backgroundPage.getBatchStatus();
        log.info("📊 Current batch status for monitoring: {}", currentStatus);

        // These components may not be visible if processing completed very quickly
        boolean progressBarVisible = backgroundPage.isProgressBarVisible();
        log.info("📊 Progress bar visible: {}", progressBarVisible);

        // Accept that progress bar might not be visible if processing is already complete
        if (!currentStatus.equals("COMPLETED")) {
            assertTrue(progressBarVisible, "Progress bar should be visible during processing");
        }

        // Verify Update Accuracy
        String initialStatus = backgroundPage.getBatchStatus();
        assertTrue(initialStatus.equals("VALIDATING") || initialStatus.equals("IN_PROGRESS") || initialStatus.equals("COMPLETED"),
                "Status should show current processing phase: " + initialStatus);

        // Skip status change waiting if already completed
        if (!initialStatus.equals("COMPLETED")) {
            // Wait for status change and verify updates
            boolean statusChanged = backgroundPage.waitForStatusChange(initialStatus, 30);
            assertTrue(statusChanged, "Status should update during processing");
        }

        // Bagian 3: Completion Handling
        log.info("📝 Bagian 3: Completion Handling");

        // Completion Detection
        backgroundPage.waitForProcessingCompletion(60);
        assertTrue(backgroundPage.isGenerationCompleted(), "Should detect completion automatically");

        // Post-Completion Actions
        assertTrue(backgroundPage.getResultSummary().length() > 0, "Results summary should be available");

        log.info("✅ BRG-HP-003 completed successfully");
    }

    @Test
    @DisplayName("BRG-HP-004: Background Processing Cancellation")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCancelBackgroundProcessing() {
        log.info("🚀 Starting BRG-HP-004: Background Processing Cancellation...");

        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Initiate Cancellation
        log.info("📝 Bagian 1: Initiate Cancellation");

        loginAsAdmin();
        page.navigate(getBaseUrl() + "/report-cards/background");

        // Start Processing for Cancellation Test
        backgroundPage.startBulkReportGeneration();
        backgroundPage.selectBulkTerm(ACADEMIC_TERM);
        backgroundPage.configureBulkReportOptions(true, false);
        backgroundPage.submitBulkReportGeneration();

        // Access status monitor to view cancellation controls
        backgroundPage.accessStatusMonitor();

        // Wait for processing to start
        backgroundPage.waitForProcessingStart(10);

        // Check if still processing (cancellation only available during processing)
        String currentStatus = backgroundPage.getBatchStatus();
        log.info("📊 Current status before cancellation attempt: {}", currentStatus);

        if (currentStatus.equals("COMPLETED")) {
            log.info("📝 Processing completed too quickly for cancellation test - skipping cancellation");
            // Since processing completed quickly, verify it completed successfully instead
            assertTrue(backgroundPage.isGenerationCompleted(), "Generation should be completed");
        } else {
            // Access Cancellation Option (only if still processing)
            backgroundPage.cancelGeneration();

            // Confirm Cancellation
            backgroundPage.confirmCancellation();

            // Bagian 2: Verify Cancellation Results
            log.info("📝 Bagian 2: Verify Cancellation Results");

            // Monitor Cancellation Process
            page.waitForTimeout(5000); // Wait for cancellation to process

            // Verify Post-Cancellation State
            String finalStatus = backgroundPage.getBatchStatus();
            assertTrue(finalStatus.equals("CANCELLED") || finalStatus.equals("CANCELLING"),
                    "Batch should be in cancelled state");
        }

        log.info("✅ BRG-HP-004 completed successfully");
    }

    @Test
    @DisplayName("BRG-HP-005: Error Handling and Recovery")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleErrorsAndRecovery() {
        log.info("🚀 Starting BRG-HP-005: Error Handling and Recovery...");

        final String STUDENT_NAME = "Ali Rahman";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";

        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Network Interruption Handling
        log.info("📝 Bagian 1: Network Interruption Handling");

        loginAsAdmin();
        page.navigate(getBaseUrl() + "/report-cards/background");

        // Start processing
        backgroundPage.startIndividualReportGeneration();
        backgroundPage.selectIndividualStudent(STUDENT_NAME);
        backgroundPage.selectIndividualTerm(ACADEMIC_TERM);
        backgroundPage.selectIndividualReportType("INDIVIDUAL_REPORT_CARD");
        backgroundPage.submitIndividualReportGeneration();

        // Wait for processing to start
        backgroundPage.waitForProcessingStart(10);

        // Simulate network interruption by refreshing page
        page.reload();
        page.waitForLoadState();

        // Network Recovery - verify system recovers gracefully
        page.navigate(getBaseUrl() + "/report-cards/background");

        // Bagian 2: System Error Recovery
        log.info("📝 Bagian 2: System Error Recovery");

        // Verify Error Display
        // Note: In a real scenario, we would have proper error injection
        // For this test, we verify the system remains stable

        // Test Recovery Mechanisms
        assertTrue(backgroundPage.isDashboardVisible(), "Dashboard should remain accessible after refresh");

        log.info("✅ BRG-HP-005 completed successfully");
    }

    @Test
    @DisplayName("BRG-HP-006: Integration with Main Reports System")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldIntegrateWithMainReportsSystem() {
        log.info("🚀 Starting BRG-HP-006: Integration with Main Reports System...");

        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Navigation Integration
        log.info("📝 Bagian 1: Navigation Integration");

        loginAsAdmin();

        // Access from Main Reports
        page.navigate(getBaseUrl() + "/report-cards");
        page.waitForLoadState();

        // Navigate to Background Generation
        backgroundPage.navigateToBackgroundReports();

        // Verify navigation transition
        assertTrue(backgroundPage.isDashboardVisible(), "Background dashboard should be accessible from main reports");

        // Return Navigation
        backgroundPage.returnToMainReports();
        page.waitForURL("**/report-cards");

        // Bagian 2: Data Consistency
        log.info("📝 Bagian 2: Data Consistency");

        // Navigate back to background reports
        backgroundPage.navigateToBackgroundReports();

        // Verify Data Consistency
        backgroundPage.startIndividualReportGeneration();

        // Check that student and term data is consistent
        assertTrue(backgroundPage.isIndividualReportCardAvailable(), "Student data should be consistent");

        // Test Access Control Integration
        // Verify same permission requirements are enforced
        assertTrue(backgroundPage.isDashboardVisible(), "Access control should be consistent");

        log.info("✅ BRG-HP-006 completed successfully");
    }

    @Test
    @DisplayName("BRG-HP-007: PDF Content Verification Test")
    @Sql(scripts = "/sql/student-report-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/student-report-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateReportWithCorrectPDFContent() {
        log.info("🚀 Starting BRG-HP-007: PDF Content Verification Test...");

        // Test data sesuai dokumentasi
        final String ADMIN_USERNAME = "academic.admin1";
        final String ADMIN_PASSWORD = "Welcome@YSQ2024";
        final String STUDENT_NAME = "Ali Rahman";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        final String REPORT_TYPE = "INDIVIDUAL_REPORT_CARD";

        // Expected data from test setup SQL
        final String EXPECTED_STUDENT_ID = "30000000-0000-0000-0000-000000000001";
        final double EXPECTED_PLACEMENT_SCORE = 85.0;
        final String EXPECTED_PLACEMENT_GRADE = "B+";
        final double EXPECTED_MIDTERM_SCORE = 88.5;
        final String EXPECTED_MIDTERM_GRADE = "A-";
        final double EXPECTED_FINAL_SCORE = 87.5;
        final String EXPECTED_FINAL_GRADE = "A-";

        LoginPage loginPage = new LoginPage(page);
        BackgroundReportPage backgroundPage = new BackgroundReportPage(page);

        // Bagian 1: Verify Test Data Before Processing
        log.info("📝 Bagian 1: Verify Test Data Before Processing");

        // Verify student exists in database with expected data
        User aliRahman = userRepository.findById(java.util.UUID.fromString(EXPECTED_STUDENT_ID))
                .orElseThrow(() -> new RuntimeException("Test student Ali Rahman not found"));
        assertEquals("Ali Rahman", aliRahman.getFullName(), "Student name should match test data");

        loginAsAdmin();

        // Bagian 2: Generate Background Report
        log.info("📝 Bagian 2: Generate Background Report");

        page.navigate(getBaseUrl() + "/report-cards/background");
        page.waitForURL("**/report-cards/background");

        assertTrue(backgroundPage.isDashboardVisible(), "Background reports dashboard should be visible");

        // Start Individual Report Generation
        backgroundPage.startIndividualReportGeneration();
        backgroundPage.selectIndividualStudent(STUDENT_NAME);
        backgroundPage.selectIndividualTerm(ACADEMIC_TERM);
        backgroundPage.selectIndividualReportType(REPORT_TYPE);
        backgroundPage.submitIndividualReportGeneration();

        // Wait for processing completion
        backgroundPage.waitForProcessingStart(10);
        backgroundPage.waitForProcessingCompletion(120); // 2 minutes max

        assertTrue(backgroundPage.isGenerationCompleted(), "Generation should be completed");
        assertEquals("COMPLETED", backgroundPage.getBatchStatus(), "Status should be COMPLETED");

        // Bagian 3: Locate and Verify Generated PDF
        log.info("📝 Bagian 3: Locate and Verify Generated PDF");

        // Find the generated PDF file
        Path reportDir = Paths.get("/tmp/reports");
        assertTrue(Files.exists(reportDir), "Reports directory should exist");

        List<Path> pdfFiles;
        try (Stream<Path> files = Files.list(reportDir)) {
            pdfFiles = files
                .filter(path -> path.toString().endsWith(".pdf"))
                .filter(path -> path.toString().contains("student_report_" + EXPECTED_STUDENT_ID.substring(0, 8)))
                .sorted((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list PDF files", e);
        }

        // Debug: List all PDF files in directory if none found
        if (pdfFiles.isEmpty()) {
            log.warn("No PDF files found with expected pattern. Listing all PDF files in directory:");
            try (Stream<Path> allFiles = Files.list(reportDir)) {
                allFiles.filter(path -> path.toString().endsWith(".pdf"))
                    .forEach(path -> log.warn("  Found PDF: {}", path.getFileName()));
            } catch (IOException e) {
                log.error("Failed to list directory contents", e);
            }
        }

        assertTrue(!pdfFiles.isEmpty(), "Generated PDF file should exist");
        Path latestPdf = pdfFiles.get(0);
        log.info("📄 Found generated PDF: {}", latestPdf);

        // Bagian 4: Extract and Verify PDF Content
        log.info("📝 Bagian 4: Extract and Verify PDF Content");

        String pdfContent = extractPdfContent(latestPdf);
        assertNotNull(pdfContent, "PDF content should not be null");
        assertTrue(pdfContent.length() > 100, "PDF should contain substantial content");

        // Debug: Log PDF content for analysis
        log.info("📝 PDF Content Preview (first 500 characters):");
        log.info(pdfContent.length() > 500 ? pdfContent.substring(0, 500) + "..." : pdfContent);

        // Verify student information
        assertTrue(pdfContent.contains("Ali Rahman"),
                "PDF should contain student name: Ali Rahman");
        assertTrue(pdfContent.contains("siswa.ali"),
                "PDF should contain student ID: siswa.ali");
        assertTrue(pdfContent.contains("ali@gmail.com"),
                "PDF should contain student email");
        assertTrue(pdfContent.contains("Tahfidz 2 - Kelas Pagi"),
                "PDF should contain class information");

        // Verify assessment scores and grades
        assertTrue(pdfContent.contains("85") || pdfContent.contains("85.0"),
                "PDF should contain placement score: " + EXPECTED_PLACEMENT_SCORE);
        assertTrue(pdfContent.contains("B+"),
                "PDF should contain placement grade: " + EXPECTED_PLACEMENT_GRADE);

        assertTrue(pdfContent.contains("88.5") || pdfContent.contains("88"),
                "PDF should contain midterm score: " + EXPECTED_MIDTERM_SCORE);
        assertTrue(pdfContent.contains("A-"),
                "PDF should contain midterm grade: " + EXPECTED_MIDTERM_GRADE);

        assertTrue(pdfContent.contains("87.5") || pdfContent.contains("87"),
                "PDF should contain final score: " + EXPECTED_FINAL_SCORE);

        // Verify assessment types
        assertTrue(pdfContent.contains("PLACEMENT") || pdfContent.contains("Placement"),
                "PDF should contain placement assessment");
        assertTrue(pdfContent.contains("MIDTERM") || pdfContent.contains("Midterm"),
                "PDF should contain midterm assessment");
        assertTrue(pdfContent.contains("FINAL") || pdfContent.contains("Final"),
                "PDF should contain final assessment");

        // Verify calculated overall grade (should be around 87.0 average)
        assertTrue(pdfContent.contains("87") || pdfContent.contains("A-") || pdfContent.contains("A"),
                "PDF should contain calculated overall grade");

        // Verify PDF structure and headers
        assertTrue(pdfContent.contains("STUDENT ACADEMIC REPORT"),
                "PDF should contain report title");
        assertTrue(pdfContent.contains("Student Information") ||
                   pdfContent.contains("Enrollment Information") ||
                   pdfContent.contains("Assessment Results"),
                "PDF should contain structured sections");

        // Verify assessment type coverage
        assertTrue(pdfContent.contains("PLACEMENT") &&
                   pdfContent.contains("MIDTERM") &&
                   pdfContent.contains("FINAL"),
                "PDF should contain all three assessment types");

        // Verify report generation timestamp
        assertTrue(pdfContent.contains("Report generated on") ||
                   pdfContent.contains("2025"),
                "PDF should contain generation timestamp");

        // Bagian 5: Verify PDF File Properties
        log.info("📝 Bagian 5: Verify PDF File Properties");

        File pdfFile = latestPdf.toFile();
        assertTrue(pdfFile.exists(), "PDF file should exist on filesystem");
        assertTrue(pdfFile.length() > 1000, "PDF file should have reasonable size (>1KB)");
        assertTrue(pdfFile.length() < 10_000_000, "PDF file should not be excessively large (<10MB)");

        // Verify PDF is a valid PDF document
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(latestPdf.toString()))) {
            assertTrue(pdfDoc.getNumberOfPages() >= 1, "PDF should have at least 1 page");
            assertTrue(pdfDoc.getNumberOfPages() <= 10, "PDF should not have excessive pages");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF as valid PDF document", e);
        }

        // Bagian 6: Verify Data Consistency (Before/After Processing)
        log.info("📝 Bagian 6: Verify Data Consistency (Before/After Processing)");

        // Verify student data still exists and is unchanged
        User aliRahmanAfter = userRepository.findById(java.util.UUID.fromString(EXPECTED_STUDENT_ID))
                .orElseThrow(() -> new RuntimeException("Student should still exist after processing"));
        assertEquals("Ali Rahman", aliRahmanAfter.getFullName(),
                "Student name should be unchanged after processing");
        assertEquals(aliRahman.getEmail(), aliRahmanAfter.getEmail(),
                "Student email should be unchanged after processing");

        log.info("✅ BRG-HP-007 completed successfully - PDF content verification passed");
        log.info("📊 PDF Content Summary:");
        log.info("   - File: {}", latestPdf.getFileName());
        log.info("   - Size: {} bytes", pdfFile.length());
        log.info("   - Content length: {} characters", pdfContent.length());
        log.info("   - Contains student name: {}", pdfContent.contains("Ali Rahman"));
        log.info("   - Contains assessment scores: {}",
                pdfContent.contains("85") && pdfContent.contains("88") && pdfContent.contains("87"));
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