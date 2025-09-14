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
public class StudentProgressionAnalyticsDto {
    private UUID studentId;
    private String studentName;
    private List<AcademicJourney> journeyHistory;
    private BigDecimal overallGPA;
    private BigDecimal gpaImprovement;
    private String currentLevel;
    private String progressionStatus;
    private BigDecimal learningVelocity;
    private CohortComparison cohortComparison;
    private List<Prediction> futurePredictions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcademicJourney {
        private String termName;
        private String level;
        private String grade;
        private BigDecimal gpa;
        private BigDecimal attendanceRate;
        private String teacherName;
        private String advancement;
        private Map<String, String> achievements;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CohortComparison {
        private Integer cohortSize;
        private Integer studentRanking;
        private BigDecimal percentileRank;
        private BigDecimal cohortAverageGPA;
        private BigDecimal studentGPAVsCohort;
        private String performanceCategory; // ABOVE_AVERAGE, AVERAGE, BELOW_AVERAGE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prediction {
        private String metric;
        private String predictedValue;
        private BigDecimal confidence;
        private String timeframe;
        private List<String> recommendations;
    }
}