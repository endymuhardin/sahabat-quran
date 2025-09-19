package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "term_preparation_blockers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermPreparationBlocker {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "id_term", nullable = false)
    private UUID termId;

    @Enumerated(EnumType.STRING)
    @Column(name = "blocker_type", length = 50, nullable = false)
    private BlockerType blockerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20, nullable = false)
    private Severity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private BlockerStatus status = BlockerStatus.OPEN;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum BlockerType {
        TEACHER_ASSIGNMENT, CLASS_SCHEDULING, RESOURCE_ALLOCATION, ENROLLMENT, SYSTEM, OTHER
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum BlockerStatus {
        OPEN, IN_PROGRESS, RESOLVED
    }
}
