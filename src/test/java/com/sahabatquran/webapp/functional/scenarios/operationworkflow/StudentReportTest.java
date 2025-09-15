package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.StudentReportPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Student Report and Transcript Generation Tests.
 * Covers student report card generation and academic transcript creation.
 * 
 * User Role: ACADEMIC_ADMIN, FINANCE_STAFF
 * Focus: Report generation, transcript creation, data validation, export functionality.
 */
@Slf4j
@DisplayName("LS-HP: Student Report Happy Path Scenarios")
class StudentReportTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("LS-HP-001: Generate Laporan Kartu Nilai Siswa Individual")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateIndividualStudentReportCard() {
        log.info("🚀 Starting LS-HP-001: Generate Laporan Kartu Nilai Siswa Individual...");
        
        // Test data sesuai dokumentasi
        final String ADMIN_USERNAME = "academic.admin1";
        final String ADMIN_PASSWORD = "Welcome@YSQ2024";
        final String STUDENT_NAME = "Ali Rahman";
        final String LEVEL = "Tahfidz 2";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        final String FINAL_GRADE = "A- (87.5)";
        final String ATTENDANCE_RATE = "92%";
        final String TEACHER_FEEDBACK = "Menunjukkan kemajuan konsisten dalam hafalan";
        
        LoginPage loginPage = new LoginPage(page);
        StudentReportPage reportPage = new StudentReportPage(page);
        
        // Bagian 1: Akses dan Navigasi Laporan
        log.info("📝 Bagian 1: Akses dan Navigasi Laporan");
        
        loginAsAdmin();
        
        // Verify admin dashboard with reports menu
        page.waitForURL("**/dashboard**");
        assertTrue(page.locator("#reports-menu-button").isVisible(), "Reports menu should be visible");
        
        // Navigate to Student Report Cards
        reportPage.navigateToStudentReports();
        assertTrue(reportPage.isStudentListDisplayed(), "Student list should be displayed");
        assertTrue(reportPage.isDataCompletionIndicatorVisible(), "Data completion indicator should be visible");
        
        // Bagian 2: Pilih Siswa dan Term
        log.info("📝 Bagian 2: Pilih Siswa dan Term");
        
        // Search and select student
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        assertTrue(reportPage.isStudentInfoDisplayed(), "Student info should be displayed");
        
        // Select academic term
        reportPage.selectTerm(ACADEMIC_TERM);
        assertTrue(reportPage.isTermDisplayed(ACADEMIC_TERM), "Selected term should be displayed");
        
        // Select report type
        reportPage.selectReportType("INDIVIDUAL_REPORT_CARD");
        
        // Bagian 3: Generate dan Verify Report
        log.info("📝 Bagian 3: Generate dan Verify Report");
        
        // Generate report
        reportPage.generateReport();
        assertTrue(reportPage.isReportGenerationSuccessful(), "Report generation should be successful");
        assertTrue(reportPage.isReportPreviewVisible(), "Report preview should be visible");
        
        // Verify student information
        assertTrue(reportPage.isStudentNameCorrect(STUDENT_NAME), "Student name should be correct");
        assertTrue(reportPage.isLevelDisplayed(LEVEL), "Student level should be displayed");
        assertTrue(reportPage.isTermDisplayed(ACADEMIC_TERM), "Academic term should be displayed");
        
        // Verify grade components
        assertTrue(reportPage.isGradeComponentsVisible(), "Grade components should be visible");
        assertTrue(reportPage.isPlacementTestScoreVisible(), "Placement test score should be visible");
        assertTrue(reportPage.isMidtermAssessmentVisible(), "Midterm assessment should be visible");
        assertTrue(reportPage.isFinalAssessmentVisible(), "Final assessment should be visible");
        assertTrue(reportPage.isTeacherEvaluationVisible(), "Teacher evaluation should be visible");
        assertTrue(reportPage.isAttendanceScoreVisible(), "Attendance score should be visible");
        
        // Verify final grade and attendance
        assertTrue(reportPage.isFinalGradeDisplayed(), "Final grade should be displayed");
        assertTrue(reportPage.isAttendanceRateDisplayed(), "Attendance rate should be displayed");
        assertTrue(reportPage.isTeacherFeedbackDisplayed(), "Teacher feedback should be displayed");
        
