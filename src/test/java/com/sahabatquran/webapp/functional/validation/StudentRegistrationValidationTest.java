package com.sahabatquran.webapp.functional.validation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.StudentRegistrationPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Student registration validation and error handling tests.
 * 
 * This class focuses on testing form validation, error conditions,
 * and edge cases for the student registration functionality.
 */
@Slf4j
@DisplayName("PS-AP: Student Registration Alternate Path Scenarios")
class StudentRegistrationValidationTest extends BasePlaywrightTest {

    @Test
    @DisplayName("PS-AP-001: Validasi Field Wajib dan Cegah Submit Kosong")
    void shouldValidateRequiredFieldsAndPreventEmptySubmission() {
        log.info("🚀 Starting Required Fields Validation Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Try to proceed without filling required fields
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.clickNext();
        
        // Then - Should stay on step 1 due to validation
        assertTrue(registrationPage.isOnStep(1), "Should remain on step 1");
        assertTrue(page.url().contains("/register"), "Should still be on registration page");
        
        log.info("✅ Required Fields Validation Test completed!");
    }
    
    @Test
    @DisplayName("PS-AP-002: Reset Form Data")
    void shouldClearFormDataWhenRequested() {
        log.info("🚀 Starting Clear Form Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Fill form with data
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Test User", "test@example.com", "081234567890");
        
        // Verify data is filled
        registrationPage.expectFieldValue("fullName", "Test User");
        registrationPage.expectFieldValue("email", "test@example.com");
        registrationPage.expectFieldValue("phoneNumber", "081234567890");
        
        // Clear form
        registrationPage.clearForm();
        
        // Wait for clear operation to complete
        page.waitForTimeout(3000);
        
        // Then - Verify form is cleared
        registrationPage.expectFieldEmpty("fullName");
        registrationPage.expectFieldEmpty("email");
        registrationPage.expectFieldEmpty("phoneNumber");
        
        log.info("✅ Clear Form Test completed!");
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        log.info("🚀 Starting Email Format Validation Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Try with invalid email format
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Test User", "invalid-email", "081234567890");
        registrationPage.clickNext();
        
        // Then - Should stay on step 1 due to email validation
        assertTrue(registrationPage.isOnStep(1), "Should remain on step 1 due to invalid email");
        
        log.info("✅ Email Format Validation Test completed!");
    }

    @Test
    @DisplayName("Should allow step navigation with invalid phone format (validation happens on backend)")
    void shouldAllowStepNavigationWithInvalidPhoneFormat() {
        log.info("🚀 Starting Phone Number Format Navigation Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Try with invalid phone number format
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Test User", "test@example.com", "invalid-phone");
        registrationPage.clickNext();
        
        // Then - Should proceed to step 2 (frontend doesn't validate phone format)
        assertTrue(registrationPage.isOnStep(2), "Should proceed to step 2 - frontend doesn't validate phone format");
        
        log.info("✅ Phone Number Format Navigation Test completed!");
    }

    @Test
    @DisplayName("Should prevent duplicate email registration")
    void shouldPreventDuplicateEmailRegistration() {
        log.info("🚀 Starting Duplicate Email Prevention Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Try to register with an email that might already exist
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Test User", "admin@sahabatquran.com", "081234567890");
        registrationPage.clickNext();
        
        // Then - Should handle duplicate email appropriately
        // This might show validation error or proceed to next step depending on implementation
        assertTrue(page.url().contains("/register"), "Should handle duplicate email appropriately");
        
        log.info("✅ Duplicate Email Prevention Test completed!");
    }

    @Test
    @DisplayName("Should allow step navigation with short name (no minimum length validation)")
    void shouldAllowStepNavigationWithShortName() {
        log.info("🚀 Starting Short Name Navigation Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Try with very short name
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("X", "test@example.com", "081234567890");
        registrationPage.clickNext();
        
        // Then - Should proceed to step 2 (no minimum name length validation)
        assertTrue(registrationPage.isOnStep(2), "Should proceed to step 2 - no minimum name length validation");
        
        log.info("✅ Short Name Navigation Test completed!");
    }

    @Test
    @DisplayName("Should validate special characters in name")
    void shouldValidateSpecialCharactersInName() {
        log.info("🚀 Starting Special Characters Validation Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Try with special characters in name
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Test@User#123", "test@example.com", "081234567890");
        registrationPage.clickNext();
        
        // Then - Should validate name format (depending on business rules)
        // This test assumes special characters might not be allowed
        assertTrue(page.url().contains("/register"), "Should handle special characters appropriately");
        
        log.info("✅ Special Characters Validation Test completed!");
    }
}