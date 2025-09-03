package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.*;
import com.sahabatquran.webapp.entity.Level;
import com.sahabatquran.webapp.entity.StudentRegistration;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.LevelRepository;
import com.sahabatquran.webapp.service.StudentRegistrationService;
import com.sahabatquran.webapp.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/registrations")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    
    private final StudentRegistrationService registrationService;
    private final UserService userService;
    private final LevelRepository levelRepository;
    
    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_REG_VIEW')")
    public String listRegistrations(@ModelAttribute RegistrationSearchRequest searchRequest, Model model) {
        log.info("Displaying registration list with search criteria: {}", searchRequest);
        
        Page<StudentRegistrationResponse> registrations;
        
        // Staff users should not see DRAFT registrations
        if (searchRequest.getRegistrationStatus() == StudentRegistration.RegistrationStatus.DRAFT) {
            log.info("Staff user attempted to view DRAFT registrations, returning empty results");
            searchRequest.setRegistrationStatus(null); // Clear the DRAFT filter to avoid confusion
            registrations = Page.empty();
        } else {
            registrations = registrationService.searchRegistrations(searchRequest);
        }
        
        model.addAttribute("registrations", registrations);
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("programs", registrationService.getActivePrograms());
        
        // Exclude DRAFT status from options for staff users
        List<StudentRegistration.RegistrationStatus> availableStatuses = Arrays.stream(StudentRegistration.RegistrationStatus.values())
                .filter(status -> status != StudentRegistration.RegistrationStatus.DRAFT)
                .collect(Collectors.toList());
        model.addAttribute("statusOptions", availableStatuses);
        
        model.addAttribute("placementStatusOptions", StudentRegistration.PlacementTestStatus.values());
        model.addAttribute("teachers", userService.getTeachers());
        
        // Add pagination information
        model.addAttribute("currentPage", registrations.getNumber());
        model.addAttribute("totalPages", registrations.getTotalPages());
        model.addAttribute("totalElements", registrations.getTotalElements());
        
        return "registrations/list";
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT_REG_VIEW')")
    public String viewRegistration(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Displaying registration details for admin view: {}", id);
        
        try {
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            model.addAttribute("registration", registration);
            
            return "registrations/detail";
            
        } catch (Exception e) {
            log.error("Error fetching registration details: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/registrations";
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
                return "redirect:/registrations/" + id;
            }
            
            RegistrationReviewRequest reviewRequest = new RegistrationReviewRequest();
            reviewRequest.setRegistrationId(id);
            
            model.addAttribute("registration", registration);
            model.addAttribute("reviewRequest", reviewRequest);
            model.addAttribute("statusOptions", getAssignableStatuses());
            
            return "registrations/review";
            
        } catch (Exception e) {
            log.error("Error loading registration for review: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/registrations";
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
                model.addAttribute("statusOptions", getAssignableStatuses());
                return "registrations/review";
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
            
            return "redirect:/registrations/assigned";
            
        } catch (Exception e) {
            log.error("Error during registration review", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat memproses review: " + e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
                model.addAttribute("statusOptions", getAssignableStatuses());
                return "registrations/review";
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
        
        return "registrations/placement-tests";
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
                return "redirect:/registrations/" + id;
            }
            
            PlacementTestEvaluationRequest evalRequest = new PlacementTestEvaluationRequest();
            evalRequest.setRegistrationId(id);
            
            model.addAttribute("registration", registration);
            model.addAttribute("evaluationRequest", evalRequest);
            model.addAttribute("difficultyLevels", List.of(1, 2, 3, 4, 5));
            
            return "registrations/evaluate-placement";
            
        } catch (Exception e) {
            log.error("Error loading placement test for evaluation: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/registrations";
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
                return "registrations/evaluate-placement";
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
            
            return "redirect:/registrations/" + id;
            
        } catch (Exception e) {
            log.error("Error during placement test evaluation", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat evaluasi: " + e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
                model.addAttribute("difficultyLevels", List.of(1, 2, 3, 4, 5));
                return "registrations/evaluate-placement";
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
        
        try {
            // Total registrations
            long totalRegistrations = registrationService
                .searchRegistrations(new RegistrationSearchRequest()).getTotalElements();
            
            // Get status counts using StudentRegistrationService or repository
            Map<String, Long> statusCounts = new HashMap<>();
            
            // Get registrations by status for counting
            RegistrationSearchRequest submittedRequest = new RegistrationSearchRequest();
            submittedRequest.setRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
            long submittedCount = registrationService.searchRegistrations(submittedRequest).getTotalElements();
            
            RegistrationSearchRequest assignedRequest = new RegistrationSearchRequest();
            assignedRequest.setRegistrationStatus(StudentRegistration.RegistrationStatus.ASSIGNED);
            long assignedCount = registrationService.searchRegistrations(assignedRequest).getTotalElements();
            
            RegistrationSearchRequest reviewedRequest = new RegistrationSearchRequest();
            reviewedRequest.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
            long reviewedCount = registrationService.searchRegistrations(reviewedRequest).getTotalElements();
            
            RegistrationSearchRequest completedRequest = new RegistrationSearchRequest();
            completedRequest.setRegistrationStatus(StudentRegistration.RegistrationStatus.COMPLETED);
            long completedCount = registrationService.searchRegistrations(completedRequest).getTotalElements();
            
            RegistrationSearchRequest rejectedRequest = new RegistrationSearchRequest();
            rejectedRequest.setRegistrationStatus(StudentRegistration.RegistrationStatus.REJECTED);
            long rejectedCount = registrationService.searchRegistrations(rejectedRequest).getTotalElements();
            
            // Calculate derived metrics
            long pendingAssignments = submittedCount; // SUBMITTED status
            long completedReviews = reviewedCount + completedCount + rejectedCount; // All completed states
            
            // Add all data to model for the template
            model.addAttribute("totalRegistrations", totalRegistrations);
            model.addAttribute("pendingAssignments", pendingAssignments);
            model.addAttribute("completedReviews", completedReviews);
            model.addAttribute("submittedCount", submittedCount);
            model.addAttribute("assignedCount", assignedCount);
            model.addAttribute("reviewedCount", reviewedCount);
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("rejectedCount", rejectedCount);
            
            // For the template logic
            statusCounts.put("SUBMITTED", submittedCount);
            statusCounts.put("ASSIGNED", assignedCount);
            statusCounts.put("REVIEWED", reviewedCount);
            statusCounts.put("COMPLETED", completedCount);
            statusCounts.put("REJECTED", rejectedCount);
            
            model.addAttribute("statusCounts", statusCounts);
            model.addAttribute("pageTitle", "Registration Reports");
            
            return "registrations/reports";
            
        } catch (Exception e) {
            log.error("Error loading registration reports", e);
            model.addAttribute("error", "Failed to load reports data: " + e.getMessage());
            return "registrations/reports";
        }
    }
    
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('STUDENT_REG_ASSIGN_TEACHER')")
    @ResponseBody
    public ResponseEntity<Map<String, String>> assignTeacherToRegistration(@PathVariable UUID id,
                                                                          @RequestBody TeacherAssignmentRequest assignmentRequest,
                                                                          Authentication authentication) {
        log.info("Assigning teacher to registration ID: {}", id);
        
        try {
            assignmentRequest.setRegistrationId(id);
            
            // Get current user for audit trail
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
            
            StudentRegistrationResponse response = registrationService.assignTeacherToRegistration(assignmentRequest, currentUser);
            
            Map<String, String> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Guru berhasil ditugaskan");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error assigning teacher to registration", e);
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Terjadi kesalahan saat menugaskan guru: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Teacher-specific endpoints
    @GetMapping("/assigned")
    @PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")
    public String listAssignedRegistrations(Authentication authentication, Model model) {
        log.info("Teacher {} viewing assigned registrations", authentication.getName());
        
        // Get current teacher user
        User currentTeacher = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Teacher user not found"));
        
        // Get registrations assigned to this teacher
        List<StudentRegistrationResponse> assignedRegistrations = 
                registrationService.getRegistrationsAssignedToTeacher(currentTeacher.getId());
        
        model.addAttribute("registrations", assignedRegistrations);
        model.addAttribute("teacherName", currentTeacher.getFullName());
        
        // Count by status for dashboard
        long pendingCount = assignedRegistrations.stream()
                .filter(r -> r.getTeacherReviewStatus() == StudentRegistration.TeacherReviewStatus.PENDING)
                .count();
        long inReviewCount = assignedRegistrations.stream()
                .filter(r -> r.getTeacherReviewStatus() == StudentRegistration.TeacherReviewStatus.IN_REVIEW)
                .count();
        long completedCount = assignedRegistrations.stream()
                .filter(r -> r.getTeacherReviewStatus() == StudentRegistration.TeacherReviewStatus.COMPLETED)
                .count();
        
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("inReviewCount", inReviewCount);
        model.addAttribute("completedCount", completedCount);
        
        return "registrations/teacher-list";
    }
    
    @GetMapping("/assigned/{id}/review")
    @PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")
    public String showTeacherReviewForm(@PathVariable UUID id, 
                                       Authentication authentication,
                                       Model model, 
                                       RedirectAttributes redirectAttributes) {
        log.info("Teacher {} accessing review form for registration ID: {}", authentication.getName(), id);
        
        try {
            // Get current teacher user
            User currentTeacher = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("Teacher user not found"));
            
            // Get the registration
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            
            // Check if this registration is assigned to the current teacher
            if (registration.getAssignedTeacherId() == null || 
                !registration.getAssignedTeacherId().equals(currentTeacher.getId())) {
                log.warn("Teacher {} attempted to access registration {} which is not assigned to them", 
                         authentication.getName(), id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Anda tidak memiliki akses untuk mereview registrasi ini");
                return "redirect:/registrations/assigned";
            }
            
            // Check if registration can be reviewed
            if (registration.getRegistrationStatus() != StudentRegistration.RegistrationStatus.ASSIGNED) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Registrasi ini belum siap untuk direview");
                return "redirect:/registrations/assigned";
            }
            
            // Prepare review form
            TeacherReviewRequest reviewRequest = new TeacherReviewRequest();
            reviewRequest.setRegistrationId(id);
            reviewRequest.setTeacherId(currentTeacher.getId());
            
            // Get available levels for recommendation
            List<Level> availableLevels = levelRepository.findByOrderByOrderNumber();
            
            model.addAttribute("registration", registration);
            model.addAttribute("reviewRequest", reviewRequest);
            model.addAttribute("reviewStatusOptions", StudentRegistration.TeacherReviewStatus.values());
            model.addAttribute("availableLevels", availableLevels);
            
            return "registrations/teacher-review";
            
        } catch (Exception e) {
            log.error("Error loading registration for teacher review: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Registrasi tidak ditemukan");
            return "redirect:/registrations/assigned";
        }
    }
    
    @PostMapping("/assigned/{id}/review")
    @PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")
    public String submitTeacherReviewFromAssigned(@PathVariable UUID id,
                                    @Valid @ModelAttribute TeacherReviewRequest reviewRequest,
                                    BindingResult bindingResult,
                                    Authentication authentication,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        log.info("Teacher {} submitting review for registration ID via assigned path: {}", authentication.getName(), id);
        
        // Get current teacher user
        User currentTeacher = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Teacher user not found"));
        
        reviewRequest.setRegistrationId(id);
        reviewRequest.setTeacherId(currentTeacher.getId());
        
        if (bindingResult.hasErrors()) {
            log.warn("Review form has validation errors: {}", bindingResult.getAllErrors());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                
                // Verify teacher is assigned to this registration
                if (registration.getAssignedTeacherId() == null || 
                    !registration.getAssignedTeacherId().equals(currentTeacher.getId())) {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                        "Anda tidak memiliki akses untuk mereview registrasi ini");
                    return "redirect:/registrations/assigned";
                }
                
                List<Level> availableLevels = levelRepository.findByOrderByOrderNumber();
                
                model.addAttribute("registration", registration);
                model.addAttribute("reviewRequest", reviewRequest);
                model.addAttribute("reviewStatusOptions", StudentRegistration.TeacherReviewStatus.values());
                model.addAttribute("availableLevels", availableLevels);
                return "registrations/teacher-review";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/registrations/assigned";
            }
        }
        
        try {
            // Verify teacher is assigned to this registration before processing
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            if (registration.getAssignedTeacherId() == null || 
                !registration.getAssignedTeacherId().equals(currentTeacher.getId())) {
                log.warn("Teacher {} attempted to submit review for registration {} which is not assigned to them", 
                         authentication.getName(), id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Anda tidak memiliki akses untuk mereview registrasi ini");
                return "redirect:/registrations/assigned";
            }
            
            StudentRegistrationResponse response = registrationService.submitTeacherReview(reviewRequest);
            log.info("Teacher review completed successfully: {}", response.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Review berhasil disimpan!");
            
            return "redirect:/registrations/assigned";
            
        } catch (Exception e) {
            log.error("Error during teacher review", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat memproses review: " + e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                List<Level> availableLevels = levelRepository.findByOrderByOrderNumber();
                
                model.addAttribute("registration", registration);
                model.addAttribute("reviewRequest", reviewRequest);
                model.addAttribute("reviewStatusOptions", StudentRegistration.TeacherReviewStatus.values());
                model.addAttribute("availableLevels", availableLevels);
                return "registrations/teacher-review";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/registrations/assigned";
            }
        }
    }
    
    @PostMapping("/{id}/teacher-review")
    @PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")
    public String submitTeacherReview(@PathVariable UUID id,
                                    @Valid @ModelAttribute TeacherReviewRequest reviewRequest,
                                    BindingResult bindingResult,
                                    Authentication authentication,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        log.info("Teacher {} submitting review for registration ID: {}", authentication.getName(), id);
        
        // Get current teacher user
        User currentTeacher = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Teacher user not found"));
        
        reviewRequest.setRegistrationId(id);
        reviewRequest.setTeacherId(currentTeacher.getId());
        
        if (bindingResult.hasErrors()) {
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                List<Level> availableLevels = levelRepository.findByOrderByOrderNumber();
                
                model.addAttribute("registration", registration);
                model.addAttribute("reviewStatusOptions", StudentRegistration.TeacherReviewStatus.values());
                model.addAttribute("availableLevels", availableLevels);
                return "registrations/teacher-review";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/registrations/assigned";
            }
        }
        
        try {
            StudentRegistrationResponse response = registrationService.submitTeacherReview(reviewRequest);
            log.info("Teacher review completed successfully: {}", response.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Review berhasil disimpan!");
            
            return "redirect:/registrations/assigned";
            
        } catch (Exception e) {
            log.error("Error during teacher review", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan saat memproses review: " + e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                List<Level> availableLevels = levelRepository.findByOrderByOrderNumber();
                
                model.addAttribute("registration", registration);
                model.addAttribute("reviewStatusOptions", StudentRegistration.TeacherReviewStatus.values());
                model.addAttribute("availableLevels", availableLevels);
                return "registrations/teacher-review";
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/registrations/assigned";
            }
        }
    }
    
    // Helper methods
    
    private List<StudentRegistration.RegistrationStatus> getAssignableStatuses() {
        return List.of(
            StudentRegistration.RegistrationStatus.SUBMITTED,
            StudentRegistration.RegistrationStatus.ASSIGNED
        );
    }
    
    private String getStatusMessage(StudentRegistration.RegistrationStatus status) {
        return switch (status) {
            case SUBMITTED -> "disubmit";
            case ASSIGNED -> "ditugaskan";
            case REVIEWED -> "direview";
            case COMPLETED -> "selesai";
            case REJECTED -> "ditolak";
            default -> "diproses";
        };
    }
}