package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "teacher_availability_change_request")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(exclude = {"teacher", "term", "reviewedBy"})
@ToString(exclude = {"teacher", "term", "reviewedBy"})
public class TeacherAvailabilityChangeRequest {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher", nullable = false)
    private User teacher;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term", nullable = false)
    private AcademicTerm term;
    
    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "requested_changes", columnDefinition = "jsonb")
    private List<AvailabilityChange> requestedChanges;
    
    @Column(name = "original_max_classes")
    private Integer originalMaxClasses;
    
    @Column(name = "new_max_classes")
    private Integer newMaxClasses;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reviewed_by")
    private User reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "review_comments", columnDefinition = "TEXT")
    private String reviewComments;
    
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
    
    public enum RequestStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }
    
    @Data
    public static class AvailabilityChange {
        private String changeType; // ADD, REMOVE, MODIFY
        private String dayOfWeek;
        private String sessionCode;
        private String sessionName;
        private Boolean newAvailability;
        private String description;
    }
}