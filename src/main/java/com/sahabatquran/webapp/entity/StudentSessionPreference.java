package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_session_preferences")
@Data
@EqualsAndHashCode(exclude = {"registration", "session"})
@ToString(exclude = {"registration", "session"})
public class StudentSessionPreference {
    
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registration", nullable = false)
    private StudentRegistration registration;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = false)
    private Session session;
    
    @Column(name = "preference_priority", nullable = false)
    private Integer preferencePriority;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_days", nullable = false, columnDefinition = "jsonb")
    private String preferredDays;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}