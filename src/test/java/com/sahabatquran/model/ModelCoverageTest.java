package com.sahabatquran.model;

import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserRole;
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
        user.setPassword("password");
        user.setFullname("Test User");
        user.setEmail("test@user.com");
        user.setPhoneNumber("1234567890");
        user.setRole(UserRole.STUDENT);
        user.setActive(true);

        assertNotNull(user.getId());
        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("Test User", user.getFullname());
        assertEquals("test@user.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals(UserRole.STUDENT, user.getRole());
        assertTrue(user.isActive());
    }
}
