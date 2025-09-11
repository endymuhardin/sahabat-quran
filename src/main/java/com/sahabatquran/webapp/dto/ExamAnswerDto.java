package com.sahabatquran.webapp.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ExamAnswerDto {
    private UUID id;
    private UUID examResultId;
    private UUID examQuestionId;
    private Integer questionNumber;
    private String questionType;
    private String questionText;
    private Map<String, Object> answerData;
    private Integer pointsEarned;
    private Integer pointsPossible;
    private Boolean isCorrect;
    private String instructorFeedback;
    private String gradedBy;
    private LocalDateTime gradedAt;
    private LocalDateTime answeredAt;
    
    // Display fields
    private String studentAnswer;
    private String correctAnswer;
    private String status; // ANSWERED, GRADED, PENDING_GRADING
}