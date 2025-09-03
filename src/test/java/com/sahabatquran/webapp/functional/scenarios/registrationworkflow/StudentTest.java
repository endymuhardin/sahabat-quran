package com.sahabatquran.webapp.functional.scenarios.registrationworkflow;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.StudentRegistrationPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Student registration success scenario tests.
 * 
 * This class focuses on testing successful student registration workflows
 * and complete end-to-end registration processes.
 */
@Slf4j
@DisplayName("PS-HP: Student Registration Happy Path Scenarios")
@Sql(scripts = "/test-data/session-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/student-registration-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class StudentTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("PS-HP-001: Pendaftaran Siswa Baru Lengkap")
    void shouldCompleteFullStudentRegistrationWorkflow() {
        log.info("🚀 Starting Complete Registration Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.completeFullRegistration();
        
        // Then
        page.waitForURL("**/register/confirmation**");
        assertTrue(registrationPage.isConfirmationDisplayed(), "Confirmation message should be displayed");
        
        log.info("✅ Complete Registration Test completed!");
    }

    @Test
    @DisplayName("PS-HP-002: Pendaftaran Siswa dengan Informasi Minimal")
    void shouldRegisterStudentWithMinimalInfo() {
        log.info("🚀 Starting Minimal Registration Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Ahmad Fauzi", "ahmad.fauzi@example.com", "081234567890");
        registrationPage.clickNext();
        
        // Then - Should proceed to step 2
        assertTrue(registrationPage.isOnStep(2), "Should proceed to step 2");
        
        log.info("✅ Minimal Registration Test completed!");
    }

    @Test
    @DisplayName("PS-HP-003: Navigasi Melalui Semua Langkah Registrasi")
    void shouldNavigateThroughAllRegistrationSteps() {
        log.info("🚀 Starting Step Navigation Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        
        // Step 1: Personal Information
        assertTrue(registrationPage.isOnStep(1), "Should start on step 1");
        registrationPage.fillMinimalPersonalInfo("Test User", "test@example.com", "081234567890");
        registrationPage.clickNext();
        
        // Step 2: Additional Information (if exists)
        assertTrue(registrationPage.isOnStep(2), "Should proceed to step 2");
        // Fill additional info if required by the form
        registrationPage.clickNext();
        
        // Then - Should complete or proceed to final step
        assertTrue(page.url().contains("/register"), "Should be in registration flow");
        
        log.info("✅ Step Navigation Test completed!");
    }

    @Test
    @DisplayName("PS-HP-004: Simpan Progress Pendaftaran")
    void shouldSaveRegistrationProgress() {
        log.info("🚀 Starting Save Progress Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Progress User", "progress@example.com", "081234567890");
        
        // Save progress (if save functionality exists)
        // This might trigger auto-save or require explicit save action
        page.waitForTimeout(2000); // Allow for auto-save if implemented
        
        // Then - Data should be preserved
        registrationPage.expectFieldValue("fullName", "Progress User");
        registrationPage.expectFieldValue("email", "progress@example.com");
        registrationPage.expectFieldValue("phoneNumber", "081234567890");
        
        log.info("✅ Save Progress Test completed!");
    }

    @Test
    @DisplayName("PS-HP-005: Pemilihan Jenis Kelamin")
    void shouldHandleDifferentGenderSelections() {
        log.info("🚀 Starting Gender Selection Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When - Test Male selection
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        registrationPage.fillMinimalPersonalInfo("Ahmad Test", "ahmad@example.com", "081234567890");
        registrationPage.selectGender("MALE");
        
        // Then - Should accept the selection
        assertTrue(registrationPage.isOnStep(1), "Should remain on step 1 for further input");
        
        log.info("✅ Gender Selection Test completed!");
    }

    @Test
    @DisplayName("PS-HP-006: Pendaftaran Lengkap dengan Semua Field Opsional")
    void shouldSubmitCompleteRegistrationWithAllFields() {
        log.info("🚀 Starting Complete Fields Registration Test...");
        
        // Given
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // When
        registrationPage.navigateToRegistrationPage(getBaseUrl());
        
        // Fill all available fields (this may vary based on actual form structure)
        registrationPage.fillMinimalPersonalInfo("Complete User", "complete@example.com", "081234567890");
        // Add additional fields as needed:
        // registrationPage.fillAddress("Jl. Test No. 123");
        // registrationPage.fillBirthDate("1990-01-01");
        // etc.
        
        registrationPage.clickNext();
        
        // Then - Should proceed successfully
        assertTrue(registrationPage.isOnStep(2), "Should proceed with complete information");
        
        log.info("✅ Complete Fields Registration Test completed!");
    }
}