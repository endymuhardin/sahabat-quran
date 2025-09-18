package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_availability", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_teacher", "id_term", "id_time_slot"}))
@Data
@EqualsAndHashCode(exclude = {"teacher", "term", "timeSlot"})
@ToString(exclude = {"teacher", "term", "timeSlot"})
public class TeacherAvailability {
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_time_slot")
    private TimeSlot timeSlot;
    
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "capacity")
    private Integer capacity = 1;
    
    @Column(name = "max_classes_per_week")
    private Integer maxClassesPerWeek = 6;
    
    @Column(columnDefinition = "TEXT")
    private String preferences;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Day of week and session are now encapsulated by TimeSlot
    
}