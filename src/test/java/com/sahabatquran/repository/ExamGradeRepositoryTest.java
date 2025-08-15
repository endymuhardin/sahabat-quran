package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ExamGradeRepositoryTest {

    @Autowired
    private ExamGradeRepository examGradeRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindExamGrade() {
        Classroom classroom = new Classroom();
        classroom.setName("Kelas Nilai");
        classroomRepository.saveAndFlush(classroom);

        Exam exam = new Exam();
        exam.setName("Ujian Bulanan");
        exam.setClassroom(classroom);
        examRepository.saveAndFlush(exam);

        Role role = new Role();
        role.setName("STUDENT");
        roleRepository.save(role);

        User studentUser = new User();
        studentUser.setUsername("fulan.student");
        studentUser.setRole(role);
        studentUser.setEmail("fulan.student@sahabatquran.id");

        UserPassword userPassword = new UserPassword();
        userPassword.setPassword("password");
        userPassword.setUser(studentUser);
        studentUser.setPassword(userPassword);

        userRepository.save(studentUser);

        Student student = new Student();
        student.setUser(studentUser);
        studentRepository.saveAndFlush(student);

        ExamGrade grade = new ExamGrade();
        grade.setExam(exam);
        grade.setStudent(student);
        grade.setGrade(95.5f);
        examGradeRepository.saveAndFlush(grade);

        ExamGrade found = examGradeRepository.findById(grade.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getGrade()).isEqualTo(95.5f);
    }
}
