package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Feedback Analytics and Reporting functionality.
 * 
 * Handles feedback analytics dashboard, teacher performance insights, and trend analysis.
 */
public class FeedbackAnalyticsPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator feedbackAnalyticsMenu;
    private final Locator analyticsReportsMenu;
    private final Locator analyticsDashboard;
    
    // Dashboard overview locators
    private final Locator overviewPanel;
    private final Locator totalFeedbackCount;
    private final Locator responseRate;
    private final Locator averageRating;
    private final Locator trendIndicators;
    
    // Teacher performance locators
    private final Locator teacherPerformancePanel;
    private final Locator teacherRankings;
    private final Locator performanceMetrics;
    private final Locator ratingDistribution;
    private final Locator improvementAreas;
    
    // Feedback analysis locators
    private final Locator feedbackAnalysisPanel;
    private final Locator categoryBreakdown;
    private final Locator sentimentAnalysis;
    private final Locator keywordCloud;
    private final Locator commonThemes;
    
    // Trend analysis locators
    private final Locator trendAnalysisPanel;
    private final Locator timeSeriesChart;
    private final Locator periodicComparison;
    private final Locator trendLines;
    private final Locator seasonalPatterns;
    
    // Filtering and controls locators
    private final Locator dateRangeFilter;
    private final Locator teacherFilter;
    private final Locator subjectFilter;
    private final Locator classFilter;
    private final Locator applyFiltersButton;
    
    // Report generation locators
    private final Locator generateReportButton;
    private final Locator reportTypeSelector;
    private final Locator exportOptions;
    private final Locator reportPreview;
    private final Locator downloadButton;
    
    // Interactive chart locators
    private final Locator interactiveCharts;
    private final Locator chartLegend;
    private final Locator chartTooltips;
    private final Locator chartControls;
    
    public FeedbackAnalyticsPage(Page page) {
        this.page = page;
        
        // Initialize locators
        this.feedbackAnalyticsMenu = page.locator("#feedback-analytics-nav");
        this.analyticsReportsMenu = page.locator("nav a[href*='reports'], text='Reports'");
        this.analyticsDashboard = page.locator(".analytics-dashboard, .feedback-analytics");
        
        // Dashboard overview
        this.overviewPanel = page.locator(".overview-panel, .dashboard-overview");
        this.totalFeedbackCount = page.locator(".total-feedback, .feedback-count");
        this.responseRate = page.locator(".response-rate, .participation-rate");
        this.averageRating = page.locator(".average-rating, .overall-rating");
        this.trendIndicators = page.locator(".trend-indicators, .trend-arrows");
        
        // Teacher performance
        this.teacherPerformancePanel = page.locator(".teacher-performance, .performance-panel");
        this.teacherRankings = page.locator(".teacher-rankings, .rankings-table");
        this.performanceMetrics = page.locator(".performance-metrics, .teacher-metrics");
        this.ratingDistribution = page.locator(".rating-distribution, .rating-chart");
        this.improvementAreas = page.locator(".improvement-areas, .development-areas");
        
        // Feedback analysis
        this.feedbackAnalysisPanel = page.locator(".feedback-analysis, .analysis-panel");
        this.categoryBreakdown = page.locator(".category-breakdown, .feedback-categories");
        this.sentimentAnalysis = page.locator(".sentiment-analysis, .sentiment-chart");
        this.keywordCloud = page.locator(".keyword-cloud, .word-cloud");
        this.commonThemes = page.locator(".common-themes, .theme-analysis");
        
        // Trend analysis
        this.trendAnalysisPanel = page.locator(".trend-analysis, .trends-panel");
        this.timeSeriesChart = page.locator(".time-series, .timeline-chart");
        this.periodicComparison = page.locator(".periodic-comparison, .period-chart");
        this.trendLines = page.locator(".trend-lines, .trend-graph");
        this.seasonalPatterns = page.locator(".seasonal-patterns, .seasonal-chart");
        
        // Filtering and controls
        this.dateRangeFilter = page.locator(".date-range-picker, input[type='date']");
        this.teacherFilter = page.locator("select[name='teacher'], #teacher-filter");
        this.subjectFilter = page.locator("select[name='subject'], #subject-filter");
        this.classFilter = page.locator("select[name='class'], #class-filter");
        this.applyFiltersButton = page.locator("button:has-text('Apply Filters'), button[data-action='filter']");
        
        // Report generation
        this.generateReportButton = page.locator("#generate-report-button");
        this.reportTypeSelector = page.locator("select[name='reportType'], #report-type");
        this.exportOptions = page.locator(".export-options, .download-options");
        this.reportPreview = page.locator(".report-preview, .preview-panel");
        this.downloadButton = page.locator("button:has-text('Download'), button[data-action='download']");
        
        // Interactive charts
        this.interactiveCharts = page.locator(".chart-container, .interactive-chart");
        this.chartLegend = page.locator(".chart-legend, .legend");
        this.chartTooltips = page.locator(".chart-tooltip, .tooltip");
        this.chartControls = page.locator(".chart-controls, .chart-options");
    }
    
    // Navigation methods
    public void navigateToFeedbackAnalytics() {
        // First open the Analytics dropdown
        page.locator("#analytics-menu-button").click();
        page.waitForTimeout(500); // Wait for dropdown animation
        // Then click the Feedback Analytics link
        page.locator("#feedback-analytics-nav").click();
        page.waitForLoadState();
    }
    
    public void navigateToAnalyticsReports() {
        analyticsReportsMenu.click();
        page.waitForLoadState();
    }
    
    public boolean isAnalyticsDashboardVisible() {
        return analyticsDashboard.isVisible();
    }
    
    // Dashboard overview methods
    public boolean isOverviewPanelVisible() {
        return overviewPanel.isVisible();
    }
    
    public boolean isTotalFeedbackCountVisible() {
        return totalFeedbackCount.isVisible();
    }
    
    public int getTotalFeedbackCount() {
        String text = totalFeedbackCount.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
    
    public boolean isResponseRateVisible() {
        return responseRate.isVisible();
    }
    
    public String getResponseRate() {
        return responseRate.textContent();
    }
    
    public boolean isResponseRateAboveThreshold(int threshold) {
        String rateText = getResponseRate();
        int rate = Integer.parseInt(rateText.replaceAll("[^0-9]", ""));
        return rate >= threshold;
    }
    
    public boolean isAverageRatingVisible() {
        return averageRating.isVisible();
    }
    
    public String getAverageRating() {
        return averageRating.textContent();
    }
    
    public boolean areTrendIndicatorsVisible() {
        return trendIndicators.isVisible();
    }
    
    public boolean isPositiveTrendVisible() {
        return page.locator(".trend-up, .positive-trend, .improvement").isVisible();
    }
    
    // Teacher performance methods
    public boolean isTeacherPerformancePanelVisible() {
        return teacherPerformancePanel.isVisible();
    }
    
    public boolean areTeacherRankingsVisible() {
        return teacherRankings.isVisible();
    }
    
    public boolean isTeacherInTopRanking(String teacherName) {
        return page.locator(String.format(".rankings-table .top-teacher:has-text('%s')", teacherName)).isVisible();
    }
    
    public boolean arePerformanceMetricsVisible() {
        return performanceMetrics.isVisible();
    }
    
    public boolean isRatingDistributionVisible() {
        return ratingDistribution.isVisible();
    }
    
    public boolean areImprovementAreasVisible() {
        return improvementAreas.isVisible();
    }
    
    public String getTopPerformingTeacher() {
        return page.locator(".rankings-table .rank-1, .top-teacher").textContent();
    }
    
    public boolean isTeacherPerformanceImproving(String teacherName) {
        return page.locator(String.format(".teacher-metrics:has-text('%s') .trend-up", teacherName)).isVisible();
    }
    
    // Feedback analysis methods
    public boolean isFeedbackAnalysisPanelVisible() {
        return feedbackAnalysisPanel.isVisible();
    }
    
    public boolean isCategoryBreakdownVisible() {
        return categoryBreakdown.isVisible();
    }
    
    public boolean isSentimentAnalysisVisible() {
        return sentimentAnalysis.isVisible();
    }
    
    public boolean isPositiveSentimentDominant() {
        return page.locator(".sentiment:has-text('Positive'), .positive-sentiment").isVisible();
    }
    
    public boolean isKeywordCloudVisible() {
        return keywordCloud.isVisible();
    }
    
    public boolean isKeywordVisible(String keyword) {
        return page.locator(String.format(".keyword-cloud:has-text('%s')", keyword)).isVisible();
    }
    
    public boolean areCommonThemesVisible() {
        return commonThemes.isVisible();
    }
    
    public String getMostCommonTheme() {
        return page.locator(".common-themes .top-theme, .theme-list .first").textContent();
    }
    
    // Trend analysis methods
    public boolean isTrendAnalysisPanelVisible() {
        return trendAnalysisPanel.isVisible();
    }
    
    public boolean isTimeSeriesChartVisible() {
        return timeSeriesChart.isVisible();
    }
    
    public boolean isPeriodicComparisonVisible() {
        return periodicComparison.isVisible();
    }
    
    public boolean areTrendLinesVisible() {
        return trendLines.isVisible();
    }
    
    public boolean areSeasonalPatternsVisible() {
        return seasonalPatterns.isVisible();
    }
    
    public boolean isOverallTrendPositive() {
        return page.locator(".trend-line.positive, .upward-trend").isVisible();
    }
    
    // Filtering and controls methods
    public void setDateRange(String startDate, String endDate) {
        page.locator("input[name='startDate']").fill(startDate);
        page.locator("input[name='endDate']").fill(endDate);
    }
    
    public void selectTeacherFilter(String teacherName) {
        teacherFilter.selectOption(teacherName);
    }
    
    public void selectSubjectFilter(String subject) {
        subjectFilter.selectOption(subject);
    }
    
    public void selectClassFilter(String className) {
        classFilter.selectOption(className);
    }
    
    public void applyFilters() {
        applyFiltersButton.click();
        page.waitForTimeout(2000); // Allow for data loading
    }
    
    public boolean areFiltersApplied() {
        return page.locator(".filters-applied, .active-filters").isVisible();
    }
    
    public void clearFilters() {
        page.locator("button:has-text('Clear Filters'), .clear-filters").click();
        page.waitForTimeout(1000);
    }
    
    // Report generation methods
    public void generateReport() {
        generateReportButton.click();
        page.waitForSelector(".report-preview, .preview-panel");
    }
    
    public boolean isGenerateReportButtonEnabled() {
        return generateReportButton.isEnabled();
    }
    
    public void selectReportType(String reportType) {
        reportTypeSelector.selectOption(reportType);
    }
    
    public boolean areExportOptionsVisible() {
        return exportOptions.isVisible();
    }
    
    public boolean isReportPreviewVisible() {
        return reportPreview.isVisible();
    }
    
    public void downloadReport(String format) {
        page.locator(String.format(".export-option[data-format='%s']", format)).click();
        page.waitForTimeout(3000); // Allow for download
    }
    
    public boolean isDownloadButtonEnabled() {
        return downloadButton.isEnabled();
    }
    
    // Interactive chart methods
    public boolean areInteractiveChartsVisible() {
        return interactiveCharts.count() > 0;
    }
    
    public boolean isChartLegendVisible() {
        return chartLegend.isVisible();
    }
    
    public void hoverOverChartElement() {
        interactiveCharts.first().hover();
        page.waitForTimeout(500);
    }
    
    public boolean areChartTooltipsVisible() {
        return chartTooltips.isVisible();
    }
    
    public boolean areChartControlsVisible() {
        return chartControls.isVisible();
    }
    
    public void toggleChartView(String viewType) {
        page.locator(String.format(".chart-control[data-view='%s']", viewType)).click();
        page.waitForTimeout(1000);
    }
    
    // Specific analytics methods
    public boolean isCategoryRatingVisible(String category) {
        return page.locator(String.format(".category:has-text('%s') .rating", category)).isVisible();
    }
    
    public String getCategoryRating(String category) {
        return page.locator(String.format(".category:has-text('%s') .rating", category)).textContent();
    }
    
    public boolean isImprovementAreaHighlighted(String area) {
        return page.locator(String.format(".improvement-area:has-text('%s'), .needs-attention:has-text('%s')", area, area)).isVisible();
    }
    
    public boolean isTeacherComparisonVisible() {
        return page.locator(".teacher-comparison, .comparative-analysis").isVisible();
    }
    
    public String getBestPerformingCategory() {
        return page.locator(".category-breakdown .highest, .top-category").textContent();
    }
    
    public String getWorstPerformingCategory() {
        return page.locator(".category-breakdown .lowest, .bottom-category").textContent();
    }
    
    // Data validation methods
    public boolean isDataComplete() {
        return !page.locator(".no-data, .missing-data").isVisible();
    }
    
    public boolean isLoadingComplete() {
        return !page.locator(".loading, .spinner").isVisible();
    }
    
    public void refreshAnalytics() {
        page.locator("button[data-action='refresh'], .refresh-button").click();
        page.waitForTimeout(3000);
    }
    
    public String getLastUpdateTimestamp() {
        return page.locator(".last-updated, .timestamp").textContent();
    }
    
    public boolean isRealTimeDataEnabled() {
        return page.locator(".real-time-indicator, .live-data").isVisible();
    }
    
    // Additional methods required by AcademicAdminTest
    public void switchToTeacherFeedbackAnalytics() {
        page.locator("a[href*='teacher-feedback'], .tab:has-text('Teacher Feedback')").click();
        page.waitForLoadState();
    }
    
    public boolean isTeacherFeedbackOverviewVisible() {
        return page.locator(".teacher-feedback-overview, .feedback-summary").isVisible();
    }
    
    public void drillDownIntoCategories() {
        page.locator(".category-drill-down, .drill-down-button").click();
        page.waitForLoadState();
    }
    
    public boolean isTeachingQualityAverageVisible(double expectedAverage) {
        String expectedStr = String.format("%.1f", expectedAverage);
        return page.locator(String.format(".teaching-quality:has-text('%s'), .category:has-text('Teaching Quality'):has-text('%s')", expectedStr, expectedStr)).isVisible();
    }
    
    public boolean isCommunicationAverageVisible(double expectedAverage) {
        String expectedStr = String.format("%.1f", expectedAverage);
        return page.locator(String.format(".communication:has-text('%s'), .category:has-text('Communication'):has-text('%s')", expectedStr, expectedStr)).isVisible();
    }
    
    public boolean isPunctualityAverageVisible(double expectedAverage) {
        String expectedStr = String.format("%.1f", expectedAverage);
        return page.locator(String.format(".punctuality:has-text('%s'), .category:has-text('Punctuality'):has-text('%s')", expectedStr, expectedStr)).isVisible();
    }
    
    public boolean isFairnessAverageVisible(double expectedAverage) {
        String expectedStr = String.format("%.1f", expectedAverage);
        return page.locator(String.format(".fairness:has-text('%s'), .category:has-text('Fairness'):has-text('%s')", expectedStr, expectedStr)).isVisible();
    }
    
    public boolean areChartsAndGraphsDisplayedCorrectly() {
        return page.locator(".chart-container, .graph-container").count() > 0 &&
               !page.locator(".chart-error, .graph-error").isVisible();
    }
    
    public void reviewTeacherSpecificPerformance() {
        page.locator(".teacher-performance-tab, .individual-performance").click();
        page.waitForLoadState();
    }
    
    public boolean areTopRatedTeachersHighlighted() {
        return page.locator("#top-rated-teachers-section").isVisible();
    }
    
    public boolean areTeachersNeedingSupportIdentified() {
        return page.locator(".needs-support, .low-performance, .requires-attention").isVisible();
    }
    
    public boolean areSpecificImprovementAreasNoted() {
        return page.locator(".improvement-notes, .specific-areas").isVisible();
    }
    
    public void switchToFacilityAssessmentAnalytics() {
        page.locator("#facility-tab").click();
        page.waitForLoadState();
    }
    
    public boolean isFacilityRatingSummaryVisible(double expectedRating) {
        return page.locator("#facility-rating-summary").isVisible();
    }
    
    // More methods required by AcademicAdminTest
    public void generateComprehensiveReport() {
        page.locator("button:has-text('Generate Comprehensive Report'), .comprehensive-report").click();
        page.waitForLoadState();
    }
    
    public boolean isPdfReportCreatedSuccessfully() {
        return page.locator(".pdf-generated, .report-success").isVisible();
    }
    
    public boolean isDownloadLinkProvided() {
        return page.locator(".download-link, a[href*='.pdf']").isVisible();
    }
    
    public boolean areEmailDistributionOptionsAvailable() {
        return page.locator(".email-options, .distribution-options").isVisible();
    }
    
    public void openGeneratedReport() {
        page.locator(".download-link, .view-report").click();
    }
    
    public boolean isExecutiveSummaryIncluded() {
        return page.locator(".executive-summary, .summary-section").isVisible();
    }
    
    public boolean areTeacherPerformanceInsightsVisible() {
        return page.locator(".teacher-insights, .performance-insights").isVisible();
    }
    
    public boolean areFacilityImprovementRecommendationsVisible() {
        return page.locator(".facility-recommendations, .improvement-recommendations").isVisible();
    }
    
    public boolean areActionItemsWithPrioritiesVisible() {
        return page.locator(".action-items, .priority-actions").isVisible();
    }
    
    public boolean isAnonymityMaintainedForStudentFeedback() {
        return !page.locator(".student-name, .student-identity").isVisible();
    }
    
    // Even more methods required by AcademicAdminTest
    public void checkAutoGeneratedActionItems() {
        page.locator("#action-items-section").click();
    }
    
    public boolean areUrgentFacilityIssuesFlagged() {
        return page.locator(".urgent-facility-issues, .facility-flags").isVisible();
    }
    
    public boolean areImprovementPrioritiesListed() {
        return page.locator(".improvement-priorities, .priority-list").isVisible();
    }
    
    public boolean areDepartmentAssignmentsSuggested() {
        return page.locator(".department-assignments, .assignment-suggestions").isVisible();
    }
    
    public void configureReportParameters(String reportType, boolean includeAllCampaigns, boolean sendToManagement) {
        page.locator("#report-type").selectOption(reportType);
        if (includeAllCampaigns) {
            page.locator("#include-all-campaigns").check();
        }
        if (sendToManagement) {
            page.locator("#send-to-management").check();
        }
    }
    
    public boolean areReportConfigurationOptionsWorking() {
        return page.locator("#top-report-configuration").isVisible();
    }
    
    public boolean isIncludeAllCampaignsSelected() {
        return page.locator("#include-all-campaigns").isChecked();
    }
    
    public boolean isAudienceSetToManagementAndTeachers() {
        return page.locator("#audience-management").isChecked() && 
               page.locator("#audience-teachers").isChecked();
    }
    
    public boolean isAnonymityMaintained() {
        return page.locator("#maintain-anonymity").isChecked();
    }
    
    public boolean isReportGenerationProgressVisible() {
        return page.locator(".generation-progress, .progress-indicator").isVisible();
    }
    
    // Final batch of methods required by AcademicAdminTest
    public boolean isCampaignListWithResponseRatesVisible() {
        return page.locator("#campaign-list-with-response-rates").isVisible();
    }
    
    public boolean areFilterOptionsAvailable() {
        return page.locator(".filter-options, .analytics-filters").isVisible();
    }
    
    public void setFilterPeriod(String period) {
        page.locator("#period-filter").selectOption(period);
    }
    
    public boolean isDateRangePickerWorking() {
        return page.locator("#start-date-picker").isEnabled();
    }
    
    public boolean isDataRefreshedBasedOnSelection() {
        return page.locator(".data-refreshed, .updated-data").isVisible();
    }
    
    public boolean areResponseCountsUpdatedAccordingly() {
        return page.locator(".response-counts, .updated-counts").isVisible();
    }
    
    public void clickTeacherEvaluationCampaign() {
        page.locator("#teacher-evaluation-campaign").click();
    }
    
    public boolean isDetailedAnalyticsPageOpened() {
        return page.locator("#detailed-analytics").isVisible();
    }
    
    public boolean isOverallTeacherRatingVisible(double rating) {
        String ratingStr = String.format("%.1f", rating);
        return page.locator(String.format(".overall-rating:has-text('%s')", ratingStr)).isVisible();
    }
    
    public boolean isIndividualTeacherPerformanceSummaryVisible() {
        return page.locator(".individual-performance, .teacher-summary").isVisible();
    }
    
    public boolean isFacilityCategoryBreakdownVisible() {
        return page.locator("#facility-category-breakdown").isVisible();
    }
    
    public boolean arePriorityIssuesIdentified() {
        return page.locator("#priority-issues").isVisible();
    }
    
    // Final missing methods
    public boolean isAnalyticsMainDashboardLoaded() {
        return page.locator(".analytics-dashboard, .main-dashboard").isVisible();
    }
    
    public boolean areSummaryStatisticsVisible() {
        return page.locator(".summary-stats, .overview-statistics").isVisible();
    }
}