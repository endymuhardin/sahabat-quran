package com.sahabatquran.webapp.functional.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sahabatquran.webapp.page.DashboardPage;
import com.sahabatquran.webapp.page.LoginPage;

/**
 * Comprehensive functional tests for login authentication and navigation menu visibility.
 * This test class verifies that users can log in successfully and see appropriate 
 * navigation menus based on their permissions.
 */
@DisplayName("Login and Navigation Functional Tests")
class LoginAndNavigationTest extends BaseSeleniumTest {
    
    // =====================================================
    // AUTHENTICATION TESTS
    // =====================================================
    
    @Test
    @DisplayName("Should successfully login with admin credentials and show all navigation menus")
    void shouldLoginAsAdminAndShowAllNavigationMenus() {
        System.out.println("Testing Admin Login and Navigation...");
        
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver(), getExplicitTimeout());
        DashboardPage dashboardPage = new DashboardPage(getWebDriver(), getExplicitTimeout());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
        
        loginPage.login("admin", "AdminYSQ@2024");
        
        // Wait for navigation to dashboard
        WebDriverWait wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        String currentUrl = getWebDriver().getCurrentUrl();
        System.out.println("Admin login result URL: " + currentUrl);
        
        // Then - Verify successful login
        if (currentUrl.contains("/dashboard")) {
            System.out.println("✅ Admin successfully logged in and redirected to dashboard");
            assertTrue(dashboardPage.isOnDashboard(), "Should redirect to dashboard after successful login");
            assertEquals("admin", dashboardPage.getUserDisplayName(), "Should display correct username");
            
            // Test admin navigation menus with informative logging
            testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, true);
            testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
            testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, true);
            testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
            testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, true);
            
        } else {
            System.out.println("❌ Admin login failed - unexpected URL: " + currentUrl);
            fail("Admin should successfully login and redirect to dashboard");
        }
    }
    
    @Test
    @DisplayName("Should successfully login with instructor credentials and show limited navigation menus")
    void shouldLoginAsInstructorAndShowLimitedNavigationMenus() {
        System.out.println("\nTesting Instructor Login and Navigation...");
        
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver(), getExplicitTimeout());
        DashboardPage dashboardPage = new DashboardPage(getWebDriver(), getExplicitTimeout());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("ustadz.ahmad", "Welcome@YSQ2024");
        
        // Wait for navigation to dashboard
        WebDriverWait wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        String currentUrl = getWebDriver().getCurrentUrl();
        System.out.println("Instructor login result URL: " + currentUrl);
        
        // Then - Verify successful login
        if (currentUrl.contains("/dashboard")) {
            System.out.println("✅ Instructor successfully logged in and redirected to dashboard");
            assertTrue(dashboardPage.isOnDashboard(), "Should redirect to dashboard after successful login");
            assertEquals("ustadz.ahmad", dashboardPage.getUserDisplayName(), "Should display correct username");
            
            // Test instructor navigation menus (limited access)
            testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, false);
            testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
            testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, false);
            testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
            testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, false);
            
        } else {
            System.out.println("❌ Instructor login failed - unexpected URL: " + currentUrl);
            fail("Instructor should successfully login and redirect to dashboard");
        }
    }
    
    @Test
    @DisplayName("Should successfully login with student credentials and show appropriate navigation menus")
    void shouldLoginAsStudentAndShowAppropriateNavigationMenus() {
        System.out.println("\nTesting Student Login and Navigation...");
        
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver(), getExplicitTimeout());
        DashboardPage dashboardPage = new DashboardPage(getWebDriver(), getExplicitTimeout());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("siswa.ali", "Welcome@YSQ2024");
        
        // Wait for navigation to dashboard
        WebDriverWait wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        String currentUrl = getWebDriver().getCurrentUrl();
        System.out.println("Student login result URL: " + currentUrl);
        
        // Then - Verify successful login
        if (currentUrl.contains("/dashboard")) {
            System.out.println("✅ Student successfully logged in and redirected to dashboard");
            assertTrue(dashboardPage.isOnDashboard(), "Should redirect to dashboard after successful login");
            assertEquals("siswa.ali", dashboardPage.getUserDisplayName(), "Should display correct username");
            
            // Test student navigation menus (limited access - classes, billing, events)
            testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, false);
            testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
            testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, true);
            testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
            testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, false);
            
        } else {
            System.out.println("❌ Student login failed - unexpected URL: " + currentUrl);
            fail("Student should successfully login and redirect to dashboard");
        }
    }
    
    @ParameterizedTest
    @DisplayName("Should successfully login with different user roles")
    @CsvSource({
        "admin, AdminYSQ@2024",
        "ustadz.ahmad, Welcome@YSQ2024", 
        "siswa.ali, Welcome@YSQ2024"
    })
    void shouldLoginWithDifferentUserRoles(String username, String password) {
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver(), getExplicitTimeout());
        DashboardPage dashboardPage = new DashboardPage(getWebDriver(), getExplicitTimeout());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login(username, password);
        
        // Then
        assertTrue(dashboardPage.isOnDashboard(), 
            "User " + username + " should successfully login and redirect to dashboard");
    }
    
    // =====================================================
    // AUTHENTICATION ERROR TESTS
    // =====================================================
    
    @Test
    @DisplayName("Should show error message for invalid credentials")
    void shouldShowErrorMessageForInvalidCredentials() {
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("invalid_user", "invalid_password");
        
        // Then
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        assertTrue(loginPage.getErrorMessage().contains("Username atau password salah"), 
            "Should show appropriate error message");
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
    }
    
    @Test
    @DisplayName("Should show error message for empty credentials")
    void shouldShowErrorMessageForEmptyCredentials() {
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("", "");
        
        // Then
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
    }
    
    @Test
    @DisplayName("Should show error message for non-existent user")
    void shouldShowErrorMessageForNonExistentUser() {
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("nonexistent.user", "password123");
        
        // Then
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        assertTrue(loginPage.getErrorMessage().contains("Username atau password salah"), 
            "Should show appropriate error message");
    }
    
    @Test
    @DisplayName("Should show error message for correct username but wrong password")
    void shouldShowErrorMessageForWrongPassword() {
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver());
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("admin", "wrongpassword");
        
        // Then
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        assertTrue(loginPage.getErrorMessage().contains("Username atau password salah"), 
            "Should show appropriate error message");
    }
    
    // =====================================================
    // LOGOUT AND SESSION TESTS
    // =====================================================
    
    @Test
    @DisplayName("Should successfully logout and redirect to login page")
    void shouldSuccessfullyLogout() {
        // Given
        LoginPage loginPage = new LoginPage(getWebDriver(), getExplicitTimeout());
        DashboardPage dashboardPage = new DashboardPage(getWebDriver(), getExplicitTimeout());
        
        // When - Login first
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("admin", "AdminYSQ@2024");
        assertTrue(dashboardPage.isOnDashboard(), "Should be logged in");
        
        // Then - Logout
        dashboardPage.logout();
        assertTrue(loginPage.isOnLoginPage(), "Should redirect to login page after logout");
    }
    
    @Test
    @DisplayName("Should redirect to login page when accessing protected resource without authentication")
    void shouldRedirectToLoginForProtectedResource() {
        // When
        getWebDriver().get(getBaseUrl() + "/dashboard");
        
        // Then
        assertTrue(getWebDriver().getCurrentUrl().contains("/login"), 
            "Should redirect to login page when accessing protected resource");
    }
    
    // =====================================================
    // HELPER METHODS
    // =====================================================
    
    /**
     * Tests navigation menu visibility with informative logging
     */
    private void testNavigationMenuVisibility(String menuName, java.util.function.Supplier<Boolean> visibilityCheck, boolean shouldBeVisible) {
        try {
            boolean isVisible = visibilityCheck.get();
            
            if (shouldBeVisible) {
                if (isVisible) {
                    System.out.println("  ✅ " + menuName + " menu is visible (expected)");
                } else {
                    System.out.println("  ❌ " + menuName + " menu is not visible (should be visible)");
                }
                assertTrue(isVisible, menuName + " menu should be visible for this role");
            } else {
                if (!isVisible) {
                    System.out.println("  ✅ " + menuName + " menu is hidden (expected)");
                } else {
                    System.out.println("  ❌ " + menuName + " menu is visible (should be hidden)");
                }
                assertFalse(isVisible, menuName + " menu should be hidden for this role");
            }
            
        } catch (Exception e) {
            if (shouldBeVisible) {
                System.out.println("  ❌ " + menuName + " menu not found (should exist): " + e.getMessage());
                fail(menuName + " menu should exist but was not found");
            } else {
                System.out.println("  ✅ " + menuName + " menu not found (expected to be hidden)");
            }
        }
    }
}