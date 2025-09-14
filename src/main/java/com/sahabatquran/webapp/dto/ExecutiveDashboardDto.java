package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutiveDashboardDto {
    private DashboardHeader header;
    private List<KeyPerformanceIndicator> kpis;
    private StrategicMetrics strategicMetrics;
    private List<Alert> alerts;
    private List<QuickAction> quickActions;
    private Map<String, ChartData> charts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardHeader {
        private String period;
        private LocalDate lastUpdated;
        private Integer termCount;
        private String currentTerm;
        private String dashboardVersion;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyPerformanceIndicator {
        private String name;
        private BigDecimal value;
        private String unit;
        private BigDecimal target;
        private BigDecimal variance;
        private String status; // ON_TRACK, AT_RISK, OFF_TRACK
        private String trend; // UP, DOWN, STABLE
        private String category;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrategicMetrics {
        private BigDecimal enrollmentGrowth;
        private BigDecimal financialPerformance;
        private BigDecimal operationalEfficiency;
        private BigDecimal stakeholderSatisfaction;
        private BigDecimal academicQuality;
        private Map<String, BigDecimal> goalAchievement;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alert {
        private String type; // INFO, WARNING, ERROR
        private String category;
        private String message;
        private String recommendation;
        private LocalDate dateIdentified;
        private String priority;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuickAction {
        private String action;
        private String description;
        private String link;
        private String icon;
        private String category;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private String chartType;
        private List<String> labels;
        private List<DataSeries> series;
        private Map<String, Object> options;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataSeries {
        private String name;
        private List<BigDecimal> data;
        private String color;
        private String type;
    }
}