package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_level_assignments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_teacher", "id_level", "id_term"}))
@Data
@EqualsAndHashCode(exclude = {"teacher", "level", "term", "assignedBy"})
@ToString(exclude = {"teacher", "level", "term", "assignedBy"})
public class TeacherLevelAssignment {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher", nullable = false)
    private User teacher;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_level", nullable = false)
    private Level level;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term", nullable = false)
    private AcademicTerm term;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "competency_level", nullable = false, length = 20)
    private CompetencyLevel competencyLevel;
    
    @Column(name = "max_classes_for_level")
    private Integer maxClassesForLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Specialization specialization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
    
    public enum CompetencyLevel {
        JUNIOR, SENIOR, EXPERT
    }
    
    public enum Specialization {
        FOUNDATION, REMEDIAL, ADVANCED, MIXED
    }
}