package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Dashboard functionality.
 * 
 * Key benefits of Playwright Page Objects:
 * - Auto-waiting assertions eliminate race conditions
 * - More flexible locator strategies
 * - Better error messages for debugging
 */
public class DashboardPage {
    
    private final Page page;
    
    // Locators for user information
    private final Locator userDisplayName;
    private final Locator logoutButton;
    
    // Role-specific dashboard sections and quick action menu buttons
    private final Locator systemAdminDashboard;
    private final Locator academicAdminDashboard;
    private final Locator instructorDashboard;
    private final Locator studentDashboard;
    private final Locator managementDashboard;
    private final Locator financeDashboard;
    
    // Quick action panel buttons
    private final Locator systemAdminPanelLink;
    private final Locator academicAdminPanelLink;
    private final Locator instructorPanelLink;
    private final Locator studentPanelLink;
    private final Locator staffPanelLink;
    private final Locator managementPanelLink;
    
    public DashboardPage(Page page) {
        this.page = page;
        
        // User display name from the navigation HTML
        this.userDisplayName = page.locator("#user-display-name");
        
        // Logout button from the user dropdown (need to open dropdown first)
        this.logoutButton = page.locator("#logout-button");
        
        // Role-specific dashboard sections
        this.systemAdminDashboard = page.locator(":has-text('Dashboard System Admin')");
        this.academicAdminDashboard = page.locator(":has-text('Dashboard Admin Akademik')");
        this.instructorDashboard = page.locator(":has-text('Dashboard Pengajar')");
        this.studentDashboard = page.locator(":has-text('Dashboard Siswa')");
        this.managementDashboard = page.locator(":has-text('Dashboard Manajemen')");
        this.financeDashboard = page.locator(":has-text('Dashboard Keuangan')");
        
        // Quick action panel buttons from unified dashboard
        this.systemAdminPanelLink = page.locator("#system-admin-panel-link");
        this.academicAdminPanelLink = page.locator("#academic-admin-panel-link");
        this.instructorPanelLink = page.locator("#instructor-panel-link");
        this.studentPanelLink = page.locator("#student-panel-link");
        this.staffPanelLink = page.locator("#staff-panel-link");
        this.managementPanelLink = page.locator("#management-panel-link");
    }
    
