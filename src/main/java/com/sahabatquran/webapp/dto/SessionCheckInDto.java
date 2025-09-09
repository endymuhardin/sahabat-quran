package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCheckInDto {
    
    private UUID sessionId;
    private UUID teacherId;
    private LocalDateTime checkInTime;
    private String checkInLocation;
    private String notes;
    
    // Response fields
    private String sessionName;
    private String className;
    private LocalDateTime scheduledStartTime;
    private Integer expectedStudents;
    private String status;
}