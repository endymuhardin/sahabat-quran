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
public class SessionMonitoringDto {
    
    private LocalDate monitoringDate;
    private Integer totalSessionsScheduled;
    private Integer sessionsInProgress;
    private Integer sessionsCompleted;
    private Integer sessionsCancelled;
    private Integer teacherCheckIns;
    private Integer teacherAbsences;
    private BigDecimal averageStudentAttendance;
    private List<SystemAlertDto> alerts;
    private LocalDateTime lastUpdated;
    
    // Live session details
    private List<LiveSessionDto> liveSessions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LiveSessionDto {
        private UUID sessionId;
        private String className;
        private String teacherName;
        private String sessionStatus;
        private LocalDateTime scheduledStartTime;
        private LocalDateTime actualStartTime;
        private Boolean teacherCheckedIn;
        private LocalDateTime checkInTime;
        private Integer studentsPresent;
        private Integer totalStudents;
        private BigDecimal attendanceRate;
        private List<String> alerts;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemAlertDto {
        private UUID alertId;
        private String alertType;
        private String severity;
        private String message;
        private String teacherName;
        private String className;
        private LocalDateTime createdAt;
        private Boolean isResolved;
    }
}