package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for tracking individual report generation items within a batch
 */
@Entity
@Table(name = "report_generation_items")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(exclude = {"batch"})
@ToString(exclude = {"batch"})
public class ReportGenerationItem {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_batch", nullable = false)
    private ReportGenerationBatch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_group")
    private ClassGroup classGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher")
    private User teacher;

    @Column(name = "report_subject", nullable = false, length = 100)
    private String reportSubject; // e.g., "Student Report - Ali Ahmad", "Class Summary - Level 1A"

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 100)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ItemStatus status = ItemStatus.PENDING;

    @Column(name = "priority")
    private Integer priority = 5; // 1-10, where 1 is highest priority

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "processing_duration_seconds")
    private Integer processingDurationSeconds;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;

    @Column(name = "validation_errors", columnDefinition = "TEXT")
    private String validationErrors;

    @Column(name = "generation_metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    private String generationMetadata; // JSON string for additional metadata

    @Column(name = "distributed")
    private Boolean distributed = false;

    @Column(name = "distributed_at")
    private LocalDateTime distributedAt;

    @Column(name = "distribution_method", length = 50)
    private String distributionMethod; // EMAIL, PORTAL, DOWNLOAD

    @Column(name = "recipient_email", length = 100)
    private String recipientEmail;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ItemStatus {
        PENDING,       // Waiting to be processed
        VALIDATING,    // Validating data for report generation
        GENERATING,    // Currently being generated
        COMPLETED,     // Successfully generated
        FAILED,        // Generation failed
        RETRYING,      // Being retried after failure
        CANCELLED      // Cancelled by system or user
    }

    public enum ReportType {
        STUDENT_REPORT,          // Individual student report
        CLASS_SUMMARY,           // Class summary report
        TEACHER_EVALUATION,      // Teacher evaluation report
        PARENT_NOTIFICATION,     // Parent notification report
        MANAGEMENT_SUMMARY       // Management summary report
    }

    // Convenience methods
    public boolean isCompleted() {
        return status == ItemStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ItemStatus.FAILED;
    }

    public boolean canBeRetried() {
        return status == ItemStatus.FAILED && retryCount < 3;
    }

    public void markAsStarted() {
        this.status = ItemStatus.GENERATING;
        this.startedAt = LocalDateTime.now();
    }

    public void markAsCompleted(String filePath, Long fileSizeBytes) {
        this.status = ItemStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.filePath = filePath;
        this.fileSizeBytes = fileSizeBytes;

        if (startedAt != null) {
            this.processingDurationSeconds = (int) java.time.Duration.between(startedAt, completedAt).getSeconds();
        }
    }

    public void markAsFailed(String errorMessage) {
        this.status = ItemStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
        this.lastRetryAt = LocalDateTime.now();
    }
}