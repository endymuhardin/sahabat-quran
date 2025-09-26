package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import com.microsoft.playwright.Page;
import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import com.sahabatquran.webapp.functional.page.StudentFeedbackPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

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
        log.info("Question answered, waiting for auto-save indicator...");
        feedbackPage.waitForAutoSaveIndicator(); // Use the dedicated method
        log.info("DEBUG: waitForAutoSaveIndicator() completed");
        
        if (feedbackPage.isAutoSaveIndicatorVisible()) {
            log.info("✓ Auto-save triggered successfully");
        } else {
            log.info("Auto-save indicator not visible (this may be okay if auto-save is working in background)");
        }
        log.info("DEBUG: Auto-save check completed");
        
        // Complete remaining questions with sample data
        log.info("DEBUG: Starting answerRemainingRequiredQuestions()");
        feedbackPage.answerRemainingRequiredQuestions();
        log.info("DEBUG: answerRemainingRequiredQuestions() completed");
        
        log.info("DEBUG: Starting answerOptionalQuestions()");
        feedbackPage.answerOptionalQuestions();
        log.info("DEBUG: answerOptionalQuestions() completed");
        
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
        log.info("Student login completed");
        
        // Initialize page object and navigate to feedback form
        feedbackPage = new StudentFeedbackPage(page);
        log.info("StudentFeedbackPage initialized");
        
        page.navigate(getBaseUrl() + "/student/feedback");
        log.info("Navigated to feedback dashboard");
        
        feedbackPage.startFeedbackSession();
        log.info("Started feedback session");
        
        // Verify enhanced progress grid exists (indicates auto-save is enabled)
        assertTrue(feedbackPage.hasEnhancedProgressGrid(), 
            "Enhanced progress grid should be present");
        log.info("✓ Enhanced progress grid verified");
        
        // Answer a question to trigger auto-save
        log.info("Answering first required rating question");
        feedbackPage.answerFirstRequiredRatingQuestion();
        log.info("First question answered");
        
        // Verify the question was answered using page object method
        String questionId = "e5555551-5555-5555-5555-555555555555";
        String expectedValue = "3";
        assertTrue(feedbackPage.isQuestionAnswered(questionId, expectedValue), 
            "Question should be answered before testing auto-save");
        log.info("✓ Question answer verified in form");
        
        // Wait for auto-save to complete using page object method
        log.info("Waiting for auto-save completion...");
        
        // Verify the progress item exists
        assertTrue(feedbackPage.progressItemExists(questionId), 
            "Progress item should exist for question");
        log.info("✓ Progress item exists");
        
        // Log current status before waiting
        String currentClasses = feedbackPage.getProgressItemClasses(questionId);
        String statusText = feedbackPage.getProgressItemStatusText(questionId);
        log.info("Current classes before wait: {}", currentClasses);
        log.info("Current status text: {}", statusText);
        
        // Wait for auto-save completion with proper timeout handling
        boolean autoSaveCompleted = feedbackPage.waitForAutoSaveCompletion(questionId, 5);
        
        if (autoSaveCompleted) {
            log.info("✅ Auto-save completed successfully - question progress shows saved state");
            return; // Test passed - auto-save worked with visible confirmation
        } else {
            log.warn("Auto-save saved state not found after 5 seconds, checking data persistence...");
        }
        
        // Refresh page to check if data was actually saved despite missing notification
        page.reload();
        feedbackPage.startFeedbackSession();
        
        // Check if the rating data is present after refresh using page object method
        boolean dataPersisted = feedbackPage.isQuestionAnswered(questionId, expectedValue);
        
        if (dataPersisted) {
            fail("❌ Data is saved but visual notification failed - auto-save worked but UI feedback is broken");
        } else {
            fail("❌ Data not saved - auto-save functionality is not working");
        }
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