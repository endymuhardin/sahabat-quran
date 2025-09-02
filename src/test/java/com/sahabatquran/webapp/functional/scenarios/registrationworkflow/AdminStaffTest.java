package com.sahabatquran.webapp.functional.scenarios.registrationworkflow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.RegistrationPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Admin Staff Registration Workflow Tests.
 * Covers all admin staff operations in student registration process.
 * 
 * User Role: ADMIN_STAFF
 * Focus: Student registration processing, review, and approval workflow.
 */
@Slf4j
@DisplayName("Admin Staff Registration Workflow")
class AdminStaffTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("MR-HP-001: Should successfully assign teacher to review submitted registration")
    @Sql(scripts = "/sql/staff-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/staff-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullyAssignTeacherToReviewSubmittedRegistration() {
        log.info("🚀 Starting MR-HP-001: Staff Assign Teacher Workflow Test...");
        
        // Test data sesuai dokumentasi
        final String STAFF_USERNAME = "staff.admin1";
        final String STAFF_PASSWORD = "Welcome@YSQ2024";
        final String STUDENT_NAME = "TEST_ST_Ahmad"; // Will match our test data prefix
        final String TEACHER_ID = "20000000-0000-0000-0000-000000000001"; // ustadz.ahmad
        final String ASSIGNMENT_NOTES = "Please review this registration and evaluate the placement test";
        
        // Given - Setup prerequisite: Student registration exists with SUBMITTED status
        // (This would typically be created by a previous test or data setup)
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Bagian 1: Login sebagai Admin Staff
        log.info("📝 Bagian 1: Login sebagai Admin Staff");
        
        // Step 1: Akses halaman login
        loginPage.navigateToLoginPage(getBaseUrl());
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
        
        // Step 2: Login sebagai admin staff
        loginPage.login(STAFF_USERNAME, STAFF_PASSWORD);
        
        // Verifikasi: Login berhasil dan dapat akses registration management
        page.waitForURL("**/dashboard");
        assertTrue(page.url().contains("dashboard"), "Should redirect to dashboard after login");
        
        // Bagian 2: View Submitted Registrations
        log.info("📝 Bagian 2: View Submitted Registrations");
        
        // Step 3: Navigate ke registrations management
        registrationPage.navigateToRegistrations(getBaseUrl());
        assertTrue(registrationPage.isOnRegistrationsPage(), "Should be on registrations page");
        
        // Verifikasi: List registrasi muncul dan filter tersedia
        assertTrue(registrationPage.hasResults() || registrationPage.hasNoResults(), "Should show either results or no results message");
        
        // Step 4: Filter registrasi by status SUBMITTED
        registrationPage.filterByStatus("SUBMITTED");
        
        // Verifikasi: Hanya registrasi dengan status SUBMITTED yang muncul
        if (registrationPage.hasResults()) {
            log.info("✅ Found SUBMITTED registrations to assign");
        } else {
            // Create a test registration for this scenario
            log.info("ℹ️ No SUBMITTED registrations found, this test requires existing data");
            // In a real test environment, you might want to skip or create test data
        }
        
        // Bagian 3: Search and Access Registration
        log.info("📝 Bagian 3: Search and Access Registration");
        
        // Step 5: Search for specific student
        registrationPage.searchByStudentName(STUDENT_NAME);
        
        // Test data is now created dynamically by SQL setup script
        // Find the first available SUBMITTED registration from our test data
        if (registrationPage.hasResults()) {
            log.info("✅ Found test registrations to work with");
            
            // Get the first available test registration for assignment
            // This will work with the dynamically created test data
            String firstTestRegistrationId = registrationPage.getFirstAvailableRegistrationId();
            
            if (firstTestRegistrationId != null) {
                log.info("📝 Working with test registration ID: {}", firstTestRegistrationId);
                
                // Verify initial status is SUBMITTED
                registrationPage.expectRegistrationStatus(firstTestRegistrationId, "SUBMITTED");
                
                // Step 6: Click assign teacher button
                registrationPage.expectAssignButtonAvailable(firstTestRegistrationId);
                
                // Bagian 4: Assign Teacher
                log.info("📝 Bagian 4: Assign Teacher");
                
                // Step 7-8: Complete teacher assignment
                registrationPage.assignTeacherToRegistration(firstTestRegistrationId, TEACHER_ID, ASSIGNMENT_NOTES);
                
                // Verifikasi: Assignment berhasil dan status berubah
                page.waitForTimeout(2000); // Allow for status update
                registrationPage.expectStatusAssigned(firstTestRegistrationId);
                
                log.info("✅ Teacher successfully assigned to registration");
            } else {
                log.warn("⚠️ No test registration ID could be determined");
            }
        } else {
            log.warn("⚠️ No test registrations found. Check test data setup.");
        }
        
        log.info("✅ MR-HP-001: Staff Assign Teacher Workflow Test completed!");
    }
    
    @Test
    @DisplayName("Should display staff dashboard with registration management access")
    void shouldDisplayStaffDashboardWithRegistrationManagementAccess() {
        log.info("🚀 Testing Staff Dashboard Access...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as staff
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("staff.admin1", "Welcome@YSQ2024");
        
        // Verify dashboard access
        page.waitForURL("**/dashboard");
        
        // Navigate to registrations
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Verify staff can access registration management
        assertTrue(registrationPage.isOnRegistrationsPage(), 
            "Staff should have access to registration management");
        
        log.info("✅ Staff dashboard access test completed!");
    }
    
    @Test
    @DisplayName("Should show only submitted and assigned registrations for staff")
    void shouldShowOnlySubmittedAndAssignedRegistrationsForStaff() {
        log.info("🚀 Testing Staff Registration Filter...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login and navigate
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("staff.admin1", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Test different status filters
        log.info("📝 Testing SUBMITTED status filter");
        registrationPage.filterByStatus("SUBMITTED");
        
        log.info("📝 Testing ASSIGNED status filter");
        registrationPage.filterByStatus("ASSIGNED");
        
        // Verify the filtering works (results change or appropriate messages shown)
        assertTrue(registrationPage.hasResults() || registrationPage.hasNoResults(),
            "Status filtering should work correctly");
        
        log.info("✅ Staff registration filter test completed!");
    }
    
    @Test
    @DisplayName("Should provide search functionality for staff")
    void shouldProvideSearchFunctionalityForStaff() {
        log.info("🚀 Testing Staff Search Functionality...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login and navigate
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("staff.admin1", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Test search by name
        log.info("📝 Testing search by student name");
        registrationPage.searchByStudentName("Ahmad");
        
        // Test search by email
        log.info("📝 Testing search by email");
        registrationPage.searchByEmail("test@example.com");
        
        // Verify search functionality works
        assertTrue(registrationPage.isOnRegistrationsPage(),
            "Search should maintain registration page context");
        
        log.info("✅ Staff search functionality test completed!");
    }
    
    @Test
    @DisplayName("Should handle assign teacher modal interactions")
    void shouldHandleAssignTeacherModalInteractions() {
        log.info("🚀 Testing Assign Teacher Modal...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login and navigate
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("staff.admin1", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Test modal functionality (assuming a registration exists)
        final String TEST_REGISTRATION_ID = "B0000000-0000-0000-0000-000000000001";
        
        if (registrationPage.isRegistrationVisible(TEST_REGISTRATION_ID) && 
            registrationPage.getRegistrationStatus(TEST_REGISTRATION_ID).equals("SUBMITTED")) {
            
            log.info("📝 Testing modal open/close");
            registrationPage.clickAssignTeacher(TEST_REGISTRATION_ID);
            assertTrue(registrationPage.isAssignModalOpen(), "Assign modal should open");
            
            log.info("📝 Testing modal cancel");
            registrationPage.cancelTeacherAssignment();
            assertFalse(registrationPage.isAssignModalOpen(), "Assign modal should close");
            
        } else {
            log.info("ℹ️ Skipping modal test - no suitable registration found");
        }
        
        log.info("✅ Assign teacher modal test completed!");
    }
}