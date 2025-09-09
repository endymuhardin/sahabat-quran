package com.sahabatquran.webapp.functional.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright Page Object for Student Feedback functionality.
 * 
 * Handles anonymous teacher evaluation and parent notification response workflows.
 */
public class StudentFeedbackPage {
    
    private final Page page;
    
    // Navigation locators
    private final Locator feedbackMenu;
    private final Locator studentDashboard;
    private final Locator feedbackNotificationBadge;
    
    // Feedback campaign locators
    private final Locator activeCampaigns;
    private final Locator teacherEvaluationCampaign;
    private final Locator anonymousBadge;
    private final Locator startFeedbackButton;
    
    // Feedback form locators
    private final Locator feedbackForm;
    private final Locator anonymityNotice;
    private final Locator progressIndicator;
    private final Locator questionCategories;
    private final Locator progressBar;
    private final Locator autoSaveIndicator;
    
    // Rating interface locators
    private final Locator ratingStars;
    private final Locator ratingInterface;
    private final Locator textAreas;
    private final Locator characterLimitIndicator;
    
    // Review and submission locators
    private final Locator reviewSection;
    private final Locator anonymousSubmissionReminder;
    private final Locator submitFeedbackButton;
    private final Locator editAnswersOption;
    private final Locator confirmationModal;
    private final Locator thankYouMessage;
    
    // Parent notification locators
    private final Locator teacherChangeNotification;
    private final Locator substituteTeacherInfo;
    private final Locator changeDuration;
    private final Locator feedbackLink;
    private final Locator anonymousFeedbackForm;
    private final Locator parentComments;
    
    public StudentFeedbackPage(Page page) {
        this.page = page;
        
        // Initialize locators
        this.feedbackMenu = page.locator("nav a[href*='feedback'], text='Feedback'");
        this.studentDashboard = page.locator(".student-dashboard, .dashboard");
        this.feedbackNotificationBadge = page.locator(".notification-badge, .feedback-notification");
        
        // Feedback campaigns
        this.activeCampaigns = page.locator(".active-campaigns, .feedback-campaigns");
        this.teacherEvaluationCampaign = page.locator(".campaign-card:has-text('Teacher Evaluation')");
        this.anonymousBadge = page.locator(".anonymous-badge, .badge:has-text('Anonymous')");
        this.startFeedbackButton = page.locator("button:has-text('Start Feedback'), button[data-action='start-feedback']");
        
        // Feedback form
        this.feedbackForm = page.locator(".feedback-form, .evaluation-form");
        this.anonymityNotice = page.locator(".anonymity-notice, .anonymous-notice");
        this.progressIndicator = page.locator(".progress-indicator, .question-progress");
        this.questionCategories = page.locator(".question-categories, .category-sections");
        this.progressBar = page.locator(".progress-bar, progress");
        this.autoSaveIndicator = page.locator(".auto-save, .saving-indicator");
        
        // Rating interface
        this.ratingStars = page.locator(".rating-stars, .star-rating");
        this.ratingInterface = page.locator(".rating-interface, .rating-section");
        this.textAreas = page.locator("textarea[name*='feedback'], textarea[name*='comment']");
        this.characterLimitIndicator = page.locator(".char-limit, .character-count");
        
        // Review and submission
        this.reviewSection = page.locator(".review-section, .answer-review");
        this.anonymousSubmissionReminder = page.locator(".submission-reminder:has-text('Anonymous')");
        this.submitFeedbackButton = page.locator("button:has-text('Submit Feedback'), button[data-action='submit']");
        this.editAnswersOption = page.locator("button:has-text('Edit Answers'), .edit-answers");
        this.confirmationModal = page.locator(".confirmation-modal, .modal:has-text('Confirm')");
        this.thankYouMessage = page.locator(".thank-you-message, .success-message:has-text('Thank you')");
        
        // Parent notification
        this.teacherChangeNotification = page.locator(".teacher-change-notification, .change-notice");
        this.substituteTeacherInfo = page.locator(".substitute-info, .teacher-info");
        this.changeDuration = page.locator(".change-duration, .duration-info");
        this.feedbackLink = page.locator("a[href*='feedback']:has-text('feedback'), .feedback-link");
        this.anonymousFeedbackForm = page.locator(".anonymous-feedback-form");
        this.parentComments = page.locator("textarea[name='parentComments'], #parent-comments");
    }
    
    // Dashboard navigation methods
    public boolean isStudentDashboardVisible() {
        return studentDashboard.isVisible();
    }
    
