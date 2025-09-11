package com.sahabatquran.webapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ExamDto {
    
    private UUID id;
    
    @NotBlank(message = "Exam title is required")
    @Size(max = 200, message = "Exam title must not exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Exam type is required")
    private String examType; // PLACEMENT, MIDTERM, FINAL, QUIZ, PRACTICE
    
    @NotNull(message = "Class group is required")
    private UUID classGroupId;
    
    @NotNull(message = "Academic term is required")
    private UUID academicTermId;
    
    @NotNull(message = "Scheduled start time is required")
    @Future(message = "Scheduled start time must be in the future")
    private LocalDateTime scheduledStart;
    
    @NotNull(message = "Scheduled end time is required")
    private LocalDateTime scheduledEnd;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration must not exceed 8 hours")
    private Integer durationMinutes;
    
    @Min(value = 1, message = "Max attempts must be at least 1")
    @Max(value = 10, message = "Max attempts must not exceed 10")
    private Integer maxAttempts = 1;
    
    @DecimalMin(value = "0.0", message = "Passing score must be non-negative")
    @DecimalMax(value = "100.0", message = "Passing score must not exceed 100")
    private Double passingScore = 60.0;
    
    private Double totalPoints = 100.0;
    
    private String status; // DRAFT, SCHEDULED, ACTIVE, COMPLETED, CANCELLED
    
    @Size(max = 2000, message = "Instructions must not exceed 2000 characters")
    private String instructions;
    
    private Boolean allowReview = true;
    
    private Boolean randomizeQuestions = false;
    
    private Boolean showResultsImmediately = false;
    
    private String createdBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Additional fields for display
    private String classGroupName;
    
    private String academicTermName;
    
    private Integer questionCount;
    
    private Integer submissionCount;
    
    private Boolean canTakeExam;
    
    private Boolean hasResults;
    
    private List<ExamQuestionDto> questions = new ArrayList<>();
    
    // Derived fields
    public boolean isEditable() {
        return "DRAFT".equals(status);
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean canBeScheduled() {
        return "DRAFT".equals(status) && questionCount != null && questionCount > 0;
    }
    
    // Validation method for end time
    @AssertTrue(message = "Scheduled end time must be after start time")
    public boolean isValidScheduleTime() {
        if (scheduledStart == null || scheduledEnd == null) {
            return true; // Let individual field validation handle null values
        }
        return scheduledEnd.isAfter(scheduledStart);
    }
    
    // Validation method for duration consistency
    @AssertTrue(message = "Duration must be consistent with scheduled time")
    public boolean isValidDuration() {
        if (scheduledStart == null || scheduledEnd == null || durationMinutes == null) {
            return true; // Let individual field validation handle null values
        }
        
        long actualDurationMinutes = java.time.Duration.between(scheduledStart, scheduledEnd).toMinutes();
        return actualDurationMinutes >= durationMinutes;
    }
}