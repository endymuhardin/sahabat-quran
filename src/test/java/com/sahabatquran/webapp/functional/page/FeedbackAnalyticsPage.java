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
        this.analyticsReportsMenu = page.locator("#analytics-reports-menu");
        this.analyticsDashboard = page.locator("#analytics-dashboard");
        
        // Dashboard overview
        this.overviewPanel = page.locator("#overview-panel");
        this.totalFeedbackCount = page.locator("#total-feedback-count");
        this.responseRate = page.locator("#response-rate");
        this.averageRating = page.locator("#average-rating");
        this.trendIndicators = page.locator("#trend-indicators");
        
        // Teacher performance
        this.teacherPerformancePanel = page.locator("#teacher-performance-panel");
        this.teacherRankings = page.locator("#teacher-rankings");
        this.performanceMetrics = page.locator("#performance-metrics");
        this.ratingDistribution = page.locator("#rating-distribution");
        this.improvementAreas = page.locator("#improvement-areas");
        
        // Feedback analysis
        this.feedbackAnalysisPanel = page.locator("#feedback-analysis-panel");
        this.categoryBreakdown = page.locator("#category-breakdown");
        this.sentimentAnalysis = page.locator("#sentiment-analysis");
        this.keywordCloud = page.locator("#keyword-cloud");
        this.commonThemes = page.locator("#common-themes");
        
        // Trend analysis
        this.trendAnalysisPanel = page.locator("#trend-analysis-panel");
        this.timeSeriesChart = page.locator("#time-series-chart");
        this.periodicComparison = page.locator("#periodic-comparison");
        this.trendLines = page.locator("#trend-lines");
        this.seasonalPatterns = page.locator("#seasonal-patterns");
        
        // Filtering and controls
        this.dateRangeFilter = page.locator("#date-range-filter");
        this.teacherFilter = page.locator("#teacher-filter");
        this.subjectFilter = page.locator("#subject-filter");
        this.classFilter = page.locator("#class-filter");
        this.applyFiltersButton = page.locator("#apply-filters-button");
        
        // Report generation
        this.generateReportButton = page.locator("#generate-report-button");
        this.reportTypeSelector = page.locator("#report-type-selector");
        this.exportOptions = page.locator("#export-options");
        this.reportPreview = page.locator("#report-preview");
        this.downloadButton = page.locator("#download-button");
        
        // Interactive charts
        this.interactiveCharts = page.locator("#chart-container");
        this.chartLegend = page.locator("#chart-legend");
        this.chartTooltips = page.locator("#chart-tooltips");
        this.chartControls = page.locator("#chart-controls");
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
        return page.locator("#positive-trend-indicator").isVisible();
    }
    
    // Teacher performance methods
    public boolean isTeacherPerformancePanelVisible() {
        return teacherPerformancePanel.isVisible();
    }
    
    public boolean areTeacherRankingsVisible() {
        return teacherRankings.isVisible();
    }
    
    public boolean isTeacherInTopRanking(String teacherName) {
        return page.locator("#teacher-rankings").textContent().contains(teacherName);
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
        return page.locator("#top-performing-teacher").textContent();
    }
    
    public boolean isTeacherPerformanceImproving(String teacherName) {
        return page.locator(String.format("#teacher-%s-improvement-trend", teacherName.toLowerCase().replaceAll(" ", "-"))).isVisible();
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
        return page.locator("#positive-sentiment-indicator").isVisible();
    }
    
    public boolean isKeywordCloudVisible() {
        return keywordCloud.isVisible();
    }
    
    public boolean isKeywordVisible(String keyword) {
        return page.locator("#keyword-cloud").textContent().contains(keyword);
    }
    
    public boolean areCommonThemesVisible() {
        return commonThemes.isVisible();
    }
    
    public String getMostCommonTheme() {
        return page.locator("#top-theme").textContent();
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
        return page.locator("#overall-positive-trend").isVisible();
    }
    
    // Filtering and controls methods
    public void setDateRange(String startDate, String endDate) {
        page.locator("#start-date-input").fill(startDate);
        page.locator("#end-date-input").fill(endDate);
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
        return page.locator("#active-filters-indicator").isVisible();
    }
    
    public void clearFilters() {
        page.locator("#clear-filters-button").click();
        page.waitForTimeout(1000);
    }
    
    // Report generation methods
    public void generateReport() {
        generateReportButton.click();
        page.waitForSelector(".report-preview", new Page.WaitForSelectorOptions().setTimeout(5000));
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
        return page.locator("#report-preview").isVisible();
    }
    
    public void downloadReport(String format) {
        page.locator(String.format("#export-%s-button", format.toLowerCase())).click();
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
        page.locator(String.format("#chart-view-%s", viewType.toLowerCase())).click();
        page.waitForTimeout(1000);
    }
    
    // Specific analytics methods
    public boolean isCategoryRatingVisible(String category) {
        return page.locator(String.format("#category-%s-rating", category.toLowerCase().replaceAll(" ", "-"))).isVisible();
    }
    
    public String getCategoryRating(String category) {
        return page.locator(String.format("#category-%s-rating", category.toLowerCase().replaceAll(" ", "-"))).textContent();
    }
    
    public boolean isImprovementAreaHighlighted(String area) {
        return page.locator(String.format("#improvement-area-%s", area.toLowerCase().replaceAll(" ", "-"))).isVisible();
    }
    
    public boolean isTeacherComparisonVisible() {
        return page.locator("#teacher-comparison-panel").isVisible();
    }
    
    public String getBestPerformingCategory() {
        return page.locator("#best-performing-category").textContent();
    }
    
    public String getWorstPerformingCategory() {
        return page.locator("#worst-performing-category").textContent();
    }
    
    // Data validation methods
    public boolean isDataComplete() {
        return !page.locator("#no-data-indicator").isVisible();
    }
    
    public boolean isLoadingComplete() {
        return !page.locator("#loading-indicator").isVisible();
    }
    
    public void refreshAnalytics() {
        page.locator("#refresh-analytics-button").click();
        page.waitForTimeout(3000);
    }
    
    public String getLastUpdateTimestamp() {
        return page.locator("#last-update-timestamp").textContent();
    }
    
    public boolean isRealTimeDataEnabled() {
        return page.locator("#real-time-data-indicator").isVisible();
    }
    
    // Additional methods required by AcademicAdminTest
    public void switchToTeacherFeedbackAnalytics() {
        page.locator("#teacher-feedback-analytics-tab").click();
        page.waitForLoadState();
    }
    
    public boolean isTeacherFeedbackOverviewVisible() {
        return page.locator("#teacher-feedback-overview").isVisible();
    }
    
    public void drillDownIntoCategories() {
        page.locator("#category-drill-down-button").click();
        page.waitForLoadState();
    }
    
    public boolean isTeachingQualityAverageVisible(double expectedAverage) {
        return page.locator("#teaching-quality-average").isVisible();
    }
    
    public boolean isCommunicationAverageVisible(double expectedAverage) {
        return page.locator("#communication-average").isVisible();
    }
    
    public boolean isPunctualityAverageVisible(double expectedAverage) {
        return page.locator("#punctuality-average").isVisible();
    }
    
    public boolean isFairnessAverageVisible(double expectedAverage) {
        return page.locator("#fairness-average").isVisible();
    }
    
    public boolean areChartsAndGraphsDisplayedCorrectly() {
        return page.locator(".chart-container").count() > 0 &&
               !page.locator("#chart-error-indicator").isVisible();
    }
    
    public void reviewTeacherSpecificPerformance() {
        page.locator("#teacher-performance-tab").click();
        page.waitForLoadState();
    }
    
    public boolean areTopRatedTeachersHighlighted() {
        return page.locator("#top-rated-teachers-section").isVisible();
    }
    
    public boolean areTeachersNeedingSupportIdentified() {
        return page.locator("#teachers-needing-support").isVisible();
    }
    
    public boolean areSpecificImprovementAreasNoted() {
        return page.locator("#specific-improvement-areas").isVisible();
    }
    
    public void switchToFacilityAssessmentAnalytics() {
        page.locator("#facility-filter-btn").click();
        page.waitForLoadState();
    }
    
    public boolean isFacilityRatingSummaryVisible(double expectedRating) {
        return page.locator("#facility-rating-summary").isVisible();
    }
    
    // More methods required by AcademicAdminTest
    public void generateComprehensiveReport() {
        page.locator("#generate-comprehensive-report-button").click();
        page.waitForLoadState();
    }
    
    public boolean isPdfReportCreatedSuccessfully() {
        return page.locator("#pdf-generated").isVisible();
    }
    
    public boolean isDownloadLinkProvided() {
        return page.locator("#download-link").isVisible();
    }
    
    public boolean areEmailDistributionOptionsAvailable() {
        return page.locator("#feedback-email-distribution").isVisible();
    }
    
    public void openGeneratedReport() {
        page.locator("#download-link").click();
    }
    
    public boolean isExecutiveSummaryIncluded() {
        return page.locator("#executive-summary").isVisible();
    }
    
    public boolean areTeacherPerformanceInsightsVisible() {
        return page.locator("#teacher-performance").isVisible();
    }
    
    public boolean areFacilityImprovementRecommendationsVisible() {
        return page.locator("#facility-recommendations").isVisible();
    }
    
    public boolean areActionItemsWithPrioritiesVisible() {
        return page.locator("#action-items").isVisible();
    }
    
    public boolean isAnonymityMaintainedForStudentFeedback() {
        return !page.locator("#student-identity-info").isVisible();
    }
    
    // Even more methods required by AcademicAdminTest
    public void checkAutoGeneratedActionItems() {
        // Scroll element into view and click with force to avoid footer interference
        page.locator("#action-items-section").scrollIntoViewIfNeeded();
        page.waitForTimeout(500); // Brief wait for scroll to complete
        page.locator("#action-items-section").click(new Locator.ClickOptions().setForce(true));
    }
    
    public boolean areUrgentFacilityIssuesFlagged() {
        return page.locator("#urgent-facility-issues").isVisible();
    }
    
    public boolean areImprovementPrioritiesListed() {
        return page.locator("#improvement-priorities-list").isVisible();
    }
    
    public boolean areDepartmentAssignmentsSuggested() {
        return page.locator("#department-assignment-suggestions").isVisible();
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
        return page.locator(".generation-progress").isVisible();
    }
    
    // Final batch of methods required by AcademicAdminTest
    public boolean isCampaignListWithResponseRatesVisible() {
        return page.locator("#campaign-list-with-response-rates").isVisible();
    }
    
    public boolean areFilterOptionsAvailable() {
        return page.locator("#analytics-filter-options").isVisible();
    }
    
    public void setFilterPeriod(String period) {
        page.locator("#period-filter").selectOption(period);
    }
    
    public boolean isDateRangePickerWorking() {
        return page.locator("#start-date-picker").isEnabled();
    }
    
    public boolean isDataRefreshedBasedOnSelection() {
        return page.locator("#data-refresh-indicator").isVisible();
    }
    
    public boolean areResponseCountsUpdatedAccordingly() {
        return page.locator("#response-counts-updated").isVisible();
    }
    
    public void clickTeacherEvaluationCampaign() {
        page.locator("#teacher-evaluation-campaign").click();
    }
    
    public boolean isDetailedAnalyticsPageOpened() {
        return page.locator("#detailed-analytics").isVisible();
    }
    
    public boolean isOverallTeacherRatingVisible(double rating) {
        return page.locator("#overall-teacher-rating").isVisible();
    }
    
    public boolean isIndividualTeacherPerformanceSummaryVisible() {
        return page.locator("#individual-teacher-performance").isVisible();
    }
    
    public boolean isFacilityCategoryBreakdownVisible() {
        return page.locator("#facility-category-breakdown").isVisible();
    }
    
    public boolean arePriorityIssuesIdentified() {
        return page.locator("#priority-issues").isVisible();
    }
    
    // Final missing methods
    public boolean isAnalyticsMainDashboardLoaded() {
        return page.locator("#analytics-main-dashboard").isVisible();
    }
    
    public boolean areSummaryStatisticsVisible() {
        return page.locator("#summary-statistics").isVisible();
    }
}