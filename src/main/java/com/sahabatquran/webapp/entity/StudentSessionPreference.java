package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_session_preferences")
@Data
@EqualsAndHashCode(exclude = {"registration", "timeSlot"})
@ToString(exclude = {"registration", "timeSlot"})
public class StudentSessionPreference {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registration", nullable = false)
    private StudentRegistration registration;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_time_slot")
    private TimeSlot timeSlot;
    
    @Column(name = "preference_priority", nullable = false)
    private Integer preferencePriority;
    
    // Removed legacy fields: session and preferredDays (JSON). TimeSlot captures both day and session.
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}