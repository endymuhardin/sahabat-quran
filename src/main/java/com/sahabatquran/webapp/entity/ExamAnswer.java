package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "exam_answers")
@Data
@EqualsAndHashCode(exclude = {"examResult", "examQuestion", "gradedBy"})
@ToString(exclude = {"examResult", "examQuestion", "gradedBy"})
public class ExamAnswer {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exam_result", nullable = false)
    private ExamResult examResult;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exam_question", nullable = false)
    private ExamQuestion examQuestion;
    
    // JSON field for storing the actual answer data
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_data", columnDefinition = "jsonb")
    private Map<String, Object> answerData;
    
    @Column(name = "points_earned", precision = 8, scale = 2)
    private BigDecimal pointsEarned;
    
    @Column(name = "points_possible", precision = 8, scale = 2)
    private BigDecimal pointsPossible;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "grading_status", nullable = false, length = 20)
    private GradingStatus gradingStatus = GradingStatus.NOT_GRADED;
    
    @Column(name = "instructor_feedback", columnDefinition = "TEXT")
    private String instructorFeedback;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @Column(name = "time_spent_seconds")
    private Long timeSpentSeconds;
    
    @Column(name = "answer_sequence")
    private Integer answerSequence; // For tracking order of answers in multi-part questions
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum GradingStatus {
        NOT_GRADED,       // Answer not yet graded
        AUTO_GRADED,      // Automatically graded (MC, T/F, etc.)
        MANUAL_GRADED,    // Manually graded by instructor
        PENDING_REVIEW,   // Needs instructor review
        FLAGGED          // Flagged for special attention
    }
    
    // Helper methods for different answer types
    @SuppressWarnings("unchecked")
    public List<String> getMultipleChoiceAnswers() {
        if (answerData == null) return List.of();
        Object answers = answerData.get("selectedOptions");
        if (answers instanceof List) {
            return (List<String>) answers;
        }
        return List.of();
    }
    
    public String getSingleChoiceAnswer() {
        if (answerData == null) return null;
        return (String) answerData.get("selectedOption");
    }
    
    public Boolean getTrueFalseAnswer() {
        if (answerData == null) return null;
        return (Boolean) answerData.get("answer");
    }
    
    public String getTextAnswer() {
        if (answerData == null) return null;
        return (String) answerData.get("textAnswer");
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getFillBlankAnswers() {
        if (answerData == null) return List.of();
        Object answers = answerData.get("blankAnswers");
        if (answers instanceof List) {
            return (List<String>) answers;
        }
        return List.of();
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, String> getMatchingAnswers() {
        if (answerData == null) return Map.of();
        Object answers = answerData.get("matchingAnswers");
        if (answers instanceof Map) {
            return (Map<String, String>) answers;
        }
        return Map.of();
    }
    
    public String getRecitationFilePath() {
        if (answerData == null) return null;
        return (String) answerData.get("audioFilePath");
    }
    
    // Auto-grading methods
    public void autoGrade() {
        if (examQuestion == null || gradingStatus == GradingStatus.MANUAL_GRADED) {
            return;
        }
        
        pointsPossible = examQuestion.getPoints();
        
        switch (examQuestion.getQuestionType()) {
            case MULTIPLE_CHOICE:
                autoGradeMultipleChoice();
                break;
            case MULTIPLE_SELECT:
                autoGradeMultipleSelect();
                break;
            case TRUE_FALSE:
                autoGradeTrueFalse();
                break;
            case FILL_BLANK:
                autoGradeFillBlank();
                break;
            case MATCHING:
                autoGradeMatching();
                break;
            default:
                // SHORT_ANSWER, ESSAY, RECITATION need manual grading
                gradingStatus = GradingStatus.PENDING_REVIEW;
                pointsEarned = BigDecimal.ZERO;
                isCorrect = null;
                break;
        }
    }
    
    private void autoGradeMultipleChoice() {
        String selectedAnswer = getSingleChoiceAnswer();
        String correctAnswer = examQuestion.getCorrectAnswer();
        
        boolean correct = selectedAnswer != null && selectedAnswer.equals(correctAnswer);
        isCorrect = correct;
        pointsEarned = correct ? pointsPossible : BigDecimal.ZERO;
        gradingStatus = GradingStatus.AUTO_GRADED;
    }
    
    private void autoGradeMultipleSelect() {
        List<String> selectedAnswers = getMultipleChoiceAnswers();
        List<String> correctAnswers = examQuestion.getCorrectAnswers();
        
        boolean correct = selectedAnswers.size() == correctAnswers.size() && 
                         correctAnswers.containsAll(selectedAnswers);
        
        isCorrect = correct;
        pointsEarned = correct ? pointsPossible : BigDecimal.ZERO;
        gradingStatus = GradingStatus.AUTO_GRADED;
    }
    
    private void autoGradeTrueFalse() {
        Boolean selectedAnswer = getTrueFalseAnswer();
        Boolean correctAnswer = examQuestion.getTrueFalseAnswer();
        
        boolean correct = selectedAnswer != null && selectedAnswer.equals(correctAnswer);
        isCorrect = correct;
        pointsEarned = correct ? pointsPossible : BigDecimal.ZERO;
        gradingStatus = GradingStatus.AUTO_GRADED;
    }
    
    private void autoGradeFillBlank() {
        List<String> studentAnswers = getFillBlankAnswers();
        List<String> correctAnswers = examQuestion.getFillBlankAnswers();
        
        if (studentAnswers.size() != correctAnswers.size()) {
            isCorrect = false;
            pointsEarned = BigDecimal.ZERO;
        } else {
            int correctCount = 0;
            for (int i = 0; i < studentAnswers.size(); i++) {
                if (i < correctAnswers.size() && 
                    studentAnswers.get(i).trim().equalsIgnoreCase(correctAnswers.get(i).trim())) {
                    correctCount++;
                }
            }
            
            BigDecimal partialCredit = BigDecimal.valueOf(correctCount)
                    .divide(BigDecimal.valueOf(correctAnswers.size()), 4, java.math.RoundingMode.HALF_UP);
            pointsEarned = pointsPossible.multiply(partialCredit);
            isCorrect = correctCount == correctAnswers.size();
        }
        
        gradingStatus = GradingStatus.AUTO_GRADED;
    }
    
    private void autoGradeMatching() {
        Map<String, String> studentMatches = getMatchingAnswers();
        Map<String, String> correctMatches = examQuestion.getMatchingPairs();
        
        int correctCount = 0;
        for (Map.Entry<String, String> entry : studentMatches.entrySet()) {
            String correctMatch = correctMatches.get(entry.getKey());
            if (correctMatch != null && correctMatch.equals(entry.getValue())) {
                correctCount++;
            }
        }
        
        BigDecimal partialCredit = correctMatches.isEmpty() ? BigDecimal.ZERO : 
                              BigDecimal.valueOf(correctCount)
                                      .divide(BigDecimal.valueOf(correctMatches.size()), 4, java.math.RoundingMode.HALF_UP);
        pointsEarned = pointsPossible.multiply(partialCredit);
        isCorrect = correctCount == correctMatches.size();
        gradingStatus = GradingStatus.AUTO_GRADED;
    }
    
    public void markAsManuallyGraded(User gradedByUser, BigDecimal points, String feedback) {
        this.pointsEarned = points;
        this.instructorFeedback = feedback;
        this.gradedBy = gradedByUser;
        this.gradedAt = LocalDateTime.now();
        this.gradingStatus = GradingStatus.MANUAL_GRADED;
        
        if (pointsPossible != null && pointsPossible.compareTo(BigDecimal.ZERO) > 0) {
            this.isCorrect = points.compareTo(pointsPossible) >= 0;
        }
    }
    
    public boolean needsManualGrading() {
        return gradingStatus == GradingStatus.PENDING_REVIEW || 
               gradingStatus == GradingStatus.NOT_GRADED;
    }
    
    public boolean isAutoGradeable() {
        if (examQuestion == null) return false;
        
        return examQuestion.getQuestionType() == ExamQuestion.QuestionType.MULTIPLE_CHOICE ||
               examQuestion.getQuestionType() == ExamQuestion.QuestionType.MULTIPLE_SELECT ||
               examQuestion.getQuestionType() == ExamQuestion.QuestionType.TRUE_FALSE ||
               examQuestion.getQuestionType() == ExamQuestion.QuestionType.FILL_BLANK ||
               examQuestion.getQuestionType() == ExamQuestion.QuestionType.MATCHING;
    }
}