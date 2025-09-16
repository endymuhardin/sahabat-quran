package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSubmissionDto {
    
    private UUID campaignId;
    private String anonymousToken;
    private UUID targetTeacherId; // Optional
    private UUID classGroupId; // Optional
    private List<AnswerDto> answers;
    private String deviceInfo;
    
    // Response fields
    private UUID responseId;
    private Boolean isComplete;
    private String submissionStatus;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDto {
        private UUID questionId;
        private Integer ratingValue;
        private Boolean booleanValue;
        private String textValue;
        private String selectedOption;
        private Integer scaleValue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackData {
        private UUID campaignId;
        private List<AnswerData> answers;
        private String deviceInfo;
        private String sessionToken;
        private Boolean isComplete;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerData {
        private UUID questionId;
        private String answerText;
        private Integer rating;
        private Boolean yesNo;
        private List<String> multipleChoice;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartialData {
        private UUID campaignId;
        private List<AnswerData> partialAnswers;
        private Integer currentQuestionIndex;
        private LocalDateTime lastAutoSave;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionResult {
        private Boolean success;
        private String message;
        private UUID submissionId;
        private String anonymousToken;
        private LocalDateTime submittedAt;
        private Boolean isDuplicate;
        private List<String> errors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResult {
        private Boolean isValid;
        private List<String> errors;
        private List<String> warnings;
        private List<UUID> missingRequiredQuestions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnonymousFeedbackData {
        private String notificationType;
        private Integer notificationTimingRating;
        private Boolean informationClarity;
        private Boolean advanceNoticeSufficiency;
        private String parentComments;
        private String deviceInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnonymousSubmissionResult {
        private Boolean success;
        private String message;
        private UUID submissionId;
        private LocalDateTime submittedAt;
        private List<String> errors;
    }
}