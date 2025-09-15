package com.sahabatquran.webapp.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

/**
 * Gmail API Configuration
 *
 * Configures Gmail service using refresh token approach for OAuth2 authentication.
 * This allows server-side email sending without runtime OAuth flows.
 *
 * Required environment variables:
 * - GMAIL_CLIENT_ID: OAuth2 client ID from Google Cloud Console
 * - GMAIL_CLIENT_SECRET: OAuth2 client secret
 * - GMAIL_REFRESH_TOKEN: Long-lived refresh token (generated once)
 * - GMAIL_NOTIFICATION_EMAIL: Sender email address
 */
@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "gmail.enabled", havingValue = "true", matchIfMissing = false)
public class GmailConfig {

    private static final String APPLICATION_NAME = "Sahabat Quran Web Application";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String GMAIL_SCOPE = "https://www.googleapis.com/auth/gmail.send";

    @Value("${gmail.client-id:#{null}}")
    private String clientId;

    @Value("${gmail.client-secret:#{null}}")
    private String clientSecret;

    @Value("${gmail.refresh-token:#{null}}")
    private String refreshToken;

    @Value("${gmail.notification-email:#{null}}")
    private String notificationEmail;

    @Value("${gmail.application-name:Sahabat Quran}")
    private String applicationName;

    /**
     * Creates Gmail service bean with OAuth2 authentication using refresh token.
     *
     * @return Configured Gmail service instance
     * @throws GeneralSecurityException If there's a security error
     * @throws IOException If there's an I/O error
     */
    @Bean
    public Gmail gmailService() throws GeneralSecurityException, IOException {
        if (!isGmailConfigured()) {
            log.warn("Gmail configuration is incomplete. Gmail service will not be available.");
            return null;
        }

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Create credentials from refresh token
        GoogleCredentials credentials = createCredentialsFromRefreshToken();

        // Build Gmail service
        Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();

        log.info("Gmail service initialized successfully for account: {}", notificationEmail);
        return service;
    }

    /**
     * Creates GoogleCredentials from refresh token.
     * This method constructs the OAuth2 credentials JSON and creates credentials
     * that will automatically refresh the access token when needed.
     *
     * @return GoogleCredentials instance
     * @throws IOException If there's an error creating credentials
     */
    private GoogleCredentials createCredentialsFromRefreshToken() throws IOException {
        // Build OAuth2 credentials JSON
        String credentialsJson = String.format("""
            {
              "type": "authorized_user",
              "client_id": "%s",
              "client_secret": "%s",
              "refresh_token": "%s"
            }
            """, clientId, clientSecret, refreshToken);

        // Create credentials from JSON
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(credentialsJson.getBytes()))
                .createScoped(Collections.singletonList(GMAIL_SCOPE));

        // Refresh the access token to ensure it's valid
        try {
            credentials.refreshIfExpired();
            log.debug("Gmail credentials refreshed successfully");
        } catch (IOException e) {
            log.error("Failed to refresh Gmail credentials", e);
            throw e;
        }

        return credentials;
    }

    /**
     * Checks if Gmail configuration is complete.
     *
     * @return true if all required configuration is present
     */
    public boolean isGmailConfigured() {
        boolean configured = clientId != null && !clientId.isEmpty() &&
                           clientSecret != null && !clientSecret.isEmpty() &&
                           refreshToken != null && !refreshToken.isEmpty() &&
                           notificationEmail != null && !notificationEmail.isEmpty();

        if (!configured) {
            log.debug("Gmail configuration missing: clientId={}, clientSecret={}, refreshToken={}, email={}",
                    clientId != null, clientSecret != null, refreshToken != null, notificationEmail != null);
        }

        return configured;
    }

    /**
     * Gets the configured notification email address.
     *
     * @return Notification email address
     */
    public String getNotificationEmail() {
        return notificationEmail;
    }

    /**
     * Health check for Gmail service.
     * Tests if the service can authenticate and is ready to send emails.
     *
     * @param gmailService Gmail service to check
     * @return true if service is healthy
     */
    public boolean isServiceHealthy(Gmail gmailService) {
        if (gmailService == null) {
            return false;
        }

        try {
            // Try to get user profile as a health check
            gmailService.users().getProfile("me").execute();
            return true;
        } catch (Exception e) {
            log.error("Gmail service health check failed", e);
            return false;
        }
    }
}