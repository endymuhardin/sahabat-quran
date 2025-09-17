package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for report validation results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportValidationResultDto {

    private boolean isValid;
    private UUID studentId;
    private String studentName;
    private List<String> validationErrors;
    private List<String> warnings;

    // Enrollment validation
    private boolean isEnrolledInTerm;
    private List<String> availableTerms;
    private String suggestedTerm;

    // Data completeness
    private DataCompletenessDto dataCompleteness;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataCompletenessDto {
        private boolean hasCompleteData;
        private List<String> missingAssessments;
        private List<String> incompleteFields;
        private int completionPercentage;
        private String completionStatus; // COMPLETE, PARTIAL, MISSING
    }
}