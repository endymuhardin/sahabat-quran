package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.StudentReportEmailDto;

public interface EmailService {

    /**
     * Generate email content from template and data
     */
    String generateEmailContent(StudentReportEmailDto emailData);

    /**
     * Send student report email
     */
    void sendStudentReportEmail(StudentReportEmailDto emailData);

    /**
     * Create sample email data for preview/testing
     */
    StudentReportEmailDto createSampleEmailData(String studentId);

    /**
     * Send report notification with download link
     */
    void sendReportNotification(String recipientEmail, String subject, String body, String downloadUrl);
}