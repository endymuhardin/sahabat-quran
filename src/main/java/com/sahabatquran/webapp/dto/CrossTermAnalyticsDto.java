package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossTermAnalyticsDto {
    private List<TermInfo> selectedTerms;
    private List<TermInfo> availableTerms;
    private EnrollmentAnalytics enrollmentAnalytics;
    private TeacherAnalytics teacherAnalytics;
    private PerformanceMetrics performanceMetrics;
    private FinancialAnalytics financialAnalytics;
    private OperationalAnalytics operationalAnalytics;
    private Map<String, Object> customMetrics;
    private DataValidationDto dataValidation;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermInfo {
        private UUID termId;
        private String termName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentAnalytics {
        private List<EnrollmentTrend> trends;
        private BigDecimal growthRate;
        private Map<String, Long> studentCountByTerm;
        private Map<String, Long> studentCountByLevel;
        private Long totalStudents;
        private Long activeStudents;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentTrend {
        private String termName;
        private Long studentCount;
        private BigDecimal percentageChange;
        private String trend; // INCREASING, DECREASING, STABLE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherAnalytics {
        private List<TeacherTrend> trends;
        private Map<String, Long> teacherCountByTerm;
        private BigDecimal averageUtilization;
        private BigDecimal retentionRate;
        private BigDecimal averageSatisfactionScore;
        private Long totalTeachers;
        private Long activeTeachers;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherTrend {
        private String termName;
        private Long teacherCount;
        private BigDecimal averagePerformance;
        private BigDecimal utilizationRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        private BigDecimal averageCompletionRate;
        private BigDecimal averageSatisfactionScore;
        private BigDecimal averageStudentFeedback;
        private Map<String, BigDecimal> completionRateByTerm;
        private Map<String, BigDecimal> satisfactionByTerm;
        private List<PerformanceTrend> trends;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceTrend {
        private String termName;
        private BigDecimal completionRate;
        private BigDecimal satisfactionScore;
        private String trend;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialAnalytics {
        private Map<String, BigDecimal> revenueByTerm;
        private Map<String, BigDecimal> costByTerm;
        private BigDecimal totalRevenue;
        private BigDecimal totalCost;
        private BigDecimal averageRevenuePerStudent;
        private BigDecimal averageCostPerStudent;
        private BigDecimal revenueGrowthRate;
        private BigDecimal totalOperationalCosts;
        private BigDecimal costEfficiency;
        private List<FinancialTrend> trends;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialTrend {
        private String termName;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profitMargin;
        private BigDecimal revenuePerStudent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationalAnalytics {
        private BigDecimal averageClassSize;
        private BigDecimal capacityUtilization;
        private BigDecimal capacityGrowthRate;
        private String optimalClassSizeRange;
        private List<ClassBreakdown> classBreakdown;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassBreakdown {
        private String className;
        private String levelName;
        private Long studentCount;
        private String teacherName;
    }
}