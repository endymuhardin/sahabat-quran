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
public class CrossTermComparisonDto {
    private String comparisonPeriod;
    private List<String> comparedTerms;
    private ComparisonMetrics enrollmentComparison;
    private ComparisonMetrics performanceComparison;
    private ComparisonMetrics financialComparison;
    private ComparisonMetrics operationalComparison;
    private List<KeyInsight> keyInsights;
    private Map<String, Object> drillDownOptions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonMetrics {
        private String metricName;
        private Map<String, BigDecimal> valuesByTerm;
        private BigDecimal percentageChange;
        private String trend;
        private String analysis;
        private List<DataPoint> dataPoints;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private String label;
        private BigDecimal value;
        private String termName;
        private String color;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyInsight {
        private String category;
        private String insight;
        private String recommendation;
        private String priority; // HIGH, MEDIUM, LOW
        private BigDecimal impactScore;
    }
}