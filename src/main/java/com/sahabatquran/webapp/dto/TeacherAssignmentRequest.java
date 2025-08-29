package com.sahabatquran.webapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class TeacherAssignmentRequest {
    
    @NotNull(message = "Registration ID is required")
    private UUID registrationId;
    
    @NotNull(message = "Teacher ID is required")
    private UUID teacherId;
    
    
    @Size(max = 500, message = "Assignment notes cannot exceed 500 characters")
    private String assignmentNotes;
}