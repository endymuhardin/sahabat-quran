package com.sahabatquran.webapp.functional.playwright;

import com.sahabatquran.webapp.page.playwright.DashboardPagePlaywright;
import com.sahabatquran.webapp.page.playwright.LoginPagePlaywright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Playwright version of login and navigation tests.
 * 
 * Key differences from Selenium version:
 * - Uses Playwright's built-in assertions with auto-waiting
 * - More concise syntax for element interactions
 * - Better handling of dynamic content
 * - Built-in screenshot capabilities for debugging
 */
@DisplayName("Login and Navigation - Playwright Tests")
class LoginAndNavigationPlaywrightTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("Should successfully login as admin and show all navigation menus")
    void shouldLoginAsAdminAndShowAllNavigationMenus() {
        System.out.println("🚀 Testing Admin Login and Navigation with Playwright...");
        
        // Given
        LoginPagePlaywright loginPage = new LoginPagePlaywright(page);
        DashboardPagePlaywright dashboardPage = new DashboardPagePlaywright(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
        
        loginPage.login("admin", "AdminYSQ@2024");
        
        // Wait for navigation to dashboard
        page.waitForURL("**/dashboard");
        
        // Then - Verify successful login with debugging
        System.out.println("Current URL: " + page.url());
        System.out.println("Page title: " + page.title());
        
        boolean isOnDashboard = dashboardPage.isOnDashboard();
        if (!isOnDashboard) {
            System.out.println("Dashboard detection failed - current page source length: " + page.content().length());
            System.out.println("Welcome message present: " + (page.locator("#welcome-message").count() > 0));
            System.out.println("Quick actions present: " + (page.locator(":has-text('Menu Akses Cepat')").count() > 0));
        }
        
        assertTrue(isOnDashboard, "Should redirect to dashboard after successful login");
        String actualUsername = dashboardPage.getUserDisplayName();
        System.out.println("Actual username displayed: " + actualUsername);
        assertEquals("admin", actualUsername, "Should display correct username");
        
        // Test admin navigation menus with informative logging
        testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, true);
        testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
        testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, true);
        testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
        testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, true);
        
        System.out.println("✅ Admin login and navigation test completed!");
    }
    
    @Test
    @DisplayName("Should successfully login as instructor and show limited navigation menus")
    void shouldLoginAsInstructorAndShowLimitedNavigationMenus() {
        System.out.println("🚀 Testing Instructor Login and Navigation with Playwright...");
        
        // Given
        LoginPagePlaywright loginPage = new LoginPagePlaywright(page);
        DashboardPagePlaywright dashboardPage = new DashboardPagePlaywright(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("ustadz.ahmad", "Welcome@YSQ2024");
        
        // Wait for navigation to dashboard
        page.waitForURL("**/dashboard");
        
        // Then
        assertTrue(dashboardPage.isOnDashboard(), "Should redirect to dashboard after successful login");
        assertEquals("ustadz.ahmad", dashboardPage.getUserDisplayName(), "Should display correct username");
        
        // Test instructor navigation menus (limited access)
        testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, false);
        testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
        testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, false);
        testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
        testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, false);
        
        System.out.println("✅ Instructor login and navigation test completed!");
    }
    
    @Test
    @DisplayName("Should successfully login as student and show appropriate navigation menus")
    void shouldLoginAsStudentAndShowAppropriateNavigationMenus() {
        System.out.println("🚀 Testing Student Login and Navigation with Playwright...");
        
        // Given
        LoginPagePlaywright loginPage = new LoginPagePlaywright(page);
        DashboardPagePlaywright dashboardPage = new DashboardPagePlaywright(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("siswa.ali", "Welcome@YSQ2024");
        
        // Wait for navigation to dashboard
        page.waitForURL("**/dashboard");
        
        // Then
        assertTrue(dashboardPage.isOnDashboard(), "Should redirect to dashboard after successful login");
        assertEquals("siswa.ali", dashboardPage.getUserDisplayName(), "Should display correct username");
        
        // Test student navigation menus (limited access - classes, billing, events)
        testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, false);
        testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
        testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, true);
        testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
        testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, false);
        
        System.out.println("✅ Student login and navigation test completed!");
    }
    
    @Test
    @DisplayName("Should show error message for invalid credentials")
    void shouldShowErrorMessageForInvalidCredentials() {
        System.out.println("🚀 Testing invalid credentials with Playwright...");
        
        // Given
        LoginPagePlaywright loginPage = new LoginPagePlaywright(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("invalid_user", "invalid_password");
        
        // Then
        assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        assertTrue(loginPage.getErrorMessage().contains("Username atau password salah"), 
            "Should show appropriate error message");
        assertTrue(loginPage.isOnLoginPage(), "Should remain on login page");
        
        System.out.println("✅ Invalid credentials test completed!");
    }
    
    @Test
    @DisplayName("Should successfully logout and redirect to login page")
    void shouldSuccessfullyLogout() {
        System.out.println("🚀 Testing logout functionality with Playwright...");
        
        // Given
        LoginPagePlaywright loginPage = new LoginPagePlaywright(page);
        DashboardPagePlaywright dashboardPage = new DashboardPagePlaywright(page);
        
        // When - Login first
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("admin", "AdminYSQ@2024");
        page.waitForURL("**/dashboard");
        assertTrue(dashboardPage.isOnDashboard(), "Should be logged in");
        
        // Then - Logout
        dashboardPage.logout();
        assertTrue(loginPage.isOnLoginPage(), "Should redirect to login page after logout");
        
        System.out.println("✅ Logout test completed!");
    }
    
    @Test
    @DisplayName("Should redirect to login page when accessing protected resource without authentication")
    void shouldRedirectToLoginForProtectedResource() {
        System.out.println("🚀 Testing protected resource access with Playwright...");
        
        // Given
        LoginPagePlaywright loginPage = new LoginPagePlaywright(page);
        
        // When
        page.navigate(getBaseUrl() + "/dashboard");
        
        // Then
        page.waitForURL("**/login");
        assertTrue(loginPage.isOnLoginPage(), "Should redirect to login page when accessing protected resource");
        
        System.out.println("✅ Protected resource test completed!");
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