package com.sahabatquran.webapp.functional.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validation tests for admin registration management.
 * 
 * Test Pattern: [BusinessProcess][Validation]Test
 * Run with: ./mvnw test -Dtest="*AdminRegistration*Validation*"
 */
@DisplayName("Admin Registration Management - Validation Tests")
class AdminRegistrationValidationTest extends BaseSeleniumTest {
    
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    @BeforeEach
    void setUpTest() {
        wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        js = (JavascriptExecutor) getWebDriver();
    }
    
    @Test
    @DisplayName("Should prevent unauthorized access to admin registration page")
    void shouldPreventUnauthorizedAccessToAdminRegistrationPage() {
        System.out.println("🚀 Starting Unauthorized Access Prevention Test...");
        System.out.println("📋 Test Scenario: AR-AP-001 - Akses Tanpa Otentikasi");
        
        // Try to access admin registrations without login
        System.out.println("📋 AR-AP-001 Step 1: Attempting access without authentication");
        getWebDriver().get(getBaseUrl() + "/admin/registrations");
        
        // Should redirect to login page
        System.out.println("📋 AR-AP-001 Step 2: Verifying redirect to login page");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.titleContains("Login")
        ));
        
        assertThat(getWebDriver().getCurrentUrl()).containsAnyOf("/login", "/signin");
        
        System.out.println("✅ Unauthorized Access Prevention Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent non-admin users from accessing admin registration page")
    void shouldPreventNonAdminUsersFromAccessingAdminRegistrationPage() {
        System.out.println("🚀 Starting Non-Admin Access Prevention Test...");
        
        // Login as regular user (assuming 'user' exists)
        loginAsUser();
        
        // Try to access admin registrations
        getWebDriver().get(getBaseUrl() + "/admin/registrations");
        
        // Should show access denied or redirect
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/access-denied"),
                ExpectedConditions.urlContains("/403"),
                ExpectedConditions.titleContains("Access Denied"),
                ExpectedConditions.titleContains("Forbidden")
        ));
        
        System.out.println("✅ Non-Admin Access Prevention Test completed!");
    }
    
    @Test
    @DisplayName("Should validate review form required fields")
    void shouldValidateReviewFormRequiredFields() {
        System.out.println("🚀 Starting Review Form Validation Test...");
        
        // Create a registration and login as admin
        String registrationId = createStudentRegistrationForValidation();
        loginAsAdmin();
        
        // Navigate to registration review
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/review");
        wait.until(ExpectedConditions.urlContains("/review"));
        
        // Try to submit empty review form
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        // Should stay on review page due to validation
        wait.until(ExpectedConditions.urlContains("/review"));
        assertThat(getWebDriver().getCurrentUrl()).contains("/review");
        
        System.out.println("✅ Review Form Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should validate placement test evaluation form")
    void shouldValidatePlacementTestEvaluationForm() {
        System.out.println("🚀 Starting Placement Test Evaluation Validation Test...");
        
        // Create and approve a registration first
        String registrationId = createAndApproveRegistration();
        
        // Navigate to placement test evaluation
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        
        // Try to submit empty evaluation form
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        // Should stay on evaluation page due to validation
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        assertThat(getWebDriver().getCurrentUrl()).contains("/placement-test");
        
        System.out.println("✅ Placement Test Evaluation Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent duplicate review submission")
    void shouldPreventDuplicateReviewSubmission() {
        System.out.println("🚀 Starting Duplicate Review Prevention Test...");
        
        // Create a registration and submit initial review
        String registrationId = createAndApproveRegistration();
        
        // Try to access review page again
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/review");
        
        // Should either redirect or show message that review is already done
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), 'sudah direview') or contains(text(), 'already reviewed')]"))
        ));
        
        System.out.println("✅ Duplicate Review Prevention Test completed!");
    }
    
    private void loginAsUser() {
        getWebDriver().get(getBaseUrl() + "/login");
        wait.until(ExpectedConditions.titleContains("Login"));
        
        getWebDriver().findElement(By.id("username")).sendKeys("user");
        getWebDriver().findElement(By.id("password")).sendKeys("UserYSQ@2024");
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        System.out.println("   ✓ Regular user logged in");
    }
    
    private void loginAsAdmin() {
        getWebDriver().get(getBaseUrl() + "/login");
        wait.until(ExpectedConditions.titleContains("Login"));
        
        getWebDriver().findElement(By.id("username")).sendKeys("admin");
        getWebDriver().findElement(By.id("password")).sendKeys("AdminYSQ@2024");
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        System.out.println("   ✓ Admin logged in");
    }
    
    private String createStudentRegistrationForValidation() {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill form quickly for validation testing
        getWebDriver().findElement(By.id("fullName")).sendKeys("Validation Test Student");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='MALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011990");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("081333333333");
        getWebDriver().findElement(By.id("email")).sendKeys("validation.test@example.com");
        getWebDriver().findElement(By.id("address")).sendKeys("Validation Test Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("Validation Emergency");
        getWebDriver().findElement(By.id("emergencyContactPhone")).sendKeys("081444444444");
        getWebDriver().findElement(By.xpath("//select[@id='emergencyContactRelation']/option[@value='Orang Tua']")).click();
        
        js.executeScript("window.scrollTo(0, document.querySelector('#section-education').offsetTop);");
        getWebDriver().findElement(By.xpath("//select[@id='educationLevel']/option[@value='S1']")).click();
        getWebDriver().findElement(By.xpath("//input[@name='previousTahsinExperience' and @value='false']")).click();
        
        js.executeScript("window.scrollTo(0, document.querySelector('#section-program').offsetTop);");
        WebElement firstProgram = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".program-option input[type='radio']")));
        js.executeScript("arguments[0].click();", firstProgram);
        
        js.executeScript("window.scrollTo(0, document.querySelector('#section-schedule').offsetTop);");
        getWebDriver().findElement(By.xpath("//select[@id='sessionPreferences0.sessionId']/option[2]")).click();
        getWebDriver().findElement(By.xpath("//select[@id='sessionPreferences0.priority']/option[@value='1']")).click();
        js.executeScript("arguments[0].click();", getWebDriver().findElement(By.cssSelector("label[for='day_0_MONDAY']")));
        
        js.executeScript("window.scrollTo(0, document.querySelector('#section-placement').offsetTop);");
        getWebDriver().findElement(By.id("recordingDriveLink")).sendKeys("https://drive.google.com/file/d/validation-test/view");
        
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
        
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        
        // Get registration ID and submit for review
        WebElement viewDetailButton = getWebDriver().findElement(By.xpath("//a[contains(text(), 'Lihat Detail')]"));
        viewDetailButton.click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        String currentUrl = getWebDriver().getCurrentUrl();
        String registrationId = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);
        
        WebElement submitReviewButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Submit untuk Review')]")));
        js.executeScript("arguments[0].click();", submitReviewButton);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@class, 'bg-green-50')]")));
        
        System.out.println("   ✓ Validation test registration created and submitted");
        return registrationId;
    }
    
    private String createAndApproveRegistration() {
        String registrationId = createStudentRegistrationForValidation();
        
        // Login as admin and approve the registration
        loginAsAdmin();
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/review");
        wait.until(ExpectedConditions.urlContains("/review"));
        
        Select statusSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("newStatus"))));
        statusSelect.selectByValue("APPROVED");
        
        getWebDriver().findElement(By.name("reviewNotes")).sendKeys("Approved for validation test");
        getWebDriver().findElement(By.name("decisionReason")).sendKeys("Test approval");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        
        System.out.println("   ✓ Registration approved for validation test");
        return registrationId;
    }
}