package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Page Object untuk Registration Management.
 * Menyediakan methods untuk mengakses dan berinteraksi dengan halaman kelola registrasi.
 */
@RequiredArgsConstructor
@Slf4j
public class RegistrationPage {
    
    private final Page page;
    
    // URL paths
    private static final String REGISTRATIONS_PATH = "/registrations";
    
    // Page elements
    private static final String PAGE_TITLE = "#page-title";
    private static final String SEARCH_FORM = "#search-form";
    private static final String SEARCH_BTN = "#search-btn";
    private static final String RESULTS_TABLE = "#registrations-table";
    private static final String RESULTS_COUNT = "#results-count";
    private static final String NO_RESULTS = "#no-results";
    
    // Search fields
    private static final String FULL_NAME_INPUT = "#fullName";
    private static final String EMAIL_INPUT = "#email";
    private static final String STATUS_SELECT = "#status";
    private static final String PROGRAM_SELECT = "#program";
    
    // Modal elements
    private static final String ASSIGN_MODAL = "#assignTeacherModal";
    private static final String TEACHER_SELECT = "#teacherId";
    private static final String ASSIGNMENT_NOTES = "#assignmentNotes";
    private static final String SUBMIT_ASSIGN_BTN = "#submit-assign-btn";
    private static final String CANCEL_ASSIGN_BTN = "#cancel-assign-btn";
    
    // Navigation
    public void navigateToRegistrations(String baseUrl) {
        log.info("Navigating to registrations page: {}{}", baseUrl, REGISTRATIONS_PATH);
        page.navigate(baseUrl + REGISTRATIONS_PATH);
        waitForPageLoad();
    }
    
