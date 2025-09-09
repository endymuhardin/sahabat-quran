package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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
    private final Locator studentList;
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
        
        // Initialize locators
        this.myClassesMenu = page.locator("nav a[href*='my-classes'], text='My Classes'");
        this.sessionDashboard = page.locator(".session-dashboard");
        
        // Check-in related
        this.todaysSession = page.locator(".today-session, .session-card[data-date='today']");
        this.checkInButton = page.locator("button:has-text('Check-in'), button[data-action='check-in']");
        this.checkInModal = page.locator(".check-in-modal, .modal:has-text('Check-in')");
        this.arrivalTimeField = page.locator("input[name='arrivalTime'], #arrival-time");
        this.locationField = page.locator("input[name='location'], #check-in-location");
        this.confirmCheckInButton = page.locator("button:has-text('Confirm Check-in'), button[data-action='confirm-checkin']");
        this.checkInSuccessMessage = page.locator(".success-message:has-text('Check-in berhasil'), .alert-success");
        this.sessionTimer = page.locator(".session-timer, .timer");
        this.startSessionButton = page.locator("button:has-text('Start Session'), button[data-action='start-session']");
        
        // Session execution
        this.attendanceInterface = page.locator(".attendance-interface, .student-attendance");
        this.studentList = page.locator(".student-list, .attendance-list");
        this.attendanceCounter = page.locator(".attendance-counter, .present-count");
        this.sessionNotesField = page.locator("textarea[name='sessionNotes'], #session-notes");
        this.objectivesCheckboxes = page.locator("input[type='checkbox'][name*='objective']");
        this.autoSaveIndicator = page.locator(".auto-save, .saving-indicator");
        
        // Session completion
        this.endSessionButton = page.locator("button:has-text('End Session'), button[data-action='end-session']");
        this.endSessionModal = page.locator(".end-session-modal, .modal:has-text('End Session')");
        this.sessionSummary = page.locator(".session-summary");
        this.submitSessionButton = page.locator("button:has-text('Submit Session'), button[data-action='submit-session']");
        this.completionMessage = page.locator(".completion-message, .alert-success:has-text('completed')");
        
        // Reschedule related
        this.tomorrowSession = page.locator(".tomorrow-session, .session-card[data-date='tomorrow']");
        this.rescheduleButton = page.locator("button:has-text('Reschedule'), button[data-action='reschedule']");
        this.rescheduleModal = page.locator(".reschedule-modal, .modal:has-text('Reschedule')");
        this.reasonDropdown = page.locator("select[name='reason'], #reschedule-reason");
        this.dateTimePicker = page.locator("input[type='date'], input[type='time']");
        this.rescheduleNotes = page.locator("textarea[name='rescheduleNotes'], #reschedule-notes");
        this.submitRescheduleButton = page.locator("button:has-text('Submit Reschedule'), button[data-action='submit-reschedule']");
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
        page.waitForSelector(".check-in-modal, .modal:has-text('Check-in')");
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
        page.waitForSelector(".success-message, .alert-success");
    }
    
    public boolean isCheckInSuccessVisible() {
        return checkInSuccessMessage.isVisible();
    }
    
    public boolean isSessionStatusInProgress() {
        return page.locator("text='IN_PROGRESS', .status:has-text('In Progress')").isVisible();
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
        page.waitForSelector(".session-dashboard, .attendance-interface");
    }
    
    public boolean isSessionDashboardVisible() {
        return sessionDashboard.isVisible();
    }
    
    public boolean isStudentAttendanceInterfaceVisible() {
        return attendanceInterface.isVisible();
    }
    
    public boolean areSessionObjectivesVisible() {
        return page.locator(".session-objectives, .learning-objectives").isVisible();
    }
    
    public boolean areSessionMaterialsVisible() {
        return page.locator(".session-materials, .teaching-materials").isVisible();
    }
    
    public void markStudentAttendance(int presentCount, int absentCount) {
        // Mark students as present
        for (int i = 0; i < presentCount; i++) {
            page.locator(".student-row").nth(i).locator("input[type='checkbox']").check();
        }
        // Leave remaining students unmarked (absent)
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean isAttendanceCounterUpdated(int present, int total) {
        String expectedText = String.format("%d/%d", present, total);
        return page.locator(String.format("text='%s'", expectedText)).isVisible();
    }
    
    public boolean isAutoSaveActive() {
        return autoSaveIndicator.isVisible();
    }
    
    public void addSessionNotes(String notes) {
        sessionNotesField.fill(notes);
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean isSessionNotesAutoSaved() {
        return page.locator(".auto-saved, .saved-indicator").isVisible();
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
        String expectedText = String.format("%d/%d", present, total);
        return page.locator(String.format("text='%s hadir'", expectedText)).isVisible();
    }
    
    public String getDepartureTime() {
        return page.locator("input[name='departureTime'], #departure-time").inputValue();
    }
    
    public void submitSession() {
        submitSessionButton.click();
        page.waitForSelector(".completion-message, .alert-success");
    }
    
    public boolean isSessionCompletedStatusVisible() {
        return page.locator("text='COMPLETED', .status:has-text('Completed')").isVisible();
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
        page.waitForSelector(".reschedule-modal, .modal:has-text('Reschedule')");
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
        return page.locator(".auto-approval, text='Auto-approved'").isVisible();
    }
    
    public boolean isAdditionalNotesFieldAvailable() {
        return rescheduleNotes.isVisible();
    }
    
    public void setNewDateTime(String date, String time) {
        page.locator("input[type='date']").fill(date);
        page.locator("input[type='time']").fill(time);
    }
    
    public boolean isAvailabilityChecked() {
        return page.locator(".availability-check, text='Available'").isVisible();
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
        page.waitForSelector(".success-message, .alert-success");
    }
    
    public boolean isRescheduleRequestSubmittedSuccessfully() {
        return page.locator(".success-message:has-text('submitted'), .alert-success").isVisible();
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