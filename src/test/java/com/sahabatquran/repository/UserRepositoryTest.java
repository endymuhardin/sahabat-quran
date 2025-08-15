package com.sahabatquran.repository;

import com.sahabatquran.domain.Role;
import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserPassword;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByUsername() {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("admin");
        user.setRole(role);
        user.setEmail("admin@sahabatquran.id");

        UserPassword userPassword = new UserPassword();
        userPassword.setPassword("password");
        userPassword.setUser(user);
        user.setPassword(userPassword);

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
