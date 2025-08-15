package com.sahabatquran.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "class_sessions")
public class ClassSession {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_class_schedule", nullable = false)
    private ClassSchedule classSchedule;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    private String notes;

    private String status;
}
