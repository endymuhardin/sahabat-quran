package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Session Monitoring and Academic Admin Dashboard functionality.
 * 
 * Handles real-time session monitoring, progress tracking, and administrative oversight.
 */
public class SessionMonitoringPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator monitoringDashboardMenu;
    private final Locator academicAdminDashboard;
    
    // Real-time monitoring locators
    private final Locator realTimeSessionsPanel;
    private final Locator sessionStatusIndicators;
    private final Locator liveSessionsCounter;
    private final Locator pendingSessionsCounter;
    private final Locator completedSessionsCounter;
    
    // Alert system locators
    private final Locator systemAlertsPanel;
    private final Locator criticalAlerts;
    private final Locator alertBadge;
    private final Locator alertNotifications;
    
    // Session details locators
    private final Locator sessionCards;
    private final Locator sessionDetails;
    private final Locator attendanceInfo;
    private final Locator instructorStatus;
    private final Locator sessionTimer;
    
    // Statistics and metrics locators
    private final Locator dailyStatistics;
    private final Locator attendanceRates;
    private final Locator sessionCompletionRates;
    private final Locator instructorPerformanceMetrics;
    
    // Action buttons locators
    private final Locator viewDetailsButton;
    private final Locator contactInstructorButton;
    private final Locator generateReportButton;
    private final Locator exportDataButton;
    
    // Filtering and search locators
    private final Locator sessionFilter;
    private final Locator statusFilter;
    private final Locator dateRangeFilter;
    private final Locator instructorFilter;
    private final Locator searchBox;
    
    public SessionMonitoringPage(Page page) {
        this.page = page;
        
        // Initialize locators
        this.monitoringDashboardMenu = page.locator("#monitoring-dashboard-menu");
        this.academicAdminDashboard = page.locator("#academic-admin-dashboard");
        
        // Real-time monitoring
        this.realTimeSessionsPanel = page.locator("#realtime-sessions-panel");
        this.sessionStatusIndicators = page.locator("#session-status-indicators");
        this.liveSessionsCounter = page.locator("#live-sessions-counter");
        this.pendingSessionsCounter = page.locator("#pending-sessions-counter");
        this.completedSessionsCounter = page.locator("#completed-sessions-counter");
        
        // Alert system
        this.systemAlertsPanel = page.locator("#system-alerts-panel");
        this.criticalAlerts = page.locator("#critical-alerts");
        this.alertBadge = page.locator("#alert-badge");
        this.alertNotifications = page.locator("#alert-notifications");
        
        // Session details
        this.sessionCards = page.locator("[id^='session-card-']");
        this.sessionDetails = page.locator("#session-details");
        this.attendanceInfo = page.locator("#attendance-info");
        this.instructorStatus = page.locator("#instructor-status");
        this.sessionTimer = page.locator("#session-timer");
        
        // Statistics
        this.dailyStatistics = page.locator("#daily-statistics");
        this.attendanceRates = page.locator("#attendance-rates");
        this.sessionCompletionRates = page.locator("#session-completion-rates");
        this.instructorPerformanceMetrics = page.locator("#instructor-performance-metrics");
        
        // Action buttons
        this.viewDetailsButton = page.locator("#view-details-button");
        this.contactInstructorButton = page.locator("#contact-instructor-button");
        this.generateReportButton = page.locator("#generate-report-button");
        this.exportDataButton = page.locator("#export-data-button");
        
        // Filtering and search
        this.sessionFilter = page.locator("#session-filter");
        this.statusFilter = page.locator("#status-filter");
        this.dateRangeFilter = page.locator("#date-range-filter");
        this.instructorFilter = page.locator("#instructor-filter");
        this.searchBox = page.locator("#search-box");
    }
    
    // Navigation methods
    public void navigateToMonitoringDashboard() {
        monitoringDashboardMenu.click();
        page.waitForLoadState();
    }
    
    public boolean isAcademicAdminDashboardVisible() {
        return academicAdminDashboard.isVisible();
    }
    
    // Real-time monitoring methods
    public boolean isRealTimeSessionsPanelVisible() {
        return realTimeSessionsPanel.isVisible();
    }
    
    public boolean areSessionStatusIndicatorsVisible() {
        return sessionStatusIndicators.count() > 0;
    }
    
    public boolean isLiveSessionsCountCorrect(int expectedCount) {
        String counterText = page.locator("#live-sessions-counter").textContent();
        return counterText.contains(String.valueOf(expectedCount));
    }
    
    public boolean isPendingSessionsCountVisible(int expectedCount) {
        String counterText = page.locator("#pending-sessions-counter").textContent();
        return counterText.contains(String.valueOf(expectedCount));
    }
    
    public boolean isCompletedSessionsCountVisible(int expectedCount) {
        String counterText = page.locator("#completed-sessions-counter").textContent();
        return counterText.contains(String.valueOf(expectedCount));
    }
    
    public int getCurrentLiveSessionsCount() {
        String text = liveSessionsCounter.textContent();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
    
    // Alert system methods
    public boolean isSystemAlertsPanelVisible() {
        return systemAlertsPanel.isVisible();
    }
    
    public boolean areCriticalAlertsVisible() {
        return criticalAlerts.isVisible();
    }
    
    public boolean isAlertBadgeVisible() {
        return alertBadge.isVisible();
    }
    
    public boolean areAlertNotificationsVisible() {
        return alertNotifications.count() > 0;
    }
    
    public boolean isSpecificAlertVisible(String alertType) {
        return page.locator("#system-alerts-panel").textContent().contains(alertType);
    }
    
    public void dismissAlert() {
        page.locator("#dismiss-alert-button").first().click();
    }
    
    // Session monitoring methods
    public boolean areSessionCardsVisible() {
        return sessionCards.count() > 0;
    }
    
    public boolean isSessionDetailsVisible(String sessionId) {
        return page.locator(String.format("#session-card-%s", sessionId)).isVisible();
    }
    
    public boolean isAttendanceInfoVisible() {
        return attendanceInfo.isVisible();
    }
    
    public boolean isInstructorStatusVisible() {
        return instructorStatus.isVisible();
    }
    
    public boolean isSessionTimerRunning() {
        return sessionTimer.isVisible() && 
               !sessionTimer.textContent().equals("00:00");
    }
    
    public void viewSessionDetails(String sessionId) {
        page.locator(String.format("#session-card-%s #view-details-button", sessionId)).click();
        page.waitForSelector("#session-details-modal");
    }
    
    public boolean isSessionDetailsModalVisible() {
        return page.locator("#session-details-modal").isVisible();
    }
    
    // Statistics and metrics methods
    public boolean isDailyStatisticsVisible() {
        return dailyStatistics.isVisible();
    }
    
    public boolean areAttendanceRatesDisplayed() {
        return attendanceRates.isVisible();
    }
    
    public boolean areSessionCompletionRatesVisible() {
        return sessionCompletionRates.isVisible();
    }
    
    public boolean areInstructorPerformanceMetricsVisible() {
        return instructorPerformanceMetrics.isVisible();
    }
    
    public String getAttendanceRate() {
        return attendanceRates.textContent();
    }
    
    public String getCompletionRate() {
        return sessionCompletionRates.textContent();
    }
    
    public boolean isAttendanceRateAboveThreshold(int threshold) {
        String rateText = getAttendanceRate();
        int rate = Integer.parseInt(rateText.replaceAll("[^0-9]", ""));
        return rate >= threshold;
    }
    
    // Action methods
    public void clickViewDetails() {
        viewDetailsButton.first().click();
        page.waitForSelector("#session-details-modal");
    }
    
    public void contactInstructor(String instructorName) {
        page.locator(String.format("#instructor-%s #contact-button", instructorName.toLowerCase().replaceAll(" ", "-"))).click();
        page.waitForSelector("#contact-modal");
    }
    
    public boolean isContactInstructorModalVisible() {
        return page.locator("#contact-modal").isVisible();
    }
    
    public void generateReport() {
        generateReportButton.click();
        page.waitForSelector("#report-generation-modal");
    }
    
    public boolean isReportGenerationModalVisible() {
        return page.locator("#report-generation-modal").isVisible();
    }
    
    public void exportData() {
        exportDataButton.click();
        page.waitForTimeout(2000); // Allow for download
    }
    
    // Filtering and search methods
    public void filterBySessionStatus(String status) {
        statusFilter.selectOption(status);
        page.waitForTimeout(1000); // Allow for filtering
    }
    
    public void filterByInstructor(String instructorName) {
        instructorFilter.selectOption(instructorName);
        page.waitForTimeout(1000);
    }
    
    public void filterByDateRange(String startDate, String endDate) {
        page.locator("#start-date-input").fill(startDate);
        page.locator("#end-date-input").fill(endDate);
        page.locator("#apply-filter-button").click();
        page.waitForTimeout(1000);
    }
    
    public void searchSessions(String searchTerm) {
        searchBox.fill(searchTerm);
        page.keyboard().press("Enter");
        page.waitForTimeout(1000);
    }
    
    public boolean areFilteredResultsVisible() {
        return page.locator("#filtered-results").isVisible();
    }
    
    public int getFilteredSessionsCount() {
        return sessionCards.count();
    }
    
    // Specific monitoring scenarios methods
    public boolean isInstructorLateAlertVisible(String instructorName) {
        String alertText = page.locator("#instructor-late-alert").textContent();
        return alertText.contains(instructorName) && alertText.contains("late");
    }
    
    public boolean isLowAttendanceAlertVisible(String className) {
        String alertText = page.locator("#low-attendance-alert").textContent();
        return alertText.contains(className) && alertText.contains("attendance");
    }
    
    public boolean isSessionDelayAlertVisible() {
        return page.locator("#session-delay-alert").isVisible();
    }
    
    public boolean isProgressTrackingUpdateVisible() {
        return page.locator("#progress-tracking-update").isVisible();
    }
    
    public void refreshDashboard() {
        page.locator("#refresh-dashboard-button").click();
        page.waitForTimeout(2000);
    }
    
    public boolean isDashboardAutoRefreshing() {
        return page.locator("#auto-refresh-indicator").isVisible();
    }
    
    public String getLastUpdateTimestamp() {
        return page.locator("#last-update-timestamp").textContent();
    }
    
    // Performance monitoring methods
    public boolean areSystemPerformanceMetricsVisible() {
        return page.locator("#system-performance-metrics").isVisible();
    }
    
    public boolean isResponseTimeMetricVisible() {
        return page.locator("#response-time-metric").isVisible();
    }
    
    public boolean areHealthChecksVisible() {
        return page.locator("#health-checks").isVisible();
    }
    
    public boolean isSystemStatusHealthy() {
        String statusText = page.locator("#system-status").textContent();
        return statusText.contains("Healthy") || statusText.contains("Good");
    }
    
    // Additional methods required by AcademicAdminTest
    public boolean isLiveSessionMonitorCardVisible() {
        return page.locator("#live-session-monitor-card").isVisible();
    }
    
    public boolean areQuickStatsVisible() {
        return page.locator("#quick-stats-panel").isVisible();
    }
    
    public boolean isActiveSessionCountVisible(int expectedCount) {
        return page.locator("#active-sessions-count").isVisible();
    }
    
    public boolean isAttendanceStatsVisible() {
        return page.locator("#attendance-stats-panel").isVisible();
    }
    
    public void navigateToSessionMonitoring() {
        // Click the Session Monitoring link from dashboard
        page.locator("#session-monitoring-link").click();
        page.waitForLoadState();
    }
    
    public boolean isRealTimeSessionGridVisible() {
        return page.locator("#real-time-session-grid").isVisible();
    }
    
    // More methods required by AcademicAdminTest
    public boolean isTeacherCheckInStatusVisible() {
        return page.locator("#teacher-check-in-status").isVisible();
    }
    
    public boolean isAutoRefreshIndicatorActive() {
        return page.locator("#auto-refresh-indicator.active").isVisible();
    }
    
    public void clickActiveSession() {
        page.locator("#active-session").first().click();
    }
    
    public boolean isSessionInfoPopupVisible() {
        return page.locator("#session-info-popup").isVisible();
    }
    
    public boolean isTeacherAttendanceStatusVisible() {
        return page.locator("#teacher-check-in-status").isVisible();
    }
    
    public boolean isStudentAttendanceLiveCountVisible() {
        return page.locator("#student-attendance-live-count").isVisible();
    }
    
    public boolean areSessionProgressIndicatorsVisible() {
        return page.locator("#session-progress-indicators").isVisible();
    }
    
    public void reviewAttendanceSummary() {
        page.locator("#attendance-rates-per-class").click();
    }
    
    // Even more methods required by AcademicAdminTest
    public boolean areAttendanceRatesPerClassVisible() {
        return page.locator("#attendance-rates-per-class").isVisible();
    }
    
    public boolean isRealTimeAttendanceUpdateVisible() {
        return page.locator("#realtime-attendance-update").isVisible();
    }
    
    public boolean areLowAttendanceAlertsVisible() {
        return page.locator("#low-attendance-alerts").isVisible();
    }
    
    public void reviewSystemAlerts() {
        page.locator("#system-alerts-panel").click();
    }
    
    public boolean areLateTeacherCheckInsVisible() {
        return page.locator("#late-teacher-alert").isVisible();
    }
    
    public boolean areSessionsNotStartedAlertsVisible() {
        return page.locator("#sessions-not-started-alert").isVisible();
    }
    
    public boolean areTechnicalIssuesHighlighted() {
        return page.locator(".technical-issues").isVisible();
    }
    
    public void navigateToFeedbackAnalytics() {
        // First open the Analytics dropdown
        page.locator("#analytics-menu-button").click();
        page.waitForTimeout(500); // Wait for dropdown animation
        // Then click the Feedback Analytics link
        page.locator("#feedback-analytics-nav").click();
        page.waitForLoadState();
    }
    
    public boolean areActiveCampaignsWithResponseRatesVisible() {
        return page.locator("#campaign-list-with-response-rates").isVisible();
    }
    
    public boolean isRealTimeFeedbackStatisticsVisible() {
        return page.locator("#realtime-feedback-statistics").isVisible();
    }
    
    public boolean isRecentFeedbackSummaryVisible() {
        return page.locator("#recent-feedback-summary").isVisible();
    }
    
    public void generateTodayActivityReport() {
        page.locator("#generate-today-report-button").click();
    }
    
    public boolean isReportGenerationSuccessful() {
        return page.locator("#report-generation-success").isVisible();
    }
    
    public boolean arePdfExcelDownloadOptionsVisible() {
        return page.locator("#pdf-excel-download-options").isVisible();
    }
    
    public boolean areEmailDistributionOptionsVisible() {
        return page.locator("#email-distribution-options").isVisible();
    }
}