package com.sahabatquran.webapp.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for email report requests.
 */
@Data
public class EmailReportRequestDto {

    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    private String emailSubject;
    private String emailMessage;
    private boolean includeAttachment = true;
}