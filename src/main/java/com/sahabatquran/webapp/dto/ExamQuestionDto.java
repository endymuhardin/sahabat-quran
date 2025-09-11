package com.sahabatquran.webapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ExamQuestionDto {
    
    private UUID id;
    
    private UUID examId;
    
    @NotNull(message = "Question number is required")
    @Min(value = 1, message = "Question number must be at least 1")
    private Integer questionNumber;
    
    @NotNull(message = "Question type is required")
    private String questionType; // MULTIPLE_CHOICE, MULTIPLE_SELECT, TRUE_FALSE, SHORT_ANSWER, ESSAY, FILL_BLANK, MATCHING, RECITATION
    
    @NotBlank(message = "Question text is required")
    @Size(max = 2000, message = "Question text must not exceed 2000 characters")
    private String questionText;
    
    @NotNull(message = "Points are required")
    @DecimalMin(value = "0.1", message = "Points must be at least 0.1")
    @DecimalMax(value = "100.0", message = "Points must not exceed 100")
    private Double points = 1.0;
    
    private Boolean required = true;
    
    // Question-specific data stored as key-value pairs
    private Map<String, Object> questionData;
    
    @Size(max = 1000, message = "Explanation must not exceed 1000 characters")
    private String explanation;
    
    @Min(value = 1, message = "Time limit must be at least 1 second")
    @Max(value = 3600, message = "Time limit must not exceed 1 hour")
    private Integer timeLimitSeconds;
    
    private String createdBy;
    
    private LocalDateTime createdAt;
    
    // Helper fields for specific question types (derived from questionData)
    
    // Multiple Choice / Multiple Select
    private List<String> options;
    private List<String> correctAnswers;
    
    // True/False
    private Boolean trueFalseAnswer;
    
    // Short Answer / Essay
    private Integer maxWords;
    
    // Fill in the Blank
    private List<String> blankAnswers;
    
    // Matching
    private Map<String, String> matchingPairs;
    
    // Validation methods for different question types
    
    @AssertTrue(message = "Multiple choice questions must have at least 2 options and 1 correct answer")
    public boolean isValidMultipleChoice() {
        if (!"MULTIPLE_CHOICE".equals(questionType)) {
            return true;
        }
        return options != null && options.size() >= 2 && 
               correctAnswers != null && correctAnswers.size() == 1;
    }
    
    @AssertTrue(message = "Multiple select questions must have at least 2 options and at least 1 correct answer")
    public boolean isValidMultipleSelect() {
        if (!"MULTIPLE_SELECT".equals(questionType)) {
            return true;
        }
        return options != null && options.size() >= 2 && 
               correctAnswers != null && !correctAnswers.isEmpty();
    }
    
    @AssertTrue(message = "True/false questions must have a correct answer")
    public boolean isValidTrueFalse() {
        if (!"TRUE_FALSE".equals(questionType)) {
            return true;
        }
        return trueFalseAnswer != null;
    }
    
    @AssertTrue(message = "Fill in the blank questions must have at least one answer")
    public boolean isValidFillBlank() {
        if (!"FILL_BLANK".equals(questionType)) {
            return true;
        }
        return blankAnswers != null && !blankAnswers.isEmpty();
    }
    
    @AssertTrue(message = "Matching questions must have at least one pair")
    public boolean isValidMatching() {
        if (!"MATCHING".equals(questionType)) {
            return true;
        }
        return matchingPairs != null && !matchingPairs.isEmpty();
    }
    
    // Helper methods
    
    public boolean isAutoGradeable() {
        return "MULTIPLE_CHOICE".equals(questionType) ||
               "MULTIPLE_SELECT".equals(questionType) ||
               "TRUE_FALSE".equals(questionType) ||
               "FILL_BLANK".equals(questionType) ||
               "MATCHING".equals(questionType);
    }
    
    public boolean needsManualGrading() {
        return "SHORT_ANSWER".equals(questionType) ||
               "ESSAY".equals(questionType) ||
               "RECITATION".equals(questionType);
    }
    
    public String getQuestionTypeDisplay() {
        switch (questionType) {
            case "MULTIPLE_CHOICE": return "Multiple Choice";
            case "MULTIPLE_SELECT": return "Multiple Select";
            case "TRUE_FALSE": return "True/False";
            case "SHORT_ANSWER": return "Short Answer";
            case "ESSAY": return "Essay";
            case "FILL_BLANK": return "Fill in the Blank";
            case "MATCHING": return "Matching";
            case "RECITATION": return "Recitation";
            default: return questionType;
        }
    }
}