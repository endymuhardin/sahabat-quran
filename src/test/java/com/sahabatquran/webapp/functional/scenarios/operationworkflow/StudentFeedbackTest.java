package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.StudentFeedbackPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Student Feedback functionality tests.
 * Tests both happy path and alternate path scenarios for anonymous feedback submission.
 * 
 * Covers:
 * - AKH-HP-002: Student Anonymous Feedback Submission (Happy Path)
 * - AKH-AP-002: Duplicate Feedback Prevention (Alternate Path)
 * - AKH-AP-007: Technical Failure Handling (Alternate Path)
 */
@Slf4j
@DisplayName("Student Feedback Operations - Comprehensive Test Suite")
class StudentFeedbackTest extends BasePlaywrightTest {
    
    private StudentFeedbackPage feedbackPage;
    
    // Test data constants
    private static final String STUDENT_USERNAME = "siswa.ali";
    private static final String STUDENT_PASSWORD = "Welcome@YSQ2024";
    private static final String STUDENT_NAME = "Ali Rahman";
    private static final String TEACHER_NAME = "Ustadz Ahmad";
    private static final String CLASS_NAME = "Tahsin 1";
    
    @Test
    @DisplayName("AKH-HP-002: Student submits anonymous teacher evaluation feedback successfully")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSuccessfullySubmitAnonymousTeacherEvaluation() {
        log.info("🚀 Starting AKH-HP-002: Student Anonymous Feedback Submission");
        
        // Login as student
        loginAsStudent();
        
        // Initialize Page Object
        feedbackPage = new StudentFeedbackPage(page);
        
        // ========== BAGIAN 1: Access Feedback System ==========
        log.info("📝 Bagian 1: Access Feedback System");
        
        // Navigate to feedback dashboard  
        page.navigate(getBaseUrl() + "/student/feedback");
        page.waitForLoadState();
        
        // Wait for and verify dashboard elements
        page.waitForSelector("#feedback-dashboard-title", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertTrue(feedbackPage.isDashboardTitleVisible(), "Dashboard title should be visible");
        assertTrue(feedbackPage.isAnonymityNoticeDisplayed(), "Anonymity notice should be visible");
        
        // Check active campaigns
        assertTrue(feedbackPage.isActiveCampaignsVisible(), "Active campaigns should be visible");
        assertTrue(feedbackPage.isCampaignCardVisible("d4444444-4444-4444-4444-444444444444"), 
            "Teacher evaluation campaign should be visible");
        
        // Verify anonymous badge
        assertTrue(feedbackPage.isCampaignAnonymousBadgeVisible("d4444444-4444-4444-4444-444444444444"), 
            "Anonymous badge should be visible on campaign");
        
        // Start feedback session
        feedbackPage.startFeedbackSession();
        feedbackPage.waitForFeedbackForm();
        
        // ========== BAGIAN 2: Complete Feedback Form ==========
        log.info("📝 Bagian 2: Complete Feedback Form");
        
        // Verify form loaded with anonymity notice
        assertTrue(feedbackPage.isFeedbackFormTitleVisible(), "Feedback form title should be visible");
        assertTrue(feedbackPage.isFeedbackFormOpened(), "Feedback form should be visible");
        
    // Check progress indicator (ID-based only; template initializes to 1/total)
    assertEquals("1", feedbackPage.getProgressText());
        
        // Answer Question 1: Teaching Quality Rating (5 stars)
        log.info("Answering Q1: Teaching Quality");
        feedbackPage.answerQuestion1Rating(4); // 4/5 rating
        
        // Check progress updated
        assertEquals("1", feedbackPage.getProgressText());
        
        // Answer Question 2: Communication Skills (5 stars)
        log.info("Answering Q2: Communication Skills");
        feedbackPage.answerQuestion2Rating(5); // 5/5 rating
        assertEquals("2", feedbackPage.getProgressText());
        
        // Answer Question 3: Punctuality (Yes/No)
        log.info("Answering Q3: Punctuality");
        feedbackPage.answerQuestion3YesNo(true); // Yes
        assertEquals("3", feedbackPage.getProgressText());
        
        // Answer Question 4: Teaching Methods (Multiple Choice)
        log.info("Answering Q4: Teaching Methods");
        feedbackPage.answerQuestion4MultipleChoice(0); // "Sangat Baik"
        assertEquals("4", feedbackPage.getProgressText());
        
        // Answer Question 5: Open-ended positive feedback
        log.info("Answering Q5: Positive Feedback");
        feedbackPage.answerQuestion5Text("Penjelasan tajwid sangat jelas dan sabar. Ustadz selalu memberikan contoh yang mudah dipahami.");
        assertEquals("5", feedbackPage.getProgressText());
        
        // Answer Question 6: Suggestions for improvement
        log.info("Answering Q6: Suggestions");
        feedbackPage.answerQuestion6Text("Mungkin bisa lebih banyak praktik langsung dan quiz interaktif.");
        assertEquals("6", feedbackPage.getProgressText());
        
        // Wait for auto-save indicator (should appear after 2 seconds of inactivity)
        feedbackPage.waitForTimeout(2500);
        if (feedbackPage.isAutoSaveIndicatorVisible()) {
            log.info("✓ Auto-save triggered successfully");
        }
        
        // Complete remaining questions with sample data
        feedbackPage.answerRemainingRequiredQuestions();
        feedbackPage.answerOptionalQuestions();
        
        // Verify all questions answered
        assertEquals("12", feedbackPage.getProgressText());
        assertTrue(feedbackPage.getProgressPercentage().contains("100%"));
        
        // ========== BAGIAN 3: Submit Feedback ==========
        log.info("📝 Bagian 3: Submit Feedback");
        
        // Click submit button
        feedbackPage.clickSubmitButton();
        
        // Handle confirmation if it appears
        if (feedbackPage.isConfirmationModalVisible()) {
            feedbackPage.clickConfirmSubmit();
        }
        
        // Wait for submission and redirect to confirmation page
        feedbackPage.waitForConfirmationPage();
        
        // Verify confirmation page
        assertTrue(feedbackPage.getFeedbackFormTitle().contains("Feedback Berhasil Dikirim"));
        assertTrue(feedbackPage.isSuccessIconVisible(), "Success icon should be visible");
        
        // Check confirmation details
        assertTrue(feedbackPage.isSuccessTitleVisible());
        
        // Verify anonymous notice on confirmation
        assertTrue(feedbackPage.isAnonymityMessageInConfirmation());
        
        // Return to dashboard
        feedbackPage.clickBackToDashboard();
        feedbackPage.waitForURL("**/student/feedback");
        
        // Verify campaign now shows as completed
        if (feedbackPage.isCampaignCardPresent("Teacher Evaluation")) {
            assertTrue(feedbackPage.isCampaignCompleted(),
                      "Campaign should show as completed or hide start button");
        }
        
        log.info("✅ AKH-HP-002: Anonymous feedback submission completed successfully!");
    }
    
    @Test
    @DisplayName("AKH-AP-002: System prevents duplicate feedback submission")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldPreventDuplicateFeedbackSubmission() {
        log.info("🚀 Starting AKH-AP-002: Duplicate Feedback Prevention Test");
        
        // First, submit a feedback successfully
        submitInitialFeedback();
        
        // Now attempt to submit again
        log.info("📝 Attempting duplicate submission");
        
        // Navigate back to feedback dashboard
        page.navigate(getBaseUrl() + "/student/feedback");
        
        // Try to access the same campaign
        if (feedbackPage.isCampaignCardPresent("Teacher Evaluation")) {
            // If visible, should not have start button or should be disabled
            assertFalse(feedbackPage.isStartButtonVisibleOrEnabled(), 
                "Start button should be hidden or disabled for completed campaign");
        }
        
        // Try direct URL access to the campaign
        feedbackPage.navigateDirectToCampaign(getBaseUrl(), "d4444444-4444-4444-4444-444444444444");
        
        // Should be redirected to already-submitted page
        feedbackPage.waitForAlreadySubmittedPage();
        
        // Verify the already submitted message
        assertTrue(feedbackPage.isAlreadySubmittedTitleVisible());
        assertTrue(feedbackPage.isAlreadySubmittedMessageVisible());
        
        // Check for anonymity explanation
        assertTrue(feedbackPage.isAnonymousSubmissionExplanationVisible());
        
        // Verify options provided
        assertTrue(feedbackPage.isBackToDashboardButtonVisible());
        assertTrue(feedbackPage.isReopenExplanationVisible());
        
        log.info("✅ AKH-AP-002: Duplicate submission prevention working correctly!");
    }
    
    @Test
    @DisplayName("AKH-AP-007: System handles technical failures gracefully")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleTechnicalFailuresGracefully() {
        log.info("🚀 Starting AKH-AP-007: Technical Failure Handling Test");
        
        loginAsStudent();
        
        // Initialize page object and navigate to feedback form
        feedbackPage = new StudentFeedbackPage(page);
        page.navigate(getBaseUrl() + "/student/feedback");
        feedbackPage.startFeedbackSession();
        
        // Answer first few questions
        log.info("📝 Answering initial questions");
        feedbackPage.answerFirstRequiredRatingQuestion(); // Q1
        feedbackPage.answerQuestion2Rating(5); // Q2
        
        // Wait for auto-save to complete before going offline
        log.info("📝 Waiting for auto-save to complete");
        feedbackPage.waitForTimeout(2500); // Wait longer than the 2000ms auto-save delay
        
        // Simulate network interruption
        log.info("📝 Simulating network interruption");
        page.context().setOffline(true);
        
        // Try to answer another question
        feedbackPage.answerQuestion3YesNo(true);
        
        // Auto-save should fail but not crash
        feedbackPage.waitForTimeout(2500);
        
        // Re-enable network
        page.context().setOffline(false);
        
        // Refresh the page
        log.info("📝 Testing session recovery after refresh");
        page.reload();
        
        // Check if resume data is loaded
        if (feedbackPage.isResumeNoticePresent()) {
            log.info("✓ Resume notice displayed");
        }
        
        // Verify previously answered questions are restored
        assertTrue(feedbackPage.isQuestion1RatingActive(3),
            "First question answer should be restored");
        
        // Complete the feedback with remaining questions only (Q1-Q2 should be restored)
        log.info("📝 Completing feedback after recovery - answering remaining questions");
        feedbackPage.answerRemainingQuestionsFromQ3();
        
        // Submit successfully
        feedbackPage.clickSubmitButton();
        feedbackPage.waitForConfirmationPage();
        
        assertTrue(feedbackPage.getFeedbackFormTitle().contains("Feedback Berhasil Dikirim"));
        
        log.info("✅ AKH-AP-007: Technical failure handling successful!");
    }
    
    @Test
    @DisplayName("Feedback form validates required fields before submission")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldValidateRequiredFieldsBeforeSubmission() {
        log.info("🚀 Starting required field validation test");
        
        loginAsStudent();
        
        // Initialize page object and navigate to feedback form
        feedbackPage = new StudentFeedbackPage(page);
        page.navigate(getBaseUrl() + "/student/feedback");
        feedbackPage.startFeedbackSession();
        
        // Try to submit without answering required questions
        feedbackPage.clickSubmitButton();
        
        // Should show validation error
        feedbackPage.waitForValidationError();
        
        // Answer only required questions
        feedbackPage.answerAllRequiredQuestions();
        
        // Now submission should work
        feedbackPage.clickSubmitButton();
        feedbackPage.waitForConfirmationPage();
        
        log.info("✅ Required field validation working correctly!");
    }
    
    @Test
    @DisplayName("Auto-save preserves feedback progress during long sessions")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAutoSaveFeedbackProgress() {
        log.info("🚀 Starting auto-save functionality test");
        
        loginAsStudent();
        
        // Initialize page object and navigate to feedback form
        feedbackPage = new StudentFeedbackPage(page);
        page.navigate(getBaseUrl() + "/student/feedback");
        feedbackPage.startFeedbackSession();
        
        // Answer a question
        feedbackPage.answerFirstRequiredRatingQuestion();
        
        // Wait for auto-save (2 seconds of inactivity)
        feedbackPage.waitForTimeout(2500);
        
        // Check for auto-save indicator
        assertTrue(feedbackPage.isAutoSaveIndicatorDisplayed(), "Auto-save indicator should be displayed");
        assertTrue(feedbackPage.getAutoSaveMessage().contains("Progress tersimpan otomatis"), "Auto-save message should be correct");
        
        // Navigate away and come back
        page.navigate(getBaseUrl() + "/student/feedback");
        feedbackPage.startFeedbackSession();
        
        // Check if previous answer is preserved
        assertTrue(feedbackPage.isQuestion1RatingActive(3), "Previously selected rating should be preserved");
        
        log.info("✅ Auto-save functionality working correctly!");
    }
    
    // Helper methods
    
    private void submitInitialFeedback() {
        loginAsStudent();
        feedbackPage = new StudentFeedbackPage(page);
        page.navigate(getBaseUrl() + "/student/feedback");
        
        // Quick submission for testing duplicate prevention
        feedbackPage.waitForStartFeedbackButton();
        feedbackPage.startFeedbackSession();
        
        // Answer all questions quickly
        feedbackPage.answerAllQuestionsQuickly();
        
        feedbackPage.clickSubmitButton();
        feedbackPage.waitForConfirmationPage();
    }
}