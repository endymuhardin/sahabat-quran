package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Data
@EqualsAndHashCode(exclude = {"enrollment", "createdBy"})
@ToString(exclude = {"enrollment", "createdBy"})
public class Attendance {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_enrollment", nullable = false)
    private Enrollment enrollment;
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Column(name = "is_present")
    private Boolean isPresent = false;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_created_by")
    private User createdBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Convenience methods
    public boolean isPresent() {
        return Boolean.TRUE.equals(isPresent);
    }
    
    public void markPresent() {
        this.isPresent = true;
    }
    
    public void markAbsent() {
        this.isPresent = false;
    }
}