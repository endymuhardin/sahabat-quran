package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import com.sahabatquran.webapp.service.impl.GmailEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests for Gmail Email Service.
 *
 * These tests require Gmail to be properly configured with valid credentials.
 * They will be skipped if Gmail is not configured.
 *
 * To run these tests:
 * 1. Set up Gmail OAuth credentials using GmailTokenGenerator
 * 2. Set environment variables: GMAIL_CLIENT_ID, GMAIL_CLIENT_SECRET, GMAIL_REFRESH_TOKEN, GMAIL_NOTIFICATION_EMAIL
 * 3. Run tests with gmail profile: mvn test -Dspring.profiles.active=gmail
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "gmail.enabled=false" // Disabled by default, enable only when credentials are available
})
@DisplayName("Gmail Email Service Integration Tests")
class GmailEmailServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired(required = false)
    private EmailService emailService;

    @Autowired(required = false)
    private GmailHealthMonitor healthMonitor;

    private boolean gmailConfigured = false;

    @BeforeEach
    void setUp() {
        // Check if Gmail is configured
        gmailConfigured = emailService instanceof GmailEmailService;

        if (!gmailConfigured) {
            System.out.println("⚠️  Gmail is not configured. Tests will be skipped.");
            System.out.println("   To enable: Set gmail.enabled=true and configure credentials");
        }
    }

    @Test
    @DisplayName("Should use NoopEmailService when Gmail is disabled")
    void shouldUseNoopEmailServiceByDefault() {
        // When Gmail is disabled, NoopEmailService should be used
        assertNotNull(emailService, "EmailService should not be null");

        if (!gmailConfigured) {
            assertFalse(emailService instanceof GmailEmailService,
                "Should not use GmailEmailService when gmail.enabled=false");
        }
    }

    @Test
    @DisplayName("Should generate email content from template")
    void shouldGenerateEmailContent() {
        // This should work regardless of which implementation is used
        StudentReportEmailDto emailData = emailService.createSampleEmailData("test-student-id");
        String content = emailService.generateEmailContent(emailData);

        assertNotNull(content, "Email content should not be null");
        assertTrue(content.contains("Ahmad Fauzan"), "Email should contain student name");
        assertTrue(content.contains("Tahsin 2"), "Email should contain level name");
    }

    @Test
    @DisplayName("Should send email successfully when Gmail is configured")
    void shouldSendEmailWhenGmailConfigured() {
        // Skip if Gmail is not configured
        assumeTrue(gmailConfigured, "Gmail is not configured, skipping test");

        // Create sample email data
        StudentReportEmailDto emailData = emailService.createSampleEmailData("test-student-id");

        // For testing, use a test email address
        emailData.setRecipientEmail("test@example.com");
        emailData.setRecipientName("Test Recipient");

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            emailService.sendStudentReportEmail(emailData);
        }, "Should send email without throwing exception");
    }

    @Test
    @DisplayName("Should perform health check when Gmail is configured")
    void shouldPerformHealthCheck() {
        // Skip if Gmail is not configured
        assumeTrue(gmailConfigured && healthMonitor != null,
            "Gmail health monitor is not configured, skipping test");

        // Perform manual health check
        GmailHealthMonitor.HealthCheckResult result = healthMonitor.performManualHealthCheck();

        assertNotNull(result, "Health check result should not be null");
        assertNotNull(result.getTimestamp(), "Health check should have timestamp");
        assertNotNull(result.getConfiguredEmail(), "Should have configured email");

        if (result.isHealthy()) {
            assertEquals(0, result.getConsecutiveFailures(),
                "Healthy service should have no consecutive failures");
        }
    }

    @Test
    @DisplayName("Should handle invalid email gracefully")
    void shouldHandleInvalidEmail() {
        StudentReportEmailDto emailData = emailService.createSampleEmailData("test-student-id");
        emailData.setRecipientEmail("invalid-email");
        emailData.setRecipientName("Invalid User");

        if (gmailConfigured) {
            // Gmail should validate email format
            assertThrows(Exception.class, () -> {
                emailService.sendStudentReportEmail(emailData);
            }, "Should throw exception for invalid email format");
        } else {
            // NoopEmailService should not throw exception (just logs)
            assertDoesNotThrow(() -> {
                emailService.sendStudentReportEmail(emailData);
            }, "Noop service should not throw exception");
        }
    }

    @Test
    @DisplayName("Should create sample data with all required fields")
    void shouldCreateCompleteEmailData() {
        StudentReportEmailDto emailData = emailService.createSampleEmailData("test-student-id");

        assertNotNull(emailData, "Email data should not be null");
        assertNotNull(emailData.getStudentName(), "Should have student name");
        assertNotNull(emailData.getLevelName(), "Should have level name");
        assertNotNull(emailData.getTermName(), "Should have term name");
        assertNotNull(emailData.getGrades(), "Should have grades");
        assertFalse(emailData.getGrades().isEmpty(), "Grades should not be empty");
        assertNotNull(emailData.getOverallGrade(), "Should have overall grade");
        assertNotNull(emailData.getAttendanceRate(), "Should have attendance rate");
        assertNotNull(emailData.getTeacherEvaluation(), "Should have teacher evaluation");
        assertNotNull(emailData.getRecipientEmail(), "Should have recipient email");
    }
}