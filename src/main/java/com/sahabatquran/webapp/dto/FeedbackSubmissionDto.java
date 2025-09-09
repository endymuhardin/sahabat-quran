package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}