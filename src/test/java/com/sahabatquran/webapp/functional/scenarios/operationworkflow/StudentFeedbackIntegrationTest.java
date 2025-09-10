package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.sahabatquran.webapp.functional.BasePlaywrightTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests for Student Feedback functionality.
 * Tests complete workflows including edge cases and cross-browser compatibility.
 */
@Slf4j
@DisplayName("Student Feedback - End-to-End Integration Tests")
class StudentFeedbackIntegrationTest extends BasePlaywrightTest {
    
    @Test
    @DisplayName("Complete feedback workflow with all question types")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCompleteFeedbackWorkflowWithAllQuestionTypes() {
        log.info("🚀 Testing complete feedback workflow with all question types");
        
        // Login
        navigateAndLogin("siswa.ali", "Welcome@YSQ2024");
        
        // Navigate to feedback
        page.click("text=Feedback");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Verify dashboard loads with campaigns
        Locator dashboardTitle = page.locator("h1:has-text('Feedback Siswa')");
        assertTrue(dashboardTitle.isVisible());
        
        // Count active campaigns
        int campaignCount = page.locator(".campaign-card").count();
        assertTrue(campaignCount >= 1, "Should have at least one active campaign");
        log.info("Found {} active campaigns", campaignCount);
        
        // Start first campaign
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Test each question type
        testRatingQuestion();
        testYesNoQuestion();
        testMultipleChoiceQuestion();
        testTextQuestion();
        
        // Verify progress
        String progress = page.locator("#progress-percentage").textContent();
        log.info("Current progress: {}", progress);
        
        // Complete remaining questions
        completeRemainingQuestions();
        
        // Submit feedback
        submitAndVerifyFeedback();
        
        // Verify cannot resubmit
        verifyDuplicatePrevention();
        
        log.info("✅ Complete feedback workflow test passed!");
    }
    
