package com.sahabatquran.webapp.service.impl;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.sahabatquran.webapp.config.GmailConfig;
import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import com.sahabatquran.webapp.service.EmailService;
import com.sahabatquran.webapp.service.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;

/**
 * Gmail implementation of EmailService.
 *
 * This service sends actual emails using Gmail API with OAuth2 authentication.
 * It uses a refresh token approach to avoid runtime OAuth flows.
 *
 * To use this service:
 * 1. Set gmail.enabled=true in application properties
 * 2. Configure Gmail OAuth2 credentials (client ID, secret, refresh token)
 * 3. This will automatically replace the NoopEmailService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gmail.enabled", havingValue = "true")
public class GmailEmailService implements EmailService {

    private final EmailTemplateService emailTemplateService;
    private final Gmail gmailService;
    private final GmailConfig gmailConfig;

    @Override
    public String generateEmailContent(StudentReportEmailDto emailData) {
        return emailTemplateService.generateEmailContent(emailData);
    }

    @Override
    public void sendStudentReportEmail(StudentReportEmailDto emailData) {
        try {
            // Generate email content and subject
            String emailContent = generateEmailContent(emailData);
            String subject = emailTemplateService.generateEmailSubject(emailData);

            // Create and send the email
            MimeMessage email = createEmail(
                emailData.getRecipientEmail(),
                gmailConfig.getNotificationEmail(),
                subject,
                emailContent
            );

            Message message = sendMessage(email);

            log.info("✅ Email sent successfully to {} with message ID: {}",
                emailData.getRecipientEmail(), message.getId());
            log.debug("Email details - To: {} <{}>, Subject: {}",
                emailData.getRecipientName(), emailData.getRecipientEmail(), subject);

        } catch (Exception e) {
            log.error("❌ Failed to send student report email to: {}", emailData.getRecipientEmail(), e);
            throw new RuntimeException("Failed to send email via Gmail", e);
        }
    }

    @Override
    public StudentReportEmailDto createSampleEmailData(String studentId) {
        // Create sample data for testing/preview
        return StudentReportEmailDto.builder()
            .studentName("Ahmad Fauzan")
            .levelName("Tahsin 2")
            .termName("Semester 1 2024/2025")
            .reportDate("15 September 2025")
            .grades(Arrays.asList(
                StudentReportEmailDto.GradeDto.builder()
                    .subject("Tahsin Al-Quran")
                    .grade("A-")
                    .score("87")
                    .build(),
                StudentReportEmailDto.GradeDto.builder()
                    .subject("Hafalan Surat Pendek")
                    .grade("A")
                    .score("92")
                    .build(),
                StudentReportEmailDto.GradeDto.builder()
                    .subject("Adab & Akhlaq")
                    .grade("B+")
                    .score("85")
                    .build()
            ))
            .overallGrade("A-")
            .overallScore("88")
            .attendanceRate("92")
            .attendanceNotes("Kehadiran sangat baik, hanya absen 2 kali karena sakit")
            .teacherEvaluation("Ahmad menunjukkan kemajuan yang konsisten dalam bacaan Al-Quran. " +
                "Tajwid sudah sangat baik, hafalan lancar. Perlu sedikit perbaikan dalam makhraj huruf 'ra'.")
            .recommendations(Arrays.asList(
                "Tingkatkan latihan makhraj huruf 'ra' di rumah",
                "Lanjutkan hafalan surat Al-Mulk",
                "Pertahankan kedisiplinan dalam mengerjakan tugas"
            ))
            .additionalNotes("Anak yang sangat antusias dan memiliki motivasi tinggi untuk belajar Al-Quran")
            .schoolAddress("Jl. Sahabat Quran No. 123, Jakarta Selatan 12345")
            .schoolPhone("+62-21-1234-5678")
            .schoolEmail("info@sahabatquran.org")
            .parentPortalUrl("https://portal.sahabatquran.org")
            .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")))
            .recipientEmail("parent.ahmad@example.com")
            .recipientName("Bapak/Ibu Ahmad Fauzan")
            .build();
    }

    /**
     * Creates a MIME email message.
     *
     * @param to Recipient email address
     * @param from Sender email address
     * @param subject Email subject
     * @param htmlContent HTML content of the email
     * @return MimeMessage object
     * @throws MessagingException If there's an error creating the message
     */
    private MimeMessage createEmail(String to, String from, String subject, String htmlContent)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // Set HTML content with UTF-8 encoding
        email.setContent(htmlContent, "text/html; charset=utf-8");

        return email;
    }

    /**
     * Sends a message using Gmail API.
     *
     * @param emailContent MIME message to send
     * @return Sent message
     * @throws MessagingException If there's an error with the message
     * @throws IOException If there's an error sending the message
     */
    private Message sendMessage(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        try {
            message = gmailService.users().messages().send("me", message).execute();
            log.debug("Message sent with ID: {}", message.getId());
            return message;
        } catch (IOException e) {
            log.error("Failed to send message via Gmail API", e);
            throw e;
        }
    }

    /**
     * Health check for Gmail service.
     *
     * @return true if service is healthy and ready to send emails
     */
    public boolean isHealthy() {
        if (gmailService == null) {
            log.warn("Gmail service is not initialized");
            return false;
        }

        try {
            // Try to get user profile as a health check
            gmailService.users().getProfile("me").execute();
            log.debug("Gmail service health check passed");
            return true;
        } catch (Exception e) {
            log.error("Gmail service health check failed", e);
            return false;
        }
    }
}