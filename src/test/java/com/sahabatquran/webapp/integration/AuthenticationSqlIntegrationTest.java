package com.sahabatquran.webapp.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("Authentication SQL Integration Tests")
class AuthenticationSqlIntegrationTest extends BaseIntegrationTest {
    
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    @DisplayName("Should debug authentication SQL query")
    void shouldDebugAuthenticationSqlQuery() {
        System.out.println("=== DEBUGGING AUTHENTICATION SQL ===");
        
        // Test the exact authentication query used in SecurityConfig
        String authQuery = "SELECT u.username, uc.password_hash, u.is_active " +
                          "FROM users u " +
                          "JOIN user_credentials uc ON u.id = uc.id_user " +
                          "WHERE u.username = ?";
        
        System.out.println("Authentication Query:");
        System.out.println(authQuery);
        System.out.println();
        
        // Test with system admin user
        testAuthenticationQuery("sysadmin", authQuery);
        testAuthenticationQuery("ustadz.ahmad", authQuery);
        testAuthenticationQuery("siswa.ali", authQuery);
    }
    
    @Test
    @DisplayName("Should debug authorization SQL query")
    void shouldDebugAuthorizationSqlQuery() {
        System.out.println("=== DEBUGGING AUTHORIZATION SQL ===");
        
        // Test the exact authorization query used in SecurityConfig
        String authzQuery = "SELECT u.username, p.code as authority " +
                           "FROM users u " +
                           "JOIN user_roles ur ON u.id = ur.id_user " +
                           "JOIN role_permissions rp ON ur.id_role = rp.id_role " +
                           "JOIN permissions p ON rp.id_permission = p.id " +
                           "WHERE u.username = ? AND u.is_active = true";
        
        System.out.println("Authorization Query:");
        System.out.println(authzQuery);
        System.out.println();
        
        // Test with system admin user
        testAuthorizationQuery("sysadmin", authzQuery);
        testAuthorizationQuery("ustadz.ahmad", authzQuery);
        testAuthorizationQuery("siswa.ali", authzQuery);
    }
    
    private void testAuthenticationQuery(String username, String query) {
        System.out.println("--- Testing Authentication for: " + username + " ---");
        
        try {
            jdbcTemplate.query(query, (rs, rowNum) -> {
                System.out.println("  Username: " + rs.getString("username"));
                System.out.println("  Password Hash: " + rs.getString("password_hash").substring(0, 20) + "...");
                System.out.println("  Is Active: " + rs.getBoolean("is_active"));
                return null;
            }, username);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        System.out.println();
    }
    
    private void testAuthorizationQuery(String username, String query) {
        System.out.println("--- Testing Authorization for: " + username + " ---");
        
        try {
            jdbcTemplate.query(query, (rs, rowNum) -> {
                System.out.println("  Authority: " + rs.getString("authority"));
                return null;
            }, username);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        System.out.println();
    }
    
    @Test
    @DisplayName("Should check password format and verify BCrypt")
    void shouldCheckPasswordFormatAndVerifyBCrypt() {
        System.out.println("=== CHECKING PASSWORD FORMAT ===");
        
        String query = "SELECT u.username, uc.password_hash " +
                      "FROM users u " +
                      "JOIN user_credentials uc ON u.id = uc.id_user " +
                      "WHERE u.username IN ('sysadmin', 'ustadz.ahmad', 'siswa.ali')";
        
        jdbcTemplate.query(query, (rs, rowNum) -> {
            String username = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            
            System.out.println("User: " + username);
            System.out.println("  Password Hash: " + passwordHash);
            System.out.println("  Hash Length: " + passwordHash.length());
            System.out.println("  Starts with $2a$ (BCrypt): " + passwordHash.startsWith("$2a$"));
            System.out.println("  Starts with $2b$ (BCrypt): " + passwordHash.startsWith("$2b$"));
            System.out.println();
            return null;
        });
        
        // Test BCrypt verification manually
        System.out.println("=== MANUAL BCRYPT VERIFICATION ===");
        String sysadminPasswordHash = jdbcTemplate.queryForObject(
            "SELECT uc.password_hash FROM users u JOIN user_credentials uc ON u.id = uc.id_user WHERE u.username = 'sysadmin'", 
            String.class
        );
        
        System.out.println("Sysadmin password hash from DB: " + sysadminPasswordHash);
        
        // Try to verify the password using BCrypt
        try {
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            
            boolean matches1 = encoder.matches("SysAdmin@YSQ2024", sysadminPasswordHash);
            boolean matches2 = encoder.matches("AdminYSQ@2024", sysadminPasswordHash);
            
            System.out.println("BCrypt matches 'SysAdmin@YSQ2024': " + matches1);
            System.out.println("BCrypt matches 'AdminYSQ@2024': " + matches2);
            
        } catch (Exception e) {
            System.out.println("BCrypt verification error: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should test the complete user lookup chain")
    void shouldTestCompleteUserLookupChain() {
        System.out.println("=== COMPLETE USER LOOKUP CHAIN ===");
        
        String completeQuery = "SELECT u.id, u.username, u.full_name, u.email, u.is_active, " +
                              "uc.password_hash, " +
                              "r.code as role_code, r.name as role_name, " +
                              "p.code as permission_code, p.name as permission_name " +
                              "FROM users u " +
                              "LEFT JOIN user_credentials uc ON u.id = uc.id_user " +
                              "LEFT JOIN user_roles ur ON u.id = ur.id_user " +
                              "LEFT JOIN roles r ON ur.id_role = r.id " +
                              "LEFT JOIN role_permissions rp ON r.id = rp.id_role " +
                              "LEFT JOIN permissions p ON rp.id_permission = p.id " +
                              "WHERE u.username = 'sysadmin' " +
                              "ORDER BY p.code";
        
        System.out.println("Complete Query for Sysadmin:");
        jdbcTemplate.query(completeQuery, (rs, rowNum) -> {
            if (rowNum == 0) {
                System.out.println("User Info:");
                System.out.println("  ID: " + rs.getString("id"));
                System.out.println("  Username: " + rs.getString("username"));
                System.out.println("  Full Name: " + rs.getString("full_name"));
                System.out.println("  Email: " + rs.getString("email"));
                System.out.println("  Is Active: " + rs.getBoolean("is_active"));
                System.out.println("  Has Password: " + (rs.getString("password_hash") != null));
                System.out.println("  Role: " + rs.getString("role_name"));
                System.out.println();
                System.out.println("Permissions:");
            }
            System.out.println("  " + rs.getString("permission_code") + " - " + rs.getString("permission_name"));
            return null;
        });
    }
}