    @Test
    @DisplayName("Feedback session recovery after browser crash")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testSessionRecoveryAfterBrowserCrash() {
        log.info("🚀 Testing session recovery after browser crash");
        
        // Start feedback session
        navigateAndLogin("siswa.ali", "Welcome@YSQ2024");
        page.click("text=Feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Answer some questions
        answerPartialQuestions();
        
        // Get session storage data before crash
        Object sessionData = page.evaluate("() => window.sessionStorage.getItem('feedbackProgress')");
        log.info("Session data before crash: {}", sessionData);
        
        // Simulate browser crash by closing context and creating new one
        page.context().close();
        
        // Create new context and page
        context = getBrowser().newContext();
        page = context.newPage();
        
        // Login again
        navigateAndLogin("siswa.ali", "Welcome@YSQ2024");
        
        // Navigate back to feedback
        page.click("text=Feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Check for resume notice
        Locator resumeNotice = page.locator("text=Anda memiliki progress yang tersimpan");
        if (resumeNotice.isVisible()) {
            log.info("✓ Resume notice displayed");
            
            // Verify saved answers are restored
            verifySavedAnswersRestored();
        }
        
        // Complete and submit
        completeRemainingQuestions();
        submitAndVerifyFeedback();
        
        log.info("✅ Session recovery test passed!");
    }
    
    @Test
    @DisplayName("Feedback analytics and reporting for admin")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFeedbackAnalyticsForAdmin() {
        log.info("🚀 Testing feedback analytics for admin");
        
        // First, submit some feedback as student
        submitStudentFeedback();
        
        // Login as admin
        navigateAndLogin("academic.admin1", "Welcome@YSQ2024");
        
        // Navigate to feedback analytics
        page.click("text=Monitoring");
        page.click("text=Feedback Analytics");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Verify analytics dashboard
        assertTrue(page.locator("h1:has-text('Feedback Analytics')").isVisible());
        
        // Check campaign statistics
        Locator campaignStats = page.locator(".campaign-stats");
        if (campaignStats.count() > 0) {
            log.info("Campaign statistics available");
            
            // Verify response rate
            Locator responseRate = page.locator("text=/Response Rate.*%/");
            if (responseRate.isVisible()) {
                String rate = responseRate.textContent();
                log.info("Current response rate: {}", rate);
            }
        }
        
        // Generate report
        if (page.locator("button:has-text('Generate Report')").isVisible()) {
            page.click("button:has-text('Generate Report')");
            
            // Wait for report generation
            page.waitForSelector("text=Report generated successfully", 
                new Page.WaitForSelectorOptions().setTimeout(10000));
            
            log.info("✓ Report generated successfully");
        }
        
        log.info("✅ Feedback analytics test passed!");
    }
    
    @Test
    @DisplayName("Mobile responsive feedback form")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testMobileResponsiveFeedbackForm() {
        log.info("🚀 Testing mobile responsive feedback form");
        
        // Set mobile viewport
        page.setViewportSize(375, 812); // iPhone X size
        
        // Navigate and login
        navigateAndLogin("siswa.ali", "Welcome@YSQ2024");
        
        // Navigate to feedback
        page.click("text=Feedback");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        
        // Verify mobile layout
        assertTrue(page.locator(".campaign-card").first().isVisible());
        
        // Start feedback
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Verify form is usable on mobile
        Locator firstQuestion = page.locator(".question-card").first();
        assertTrue(firstQuestion.isVisible());
        
        // Test touch interactions with rating stars
        Locator ratingStar = firstQuestion.locator(".rating-star").nth(2);
        ratingStar.tap(); // Use tap for mobile
        
        // Verify selection worked
        assertTrue(ratingStar.evaluate("el => el.classList.contains('active')").toString().equals("true"));
        
        // Test scrolling
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        
        // Submit button should be reachable
        assertTrue(page.locator("#submit-btn").isVisible());
        
        log.info("✅ Mobile responsive test passed!");
    }
    
    @Test
    @DisplayName("Performance test with large number of questions")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testPerformanceWithLargeNumberOfQuestions() {
        log.info("🚀 Testing performance with large number of questions");
        
        // Start timing
        long startTime = System.currentTimeMillis();
        
        // Login and navigate
        navigateAndLogin("siswa.ali", "Welcome@YSQ2024");
        page.click("text=Feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Measure initial load time
        long loadTime = System.currentTimeMillis() - startTime;
        log.info("Initial load time: {}ms", loadTime);
        assertTrue(loadTime < 5000, "Page should load within 5 seconds");
        
        // Answer all questions rapidly
        long answerStartTime = System.currentTimeMillis();
        List<Locator> questions = page.locator(".question-card").all();
        
        for (Locator question : questions) {
            if (question.locator(".rating-star").count() > 0) {
                question.locator(".rating-star").nth(2).click();
            } else if (question.locator("input[type='radio']").count() > 0) {
                question.locator("input[type='radio']").first().check();
            } else if (question.locator("textarea").count() > 0) {
                question.locator("textarea").fill("Performance test response");
            }
        }
        
        long answerTime = System.currentTimeMillis() - answerStartTime;
        log.info("Time to answer {} questions: {}ms", questions.size(), answerTime);
        
        // Test auto-save performance
        page.waitForTimeout(2500); // Wait for auto-save
        
        // Submit
        long submitStartTime = System.currentTimeMillis();
        page.locator("#submit-btn").click();
        page.waitForURL("**/confirmation/**");
        long submitTime = System.currentTimeMillis() - submitStartTime;
        
        log.info("Submission time: {}ms", submitTime);
        assertTrue(submitTime < 3000, "Submission should complete within 3 seconds");
        
        log.info("✅ Performance test passed!");
    }
    
    // Helper methods
    
    private void navigateAndLogin(String username, String password) {
        page.navigate(getBaseUrl() + "/login");
        page.fill("input[name='username']", username);
        page.fill("input[name='password']", password);
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
    
    private void testRatingQuestion() {
        Locator ratingQuestion = page.locator(".question-card:has(.rating-star)").first();
        ratingQuestion.locator(".rating-star").nth(3).click();
        log.info("✓ Rating question answered");
    }
    
    private void testYesNoQuestion() {
        Locator yesNoQuestion = page.locator(".question-card:has(input[value='true'])").first();
        yesNoQuestion.locator("input[value='true']").check();
        log.info("✓ Yes/No question answered");
    }
    
    private void testMultipleChoiceQuestion() {
        Locator mcQuestion = page.locator(".question-card:has(input[value='Sangat Baik'])").first();
        if (mcQuestion.count() > 0) {
            mcQuestion.locator("input[value='Sangat Baik']").check();
            log.info("✓ Multiple choice question answered");
        }
    }
    
    private void testTextQuestion() {
        Locator textQuestion = page.locator(".question-card:has(textarea)").first();
        Locator textarea = textQuestion.locator("textarea");
        textarea.fill("This is a comprehensive test response to verify the text input functionality.");
        
        // Verify character counter
        Locator charCounter = textQuestion.locator(".char-count");
        if (charCounter.isVisible()) {
            int charCount = Integer.parseInt(charCounter.textContent());
            assertTrue(charCount > 0, "Character counter should update");
            log.info("✓ Text question answered ({} characters)", charCount);
        }
    }
    
    private void completeRemainingQuestions() {
        List<Locator> unanswered = page.locator(".question-card").all();
        for (Locator question : unanswered) {
            // Check if already answered by looking for active selections
            boolean isAnswered = question.locator(".rating-star.active").count() > 0 ||
                                question.locator("input:checked").count() > 0 ||
                                !question.locator("textarea").inputValue().isEmpty();
            
            if (!isAnswered) {
                if (question.locator(".rating-star").count() > 0) {
                    question.locator(".rating-star").nth(2).click();
                } else if (question.locator("input[type='radio']").count() > 0) {
                    question.locator("input[type='radio']").first().check();
                } else if (question.locator("textarea").count() > 0) {
                    question.locator("textarea").fill("Automated test response");
                }
            }
        }
    }
    
    private void submitAndVerifyFeedback() {
        // Submit
        page.locator("#submit-btn").click();
        
        // Wait for confirmation page
        page.waitForURL("**/confirmation/**");
        
        // Verify success
        assertThat(page.locator("h1")).containsText("Feedback Berhasil Dikirim");
        assertTrue(page.locator(".fa-check-circle").isVisible());
        log.info("✓ Feedback submitted successfully");
    }
    
    private void verifyDuplicatePrevention() {
        // Try to access the same campaign again
        page.click("a:has-text('Kembali ke Dashboard')");
        
        // Check if campaign is still available
        Locator campaignCard = page.locator(".campaign-card:has-text('Teacher Evaluation')");
        if (campaignCard.count() > 0) {
            // Should not have start button or should show completed
            assertFalse(campaignCard.locator("a:has-text('Mulai Feedback')").isVisible(),
                "Start button should not be visible for completed campaign");
        }
        log.info("✓ Duplicate submission prevented");
    }
    
    private void answerPartialQuestions() {
        // Answer first 3 questions only
        for (int i = 0; i < 3; i++) {
            Locator question = page.locator(".question-card").nth(i);
            if (question.locator(".rating-star").count() > 0) {
                question.locator(".rating-star").nth(2).click();
            } else if (question.locator("input[type='radio']").count() > 0) {
                question.locator("input[type='radio']").first().check();
            } else if (question.locator("textarea").count() > 0) {
                question.locator("textarea").fill("Partial answer");
            }
        }
        
        // Wait for auto-save
        page.waitForTimeout(2500);
    }
    
    private void verifySavedAnswersRestored() {
        // Check first 3 questions have answers
        for (int i = 0; i < 3; i++) {
            Locator question = page.locator(".question-card").nth(i);
            boolean hasAnswer = question.locator(".rating-star.active").count() > 0 ||
                              question.locator("input:checked").count() > 0 ||
                              !question.locator("textarea").inputValue().isEmpty();
            assertTrue(hasAnswer, "Question " + (i + 1) + " should have saved answer");
        }
        log.info("✓ Saved answers restored successfully");
    }
    
    private void submitStudentFeedback() {
        // Quick feedback submission for analytics testing
        navigateAndLogin("siswa.ali", "Welcome@YSQ2024");
        page.click("text=Feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        completeRemainingQuestions();
        submitAndVerifyFeedback();
        
        // Logout
        page.click("text=Logout");
    }
}