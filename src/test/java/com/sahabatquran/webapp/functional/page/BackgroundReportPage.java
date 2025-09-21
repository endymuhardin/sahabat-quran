package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Playwright Page Object for Background Report Generation functionality.
 *
 * Handles background report processing, monitoring, and status management for both
 * individual and bulk report generation operations.
 */
public class BackgroundReportPage {

    private final Page page;

    // Navigation locators
    private final Locator backgroundReportButton;
    private final Locator backToMainReportsButton;

    // Dashboard cards
    private final Locator individualReportCard;
    private final Locator bulkReportCard;
    private final Locator processingStatusCard;

    // Individual report form locators
    private final Locator startIndividualButton;
    private final Locator individualStudentSelector;
    private final Locator individualTermSelector;
    private final Locator individualReportTypeSelector;
    private final Locator startIndividualGenerationButton;

    // Bulk report form locators
    private final Locator startBulkButton;
    private final Locator bulkTermSelector;
    private final Locator bulkClassFilter;
    private final Locator bulkLevelFilter;
    private final Locator includeStudentReportsCheckbox;
    private final Locator includeClassSummariesCheckbox;
    private final Locator startBulkGenerationButton;

    // Status monitoring locators
    private final Locator viewStatusButton;
    private final Locator batchStatusDisplay;
    private final Locator progressBar;
    private final Locator progressPercentage;
    private final Locator completedItemsCount;
    private final Locator pendingItemsCount;
    private final Locator failedItemsCount;
    private final Locator estimatedCompletionTime;
    private final Locator actualDuration;

    // Real-time polling indicators
    private final Locator statusLastUpdated;
    private final Locator pollingIndicator;
    private final Locator connectionStatus;

    // Processing control locators
    private final Locator cancelGenerationButton;
    private final Locator cancelConfirmButton;
    private final Locator retryButton;

    // Results and completion locators
    private final Locator completionMessage;
    private final Locator downloadLinksSection;
    private final Locator resultsummary;
    private final Locator generatedFilesCount;
    private final Locator totalFileSize;

    // Error and notification locators
    private final Locator errorMessage;
    private final Locator successNotification;
    private final Locator warningMessage;
    private final Locator validationErrors;

    public BackgroundReportPage(Page page) {
        this.page = page;

        // Initialize navigation locators
        this.backgroundReportButton = page.locator("a[href='/report-cards/background']");
        this.backToMainReportsButton = page.locator("a[href='/report-cards']");

        // Initialize dashboard cards - match the actual HTML structure more specifically
        this.individualReportCard = page.locator("div.bg-white.overflow-hidden.shadow.rounded-lg").filter(new Locator.FilterOptions().setHas(page.locator("#btn-individual-report")));
        this.bulkReportCard = page.locator("div.bg-white.overflow-hidden.shadow.rounded-lg").filter(new Locator.FilterOptions().setHas(page.locator("#btn-bulk-reports")));
        this.processingStatusCard = page.locator("div.bg-white.overflow-hidden.shadow.rounded-lg").filter(new Locator.FilterOptions().setHas(page.locator("#btn-view-status")));

        // Initialize individual report form - match actual IDs from template
        this.startIndividualButton = page.locator("#btn-individual-report");
        this.individualStudentSelector = page.locator("#individual-student");
        this.individualTermSelector = page.locator("#individual-term");
        this.individualReportTypeSelector = page.locator("#individual-type");
        this.startIndividualGenerationButton = page.locator("#form-individual-report button[type='submit']");

        // Initialize bulk report form - match actual IDs from template
        this.startBulkButton = page.locator("#btn-bulk-reports");
        this.bulkTermSelector = page.locator("#bulk-term");
        this.bulkClassFilter = page.locator("#bulk-class");
        this.bulkLevelFilter = page.locator("#bulk-level");
        this.includeStudentReportsCheckbox = page.locator("input[name='includeStudentReports']");
        this.includeClassSummariesCheckbox = page.locator("input[name='includeClassSummaries']");
        this.startBulkGenerationButton = page.locator("#form-bulk-reports button[type='submit']");

        // Initialize status monitoring
        this.viewStatusButton = page.locator("#btn-view-status");
        this.batchStatusDisplay = page.locator("#status-content");
        this.progressBar = page.locator("#progress-bar");
        this.progressPercentage = page.locator("#progress-percentage");
        this.completedItemsCount = page.locator("#completed-items");
        this.pendingItemsCount = page.locator("#pending-items");
        this.failedItemsCount = page.locator("#failed-items");
        this.estimatedCompletionTime = page.locator("#estimated-completion");
        this.actualDuration = page.locator("#actual-duration");

        // Initialize real-time indicators
        this.statusLastUpdated = page.locator("#status-last-updated");
        this.pollingIndicator = page.locator("#polling-indicator");
        this.connectionStatus = page.locator("#connection-status");

        // Initialize processing controls
        this.cancelGenerationButton = page.locator("#cancel-generation");
        this.cancelConfirmButton = page.locator("#confirm-cancellation");
        this.retryButton = page.locator("#retry-generation");

        // Initialize results
        this.completionMessage = page.locator("#completion-message");
        this.downloadLinksSection = page.locator("#download-links");
        this.resultsummary = page.locator("#result-summary");
        this.generatedFilesCount = page.locator("#generated-files-count");
        this.totalFileSize = page.locator("#total-file-size");

        // Initialize notifications
        this.errorMessage = page.locator(".error-message, .alert-danger");
        this.successNotification = page.locator(".success-message, .alert-success");
        this.warningMessage = page.locator(".warning-message, .alert-warning");
        this.validationErrors = page.locator(".validation-error");
    }

