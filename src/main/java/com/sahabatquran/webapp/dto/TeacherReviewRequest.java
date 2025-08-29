package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentRegistration;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class TeacherReviewRequest {
    
    @NotNull(message = "Registration ID is required")
    private UUID registrationId;
    
    @NotNull(message = "Teacher ID is required")
    private UUID teacherId;
    
    @NotNull(message = "Review status is required")
    private StudentRegistration.TeacherReviewStatus reviewStatus;
    
    @Size(min = 10, max = 2000, message = "Teacher remarks must be between 10 and 2000 characters")
    private String teacherRemarks;
    
    private UUID recommendedLevelId;
    
    // For placement test evaluation (if included in teacher review)
    private Integer placementTestResult;
    
    @Size(max = 1000, message = "Placement notes cannot exceed 1000 characters")
    private String placementNotes;
}