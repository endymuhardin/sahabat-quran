package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentAssessment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentFoundationDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewStudentStats {
        private Long totalRegistrations;
        private Long placementTestsCompleted;
        private Long testsScheduled;
        private Long testsNotScheduled;
        private BigDecimal completionPercentage;
        private Long levelAssignmentsReady;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExistingStudentStats {
        private Long totalContinuingStudents;
        private Long examResultsSubmitted;
        private Long partialResults;
        private Long resultsMissing;
        private BigDecimal completionPercentage;
        private Long levelAssignmentsReady;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallReadiness {
        private Long totalStudentsReady;
        private Long totalStudents;
        private BigDecimal overallReadinessPercentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingAssessmentItem {
        private String studentName;
        private String studentUsername;
        private StudentAssessment.StudentCategory category;
        private StudentAssessment.AssessmentType assessmentType;
        private String status;
        private String notes;
    }
    
    private String termName;
    private NewStudentStats newStudentStats;
    private ExistingStudentStats existingStudentStats;
    private OverallReadiness overallReadiness;
    private List<PendingAssessmentItem> pendingAssessments;
}