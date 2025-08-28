package com.sahabatquran.webapp.dto;

import com.sahabatquran.webapp.entity.StudentRegistration;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegistrationSearchRequest {
    
    private String fullName;
    private String email;
    private String phoneNumber;
    private StudentRegistration.RegistrationStatus registrationStatus;
    private StudentRegistration.PlacementTestStatus placementTestStatus;
    private UUID programId;
    private LocalDate registrationDateFrom;
    private LocalDate registrationDateTo;
    
    // Pagination
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
}