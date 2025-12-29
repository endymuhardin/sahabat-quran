package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_alerts")
@Data
@EqualsAndHashCode(exclude = {"classSession", "teacher", "resolvedBy"})
@ToString(exclude = {"classSession", "teacher", "resolvedBy"})
public class SystemAlert {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @Column(name = "alert_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AlertType alertType;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private Severity severity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_session")
    private ClassSession classSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher")
    private User teacher;
    
    @Column(name = "alert_message", nullable = false, columnDefinition = "TEXT")
    private String alertMessage;
    
    @Column(name = "is_resolved")
    private Boolean isResolved = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void resolve(User resolver, String notes) {
        this.isResolved = true;
        this.resolvedBy = resolver;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNotes = notes;
    }
    
    public enum AlertType {
        LATE_CHECK_IN,
        NO_CHECK_IN,
        LOW_ATTENDANCE,
        SESSION_NOT_STARTED,
        SUBSTITUTE_NEEDED,
        TECHNICAL_ISSUE
    }
    
    public enum Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}