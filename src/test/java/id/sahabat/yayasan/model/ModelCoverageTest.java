package id.sahabat.yayasan.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ModelCoverageTest {

    @Test
    void testPermission() {
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setName("test:permission");

        assertNotNull(permission.getId());
        assertEquals("test:permission", permission.getName());
    }

    @Test
    void testRole() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("TEST_ROLE");
        role.setPermissions(new HashSet<>());

        assertNotNull(role.getId());
        assertEquals("TEST_ROLE", role.getName());
        assertNotNull(role.getPermissions());
    }

    @Test
    void testUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRoles(new HashSet<>());

        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertNotNull(user.getRoles());
    }
}
