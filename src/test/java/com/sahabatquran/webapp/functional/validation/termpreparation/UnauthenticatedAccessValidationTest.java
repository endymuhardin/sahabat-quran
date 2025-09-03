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
 * PS-AP-001: Academic Admin - Unauthenticated Access Validation Tests.
 * 
 * Tests unauthorized access to Academic Planning endpoints to ensure proper
 * security mechanisms are in place for Term Preparation workflows.
 * 
 * Test Scenario: PS-AP-001 (Persiapan Semester - Alternate Path - 001)
 * Priority: High
 * Role: Unauthenticated User
 * Estimated Time: 3-4 minutes
 * 
 * Prerequisites:
 * - Browser in logout state (no session)
 * - Database in normal state
 */
@Slf4j
@DisplayName("PS-AP-001: Academic Admin - Unauthenticated Access Validation")
@Sql(scripts = "/sql/class-preparation-workflow-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/class-preparation-workflow-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UnauthenticatedAccessValidationTest extends BasePlaywrightTest {

    @BeforeEach
    void setupUnauthenticatedTest() {
        log.info("🔧 Setting up unauthenticated access validation test");
        // Ensure no user is logged in - navigate to logout first to clear any existing session
        page.navigate(getBaseUrl() + "/logout");
        page.waitForLoadState();
    }

    @Test
    @DisplayName("PS-AP-001-1: Assessment Foundation Access Without Authentication")
    void assessmentFoundationAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-1: Testing assessment foundation access without authentication");
        
        // Given: User is not authenticated (no session)
        // When: Attempt to directly access assessment foundation URL
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        
        // Then: Should automatically redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Should redirect to login page when accessing assessment foundation without authentication");
        
        // Verify we're on the login page
        assertThat(page).hasTitle("Login - Yayasan Sahabat Quran");
        assertThat(page.locator("#username")).isVisible();
        assertThat(page.locator("#password")).isVisible();
        assertThat(page.locator("button[type='submit']")).isVisible();
        
        log.info("✅ PS-AP-001-1: Assessment foundation access properly redirected to login");
    }

    @Test
    @DisplayName("PS-AP-001-2: Management Functions Access Without Authentication")
    void managementFunctionsAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-2: Testing management functions access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to directly access management teacher assignments URL
        page.navigate(getBaseUrl() + "/management/teacher-level-assignments");
        
        // Then: Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Should redirect to login page when accessing management functions without authentication");
        
        // Verify security mechanism is functioning
        assertThat(page.locator("#username")).isVisible();
        assertThat(page.locator("#password")).isVisible();
        
        // Ensure no system exposure occurred
        assertThat(page.locator("body")).not().containsText("Teacher Level Assignments");
        assertThat(page.locator("body")).not().containsText("Drag-and-drop");
        
        log.info("✅ PS-AP-001-2: Management functions access properly secured");
    }

    @Test
    @DisplayName("PS-AP-001-3: Class Generation Access Without Authentication")
    void classGenerationAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-3: Testing class generation access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to directly access class generation URL
        page.navigate(getBaseUrl() + "/academic/class-generation");
        
        // Then: Authentication should be required
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Should require authentication for class generation access");
        
        // Verify proper redirect behavior
        assertThat(page.locator("#login-button")).isVisible();
        
        // Ensure no error or system crash occurred
        assertThat(page.locator("body")).not().containsText("Error 500");
        assertThat(page.locator("body")).not().containsText("Exception");
        
        log.info("✅ PS-AP-001-3: Class generation access properly requires authentication");
    }

    @Test
    @DisplayName("PS-AP-001-4: Level Distribution Access Without Authentication")
    void levelDistributionAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-4: Testing level distribution access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to access level distribution analysis
        page.navigate(getBaseUrl() + "/academic/level-distribution");
        
        // Then: Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Level distribution should require authentication");
        
        log.info("✅ PS-AP-001-4: Level distribution access properly secured");
    }

    @Test
    @DisplayName("PS-AP-001-5: Availability Monitoring Access Without Authentication")
    void availabilityMonitoringAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-5: Testing availability monitoring access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to access availability monitoring
        page.navigate(getBaseUrl() + "/academic/availability-monitoring");
        
        // Then: Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Availability monitoring should require authentication");
        
        log.info("✅ PS-AP-001-5: Availability monitoring access properly secured");
    }

    @Test
    @DisplayName("PS-AP-001-6: Class Refinement Access Without Authentication")
    void classRefinementAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-6: Testing class refinement access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to access class refinement interface
        page.navigate(getBaseUrl() + "/academic/class-refinement");
        
        // Then: Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Class refinement should require authentication");
        
        log.info("✅ PS-AP-001-6: Class refinement access properly secured");
    }

    @Test
    @DisplayName("PS-AP-001-7: Final Schedule Review Access Without Authentication")
    void finalScheduleReviewAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-7: Testing final schedule review access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to access final schedule review
        page.navigate(getBaseUrl() + "/academic/final-schedule-review");
        
        // Then: Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "Final schedule review should require authentication");
        
        log.info("✅ PS-AP-001-7: Final schedule review access properly secured");
    }

    @Test
    @DisplayName("PS-AP-001-8: System Go-Live Access Without Authentication")
    void systemGoLiveAccessWithoutAuth() {
        log.info("🎯 PS-AP-001-8: Testing system go-live access without authentication");
        
        // Given: User is not authenticated
        // When: Attempt to access system go-live interface
        page.navigate(getBaseUrl() + "/academic/system-golive");
        
        // Then: Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
                  "System go-live should require authentication");
        
        // This is critical functionality - ensure it's properly protected
        assertThat(page.locator("body")).not().containsText("Execute Go-Live");
        assertThat(page.locator("body")).not().containsText("WARNING: This action is irreversible");
        
        log.info("✅ PS-AP-001-8: System go-live access properly secured");
    }

    @Test
    @DisplayName("PS-AP-001-9: Multiple Sequential Access Attempts")
    void multipleSequentialAccessAttempts() {
        log.info("🎯 PS-AP-001-9: Testing multiple sequential unauthorized access attempts");
        
        // Test multiple endpoints in sequence to ensure consistent security behavior
        String[] restrictedEndpoints = {
            "/academic/assessment-foundation",
            "/management/teacher-level-assignments", 
            "/academic/class-generation",
            "/academic/level-distribution",
            "/academic/availability-monitoring"
        };
        
        for (String endpoint : restrictedEndpoints) {
            log.info("Testing unauthorized access to: {}", endpoint);
            
            // Navigate to restricted endpoint
            page.navigate(getBaseUrl() + endpoint);
            
            // Should consistently redirect to login
            page.waitForURL("**/login**");
            assertTrue(page.url().contains("login"), 
                      "Endpoint " + endpoint + " should consistently redirect to login");
            
            // Verify login form is present
            assertThat(page.locator("#username")).isVisible();
            assertThat(page.locator("#password")).isVisible();
        }
        
        log.info("✅ PS-AP-001-9: All endpoints consistently require authentication");
    }

    @Test
    @DisplayName("PS-AP-001-10: Login Page Accessibility After Redirect")
    void loginPageAccessibilityAfterRedirect() {
        log.info("🎯 PS-AP-001-10: Testing login page functionality after security redirect");
        
        // Given: Redirected to login after unauthorized access attempt
        page.navigate(getBaseUrl() + "/academic/assessment-foundation");
        page.waitForURL("**/login**");
        
        // When: Attempt to use the login form
        page.fill("#username", "academic.admin1");
        page.fill("#password", "Welcome@YSQ2024");
        
        // Then: Login form should be functional
        assertThat(page.locator("#username")).hasValue("academic.admin1");
        assertThat(page.locator("#password")).hasValue("Welcome@YSQ2024");
        assertThat(page.locator("button[type='submit']")).isEnabled();
        
        // Submit login (but don't complete the test to avoid side effects)
        // Just verify the form is functional
        log.info("Login form is functional after security redirect");
        
        log.info("✅ PS-AP-001-10: Login page remains fully functional after security redirects");
    }
}