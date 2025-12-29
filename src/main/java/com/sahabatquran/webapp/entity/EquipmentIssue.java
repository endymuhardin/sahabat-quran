package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing equipment issues reported during sessions
 */
@Entity
@Table(name = "equipment_issues")
@Data
@EqualsAndHashCode(exclude = {"session", "reportedBy"})
@ToString(exclude = {"session", "reportedBy"})
public class EquipmentIssue {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session")
    private ClassSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reported_by")
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @Column(name = "issue_description", columnDefinition = "TEXT", nullable = false)
    private String issueDescription;

    @Column(name = "is_urgent", nullable = false)
    private Boolean isUrgent = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IssueStatus status = IssueStatus.REPORTED;

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @Column(name = "maintenance_notified_at")
    private LocalDateTime maintenanceNotifiedAt;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EquipmentType {
        PROJECTOR("Projector"),
        SOUND_SYSTEM("Sound System"),
        MICROPHONE("Microphone"),
        COMPUTER("Computer"),
        LIGHTING("Lighting"),
        AIR_CONDITIONING("Air Conditioning"),
        WHITEBOARD("Whiteboard"),
        FURNITURE("Furniture"),
        OTHER("Other");

        private final String displayName;

        EquipmentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum IssueStatus {
        REPORTED("Reported"),
        MAINTENANCE_NOTIFIED("Maintenance Notified"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved"),
        CANCELLED("Cancelled");

        private final String displayName;

        IssueStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        if (trackingNumber == null) {
            trackingNumber = generateTrackingNumber();
        }
    }

    private String generateTrackingNumber() {
        return "EQ" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}