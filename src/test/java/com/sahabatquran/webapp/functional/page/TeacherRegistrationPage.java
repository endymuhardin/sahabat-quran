package com.sahabatquran.webapp.functional.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Page Object untuk Teacher Registration Review.
 * Menyediakan methods untuk mengakses dan berinteraksi dengan halaman review registrasi guru.
 */
@RequiredArgsConstructor
@Slf4j
public class TeacherRegistrationPage {
    
    private final Page page;
    
    // URL paths
    private static final String TEACHER_REGISTRATIONS_PATH = "/registrations/assigned";
    
    // Page elements - List page
    private static final String PAGE_TITLE = "#page-title";
    private static final String ASSIGNMENTS_TABLE = "#assignments-table";
    private static final String NO_ASSIGNMENTS = "#no-assignments";
    private static final String PENDING_COUNT = "#pending-count";
    private static final String IN_REVIEW_COUNT = "#in-review-count";
    private static final String COMPLETED_COUNT = "#completed-count";
    private static final String TOTAL_ASSIGNMENTS = "#total-assignments";
    
    // Page elements - Review page
    private static final String STUDENT_INFO_CARD = "#student-info-card";
    private static final String REVIEW_FORM = "#teacher-review-form";
    private static final String REVIEW_STATUS_SELECT = "#reviewStatus";
    private static final String TEACHER_REMARKS = "#teacherRemarks";
    private static final String RECOMMENDED_LEVEL = "#recommendedLevelId";
    private static final String PLACEMENT_TEST_RESULT = "#placementTestResult";
    private static final String PLACEMENT_NOTES = "#placementNotes";
    private static final String SUBMIT_REVIEW_BTN = "#submit-review-btn";
    private static final String SAVE_DRAFT_BTN = "#save-draft-btn";
    private static final String PLAY_RECORDING_BTN = "#play-recording-btn";
    
    // Navigation
    public void navigateToTeacherRegistrations(String baseUrl) {
        log.info("Navigating to teacher registrations page: {}{}", baseUrl, TEACHER_REGISTRATIONS_PATH);
        page.navigate(baseUrl + TEACHER_REGISTRATIONS_PATH);
        waitForPageLoad();
    }
    
