package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "feedback_questions")
@Data
@EqualsAndHashCode(exclude = {"campaign"})
@ToString(exclude = {"campaign"})
public class FeedbackQuestion {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campaign", nullable = false)
    private FeedbackCampaign campaign;
    
    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;
    
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;
    
    @Column(name = "question_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    
    @Column(name = "question_category", length = 50)
    private String questionCategory;
    
    @Column(name = "is_required")
    private Boolean isRequired = true;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> options; // For multiple choice questions
    
    @Column(name = "min_value")
    private Integer minValue; // For scale questions
    
    @Column(name = "max_value")
    private Integer maxValue; // For scale questions
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum QuestionType {
        RATING,      // 1-5 star rating
        YES_NO,      // Boolean yes/no
        MULTIPLE_CHOICE, // Select from options
        TEXT,        // Free text response
        SCALE        // Numeric scale (e.g., 1-10)
    }
}