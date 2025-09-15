package com.sahabatquran.webapp.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class TranscriptRequestDto {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotBlank(message = "Transcript format is required")
    private String transcriptFormat; // OFFICIAL_TRANSCRIPT, UNOFFICIAL_TRANSCRIPT, GRADE_REPORT

    private boolean includeCurrentTerm = true;
    private boolean includeInProgress = false;
    private String specificTerm; // Optional: for specific term transcripts
}