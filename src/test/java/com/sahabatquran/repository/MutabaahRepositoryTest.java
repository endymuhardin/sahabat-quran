package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MutabaahRepositoryTest {

    @Autowired
    private MutabaahRepository mutabaahRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindMutabaah() {
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

        Curriculum curriculum = new Curriculum();
        curriculum.setName("Hafalan");
        curriculumRepository.saveAndFlush(curriculum);

        Mutabaah mutabaah = new Mutabaah();
        mutabaah.setStudent(student);
        mutabaah.setCurriculum(curriculum);
        mutabaah.setNotes("Sudah hafal juz 30");
        mutabaah.setRecordDate(LocalDate.now());
        mutabaahRepository.saveAndFlush(mutabaah);

        Mutabaah found = mutabaahRepository.findById(mutabaah.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getNotes()).isEqualTo("Sudah hafal juz 30");
    }
}
