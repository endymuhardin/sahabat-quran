package id.sahabat.yayasan.repository;

import id.sahabat.yayasan.BaseIntegrationTest;
import id.sahabat.yayasan.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/test-data.sql")
public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindAll() {
        assertThat(userRepository.findAll()).isEmpty();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/users.csv", numLinesToSkip = 1)
    void testCreateAndFindUser(String username, String password) {
        var user = new User();
        user.setUsername(username);
        user.setPassword(password);
        roleRepository.findByName("ROLE_USER").ifPresent(role -> user.setRoles(Set.of(role)));
        userRepository.save(user);

        var foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(username);
        assertThat(foundUser.get().getRoles()).anyMatch(role -> role.getName().equals("ROLE_USER"));
    }
}
