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
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_teacher", "id_term", "day_of_week", "session_time"}))
@Data
@EqualsAndHashCode(exclude = {"teacher", "term"})
@ToString(exclude = {"teacher", "term"})
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
    
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 1=Monday, 7=Sunday
    
    @Enumerated(EnumType.STRING)
    @Column(name = "session_time", nullable = false, length = 20)
    private SessionTime sessionTime;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = false;
    
    @Column(name = "max_classes_per_week")
    private Integer maxClassesPerWeek = 6;
    
    @Column(columnDefinition = "TEXT")
    private String preferences;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum SessionTime {
        PAGI_AWAL, PAGI, SIANG, SORE, MALAM
    }
}