package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherPerformanceTrendDto {
    private UUID teacherId;
    private String teacherName;
    private List<TermPerformance> performanceHistory;
    private BigDecimal averagePerformanceScore;
    private BigDecimal performanceImprovement;
    private List<String> strengths;
    private List<String> improvementAreas;
    private Map<String, BigDecimal> skillMetrics;
    private List<ProfessionalDevelopment> developmentHistory;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermPerformance {
        private String termName;
        private BigDecimal performanceScore;
        private BigDecimal studentSatisfaction;
        private Integer classCount;
        private BigDecimal completionRate;
        private Integer studentCount;
        private BigDecimal workloadUtilization;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfessionalDevelopment {
        private String trainingName;
        private String completionDate;
        private String impact;
        private BigDecimal performanceBeforeTraining;
        private BigDecimal performanceAfterTraining;
    }
}