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
        this.feedbackMenu = page.locator("#feedback-menu");
        this.studentDashboard = page.locator("#student-dashboard");
        this.feedbackNotificationBadge = page.locator("#feedback-notification-badge");
        
        // Feedback campaigns
        this.activeCampaigns = page.locator("#active-campaigns");
        this.teacherEvaluationCampaign = page.locator("#teacher-evaluation-campaign");
        this.anonymousBadge = page.locator("#anonymous-badge");
        this.startFeedbackButton = page.locator("#start-feedback-button");
        
        // Feedback form
        this.feedbackForm = page.locator("#feedback-form");
        this.anonymityNotice = page.locator("#anonymity-notice");
        this.progressIndicator = page.locator("#progress-indicator");
        this.questionCategories = page.locator("#question-categories");
        this.progressBar = page.locator("#progress-bar");
        this.autoSaveIndicator = page.locator("#auto-save-indicator");
        
        // Rating interface
        this.ratingStars = page.locator("#rating-stars");
        this.ratingInterface = page.locator("#rating-interface");
        this.textAreas = page.locator("textarea[id^='feedback-'], textarea[id^='comment-']");
        this.characterLimitIndicator = page.locator("#character-limit-indicator");
        
        // Review and submission
        this.reviewSection = page.locator("#review-section");
        this.anonymousSubmissionReminder = page.locator("#anonymous-submission-reminder");
        this.submitFeedbackButton = page.locator("#submit-feedback-button");
        this.editAnswersOption = page.locator("#edit-answers-button");
        this.confirmationModal = page.locator("#confirmation-modal");
        this.thankYouMessage = page.locator("#thank-you-message");
        
        // Parent notification
        this.teacherChangeNotification = page.locator("#teacher-change-notification");
        this.substituteTeacherInfo = page.locator("#substitute-teacher-info");
        this.changeDuration = page.locator("#change-duration");
        this.feedbackLink = page.locator("#feedback-link");
        this.anonymousFeedbackForm = page.locator("#anonymous-feedback-form");
        this.parentComments = page.locator("#parent-comments");
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
        return page.locator(String.format("#campaign-%s", campaignType.toLowerCase().replaceAll(" ", "-"))).isVisible();
    }
    
    public boolean isAnonymousBadgeVisible() {
        return anonymousBadge.isVisible();
    }
    
    public boolean isStartFeedbackButtonAvailable() {
        return startFeedbackButton.isEnabled();
    }
    
    public void startFeedbackSession(String targetTeacher) {
        page.locator(String.format("#campaign-%s", targetTeacher.toLowerCase().replaceAll(" ", "-")))
            .locator("#start-campaign-button").click();
        page.waitForSelector("#feedback-form");
    }
    
    // Feedback form methods
    public boolean isFeedbackFormOpened() {
        return feedbackForm.isVisible();
    }
    
    public boolean isAnonymityNoticeVisible() {
        return anonymityNotice.isVisible();
    }
    
    public boolean isProgressIndicatorVisible(int current, int total) {
        return page.locator("#progress-indicator").textContent().contains(String.format("%d/%d", current, total));
    }
    
    public boolean areQuestionCategoriesVisible() {
        return questionCategories.isVisible();
    }
    
    // Question answering methods
    public void answerTeachingQualityQuestions(int q1Rating, int q2Rating, boolean q3Answer) {
        // Q1: Teaching quality rating
        page.locator("#question-1-rating").nth(q1Rating - 1).click();
        
        // Q2: Content delivery rating
        page.locator("#question-2-rating").nth(q2Rating - 1).click();
        
        // Q3: Yes/No question
        page.locator(String.format("#question-3-%s", q3Answer ? "yes" : "no")).check();
        
        page.waitForTimeout(1000); // Allow for auto-save
    }
    
    public void answerCommunicationQuestions(int q4Rating, int q5Rating, int q6Rating) {
        page.locator("#question-4-rating").nth(q4Rating - 1).click();
        page.locator("#question-5-rating").nth(q5Rating - 1).click();
        page.locator("#question-6-rating").nth(q6Rating - 1).click();
        page.waitForTimeout(1000);
    }
    
    public void answerPunctualityQuestions(int q7Rating, int q8Rating) {
        page.locator("#question-7-rating").nth(q7Rating - 1).click();
        page.locator("#question-8-rating").nth(q8Rating - 1).click();
        page.waitForTimeout(1000);
    }
    
    public void answerFairnessQuestions(int q9Rating, int q10Rating) {
        page.locator("#question-9-rating").nth(q9Rating - 1).click();
        page.locator("#question-10-rating").nth(q10Rating - 1).click();
        page.waitForTimeout(1000);
    }
    
    public void answerOpenEndedQuestions(String positiveComment, String suggestionComment) {
        page.locator("#question-11-textarea").fill(positiveComment);
        page.locator("#question-12-textarea").fill(suggestionComment);
        page.waitForTimeout(1000);
    }
    
    // Interface validation methods
    public boolean areRatingStarsWorking() {
        return ratingStars.first().isEnabled();
    }
    
    public boolean isProgressBarUpdated(int current, int total) {
        return page.locator("#progress-indicator").textContent().contains(String.format("%d/%d", current, total));
    }
    
    public boolean isAutoSaveIndicatorActive() {
        return autoSaveIndicator.isVisible();
    }
    
    public boolean isConsistentRatingInterface() {
        return ratingInterface.count() > 0 && 
               page.locator("[id^='question-'][id$='-rating']").count() >= 10; // At least 10 rating questions
    }
    
    public boolean areTextAreasWorking() {
        return textAreas.first().isEnabled();
    }
    
    public boolean isCharacterLimitIndicatorVisible() {
        return characterLimitIndicator.isVisible();
    }
    
    public boolean isProgressBarCompleted(int total) {
        return page.locator("#progress-indicator").textContent().contains(String.format("%d/%d", total, total));
    }
    
    // Review and submission methods
    public void reviewAnswers() {
        page.locator("#review-answers-button").click();
        page.waitForSelector("#review-section");
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
        page.waitForSelector("#confirmation-modal");
        page.locator("#confirm-submit-button").click();
    }
    
    public boolean isConfirmationModalVisible() {
        return confirmationModal.isVisible();
    }
    
    public boolean isAnonymousSubmissionMessageVisible() {
        return page.locator("#anonymous-submission-message").isVisible();
    }
    
    public boolean isThankYouMessageVisible() {
        return thankYouMessage.isVisible();
    }
    
    public boolean isCampaignStatusCompleted() {
        return page.locator("#campaign-status").textContent().contains("Completed");
    }
    
    public boolean canResubmitSameCampaign() {
        return page.locator("#start-feedback-button").isEnabled();
    }
    
    // Parent notification methods
    public boolean isTeacherChangeNotificationVisible() {
        return teacherChangeNotification.isVisible();
    }
    
    public boolean isSubstituteTeacherInfoVisible(String teacherName) {
        return page.locator("#substitute-teacher-info").textContent().contains(teacherName);
    }
    
    public boolean isChangeDurationVisible(String duration) {
        return page.locator("#change-duration").textContent().contains(duration);
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
        return !page.locator("#login-form").isVisible();
    }
    
    public boolean areClearInstructionsProvided() {
        return page.locator("#form-instructions").isVisible();
    }
    
    public boolean isChildClassContextShown(String className) {
        return page.locator("#child-class-context").textContent().contains(className);
    }
    
    // Parent feedback methods
    public void rateNotificationTiming(int rating) {
        page.locator("#notification-timing-rating").nth(rating - 1).click();
    }
    
    public void rateInformationClarity(boolean clear) {
        page.locator(String.format("#information-clarity-%s", clear ? "yes" : "no")).check();
    }
    
    public void rateAdvanceNoticeSufficiency(boolean sufficient) {
        page.locator(String.format("#advance-notice-%s", sufficient ? "yes" : "no")).check();
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
        String limitText = page.locator("#character-limit-indicator").textContent();
        return limitText.contains("500") || limitText.contains("1000");
    }
    
    public boolean isOptionalNatureClear() {
        return page.locator("#optional-indicator").isVisible();
    }
    
    public void submitParentFeedback() {
        page.locator("#submit-parent-feedback-button").click();
        page.waitForSelector("#success-message");
    }
    
    public boolean isSubmissionSuccessful() {
        return page.locator("#success-message").isVisible();
    }
    
    public boolean isThankYouMessageDisplayed() {
        return thankYouMessage.isVisible();
    }
    
    public boolean isAnonymityConfirmed() {
        return page.locator("#anonymity-confirmation").isVisible();
    }
    
    public boolean isNoPersonalInfoRetained() {
        return page.locator("#privacy-notice").isVisible();
    }
}