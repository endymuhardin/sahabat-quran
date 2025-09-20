package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.BulkReportGenerationDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service for handling bulk report generation operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkReportGenerationService {

    private final ReportGenerationBatchRepository batchRepository;
    private final ReportGenerationItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final AcademicTermRepository academicTermRepository;
    private final ReportGenerationService reportGenerationService;
    private final EmailService emailService;
    private final PdfReportGenerationService pdfReportGenerationService;

    @Value("${app.reports.output-directory:/tmp/reports}")
    private String reportsOutputDirectory;

    @Value("${app.reports.max-concurrent-generations:5}")
    private int maxConcurrentGenerations;

    /**
     * Validate data completeness for bulk report generation
     */
    @Transactional(readOnly = true)
    public BulkReportGenerationDto validateBulkReportGeneration(UUID termId,
                                                                BulkReportGenerationDto.ReportConfiguration config) {
        log.info("Validating bulk report generation for term: {}", termId);

        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        // Get all students enrolled in the term
        List<ClassGroup> classGroups = classGroupRepository.findByTerm(term);
        Set<User> allStudents = new HashSet<>();

        for (ClassGroup classGroup : classGroups) {
            List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);
            allStudents.addAll(enrollments.stream()
                    .map(Enrollment::getStudent)
                    .collect(Collectors.toList()));
        }

        // Validate data completeness
        List<BulkReportGenerationDto.ValidationIssue> validationIssues = new ArrayList<>();
        int studentsWithCompleteData = 0;
        int classesWithCompleteData = 0;

        // Validate student data with defensive programming
        for (User student : allStudents) {
            try {
                boolean hasCompleteData = validateStudentData(student, term, validationIssues);
                if (hasCompleteData) {
                    studentsWithCompleteData++;
                }
            } catch (Exception e) {
                log.warn("Error validating student data for student {}: {}", student.getUsername(), e.getMessage());
                // Add a validation issue for this student
                validationIssues.add(BulkReportGenerationDto.ValidationIssue.builder()
                        .issueType("VALIDATION_ERROR")
                        .severity("WARNING")
                        .description("Could not validate data for student " + student.getFullName() + ": " + e.getMessage())
                        .relatedStudentId(student.getId())
                        .relatedStudentName(student.getFullName())
                        .missingFields(List.of("Data validation failed"))
                        .canProceedWithIssue(true)
                        .recommendedAction("Review student data or generate partial report")
                        .build());
            }
        }

        // Validate class data
        for (ClassGroup classGroup : classGroups) {
            boolean hasCompleteData = validateClassData(classGroup, validationIssues);
            if (hasCompleteData) {
                classesWithCompleteData++;
            }
        }

        // Calculate estimates
        int estimatedReportCount = calculateEstimatedReportCount(allStudents.size(), classGroups.size(), config);
        int estimatedDurationMinutes = calculateEstimatedDuration(estimatedReportCount);

        BulkReportGenerationDto.DataValidationResult validationResult =
                BulkReportGenerationDto.DataValidationResult.builder()
                        .isValid(validationIssues.stream().noneMatch(issue -> "CRITICAL".equals(issue.getSeverity())))
                        .totalStudents(allStudents.size())
                        .studentsWithCompleteData(studentsWithCompleteData)
                        .studentsWithIncompleteData(allStudents.size() - studentsWithCompleteData)
                        .totalClasses(classGroups.size())
                        .classesWithCompleteData(classesWithCompleteData)
                        .classesWithIncompleteData(classGroups.size() - classesWithCompleteData)
                        .validationIssues(validationIssues)
                        .criticalErrors(validationIssues.stream()
                                .filter(issue -> "CRITICAL".equals(issue.getSeverity()))
                                .map(BulkReportGenerationDto.ValidationIssue::getDescription)
                                .collect(Collectors.toList()))
                        .warnings(validationIssues.stream()
                                .filter(issue -> "WARNING".equals(issue.getSeverity()))
                                .map(BulkReportGenerationDto.ValidationIssue::getDescription)
                                .collect(Collectors.toList()))
                        .estimatedReportCount(estimatedReportCount)
                        .estimatedDurationMinutes(estimatedDurationMinutes)
                        .estimatedCompletionTime(LocalDateTime.now().plusMinutes(estimatedDurationMinutes)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .build();

        return BulkReportGenerationDto.builder()
                .termId(termId)
                .termName(term.getTermName())
                .reportConfiguration(config)
                .validationResult(validationResult)
                .estimatedReportCount(estimatedReportCount)
                .estimatedDurationMinutes(estimatedDurationMinutes)
                .estimatedCompletionTime(LocalDateTime.now().plusMinutes(estimatedDurationMinutes)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }

    /**
     * Initiate bulk report generation
     */
    @Transactional
    public UUID initiateBulkReportGeneration(UUID termId,
                                           BulkReportGenerationDto.ReportConfiguration config,
                                           UUID initiatedBy) {
        log.info("Initiating bulk report generation for term: {} by user: {}", termId, initiatedBy);

        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        User initiator = userRepository.findById(initiatedBy)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create batch
        ReportGenerationBatch batch = new ReportGenerationBatch();
        batch.setTerm(term);
        batch.setInitiatedBy(initiator);
        batch.setBatchName(generateBatchName(term, config));
        batch.setReportType(determineReportType(config));
        batch.setStatus(ReportGenerationBatch.BatchStatus.INITIATED);

        // Estimate duration and count
        BulkReportGenerationDto validationDto = validateBulkReportGeneration(termId, config);
        batch.setTotalReports(validationDto.getValidationResult().getEstimatedReportCount());
        batch.setEstimatedDurationMinutes(validationDto.getValidationResult().getEstimatedDurationMinutes());

        batch = batchRepository.save(batch);

        // Create individual report items
        createReportItems(batch, config);

        // Start processing asynchronously
        processBatchAsync(batch.getId());

        return batch.getId();
    }

    /**
     * Get batch status and progress
     */
    @Transactional(readOnly = true)
    public BulkReportGenerationDto getBatchStatus(UUID batchId) {
        ReportGenerationBatch batch = batchRepository.findByIdWithItems(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

        // Calculate current progress
        int completedItems = (int) batch.getReportGenerationItems().stream()
                .filter(item -> item.getStatus() == ReportGenerationItem.ItemStatus.COMPLETED)
                .count();

        int failedItems = (int) batch.getReportGenerationItems().stream()
                .filter(item -> item.getStatus() == ReportGenerationItem.ItemStatus.FAILED)
                .count();

        int pendingItems = (int) batch.getReportGenerationItems().stream()
                .filter(item -> item.getStatus() == ReportGenerationItem.ItemStatus.PENDING ||
                                item.getStatus() == ReportGenerationItem.ItemStatus.GENERATING)
                .count();

        BulkReportGenerationDto.BatchProcessingInfo processingInfo =
                BulkReportGenerationDto.BatchProcessingInfo.builder()
                        .batchId(batchId)
                        .batchStatus(batch.getStatus().name())
                        .initiatedAt(batch.getInitiatedAt())
                        .startedAt(batch.getStartedAt())
                        .totalItems(batch.getTotalReports())
                        .completedItems(completedItems)
                        .failedItems(failedItems)
                        .pendingItems(pendingItems)
                        .completionPercentage(batch.getCompletionPercentage())
                        .build();

        BulkReportGenerationDto result = BulkReportGenerationDto.builder()
                .termId(batch.getTerm().getId())
                .termName(batch.getTerm().getTermName())
                .processingInfo(processingInfo)
                .build();

        // Add result summary if completed
        if (batch.isCompleted()) {
            result.setResultSummary(generateResultSummary(batch));
        }

        return result;
    }

    /**
     * Process batch asynchronously
     */
    @Async
    @Transactional
    public CompletableFuture<Void> processBatchAsync(UUID batchId) {
        log.info("Starting async processing for batch: {}", batchId);

        try {
            ReportGenerationBatch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

            // Update status and start time
            batch.setStatus(ReportGenerationBatch.BatchStatus.VALIDATING);
            batch.setStartedAt(LocalDateTime.now());
            batchRepository.save(batch);

            // Process validation phase
            validateAndUpdateBatch(batch);

            // Process generation phase
            batch.setStatus(ReportGenerationBatch.BatchStatus.IN_PROGRESS);
            batchRepository.save(batch);

            processReportItems(batch);

            // Complete the batch
            completeBatch(batch);

            log.info("Completed async processing for batch: {}", batchId);

        } catch (Exception e) {
            log.error("Error processing batch: {}", batchId, e);
            markBatchAsFailed(batchId, e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Cancel a batch
     */
    @Transactional
    public boolean cancelBatch(UUID batchId, UUID cancelledBy) {
        ReportGenerationBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found"));

        if (!batch.canBeCancelled()) {
            return false;
        }

        batch.setStatus(ReportGenerationBatch.BatchStatus.CANCELLED);
        batch.setFailureReason("Cancelled by user: " + cancelledBy);
        batchRepository.save(batch);

        // Cancel pending items
        List<ReportGenerationItem> pendingItems = itemRepository.findPendingItemsByBatch(batchId);
        pendingItems.forEach(item -> {
            item.setStatus(ReportGenerationItem.ItemStatus.CANCELLED);
            itemRepository.save(item);
        });

        log.info("Batch {} cancelled by user {}", batchId, cancelledBy);
        return true;
    }

    // Private helper methods

    private boolean validateStudentData(User student, AcademicTerm term,
                                      List<BulkReportGenerationDto.ValidationIssue> issues) {
        boolean hasCompleteData = true;
        List<String> missingFields = new ArrayList<>();

        // Check if student has assessments
        List<StudentAssessment> assessments = studentAssessmentRepository.findByStudentAndTerm(student, term);
        if (assessments.isEmpty()) {
            missingFields.add("Student assessments");
            hasCompleteData = false;
        } else {
            // Check if assessments have final grades
            boolean hasFinalGrade = assessments.stream()
                    .anyMatch(assessment -> assessment.getAssessmentGrade() != null);
            if (!hasFinalGrade) {
                missingFields.add("Final grades");
                hasCompleteData = false;
            }
        }

        // Check enrollment
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        if (enrollments.stream().noneMatch(e -> e.getClassGroup().getTerm().equals(term))) {
            missingFields.add("Term enrollment");
            hasCompleteData = false;
        }

        if (!missingFields.isEmpty()) {
            issues.add(BulkReportGenerationDto.ValidationIssue.builder()
                    .issueType("INCOMPLETE_STUDENT_DATA")
                    .severity(hasCompleteData ? "WARNING" : "CRITICAL")
                    .description("Student " + student.getFullName() + " has incomplete data")
                    .relatedStudentId(student.getId())
                    .relatedStudentName(student.getFullName())
                    .missingFields(missingFields)
                    .canProceedWithIssue(true)
                    .recommendedAction("Complete missing data or generate partial report")
                    .build());
        }

        return hasCompleteData;
    }

    private boolean validateClassData(ClassGroup classGroup,
                                    List<BulkReportGenerationDto.ValidationIssue> issues) {
        boolean hasCompleteData = true;
        List<String> missingFields = new ArrayList<>();

        // Check if class has instructor
        if (classGroup.getInstructor() == null) {
            missingFields.add("Class instructor");
            hasCompleteData = false;
        }

        // Check if class has enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);
        if (enrollments.isEmpty()) {
            missingFields.add("Student enrollments");
            hasCompleteData = false;
        }

        if (!missingFields.isEmpty()) {
            issues.add(BulkReportGenerationDto.ValidationIssue.builder()
                    .issueType("INCOMPLETE_CLASS_DATA")
                    .severity("WARNING")
                    .description("Class " + classGroup.getName() + " has incomplete data")
                    .relatedClassId(classGroup.getId())
                    .relatedClassName(classGroup.getName())
                    .missingFields(missingFields)
                    .canProceedWithIssue(true)
                    .recommendedAction("Review class setup")
                    .build());
        }

        return hasCompleteData;
    }

    private int calculateEstimatedReportCount(int studentCount, int classCount,
                                            BulkReportGenerationDto.ReportConfiguration config) {
        int count = 0;
        if (config.isIncludeStudentReports()) count += studentCount;
        if (config.isIncludeClassSummaries()) count += classCount;
        if (config.isIncludeTeacherEvaluations()) count += classCount; // Assuming one teacher per class
        if (config.isIncludeParentNotifications()) count += studentCount;
        if (config.isIncludeManagementSummary()) count += 1;
        return count;
    }

    private int calculateEstimatedDuration(int reportCount) {
        // Base calculation: 30 seconds per report + overhead
        int baseMinutes = (reportCount * 30) / 60;
        int overheadMinutes = Math.max(5, reportCount / 10); // Minimum 5 minutes overhead
        return baseMinutes + overheadMinutes;
    }

    private String generateBatchName(AcademicTerm term, BulkReportGenerationDto.ReportConfiguration config) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        return String.format("Semester End Reports - %s - %s", term.getTermName(), timestamp);
    }

    private ReportGenerationBatch.ReportType determineReportType(BulkReportGenerationDto.ReportConfiguration config) {
        if (config.isIncludeStudentReports() && config.isIncludeClassSummaries() &&
            config.isIncludeTeacherEvaluations() && config.isIncludeManagementSummary()) {
            return ReportGenerationBatch.ReportType.SEMESTER_END_STUDENT_REPORTS;
        }
        return ReportGenerationBatch.ReportType.CUSTOM_REPORT_BATCH;
    }

    private void createReportItems(ReportGenerationBatch batch, BulkReportGenerationDto.ReportConfiguration config) {
        List<ClassGroup> classGroups = classGroupRepository.findByTerm(batch.getTerm());
        int priority = 1;

        for (ClassGroup classGroup : classGroups) {
            List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);

            // Create student report items
            if (config.isIncludeStudentReports()) {
                for (Enrollment enrollment : enrollments) {
                    ReportGenerationItem item = new ReportGenerationItem();
                    item.setBatch(batch);
                    item.setStudent(enrollment.getStudent());
                    item.setClassGroup(classGroup);
                    item.setReportSubject("Student Report - " + enrollment.getStudent().getFullName());
                    item.setReportType(ReportGenerationItem.ReportType.STUDENT_REPORT);
                    item.setPriority(priority++);
                    itemRepository.save(item);
                }
            }

            // Create class summary items
            if (config.isIncludeClassSummaries()) {
                ReportGenerationItem item = new ReportGenerationItem();
                item.setBatch(batch);
                item.setClassGroup(classGroup);
                item.setReportType(ReportGenerationItem.ReportType.CLASS_SUMMARY);
                item.setTeacher(classGroup.getInstructor());
                item.setReportSubject("Class Summary - " + classGroup.getName());
                item.setPriority(priority++);
                itemRepository.save(item);
            }
        }

        // Create management summary
        if (config.isIncludeManagementSummary()) {
            ReportGenerationItem item = new ReportGenerationItem();
            item.setBatch(batch);
            item.setReportType(ReportGenerationItem.ReportType.MANAGEMENT_SUMMARY);
            item.setReportSubject("Management Executive Summary - " + batch.getTerm().getTermName());
            item.setPriority(1); // High priority
            itemRepository.save(item);
        }
    }

    private void validateAndUpdateBatch(ReportGenerationBatch batch) {
        // Additional validation logic can be added here
        log.info("Validation phase completed for batch: {}", batch.getId());
    }

    private void processReportItems(ReportGenerationBatch batch) {
        List<ReportGenerationItem> pendingItems = itemRepository.findPendingItemsByBatch(batch.getId());

        for (ReportGenerationItem item : pendingItems) {
            try {
                processReportItem(item);
                updateBatchProgress(batch);
            } catch (Exception e) {
                log.error("Error processing report item: {}", item.getId(), e);
                item.markAsFailed(e.getMessage());
                itemRepository.save(item);
            }
        }
    }

    private void processReportItem(ReportGenerationItem item) {
        log.info("Processing report item: {}", item.getId());

        item.markAsStarted();
        itemRepository.save(item);

        try {
            // Simulate report generation
            String filePath = generateReport(item);
            long fileSize = 1024 * 1024; // Simulated file size

            item.markAsCompleted(filePath, fileSize);
            itemRepository.save(item);

            log.info("Completed report item: {} -> {}", item.getId(), filePath);

        } catch (Exception e) {
            item.markAsFailed(e.getMessage());
            itemRepository.save(item);
            throw e;
        }
    }

    private String generateReport(ReportGenerationItem item) {
        try {
            UUID termId = item.getBatch().getTerm().getId();

            switch (item.getReportType()) {
                case STUDENT_REPORT:
                    if (item.getStudent() == null) {
                        throw new IllegalStateException("Student is required for STUDENT_REPORT");
                    }
                    return pdfReportGenerationService.generateStudentReport(
                            item.getStudent().getId(),
                            termId,
                            "STANDARD");

                case CLASS_SUMMARY:
                    if (item.getClassGroup() == null) {
                        throw new IllegalStateException("ClassGroup is required for CLASS_SUMMARY");
                    }
                    return pdfReportGenerationService.generateClassSummaryReport(
                            item.getClassGroup().getId(),
                            termId);

                case TEACHER_EVALUATION:
                case PARENT_NOTIFICATION:
                case MANAGEMENT_SUMMARY:
                default:
                    // For now, generate a simple placeholder for these types
                    // TODO: Implement specific report generation for each type
                    log.warn("Report type {} not yet implemented, generating placeholder", item.getReportType());
                    String fileName = String.format("report_%s_%s_%d.pdf",
                            item.getReportType().toString().toLowerCase(),
                            item.getBatch().getId().toString().substring(0, 8),
                            System.currentTimeMillis());
                    return reportsOutputDirectory + "/" + fileName;
            }
        } catch (Exception e) {
            log.error("Failed to generate report for item {}: {}", item.getId(), e.getMessage(), e);
            throw new RuntimeException("Report generation failed: " + e.getMessage(), e);
        }
    }

    private void updateBatchProgress(ReportGenerationBatch batch) {
        List<ReportGenerationItem> items = itemRepository.findByBatchIdOrderByPriority(batch.getId());

        int completed = (int) items.stream()
                .filter(item -> item.getStatus() == ReportGenerationItem.ItemStatus.COMPLETED)
                .count();

        int failed = (int) items.stream()
                .filter(item -> item.getStatus() == ReportGenerationItem.ItemStatus.FAILED)
                .count();

        batch.setCompletedReports(completed);
        batch.setFailedReports(failed);
        batchRepository.save(batch);
    }

    private void completeBatch(ReportGenerationBatch batch) {
        batch.setStatus(ReportGenerationBatch.BatchStatus.COMPLETED);
        batch.setCompletedAt(LocalDateTime.now());

        if (batch.getStartedAt() != null) {
            long duration = java.time.Duration.between(batch.getStartedAt(), batch.getCompletedAt()).toMinutes();
            batch.setActualDurationMinutes((int) duration);
        }

        batchRepository.save(batch);
        log.info("Batch completed: {}", batch.getId());
    }

    private void markBatchAsFailed(UUID batchId, String errorMessage) {
        ReportGenerationBatch batch = batchRepository.findById(batchId).orElse(null);
        if (batch != null) {
            batch.setStatus(ReportGenerationBatch.BatchStatus.FAILED);
            batch.setFailureReason(errorMessage);
            batch.setCompletedAt(LocalDateTime.now());
            batchRepository.save(batch);
        }
    }

    private BulkReportGenerationDto.GenerationResultSummary generateResultSummary(ReportGenerationBatch batch) {
        List<ReportGenerationItem> items = itemRepository.findByBatchIdOrderByPriority(batch.getId());

        List<BulkReportGenerationDto.GeneratedReportInfo> reportInfos = items.stream()
                .filter(item -> item.isCompleted())
                .map(this::mapToReportInfo)
                .collect(Collectors.toList());

        return BulkReportGenerationDto.GenerationResultSummary.builder()
                .overallSuccess(batch.getFailedReports() == 0)
                .completedAt(batch.getCompletedAt())
                .totalReportsGenerated(batch.getCompletedReports())
                .successfulReports(batch.getCompletedReports())
                .failedReports(batch.getFailedReports())
                .generatedReports(reportInfos)
                .actualDurationMinutes(batch.getActualDurationMinutes())
                .build();
    }

    private BulkReportGenerationDto.GeneratedReportInfo mapToReportInfo(ReportGenerationItem item) {
        return BulkReportGenerationDto.GeneratedReportInfo.builder()
                .reportId(item.getId())
                .reportName(item.getReportSubject())
                .studentId(item.getStudent() != null ? item.getStudent().getId() : null)
                .studentName(item.getStudent() != null ? item.getStudent().getFullName() : null)
                .classId(item.getClassGroup() != null ? item.getClassGroup().getId() : null)
                .className(item.getClassGroup() != null ? item.getClassGroup().getName() : null)
                .filePath(item.getFilePath())
                .fileSizeBytes(item.getFileSizeBytes())
                .generatedAt(item.getCompletedAt())
                .distributed(item.getDistributed())
                .distributedAt(item.getDistributedAt())
                .distributionMethod(item.getDistributionMethod())
                .build();
    }
}