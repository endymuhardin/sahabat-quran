package com.sahabatquran.webapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Teacher Pre-Closure Dashboard
 * Contains all validation data for teacher tasks before semester closure
 */
@Data
@Builder
public class TeacherPreClosureDto {

    // Teacher Information
    private UUID teacherId;
    private String teacherName;
    private String teacherCode;

    // Term Information
    private UUID termId;
    private String termName;
    private LocalDate termEndDate;
    private Integer daysUntilClosure;

    // Overall Statistics
    private Integer totalClasses;
    private Integer classesWithCompleteGrades;
    private Integer classesWithIncompleteGrades;
    private Integer classesWithVerifiedAttendance;
    private Integer classesWithUnverifiedAttendance;
    private Integer pendingEvaluations;
    private Integer completedEvaluations;

    // Completion Percentages
    private Double gradeCompletionPercentage;
    private Double attendanceVerificationPercentage;
    private Double evaluationCompletionPercentage;
    private Double overallCompletionPercentage;

    // Detailed Class Information
    private List<ClassPreClosureStatus> classStatuses;

    // Pending Tasks Summary
    private List<PendingTask> pendingTasks;

    // Validation Status
    private boolean allGradesFinalized;
    private boolean allAttendanceVerified;
    private boolean allEvaluationsCompleted;
    private boolean readyForClosure;

    /**
     * Individual Class Pre-Closure Status
     */
    @Data
    @Builder
    public static class ClassPreClosureStatus {
        private UUID classId;
        private String className;
        private String levelName;
        private Integer totalStudents;

        // Grade Status
        private Integer studentsWithGrades;
        private Integer studentsWithoutGrades;
        private boolean gradesFinalized;
        private LocalDate gradesFinalizedDate;

        // Attendance Status
        private Integer totalSessions;
        private Integer sessionsWithAttendance;
        private Integer sessionsWithoutAttendance;
        private boolean attendanceVerified;
        private LocalDate attendanceVerifiedDate;

        // Evaluation Status
        private boolean evaluationSubmitted;
        private LocalDate evaluationSubmittedDate;

        // Missing Items Details
        private List<String> missingGradeStudents;
        private List<String> missingSessions;

        // Overall Status
        private String status; // COMPLETE, INCOMPLETE, PENDING
        private Double completionPercentage;
    }

    /**
     * Pending Task Information
     */
    @Data
    @Builder
    public static class PendingTask {
        private String taskType; // GRADE_ENTRY, ATTENDANCE_VERIFICATION, EVALUATION_SUBMISSION
        private String description;
        private UUID relatedClassId;
        private String relatedClassName;
        private Integer itemCount;
        private String urgency; // HIGH, MEDIUM, LOW
        private LocalDate deadline;
    }
}