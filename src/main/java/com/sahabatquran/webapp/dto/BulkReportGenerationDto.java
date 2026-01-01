package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for bulk report generation operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportConfiguration {
        @Builder.Default
        private Boolean includeStudentReports = false;
        @Builder.Default
        private Boolean includeClassSummaries = false;
        @Builder.Default
        private Boolean includeTeacherEvaluations = false;
        @Builder.Default
        private Boolean includeParentNotifications = false;
        @Builder.Default
        private Boolean includeManagementSummary = false;

        // Filtering options
        private List<UUID> specificClassIds;
        private List<UUID> specificStudentIds;
        private List<String> reportFormats; // PDF, EXCEL, etc.

        // Distribution options
        @Builder.Default
        private Boolean autoDistribute = false;
        @Builder.Default
        private Boolean emailToParents = false;
        @Builder.Default
        private Boolean emailToTeachers = false;
        @Builder.Default
        private Boolean portalNotification = false;

        // Quality settings
        private String reportQuality; // DRAFT, STANDARD, HIGH_QUALITY
        @Builder.Default
        private Boolean includeCharts = false;
        @Builder.Default
        private Boolean includeDetailedAnalysis = false;
        private String language; // id, en

        // Helper methods for null-safe access
        public boolean isIncludeStudentReports() {
            return Boolean.TRUE.equals(includeStudentReports);
        }
        public boolean isIncludeClassSummaries() {
            return Boolean.TRUE.equals(includeClassSummaries);
        }
        public boolean isIncludeTeacherEvaluations() {
            return Boolean.TRUE.equals(includeTeacherEvaluations);
        }
        public boolean isIncludeParentNotifications() {
            return Boolean.TRUE.equals(includeParentNotifications);
        }
        public boolean isIncludeManagementSummary() {
            return Boolean.TRUE.equals(includeManagementSummary);
        }
        public boolean isAutoDistribute() {
            return Boolean.TRUE.equals(autoDistribute);
        }
        public boolean isIncludeCharts() {
            return Boolean.TRUE.equals(includeCharts);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
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