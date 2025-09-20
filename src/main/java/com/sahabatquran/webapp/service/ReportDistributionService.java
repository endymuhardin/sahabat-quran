package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for distributing generated reports to stakeholders
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportDistributionService {

    private final ReportGenerationBatchRepository batchRepository;
    private final ReportGenerationItemRepository itemRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Value("${app.reports.base-url:http://localhost:8080/reports}")
    private String reportsBaseUrl;

    @Value("${app.notifications.enabled:true}")
    private boolean notificationsEnabled;

    /**
     * Distribute all completed reports in a batch
     */
    @Async
    @Transactional
    public CompletableFuture<Void> distributeCompletedBatch(ReportGenerationBatch batch) {
        log.info("Starting distribution for batch: {}", batch.getId());

        try {
            List<ReportGenerationItem> completedItems = itemRepository
                    .findByBatchAndStatusOrderByPriorityAsc(batch, ReportGenerationItem.ItemStatus.COMPLETED);

            int distributed = 0;
            int failed = 0;

            for (ReportGenerationItem item : completedItems) {
                try {
                    distributeReportItem(item);
                    distributed++;
                } catch (Exception e) {
                    log.error("Failed to distribute report item: {}", item.getId(), e);
                    failed++;
                }
            }

            // Update batch distribution status
            batch.setDistributionCompleted(true);
            batch.setDistributionCompletedAt(LocalDateTime.now());
            batchRepository.save(batch);

            log.info("Distribution completed for batch: {} - Distributed: {}, Failed: {}",
                    batch.getId(), distributed, failed);

        } catch (Exception e) {
            log.error("Error distributing batch: {}", batch.getId(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Distribute a single report item
     */
    @Transactional
    public void distributeReportItem(ReportGenerationItem item) {
        log.info("Distributing report item: {}", item.getId());

        if (!notificationsEnabled) {
            log.info("Notifications disabled, skipping distribution");
            return;
        }

        try {
            String distributionMethod = determineDistributionMethod(item);
            String recipientEmail = getRecipientEmail(item);

            if (recipientEmail == null) {
                log.warn("No recipient email found for item: {}", item.getId());
                return;
            }

            switch (distributionMethod) {
                case "EMAIL":
                    distributeViaEmail(item, recipientEmail);
                    break;
                case "PORTAL":
                    distributeViaPortal(item);
                    break;
                default:
                    log.warn("Unknown distribution method: {}", distributionMethod);
                    return;
            }

            // Mark as distributed
            item.setDistributed(true);
            item.setDistributedAt(LocalDateTime.now());
            item.setDistributionMethod(distributionMethod);
            item.setRecipientEmail(recipientEmail);
            itemRepository.save(item);

            log.info("Successfully distributed report item: {} via {}", item.getId(), distributionMethod);

        } catch (Exception e) {
            log.error("Error distributing report item: {}", item.getId(), e);
            throw e;
        }
    }

    /**
     * Distribute via email
     */
    private void distributeViaEmail(ReportGenerationItem item, String recipientEmail) {
        String subject = generateEmailSubject(item);
        String body = generateEmailBody(item);
        String downloadUrl = generateDownloadUrl(item);

        // Send email with download link
        emailService.sendReportNotification(recipientEmail, subject, body, downloadUrl);
    }

    /**
     * Distribute via portal notification
     */
    private void distributeViaPortal(ReportGenerationItem item) {
        // This would integrate with a notification system
        // For now, we'll just log the action
        log.info("Portal notification sent for report item: {}", item.getId());
    }

    /**
     * Determine distribution method based on report type and recipient
     */
    private String determineDistributionMethod(ReportGenerationItem item) {
        // Student reports and parent notifications -> EMAIL
        if (item.getStudent() != null) {
            return "EMAIL";
        }

        // Teacher and management reports -> PORTAL (with email notification)
        if (item.getTeacher() != null || item.getReportSubject().contains("Management")) {
            return "EMAIL";
        }

        return "EMAIL"; // Default
    }

    /**
     * Get recipient email address
     */
    private String getRecipientEmail(ReportGenerationItem item) {
        // For student reports, send to parent email
        if (item.getStudent() != null) {
            return getParentEmail(item.getStudent());
        }

        // For teacher reports, send to teacher email
        if (item.getTeacher() != null) {
            return item.getTeacher().getEmail();
        }

        // For management reports, send to management email
        if (item.getReportSubject().contains("Management")) {
            return getManagementEmail();
        }

        return null;
    }

    /**
     * Get parent email for a student
     */
    private String getParentEmail(User student) {
        // This would be based on your data model
        // For now, we'll use a convention-based approach
        return student.getEmail(); // Assuming student email is parent's email
    }

    /**
     * Get management email
     */
    private String getManagementEmail() {
        // This should be configurable
        return "management@sahabatquran.org";
    }

    /**
     * Generate email subject
     */
    private String generateEmailSubject(ReportGenerationItem item) {
        String termName = item.getBatch().getTerm().getTermName();

        if (item.getStudent() != null) {
            return String.format("Rapor Semester - %s - %s",
                    item.getStudent().getFullName(), termName);
        }

        if (item.getTeacher() != null) {
            return String.format("Ringkasan Kelas - %s - %s",
                    item.getClassGroup().getName(), termName);
        }

        if (item.getReportSubject().contains("Management")) {
            return String.format("Laporan Eksekutif - %s", termName);
        }

        return String.format("Laporan - %s", item.getReportSubject());
    }

    /**
     * Generate email body
     */
    private String generateEmailBody(ReportGenerationItem item) {
        StringBuilder body = new StringBuilder();
        String termName = item.getBatch().getTerm().getTermName();

        body.append("Assalamu'alaikum Warahmatullahi Wabarakatuh,\n\n");

        if (item.getStudent() != null) {
            body.append(String.format("Kami dengan senang hati menyampaikan rapor semester untuk %s " +
                            "pada %s.\n\n",
                    item.getStudent().getFullName(), termName));
            body.append("Rapor ini berisi informasi lengkap mengenai:\n");
            body.append("- Nilai dan pencapaian akademik\n");
            body.append("- Kehadiran dan partisipasi\n");
            body.append("- Evaluasi dari ustadz/ustadzah\n");
            body.append("- Rekomendasi untuk semester berikutnya\n\n");
        } else if (item.getTeacher() != null) {
            body.append(String.format("Berikut adalah ringkasan kinerja kelas %s " +
                            "pada %s.\n\n",
                    item.getClassGroup().getName(), termName));
            body.append("Laporan ini mencakup:\n");
            body.append("- Statistik kelas secara keseluruhan\n");
            body.append("- Analisis pencapaian siswa\n");
            body.append("- Evaluasi metode pengajaran\n");
            body.append("- Rekomendasi untuk perbaikan\n\n");
        } else {
            body.append(String.format("Berikut adalah laporan yang Anda minta untuk %s.\n\n", termName));
        }

        body.append("Silakan klik tautan di bawah ini untuk mengunduh laporan:\n");
        body.append("{{DOWNLOAD_URL}}\n\n");

        body.append("Jika Anda memiliki pertanyaan atau memerlukan penjelasan lebih lanjut, " +
                "jangan ragu untuk menghubungi kami.\n\n");

        body.append("Barakallahu fiikum,\n");
        body.append("Tim Yayasan Sahabat Quran");

        return body.toString();
    }

    /**
     * Generate download URL for report
     */
    private String generateDownloadUrl(ReportGenerationItem item) {
        return String.format("%s/download/%s", reportsBaseUrl, item.getId());
    }

    /**
     * Send reminder for unclaimed reports
     */
    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Void> sendReportReminders() {
        log.info("Sending reminders for unclaimed reports");

        List<ReportGenerationItem> unclaimedItems = itemRepository
                .findCompletedItemsAwaitingDistribution();

        // Filter items older than 3 days
        LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
        List<ReportGenerationItem> overdueItems = unclaimedItems.stream()
                .filter(item -> item.getCompletedAt().isBefore(cutoff))
                .toList();

        for (ReportGenerationItem item : overdueItems) {
            try {
                sendReminderEmail(item);
            } catch (Exception e) {
                log.error("Failed to send reminder for item: {}", item.getId(), e);
            }
        }

        log.info("Sent {} reminders", overdueItems.size());
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Send reminder email
     */
    private void sendReminderEmail(ReportGenerationItem item) {
        String recipientEmail = getRecipientEmail(item);
        if (recipientEmail == null) {
            return;
        }

        String subject = "Pengingat: " + generateEmailSubject(item);
        String body = generateReminderEmailBody(item);
        String downloadUrl = generateDownloadUrl(item);

        emailService.sendReportNotification(recipientEmail, subject, body, downloadUrl);
    }

    /**
     * Generate reminder email body
     */
    private String generateReminderEmailBody(ReportGenerationItem item) {
        StringBuilder body = new StringBuilder();

        body.append("Assalamu'alaikum Warahmatullahi Wabarakatuh,\n\n");
        body.append("Kami ingin mengingatkan bahwa laporan berikut masih tersedia untuk diunduh:\n\n");
        body.append(String.format("- %s\n", item.getReportSubject()));
        body.append(String.format("- Dibuat: %s\n",
                item.getCompletedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"))));

        body.append("\nSilakan klik tautan di bawah ini untuk mengunduh:\n");
        body.append("{{DOWNLOAD_URL}}\n\n");

        body.append("Laporan ini akan tersedia hingga akhir bulan ini.\n\n");

        body.append("Barakallahu fiikum,\n");
        body.append("Tim Yayasan Sahabat Quran");

        return body.toString();
    }

    /**
     * Get distribution statistics for a batch
     */
    @Transactional(readOnly = true)
    public DistributionStats getDistributionStats(ReportGenerationBatch batch) {
        List<ReportGenerationItem> items = itemRepository
                .findByBatchOrderByPriorityAscStartedAtAsc(batch);

        int totalItems = items.size();
        int distributedItems = (int) items.stream()
                .filter(ReportGenerationItem::getDistributed)
                .count();

        int emailDistributions = (int) items.stream()
                .filter(item -> "EMAIL".equals(item.getDistributionMethod()))
                .count();

        int portalDistributions = (int) items.stream()
                .filter(item -> "PORTAL".equals(item.getDistributionMethod()))
                .count();

        return DistributionStats.builder()
                .totalItems(totalItems)
                .distributedItems(distributedItems)
                .pendingItems(totalItems - distributedItems)
                .emailDistributions(emailDistributions)
                .portalDistributions(portalDistributions)
                .distributionPercentage((distributedItems * 100.0) / totalItems)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class DistributionStats {
        private int totalItems;
        private int distributedItems;
        private int pendingItems;
        private int emailDistributions;
        private int portalDistributions;
        private double distributionPercentage;
    }
}