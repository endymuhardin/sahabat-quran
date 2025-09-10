package com.sahabatquran.webapp.functional.validation.operation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

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
    @DisplayName("LS-AP-001: Generate Report dengan Data Nilai Tidak Lengkap")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleIncompleteGradeData() {
        log.info("🚀 Starting LS-AP-001: Generate Report dengan Data Nilai Tidak Lengkap...");
        
        final String STUDENT_NAME = "Ahmad Fauzan";
        final String LEVEL = "Tahsin 2";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        
        // Bagian 1: Attempt Report Generation dengan Missing Data
        log.info("📝 Bagian 1: Attempt Report Generation dengan Missing Data");
        
        reportPage.navigateToStudentReports();
        assertTrue(reportPage.isStudentListDisplayed(), "Student list should be displayed");
        
        // Search student with incomplete data
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        assertTrue(reportPage.isIncompleteDataIndicatorVisible(), "Incomplete data indicator should be visible");
        
        // Select term and attempt generation
        reportPage.selectTerm(ACADEMIC_TERM);
        reportPage.selectReportType("INDIVIDUAL_REPORT_CARD");
        
        // Attempt to generate report
        reportPage.attemptReportGeneration();
        
        // Bagian 2: Verify Missing Data Warnings
        log.info("📝 Bagian 2: Verify Missing Data Warnings");
        
        assertTrue(reportPage.isMissingDataWarningVisible(), "Missing data warning should be displayed");
        assertTrue(reportPage.isMissingGradesWarningVisible(), "Missing grades warning should be specific");
        assertTrue(reportPage.isPartialDataNoticeVisible(), "Partial data notice should be shown");
        
        // Check specific missing components
        assertTrue(page.locator("#missing-midterm-warning").isVisible(), "Missing midterm assessment warning should be visible");
        assertTrue(page.locator("#missing-final-warning").isVisible(), "Missing final assessment warning should be visible");
        assertTrue(page.locator("#missing-teacher-eval-warning").isVisible(), "Missing teacher evaluation warning should be visible");
        
        // Bagian 3: Generate Partial Report Option
        log.info("📝 Bagian 3: Generate Partial Report Option");
        
        // Check if partial generation option is available
        assertTrue(page.locator("#generate-partial-report").isVisible(), "Generate partial report option should be available");
        assertTrue(page.locator("#include-disclaimers").isVisible(), "Include disclaimers option should be available");
        
        // Generate partial report with disclaimers
        page.locator("#include-disclaimers").check();
        page.locator("#generate-partial-report").click();
        
        // Verify partial report generation
        assertTrue(reportPage.isReportGenerationSuccessful(), "Partial report generation should be successful");
        assertTrue(page.locator("#partial-report-disclaimer").isVisible(), "Partial report disclaimer should be visible");
        assertTrue(page.locator("#missing-data-notice").isVisible(), "Missing data notice should be in the report");
        
        log.info("✅ LS-AP-001: Incomplete Grade Data handling completed successfully!");
    }
    
    @Test
    @DisplayName("LS-AP-002: Validation Error - Student Tidak Terdaftar di Term")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleStudentNotEnrolledInTerm() {
        log.info("🚀 Starting LS-AP-002: Student Tidak Terdaftar di Term...");
        
        final String STUDENT_NAME = "Maria Santos";
        final String INVALID_TERM = "Semester 2 2023/2024"; // Student not enrolled in this term
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Bagian 1: Select Student and Invalid Term
        log.info("📝 Bagian 1: Select Student and Invalid Term");
        
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        
        // Select term where student is not enrolled
        reportPage.selectTerm(INVALID_TERM);
        reportPage.selectReportType("INDIVIDUAL_REPORT_CARD");
        
        // Attempt report generation
        reportPage.attemptReportGeneration();
        
        // Bagian 2: Verify Enrollment Validation
        log.info("📝 Bagian 2: Verify Enrollment Validation");
        
        assertTrue(reportPage.isValidationErrorVisible(), "Validation error should be displayed");
        assertTrue(page.locator("#enrollment-error").isVisible(), "Enrollment error should be specific");
        assertTrue(page.locator("text='not enrolled'").isVisible(), "Not enrolled message should be shown");
        
        // Check available terms for the student
        assertTrue(page.locator("#available-terms").isVisible(), "Available terms should be shown");
        assertTrue(page.locator("#suggest-correct-term").isVisible(), "Correct term suggestion should be available");
        
        // Bagian 3: Handle Correction
        log.info("📝 Bagian 3: Handle Correction");
        
        // Click on suggested correct term
        page.locator("#suggest-correct-term").click();
        
        // Verify automatic correction
        assertTrue(page.locator("#term-corrected-notice").isVisible(), "Term corrected notice should be visible");
        
        // Attempt generation again with correct term
        reportPage.generateReport();
        assertTrue(reportPage.isReportGenerationSuccessful(), "Report should generate successfully with correct term");
        
        log.info("✅ LS-AP-002: Student Term Enrollment validation completed successfully!");
    }
    
    @Test
    @DisplayName("LS-AP-003: Bulk Generation - Mixed Data Quality")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleMixedDataQualityInBulkGeneration() {
        log.info("🚀 Starting LS-AP-003: Bulk Generation dengan Mixed Data Quality...");
        
        final String CLASS_NAME = "Mixed Data Class";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Bagian 1: Setup Bulk Generation with Mixed Data
        log.info("📝 Bagian 1: Setup Bulk Generation with Mixed Data");
        
        reportPage.filterByClass(CLASS_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);
        reportPage.selectAllStudentsInClass();
        
        // Attempt bulk generation
        reportPage.generateBulkReports();
        
        // Bagian 2: Handle Data Quality Issues
        log.info("📝 Bagian 2: Handle Data Quality Issues");
        
        assertTrue(reportPage.isDataQualityWarningVisible(), "Data quality warning should be displayed");
        assertTrue(page.locator("#mixed-data-quality-report").isVisible(), "Mixed data quality report should be shown");
        
        // Check categorization of students
        assertTrue(page.locator("#complete-data-students").isVisible(), "Complete data students should be listed");
        assertTrue(page.locator("#incomplete-data-students").isVisible(), "Incomplete data students should be listed");
        assertTrue(page.locator("#missing-data-students").isVisible(), "Missing data students should be listed");
        
        // Verify processing options
        assertTrue(page.locator("#process-complete-only").isVisible(), "Process complete only option should be available");
        assertTrue(page.locator("#process-all-with-disclaimers").isVisible(), "Process all with disclaimers option should be available");
        assertTrue(page.locator("#skip-problematic").isVisible(), "Skip problematic option should be available");
        
        // Bagian 3: Choose Processing Strategy
        log.info("📝 Bagian 3: Choose Processing Strategy");
        
        // Select to process all with disclaimers
        page.locator("#process-all-with-disclaimers").check();
        page.locator("#continue-bulk-generation").click();
        
        // Verify bulk processing with mixed data
        assertTrue(reportPage.isBulkGenerationProgressVisible(), "Bulk generation progress should be visible");
        assertTrue(page.locator("#processing-status").isVisible(), "Processing status should be shown");
        
        // Wait for completion
        page.waitForSelector("#bulk-completed");
        assertTrue(reportPage.isBulkGenerationCompleted(), "Bulk generation should complete");
        
        // Verify results summary
        assertTrue(page.locator("#generation-summary").isVisible(), "Generation summary should be available");
        assertTrue(page.locator("#successful-reports-count").isVisible(), "Successful reports count should be shown");
        assertTrue(page.locator("#partial-reports-count").isVisible(), "Partial reports count should be shown");
        
        log.info("✅ LS-AP-003: Mixed Data Quality Bulk Generation completed successfully!");
    }
    
    @Test
    @DisplayName("LS-AP-004: Report Access Control - Unauthorized User")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldEnforceReportAccessControl() {
        log.info("🚀 Starting LS-AP-004: Report Access Control validation...");
        
        // Try to access as instructor (should have limited access)
        loginAsInstructor();
        
        // Bagian 1: Test Access Restrictions
        log.info("📝 Bagian 1: Test Access Restrictions");
        
        // Attempt to navigate to student reports
        page.locator("#reports-menu").click();
        
        // Should not have access to all student reports
        assertFalse(page.locator("#all-student-reports").isVisible(), "All student reports should not be accessible");
        
        // Should only see limited options
        assertTrue(page.locator("#my-class-reports-only").isVisible(), "Should only see own class reports");
        
        // Bagian 2: Test Data Filtering by Role
        log.info("📝 Bagian 2: Test Data Filtering by Role");
        
        // Try to access student report that instructor doesn't teach
        page.navigate(page.url() + "/../reports/student/999"); // Unauthorized student ID
        
        // Should be blocked or redirected
        assertTrue(page.locator("#access-denied").isVisible() || 
                  page.url().contains("error") || 
                  page.url().contains("unauthorized"), 
                  "Access should be denied or redirected");
        
        log.info("✅ LS-AP-004: Report Access Control validation completed successfully!");
    }
    
    @Test
    @DisplayName("LS-AP-005: Email Delivery Failure Handling")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleEmailDeliveryFailure() {
        log.info("🚀 Starting LS-AP-005: Email Delivery Failure Handling...");
        
        final String STUDENT_NAME = "Invalid Email Student";
        final String ACADEMIC_TERM = "Semester 1 2024/2025";
        
        StudentReportPage reportPage = new StudentReportPage(page);
        
        loginAsAdmin();
        reportPage.navigateToStudentReports();
        
        // Generate report first
        reportPage.searchStudent(STUDENT_NAME);
        reportPage.selectStudent(STUDENT_NAME);
        reportPage.selectTerm(ACADEMIC_TERM);
        reportPage.generateReport();
        
        // Bagian 1: Attempt Email with Invalid Address
        log.info("📝 Bagian 1: Attempt Email with Invalid Address");
        
        reportPage.emailReport();
        
        // Configure email to invalid address
        page.locator("#email-recipient").fill("invalid-email-address");
        page.locator("#email-subject").fill("Test Report");
        page.locator("#btn-send-email").click();
        
        // Bagian 2: Handle Email Validation
        log.info("📝 Bagian 2: Handle Email Validation");
        
        assertTrue(page.locator("#email-validation-error").isVisible(), "Email validation error should be shown");
        assertTrue(page.locator("#invalid-email-format").isVisible(), "Invalid email format message should be displayed");
        
        // Correct the email and try again
        page.locator("#email-recipient").fill("valid@email.com");
        page.locator("#btn-send-email").click();
        
        // Simulate delivery failure (this would be handled by the backend)
        // For testing purposes, we check if the system handles failure gracefully
        if (page.locator("#email-delivery-failed").isVisible()) {
            assertTrue(page.locator("#retry-email-option").isVisible(), "Retry email option should be available");
            assertTrue(page.locator("#save-report-instead").isVisible(), "Save report instead option should be available");
            
            // Choose to save report instead
            page.locator("#save-report-instead").click();
            assertTrue(reportPage.isDownloadSuccessMessageVisible(), "Report should be saved successfully");
        } else {
            // Email was successful
            assertTrue(reportPage.isEmailSentConfirmation(), "Email should be sent successfully");
        }
        
        log.info("✅ LS-AP-005: Email Delivery Failure Handling completed successfully!");
    }
}
