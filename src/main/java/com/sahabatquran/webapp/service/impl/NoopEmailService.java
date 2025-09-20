package com.sahabatquran.webapp.service.impl;

import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import com.sahabatquran.webapp.service.EmailService;
import com.sahabatquran.webapp.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gmail.enabled", havingValue = "false", matchIfMissing = true)
public class NoopEmailService implements EmailService {

    private final EmailTemplateService emailTemplateService;

    @Override
    public String generateEmailContent(StudentReportEmailDto emailData) {
        return emailTemplateService.generateEmailContent(emailData);
    }

    @Override
    public void sendStudentReportEmail(StudentReportEmailDto emailData) {
        try {
            String emailContent = generateEmailContent(emailData);
            String subject = emailTemplateService.generateEmailSubject(emailData);

            // NOOP implementation - just log the email details
            log.info("📧 [NOOP] SIMULATING EMAIL SEND:");
            log.info("To: {} <{}>", emailData.getRecipientName(), emailData.getRecipientEmail());
            log.info("Subject: {}", subject);
            log.info("Content Length: {} characters", emailContent.length());
            log.info("Content Preview: {}", emailContent.substring(0, Math.min(200, emailContent.length())) + "...");
            log.info("📧 [NOOP] EMAIL SEND SIMULATION COMPLETED");

        } catch (Exception e) {
            log.error("Failed to simulate sending student report email to: {}", emailData.getRecipientEmail(), e);
            throw new RuntimeException("Failed to send email", e);
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

    @Override
    public void sendReportNotification(String recipientEmail, String subject, String body, String downloadUrl) {
        try {
            // Replace download URL placeholder in body
            String emailContent = body.replace("{{DOWNLOAD_URL}}", downloadUrl);

            // NOOP implementation - just log the email details
            log.info("📧 [NOOP] SIMULATING REPORT NOTIFICATION:");
            log.info("To: {}", recipientEmail);
            log.info("Subject: {}", subject);
            log.info("Download URL: {}", downloadUrl);
            log.info("Content Length: {} characters", emailContent.length());
            log.info("Content Preview: {}", emailContent.substring(0, Math.min(300, emailContent.length())) + "...");
            log.info("📧 [NOOP] REPORT NOTIFICATION SIMULATION COMPLETED");

        } catch (Exception e) {
            log.error("Failed to simulate sending report notification to: {}", recipientEmail, e);
            throw new RuntimeException("Failed to send report notification", e);
        }
    }
}