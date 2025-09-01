package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.TeacherLevelAssignmentDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.TeacherLevelAssignmentService;
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
@RequestMapping("/management")
@RequiredArgsConstructor
@Slf4j
public class ManagementController {
    
    private final TeacherLevelAssignmentService teacherLevelAssignmentService;
    private final UserRepository userRepository;
    private final AcademicTermRepository academicTermRepository;
    
    /**
     * Teacher Level Assignments Dashboard
     * URL: /management/teacher-level-assignments
     */
    @GetMapping("/teacher-level-assignments")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String teacherLevelAssignments(@RequestParam(required = false) UUID termId,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        log.info("Loading teacher level assignments for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get active or selected term
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get teacher level assignment data
            TeacherLevelAssignmentDto assignmentData = teacherLevelAssignmentService
                    .getTeacherLevelAssignments(selectedTerm.getId());
            
            // Get available terms for management
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("assignmentData", assignmentData);
            model.addAttribute("pageTitle", "Teacher Level Assignments");
            
            return "management/teacher-level-assignments";
            
        } catch (Exception e) {
            log.error("Error loading teacher level assignments", e);
            model.addAttribute("error", "Failed to load assignment data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Create/Update Teacher Level Assignment
     * POST: /management/teacher-level-assignments/assign
     */
    @PostMapping("/teacher-level-assignments/assign")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String assignTeacherToLevel(@RequestParam UUID teacherId,
                                     @RequestParam UUID levelId,
                                     @RequestParam UUID termId,
                                     @RequestParam String competencyLevel,
                                     @RequestParam(required = false) Integer maxClassesForLevel,
                                     @RequestParam(required = false) String specialization,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        log.info("Assigning teacher: {} to level: {} for term: {} by user: {}", 
                teacherId, levelId, termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            teacherLevelAssignmentService.assignTeacherToLevel(
                    teacherId, levelId, termId, competencyLevel, maxClassesForLevel, 
                    specialization, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Teacher assignment created successfully");
            
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error assigning teacher to level", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create assignment: " + e.getMessage());
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
        }
    }
    
    /**
     * Update Teacher Level Assignment
     * PUT: /management/teacher-level-assignments/{assignmentId}
     */
    @PostMapping("/teacher-level-assignments/{assignmentId}/update")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String updateTeacherAssignment(@PathVariable UUID assignmentId,
                                        @RequestParam String competencyLevel,
                                        @RequestParam(required = false) Integer maxClassesForLevel,
                                        @RequestParam(required = false) String specialization,
                                        @RequestParam UUID termId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        log.info("Updating teacher assignment: {} by user: {}", assignmentId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            teacherLevelAssignmentService.updateTeacherAssignment(
                    assignmentId, competencyLevel, maxClassesForLevel, specialization, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                "Teacher assignment updated successfully");
            
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error updating teacher assignment", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to update assignment: " + e.getMessage());
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
        }
    }
    
    /**
     * Delete Teacher Level Assignment
     * DELETE: /management/teacher-level-assignments/{assignmentId}
     */
    @PostMapping("/teacher-level-assignments/{assignmentId}/delete")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String deleteTeacherAssignment(@PathVariable UUID assignmentId,
                                        @RequestParam UUID termId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        log.info("Deleting teacher assignment: {} by user: {}", assignmentId, userDetails.getUsername());
        
        try {
            teacherLevelAssignmentService.deleteTeacherAssignment(assignmentId);
            
            redirectAttributes.addFlashAttribute("success", 
                "Teacher assignment deleted successfully");
            
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error deleting teacher assignment", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to delete assignment: " + e.getMessage());
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
        }
    }
    
    /**
     * Teacher Workload Analysis
     * URL: /management/teacher-workload-analysis
     */
    @GetMapping("/teacher-workload-analysis")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String teacherWorkloadAnalysis(@RequestParam(required = false) UUID termId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        log.info("Loading teacher workload analysis for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get workload analysis data
            var workloadData = teacherLevelAssignmentService
                    .getTeacherWorkloadAnalysis(selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("workloadData", workloadData);
            model.addAttribute("pageTitle", "Teacher Workload Analysis");
            
            return "management/teacher-workload-analysis";
            
        } catch (Exception e) {
            log.error("Error loading teacher workload analysis", e);
            model.addAttribute("error", "Failed to load workload data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Auto-assign Teachers Based on Availability and Competency
     * POST: /management/teacher-level-assignments/auto-assign
     */
    @PostMapping("/teacher-level-assignments/auto-assign")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String autoAssignTeachers(@RequestParam UUID termId,
                                   @RequestParam(defaultValue = "false") Boolean overrideExisting,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        log.info("Auto-assigning teachers for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            int assignmentsCreated = teacherLevelAssignmentService
                    .autoAssignTeachersToLevels(termId, overrideExisting, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Auto-assignment completed. %d teacher assignments created.", assignmentsCreated));
            
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error in auto-assignment", e);
            redirectAttributes.addFlashAttribute("error", 
                "Auto-assignment failed: " + e.getMessage());
            return "redirect:/management/teacher-level-assignments?termId=" + termId;
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
        
        // Default to first planning term
        List<AcademicTerm> planningTerms = academicTermRepository.findPlanningTerms();
        if (!planningTerms.isEmpty()) {
            return planningTerms.get(0);
        }
        
        throw new RuntimeException("No planning terms available");
    }
}