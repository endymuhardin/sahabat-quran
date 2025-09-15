package com.sahabatquran.webapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StudentReportEmailDto {
    private String studentName;
    private String levelName;
    private String termName;
    private String reportDate;
    private List<GradeDto> grades;
    private String overallGrade;
    private String overallScore;
    private String attendanceRate;
    private String attendanceNotes;
    private String teacherEvaluation;
    private List<String> recommendations;
    private String additionalNotes;
    private String schoolAddress;
    private String schoolPhone;
    private String schoolEmail;
    private String parentPortalUrl;
    private String generatedAt;
    private String recipientEmail;
    private String recipientName;

    @Data
    @Builder
    public static class GradeDto {
        private String subject;
        private String grade;
        private String score;
    }
}