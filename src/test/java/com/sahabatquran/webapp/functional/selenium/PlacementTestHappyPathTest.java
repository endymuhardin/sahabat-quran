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
 * Happy path tests for placement test management workflow.
 * 
 * Test Pattern: [BusinessProcess][HappyPath]Test
 * Run with: ./mvnw test -Dtest="*PlacementTest*HappyPath*"
 */
@DisplayName("Placement Test Management - Happy Path Tests")
class PlacementTestHappyPathTest extends BaseSeleniumTest {
    
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    @BeforeEach
    void setUpTest() {
        wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        js = (JavascriptExecutor) getWebDriver();
    }
    
    @Test
    @DisplayName("Should complete placement test workflow successfully")
    void shouldCompletePlacementTestWorkflow() {
        System.out.println("🚀 Starting Placement Test Happy Path Test...");
        
        // Create and approve a registration first
        String registrationId = createAndApproveRegistration();
        
        // Navigate to placement tests management as admin
        navigateToPlacementTests();
        
        // Find and evaluate the placement test
        searchForPlacementTest("Placement Test Student");
        
        // Evaluate the placement test
        evaluateAndSubmitPlacementTest();
        
        // Verify the placement test status
        verifyPlacementTestCompletion();
        
        System.out.println("✅ Placement Test Happy Path completed successfully!");
    }
    
    @Test
    @DisplayName("Should handle placement test assignment and random verse selection")
    void shouldHandlePlacementTestAssignmentAndRandomVerseSelection() {
        System.out.println("🚀 Starting Placement Test Assignment Test...");
        
        // Create registration without placement test initially
        String registrationId = createBasicRegistration();
        
        // Login as admin and assign placement test
        loginAsAdmin();
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId);
        wait.until(ExpectedConditions.urlContains("/admin/registrations/"));
        