    public boolean isFeedbackNotificationBadgeVisible() {
        return feedbackNotificationBadge.isVisible();
    }
    
    public boolean isFeedbackMenuAvailable() {
        return feedbackMenu.isVisible();
    }
    
    public void navigateToFeedback() {
        feedbackMenu.click();
        page.waitForLoadState();
    }
    
    // Feedback campaign methods
    public boolean areActiveCampaignsVisible() {
        return activeCampaigns.isVisible();
    }
    
    public boolean isTeacherEvaluationCampaignVisible(String campaignType) {
        return page.locator(String.format(".campaign-card:has-text('%s')", campaignType)).isVisible();
    }
    
    public boolean isAnonymousBadgeVisible() {
        return anonymousBadge.isVisible();
    }
    
    public boolean isStartFeedbackButtonAvailable() {
        return startFeedbackButton.isEnabled();
    }
    
    public void startFeedbackSession(String targetTeacher) {
        page.locator(String.format(".campaign-card:has-text('%s')", targetTeacher))
            .locator("button:has-text('Start'), button[data-action='start']").click();
        page.waitForSelector(".feedback-form, .evaluation-form");
    }
    
    // Feedback form methods
    public boolean isFeedbackFormOpened() {
        return feedbackForm.isVisible();
    }
    
    public boolean isAnonymityNoticeVisible() {
        return anonymityNotice.isVisible();
    }
    
    public boolean isProgressIndicatorVisible(int current, int total) {
        String expectedText = String.format("%d/%d", current, total);
        return page.locator(String.format("text='%s'", expectedText)).isVisible();
    }
    
    public boolean areQuestionCategoriesVisible() {
        return questionCategories.isVisible();
    }
    
