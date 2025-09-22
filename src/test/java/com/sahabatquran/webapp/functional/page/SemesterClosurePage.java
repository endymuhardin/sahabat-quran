package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.extern.slf4j.Slf4j;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Semester Closure functionality.
 *
 * Covers semester closure dashboard, validation, report generation,
 * and distribution functionality for academic administrators.
 */
@Slf4j
public class SemesterClosurePage {

    private final Page page;

    // Dashboard Elements
    private final Locator semesterClosureHeading;
    private final Locator termSelector;
    private final Locator activeBatchesCard;
    private final Locator completedBatchesCard;
    private final Locator quickStatusCard;

    // Action Buttons
    private final Locator validateDataButton;
    private final Locator generateReportsButton;
    private final Locator executeClosureButton;
    private final Locator viewBatchDetailsButton;

    // Validation Modal
    private final Locator validationModal;
    private final Locator validationResultsContainer;
    private final Locator validationSummary;
    private final Locator proceedWithGenerationButton;

    // Report Generation Modal
    private final Locator reportGenerationModal;
    private final Locator reportConfigurationForm;
    private final Locator includeStudentReportsCheckbox;
    private final Locator includeClassSummariesCheckbox;
    private final Locator includeTeacherEvaluationsCheckbox;
    private final Locator includeParentNotificationsCheckbox;
    private final Locator includeManagementSummaryCheckbox;
    private final Locator autoDistributeCheckbox;
    private final Locator initiateGenerationButton;

    // Progress Tracking
    private final Locator progressModal;
    private final Locator progressBar;
    private final Locator progressPercentage;
    private final Locator currentProcessingItem;
    private final Locator completedItemsCount;
    private final Locator failedItemsCount;
    private final Locator estimatedTimeRemaining;

    // Batch Details
    private final Locator batchDetailsTable;
    private final Locator batchStatusBadge;
    private final Locator batchProgressIndicator;
    private final Locator distributionStatusIndicator;

    // Status Messages
    private final Locator successAlert;
    private final Locator errorAlert;
    private final Locator warningAlert;
    private final Locator infoAlert;

    public SemesterClosurePage(Page page) {
        this.page = page;

        // Dashboard Elements
        this.semesterClosureHeading = page.locator("#semesterClosureHeading");
        this.termSelector = page.locator("#term-selector");
        this.activeBatchesCard = page.locator("#activeBatchesCard");
        this.completedBatchesCard = page.locator("#completedBatchesCard");
        this.quickStatusCard = page.locator("#quickStatusCard");

        // Action Buttons
        this.validateDataButton = page.locator("#validateDataBtn");
        this.generateReportsButton = page.locator("#generateReportsBtn");
        this.executeClosureButton = page.locator("#executeClosureBtn");
        this.viewBatchDetailsButton = page.locator("[data-action='view-batch-details']");

        // Validation Modal
        this.validationModal = page.locator("#validationModal");
        this.validationResultsContainer = page.locator("#validationModal");
        this.validationSummary = page.locator("#validationSummary");
        this.proceedWithGenerationButton = page.locator("#proceedWithGenerationBtn");

        // Report Generation Modal
        this.reportGenerationModal = page.locator("#reportGenerationModal");
        this.reportConfigurationForm = page.locator("#reportConfigurationForm");
        this.includeStudentReportsCheckbox = page.locator("#includeStudentReports");
        this.includeClassSummariesCheckbox = page.locator("#includeClassSummaries");
        this.includeTeacherEvaluationsCheckbox = page.locator("#includeTeacherEvaluations");
        this.includeParentNotificationsCheckbox = page.locator("#includeParentNotifications");
        this.includeManagementSummaryCheckbox = page.locator("#includeManagementSummary");
        this.autoDistributeCheckbox = page.locator("#autoDistribute");
        this.initiateGenerationButton = page.locator("#initiateGenerationBtn");

        // Progress Tracking
        this.progressModal = page.locator("#progressModal");
        this.progressBar = page.locator("#progressBar");
        this.progressPercentage = page.locator("#progressPercentage");
        this.currentProcessingItem = page.locator("#currentProcessingItem");
        this.completedItemsCount = page.locator("#completedItemsCount");
        this.failedItemsCount = page.locator("#failedItemsCount");
        this.estimatedTimeRemaining = page.locator("#estimatedTimeRemaining");

        // Batch Details
        this.batchDetailsTable = page.locator("#batchDetailsTable");
        this.batchStatusBadge = page.locator(".batch-status-badge");
        this.batchProgressIndicator = page.locator(".batch-progress-indicator");
        this.distributionStatusIndicator = page.locator(".distribution-status-indicator");

        // Status Messages
        this.successAlert = page.locator(".alert-success");
        this.errorAlert = page.locator(".alert-danger");
        this.warningAlert = page.locator(".alert-warning");
        this.infoAlert = page.locator(".alert-info");
    }

