package com.sahabatquran.webapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for bulk report generation operations
 */
@Data
@Builder
public class BulkReportGenerationDto {

    // Request Information
    private UUID termId;
    private String termName;
    private String reportType;
    private String batchName;
    private UUID initiatedBy;
    private String initiatedByName;

    // Batch Configuration
    private ReportConfiguration reportConfiguration;

    // Validation Results
    private DataValidationResult validationResult;

    // Processing Information
    private BatchProcessingInfo processingInfo;

    // Results Summary
    private GenerationResultSummary resultSummary;

    // Top-level estimates for UI display
    private Integer estimatedReportCount;
    private Integer estimatedDurationMinutes;
    private String estimatedCompletionTime;

    @Data
    @Builder
    public static class ReportConfiguration {
        private boolean includeStudentReports;
        private boolean includeClassSummaries;
        private boolean includeTeacherEvaluations;
        private boolean includeParentNotifications;
        private boolean includeManagementSummary;

        // Filtering options
        private List<UUID> specificClassIds;
        private List<UUID> specificStudentIds;
        private List<String> reportFormats; // PDF, EXCEL, etc.

        // Distribution options
        private boolean autoDistribute;
        private boolean emailToParents;
        private boolean emailToTeachers;
        private boolean portalNotification;

        // Quality settings
        private String reportQuality; // DRAFT, STANDARD, HIGH_QUALITY
        private boolean includeCharts;
        private boolean includeDetailedAnalysis;
        private String language; // id, en
    }

    @Data
    @Builder
    public static class DataValidationResult {
        private boolean isValid;
        private int totalStudents;
        private int studentsWithCompleteData;
        private int studentsWithIncompleteData;
        private int totalClasses;
        private int classesWithCompleteData;
        private int classesWithIncompleteData;
        private int totalTeachers;
        private int teachersWithCompleteEvaluations;

        // Detailed validation issues
        private List<ValidationIssue> validationIssues;
        private List<String> criticalErrors;
        private List<String> warnings;

        // Estimates
        private int estimatedReportCount;
        private int estimatedDurationMinutes;
        private String estimatedCompletionTime;
    }

    @Data
    @Builder
    public static class ValidationIssue {
        private String issueType; // MISSING_GRADES, INCOMPLETE_ATTENDANCE, etc.
        private String severity; // CRITICAL, WARNING, INFO
        private String description;
        private UUID relatedStudentId;
        private String relatedStudentName;
        private UUID relatedClassId;
        private String relatedClassName;
        private List<String> missingFields;
        private boolean canProceedWithIssue;
        private String recommendedAction;
    }

    @Data
    @Builder
    public static class BatchProcessingInfo {
        private UUID batchId;
        private String batchStatus;
        private LocalDateTime initiatedAt;
        private LocalDateTime startedAt;
        private LocalDateTime estimatedCompletionAt;
        private LocalDateTime actualCompletionAt;

        // Progress tracking
        private int totalItems;
        private int completedItems;
        private int failedItems;
        private int pendingItems;
        private double completionPercentage;

        // Performance metrics
        private int itemsPerMinute;
        private int estimatedRemainingMinutes;
        private String currentProcessingItem;

        // Error tracking
        private int totalErrors;
        private List<String> recentErrors;
        private boolean hasRetryableErrors;
    }

    @Data
    @Builder
    public static class GenerationResultSummary {
        private boolean overallSuccess;
        private LocalDateTime completedAt;
        private int totalReportsGenerated;
        private int successfulReports;
        private int failedReports;
        private int partialReports;

        // File information
        private List<GeneratedReportInfo> generatedReports;
        private long totalFileSizeBytes;
        private String downloadPackageUrl;

        // Distribution summary
        private boolean distributionCompleted;
        private int emailsSent;
        private int portalNotificationsSent;
        private List<String> distributionErrors;

        // Performance summary
        private int actualDurationMinutes;
        private double averageReportGenerationTime;
        private String performanceRating; // EXCELLENT, GOOD, AVERAGE, POOR
    }

    @Data
    @Builder
    public static class GeneratedReportInfo {
        private UUID reportId;
        private String reportName;
        private String reportType;
        private UUID studentId;
        private String studentName;
        private UUID classId;
        private String className;
        private String filePath;
        private long fileSizeBytes;
        private String downloadUrl;
        private boolean distributed;
        private LocalDateTime generatedAt;
        private LocalDateTime distributedAt;
        private String distributionMethod;
    }

    // Progress tracking methods
    public boolean isValidationComplete() {
        return validationResult != null && validationResult.isValid();
    }

    public boolean isProcessingComplete() {
        return processingInfo != null &&
               "COMPLETED".equals(processingInfo.getBatchStatus());
    }

    public boolean hasErrors() {
        return processingInfo != null && processingInfo.getTotalErrors() > 0;
    }

    public double getOverallProgress() {
        if (processingInfo == null) return 0.0;
        return processingInfo.getCompletionPercentage();
    }
}