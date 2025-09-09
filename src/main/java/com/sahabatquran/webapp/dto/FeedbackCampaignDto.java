package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCampaignDto {
    
    private UUID campaignId;
    private String campaignName;
    private String campaignType;
    private String targetAudience;
    private UUID termId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isAnonymous;
    private Boolean isActive;
    private Integer minResponsesRequired;
    private Integer currentResponses;
    private BigDecimal responseRate;
    private List<FeedbackQuestionDto> questions;
    
    // Response fields
    private String termName;
    private String status; // ACTIVE, COMPLETED, DRAFT
    private Integer daysRemaining;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackQuestionDto {
        private UUID questionId;
        private Integer questionNumber;
        private String questionText;
        private String questionType;
        private String questionCategory;
        private Boolean isRequired;
        private List<String> options;
        private Integer minValue;
        private Integer maxValue;
    }
}