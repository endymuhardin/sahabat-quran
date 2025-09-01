package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.AssessmentFoundationDto;
import com.sahabatquran.webapp.dto.LevelDistributionDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.*;
import com.sahabatquran.webapp.service.AcademicPlanningService;
import com.sahabatquran.webapp.service.ClassGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/academic")
@RequiredArgsConstructor
@Slf4j
public class AcademicPlanningController {
    
    private final AcademicPlanningService academicPlanningService;
    private final ClassGenerationService classGenerationService;
    private final UserRepository userRepository;
    private final AcademicTermRepository academicTermRepository;
    
    /**
     * Assessment Prerequisites Dashboard
     * URL: /academic/assessment-foundation
     */
    @GetMapping("/assessment-foundation")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String assessmentFoundation(@RequestParam(required = false) UUID termId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        log.info("Loading assessment foundation dashboard for user: {}", userDetails.getUsername());
        
        try {
            // Get current user
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get active or selected term
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get assessment foundation data
            AssessmentFoundationDto foundationData = academicPlanningService
                    .getAssessmentFoundationData(selectedTerm.getId());
            
            // Get all available terms for dropdown
            List<AcademicTerm> availableTerms = academicTermRepository.findAllOrderByStartDateDesc();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("foundationData", foundationData);
            model.addAttribute("pageTitle", "Assessment Prerequisites Dashboard");
            
            return "academic/assessment-foundation";
            
        } catch (Exception e) {
            log.error("Error loading assessment foundation dashboard", e);
            model.addAttribute("error", "Failed to load assessment data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Level Distribution Analysis
     * URL: /academic/level-distribution
     */
    @GetMapping("/level-distribution")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String levelDistribution(@RequestParam(required = false) UUID termId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Loading level distribution analysis for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            LevelDistributionDto distributionData = academicPlanningService
                    .getLevelDistributionData(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findAllOrderByStartDateDesc();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("distributionData", distributionData);
            model.addAttribute("pageTitle", "Level Distribution Analysis");
            
            return "academic/level-distribution";
            
        } catch (Exception e) {
            log.error("Error loading level distribution analysis", e);
            model.addAttribute("error", "Failed to load distribution data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Semester Launch Dashboard
     * URL: /academic/semester-launch
     */
    @GetMapping("/semester-launch")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String semesterLaunch(@RequestParam(required = false) UUID termId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        log.info("Loading semester launch dashboard for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Check if term can be launched
            boolean canLaunch = academicPlanningService.canLaunchPreparationProcess(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("canLaunch", canLaunch);
            model.addAttribute("pageTitle", "Semester Preparation Launch");
            
            return "academic/semester-launch";
            
        } catch (Exception e) {
            log.error("Error loading semester launch dashboard", e);
            model.addAttribute("error", "Failed to load launch data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Initiate Semester Preparation Process
     * POST: /academic/semester-launch/initiate
     */
    @PostMapping("/semester-launch/initiate")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String initiateSemesterPreparation(@RequestParam UUID termId,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            RedirectAttributes redirectAttributes) {
        log.info("Initiating semester preparation for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            academicPlanningService.initiatePreparationProcess(termId, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Semester preparation process initiated successfully. Notifications sent to all stakeholders.");
            
            return "redirect:/academic/availability-monitoring?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error initiating semester preparation", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to initiate preparation process: " + e.getMessage());
            return "redirect:/academic/semester-launch?termId=" + termId;
        }
    }
    
    /**
     * Teacher Availability Monitoring
     * URL: /academic/availability-monitoring
     */
    @GetMapping("/availability-monitoring")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_VIEW')")
    public String availabilityMonitoring(@RequestParam(required = false) UUID termId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        log.info("Loading availability monitoring for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get availability submission status
            var availabilityStatus = academicPlanningService
                    .getTeacherAvailabilityStatus(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findActiveTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("availabilityStatus", availabilityStatus);
            model.addAttribute("pageTitle", "Teacher Availability Monitoring");
            
            return "academic/availability-monitoring";
            
        } catch (Exception e) {
            log.error("Error loading availability monitoring", e);
            model.addAttribute("error", "Failed to load availability data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Class Generation Readiness Check
     * URL: /academic/generation-readiness
     */
    @GetMapping("/generation-readiness")
    @PreAuthorize("hasAuthority('CLASS_GENERATION_RUN')")
    public String generationReadiness(@RequestParam(required = false) UUID termId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading generation readiness check for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get generation readiness status
            var readinessStatus = classGenerationService.getGenerationReadiness(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("readinessStatus", readinessStatus);
            model.addAttribute("pageTitle", "Class Generation Readiness");
            
            return "academic/generation-readiness";
            
        } catch (Exception e) {
            log.error("Error loading generation readiness", e);
            model.addAttribute("error", "Failed to load readiness data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Generate Classes
     * POST: /academic/generate-classes
     */
    @PostMapping("/generate-classes")
    @PreAuthorize("hasAuthority('CLASS_GENERATION_RUN')")
    public String generateClasses(@RequestParam UUID termId,
                                 @RequestParam(defaultValue = "7") Integer defaultMinClassSize,
                                 @RequestParam(defaultValue = "10") Integer defaultMaxClassSize,
                                 @RequestParam(defaultValue = "40") Integer newStudentPercentage,
                                 @RequestParam(defaultValue = "6") Integer maxClassesPerTeacher,
                                 @RequestParam(defaultValue = "false") Boolean allowUndersizedClasses,
                                 @RequestParam(defaultValue = "BALANCE") String priorityStrategy,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        log.info("Generating classes for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Build generation parameters
            var parameters = com.sahabatquran.webapp.dto.ClassGenerationDto.GenerationParameters.builder()
                    .defaultMinClassSize(defaultMinClassSize)
                    .defaultMaxClassSize(defaultMaxClassSize)
                    .newExistingStudentRatio(BigDecimal.valueOf(newStudentPercentage / 100.0))
                    .maxClassesPerTeacher(maxClassesPerTeacher)
                    .allowUndersizedClasses(allowUndersizedClasses)
                    .priorityStrategy(priorityStrategy)
                    .optimizeForTeacherWorkload(true)
                    .levelSpecificSizes(new HashMap<>())
                    .build();
            
            // Generate classes
            var proposal = classGenerationService.generateClasses(termId, parameters, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Class generation completed! Generated %d classes with optimization score %.1f", 
                        proposal.getClasses().size(), proposal.getOptimizationScore()));
            
            return "redirect:/academic/class-refinement?termId=" + termId + "&proposalId=" + proposal.getProposalId();
            
        } catch (Exception e) {
            log.error("Error generating classes", e);
            redirectAttributes.addFlashAttribute("error", 
                "Class generation failed: " + e.getMessage());
            return "redirect:/academic/generation-readiness?termId=" + termId;
        }
    }
    
    /**
     * Class Refinement Interface
     * URL: /academic/class-refinement
     */
    @GetMapping("/class-refinement")
    @PreAuthorize("hasAuthority('CLASS_GENERATION_REVIEW')")
    public String classRefinement(@RequestParam(required = false) UUID termId,
                                 @RequestParam(required = false) UUID proposalId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        log.info("Loading class refinement interface for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get refinement data
            var refinementData = proposalId != null ? 
                    classGenerationService.getClassRefinementData(selectedTerm.getId(), proposalId) : null;
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("refinementData", refinementData);
            model.addAttribute("proposalId", proposalId);
            model.addAttribute("pageTitle", "Class Refinement & Optimization");
            
            return "academic/class-refinement";
            
        } catch (Exception e) {
            log.error("Error loading class refinement interface", e);
            model.addAttribute("error", "Failed to load refinement data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Transfer Student Between Classes
     * POST: /academic/class-refinement/transfer-student
     */
    @PostMapping("/class-refinement/transfer-student")
    @PreAuthorize("hasAuthority('CLASS_GENERATION_REVIEW')")
    @ResponseBody
    public Map<String, Object> transferStudent(@RequestParam UUID proposalId,
                                             @RequestParam UUID studentId,
                                             @RequestParam UUID fromClassId,
                                             @RequestParam UUID toClassId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Transferring student: {} from class: {} to class: {} by user: {}", 
                studentId, fromClassId, toClassId, userDetails.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            classGenerationService.transferStudent(proposalId, studentId, fromClassId, toClassId);
            
            response.put("success", true);
            response.put("message", "Student transferred successfully");
            
        } catch (Exception e) {
            log.error("Error transferring student", e);
            response.put("success", false);
            response.put("message", "Transfer failed: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Approve Class Proposal
     * POST: /academic/class-refinement/approve-override
     */
    @PostMapping("/class-refinement/approve-proposal")
    @PreAuthorize("hasAuthority('SCHEDULE_APPROVE')")
    public String approveProposal(@RequestParam UUID proposalId,
                                 @RequestParam UUID termId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        log.info("Approving class proposal: {} by user: {}", proposalId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            classGenerationService.approveClassProposal(proposalId, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Class proposal approved successfully. Classes have been created and are ready for final review.");
            
            return "redirect:/academic/final-schedule-review?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error approving class proposal", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to approve proposal: " + e.getMessage());
            return "redirect:/academic/class-refinement?termId=" + termId + "&proposalId=" + proposalId;
        }
    }
    
    /**
     * Final Schedule Review
     * URL: /academic/final-schedule-review
     */
    @GetMapping("/final-schedule-review")
    @PreAuthorize("hasAuthority('SCHEDULE_APPROVE')")
    public String finalScheduleReview(@RequestParam(required = false) UUID termId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading final schedule review for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get final schedule data
            var scheduleData = academicPlanningService.getFinalScheduleData(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("scheduleData", scheduleData);
            model.addAttribute("pageTitle", "Final Schedule Review");
            
            return "academic/final-schedule-review";
            
        } catch (Exception e) {
            log.error("Error loading final schedule review", e);
            model.addAttribute("error", "Failed to load schedule data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * System Implementation
     * URL: /academic/system-implementation
     */
    @GetMapping("/system-implementation")
    @PreAuthorize("hasAuthority('SYSTEM_GOLIVE_MANAGE')")
    public String systemImplementation(@RequestParam(required = false) UUID termId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        log.info("Loading system implementation for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get implementation status
            var implementationStatus = academicPlanningService.getSystemImplementationStatus(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("implementationStatus", implementationStatus);
            model.addAttribute("pageTitle", "System Implementation");
            
            return "academic/system-implementation";
            
        } catch (Exception e) {
            log.error("Error loading system implementation", e);
            model.addAttribute("error", "Failed to load implementation data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * System Go-Live
     * URL: /academic/system-golive
     */
    @GetMapping("/system-golive")
    @PreAuthorize("hasAuthority('SYSTEM_GOLIVE_MANAGE')")
    public String systemGoLive(@RequestParam(required = false) UUID termId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        log.info("Loading system go-live for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get go-live readiness
            var goLiveStatus = academicPlanningService.getGoLiveReadiness(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("goLiveStatus", goLiveStatus);
            model.addAttribute("pageTitle", "System Go-Live");
            
            return "academic/system-golive";
            
        } catch (Exception e) {
            log.error("Error loading system go-live", e);
            model.addAttribute("error", "Failed to load go-live data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Execute System Go-Live
     * POST: /academic/system-golive/execute
     */
    @PostMapping("/system-golive/execute")
    @PreAuthorize("hasAuthority('SYSTEM_GOLIVE_MANAGE')")
    public String executeGoLive(@RequestParam UUID termId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        log.info("Executing system go-live for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            academicPlanningService.executeSystemGoLive(termId, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "System go-live executed successfully! Classes are now active and ready for students.");
            
            return "redirect:/academic/system-golive?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error executing system go-live", e);
            redirectAttributes.addFlashAttribute("error", 
                "Go-live execution failed: " + e.getMessage());
            return "redirect:/academic/system-golive?termId=" + termId;
        }
    }
    
    /**
     * Helper method to get selected term or default to active planning term
     */
    private AcademicTerm getSelectedTerm(UUID termId) {
        if (termId != null) {
            return academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
        }
        
        // Default to first planning term
        List<AcademicTerm> planningTerms = academicTermRepository.findPlanningTerms();
        if (!planningTerms.isEmpty()) {
            return planningTerms.get(0);
        }
        
        throw new RuntimeException("No planning terms available");
    }
}