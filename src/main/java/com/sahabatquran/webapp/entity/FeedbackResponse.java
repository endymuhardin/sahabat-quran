package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "feedback_responses")
@Data
@EqualsAndHashCode(exclude = {"campaign", "targetTeacher", "classGroup", "answers"})
@ToString(exclude = {"campaign", "targetTeacher", "classGroup", "answers"})
public class FeedbackResponse {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campaign", nullable = false)
    private FeedbackCampaign campaign;
    
    @Column(name = "anonymous_token", nullable = false, length = 100)
    private String anonymousToken;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_target_teacher")
    private User targetTeacher; // Teacher being evaluated (if applicable)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_group")
    private ClassGroup classGroup; // Class context (if applicable)
    
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;
    
    @Column(name = "is_complete")
    private Boolean isComplete = false;
    
    @Column(name = "device_info", length = 200)
    private String deviceInfo;
    
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackAnswer> answers = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        submissionDate = LocalDateTime.now();
        if (anonymousToken == null) {
            anonymousToken = generateAnonymousToken();
        }
    }
    
    private String generateAnonymousToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
    
    public void addAnswer(FeedbackAnswer answer) {
        answers.add(answer);
        answer.setResponse(this);
    }
    
    public void removeAnswer(FeedbackAnswer answer) {
        answers.remove(answer);
        answer.setResponse(null);
    }
    
    public void markComplete() {
        this.isComplete = true;
        this.submissionDate = LocalDateTime.now();
    }
}