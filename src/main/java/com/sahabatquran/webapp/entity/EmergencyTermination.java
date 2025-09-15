package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergency_terminations")
@Data
@EqualsAndHashCode(exclude = {"session", "terminatedBy"})
@ToString(exclude = {"session", "terminatedBy"})
public class EmergencyTermination {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session")
    private ClassSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terminated_by")
    private User terminatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "emergency_type", nullable = false)
    private EmergencyType emergencyType;

    @Column(name = "emergency_reason", columnDefinition = "TEXT", nullable = false)
    private String emergencyReason;

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @Column(name = "notifications_sent")
    private Boolean notificationsSent = false;

    @Column(name = "stakeholder_notification_at")
    private LocalDateTime stakeholderNotificationAt;

    @Column(name = "parent_notification_at")
    private LocalDateTime parentNotificationAt;

    @Column(name = "emergency_report_generated")
    private Boolean emergencyReportGenerated = false;

    @Column(name = "session_data_preserved")
    private Boolean sessionDataPreserved = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum EmergencyType {
        FIRE_EVACUATION("Fire Evacuation"),
        MEDICAL_EMERGENCY("Medical Emergency"),
        NATURAL_DISASTER("Natural Disaster"),
        SECURITY_THREAT("Security Threat"),
        POWER_OUTAGE("Power Outage"),
        BUILDING_EVACUATION("Building Evacuation"),
        WEATHER_EMERGENCY("Weather Emergency"),
        OTHER("Other Emergency");

        private final String displayName;

        EmergencyType(String displayName) {
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
        return "EM" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}