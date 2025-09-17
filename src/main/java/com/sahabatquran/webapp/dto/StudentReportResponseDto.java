package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for student report generation responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportResponseDto {

    private UUID studentId;
    private String studentName;
    private String level;
    private String className;
    private String termName;
    private String reportType;

    // Grade information
    private BigDecimal finalGrade;
    private String finalLetterGrade;
    private BigDecimal attendanceRate;

    // Assessment components
    private List<AssessmentComponentDto> assessmentComponents;

    // Teacher feedback
    private String teacherFeedback;
    private String teacherName;

    // Data quality indicators
    private boolean hasCompleteData;
    private List<String> missingDataWarnings;
    private List<String> incompleteDataFields;

    // Report metadata
    private LocalDate generatedDate;
    private boolean isPartialReport;
    private boolean hasDisclaimers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentComponentDto {
        private String componentName;
        private BigDecimal score;
        private BigDecimal maxScore;
        private String letterGrade;
        private BigDecimal weight;
        private boolean isMissing;
    }
}