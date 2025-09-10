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
        analyticsPage.navigateToCrossTermAnalytics();
        assertTrue(analyticsPage.isCrossTermAnalyticsVisible(), "Cross-term analytics dashboard should be visible");
        assertTrue(analyticsPage.isTermSelectorVisible(), "Term selector should be visible");
        assertTrue(analyticsPage.isMultiTermSelectorVisible(), "Multi-term selector should be available");
        
        // Bagian 2: Setup Historical Comparison
        log.info("📝 Bagian 2: Setup Historical Comparison");
        
        // Select multiple terms for comparison
        analyticsPage.selectMultipleTerms(TERM1, TERM2, CURRENT_TERM);
        assertTrue(page.locator("#selected-terms").textContent().contains("3 terms"), "Three terms should be selected");
        
        // Set comparison period
        analyticsPage.selectComparisonPeriod("SEMESTER_COMPARISON");
        
        // Generate analytics
        analyticsPage.generateAnalytics();
        assertTrue(analyticsPage.isPerformanceMetricsVisible(), "Performance metrics should be displayed");
        
        // Bagian 3: Verify Enrollment Analysis
        log.info("📝 Bagian 3: Verify Enrollment Analysis");
        
        assertTrue(analyticsPage.isEnrollmentTrendsVisible(), "Enrollment trends should be visible");
        assertTrue(page.locator("#enrollment-growth-rate").isVisible(), "Enrollment growth rate should be displayed");
        assertTrue(page.locator("#student-count-comparison").isVisible(), "Student count comparison should be shown");
        
        // Verify specific enrollment data
        assertTrue(page.locator("text='120 students'").isVisible(), "Term 1 enrollment (120) should be displayed");
        assertTrue(page.locator("text='135 students'").isVisible(), "Term 2 enrollment (135) should be displayed");
        assertTrue(page.locator("text='150 students'").isVisible(), "Current term enrollment (150) should be displayed");
        assertTrue(page.locator("text='+12.5%'").isVisible(), "Growth rate should be displayed");
        
        // Bagian 4: Analyze Teacher Performance
        log.info("📝 Bagian 4: Analyze Teacher Performance");
        
        assertTrue(analyticsPage.isTeacherPerformanceComparisonVisible(), "Teacher performance comparison should be visible");
        assertTrue(page.locator("#teacher-count-trends").isVisible(), "Teacher count trends should be displayed");
        assertTrue(page.locator("#teacher-utilization").isVisible(), "Teacher utilization should be shown");
        
        // Verify teacher data
        assertTrue(page.locator("text='18 teachers'").isVisible(), "Term 1 teacher count should be displayed");
        assertTrue(page.locator("text='20 teachers'").isVisible(), "Term 2 teacher count should be displayed");
        assertTrue(page.locator("text='22 teachers'").isVisible(), "Current term teacher count should be displayed");
        
        // Bagian 5: Review Completion and Satisfaction Rates
        log.info("📝 Bagian 5: Review Completion and Satisfaction Rates");
        
        assertTrue(analyticsPage.isCompletionRateComparisonVisible(), "Completion rate comparison should be visible");
        assertTrue(analyticsPage.isStudentSatisfactionTrendsVisible(), "Student satisfaction trends should be visible");
        
        // Verify completion rates
        assertTrue(page.locator("text='85%'").isVisible(), "Term 1 completion rate should be displayed");
        assertTrue(page.locator("text='89%'").isVisible(), "Term 2 completion rate should be displayed");
        assertTrue(page.locator("text='+4%'").isVisible(), "Completion rate improvement should be shown");
        
        // Verify satisfaction scores
        assertTrue(page.locator("text='4.2/5.0'").isVisible(), "Term 1 satisfaction should be displayed");
        assertTrue(page.locator("text='4.4/5.0'").isVisible(), "Term 2 satisfaction should be displayed");
        assertTrue(page.locator("text='4.3/5.0'").isVisible(), "Current satisfaction should be displayed");
        
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
        analyticsPage.navigateToCrossTermAnalytics();
        
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
        assertTrue(page.locator("#average-class-size").isVisible(), "Average class size should be displayed");
        assertTrue(page.locator("#class-capacity-utilization").isVisible(), "Class capacity utilization should be shown");
        
        // Verify specific metrics
        assertTrue(page.locator("text='22 classes'").isVisible(), "Term 1 class count should be displayed");
        assertTrue(page.locator("text='25 classes'").isVisible(), "Term 2 class count should be displayed");
        assertTrue(page.locator("text='28 classes'").isVisible(), "Current term class count should be displayed");
        
        // Bagian 3: Analyze Capacity Trends
        log.info("📝 Bagian 3: Analyze Capacity Trends");
        
        assertTrue(page.locator("#capacity-growth-trends").isVisible(), "Capacity growth trends should be visible");
        assertTrue(page.locator("#optimal-class-size-analysis").isVisible(), "Optimal class size analysis should be shown");
        
        // Interactive features
        analyticsPage.clickTermInChart("Semester 1 2024/2025");
        assertTrue(analyticsPage.isDrillDownOptionVisible(), "Drill-down option should be available");
        
        analyticsPage.drillDownToDetails();
        assertTrue(page.locator("#detailed-class-breakdown").isVisible(), "Detailed class breakdown should be shown");
        
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
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Bagian 1: Setup Financial Analysis
        log.info("📝 Bagian 1: Setup Financial Analysis");
        
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        analyticsPage.generateAnalytics();
        
        // Bagian 2: Verify Revenue Analysis
        log.info("📝 Bagian 2: Verify Revenue Analysis");
        
        assertTrue(analyticsPage.isRevenueAnalysisVisible(), "Revenue analysis should be visible");
        assertTrue(page.locator("#revenue-trends").isVisible(), "Revenue trends should be displayed");
        assertTrue(page.locator("#revenue-per-student").isVisible(), "Revenue per student should be shown");
        
        // Verify financial metrics
        assertTrue(page.locator("#total-revenue-comparison").isVisible(), "Total revenue comparison should be visible");
        assertTrue(page.locator("#revenue-growth-rate").isVisible(), "Revenue growth rate should be displayed");
        
        // Bagian 3: Analyze Cost Efficiency
        log.info("📝 Bagian 3: Analyze Cost Efficiency");
        
        assertTrue(analyticsPage.isCostPerStudentAnalysisVisible(), "Cost per student analysis should be visible");
        assertTrue(page.locator("#operational-costs").isVisible(), "Operational costs should be displayed");
        assertTrue(page.locator("#cost-efficiency-metrics").isVisible(), "Cost efficiency metrics should be shown");
        
        // Bagian 4: Generate Financial Report
        log.info("📝 Bagian 4: Generate Financial Report");
        
        analyticsPage.openExportOptions();
        assertTrue(analyticsPage.isExportPdfOptionVisible(), "PDF export should be available");
        assertTrue(analyticsPage.isExportExcelOptionVisible(), "Excel export should be available");
        
        analyticsPage.exportToPdf();
        assertTrue(page.locator("#export-success").isVisible(), "Export should be successful");
        
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
        analyticsPage.navigateToCrossTermAnalytics();
        
        // Bagian 1: Filter for Teacher Analysis
        log.info("📝 Bagian 1: Filter for Teacher Analysis");
        
        analyticsPage.selectMultipleTerms("Semester 1 2023/2024", "Semester 2 2023/2024", "Semester 1 2024/2025");
        analyticsPage.filterByProgram("ALL_PROGRAMS");
        analyticsPage.generateAnalytics();
        
        // Bagian 2: Verify Teacher Performance Metrics
        log.info("📝 Bagian 2: Verify Teacher Performance Metrics");
        
        assertTrue(analyticsPage.isTeacherPerformanceComparisonVisible(), "Teacher performance comparison should be visible");
        assertTrue(page.locator("#teacher-satisfaction-scores").isVisible(), "Teacher satisfaction scores should be displayed");
        assertTrue(page.locator("#teacher-retention-rate").isVisible(), "Teacher retention rate should be shown");
        
        // Verify teacher-specific metrics
        assertTrue(page.locator("#average-student-feedback").isVisible(), "Average student feedback should be displayed");
        assertTrue(page.locator("#class-completion-by-teacher").isVisible(), "Class completion by teacher should be shown");
        
        // Bagian 3: Individual Teacher Analysis
        log.info("📝 Bagian 3: Individual Teacher Analysis");
        
        analyticsPage.filterByTeacher("Ustadz Ahmad");
        assertTrue(page.locator("#individual-teacher-performance").isVisible(), "Individual teacher performance should be visible");
        assertTrue(page.locator("#teacher-performance-trends").isVisible(), "Teacher performance trends should be displayed");
        
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
        analyticsPage.navigateToCrossTermAnalytics();
        
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
        assertTrue(page.locator("#custom-report-results").isVisible(), "Custom report results should be displayed");
        assertTrue(page.locator("#custom-chart").isVisible(), "Custom chart should be generated");
        
        // Verify custom metrics are displayed
        assertTrue(page.locator("#selected-metrics-display").isVisible(), "Selected metrics should be displayed");
        assertTrue(page.locator("#custom-insights").isVisible(), "Custom insights should be generated");
        
        // Bagian 4: Save and Share Custom Report
        log.info("📝 Bagian 4: Save and Share Custom Report");
        
        page.locator("#save-custom-report").click();
        page.locator("#report-name").fill("Management Performance Dashboard Q1 2024");
        page.locator("#btn-save-report").click();
        
        assertTrue(page.locator("#report-saved-success").isVisible(), "Report should be saved successfully");
        
        // Test sharing functionality
        analyticsPage.shareReport();
        assertTrue(page.locator("#share-options").isVisible(), "Share options should be available");
        
        log.info("✅ CTA-HP-005: Custom Analytics Report Builder completed successfully!");
    }
}
