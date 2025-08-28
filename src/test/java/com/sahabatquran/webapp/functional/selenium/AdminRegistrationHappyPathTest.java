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
 * Happy path tests for admin registration management.
 * 
 * Test Pattern: [BusinessProcess][HappyPath]Test
 * Run with: ./mvnw test -Dtest="*AdminRegistration*HappyPath*"
 */
@DisplayName("Admin Registration Management - Happy Path Tests")
class AdminRegistrationHappyPathTest extends BaseSeleniumTest {
    
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    @BeforeEach
    void setUpTest() {
        wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        js = (JavascriptExecutor) getWebDriver();
    }
    
    @Test
    @DisplayName("Should complete admin registration management workflow successfully")
    void shouldCompleteAdminRegistrationManagementWorkflow() {
        System.out.println("🚀 Starting Admin Registration Management Happy Path Test...");
        System.out.println("📋 Test Scenario: AR-HP-001 - Workflow Review dan Approval Registrasi");
        
        // First create a student registration to manage
        System.out.println("📋 AR-HP-001 Step 1: Creating student registration for admin review");
        String registrationId = createStudentRegistrationForAdmin();
        
        // Login as admin
        System.out.println("📋 AR-HP-001 Step 2-3: Admin login and authentication");
        loginAsAdmin();
        
        // Navigate to registrations management
        System.out.println("📋 AR-HP-001 Step 4: Accessing registrations management page");
        navigateToAdminRegistrations();
        
        // Search and find the registration
        System.out.println("📋 AR-HP-001 Step 5: Searching for registration to review");
        searchForRegistration("Admin Test Student");
        
        // Review and approve registration
        System.out.println("📋 AR-HP-001 Step 6-10: Reviewing and approving registration");
        reviewAndApproveRegistration();
        
        // Evaluate placement test
        System.out.println("📋 AR-HP-001 Step 11-14: Evaluating placement test");
        evaluatePlacementTest();
        
        // Verify final status
        System.out.println("📋 AR-HP-001 Step 15: Verifying final workflow completion");
        verifyFinalAdminWorkflowStatus();
        
        System.out.println("✅ Admin Registration Management Happy Path completed successfully!");
    }
    
    private String createStudentRegistrationForAdmin() {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill form quickly
        getWebDriver().findElement(By.id("fullName")).sendKeys("Admin Test Student");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='MALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011990");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("081111111111");
        getWebDriver().findElement(By.id("email")).sendKeys("admin.test.student@example.com");
        getWebDriver().findElement(By.id("address")).sendKeys("Admin Test Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("Admin Emergency");
        getWebDriver().findElement(By.id("emergencyContactPhone")).sendKeys("081222222222");
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
        getWebDriver().findElement(By.id("recordingDriveLink")).sendKeys("https://drive.google.com/file/d/admin-test/view");
        
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
        
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        
        // Navigate to detail and submit for review
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
        
        System.out.println("   ✓ Student registration created and submitted");
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
    
    private void navigateToAdminRegistrations() {
        getWebDriver().get(getBaseUrl() + "/admin/registrations");
        wait.until(ExpectedConditions.titleContains("Kelola"));
        System.out.println("   ✓ Navigated to admin registrations page");
    }
    
    private void searchForRegistration(String studentName) {
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("fullName")));
        searchInput.sendKeys(studentName);
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), '" + studentName + "')]")));
        System.out.println("   ✓ Found registration in search results");
    }
    
    private void reviewAndApproveRegistration() {
        WebElement detailLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/admin/registrations/')]")));
        detailLink.click();
        
        wait.until(ExpectedConditions.urlContains("/admin/registrations/"));
        
        WebElement reviewButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Review')]")));
        reviewButton.click();
        
        wait.until(ExpectedConditions.urlContains("/review"));
        
        Select statusSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("newStatus"))));
        statusSelect.selectByValue("APPROVED");
        
        getWebDriver().findElement(By.name("reviewNotes")).sendKeys("Registration approved after review");
        getWebDriver().findElement(By.name("decisionReason")).sendKeys("Meets all requirements");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'disetujui')]")));
        
        System.out.println("   ✓ Registration reviewed and approved");
    }
    
    private void evaluatePlacementTest() {
        getWebDriver().get(getBaseUrl() + "/admin/registrations/placement-tests");
        wait.until(ExpectedConditions.titleContains("Tes Penempatan"));
        
        WebElement evaluateButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Evaluasi') or contains(@href, 'placement-test')]")));
        evaluateButton.click();
        
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        
        Select resultSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("placementResult"))));
        resultSelect.selectByValue("4");
        
        getWebDriver().findElement(By.name("evaluationNotes")).sendKeys("Good recitation quality");
        getWebDriver().findElement(By.name("evaluationReason")).sendKeys("Evaluated based on recording");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@class, 'bg-green-50') and contains(text(), 'Evaluasi')]")));
        
        System.out.println("   ✓ Placement test evaluated successfully");
    }
    
    private void verifyFinalAdminWorkflowStatus() {
        WebElement registrationStatus = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".status-badge")));
        assertThat(registrationStatus.getText()).containsIgnoringCase("disetujui");
        
        WebElement placementStatus = getWebDriver().findElement(
                By.xpath("//div[contains(@class, 'placement-test')]//span[contains(@class, 'status-badge')]"));
        assertThat(placementStatus.getText()).containsIgnoringCase("dinilai");
        
        System.out.println("   ✓ Final status verified: Registration APPROVED, Placement Test EVALUATED");
    }
}