        // Check if placement test is assigned (should be automatic)
        WebElement placementSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'placement-test') or contains(text(), 'Tes Penempatan')]")));
        
        assertThat(placementSection.isDisplayed()).isTrue();
        
        // Verify that Quranic verse is displayed
        WebElement arabicText = getWebDriver().findElement(
                By.xpath("//*[contains(@class, 'arabic') or contains(@style, 'text-align: right')]"));
        assertThat(arabicText.isDisplayed()).isTrue();
        
        System.out.println("✅ Placement Test Assignment Test completed!");
    }
    
    @Test
    @DisplayName("Should allow student to update recording link after initial registration")
    void shouldAllowStudentToUpdateRecordingLink() {
        System.out.println("🚀 Starting Recording Link Update Test...");
        
        // Create registration without recording link
        String registrationId = createRegistrationWithoutRecording();
        
        // Navigate to edit registration
        getWebDriver().get(getBaseUrl() + "/register/" + registrationId + "/edit");
        wait.until(ExpectedConditions.urlContains("/edit"));
        
        // Add recording link
        js.executeScript("window.scrollTo(0, document.querySelector('#section-placement').offsetTop);");
        WebElement recordingField = wait.until(ExpectedConditions.elementToBeClickable(By.id("recordingDriveLink")));
        recordingField.clear();
        recordingField.sendKeys("https://drive.google.com/file/d/updated-recording/view");
        
        // Save changes
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", saveButton);
        
        // Verify update
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        
        WebElement recordingLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href, 'updated-recording')]")));
        assertThat(recordingLink.isDisplayed()).isTrue();
        
        System.out.println("✅ Recording Link Update Test completed!");
    }
    
    private String createAndApproveRegistration() {
        String registrationId = createBasicRegistration();
        
        // Login as admin and approve
        loginAsAdmin();
        getWebDriver().get(getBaseUrl() + "/admin/registrations/" + registrationId + "/review");
        wait.until(ExpectedConditions.urlContains("/review"));
        
        Select statusSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("newStatus"))));
        statusSelect.selectByValue("APPROVED");
        
        getWebDriver().findElement(By.name("reviewNotes")).sendKeys("Approved for placement test");
        getWebDriver().findElement(By.name("decisionReason")).sendKeys("Ready for evaluation");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        System.out.println("   ✓ Registration approved for placement test");
        
        return registrationId;
    }
    
    private String createBasicRegistration() {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill registration form
        getWebDriver().findElement(By.id("fullName")).sendKeys("Placement Test Student");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='MALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011990");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("081555555555");
        getWebDriver().findElement(By.id("email")).sendKeys("placement.test@example.com");
        getWebDriver().findElement(By.id("address")).sendKeys("Placement Test Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("Placement Emergency");
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
        
        js.executeScript("window.scrollTo(0, document.querySelector('#section-placement').offsetTop);");
        getWebDriver().findElement(By.id("recordingDriveLink")).sendKeys("https://drive.google.com/file/d/placement-test/view");
        
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
        
        System.out.println("   ✓ Basic registration created and submitted");
        return registrationId;
    }
    
    private String createRegistrationWithoutRecording() {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill registration form without recording
        getWebDriver().findElement(By.id("fullName")).sendKeys("Recording Update Test");
        getWebDriver().findElement(By.xpath("//select[@id='gender']/option[@value='FEMALE']")).click();
        getWebDriver().findElement(By.id("dateOfBirth")).sendKeys("01011995");
        getWebDriver().findElement(By.id("placeOfBirth")).sendKeys("Jakarta");
        getWebDriver().findElement(By.id("phoneNumber")).sendKeys("081777777777");
        getWebDriver().findElement(By.id("email")).sendKeys("recording.update@example.com");
        getWebDriver().findElement(By.id("address")).sendKeys("Recording Test Address");
        getWebDriver().findElement(By.id("emergencyContactName")).sendKeys("Recording Emergency");
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
        
        // Skip recording link section intentionally
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
        
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        
        WebElement viewDetailButton = getWebDriver().findElement(By.xpath("//a[contains(text(), 'Lihat Detail')]"));
        viewDetailButton.click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        String currentUrl = getWebDriver().getCurrentUrl();
        String registrationId = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);
        
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
    
    private void navigateToPlacementTests() {
        getWebDriver().get(getBaseUrl() + "/admin/registrations/placement-tests");
        wait.until(ExpectedConditions.titleContains("Tes Penempatan"));
        System.out.println("   ✓ Navigated to placement tests page");
    }
    
    private void searchForPlacementTest(String studentName) {
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("fullName")));
        searchInput.sendKeys(studentName);
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), '" + studentName + "')]")));
        System.out.println("   ✓ Found placement test in search results");
    }
    
    private void evaluateAndSubmitPlacementTest() {
        WebElement evaluateButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Evaluasi') or contains(@href, 'placement-test')]")));
        evaluateButton.click();
        
        wait.until(ExpectedConditions.urlContains("/placement-test"));
        
        Select resultSelect = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.name("placementResult"))));
        resultSelect.selectByValue("3");
        
        getWebDriver().findElement(By.name("evaluationNotes")).sendKeys("Good recitation with minor improvements needed");
        getWebDriver().findElement(By.name("evaluationReason")).sendKeys("Based on clear audio recording");
        
        getWebDriver().findElement(By.cssSelector("button[type='submit']")).click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/admin\\/registrations\\/[a-f0-9-]+$"));
        System.out.println("   ✓ Placement test evaluated and submitted");
    }
    
    private void verifyPlacementTestCompletion() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@class, 'bg-green-50') and contains(text(), 'Evaluasi')]")));
        
        WebElement placementStatus = getWebDriver().findElement(
                By.xpath("//div[contains(@class, 'placement-test')]//span[contains(@class, 'status-badge')]"));
        assertThat(placementStatus.getText()).containsIgnoringCase("dinilai");
        
        System.out.println("   ✓ Placement test completion verified");
    }
}