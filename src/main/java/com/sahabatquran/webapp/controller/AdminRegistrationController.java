package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.*;
import com.sahabatquran.webapp.entity.StudentRegistration;
import com.sahabatquran.webapp.service.StudentRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/registrations")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('STUDENT_REG_VIEW')")
@Slf4j
public class AdminRegistrationController {
    
    private final StudentRegistrationService registrationService;
    
    @GetMapping
    public String listRegistrations(@ModelAttribute RegistrationSearchRequest searchRequest, Model model) {
        log.info("Displaying registration list with search criteria: {}", searchRequest);
        
        Page<StudentRegistrationResponse> registrations = registrationService.searchRegistrations(searchRequest);
        
        model.addAttribute("registrations", registrations);
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("programs", registrationService.getActivePrograms());
        model.addAttribute("statusOptions", StudentRegistration.RegistrationStatus.values());
        model.addAttribute("placementStatusOptions", StudentRegistration.PlacementTestStatus.values());
        
        // Add pagination information
        model.addAttribute("currentPage", registrations.getNumber());
        model.addAttribute("totalPages", registrations.getTotalPages());
        model.addAttribute("totalElements", registrations.getTotalElements());
        
        return "admin/registrations/list";
    }
    
    @GetMapping("/{id}")
    public String viewRegistration(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Displaying registration details for admin view: {}", id);
        
        try {
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            model.addAttribute("registration", registration);
            
            return "admin/registrations/detail";
            
        } catch (Exception e) {
            log.error("Error fetching registration details: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/admin/registrations";
        }
    }
    
    @GetMapping("/{id}/review")
    @PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")
    public String showReviewForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Displaying registration review form for ID: {}", id);
        
        try {
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            
            // Check if registration can be reviewed
            if (registration.getRegistrationStatus() == StudentRegistration.RegistrationStatus.DRAFT) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Pendaftaran belum disubmit untuk review");
                return "redirect:/admin/registrations/" + id;
            }
            
            RegistrationReviewRequest reviewRequest = new RegistrationReviewRequest();
            reviewRequest.setRegistrationId(id);
            
            model.addAttribute("registration", registration);
            model.addAttribute("reviewRequest", reviewRequest);
            model.addAttribute("statusOptions", getReviewableStatuses());
            
            return "admin/registrations/review";
            
        } catch (Exception e) {
            log.error("Error loading registration for review: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/admin/registrations";
        }
    }
    
    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")
    public String submitReview(@PathVariable UUID id,
                             @Valid @ModelAttribute RegistrationReviewRequest reviewRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        log.info("Processing registration review for ID: {}", id);
        
        reviewRequest.setRegistrationId(id);
        
        if (bindingResult.hasErrors()) {
            log.warn("Review form has validation errors: {}", bindingResult.getAllErrors());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
                model.addAttribute("statusOptions", getReviewableStatuses());
                return "admin/registrations/review";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/admin/registrations";
            }
        }
        
        try {
            StudentRegistrationResponse response = registrationService.reviewRegistration(reviewRequest);
            log.info("Registration review completed successfully: {}", response.getId());
            
            String statusMessage = getStatusMessage(reviewRequest.getNewStatus());
            redirectAttributes.addFlashAttribute("successMessage", 
                "Pendaftaran berhasil " + statusMessage);
            
            return "redirect:/admin/registrations/" + id;
            
        } catch (Exception e) {
            log.error("Error during registration review", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat memproses review: " + e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
                model.addAttribute("statusOptions", getReviewableStatuses());
                return "admin/registrations/review";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/admin/registrations";
            }
        }
    }
    
    @GetMapping("/placement-tests")
    @PreAuthorize("hasAuthority('PLACEMENT_TEST_EVALUATE')")
    public String listPendingPlacementTests(Model model) {
        log.info("Displaying pending placement tests");
        
        List<StudentRegistrationResponse> pendingTests = registrationService.getPendingPlacementTests();
        model.addAttribute("pendingTests", pendingTests);
        
        return "admin/placement-tests/list";
    }
    
    @GetMapping("/{id}/placement-test")
    @PreAuthorize("hasAuthority('PLACEMENT_TEST_EVALUATE')")
    public String showPlacementTestEvaluation(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Displaying placement test evaluation form for registration ID: {}", id);
        
        try {
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            
            // Check if placement test can be evaluated
            if (registration.getPlacementTest() == null || 
                registration.getPlacementTest().getStatus() != StudentRegistration.PlacementTestStatus.SUBMITTED) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Placement test belum disubmit atau belum tersedia");
                return "redirect:/admin/registrations/" + id;
            }
            
            PlacementTestEvaluationRequest evalRequest = new PlacementTestEvaluationRequest();
            evalRequest.setRegistrationId(id);
            
            model.addAttribute("registration", registration);
            model.addAttribute("evaluationRequest", evalRequest);
            model.addAttribute("difficultyLevels", List.of(1, 2, 3, 4, 5));
            
            return "admin/placement-tests/evaluate";
            
        } catch (Exception e) {
            log.error("Error loading placement test for evaluation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/admin/registrations";
        }
    }
    
    @PostMapping("/{id}/placement-test")
    @PreAuthorize("hasAuthority('PLACEMENT_TEST_EVALUATE')")
    public String submitPlacementTestEvaluation(@PathVariable UUID id,
                                              @Valid @ModelAttribute PlacementTestEvaluationRequest evalRequest,
                                              BindingResult bindingResult,
                                              Model model,
                                              RedirectAttributes redirectAttributes) {
        log.info("Processing placement test evaluation for registration ID: {}", id);
        
        evalRequest.setRegistrationId(id);
        
        if (bindingResult.hasErrors()) {
            log.warn("Placement test evaluation form has validation errors: {}", bindingResult.getAllErrors());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
                model.addAttribute("difficultyLevels", List.of(1, 2, 3, 4, 5));
                return "admin/placement-tests/evaluate";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/admin/registrations";
            }
        }
        
        try {
            StudentRegistrationResponse response = registrationService.evaluatePlacementTest(evalRequest);
            log.info("Placement test evaluation completed successfully: {}", response.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Evaluasi placement test berhasil disimpan!");
            
            return "redirect:/admin/registrations/" + id;
            
        } catch (Exception e) {
            log.error("Error during placement test evaluation", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat evaluasi: " + e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
                model.addAttribute("difficultyLevels", List.of(1, 2, 3, 4, 5));
                return "admin/placement-tests/evaluate";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/admin/registrations";
            }
        }
    }
    
    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String showReports(Model model) {
        log.info("Displaying registration reports");
        
        // Add summary statistics
        model.addAttribute("totalRegistrations", 
            registrationService.searchRegistrations(new RegistrationSearchRequest()).getTotalElements());
        
        return "admin/registrations/reports";
    }
    
    // Helper methods
    
    private List<StudentRegistration.RegistrationStatus> getReviewableStatuses() {
        return List.of(
            StudentRegistration.RegistrationStatus.UNDER_REVIEW,
            StudentRegistration.RegistrationStatus.APPROVED,
            StudentRegistration.RegistrationStatus.REJECTED
        );
    }
    
    private String getStatusMessage(StudentRegistration.RegistrationStatus status) {
        return switch (status) {
            case UNDER_REVIEW -> "ditinjau";
            case APPROVED -> "disetujui";
            case REJECTED -> "ditolak";
            default -> "diproses";
        };
    }
}