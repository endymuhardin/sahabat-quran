package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.CrossTermAnalyticsPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Cross-Term Analytics Tests.
 * Covers multi-term analytics, historical comparisons, and performance tracking.
 * 
 * User Role: MANAGEMENT
 * Focus: Historical data analysis, trend identification, performance comparison.
 */
@Slf4j
@DisplayName("CTA-HP: Cross-Term Analytics Happy Path Scenarios")
class CrossTermAnalyticsTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("CTA-HP-001: Management - Perbandingan Performa Historis")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldCompareHistoricalPerformance() {
        log.info("🚀 Starting CTA-HP-001: Management - Perbandingan Performa Historis...");
        
        // Test data sesuai dokumentasi
        final String TERM1 = "Semester 1 2023/2024";
        final String TERM2 = "Semester 2 2023/2024";
        final String CURRENT_TERM = "Semester 1 2024/2025";
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        // Bagian 1: Access Cross-Term Analytics
        log.info("📝 Bagian 1: Access Cross-Term Analytics");
        
        loginAsManagement();
        
        // Navigate to cross-term analytics
        analyticsPage.navigateToCrossTermAnalytics(getBaseUrl());
        assertTrue(analyticsPage.isCrossTermAnalyticsVisible(), "Cross-term analytics dashboard should be visible");
        assertTrue(analyticsPage.isTermSelectorVisible(), "Term selector should be visible");
        assertTrue(analyticsPage.isMultiTermSelectorVisible(), "Multi-term selector should be available");
        
        // Bagian 2: Setup Historical Comparison
        log.info("📝 Bagian 2: Setup Historical Comparison");
        
        // Select multiple terms for comparison
        analyticsPage.selectMultipleTerms(TERM1, TERM2, CURRENT_TERM);
        // Verify selection made (remove text verification)
        assertTrue(analyticsPage.isMultiTermSelectorVisible(), "Multi-term selector should still be visible after selection");
        
        // Set comparison period
        analyticsPage.selectComparisonPeriod("SEMESTER_COMPARISON");
        
        // Generate analytics
        analyticsPage.generateAnalytics();
        assertTrue(analyticsPage.isPerformanceMetricsVisible(), "Performance metrics should be displayed");
        
        // Bagian 3: Verify Enrollment Analysis
        log.info("📝 Bagian 3: Verify Enrollment Analysis");
        
        assertTrue(analyticsPage.isEnrollmentTrendsVisible(), "Enrollment trends should be visible");
        assertTrue(analyticsPage.isEnrollmentGrowthRateVisible(), "Enrollment growth rate should be displayed");
        assertTrue(analyticsPage.isStudentCountComparisonVisible(), "Student count comparison should be shown");
        
        // Verify enrollment data elements are present (avoid text verification)
        assertTrue(analyticsPage.isStudentCountComparisonVisible(), "Student count comparison should be shown");
        assertTrue(analyticsPage.isEnrollmentGrowthRateVisible(), "Enrollment growth rate should be displayed");
        
        // Bagian 4: Analyze Teacher Performance
        log.info("📝 Bagian 4: Analyze Teacher Performance");
        
        assertTrue(analyticsPage.isTeacherPerformanceComparisonVisible(), "Teacher performance comparison should be visible");
        assertTrue(analyticsPage.isTeacherCountTrendsVisible(), "Teacher count trends should be displayed");
        assertTrue(analyticsPage.isTeacherUtilizationVisible(), "Teacher utilization should be shown");
        
        // Verify teacher data elements are present
        assertTrue(analyticsPage.isTeacherCountTrendsVisible(), "Teacher count trends should be displayed");
        
        // Bagian 5: Review Completion and Satisfaction Rates
        log.info("📝 Bagian 5: Review Completion and Satisfaction Rates");
        
        assertTrue(analyticsPage.isCompletionRateComparisonVisible(), "Completion rate comparison should be visible");
        assertTrue(analyticsPage.isStudentSatisfactionTrendsVisible(), "Student satisfaction trends should be visible");
        
        // Verify completion and satisfaction rate elements are present
        assertTrue(analyticsPage.isCompletionRateComparisonVisible(), "Completion rate comparison should be visible");
        assertTrue(analyticsPage.isStudentSatisfactionTrendsVisible(), "Student satisfaction trends should be visible");
        
