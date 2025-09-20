package com.sahabatquran.webapp.functional.scenarios.semesterclosure;

import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Debug test to verify permission assignments in testcontainer environment
 */
@Slf4j
@DisplayName("Permission Debug Test")
class PermissionDebugTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Debug: Check TERM_CLOSURE permissions in testcontainer")
    @Sql(scripts = "/sql/semester-closure-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void debugTermClosurePermissions() {

        // Check if TERM_CLOSURE permissions exist
        log.info("🔍 Checking if TERM_CLOSURE permissions exist...");
        List<Map<String, Object>> permissions = jdbcTemplate.queryForList(
            "SELECT code, description FROM permissions WHERE code LIKE '%TERM_CLOSURE%' ORDER BY code"
        );

        log.info("📋 Found {} TERM_CLOSURE permissions:", permissions.size());
        for (Map<String, Object> perm : permissions) {
            log.info("   - {}: {}", perm.get("code"), perm.get("description"));
        }

        assertTrue(permissions.size() >= 2, "Should have at least 2 TERM_CLOSURE permissions");

        // Check if ACADEMIC_ADMIN role has TERM_CLOSURE permissions
        log.info("🔍 Checking ACADEMIC_ADMIN role permissions...");
        List<Map<String, Object>> rolePerms = jdbcTemplate.queryForList(
            "SELECT r.code as role_code, p.code as permission_code " +
            "FROM roles r " +
            "JOIN role_permissions rp ON r.id = rp.id_role " +
            "JOIN permissions p ON rp.id_permission = p.id " +
            "WHERE r.code = 'ACADEMIC_ADMIN' AND p.code LIKE '%TERM_CLOSURE%' " +
            "ORDER BY p.code"
        );

        log.info("📋 ACADEMIC_ADMIN has {} TERM_CLOSURE permissions:", rolePerms.size());
        for (Map<String, Object> rolePerm : rolePerms) {
            log.info("   - {}: {}", rolePerm.get("role_code"), rolePerm.get("permission_code"));
        }

        assertEquals(2, rolePerms.size(), "ACADEMIC_ADMIN should have 2 TERM_CLOSURE permissions");

        // Check what authorities academic.admin1 actually has
        log.info("🔍 Checking authorities for academic.admin1...");
        List<Map<String, Object>> userAuthorities = jdbcTemplate.queryForList(
            "SELECT u.username, p.code as authority " +
            "FROM users u " +
            "JOIN user_roles ur ON u.id = ur.id_user " +
            "JOIN role_permissions rp ON ur.id_role = rp.id_role " +
            "JOIN permissions p ON rp.id_permission = p.id " +
            "WHERE u.username = 'academic.admin1' AND p.code LIKE '%TERM_CLOSURE%' " +
            "ORDER BY p.code"
        );

        log.info("📋 academic.admin1 has {} TERM_CLOSURE authorities:", userAuthorities.size());
        for (Map<String, Object> auth : userAuthorities) {
            log.info("   - {}: {}", auth.get("username"), auth.get("authority"));
        }

        assertEquals(2, userAuthorities.size(), "academic.admin1 should have 2 TERM_CLOSURE authorities");

        // Log success
        log.info("✅ All permission checks passed - TERM_CLOSURE permissions are correctly assigned");
    }
}