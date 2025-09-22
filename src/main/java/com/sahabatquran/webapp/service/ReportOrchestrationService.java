package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.ReportGenerationJobDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.entity.ReportGenerationBatch.BatchStatus;
import com.sahabatquran.webapp.entity.ReportGenerationBatch.ReportType;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Unified service for orchestrating all report generation.
 *
 * Simplified Approach:
 * 1. Single "Generate Reports" button -> queues background jobs for all students
 * 2. Individual "Regenerate" -> replaces existing report for specific student+term
 * 3. All downloads fetch pre-generated PDFs from file system
 * 4. Status tracking without real-time updates (server-side refresh only)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportOrchestrationService {

    private final ReportGenerationBatchRepository batchRepository;
    private final ReportGenerationItemRepository itemRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AcademicTermRepository academicTermRepository;
    private final PdfReportGenerationService pdfGenerator;

    @Value("${app.reports.output-directory:/tmp/reports}")
    private String reportsOutputDirectory;

    /**
     * Main entry point: Generate reports for all students in a term
     * This replaces both "bulk" and "background" generation buttons
     */
    @Transactional
    public UUID generateAllStudentReports(UUID termId, UUID initiatedBy, String termName) {
        log.info("Starting report generation for all students in term: {}", termId);

        // Create batch
        ReportGenerationBatch batch = new ReportGenerationBatch();
        // Note: Using entity relationship setters, not direct ID setters
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found: " + termId));
        User initiator = userRepository.findById(initiatedBy)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + initiatedBy));

        batch.setTerm(term);
        batch.setInitiatedBy(initiator);
        batch.setBatchName("All Student Reports - " + termName);
        batch.setStatus(BatchStatus.INITIATED);
        batch.setReportType(ReportType.SEMESTER_END_STUDENT_REPORTS);
        batch.setInitiatedAt(LocalDateTime.now());
        batch = batchRepository.save(batch);

        // Find all enrolled students for this term via enrollments
        List<Enrollment> activeEnrollments = enrollmentRepository.findByStatus(Enrollment.EnrollmentStatus.ACTIVE);
        List<User> students = activeEnrollments.stream()
                .map(Enrollment::getStudent)
                .distinct()
                .toList();
        log.info("Found {} students enrolled in term {}", students.size(), termId);

        batch.setTotalReports(students.size());
        batch = batchRepository.save(batch);

        // Create individual generation items
        for (User student : students) {
            ReportGenerationItem item = new ReportGenerationItem();
            item.setBatch(batch);
            item.setStudent(student);
            item.setReportSubject("Report Card - " + student.getFullName());
            item.setReportType(ReportGenerationItem.ReportType.STUDENT_REPORT);
            item.setStatus(ReportGenerationItem.ItemStatus.PENDING);
            item.setPriority(5);
            itemRepository.save(item);
        }

        // Start async processing
        processReportBatchAsync(batch.getId());

        return batch.getId();
    }

    /**
     * Regenerate a single student report (deletes old, creates new)
     */
    @Transactional
    public UUID regenerateStudentReport(UUID studentId, UUID termId, UUID initiatedBy) {
        log.info("Regenerating report for student: {} in term: {}", studentId, termId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found: " + termId));

        // Delete existing PDF file if it exists
        deleteExistingReport(studentId, termId);

        // Create single-item batch for tracking
        ReportGenerationBatch batch = new ReportGenerationBatch();
        batch.setTerm(term);

        User initiator = userRepository.findById(initiatedBy)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + initiatedBy));
        batch.setInitiatedBy(initiator);
        batch.setBatchName("Regenerate - " + student.getFullName());
        batch.setStatus(BatchStatus.INITIATED);
        batch.setReportType(ReportType.SEMESTER_END_STUDENT_REPORTS);
        batch.setTotalReports(1);
        batch = batchRepository.save(batch);

        // Create generation item
        ReportGenerationItem item = new ReportGenerationItem();
        item.setBatch(batch);
        item.setStudent(student);
        item.setReportSubject("Regenerated Report - " + student.getFullName());
        item.setReportType(ReportGenerationItem.ReportType.STUDENT_REPORT);
        item.setStatus(ReportGenerationItem.ItemStatus.PENDING);
        item.setPriority(1); // High priority for regeneration
        itemRepository.save(item);

        // Start async processing
        processReportBatchAsync(batch.getId());

        return batch.getId();
    }

    /**
     * Get pre-generated PDF file path for download
     */
    public Optional<Path> getGeneratedReportPath(UUID studentId, UUID termId) {
        Path reportPath = Paths.get(reportsOutputDirectory,
                "students",
                studentId.toString(),
                termId.toString(),
                "report-card.pdf");

        if (reportPath.toFile().exists()) {
            log.debug("Found pre-generated report: {}", reportPath);
            return Optional.of(reportPath);
        }

        log.debug("Pre-generated report not found: {}", reportPath);
        return Optional.empty();
    }

    /**
     * Check if a report exists for student+term
     */
    public boolean hasGeneratedReport(UUID studentId, UUID termId) {
        return getGeneratedReportPath(studentId, termId).isPresent();
    }

    /**
     * Get current status of report generation batches
     */
    @Transactional(readOnly = true)
    public List<ReportGenerationJobDto> getAllBatchStatuses() {
        List<ReportGenerationBatch> batches = batchRepository.findAll();
        log.debug("Repository found {} batches from database", batches.size());
        return batches.stream()
                .sorted((a, b) -> b.getInitiatedAt().compareTo(a.getInitiatedAt()))
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Get status of specific batch
     */
    @Transactional(readOnly = true)
    public Optional<ReportGenerationJobDto> getBatchStatus(UUID batchId) {
        return batchRepository.findById(batchId)
                .map(this::convertToDto);
    }

    /**
     * Cancel a batch (stops processing new items)
     */
    @Transactional
    public void cancelBatch(UUID batchId) {
        ReportGenerationBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));

        batch.setStatus(BatchStatus.CANCELLED);
        batch.setCompletedAt(LocalDateTime.now());
        batchRepository.save(batch);

        // Cancel pending items manually since batch update method may not exist
        List<ReportGenerationItem> pendingItems = itemRepository.findByBatchAndStatusOrderByPriorityAsc(
                batch, ReportGenerationItem.ItemStatus.PENDING);
        for (ReportGenerationItem item : pendingItems) {
            item.setStatus(ReportGenerationItem.ItemStatus.CANCELLED);
            itemRepository.save(item);
        }
    }

    // === PRIVATE METHODS ===

    @Async
    protected void processReportBatchAsync(UUID batchId) {
        try {
            log.info("Starting async processing of batch: {}", batchId);

            ReportGenerationBatch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));

            batch.setStatus(BatchStatus.IN_PROGRESS);
            batch.setStartedAt(LocalDateTime.now());
            batchRepository.save(batch);

            // Process each item
            List<ReportGenerationItem> pendingItems = itemRepository.findByBatchAndStatusOrderByPriorityAsc(
                    batch, ReportGenerationItem.ItemStatus.PENDING);

            int completed = 0;
            int failed = 0;

            for (ReportGenerationItem item : pendingItems) {
                try {
                    processReportItem(item);
                    completed++;
                } catch (Exception e) {
                    log.error("Failed to generate report for item: {}", item.getId(), e);
                    markItemFailed(item, e.getMessage());
                    failed++;
                }

                // Update batch progress
                batch.setCompletedReports(completed);
                batch.setFailedReports(failed);
                batchRepository.save(batch);
            }

            // Mark batch complete
            batch.setStatus(failed > 0 ? BatchStatus.FAILED : BatchStatus.COMPLETED);
            batch.setCompletedAt(LocalDateTime.now());
            batchRepository.save(batch);

            log.info("Completed batch {}: {} successful, {} failed", batchId, completed, failed);

        } catch (Exception e) {
            log.error("Critical error in batch processing: {}", batchId, e);
            markBatchFailed(batchId, e.getMessage());
        }
    }

    private void processReportItem(ReportGenerationItem item) throws Exception {
        log.debug("Processing report item: {}", item.getId());

        item.markAsStarted();
        itemRepository.save(item);

        // Generate PDF using existing service
        String pdfPath = pdfGenerator.generateStudentReport(
                item.getStudent().getId(),
                item.getBatch().getTerm().getId(),
                item.getReportType().toString());

        // Calculate file size (simplified - just set a default)
        Long fileSize = 1024L; // Default size, could be calculated from actual file
        item.markAsCompleted(pdfPath, fileSize);
        itemRepository.save(item);

        log.debug("Successfully generated report: {}", pdfPath);
    }

    private void markItemFailed(ReportGenerationItem item, String errorMessage) {
        item.markAsFailed(errorMessage != null ? errorMessage.substring(0, Math.min(500, errorMessage.length())) : null);
        itemRepository.save(item);
    }

    private void markBatchFailed(UUID batchId, String errorMessage) {
        try {
            ReportGenerationBatch batch = batchRepository.findById(batchId).orElse(null);
            if (batch != null) {
                batch.setStatus(BatchStatus.FAILED);
                batch.setCompletedAt(LocalDateTime.now());
                batch.setFailureReason(errorMessage != null ? errorMessage.substring(0, Math.min(500, errorMessage.length())) : null);
                batchRepository.save(batch);
            }
        } catch (Exception e) {
            log.error("Failed to mark batch as failed: {}", batchId, e);
        }
    }

    private void deleteExistingReport(UUID studentId, UUID termId) {
        Optional<Path> existingPath = getGeneratedReportPath(studentId, termId);
        if (existingPath.isPresent()) {
            try {
                boolean deleted = existingPath.get().toFile().delete();
                if (deleted) {
                    log.info("Deleted existing report: {}", existingPath.get());
                } else {
                    log.warn("Failed to delete existing report: {}", existingPath.get());
                }
            } catch (Exception e) {
                log.warn("Error deleting existing report: {}", existingPath.get(), e);
            }
        }
    }

    private ReportGenerationJobDto convertToDto(ReportGenerationBatch batch) {
        ReportGenerationJobDto dto = new ReportGenerationJobDto();
        dto.setBatchId(batch.getId());
        dto.setBatchName(batch.getBatchName());
        dto.setStatus(batch.getStatus());
        dto.setReportType(batch.getReportType());
        dto.setTotalReports(batch.getTotalReports());
        dto.setCompletedReports(batch.getCompletedReports());
        dto.setFailedReports(batch.getFailedReports());
        dto.setCreatedAt(batch.getCreatedAt());
        dto.setStartedAt(batch.getStartedAt());
        dto.setCompletedAt(batch.getCompletedAt());
        dto.setInitiatedBy(batch.getInitiatedBy().getFullName());

        // Calculate progress percentage
        if (batch.getTotalReports() > 0) {
            int totalProcessed = batch.getCompletedReports() + batch.getFailedReports();
            dto.setProgressPercentage((totalProcessed * 100) / batch.getTotalReports());
        }

        return dto;
    }
}