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
@Table(name = "session_reschedule_requests")
@Data
@EqualsAndHashCode(exclude = {"classSession", "requestedBy", "approvedBy"})
@ToString(exclude = {"classSession", "requestedBy", "approvedBy"})
public class SessionRescheduleRequest {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_class_session", nullable = false)
    private ClassSession classSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_requested_by", nullable = false)
    private User requestedBy;
    
    @Column(name = "original_date", nullable = false)
    private LocalDate originalDate;
    
    @Column(name = "original_time", length = 20)
    private String originalTime;
    
    @Column(name = "proposed_date", nullable = false)
    private LocalDate proposedDate;
    
    @Column(name = "proposed_time", length = 20)
    private String proposedTime;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RescheduleReason reason;
    
    @Column(name = "reason_details", columnDefinition = "TEXT")
    private String reasonDetails;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private RescheduleStatus status = RescheduleStatus.PENDING;
    
    @Column(name = "auto_approved")
    private Boolean autoApproved = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_approved_by")
    private User approvedBy;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;
    
    @Column(name = "students_notified")
    private Boolean studentsNotified = false;
    
    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_new_session")
    private ClassSession newSession;
    
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
    
    public enum RescheduleReason {
        TEACHER_ILLNESS,
        EMERGENCY,
        HOLIDAY,
        FACILITY_ISSUE,
        OTHER
    }
    
    public enum RescheduleStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}