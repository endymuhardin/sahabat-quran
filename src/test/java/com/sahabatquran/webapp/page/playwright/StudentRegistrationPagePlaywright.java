package com.sahabatquran.webapp.page.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Student Registration multi-step form.
 * 
 * Benefits over Selenium version:
 * - Better handling of dynamic step transitions
 * - More reliable form validation detection
 * - Built-in retry mechanisms for element interactions
 */
public class StudentRegistrationPagePlaywright {
    
    private final Page page;
    
    // Step indicators
    private final Locator step1Personal;
    private final Locator step2Education;
    private final Locator step3Program;
    private final Locator step4Schedule;
    private final Locator step5Placement;
    
    // Navigation buttons
    private final Locator nextButton;
    private final Locator previousButton;
    private final Locator submitButton;
    private final Locator clearButton;
    
    // Step 1: Personal Information
    private final Locator fullNameField;
    private final Locator genderSelect;
    private final Locator dateOfBirthField;
    private final Locator placeOfBirthField;
    private final Locator phoneNumberField;
    private final Locator emailField;
    private final Locator addressField;
    private final Locator emergencyContactNameField;
    private final Locator emergencyContactPhoneField;
    private final Locator emergencyContactRelationSelect;
    
    // Step 2: Education
    private final Locator educationLevelSelect;
    private final Locator previousTahsinExperienceNo;
    private final Locator previousTahsinExperienceYes;
    
    // Step 3: Program
    private final Locator programOptions;
    
    // Step 4: Schedule
    private final Locator sessionSelect;
    private final Locator prioritySelect;
    private final Locator mondayCheckbox;
    
    // Step 5: Placement Test
    private final Locator recordingLinkField;
    
    // Confirmation and validation - separate selectors for success and error
    private final Locator confirmationMessage;
    private final Locator validationErrors;
    private final Locator errorMessage;
    
    public StudentRegistrationPagePlaywright(Page page) {
        this.page = page;
        
        // Step indicators
        this.step1Personal = page.locator("#step-personal.active, [data-step='1'].active");
        this.step2Education = page.locator("#step-education.active, [data-step='2'].active");
        this.step3Program = page.locator("#step-program.active, [data-step='3'].active");
        this.step4Schedule = page.locator("#step-schedule.active, [data-step='4'].active");
        this.step5Placement = page.locator("#step-placement.active, [data-step='5'].active");
        
        // Navigation buttons - using specific IDs for reliable element selection
        this.nextButton = page.locator("[id^='next-btn-step-']:visible").first();
        this.previousButton = page.locator("[id^='prev-btn-step-']:visible").first();
        this.submitButton = page.locator("#submit-registration-btn");
        this.clearButton = page.locator("#clearFormBtn");
        
        // Step 1: Personal Information fields
        this.fullNameField = page.locator("#fullName");
        this.genderSelect = page.locator("#gender");
        this.dateOfBirthField = page.locator("#dateOfBirth");
        this.placeOfBirthField = page.locator("#placeOfBirth");
        this.phoneNumberField = page.locator("#phoneNumber");
        this.emailField = page.locator("#email");
        this.addressField = page.locator("#address");
        this.emergencyContactNameField = page.locator("#emergencyContactName");
        this.emergencyContactPhoneField = page.locator("#emergencyContactPhone");
        this.emergencyContactRelationSelect = page.locator("#emergencyContactRelation");
        
        // Step 2: Education fields
        this.educationLevelSelect = page.locator("#educationLevel");
        this.previousTahsinExperienceNo = page.locator("#previousTahsinExperienceNo");
        this.previousTahsinExperienceYes = page.locator("#previousTahsinExperienceYes");
        
        // Step 3: Program selection
        this.programOptions = page.locator(".program-option input[type='radio']");
        
        // Step 4: Schedule fields
        this.sessionSelect = page.locator("#sessionPreferences0\\.sessionId");
        this.prioritySelect = page.locator("#sessionPreferences0\\.priority");
        this.mondayCheckbox = page.locator("#day_0_MONDAY");
        
        // Step 5: Placement Test
        this.recordingLinkField = page.locator("#recordingDriveLink");
        
        // Confirmation and validation - using specific test IDs
        this.confirmationMessage = page.locator("#registration-success-message, #flash-success-message");
        this.errorMessage = page.locator("#flash-error-message");
        this.validationErrors = page.locator("#form-validation-errors");
    }
    
