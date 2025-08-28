package com.sahabatquran.webapp.functional.scenarios;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.DashboardPage;
import com.sahabatquran.webapp.functional.page.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Login and navigation success scenario tests.
 * 
 * This class focuses on testing successful user authentication workflows
 * and role-based navigation functionality.
 */
@Slf4j
@DisplayName("Login and Navigation Success Scenarios")
class LoginAndNavigationTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("Should successfully login as admin and show all navigation menus")
    void shouldLoginAsAdminAndShowAllNavigationMenus() {
        log.info("🚀 Testing Admin Login and Navigation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        DashboardPage dashboardPage = new DashboardPage(page);
        
        // When
        loginPage.navigateToLoginPage(getBaseUrl());
        assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
        
        loginPage.login("admin", "AdminYSQ@2024");
        
        // Wait for navigation to dashboard
        page.waitForURL("**/dashboard");
        
        // Then - Verify successful login with debugging
        log.debug("Current URL: {}", page.url());
        log.debug("Page title: {}", page.title());
        
        boolean isOnDashboard = dashboardPage.isOnDashboard();
        if (!isOnDashboard) {
            log.warn("Dashboard detection failed - current page source length: {}", page.content().length());
            log.warn("Welcome message present: {}", (page.locator("#welcome-message").count() > 0));
            log.warn("Quick actions present: {}", (page.locator(":has-text('Menu Akses Cepat')").count() > 0));
        }
        
        assertTrue(isOnDashboard, "Should redirect to dashboard after successful login");
        String actualUsername = dashboardPage.getUserDisplayName();
        log.debug("Actual username displayed: {}", actualUsername);
        assertEquals("admin", actualUsername, "Should display correct username");
        
        // Test admin navigation menus with informative logging
        testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, true);
        testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
        testNavigationMenuVisibility("Finance", dashboardPage::isFinanceMenuVisible, true);
        testNavigationMenuVisibility("Events", dashboardPage::isEventsMenuVisible, true);
        testNavigationMenuVisibility("Reports", dashboardPage::isReportsMenuVisible, true);
        
        log.info("✅ Admin login and navigation test completed!");
    }
    
    @Test
    @DisplayName("Should successfully login as instructor and show limited navigation menus")
    void shouldLoginAsInstructorAndShowLimitedNavigationMenus() {
        log.info("🚀 Testing Instructor Login and Navigation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        DashboardPage dashboardPage = new DashboardPage(page);
        
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
        
        log.info("✅ Instructor login and navigation test completed!");
    }
    
    @Test
    @DisplayName("Should successfully login as student and show appropriate navigation menus")
    void shouldLoginAsStudentAndShowAppropriateNavigationMenus() {
        log.info("🚀 Testing Student Login and Navigation...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        DashboardPage dashboardPage = new DashboardPage(page);
        
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
        
        log.info("✅ Student login and navigation test completed!");
    }
    
    @Test
    @DisplayName("Should successfully logout and redirect to login page")
    void shouldSuccessfullyLogout() {
        log.info("🚀 Testing logout functionality...");
        
        // Given
        LoginPage loginPage = new LoginPage(page);
        DashboardPage dashboardPage = new DashboardPage(page);
        
        // When - Login first
        loginPage.navigateToLoginPage(getBaseUrl());
        loginPage.login("admin", "AdminYSQ@2024");
        page.waitForURL("**/dashboard");
        assertTrue(dashboardPage.isOnDashboard(), "Should be logged in");
        
        // Then - Logout
        dashboardPage.logout();
        assertTrue(loginPage.isOnLoginPage(), "Should redirect to login page after logout");
        
        log.info("✅ Logout test completed!");
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
                    log.debug("  ✅ {} menu is visible (expected)", menuName);
                } else {
                    log.warn("  ❌ {} menu is not visible (should be visible)", menuName);
                }
                assertTrue(isVisible, menuName + " menu should be visible for this role");
            } else {
                if (!isVisible) {
                    log.debug("  ✅ {} menu is hidden (expected)", menuName);
                } else {
                    log.warn("  ❌ {} menu is visible (should be hidden)", menuName);
                }
                assertFalse(isVisible, menuName + " menu should be hidden for this role");
            }
            
        } catch (Exception e) {
            if (shouldBeVisible) {
                log.error("  ❌ {} menu not found (should exist): {}", menuName, e.getMessage());
                fail(menuName + " menu should exist but was not found");
            } else {
                log.debug("  ✅ {} menu not found (expected to be hidden)", menuName);
            }
        }
    }
}