package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "feedback_campaigns")
@Data
@EqualsAndHashCode(exclude = {"term", "createdBy", "questions", "responses"})
@ToString(exclude = {"term", "createdBy", "questions", "responses"})
public class FeedbackCampaign {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @Column(name = "campaign_name", nullable = false, length = 200)
    private String campaignName;
    
    @Column(name = "campaign_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CampaignType campaignType;
    
    @Column(name = "target_audience", length = 50)
    @Enumerated(EnumType.STRING)
    private TargetAudience targetAudience;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term")
    private AcademicTerm term;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = true;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "template_id")
    private UUID templateId;
    
    @Column(name = "min_responses_required")
    private Integer minResponsesRequired;
    
    @Column(name = "current_responses")
    private Integer currentResponses = 0;
    
    @Column(name = "response_rate", precision = 5, scale = 2)
    private BigDecimal responseRate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_created_by")
    private User createdBy;
    
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionNumber ASC")
    private List<FeedbackQuestion> questions = new ArrayList<>();
    
    @OneToMany(mappedBy = "campaign")
    private List<FeedbackResponse> responses = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void addQuestion(FeedbackQuestion question) {
        questions.add(question);
        question.setCampaign(this);
    }
    
    public void removeQuestion(FeedbackQuestion question) {
        questions.remove(question);
        question.setCampaign(null);
    }
    
    public void updateResponseRate() {
        if (minResponsesRequired != null && minResponsesRequired > 0) {
            responseRate = BigDecimal.valueOf(currentResponses)
                .divide(BigDecimal.valueOf(minResponsesRequired), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
    }
    
    public enum CampaignType {
        TEACHER_EVALUATION,
        FACILITY_ASSESSMENT,
        OVERALL_EXPERIENCE,
        CURRICULUM_FEEDBACK,
        CUSTOM
    }
    
    public enum TargetAudience {
        STUDENTS,
        PARENTS,
        BOTH
    }
}