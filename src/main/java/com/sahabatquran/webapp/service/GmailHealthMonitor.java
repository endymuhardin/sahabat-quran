package com.sahabatquran.webapp.service;

import com.google.api.services.gmail.Gmail;
import com.sahabatquran.webapp.config.GmailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gmail Health Monitor Service
 *
 * Monitors Gmail service health and keeps the refresh token active.
 * Gmail refresh tokens expire after 6 months of inactivity, so this service
 * performs periodic health checks to prevent expiration.
 *
 * Features:
 * - Daily health checks to keep token active
 * - Alerts on service failures
 * - Token expiration prevention
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gmail.enabled", havingValue = "true")
public class GmailHealthMonitor {

    private final Gmail gmailService;
    private final GmailConfig gmailConfig;

    private LocalDateTime lastSuccessfulCheck;
    private int consecutiveFailures = 0;
    private static final int MAX_CONSECUTIVE_FAILURES = 3;

    /**
     * Performs daily health check on Gmail service.
     * Runs at 2 AM every day to avoid peak hours.
     *
     * This check serves two purposes:
     * 1. Monitor service availability
     * 2. Keep refresh token active (prevents 6-month expiration)
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void performDailyHealthCheck() {
        log.info("🔍 Starting Gmail service health check...");

        try {
            // Perform health check
            boolean isHealthy = checkServiceHealth();

            if (isHealthy) {
                handleSuccessfulCheck();
            } else {
                handleFailedCheck("Service health check returned false");
            }

        } catch (Exception e) {
            handleFailedCheck("Exception during health check: " + e.getMessage());
        }
    }

    /**
     * Performs hourly quick health check during business hours.
     * Runs every hour from 8 AM to 6 PM on weekdays.
     */
    @Scheduled(cron = "0 0 8-18 * * MON-FRI") // Every hour during business hours
    public void performBusinessHoursCheck() {
        log.debug("⚡ Quick Gmail service check during business hours");

        try {
            if (gmailService != null) {
                // Just verify the service is responsive
                gmailService.users().getProfile("me").setFields("emailAddress").execute();
                log.debug("Gmail service is responsive");
                consecutiveFailures = 0;
            }
        } catch (Exception e) {
            log.warn("Quick health check failed: {}", e.getMessage());
            consecutiveFailures++;

            if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                log.error("❌ Gmail service has failed {} consecutive checks!", consecutiveFailures);
                // Here you could trigger alerts, send notifications, etc.
            }
        }
    }

    /**
     * Performs the actual health check on Gmail service.
     *
     * @return true if service is healthy
     */
    private boolean checkServiceHealth() {
        if (gmailService == null) {
            log.error("Gmail service is null - not properly initialized");
            return false;
        }

        try {
            // Get user profile to verify authentication
            var profile = gmailService.users().getProfile("me").execute();

            log.debug("Gmail profile retrieved: {}", profile.getEmailAddress());

            // Verify it matches our configured email
            if (!profile.getEmailAddress().equalsIgnoreCase(gmailConfig.getNotificationEmail())) {
                log.warn("Email mismatch - configured: {}, actual: {}",
                    gmailConfig.getNotificationEmail(), profile.getEmailAddress());
            }

            // Get quota information (optional, may require additional scopes)
            try {
                var labels = gmailService.users().labels().list("me").execute();
                log.debug("Gmail account has {} labels", labels.getLabels().size());
            } catch (Exception e) {
                log.debug("Could not retrieve labels (may need additional scopes): {}", e.getMessage());
            }

            return true;

        } catch (Exception e) {
            log.error("Gmail health check failed", e);
            return false;
        }
    }

    /**
     * Handles successful health check.
     */
    private void handleSuccessfulCheck() {
        lastSuccessfulCheck = LocalDateTime.now();
        consecutiveFailures = 0;

        log.info("✅ Gmail service health check passed at {}",
            lastSuccessfulCheck.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Log token keep-alive success
        log.info("🔄 Refresh token keep-alive successful - token will remain valid");
    }

    /**
     * Handles failed health check.
     *
     * @param reason Reason for failure
     */
    private void handleFailedCheck(String reason) {
        consecutiveFailures++;

        log.error("❌ Gmail service health check failed: {}", reason);
        log.error("Consecutive failures: {}/{}", consecutiveFailures, MAX_CONSECUTIVE_FAILURES);

        if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
            log.error("🚨 CRITICAL: Gmail service has failed {} consecutive health checks!", consecutiveFailures);
            log.error("Action required: Check Gmail credentials and service configuration");

            // Here you could:
            // - Send alert emails (using a backup service)
            // - Post to Slack/Discord
            // - Create a system notification
            // - Switch to fallback email service
        }

        if (lastSuccessfulCheck != null) {
            log.warn("Last successful check was at: {}",
                lastSuccessfulCheck.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    /**
     * Manual health check that can be called programmatically.
     *
     * @return Health check result
     */
    public HealthCheckResult performManualHealthCheck() {
        log.info("🔍 Manual Gmail health check requested");

        boolean isHealthy = checkServiceHealth();

        return HealthCheckResult.builder()
            .healthy(isHealthy)
            .lastSuccessfulCheck(lastSuccessfulCheck)
            .consecutiveFailures(consecutiveFailures)
            .configuredEmail(gmailConfig.getNotificationEmail())
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Health check result DTO.
     */
    @lombok.Builder
    @lombok.Data
    public static class HealthCheckResult {
        private boolean healthy;
        private LocalDateTime lastSuccessfulCheck;
        private int consecutiveFailures;
        private String configuredEmail;
        private LocalDateTime timestamp;
    }
}