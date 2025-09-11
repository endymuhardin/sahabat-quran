package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.TeacherAvailabilityChangeRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TeacherAvailabilityChangeRequestDto {
    
    private UUID id;
    private UUID teacherId;
    private String teacherName;
    private UUID termId;
    private String termName;
    
    @NotBlank(message = "Reason for change is required")
    @Size(min = 10, max = 1000, message = "Reason must be between 10 and 1000 characters")
    private String reason;
    
    @NotEmpty(message = "At least one change must be specified")
    private List<AvailabilityChangeDto> requestedChanges;
    
    private Integer originalMaxClasses;
    private Integer newMaxClasses;
    
    private TeacherAvailabilityChangeRequest.RequestStatus status;
    private LocalDateTime submittedAt;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private String reviewComments;
    
    @Data
    public static class AvailabilityChangeDto {
        private String changeType; // ADD, REMOVE, MODIFY
        private String dayOfWeek;
        private String sessionCode;
        private String sessionName;
        private Boolean newAvailability;
        private String description;
    }
    
    @Data
    public static class ChangeRequestFormDto {
        private UUID termId;
        private String reason;
        private List<String> slotsToAdd;
        private List<String> slotsToRemove;
        private Integer newMaxClasses;
        private String additionalNotes;
    }
}