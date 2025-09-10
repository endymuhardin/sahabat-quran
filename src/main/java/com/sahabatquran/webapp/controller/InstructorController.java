package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.TeacherAvailabilityDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.repository.SessionRepository;
import com.sahabatquran.webapp.service.TeacherAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
@Slf4j
public class InstructorController {
    
    private final TeacherAvailabilityService teacherAvailabilityService;
    private final UserRepository userRepository;
    private final AcademicTermRepository academicTermRepository;
    private final SessionRepository sessionRepository;
    
    /**
     * Instructor Dashboard
     * URL: /instructor/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        log.info("Loading instructor dashboard for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Instructor Dashboard");
            
            return "instructor/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading instructor dashboard", e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Teacher Availability Submission Interface
     * URL: /instructor/availability-submission
     */
    @GetMapping("/availability-submission")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_SUBMIT')")
    public String availabilitySubmission(@RequestParam(required = false) UUID termId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        log.info("Loading availability submission for instructor: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get active or selected term
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get current availability data for this teacher and term
            TeacherAvailabilityDto.AvailabilityMatrix currentMatrix = teacherAvailabilityService
                    .getTeacherAvailabilityMatrix(currentUser.getId(), selectedTerm.getId());
            
            // Get available terms for instructor submission (planning terms only)
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            // Check if submission is allowed
            boolean canSubmit = teacherAvailabilityService.canSubmitAvailability(selectedTerm.getId());
            
            // Get all active sessions for dynamic display
            var allSessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("availabilityMatrix", currentMatrix);
            model.addAttribute("allSessions", allSessions);
            model.addAttribute("canSubmit", canSubmit);
            model.addAttribute("pageTitle", "Teacher Availability Submission");
            
            return "instructor/availability-submission";
            
        } catch (Exception e) {
            log.error("Error loading availability submission page", e);
            model.addAttribute("error", "Failed to load availability form: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Submit Teacher Availability
     * POST: /instructor/availability-submission
     */
    @PostMapping("/availability-submission")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_SUBMIT')")
    public String submitAvailability(@RequestParam UUID termId,
                                   @RequestParam(defaultValue = "6") Integer maxClassesPerWeek,
                                   @RequestParam(required = false) String preferences,
                                   @RequestParam(required = false) List<String> preferredLevels,
                                   @RequestParam(required = false) String specialConstraints,
                                   @RequestParam List<String> availabilitySlots, // Format: "dayOfWeek-sessionTime"
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        log.info("Processing availability submission for instructor: {} and term: {}", 
                userDetails.getUsername(), termId);
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Build availability matrix from form data
            TeacherAvailabilityDto.AvailabilityMatrix matrix = TeacherAvailabilityDto.AvailabilityMatrix.builder()
                    .maxClassesPerWeek(maxClassesPerWeek)
                    .preferences(preferences)
                    .preferredLevels(preferredLevels)
                    .specialConstraints(specialConstraints)
                    .build();
            
            // Process availability slots
            teacherAvailabilityService.submitTeacherAvailability(
                    currentUser.getId(), termId, matrix, availabilitySlots);
            
            redirectAttributes.addFlashAttribute("success", 
                "Availability submitted successfully. You can modify it until the deadline.");
            
            return "redirect:/instructor/availability-submission?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error submitting teacher availability", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to submit availability: " + e.getMessage());
            return "redirect:/instructor/availability-submission?termId=" + termId;
        }
    }
    
    /**
     * My Assigned Classes Dashboard
     * URL: /instructor/my-classes
     */
    @GetMapping("/my-classes")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String myClasses(@RequestParam(required = false) UUID termId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        log.info("Loading assigned classes for instructor: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get assigned classes for this instructor
            var assignedClasses = teacherAvailabilityService
                    .getInstructorAssignedClasses(currentUser.getId(), selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findActiveTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("assignedClasses", assignedClasses);
            model.addAttribute("pageTitle", "My Assigned Classes");
            
            return "instructor/my-classes";
            
        } catch (Exception e) {
            log.error("Error loading assigned classes", e);
            model.addAttribute("error", "Failed to load classes: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Individual Class Preparation Interface
     * URL: /instructor/class/{classId}/preparation
     */
    @GetMapping("/class/{classId}/preparation")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String classPreparation(@PathVariable UUID classId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        log.info("Loading class preparation for class: {} by instructor: {}", 
                classId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify instructor has access to this class
            boolean hasAccess = teacherAvailabilityService
                    .hasAccessToClass(currentUser.getId(), classId);
            
            if (!hasAccess) {
                model.addAttribute("error", "You do not have access to this class");
                return "error/403";
            }
            
            // Get class preparation data
            var preparationData = teacherAvailabilityService
                    .getClassPreparationData(classId, currentUser.getId());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("classId", classId);
            model.addAttribute("preparationData", preparationData);
            model.addAttribute("pageTitle", "Class Preparation");
            
            return "instructor/class-preparation";
            
        } catch (Exception e) {
            log.error("Error loading class preparation", e);
            model.addAttribute("error", "Failed to load preparation data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Update Preparation Checklist
     * POST: /instructor/class/{classId}/preparation/checklist
     */
    @PostMapping("/class/{classId}/preparation/checklist")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String updatePreparationChecklist(@PathVariable UUID classId,
                                           @RequestParam List<UUID> completedItems,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {
        log.info("Updating preparation checklist for class: {} by instructor: {}", 
                classId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            teacherAvailabilityService.updatePreparationChecklist(
                    classId, currentUser.getId(), completedItems);
            
            redirectAttributes.addFlashAttribute("success", 
                "Preparation checklist updated successfully");
            
            return "redirect:/instructor/class/" + classId + "/preparation";
            
        } catch (Exception e) {
            log.error("Error updating preparation checklist", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to update checklist: " + e.getMessage());
            return "redirect:/instructor/class/" + classId + "/preparation";
        }
    }
    
    /**
     * Upload Class Materials
     * POST: /instructor/class/{classId}/materials/upload
     */
    @PostMapping("/class/{classId}/materials/upload")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String uploadClassMaterials(@PathVariable UUID classId,
                                     @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                     @RequestParam String materialType,
                                     @RequestParam String materialTitle,
                                     @RequestParam(defaultValue = "false") Boolean shareWithStudents,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        log.info("Uploading material for class: {} by instructor: {}", 
                classId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            teacherAvailabilityService.uploadClassMaterial(
                    classId, currentUser.getId(), file, materialType, materialTitle, shareWithStudents);
            
            redirectAttributes.addFlashAttribute("success", 
                "Material uploaded successfully");
            
            return "redirect:/instructor/class/" + classId + "/preparation";
            
        } catch (Exception e) {
            log.error("Error uploading class material", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to upload material: " + e.getMessage());
            return "redirect:/instructor/class/" + classId + "/preparation";
        }
    }
    
    /**
     * Helper method to get selected term
     */
    private AcademicTerm getSelectedTerm(UUID termId) {
        if (termId != null) {
            return academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
        }
        
        // Default to first planning term for instructor availability submission
        List<AcademicTerm> planningTerms = academicTermRepository.findPlanningTerms();
        if (!planningTerms.isEmpty()) {
            return planningTerms.get(0);
        }
        
        // If no planning terms available, fallback to active terms
        List<AcademicTerm> activeTerms = academicTermRepository.findActiveTerms();
        if (!activeTerms.isEmpty()) {
            return activeTerms.get(0);
        }
        
        throw new RuntimeException("No available terms");
    }
}