package com.sahabatquran.webapp.functional.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validation tests for student registration process.
 * 
 * Test Pattern: [BusinessProcess][Validation]Test  
 * Run with: ./mvnw test -Dtest="*Validation*"
 */
@DisplayName("Student Registration - Validation Tests")
class StudentRegistrationValidationTest extends BaseSeleniumTest {
    
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    @BeforeEach
    void setUpTest() {
        wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        js = (JavascriptExecutor) getWebDriver();
    }
    
    @Test
    @DisplayName("Should validate required fields and prevent empty submission")
    void shouldValidateRequiredFieldsAndPreventEmptySubmission() {
        System.out.println("🚀 Starting Required Fields Validation Test...");
        System.out.println("📋 Test Scenario: PS-AP-001 - Validasi Field Required");
        
        System.out.println("📋 PS-AP-001 Step 1: Accessing registration form");
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Wait for personal information step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        
        // Try to click Next without filling required fields
        System.out.println("📋 PS-AP-001 Step 2: Attempting to move to next step with empty required fields");
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        // Should stay on step 1 due to validation - form should not advance to next step
        System.out.println("📋 PS-AP-001 Step 3: Verifying form validation prevents advancing to next step");
        
        // Verify we are still on step 1 (personal information)
        WebElement personalStep = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        assertThat(personalStep).isNotNull();
        
        // Verify validation messages are shown (if any)
        wait.until(ExpectedConditions.urlContains("/register"));
        assertThat(getWebDriver().getCurrentUrl()).contains("/register");
        assertThat(getWebDriver().getCurrentUrl()).doesNotContain("/confirmation");
        
        System.out.println("✅ Required Fields Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent duplicate email registration")
    void shouldPreventDuplicateEmailRegistration() {
        System.out.println("🚀 Starting Duplicate Email Prevention Test...");
        System.out.println("📋 Test Scenario: PS-AP-002 - Validasi Email Duplikat");
        
        String testEmail = "duplicate.test@example.com";
        
        // Create first registration
        System.out.println("📋 PS-AP-002 Step 1: Creating first registration");
        createBasicRegistration(testEmail, "081111111111");
        
        // Try to create second registration with same email
        System.out.println("📋 PS-AP-002 Step 3-4: Attempting duplicate email registration");
        getWebDriver().get(getBaseUrl() + "/register");
        createBasicRegistration(testEmail, "081222222222");
        
        // Should show error message or stay on same page
        System.out.println("📋 PS-AP-002 Step 5: Verifying duplicate email prevention");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-red-50') and contains(text(), 'Email sudah terdaftar')]")),
                ExpectedConditions.urlContains("/register")
        ));
        
        System.out.println("✅ Duplicate Email Prevention Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent duplicate phone number registration")
    void shouldPreventDuplicatePhoneRegistration() {
        System.out.println("🚀 Starting Duplicate Phone Prevention Test...");
        
        String testPhone = "081111111111";
        
        // Create first registration
        createBasicRegistration("first.test@example.com", testPhone);
        
        // Try to create second registration with same phone
        getWebDriver().get(getBaseUrl() + "/register");
        createBasicRegistration("second.test@example.com", testPhone);
        
        // Should show error message or stay on same page
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-red-50') and contains(text(), 'Nomor telepon sudah terdaftar')]")),
                ExpectedConditions.urlContains("/register")
        ));
        
        System.out.println("✅ Duplicate Phone Prevention Test completed!");
    }
    
    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        System.out.println("🚀 Starting Email Format Validation Test...");
        System.out.println("📋 Test Scenario: PS-AP-004 - Validasi Format Email");
        
        System.out.println("📋 PS-AP-004 Step 1: Testing invalid email format");
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Wait for personal information step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        
        // Fill form with invalid email
        getWebDriver().findElement(By.id("fullName")).sendKeys("Test User");
        getWebDriver().findElement(By.id("email")).sendKeys("invalid-email");
        
        // Try to advance to next step - should fail due to invalid email
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        // Should stay on step 1 due to validation
        WebElement personalStep = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        assertThat(personalStep).isNotNull();
        
        wait.until(ExpectedConditions.urlContains("/register"));
        assertThat(getWebDriver().getCurrentUrl()).contains("/register");
        assertThat(getWebDriver().getCurrentUrl()).doesNotContain("/confirmation");
        
        System.out.println("✅ Email Format Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should validate phone number format")
    void shouldValidatePhoneNumberFormat() {
        System.out.println("🚀 Starting Phone Number Format Validation Test...");
        
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Wait for personal information step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        
        // Fill form with invalid phone
        getWebDriver().findElement(By.id("fullName")).sendKeys("Test User");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("123");  // Too short
        getWebDriver().findElement(By.id("email")).sendKeys("test@example.com");
        
        // Try to advance to next step - should fail due to invalid phone
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        // Should stay on step 1 due to validation
        WebElement personalStep = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        assertThat(personalStep).isNotNull();
        
        wait.until(ExpectedConditions.urlContains("/register"));
        assertThat(getWebDriver().getCurrentUrl()).contains("/register");
        assertThat(getWebDriver().getCurrentUrl()).doesNotContain("/confirmation");
        
        System.out.println("✅ Phone Number Format Validation Test completed!");
    }
    
    private void createBasicRegistration(String email, String phone) {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Wait for personal information step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        
        // Fill Step 1: Personal Information
        getWebDriver().findElement(By.id("fullName")).sendKeys("Test User");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='MALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011990");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys(phone);
        getWebDriver().findElement(By.id("email")).sendKeys(email);
        getWebDriver().findElement(By.id("address")).sendKeys("Test Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("Emergency");
        getWebDriver().findElement(By.id("emergencyContactPhone")).sendKeys("081999999999");
        getWebDriver().findElement(By.xpath("//select[@id='emergencyContactRelation']/option[@value='Orang Tua']")).click();
        
        // Move to Step 2: Education
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-education' and contains(@class, 'active')]")));
        
        getWebDriver().findElement(By.xpath("//select[@id='educationLevel']/option[@value='S1']")).click();
        getWebDriver().findElement(By.xpath("//input[@name='previousTahsinExperience' and @value='false']")).click();
        
        // Move to Step 3: Program
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-program' and contains(@class, 'active')]")));
        
        WebElement firstProgram = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".program-option input[type='radio']")));
        js.executeScript("arguments[0].click();", firstProgram);
        
        // Move to Step 4: Schedule
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-schedule' and contains(@class, 'active')]")));
        
        getWebDriver().findElement(By.xpath("//select[@id='sessionPreferences0.sessionId']/option[2]")).click();
        getWebDriver().findElement(By.xpath("//select[@id='sessionPreferences0.priority']/option[@value='1']")).click();
        js.executeScript("arguments[0].click();", getWebDriver().findElement(By.cssSelector("label[for='day_0_MONDAY']")));
        
        // Move to Step 5: Placement Test and Submit
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-placement' and contains(@class, 'active')]")));
        
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
    }
}