package id.sahabat.yayasan.repository;

import id.sahabat.yayasan.BaseIntegrationTest;
import id.sahabat.yayasan.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindAll() {
        // V2 migration seeds 4 users
        assertThat(userRepository.findAll()).hasSize(4);
    }

    @ParameterizedTest
    @CsvSource({
            "new.student, password123",
            "another.student, password456"
    })
    void testCreateAndFindUser(String username, String password) {
        var user = new User();
        user.setUsername(username);
        user.setPassword(password);
        // The 'student' role is seeded by the V2 migration
        roleRepository.findByName("student").ifPresent(role -> user.setRoles(Set.of(role)));
        userRepository.save(user);

        var foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(username);
        assertThat(foundUser.get().getRoles()).anyMatch(role -> role.getName().equals("student"));
    }
}
