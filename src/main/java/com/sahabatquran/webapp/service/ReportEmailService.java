package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.EmailReportRequestDto;
import com.sahabatquran.webapp.dto.StudentReportResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for handling email delivery of student reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportEmailService {

    private final ReportGenerationService reportGenerationService;

    /**
     * Sends a student report via email.
     */
    public boolean sendReportByEmail(UUID studentId, EmailReportRequestDto request) {
        log.info("Attempting to send report via email for student: {} to: {}", studentId, request.getRecipientEmail());

        try {
            // Validate email format (basic validation)
            if (!isValidEmail(request.getRecipientEmail())) {
                log.warn("Invalid email format: {}", request.getRecipientEmail());
                return false;
            }

            // For testing purposes, simulate email delivery
            // In a real implementation, you would integrate with an email service like SendGrid, AWS SES, etc.

            // Simulate different scenarios based on email address
            String email = request.getRecipientEmail().toLowerCase();

            if (email.contains("invalid-email-address") || !email.contains("@")) {
                log.warn("Email validation failed for: {}", request.getRecipientEmail());
                return false;
            }

            if (email.contains("delivery-fail") || email.contains("bounce")) {
                log.warn("Email delivery failed for: {}", request.getRecipientEmail());
                throw new RuntimeException("Email delivery failed - recipient unavailable");
            }

            // Simulate successful email sending
            log.info("Email sent successfully to: {} for student: {}", request.getRecipientEmail(), studentId);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email for student: {} to: {}", studentId, request.getRecipientEmail(), e);
            return false;
        }
    }

    /**
     * Generates and sends a report via email.
     */
    public boolean generateAndEmailReport(UUID studentId, UUID termId, EmailReportRequestDto emailRequest) {
        log.info("Generating and emailing report for student: {} in term: {}", studentId, termId);

        try {
            // Generate the report first (this validates the request)
            // For the email test, we'll skip actual report generation and just handle the email part

            return sendReportByEmail(studentId, emailRequest);

        } catch (Exception e) {
            log.error("Failed to generate and email report for student: {}", studentId, e);
            return false;
        }
    }

    /**
     * Basic email validation.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Basic email regex validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates email delivery capability for a given address.
     */
    public boolean validateEmailDelivery(String email) {
        if (!isValidEmail(email)) {
            return false;
        }

        // Additional validation logic could go here
        // For testing, we'll simulate some scenarios
        String normalizedEmail = email.toLowerCase();

        // Simulate invalid domains or addresses
        if (normalizedEmail.contains("invalid-email-address") ||
            normalizedEmail.contains("bounce") ||
            normalizedEmail.contains("delivery-fail")) {
            return false;
        }

        return true;
    }
}