    public void waitForPageLoad() {
        page.waitForSelector(PAGE_TITLE, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        log.debug("Teacher registrations page loaded successfully");
    }
    
    // Page verification
    public boolean isOnTeacherRegistrationsPage() {
        try {
            // Check for specific page elements that indicate we're on the teacher registrations page
            page.waitForSelector(PAGE_TITLE, new Page.WaitForSelectorOptions().setTimeout(5000));
            // Check for unique elements specific to teacher registrations page
            // Either assignments table is visible OR no-assignments message is visible
            return page.locator(PAGE_TITLE).isVisible() && 
                   page.locator(TOTAL_ASSIGNMENTS).isVisible() &&
                   (page.locator(ASSIGNMENTS_TABLE).isVisible() || page.locator(NO_ASSIGNMENTS).isVisible());
        } catch (Exception e) {
            log.warn("Failed to verify teacher registrations page: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean isOnReviewPage() {
        try {
            page.waitForSelector(REVIEW_FORM, new Page.WaitForSelectorOptions().setTimeout(5000));
            return page.isVisible(REVIEW_FORM);
        } catch (Exception e) {
            log.warn("Failed to verify review page: {}", e.getMessage());
            return false;
        }
    }
    
    // Dashboard counts
    public int getPendingCount() {
        try {
            String countText = page.textContent(PENDING_COUNT);
            return Integer.parseInt(countText.trim());
        } catch (Exception e) {
            log.warn("Failed to get pending count: {}", e.getMessage());
            return 0;
        }
    }
    
    public int getInReviewCount() {
        try {
            String countText = page.textContent(IN_REVIEW_COUNT);
            return Integer.parseInt(countText.trim());
        } catch (Exception e) {
            log.warn("Failed to get in-review count: {}", e.getMessage());
            return 0;
        }
    }
    
    public int getCompletedCount() {
        try {
            String countText = page.textContent(COMPLETED_COUNT);
            return Integer.parseInt(countText.trim());
        } catch (Exception e) {
            log.warn("Failed to get completed count: {}", e.getMessage());
            return 0;
        }
    }
    
    public int getTotalAssignments() {
        try {
            String countText = page.textContent(TOTAL_ASSIGNMENTS);
            return Integer.parseInt(countText.trim());
        } catch (Exception e) {
            log.warn("Failed to get total assignments: {}", e.getMessage());
            return 0;
        }
    }
    
    // Assignments list
    public boolean hasAssignments() {
        return page.isVisible(ASSIGNMENTS_TABLE);
    }
    
    public boolean hasNoAssignments() {
        return page.isVisible(NO_ASSIGNMENTS);
    }
    
    public String getFirstAssignedRegistrationId() {
        try {
            if (hasAssignments()) {
                // Get the first row's registration ID from data attribute
                return page.locator("#assignments-table tbody tr").first().getAttribute("data-assignment-id");
            }
            return null;
        } catch (Exception e) {
            log.warn("Failed to get first assigned registration ID: {}", e.getMessage());
            return null;
        }
    }
    
    public boolean isAssignmentVisible(String registrationId) {
        String rowSelector = "#assignment-row-" + registrationId;
        return page.isVisible(rowSelector);
    }
    
    public String getReviewStatus(String registrationId) {
        String statusSelector = "#review-status-badge-" + registrationId;
        return page.textContent(statusSelector);
    }
    
    // Assignment actions
    public void viewAssignmentDetail(String registrationId) {
        log.info("Viewing assignment detail for ID: {}", registrationId);
        String detailBtnSelector = "#detail-btn-" + registrationId;
        page.click(detailBtnSelector);
    }
    
    public void startReview(String registrationId) {
        log.info("Starting review for registration ID: {}", registrationId);
        String reviewBtnSelector = "#review-btn-" + registrationId;
        page.click(reviewBtnSelector);
        waitForReviewPageLoad();
    }
    
    public void continueReview(String registrationId) {
        log.info("Continuing review for registration ID: {}", registrationId);
        String reviewBtnSelector = "#review-btn-" + registrationId;
        page.click(reviewBtnSelector);
        waitForReviewPageLoad();
    }
    
    private void waitForReviewPageLoad() {
        // Add a short wait for navigation to complete
        page.waitForTimeout(3000);
        
        // Log current URL for debugging
        log.info("Current URL after clicking review: {}", page.url());
        
        // Monitor console messages for errors
        page.onConsoleMessage(msg -> {
            log.info("Console {}: {}", msg.type(), msg.text());
        });
        
        // Wait for review form to be visible with a longer timeout
        try {
            page.waitForSelector(REVIEW_FORM, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(60000));
            log.debug("Review page loaded successfully");
            
            // Additional wait for dynamic content to load
            page.waitForTimeout(2000);
            
        } catch (Exception e) {
            log.error("Failed to find review form. Current URL: {}", page.url());
            log.error("Page title: {}", page.title());
            
            // Check for template/HTML issues
            String pageContent = page.content();
            
            // Check for unclosed HTML tags
            long divOpenCount = pageContent.chars().filter(ch -> ch == '<').count();
            long divCloseCount = pageContent.chars().filter(ch -> ch == '>').count();
            if (divOpenCount != divCloseCount) {
                log.error("Possible unclosed HTML tags detected. Open: {}, Close: {}", divOpenCount, divCloseCount);
            }
            
            // Check if CSS/JS resources are loaded
            if (!pageContent.contains("bootstrap") && !pageContent.contains("css")) {
                log.error("CSS resources may not be loading properly");
            }
            
            // Log specific form-related content
            if (pageContent.contains("teacher-review-form")) {
                log.info("Form HTML found in page source");
                // Extract just the form section
                int formStart = pageContent.indexOf("<form");
                int formEnd = pageContent.indexOf("</form>", formStart) + 7;
                if (formStart >= 0 && formEnd > formStart) {
                    String formHtml = pageContent.substring(formStart, Math.min(formEnd, formStart + 500));
                    log.info("Form HTML snippet: {}", formHtml);
                }
            } else {
                log.error("teacher-review-form not found in page source");
            }
            
            // Check for JavaScript errors in the page
            if (pageContent.contains("SyntaxError") || pageContent.contains("ReferenceError")) {
                log.error("JavaScript syntax errors detected in page source");
            }
            
            throw e;
        }
    }
    
    // Student information verification
    public String getStudentName() {
        return page.textContent("#student-name");
    }
    
    public String getStudentEmail() {
        return page.textContent("#student-email");
    }
    
    public String getStudentProgram() {
        return page.textContent("#student-program");
    }
    
    public void expectStudentName(String expectedName) {
        String actualName = getStudentName();
        assertEquals(expectedName, actualName, "Student name should match");
    }
    
    // Placement test
    public boolean isRecordingAvailable() {
        return page.isVisible(PLAY_RECORDING_BTN);
    }
    
    public void playRecording() {
        log.info("Playing placement test recording");
        page.click(PLAY_RECORDING_BTN);
        // Note: This will open in new tab, actual playback testing would need additional setup
    }
    
    // Review form actions
    public void setReviewStatus(String status) {
        log.info("Setting review status to: {}", status);
        page.selectOption(REVIEW_STATUS_SELECT, status);
    }
    
    public void fillTeacherRemarks(String remarks) {
        log.info("Filling teacher remarks: {}", remarks.substring(0, Math.min(50, remarks.length())) + "...");
        page.fill(TEACHER_REMARKS, remarks);
    }
    
    public void selectRecommendedLevel(String levelId) {
        log.info("Selecting recommended level: {}", levelId);
        
        // Debug: Check if the select element exists and is visible
        if (!page.locator(RECOMMENDED_LEVEL).isVisible()) {
            log.error("Recommended level select element is not visible!");
            log.info("Checking page content for debugging...");
            
            // Check for any error messages on the page
            if (page.locator(".alert-danger, .error").count() > 0) {
                log.error("Error message on page: {}", page.locator(".alert-danger, .error").first().textContent());
            }
            
            // Check if form exists
            if (!page.locator("#teacher-review-form").isVisible()) {
                log.error("Teacher review form is not visible!");
            }
            
            // Log current URL to verify we're on the right page
            log.info("Current URL: {}", page.url());
            
            // Log more info about the form state
            log.info("Form element count: {}", page.locator("form#teacher-review-form select, form#teacher-review-form input, form#teacher-review-form textarea").count());
        }
        
        page.selectOption(RECOMMENDED_LEVEL, new SelectOption().setLabel(levelId));
    }
    
    public void setPlacementTestResult(String result) {
        log.info("Setting placement test result: {}", result);
        page.selectOption(PLACEMENT_TEST_RESULT, result);
    }
    
    public void fillPlacementNotes(String notes) {
        log.info("Filling placement notes: {}", notes);
        page.fill(PLACEMENT_NOTES, notes);
    }
    
    public void saveDraft() {
        log.info("Saving review as draft");
        
        // Debug: Check field values before clicking
        String reviewStatusValue = page.locator(REVIEW_STATUS_SELECT).inputValue();
        String teacherRemarksValue = page.locator(TEACHER_REMARKS).inputValue();
        log.info("Before save draft click - Review status: '{}', Teacher remarks length: {}", 
                 reviewStatusValue, teacherRemarksValue.length());
        
        // Check if save draft button is visible and enabled
        if (!page.locator(SAVE_DRAFT_BTN).isVisible()) {
            throw new AssertionError("Save draft button is not visible");
        }
        if (!page.locator(SAVE_DRAFT_BTN).isEnabled()) {
            throw new AssertionError("Save draft button is not enabled");
        }
        
        // Listen for console messages to capture JavaScript errors
        page.onConsoleMessage(msg -> {
            String type = msg.type();
            String text = msg.text();
            if ("error".equals(type)) {
                log.error("JavaScript Error: {}", text);
            } else if ("warning".equals(type)) {
                log.warn("JavaScript Warning: {}", text);
            } else {
                log.info("Console {}: {}", type, text);
            }
        });
        
        // Listen for page errors
        page.onPageError(error -> {
            log.error("Page Error: {}", error);
        });
        
        
        // Now click the save draft button to see what happens
        log.info("Clicking save draft button...");
        page.click(SAVE_DRAFT_BTN);
        
        // Wait a moment to capture any console output
        page.waitForTimeout(1000);
        
        waitForFormSubmission(); // Draft saves also redirect on successful submission
    }
    
    public void submitReview() {
        log.info("Submitting final review");
        page.click(SUBMIT_REVIEW_BTN);
        waitForFormSubmission();
    }
    
    public void submitReviewExpectingValidation() {
        log.info("Submitting final review expecting validation errors");
        page.click(SUBMIT_REVIEW_BTN);
        waitForValidationErrors();
    }
    
    
    private void waitForValidationErrors() {
        // Brief wait for form validation to trigger
        page.waitForTimeout(500);
        
        // Check for validation errors using IDs - this is the expected behavior
        boolean hasErrorAlert = page.locator("#error-alert").count() > 0;
        boolean hasStatusError = page.locator("#reviewStatus-error:not(.hidden)").count() > 0;
        boolean hasRemarksError = page.locator("#teacherRemarks-error:not(.hidden)").count() > 0;
        
        // For validation errors, we expect to stay on the same page WITH validation indicators
        String currentUrl = page.url();
        if (!currentUrl.contains("/review")) {
            throw new AssertionError("Expected to stay on review page due to validation, but URL is: " + currentUrl);
        }
        
        // Log validation state for debugging
        String errorMessage = hasErrorAlert ? page.locator("#error-alert").textContent() : "";
        log.info("Validation check completed - Error alert: {}, Status error visible: {}, Remarks error visible: {}, URL: {}", 
                 errorMessage, hasStatusError, hasRemarksError, currentUrl);
        
        // Success - we stayed on review page as expected for validation errors
        log.info("Form validation working correctly - stayed on review page");
    }
    
    private void waitForFormSubmission() {
        // Wait briefly for form processing
        page.waitForTimeout(1000);
        
        // Check for validation errors first using IDs
        boolean hasErrorAlert = page.locator("#error-alert").count() > 0;
        boolean hasStatusError = page.locator("#reviewStatus-error").isVisible();
        boolean hasRemarksError = page.locator("#teacherRemarks-error").isVisible();
        
        if (hasErrorAlert || hasStatusError || hasRemarksError) {
            String errorMessage = hasErrorAlert ? page.locator("#error-alert").textContent() : "";
            log.warn("Form validation errors found - Error alert: {}, Status error visible: {}, Remarks error visible: {}", 
                     errorMessage, hasStatusError, hasRemarksError);
            
            throw new AssertionError("Form submission failed with validation errors. Error message: " + errorMessage);
        }
        
        // Check for success messages that might indicate form was processed but stayed on same page
        if (page.locator("#success-alert").count() > 0) {
            String successMessage = page.locator("#success-alert").textContent();
            log.info("Form processed successfully with message: {}", successMessage);
            return; // Form was processed successfully, no redirect needed
        }
        
        // Must redirect to assignments list for successful submission
        try {
            page.waitForURL("**/registrations/assigned", new Page.WaitForURLOptions().setTimeout(8000));
            log.info("Form submitted successfully - redirected to assignments list");
        } catch (Exception e) {
            // Check current URL and fail with clear message
            String currentUrl = page.url();
            
            // Additional debugging - check page content for clues
            String pageTitle = page.title();
            boolean hasForm = page.locator("#teacher-review-form").count() > 0;
            
            log.error("Form submission issue - Current URL: {}, Page title: {}, Has form: {}", 
                     currentUrl, pageTitle, hasForm);
            
            // Check if there are any alert messages we missed using IDs
            String successAlert = page.locator("#success-alert").count() > 0 ? page.locator("#success-alert").textContent() : "";
            String errorAlert = page.locator("#error-alert").count() > 0 ? page.locator("#error-alert").textContent() : "";
            String allAlerts = successAlert + " " + errorAlert;
            if (!allAlerts.trim().isEmpty()) {
                log.error("All page alerts: {}", allAlerts);
            }
            
            if (currentUrl.contains("/review")) {
                throw new AssertionError("Form submission failed - stayed on review page. Expected redirect to /registrations/assigned but URL is: " + currentUrl + ". Page alerts: " + allAlerts);
            } else {
                throw new AssertionError("Form submission failed - unexpected URL after submission: " + currentUrl + ". Page alerts: " + allAlerts);
            }
        }
    }
    
    // Complete review workflow
    public void completeReview(String registrationId, String remarks, String recommendedLevelId) {
        log.info("Complete review workflow for registration: {}", registrationId);
        
        // Navigate to review if not already there
        if (!isOnReviewPage()) {
            startReview(registrationId);
        }
        
        // Fill review form
        setReviewStatus("COMPLETED");
        fillTeacherRemarks(remarks);
        
        if (recommendedLevelId != null) {
            selectRecommendedLevel(recommendedLevelId);
        }
        
        // Submit review
        submitReview();
    }
    
    public void completeReviewWithPlacementTest(String registrationId, String remarks, 
                                              String recommendedLevelId, String placementResult, 
                                              String placementNotes) {
        log.info("Complete review with placement test for registration: {}", registrationId);
        
        // Navigate to review if not already there
        if (!isOnReviewPage()) {
            startReview(registrationId);
        }
        
        // Fill review form
        setReviewStatus("COMPLETED");
        fillTeacherRemarks(remarks);
        
        if (recommendedLevelId != null) {
            selectRecommendedLevel(recommendedLevelId);
        }
        
        // Fill placement test evaluation
        if (placementResult != null) {
            setPlacementTestResult(placementResult);
        }
        
        if (placementNotes != null) {
            fillPlacementNotes(placementNotes);
        }
        
        // Submit review
        submitReview();
    }
    
    public void saveDraftReview(String registrationId, String remarks) {
        log.info("Save draft review for registration: {}", registrationId);
        
        // Navigate to review if not already there
        if (!isOnReviewPage()) {
            startReview(registrationId);
        }
        
        // Fill partial review - Don't set status, let JavaScript handle it
        // The save draft button click will set status to IN_REVIEW
        fillTeacherRemarks(remarks);
        
        // Save draft - JavaScript will set the status when button is clicked
        saveDraft();
    }
    
    // Validation methods
    public void expectReviewStatus(String registrationId, String expectedStatus) {
        String actualStatus = getReviewStatus(registrationId);
        assertEquals(expectedStatus, actualStatus, 
            "Review status for registration " + registrationId + " should be " + expectedStatus);
    }
    
    public void expectAssignmentVisible(String registrationId) {
        assertTrue(isAssignmentVisible(registrationId), 
            "Assignment " + registrationId + " should be visible");
    }
    
    public void expectReviewButtonAvailable(String registrationId) {
        String reviewBtnSelector = "#review-btn-" + registrationId;
        assertTrue(page.isVisible(reviewBtnSelector), 
            "Review button should be available for assignment " + registrationId);
    }
    
    public void expectDashboardCounts(int expectedPending, int expectedInReview, int expectedCompleted) {
        assertEquals(expectedPending, getPendingCount(), "Pending count should match");
        assertEquals(expectedInReview, getInReviewCount(), "In-review count should match");
        assertEquals(expectedCompleted, getCompletedCount(), "Completed count should match");
    }
    
    public void expectFormValidationError() {
        // Check for validation error indicators using IDs - need to check for visible errors (not hidden)
        boolean hasErrorAlert = page.locator("#error-alert").count() > 0;
        boolean hasStatusError = page.locator("#reviewStatus-error:not(.hidden)").count() > 0;
        boolean hasRemarksError = page.locator("#teacherRemarks-error:not(.hidden)").count() > 0;
        boolean hasInvalidFields = page.locator(".is-invalid").count() > 0;
        
        // Debug information
        String reviewStatusValue = page.locator("#reviewStatus").inputValue();
        String teacherRemarksValue = page.locator("#teacherRemarks").inputValue();
        
        log.info("Form validation check - Review status: '{}', Teacher remarks: '{}' (length: {})", 
                 reviewStatusValue, teacherRemarksValue, teacherRemarksValue.length());
        log.info("Validation indicators - Error alert: {}, Status error: {}, Remarks error: {}, Invalid fields: {}", 
                 hasErrorAlert, hasStatusError, hasRemarksError, hasInvalidFields);
        
        assertTrue(hasErrorAlert || hasStatusError || hasRemarksError || hasInvalidFields, 
            "Form should show validation errors");
    }
}