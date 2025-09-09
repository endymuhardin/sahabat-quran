package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Weekly Progress Recording functionality.
 * 
 * Handles teacher's weekly student progress recording and assessment workflows.
 */
public class WeeklyProgressPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator weeklyProgressMenu;
    private final Locator progressInterface;
    
    // Progress interface locators
    private final Locator weekSelector;
    private final Locator studentList;
    private final Locator progressCategories;
    private final Locator sessionSummary;
    private final Locator sessionDates;
    private final Locator learningObjectivesSummary;
    
    // Student progress input locators
    private final Locator scoreInputs;
    private final Locator gradeDropdowns;
    private final Locator notesTextAreas;
    
    // Class summary locators
    private final Locator classSummaryField;
    private final Locator supportFlags;
    private final Locator parentCommunicationNotes;
    private final Locator autoSaveIndicator;
    
    // Submission locators
    private final Locator submitProgressButton;
    private final Locator confirmationMessage;
    private final Locator summaryStatistics;
    
    public WeeklyProgressPage(Page page) {
        this.page = page;
        
        // Initialize locators
        this.weeklyProgressMenu = page.locator("nav a[href*='weekly-progress'], text='Weekly Progress'");
        this.progressInterface = page.locator(".progress-recording-interface, .weekly-progress");
        
        // Progress interface
        this.weekSelector = page.locator("select[name='week'], #week-selector");
        this.studentList = page.locator(".student-list, .progress-students");
        this.progressCategories = page.locator(".progress-categories, .assessment-categories");
        this.sessionSummary = page.locator(".session-summary, .completed-sessions");
        this.sessionDates = page.locator(".session-dates, .session-list");
        this.learningObjectivesSummary = page.locator(".objectives-summary, .learning-objectives");
        
        // Input controls
        this.scoreInputs = page.locator("input[type='number'][name*='score']");
        this.gradeDropdowns = page.locator("select[name*='grade']");
        this.notesTextAreas = page.locator("textarea[name*='notes'], textarea[name*='progress']");
        
        // Summary controls
        this.classSummaryField = page.locator("textarea[name='classSummary'], #class-summary");
        this.supportFlags = page.locator("input[type='checkbox'][name*='support']");
        this.parentCommunicationNotes = page.locator("textarea[name='parentNotes'], #parent-communication");
        this.autoSaveIndicator = page.locator(".auto-save, .saving-indicator");
        
        // Submission
        this.submitProgressButton = page.locator("button:has-text('Submit Weekly Progress'), button[data-action='submit-progress']");
        this.confirmationMessage = page.locator(".confirmation-message, .alert-success");
        this.summaryStatistics = page.locator(".summary-stats, .progress-summary");
    }
    
    // Navigation methods
    public void navigateToWeeklyProgress() {
        weeklyProgressMenu.click();
        page.waitForLoadState();
    }
    
    // Interface verification methods
    public boolean isProgressRecordingInterfaceVisible() {
        return progressInterface.isVisible();
    }
    
    public boolean isWeekNumberSelected(int weekNumber) {
        return weekSelector.inputValue().equals(String.valueOf(weekNumber));
    }
    
    public boolean isStudentListVisible(int expectedCount) {
        return studentList.locator(".student-row, .student-item").count() == expectedCount;
    }
    
    public boolean areProgressCategoriesVisible() {
        return progressCategories.isVisible();
    }
    
    public boolean areCompletedSessionsVisible(int sessionCount) {
        return page.locator(String.format("text='%d completed sessions', text='%d sessions'", sessionCount, sessionCount)).isVisible();
    }
    
    public boolean areSessionDatesVisible() {
        return sessionDates.isVisible();
    }
    
    public boolean isLearningObjectivesSummaryVisible() {
        return learningObjectivesSummary.isVisible();
    }
    
    // Student progress recording methods
    public void recordStudentProgress(String studentName, int recitationScore, String recitationGrade, 
                                     String memorizationProgress, int tajweedScore, String tajweedGrade, 
                                     String participationGrade) {
        Locator studentRow = page.locator(String.format(".student-row:has-text('%s')", studentName));
        
        // Fill recitation score and grade
        studentRow.locator("input[name*='recitation'][name*='score']").fill(String.valueOf(recitationScore));
        studentRow.locator("select[name*='recitation'][name*='grade']").selectOption(recitationGrade);
        
        // Fill memorization progress
        studentRow.locator("textarea[name*='memorization']").fill(memorizationProgress);
        
        // Fill tajweed score and grade
        studentRow.locator("input[name*='tajweed'][name*='score']").fill(String.valueOf(tajweedScore));
        studentRow.locator("select[name*='tajweed'][name*='grade']").selectOption(tajweedGrade);
        
        // Fill participation grade
        studentRow.locator("select[name*='participation']").selectOption(participationGrade);
        
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean areScoreInputsWorking() {
        return scoreInputs.first().isEnabled();
    }
    
    public boolean areGradeDropdownsWorking() {
        return gradeDropdowns.first().isEnabled();
    }
    
    public boolean areTextAreasForNotesVisible() {
        return notesTextAreas.count() > 0;
    }
    
    public boolean isConsistentInputInterface() {
        // Check that all student rows have similar input structure
        return page.locator(".student-row").count() > 0 && 
               page.locator(".student-row input[name*='score']").count() > 0;
    }
    
    public boolean areLowerScoresAccepted() {
        // Verify that lower scores (like 60-65) are accepted
        return scoreInputs.first().getAttribute("min").equals("0");
    }
    
    public boolean isNotesFieldForConcernsVisible() {
        return page.locator("textarea[name*='concerns'], textarea[name*='improvement']").isVisible();
    }
    
    // Class summary methods
    public void addClassSummary(String summary) {
        classSummaryField.fill(summary);
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean isSummaryFieldAcceptingLongerText() {
        return classSummaryField.getAttribute("maxlength") == null || 
               Integer.parseInt(classSummaryField.getAttribute("maxlength")) > 500;
    }
    
    public boolean isAutoSaveFunctionalityWorking() {
        return autoSaveIndicator.isVisible();
    }
    
    public void flagStudentForSupport(String studentName, String reason) {
        Locator studentRow = page.locator(String.format(".student-row:has-text('%s')", studentName));
        studentRow.locator("input[type='checkbox'][name*='support']").check();
        studentRow.locator("textarea[name*='supportReason'], input[name*='supportReason']").fill(reason);
    }
    
    public boolean isSupportFlagFunctionalityWorking() {
        return supportFlags.first().isEnabled();
    }
    
    public boolean isReasonForSupportFieldAvailable() {
        return page.locator("textarea[name*='supportReason'], input[name*='supportReason']").isVisible();
    }
    
    public void addParentCommunicationNotes(String notes) {
        parentCommunicationNotes.fill(notes);
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public boolean isParentCommunicationTrackingVisible() {
        return parentCommunicationNotes.isVisible();
    }
    
    public boolean areFollowUpReminderOptionsVisible() {
        return page.locator(".follow-up-options, .reminder-settings").isVisible();
    }
    
    // Submission verification methods
    public boolean areAllStudentsProgressRecorded(int studentCount) {
        // Check that all students have at least one score filled
        return page.locator(".student-row:has(input[name*='score'][value!=''])").count() == studentCount;
    }
    
    public boolean areSummaryStatisticsCalculated() {
        return summaryStatistics.isVisible();
    }
    
    public boolean isMissingDataHighlighted() {
        return page.locator(".missing-data, .required-field:empty").isVisible();
    }
    
    public void submitWeeklyProgress() {
        submitProgressButton.click();
        page.waitForSelector(".confirmation-message, .alert-success");
    }
    
    public boolean isProgressSubmittedSuccessfully() {
        return page.locator(".success-message:has-text('submitted'), .alert-success").isVisible();
    }
    
    public boolean isConfirmationMessageShown() {
        return confirmationMessage.isVisible();
    }
    
    public boolean isDataSavedToDatabase() {
        // Check for success indicators or database save confirmation
        return page.locator("text='saved', text='database', .save-confirmation").isVisible();
    }
    
    public boolean isProgressReadOnly() {
        // Check that inputs are disabled after submission
        return scoreInputs.first().isDisabled();
    }
    
    public boolean isParentsNotificationTriggered() {
        return page.locator("text='Parents notified', .notification-sent").isVisible();
    }
}