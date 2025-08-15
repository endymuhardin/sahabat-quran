package com.sahabatquran.model;

import com.sahabatquran.domain.Role;
import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserPassword;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModelCoverageTest {

    @Test
    void testUser() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUsername("testuser");
        user.setFullname("Test User");
        user.setEmail("test@user.com");
        user.setPhoneNumber("1234567890");

        Role role = new Role();
        role.setName("STUDENT");
        user.setRole(role);

        UserPassword userPassword = new UserPassword();
        userPassword.setPassword("password");
        user.setPassword(userPassword);

        user.setActive(true);

        assertNotNull(user.getId());
        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword().getPassword());
        assertEquals("Test User", user.getFullname());
        assertEquals("test@user.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("STUDENT", user.getRole().getName());
        assertTrue(user.isActive());
    }
}
