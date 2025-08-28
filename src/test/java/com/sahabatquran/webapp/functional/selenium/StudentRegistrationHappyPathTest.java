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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Happy path functional tests for student registration process.
 * 
 * Test Pattern: [BusinessProcess][HappyPath]Test
 * Run with: ./mvnw test -Dtest="*HappyPath*" -Dselenium.debug.vnc.enabled=true
 */
@DisplayName("Student Registration - Happy Path Tests")
class StudentRegistrationHappyPathTest extends BaseSeleniumTest {
    
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    @BeforeEach
    void setUpTest() {
        wait = new WebDriverWait(getWebDriver(), getExplicitTimeout());
        js = (JavascriptExecutor) getWebDriver();
    }
    
    @Test
    @DisplayName("Should complete full student registration happy path successfully")
    void shouldCompleteStudentRegistrationHappyPath() {
        System.out.println("🚀 Starting Student Registration Happy Path Test...");
        System.out.println("📋 Test Scenario: PS-HP-001 - Pendaftaran Siswa Baru Lengkap");
        
        // Navigate to registration page from home
        System.out.println("📋 PS-HP-001 Step 1-2: Accessing registration form");
        navigateToRegistrationFromHome();
        
        // Fill comprehensive registration form
        System.out.println("📋 PS-HP-001 Step 3-4: Filling personal information section");
        fillPersonalInformationSection();
        
        System.out.println("📋 PS-HP-001 Step 5: Filling educational information section");
        fillEducationalInformationSection();
        
        System.out.println("📋 PS-HP-001 Step 6: Filling program selection section");
        fillProgramSelectionSection();
        
        System.out.println("📋 PS-HP-001 Step 7-8: Filling session preferences section");
        fillSessionPreferencesSection();
        
        System.out.println("📋 PS-HP-001 Step 9-10: Filling placement test section");
        fillPlacementTestSection();
        
        // Submit form and verify confirmation
        System.out.println("📋 PS-HP-001 Step 11: Submitting registration form");
        submitRegistrationForm();
        
        System.out.println("📋 PS-HP-001 Step 12: Verifying confirmation page");
        verifyConfirmationPage();
        
        // Navigate to detail page and verify data
        System.out.println("📋 PS-HP-001 Step 13: Accessing registration detail page");
        navigateToRegistrationDetail();
        verifyRegistrationDetails();
        
        // Test editing functionality (DRAFT status)
        System.out.println("📋 PS-HP-002: Testing edit functionality in DRAFT status");
        testRegistrationEdit();
        
        // Submit for review and verify final status
        System.out.println("📋 PS-HP-001 Step 14: Submitting registration for review");
        submitForReview();
        verifyFinalDraftToSubmittedStatus();
        
        System.out.println("✅ Student Registration Happy Path completed successfully!");
    }
    
