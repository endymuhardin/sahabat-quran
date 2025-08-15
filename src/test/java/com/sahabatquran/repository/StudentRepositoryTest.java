package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
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

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveAndFindStudent() {
        Role role = new Role();
        role.setName("STUDENT");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("fulan.student");
        user.setRole(role);
        user.setEmail("fulan.student@sahabatquran.id");

        UserPassword userPassword = new UserPassword();
        userPassword.setPassword("password");
        userPassword.setUser(user);
        user.setPassword(userPassword);

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
