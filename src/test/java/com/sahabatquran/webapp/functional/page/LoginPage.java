package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Login functionality.
 * 
 * Modern Playwright implementation:
 * - Built-in waiting with assertions
 * - More reliable element detection
 * - Cleaner API with better readability
 */
public class LoginPage {
    
    private final Page page;
    
    // Locators
    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator loginButton;
    private final Locator errorMessage;
    
    public LoginPage(Page page) {
        this.page = page;
        this.usernameField = page.locator("#username");
        this.passwordField = page.locator("#password");
        this.loginButton = page.locator("#login-button");
        this.errorMessage = page.locator("#error-message");
    }
    
    public void navigateToLoginPage(String baseUrl) {
        page.navigate(baseUrl + "/login");
        assertThat(page).hasTitle("Login - Yayasan Sahabat Quran");
    }
    
    public void login(String username, String password) {
        usernameField.fill(username);
        passwordField.fill(password);
        loginButton.click();
    }
    
    public boolean isOnLoginPage() {
        try {
            // Check if current URL contains login
            String currentUrl = page.url();
            return currentUrl.contains("/login");
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isErrorMessageDisplayed() {
        try {
            assertThat(errorMessage).isVisible();
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
    
    public String getErrorMessage() {
        return errorMessage.textContent();
    }
    
    public void fillUsername(String username) {
        usernameField.fill(username);
    }
    
    public void fillPassword(String password) {
        passwordField.fill(password);
    }
    
    public void clickLogin() {
        loginButton.click();
    }
    
    public void waitForErrorMessage() {
        errorMessage.waitFor();
    }
    
    public void expectToBeOnLoginPage() {
        assertThat(page).hasURL("**/login*");
    }
    
    public void expectErrorMessage(String expectedMessage) {
        assertThat(errorMessage).containsText(expectedMessage);
    }
}