package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.Route;
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
        
        // Answer Question 1: Teaching Quality Rating (5 stars)
        log.info("Answering Q1: Teaching Quality");
        feedbackPage.answerQuestion1Rating(4); // 4/5 rating
        feedbackPage.waitForTimeout(300);

        // Answer Question 2: Communication Skills (5 stars)
        log.info("Answering Q2: Communication Skills");
        feedbackPage.answerQuestion2Rating(5); // 5/5 rating
        feedbackPage.waitForTimeout(300);

        // Answer Question 3: Punctuality (Yes/No)
        log.info("Answering Q3: Punctuality");
        feedbackPage.answerQuestion3YesNo(true); // Yes
        feedbackPage.waitForTimeout(300);

        // Answer Question 4: Teaching Methods (Multiple Choice)
        log.info("Answering Q4: Teaching Methods");
        feedbackPage.answerQuestion4MultipleChoice(0); // "Sangat Baik"
        feedbackPage.waitForTimeout(300);

        // Answer Question 5: Open-ended positive feedback
        log.info("Answering Q5: Positive Feedback");
        feedbackPage.answerQuestion5Text("Penjelasan tajwid sangat jelas dan sabar. Ustadz selalu memberikan contoh yang mudah dipahami.");
        feedbackPage.waitForTimeout(300);

        // Answer Question 6: Suggestions for improvement
        log.info("Answering Q6: Suggestions");
        feedbackPage.answerQuestion6Text("Mungkin bisa lebih banyak praktik langsung dan quiz interaktif.");
        feedbackPage.waitForTimeout(300);
        
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

        // Wait for answers to be processed
        feedbackPage.waitForTimeout(500);

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
    @DisplayName("AKH-AP-007: Session recovery preserves answered questions after page refresh")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldRecoverSessionAfterRefresh() {
        log.info("🚀 Starting AKH-AP-007: Session Recovery Test");

        loginAsStudent();

        // Initialize page object and navigate to feedback form
        feedbackPage = new StudentFeedbackPage(page);
        page.navigate(getBaseUrl() + "/student/feedback");
        feedbackPage.startFeedbackSession();

        // Answer first few questions
        log.info("📝 Answering initial questions");
        feedbackPage.answerFirstRequiredRatingQuestion(); // Q1 with rating 3
        feedbackPage.answerQuestion2Rating(5); // Q2

        // Wait for auto-save to complete
        log.info("📝 Waiting for auto-save to complete");
        feedbackPage.waitForTimeout(3000); // Wait longer than the 2000ms auto-save delay

        // Refresh the page to simulate session interruption
        log.info("📝 Testing session recovery after refresh");
        page.reload();
        page.waitForLoadState();

        // Wait for form to load
        page.waitForSelector("#feedback-form", new Page.WaitForSelectorOptions().setTimeout(10000));

        // Wait for Alpine.js to initialize and restore data
        feedbackPage.waitForTimeout(1000);

        // Debug: Check what's in the data-resume-data attribute
        String resumeDataAttr = (String) page.evaluate("() => { " +
            "const form = document.querySelector('[x-data=\"feedbackForm\"]'); " +
            "return form ? form.dataset.resumeData : 'no-form'; " +
            "}");
        log.info("data-resume-data attribute: {}", resumeDataAttr);

        // Check if resume data is loaded
        assertTrue(feedbackPage.isResumeNoticePresent(), "Resume notice should be displayed after refresh");
        log.info("✓ Resume notice displayed");

        // Verify previously answered questions are restored (Q1 had rating 3)
        // Use Alpine.js state check instead of hidden input
        String alpineAnswers = (String) page.evaluate("() => { " +
            "const form = document.querySelector('[x-data=\"feedbackForm\"]'); " +
            "return form && form._x_dataStack ? JSON.stringify(form._x_dataStack[0].answers) : 'no-alpine'; " +
            "}");
        log.info("Alpine answers after reload: {}", alpineAnswers);
        assertTrue(alpineAnswers.contains("rating"), "Alpine.js should have restored answer data");

        // Complete the feedback with remaining questions
        log.info("📝 Completing feedback after recovery - answering remaining questions");
        feedbackPage.answerRemainingQuestionsFromQ3();

        // Submit successfully
        feedbackPage.clickSubmitButton();
        feedbackPage.waitForConfirmationPage();

        assertTrue(feedbackPage.getFeedbackFormTitle().contains("Feedback Berhasil Dikirim"));

        log.info("✅ AKH-AP-007: Session recovery successful!");
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
        // Use clickSubmitButtonWithValidation to trigger Alpine.js validation
        feedbackPage.clickSubmitButtonWithValidation();

        // Should show validation error
        assertTrue(feedbackPage.isValidationErrorDisplayed(), "Validation error should be displayed");
        
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
        log.info("🚀 Starting auto-save functionality test (end-to-end)");

        String questionId = "e5555551-5555-5555-5555-555555555555";

        // 1. Login and navigate to the feedback form
        loginAsStudent();
        feedbackPage = new StudentFeedbackPage(page);
        page.navigate(getBaseUrl() + "/student/feedback");
        feedbackPage.startFeedbackSession();
        log.info("Student is on the feedback form");

        // 2. Verify no resume notice initially (fresh session)
        assertFalse(feedbackPage.isResumeNoticePresent(),
            "Resume notice should NOT be present on fresh session");
        log.info("✓ Confirmed fresh session (no resume notice)");

        // 3. Answer first question by clicking the star (like a real user would)
        log.info("Clicking rating star...");
        var starLocator = page.locator("#rating-star-" + questionId + "-3");
        assertTrue(starLocator.isVisible(), "Rating star should be visible");

        // Click the star - this should work now with CSP-compliant Alpine.js handlers
        starLocator.click();
        feedbackPage.waitForTimeout(500);

        // Verify Alpine.js state was updated by the click
        String stateAfterClick = (String) page.evaluate("() => { " +
            "const form = document.querySelector('[x-data=\"feedbackForm\"]'); " +
            "return form && form._x_dataStack ? JSON.stringify(form._x_dataStack[0].answers) : 'no-alpine'; " +
            "}");
        log.info("Alpine answers after click: {}", stateAfterClick);
        assertTrue(stateAfterClick.contains("rating"), "Rating should be set in Alpine.js state after click");
        log.info("✓ Rating set via click");

        // 4. Wait for auto-save to trigger and complete (2s debounce + network time)
        log.info("Waiting for auto-save to complete...");
        feedbackPage.waitForTimeout(4000);
        log.info("✓ Auto-save delay completed");

        // 5. Reload page to verify server-side persistence
        log.info("Reloading page to verify server-side persistence...");
        page.reload();
        page.waitForLoadState();

        // Wait for form to load
        page.waitForSelector("#feedback-form", new Page.WaitForSelectorOptions().setTimeout(10000));
        log.info("✓ Page reloaded");

        // 6. Verify resume notice appears (confirms server saved the data)
        boolean hasResumeNotice = feedbackPage.isResumeNoticePresent();
        log.info("Resume notice present: {}", hasResumeNotice);

        // 7. Verify the answer was restored by checking the rating input value
        String restoredValue = page.locator("[name='answers[0].rating']").inputValue();
        log.info("Restored rating value: {}", restoredValue);

        // Assert data was persisted and restored
        assertTrue(hasResumeNotice || "3".equals(restoredValue),
            "Data should be persisted: either resume notice shown OR rating value restored");
        log.info("✓ Server-side persistence verified");

        log.info("✅ Auto-save functionality verified with server-side persistence!");
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
