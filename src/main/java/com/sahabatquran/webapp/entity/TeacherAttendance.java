package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_attendance")
@Data
@EqualsAndHashCode(exclude = {"classSession", "scheduledInstructor", "actualInstructor"})
@ToString(exclude = {"classSession", "scheduledInstructor", "actualInstructor"})
public class TeacherAttendance {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_session", nullable = false)
    private ClassSession classSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_scheduled_instructor", nullable = false)
    private User scheduledInstructor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actual_instructor")
    private User actualInstructor; // NULL if absent, different if substitute
    
    // Basic attendance fields
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    
    @Column(name = "is_present")
    private Boolean isPresent = false;
    
    @Column(name = "absence_reason", length = 200)
    private String absenceReason;
    
    @Column(name = "substitute_arranged")
    private Boolean substituteArranged = false;
    
    @Column(name = "substitute_notes", columnDefinition = "TEXT")
    private String substituteNotes;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // Daily Operations fields
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "check_in_location", length = 200)
    private String checkInLocation;
    
    @Column(name = "session_completed")
    private Boolean sessionCompleted = false;
    
    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;
    
    @Column(name = "substitute_reason", length = 50)
    @Enumerated(EnumType.STRING)
    private SubstituteReason substituteReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void checkIn(String location) {
        this.checkInTime = LocalDateTime.now();
        this.checkInLocation = location;
        this.isPresent = true;
        this.arrivalTime = this.checkInTime;
    }
    
    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
        this.departureTime = this.checkOutTime;
        this.sessionCompleted = true;
    }
    
    public enum SubstituteReason {
        ILLNESS,
        EMERGENCY,
        PLANNED_LEAVE,
        OTHER
    }
}