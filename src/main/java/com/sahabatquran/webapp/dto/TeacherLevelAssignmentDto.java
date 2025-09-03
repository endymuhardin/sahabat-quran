package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.TeacherLevelAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherLevelAssignmentDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assignment {
        private UUID assignmentId;
        private UUID teacherId;
        private String teacherName;
        private String teacherUsername;
        private UUID levelId;
        private String levelName;
        private String levelDescription;
        private TeacherLevelAssignment.CompetencyLevel competencyLevel;
        private Integer maxClassesForLevel;
        private TeacherLevelAssignment.Specialization specialization;
        private String assignedByName;
        private LocalDateTime assignedAt;
        private Boolean canEdit;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherSummary {
        private UUID teacherId;
        private String teacherName;
        private String teacherUsername;
        private Integer totalLevelsAssigned;
        private Integer totalMaxClasses;
        private List<String> assignedLevelNames;
        private List<TeacherLevelAssignment.Specialization> specializations;
        private Boolean hasAvailabilitySubmitted;
        private Integer availableSlotsCount;
        private String workloadStatus; // UNDERLOADED, OPTIMAL, OVERLOADED
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelSummary {
        private UUID levelId;
        private String levelName;
        private String levelDescription;
        private Integer assignedTeachers;
        private Integer requiredTeachers; // Based on estimated student count
        private List<TeacherLevelAssignment.CompetencyLevel> competencyDistribution;
        private String coverageStatus; // INSUFFICIENT, ADEQUATE, EXCELLENT
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkloadAnalysis {
        private Integer totalTeachers;
        private Integer assignedTeachers;
        private Integer unassignedTeachers;
        private Double averageClassesPerTeacher;
        private Integer totalClassCapacity;
        private Map<String, Integer> workloadDistribution; // Status -> Count
        private List<String> recommendations;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnassignedTeacher {
        private UUID teacherId;
        private String teacherName;
        private String teacherUsername;
        private Boolean hasAvailabilitySubmitted;
        private Integer availableSlotsCount;
        private List<String> suggestedLevels;
        private String reason; // Why not assigned yet
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelAssignmentData {
        private Integer assignedCount;
        private Integer workloadPercentage;
        private List<AssignedTeacherInfo> assignedTeachers;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignedTeacherInfo {
        private UUID teacherId;
        private String teacherName;
        private TeacherLevelAssignment.CompetencyLevel competencyLevel;
        private Integer maxClassesForLevel;
        private String specialization;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelAssignments {
        private LevelAssignmentData tahsin1;
        private LevelAssignmentData tahsin2;
        private LevelAssignmentData tahsin3;
        private LevelAssignmentData tahfidz;
    }
    
    private String termName;
    private List<Assignment> assignments;
    private List<TeacherSummary> teacherSummaries;
    private List<LevelSummary> levelSummaries;
    private WorkloadAnalysis workloadAnalysis;
    private List<UnassignedTeacher> unassignedTeachers;
    private LevelAssignments levelAssignments;
    
    // Available options for dropdowns
    private List<TeacherOption> availableTeachers;
    private List<LevelOption> availableLevels;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherOption {
        private UUID teacherId;
        private String teacherName;
        private String teacherUsername;
        private Boolean isActive;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelOption {
        private UUID levelId;
        private String levelName;
        private String levelDescription;
        private Boolean isActive;
    }
}