        // Bagian 4: Export dan Download Options
        log.info("📝 Bagian 4: Export dan Download Options");
        
        // Verify export options
        assertTrue(reportPage.isDownloadPdfAvailable(), "PDF download should be available");
        assertTrue(reportPage.isDownloadExcelAvailable(), "Excel download should be available");
        assertTrue(reportPage.isPrintOptionAvailable(), "Print option should be available");
        assertTrue(reportPage.isEmailOptionAvailable(), "Email option should be available");
        
        // Test PDF download
        reportPage.downloadPdf();
        assertTrue(reportPage.isDownloadSuccessMessageVisible(), "PDF download should be successful");
        
        log.info("✅ LS-HP-001: Individual Student Report Card generation completed successfully!");
    }
    
    @Test
    @DisplayName("LS-HP-002: Generate Transkrip Akademik Siswa")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateAcademicTranscript() {
        log.info("🚀 Starting LS-HP-002: Generate Transkrip Akademik Siswa...");
        
        final String STUDENT_NAME = "Fatimah Zahra";
        final String TRANSCRIPT_FORMAT = "OFFICIAL_TRANSCRIPT";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        
        // Bagian 1: Access Transcript Section
        log.info("📝 Bagian 1: Access Transcript Section");
        
        reportPage.navigateToTranscripts();
        assertTrue(page.locator("#transcript-section").isVisible(), "Transcript section should be visible");
        
        // Search and select student
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        
        // Select transcript format
        reportPage.selectTranscriptFormat(TRANSCRIPT_FORMAT);
        
        // Bagian 2: Generate Multi-Term Transcript
        log.info("📝 Bagian 2: Generate Multi-Term Transcript");
        
        reportPage.generateReport();
        assertTrue(reportPage.isReportGenerationSuccessful(), "Transcript generation should be successful");
        
        // Verify transcript content
        assertTrue(reportPage.isMultiTermDataVisible(), "Multi-term data should be visible");
        assertTrue(reportPage.isAcademicProgressionVisible(), "Academic progression should be visible");
        assertTrue(reportPage.isGpaCalculationVisible(), "GPA calculation should be visible");
        assertTrue(reportPage.isOfficialTranscriptSealVisible(), "Official transcript seal should be visible");
        
        // Test export
        reportPage.downloadPdf();
        assertTrue(reportPage.isDownloadSuccessMessageVisible(), "Transcript download should be successful");
        
        log.info("✅ LS-HP-002: Academic Transcript generation completed successfully!");
    }
    
    @Test
    @DisplayName("LS-HP-003: Generate Bulk Reports untuk Seluruh Kelas")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateBulkClassReports() {
        log.info("🚀 Starting LS-HP-003: Generate Bulk Reports untuk Seluruh Kelas...");
        
        final String CLASS_NAME = "Tahsin 1 - Kelas Pagi A";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Bagian 1: Setup Bulk Generation
        log.info("📝 Bagian 1: Setup Bulk Generation");
        
        // Filter by class
        reportPage.filterByClass(CLASS_NAME);
        assertTrue(reportPage.isFilteredResultsVisible(), "Filtered results should be visible");
        
        // Select all students in class
        reportPage.selectAllStudentsInClass();
        assertTrue(reportPage.isBulkGenerationOptionVisible(), "Bulk generation option should be visible");
        
        // Select term
        reportPage.selectTerm(ACADEMIC_TERM);
        
        // Bagian 2: Execute Bulk Generation
        log.info("📝 Bagian 2: Execute Bulk Generation");
        
        reportPage.generateBulkReports();
        assertTrue(reportPage.isBulkGenerationProgressVisible(), "Bulk generation progress should be visible");
        
        // Wait for completion
        page.waitForSelector("#bulk-completed");
        page.waitForTimeout(5000); // Allow time for bulk generation
        assertTrue(reportPage.isBulkGenerationCompleted(), "Bulk generation should be completed");
        
        // Verify download options
        assertTrue(reportPage.isDownloadPdfAvailable(), "Bulk PDF download should be available");
        assertTrue(reportPage.isDownloadExcelAvailable(), "Bulk Excel download should be available");
        
        log.info("✅ LS-HP-003: Bulk Class Reports generation completed successfully!");
    }
    
    @Test
    @DisplayName("LS-HP-004: Email Report Otomatis ke Orang Tua")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldEmailReportToParents() {
        log.info("🚀 Starting LS-HP-004: Email Report Otomatis ke Orang Tua...");
        
        final String STUDENT_NAME = "Ahmad Zaki";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Generate individual report
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);
        reportPage.generateReport();
        
        // Bagian 1: Setup Email Delivery
        log.info("📝 Bagian 1: Setup Email Delivery");
        
        assertTrue(reportPage.isEmailOptionAvailable(), "Email option should be available");
        
        reportPage.emailReport();
        assertTrue(page.locator("#email-setup-modal").isVisible(), "Email setup modal should be visible");
        
        // Configure email settings
        page.locator("#email-subject").fill("Laporan Nilai Semester - " + STUDENT_NAME);
        page.locator("#email-message").fill("Assalamu'alaikum. Terlampir laporan nilai semester untuk putra/putri Anda.");
        page.locator("#include-attachment").check();
        
        // Send email
        page.locator("#btn-send-email").click();

        // Wait a moment for the email to be processed and confirmation to show
        page.waitForTimeout(1000);
        assertTrue(reportPage.isEmailSentConfirmation(), "Email sent confirmation should be visible");
        
        log.info("✅ LS-HP-004: Email Report to Parents completed successfully!");
    }
    
    @Test
    @DisplayName("LS-HP-005: Filter dan Search Advanced Reports")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFilterAndSearchReports() {
        log.info("🚀 Starting LS-HP-005: Filter dan Search Advanced Reports...");
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Bagian 1: Apply Multiple Filters
        log.info("📝 Bagian 1: Apply Multiple Filters");
        
        // Filter by level
        reportPage.filterByLevel("Tahfidz 2");
        assertTrue(reportPage.isFilteredResultsVisible(), "Level filtered results should be visible");
        
        // Filter by completion status
        reportPage.filterByCompletionStatus("COMPLETED");
        assertTrue(reportPage.isFilteredResultsVisible(), "Completion filtered results should be visible");
        
        // Filter by term
        reportPage.filterByTerm("Semester 1 2024/2025");
        assertTrue(reportPage.isFilteredResultsVisible(), "Term filtered results should be visible");
        
        // Bagian 2: Test Search Functionality
        log.info("📝 Bagian 2: Test Search Functionality");
        
        // Search for specific student
        reportPage.searchStudent("Ali");
        assertTrue(reportPage.isFilteredResultsVisible(), "Search filtered results should be visible");
        
        // Generate report for filtered results
        reportPage.selectAllStudentsInClass();
        reportPage.generateBulkReports();
        assertTrue(reportPage.isBulkGenerationCompleted(), "Filtered bulk generation should be completed");
        
        log.info("✅ LS-HP-005: Filter dan Search Advanced Reports completed successfully!");
    }
    
    @Test
    @DisplayName("LS-HP-006: Generate Historical Performance Report")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldGenerateHistoricalPerformanceReport() {
        log.info("🚀 Starting LS-HP-006: Generate Historical Performance Report...");
        
        final String STUDENT_NAME = "Siti Khadijah";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Search student
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        
        // Select historical report type
        reportPage.selectReportType("HISTORICAL_PERFORMANCE");
        
        // Generate report
        reportPage.generateReport();
        assertTrue(reportPage.isReportGenerationSuccessful(), "Historical report generation should be successful");
        
        // Verify historical data
        assertTrue(reportPage.isMultiTermDataVisible(), "Multi-term historical data should be visible");
        assertTrue(reportPage.isAcademicProgressionVisible(), "Academic progression should be visible");
        
        // Test export
        reportPage.downloadPdf();
        assertTrue(reportPage.isDownloadSuccessMessageVisible(), "Historical report download should be successful");
        
        log.info("✅ LS-HP-006: Historical Performance Report completed successfully!");
    }
}
