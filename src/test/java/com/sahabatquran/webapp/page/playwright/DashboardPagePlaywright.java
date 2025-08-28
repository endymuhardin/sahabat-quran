package com.sahabatquran.webapp.page.playwright;

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
public class DashboardPagePlaywright {
    
    private final Page page;
    
    // Locators for user information
    private final Locator userDisplayName;
    private final Locator logoutButton;
    
    // Navigation menu button locators (dropdown triggers)
    private final Locator adminMenuButton;
    private final Locator academicMenuButton;
    private final Locator financeMenuButton;
    private final Locator eventsMenuButton;
    private final Locator reportsMenuButton;
    
    public DashboardPagePlaywright(Page page) {
        this.page = page;
        
        // User display name from the navigation HTML
        this.userDisplayName = page.locator("#user-display-name, span[sec\\:authentication='name']");
        
        // Logout button from the user dropdown (need to open dropdown first)
        this.logoutButton = page.locator("#logout-button, form[action*='logout'] button");
        
        // Navigation menu button locators based on actual HTML structure
        this.adminMenuButton = page.locator("#admin-menu-button");
        this.academicMenuButton = page.locator("#academic-menu-button");
        this.financeMenuButton = page.locator("#finance-menu-button");
        this.eventsMenuButton = page.locator("#event-menu-button");
        this.reportsMenuButton = page.locator("#reports-menu-button");
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
                page.waitForSelector("#loginForm", 
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
    
    // Navigation menu button visibility methods
    public boolean isAdminMenuVisible() {
        try {
            assertThat(adminMenuButton).isVisible();
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    public boolean isAcademicMenuVisible() {
        try {
            assertThat(academicMenuButton).isVisible();
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    public boolean isFinanceMenuVisible() {
        try {
            assertThat(financeMenuButton).isVisible();
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    public boolean isEventsMenuVisible() {
        try {
            assertThat(eventsMenuButton).isVisible();
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    public boolean isReportsMenuVisible() {
        try {
            assertThat(reportsMenuButton).isVisible();
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    // Navigation actions - click menu buttons to open dropdowns
    public void clickAdminMenu() {
        adminMenuButton.click();
    }
    
    public void clickAcademicMenu() {
        academicMenuButton.click();
    }
    
    public void clickFinanceMenu() {
        financeMenuButton.click();
    }
    
    public void clickEventsMenu() {
        eventsMenuButton.click();
    }
    
    public void clickReportsMenu() {
        reportsMenuButton.click();
    }
    
    // Verification methods with built-in waiting
    public void expectToBeOnDashboard() {
        assertThat(page).hasURL("**/dashboard**");
    }
    
    public void expectUserDisplayName(String expectedName) {
        assertThat(userDisplayName).containsText(expectedName);
    }
    
    public void expectAdminMenuVisible() {
        assertThat(adminMenuButton).isVisible();
    }
    
    public void expectAdminMenuHidden() {
        assertThat(adminMenuButton).not().isVisible();
    }
    
    public void expectAcademicMenuVisible() {
        assertThat(academicMenuButton).isVisible();
    }
    
    public void expectAcademicMenuHidden() {
        assertThat(academicMenuButton).not().isVisible();
    }
    
    public void expectFinanceMenuVisible() {
        assertThat(financeMenuButton).isVisible();
    }
    
    public void expectFinanceMenuHidden() {
        assertThat(financeMenuButton).not().isVisible();
    }
    
    public void expectEventsMenuVisible() {
        assertThat(eventsMenuButton).isVisible();
    }
    
    public void expectEventsMenuHidden() {
        assertThat(eventsMenuButton).not().isVisible();
    }
    
    public void expectReportsMenuVisible() {
        assertThat(reportsMenuButton).isVisible();
    }
    
    public void expectReportsMenuHidden() {
        assertThat(reportsMenuButton).not().isVisible();
    }
}