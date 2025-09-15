package com.sahabatquran.webapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AcademicTranscriptDto {
    private String studentId;
    private String studentName;
    private String studentNumber;
    private LocalDate dateOfBirth;
    private String currentLevel;
    private String transcriptType;
    private LocalDate generationDate;
    private List<TermRecordDto> termRecords;
    private String cumulativeGpa;
    private String totalCredits;
    private String academicStanding;
    private String officialSeal;
    private String issuedBy;
    private LocalDate issuedDate;

    @Data
    @Builder
    public static class TermRecordDto {
        private String termName;
        private String level;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<SubjectGradeDto> subjects;
        private String termGpa;
        private String attendanceRate;
        private String status;
    }

    @Data
    @Builder
    public static class SubjectGradeDto {
        private String subjectName;
        private String grade;
        private String score;
        private String credits;
        private String teacherName;
    }
}