package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Playwright Page Object for Student Report and Transcript functionality.
 * 
 * Handles student report card generation, transcript creation, and academic record management.
 */
public class StudentReportPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator academicMenuButton;
    private final Locator studentReportsSection;
    private final Locator transcriptSection;
    
    // Report generation locators
    private final Locator studentSearchField;
    private final Locator studentSelector;
    private final Locator termSelector;
    private final Locator reportTypeSelector;
    private final Locator generateReportButton;
    
    // Report content locators
    private final Locator reportPreview;
    private final Locator studentInfo;
    private final Locator gradeComponents;
    private final Locator attendanceData;
    private final Locator teacherFeedback;
    private final Locator reportActions;
    
    // Validation and error locators
    private final Locator validationErrors;
    private final Locator missingDataWarning;
    private final Locator incompleteDataIndicator;
    private final Locator dataCompletionStatus;
    
    // Export and download locators
    private final Locator downloadPdfButton;
    private final Locator downloadExcelButton;
    private final Locator printButton;
    private final Locator emailReportButton;
    
    public StudentReportPage(Page page) {
        this.page = page;
        
        // Initialize navigation locators
        this.academicMenuButton = page.locator("#academic-menu-button");
        this.studentReportsSection = page.locator("#student-reports");
        this.transcriptSection = page.locator("#transcript-section");
        
        // Report generation
        this.studentSearchField = page.locator("#student-search");
        this.studentSelector = page.locator("#student-selector");
        this.termSelector = page.locator("#term-selector");
        this.reportTypeSelector = page.locator("#report-type");
        this.generateReportButton = page.locator("#btn-generate-report");
        
        // Report content
        this.reportPreview = page.locator("#report-preview");
        this.studentInfo = page.locator("#student-info");
        this.gradeComponents = page.locator("#grade-components");
        this.attendanceData = page.locator("#attendance-data");
        this.teacherFeedback = page.locator("#teacher-feedback");
        this.reportActions = page.locator("#report-actions");
        
        // Validation and errors
        this.validationErrors = page.locator("#validation-errors");
        this.missingDataWarning = page.locator("#missing-data-warning");
        this.incompleteDataIndicator = page.locator("#incomplete-data");
        this.dataCompletionStatus = page.locator("#data-completion-status");
        
        // Export and download
        this.downloadPdfButton = page.locator("#btn-download-pdf");
        this.downloadExcelButton = page.locator("#btn-download-excel");
        this.printButton = page.locator("#btn-print");
        this.emailReportButton = page.locator("#btn-email-report");
    }
    
    // ====================== NAVIGATION METHODS ======================
    
    public void navigateToStudentReports() {
        // Navigate through the Academic menu to Rapor & Sertifikat
        page.locator("#academic-menu-button").click();
        page.waitForTimeout(500); // Wait for dropdown to appear
        page.locator("a[href='/report-cards']").click();
        page.waitForSelector("#student-reports");
    }
    
    public void navigateToTranscripts() {
        // Navigate through the Akademik menu to transcripts
        academicMenuButton.click();
        page.waitForTimeout(500); // Wait for dropdown to appear
        page.locator("a[href='/report-cards/transcripts']").click();
        page.waitForSelector("#transcript-section");
    }
    
    // ====================== REPORT GENERATION METHODS ======================
    
    public void searchStudent(String studentName) {
        studentSearchField.fill(studentName);
        page.waitForTimeout(500); // Allow for search suggestions
    }
    
    public void selectStudent(String studentName) {
        page.locator(String.format(".student-option:has-text('%s')", studentName)).click();
    }
    
    public void selectTerm(String termName) {
        termSelector.selectOption(termName);
    }
    
    public void selectReportType(String reportType) {
        reportTypeSelector.selectOption(reportType);
    }
    
    public void generateReport() {
        generateReportButton.click();
        page.waitForSelector("#report-preview");
    }
    
    public void attemptReportGeneration() {
        generateReportButton.click();
        page.waitForTimeout(1000); // Wait for validation/processing
    }
    
    // ====================== VALIDATION AND ERROR CHECKING ======================
    
    public boolean isStudentListDisplayed() {
        return page.locator("#student-list").isVisible();
    }
    
    public boolean isDataCompletionIndicatorVisible() {
        return dataCompletionStatus.isVisible();
    }
    
    public boolean isMissingDataWarningVisible() {
        return missingDataWarning.isVisible();
    }
    
    public boolean isIncompleteDataIndicatorVisible() {
        return incompleteDataIndicator.isVisible();
    }
    
    public boolean isValidationErrorVisible() {
        return validationErrors.isVisible();
    }
    
    public boolean isMissingGradesWarningVisible() {
        return page.locator("#missing-grades-warning").isVisible();
    }
    
    public boolean isPartialDataNoticeVisible() {
        return page.locator("#partial-data-notice").isVisible();
    }
    
    public boolean isDataQualityWarningVisible() {
        return page.locator("#data-quality-warning").isVisible();
    }
    
    // ====================== REPORT CONTENT VERIFICATION ======================
    
    public boolean isReportPreviewVisible() {
        return reportPreview.isVisible();
    }
    
    public boolean isStudentInfoDisplayed() {
        return studentInfo.isVisible();
    }
    
    public boolean isStudentNameCorrect(String expectedName) {
        return page.locator("#student-name").textContent().contains(expectedName);
    }
    
    public boolean isLevelDisplayed(String expectedLevel) {
        return page.locator("#student-level").textContent().contains(expectedLevel);
    }
    
    public boolean isTermDisplayed(String expectedTerm) {
        return page.locator("#academic-term").textContent().contains(expectedTerm);
    }
    
    public boolean isFinalGradeDisplayed() {
        return page.locator("#final-grade").isVisible();
    }
    
    public boolean isAttendanceRateDisplayed() {
        return page.locator("#attendance-rate").isVisible();
    }
    
    public boolean isTeacherFeedbackDisplayed() {
        return teacherFeedback.isVisible();
    }
    
    public boolean isGradeComponentsVisible() {
        return gradeComponents.isVisible();
    }
    
    public boolean isPlacementTestScoreVisible() {
        return page.locator("#placement-test-score").isVisible();
    }
    
    public boolean isMidtermAssessmentVisible() {
        return page.locator("#midterm-assessment").isVisible();
    }
    
    public boolean isFinalAssessmentVisible() {
        return page.locator("#final-assessment").isVisible();
    }
    
    public boolean isTeacherEvaluationVisible() {
        return page.locator("#teacher-evaluation").isVisible();
    }
    
    public boolean isAttendanceScoreVisible() {
        return page.locator("#attendance-score").isVisible();
    }
    
    // ====================== REPORT ACTIONS ======================
    
    public boolean isDownloadPdfAvailable() {
        return downloadPdfButton.isVisible();
    }
    
    public boolean isDownloadExcelAvailable() {
        return downloadExcelButton.isVisible();
    }
    
    public boolean isPrintOptionAvailable() {
        return printButton.isVisible();
    }
    
    public boolean isEmailOptionAvailable() {
        return emailReportButton.isVisible();
    }
    
    public void downloadPdf() {
        downloadPdfButton.click();
    }
    
    public void downloadExcel() {
        downloadExcelButton.click();
    }
    
    public void printReport() {
        printButton.click();
    }
    
    public void emailReport() {
        emailReportButton.click();
    }
    
    // ====================== SUCCESS AND COMPLETION CHECKS ======================
    
    public boolean isReportGenerationSuccessful() {
        return page.locator("#report-generated-success").isVisible();
    }
    
    public boolean isDownloadSuccessMessageVisible() {
        return page.locator("#download-success").isVisible();
    }
    
    public boolean isPrintDialogOpened() {
        return page.locator(".print-dialog").isVisible();
    }
    
    public boolean isEmailSentConfirmation() {
        return page.locator("#email-sent-confirmation").isVisible();
    }
    
    // ====================== MULTIPLE REPORTS METHODS ======================
    
    public void selectMultipleStudents() {
        page.locator("#select-multiple-students").check();
    }
    
    public void selectAllStudentsInClass() {
        page.locator("#select-all-class").check();
    }
    
    public boolean isBulkGenerationOptionVisible() {
        return page.locator("#bulk-generation").isVisible();
    }
    
    public void generateBulkReports() {
        page.locator("#btn-generate-bulk").click();
    }
    
    public boolean isBulkGenerationProgressVisible() {
        return page.locator("#bulk-progress").isVisible();
    }
    
    public boolean isBulkGenerationCompleted() {
        return page.locator("#bulk-completed").isVisible();
    }
    
    // ====================== TRANSCRIPT SPECIFIC METHODS ======================
    
    public void selectTranscriptFormat(String format) {
        page.locator("#transcript-format").selectOption(format);
    }
    
    public boolean isMultiTermDataVisible() {
        return page.locator("#multi-term-data").isVisible();
    }
    
    public boolean isAcademicProgressionVisible() {
        return page.locator("#academic-progression").isVisible();
    }
    
    public boolean isGpaCalculationVisible() {
        return page.locator("#gpa-calculation").isVisible();
    }
    
    public boolean isOfficialTranscriptSealVisible() {
        return page.locator("#official-seal").isVisible();
    }
    
    // ====================== FILTER AND SEARCH METHODS ======================
    
    public void filterByClass(String className) {
        page.locator("#class-filter").selectOption(className);
    }
    
    public void filterByLevel(String level) {
        page.locator("#level-filter").selectOption(level);
    }
    
    public void filterByTerm(String term) {
        page.locator("#term-filter").selectOption(term);
    }
    
    public void filterByCompletionStatus(String status) {
        page.locator("#completion-filter").selectOption(status);
    }
    
    public boolean isFilteredResultsVisible() {
        return page.locator("#filtered-results").isVisible();
    }
}
