package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Playwright Page Object for Instructor Session Management functionality.
 * 
 * Handles session check-in/out, attendance marking, and session execution workflows.
 */
public class InstructorSessionPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator myClassesMenu;
    private final Locator sessionDashboard;
    
    // Check-in related locators
    private final Locator todaysSession;
    private final Locator checkInButton;
    private final Locator checkInModal;
    private final Locator arrivalTimeField;
    private final Locator locationField;
    private final Locator confirmCheckInButton;
    private final Locator checkInSuccessMessage;
    private final Locator sessionTimer;
    private final Locator startSessionButton;
    
    // Session execution locators
    private final Locator attendanceInterface;
    private final Locator attendanceCounter;
    private final Locator sessionNotesField;
    private final Locator objectivesCheckboxes;
    private final Locator autoSaveIndicator;
    
    // Session completion locators
    private final Locator endSessionButton;
    private final Locator endSessionModal;
    private final Locator sessionSummary;
    private final Locator submitSessionButton;
    private final Locator completionMessage;
    
    // Reschedule related locators
    private final Locator tomorrowSession;
    private final Locator rescheduleButton;
    private final Locator rescheduleModal;
    private final Locator reasonDropdown;
    private final Locator dateTimePicker;
    private final Locator rescheduleNotes;
    private final Locator submitRescheduleButton;
    
    public InstructorSessionPage(Page page) {
        this.page = page;
        
        // Initialize locators with IDs
        this.myClassesMenu = page.locator("#nav-my-classes");
        this.sessionDashboard = page.locator("#session-dashboard");
        
        // Check-in related
        this.todaysSession = page.locator("#today-session");
        this.checkInButton = page.locator("#btn-check-in");
        this.checkInModal = page.locator("#modal-check-in");
        this.arrivalTimeField = page.locator("#arrival-time");
        this.locationField = page.locator("#check-in-location");
        this.confirmCheckInButton = page.locator("#btn-confirm-check-in");
        this.checkInSuccessMessage = page.locator("#check-in-success");
        this.sessionTimer = page.locator("#session-timer");
        this.startSessionButton = page.locator("#btn-start-session");
        
        // Session execution
        this.attendanceInterface = page.locator("#attendance-interface");
        this.attendanceCounter = page.locator("#attendance-counter");
        this.sessionNotesField = page.locator("#session-notes");
        this.objectivesCheckboxes = page.locator("input[id^='objective-']");
        this.autoSaveIndicator = page.locator("#auto-save-indicator");
        
        // Session completion
        this.endSessionButton = page.locator("#btn-end-session");
        this.endSessionModal = page.locator("#modal-end-session");
        this.sessionSummary = page.locator("#session-summary");
        this.submitSessionButton = page.locator("#btn-submit-session");
        this.completionMessage = page.locator("#completion-message");
        
        // Reschedule related
        this.tomorrowSession = page.locator("#tomorrow-session");
        this.rescheduleButton = page.locator("#btn-reschedule");
        this.rescheduleModal = page.locator("#modal-reschedule");
        this.reasonDropdown = page.locator("#reschedule-reason");
        this.dateTimePicker = page.locator("input[type='date'], input[type='time']");
        this.rescheduleNotes = page.locator("#reschedule-notes");
        this.submitRescheduleButton = page.locator("#btn-submit-reschedule");
    }
    
    // Navigation methods
    public void navigateToMyClasses() {
        myClassesMenu.click();
        page.waitForLoadState();
    }
    
    // Check-in related methods
    public boolean isTodaysSessionVisible() {
        return todaysSession.isVisible();
    }
    
    public boolean isCheckInButtonEnabled() {
        return checkInButton.isEnabled();
    }
    
    public void clickCheckIn() {
        checkInButton.click();
        page.waitForSelector("#modal-check-in");
    }
    
    public boolean isCheckInModalVisible() {
        return checkInModal.isVisible();
    }
    
    public String getArrivalTime() {
        return arrivalTimeField.inputValue();
    }
    
    public void enterCheckInLocation(String location) {
        locationField.fill(location);
    }
    
    public void confirmCheckIn() {
        confirmCheckInButton.click();
        page.waitForSelector("#check-in-success");
    }
    
    public boolean isCheckInSuccessVisible() {
        return checkInSuccessMessage.isVisible();
    }
    
    public boolean isSessionStatusInProgress() {
        return page.locator("#session-status:has-text('IN_PROGRESS')").isVisible();
    }
    
    public boolean isSessionTimerVisible() {
        return sessionTimer.isVisible();
    }
    
    public boolean isStartSessionButtonVisible() {
        return startSessionButton.isVisible();
    }
    
    // Session execution methods
    public void clickStartSession() {
        startSessionButton.click();
        page.waitForSelector("#attendance-interface");
    }
    
    public boolean isSessionDashboardVisible() {
        return sessionDashboard.isVisible();
    }
    
    public boolean isStudentAttendanceInterfaceVisible() {
        return attendanceInterface.isVisible();
    }
    
    public boolean areSessionObjectivesVisible() {
        return page.locator("#session-objectives").isVisible();
    }
    
    public boolean areSessionMaterialsVisible() {
        return page.locator("#session-materials").isVisible();
    }
    
    public void markStudentAttendance(int presentCount, int absentCount) {
        // Mark students as present
        for (int i = 0; i < presentCount; i++) {
            page.locator("#student-list .student-row").nth(i).locator("input[type='checkbox']").check();
        }
        // Leave remaining students unmarked (absent)
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean isAttendanceCounterUpdated(int present, int total) {
        String expectedText = String.format("%d/%d", present, total);
        return attendanceCounter.textContent().contains(expectedText);
    }
    
    public boolean isAutoSaveActive() {
        return autoSaveIndicator.isVisible();
    }
    
    public void addSessionNotes(String notes) {
        sessionNotesField.fill(notes);
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean isSessionNotesAutoSaved() {
        return page.locator("#auto-saved-indicator").isVisible();
    }
    
    public boolean isCharacterCounterVisible() {
        return page.locator(".character-count, .char-counter").isVisible();
    }
    
    public void markLearningObjectivesAchieved() {
        objectivesCheckboxes.first().check();
        page.waitForTimeout(500);
    }
    
    public boolean isProgressIndicatorVisible() {
        return page.locator(".progress-indicator, .completion-progress").isVisible();
    }
    
    public boolean areAdditionalNotesFieldVisible() {
        return page.locator("textarea[name*='additionalNotes'], .additional-notes").isVisible();
    }
    
    // Session completion methods
    public void clickEndSession() {
        endSessionButton.click();
        page.waitForSelector(".end-session-modal, .modal:has-text('End Session')");
    }
    
    public boolean isEndSessionModalVisible() {
        return endSessionModal.isVisible();
    }
    
    public boolean isSessionSummaryVisible() {
        return sessionSummary.isVisible();
    }
    
    public boolean isAttendanceSummaryVisible(int present, int total) {
        return page.locator("#attendance-summary").isVisible();
    }
    
    public String getDepartureTime() {
        return page.locator("#departure-time").inputValue();
    }
    
    public void submitSession() {
        submitSessionButton.click();
        page.waitForSelector("#completion-message");
    }
    
    public boolean isSessionCompletedStatusVisible() {
        return page.locator("#session-status").textContent().contains("COMPLETED");
    }
    
    public boolean isSubmissionSuccessMessageVisible() {
        return completionMessage.isVisible();
    }
    
    public boolean isSessionCompletedInList() {
        return page.locator(".session-card .status:has-text('Completed')").isVisible();
    }
    
    // Reschedule methods
    public boolean isTomorrowSessionVisible() {
        return tomorrowSession.isVisible();
    }
    
    public boolean isRescheduleOptionAvailable() {
        return rescheduleButton.isVisible();
    }
    
    public void clickReschedule() {
        rescheduleButton.click();
        page.waitForSelector("#modal-reschedule");
    }
    
    public boolean isRescheduleModalVisible() {
        return rescheduleModal.isVisible();
    }
    
    public boolean areSessionDetailsCorrect() {
        return page.locator(".session-details, .modal .session-info").isVisible();
    }
    
    public boolean isReasonDropdownAvailable() {
        return reasonDropdown.isVisible();
    }
    
    public boolean isDateTimePickerAvailable() {
        return dateTimePicker.count() >= 2; // Date and time pickers
    }
    
    public void selectRescheduleReason(String reason) {
        reasonDropdown.selectOption(reason);
    }
    
    public boolean isAutoApprovalIndicatorVisible() {
        return page.locator("#auto-approval-status").isVisible();
    }
    
    public boolean isAdditionalNotesFieldAvailable() {
        return rescheduleNotes.isVisible();
    }
    
    public void setNewDateTime(String date, String time) {
        page.locator("#reschedule-date").fill(date);
        page.locator("#reschedule-time").fill(time);
    }
    
    public boolean isAvailabilityChecked() {
        return page.locator("#availability-status").isVisible();
    }
    
    public boolean isStudentImpactAssessmentVisible() {
        return page.locator(".impact-assessment, .student-impact").isVisible();
    }
    
    public void addRescheduleNotes(String notes) {
        rescheduleNotes.fill(notes);
    }
    
    public boolean isCharacterLimitIndicatorVisible() {
        return page.locator(".char-limit, .character-limit").isVisible();
    }
    
    public boolean isAffectedStudentsCountVisible(int count) {
        return page.locator(String.format("text='%d students'", count)).isVisible();
    }
    
    public boolean isParentNotificationRequirementVisible() {
        return page.locator("text='Parent notification', .parent-notify").isVisible();
    }
    
    public boolean isAutoApprovalStatusVisible() {
        return page.locator(".approval-status, text='Auto-approval'").isVisible();
    }
    
    public void submitRescheduleRequest() {
        submitRescheduleButton.click();
        page.waitForSelector("#reschedule-success");
    }
    
    public boolean isRescheduleRequestSubmittedSuccessfully() {
        return page.locator("#reschedule-success").isVisible();
    }
    
    public String getRequestId() {
        return page.locator(".request-id, [data-request-id]").textContent();
    }
    
    public boolean isStatusApproved() {
        return page.locator("text='APPROVED', .status:has-text('Approved')").isVisible();
    }
    
    public void refreshMyClasses() {
        page.reload();
        page.waitForLoadState();
    }
    
    public boolean isOriginalSessionRescheduled() {
        return page.locator(".session-card .status:has-text('RESCHEDULED')").isVisible();
    }
    
    public boolean isNewSessionCreated() {
        return page.locator(".session-card:has-text('New'), .new-session").isVisible();
    }
    
    public boolean isRescheduleLogVisible() {
        return page.locator(".reschedule-log, .session-history").isVisible();
    }
    
    public boolean isStudentsNotifiedStatusTrue() {
        return page.locator("text='Students Notified: true', .notified-status").isVisible();
    }
    
    public String getNotificationTimestamp() {
        return page.locator(".notification-timestamp, [data-notified-at]").textContent();
    }
    
    public boolean isParentNotificationStatusAvailable() {
        return page.locator(".parent-notification-status, .parent-notify-status").isVisible();
    }
}