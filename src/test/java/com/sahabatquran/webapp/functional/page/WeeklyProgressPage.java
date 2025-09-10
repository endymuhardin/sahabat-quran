package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

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
    private final Locator confirmationMessage;
    private final Locator summaryStatistics;
    
    public WeeklyProgressPage(Page page) {
        this.page = page;
        
        // Initialize locators with IDs
        this.weeklyProgressMenu = page.locator("#nav-weekly-progress");
        this.progressInterface = page.locator("#progress-interface");
        
        // Progress interface
        this.weekSelector = page.locator("#week-selector");
        this.studentList = page.locator("#student-list");
        this.progressCategories = page.locator("#progress-categories");
        this.sessionDates = page.locator("#session-dates");
        this.learningObjectivesSummary = page.locator("#objectives-summary");
        
        // Input controls
        this.scoreInputs = page.locator("input[id^='score-']");
        this.gradeDropdowns = page.locator("select[id^='grade-']");
        this.notesTextAreas = page.locator("textarea[id^='notes-']");
        
        // Summary controls
        this.classSummaryField = page.locator("#class-summary");
        this.supportFlags = page.locator("input[id^='support-']");
        this.parentCommunicationNotes = page.locator("#parent-communication");
        this.autoSaveIndicator = page.locator("#auto-save-indicator");
        
        // Submission
        this.confirmationMessage = page.locator("#confirmation-message");
        this.summaryStatistics = page.locator("#summary-statistics");
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
        return studentList.locator("#student-row-ali, #student-row-fatima, .student-row").count() == expectedCount;
    }
    
    public boolean areProgressCategoriesVisible() {
        return progressCategories.isVisible();
    }
    
    public boolean areCompletedSessionsVisible(int sessionCount) {
        return sessionDates.locator("li").count() == sessionCount;
    }
    
    public boolean areSessionDatesVisible() {
        return sessionDates.isVisible();
    }
    
    public boolean isLearningObjectivesSummaryVisible() {
        return learningObjectivesSummary.isVisible();
    }
    
    public void recordStudentProgress(String studentName, int recitationScore, String recitationGrade,
                                      String memorizationProgress, int tajweedScore, String tajweedGrade, 
                                      String participationGrade) {
        // Find the student row by ID (e.g., #student-row-ali)
        String studentId = studentName.toLowerCase();
        Locator studentRow = page.locator("#student-row-" + studentId);
        
        // Fill recitation score and grade
        studentRow.locator("#score-" + studentId + "-mem").fill(String.valueOf(recitationScore));
        studentRow.locator("#grade-" + studentId + "-mem").selectOption(recitationGrade);
        
        // Fill memorization progress
        studentRow.locator("#notes-" + studentId).fill(memorizationProgress);
        
        // Fill tajweed score and grade
        studentRow.locator("#score-" + studentId + "-rec").fill(String.valueOf(tajweedScore));
        studentRow.locator("#grade-" + studentId + "-rec").selectOption(tajweedGrade);
        
        // Fill participation grade
        studentRow.locator("#grade-" + studentId + "-overall").selectOption(participationGrade);
        
        page.waitForTimeout(1000); // Allow for auto-save
    }    public boolean areScoreInputsWorking() {
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
        String studentId = studentName.toLowerCase();
        page.locator("#support-" + studentId).check();
        page.locator("#support-reason-" + studentId).fill(reason);
    }
    
    public boolean isSupportFlagFunctionalityWorking() {
        return supportFlags.first().isEnabled();
    }
    
    public boolean isReasonForSupportFieldAvailable() {
        return page.locator("[id*='support-reason-']").first().isVisible();
    }
    
    public void addParentCommunicationNotes(String notes) {
        parentCommunicationNotes.fill(notes);
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
    
    public void saveProgress() {
        page.locator("#save-progress-btn").click();
    }
    
    public void submitWeeklyProgress() {
        page.locator("#submit-progress-btn").click();
    }
    
    public boolean isProgressSaved() {
        return page.locator("#success-message").isVisible();
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