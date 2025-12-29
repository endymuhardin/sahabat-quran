package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parent_notifications")
@Data
@EqualsAndHashCode(exclude = {"student", "parent", "relatedSession"})
@ToString(exclude = {"student", "parent", "relatedSession"})
public class ParentNotification {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent")
    private User parent; // NULL if sent to phone/email directly
    
    @Column(name = "notification_type", length = 50)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    
    @Column(length = 200)
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "delivery_method", length = 20)
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;
    
    @Column(name = "recipient_contact", length = 100)
    private String recipientContact; // Phone or email
    
    @Column(name = "is_sent")
    private Boolean isSent = false;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_related_session")
    private ClassSession relatedSession;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        TEACHER_CHANGE,
        SESSION_RESCHEDULE,
        PROGRESS_UPDATE,
        ATTENDANCE_ALERT,
        GENERAL
    }
    
    public enum DeliveryMethod {
        SMS,
        EMAIL,
        WHATSAPP,
        IN_APP
    }
}