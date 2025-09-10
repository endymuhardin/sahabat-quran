package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StudentFeedbackDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignSummary {
        private UUID campaignId;
        private String campaignName;
        private String campaignType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isAnonymous;
        private Boolean hasSubmitted;
        private Integer daysRemaining;
        private String status; // ACTIVE, COMPLETED, UPCOMING
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignDetails {
        private UUID campaignId;
        private String campaignName;
        private String description;
        private String campaignType;
        private Boolean isAnonymous;
        private Integer totalQuestions;
        private Integer estimatedTime; // in minutes
        private Boolean hasSubmitted;
        private List<QuestionDto> questions;
        private String teacherName; // if teacher evaluation
        private String className; // if class-specific
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private UUID questionId;
        private String questionText;
        private String questionType; // RATING, YES_NO, TEXT, MULTIPLE_CHOICE
        private Boolean isRequired;
        private Integer orderIndex;
        private String category;
        private List<String> options; // for multiple choice
        private Integer minRating; // for rating questions
        private Integer maxRating; // for rating questions
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumeData {
        private Boolean canResume;
        private LocalDateTime lastSaved;
        private Integer completedQuestions;
        private Integer totalQuestions;
        private Map<UUID, FeedbackSubmissionDto.AnswerData> savedAnswers;
        private Integer progressPercentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionHistory {
        private UUID campaignId;
        private String campaignName;
        private LocalDateTime submittedAt;
        private String campaignType;
        private Boolean wasAnonymous;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionConfirmation {
        private UUID confirmationId;
        private String campaignName;
        private LocalDateTime submittedAt;
        private String message;
        private String referenceNumber;
    }
    
    public enum CompletionStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        EXPIRED
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackStats {
        private Integer totalCampaigns;
        private Integer completedCampaigns;
        private Integer pendingCampaigns;
        private Double completionRate;
        private LocalDateTime lastSubmission;
    }
}