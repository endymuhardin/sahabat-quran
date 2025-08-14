package id.sahabat.yayasan.repository;

import id.sahabat.yayasan.BaseIntegrationTest;
import id.sahabat.yayasan.model.User;
import id.sahabat.yayasan.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindAll() {
        // V2 migration seeds 4 users
        assertThat(userRepository.findAll()).hasSize(4);
    }

    @Test
    void testCreateAndFindUser() {
        var user = new User();
        user.setFullname("New Student");
        user.setUsername("new.student");
        user.setPassword("password123");
        user.setEmail("new.student@sahabatquran.id");
        user.setRole(UserRole.STUDENT);

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        var foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("new.student");
        assertThat(foundUser.get().getRole()).isEqualTo(UserRole.STUDENT);
    }
}