    @Test
    @DisplayName("Should clear form correctly when clear button is clicked")
    void shouldClearFormCorrectly() {
        System.out.println("🧹 Starting Clear Form Test...");
        System.out.println("📋 Test Scenario: Clear form mid-way through registration");
        
        // Navigate to registration page
        navigateToRegistrationFromHome();
        
        // Fill some data in the first step
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("fullName")));
        fullNameInput.sendKeys("Test User for Clear");
        
        WebElement emailInput = getWebDriver().findElement(By.id("email"));
        emailInput.sendKeys("testclear@example.com");
        
        // Move to next step to add more data
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        // Wait for education step and add some data there too
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-education' and contains(@class, 'active')]")));
        
        Select educationLevelSelect = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id("educationLevel"))));
        educationLevelSelect.selectByValue("S1");
        
        // Click clear form button
        WebElement clearBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("clearFormBtn")));
        clearBtn.click();
        
        // Accept the confirmation dialog
        getWebDriver().switchTo().alert().accept();
        
        // Verify we're back to step 1 and form is cleared
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        
        // Wait for form to be cleared and verify fields are empty
        wait.until(ExpectedConditions.attributeToBe(By.id("fullName"), "value", ""));
        wait.until(ExpectedConditions.attributeToBe(By.id("email"), "value", ""));
        
        // Verify education field is also cleared (navigate to education step to check)
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-education' and contains(@class, 'active')]")));
        
        Select clearedEducationSelect = new Select(getWebDriver().findElement(By.id("educationLevel")));
        assertThat(clearedEducationSelect.getFirstSelectedOption().getAttribute("value")).isEmpty();
        
        // Go back to step 1 to verify success message
        WebElement prevButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("prevBtn")));
        js.executeScript("arguments[0].click();", prevButton);
        
        // Verify success message appears
        WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class, 'bg-green-50') and contains(text(), 'Form berhasil dibersihkan')]")));
        assertThat(successMessage).isNotNull();
        
        System.out.println("   ✅ Form cleared successfully");
        System.out.println("   ✅ Returned to step 1");
        System.out.println("   ✅ Success message displayed");
        System.out.println("   ✅ All steps data cleared");
    }
    
    @Test
    @DisplayName("Should handle multiple session preferences correctly")
    void shouldHandleMultipleSessionPreferencesCorrectly() {
        System.out.println("🚀 Starting Multiple Session Preferences Test...");
        
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill minimal required info to focus on session preferences
        fillMinimalRequiredInformation();
        
        // Navigate to session preferences section
        js.executeScript("window.scrollTo(0, document.querySelector('#section-schedule').offsetTop - 100);");
        
        // Test session preferences functionality
        testSessionPreferenceManagement();
        testSessionPreferencePriorityHandling();
        testDaySelectionFunctionality();
        
        // Submit and verify preferences are saved correctly
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
        
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        
        // Navigate to detail and verify session preferences
        WebElement viewDetailButton = getWebDriver().findElement(
                By.xpath("//a[contains(text(), 'Lihat Detail Pendaftaran')]"));
        viewDetailButton.click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        
        // Verify all 3 session preferences are displayed with correct priorities
        List<WebElement> sessionPrefs = getWebDriver().findElements(
                By.xpath("//div[contains(., 'Prioritas')]"));
        assertThat(sessionPrefs).hasSizeGreaterThanOrEqualTo(3);
        
        // Verify priority ordering
        WebElement priority1 = getWebDriver().findElement(By.xpath("//span[contains(text(), 'Prioritas 1')]"));
        WebElement priority2 = getWebDriver().findElement(By.xpath("//span[contains(text(), 'Prioritas 2')]"));
        WebElement priority3 = getWebDriver().findElement(By.xpath("//span[contains(text(), 'Prioritas 3')]"));
        
        assertThat(priority1).isNotNull();
        assertThat(priority2).isNotNull();
        assertThat(priority3).isNotNull();
        
        System.out.println("✅ Multiple Session Preferences handled correctly!");
    }
    
    @Test
    @DisplayName("Should preserve form data during navigation and editing")
    void shouldPreserveFormDataDuringNavigationAndEditing() {
        System.out.println("🚀 Starting Form Data Preservation Test...");
        
        // Create a registration with specific data
        String studentEmail = "data.preservation.test@example.com";
        String registrationId = createRegistrationWithSpecificData(studentEmail);
        
        // Navigate away and back to verify data persistence
        getWebDriver().get(getBaseUrl());
        getWebDriver().get(getBaseUrl() + "/register/" + registrationId);
        
        // Verify all data is still present
        verifySpecificDataPresent(studentEmail);
        
        // Test edit functionality preserves data
        WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Edit Pendaftaran')]")));
        editButton.click();
        
        wait.until(ExpectedConditions.urlContains("/edit"));
        
        // Verify form is pre-populated with existing data
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));
        assertThat(emailInput.getAttribute("value")).isEqualTo(studentEmail);
        
        WebElement fullNameInput = getWebDriver().findElement(By.id("fullName"));
        assertThat(fullNameInput.getAttribute("value")).isEqualTo("Data Preservation Test Student");
        
        // Make a small change and save
        WebElement registrationReasonInput = getWebDriver().findElement(By.id("registrationReason"));
        String originalReason = registrationReasonInput.getAttribute("value");
        registrationReasonInput.clear();
        registrationReasonInput.sendKeys(originalReason + " (EDITED)");
        
        WebElement updateButton = getWebDriver().findElement(By.cssSelector("button[type='submit']"));
        js.executeScript("arguments[0].scrollIntoView(true);", updateButton);
        js.executeScript("arguments[0].click();", updateButton);
        
        // Verify update success and data preservation
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-green-50') and contains(text(), 'berhasil')]")));
        assertThat(successMessage.getText()).containsIgnoringCase("berhasil diperbarui");
        
        // Verify the edit is reflected
        WebElement updatedReason = getWebDriver().findElement(
                By.xpath("//td[contains(text(), '(EDITED)')]"));
        assertThat(updatedReason).isNotNull();
        
        System.out.println("✅ Form Data Preservation during navigation and editing verified!");
    }
    
    // Helper methods
    
    private void navigateToRegistrationFromHome() {
        getWebDriver().get(getBaseUrl());
        
        WebElement registerLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/register')]")));
        registerLink.click();
        
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        assertThat(getWebDriver().getTitle()).contains("Pendaftaran Siswa Baru");
        
        // Verify logo and branding
        WebElement logoImg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img[alt*='Yayasan Sahabat Quran']")));
        assertThat(logoImg).isNotNull();
        
        System.out.println("   ✓ Navigated to registration form from home page");
    }
    
    private void fillPersonalInformationSection() {
        // Wait for personal information step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-personal' and contains(@class, 'active')]")));
        
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("fullName")));
        fullNameInput.clear();
        fullNameInput.sendKeys("Ahmad Zaki Mubarak");
        
        Select genderSelect = new Select(getWebDriver().findElement(By.id("gender")));
        genderSelect.selectByValue("MALE");
        
        WebElement dateOfBirthInput = getWebDriver().findElement(By.id("dateOfBirth"));
        dateOfBirthInput.sendKeys("15031995");
        
        WebElement placeOfBirthInput = getWebDriver().findElement(By.id("placeOfBirth"));
        placeOfBirthInput.sendKeys("Jakarta");
        
        WebElement phoneNumberInput = getWebDriver().findElement(By.id("phoneNumber"));
        phoneNumberInput.sendKeys("081234567890");
        
        WebElement emailInput = getWebDriver().findElement(By.id("email"));
        emailInput.sendKeys("ahmad.zaki.happy@example.com");
        
        WebElement addressInput = getWebDriver().findElement(By.id("address"));
        addressInput.sendKeys("Jl. Raya Bogor No. 123, Jakarta Timur, DKI Jakarta");
        
        WebElement emergencyNameInput = getWebDriver().findElement(By.id("emergencyContactName"));
        emergencyNameInput.sendKeys("Fatimah Zaki");
        
        WebElement emergencyPhoneInput = getWebDriver().findElement(By.id("emergencyContactPhone"));
        emergencyPhoneInput.sendKeys("081234567891");
        
        Select emergencyRelationSelect = new Select(getWebDriver().findElement(By.id("emergencyContactRelation")));
        emergencyRelationSelect.selectByValue("Suami/Istri");
        
        // Click Next button to proceed to education section
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        System.out.println("   ✓ Personal information section filled and moved to next step");
    }
    
    private void fillEducationalInformationSection() {
        // Wait for educational information step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-education' and contains(@class, 'active')]")));
        
        Select educationLevelSelect = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id("educationLevel"))));
        educationLevelSelect.selectByValue("S1");
        
        WebElement schoolNameInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("schoolName")));
        schoolNameInput.sendKeys("Universitas Indonesia");
        
        // Scroll specifically to the quranReadingExperience element and center it
        WebElement quranExperienceInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("quranReadingExperience")));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", quranExperienceInput);
        
        // Wait for it to be clickable after scrolling
        wait.until(ExpectedConditions.elementToBeClickable(quranExperienceInput));
        quranExperienceInput.sendKeys("Sudah bisa membaca Al-Quran sejak kecil, namun merasa perlu perbaikan makhorijul huruf dan tajwid.");
        
        WebElement tahsinYesRadio = getWebDriver().findElement(
                By.xpath("//input[@name='previousTahsinExperience' and @value='true']"));
        js.executeScript("arguments[0].click();", tahsinYesRadio);
        
        // First check if element exists in DOM
        WebElement tahsinDetailsInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("previousTahsinDetails")));
        
        // Manually trigger the JavaScript show functionality
        js.executeScript("document.getElementById('tahsinDetailsContainer').style.display = 'block';");
        
        // Now wait for visibility
        wait.until(ExpectedConditions.visibilityOf(tahsinDetailsInput));
        tahsinDetailsInput.sendKeys("Pernah belajar tahsin selama 6 bulan di masjid Al-Hidayah, sudah mengenal dasar-dasar tajwid.");
        
        // Click Next button to proceed to program section
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        System.out.println("   ✓ Educational information section filled and moved to next step");
    }
    
    private void fillProgramSelectionSection() {
        // Wait for program selection step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-program' and contains(@class, 'active')]")));
        
        List<WebElement> programOptions = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".program-option input[type='radio']")));
        
        WebElement firstProgram = programOptions.get(0);
        js.executeScript("arguments[0].click();", firstProgram);
        
        WebElement registrationReasonInput = getWebDriver().findElement(By.id("registrationReason"));
        registrationReasonInput.sendKeys("Ingin memperbaiki bacaan Al-Quran dan mempersiapkan diri untuk program tahfidz selanjutnya.");
        
        WebElement learningGoalsInput = getWebDriver().findElement(By.id("learningGoals"));
        learningGoalsInput.sendKeys("Menguasai tajwid dengan baik, dapat membaca Al-Quran dengan fasih dan benar, serta memiliki fondasi yang kuat untuk melanjutkan ke program tahfidz.");
        
        // Click Next button to proceed to schedule section
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        System.out.println("   ✓ Program selection section filled and moved to next step");
    }
    
    private void fillSessionPreferencesSection() {
        // Wait for schedule preferences step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-schedule' and contains(@class, 'active')]")));
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("session-preferences-container")));
        
        // Fill first preference
        fillSessionPreference(0, 1, new String[]{"MONDAY", "WEDNESDAY"});
        
        // Add and fill second preference
        WebElement addPreferenceButton = getWebDriver().findElement(By.id("add-preference"));
        js.executeScript("arguments[0].click();", addPreferenceButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("session-pref-1")));
        fillSessionPreference(1, 2, new String[]{"TUESDAY", "THURSDAY"});
        
        // Add and fill third preference
        js.executeScript("arguments[0].click();", addPreferenceButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("session-pref-2")));
        fillSessionPreference(2, 3, new String[]{"SATURDAY"});
        
        // Click Next button to proceed to placement test section
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        System.out.println("   ✓ Session preferences section filled and moved to next step");
    }
    
    private void fillSessionPreference(int index, int priority, String[] days) {
        String sessionSelectId = String.format("sessionPreferences%d.sessionId", index);
        Select sessionSelect = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id(sessionSelectId))));
        
        if (sessionSelect.getOptions().size() > priority) {
            sessionSelect.selectByIndex(priority);
        }
        
        String prioritySelectId = String.format("sessionPreferences%d.priority", index);
        Select prioritySelect = new Select(getWebDriver().findElement(By.id(prioritySelectId)));
        prioritySelect.selectByValue(String.valueOf(priority));
        
        for (String day : days) {
            String dayCheckboxId = String.format("day_%d_%s", index, day);
            WebElement dayCheckbox = getWebDriver().findElement(By.id(dayCheckboxId));
            if (!dayCheckbox.isSelected()) {
                WebElement dayLabel = getWebDriver().findElement(By.cssSelector("label[for='" + dayCheckboxId + "']"));
                js.executeScript("arguments[0].click();", dayLabel);
            }
        }
    }
    
    private void fillPlacementTestSection() {
        // Wait for placement test step to be active
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-placement' and contains(@class, 'active')]")));
        
        WebElement recordingLinkInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("recordingDriveLink")));
        recordingLinkInput.sendKeys("https://drive.google.com/file/d/1example-happy-path-recording/view?usp=sharing");
        
        System.out.println("   ✓ Placement test section filled");
    }
    
    private void submitRegistrationForm() {
        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        
        js.executeScript("arguments[0].scrollIntoView(true);", submitButton);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        js.executeScript("arguments[0].click();", submitButton);
        System.out.println("   ✓ Registration form submitted");
    }
    
    private void verifyConfirmationPage() {
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        
        WebElement successIcon = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fa-check-circle")));
        assertThat(successIcon).isNotNull();
        
        WebElement pageTitle = getWebDriver().findElement(By.xpath("//h1[contains(text(), 'Pendaftaran Berhasil')]"));
        assertThat(pageTitle.getText()).contains("Pendaftaran Berhasil");
        
        WebElement nextSteps = getWebDriver().findElement(By.xpath("//h3[contains(text(), 'Langkah Selanjutnya')]"));
        assertThat(nextSteps).isNotNull();
        
        System.out.println("   ✓ Confirmation page verified");
    }
    
    private void navigateToRegistrationDetail() {
        WebElement viewDetailButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Lihat Detail Pendaftaran')]")));
        viewDetailButton.click();
        
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        System.out.println("   ✓ Navigated to registration detail page");
    }
    
    private void verifyRegistrationDetails() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(), 'Detail Pendaftaran')]")));
        
        WebElement fullNameElement = getWebDriver().findElement(
                By.xpath("//td[text()='Ahmad Zaki Mubarak']"));
        assertThat(fullNameElement.getText()).isEqualTo("Ahmad Zaki Mubarak");
        
        WebElement emailElement = getWebDriver().findElement(
                By.xpath("//td[text()='ahmad.zaki.happy@example.com']"));
        assertThat(emailElement.getText()).isEqualTo("ahmad.zaki.happy@example.com");
        
        WebElement statusElement = getWebDriver().findElement(By.cssSelector(".status-badge"));
        assertThat(statusElement.getText()).containsIgnoringCase("draft");
        
        System.out.println("   ✓ Registration details verified");
    }
    
    private void testRegistrationEdit() {
        WebElement editButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Edit Pendaftaran')]")));
        editButton.click();
        
        wait.until(ExpectedConditions.urlContains("/edit"));
        wait.until(ExpectedConditions.titleContains("Edit Pendaftaran"));
        
        WebElement registrationReasonInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("registrationReason")));
        
        js.executeScript("arguments[0].scrollIntoView(true);", registrationReasonInput);
        registrationReasonInput.clear();
        registrationReasonInput.sendKeys("Ingin memperbaiki bacaan Al-Quran dan mempersiapkan diri untuk program tahfidz selanjutnya. (UPDATED)");
        
        WebElement updateButton = getWebDriver().findElement(By.cssSelector("button[type='submit']"));
        js.executeScript("arguments[0].scrollIntoView(true);", updateButton);
        js.executeScript("arguments[0].click();", updateButton);
        
        wait.until(ExpectedConditions.urlMatches(".*\\/register\\/[a-f0-9-]+$"));
        
        WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-green-50') and contains(text(), 'berhasil diperbarui')]")));
        assertThat(successMessage.getText()).containsIgnoringCase("berhasil diperbarui");
        
        System.out.println("   ✓ Registration edit functionality tested");
    }
    
    private void submitForReview() {
        WebElement submitReviewButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Submit untuk Review')]")));
        
        js.executeScript("arguments[0].scrollIntoView(true);", submitReviewButton);
        submitReviewButton.click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@class, 'bg-green-50') and contains(text(), 'disubmit')]")));
        
        System.out.println("   ✓ Registration submitted for review");
    }
    
    private void verifyFinalDraftToSubmittedStatus() {
        WebElement statusElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".status-badge")));
        assertThat(statusElement.getText()).containsIgnoringCase("disubmit");
        
        List<WebElement> editButtons = getWebDriver().findElements(
                By.xpath("//a[contains(text(), 'Edit Pendaftaran')]"));
        assertThat(editButtons).isEmpty();
        
        List<WebElement> submitButtons = getWebDriver().findElements(
                By.xpath("//button[contains(text(), 'Submit untuk Review')]"));
        assertThat(submitButtons).isEmpty();
        
        WebElement placementTestStatus = getWebDriver().findElement(
                By.xpath("//div[contains(@class, 'placement-test-info')]//span[contains(@class, 'status-badge')]"));
        assertThat(placementTestStatus.getText()).containsIgnoringCase("menunggu");
        
        System.out.println("   ✓ Final status verified: DRAFT → SUBMITTED");
    }
    
    private void fillMinimalRequiredInformation() {
        // Step 1: Personal Information
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("fullName")));
        fullNameInput.sendKeys("Minimal Test Student");
        
        Select genderSelect = new Select(getWebDriver().findElement(By.id("gender")));
        genderSelect.selectByValue("MALE");
        
        WebElement dateOfBirthInput = getWebDriver().findElement(By.id("dateOfBirth"));
        dateOfBirthInput.sendKeys("01011990");
        
        WebElement placeOfBirthInput = getWebDriver().findElement(By.id("placeOfBirth"));
        placeOfBirthInput.sendKeys("Jakarta");
        
        WebElement phoneNumberInput = getWebDriver().findElement(By.id("phoneNumber"));
        phoneNumberInput.sendKeys("081234567890");
        
        WebElement emailInput = getWebDriver().findElement(By.id("email"));
        emailInput.sendKeys("minimal.test@example.com");
        
        WebElement addressInput = getWebDriver().findElement(By.id("address"));
        addressInput.sendKeys("Jl. Test Minimal No. 1");
        
        WebElement emergencyNameInput = getWebDriver().findElement(By.id("emergencyContactName"));
        emergencyNameInput.sendKeys("Emergency Contact");
        
        WebElement emergencyPhoneInput = getWebDriver().findElement(By.id("emergencyContactPhone"));
        emergencyPhoneInput.sendKeys("081234567891");
        
        Select emergencyRelationSelect = new Select(getWebDriver().findElement(By.id("emergencyContactRelation")));
        emergencyRelationSelect.selectByValue("Orang Tua");
        
        // Move to Step 2: Education
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-education' and contains(@class, 'active')]")));
        
        Select educationLevelSelect = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id("educationLevel"))));
        educationLevelSelect.selectByValue("S1");
        
        WebElement tahsinNoRadio = getWebDriver().findElement(
                By.xpath("//input[@name='previousTahsinExperience' and @value='false']"));
        js.executeScript("arguments[0].click();", tahsinNoRadio);
        
        // Move to Step 3: Program
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-program' and contains(@class, 'active')]")));
        
        List<WebElement> programOptions = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".program-option input[type='radio']")));
        js.executeScript("arguments[0].click();", programOptions.get(0));
        
        // Move to Step 4: Schedule for session preferences
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-schedule' and contains(@class, 'active')]")));
    }
    
    private void testSessionPreferenceManagement() {
        // Test adding preferences
        WebElement addButton = getWebDriver().findElement(By.id("add-preference"));
        
        // Add second preference
        js.executeScript("arguments[0].click();", addButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("session-pref-1")));
        
        // Add third preference
        js.executeScript("arguments[0].click();", addButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("session-pref-2")));
        
        // Verify button is hidden after 3 preferences
        assertThat(addButton.isDisplayed()).isFalse();
        
        // Test removing a preference
        List<WebElement> removeButtons = getWebDriver().findElements(By.cssSelector(".remove-preference"));
        if (!removeButtons.isEmpty()) {
            js.executeScript("arguments[0].click();", removeButtons.get(0));
            // Verify add button becomes visible again
            wait.until(ExpectedConditions.visibilityOf(addButton));
        }
    }
    
    private void testSessionPreferencePriorityHandling() {
        // Fill preferences with different priorities
        fillSessionPreference(0, 1, new String[]{"MONDAY"});
        fillSessionPreference(1, 2, new String[]{"TUESDAY"});
        
        // Verify priority selectors work correctly
        Select priority1 = new Select(getWebDriver().findElement(By.id("sessionPreferences0.priority")));
        Select priority2 = new Select(getWebDriver().findElement(By.id("sessionPreferences1.priority")));
        
        assertThat(priority1.getFirstSelectedOption().getAttribute("value")).isEqualTo("1");
        assertThat(priority2.getFirstSelectedOption().getAttribute("value")).isEqualTo("2");
    }
    
    private void testDaySelectionFunctionality() {
        // Test day checkbox styling and interaction
        String dayCheckboxId = "day_0_WEDNESDAY";
        WebElement dayCheckbox = getWebDriver().findElement(By.id(dayCheckboxId));
        WebElement dayContainer = dayCheckbox.findElement(By.xpath(".."));
        
        // Click the day checkbox
        WebElement dayLabel = getWebDriver().findElement(By.cssSelector("label[for='" + dayCheckboxId + "']"));
        js.executeScript("arguments[0].click();", dayLabel);
        
        // Verify selection styling
        assertThat(dayContainer.getAttribute("class")).contains("selected");
        
        // Click again to deselect
        js.executeScript("arguments[0].click();", dayLabel);
        assertThat(dayContainer.getAttribute("class")).doesNotContain("selected");
    }
    
    private String createRegistrationWithSpecificData(String email) {
        getWebDriver().get(getBaseUrl() + "/register");
        wait.until(ExpectedConditions.titleContains("Pendaftaran Siswa Baru"));
        
        // Fill with specific test data
        WebElement fullNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("fullName")));
        fullNameInput.sendKeys("Data Preservation Test Student");
        
        Select genderSelect = new Select(getWebDriver().findElement(By.id("gender")));
        genderSelect.selectByValue("FEMALE");
        
        WebElement dateOfBirthInput = getWebDriver().findElement(By.id("dateOfBirth"));
        dateOfBirthInput.sendKeys("01011985");
        
        WebElement placeOfBirthInput = getWebDriver().findElement(By.id("placeOfBirth"));
        placeOfBirthInput.sendKeys("Bandung");
        
        WebElement phoneNumberInput = getWebDriver().findElement(By.id("phoneNumber"));
        phoneNumberInput.sendKeys("081999888777");
        
        WebElement emailInput = getWebDriver().findElement(By.id("email"));
        emailInput.sendKeys(email);
        
        WebElement addressInput = getWebDriver().findElement(By.id("address"));
        addressInput.sendKeys("Jl. Data Preservation Test No. 123");
        
        fillMinimalEmergencyAndEducationInfo();
        selectFirstProgram();
        fillMinimalSessionPreference();
        
        // Submit form
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", submitButton);
        
        // Get registration ID from confirmation page
        wait.until(ExpectedConditions.urlContains("/register/confirmation"));
        String currentUrl = getWebDriver().getCurrentUrl();
        if (currentUrl.contains("id=")) {
            return currentUrl.substring(currentUrl.lastIndexOf("id=") + 3);
        }
        
        // Fallback: get from page content
        WebElement idElement = getWebDriver().findElement(
                By.xpath("//span[contains(@class, 'info-value') and contains(text(), '-')]"));
        return idElement.getText();
    }
    
    private void fillMinimalEmergencyAndEducationInfo() {
        // Fill emergency contact info in step 1
        WebElement emergencyNameInput = getWebDriver().findElement(By.id("emergencyContactName"));
        emergencyNameInput.sendKeys("Test Emergency");
        
        WebElement emergencyPhoneInput = getWebDriver().findElement(By.id("emergencyContactPhone"));
        emergencyPhoneInput.sendKeys("081888777666");
        
        Select emergencyRelationSelect = new Select(getWebDriver().findElement(By.id("emergencyContactRelation")));
        emergencyRelationSelect.selectByValue("Orang Tua");
        
        // Move to Step 2: Education
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-education' and contains(@class, 'active')]")));
        
        Select educationLevelSelect = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id("educationLevel"))));
        educationLevelSelect.selectByValue("S2");
        
        WebElement tahsinNoRadio = getWebDriver().findElement(
                By.xpath("//input[@name='previousTahsinExperience' and @value='false']"));
        js.executeScript("arguments[0].click();", tahsinNoRadio);
        
        // Move to Step 3: Program
        nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-program' and contains(@class, 'active')]")));
        
        WebElement registrationReasonInput = getWebDriver().findElement(By.id("registrationReason"));
        registrationReasonInput.sendKeys("Original registration reason for data preservation test");
    }
    
    private void selectFirstProgram() {
        // Program should already be active from previous navigation
        List<WebElement> programOptions = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".program-option input[type='radio']")));
        js.executeScript("arguments[0].click();", programOptions.get(0));
    }
    
    private void fillMinimalSessionPreference() {
        // Move to Step 4: Schedule
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nextBtn")));
        js.executeScript("arguments[0].click();", nextButton);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='step-schedule' and contains(@class, 'active')]")));
        
        Select sessionSelect = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id("sessionPreferences0.sessionId"))));
        if (sessionSelect.getOptions().size() > 1) {
            sessionSelect.selectByIndex(1);
        }
        
        Select prioritySelect = new Select(getWebDriver().findElement(By.id("sessionPreferences0.priority")));
        prioritySelect.selectByValue("1");
        
        WebElement mondayCheckbox = getWebDriver().findElement(By.id("day_0_MONDAY"));
        if (!mondayCheckbox.isSelected()) {
            WebElement mondayLabel = getWebDriver().findElement(By.cssSelector("label[for='day_0_MONDAY']"));
            js.executeScript("arguments[0].click();", mondayLabel);
        }
    }
    
    private void verifySpecificDataPresent(String email) {
        WebElement emailElement = getWebDriver().findElement(
                By.xpath("//td[text()='" + email + "']"));
        assertThat(emailElement.getText()).isEqualTo(email);
        
        WebElement nameElement = getWebDriver().findElement(
                By.xpath("//td[text()='Data Preservation Test Student']"));
        assertThat(nameElement.getText()).isEqualTo("Data Preservation Test Student");
        
        WebElement phoneElement = getWebDriver().findElement(
                By.xpath("//td[text()='081999888777']"));
        assertThat(phoneElement.getText()).isEqualTo("081999888777");
        
        WebElement addressElement = getWebDriver().findElement(
                By.xpath("//td[contains(text(), 'Data Preservation Test')]"));
        assertThat(addressElement.getText()).contains("Data Preservation Test");
    }
}