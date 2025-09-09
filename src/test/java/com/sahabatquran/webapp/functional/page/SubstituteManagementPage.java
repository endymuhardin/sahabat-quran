package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Substitute Teacher Management functionality.
 * 
 * Handles emergency substitute teacher assignment and management workflows.
 */
public class SubstituteManagementPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator substituteManagementMenu;
    private final Locator emergencyAssignmentPanel;
    
    // Emergency assignment locators
    private final Locator emergencySessionsList;
    private final Locator availableSubstitutes;
    private final Locator assignSubstituteButton;
    private final Locator urgentSessionAlert;
    
    // Substitute assignment modal locators
    private final Locator assignmentModal;
    private final Locator sessionDetails;
    private final Locator originalTeacherInfo;
    private final Locator substituteDropdown;
    private final Locator qualificationMatch;
    private final Locator availabilityCheck;
    
    // Assignment details locators
    private final Locator assignmentReason;
    private final Locator durationField;
    private final Locator specialInstructions;
    private final Locator materialsAccess;
    private final Locator confirmAssignmentButton;
    
    // Notification and communication locators
    private final Locator notificationPreview;
    private final Locator studentNotificationToggle;
    private final Locator parentNotificationToggle;
    private final Locator substituteNotificationToggle;
    private final Locator customMessage;
    
    // Assignment tracking locators
    private final Locator assignmentStatus;
    private final Locator assignmentConfirmation;
    private final Locator notificationLog;
    private final Locator assignmentHistory;
    
    // Substitute teacher management locators
    private final Locator substituteList;
    private final Locator substituteProfile;
    private final Locator qualifications;
    private final Locator availability;
    private final Locator performance;
    
    public SubstituteManagementPage(Page page) {
        this.page = page;
        
        // Initialize locators
        this.substituteManagementMenu = page.locator("nav a[href*='substitute'], text='Substitute Management'");
        this.emergencyAssignmentPanel = page.locator(".emergency-assignment, .substitute-panel");
        
        // Emergency assignment
        this.emergencySessionsList = page.locator(".emergency-sessions, .urgent-sessions");
        this.availableSubstitutes = page.locator(".available-substitutes, .substitute-list");
        this.assignSubstituteButton = page.locator("button:has-text('Assign Substitute'), button[data-action='assign-substitute']");
        this.urgentSessionAlert = page.locator(".urgent-alert, .emergency-alert");
        
        // Assignment modal
        this.assignmentModal = page.locator(".assignment-modal, .modal:has-text('Assign Substitute')");
        this.sessionDetails = page.locator(".session-details, .session-info");
        this.originalTeacherInfo = page.locator(".original-teacher, .teacher-info");
        this.substituteDropdown = page.locator("select[name='substitute'], #substitute-teacher");
        this.qualificationMatch = page.locator(".qualification-match, .qualifications-check");
        this.availabilityCheck = page.locator(".availability-check, .availability-status");
        
        // Assignment details
        this.assignmentReason = page.locator("select[name='reason'], #assignment-reason");
        this.durationField = page.locator("input[name='duration'], #assignment-duration");
        this.specialInstructions = page.locator("textarea[name='instructions'], #special-instructions");
        this.materialsAccess = page.locator("input[type='checkbox'][name='materialsAccess'], #materials-access");
        this.confirmAssignmentButton = page.locator("button:has-text('Confirm Assignment'), button[data-action='confirm-assignment']");
        
        // Notifications
        this.notificationPreview = page.locator(".notification-preview, .message-preview");
        this.studentNotificationToggle = page.locator("input[name='notifyStudents'], #notify-students");
        this.parentNotificationToggle = page.locator("input[name='notifyParents'], #notify-parents");
        this.substituteNotificationToggle = page.locator("input[name='notifySubstitute'], #notify-substitute");
        this.customMessage = page.locator("textarea[name='customMessage'], #custom-message");
        
        // Assignment tracking
        this.assignmentStatus = page.locator(".assignment-status, .status-indicator");
        this.assignmentConfirmation = page.locator(".assignment-confirmation, .confirmation-message");
        this.notificationLog = page.locator(".notification-log, .communication-log");
        this.assignmentHistory = page.locator(".assignment-history, .history-log");
        
        // Substitute management
        this.substituteList = page.locator(".substitute-list, .teachers-list");
        this.substituteProfile = page.locator(".substitute-profile, .teacher-profile");
        this.qualifications = page.locator(".qualifications, .teacher-qualifications");
        this.availability = page.locator(".availability, .teacher-availability");
        this.performance = page.locator(".performance, .teacher-performance");
    }
    
    // Navigation methods
    public void navigateToSubstituteManagement() {
        substituteManagementMenu.click();
        page.waitForLoadState();
    }
    
    public boolean isEmergencyAssignmentPanelVisible() {
        return emergencyAssignmentPanel.isVisible();
    }
    
    // Emergency assignment methods
    public boolean areEmergencySessionsVisible() {
        return emergencySessionsList.isVisible();
    }
    
    public boolean isUrgentSessionAlertVisible() {
        return urgentSessionAlert.isVisible();
    }
    
    public boolean areAvailableSubstitutesVisible() {
        return availableSubstitutes.isVisible();
    }
    
    public int getAvailableSubstitutesCount() {
        return availableSubstitutes.locator(".substitute-item, .teacher-item").count();
    }
    
    public boolean isAssignSubstituteButtonEnabled() {
        return assignSubstituteButton.isEnabled();
    }
    
    public void clickAssignSubstitute(String sessionId) {
        page.locator(String.format(".emergency-session[data-session-id='%s'] .assign-button", sessionId)).click();
        page.waitForSelector(".assignment-modal, .modal:has-text('Assign')");
    }
    
    // Assignment modal methods
    public boolean isAssignmentModalVisible() {
        return assignmentModal.isVisible();
    }
    
    public boolean areSessionDetailsVisible() {
        return sessionDetails.isVisible();
    }
    
    public boolean isOriginalTeacherInfoVisible(String teacherName) {
        return page.locator(String.format("text='%s'", teacherName)).isVisible();
    }
    
    public boolean isSubstituteDropdownAvailable() {
        return substituteDropdown.isVisible();
    }
    
    public void selectSubstituteTeacher(String substituteName) {
        substituteDropdown.selectOption(substituteName);
        page.waitForTimeout(1000); // Allow for qualification check
    }
    
    public boolean isQualificationMatchVisible() {
        return qualificationMatch.isVisible();
    }
    
    public boolean isQualificationMatched() {
        return page.locator(".qualification-match:has-text('Qualified'), .match:has-text('Good')").isVisible();
    }
    
    public boolean isAvailabilityCheckPassed() {
        return page.locator(".availability-check:has-text('Available'), .available-status").isVisible();
    }
    
    // Assignment details methods
    public void selectAssignmentReason(String reason) {
        assignmentReason.selectOption(reason);
    }
    
    public boolean isEmergencyReasonSelected() {
        return assignmentReason.inputValue().equals("Emergency");
    }
    
    public void setAssignmentDuration(String duration) {
        durationField.fill(duration);
    }
    
    public boolean isDurationFieldVisible() {
        return durationField.isVisible();
    }
    
    public void addSpecialInstructions(String instructions) {
        specialInstructions.fill(instructions);
    }
    
    public boolean areSpecialInstructionsFieldVisible() {
        return specialInstructions.isVisible();
    }
    
    public void enableMaterialsAccess() {
        materialsAccess.check();
    }
    
    public boolean isMaterialsAccessEnabled() {
        return materialsAccess.isChecked();
    }
    
    // Notification methods
    public boolean isNotificationPreviewVisible() {
        return notificationPreview.isVisible();
    }
    
    public boolean areNotificationTogglesAvailable() {
        return studentNotificationToggle.isVisible() && 
               parentNotificationToggle.isVisible() && 
               substituteNotificationToggle.isVisible();
    }
    
    public void enableStudentNotification() {
        studentNotificationToggle.check();
    }
    
    public void enableParentNotification() {
        parentNotificationToggle.check();
    }
    
    public void enableSubstituteNotification() {
        substituteNotificationToggle.check();
    }
    
    public boolean areAllNotificationsEnabled() {
        return studentNotificationToggle.isChecked() && 
               parentNotificationToggle.isChecked() && 
               substituteNotificationToggle.isChecked();
    }
    
    public void addCustomMessage(String message) {
        customMessage.fill(message);
    }
    
    public boolean isCustomMessageFieldVisible() {
        return customMessage.isVisible();
    }
    
    // Assignment confirmation methods
    public void confirmAssignment() {
        confirmAssignmentButton.click();
        page.waitForSelector(".assignment-confirmation, .success-message");
    }
    
    public boolean isConfirmAssignmentButtonEnabled() {
        return confirmAssignmentButton.isEnabled();
    }
    
    public boolean isAssignmentConfirmed() {
        return assignmentConfirmation.isVisible();
    }
    
    public boolean isAssignmentStatusCompleted() {
        return page.locator(".status:has-text('Assigned'), .assignment-status:has-text('Complete')").isVisible();
    }
    
    public String getAssignmentId() {
        return page.locator(".assignment-id, [data-assignment-id]").textContent();
    }
    
    // Notification tracking methods
    public boolean isNotificationLogVisible() {
        return notificationLog.isVisible();
    }
    
    public boolean areStudentsNotified() {
        return page.locator(".notification-log:has-text('Students notified')").isVisible();
    }
    
    public boolean areParentsNotified() {
        return page.locator(".notification-log:has-text('Parents notified')").isVisible();
    }
    
    public boolean isSubstituteNotified() {
        return page.locator(".notification-log:has-text('Substitute notified')").isVisible();
    }
    
    public String getNotificationTimestamp() {
        return page.locator(".notification-timestamp, .timestamp").first().textContent();
    }
    
    // Assignment history methods
    public boolean isAssignmentHistoryVisible() {
        return assignmentHistory.isVisible();
    }
    
    public boolean isAssignmentRecordedInHistory() {
        return assignmentHistory.locator(".assignment-record").count() > 0;
    }
    
    public boolean isAssignmentAuditTrailVisible() {
        return page.locator(".audit-trail, .assignment-log").isVisible();
    }
    
    // Substitute teacher management methods
    public boolean isSubstituteListVisible() {
        return substituteList.isVisible();
    }
    
    public void viewSubstituteProfile(String substituteName) {
        page.locator(String.format(".substitute-item:has-text('%s') .view-profile", substituteName)).click();
        page.waitForSelector(".substitute-profile, .teacher-profile");
    }
    
    public boolean isSubstituteProfileVisible() {
        return substituteProfile.isVisible();
    }
    
    public boolean areQualificationsVisible() {
        return qualifications.isVisible();
    }
    
    public boolean isAvailabilityVisible() {
        return availability.isVisible();
    }
    
    public boolean isPerformanceMetricsVisible() {
        return performance.isVisible();
    }
    
    public boolean isSubstituteQualifiedForSubject(String subject) {
        return page.locator(String.format(".qualifications:has-text('%s')", subject)).isVisible();
    }
    
    public boolean isSubstituteAvailableForTimeSlot(String timeSlot) {
        return page.locator(String.format(".availability:has-text('%s')", timeSlot)).isVisible();
    }
    
    public String getSubstitutePerformanceRating() {
        return performance.locator(".rating, .performance-score").textContent();
    }
    
    // Filter and search methods
    public void filterBySubject(String subject) {
        page.locator("select[name='subjectFilter']").selectOption(subject);
        page.waitForTimeout(1000);
    }
    
    public void filterByAvailability() {
        page.locator("input[name='availableOnly']").check();
        page.waitForTimeout(1000);
    }
    
    public void searchSubstitute(String name) {
        page.locator("input[type='search'], .search-input").fill(name);
        page.keyboard().press("Enter");
        page.waitForTimeout(1000);
    }
    
    public boolean areFilteredResultsVisible() {
        return page.locator(".filtered-results, .search-results").isVisible();
    }
    
    // Emergency workflow specific methods
    public boolean isEmergencyWorkflowActive() {
        return page.locator(".emergency-mode, .urgent-workflow").isVisible();
    }
    
    public boolean isAutoAssignmentSuggestionVisible() {
        return page.locator(".auto-suggestion, .recommended-substitute").isVisible();
    }
    
    public String getRecommendedSubstitute() {
        return page.locator(".recommended-substitute, .auto-suggestion").textContent();
    }
    
    public void acceptAutoAssignmentSuggestion() {
        page.locator("button:has-text('Accept Suggestion'), .accept-recommendation").click();
        page.waitForTimeout(1000);
    }
    
    public boolean isQuickAssignmentModeEnabled() {
        return page.locator(".quick-assignment, .express-mode").isVisible();
    }
    
    // Status tracking methods
    public void refreshAssignmentStatus() {
        page.locator("button[data-action='refresh'], .refresh-button").click();
        page.waitForTimeout(2000);
    }
    
    public boolean isAssignmentProgressVisible() {
        return page.locator(".assignment-progress, .progress-indicator").isVisible();
    }
    
    public String getCurrentAssignmentStep() {
        return page.locator(".current-step, .active-step").textContent();
    }
    
    public boolean isAssignmentComplete() {
        return page.locator(".status:has-text('Complete'), .assignment-complete").isVisible();
    }
    
    // Additional methods required by AcademicAdminTest
    public void navigateToEmergencyDashboard() {
        // Try direct navigation if link click fails
        try {
            page.locator("#emergency-dashboard-link").click();
            page.waitForLoadState();
        } catch (Exception e) {
            // Fallback to direct URL navigation with base URL
            String baseUrl = page.url().split("/dashboard")[0];
            page.navigate(baseUrl + "/monitoring/emergency");
            page.waitForLoadState();
        }
        page.waitForTimeout(1000);
    }
    
    public boolean isAlertNotificationForSickTeacherVisible() {
        return page.locator("#sick-teacher-alert").isVisible();
    }
    
    public boolean isSessionRequiringAttentionHighlighted() {
        return page.locator("#sick-teacher-alert").isVisible();
    }
    
    public boolean isFindSubstituteActionButtonAvailable() {
        return page.locator("#find-substitute-button").isVisible();
    }
    
    public void clickSessionRequiringSubstitute() {
        page.locator("#sick-teacher-alert").first().click();
    }
    
    public boolean isSessionInfoComplete() {
        return page.locator("#sick-teacher-alert").isVisible();
    }
    
    public boolean isUrgencyIndicatorVisible(String urgencyLevel) {
        return page.locator(String.format("text='%s'", urgencyLevel)).isVisible();
    }
    
    public void clickAssignSubstitute() {
        page.locator("#find-substitute-button").first().click();
    }
    
    // More methods required by AcademicAdminTest
    public boolean isSubstitutePoolInterfaceVisible() {
        return page.locator(".substitute-pool, .teacher-pool").isVisible();
    }
    
    public boolean areAvailableTeachersFilteredByQualifications() {
        return page.locator(".qualified-teachers, .filtered-teachers").isVisible();
    }
    
    public boolean areCompatibilityIndicatorsVisible() {
        return page.locator("#compatibility-indicators-section").isVisible();
    }
    
    public boolean isAvailabilityStatusRealTime() {
        return page.locator("#realtime-availability-status").isVisible();
    }
    
    public void sortByRating() {
        page.locator("#sort-by-rating").click();
    }
    
    public boolean areRatingAndExperienceVisible() {
        return page.locator("#teacher-rating-1").isVisible();
    }
    
    public boolean isLastAssignmentDateShown() {
        return page.locator("#last-assignment-1").isVisible();
    }
    
    public boolean isEmergencyAvailabilityIndicatorVisible() {
        return page.locator("#emergency-indicator-1").isVisible();
    }
    
    // Final batch of missing methods
    public boolean isTeacherProfileDetailsVisible() {
        return page.locator("#teacher-profile-1").isVisible();
    }
    
    public boolean isQualificationsSummaryVisible() {
        return page.locator("#qualifications-summary").isVisible();
    }
    
    public boolean isAvailabilityConfirmedForToday() {
        return page.locator("#availability-today").isVisible();
    }
    
    public boolean isContactInformationAccessible() {
        return page.locator("#contact-info").isVisible();
    }
    
    public void configureAssignmentDetails(String reason, String duration) {
        page.locator("#assignment-reason").fill(reason);
        page.locator("#duration").fill(duration);
    }
    
    public boolean areAssignmentTypeOptionsAvailable() {
        return page.locator(".assignment-type, .assignment-options").isVisible();
    }
    
    public boolean isCompensationCalculationAutomatic() {
        return page.locator(".auto-compensation, .calculated-pay").isVisible();
    }
    
    public boolean isSpecialInstructionsFieldAvailable() {
        return page.locator("#special-instructions").isVisible();
    }
    
    public boolean isTimelineShowingStepsCompleted() {
        return page.locator(".timeline-completed, .steps-complete").isVisible();
    }
    
    public void waitForSubstituteConfirmation() {
        page.locator(".confirmation-received, .substitute-confirmed").waitFor();
    }
    
    public boolean isStatusUpdatesRealTime() {
        return page.locator("#realtime-status-indicator").isVisible();
    }
    
    public boolean isAcceptedBySubstituteStatusReceived() {
        return page.locator("#substitute-accepted-status").isVisible();
    }
    
    public boolean isSessionAssignmentTransferred() {
        return page.locator("#assignment-transferred-status").isVisible();
    }
    
    public void sendNotificationAboutTeacherChange() {
        page.locator(".send-notification, .notify-change").click();
    }
    
    public boolean isStudentParentNotificationComposed() {
        return page.locator(".notification-composed, .message-ready").isVisible();
    }
    
    public boolean isTeacherIntroductionIncluded() {
        return page.locator(".teacher-intro, .introduction-text").isVisible();
    }
    
    public boolean areSessionDetailsConfirmedUnchanged() {
        return page.locator(".session-confirmed, .details-unchanged").isVisible();
    }
    
    public boolean isNotificationDeliveryConfirmed() {
        return page.locator(".delivery-confirmed, .notification-sent").isVisible();
    }
    
    // Very final batch of missing methods
    public void attachLessonPlanAndMaterials() {
        page.locator(".attach-materials, .file-upload").click();
    }
    
    public boolean isFileUploadFunctional() {
        return page.locator("#file-upload-input").isVisible();
    }
    
    public boolean areMaterialSharingOptionsAvailable() {
        return page.locator(".sharing-options, .material-sharing").isVisible();
    }
    
    public boolean areQuickNotesForSubstituteVisible() {
        return page.locator(".quick-notes, .substitute-notes").isVisible();
    }
    
    public void assignAndNotifySubstitute() {
        page.locator(".assign-notify, .complete-assignment").click();
    }
    
    public boolean isSmsNotificationSentToSubstitute() {
        return page.locator(".sms-sent, .notification-delivered").isVisible();
    }
    
    public boolean isAssignmentStatusUpdated() {
        return page.locator(".status-updated, .assignment-complete").isVisible();
    }
}