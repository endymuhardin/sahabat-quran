package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "academic_terms")
@Data
@EqualsAndHashCode(exclude = {"teacherAvailabilities", "teacherLevelAssignments", "classGroups", "generatedClassProposals", "studentAssessments", "classGenerationLogs"})
@ToString(exclude = {"teacherAvailabilities", "teacherLevelAssignments", "classGroups", "generatedClassProposals", "studentAssessments", "classGenerationLogs"})
public class AcademicTerm {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "term_name", nullable = false, length = 50)
    private String termName;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TermStatus status = TermStatus.PLANNING;
    
    @Column(name = "preparation_deadline")
    private LocalDate preparationDeadline;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeacherAvailability> teacherAvailabilities = new HashSet<>();
    
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeacherLevelAssignment> teacherLevelAssignments = new HashSet<>();
    
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassGroup> classGroups = new HashSet<>();
    
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GeneratedClassProposal> generatedClassProposals = new HashSet<>();
    
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StudentAssessment> studentAssessments = new HashSet<>();
    
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassGenerationLog> classGenerationLogs = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum TermStatus {
        PLANNING, ACTIVE, COMPLETED
    }
}