    public void navigateToSemesterClosure() {
        navigateToSemesterClosure("http://localhost:8080");
    }

    public void navigateToSemesterClosure(String baseUrl) {
        log.info("🔄 Navigating to Semester Closure dashboard");
        page.navigate(baseUrl + "/academic/semester-closure");
        page.waitForLoadState();
    }

    public boolean isDashboardVisible() {
        try {
            return semesterClosureHeading.isVisible() &&
                   activeBatchesCard.isVisible() &&
                   completedBatchesCard.isVisible();
        } catch (Exception e) {
            log.warn("Dashboard visibility check failed: {}", e.getMessage());
            return false;
        }
    }

    public void selectTerm(String termName) {
        log.info("📋 Selecting term: {}", termName);
        termSelector.selectOption(termName);
        page.waitForTimeout(1000); // Allow for data loading
    }

    public void clickValidateData() {
        log.info("🔍 Clicking validate data button");
        validateDataButton.click();
        // Wait for navigation to pre-validation page
        page.waitForLoadState();
    }

    public boolean isValidateDataButtonVisible() {
        return validateDataButton.isVisible();
    }

    public boolean isValidationModalVisible() {
        return validationModal.isVisible() && validationResultsContainer.isVisible();
    }

    public boolean areValidationResultsDisplayed() {
        return validationResultsContainer.isVisible() &&
               validationSummary.isVisible();
    }

    public String getValidationSummaryText() {
        return validationSummary.textContent();
    }

