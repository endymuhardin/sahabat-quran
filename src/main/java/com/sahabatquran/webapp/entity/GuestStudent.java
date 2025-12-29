package com.sahabatquran.webapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "guest_students")
@Data
@EqualsAndHashCode(exclude = {"session", "addedBy"})
@ToString(exclude = {"session", "addedBy"})
public class GuestStudent {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session")
    private ClassSession session;

    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "guest_type", nullable = false)
    private GuestType guestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_added_by")
    private User addedBy;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum GuestType {
        TRIAL_CLASS("Trial Class"),
        MAKEUP_CLASS("Make-up Class"),
        VISITOR("Visitor"),
        SUBSTITUTE("Substitute Student"),
        OTHER("Other");

        private final String displayName;

        GuestType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}