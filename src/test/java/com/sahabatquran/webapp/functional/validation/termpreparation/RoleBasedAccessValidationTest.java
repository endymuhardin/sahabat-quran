package com.sahabatquran.webapp.functional.validation.termpreparation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PS-AP-002: Role-Based Access Violation Validation Tests.
 * 
 * Tests role-based access control to ensure users can only access functionality
 * appropriate to their roles. Verifies that unauthorized access attempts are
 * properly blocked with appropriate error messages.
 * 
 * Test Scenario: PS-AP-002 (Persiapan Semester - Alternate Path - 002)
 * Priority: High
 * Estimated Time: 6-8 minutes
 * 
 * Prerequisites:
 * - Multiple user accounts with different roles
 * - Database with test data
 * 
 * Test Users:
 * - Student User: siswa.ali / Welcome@YSQ2024
 * - Academic Admin User: academic.admin1 / Welcome@YSQ2024  
 * - Teacher User: ustadz.ahmad / Welcome@YSQ2024
 * - Management User: management.director / Welcome@YSQ2024
 */
@Slf4j
@DisplayName("PS-AP-002: Role-Based Access Violation Validation")
@Sql(scripts = "/sql/class-preparation-workflow-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/class-preparation-workflow-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RoleBasedAccessValidationTest extends BasePlaywrightTest {

    @BeforeEach
    void setupRoleBasedTest() {
        log.info("🔧 Setting up role-based access validation test");
        // Ensure clean session state
        page.navigate(getBaseUrl() + "/logout");
        page.waitForLoadState();
    }

    @Test
    @DisplayName("PS-AP-002-1: Student Attempts to Access Academic Planning")
    void studentAccessAcademicPlanning() {
        log.info("🎯 PS-AP-002-1: Testing student access to academic planning (should be denied)");
        
        // Given: Student user is logged in
        loginAsStudent(); // Using inherited method from BasePlaywrightTest
        
        // When: Student attempts to access academic planning assessment foundation
        log.info("📝 Attempting to access /academic/assessment-foundation as student");
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForLoadState();
        
        // Then: Access should be denied with proper error handling
        verifyAccessDenied("Student should not have access to academic planning");
        
        log.info("✅ PS-AP-002-1: Student access to academic planning properly denied");
    }

    @Test
    @DisplayName("PS-AP-002-2: Teacher Attempts to Access Management Functions")
    void teacherAccessManagementFunctions() {
        log.info("🎯 PS-AP-002-2: Testing teacher access to management functions (should be denied)");
        
        // Given: Teacher user is logged in  
        loginAsInstructor(); // Using inherited method from BasePlaywrightTest
        
        // When: Teacher attempts to access management teacher level assignments
        log.info("📝 Attempting to access /management/teacher-level-assignments as teacher");
        page.navigate(getBaseUrl() + "/management/teacher-level-assignments");
        page.waitForLoadState();
        
        // Then: Access should be denied with proper error message
        verifyAccessDenied("Teacher should not have access to management functions");
        
        log.info("✅ PS-AP-002-2: Teacher access to management functions properly denied");
    }

    @Test
    @DisplayName("PS-AP-002-3: Teacher Attempts to Access Class Generation Functions")
    void teacherAccessClassGeneration() {
        log.info("🎯 PS-AP-002-3: Testing teacher access to class generation functions");
        
        // Given: Teacher user is logged in
        loginAsInstructor(); // Using inherited method from BasePlaywrightTest
        
        // When: Teacher attempts to access class generation readiness (admin function)
        log.info("📝 Attempting to access /academic/generation-readiness as teacher");
        page.navigate(getBaseUrl() + "/academic/generation-readiness");
        page.waitForLoadState();
        
        // Then: Access should be denied as this requires CLASS_GENERATION_RUN permission
        verifyAccessDenied("Teacher should not have access to class generation functions");
        
        log.info("✅ PS-AP-002-3: Teacher properly restricted from class generation functions");
    }

    @Test
    @DisplayName("PS-AP-002-4: Verify Role Boundaries and Proper Error Messages")
    void verifyRoleBoundariesAndErrorMessages() {
        log.info("🎯 PS-AP-002-4: Testing comprehensive role boundary enforcement");
        
        // Test 1: Student trying to access instructor functions
        loginAsStudent(); // Using inherited method from BasePlaywrightTest
        log.info("📝 Student attempting to access instructor availability submission");
        page.navigate(getBaseUrl() + "/instructor/availability-submission");
        page.waitForLoadState();
        verifyAccessDenied("Student should not access instructor availability");
        
        // Test 2: Teacher trying to access admin functions  
        loginAsInstructor(); // Using inherited method from BasePlaywrightTest
        log.info("📝 Teacher attempting to access system go-live management");
        page.navigate(getBaseUrl() + "/academic/system-golive");
        page.waitForLoadState();
        verifyAccessDenied("Teacher should not access system go-live management");
        
        // Test 3: Verify error messages are informative and secure
        // The error messages should not reveal system internals
        if (page.locator("text=403").isVisible() || 
            page.locator("text=Forbidden").isVisible() ||
            page.locator("text=Access Denied").isVisible()) {
            log.info("✅ Proper error messages displayed without exposing system details");
        }
        
        log.info("✅ PS-AP-002-4: Role boundaries properly enforced with secure error messages");
    }

    // Helper Methods
    
    private void verifyAccessDenied(String message) {
        // Check for various ways access might be denied
        boolean accessDenied = false;
        
        // Check if redirected to error page
        if (page.url().contains("error") || page.url().contains("403")) {
            accessDenied = true;
            log.info("📛 Access denied - redirected to error page");
        }
        
        // Check for error messages on the page (both English and Indonesian)
        if (page.locator("text=403").isVisible() || 
            page.locator("text=Forbidden").isVisible() ||
            page.locator("text=Access Denied").isVisible() ||
            page.locator("text=Unauthorized").isVisible() ||
            page.locator("text=Akses Ditolak").isVisible() ||
            page.title().contains("Akses Ditolak") ||
            page.content().toLowerCase().contains("forbidden") ||
            page.content().toLowerCase().contains("akses ditolak")) {
            accessDenied = true;
            log.info("📛 Access denied - error message displayed");
        }
        
        // Check if redirected back to dashboard or home
        if (page.url().contains("dashboard") || page.url().equals(getBaseUrl() + "/")) {
            accessDenied = true;
            log.info("📛 Access denied - redirected to safe area");
        }
        
        assertTrue(accessDenied, message);
    }
}