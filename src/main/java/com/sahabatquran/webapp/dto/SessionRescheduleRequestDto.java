package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRescheduleRequestDto {
    
    private UUID sessionId;
    private LocalDate originalDate;
    private String originalTime;
    private LocalDate proposedDate;
    private String proposedTime;
    private String reason;
    private String reasonDetails;
    private Boolean autoApproved;
    
    // Response fields
    private UUID requestId;
    private String status;
    private String className;
    private String teacherName;
    private Integer studentsAffected;
    private Boolean requiresApproval;
}