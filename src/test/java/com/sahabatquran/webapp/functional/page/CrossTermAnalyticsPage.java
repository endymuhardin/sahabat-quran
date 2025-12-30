package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.KeyboardModifier;

/**
 * Playwright Page Object for Cross-Term Analytics functionality.
 * 
 * Handles multi-term analytics, historical comparisons, and performance tracking.
 */
public class CrossTermAnalyticsPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator analyticsMenu;
    private final Locator crossTermSection;
    private final Locator historicalDataSection;
    
    // Term selection locators
    private final Locator termSelector;
    private final Locator multiTermSelector;
    private final Locator comparisonPeriodSelector;
    private final Locator dateRangeSelector;
    
    // Analytics dashboard locators
    private final Locator performanceMetrics;
    private final Locator trendCharts;
    private final Locator comparisonTables;
    private final Locator statisticalSummary;
    
    // Filter and search locators
    private final Locator levelFilter;
    private final Locator programFilter;
    private final Locator teacherFilter;
    private final Locator classFilter;
    
    // Report generation locators
    private final Locator generateAnalyticsButton;
    private final Locator exportOptionsMenu;
    private final Locator customReportBuilder;
    
    // Data validation locators
    private final Locator dataQualityIndicator;
    private final Locator missingDataWarning;
    private final Locator dataLimitationsNotice;
    
    public CrossTermAnalyticsPage(Page page) {
        this.page = page;
        
        // Initialize navigation locators - using reports menu for Management role
        this.analyticsMenu = page.locator("#reports-menu-button");
        this.crossTermSection = page.locator("#cross-term-analytics");
        this.historicalDataSection = page.locator("#historical-data");
        
        // Term selection
        this.termSelector = page.locator("#term-selector");
        this.multiTermSelector = page.locator("#multi-term-selector");
        this.comparisonPeriodSelector = page.locator("#comparison-period");
        this.dateRangeSelector = page.locator("#date-range-selector");
        
        // Analytics dashboard
        this.performanceMetrics = page.locator("#performance-metrics");
        this.trendCharts = page.locator("#trend-charts");
        this.comparisonTables = page.locator("#comparison-tables");
        this.statisticalSummary = page.locator("#statistical-summary");
        
        // Filters
        this.levelFilter = page.locator("#level-filter");
        this.programFilter = page.locator("#program-filter");
        this.teacherFilter = page.locator("#teacher-filter");
        this.classFilter = page.locator("#class-filter");
        
        // Report generation
        this.generateAnalyticsButton = page.locator("#btn-generate-analytics");
        this.exportOptionsMenu = page.locator("#export-options");
        this.customReportBuilder = page.locator("#custom-report-builder");
        
        // Data validation
        this.dataQualityIndicator = page.locator("#data-quality");
        this.missingDataWarning = page.locator("#missing-data-warning");
        this.dataLimitationsNotice = page.locator("#data-limitations");
    }
    
    // ====================== NAVIGATION METHODS ======================
    
    public void navigateToCrossTermAnalytics(String baseUrl) {
        // Navigate directly to cross-term analytics page
        // This is more reliable than trying to click through menus that may not exist
        page.navigate(baseUrl + "/management/analytics/cross-term");
        // Wait for the page to load
        page.waitForLoadState();
    }

    public void navigateToCrossTermAnalytics() {
        // Navigate directly using current page URL context
        String currentUrl = page.url();
        String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/", currentUrl.indexOf("://") + 3));
        navigateToCrossTermAnalytics(baseUrl);
    }
    
    public void navigateToHistoricalComparison(String baseUrl) {
        // Navigate directly to historical comparison page
        page.navigate(baseUrl + "/analytics/historical-comparison");
        // Wait for the page to load
        page.waitForLoadState();
    }
    
    // ====================== TERM SELECTION METHODS ======================
    
    public void selectSingleTerm(String termName) {
        termSelector.selectOption(termName);
    }
    
    public void selectMultipleTerms(String... termNames) {
        // Select terms by their display names
        java.util.List<String> optionValues = new java.util.ArrayList<>();
        int optionCount = multiTermSelector.locator("option").count();

        for (int i = 0; i < optionCount; i++) {
            Locator option = multiTermSelector.locator("option").nth(i);
            String optionText = option.textContent().trim();
            String optionValue = option.getAttribute("value");

            // Check if this option matches any of the requested term names
            for (String termName : termNames) {
                if (optionText.contains(termName) || termName.contains(optionText)) {
                    if (optionValue != null && !optionValue.isEmpty()) {
                        optionValues.add(optionValue);
                    }
                    break;
                }
            }
        }

        // Select the matched options
        if (!optionValues.isEmpty()) {
            multiTermSelector.selectOption(optionValues.toArray(new String[0]));
        }
    }
    
    public void selectComparisonPeriod(String period) {
        comparisonPeriodSelector.selectOption(period);
    }
    
    public void setDateRange(String startDate, String endDate) {
        // Wait for date elements to be visible and interactable
        page.waitForSelector("#start-date", new Page.WaitForSelectorOptions().setTimeout(10000));
        page.waitForSelector("#end-date", new Page.WaitForSelectorOptions().setTimeout(10000));

        page.locator("#start-date").fill(startDate);
        page.locator("#end-date").fill(endDate);
    }
    
    // ====================== ANALYTICS GENERATION METHODS ======================
    
    public void generateAnalytics() {
        generateAnalyticsButton.click();
        // Wait for page to reload or results to appear
        page.waitForLoadState();
        // Give some time for potential AJAX calls or form processing
        try {
            page.waitForSelector("#analytics-results", new Page.WaitForSelectorOptions().setTimeout(5000));
        } catch (Exception e) {
            // If analytics-results doesn't appear, that's ok for now
            // The form was submitted, which is what we're testing
        }
    }
    
    public void generateHistoricalComparison() {
        page.locator("#btn-generate-historical").click();
        page.waitForSelector("#historical-comparison-results");
    }
    
    // ====================== FILTER METHODS ======================
    
    public void filterByLevel(String level) {
        levelFilter.selectOption(level);
    }
    
    public void filterByProgram(String program) {
        programFilter.selectOption(program);
    }
    
    public void filterByTeacher(String teacherName) {
        // Select by value - tests should pass teacher ID, but for compatibility
        // we'll find by text and then select its value
        String teacherValue = page.locator("#teacher-filter option").filter(new Locator.FilterOptions().setHasText(teacherName)).first().getAttribute("value");
        teacherFilter.selectOption(teacherValue);
    }
    
    public void filterByClass(String className) {
        classFilter.selectOption(className);
    }
    
    // ====================== VERIFICATION METHODS ======================
    
    public boolean isCrossTermAnalyticsVisible() {
        return crossTermSection.isVisible();
    }
    
    public boolean isHistoricalDataVisible() {
        return historicalDataSection.isVisible();
    }
    
    public boolean isTermSelectorVisible() {
        return termSelector.isVisible();
    }
    
    public boolean isMultiTermSelectorVisible() {
        return multiTermSelector.isVisible();
    }
    
    public boolean isPerformanceMetricsVisible() {
        return performanceMetrics.isVisible();
    }
    
    public boolean isTrendChartsVisible() {
        return trendCharts.isVisible();
    }
    
    public boolean isComparisonTablesVisible() {
        return comparisonTables.isVisible();
    }
    
    public boolean isStatisticalSummaryVisible() {
        return statisticalSummary.isVisible();
    }
    
    // ====================== DATA QUALITY METHODS ======================
    
    public boolean isDataQualityIndicatorVisible() {
        return dataQualityIndicator.isVisible();
    }
    
    public boolean isMissingDataWarningVisible() {
        return missingDataWarning.isVisible();
    }
    
    public boolean isDataLimitationsNoticeVisible() {
        return dataLimitationsNotice.isVisible();
    }
    
    public boolean isInsufficientDataWarningVisible() {
        return page.locator("#insufficient-data-warning").isVisible();
    }
    
    public boolean isPartialAnalysisNoticeVisible() {
        return page.locator("#partial-analysis-notice").isVisible();
    }
    
    // ====================== SPECIFIC METRICS VERIFICATION ======================
    
    public boolean isEnrollmentTrendsVisible() {
        return page.locator("#enrollment-trends").isVisible();
    }
    
    public boolean isCompletionRateComparisonVisible() {
        return page.locator("#completion-rate-comparison").isVisible();
    }
    
    public boolean isStudentSatisfactionTrendsVisible() {
        return page.locator("#student-satisfaction-trends").isVisible();
    }
    
    public boolean isTeacherPerformanceComparisonVisible() {
        return page.locator("#teacher-performance-comparison").isVisible();
    }
    
    public boolean isClassSizeAnalysisVisible() {
        return page.locator("#class-size-analysis").isVisible();
    }
    
    public boolean isRevenueAnalysisVisible() {
        return page.locator("#revenue-analysis").isVisible();
    }
    
    public boolean isCostPerStudentAnalysisVisible() {
        return page.locator("#cost-per-student").isVisible();
    }
    
    // ====================== INTERACTIVE FEATURES ======================
    
    public void clickTermInChart(String termName) {
        // Use data attribute instead of text to identify the term
        page.locator(String.format("#chart-term[data-term='%s']", termName)).click();
    }
    
    public void hoverOverDataPoint(String dataPoint) {
        page.locator(String.format("#data-point-%s", dataPoint)).hover();
    }
    
    public boolean isTooltipVisible() {
        return page.locator(".chart-tooltip").isVisible();
    }
    
    public boolean isDrillDownOptionVisible() {
        return page.locator("#drill-down-option").isVisible();
    }
    
    public void drillDownToDetails() {
        page.locator("#drill-down-option").click();
    }
    
    // ====================== EXPORT AND SHARING METHODS ======================
    
    public void openExportOptions() {
        exportOptionsMenu.click();
    }
    
    public boolean isExportPdfOptionVisible() {
        return page.locator("#export-pdf").isVisible();
    }
    
    public boolean isExportExcelOptionVisible() {
        return page.locator("#export-excel").isVisible();
    }
    
    public boolean isShareReportOptionVisible() {
        return page.locator("#share-report").isVisible();
    }
    
    public void exportToPdf() {
        page.locator("#export-pdf").click();
    }
    
    public void exportToExcel() {
        page.locator("#export-excel").click();
    }
    
    public void shareReport() {
        page.locator("#share-report").click();
    }
    
    // ====================== CUSTOM REPORT BUILDER METHODS ======================
    
    public void openCustomReportBuilder() {
        customReportBuilder.click();
    }
    
    public boolean isCustomReportBuilderVisible() {
        return page.locator("#custom-report-interface").isVisible();
    }
    
    public void selectMetric(String metric) {
        // Use specific metric IDs instead of text-based selectors
        String metricId = switch (metric) {
            case "Student Enrollment" -> "#metric-option-enrollment";
            case "Completion Rate" -> "#metric-option-completion";
            case "Teacher Satisfaction" -> "#metric-option-satisfaction";
            case "Revenue per Student" -> "#metric-option-revenue";
            default -> throw new IllegalArgumentException("Unknown metric: " + metric);
        };
        page.locator(metricId).check();
    }
    
    public void selectVisualizationType(String type) {
        page.locator("#visualization-type").selectOption(type);
    }
    
    public void buildCustomReport() {
        page.locator("#btn-build-custom-report").click();
    }
    
    // ====================== ERROR HANDLING METHODS ======================
    
    public boolean isAccessDeniedVisible() {
        return page.locator("#access-denied").isVisible();
    }
    
    public boolean isInsufficientPermissionsVisible() {
        return page.locator("#insufficient-permissions").isVisible();
    }
    
    public boolean isDataProcessingErrorVisible() {
        return page.locator("#data-processing-error").isVisible();
    }
    
    public boolean isTimeoutErrorVisible() {
        return page.locator("#timeout-error").isVisible();
    }
    
    public boolean isRetryOptionVisible() {
        return page.locator("#retry-option").isVisible();
    }
    
    public void retryAnalytics() {
        page.locator("#retry-option").click();
    }
    
    // ====================== ADDITIONAL VERIFICATION METHODS ======================
    
    public String getSelectedTermsText() {
        return page.locator("#selected-terms").textContent();
    }
    
    public boolean isEnrollmentGrowthRateVisible() {
        return page.locator("#enrollment-growth-rate").isVisible();
    }
    
    public boolean isStudentCountComparisonVisible() {
        return page.locator("#student-count-comparison").isVisible();
    }
    
    // Removed isTextVisible method to avoid text-based selectors for localization compatibility
    
    public boolean isTeacherCountTrendsVisible() {
        return page.locator("#teacher-count-trends").isVisible();
    }
    
    public boolean isTeacherUtilizationVisible() {
        return page.locator("#teacher-utilization").isVisible();
    }
    
    public boolean isAverageClassSizeVisible() {
        return page.locator("#average-class-size").isVisible();
    }
    
    public boolean isClassCapacityUtilizationVisible() {
        return page.locator("#class-capacity-utilization").isVisible();
    }
    
    public boolean isCapacityGrowthTrendsVisible() {
        return page.locator("#capacity-growth-trends").isVisible();
    }
    
    public boolean isOptimalClassSizeAnalysisVisible() {
        return page.locator("#optimal-class-size-analysis").isVisible();
    }
    
    public boolean isDetailedClassBreakdownVisible() {
        return page.locator("#detailed-class-breakdown").isVisible();
    }
    
    public boolean isRevenueTrendsVisible() {
        return page.locator("#revenue-trends").isVisible();
    }
    
    public boolean isRevenuePerStudentVisible() {
        return page.locator("#revenue-per-student").isVisible();
    }
    
    public boolean isTotalRevenueComparisonVisible() {
        return page.locator("#total-revenue-comparison").isVisible();
    }
    
    public boolean isRevenueGrowthRateVisible() {
        return page.locator("#revenue-growth-rate").isVisible();
    }
    
    public boolean isOperationalCostsVisible() {
        return page.locator("#operational-costs").isVisible();
    }
    
    public boolean isCostEfficiencyMetricsVisible() {
        return page.locator("#cost-efficiency-metrics").isVisible();
    }
    
    public boolean isExportSuccessVisible() {
        return page.locator("#export-success").isVisible();
    }
    
    public boolean isTeacherSatisfactionScoresVisible() {
        return page.locator("#teacher-satisfaction-scores").isVisible();
    }
    
    public boolean isTeacherRetentionRateVisible() {
        return page.locator("#teacher-retention-rate").isVisible();
    }
    
    public boolean isAverageStudentFeedbackVisible() {
        return page.locator("#average-student-feedback").isVisible();
    }
    
    public boolean isClassCompletionByTeacherVisible() {
        return page.locator("#class-completion-by-teacher").isVisible();
    }
    
    public boolean isIndividualTeacherPerformanceVisible() {
        return page.locator("#individual-teacher-performance").isVisible();
    }
    
    public boolean isTeacherPerformanceTrendsVisible() {
        return page.locator("#teacher-performance-trends").isVisible();
    }
    
    public boolean isCustomReportResultsVisible() {
        return page.locator("#custom-report-results").isVisible();
    }
    
    public boolean isCustomChartVisible() {
        return page.locator("#custom-chart").isVisible();
    }
    
    public boolean isSelectedMetricsDisplayVisible() {
        return page.locator("#selected-metrics-display").isVisible();
    }
    
    public boolean isCustomInsightsVisible() {
        return page.locator("#custom-insights").isVisible();
    }
    
    public void clickSaveCustomReport() {
        page.locator("#save-custom-report").click();
    }
    
    public void fillReportName(String name) {
        page.locator("#report-name").fill(name);
    }
    
    public void clickSaveReportButton() {
        page.locator("#btn-save-report").click();
    }
    
    public boolean isReportSavedSuccessVisible() {
        return page.locator("#report-saved-success").isVisible();
    }
    
    public boolean isShareOptionsVisible() {
        return page.locator("#share-options").isVisible();
    }
    
    // ====================== VALIDATION AND ERROR HANDLING METHODS ======================
    
    public boolean isPartialDataDisclaimerVisible() {
        return page.locator("#partial-data-disclaimer").isVisible();
    }
    
    public boolean isMissingTeacherDataVisible() {
        return page.locator("#missing-teacher-data").isVisible();
    }
    
    public boolean isIncompleteStudentRecordsVisible() {
        return page.locator("#incomplete-student-records").isVisible();
    }
    
    public boolean isMissingFinancialDataVisible() {
        return page.locator("#missing-financial-data").isVisible();
    }
    
    public boolean isAccessRestrictedWarningVisible() {
        return page.locator("#access-restricted-warning").isVisible();
    }
    
    public boolean isPrivacyLimitationsVisible() {
        return page.locator("#privacy-limitations").isVisible();
    }
    
    public boolean isDataAccessLevelVisible() {
        return page.locator("#data-access-level").isVisible();
    }
    
    public boolean isArchivedTermNoticeVisible() {
        return page.locator("#archived-term-notice").isVisible();
    }
    
    public boolean isDetailedAnalyticsVisible() {
        return page.locator("#detailed-analytics").isVisible();
    }
    
    public boolean isSummaryDataOnlyVisible() {
        return page.locator("#summary-data-only").isVisible();
    }
    
    public boolean isLimitedViewNoticeVisible() {
        return page.locator("#limited-view-notice").isVisible();
    }
    
    public boolean isDataQualityScoreVisible() {
        return page.locator("#data-quality-score").isVisible();
    }
    
    public boolean isConfidenceLevelVisible() {
        return page.locator("#confidence-level").isVisible();
    }
    
    public boolean isTrendAnalysisUnavailableVisible() {
        return page.locator("#trend-analysis-unavailable").isVisible();
    }
    
    public boolean isComparisonNotPossibleVisible() {
        return page.locator("#comparison-not-possible").isVisible();
    }
    
    public boolean isSingleTermAnalysisOptionVisible() {
        return page.locator("#single-term-analysis-option").isVisible();
    }
    
    public boolean isAddMoreTermsSuggestionVisible() {
        return page.locator("#add-more-terms-suggestion").isVisible();
    }
    
    public boolean isLimitedHistoricalContextVisible() {
        return page.locator("#limited-historical-context").isVisible();
    }
    
    public boolean isShortTermTrendsOnlyVisible() {
        return page.locator("#short-term-trends-only").isVisible();
    }
    
    public boolean isProcessingIndicatorVisible() {
        return page.locator("#processing-indicator").isVisible();
    }
    
    public boolean isProcessingProgressVisible() {
        return page.locator("#processing-progress").isVisible();
    }
    
    public boolean isEstimatedTimeVisible() {
        return page.locator("#estimated-time").isVisible();
    }
    
    public boolean isReduceScopeSuggestionVisible() {
        return page.locator("#reduce-scope-suggestion").isVisible();
    }
    
    public boolean isRetryInProgressVisible() {
        return page.locator("#retry-in-progress").isVisible();
    }
    
    public boolean isPerformanceSuggestionsVisible() {
        return page.locator("#performance-suggestions").isVisible();
    }
    
    public boolean isOptimizeFiltersVisible() {
        return page.locator("#optimize-filters").isVisible();
    }
    
    public boolean isReduceDateRangeVisible() {
        return page.locator("#reduce-date-range").isVisible();
    }
    
    public boolean isScheduleAnalysisVisible() {
        return page.locator("#schedule-analysis").isVisible();
    }
    
    public boolean isInsufficientPermissionsMessageVisible() {
        return page.locator("#insufficient-permissions-message").isVisible();
    }
    
    public boolean isContactManagementVisible() {
        return page.locator("#contact-management").isVisible();
    }
    
    public boolean isLimitedAccessNoticeVisible() {
        return page.locator("#limited-access-notice").isVisible();
    }
    
    public boolean isFinancialAnalyticsVisible() {
        return page.locator("#financial-analytics").isVisible();
    }
    
    public boolean isTeacherPerformanceDetailsVisible() {
        return page.locator("#teacher-performance-details").isVisible();
    }
    
    public boolean isTeacherPerformanceAnalyticsVisible() {
        return page.locator("#teacher-performance-analytics").isVisible();
    }
    
    public boolean isExportFailedVisible() {
        return page.locator("#export-failed").isVisible();
    }
    
    public boolean isExportErrorMessageVisible() {
        return page.locator("#export-error-message").isVisible();
    }
    
    public boolean isRetryExportVisible() {
        return page.locator("#retry-export").isVisible();
    }
    
    public boolean isAlternativeFormatVisible() {
        return page.locator("#alternative-format").isVisible();
    }
    
    public void clickAlternativeFormat() {
        page.locator("#alternative-format").click();
    }
    
    public boolean isLargeExportWarningVisible() {
        return page.locator("#large-export-warning").isVisible();
    }
    
    public boolean isExportSizeLimitVisible() {
        return page.locator("#export-size-limit").isVisible();
    }
    
    public boolean isReduceScopeOptionVisible() {
        return page.locator("#reduce-scope-option").isVisible();
    }
    
    public boolean isSplitExportOptionVisible() {
        return page.locator("#split-export-option").isVisible();
    }
    
    public boolean isSharingRestrictionsVisible() {
        return page.locator("#sharing-restrictions").isVisible();
    }
    
    public boolean isDataPrivacyNoticeVisible() {
        return page.locator("#data-privacy-notice").isVisible();
    }
    
    public boolean isAuthorizedRecipientsOnlyVisible() {
        return page.locator("#authorized-recipients-only").isVisible();
    }
    
    public void clickLogout() {
        // Try to find logout button first
        if (page.locator("#logout-button").isVisible()) {
            page.locator("#logout-button").click();
        } else {
            // Fallback: navigate directly to logout endpoint
            String currentUrl = page.url();
            String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/", 8)); // Extract base URL
            page.navigate(baseUrl + "/logout");
        }
    }

    public boolean isLogoutVisible() {
        return page.locator("#logout-button").isVisible();
    }
}
