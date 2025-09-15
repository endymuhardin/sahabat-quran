package com.sahabatquran.webapp.functional;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import com.sahabatquran.webapp.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Gmail Verification Test using Playwright.
 *
 * This test sends an email via Gmail API and then verifies it was received
 * by checking Gmail web interface using Playwright.
 *
 * Requirements:
 * - Gmail credentials configured
 * - Test Gmail account credentials for verification
 * - Gmail profile active
 *
 * Note: This test is disabled by default as it requires real Gmail accounts.
 * Enable only for integration testing with proper test accounts.
 */
@Slf4j
@DisplayName("Gmail Email Verification Tests")
public class GmailVerificationTest extends BasePlaywrightTest {

    @Autowired
    private EmailService emailService;

    @Value("${gmail.test.recipient.email:#{null}}")
    private String testRecipientEmail;

    @Value("${gmail.test.recipient.password:#{null}}")
    private String testRecipientPassword;

    @Value("${gmail.enabled:false}")
    private boolean gmailEnabled;

    @BeforeEach
    void setUp() {
        // Skip if Gmail is not configured or test credentials not provided
        boolean testConfigured = gmailEnabled &&
            testRecipientEmail != null && !testRecipientEmail.isEmpty() &&
            testRecipientPassword != null && !testRecipientPassword.isEmpty();

        if (!testConfigured) {
            log.warn("Gmail verification test skipped - Gmail or test credentials not configured");
            log.warn("To enable: Set gmail.enabled=true and configure gmail.test.recipient.email/password");
        }

        assumeTrue(testConfigured, "Gmail test configuration not complete");
    }

    @Test
    @DisplayName("Should send email and verify in Gmail inbox")
    void shouldSendAndVerifyEmailInGmail() throws InterruptedException {
        log.info("🚀 Starting Gmail email verification test");

        // Step 1: Send test email
        String uniqueSubject = "Test Report - " + System.currentTimeMillis();
        StudentReportEmailDto emailData = createTestEmailData(uniqueSubject);

        log.info("📧 Sending test email with subject: {}", uniqueSubject);
        emailService.sendStudentReportEmail(emailData);

        // Wait for email to be delivered
        Thread.sleep(5000);

        // Step 2: Open Gmail and login
        log.info("🌐 Opening Gmail web interface");
        page.navigate("https://mail.google.com");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Check if we need to login
        if (page.locator("input[type='email']").isVisible()) {
            loginToGmail();
        }

        // Step 3: Search for the email
        log.info("🔍 Searching for email with subject: {}", uniqueSubject);
        searchForEmail(uniqueSubject);

        // Step 4: Verify email content
        log.info("✅ Verifying email content");
        verifyEmailContent(emailData);

        log.info("✅ Gmail verification test completed successfully!");
    }

    @Test
    @DisplayName("Should handle multiple recipients")
    void shouldSendToMultipleRecipients() throws InterruptedException {
        log.info("🚀 Testing multiple recipient email sending");

        // Create multiple test emails
        String baseSubject = "Multi-Recipient Test - " + System.currentTimeMillis();

        for (int i = 1; i <= 3; i++) {
            String subject = baseSubject + " - Email " + i;
            StudentReportEmailDto emailData = createTestEmailData(subject);
            emailData.setStudentName("Student " + i);

            log.info("📧 Sending email {} of 3", i);
            emailService.sendStudentReportEmail(emailData);

            // Small delay between emails
            Thread.sleep(1000);
        }

        // Verify at least one email was received
        Thread.sleep(5000);
        page.navigate("https://mail.google.com");

        if (page.locator("input[type='email']").isVisible()) {
            loginToGmail();
        }

        searchForEmail(baseSubject);

        // Should find at least one email
        assertTrue(page.locator("span:has-text('" + baseSubject + "')").first().isVisible(),
            "Should find at least one test email");

        log.info("✅ Multiple recipient test completed!");
    }

    /**
     * Creates test email data with a unique subject.
     */
    private StudentReportEmailDto createTestEmailData(String subject) {
        StudentReportEmailDto emailData = emailService.createSampleEmailData("test-student");
        emailData.setRecipientEmail(testRecipientEmail);
        emailData.setRecipientName("Test Recipient");
        // Note: Subject is set in the email template service
        return emailData;
    }

    /**
     * Logs into Gmail using test credentials.
     */
    private void loginToGmail() {
        log.info("🔐 Logging into Gmail");

        // Enter email
        page.locator("input[type='email']").fill(testRecipientEmail);
        page.locator("button:has-text('Next')").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Enter password
        page.locator("input[type='password']").fill(testRecipientPassword);
        page.locator("button:has-text('Next')").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Wait for inbox to load
        page.waitForSelector("div[role='main']", new Page.WaitForSelectorOptions()
            .setTimeout(30000));
    }

    /**
     * Searches for an email by subject.
     */
    private void searchForEmail(String subject) {
        // Click search box
        page.locator("input[aria-label*='Search']").click();
        page.locator("input[aria-label*='Search']").fill("subject:\"" + subject + "\"");
        page.keyboard().press("Enter");

        // Wait for search results
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(2000);

        // Click on the first email in results
        page.locator("tr.zA").first().click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Verifies email content matches expected data.
     */
    private void verifyEmailContent(StudentReportEmailDto expectedData) {
        // Check if email is displayed
        assertTrue(page.locator("div[role='article']").isVisible(),
            "Email content should be visible");

        // Verify student name
        assertTrue(page.locator("*:has-text('" + expectedData.getStudentName() + "')").isVisible(),
            "Email should contain student name");

        // Verify level
        assertTrue(page.locator("*:has-text('" + expectedData.getLevelName() + "')").isVisible(),
            "Email should contain level name");

        // Verify term
        assertTrue(page.locator("*:has-text('" + expectedData.getTermName() + "')").isVisible(),
            "Email should contain term name");

        // Verify overall grade
        assertTrue(page.locator("*:has-text('" + expectedData.getOverallGrade() + "')").isVisible(),
            "Email should contain overall grade");

        log.info("✅ Email content verification passed");
    }
}