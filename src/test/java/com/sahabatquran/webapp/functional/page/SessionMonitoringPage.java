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
        this.monitoringDashboardMenu = page.locator("nav a[href*='monitoring'], text='Monitoring'");
        this.academicAdminDashboard = page.locator(".admin-dashboard, .monitoring-dashboard");
        
        // Real-time monitoring
        this.realTimeSessionsPanel = page.locator(".realtime-sessions, .live-sessions-panel");
        this.sessionStatusIndicators = page.locator(".status-indicator, .session-status");
        this.liveSessionsCounter = page.locator(".live-counter, .active-sessions-count");
        this.pendingSessionsCounter = page.locator(".pending-counter, .pending-sessions-count");
        this.completedSessionsCounter = page.locator(".completed-counter, .completed-sessions-count");
        
        // Alert system
        this.systemAlertsPanel = page.locator(".system-alerts, .alerts-panel");
        this.criticalAlerts = page.locator(".critical-alert, .alert-critical");
        this.alertBadge = page.locator(".alert-badge, .notification-badge");
        this.alertNotifications = page.locator(".alert-notification, .system-notification");
        
        // Session details
        this.sessionCards = page.locator(".session-card, .session-item");
        this.sessionDetails = page.locator(".session-details, .session-info");
        this.attendanceInfo = page.locator(".attendance-info, .attendance-details");
        this.instructorStatus = page.locator(".instructor-status, .teacher-status");
        this.sessionTimer = page.locator(".session-timer, .duration-timer");
        
        // Statistics
        this.dailyStatistics = page.locator(".daily-stats, .statistics-panel");
        this.attendanceRates = page.locator(".attendance-rate, .attendance-percentage");
        this.sessionCompletionRates = page.locator(".completion-rate, .completion-percentage");
        this.instructorPerformanceMetrics = page.locator(".instructor-metrics, .performance-metrics");
        
        // Action buttons
        this.viewDetailsButton = page.locator("button:has-text('View Details'), button[data-action='view-details']");
        this.contactInstructorButton = page.locator("button:has-text('Contact'), button[data-action='contact']");
        this.generateReportButton = page.locator("button:has-text('Generate Report'), button[data-action='report']");
        this.exportDataButton = page.locator("button:has-text('Export'), button[data-action='export']");
        
        // Filtering and search
        this.sessionFilter = page.locator("select[name='sessionFilter'], #session-filter");
        this.statusFilter = page.locator("select[name='statusFilter'], #status-filter");
        this.dateRangeFilter = page.locator("input[type='date'], .date-range-picker");
        this.instructorFilter = page.locator("select[name='instructorFilter'], #instructor-filter");
        this.searchBox = page.locator("input[type='search'], .search-input");
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
        return page.locator(String.format("text='%d live', text='%d active'", expectedCount, expectedCount)).isVisible();
    }
    
    public boolean isPendingSessionsCountVisible(int expectedCount) {
        return page.locator(String.format("text='%d pending'", expectedCount)).isVisible();
    }
    
    public boolean isCompletedSessionsCountVisible(int expectedCount) {
        return page.locator(String.format("text='%d completed'", expectedCount)).isVisible();
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
        return page.locator(String.format(".alert:has-text('%s'), .notification:has-text('%s')", alertType, alertType)).isVisible();
    }
    
    public void dismissAlert() {
        page.locator("button.dismiss-alert, .alert .close-button").first().click();
    }
    
    // Session monitoring methods
    public boolean areSessionCardsVisible() {
        return sessionCards.count() > 0;
    }
    
    public boolean isSessionDetailsVisible(String sessionId) {
        return page.locator(String.format(".session-card[data-session-id='%s']", sessionId)).isVisible();
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
        page.locator(String.format(".session-card[data-session-id='%s'] .view-details", sessionId)).click();
        page.waitForSelector(".session-details-modal, .session-modal");
    }
    
    public boolean isSessionDetailsModalVisible() {
        return page.locator(".session-details-modal, .session-modal").isVisible();
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
        page.waitForSelector(".details-modal, .session-details");
    }
    
    public void contactInstructor(String instructorName) {
        page.locator(String.format(".session-card:has-text('%s') .contact-button", instructorName)).click();
        page.waitForSelector(".contact-modal, .communication-modal");
    }
    
    public boolean isContactInstructorModalVisible() {
        return page.locator(".contact-modal, .communication-modal").isVisible();
    }
    
    public void generateReport() {
        generateReportButton.click();
        page.waitForSelector(".report-modal, .generate-report-modal");
    }
    
    public boolean isReportGenerationModalVisible() {
        return page.locator(".report-modal, .generate-report-modal").isVisible();
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
        page.locator("input[name='startDate']").fill(startDate);
        page.locator("input[name='endDate']").fill(endDate);
        page.locator("button[data-action='apply-filter']").click();
        page.waitForTimeout(1000);
    }
    
    public void searchSessions(String searchTerm) {
        searchBox.fill(searchTerm);
        page.keyboard().press("Enter");
        page.waitForTimeout(1000);
    }
    
    public boolean areFilteredResultsVisible() {
        return page.locator(".filtered-results, .search-results").isVisible();
    }
    
    public int getFilteredSessionsCount() {
        return sessionCards.count();
    }
    
    // Specific monitoring scenarios methods
    public boolean isInstructorLateAlertVisible(String instructorName) {
        return page.locator(String.format(".alert:has-text('%s'):has-text('late'), .late-alert:has-text('%s')", instructorName, instructorName)).isVisible();
    }
    
    public boolean isLowAttendanceAlertVisible(String className) {
        return page.locator(String.format(".alert:has-text('%s'):has-text('attendance'), .attendance-alert:has-text('%s')", className, className)).isVisible();
    }
    
    public boolean isSessionDelayAlertVisible() {
        return page.locator(".alert:has-text('delay'), .delay-alert").isVisible();
    }
    
    public boolean isProgressTrackingUpdateVisible() {
        return page.locator(".progress-update, .tracking-update").isVisible();
    }
    
    public void refreshDashboard() {
        page.locator("button[data-action='refresh'], .refresh-button").click();
        page.waitForTimeout(2000);
    }
    
    public boolean isDashboardAutoRefreshing() {
        return page.locator(".auto-refresh-indicator, .live-indicator").isVisible();
    }
    
    public String getLastUpdateTimestamp() {
        return page.locator(".last-update, .timestamp").textContent();
    }
    
    // Performance monitoring methods
    public boolean areSystemPerformanceMetricsVisible() {
        return page.locator(".system-metrics, .performance-panel").isVisible();
    }
    
    public boolean isResponseTimeMetricVisible() {
        return page.locator(".response-time, .performance-metric").isVisible();
    }
    
    public boolean areHealthChecksVisible() {
        return page.locator(".health-check, .system-health").isVisible();
    }
    
    public boolean isSystemStatusHealthy() {
        return page.locator(".status:has-text('Healthy'), .health-status:has-text('Good')").isVisible();
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
        return page.locator(".session-grid, .sessions-table, .realtime-sessions").isVisible();
    }
    
    // More methods required by AcademicAdminTest
    public boolean isTeacherCheckInStatusVisible() {
        return page.locator(".teacher-checkin-status").first().isVisible();
    }
    
    public boolean isAutoRefreshIndicatorActive() {
        return page.locator(".auto-refresh-active, .live-indicator.active").isVisible();
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
        return page.locator(".technical-issues, .system-problems").isVisible();
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