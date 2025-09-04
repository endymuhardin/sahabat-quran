package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.ClassGroup;
import com.sahabatquran.webapp.entity.StudentAssessment;
import com.sahabatquran.webapp.entity.TeacherAvailability;
import com.sahabatquran.webapp.entity.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassGenerationDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationReadiness {
        private Boolean canGenerate;
        private BigDecimal studentDataCompleteness;
        private BigDecimal teacherAvailabilityCompleteness;
        private BigDecimal levelAssignmentCompleteness;
        private List<String> blockingIssues;
        private List<String> warnings;
        private GenerationParameters recommendedParameters;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationParameters {
        private Integer defaultMinClassSize;
        private Integer defaultMaxClassSize;
        private Map<UUID, ClassSizeOverride> levelSpecificSizes; // levelId -> size config
        private BigDecimal newExistingStudentRatio; // Target ratio: 40% new, 60% existing
        private Integer maxClassesPerTeacher;
        private Boolean allowUndersizedClasses;
        private Boolean optimizeForTeacherWorkload;
        private String priorityStrategy; // "BALANCE", "MINIMIZE_CONFLICTS", "MAXIMIZE_UTILIZATION"
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassSizeOverride {
        private Integer minStudents;
        private Integer maxStudents;
        private String justification;
        private Boolean approved;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedClassProposal {
        private UUID proposalId;
        private Integer generationRun;
        private BigDecimal optimizationScore;
        private Integer conflictCount;
        private List<GeneratedClass> classes;
        private List<Conflict> conflicts;
        private List<SizeViolation> sizeViolations;
        private GenerationMetrics metrics;
        private LocalDateTime generatedAt;
        private String generatedByName;
        private Boolean isApproved;
        private Boolean canApprove;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedClass {
        private UUID classId;
        private String className;
        private UUID levelId;
        private String levelName;
        private UUID teacherId;
        private String teacherName;
        private TeacherAvailability.DayOfWeek dayOfWeek;
        private Session session;
        private String sessionDisplay;
        private List<AssignedStudent> students;
        private Integer currentSize;
        private Integer minSize;
        private Integer maxSize;
        private Boolean isUndersized;
        private Boolean isOversized;
        private String classType; // "NEW_ONLY", "EXISTING_ONLY", "MIXED"
        private BigDecimal newStudentPercentage;
        private String roomAssignment;
        private List<String> specialNotes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignedStudent {
        private UUID studentId;
        private String studentName;
        private String studentUsername;
        private StudentAssessment.StudentCategory category;
        private BigDecimal assessmentScore;
        private String assessmentGrade;
        private String specialNeeds;
        private Boolean canReassign;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Conflict {
        private String conflictType; // "TEACHER_DOUBLE_BOOKING", "ROOM_CONFLICT", "STUDENT_CONFLICT"
        private String severity; // "HIGH", "MEDIUM", "LOW"
        private String description;
        private List<UUID> affectedClassIds;
        private List<String> resolutionOptions;
        private Boolean isResolved;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeViolation {
        private UUID classId;
        private String className;
        private String violationType; // "UNDERSIZED", "OVERSIZED"
        private Integer currentSize;
        private Integer targetMin;
        private Integer targetMax;
        private String justification;
        private Boolean requiresApproval;
        private Boolean isApproved;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationMetrics {
        private Integer totalClasses;
        private Integer totalStudentsAssigned;
        private Integer unassignedStudents;
        private BigDecimal averageClassSize;
        private Integer teachersUtilized;
        private Integer totalTeacherSlots;
        private BigDecimal teacherUtilizationRate;
        private Integer roomsRequired;
        private Map<String, Integer> classTypeDistribution;
        private BigDecimal workloadBalance; // Standard deviation of teacher workloads
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassRefinementData {
        private List<GeneratedClass> classes;
        private List<UnassignedStudent> unassignedStudents;
        private List<AvailableTeacher> availableTeachers;
        private List<String> availableTimeSlots;
        private Map<String, Integer> roomCapacities;
        private ClassRefinementConstraints constraints;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnassignedStudent {
        private UUID studentId;
        private String studentName;
        private String studentUsername;
        private StudentAssessment.StudentCategory category;
        private UUID determinedLevelId;
        private String determinedLevelName;
        private BigDecimal assessmentScore;
        private String reason; // Why not assigned
        private List<String> suggestedClasses;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableTeacher {
        private UUID teacherId;
        private String teacherName;
        private Integer currentClassCount;
        private Integer maxClasses;
        private List<AvailableSlot> availableSlots;
        private List<UUID> assignedLevelIds;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableSlot {
        private TeacherAvailability.DayOfWeek dayOfWeek;
        private Session session;
        private String display;
        private Boolean isUsed;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassRefinementConstraints {
        private Integer minClassSize;
        private Integer maxClassSize;
        private Integer maxStudentMovesPerRun;
        private Boolean allowTeacherReassignment;
        private Boolean allowTimeSlotChanges;
        private Boolean maintainStudentCategoryBalance;
        private BigDecimal maxNewStudentRatio;
    }
    
    private String termName;
    private GenerationReadiness readinessStatus;
    private GenerationParameters currentParameters;
    private List<GeneratedClassProposal> proposals;
    private GeneratedClassProposal selectedProposal;
    private ClassRefinementData refinementData;
}