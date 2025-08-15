package com.sahabatquran.repository;

import com.sahabatquran.domain.Classroom;
import com.sahabatquran.domain.Exam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ExamRepositoryTest {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Test
    void testSaveAndFindExam() {
        Classroom classroom = new Classroom();
        classroom.setName("Kelas Ujian");
        classroomRepository.saveAndFlush(classroom);

        Exam exam = new Exam();
        exam.setName("Ujian Akhir Semester");
        exam.setClassroom(classroom);
        exam.setExamDate(LocalDateTime.now());
        examRepository.saveAndFlush(exam);

        Exam found = examRepository.findById(exam.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Ujian Akhir Semester");
    }
}
