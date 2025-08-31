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
@Table(name = "class_groups")
@Data
@EqualsAndHashCode(exclude = {"level", "instructor", "term", "enrollments", "classSessions"})
@ToString(exclude = {"level", "instructor", "term", "enrollments", "classSessions"})
public class ClassGroup {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_level", nullable = false)
    private Level level;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instructor")
    private User instructor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term")
    private AcademicTerm term;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(name = "min_students")
    private Integer minStudents = 7;
    
    @Column(name = "max_students")
    private Integer maxStudents = 10;
    
    @Column(length = 100)
    private String schedule;
    
    @Column(length = 100)
    private String location;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "size_override_reason", columnDefinition = "TEXT")
    private String sizeOverrideReason;
    
    @Column(name = "is_undersized_approved")
    private Boolean isUndersizedApproved = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "student_category_mix", length = 20)
    private StudentCategoryMix studentCategoryMix = StudentCategoryMix.MIXED;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "class_type", length = 30)
    private ClassType classType = ClassType.STANDARD;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();
    
    @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ClassSession> classSessions = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum StudentCategoryMix {
        NEW_ONLY, EXISTING_ONLY, MIXED
    }
    
    public enum ClassType {
        FOUNDATION, STANDARD, REMEDIAL, ADVANCED
    }
}