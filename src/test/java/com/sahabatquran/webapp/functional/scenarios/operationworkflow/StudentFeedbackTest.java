package com.sahabatquran.webapp.functional.scenarios.operationworkflow;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.sahabatquran.webapp.functional.BasePlaywrightTest;
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
        
        // ========== BAGIAN 1: Access Feedback System ==========
        log.info("📝 Bagian 1: Access Feedback System");
        
        // Navigate to feedback dashboard
        page.click("text=Feedback");
        page.waitForURL("**/student/feedback");
        
        // Verify dashboard elements
        assertThat(page.locator("h1")).containsText("Feedback Siswa");
        assertTrue(page.locator(".bg-blue-50").isVisible(), "Anonymity notice should be visible");
        assertThat(page.locator(".bg-blue-50")).containsText("Feedback Anda bersifat anonim");
        
        // Check active campaigns
        Locator campaignCards = page.locator(".campaign-card");
        assertTrue(campaignCards.count() > 0, "Should have active feedback campaigns");
        
        // Find teacher evaluation campaign
        Locator teacherEvalCard = page.locator(".campaign-card:has-text('Teacher Evaluation')");
        assertTrue(teacherEvalCard.isVisible(), "Teacher evaluation campaign should be visible");
        
        // Verify anonymous badge
        assertTrue(teacherEvalCard.locator(".anonymous-badge").isVisible(), 
            "Anonymous badge should be visible on campaign");
        
        // Check campaign details
        assertThat(teacherEvalCard).containsText("Ustadz Ahmad");
        assertThat(teacherEvalCard).containsText("hari tersisa");
        
        // Start feedback session
        teacherEvalCard.locator("a:has-text('Mulai Feedback')").click();
        page.waitForURL("**/student/feedback/campaign/**");
        
        // ========== BAGIAN 2: Complete Feedback Form ==========
        log.info("📝 Bagian 2: Complete Feedback Form");
        
        // Verify form loaded with anonymity notice
        assertThat(page.locator("h1")).containsText("Teacher Evaluation");
        assertTrue(page.locator("text=Feedback Anda bersifat anonim").isVisible());
        
        // Check progress indicator
        Locator progressText = page.locator("#progress-text");
        assertThat(progressText).hasText("0");
        
        // Answer Question 1: Teaching Quality Rating (5 stars)
        log.info("Answering Q1: Teaching Quality");
        Locator q1Stars = page.locator(".question-card").first().locator(".rating-star");
        q1Stars.nth(3).click(); // Click 4th star (4/5 rating)
        
        // Verify star is active
        assertTrue(q1Stars.nth(3).evaluate("el => el.classList.contains('active')").toString().equals("true"));
        
        // Check progress updated
        assertThat(progressText).hasText("1");
        
        // Answer Question 2: Communication Skills (5 stars)
        log.info("Answering Q2: Communication Skills");
        Locator q2Card = page.locator(".question-card").nth(1);
        q2Card.locator(".rating-star").nth(4).click(); // 5/5 rating
        assertThat(progressText).hasText("2");
        
        // Answer Question 3: Punctuality (Yes/No)
        log.info("Answering Q3: Punctuality");
        Locator q3Card = page.locator(".question-card").nth(2);
        q3Card.locator("input[value='true']").check(); // Yes
        assertThat(progressText).hasText("3");
        
        // Answer Question 4: Teaching Methods (Multiple Choice)
        log.info("Answering Q4: Teaching Methods");
        Locator q4Card = page.locator(".question-card").nth(3);
        q4Card.locator("input[value='Sangat Baik']").check();
        assertThat(progressText).hasText("4");
        
        // Answer Question 5: Open-ended positive feedback
        log.info("Answering Q5: Positive Feedback");
        Locator q5Card = page.locator(".question-card").nth(4);
        Locator positiveTextarea = q5Card.locator("textarea");
        positiveTextarea.fill("Penjelasan tajwid sangat jelas dan sabar. Ustadz selalu memberikan contoh yang mudah dipahami.");
        
        // Verify character counter
        Locator charCounter = q5Card.locator(".char-count");
        assertTrue(Integer.parseInt(charCounter.textContent()) > 0, "Character counter should update");
        assertThat(progressText).hasText("5");
        
        // Answer Question 6: Suggestions for improvement
        log.info("Answering Q6: Suggestions");
        Locator q6Card = page.locator(".question-card").nth(5);
        q6Card.locator("textarea").fill("Mungkin bisa lebih banyak praktik langsung dan quiz interaktif.");
        assertThat(progressText).hasText("6");
        
        // Wait for auto-save indicator (should appear after 2 seconds of inactivity)
        page.waitForTimeout(2500);
        Locator autoSaveIndicator = page.locator("#auto-save-indicator");
        if (autoSaveIndicator.isVisible()) {
            log.info("✓ Auto-save triggered successfully");
        }
        
        // Complete remaining questions with sample data
        for (int i = 6; i < 12; i++) {
            Locator questionCard = page.locator(".question-card").nth(i);
            
            // Check if it's a rating question
            if (questionCard.locator(".rating-star").count() > 0) {
                questionCard.locator(".rating-star").nth(3).click(); // 4/5 rating
            }
            // Check if it's a yes/no question
            else if (questionCard.locator("input[type='radio']").count() > 0) {
                questionCard.locator("input[type='radio']").first().check();
            }
            // Check if it's a text question
            else if (questionCard.locator("textarea").count() > 0) {
                questionCard.locator("textarea").fill("Sample response for question " + (i + 1));
            }
        }
        
        // Verify all questions answered
        assertThat(progressText).hasText("12");
        assertThat(page.locator("#progress-percentage")).containsText("100%");
        
        // ========== BAGIAN 3: Submit Feedback ==========
        log.info("📝 Bagian 3: Submit Feedback");
        
        // Click submit button
        page.locator("#submit-btn").click();
        
        // Handle confirmation if it appears
        if (page.locator("text=Konfirmasi").count() > 0) {
            page.locator("button:has-text('Ya, Kirim')").click();
        }
        
        // Wait for submission and redirect to confirmation page
        page.waitForURL("**/student/feedback/confirmation/**");
        
        // Verify confirmation page
        assertThat(page.locator("h1")).containsText("Feedback Berhasil Dikirim");
        assertTrue(page.locator(".fa-check-circle").isVisible(), "Success icon should be visible");
        
        // Check confirmation details
        assertTrue(page.locator("text=Teacher Evaluation").isVisible());
        assertTrue(page.locator("text=ID Konfirmasi").isVisible());
        
        // Verify anonymous notice on confirmation
        assertThat(page.locator(".bg-blue-50")).containsText("Feedback Anda telah dikirim secara anonim");
        
        // Return to dashboard
        page.locator("a:has-text('Kembali ke Dashboard')").click();
        page.waitForURL("**/student/feedback");
        
        // Verify campaign now shows as completed
        Locator completedCampaign = page.locator(".campaign-card:has-text('Teacher Evaluation')");
        // Campaign should either be hidden or show completed status
        if (completedCampaign.count() > 0) {
            assertTrue(completedCampaign.locator(".status-completed").count() > 0 ||
                      !completedCampaign.locator("a:has-text('Mulai Feedback')").isVisible(),
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
        Locator campaignCard = page.locator(".campaign-card:has-text('Teacher Evaluation')");
        
        // Campaign might be hidden or show completed status
        if (campaignCard.count() > 0) {
            // If visible, should not have start button or should be disabled
            Locator startButton = campaignCard.locator("a:has-text('Mulai Feedback')");
            assertFalse(startButton.isVisible() || startButton.isDisabled(), 
                "Start button should be hidden or disabled for completed campaign");
        }
        
        // Try direct URL access to the campaign
        String campaignUrl = getBaseUrl() + "/student/feedback/campaign/test-campaign-id";
        page.navigate(campaignUrl);
        
        // Should be redirected to already-submitted page
        page.waitForURL("**/feedback-already-submitted**");
        
        // Verify the already submitted message
        assertThat(page.locator("h1")).containsText("Feedback Sudah Dikirim");
        assertTrue(page.locator("text=Anda sudah memberikan feedback untuk campaign ini").isVisible());
        
        // Check for anonymity explanation
        assertTrue(page.locator("text=Feedback Anda bersifat anonim dan tidak dapat diubah").isVisible());
        
        // Verify options provided
        assertTrue(page.locator("a:has-text('Kembali ke Dashboard')").isVisible());
        assertTrue(page.locator("text=Mengapa saya tidak bisa mengisi ulang?").isVisible());
        
        log.info("✅ AKH-AP-002: Duplicate submission prevention working correctly!");
    }
    
    @Test
    @DisplayName("AKH-AP-007: System handles technical failures gracefully")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldHandleTechnicalFailuresGracefully() {
        log.info("🚀 Starting AKH-AP-007: Technical Failure Handling Test");
        
        loginAsStudent();
        
        // Navigate to feedback form
        page.navigate(getBaseUrl() + "/student/feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Answer first few questions
        log.info("📝 Answering initial questions");
        page.locator(".rating-star").nth(3).click(); // Q1
        page.locator(".question-card").nth(1).locator(".rating-star").nth(4).click(); // Q2
        
        // Simulate network interruption
        log.info("📝 Simulating network interruption");
        page.context().setOffline(true);
        
        // Try to answer another question
        page.locator(".question-card").nth(2).locator("input[type='radio']").first().check();
        
        // Auto-save should fail but not crash
        page.waitForTimeout(2500);
        
        // Re-enable network
        page.context().setOffline(false);
        
        // Refresh the page
        log.info("📝 Testing session recovery after refresh");
        page.reload();
        
        // Check if resume data is loaded
        Locator resumeNotice = page.locator("text=Anda memiliki progress yang tersimpan");
        if (resumeNotice.isVisible()) {
            log.info("✓ Resume notice displayed");
            assertThat(resumeNotice).containsText("2"); // Should show 2 questions answered
        }
        
        // Verify previously answered questions are restored
        Locator q1Stars = page.locator(".question-card").first().locator(".rating-star");
        assertTrue(q1Stars.nth(3).evaluate("el => el.classList.contains('active')").toString().equals("true"),
            "First question answer should be restored");
        
        // Complete the feedback
        log.info("📝 Completing feedback after recovery");
        for (int i = 2; i < 12; i++) {
            Locator questionCard = page.locator(".question-card").nth(i);
            if (questionCard.locator(".rating-star").count() > 0) {
                questionCard.locator(".rating-star").nth(2).click();
            } else if (questionCard.locator("input[type='radio']").count() > 0) {
                questionCard.locator("input[type='radio']").first().check();
            } else if (questionCard.locator("textarea").count() > 0) {
                questionCard.locator("textarea").fill("Test response");
            }
        }
        
        // Submit successfully
        page.locator("#submit-btn").click();
        page.waitForURL("**/confirmation/**");
        
        assertThat(page.locator("h1")).containsText("Feedback Berhasil Dikirim");
        
        log.info("✅ AKH-AP-007: Technical failure handling successful!");
    }
    
    @Test
    @DisplayName("Feedback form validates required fields before submission")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldValidateRequiredFieldsBeforeSubmission() {
        log.info("🚀 Starting required field validation test");
        
        loginAsStudent();
        
        // Navigate to feedback form
        page.navigate(getBaseUrl() + "/student/feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Try to submit without answering required questions
        page.locator("#submit-btn").click();
        
        // Should show validation error
        page.waitForSelector("text=Mohon jawab semua pertanyaan yang wajib");
        
        // First unanswered required question should be highlighted
        Locator firstRequired = page.locator(".question-card:has(span.text-red-500)").first();
        assertTrue(firstRequired.boundingBox() != null, "First required question should be scrolled into view");
        
        // Answer only required questions (marked with *)
        page.locator(".question-card:has(span.text-red-500)").all().forEach(card -> {
            if (card.locator(".rating-star").count() > 0) {
                card.locator(".rating-star").nth(2).click();
            } else if (card.locator("input[type='radio']").count() > 0) {
                card.locator("input[type='radio']").first().check();
            } else if (card.locator("textarea").count() > 0) {
                card.locator("textarea").fill("Required field response");
            }
        });
        
        // Now submission should work
        page.locator("#submit-btn").click();
        page.waitForURL("**/confirmation/**");
        
        log.info("✅ Required field validation working correctly!");
    }
    
    @Test
    @DisplayName("Auto-save preserves feedback progress during long sessions")
    @Sql(scripts = "/sql/feedback-campaign-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/feedback-campaign-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldAutoSaveFeedbackProgress() {
        log.info("🚀 Starting auto-save functionality test");
        
        loginAsStudent();
        
        // Navigate to feedback form
        page.navigate(getBaseUrl() + "/student/feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Answer a question
        page.locator(".rating-star").nth(3).click();
        
        // Wait for auto-save (2 seconds of inactivity)
        page.waitForTimeout(2500);
        
        // Check for auto-save indicator
        Locator autoSaveIndicator = page.locator("#auto-save-indicator");
        page.waitForSelector("#auto-save-indicator.active", new com.microsoft.playwright.Page.WaitForSelectorOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
            .setTimeout(5000));
        
        assertThat(autoSaveIndicator).containsText("Progress tersimpan otomatis");
        
        // Navigate away and come back
        page.navigate(getBaseUrl() + "/student/feedback");
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Check if previous answer is preserved
        assertTrue(page.locator(".rating-star").nth(3)
            .evaluate("el => el.classList.contains('active')").toString().equals("true"),
            "Previously selected rating should be preserved");
        
        log.info("✅ Auto-save functionality working correctly!");
    }
    
    // Helper methods
    
    @Override
    protected void loginAsStudent() {
        page.navigate(getBaseUrl() + "/login");
        page.fill("input[name='username']", STUDENT_USERNAME);
        page.fill("input[name='password']", STUDENT_PASSWORD);
        page.click("button[type='submit']");
        page.waitForURL("**/dashboard");
    }
    
    private void submitInitialFeedback() {
        loginAsStudent();
        page.navigate(getBaseUrl() + "/student/feedback");
        
        // Quick submission for testing duplicate prevention
        page.locator(".campaign-card").first().locator("a:has-text('Mulai Feedback')").click();
        
        // Answer all questions quickly
        for (int i = 0; i < 12; i++) {
            Locator card = page.locator(".question-card").nth(i);
            if (card.locator(".rating-star").count() > 0) {
                card.locator(".rating-star").nth(2).click();
            } else if (card.locator("input[type='radio']").count() > 0) {
                card.locator("input[type='radio']").first().check();
            } else if (card.locator("textarea").count() > 0) {
                card.locator("textarea").fill("Quick test response");
            }
        }
        
        page.locator("#submit-btn").click();
        page.waitForURL("**/confirmation/**");
    }
}