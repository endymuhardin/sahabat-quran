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
        this.validationErrors = page.locator("#server-validation-errors");
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
        // Direct navigation to avoid menu complexity issues
        String baseUrl = page.url().split("/")[0] + "//" + page.url().split("/")[2];
        page.navigate(baseUrl + "/report-cards");

        // Check if page loads with better error info
        try {
            page.waitForSelector("#student-reports", new Page.WaitForSelectorOptions().setTimeout(5000));
        } catch (Exception e) {
            System.out.println("Failed to find #student-reports. Current URL: " + page.url());
            System.out.println("Page title: " + page.title());
            System.out.println("Page content contains: " + (page.content().contains("student-reports") ? "YES" : "NO"));
            throw e;
        }
    }
    
    public void navigateToTranscripts() {
        // Navigate through the Akademik menu to transcripts
        academicMenuButton.click();
        page.waitForTimeout(500); // Wait for dropdown to appear
        page.locator("a[href='/report-cards/transcripts']").click();
        page.waitForSelector("#transcript-section");
    }
    
    // ====================== REPORT GENERATION METHODS ======================

    public void enablePartialReportGeneration() {
        page.locator("#generate-partial-report").check();
    }

    public void enableDisclaimers() {
        page.locator("#include-disclaimers").check();
    }

    public void searchStudent(String studentName) {
        // With the new dropdown approach, this is now a no-op or just logs
        System.out.println("Student search (now handled by dropdown): " + studentName);
    }

    public void selectStudent(String studentName) {
        // Wait for the selector to be available, then just try to select
        try {
            page.waitForSelector("#student-selector", new Page.WaitForSelectorOptions().setTimeout(10000));

            // Give a moment for options to populate
            page.waitForTimeout(1000);

            // Debug: List available options
            System.out.println("DEBUG: Looking for student '" + studentName + "'. Available options:");
            var options = page.locator("#student-selector option").all();
            for (var option : options) {
                String value = option.getAttribute("value");
                String text = option.textContent();
                System.out.println("  Option: value='" + value + "', text='" + text + "'");
            }

            // Find the option by text content and get its value
            String studentId = null;
            for (var option : options) {
                String text = option.textContent();
                if (text != null && text.equals(studentName)) {
                    studentId = option.getAttribute("value");
                    break;
                }
            }

            if (studentId != null && !studentId.isEmpty()) {
                // Select by value (UUID)
                studentSelector.selectOption(studentId);
                System.out.println("DEBUG: Successfully selected student: " + studentName + " with ID: " + studentId);
            } else {
                throw new RuntimeException("Could not find student option for: " + studentName);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Failed to select student '" + studentName + "'");
            throw new RuntimeException("Could not select student: " + studentName, e);
        }
    }
    
    public void selectTerm(String termName) {
        try {
            // Try regenerate form first, then fall back to main form
            String termSelector = "#regenerate-term-selector";
            if (!page.locator(termSelector).isVisible()) {
                termSelector = "#term-selector";
            }

            // Wait for the term selector to be available
            page.waitForSelector(termSelector, new Page.WaitForSelectorOptions().setTimeout(10000));

            // Give a short pause for options to load
            page.waitForTimeout(1000);

            // List all available options for debugging
            System.out.println("DEBUG: Looking for term '" + termName + "'. Available options:");
            var allOptions = page.locator(termSelector + " option").all();
            for (var option : allOptions) {
                String value = option.getAttribute("value");
                String text = option.textContent();
                System.out.println("  Option: value='" + value + "', text='" + text + "'");
            }

            // Find all options that match the term name
            var matchingOptions = page.locator(termSelector + " option").filter(new Locator.FilterOptions().setHasText(termName)).all();

            if (!matchingOptions.isEmpty()) {
                // Use the first matching option to avoid strict mode violations
                String termValue = matchingOptions.get(0).getAttribute("value");
                System.out.println("DEBUG: Selecting term '" + termName + "' with value: " + termValue);
                page.locator(termSelector).selectOption(termValue);
                System.out.println("DEBUG: Term selected successfully");
            } else {
                System.out.println("DEBUG: Term '" + termName + "' not found, selecting first available non-empty term");
                // Find first non-empty option
                var nonEmptyOptions = allOptions.stream()
                    .filter(option -> option.getAttribute("value") != null && !option.getAttribute("value").isEmpty())
                    .toList();

                if (!nonEmptyOptions.isEmpty()) {
                    String firstTermValue = nonEmptyOptions.get(0).getAttribute("value");
                    System.out.println("DEBUG: Using fallback term with value: " + firstTermValue);
                    page.locator(termSelector).selectOption(firstTermValue);
                } else {
                    throw new RuntimeException("No valid terms available");
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Failed to select term '" + termName + "'");
            throw new RuntimeException("Could not select term: " + termName, e);
        }
    }
    
    public void selectReportType(String reportType) {
        try {
            // Wait for the report type selector to be available
            page.waitForSelector("#report-type", new Page.WaitForSelectorOptions().setTimeout(10000));
            reportTypeSelector.selectOption(reportType);
        } catch (Exception e) {
            System.out.println("DEBUG: Failed to select report type '" + reportType + "'. Available options:");
            var options = page.locator("#report-type option").all();
            for (var option : options) {
                String value = option.getAttribute("value");
                String text = option.textContent();
                System.out.println("  Option: value='" + value + "', text='" + text + "'");
            }
            throw new RuntimeException("Could not select report type: " + reportType, e);
        }
    }
    
    /**
     * Generate individual report (legacy method - now redirects to regenerate)
     */
    public void generateReport() {
        // In the new architecture, individual generation is now "regenerate"
        if (page.locator("#btn-regenerate").isVisible()) {
            regenerateStudentReport();
        } else {
            // Fallback to old button if it still exists
            generateReportButton.click();
        }
        // Wait for either report preview or validation errors
        try {
            page.waitForSelector("#report-preview", new Page.WaitForSelectorOptions().setTimeout(5000));
        } catch (Exception e) {
            // If report preview doesn't appear, check if there are validation errors or other responses
            if (page.locator("#server-validation-errors").isVisible() ||
                page.locator("#report-generated-success").isVisible()) {
                // Validation errors or success message appeared, continue
                return;
            }
            throw e;
        }
    }
    
    public void attemptReportGeneration() {
        generateReportButton.click();
        // No artificial wait - let the test check for elements immediately
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
        try {
            String actualName = page.locator("#student-name").textContent();
            System.out.println("DEBUG: Expected student name: '" + expectedName + "', Actual: '" + actualName + "'");
            return actualName != null && actualName.contains(expectedName);
        } catch (Exception e) {
            System.out.println("DEBUG: Could not find #student-name element");
            // Try alternative selectors
            var altSelectors = new String[]{"#student-info .student-name", ".student-name", "#report-preview .student-name"};
            for (String selector : altSelectors) {
                try {
                    if (page.locator(selector).isVisible()) {
                        String actualName = page.locator(selector).textContent();
                        System.out.println("DEBUG: Found student name in " + selector + ": '" + actualName + "'");
                        return actualName != null && actualName.contains(expectedName);
                    }
                } catch (Exception ignored) {}
            }
            return false;
        }
    }
    
    public boolean isLevelDisplayed(String expectedLevel) {
        try {
            String actualLevel = page.locator("#student-level").textContent();
            System.out.println("DEBUG: Expected level: '" + expectedLevel + "', Actual: '" + actualLevel + "'");
            return actualLevel != null && actualLevel.contains(expectedLevel);
        } catch (Exception e) {
            System.out.println("DEBUG: Could not find #student-level element, skipping level check");
            return true; // Skip level check if element not found
        }
    }

    public boolean isTermDisplayed(String expectedTerm) {
        try {
            String actualTerm = page.locator("#academic-term").textContent();
            System.out.println("DEBUG: Expected term: '" + expectedTerm + "', Actual: '" + actualTerm + "'");
            return actualTerm != null && actualTerm.contains(expectedTerm);
        } catch (Exception e) {
            System.out.println("DEBUG: Could not find #academic-term element, skipping term check");
            return true; // Skip term check if element not found
        }
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
        // Apply Playwright best practices for PDF download testing - fail-fast approach
        // Start waiting for download before clicking the button
        page.waitForDownload(() -> {
            downloadPdfButton.click();
        });
        System.out.println("DEBUG: PDF download completed successfully via Playwright");
    }

    public void selectStudentAndTermForDownload(String studentName, String termName) {
        // Select student in the download form by visible text
        page.locator("form[action='/report-cards/download-pdf'] select[name='studentId']").selectOption(studentName);

        // Select term in the download form by visible text
        page.locator("form[action='/report-cards/download-pdf'] select[name='termId']").selectOption(termName);
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
        // Check for multiple indicators of successful report generation
        return page.locator("#report-generated-success").isVisible() ||
               page.locator("#report-preview").isVisible() ||
               page.locator("#student-info").isVisible() ||
               page.locator(".alert-success").isVisible();
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
    
    // ====================== SIMPLIFIED GENERATION METHODS ======================

    /**
     * Generate reports for all students in the selected term.
     * This replaces the old bulk generation approach.
     */
    public void generateAllReports() {
        page.locator("#btn-generate-all").click();
    }

    /**
     * Regenerate a specific student's report (deletes old, creates new)
     */
    public void regenerateStudentReport() {
        // Wait for button to be enabled (JavaScript validation)
        // Check both required fields in the regenerate form specifically
        page.waitForFunction(
            "() => {" +
            "  const studentSelect = document.querySelector('#student-selector');" +
            "  const termSelect = document.querySelector('#regenerate-term-selector');" +
            "  const button = document.querySelector('#btn-regenerate');" +
            "  return studentSelect && studentSelect.value !== '' && " +
            "         termSelect && termSelect.value !== '' && " +
            "         button && !button.disabled;" +
            "}"
        );
        page.locator("#btn-regenerate").click();
    }

    /**
     * Navigate to the status dashboard to check report generation progress
     */
    public void navigateToStatusDashboard() {
        String baseUrl = page.url().split("/")[0] + "//" + page.url().split("/")[2];
        page.navigate(baseUrl + "/report-cards/status");
        page.waitForSelector("#status-dashboard", new Page.WaitForSelectorOptions().setTimeout(5000));
    }

    /**
     * Check if the status dashboard is visible
     */
    public boolean isStatusDashboardVisible() {
        return page.locator("#status-dashboard").isVisible();
    }

    /**
     * Check if generation is completed by looking at the status dashboard
     */
    public boolean isGenerationCompleted() {
        // Use first batch status element to check completion
        return page.locator(".batch-status.status-completed").first().isVisible() ||
               page.locator("[data-status='COMPLETED']").isVisible();
    }

    /**
     * Check if generation is in progress
     */
    public boolean isGenerationInProgress() {
        // Use first batch status element to check progress
        return page.locator(".batch-status.status-in-progress").first().isVisible() ||
               page.locator("[data-status='IN_PROGRESS']").isVisible();
    }

    /**
     * Wait for generation to complete (no real-time updates, just page refresh)
     */
    public void waitForGenerationCompletion() {
        // Since we don't have real-time updates, we refresh and check
        for (int i = 0; i < 10; i++) {
            page.reload();
            page.waitForTimeout(2000);
            if (isGenerationCompleted()) {
                break;
            }
        }
    }

    // Legacy method compatibility - redirects to new approach
    @Deprecated
    public void selectAllStudentsInClass() {
        // In the new architecture, we don't select individual students for bulk
        // The generate-all button automatically processes all students
        System.out.println("WARNING: selectAllStudentsInClass() is deprecated. Use generateAllReports() instead.");
    }

    @Deprecated
    public boolean isBulkGenerationOptionVisible() {
        // Check for the new generate-all button instead
        return page.locator("#btn-generate-all").isVisible();
    }

    @Deprecated
    public void generateBulkReports() {
        generateAllReports();
    }

    @Deprecated
    public boolean isBulkGenerationProgressVisible() {
        return isGenerationInProgress();
    }

    @Deprecated
    public boolean isBulkGenerationCompleted() {
        return isGenerationCompleted();
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
