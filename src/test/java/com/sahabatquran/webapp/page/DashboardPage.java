package com.sahabatquran.webapp.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class DashboardPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    @FindBy(id = "navigation-title")
    private WebElement navigationTitle;
    
    @FindBy(id = "user-display-name")
    private WebElement userDisplayName;
    
    @FindBy(id = "logout-button")
    private WebElement logoutButton;
    
    @FindBy(id = "user-menu-button")
    private WebElement userMenuButton;
    
    @FindBy(id = "welcome-message")
    private WebElement welcomeMessage;
    
    @FindBy(className = "permission-badge")
    private List<WebElement> permissionBadges;
    
    @FindBy(id = "admin-panel-link")
    private WebElement adminPanelLink;
    
    @FindBy(id = "instructor-panel-link")
    private WebElement instructorPanelLink;
    
    @FindBy(id = "student-panel-link")
    private WebElement studentPanelLink;
    
    @FindBy(id = "staff-panel-link")
    private WebElement staffPanelLink;
    
    @FindBy(id = "management-panel-link")
    private WebElement managementPanelLink;
    
    // Navigation Menu Elements
    @FindBy(id = "admin-menu-button")
    private WebElement adminMenuButton;
    
    @FindBy(id = "academic-menu-button")
    private WebElement academicMenuButton;
    
    @FindBy(id = "finance-menu-button")
    private WebElement financeMenuButton;
    
    @FindBy(id = "event-menu-button")
    private WebElement eventsMenuButton;
    
    @FindBy(id = "reports-menu-button")
    private WebElement reportsMenuButton;
    
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Default fallback
        PageFactory.initElements(driver, this);
    }
    
    public DashboardPage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
        PageFactory.initElements(driver, this);
    }
    
    public boolean isOnDashboard() {
        try {
            wait.until(ExpectedConditions.visibilityOf(navigationTitle));
            return getCurrentUrl().contains("/dashboard") && 
                   navigationTitle.getText().contains("Yayasan Sahabat Quran");
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    public String getUserDisplayName() {
        wait.until(ExpectedConditions.visibilityOf(userDisplayName));
        return userDisplayName.getText();
    }
    
    public String getWelcomeMessage() {
        wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
        return welcomeMessage.getText();
    }
    
    public int getPermissionCount() {
        wait.until(ExpectedConditions.visibilityOfAllElements(permissionBadges));
        return permissionBadges.size();
    }
    
    public boolean hasPermissionBadge(String permission) {
        return permissionBadges.stream()
            .anyMatch(badge -> badge.getText().contains(permission));
    }
    
    public boolean isAdminPanelVisible() {
        try {
            return adminPanelLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isInstructorPanelVisible() {
        try {
            return instructorPanelLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isStudentPanelVisible() {
        try {
            return studentPanelLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isStaffPanelVisible() {
        try {
            return staffPanelLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isManagementPanelVisible() {
        try {
            return managementPanelLink.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void clickLogout() {
        // First open the user dropdown menu
        clickUserMenu();
        
        // Wait for dropdown to open and logout button to be visible and clickable
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        logoutButton.click();
    }
    
    public void logout() {
        clickLogout();
    }
    
    public void clickAdminPanel() {
        if (isAdminPanelVisible()) {
            adminPanelLink.click();
        }
    }
    
    public void clickInstructorPanel() {
        if (isInstructorPanelVisible()) {
            instructorPanelLink.click();
        }
    }
    
    public void clickStudentPanel() {
        if (isStudentPanelVisible()) {
            studentPanelLink.click();
        }
    }
    
    public void clickStaffPanel() {
        if (isStaffPanelVisible()) {
            staffPanelLink.click();
        }
    }
    
    public void clickManagementPanel() {
        if (isManagementPanelVisible()) {
            managementPanelLink.click();
        }
    }
    
    // Navigation Menu Methods
    public boolean isAdminMenuVisible() {
        try {
            return adminMenuButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isAcademicMenuVisible() {
        try {
            return academicMenuButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isFinanceMenuVisible() {
        try {
            return financeMenuButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isEventsMenuVisible() {
        try {
            return eventsMenuButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isReportsMenuVisible() {
        try {
            return reportsMenuButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public void clickAdminMenu() {
        if (isAdminMenuVisible()) {
            wait.until(ExpectedConditions.elementToBeClickable(adminMenuButton));
            adminMenuButton.click();
        }
    }
    
    public void clickAcademicMenu() {
        if (isAcademicMenuVisible()) {
            wait.until(ExpectedConditions.elementToBeClickable(academicMenuButton));
            academicMenuButton.click();
        }
    }
    
    public boolean isMenuItemVisible(String menuItemText) {
        try {
            // Use linkText locator instead of xpath
            WebElement menuItem = driver.findElement(By.linkText(menuItemText));
            return menuItem.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    // Admin Menu Items
    public boolean isUserManagementVisible() {
        return isMenuItemVisible("Manajemen Pengguna");
    }
    
    public boolean isSystemSettingsVisible() {
        return isMenuItemVisible("Pengaturan Sistem");
    }
    
    public boolean isAuditLogVisible() {
        return isMenuItemVisible("Log Audit");
    }
    
    // Academic Menu Items
    public boolean isClassManagementVisible() {
        return isMenuItemVisible("Manajemen Kelas");
    }
    
    public boolean isEnrollmentVisible() {
        return isMenuItemVisible("Pendaftaran Siswa");
    }
    
    public boolean isAttendanceVisible() {
        return isMenuItemVisible("Kehadiran");
    }
    
    public boolean isAssessmentVisible() {
        return isMenuItemVisible("Penilaian");
    }
    
    // Finance Menu Items
    public boolean isBillingVisible() {
        return isMenuItemVisible("Tagihan");
    }
    
    public boolean isPaymentVisible() {
        return isMenuItemVisible("Pembayaran");
    }
    
    public boolean isPayrollVisible() {
        return isMenuItemVisible("Penggajian");
    }
    
    // User Profile Menu
    public boolean isUserMenuVisible() {
        try {
            return userMenuButton.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public void clickUserMenu() {
        if (isUserMenuVisible()) {
            wait.until(ExpectedConditions.elementToBeClickable(userMenuButton));
            userMenuButton.click();
            
            // Wait for dropdown menu to become visible (Alpine.js animation)
            wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        }
    }
    
    public boolean isProfileMenuItemVisible(String itemText) {
        clickUserMenu();
        try {
            // Use linkText locator and proper wait
            WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(itemText)));
            return menuItem.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}