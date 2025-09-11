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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "exam_results")
@Data
@EqualsAndHashCode(exclude = {"exam", "student", "gradedBy", "examAnswers"})
@ToString(exclude = {"exam", "student", "gradedBy", "examAnswers"})
public class ExamResult {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exam", nullable = false)
    private Exam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private User student;
    
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "time_taken_seconds")
    private Long timeTakenSeconds;
    
    @Column(name = "total_score", precision = 8, scale = 2)
    private Double totalScore;
    
    @Column(name = "percentage_score", precision = 5, scale = 2)
    private Double percentageScore;
    
    @Column(name = "points_earned", precision = 8, scale = 2)
    private Double pointsEarned;
    
    @Column(name = "points_possible", precision = 8, scale = 2)
    private Double pointsPossible;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExamResultStatus status = ExamResultStatus.IN_PROGRESS;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 5)
    private Grade grade;
    
    @Column(name = "passed", nullable = false)
    private Boolean passed = false;
    
    @Column(name = "auto_submitted", nullable = false)
    private Boolean autoSubmitted = false;
    
    @Column(name = "instructor_feedback", columnDefinition = "TEXT")
    private String instructorFeedback;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    // JSON field for storing additional metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamAnswer> examAnswers = new ArrayList<>();
    
    public enum ExamResultStatus {
        IN_PROGRESS,    // Student is taking the exam
        SUBMITTED,      // Student submitted, waiting for grading
        GRADED,         // Grading complete
        ABANDONED,      // Student left without submitting
        TIME_EXPIRED    // Time limit exceeded
    }
    
    public enum Grade {
        A_PLUS("A+", 97.0, 100.0),
        A("A", 93.0, 96.9),
        A_MINUS("A-", 90.0, 92.9),
        B_PLUS("B+", 87.0, 89.9),
        B("B", 83.0, 86.9),
        B_MINUS("B-", 80.0, 82.9),
        C_PLUS("C+", 77.0, 79.9),
        C("C", 73.0, 76.9),
        C_MINUS("C-", 70.0, 72.9),
        D_PLUS("D+", 67.0, 69.9),
        D("D", 63.0, 66.9),
        D_MINUS("D-", 60.0, 62.9),
        F("F", 0.0, 59.9);
        
        private final String displayName;
        private final Double minScore;
        private final Double maxScore;
        
        Grade(String displayName, Double minScore, Double maxScore) {
            this.displayName = displayName;
            this.minScore = minScore;
            this.maxScore = maxScore;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public Double getMinScore() {
            return minScore;
        }
        
        public Double getMaxScore() {
            return maxScore;
        }
        
        public static Grade fromPercentageScore(Double percentage) {
            if (percentage == null) return null;
            
            for (Grade grade : values()) {
                if (percentage >= grade.minScore && percentage <= grade.maxScore) {
                    return grade;
                }
            }
            return F;
        }
    }
    
    // Helper methods
    public boolean isCompleted() {
        return completedAt != null;
    }
    
    public boolean isGraded() {
        return status == ExamResultStatus.GRADED;
    }
    
    public boolean needsManualGrading() {
        return status == ExamResultStatus.SUBMITTED && gradedAt == null;
    }
    
    public Duration getTimeTaken() {
        if (timeTakenSeconds != null) {
            return Duration.ofSeconds(timeTakenSeconds);
        }
        if (startedAt != null && completedAt != null) {
            return Duration.between(startedAt, completedAt);
        }
        return Duration.ZERO;
    }
    
    public void calculateScore() {
        if (examAnswers.isEmpty()) {
            this.pointsEarned = 0.0;
            this.totalScore = 0.0;
            this.percentageScore = 0.0;
            return;
        }
        
        double totalEarned = examAnswers.stream()
                .mapToDouble(answer -> answer.getPointsEarned() != null ? answer.getPointsEarned() : 0.0)
                .sum();
        
        double totalPossible = examAnswers.stream()
                .mapToDouble(answer -> answer.getPointsPossible() != null ? answer.getPointsPossible() : 0.0)
                .sum();
        
        this.pointsEarned = totalEarned;
        this.pointsPossible = totalPossible;
        this.totalScore = totalEarned;
        
        if (totalPossible > 0) {
            this.percentageScore = (totalEarned / totalPossible) * 100.0;
            this.grade = Grade.fromPercentageScore(this.percentageScore);
            
            // Check if passed based on exam's passing score
            if (exam != null && exam.getPassingScore() != null) {
                this.passed = this.percentageScore >= exam.getPassingScore();
            }
        } else {
            this.percentageScore = 0.0;
            this.grade = Grade.F;
            this.passed = false;
        }
    }
    
    public void markAsGraded(User gradedByUser) {
        this.status = ExamResultStatus.GRADED;
        this.gradedBy = gradedByUser;
        this.gradedAt = LocalDateTime.now();
    }
    
    public void addAnswer(ExamAnswer answer) {
        examAnswers.add(answer);
        answer.setExamResult(this);
    }
    
    public void removeAnswer(ExamAnswer answer) {
        examAnswers.remove(answer);
        answer.setExamResult(null);
    }
}