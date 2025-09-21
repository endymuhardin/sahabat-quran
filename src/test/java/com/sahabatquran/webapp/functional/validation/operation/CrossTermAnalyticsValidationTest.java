package com.sahabatquran.webapp.functional.validation.operation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.CrossTermAnalyticsPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Cross-Term Analytics Validation Tests for Alternate Path scenarios.
 * Tests error handling, access control, and data limitation scenarios.
 *
 * User Role: MANAGEMENT
 * Focus: Validation and error scenarios during analytics generation.
 *
 * The cross-term analytics feature is implemented at /management/analytics/cross-term
 */
@Slf4j
@DisplayName("CTA-AP: Cross-Term Analytics Validation Alternate Path Scenarios")
class CrossTermAnalyticsValidationTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("CTA-AP-001: Management - Data Akses Terbatas dan Error Handling")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleLimitedDataAccessAndErrors() {
        log.info("🚀 Starting CTA-AP-001: Data Akses Terbatas dan Error Handling...");
        
        // Test data sesuai dokumentasi - using actual term names from test SQL
        final String PARTIAL_TERM = "Semester 1 2023/2024"; // Term with partial data
        final String RESTRICTED_TERM = "Semester 2 2023/2024"; // Term with access restrictions
        final String ACTIVE_TERM = "Semester 1 2024/2025"; // Active term with incomplete data
        final String ARCHIVED_TERM = "Intensive 2023/2024"; // Archived term
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        
        // Bagian 1: Akses Term dengan Data Tidak Lengkap
        log.info("📝 Bagian 1: Akses Term dengan Data Tidak Lengkap");
        
        analyticsPage.navigateToCrossTermAnalytics();
        assertTrue(analyticsPage.isCrossTermAnalyticsVisible(), "Analytics dashboard should be accessible");
        
        // Select term with incomplete data
        analyticsPage.selectMultipleTerms(PARTIAL_TERM, ACTIVE_TERM);
        analyticsPage.generateAnalytics();
        
        // Verify missing data warnings
        assertTrue(analyticsPage.isMissingDataWarningVisible(), "Missing data warning should be displayed");
        assertTrue(analyticsPage.isDataLimitationsNoticeVisible(), "Data limitations notice should be shown");
        assertTrue(analyticsPage.isPartialDataDisclaimerVisible(), "Partial data disclaimer should be visible");
        
        // Check specific data issues
        assertTrue(analyticsPage.isMissingTeacherDataVisible(), "Missing teacher data should be flagged");
        assertTrue(analyticsPage.isIncompleteStudentRecordsVisible(), "Incomplete student records should be noted");
        assertTrue(analyticsPage.isMissingFinancialDataVisible(), "Missing financial data should be indicated");
        
        // Bagian 2: Handle Restricted Access Terms
        log.info("📝 Bagian 2: Handle Restricted Access Terms");
        
        // Attempt to access restricted term
        analyticsPage.selectSingleTerm(RESTRICTED_TERM);
        analyticsPage.generateAnalytics();
        
        // Should show access restrictions
        assertTrue(analyticsPage.isAccessRestrictedWarningVisible(), "Access restricted warning should be displayed");
        assertTrue(analyticsPage.isPrivacyLimitationsVisible(), "Privacy limitations should be explained");
        assertTrue(analyticsPage.isDataAccessLevelVisible(), "Data access level should be indicated");
        
        // Bagian 3: Archived Term Access
        log.info("📝 Bagian 3: Archived Term Access");
        
        // Try to access archived term
        analyticsPage.selectSingleTerm(ARCHIVED_TERM);
        analyticsPage.generateAnalytics();
        
        // Should indicate archived status
        assertTrue(analyticsPage.isArchivedTermNoticeVisible(), "Archived term notice should be displayed");
        assertFalse(analyticsPage.isDetailedAnalyticsVisible(), "Detailed analytics should not be available for archived terms");
        
        // Check if summary data is available
        if (analyticsPage.isSummaryDataOnlyVisible()) {
            assertTrue(analyticsPage.isLimitedViewNoticeVisible(), "Limited view notice should be shown");
        }
        
        // Bagian 4: Partial Analysis Processing
        log.info("📝 Bagian 4: Partial Analysis Processing");
        
        // Select mix of available and problematic terms
        analyticsPage.selectMultipleTerms(PARTIAL_TERM, ACTIVE_TERM);
        analyticsPage.generateAnalytics();
        
        // Verify partial analysis
        assertTrue(analyticsPage.isPartialAnalysisNoticeVisible(), "Partial analysis notice should be visible");
        assertTrue(analyticsPage.isDataQualityScoreVisible(), "Data quality score should be displayed");
        assertTrue(analyticsPage.isConfidenceLevelVisible(), "Confidence level should be indicated");
        
