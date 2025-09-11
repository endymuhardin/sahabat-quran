package com.sahabatquran.webapp.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ExamResultDto {
    
    private UUID id;
    
    private UUID examId;
    
    private String examTitle;
    
    private UUID studentId;
    
    private String studentName;
    
    private String studentUsername;
    
    private Integer attemptNumber;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Long timeTakenSeconds;
    
    private Double totalScore;
    
    private Double percentageScore;
    
    private Double pointsEarned;
    
    private Double pointsPossible;
    
    private String status; // IN_PROGRESS, SUBMITTED, GRADED, ABANDONED, TIME_EXPIRED
    
    private String grade; // A+, A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F
    
    private Boolean passed;
    
    private Boolean autoSubmitted;
    
    private String instructorFeedback;
    
    private String gradedBy;
    
    private LocalDateTime gradedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Additional fields for display
    private String formattedTimeTaken;
    
    private String statusDisplay;
    
    private String gradeColor;
    
    private List<ExamAnswerDto> answers;
    
    private Map<String, Object> metadata;
    
    // Statistics (for instructor view)
    private Double classAverage;
    
    private Integer classRank;
    
    private Integer totalStudents;
    
    // Progress tracking
    private Integer answeredQuestions;
    
    private Integer totalQuestions;
    
    private Double progressPercentage;
    
    // Helper methods
    
    public boolean isCompleted() {
        return completedAt != null;
    }
    
    public boolean isGraded() {
        return "GRADED".equals(status);
    }
    
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }
    
    public boolean needsGrading() {
        return "SUBMITTED".equals(status);
    }
    
    public String getFormattedTimeTaken() {
        if (timeTakenSeconds == null) return "N/A";
        
        Duration duration = Duration.ofSeconds(timeTakenSeconds);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    
    public String getStatusDisplay() {
        switch (status) {
            case "IN_PROGRESS": return "In Progress";
            case "SUBMITTED": return "Submitted";
            case "GRADED": return "Graded";
            case "ABANDONED": return "Abandoned";
            case "TIME_EXPIRED": return "Time Expired";
            default: return status;
        }
    }
    
    public String getGradeColor() {
        if (grade == null) return "text-gray-500";
        
        switch (grade) {
            case "A+":
            case "A":
            case "A-":
                return "text-green-600";
            case "B+":
            case "B":
            case "B-":
                return "text-blue-600";
            case "C+":
            case "C":
            case "C-":
                return "text-yellow-600";
            case "D+":
            case "D":
            case "D-":
                return "text-orange-600";
            case "F":
                return "text-red-600";
            default:
                return "text-gray-500";
        }
    }
    
    public Double getProgressPercentage() {
        if (answeredQuestions == null || totalQuestions == null || totalQuestions == 0) {
            return 0.0;
        }
        return (answeredQuestions.doubleValue() / totalQuestions.doubleValue()) * 100.0;
    }
    
    public String getFormattedPercentageScore() {
        if (percentageScore == null) return "N/A";
        return String.format("%.1f%%", percentageScore);
    }
    
    public String getPassFailStatus() {
        if (passed == null) return "Pending";
        return passed ? "Passed" : "Failed";
    }
    
    public String getPassFailColor() {
        if (passed == null) return "text-gray-500";
        return passed ? "text-green-600" : "text-red-600";
    }
}