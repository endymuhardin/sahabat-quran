package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "substitute_assignments")
@Data
@EqualsAndHashCode(exclude = {"classSession", "originalTeacher", "substituteTeacher", "assignedBy"})
@ToString(exclude = {"classSession", "originalTeacher", "substituteTeacher", "assignedBy"})
public class SubstituteAssignment {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_session", nullable = false)
    private ClassSession classSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_original_teacher", nullable = false)
    private User originalTeacher;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_substitute_teacher", nullable = false)
    private User substituteTeacher;
    
    @Column(name = "assignment_type", length = 20)
    @Enumerated(EnumType.STRING)
    private AssignmentType assignmentType;
    
    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assigned_by", nullable = false)
    private User assignedBy;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "materials_shared")
    private Boolean materialsShared = false;
    
    @Column(name = "materials_shared_at")
    private LocalDateTime materialsSharedAt;
    
    @Column(name = "substitute_accepted")
    private Boolean substituteAccepted;
    
    @Column(name = "acceptance_time")
    private LocalDateTime acceptanceTime;
    
    @Column(name = "compensation_amount", precision = 10, scale = 2)
    private BigDecimal compensationAmount;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Column(name = "performance_rating", precision = 3, scale = 2)
    private BigDecimal performanceRating;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        assignmentDate = LocalDateTime.now();
    }
    
    public enum AssignmentType {
        EMERGENCY,
        PLANNED,
        TEMPORARY
    }
}