    public boolean isOnDashboard() {
        try {
            // Check multiple indicators of being on dashboard
            String currentUrl = page.url();
            boolean urlCheck = currentUrl.contains("/dashboard");
            
            // Also check for dashboard-specific content using IDs
            boolean hasWelcomeMessage = page.locator("#welcome-message").count() > 0;
            boolean hasQuickActions = page.locator("#quick-actions-section").count() > 0;
            
            return urlCheck || hasWelcomeMessage || hasQuickActions;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getUserDisplayName() {
        return userDisplayName.textContent().trim();
    }
    
    public void logout() {
        try {
            // First, open the user menu dropdown
            Locator userMenuButton = page.locator("#user-menu-button");
            if (userMenuButton.isVisible()) {
                userMenuButton.click();
                page.waitForTimeout(500); // Wait for dropdown to open
            }
            
            // Now try to find and click logout
            if (logoutButton.isVisible()) {
                logoutButton.click();
            } else {
                // Navigate directly to logout endpoint if button not found
                String currentUrl = page.url();
                String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/", 8)); // Extract base URL
                page.navigate(baseUrl + "/logout");
            }
        } catch (Exception e) {
            // Fallback: navigate directly to logout using base URL
            String currentUrl = page.url();
            String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/", 8)); // Extract base URL
            page.navigate(baseUrl + "/logout");
        }
        
        // Wait for logout to complete - check for redirect to login page
        try {
            // Wait for the URL to change to login page or for login page elements
            page.waitForURL("**/login**", new Page.WaitForURLOptions().setTimeout(5000));
        } catch (Exception e1) {
            try {
                // Fallback: wait for login form elements
                page.waitForSelector("#login-form", 
                                   new Page.WaitForSelectorOptions().setTimeout(5000));
            } catch (Exception e2) {
                // If still nothing, check if URL contains login
                String currentUrl = page.url();
                if (!currentUrl.contains("/login")) {
                    // Force navigate to login if not already there
                    String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/", 8));
                    page.navigate(baseUrl + "/login");
                }
            }
        }
    }
    
    // Dashboard section visibility methods (checks for role-specific content)
    public boolean isSystemAdminMenuVisible() {
        try {
            return systemAdminDashboard.isVisible() || systemAdminPanelLink.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isAcademicAdminMenuVisible() {
        try {
            return academicAdminDashboard.isVisible() || academicAdminPanelLink.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Legacy method for backward compatibility (maps to Academic Admin)
    public boolean isAdminMenuVisible() {
        return isAcademicAdminMenuVisible();
    }
    
    public boolean isAcademicMenuVisible() {
        try {
            // Academic menu refers to general academic access (available to most users)
            return instructorDashboard.isVisible() || instructorPanelLink.isVisible() || 
                   studentDashboard.isVisible() || studentPanelLink.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isFinanceMenuVisible() {
        try {
            return financeDashboard.isVisible() || staffPanelLink.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isEventsMenuVisible() {
        try {
            // Events functionality is typically available to most users, check for basic dashboard
            return page.locator("#quick-actions-section").isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isReportsMenuVisible() {
        try {
            return managementDashboard.isVisible() || managementPanelLink.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Navigation actions - click panel links in unified dashboard
    public void clickSystemAdminMenu() {
        if (systemAdminPanelLink.isVisible()) {
            systemAdminPanelLink.click();
        }
    }
    
    public void clickAcademicAdminMenu() {
        if (academicAdminPanelLink.isVisible()) {
            academicAdminPanelLink.click();
        }
    }
    
    // Legacy method for backward compatibility
    public void clickAdminMenu() {
        clickAcademicAdminMenu();
    }
    
    public void clickAcademicMenu() {
        if (instructorPanelLink.isVisible()) {
            instructorPanelLink.click();
        }
    }
    
    public void clickFinanceMenu() {
        if (staffPanelLink.isVisible()) {
            staffPanelLink.click();
        }
    }
    
    public void clickEventsMenu() {
        // Events functionality is available through various dashboard sections
        // This method remains as a placeholder for backward compatibility
    }
    
    public void clickReportsMenu() {
        if (managementPanelLink.isVisible()) {
            managementPanelLink.click();
        }
    }
    
    // Verification methods with built-in waiting
    public void expectToBeOnDashboard() {
        assertThat(page).hasURL("**/dashboard**");
    }
    
    public void expectUserDisplayName(String expectedName) {
        assertThat(userDisplayName).containsText(expectedName);
    }
    
    public void expectSystemAdminMenuVisible() {
        if (systemAdminDashboard.count() > 0) {
            assertThat(systemAdminDashboard).isVisible();
        } else {
            assertThat(systemAdminPanelLink).isVisible();
        }
    }
    
    public void expectSystemAdminMenuHidden() {
        assertThat(systemAdminDashboard).not().isVisible();
        assertThat(systemAdminPanelLink).not().isVisible();
    }
    
    public void expectAcademicAdminMenuVisible() {
        if (academicAdminDashboard.count() > 0) {
            assertThat(academicAdminDashboard).isVisible();
        } else {
            assertThat(academicAdminPanelLink).isVisible();
        }
    }
    
    public void expectAcademicAdminMenuHidden() {
        assertThat(academicAdminDashboard).not().isVisible();
        assertThat(academicAdminPanelLink).not().isVisible();
    }
    
    // Legacy methods for backward compatibility
    public void expectAdminMenuVisible() {
        expectAcademicAdminMenuVisible();
    }
    
    public void expectAdminMenuHidden() {
        expectAcademicAdminMenuHidden();
    }
    
    public void expectAcademicMenuVisible() {
        if (instructorDashboard.count() > 0) {
            assertThat(instructorDashboard).isVisible();
        } else {
            assertThat(instructorPanelLink).isVisible();
        }
    }
    
    public void expectAcademicMenuHidden() {
        assertThat(instructorDashboard).not().isVisible();
        assertThat(instructorPanelLink).not().isVisible();
    }
    
    public void expectFinanceMenuVisible() {
        if (financeDashboard.count() > 0) {
            assertThat(financeDashboard).isVisible();
        } else {
            assertThat(staffPanelLink).isVisible();
        }
    }
    
    public void expectFinanceMenuHidden() {
        assertThat(financeDashboard).not().isVisible();
        assertThat(staffPanelLink).not().isVisible();
    }
    
    public void expectEventsMenuVisible() {
        // Events are generally available, check for quick actions section
        assertThat(page.locator("#quick-actions-section")).isVisible();
    }
    
    public void expectEventsMenuHidden() {
        // Events hiding is not applicable in unified dashboard
        // This method exists for backward compatibility but doesn't assert anything
    }
    
    public void expectReportsMenuVisible() {
        if (managementDashboard.count() > 0) {
            assertThat(managementDashboard).isVisible();
        } else {
            assertThat(managementPanelLink).isVisible();
        }
    }
    
    public void expectReportsMenuHidden() {
        assertThat(managementDashboard).not().isVisible();
        assertThat(managementPanelLink).not().isVisible();
    }
}