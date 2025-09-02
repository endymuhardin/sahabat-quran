package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_assessments")
@Data
@EqualsAndHashCode(exclude = {"student", "term", "determinedLevel", "previousClassGroup", "assessedBy", "validatedBy"})
@ToString(exclude = {"student", "term", "determinedLevel", "previousClassGroup", "assessedBy", "validatedBy"})
public class StudentAssessment {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_term", nullable = false)
    private AcademicTerm term;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "student_category", nullable = false, length = 20)
    private StudentCategory studentCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false, length = 30)
    private AssessmentType assessmentType;
    
    @Column(name = "assessment_score", precision = 5, scale = 2)
    private BigDecimal assessmentScore;
    
    @Column(name = "assessment_grade", length = 5)
    private String assessmentGrade; // 'A', 'B', 'C', 'D' for exams
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "determined_level")
    private Level determinedLevel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_class_group")
    private ClassGroup previousClassGroup; // for existing students
    
    @Column(name = "assessment_date")
    private LocalDate assessmentDate;
    
    @Column(name = "assessment_notes", columnDefinition = "TEXT")
    private String assessmentNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessed_by")
    private User assessedBy;
    
    @Column(name = "is_validated")
    private Boolean isValidated = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;
    
    @Column(name = "validated_at")
    private LocalDateTime validatedAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum StudentCategory {
        NEW, EXISTING
    }
    
    public enum AssessmentType {
        PLACEMENT, MIDTERM, FINAL
    }
}