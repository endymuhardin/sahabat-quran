package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveAndFindTeacher() {
        Role role = new Role();
        role.setName("TEACHER");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("ust.fulan");
        user.setRole(role);
        user.setEmail("ust.fulan@sahabatquran.id");

        UserPassword userPassword = new UserPassword();
        userPassword.setPassword("password");
        userPassword.setUser(user);
        user.setPassword(userPassword);

        userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setAddress("Teacher Address");
        teacher.setBio("Teacher Bio");
        teacherRepository.saveAndFlush(teacher);

        Teacher found = teacherRepository.findById(teacher.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getAddress()).isEqualTo("Teacher Address");
    }
}
