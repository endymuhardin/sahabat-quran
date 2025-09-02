package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.TeacherAvailability;
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
public class TeacherAvailabilityDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilitySlot {
        private UUID availabilityId;
        private Integer dayOfWeek; // 1=Monday, 7=Sunday
        private TeacherAvailability.SessionTime sessionTime;
        private Boolean isAvailable;
        private String sessionDisplayName;
        private String dayDisplayName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherAvailabilitySubmission {
        private UUID teacherId;
        private String teacherName;
        private String teacherUsername;
        private UUID termId;
        private List<AvailabilitySlot> availabilitySlots;
        private Integer maxClassesPerWeek;
        private String preferences;
        private LocalDateTime submittedAt;
        private Boolean isComplete;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionStatus {
        private UUID teacherId;
        private String teacherName;
        private Boolean hasSubmitted;
        private LocalDateTime submissionDate;
        private Integer availableSlotsCount;
        private String status; // PENDING, SUBMITTED, INCOMPLETE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityMatrix {
        private Map<Integer, Map<TeacherAvailability.SessionTime, Boolean>> weeklyMatrix;
        private Integer maxClassesPerWeek;
        private String preferences;
        private List<String> preferredLevels;
        private String specialConstraints;
    }
    
    private String termName;
    private List<TeacherAvailabilitySubmission> submissions;
    private List<SubmissionStatus> submissionStatuses;
    private Long totalTeachers;
    private Long submittedTeachers;
    private Long pendingTeachers;
    
    // Template-friendly property names
    private Long submittedCount;
    private Long pendingCount;
    private Integer completionPercentage;
    private List<SubmissionStatus> teacherStatuses;
    private String deadline;
}