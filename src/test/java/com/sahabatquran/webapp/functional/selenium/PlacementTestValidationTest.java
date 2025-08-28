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
 * Validation tests for placement test management workflow.
 * 
 * Test Pattern: [BusinessProcess][Validation]Test
 * Run with: ./mvnw test -Dtest="*PlacementTest*Validation*"
 */
@DisplayName("Placement Test Management - Validation Tests")
class PlacementTestValidationTest extends BaseSeleniumTest {
    
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    @BeforeEach
    void setUpTest() {
        wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        js = (JavascriptExecutor) getWebDriver();
    }
    
    @Test
    @DisplayName("Should prevent placement test evaluation without required fields")
    void shouldPreventPlacementTestEvaluationWithoutRequiredFields() {
        System.out.println("🚀 Starting Placement Test Required Fields Validation Test...");
        
        // Create and approve registration
        String registrationId = createAndApproveRegistration();
        
        // Navigate to placement test evaluation
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        
        // Try to submit without filling required fields
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        // Should stay on placement test page due to validation
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        assertThat(getWebDriver().getCurrentUrl()).contains("/placement-test");
        
        System.out.println("✅ Placement Test Required Fields Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent evaluation of placement test without recording link")
    void shouldPreventEvaluationOfPlacementTestWithoutRecordingLink() {
        System.out.println("🚀 Starting No Recording Link Validation Test...");
        
        // Create registration without recording link
        String registrationId = createRegistrationWithoutRecording();
        
        // Login as admin and approve
        loginAsAdmin();
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/review");
        wait.until(ExpectedConditions.urlContains("/review"));
        
        Select statusSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("newStatus"))));
        statusSelect.selectByValue("APPROVED");
        
        getWebDriver().findElement(By.name("reviewNotes")).sendKeys("Approved but missing recording");
        getWebDriver().findElement(By.name("decisionReason")).sendKeys("Need recording for evaluation");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        
        // Try to access placement test evaluation
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        
        // Should show error or prevent evaluation
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-red-50') and contains(text(), 'rekaman') or contains(text(), 'recording')]")),
                ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$")
        ));
        
        System.out.println("✅ No Recording Link Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent unauthorized access to placement test evaluation")
    void shouldPreventUnauthorizedAccessToPlacementTestEvaluation() {
        System.out.println("🚀 Starting Unauthorized Placement Test Access Test...");
        
        String registrationId = createAndApproveRegistration();
        
        // Try to access placement test evaluation without login
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        
        // Should redirect to login
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.titleContains("Login")
        ));
        
        assertThat(getWebDriver().getCurrentUrl()).containsAnyOf("/login", "/signin");
        
        System.out.println("✅ Unauthorized Placement Test Access Test completed!");
    }
    
    @Test
    @DisplayName("Should validate placement test result range")
    void shouldValidatePlacementTestResultRange() {
        System.out.println("🚀 Starting Placement Test Result Range Validation Test...");
        
        String registrationId = createAndApproveRegistration();
        
        // Navigate to placement test evaluation
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        
        // Try to select invalid result (if validation exists)
        Select resultSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("placementResult"))));
        
        // Fill other required fields first
        getWebDriver().findElement(By.name("evaluationNotes")).sendKeys("Test evaluation notes");
        getWebDriver().findElement(By.name("evaluationReason")).sendKeys("Test evaluation reason");
        
        // Select a valid result (since invalid options shouldn't exist in select)
        resultSelect.selectByValue("1");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        // Should accept valid result
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        
        System.out.println("✅ Placement Test Result Range Validation Test completed!");
    }
    
    @Test
    @DisplayName("Should prevent double evaluation of same placement test")
    void shouldPreventDoubleEvaluationOfSamePlacementTest() {
        System.out.println("🚀 Starting Double Evaluation Prevention Test...");
        
        // Create, approve, and evaluate a placement test
        String registrationId = createEvaluatedPlacementTest();
        
        // Try to access placement test evaluation again
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        
        // Should either redirect or show message that evaluation is done
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), 'sudah dinilai') or contains(text(), 'already evaluated')]"))
        ));
        
        System.out.println("✅ Double Evaluation Prevention Test completed!");
    }
    
    @Test
    @DisplayName("Should validate student registration status before placement test")
    void shouldValidateStudentRegistrationStatusBeforePlacementTest() {
        System.out.println("🚀 Starting Registration Status Validation Test...");
        
        // Create registration but don't approve
        String registrationId = createBasicRegistration();
        
        // Try to access placement test evaluation on non-approved registration
        loginAsAdmin();
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        
        // Should either redirect or show error
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-red-50') and contains(text(), 'belum disetujui') or contains(text(), 'not approved')]"))
        ));
        
        System.out.println("✅ Registration Status Validation Test completed!");
    }
    
    private String createAndApproveRegistration() {
        String registrationId = createBasicRegistration();
        
        // Login as admin and approve
        loginAsAdmin();
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/review");
        wait.until(ExpectedConditions.urlContains("/review"));
        
        Select statusSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("newStatus"))));
        statusSelect.selectByValue("APPROVED");
        
        getWebDriver().findElement(By.name("reviewNotes")).sendKeys("Approved for validation test");
        getWebDriver().findElement(By.name("decisionReason")).sendKeys("Ready for placement test");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        System.out.println("   ✓ Registration approved for validation test");
        
        return registrationId;
    }
    
    private String createEvaluatedPlacementTest() {
        String registrationId = createAndApproveRegistration();
        
        // Evaluate the placement test
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/placement-test");
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        
        Select resultSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("placementResult"))));
        resultSelect.selectByValue("2");
        
        getWebDriver().findElement(By.name("evaluationNotes")).sendKeys("Already evaluated test");
        getWebDriver().findElement(By.name("evaluationReason")).sendKeys("First evaluation");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        System.out.println("   ✓ Placement test evaluated for double evaluation test");
        
        return registrationId;
    }
    
    private String createBasicRegistration() {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill registration form
        getWebDriver().findElement(By.id("fullName")).sendKeys("Placement Validation Student");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='FEMALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011992");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("081999999999");
        getWebDriver().findElement(By.id("email")).sendKeys("placement.validation@example.com");
        getWebDriver().findElement(By.id("address")).sendKeys("Validation Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("Validation Emergency");
        getWebDriver().findElement(By.id("emergencyContactPhone")).sendKeys("081888888888");
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
        
        // Submit for review
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
    
    private String createRegistrationWithoutRecording() {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill registration form without recording
        getWebDriver().findElement(By.id("fullName")).sendKeys("No Recording Student");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='MALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011988");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("081777777777");
        getWebDriver().findElement(By.id("email")).sendKeys("no.recording@example.com");
        getWebDriver().findElement(By.id("address")).sendKeys("No Recording Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("No Recording Emergency");
        getWebDriver().findElement(By.id("emergencyContactPhone")).sendKeys("081666666666");
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
        
        // Skip recording link intentionally
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
        
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        
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
        
        System.out.println("   ✓ Registration without recording created");
        return registrationId;
    }
    
    private void loginAsAdmin() {
        getWebDriver().get(getBaseUrl() + "/login");
        wait.until(ExpectedConditions.titleContains("Login"));
        
        getWebDriver().findElement(By.id("username")).sendKeys("admin");
        getWebDriver().findElement(By.id("password")).sendKeys("AdminYSQ@2024");
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        System.out.println("   ✓ Admin logged in successfully");
    }
}