        log.info("✅ CTA-AP-001: Limited Data Access and Error Handling completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-AP-002: Management - Insufficient Historical Data")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleInsufficientHistoricalData() {
        log.info("🚀 Starting CTA-AP-002: Insufficient Historical Data...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Bagian 1: Attempt Analysis with Single Term
        log.info("📝 Bagian 1: Attempt Analysis with Single Term");
        
        // Select only one term (insufficient for comparison)
        analyticsPage.selectSingleTerm("Semester 1 2024/2025");
        analyticsPage.generateAnalytics();

        // Validation working - single term selected triggers insufficient data warnings

        // Should warn about insufficient data for trends
        assertTrue(analyticsPage.isInsufficientDataWarningVisible(), "Insufficient data warning should be displayed");
        assertTrue(analyticsPage.isTrendAnalysisUnavailableVisible(), "Trend analysis unavailable notice should be shown");
        assertTrue(analyticsPage.isComparisonNotPossibleVisible(), "Comparison not possible message should be displayed");
        
        // Should offer alternative analysis options
        assertTrue(analyticsPage.isSingleTermAnalysisOptionVisible(), "Single term analysis option should be available");
        assertTrue(analyticsPage.isAddMoreTermsSuggestionVisible(), "Add more terms suggestion should be shown");
        
        // Bagian 2: Test with Very Recent Data Only
        log.info("📝 Bagian 2: Test with Very Recent Data Only");
        
        // Select only recent terms with limited historical depth
        analyticsPage.selectMultipleTerms("Semester 1 2024/2025");
        analyticsPage.setDateRange("2024-01-01", "2024-06-30");
        analyticsPage.generateAnalytics();
        
        // Should indicate limited historical context
        assertTrue(analyticsPage.isLimitedHistoricalContextVisible(), "Limited historical context notice should be displayed");
        assertTrue(analyticsPage.isShortTermTrendsOnlyVisible(), "Short-term trends only notice should be shown");
        
        log.info("✅ CTA-AP-002: Insufficient Historical Data handling completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-AP-003: Management - System Performance and Timeout")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleSystemPerformanceIssues() {
        log.info("🚀 Starting CTA-AP-003: System Performance and Timeout...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Bagian 1: Large Dataset Processing
        log.info("📝 Bagian 1: Large Dataset Processing");
        
        // Select multiple terms for complex analysis
        analyticsPage.selectMultipleTerms(
            "Semester 1 2023/2024", 
            "Semester 2 2023/2024", 
            "Semester 1 2024/2025"
        );
        
        // Filter for all data to create large dataset
        analyticsPage.filterByLevel("ALL_LEVELS");
        analyticsPage.filterByProgram("ALL_PROGRAMS");
        
        // Attempt complex analytics generation
        analyticsPage.generateAnalytics();
        
        // Check for performance indicators
        if (analyticsPage.isProcessingIndicatorVisible()) {
            assertTrue(analyticsPage.isProcessingProgressVisible(), "Processing progress should be shown");
            assertTrue(analyticsPage.isEstimatedTimeVisible(), "Estimated time should be displayed");
        }
        
        // Bagian 2: Handle Potential Timeout
        log.info("📝 Bagian 2: Handle Potential Timeout");
        
        // Wait for processing or timeout
        page.waitForTimeout(10000); // Allow time for processing
        
        if (analyticsPage.isTimeoutErrorVisible()) {
            // Handle timeout scenario
            assertTrue(analyticsPage.isRetryOptionVisible(), "Retry option should be available");
            assertTrue(analyticsPage.isReduceScopeSuggestionVisible(), "Reduce scope suggestion should be shown");
            
            // Test retry functionality
            analyticsPage.retryAnalytics();
            assertTrue(analyticsPage.isRetryInProgressVisible(), "Retry should be in progress");
        } else {
            // Processing completed successfully
            assertTrue(analyticsPage.isPerformanceMetricsVisible(), "Performance metrics should be displayed");
        }
        
        // Bagian 3: Performance Optimization Suggestions
        log.info("📝 Bagian 3: Performance Optimization Suggestions");
        
        if (analyticsPage.isPerformanceSuggestionsVisible()) {
            assertTrue(analyticsPage.isOptimizeFiltersVisible(), "Optimize filters suggestion should be available");
            assertTrue(analyticsPage.isReduceDateRangeVisible(), "Reduce date range suggestion should be shown");
            assertTrue(analyticsPage.isScheduleAnalysisVisible(), "Schedule analysis option should be available");
        }
        
        log.info("✅ CTA-AP-003: System Performance and Timeout handling completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-AP-004: Management - Role-Based Analytics Access Control")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldEnforceRoleBasedAnalyticsAccess() {
        log.info("🚀 Starting CTA-AP-004: Role-Based Analytics Access Control...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        // Bagian 1: Test Academic Admin Access
        log.info("📝 Bagian 1: Test Academic Admin Access");
        
        // Login as Academic Admin (limited access)
        loginAsAdmin();
        
        // Try to access cross-term analytics
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Should have limited access or be denied
        if (analyticsPage.isAccessDeniedVisible()) {
            assertTrue(analyticsPage.isInsufficientPermissionsMessageVisible(), "Insufficient permissions message should be shown");
            assertTrue(analyticsPage.isContactManagementVisible(), "Contact management option should be available");
        } else {
            // Limited access granted
            assertTrue(analyticsPage.isLimitedAccessNoticeVisible(), "Limited access notice should be displayed");
            assertFalse(analyticsPage.isFinancialAnalyticsVisible(), "Financial analytics should not be accessible");
            assertFalse(analyticsPage.isTeacherPerformanceDetailsVisible(), "Detailed teacher performance should not be accessible");
        }
        
        // Bagian 2: Test Instructor Access
        log.info("📝 Bagian 2: Test Instructor Access");
        
        // Logout and login as Instructor
        analyticsPage.clickLogout();
        loginAsInstructor();
        
        // Try to access analytics
        page.navigate(page.url().replace("/dashboard", "/management/analytics/cross-term"));

        // Wait for page to load completely
        page.waitForLoadState();

        // Debug: Log the current URL and page title for diagnosis
        String currentUrl = page.url();
        String pageTitle = page.title();
        log.info("Instructor access attempt - URL: {}, Title: {}", currentUrl, pageTitle);

        // Should be denied access
        assertTrue(analyticsPage.isAccessDeniedVisible() ||
                  page.url().contains("error") ||
                  page.url().contains("unauthorized") ||
                  page.url().contains("403") ||
                  pageTitle.contains("Akses Ditolak") ||
                  pageTitle.contains("Access Denied"),
                  "Instructor should be denied access to cross-term analytics. Current URL: " + currentUrl + ", Title: " + pageTitle);
        
        // Bagian 3: Verify Management Full Access
        log.info("📝 Bagian 3: Verify Management Full Access");
        
        // Logout and login as Management
        if (analyticsPage.isLogoutVisible()) {
            analyticsPage.clickLogout();
        }
        loginAsManagement();
        
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Should have full access
        assertTrue(analyticsPage.isCrossTermAnalyticsVisible(), "Management should have full access");
        assertTrue(analyticsPage.isFinancialAnalyticsVisible(), "Financial analytics should be accessible");
        assertTrue(analyticsPage.isTeacherPerformanceAnalyticsVisible(), "Teacher performance analytics should be accessible");
        assertTrue(analyticsPage.isRevenueAnalysisVisible(), "Revenue analysis should be accessible");
        
        log.info("✅ CTA-AP-004: Role-Based Analytics Access Control completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-AP-005: Management - Data Export and Sharing Failures")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleDataExportAndSharingFailures() {
        log.info("🚀 Starting CTA-AP-005: Data Export and Sharing Failures...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Generate analytics first
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024");
        analyticsPage.generateAnalytics();
        
        // Bagian 1: Test Export Failures
        log.info("📝 Bagian 1: Test Export Failures");
        
        analyticsPage.openExportOptions();
        
        // Try PDF export
        analyticsPage.exportToPdf();
        
        // Check for potential export issues
        if (analyticsPage.isExportFailedVisible()) {
            assertTrue(analyticsPage.isExportErrorMessageVisible(), "Export error message should be displayed");
            assertTrue(analyticsPage.isRetryExportVisible(), "Retry export option should be available");
            assertTrue(analyticsPage.isAlternativeFormatVisible(), "Alternative format option should be available");

            // Try alternative format
            analyticsPage.clickAlternativeFormat();
            analyticsPage.exportToExcel();
        }
        
        // Bagian 2: Test Large Dataset Export
        log.info("📝 Bagian 2: Test Large Dataset Export");
        
        // Generate large dataset
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        analyticsPage.filterByLevel("ALL_LEVELS");
        analyticsPage.generateAnalytics();
        
        // Try to export large dataset
        analyticsPage.openExportOptions();
        analyticsPage.exportToPdf();
        
        // Check for size warnings
        if (analyticsPage.isLargeExportWarningVisible()) {
            assertTrue(analyticsPage.isExportSizeLimitVisible(), "Export size limit should be indicated");
            assertTrue(analyticsPage.isReduceScopeOptionVisible(), "Reduce scope option should be available");
            assertTrue(analyticsPage.isSplitExportOptionVisible(), "Split export option should be available");
        }
        
        // Bagian 3: Test Sharing Restrictions
        log.info("📝 Bagian 3: Test Sharing Restrictions");
        
        if (analyticsPage.isShareReportOptionVisible()) {
            analyticsPage.shareReport();
            
            // Check for sharing restrictions
            if (analyticsPage.isSharingRestrictionsVisible()) {
                assertTrue(analyticsPage.isDataPrivacyNoticeVisible(), "Data privacy notice should be shown");
                assertTrue(analyticsPage.isAuthorizedRecipientsOnlyVisible(), "Authorized recipients only notice should be displayed");
            }
        }
        
        log.info("✅ CTA-AP-005: Data Export and Sharing Failures completed successfully!");
    }
}
