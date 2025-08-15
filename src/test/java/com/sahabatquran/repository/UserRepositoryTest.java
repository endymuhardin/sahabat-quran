package com.sahabatquran.repository;

import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);
        user.setEmail("admin@sahabatquran.id");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("admin");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("admin@sahabatquran.id");
    }

    @Test
    public void testFindByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertThat(found).isNotPresent();
    }
}