    // ====================== NAVIGATION METHODS ======================

    public void navigateToBackgroundReports() {
        backgroundReportButton.click();
        page.waitForURL("**/report-cards/background");
    }

    public void returnToMainReports() {
        backToMainReportsButton.click();
        page.waitForURL("**/report-cards");
    }

    // ====================== DASHBOARD VERIFICATION METHODS ======================

    public boolean isDashboardVisible() {
        return individualReportCard.isVisible() &&
               bulkReportCard.isVisible() &&
               processingStatusCard.isVisible();
    }

    public boolean isIndividualReportCardAvailable() {
        return individualReportCard.isVisible() &&
               startIndividualButton.isVisible();
    }

    public boolean isBulkReportCardAvailable() {
        return bulkReportCard.isVisible() &&
               startBulkButton.isVisible();
    }

    public boolean isProcessingStatusCardAvailable() {
        return processingStatusCard.isVisible() &&
               viewStatusButton.isVisible();
    }

    // ====================== INDIVIDUAL REPORT METHODS ======================

    public void startIndividualReportGeneration() {
        startIndividualButton.click();
        // Wait for the form to become visible
        page.waitForSelector("#individual-report-form:not(.hidden)", new Page.WaitForSelectorOptions().setTimeout(10000));
    }

    public void selectIndividualStudent(String studentName) {
        // Wait for selector to be populated
        page.waitForTimeout(1000);

        // Find student by text and get UUID value
        var options = individualStudentSelector.locator("option").all();
        for (var option : options) {
            if (studentName.equals(option.textContent())) {
                String studentId = option.getAttribute("value");
                individualStudentSelector.selectOption(studentId);
                return;
            }
        }
        throw new RuntimeException("Student not found: " + studentName);
    }

    public void selectIndividualTerm(String termName) {
        // Find term by text and select by value
        var options = individualTermSelector.locator("option").all();
        for (var option : options) {
            if (option.textContent().contains(termName)) {
                String termId = option.getAttribute("value");
                individualTermSelector.selectOption(termId);
                return;
            }
        }
        throw new RuntimeException("Term not found: " + termName);
    }

    public void selectIndividualReportType(String reportType) {
        individualReportTypeSelector.selectOption(reportType);
    }

    public void submitIndividualReportGeneration() {
        startIndividualGenerationButton.click();
    }

    // ====================== BULK REPORT METHODS ======================

    public void startBulkReportGeneration() {
        startBulkButton.click();
        page.waitForSelector("#bulk-report-form:not(.hidden)", new Page.WaitForSelectorOptions().setTimeout(10000));
    }

    public void selectBulkTerm(String termName) {
        var options = bulkTermSelector.locator("option").all();
        for (var option : options) {
            if (option.textContent().contains(termName)) {
                String termId = option.getAttribute("value");
                bulkTermSelector.selectOption(termId);
                return;
            }
        }
        throw new RuntimeException("Term not found: " + termName);
    }

    public void selectBulkClassFilter(String className) {
        if (className != null && !className.isEmpty()) {
            var options = bulkClassFilter.locator("option").all();
            for (var option : options) {
                if (option.textContent().contains(className)) {
                    String classId = option.getAttribute("value");
                    bulkClassFilter.selectOption(classId);
                    return;
                }
            }
        }
    }

    public void configureBulkReportOptions(boolean includeStudentReports, boolean includeClassSummaries) {
        if (includeStudentReports) {
            includeStudentReportsCheckbox.check();
        }
        if (includeClassSummaries) {
            includeClassSummariesCheckbox.check();
        }
    }

    public void submitBulkReportGeneration() {
        startBulkGenerationButton.click();
    }

