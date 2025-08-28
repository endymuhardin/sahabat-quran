package com.sahabatquran.webapp.functional.playwright;

import com.sahabatquran.webapp.page.playwright.StudentRegistrationPagePlaywright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Playwright version of student registration tests.
 * 
 * Advantages of Playwright over Selenium for multi-step forms:
 * - Better handling of dynamic content and JavaScript interactions
 * - More reliable step navigation detection
 * - Built-in auto-waiting reduces flaky tests
 * - Better debugging with traces and screenshots
 */
@DisplayName("Student Registration - Playwright Tests")
class StudentRegistrationPlaywrightTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("Should complete full student registration workflow")
    void shouldCompleteFullStudentRegistrationWorkflow() {
        System.out.println("🚀 Starting Complete Registration Test with Playwright...");
        
        // Given
        StudentRegistrationPagePlaywright registrationPage = new StudentRegistrationPagePlaywright(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.completeFullRegistration();
        
        // Then
        page.waitForURL("**/register/confirmation**");
        assertTrue(registrationPage.isConfirmationDisplayed(), "Confirmation message should be displayed");
        
        System.out.println("✅ Complete Registration Test completed!");
    }
    
    @Test
    @DisplayName("Should validate required fields and prevent empty submission")
    void shouldValidateRequiredFieldsAndPreventEmptySubmission() {
        System.out.println("🚀 Starting Required Fields Validation Test with Playwright...");
        
        // Given
        StudentRegistrationPagePlaywright registrationPage = new StudentRegistrationPagePlaywright(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.clickNext();
        
        // Then - Should stay on step 1 due to validation
        assertTrue(registrationPage.isOnStep(1), "Should remain on step 1");
        assertTrue(page.url().contains("/register"), "Should still be on registration page");
        
        System.out.println("✅ Required Fields Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should clear form data when requested")
    void shouldClearFormDataWhenRequested() {
        System.out.println("🚀 Starting Clear Form Test with Playwright...");
        
        // Given
        StudentRegistrationPagePlaywright registrationPage = new StudentRegistrationPagePlaywright(page);
        
        // When
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
        
        System.out.println("✅ Clear Form Test completed!");
    }
}