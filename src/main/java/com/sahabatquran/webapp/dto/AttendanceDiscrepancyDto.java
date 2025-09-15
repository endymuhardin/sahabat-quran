package com.sahabatquran.webapp.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AttendanceDiscrepancyDto {
    private UUID sessionId;
    private int registeredStudentCount;
    private int actualPresentCount;
    private int extraStudentCount;
    private DiscrepancyType discrepancyType;
    private String message;
    private List<String> suggestions;

    public enum DiscrepancyType {
        EXTRA_STUDENTS,
        FEWER_STUDENTS,
        NO_DISCREPANCY
    }

    public static AttendanceDiscrepancyDto noDiscrepancy(UUID sessionId, int count) {
        AttendanceDiscrepancyDto dto = new AttendanceDiscrepancyDto();
        dto.setSessionId(sessionId);
        dto.setRegisteredStudentCount(count);
        dto.setActualPresentCount(count);
        dto.setExtraStudentCount(0);
        dto.setDiscrepancyType(DiscrepancyType.NO_DISCREPANCY);
        dto.setMessage("Attendance matches registered student count");
        return dto;
    }

    public static AttendanceDiscrepancyDto extraStudents(UUID sessionId, int registered, int actual) {
        AttendanceDiscrepancyDto dto = new AttendanceDiscrepancyDto();
        dto.setSessionId(sessionId);
        dto.setRegisteredStudentCount(registered);
        dto.setActualPresentCount(actual);
        dto.setExtraStudentCount(actual - registered);
        dto.setDiscrepancyType(DiscrepancyType.EXTRA_STUDENTS);
        dto.setMessage("More students present than registered: " + (actual - registered) + " extra student(s)");
        dto.setSuggestions(List.of(
            "Add guest students with proper reason",
            "Verify if students are from other classes",
            "Contact academic admin for clarification"
        ));
        return dto;
    }

    public static AttendanceDiscrepancyDto fewerStudents(UUID sessionId, int registered, int actual) {
        AttendanceDiscrepancyDto dto = new AttendanceDiscrepancyDto();
        dto.setSessionId(sessionId);
        dto.setRegisteredStudentCount(registered);
        dto.setActualPresentCount(actual);
        dto.setExtraStudentCount(0);
        dto.setDiscrepancyType(DiscrepancyType.FEWER_STUDENTS);
        dto.setMessage("Fewer students present than registered: " + (registered - actual) + " student(s) absent");
        dto.setSuggestions(List.of(
            "Mark absent students in attendance",
            "Check if students notified about absence",
            "Update attendance records accordingly"
        ));
        return dto;
    }
}