    // ====================== STATUS MONITORING METHODS ======================

    public void accessStatusMonitor() {
        viewStatusButton.click();
        page.waitForSelector("#status-monitor:not(.hidden)", new Page.WaitForSelectorOptions().setTimeout(10000));
    }

    public String getBatchStatus() {
        // Wait for status content to be populated with a specific element
        page.waitForSelector("#batch-status", new Page.WaitForSelectorOptions().setTimeout(30000));

        // Get the status from the element with ID batch-status
        var statusElement = page.locator("#batch-status");
        if (statusElement.isVisible()) {
            return statusElement.textContent().trim();
        }

        // Fallback to checking the content
        String content = batchStatusDisplay.textContent();

        // Extract status from content if it contains status information
        if (content.contains("COMPLETED")) return "COMPLETED";
        if (content.contains("IN_PROGRESS")) return "IN_PROGRESS";
        if (content.contains("VALIDATING")) return "VALIDATING";
        if (content.contains("INITIATED")) return "INITIATED";
        if (content.contains("FAILED")) return "FAILED";
        if (content.contains("CANCELLED")) return "CANCELLED";

        return "UNKNOWN";
    }

    public String getProgressPercentage() {
        return progressPercentage.textContent();
    }

    public boolean isProgressBarVisible() {
        return progressBar.isVisible();
    }

    public int getCompletedItemsCount() {
        String text = completedItemsCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public int getPendingItemsCount() {
        String text = pendingItemsCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public int getFailedItemsCount() {
        String text = failedItemsCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public boolean isPollingActive() {
        return pollingIndicator.isVisible();
    }

    public String getEstimatedCompletionTime() {
        return estimatedCompletionTime.textContent();
    }

    public String getActualDuration() {
        return actualDuration.textContent();
    }

    // ====================== PROCESSING CONTROL METHODS ======================

    public void cancelGeneration() {
        cancelGenerationButton.click();
        page.waitForSelector("#confirm-cancellation", new Page.WaitForSelectorOptions().setTimeout(5000));
    }

    public void confirmCancellation() {
        cancelConfirmButton.click();
    }

    public void retryGeneration() {
        retryButton.click();
    }

    // ====================== RESULTS AND COMPLETION METHODS ======================

    public boolean isGenerationCompleted() {
        return completionMessage.isVisible() &&
               getBatchStatus().equals("COMPLETED");
    }

    public boolean isGenerationSuccessful() {
        return successNotification.isVisible() &&
               completionMessage.textContent().contains("Completed Successfully");
    }

    public boolean areDownloadLinksAvailable() {
        return downloadLinksSection.isVisible();
    }

    public int getGeneratedFilesCount() {
        String text = generatedFilesCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public String getTotalFileSize() {
        return totalFileSize.textContent();
    }

    public String getResultSummary() {
        return resultsummary.textContent();
    }

    // ====================== ERROR AND VALIDATION METHODS ======================

    public boolean hasValidationErrors() {
        return validationErrors.isVisible();
    }

    public boolean hasErrorMessage() {
        return errorMessage.isVisible();
    }

    public boolean hasWarningMessage() {
        return warningMessage.isVisible();
    }

    public String getErrorMessage() {
        return errorMessage.textContent();
    }

    public String getWarningMessage() {
        return warningMessage.textContent();
    }

    public String getValidationErrors() {
        return validationErrors.textContent();
    }

    // ====================== WAIT AND POLLING METHODS ======================

    public void waitForProcessingCompletion(int timeoutSeconds) {
        int maxAttempts = timeoutSeconds / 2; // Poll every 2 seconds
        int attempts = 0;

        while (attempts < maxAttempts) {
            String status = getBatchStatus();
            if ("COMPLETED".equals(status) || "CANCELLED".equals(status) || "FAILED".equals(status)) {
                break;
            }

            page.waitForTimeout(2000); // Wait 2 seconds between polls
            attempts++;
        }

        if (attempts >= maxAttempts) {
            throw new RuntimeException("Processing did not complete within " + timeoutSeconds + " seconds");
        }
    }

    public void waitForProcessingStart(int timeoutSeconds) {
        page.waitForFunction(
            "() => document.querySelector('#batch-status')?.textContent !== 'INITIATED'",
            null,
            new Page.WaitForFunctionOptions().setTimeout(timeoutSeconds * 1000)
        );
    }

    public boolean waitForStatusChange(String fromStatus, int timeoutSeconds) {
        try {
            page.waitForFunction(
                "status => document.querySelector('#batch-status')?.textContent !== status",
                fromStatus,
                new Page.WaitForFunctionOptions().setTimeout(timeoutSeconds * 1000)
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}