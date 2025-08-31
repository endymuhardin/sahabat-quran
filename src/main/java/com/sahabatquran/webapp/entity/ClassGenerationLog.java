package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "class_generation_log")
@Data
@EqualsAndHashCode(exclude = {"term", "proposal", "performedBy"})
@ToString(exclude = {"term", "proposal", "performedBy"})
public class ClassGenerationLog {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term", nullable = false)
    private AcademicTerm term;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proposal")
    private GeneratedClassProposal proposal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;
    
    @Column(name = "action_description", columnDefinition = "TEXT")
    private String actionDescription;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private Map<String, Object> oldData; // Previous state
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private Map<String, Object> newData; // New state
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;
    
    @Column(name = "performed_at")
    private LocalDateTime performedAt;
    
    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
    
    public enum ActionType {
        GENERATION, MANUAL_EDIT, APPROVAL, REJECTION, PUBLICATION
    }
}