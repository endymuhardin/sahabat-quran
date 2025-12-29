package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "substitute_teachers")
@Data
@EqualsAndHashCode(exclude = {"teacher"})
@ToString(exclude = {"teacher"})
public class SubstituteTeacher {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_teacher", nullable = false, unique = true)
    private User teacher;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Column(name = "emergency_available")
    private Boolean emergencyAvailable = false;
    
    
    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Column(name = "total_substitutions")
    private Integer totalSubstitutions = 0;
    
    @Column(name = "last_substitution_date")
    private LocalDate lastSubstitutionDate;
    
    @Column(name = "contact_preference", length = 20)
    @Enumerated(EnumType.STRING)
    private ContactPreference contactPreference;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
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
    
    public enum ContactPreference {
        SMS,
        WHATSAPP,
        PHONE,
        EMAIL
    }
}