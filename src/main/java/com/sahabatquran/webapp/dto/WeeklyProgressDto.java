package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyProgressDto {
    
    private UUID progressId;
    private UUID enrollmentId;
    private UUID studentId;
    private UUID teacherId;
    private Integer weekNumber;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    
    // Assessment scores
    private BigDecimal recitationScore;
    private String recitationGrade;
    private String memorizationProgress;
    private BigDecimal memorizationScore;
    private BigDecimal tajweedScore;
    private String tajweedGrade;
    private String participationGrade;
    
    // Attendance
    private Integer attendanceCount;
    private Integer sessionsAttended;
    private Integer sessionsTotal;
    private BigDecimal attendanceRate;
    
    // Notes and support
    private String teacherNotes;
    private String areasOfImprovement;
    private Boolean needsSupport;
    private String supportReason;
    private String parentCommunicationNotes;
    
    // Response fields
    private String studentName;
    private String className;
    private String overallGrade;
    private String progressStatus; // EXCELLENT, GOOD, NEEDS_IMPROVEMENT, CONCERN
}