    public void navigateToRegistrationPage(String baseUrl) {
        page.navigate(baseUrl + "/register");
        assertThat(page).hasTitle("Pendaftaran Siswa Baru - Yayasan Sahabat Quran");
    }
    
    // Navigation methods
    public void clickNext() {
        // Find the visible next button using ID pattern
        Locator visibleNext = page.locator("[id^='next-btn-step-']:visible").first();
        visibleNext.click();
        page.waitForTimeout(1000); // Allow step transition
    }
    
    public void clickPrevious() {
        // Find the visible previous button using ID pattern
        Locator visiblePrev = page.locator("[id^='prev-btn-step-']:visible").first();
        visiblePrev.click();
        page.waitForTimeout(1000);
    }
    
    public void submitForm() {
        submitButton.click();
    }
    
    public void clearForm() {
        if (clearButton.isVisible()) {
            // Set up dialog handler before clicking - accept all dialogs for clear button
            page.onDialog(dialog -> {
                System.out.println("Dialog message: " + dialog.message());
                // Since we're intentionally clicking the clear button, always accept the confirmation
                dialog.accept();
            });
            
            clearButton.click();
            
            // Wait for the form to be cleared
            page.waitForTimeout(2000);
            
            // Dialog handler will be automatically removed when page context ends
        } else {
            // Manual form clearing if button not available
            page.evaluate("document.getElementById('registrationForm').reset();");
            page.waitForTimeout(1000);
        }
    }
    
    // Step 1: Personal Information
    public void fillPersonalInformation(String fullName, String gender, String dateOfBirth, 
                                      String placeOfBirth, String phoneNumber, String email, 
                                      String address, String emergencyName, String emergencyPhone, 
                                      String emergencyRelation) {
        fullNameField.fill(fullName);
        genderSelect.selectOption(gender);
        // Convert date format from ddMMyyyy to yyyy-MM-dd for HTML date input
        String formattedDate = formatDateForHtmlInput(dateOfBirth);
        dateOfBirthField.fill(formattedDate);
        placeOfBirthField.fill(placeOfBirth);
        phoneNumberField.fill(phoneNumber);
        emailField.fill(email);
        addressField.fill(address);
        emergencyContactNameField.fill(emergencyName);
        emergencyContactPhoneField.fill(emergencyPhone);
        emergencyContactRelationSelect.selectOption(emergencyRelation);
    }
    
    public void fillMinimalPersonalInfo(String fullName, String email, String phone) {
        fullNameField.fill(fullName);
        emailField.fill(email);
        phoneNumberField.fill(phone);
    }
    
    // Step 2: Education
    public void fillEducationInformation(String educationLevel, boolean hasPreviousExperience) {
        // Wait for education section to be visible
        page.waitForSelector("#section-education[style*='display: block'], #section-education:visible", new Page.WaitForSelectorOptions().setTimeout(5000));
        educationLevelSelect.selectOption(educationLevel);
        if (hasPreviousExperience) {
            previousTahsinExperienceYes.check();
        } else {
            previousTahsinExperienceNo.check();
        }
    }
    
    // Step 3: Program
    public void selectFirstProgram() {
        // Wait for program section to be visible
        page.waitForSelector("#section-program[style*='display: block'], #section-program:visible", new Page.WaitForSelectorOptions().setTimeout(5000));
        programOptions.first().check();
    }
    
    public void selectProgramByIndex(int index) {
        page.waitForSelector("#section-program[style*='display: block'], #section-program:visible", new Page.WaitForSelectorOptions().setTimeout(5000));
        programOptions.nth(index).check();
    }
    
