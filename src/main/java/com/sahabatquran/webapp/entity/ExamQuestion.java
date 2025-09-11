package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "exam_questions")
@Data
@EqualsAndHashCode(exclude = {"exam", "createdBy"})
@ToString(exclude = {"exam", "createdBy"})
public class ExamQuestion {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exam", nullable = false)
    private Exam exam;
    
    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private QuestionType questionType;
    
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;
    
    @Column(name = "points", nullable = false, precision = 8, scale = 2)
    private BigDecimal points = BigDecimal.ONE;
    
    @Column(name = "required", nullable = false)
    private Boolean required = true;
    
    // JSON field for storing question-specific data (options, correct answers, etc.)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "question_data", columnDefinition = "jsonb")
    private Map<String, Object> questionData;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum QuestionType {
        MULTIPLE_CHOICE,    // Single correct answer from options
        MULTIPLE_SELECT,    // Multiple correct answers from options
        TRUE_FALSE,         // Boolean question
        SHORT_ANSWER,       // Text input (short)
        ESSAY,             // Text input (long)
        FILL_BLANK,        // Fill in the blanks
        MATCHING,          // Match items from two lists
        RECITATION         // Audio recording for Quran recitation
    }
    
    // Helper methods for different question types
    @SuppressWarnings("unchecked")
    public List<String> getMultipleChoiceOptions() {
        if (questionType == QuestionType.MULTIPLE_CHOICE || questionType == QuestionType.MULTIPLE_SELECT) {
            return (List<String>) questionData.getOrDefault("options", new ArrayList<>());
        }
        return new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getCorrectAnswers() {
        return (List<String>) questionData.getOrDefault("correctAnswers", new ArrayList<>());
    }
    
    public String getCorrectAnswer() {
        List<String> answers = getCorrectAnswers();
        return answers.isEmpty() ? null : answers.get(0);
    }
    
    public Boolean getTrueFalseAnswer() {
        if (questionType == QuestionType.TRUE_FALSE) {
            return (Boolean) questionData.getOrDefault("correctAnswer", null);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, String> getMatchingPairs() {
        if (questionType == QuestionType.MATCHING) {
            return (Map<String, String>) questionData.getOrDefault("matchingPairs", Map.of());
        }
        return Map.of();
    }
    
    public Integer getMaxWordsForShortAnswer() {
        if (questionType == QuestionType.SHORT_ANSWER) {
            return (Integer) questionData.getOrDefault("maxWords", 50);
        }
        return null;
    }
    
    public Integer getMaxWordsForEssay() {
        if (questionType == QuestionType.ESSAY) {
            return (Integer) questionData.getOrDefault("maxWords", 500);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getFillBlankAnswers() {
        if (questionType == QuestionType.FILL_BLANK) {
            return (List<String>) questionData.getOrDefault("blankAnswers", new ArrayList<>());
        }
        return new ArrayList<>();
    }
    
    // Validation methods
    public boolean isValidForQuestionType() {
        switch (questionType) {
            case MULTIPLE_CHOICE:
                return getMultipleChoiceOptions().size() >= 2 && getCorrectAnswers().size() == 1;
            case MULTIPLE_SELECT:
                return getMultipleChoiceOptions().size() >= 2 && !getCorrectAnswers().isEmpty();
            case TRUE_FALSE:
                return getTrueFalseAnswer() != null;
            case SHORT_ANSWER:
            case ESSAY:
                return questionText != null && !questionText.trim().isEmpty();
            case FILL_BLANK:
                return !getFillBlankAnswers().isEmpty();
            case MATCHING:
                return !getMatchingPairs().isEmpty();
            case RECITATION:
                return questionText != null && !questionText.trim().isEmpty();
            default:
                return false;
        }
    }
}