package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "class_sessions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_class_group", "session_date"}))
@Data
@EqualsAndHashCode(exclude = {"classGroup", "instructor", "sessionMaterials", "preparationChecklistItems", "createdAt", "updatedAt"})
@ToString(exclude = {"classGroup", "instructor", "sessionMaterials", "preparationChecklistItems", "learningObjectives", "createdAt", "updatedAt"})
public class ClassSession {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_group", nullable = false)
    private ClassGroup classGroup;
    
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;
    
    @Column(name = "session_number")
    private Integer sessionNumber;
    
    @ElementCollection
    @CollectionTable(name = "class_session_objectives", joinColumns = @JoinColumn(name = "class_session_id"))
    @Column(name = "learning_objective", columnDefinition = "TEXT")
    private List<String> learningObjectives = new ArrayList<>();
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "teaching_materials", columnDefinition = "jsonb")
    private Map<String, Object> teachingMaterials;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "preparation_status", length = 20)
    private PreparationStatus preparationStatus = PreparationStatus.DRAFT;
    
    // Daily Operations Fields
    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", length = 30)
    private SessionStatus sessionStatus = SessionStatus.SCHEDULED;
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "objectives_achieved", columnDefinition = "jsonb")
    private Map<String, Boolean> objectivesAchieved;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attendance_summary", columnDefinition = "jsonb")
    private Map<String, Object> attendanceSummary;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instructor", nullable = false)
    private User instructor;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SessionMaterial> sessionMaterials = new HashSet<>();
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassPreparationChecklist> preparationChecklistItems = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PreparationStatus {
        DRAFT, IN_PROGRESS, READY, COMPLETED
    }
    
    public enum SessionStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        RESCHEDULED
    }
    
    // Convenience methods for session execution
    public void startSession() {
        this.sessionStatus = SessionStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }
    
    public void completeSession() {
        this.sessionStatus = SessionStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
    }
    
    public void cancelSession() {
        this.sessionStatus = SessionStatus.CANCELLED;
    }
    
    public void rescheduleSession() {
        this.sessionStatus = SessionStatus.RESCHEDULED;
    }
}