package com.sahabatquran.repository;

import com.sahabatquran.domain.Teacher;
import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserRole;
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

    @Test
    public void testSaveAndFindTeacher() {
        User user = new User();
        user.setUsername("ust.fulan");
        user.setPassword("password");
        user.setRole(UserRole.TEACHER);
        user.setEmail("ust.fulan@sahabatquran.id");
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
