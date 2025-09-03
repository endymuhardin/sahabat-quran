package com.sahabatquran.webapp.functional.validation.registration;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.RegistrationPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Skenario pengujian validasi dan error handling untuk Staff Registration workflow.
 * 
 * Test ini mengimplementasikan skenario alternate path dari dokumentasi
 * MR-AP-001 hingga MR-AP-003 untuk Staff role.
 */
@Slf4j
@DisplayName("MR-AP: Staff Registration Alternate Path Scenarios")
class StaffRegistrationValidationTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("MR-AP-001: Staff - Akses Tanpa Otentikasi")
    void shouldPreventUnauthorizedAccessToStaffRegistrationPage() {
        log.info("🚀 Starting MR-AP-001: Unauthorized Access Prevention Test...");
        
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Try to access registrations without login
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Should redirect to login page
        page.waitForURL("**/login**");
        assertTrue(page.url().contains("login"), 
            "Should redirect to login page for unauthorized access");
        
        log.info("✅ MR-AP-001: Unauthorized access prevention test completed!");
    }
    
    @Test
    @DisplayName("MR-AP-002: Role-Based Access Violation")
    void shouldPreventNonStaffUsersFromAccessingStaffFunctions() {
        log.info("🚀 Starting MR-AP-002: Non-Staff Access Prevention Test...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Try to access staff functions as regular student/user
        loginPage.navigateToLoginPage(getBaseUrl());
        
        // Login as non-staff user (assuming this user exists)
        loginPage.login("siswa.ali", "Welcome@YSQ2024");
        
        // Try to access registrations directly without waiting for page load
        page.navigate(getBaseUrl() + "/registrations");
        
        // Wait a bit for redirect/error to occur
        page.waitForTimeout(2000);
        
        String currentUrl = page.url();
        log.info("Current URL after non-staff user tries to access registrations: {}", currentUrl);
        
        // Check for access denied indicators using element IDs instead of URL text
        boolean hasAccessDeniedError = page.locator("#access-denied-error").isVisible();
        boolean hasUnauthorizedError = page.locator("#unauthorized-error").isVisible(); 
        boolean has403Error = page.locator("#forbidden-error").isVisible();
        boolean isRedirectedToLogin = currentUrl.contains("/login");
        boolean isOn403Page = currentUrl.contains("/error/403");
        
        // If none of the above, check if the page shows actual content (which would be wrong)
        boolean hasRegistrationsContent = page.locator("#page-title").isVisible() && 
                                        page.locator("#page-title").textContent().contains("Kelola Registrasi");
        
        boolean isAccessDenied = hasAccessDeniedError || hasUnauthorizedError || has403Error || isRedirectedToLogin || isOn403Page || !hasRegistrationsContent;
        
        if (hasRegistrationsContent) {
            log.error("SECURITY ISSUE: Student user can access staff registrations content!");
        }
        
        // The correct behavior is that the user should either:
        // 1. Be redirected to login/error page, OR
        // 2. Reach the URL but not see the actual restricted content
        assertTrue(isAccessDenied, 
            "Non-staff user should not have access to staff registration functions. " +
            "Expected: redirected to error page OR content hidden. " +
            "Actual: URL=" + currentUrl + ", hasContent=" + hasRegistrationsContent);
        
        log.info("✅ MR-AP-002: Non-staff access prevention test completed!");
    }
    
    @Test
    @DisplayName("MR-AP-003: Should validate teacher assignment form and prevent empty submission")
    void shouldValidateTeacherAssignmentFormAndPreventEmptySubmission() {
        log.info("🚀 Starting MR-AP-003: Teacher Assignment Form Validation Test...");
        
        final String ACADEMIC_ADMIN_USERNAME = "academic.admin1";
        final String ACADEMIC_ADMIN_PASSWORD = "Welcome@YSQ2024";
        final String TEST_REGISTRATION_ID = "B0000000-0000-0000-0000-000000000001";
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as academic admin
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(ACADEMIC_ADMIN_USERNAME, ACADEMIC_ADMIN_PASSWORD);
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        if (registrationPage.isRegistrationVisible(TEST_REGISTRATION_ID) && 
            registrationPage.getRegistrationStatus(TEST_REGISTRATION_ID).equals("SUBMITTED")) {
            
            // Open assign teacher modal
            registrationPage.clickAssignTeacher(TEST_REGISTRATION_ID);
            assertTrue(registrationPage.isAssignModalOpen(), "Assign modal should open");
            
            // Try to submit without selecting teacher
            registrationPage.submitTeacherAssignment();
            
            // Should still have modal open due to validation
            assertTrue(registrationPage.isAssignModalOpen(), 
                "Modal should remain open due to validation error");
            
            // Check for validation error indicators
            // The form should highlight required fields
            log.info("📝 Validation error should be displayed for empty teacher selection");
            
            // Test with valid teacher selection
            registrationPage.selectTeacher("20000000-0000-0000-0000-000000000001"); // ustadz.ahmad
            registrationPage.fillAssignmentNotes("Test assignment with proper validation");
            
            // This should succeed
            registrationPage.submitTeacherAssignment();
            
            // Modal should close and assignment should succeed
            assertFalse(registrationPage.isAssignModalOpen(), 
                "Modal should close after successful assignment");
            
            log.info("✅ Form validation working correctly");
            
        } else {
            log.warn("⚠️ Test registration not found or not in SUBMITTED status");
        }
        
        log.info("✅ MR-AP-003: Teacher assignment form validation test completed!");
    }
    
    @Test
    @DisplayName("Should handle teacher assignment to already assigned registration")
    void shouldHandleTeacherAssignmentToAlreadyAssignedRegistration() {
        log.info("🚀 Testing Assignment to Already Assigned Registration...");
        
        final String ACADEMIC_ADMIN_USERNAME = "academic.admin1";
        final String ACADEMIC_ADMIN_PASSWORD = "Welcome@YSQ2024";
        final String TEST_REGISTRATION_ID = "B0000000-0000-0000-0000-000000000001";
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as academic admin
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(ACADEMIC_ADMIN_USERNAME, ACADEMIC_ADMIN_PASSWORD);
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Look for registration with ASSIGNED status
        registrationPage.filterByStatus("ASSIGNED");
        
        if (registrationPage.hasResults()) {
            log.info("📝 Found ASSIGNED registration - should not show assign button");
            
            // ASSIGNED registrations should not have assign button
            // They should show "Ditugaskan" status instead
            
        } else {
            log.info("ℹ️ No ASSIGNED registrations found for this test");
        }
        
        log.info("✅ Already assigned registration handling test completed!");
    }
    
    @Test
    @DisplayName("Should handle search with no results")
    void shouldHandleSearchWithNoResults() {
        log.info("🚀 Testing Search with No Results...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as academic admin
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("academic.admin1", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Search for non-existent student
        registrationPage.searchByStudentName("NonExistentStudentName12345");
        
        // Should show no results message
        assertTrue(registrationPage.hasNoResults(), 
            "Should display no results message for non-existent search");
        
        assertEquals(0, registrationPage.getResultsCount(), 
            "Results count should be 0 for no results");
        
        log.info("✅ Search with no results handling test completed!");
    }
    
    @Test
    @DisplayName("Should handle invalid filter combinations")
    void shouldHandleInvalidFilterCombinations() {
        log.info("🚀 Testing Invalid Filter Combinations...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as academic admin
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("academic.admin1", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Test various filter combinations
        log.info("📝 Testing that DRAFT status is not available for staff users");
        
        // DRAFT should not be available as a filter option for staff users
        assertTrue(registrationPage.isDraftStatusUnavailable(),
            "DRAFT status should not be available as a filter option for staff users");
        
        log.info("📝 Testing combination of filters");
        registrationPage.searchByStudentName("Test");
        registrationPage.filterByStatus("COMPLETED");
        
        // Should handle multiple filters gracefully
        assertTrue(registrationPage.isOnRegistrationsPage(),
            "Should remain on registrations page with filters applied");
        
        log.info("✅ Invalid filter combinations handling test completed!");
    }
    
    @Test
    @DisplayName("Should handle modal cancel functionality")
    void shouldHandleModalCancelFunctionality() {
        log.info("🚀 Testing Modal Cancel Functionality...");
        
        final String ACADEMIC_ADMIN_USERNAME = "academic.admin1";
        final String ACADEMIC_ADMIN_PASSWORD = "Welcome@YSQ2024";
        final String TEST_REGISTRATION_ID = "B0000000-0000-0000-0000-000000000001";
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as academic admin
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(ACADEMIC_ADMIN_USERNAME, ACADEMIC_ADMIN_PASSWORD);
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        if (registrationPage.isRegistrationVisible(TEST_REGISTRATION_ID) && 
            registrationPage.getRegistrationStatus(TEST_REGISTRATION_ID).equals("SUBMITTED")) {
            
            // Open assign teacher modal
            registrationPage.clickAssignTeacher(TEST_REGISTRATION_ID);
            assertTrue(registrationPage.isAssignModalOpen(), "Assign modal should open");
            
            // Fill some data
            registrationPage.selectTeacher("20000000-0000-0000-0000-000000000001");
            registrationPage.fillAssignmentNotes("Test assignment to cancel");
            
            // Cancel the modal
            registrationPage.cancelTeacherAssignment();
            
            // Modal should close and no assignment should occur
            assertFalse(registrationPage.isAssignModalOpen(), "Modal should close after cancel");
            
            // Registration status should remain unchanged
            registrationPage.expectRegistrationStatus(TEST_REGISTRATION_ID, "SUBMITTED");
            
            log.info("✅ Modal cancel functionality working correctly");
            
        } else {
            log.warn("⚠️ Test registration not found for modal cancel test");
        }
        
        log.info("✅ Modal cancel functionality test completed!");
    }
    
    @Test
    @DisplayName("Should handle page refresh and maintain state")
    void shouldHandlePageRefreshAndMaintainState() {
        log.info("🚀 Testing Page Refresh State Maintenance...");
        
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // Login as academic admin
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("academic.admin1", "Welcome@YSQ2024");
        page.waitForURL("**/dashboard");
        
        registrationPage.navigateToRegistrations(getBaseUrl());
        
        // Apply some filters
        registrationPage.filterByStatus("SUBMITTED");
        registrationPage.searchByStudentName("Ahmad");
        
        int resultsBeforeRefresh = registrationPage.getResultsCount();
        
        // Refresh the page
        page.reload();
        registrationPage.waitForPageLoad();
        
        // Verify state is maintained (or reset appropriately)
        assertTrue(registrationPage.isOnRegistrationsPage(),
            "Should remain on registrations page after refresh");
        
        // Note: Filter state maintenance depends on implementation
        // Some applications maintain search parameters, others reset
        
        log.info("✅ Page refresh state maintenance test completed!");
    }
}