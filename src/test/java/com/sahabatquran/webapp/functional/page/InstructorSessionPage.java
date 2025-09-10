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
    
    // ====================== VALIDATION AND ALTERNATE PATH METHODS ======================
    
    // Late check-in validation methods
    public boolean isLateSessionWarningVisible() {
        return page.locator("#late-session-warning, .late-warning").isVisible();
    }
    
    public boolean isLateBadgeVisible() {
        return page.locator("#late-badge, .late-badge").isVisible();
    }
    
    public boolean isOverdueColorCoding() {
        return page.locator(".session-card.overdue, .session-card.late").isVisible();
    }
    
    public boolean isLateCheckinModalVisible() {
        return page.locator("#modal-late-checkin, .late-checkin-modal").isVisible();
    }
    
    public boolean isLateWarningMessageVisible() {
        return page.locator("#late-warning-message, .late-warning-text").isVisible();
    }
    
    public boolean isReasonRequiredForLateCheckin() {
        return page.locator("#late-checkin-reason[required], #late-reason[required]").isVisible();
    }
    
    public void attemptCheckInWithoutReason() {
        page.locator("#btn-confirm-late-checkin, #btn-confirm-checkin").click();
    }
    
    public boolean isValidationErrorVisible() {
        return page.locator(".validation-error, .error-message, .field-error").isVisible();
    }
    
    public boolean isReasonFieldHighlighted() {
        return page.locator("#late-checkin-reason.error, #late-reason.error, .field-error").isVisible();
    }
    
    public void enterLateCheckinReason(String reason) {
        page.locator("#late-checkin-reason, #late-reason").fill(reason);
    }
    
    public void confirmLateCheckIn() {
        page.locator("#btn-confirm-late-checkin, #btn-confirm-checkin").click();
        page.waitForSelector("#late-checkin-success, #check-in-success");
    }
    
    public boolean isLateCheckinSuccessVisible() {
        return page.locator("#late-checkin-success, .late-success").isVisible();
    }
    
    public boolean isLateSessionStatusVisible() {
        return page.locator("#session-status:has-text('LATE'), .session-status.late").isVisible();
    }
    
    public boolean isRemainingTimeWarningVisible() {
        return page.locator("#remaining-time-warning, .time-warning").isVisible();
    }
    
    public boolean isAdministrativeNoteRecorded() {
        return page.locator("#admin-note, .administrative-note").isVisible();
    }
    
    public boolean isSessionDurationAdjusted() {
        return page.locator("#adjusted-duration, .duration-adjusted").isVisible();
    }
    
    public boolean isAutoNotificationSent() {
        return page.locator("#notification-sent, .auto-notification").isVisible();
    }
    
    public boolean isStudentWaitingTimeRecorded() {
        return page.locator("#waiting-time-recorded, .waiting-time").isVisible();
    }
    
    // Emergency and equipment failure methods
    public boolean isEmergencyOptionsMenuVisible() {
        return page.locator("#emergency-options, .emergency-menu").isVisible();
    }
    
    public void clickEmergencyOptions() {
        page.locator("#emergency-options, .emergency-menu").click();
    }
    
    public boolean isEquipmentIssueOptionVisible() {
        return page.locator("#equipment-issue-option, .equipment-issue").isVisible();
    }
    
    public void selectEquipmentIssue() {
        page.locator("#equipment-issue-option, .equipment-issue").click();
    }
    
    public boolean isEquipmentIssueFormVisible() {
        return page.locator("#equipment-issue-form, .issue-form").isVisible();
    }
    
    public void enterEquipmentIssueDescription(String description) {
        page.locator("#issue-description, #equipment-description").fill(description);
    }
    
    public void selectEquipmentType(String type) {
        page.locator("#equipment-type, #issue-type").selectOption(type);
    }
    
    public void markAsUrgent(boolean urgent) {
        if (urgent) {
            page.locator("#urgent-issue, #mark-urgent").check();
        }
    }
    
    public void submitEquipmentIssue() {
        page.locator("#btn-submit-issue, #submit-equipment-issue").click();
    }
    
    public boolean isEquipmentIssueReported() {
        return page.locator("#issue-reported, .issue-success").isVisible();
    }
    
    public boolean isMaintenanceNotificationSent() {
        return page.locator("#maintenance-notified, .maintenance-notification").isVisible();
    }
    
    public boolean isIssueTrackingNumberGenerated() {
        return page.locator("#tracking-number, .issue-number").isVisible();
    }
    
    public boolean isContinueWithoutEquipmentOptionVisible() {
        return page.locator("#continue-without-equipment, .continue-option").isVisible();
    }
    
    public boolean isRescheduleSessionOptionVisible() {
        return page.locator("#reschedule-option, .reschedule-session").isVisible();
    }
    
    public boolean isRequestAlternativeRoomOptionVisible() {
        return page.locator("#alternative-room, .room-change").isVisible();
    }
    
    public void selectContinueWithoutEquipment() {
        page.locator("#continue-without-equipment, .continue-option").click();
    }
    
    public boolean isAlternativeMethodsGuideVisible() {
        return page.locator("#alternative-methods, .teaching-alternatives").isVisible();
    }
    
    public boolean isSessionNotesUpdatedWithIssue() {
        return page.locator("#session-notes:has-text('equipment'), .notes-with-issue").isVisible();
    }
    
    // Attendance discrepancy methods
    public boolean isAttendanceDiscrepancyWarningVisible() {
        return page.locator("#attendance-discrepancy, .discrepancy-warning").isVisible();
    }
    
    public boolean isExtraStudentsDetected() {
        return page.locator("#extra-students, .additional-students").isVisible();
    }
    
    public boolean isAttendanceValidationTriggered() {
        return page.locator("#attendance-validation, .validation-check").isVisible();
    }
    
    public boolean isAddGuestStudentOptionVisible() {
        return page.locator("#add-guest-student, .guest-option").isVisible();
    }
    
    public boolean isContactAdminOptionVisible() {
        return page.locator("#contact-admin, .admin-contact").isVisible();
    }
    
    public boolean isRejectExtraStudentsOptionVisible() {
        return page.locator("#reject-extra, .reject-students").isVisible();
    }
    
    public void selectAddGuestStudents() {
        page.locator("#add-guest-student, .guest-option").click();
    }
    
    public void addGuestStudent(String name, String reason) {
        page.locator("#guest-student-name, .guest-name").fill(name);
        page.locator("#guest-reason, .guest-reason").fill(reason);
        page.locator("#btn-add-guest, .add-guest-btn").click();
    }
    
    public boolean isGuestStudentsRecorded() {
        return page.locator("#guest-students-list, .guest-list").isVisible();
    }
    
    public boolean isAdminNotifiedOfGuestStudents() {
        return page.locator("#admin-notified-guests, .guest-notification").isVisible();
    }
    
    // Emergency termination methods
    public boolean isEmergencyTerminationOptionVisible() {
        return page.locator("#emergency-termination, .emergency-stop").isVisible();
    }
    
    public void selectEmergencyTermination() {
        page.locator("#emergency-termination, .emergency-stop").click();
    }
    
    public boolean isEmergencyTerminationConfirmationVisible() {
        return page.locator("#emergency-confirmation, .emergency-confirm").isVisible();
    }
    
    public boolean isEmergencyReasonRequired() {
        return page.locator("#emergency-reason[required], .emergency-reason[required]").isVisible();
    }
    
    public void enterEmergencyReason(String reason) {
        page.locator("#emergency-reason, .emergency-reason").fill(reason);
    }
    
    public void selectEmergencyType(String type) {
        page.locator("#emergency-type, .emergency-category").selectOption(type);
    }
    
    public void confirmEmergencyTermination() {
        page.locator("#btn-confirm-emergency, .confirm-emergency").click();
    }
    
    public boolean isEmergencyNotificationSent() {
        return page.locator("#emergency-notification-sent, .emergency-notified").isVisible();
    }
    
    public boolean isSessionTerminatedImmediately() {
        return page.locator("#session-terminated, .terminated-status").isVisible();
    }
    
    public boolean isEmergencyLogCreated() {
        return page.locator("#emergency-log, .emergency-record").isVisible();
    }
    
    public boolean isParentNotificationTriggered() {
        return page.locator("#parent-emergency-notification, .parent-emergency").isVisible();
    }
    
    public boolean isSessionDataPreserved() {
        return page.locator("#data-preserved, .session-data-saved").isVisible();
    }
    
    public boolean isAttendanceDataSaved() {
        return page.locator("#attendance-saved, .attendance-preserved").isVisible();
    }
    
    public boolean isEmergencyReportGenerated() {
        return page.locator("#emergency-report, .emergency-summary").isVisible();
    }
}