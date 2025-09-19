package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_allocation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAllocation {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "id_term", nullable = false, unique = true)
    private UUID termId;

    @Column(name = "classrooms_building_a")
    private Integer classroomsBuildingA;

    @Column(name = "classrooms_building_b")
    private Integer classroomsBuildingB;

    @Column(name = "materials_budget")
    private Double materialsBudget;

    @Column(name = "technology_budget")
    private Double technologyBudget;

    @Column(name = "miscellaneous_budget")
    private Double miscellaneousBudget;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private AllocationStatus status = AllocationStatus.DRAFT;

    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum AllocationStatus { DRAFT, SUBMITTED, APPROVED }
}
