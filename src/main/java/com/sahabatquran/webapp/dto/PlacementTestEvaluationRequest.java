package com.sahabatquran.webapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class PlacementTestEvaluationRequest {
    
    @NotNull(message = "ID registrasi harus diisi")
    private UUID registrationId;
    
    @NotNull(message = "Hasil penilaian harus diisi")
    @Min(value = 1, message = "Hasil penilaian minimal 1")
    @Max(value = 5, message = "Hasil penilaian maksimal 5")
    private Integer placementResult;
    
    @Size(max = 1000, message = "Catatan evaluasi maksimal 1000 karakter")
    private String evaluationNotes;
    
    @Size(max = 500, message = "Alasan penilaian maksimal 500 karakter")
    private String evaluationReason;
}