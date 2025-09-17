package com.sahabatquran.webapp.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO for bulk report generation requests.
 */
@Data
public class BulkReportRequestDto {

    @NotNull(message = "Term ID is required")
    private UUID termId;

    @NotBlank(message = "Report type is required")
    private String reportType;

    private UUID classId; // Optional: for class-specific bulk generation
    private List<UUID> studentIds; // Optional: for specific students

    private boolean includeDisclaimers = false;
    private String processingStrategy = "PROCESS_ALL_WITH_DISCLAIMERS"; // COMPLETE_ONLY, PROCESS_ALL_WITH_DISCLAIMERS, SKIP_PROBLEMATIC
}