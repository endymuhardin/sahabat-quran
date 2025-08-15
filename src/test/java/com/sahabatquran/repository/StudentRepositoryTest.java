package com.sahabatquran.repository;

import com.sahabatquran.domain.Student;
import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindStudent() {
        User user = new User();
        user.setUsername("fulan.student");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);
        user.setEmail("fulan.student@sahabatquran.id");
        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setAddress("Student Address");
        studentRepository.saveAndFlush(student);

        Student found = studentRepository.findById(student.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getAddress()).isEqualTo("Student Address");
    }
}
