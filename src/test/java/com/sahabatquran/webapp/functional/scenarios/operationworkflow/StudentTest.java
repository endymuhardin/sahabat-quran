package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.StudentFeedbackPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Student daily operations workflow tests.
 * 
 * This class focuses on testing student interactions during semester operations,
 * primarily anonymous feedback submission and teacher evaluations.
 */
@Slf4j
@DisplayName("AKH-HP: Student Daily Operations Happy Path Scenarios")
@Sql(scripts = "/sql/operation-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/operation-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class StudentTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("AKH-HP-002: Student - Submit Anonymous Feedback")
    void shouldSuccessfullySubmitAnonymousTeacherFeedback() {
        log.info("🚀 Starting AKH-HP-002: Student Anonymous Feedback Submission...");
        
        // Test data sesuai dokumentasi
        final String STUDENT_USERNAME = "siswa.ali";
        final String STUDENT_PASSWORD = "Welcome@YSQ2024";
        final String CAMPAIGN_TYPE = "Teacher Evaluation";
        final String TARGET_TEACHER = "Ustadz Ahmad - Tahsin 1 Class";
        final int TOTAL_QUESTIONS = 12;
        
        LoginPage loginPage = new LoginPage(page);
        StudentFeedbackPage feedbackPage = new StudentFeedbackPage(page);
        
        // Bagian 1: Access Feedback System
        log.info("📝 Bagian 1: Access Feedback System");
        
        loginAsStudent();
        
        // Student Login Verification
        page.waitForURL("**/dashboard**");
        assertTrue(feedbackPage.isStudentDashboardVisible(), "Student dashboard should be visible");
        assertTrue(feedbackPage.isFeedbackNotificationBadgeVisible(), "Notification badge for feedback should be visible");
        assertTrue(feedbackPage.isFeedbackMenuAvailable(), "Feedback menu should be available");
        
        // View Available Feedback
        feedbackPage.navigateToFeedback();
        assertTrue(feedbackPage.areActiveCampaignsVisible(), "Active feedback campaigns should be visible");
        assertTrue(feedbackPage.isTeacherEvaluationCampaignVisible(CAMPAIGN_TYPE), "Teacher Evaluation campaign should be visible");
        assertTrue(feedbackPage.isAnonymousBadgeVisible(), "Anonymous badge should be visible");
        assertTrue(feedbackPage.isStartFeedbackButtonAvailable(), "Start Feedback button should be available");
        
        // Start Feedback Session
        feedbackPage.startFeedbackSession(TARGET_TEACHER);
        assertTrue(feedbackPage.isFeedbackFormOpened(), "Feedback form should be opened");
        assertTrue(feedbackPage.isAnonymityNoticeVisible(), "Anonymity notice should be clearly visible");
        assertTrue(feedbackPage.isProgressIndicatorVisible(1, TOTAL_QUESTIONS), "Progress indicator should show 1/12");
        assertTrue(feedbackPage.areQuestionCategoriesVisible(), "Question categories should be visible");
        
        // Bagian 2: Complete Feedback Questions
        log.info("📝 Bagian 2: Complete Feedback Questions");
        
        // Answer Teaching Quality Questions (Q1-3)
        feedbackPage.answerTeachingQualityQuestions(4, 5, true); // Q1: 4/5, Q2: 5/5, Q3: Yes
        assertTrue(feedbackPage.areRatingStarsWorking(), "Rating stars should be functional");
        assertTrue(feedbackPage.isProgressBarUpdated(3, TOTAL_QUESTIONS), "Progress bar should show 3/12");
        assertTrue(feedbackPage.isAutoSaveIndicatorActive(), "Auto-save indicator should be active");
        
        // Answer Communication Questions (Q4-6)
        feedbackPage.answerCommunicationQuestions(4, 5, 4); // Q4: 4/5, Q5: 5/5, Q6: 4/5
        assertTrue(feedbackPage.isConsistentRatingInterface(), "Rating interface should be consistent");
        assertTrue(feedbackPage.isProgressBarUpdated(6, TOTAL_QUESTIONS), "Progress bar should show 6/12");
        
        // Answer Punctuality Questions (Q7-8)
        feedbackPage.answerPunctualityQuestions(4, 5); // Q7: 4/5, Q8: 5/5
        assertTrue(feedbackPage.isProgressBarUpdated(8, TOTAL_QUESTIONS), "Progress bar should show 8/12");
        
        // Answer Fairness Questions (Q9-10)
        feedbackPage.answerFairnessQuestions(5, 4); // Q9: 5/5, Q10: 4/5
        assertTrue(feedbackPage.isProgressBarUpdated(10, TOTAL_QUESTIONS), "Progress bar should show 10/12");
        
        // Bagian 3: Complete Open-ended Questions
        log.info("📝 Bagian 3: Complete Open-ended Questions");
        
        // Provide Constructive Feedback (Q11-12)
        String positiveComment = "Penjelasan tajwid sangat jelas dan sabar";
        String suggestionComment = "Mungkin bisa lebih banyak contoh praktik";
        
        feedbackPage.answerOpenEndedQuestions(positiveComment, suggestionComment);
        assertTrue(feedbackPage.areTextAreasWorking(), "Text areas should function properly");
        assertTrue(feedbackPage.isCharacterLimitIndicatorVisible(), "Character limit indicator should be visible");
        assertTrue(feedbackPage.isProgressBarCompleted(TOTAL_QUESTIONS), "Progress bar should show completion 12/12");
        
        // Bagian 4: Submit Feedback
        log.info("📝 Bagian 4: Submit Feedback");
        
        // Review Feedback Before Submit
        feedbackPage.reviewAnswers();
        assertTrue(feedbackPage.areAllAnswersDisplayedCorrectly(), "All answers should be displayed correctly");
        assertTrue(feedbackPage.isAnonymousSubmissionReminderVisible(), "Anonymous submission reminder should be visible");
        assertTrue(feedbackPage.isSubmitFeedbackButtonEnabled(), "Submit Feedback button should be enabled");
        assertTrue(feedbackPage.isEditAnswersOptionAvailable(), "Edit Answers option should be available");
        
        // Final Submission
        feedbackPage.submitFeedback();
        assertTrue(feedbackPage.isConfirmationModalVisible(), "Confirmation modal should be visible");
        assertTrue(feedbackPage.isAnonymousSubmissionMessageVisible(), "Anonymous submission message should be shown");
        assertTrue(feedbackPage.isThankYouMessageVisible(), "Thank you message should be visible");
        
        // Verify completion and redirect
        page.waitForURL("**/dashboard**");
        assertTrue(feedbackPage.isCampaignStatusCompleted(), "Campaign status should be changed to Completed");
        assertFalse(feedbackPage.canResubmitSameCampaign(), "Should not be able to re-submit for same campaign");
        
        log.info("✅ AKH-HP-002: Anonymous feedback submission completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-HP-008: Parent - Respond to Teacher Change Notification")
    void shouldSuccessfullyRespondToTeacherChangeNotification() {
        log.info("🚀 Starting AKH-HP-008: Parent Response to Teacher Change Notification...");
        
        // Test data sesuai dokumentasi - using anonymous access
        final String CHILD_NAME = "Ali Rahman";
        final String CLASS_NAME = "Tahsin 1 - Senin Pagi";
        final String ORIGINAL_TEACHER = "Ustadz Ahmad";
        final String SUBSTITUTE_TEACHER = "Ustadzah Siti";
        final String CHANGE_DURATION = "1 week";
        
        StudentFeedbackPage feedbackPage = new StudentFeedbackPage(page);
        
        // Bagian 1: Receive dan Review Notification
        log.info("📝 Bagian 1: Receive dan Review Notification");
        
        // Access Notification (simulating SMS/email link click)
        String anonymousFeedbackUrl = getBaseUrl() + "/feedback/anonymous?token=parent-notification-token";
        page.navigate(anonymousFeedbackUrl);
        
        // Verify notification content
        assertTrue(feedbackPage.isTeacherChangeNotificationVisible(), "Teacher change notification should be visible");
        assertTrue(feedbackPage.isSubstituteTeacherInfoVisible(SUBSTITUTE_TEACHER), "Substitute teacher info should be provided");
        assertTrue(feedbackPage.isChangeDurationVisible(CHANGE_DURATION), "Duration of substitution should be mentioned");
        assertTrue(feedbackPage.isFeedbackLinkVisible(), "Feedback link should be included");
        
        // Click Feedback Link
        feedbackPage.clickAnonymousFeedbackLink();
        assertTrue(feedbackPage.isAnonymousFeedbackFormOpened(), "Anonymous feedback form should open");
        assertTrue(feedbackPage.isNoLoginRequired(), "No login should be required");
        assertTrue(feedbackPage.areClearInstructionsProvided(), "Clear instructions should be provided");
        assertTrue(feedbackPage.isChildClassContextShown(CLASS_NAME), "Child's class context should be shown");
        
        // Bagian 2: Provide Feedback
        log.info("📝 Bagian 2: Provide Feedback");
        
        // Rate Notification Process
        feedbackPage.rateNotificationTiming(4); // "How satisfied with notification timing?" → 4/5
        feedbackPage.rateInformationClarity(true); // "Information provided clear?" → Yes
        feedbackPage.rateAdvanceNoticeSufficiency(true); // "Advance notice sufficient?" → Yes
        assertTrue(feedbackPage.isRatingInterfaceWorking(), "Rating interface should work properly");
        
        // Provide Additional Comments
        String parentComment = "Thank you for informing us promptly. Please let us know how Ali adjusts to the new teacher.";
        feedbackPage.addParentComments(parentComment);
        assertTrue(feedbackPage.isTextFieldAcceptingInput(), "Text field should accept input");
        assertTrue(feedbackPage.isCharacterLimitAppropriate(), "Character limit should be appropriate");
        assertTrue(feedbackPage.isOptionalNatureClear(), "Optional nature should be clear");
        
        // Bagian 3: Submit Feedback
        log.info("📝 Bagian 3: Submit Feedback");
        
        // Submit Parent Feedback
        feedbackPage.submitParentFeedback();
        assertTrue(feedbackPage.isSubmissionSuccessful(), "Submission should be successful");
        assertTrue(feedbackPage.isThankYouMessageDisplayed(), "Thank you message should be displayed");
        assertTrue(feedbackPage.isAnonymityConfirmed(), "Confirmation of anonymity should be shown");
        assertTrue(feedbackPage.isNoPersonalInfoRetained(), "No personally identifiable info should be retained");
        
        log.info("✅ AKH-HP-008: Parent response to teacher change notification completed successfully!");
    }
}