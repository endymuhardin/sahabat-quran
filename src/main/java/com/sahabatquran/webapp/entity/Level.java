package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "levels")
@Data
@EqualsAndHashCode(exclude = {"classGroups", "teacherLevelAssignments", "studentAssessments"})
@ToString(exclude = {"classGroups", "teacherLevelAssignments", "studentAssessments"})
public class Level {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassGroup> classGroups = new HashSet<>();
    
    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeacherLevelAssignment> teacherLevelAssignments = new HashSet<>();
    
    @OneToMany(mappedBy = "determinedLevel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StudentAssessment> studentAssessments = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}