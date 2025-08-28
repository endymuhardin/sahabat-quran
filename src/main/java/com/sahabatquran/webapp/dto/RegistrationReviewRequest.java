package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentRegistration;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class RegistrationReviewRequest {
    
    @NotNull(message = "ID registrasi harus diisi")
    private UUID registrationId;
    
    @NotNull(message = "Status review harus dipilih")
    private StudentRegistration.RegistrationStatus newStatus;
    
    @Size(max = 1000, message = "Catatan review maksimal 1000 karakter")
    private String reviewNotes;
    
    @Size(max = 500, message = "Alasan keputusan maksimal 500 karakter")
    private String decisionReason;
}