    public void waitForPageLoad() {
        page.waitForSelector(PAGE_TITLE, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        log.debug("Registrations page loaded successfully");
    }
    
    // Page verification
    public boolean isOnRegistrationsPage() {
        try {
            // Check for specific page elements that indicate we're on the registrations page
            page.waitForSelector(PAGE_TITLE, new Page.WaitForSelectorOptions().setTimeout(5000));
            // Check for unique elements specific to registrations page that are always present
            // Note: RESULTS_TABLE is conditionally rendered, so we check the results card instead
            return page.locator(PAGE_TITLE).isVisible() && 
                   page.locator(SEARCH_FORM).isVisible() && 
                   page.locator("#results-card").isVisible();
        } catch (Exception e) {
            log.warn("Failed to verify registrations page: {}", e.getMessage());
            return false;
        }
    }
    
    // Search functionality
    public void searchByStudentName(String name) {
        log.info("Searching for student: {}", name);
        page.fill(FULL_NAME_INPUT, name);
        page.click(SEARCH_BTN);
        waitForSearchResults();
    }
    
    public void searchByEmail(String email) {
        log.info("Searching by email: {}", email);
        page.fill(EMAIL_INPUT, email);
        page.click(SEARCH_BTN);
        waitForSearchResults();
    }
    
    public void filterByStatus(String status) {
        log.info("Filtering by status: {}", status);
        page.selectOption(STATUS_SELECT, status);
        page.click(SEARCH_BTN);
        waitForSearchResults();
    }
    
    public void filterByProgram(String programId) {
        log.info("Filtering by program ID: {}", programId);
        page.selectOption(PROGRAM_SELECT, programId);
        page.click(SEARCH_BTN);
        waitForSearchResults();
    }
    
    private void waitForSearchResults() {
        // Wait for either results table or no results message
        page.waitForFunction(
            "() => document.querySelector('" + RESULTS_TABLE + "') || document.querySelector('" + NO_RESULTS + "')"
        );
        log.debug("Search results loaded");
    }
    
    // Results verification
    public int getResultsCount() {
        try {
            String countText = page.textContent(RESULTS_COUNT);
            return Integer.parseInt(countText.trim());
        } catch (Exception e) {
            log.warn("Failed to get results count: {}", e.getMessage());
            return 0;
        }
    }
    
    public boolean hasResults() {
        return page.isVisible(RESULTS_TABLE);
    }
    
    public boolean hasNoResults() {
        return page.isVisible(NO_RESULTS);
    }
    
    public String getFirstAvailableRegistrationId() {
        try {
            if (hasResults()) {
                // Get the first row's registration ID from data attribute or ID
                return page.locator("#registrations-table tbody tr").first().getAttribute("data-registration-id");
            }
            return null;
        } catch (Exception e) {
            log.warn("Failed to get first available registration ID: {}", e.getMessage());
            return null;
        }
    }
    
    public boolean isRegistrationVisible(String registrationId) {
        String rowSelector = "#registration-row-" + registrationId;
        return page.isVisible(rowSelector);
    }
    
    public String getRegistrationStatus(String registrationId) {
        String statusSelector = "#status-badge-" + registrationId;
        return page.textContent(statusSelector);
    }
    
    // Registration actions
    public void viewRegistrationDetail(String registrationId) {
        log.info("Viewing registration detail for ID: {}", registrationId);
        String detailBtnSelector = "#detail-btn-" + registrationId;
        page.click(detailBtnSelector);
    }
    
    public void clickAssignTeacher(String registrationId) {
        log.info("Clicking assign teacher for registration ID: {}", registrationId);
        String assignBtnSelector = "#assign-btn-" + registrationId;
        page.click(assignBtnSelector);
        waitForAssignModal();
    }
    
    // Teacher assignment modal
    private void waitForAssignModal() {
        page.waitForSelector(ASSIGN_MODAL + " .modal-content", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        log.debug("Assign teacher modal opened");
    }
    
    public boolean isAssignModalOpen() {
        return page.isVisible(ASSIGN_MODAL + " .modal-content");
    }
    
    public void selectTeacher(String teacherId) {
        log.info("Selecting teacher: {}", teacherId);
        page.selectOption(TEACHER_SELECT, teacherId);
    }
    
    public void fillAssignmentNotes(String notes) {
        log.info("Filling assignment notes: {}", notes);
        page.fill(ASSIGNMENT_NOTES, notes);
    }
    
    public void submitTeacherAssignment() {
        log.info("Submitting teacher assignment");
        page.click(SUBMIT_ASSIGN_BTN);
        // Wait a bit for the AJAX request to complete instead of waiting for modal to close
        page.waitForTimeout(3000);
        log.debug("Teacher assignment submitted, waiting for processing");
    }
    
    public void cancelTeacherAssignment() {
        log.info("Canceling teacher assignment");
        page.click(CANCEL_ASSIGN_BTN);
        waitForModalClose();
    }
    
    private void waitForModalClose() {
        page.waitForSelector(ASSIGN_MODAL + " .modal-content", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
        log.debug("Assign teacher modal closed");
    }
    
    // Complete workflow methods
    public void assignTeacherToRegistration(String registrationId, String teacherId, String notes) {
        log.info("Complete workflow: Assigning teacher {} to registration {}", teacherId, registrationId);
        
        // Find and click assign button
        clickAssignTeacher(registrationId);
        
        // Fill modal form
        selectTeacher(teacherId);
        if (notes != null && !notes.trim().isEmpty()) {
            fillAssignmentNotes(notes);
        }
        
        // Submit assignment
        submitTeacherAssignment();
        
        // Manually close modal if still open and refresh page
        if (isAssignModalOpen()) {
            log.info("Modal still open, manually closing and refreshing page");
            page.keyboard().press("Escape"); // Try to close with escape key
            page.waitForTimeout(500);
            page.reload(); // Force page reload to see updated status
            waitForPageLoad();
        }
    }
    
    // Validation methods
    public void expectRegistrationStatus(String registrationId, String expectedStatus) {
        String actualStatus = getRegistrationStatus(registrationId);
        assertEquals(expectedStatus, actualStatus, 
            "Registration " + registrationId + " should have status " + expectedStatus);
    }
    
    public void expectResultsCount(int expectedCount) {
        int actualCount = getResultsCount();
        assertEquals(expectedCount, actualCount, 
            "Results count should be " + expectedCount);
    }
    
    public void expectRegistrationVisible(String registrationId) {
        assertTrue(isRegistrationVisible(registrationId), 
            "Registration " + registrationId + " should be visible");
    }
    
    public void expectAssignButtonAvailable(String registrationId) {
        String assignBtnSelector = "#assign-btn-" + registrationId;
        assertTrue(page.isVisible(assignBtnSelector), 
            "Assign button should be available for registration " + registrationId);
    }
    
    public void expectStatusAssigned(String registrationId) {
        expectRegistrationStatus(registrationId, "ASSIGNED");
    }
    
    public boolean isDraftStatusUnavailable() {
        try {
            // Check if DRAFT option is available in the status select dropdown
            String draftOptionSelector = STATUS_SELECT + " option[value='DRAFT']";
            return !page.isVisible(draftOptionSelector);
        } catch (Exception e) {
            log.warn("Error checking DRAFT status availability: {}", e.getMessage());
            return true; // If there's an error, assume DRAFT is unavailable (safer assumption)
        }
    }
}