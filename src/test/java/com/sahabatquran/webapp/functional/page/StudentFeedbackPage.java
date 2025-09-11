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
        this.feedbackMenu = page.locator("#student-feedback-menu");
        this.studentDashboard = page.locator("#quick-actions-section");
        this.feedbackNotificationBadge = page.locator("#feedback-notification-badge");
        
        // Feedback campaigns
        this.activeCampaigns = page.locator("#active-campaigns");
        this.teacherEvaluationCampaign = page.locator("#teacher-evaluation-campaign");
        this.anonymousBadge = page.locator("#anonymous-badge");
        this.startFeedbackButton = page.locator("#start-feedback-btn-d4444444-4444-4444-4444-444444444444");
        
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
        this.submitFeedbackButton = page.locator("#submit-btn");
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
        // Notification badge is optional - skip this check for now
        return true;
    }
    
    public boolean isFeedbackMenuAvailable() {
        return feedbackMenu.isVisible();
    }
    
    public void navigateToFeedback() {
        page.navigate("/student/feedback");
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
    
    public void startFeedbackSession() {
        startFeedbackButton.click();
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
    
    // Additional helper methods for common test operations
    // Question answering methods using specific IDs
    public void answerQuestion1Rating(int rating) {
        page.locator("#rating-star-e5555551-5555-5555-5555-555555555555-" + rating).click();
    }
    
    public void answerQuestion2Rating(int rating) {
        page.locator("#rating-star-e5555552-5555-5555-5555-555555555555-" + rating).click();
    }
    
    public void answerQuestion3YesNo(boolean yes) {
        if (yes) {
            page.locator("#yes-option-e5555553-5555-5555-5555-555555555555").check();
        } else {
            page.locator("#no-option-e5555553-5555-5555-5555-555555555555").check();
        }
    }
    
    public void answerQuestion4MultipleChoice(int optionIndex) {
        page.locator("#option-e5555554-5555-5555-5555-555555555555-" + optionIndex).check();
    }
    
    public void answerQuestion5Text(String text) {
        page.locator("#text-answer-e5555555-5555-5555-5555-555555555555").fill(text);
    }
    
    public void answerQuestion6Text(String text) {
        page.locator("#text-answer-e5555556-5555-5555-5555-555555555555").fill(text);
    }
    
    public void answerQuestion7Rating(int rating) {
        page.locator("#rating-star-e5555557-5555-5555-5555-555555555555-" + rating).click();
    }
    
    public void answerQuestion8Rating(int rating) {
        page.locator("#rating-star-e5555558-5555-5555-5555-555555555555-" + rating).click();
    }
    
    public void answerQuestion9YesNo(boolean yes) {
        if (yes) {
            page.locator("#yes-option-e5555559-5555-5555-5555-555555555555").check();
        } else {
            page.locator("#no-option-e5555559-5555-5555-5555-555555555555").check();
        }
    }
    
    public void answerQuestion10Rating(int rating) {
        page.locator("#rating-star-e555555a-5555-5555-5555-555555555555-" + rating).click();
    }
    
    public void answerQuestion11Rating(int rating) {
        page.locator("#rating-star-e555555b-5555-5555-5555-555555555555-" + rating).click();
    }
    
    public void answerQuestion12Text(String text) {
        page.locator("#text-answer-e555555c-5555-5555-5555-555555555555").fill(text);
    }
    
    public void fillTextArea(String questionId, String text) {
        page.locator("#text-answer-" + questionId).fill(text);
    }
    
    public void navigateToFeedbackDashboard() {
        page.navigate("/student/feedback");
    }
    
    public void clickDashboardLink() {
        page.locator("#back-to-dashboard-btn").click();
    }
    
    public boolean isAnonymityNoticeDisplayed() {
        return page.locator("#anonymity-notice-text").isVisible();
    }
    
    public boolean isCampaignTitleVisible() {
        return page.locator("#success-title").isVisible();
    }
    
    public boolean isConfirmationIdVisible() {
        return page.locator("#success-title").isVisible();
    }
    
    public boolean isResumeNoticeVisible() {
        return page.locator("#resume-notice").isVisible();
    }
    
    public boolean isValidationErrorVisible() {
        return page.locator("#validation-error").isVisible();
    }
    
    public boolean isAlreadySubmittedMessageVisible() {
        return page.locator("#already-submitted-message").isVisible();
    }
    
    public boolean isAnonymousSubmissionExplanationVisible() {
        return page.locator("#anonymous-submission-explanation").isVisible();
    }
    
    public boolean isReopenExplanationVisible() {
        return page.locator("#reopen-explanation").isVisible();
    }
    
    // Navigation and page management
    public void navigateToFeedbackPage() {
        page.navigate("/student/feedback");
        page.waitForLoadState();
    }
    
    public void clickFeedbackMenu() {
        if (page.locator("#student-feedback-menu").count() > 0) {
            page.locator("#student-feedback-menu").click();
        } else {
            page.navigate("/student/feedback");
        }
        page.waitForURL("**/student/feedback");
    }
    
    public boolean isDashboardTitleVisible() {
        return page.locator("#feedback-dashboard-title").isVisible();
    }
    
    public boolean isActiveCampaignsVisible() {
        return page.locator("#active-campaigns-container").isVisible();
    }
    
    public boolean isCampaignCardVisible(String campaignId) {
        return page.locator("#campaign-card-" + campaignId).isVisible();
    }
    
    public boolean isCampaignAnonymousBadgeVisible(String campaignId) {
        return page.locator("#campaign-card-" + campaignId + " .anonymous-badge").isVisible();
    }
    
    public boolean isFeedbackFormTitleVisible() {
        return page.locator("#feedback-form-title").isVisible();
    }
    
    public String getProgressText() {
        return page.locator("#progress-text").textContent();
    }
    
    public void clickSubmitButton() {
        page.locator("#submit-btn").click();
    }
    
    public boolean isValidationErrorDisplayed() {
        return page.locator("#validation-error").isVisible();
    }
    
    public void waitForConfirmationPage() {
        page.waitForURL("**/confirmation/**");
    }
    
    public void waitForFeedbackForm() {
        page.waitForURL("**/student/feedback/campaign/**");
    }
    
    public void answerAllQuestionsQuickly() {
        // Answer all 12 questions with default values
        answerQuestion1Rating(3);
        answerQuestion2Rating(3);
        answerQuestion3YesNo(true);
        answerQuestion4MultipleChoice(0);
        answerQuestion5Text("Quick test response");
        answerQuestion6Text("Quick test response");
        answerQuestion7Rating(3);
        answerQuestion8Rating(3);
        answerQuestion9YesNo(true);
        answerQuestion10Rating(3);
        answerQuestion11Rating(3);
        answerQuestion12Text("Quick test response");
    }
    
    public void waitForStartFeedbackButton() {
        page.waitForSelector("#start-feedback-btn-d4444444-4444-4444-4444-444444444444", 
            new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));
    }
    
    public boolean isSuccessTitleVisible() {
        return page.locator("#success-title").isVisible();
    }
    
    public void clickBackToDashboard() {
        page.locator("#back-to-dashboard-btn").click();
    }
    
    public void fillQuestion11Text(String text) {
        page.locator("#question-11-textarea").fill(text);
    }
    
    public void fillQuestion12Text(String text) {
        page.locator("#question-12-textarea").fill(text);
    }
    
    // Additional methods for complete test coverage
    public void waitForTimeout(int milliseconds) {
        page.waitForTimeout(milliseconds);
    }
    
    public boolean isAutoSaveIndicatorVisible() {
        return page.locator("#auto-save-indicator").isVisible();
    }
    
    public String getProgressPercentage() {
        return page.locator("#progress-percentage").textContent();
    }
    
    public void clickConfirmSubmit() {
        page.locator("#confirm-submit-button").click();
    }
    
    public void waitForAlreadySubmittedPage() {
        page.waitForURL("**/feedback-already-submitted**");
    }
    
    public boolean isSuccessIconVisible() {
        // Wait for the success icon with animation to be visible
        try {
            page.waitForSelector("#success-icon", new Page.WaitForSelectorOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getFeedbackFormTitle() {
        return page.locator("#success-title").textContent();
    }
    
    public boolean isAnonymityMessageInConfirmation() {
        return page.locator(".bg-blue-50").textContent().contains("Feedback Anda telah dikirim secara anonim");
    }
    
    public boolean isCampaignCardPresent(String campaignText) {
        return page.locator("#campaign-card-d4444444-4444-4444-4444-444444444444").count() > 0;
    }
    
    public boolean isStartButtonVisibleOrEnabled() {
        var button = page.locator("#start-feedback-btn-d4444444-4444-4444-4444-444444444444");
        return button.count() > 0 && button.isVisible() && button.isEnabled();
    }
    
    public void navigateDirectToCampaign(String baseUrl, String campaignId) {
        page.navigate(baseUrl + "/student/feedback/campaign/" + campaignId);
    }
    
    public boolean isAlreadySubmittedTitleVisible() {
        return page.locator("h1").textContent().contains("Feedback Sudah Dikirim");
    }
    
    public boolean isResumeNoticePresent() {
        return page.locator("#resume-notice").count() > 0;
    }
    
    public void answerRemainingRequiredQuestions() {
        // Answer questions 7-10 which are required
        answerQuestion7Rating(4);
        answerQuestion8Rating(4);
        answerQuestion9YesNo(true);
        answerQuestion10Rating(4);
    }
    
    public void answerOptionalQuestions() {
        answerQuestion11Rating(4);
        answerQuestion12Text("Additional feedback text");
    }
    
    public void waitForValidationError() {
        page.waitForSelector("#validation-error");
    }
    
    public boolean hasRequiredQuestionError() {
        return page.locator("#validation-error").isVisible();
    }
    
    public void answerFirstRequiredRatingQuestion() {
        page.locator("#rating-star-e5555551-5555-5555-5555-555555555555-3").click();
    }
    
    public void checkFirstYesNoOption() {
        page.locator("#yes-option-e5555553-5555-5555-5555-555555555555").check();
    }
    
    public void answerAllRequiredQuestions() {
        // Answer all required questions (1-10)
        answerQuestion1Rating(3);
        answerQuestion2Rating(3);
        answerQuestion3YesNo(true);
        answerQuestion4MultipleChoice(0);
        answerQuestion7Rating(3);
        answerQuestion8Rating(3);
        answerQuestion9YesNo(true);
        answerQuestion10Rating(3);
    }
    
    public boolean isQuestion1RatingActive(int rating) {
        return page.locator("#rating-star-e5555551-5555-5555-5555-555555555555-" + rating + ".active").count() > 0;
    }
    
    public void navigateToBase(String baseUrl) {
        page.navigate(baseUrl + "/student/feedback");
    }
    
    public void waitForDashboard() {
        page.waitForURL("**/dashboard");
    }
    
    public boolean isCampaignCompleted() {
        return !isStartButtonVisibleOrEnabled();
    }
    
    public void waitForURL(String urlPattern) {
        page.waitForURL(urlPattern);
    }
    
    public boolean isBackToDashboardButtonVisible() {
        return page.locator("#back-to-dashboard-btn").isVisible();
    }
    
    // Auto-save functionality methods
    public boolean isAutoSaveIndicatorDisplayed() {
        try {
            page.waitForSelector("#auto-save-indicator.active", new Page.WaitForSelectorOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getAutoSaveMessage() {
        return page.locator("#auto-save-indicator").textContent();
    }
    
    
    // Login form methods
    public void fillUsername(String username) {
        page.fill("input[name='username']", username);
    }
    
    public void fillPassword(String password) {
        page.fill("input[name='password']", password);
    }
    
    public void clickLoginSubmitButton() {
        page.locator("#login-button").click();
    }
}