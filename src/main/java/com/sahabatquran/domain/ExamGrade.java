package com.sahabatquran.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@Entity
@Table(name = "exam_grades")
public class ExamGrade {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_exam", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    private Float grade;
}