    // Step 4: Schedule
    public void fillSchedulePreferences(String sessionId, String priority, boolean selectMonday) {
        // Wait for schedule section to be visible
        page.waitForSelector("#section-schedule[style*='display: block'], #section-schedule:visible", new Page.WaitForSelectorOptions().setTimeout(10000));
        
        // Make sure the schedule section is visible and displayed
        page.waitForSelector("#section-schedule[style*='display: block'], #section-schedule:not([style*='display: none'])", 
                           new Page.WaitForSelectorOptions().setTimeout(10000));
        
        sessionSelect.selectOption(sessionId);
        prioritySelect.selectOption(priority);
        if (selectMonday) {
            // The checkbox exists but may be hidden, use JavaScript to check it
            page.evaluate("document.getElementById('day_0_MONDAY').checked = true");
        }
    }
    
    // Step 5: Placement Test
    public void fillPlacementTest(String recordingLink) {
        // Wait for placement test section to be visible
        page.waitForSelector("#section-placement[style*='display: block'], #section-placement:visible", new Page.WaitForSelectorOptions().setTimeout(5000));
        recordingLinkField.fill(recordingLink);
    }
    
    // Complete workflow methods
    public void completeFullRegistration() {
        fillPersonalInformation("Playwright Test User", "MALE", "01011990", "Jakarta", 
                               "081555777999", "playwright.test@example.com", "Test Address",
                               "Emergency Contact", "081666888000", "Orang Tua");
        clickNext();
        
        fillEducationInformation("S1", false);
        clickNext();
        
        selectFirstProgram();
        clickNext();
        
        fillSchedulePreferences("90000000-0000-0000-0000-000000000002", "1", true);
        clickNext();
        
        fillPlacementTest("https://drive.google.com/file/d/playwright-test/view");
        submitForm();
    }
    
    // Helper method to convert date format from ddMMyyyy to yyyy-MM-dd for HTML date input
    private String formatDateForHtmlInput(String dateString) {
        if (dateString == null || dateString.length() != 8) {
            return dateString; // Return as-is if not in expected format
        }
        
        try {
            // Extract day, month, year from ddMMyyyy format
            String day = dateString.substring(0, 2);
            String month = dateString.substring(2, 4);
            String year = dateString.substring(4, 8);
            
            // Return in yyyy-MM-dd format required by HTML date input
            return year + "-" + month + "-" + day;
        } catch (Exception e) {
            // If parsing fails, return original string
            return dateString;
        }
    }
    
    // Validation and verification methods
    public boolean isOnStep(int stepNumber) {
        String[] sectionIds = {"", "personal", "education", "program", "schedule", "placement"};
        if (stepNumber < 1 || stepNumber > 5) return false;
        
        try {
            Locator section = page.locator("#section-" + sectionIds[stepNumber]);
            // Check if section is visible (not display: none)
            String display = section.evaluate("el => window.getComputedStyle(el).display").toString();
            return !"none".equals(display);
        } catch (Exception e) {
            return false;
        }
    }
    
    public void expectToBeOnStep(int stepNumber) {
        String[] sectionIds = {"", "personal", "education", "program", "schedule", "placement"};
        if (stepNumber >= 1 && stepNumber <= 5) {
            Locator section = page.locator("#section-" + sectionIds[stepNumber]);
            // Assert that section is visible (not display: none)
            assertThat(section).isVisible();
        }
    }
    
    public boolean isConfirmationDisplayed() {
        try {
            // Check for specific confirmation message IDs
            return confirmationMessage.first().isVisible();
        } catch (Exception e) {
            System.out.println("Confirmation check failed - URL: " + page.url() + ", Error: " + e.getMessage());
            return false;
        }
    }
    
    public void expectConfirmationMessage() {
        assertThat(page).hasURL("**/register/confirmation**");
        assertThat(confirmationMessage.first()).isVisible();
    }
    
    public boolean hasValidationErrors() {
        return validationErrors.count() > 0 && validationErrors.first().isVisible();
    }
    
    public void expectValidationErrors() {
        assertThat(validationErrors.first()).isVisible();
    }
    
    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void expectErrorMessage() {
        assertThat(errorMessage).isVisible();
    }
    
    public void expectFieldValue(String fieldId, String expectedValue) {
        assertThat(page.locator("#" + fieldId)).hasValue(expectedValue);
    }
    
    public void expectFieldEmpty(String fieldId) {
        assertThat(page.locator("#" + fieldId)).hasValue("");
    }
}