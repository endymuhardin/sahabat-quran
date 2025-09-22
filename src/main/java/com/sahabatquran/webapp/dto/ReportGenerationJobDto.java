package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.ReportGenerationBatch.BatchStatus;
import com.sahabatquran.webapp.entity.ReportGenerationBatch.ReportType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for report generation job status display
 */
@Data
public class ReportGenerationJobDto {
    private UUID batchId;
    private String batchName;
    private BatchStatus status;
    private ReportType reportType;
    private String initiatedBy;

    // Progress tracking
    private Integer totalReports;
    private Integer completedReports;
    private Integer failedReports;
    private Integer progressPercentage;

    // Timing
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    // Additional status info
    private String statusDisplayText;
    private String statusCssClass;

    public String getStatusDisplayText() {
        if (statusDisplayText != null) return statusDisplayText;

        return switch (status) {
            case INITIATED -> "Queued";
            case VALIDATING -> "Validating...";
            case IN_PROGRESS -> "Processing...";
            case COMPLETED -> "Completed";
            case FAILED -> "Failed";
            case CANCELLED -> "Cancelled";
            default -> status.toString();
        };
    }

    public String getStatusCssClass() {
        if (statusCssClass != null) return statusCssClass;

        return switch (status) {
            case INITIATED -> "bg-blue-100 text-blue-800";
            case VALIDATING -> "bg-yellow-100 text-yellow-800";
            case IN_PROGRESS -> "bg-yellow-100 text-yellow-800";
            case COMPLETED -> "bg-green-100 text-green-800";
            case FAILED -> "bg-red-100 text-red-800";
            case CANCELLED -> "bg-gray-100 text-gray-800";
            default -> "bg-gray-100 text-gray-800";
        };
    }

    public boolean isInProgress() {
        return status == BatchStatus.IN_PROGRESS || status == BatchStatus.INITIATED || status == BatchStatus.VALIDATING;
    }

    public boolean isCompleted() {
        return status == BatchStatus.COMPLETED;
    }

    public boolean hasFailed() {
        return status == BatchStatus.FAILED;
    }
}