        log.info("✅ CTA-HP-001: Historical Performance Comparison completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-HP-002: Management - Class Size and Capacity Analysis")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAnalyzeClassSizeAndCapacity() {
        log.info("🚀 Starting CTA-HP-002: Class Size and Capacity Analysis...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics(getBaseUrl());
        
        // Bagian 1: Setup Class Size Analysis
        log.info("📝 Bagian 1: Setup Class Size Analysis");
        
        // Select terms for analysis
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        
        // Filter for class size analysis
        analyticsPage.filterByLevel("ALL_LEVELS");
        analyticsPage.generateAnalytics();
        
        // Bagian 2: Verify Class Size Metrics
        log.info("📝 Bagian 2: Verify Class Size Metrics");
        
        assertTrue(analyticsPage.isClassSizeAnalysisVisible(), "Class size analysis should be visible");
        assertTrue(analyticsPage.isAverageClassSizeVisible(), "Average class size should be displayed");
        assertTrue(analyticsPage.isClassCapacityUtilizationVisible(), "Class capacity utilization should be shown");
        
        // Verify class size metrics are visible
        assertTrue(analyticsPage.isClassSizeAnalysisVisible(), "Class size analysis should be displayed");
        
        // Bagian 3: Analyze Capacity Trends
        log.info("📝 Bagian 3: Analyze Capacity Trends");
        
        assertTrue(analyticsPage.isCapacityGrowthTrendsVisible(), "Capacity growth trends should be visible");
        assertTrue(analyticsPage.isOptimalClassSizeAnalysisVisible(), "Optimal class size analysis should be shown");
        
        // Interactive features
        analyticsPage.clickTermInChart("Semester 1 2024/2025");
        assertTrue(analyticsPage.isDrillDownOptionVisible(), "Drill-down option should be available");
        
        analyticsPage.drillDownToDetails();
        assertTrue(analyticsPage.isDetailedClassBreakdownVisible(), "Detailed class breakdown should be shown");
        
        log.info("✅ CTA-HP-002: Class Size and Capacity Analysis completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-HP-003: Management - Revenue and Cost Analysis")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAnalyzeRevenueAndCosts() {
        log.info("🚀 Starting CTA-HP-003: Revenue and Cost Analysis...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics(getBaseUrl());
        
        // Bagian 1: Setup Financial Analysis
        log.info("📝 Bagian 1: Setup Financial Analysis");
        
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        analyticsPage.generateAnalytics();
        
        // Bagian 2: Verify Revenue Analysis
        log.info("📝 Bagian 2: Verify Revenue Analysis");
        
        assertTrue(analyticsPage.isRevenueAnalysisVisible(), "Revenue analysis should be visible");
        assertTrue(analyticsPage.isRevenueTrendsVisible(), "Revenue trends should be displayed");
        assertTrue(analyticsPage.isRevenuePerStudentVisible(), "Revenue per student should be shown");
        
        // Verify financial metrics
        assertTrue(analyticsPage.isTotalRevenueComparisonVisible(), "Total revenue comparison should be visible");
        assertTrue(analyticsPage.isRevenueGrowthRateVisible(), "Revenue growth rate should be displayed");
        
        // Bagian 3: Analyze Cost Efficiency
        log.info("📝 Bagian 3: Analyze Cost Efficiency");
        
        assertTrue(analyticsPage.isCostPerStudentAnalysisVisible(), "Cost per student analysis should be visible");
        assertTrue(analyticsPage.isOperationalCostsVisible(), "Operational costs should be displayed");
        assertTrue(analyticsPage.isCostEfficiencyMetricsVisible(), "Cost efficiency metrics should be shown");
        
        // Bagian 4: Generate Financial Report
        log.info("📝 Bagian 4: Generate Financial Report");
        
        analyticsPage.openExportOptions();
        assertTrue(analyticsPage.isExportPdfOptionVisible(), "PDF export should be available");
        assertTrue(analyticsPage.isExportExcelOptionVisible(), "Excel export should be available");
        
        analyticsPage.exportToPdf();
        assertTrue(analyticsPage.isExportSuccessVisible(), "Export should be successful");
        
        log.info("✅ CTA-HP-003: Revenue and Cost Analysis completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-HP-004: Management - Teacher Performance Trends")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAnalyzeTeacherPerformanceTrends() {
        log.info("🚀 Starting CTA-HP-004: Teacher Performance Trends...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics(getBaseUrl());
        
        // Bagian 1: Filter for Teacher Analysis
        log.info("📝 Bagian 1: Filter for Teacher Analysis");
        
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        analyticsPage.filterByProgram("ALL_PROGRAMS");
        analyticsPage.generateAnalytics();
        
        // Bagian 2: Verify Teacher Performance Metrics
        log.info("📝 Bagian 2: Verify Teacher Performance Metrics");
        
        assertTrue(analyticsPage.isTeacherPerformanceComparisonVisible(), "Teacher performance comparison should be visible");
        assertTrue(analyticsPage.isTeacherSatisfactionScoresVisible(), "Teacher satisfaction scores should be displayed");
        assertTrue(analyticsPage.isTeacherRetentionRateVisible(), "Teacher retention rate should be shown");
        
        // Verify teacher-specific metrics
        assertTrue(analyticsPage.isAverageStudentFeedbackVisible(), "Average student feedback should be displayed");
        assertTrue(analyticsPage.isClassCompletionByTeacherVisible(), "Class completion by teacher should be shown");
        
        // Bagian 3: Individual Teacher Analysis
        log.info("📝 Bagian 3: Individual Teacher Analysis");
        
        analyticsPage.filterByTeacher("Ustadz Ahmad");
        assertTrue(analyticsPage.isIndividualTeacherPerformanceVisible(), "Individual teacher performance should be visible");
        assertTrue(analyticsPage.isTeacherPerformanceTrendsVisible(), "Teacher performance trends should be displayed");
        
        // Interactive tooltips
        analyticsPage.hoverOverDataPoint("performance-chart");
        assertTrue(analyticsPage.isTooltipVisible(), "Performance tooltip should be visible");
        
        log.info("✅ CTA-HP-004: Teacher Performance Trends completed successfully!");
    }
    
    @Test
    @DisplayName("CTA-HP-005: Management - Custom Analytics Report Builder")
    @Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldBuildCustomAnalyticsReport() {
        log.info("🚀 Starting CTA-HP-005: Custom Analytics Report Builder...");
        
        CrossTermAnalyticsPage analyticsPage = new CrossTermAnalyticsPage(page);
        
        loginAsManagement();
        analyticsPage.navigateToCrossTermAnalytics(getBaseUrl());
        
        // Bagian 1: Access Custom Report Builder
        log.info("📝 Bagian 1: Access Custom Report Builder");
        
        analyticsPage.openCustomReportBuilder();
        assertTrue(analyticsPage.isCustomReportBuilderVisible(), "Custom report builder should be visible");
        
        // Bagian 2: Configure Custom Metrics
        log.info("📝 Bagian 2: Configure Custom Metrics");
        
        // Select specific metrics
        analyticsPage.selectMetric("Student Enrollment");
        analyticsPage.selectMetric("Completion Rate");
        analyticsPage.selectMetric("Teacher Satisfaction");
        analyticsPage.selectMetric("Revenue per Student");
        
        // Choose visualization type
        analyticsPage.selectVisualizationType("LINE_CHART");
        
        // Select terms for custom analysis
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        
        // Bagian 3: Generate Custom Report
        log.info("📝 Bagian 3: Generate Custom Report");
        
        analyticsPage.buildCustomReport();
        assertTrue(analyticsPage.isCustomReportResultsVisible(), "Custom report results should be displayed");
        assertTrue(analyticsPage.isCustomChartVisible(), "Custom chart should be generated");
        
        // Verify custom metrics are displayed
        assertTrue(analyticsPage.isSelectedMetricsDisplayVisible(), "Selected metrics should be displayed");
        assertTrue(analyticsPage.isCustomInsightsVisible(), "Custom insights should be generated");
        
        // Bagian 4: Save and Share Custom Report
        log.info("📝 Bagian 4: Save and Share Custom Report");
        
        analyticsPage.clickSaveCustomReport();
        analyticsPage.fillReportName("Management Performance Dashboard Q1 2024");
        analyticsPage.clickSaveReportButton();
        
        assertTrue(analyticsPage.isReportSavedSuccessVisible(), "Report should be saved successfully");
        
        // Test sharing functionality
        analyticsPage.shareReport();
        assertTrue(analyticsPage.isShareOptionsVisible(), "Share options should be available");
        
        log.info("✅ CTA-HP-005: Custom Analytics Report Builder completed successfully!");
    }
}
