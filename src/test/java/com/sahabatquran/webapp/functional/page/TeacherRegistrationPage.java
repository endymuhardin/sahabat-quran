package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

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
            return page.locator(PAGE_TITLE).isVisible() && 
                   page.locator(ASSIGNMENTS_TABLE).isVisible() && 
                   page.locator(TOTAL_ASSIGNMENTS).isVisible();
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
        page.waitForSelector(REVIEW_FORM, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        log.debug("Review page loaded successfully");
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
        page.selectOption(RECOMMENDED_LEVEL, levelId);
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
        page.click(SAVE_DRAFT_BTN);
        waitForFormSubmission();
    }
    
    public void submitReview() {
        log.info("Submitting final review");
        page.click(SUBMIT_REVIEW_BTN);
        waitForFormSubmission();
    }
    
    private void waitForFormSubmission() {
        // Wait for page redirect or success message
        page.waitForTimeout(2000);
        log.debug("Form submission completed");
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
        
        // Fill partial review
        setReviewStatus("IN_REVIEW");
        fillTeacherRemarks(remarks);
        
        // Save draft
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
        // Check for validation error indicators
        assertTrue(page.locator(".is-invalid").count() > 0, 
            "Form should show validation errors");
    }
}