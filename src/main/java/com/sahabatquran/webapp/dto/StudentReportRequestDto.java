package com.sahabatquran.webapp.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for student report generation requests.
 */
@Data
public class StudentReportRequestDto {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Term ID is required")
    private UUID termId;

    @NotBlank(message = "Report type is required")
    private String reportType; // INDIVIDUAL_REPORT_CARD, ACADEMIC_TRANSCRIPT, HISTORICAL_PERFORMANCE

    private boolean includeDisclaimers = false;
    private boolean generatePartialReport = false;
}