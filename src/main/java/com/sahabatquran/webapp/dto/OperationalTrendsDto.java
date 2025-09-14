package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationalTrendsDto {
    private GrowthMetrics growthMetrics;
    private CapacityAnalysis capacityAnalysis;
    private ResourceUtilization resourceUtilization;
    private EfficiencyMetrics efficiencyMetrics;
    private List<Bottleneck> identifiedBottlenecks;
    private List<OptimizationOpportunity> optimizationOpportunities;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthMetrics {
        private List<GrowthTrend> enrollmentGrowth;
        private List<GrowthTrend> facultyGrowth;
        private List<GrowthTrend> facilityGrowth;
        private BigDecimal overallGrowthRate;
        private String growthPattern; // LINEAR, EXPONENTIAL, STABLE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthTrend {
        private String termName;
        private Long value;
        private BigDecimal percentageChange;
        private String category;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapacityAnalysis {
        private Map<String, BigDecimal> classroomUtilization;
        private Map<String, BigDecimal> teacherUtilization;
        private BigDecimal currentCapacity;
        private BigDecimal projectedCapacityNeeds;
        private String expansionRecommendation;
        private List<CapacityMetric> detailedMetrics;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapacityMetric {
        private String resourceType;
        private BigDecimal currentUtilization;
        private BigDecimal optimalUtilization;
        private String status; // UNDER_UTILIZED, OPTIMAL, OVER_UTILIZED
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceUtilization {
        private BigDecimal averageClassSize;
        private BigDecimal teacherStudentRatio;
        private BigDecimal facilityUtilizationRate;
        private Map<String, BigDecimal> resourceAllocation;
        private List<ResourceTrend> utilizationTrends;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceTrend {
        private String termName;
        private String resourceType;
        private BigDecimal utilizationRate;
        private BigDecimal efficiency;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EfficiencyMetrics {
        private BigDecimal operationalEfficiency;
        private BigDecimal costPerStudent;
        private BigDecimal revenuePerStudent;
        private BigDecimal profitMargin;
        private Map<String, BigDecimal> efficiencyByDepartment;
        private List<EfficiencyTrend> trends;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EfficiencyTrend {
        private String termName;
        private BigDecimal efficiency;
        private BigDecimal costPerStudent;
        private String trend;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bottleneck {
        private String area;
        private String description;
        private BigDecimal impactScore;
        private String severity; // HIGH, MEDIUM, LOW
        private List<String> recommendations;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizationOpportunity {
        private String area;
        private String opportunity;
        private BigDecimal potentialSavings;
        private BigDecimal implementationCost;
        private String priority;
        private String timeframe;
    }
}