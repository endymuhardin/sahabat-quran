package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentAssessment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelDistributionDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelStats {
        private UUID levelId;
        private String levelName;
        private String levelDescription;
        private Long newStudentCount;
        private Long existingStudentCount;
        private Long totalStudentCount;
        private BigDecimal averageScore;
        private Long recommendedClassCount;
        private String specialNotes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentLevelAssignment {
        private UUID studentId;
        private String studentName;
        private String studentUsername;
        private StudentAssessment.StudentCategory category;
        private UUID determinedLevelId;
        private String determinedLevelName;
        private BigDecimal assessmentScore;
        private String assessmentGrade;
        private String specialCircumstances;
        private Boolean isValidated;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentRules {
        private String ruleType; // NEW_STUDENT or EXISTING_STUDENT
        private List<ScoreLevelMapping> scoreMappings;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreLevelMapping {
        private BigDecimal minScore;
        private BigDecimal maxScore;
        private String levelName;
        private String description;
        private String additionalRequirements;
    }
    
    private String termName;
    private List<LevelStats> levelStatistics;
    private List<StudentLevelAssignment> studentAssignments;
    private List<AssignmentRules> assignmentRules;
    private Long totalValidatedAssignments;
    private Long totalPendingAssignments;
}