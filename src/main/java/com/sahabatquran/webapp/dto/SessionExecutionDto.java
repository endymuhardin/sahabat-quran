package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionExecutionDto {
    
    private UUID sessionId;
    private String sessionStatus;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private String sessionNotes;
    private List<String> learningObjectives;
    private Map<String, Boolean> objectivesAchieved;
    private List<StudentAttendanceDto> studentAttendances;
    private Map<String, Object> attendanceSummary;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceDto {
        private UUID studentId;
        private String studentName;
        private Boolean isPresent;
        private String notes;
    }
}