package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

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
        
        // Initialize navigation locators
        this.analyticsMenu = page.locator("#analytics-menu");
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
    
    public void navigateToCrossTermAnalytics() {
        analyticsMenu.click();
        page.locator("#cross-term-analytics-option").click();
        page.waitForSelector("#cross-term-analytics");
    }
    
    public void navigateToHistoricalComparison() {
        analyticsMenu.click();
        page.locator("#historical-comparison-option").click();
        page.waitForSelector("#historical-data");
    }
    
    // ====================== TERM SELECTION METHODS ======================
    
    public void selectSingleTerm(String termName) {
        termSelector.selectOption(termName);
    }
    
    public void selectMultipleTerms(String... termNames) {
        multiTermSelector.click();
        for (String term : termNames) {
            page.locator(String.format("#term-option:has-text('%s')", term)).check();
        }
    }
    
    public void selectComparisonPeriod(String period) {
        comparisonPeriodSelector.selectOption(period);
    }
    
    public void setDateRange(String startDate, String endDate) {
        page.locator("#start-date").fill(startDate);
        page.locator("#end-date").fill(endDate);
    }
    
    // ====================== ANALYTICS GENERATION METHODS ======================
    
    public void generateAnalytics() {
        generateAnalyticsButton.click();
        page.waitForSelector("#analytics-results");
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
    
    public void filterByTeacher(String teacher) {
        teacherFilter.selectOption(teacher);
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
        page.locator(String.format("#chart-term:has-text('%s')", termName)).click();
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
        page.locator(String.format("#metric-option:has-text('%s')", metric)).check();
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
}
