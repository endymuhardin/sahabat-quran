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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "student_registrations")
@Data
@EqualsAndHashCode(exclude = {"program", "placementVerse", "sessionPreferences", "reviewedBy", "placementEvaluatedBy"})
@ToString(exclude = {"program", "placementVerse", "sessionPreferences", "reviewedBy", "placementEvaluatedBy"})
public class StudentRegistration {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    // Personal Information
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;
    
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(name = "place_of_birth", nullable = false, length = 100)
    private String placeOfBirth;
    
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "emergency_contact_name", nullable = false, length = 150)
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone", nullable = false, length = 20)
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relation", nullable = false, length = 50)
    private String emergencyContactRelation;
    
    // Educational Information
    @Column(name = "education_level", nullable = false, length = 50)
    private String educationLevel;
    
    @Column(name = "school_name", length = 150)
    private String schoolName;
    
    @Column(name = "quran_reading_experience", columnDefinition = "TEXT")
    private String quranReadingExperience;
    
    @Column(name = "previous_tahsin_experience", nullable = false)
    private Boolean previousTahsinExperience = false;
    
    @Column(name = "previous_tahsin_details", columnDefinition = "TEXT")
    private String previousTahsinDetails;
    
    // Program Selection
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_program", nullable = false)
    private Program program;
    
    @Column(name = "registration_reason", columnDefinition = "TEXT")
    private String registrationReason;
    
    @Column(name = "learning_goals", columnDefinition = "TEXT")
    private String learningGoals;
    
    // Schedule Preferences (JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schedule_preferences", nullable = false, columnDefinition = "jsonb")
    private String schedulePreferences;
    
    // Placement Test Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_placement_verse")
    private PlacementTestVerse placementVerse;
    
    @Column(name = "recording_drive_link", length = 500)
    private String recordingDriveLink;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "placement_test_status", nullable = false, length = 20)
    private PlacementTestStatus placementTestStatus = PlacementTestStatus.PENDING;
    
    @Column(name = "placement_result")
    private Integer placementResult;
    
    @Column(name = "placement_notes", columnDefinition = "TEXT")
    private String placementNotes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_evaluated_by")
    private User placementEvaluatedBy;
    
    @Column(name = "placement_evaluated_at")
    private LocalDateTime placementEvaluatedAt;
    
    // Registration Status and Metadata
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 20)
    private RegistrationStatus registrationStatus = RegistrationStatus.DRAFT;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;
    
    // Session Preferences
    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<StudentSessionPreference> sessionPreferences = new HashSet<>();
    
    // Audit Fields
    @Column(name = "created_at", updatable = false)
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
    
    // Enums
    public enum Gender {
        MALE, FEMALE
    }
    
    public enum RegistrationStatus {
        DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    }
    
    public enum PlacementTestStatus {
        PENDING, SUBMITTED, EVALUATED
    }
}