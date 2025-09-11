package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exams")
@Data
@EqualsAndHashCode(exclude = {"classGroup", "createdBy", "questions", "examResults"})
@ToString(exclude = {"classGroup", "createdBy", "questions", "examResults"})
public class Exam {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false, length = 20)
    private ExamType examType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_group", nullable = false)
    private ClassGroup classGroup;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_academic_term", nullable = false)
    private AcademicTerm academicTerm;
    
    @Column(name = "scheduled_start", nullable = false)
    private LocalDateTime scheduledStart;
    
    @Column(name = "scheduled_end", nullable = false)
    private LocalDateTime scheduledEnd;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 1;
    
    @Column(name = "passing_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal passingScore = BigDecimal.valueOf(60.0);
    
    @Column(name = "total_points", nullable = false, precision = 8, scale = 2)
    private BigDecimal totalPoints = BigDecimal.valueOf(100.0);
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExamStatus status = ExamStatus.DRAFT;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "allow_review", nullable = false)
    private Boolean allowReview = true;
    
    @Column(name = "randomize_questions", nullable = false)
    private Boolean randomizeQuestions = false;
    
    @Column(name = "show_results_immediately", nullable = false)
    private Boolean showResultsImmediately = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestion> questions = new ArrayList<>();
    
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamResult> examResults = new ArrayList<>();
    
    public enum ExamType {
        PLACEMENT, MIDTERM, FINAL, QUIZ, PRACTICE
    }
    
    public enum ExamStatus {
        DRAFT, SCHEDULED, ACTIVE, COMPLETED, CANCELLED
    }
    
    // Helper methods
    public boolean isActive() {
        return status == ExamStatus.ACTIVE;
    }
    
    public boolean isEditable() {
        return status == ExamStatus.DRAFT || status == ExamStatus.SCHEDULED;
    }
    
    public boolean canTakeExam() {
        LocalDateTime now = LocalDateTime.now();
        return status == ExamStatus.ACTIVE && 
               now.isAfter(scheduledStart) && 
               now.isBefore(scheduledEnd);
    }
    
    public boolean isCompleted() {
        return status == ExamStatus.COMPLETED;
    }
    
    public void addQuestion(ExamQuestion question) {
        questions.add(question);
        question.setExam(this);
    }
    
    public void removeQuestion(ExamQuestion question) {
        questions.remove(question);
        question.setExam(null);
    }
}