package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "generated_class_proposals")
@Data
@EqualsAndHashCode(exclude = {"term", "generatedBy", "approvedBy"})
@ToString(exclude = {"term", "generatedBy", "approvedBy"})
public class GeneratedClassProposal {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term", nullable = false)
    private AcademicTerm term;
    
    @Column(name = "generation_run", nullable = false)
    private Integer generationRun;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "proposal_data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> proposalData; // Complete class schedule proposal
    
    @Column(name = "optimization_score", precision = 5, scale = 2)
    private BigDecimal optimizationScore;
    
    @Column(name = "conflict_count")
    private Integer conflictCount = 0;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "size_violations", columnDefinition = "jsonb")
    private Map<String, Object> sizeViolations; // Classes violating size constraints
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "manual_overrides", columnDefinition = "jsonb")
    private Map<String, Object> manualOverrides; // Manual changes made
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "generation_parameters", columnDefinition = "jsonb")
    private Map<String, Object> generationParameters; // Parameters used for generation
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private User generatedBy;
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
    
    @Column(name = "is_approved")
    private Boolean isApproved = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}