    // Question answering methods
    public void answerTeachingQualityQuestions(int q1Rating, int q2Rating, boolean q3Answer) {
        // Q1: Teaching quality rating
        page.locator(".question-1 .rating-stars").nth(q1Rating - 1).click();
        
        // Q2: Content delivery rating
        page.locator(".question-2 .rating-stars").nth(q2Rating - 1).click();
        
        // Q3: Yes/No question
        page.locator(String.format(".question-3 input[value='%s']", q3Answer ? "yes" : "no")).check();
        
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public void answerCommunicationQuestions(int q4Rating, int q5Rating, int q6Rating) {
        page.locator(".question-4 .rating-stars").nth(q4Rating - 1).click();
        page.locator(".question-5 .rating-stars").nth(q5Rating - 1).click();
        page.locator(".question-6 .rating-stars").nth(q6Rating - 1).click();
        page.waitForTimeout(1000);
    }
    
    public void answerPunctualityQuestions(int q7Rating, int q8Rating) {
        page.locator(".question-7 .rating-stars").nth(q7Rating - 1).click();
        page.locator(".question-8 .rating-stars").nth(q8Rating - 1).click();
        page.waitForTimeout(1000);
    }
    
    public void answerFairnessQuestions(int q9Rating, int q10Rating) {
        page.locator(".question-9 .rating-stars").nth(q9Rating - 1).click();
        page.locator(".question-10 .rating-stars").nth(q10Rating - 1).click();
        page.waitForTimeout(1000);
    }
    
    public void answerOpenEndedQuestions(String positiveComment, String suggestionComment) {
        page.locator(".question-11 textarea").fill(positiveComment);
        page.locator(".question-12 textarea").fill(suggestionComment);
        page.waitForTimeout(1000);
    }
    
    // Interface validation methods
    public boolean areRatingStarsWorking() {
        return ratingStars.first().isEnabled();
    }
    
    public boolean isProgressBarUpdated(int current, int total) {
        return page.locator(String.format("text='%d/%d'", current, total)).isVisible();
    }
    
    public boolean isAutoSaveIndicatorActive() {
        return autoSaveIndicator.isVisible();
    }
    
    public boolean isConsistentRatingInterface() {
        return ratingInterface.count() > 0 && 
               page.locator(".rating-stars").count() >= 10; // At least 10 rating questions
    }
    
    public boolean areTextAreasWorking() {
        return textAreas.first().isEnabled();
    }
    
    public boolean isCharacterLimitIndicatorVisible() {
        return characterLimitIndicator.isVisible();
    }
    
    public boolean isProgressBarCompleted(int total) {
        return page.locator(String.format("text='%d/%d'", total, total)).isVisible();
    }
    
    // Review and submission methods
    public void reviewAnswers() {
        page.locator("button:has-text('Review'), .review-button").click();
        page.waitForSelector(".review-section, .answer-review");
    }
    
    public boolean areAllAnswersDisplayedCorrectly() {
        return reviewSection.isVisible();
    }
    
    public boolean isAnonymousSubmissionReminderVisible() {
        return anonymousSubmissionReminder.isVisible();
    }
    
    public boolean isSubmitFeedbackButtonEnabled() {
        return submitFeedbackButton.isEnabled();
    }
    
    public boolean isEditAnswersOptionAvailable() {
        return editAnswersOption.isVisible();
    }
    
    public void submitFeedback() {
        submitFeedbackButton.click();
        page.waitForSelector(".confirmation-modal, .modal:has-text('Confirm')");
        page.locator("button:has-text('Confirm Submit'), button[data-action='confirm']").click();
    }
    
    public boolean isConfirmationModalVisible() {
        return confirmationModal.isVisible();
    }
    
    public boolean isAnonymousSubmissionMessageVisible() {
        return page.locator(".anonymous-message, text='anonymous'").isVisible();
    }
    
    public boolean isThankYouMessageVisible() {
        return thankYouMessage.isVisible();
    }
    
    public boolean isCampaignStatusCompleted() {
        return page.locator(".campaign-status:has-text('Completed'), .status:has-text('Completed')").isVisible();
    }
    
    public boolean canResubmitSameCampaign() {
        return page.locator("button:has-text('Start Feedback')").isEnabled();
    }
    
    // Parent notification methods
    public boolean isTeacherChangeNotificationVisible() {
        return teacherChangeNotification.isVisible();
    }
    
    public boolean isSubstituteTeacherInfoVisible(String teacherName) {
        return page.locator(String.format("text='%s'", teacherName)).isVisible();
    }
    
    public boolean isChangeDurationVisible(String duration) {
        return page.locator(String.format("text='%s'", duration)).isVisible();
    }
    
    public boolean isFeedbackLinkVisible() {
        return feedbackLink.isVisible();
    }
    
    public void clickAnonymousFeedbackLink() {
        feedbackLink.click();
        page.waitForLoadState();
    }
    
    public boolean isAnonymousFeedbackFormOpened() {
        return anonymousFeedbackForm.isVisible();
    }
    
    public boolean isNoLoginRequired() {
        return !page.locator("input[name='username'], #login-form").isVisible();
    }
    
    public boolean areClearInstructionsProvided() {
        return page.locator(".instructions, .form-instructions").isVisible();
    }
    
    public boolean isChildClassContextShown(String className) {
        return page.locator(String.format("text='%s'", className)).isVisible();
    }
    
    // Parent feedback methods
    public void rateNotificationTiming(int rating) {
        page.locator(".notification-timing .rating-stars").nth(rating - 1).click();
    }
    
    public void rateInformationClarity(boolean clear) {
        page.locator(String.format("input[name='informationClarity'][value='%s']", clear ? "yes" : "no")).check();
    }
    
    public void rateAdvanceNoticeSufficiency(boolean sufficient) {
        page.locator(String.format("input[name='advanceNotice'][value='%s']", sufficient ? "yes" : "no")).check();
    }
    
    public boolean isRatingInterfaceWorking() {
        return ratingStars.first().isEnabled();
    }
    
    public void addParentComments(String comments) {
        parentComments.fill(comments);
    }
    
    public boolean isTextFieldAcceptingInput() {
        return parentComments.isEnabled();
    }
    
    public boolean isCharacterLimitAppropriate() {
        return page.locator(".char-limit:has-text('500'), .char-limit:has-text('1000')").isVisible();
    }
    
    public boolean isOptionalNatureClear() {
        return page.locator("text='Optional', .optional-indicator").isVisible();
    }
    
    public void submitParentFeedback() {
        page.locator("button:has-text('Submit Feedback'), button[data-action='submit-parent']").click();
        page.waitForSelector(".success-message, .thank-you");
    }
    
    public boolean isSubmissionSuccessful() {
        return page.locator(".success-message, .alert-success").isVisible();
    }
    
    public boolean isThankYouMessageDisplayed() {
        return thankYouMessage.isVisible();
    }
    
    public boolean isAnonymityConfirmed() {
        return page.locator("text='anonymous', .anonymity-confirmation").isVisible();
    }
    
    public boolean isNoPersonalInfoRetained() {
        return page.locator("text='no personal information', .privacy-notice").isVisible();
    }
}