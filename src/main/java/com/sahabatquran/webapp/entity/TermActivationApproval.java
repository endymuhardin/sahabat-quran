package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "term_activation_approval")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermActivationApproval {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "id_term", nullable = false, unique = true)
    private UUID termId;

    private boolean teachersAssignedConfirmed;
    private boolean studentsEnrolledConfirmed;
    private boolean schedulesPlottedConfirmed;
    private boolean resourcesAllocatedConfirmed;
    private boolean systemsReadyConfirmed;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private ActivationStatus status = ActivationStatus.PENDING;

    @Column(name = "approval_comments", columnDefinition = "TEXT")
    private String approvalComments;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public enum ActivationStatus { PENDING, APPROVED, REJECTED }
}
