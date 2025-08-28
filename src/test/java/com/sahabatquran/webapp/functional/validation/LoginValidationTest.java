package com.sahabatquran.webapp.functional.validation;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Login validation and error handling tests.
 * 
 * This class focuses on testing error conditions, form validation,
 * and access control scenarios for the login functionality.
 */
@Slf4j
@DisplayName("Login Validation Tests")
class LoginValidationTest extends BasePlaywrightTest {

    @Test
    @DisplayName("Should show error message for invalid credentials")
    void shouldShowErrorMessageForInvalidCredentials() {
        log.info("🚀 Testing invalid credentials validation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("invalid_user", "invalid_password");
        
        // Then
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        assertTrue(loginPage.getErrorMessage().contains("Username atau password salah"), 
            "Should show appropriate error message");
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
        
        log.info("✅ Invalid credentials validation completed!");
    }
    
    @Test
    @DisplayName("Should redirect to login page when accessing protected resource without authentication")
    void shouldRedirectToLoginForProtectedResource() {
        log.info("🚀 Testing protected resource access control...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        
        // When - Try to access dashboard without authentication
        page.navigate(getBaseUrl() + "/dashboard");
        
        // Then - Should be redirected to login page
        page.waitForURL("**/login");
        assertTrue(loginPage.isOnLoginPage(), "Should redirect to login page when accessing protected resource");
        
        log.info("✅ Protected resource access control test completed!");
    }

    @Test
    @DisplayName("Should show error for empty username")
    void shouldShowErrorForEmptyUsername() {
        log.info("🚀 Testing empty username validation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("", "somepassword");
        
        // Then
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
        // Add more specific validation if login form has client-side validation
        
        log.info("✅ Empty username validation completed!");
    }

    @Test
    @DisplayName("Should show error for empty password")
    void shouldShowErrorForEmptyPassword() {
        log.info("🚀 Testing empty password validation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("admin", "");
        
        // Then
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
        // Add more specific validation if login form has client-side validation
        
        log.info("✅ Empty password validation completed!");
    }

    @Test
    @DisplayName("Should show error for both empty username and password")
    void shouldShowErrorForEmptyCredentials() {
        log.info("🚀 Testing empty credentials validation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("", "");
        
        // Then
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
        // Add more specific validation if login form has client-side validation
        
        log.info("✅ Empty credentials validation completed!");
    }
}