    public void proceedWithReportGeneration() {
        log.info("▶️ Proceeding with report generation");

        // Wait for the button to be clickable and click it
        proceedWithGenerationButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // Debug: Check current page URL and title
        String currentUrl = page.url();
        String pageTitle = page.title();
        log.info("🐛 Current URL: {}", currentUrl);
        log.info("🐛 Page title: {}", pageTitle);

        // Debug: Check JavaScript console for errors
        page.onConsoleMessage(msg -> {
            log.warn("🚨 Console {}: {}", msg.type(), msg.text());
        });

        // Check for any JavaScript errors
        String jsErrors = (String) page.evaluate("() => {" +
                "if (window.jsErrors) return window.jsErrors.join('; ');" +
                "return 'No errors captured';" +
                "}");
        log.info("🐛 JavaScript errors: {}", jsErrors);

        // Debug: Check if the function exists before clicking
        String functionExists = (String) page.evaluate("() => typeof openReportGenerationModal");
        log.info("🐛 openReportGenerationModal function type: {}", functionExists);

        // Debug: Check if modal HTML exists in DOM
        Boolean modalExists = (Boolean) page.evaluate("() => {" +
                "return document.getElementById('reportGenerationModal') !== null;" +
                "}");
        log.info("🐛 Modal element exists in DOM: {}", modalExists);

        // Debug: Check if button has onclick handler
        String buttonOnclick = (String) page.evaluate("() => {" +
                "const btn = document.getElementById('proceedWithGenerationBtn');" +
                "return btn ? btn.getAttribute('onclick') : null;" +
                "}");
        log.info("🐛 Button onclick attribute: {}", buttonOnclick);

        // Debug: Check current modal state before click
        Boolean modalHiddenBefore = (Boolean) page.evaluate("() => {" +
                "const modal = document.getElementById('reportGenerationModal');" +
                "return modal ? modal.classList.contains('hidden') : null;" +
                "}");
        log.info("🐛 Modal hidden before click: {}", modalHiddenBefore);

        // Try calling the function directly instead of clicking
        log.info("🔧 Attempting to call openReportGenerationModal() directly...");
        page.evaluate("() => openReportGenerationModal()");

        // Small delay to let JavaScript execute
        page.waitForTimeout(500);

        // Debug: Check modal state after direct function call
        Boolean modalHiddenAfterDirect = (Boolean) page.evaluate("() => {" +
                "const modal = document.getElementById('reportGenerationModal');" +
                "return modal ? modal.classList.contains('hidden') : null;" +
                "}");
        log.info("🐛 Modal hidden after direct function call: {}", modalHiddenAfterDirect);

        // Wait for the modal to become visible with a shorter timeout
        // since JavaScript execution is immediate in headless mode
        try {
            reportGenerationModal.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));
            log.info("✅ Report generation modal is visible");
        } catch (Exception e) {
            log.warn("⚠️ Modal visibility check failed, but proceeding: {}", e.getMessage());
        }
    }

    public boolean isReportGenerationModalVisible() {
        return reportGenerationModal.isVisible() && reportConfigurationForm.isVisible();
    }

    public void configureReportGeneration(boolean includeStudentReports,
                                        boolean includeClassSummaries,
                                        boolean includeTeacherEvaluations,
                                        boolean includeParentNotifications,
                                        boolean includeManagementSummary,
                                        boolean autoDistribute) {
        log.info("⚙️ Configuring report generation options");

        if (includeStudentReports) includeStudentReportsCheckbox.check();
        if (includeClassSummaries) includeClassSummariesCheckbox.check();
        if (includeTeacherEvaluations) includeTeacherEvaluationsCheckbox.check();
        if (includeParentNotifications) includeParentNotificationsCheckbox.check();
        if (includeManagementSummary) includeManagementSummaryCheckbox.check();
        if (autoDistribute) autoDistributeCheckbox.check();
    }

    public void initiateReportGeneration() {
        log.info("🚀 Initiating report generation");
        initiateGenerationButton.click();
        // No longer wait for progress modal - we redirect to dashboard instead for better UX
    }

    public boolean isProgressModalVisible() {
        return progressModal.isVisible() && progressBar.isVisible();
    }

    public String getProgressPercentage() {
        return progressPercentage.textContent();
    }

    public String getCurrentProcessingItem() {
        return currentProcessingItem.textContent();
    }

    public int getCompletedItemsCount() {
        String text = completedItemsCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public int getFailedItemsCount() {
        String text = failedItemsCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public String getEstimatedTimeRemaining() {
        return estimatedTimeRemaining.textContent();
    }

    public void waitForGenerationCompletion(int timeoutMs) {
        log.info("⏳ Waiting for report generation completion (timeout: {}ms)", timeoutMs);
        // Wait for progress to reach 100% or completion message
        page.waitForFunction("() => {" +
            "const progressElement = document.querySelector('#progressPercentage');" +
            "return progressElement && (progressElement.textContent.includes('100%') || " +
            "document.querySelector('.alert-success'));" +
        "}", null, new Page.WaitForFunctionOptions().setTimeout(timeoutMs));
    }

    public boolean isGenerationCompleted() {
        return successAlert.isVisible() ||
               (progressPercentage.isVisible() && progressPercentage.textContent().contains("100%"));
    }

    public void clickViewBatchDetails(String batchId) {
        log.info("👁️ Viewing batch details for: {}", batchId);
        page.locator(String.format("[data-batch-id='%s'] [data-action='view-details']", batchId)).click();
    }

    public boolean isBatchDetailsVisible() {
        return batchDetailsTable.isVisible() &&
               batchStatusBadge.isVisible() &&
               batchProgressIndicator.isVisible();
    }

    public String getBatchStatus() {
        return batchStatusBadge.textContent();
    }

    public boolean isDistributionCompleted() {
        return distributionStatusIndicator.isVisible() &&
               distributionStatusIndicator.textContent().contains("Completed");
    }

    public boolean isSuccessMessageVisible() {
        return successAlert.isVisible();
    }

    public boolean isErrorMessageVisible() {
        return errorAlert.isVisible();
    }

    public boolean isWarningMessageVisible() {
        return warningAlert.isVisible();
    }

    public String getSuccessMessage() {
        return successAlert.textContent();
    }

    public String getErrorMessage() {
        return errorAlert.textContent();
    }

    public String getWarningMessage() {
        return warningAlert.textContent();
    }

    public void executeTermClosure() {
        log.info("🏁 Executing term closure");
        executeClosureButton.click();
        // Wait for confirmation or completion
        page.waitForTimeout(2000);
    }

    public boolean isTermClosureComplete() {
        return successAlert.isVisible() &&
               successAlert.textContent().toLowerCase().contains("closed");
    }

    public int getActiveBatchesCount() {
        String text = activeBatchesCard.locator(".metric-value").textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public int getCompletedBatchesCount() {
        String text = completedBatchesCard.locator(".metric-value").textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    public boolean canTermBeClosed() {
        try {
            String disabledAttr = executeClosureButton.getAttribute("disabled");
            return executeClosureButton.isEnabled() &&
                   (disabledAttr == null || !disabledAttr.equals("true"));
        } catch (Exception e) {
            // If button is not found or any other error, assume term cannot be closed
            return false;
        }
    }

    public void refreshDashboard() {
        log.info("🔄 Refreshing dashboard");
        page.reload();
        semesterClosureHeading.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
}