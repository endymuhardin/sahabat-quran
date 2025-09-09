package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback_answers")
@Data
@EqualsAndHashCode(exclude = {"response", "question"})
@ToString(exclude = {"response", "question"})
public class FeedbackAnswer {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_response", nullable = false)
    private FeedbackResponse response;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question", nullable = false)
    private FeedbackQuestion question;
    
    @Column(name = "rating_value")
    private Integer ratingValue; // For rating questions (1-5)
    
    @Column(name = "boolean_value")
    private Boolean booleanValue; // For yes/no questions
    
    @Column(name = "text_value", columnDefinition = "TEXT")
    private String textValue; // For text questions
    
    @Column(name = "selected_option", length = 200)
    private String selectedOption; // For multiple choice
    
    @Column(name = "scale_value")
    private Integer scaleValue; // For scale questions
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public Object getValue() {
        if (question == null) return null;
        
        switch (question.getQuestionType()) {
            case RATING:
                return ratingValue;
            case YES_NO:
                return booleanValue;
            case TEXT:
                return textValue;
            case MULTIPLE_CHOICE:
                return selectedOption;
            case SCALE:
                return scaleValue;
            default:
                return null;
        }
    }
    
    public void setValue(Object value) {
        if (question == null || value == null) return;
        
        switch (question.getQuestionType()) {
            case RATING:
                this.ratingValue = (Integer) value;
                break;
            case YES_NO:
                this.booleanValue = (Boolean) value;
                break;
            case TEXT:
                this.textValue = (String) value;
                break;
            case MULTIPLE_CHOICE:
                this.selectedOption = (String) value;
                break;
            case SCALE:
                this.scaleValue = (Integer) value;
                break;
        }
    }
}