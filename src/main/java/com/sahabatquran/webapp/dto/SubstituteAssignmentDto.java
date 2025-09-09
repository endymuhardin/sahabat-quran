package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituteAssignmentDto {
    
    private UUID sessionId;
    private UUID originalTeacherId;
    private UUID substituteTeacherId;
    private String assignmentType;
    private String reason;
    private BigDecimal compensationAmount;
    private String specialInstructions;
    private Boolean materialsShared;
    
    // Response fields
    private String className;
    private LocalDate sessionDate;
    private String sessionTime;
    private String originalTeacherName;
    private String substituteTeacherName;
    private String substituteContactPreference;
    private String substitutePhone;
    private String substituteEmail;
    private List<String> substituteSpecializations;
    private BigDecimal substituteRating;
    private Boolean substituteAccepted;
    private LocalDateTime acceptanceTime;
}