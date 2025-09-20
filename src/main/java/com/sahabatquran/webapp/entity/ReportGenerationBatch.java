package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity for tracking bulk report generation batches
 */
@Entity
@Table(name = "report_generation_batches")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(exclude = {"reportGenerationItems"})
@ToString(exclude = {"reportGenerationItems"})
public class ReportGenerationBatch {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term", nullable = false)
    private AcademicTerm term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_initiated_by", nullable = false)
    private User initiatedBy;

    @Column(name = "batch_name", nullable = false, length = 100)
    private String batchName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BatchStatus status = BatchStatus.INITIATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    private ReportType reportType;

    @Column(name = "total_reports")
    private Integer totalReports;

    @Column(name = "completed_reports")
    private Integer completedReports = 0;

    @Column(name = "failed_reports")
    private Integer failedReports = 0;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "generation_notes", columnDefinition = "TEXT")
    private String generationNotes;

    @Column(name = "distribution_completed")
    private Boolean distributionCompleted = false;

    @Column(name = "distribution_completed_at")
    private LocalDateTime distributionCompletedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReportGenerationItem> reportGenerationItems = new HashSet<>();

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (initiatedAt == null) {
            initiatedAt = LocalDateTime.now();
        }
    }

    public enum BatchStatus {
        INITIATED,     // Batch created, not started
        VALIDATING,    // Validating data completeness
        IN_PROGRESS,   // Reports being generated
        COMPLETED,     // All reports generated successfully
        FAILED,        // Batch failed
        CANCELLED      // Batch cancelled by user
    }

    public enum ReportType {
        SEMESTER_END_STUDENT_REPORTS,
        CLASS_PERFORMANCE_SUMMARIES,
        TEACHER_EVALUATION_SUMMARIES,
        PARENT_NOTIFICATION_PACKAGES,
        MANAGEMENT_EXECUTIVE_SUMMARY,
        CUSTOM_REPORT_BATCH
    }

    // Convenience methods
    public double getCompletionPercentage() {
        if (totalReports == null || totalReports == 0) {
            return 0.0;
        }
        return (completedReports * 100.0) / totalReports;
    }

    public boolean isCompleted() {
        return status == BatchStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == BatchStatus.IN_PROGRESS || status == BatchStatus.VALIDATING;
    }

    public boolean canBeCancelled() {
        return status == BatchStatus.INITIATED || status == BatchStatus.VALIDATING;
    }
}