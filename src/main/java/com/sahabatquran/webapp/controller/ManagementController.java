package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.*;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.StudentRegistration;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.StudentRegistrationRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.StudentRegistrationService;
import com.sahabatquran.webapp.service.TeacherLevelAssignmentService;
import com.sahabatquran.webapp.service.TeacherAvailabilityChangeRequestService;
import com.sahabatquran.webapp.service.CrossTermAnalyticsService;
import com.sahabatquran.webapp.service.ManagementDashboardService;
import com.sahabatquran.webapp.service.ResourceAllocationService;
import com.sahabatquran.webapp.service.TermActivationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/management")
@RequiredArgsConstructor
@Slf4j
public class ManagementController {
    
    private final TeacherLevelAssignmentService teacherLevelAssignmentService;
    private final TeacherAvailabilityChangeRequestService changeRequestService;
    private final UserRepository userRepository;
    private final AcademicTermRepository academicTermRepository;
    private final StudentRegistrationService registrationService;
    private final StudentRegistrationRepository registrationRepository;
    private final CrossTermAnalyticsService crossTermAnalyticsService;
    private final ManagementDashboardService managementDashboardService;
    private final ResourceAllocationService resourceAllocationService;
    private final TermActivationService termActivationService;
    
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
     * AJAX-friendly auto-assign endpoint (consumes JSON)
     */
    @PostMapping(value = "/teacher-level-assignments/auto-assign", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public ResponseEntity<Map<String, Object>> autoAssignTeachersAjax(@RequestBody Map<String, Object> payload,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String termIdStr = (String) payload.get("termId");
            Boolean overrideExisting = Boolean.valueOf(String.valueOf(payload.getOrDefault("overrideExisting", "false")));
            UUID termId = UUID.fromString(termIdStr);

            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            int created = teacherLevelAssignmentService.autoAssignTeachersToLevels(termId, overrideExisting, currentUser.getId());

            Map<String, Object> resp = new HashMap<>();
            resp.put("created", created);
            resp.put("status", "ok");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Error in AJAX auto-assignment", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "error");
            resp.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * AJAX-friendly workload balancing endpoint
     */
    @PostMapping(value = "/teacher-level-assignments/balance-workload", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public ResponseEntity<Map<String, Object>> balanceWorkloadAjax(@RequestBody Map<String, Object> payload,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String termIdStr = (String) payload.get("termId");
            UUID termId = UUID.fromString(termIdStr);

            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // For now, delegate to autoAssign with overrideExisting = true to rebalance
            int created = teacherLevelAssignmentService.autoAssignTeachersToLevels(termId, true, currentUser.getId());

            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "ok");
            resp.put("created", created);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Error balancing workload", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "error");
            resp.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * Export teacher assignments as CSV for a term
     */
    @GetMapping("/teacher-level-assignments/export")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public ResponseEntity<byte[]> exportTeacherAssignments(@RequestParam UUID termId,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            var dto = teacherLevelAssignmentService.getTeacherLevelAssignments(termId);

            StringBuilder csv = new StringBuilder();
            csv.append("teacher_username,teacher_name,level_name,competency_level,max_classes,assigned_by,assigned_at\n");
            if (dto.getAssignments() != null) {
                for (var a : dto.getAssignments()) {
                    csv.append(a.getTeacherUsername()).append(',')
                       .append(a.getTeacherName()).append(',')
                       .append(a.getLevelName()).append(',')
                       .append(a.getCompetencyLevel() != null ? a.getCompetencyLevel().name() : "").append(',')
                       .append(a.getMaxClassesForLevel() != null ? a.getMaxClassesForLevel() : "").append(',')
                       .append(a.getAssignedByName() != null ? a.getAssignedByName() : "").append(',')
                       .append(a.getAssignedAt() != null ? a.getAssignedAt().toString() : "").append('\n');
                }
            }

            byte[] bytes = csv.toString().getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teacher-assignments-" + termId + ".csv");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_PLAIN).body(bytes);
        } catch (Exception e) {
            log.error("Error exporting teacher assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Registration Analytics Dashboard
     * URL: /management/analytics/registrations
     */
    @GetMapping("/analytics/registrations")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String registrationAnalyticsDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                               Model model) {
        log.info("Loading registration analytics dashboard for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get registration statistics - provide default values for now
            Map<String, Long> statusCounts = new HashMap<>();
            for (StudentRegistration.RegistrationStatus status : StudentRegistration.RegistrationStatus.values()) {
                statusCounts.put(status.name(), 0L);
            }
            
            // Total registrations - default value
            long totalRegistrations = 0;
            
            // Pending assignments (SUBMITTED status) - default value  
            long pendingAssignments = 0;
            
            // Completed reviews (REVIEWED, COMPLETED, REJECTED statuses) - default value
            long completedReviews = 0;
            
            model.addAttribute("user", currentUser);
            model.addAttribute("totalRegistrations", totalRegistrations);
            model.addAttribute("pendingAssignments", pendingAssignments);
            model.addAttribute("completedReviews", completedReviews);
            model.addAttribute("statusCounts", statusCounts);
            model.addAttribute("pageTitle", "Registration Analytics");
            
            return "management/analytics/registrations";
            
        } catch (Exception e) {
            log.error("Error loading registration analytics", e);
            model.addAttribute("error", "Failed to load analytics data: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Registration Monitoring Dashboard
     * URL: /management/monitoring/registration-workflow
     */
    @GetMapping("/monitoring/registration-workflow")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String registrationWorkflowMonitoring(@AuthenticationPrincipal UserDetails userDetails,
                                               Model model) {
        log.info("Loading registration workflow monitoring for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get workflow performance metrics
            Map<String, Object> workflowMetrics = new HashMap<>();
            workflowMetrics.put("avgProcessingTime", "2.3 days"); // Mock data
            workflowMetrics.put("approvalRate", "85%"); // Mock data
            workflowMetrics.put("bottleneckAnalysis", "Teacher assignment phase"); // Mock data
            
            model.addAttribute("user", currentUser);
            model.addAttribute("workflowMetrics", workflowMetrics);
            model.addAttribute("pageTitle", "Registration Workflow Monitoring");
            
            return "management/monitoring/registration-workflow";
            
        } catch (Exception e) {
            log.error("Error loading workflow monitoring", e);
            model.addAttribute("error", "Failed to load monitoring data: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Registration Policy Configuration
     * URL: /management/policies/registration
     */
    @GetMapping("/policies/registration")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String registrationPolicies(@AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading registration policies for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Registration Policies");
            
            return "management/policies/registration";
            
        } catch (Exception e) {
            log.error("Error loading registration policies", e);
            model.addAttribute("error", "Failed to load policies: " + e.getMessage());
            return "error/500";
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
    
    /**
     * View Pending Change Requests
     * GET: /management/change-requests
     */
    @GetMapping("/change-requests")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String viewChangeRequests(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Viewing pending change requests for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<TeacherAvailabilityChangeRequestDto> pendingRequests = 
                    changeRequestService.getPendingChangeRequests();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("pageTitle", "Teacher Availability Change Requests");
            
            return "management/change-requests";
            
        } catch (Exception e) {
            log.error("Error viewing change requests", e);
            model.addAttribute("error", "Failed to load change requests: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Approve Change Request
     * POST: /management/change-requests/{requestId}/approve
     */
    @PostMapping("/change-requests/{requestId}/approve")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String approveChangeRequest(@PathVariable UUID requestId,
                                     @RequestParam(required = false) String reviewComments,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        log.info("Approving change request: {} by: {}", requestId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            changeRequestService.approveChangeRequest(requestId, currentUser.getId(), reviewComments);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Change request approved successfully!");
            
            return "redirect:/management/change-requests";
            
        } catch (Exception e) {
            log.error("Error approving change request", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to approve change request: " + e.getMessage());
            return "redirect:/management/change-requests";
        }
    }
    
    /**
     * Reject Change Request
     * POST: /management/change-requests/{requestId}/reject
     */
    @PostMapping("/change-requests/{requestId}/reject")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String rejectChangeRequest(@PathVariable UUID requestId,
                                    @RequestParam(required = false) String reviewComments,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        log.info("Rejecting change request: {} by: {}", requestId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            changeRequestService.rejectChangeRequest(requestId, currentUser.getId(), reviewComments);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Change request rejected successfully!");
            
            return "redirect:/management/change-requests";
            
        } catch (Exception e) {
            log.error("Error rejecting change request", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to reject change request: " + e.getMessage());
            return "redirect:/management/change-requests";
        }
    }
    
    /**
     * Cross-Term Analytics Dashboard
     * URL: /management/analytics/cross-term
     */
    @GetMapping("/analytics/cross-term")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String crossTermAnalyticsDashboard(@RequestParam(required = false) List<UUID> termIds,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             Model model) {
        log.info("Loading cross-term analytics for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get available terms for selection
            List<AcademicTerm> availableTerms = academicTermRepository.findAll();
            
            // Default to last 3 terms if none selected
            if (termIds == null || termIds.isEmpty()) {
                termIds = availableTerms.stream()
                    .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
                    .limit(3)
                    .map(AcademicTerm::getId)
                    .collect(Collectors.toList());
            }
            
            // Get analytics data
            CrossTermAnalyticsDto analyticsData = crossTermAnalyticsService.getCrossTermAnalytics(termIds);

            // Get filter options for the template
            List<User> availableTeachers = userRepository.findByRoleName("Pengajar");
            List<String> availableLevels = List.of("TAHSIN_1", "TAHSIN_2", "TAHFIZH_1", "TAHFIZH_2", "TAJWID_1");
            List<String> availablePrograms = List.of("TAHSIN", "TAHFIZH", "TAJWID");
            // Get available classes for the current terms
            List<Object[]> availableClasses = new ArrayList<>(); // Placeholder - would need ClassGroup repository

            model.addAttribute("user", currentUser);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("availableTeachers", availableTeachers);
            model.addAttribute("availableLevels", availableLevels);
            model.addAttribute("availablePrograms", availablePrograms);
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("selectedTermIds", termIds);
            model.addAttribute("analyticsData", analyticsData);
            model.addAttribute("pageTitle", "Cross-Term Analytics");
            
            return "analytics/cross-term";
            
        } catch (Exception e) {
            log.error("Error loading cross-term analytics", e);
            model.addAttribute("error", "Failed to load analytics data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Cross-Term Performance Comparison
     * URL: /analytics/cross-term/comparison
     */
    @GetMapping("/analytics/cross-term/comparison")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String crossTermComparison(@RequestParam List<UUID> termIds,
                                     @RequestParam(defaultValue = "SEMESTER_COMPARISON") String comparisonPeriod,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading cross-term comparison for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            CrossTermComparisonDto comparisonData = crossTermAnalyticsService
                    .compareTermPerformance(termIds, comparisonPeriod);
            
            model.addAttribute("user", currentUser);
            model.addAttribute("comparisonData", comparisonData);
            model.addAttribute("pageTitle", "Performance Comparison");
            
            return "analytics/cross-term-comparison";
            
        } catch (Exception e) {
            log.error("Error loading comparison data", e);
            model.addAttribute("error", "Failed to load comparison: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Teacher Performance Trends
     * URL: /analytics/cross-term/teacher-performance
     */
    @GetMapping("/analytics/cross-term/teacher-performance")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String teacherPerformanceTrends(@RequestParam List<UUID> termIds,
                                          @RequestParam(required = false) UUID teacherId,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          Model model) {
        log.info("Loading teacher performance trends for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get list of teachers for selection
            List<User> teachers = userRepository.findByRoleName("INSTRUCTOR");
            
            // If no teacher selected, show selection page
            if (teacherId == null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("teachers", teachers);
                model.addAttribute("selectedTermIds", termIds);
                model.addAttribute("pageTitle", "Select Teacher");
                return "analytics/teacher-selection";
            }
            
            TeacherPerformanceTrendDto performanceData = crossTermAnalyticsService
                    .getTeacherPerformanceTrends(termIds, teacherId);
            
            model.addAttribute("user", currentUser);
            model.addAttribute("performanceData", performanceData);
            model.addAttribute("pageTitle", "Teacher Performance Trends");
            
            return "analytics/teacher-performance";
            
        } catch (Exception e) {
            log.error("Error loading teacher performance", e);
            model.addAttribute("error", "Failed to load performance data: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Operational Trends Analysis
     * URL: /analytics/cross-term/operational-trends
     */
    @GetMapping("/analytics/cross-term/operational-trends")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String operationalTrends(@RequestParam List<UUID> termIds,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Loading operational trends for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            OperationalTrendsDto trendsData = crossTermAnalyticsService.getOperationalTrends(termIds);
            
            model.addAttribute("user", currentUser);
            model.addAttribute("trendsData", trendsData);
            model.addAttribute("pageTitle", "Operational Trends");
            
            return "analytics/operational-trends";
            
        } catch (Exception e) {
            log.error("Error loading operational trends", e);
            model.addAttribute("error", "Failed to load trends: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Executive Dashboard
     * URL: /analytics/cross-term/executive-dashboard
     */
    @GetMapping("/analytics/cross-term/executive-dashboard")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public String executiveDashboard(@RequestParam(required = false) List<UUID> termIds,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        log.info("Loading executive dashboard for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Default to last 3 terms if none selected
            if (termIds == null || termIds.isEmpty()) {
                List<AcademicTerm> recentTerms = academicTermRepository.findAll().stream()
                    .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
                    .limit(3)
                    .collect(Collectors.toList());
                termIds = recentTerms.stream().map(AcademicTerm::getId).collect(Collectors.toList());
            }
            
            ExecutiveDashboardDto dashboardData = crossTermAnalyticsService.getExecutiveDashboard(termIds);
            
            model.addAttribute("user", currentUser);
            model.addAttribute("dashboardData", dashboardData);
            model.addAttribute("pageTitle", "Executive Dashboard");
            
            return "analytics/executive-dashboard";
            
        } catch (Exception e) {
            log.error("Error loading executive dashboard", e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Export Analytics Report
     * URL: /analytics/cross-term/export
     */
    @GetMapping("/analytics/cross-term/export")
    @PreAuthorize("hasAuthority('STUDENT_REG_REPORT')")
    public ResponseEntity<byte[]> exportAnalyticsReport(@RequestParam List<UUID> termIds,
                                                       @RequestParam(defaultValue = "PDF") String format,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Exporting analytics report for user: {} in format: {}", userDetails.getUsername(), format);
        
        try {
            byte[] reportData = crossTermAnalyticsService.exportAnalyticsReport(termIds, format);
            
            HttpHeaders headers = new HttpHeaders();
            String filename = "cross-term-analytics." + format.toLowerCase();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            
            MediaType mediaType = format.equalsIgnoreCase("PDF") ? 
                MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM;
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(reportData);
                
        } catch (Exception e) {
            log.error("Error exporting report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Resource Allocation Management
     * URL: /management/resource-allocation/{termId}
     */
    @GetMapping("/resource-allocation/{termId}")
    @PreAuthorize("hasAuthority('RESOURCE_ALLOCATION_MANAGE')")
    public String resourceAllocation(@PathVariable UUID termId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Loading resource allocation for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
            
            // Load or create allocation draft
            var allocation = resourceAllocationService.getOrCreateDraft(termId, currentUser.getId());
            model.addAttribute("term", term);
            model.addAttribute("user", currentUser);
            model.addAttribute("allocation", allocation);
            model.addAttribute("pageTitle", "Resource Allocation Management");
            
            return "management/resource-allocation";
        } catch (Exception e) {
            log.error("Error loading resource allocation", e);
            return "error/500";
        }
    }
    
    /**
     * Teacher Assignments Management
     * URL: /management/teacher-assignments/{termId}
     */
    @GetMapping("/teacher-assignments/{termId}")
    @PreAuthorize("hasAuthority('TEACHER_LEVEL_ASSIGN')")
    public String teacherAssignments(@PathVariable UUID termId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Loading teacher assignments for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
            
            // Get teacher level assignment data
            TeacherLevelAssignmentDto assignmentData = teacherLevelAssignmentService
                    .getTeacherLevelAssignments(termId);
            
            // Prepare management-specific data
            Map<String, Object> managementData = new HashMap<>();
            if (assignmentData.getWorkloadAnalysis() != null) {
                managementData.put("totalTeachers", assignmentData.getWorkloadAnalysis().getTotalTeachers());
                managementData.put("assignedTeachers", assignmentData.getWorkloadAnalysis().getAssignedTeachers());
                managementData.put("unassignedTeachers", assignmentData.getWorkloadAnalysis().getUnassignedTeachers());
                managementData.put("workloadDistribution", assignmentData.getWorkloadAnalysis().getWorkloadDistribution());
                managementData.put("averageClassesPerTeacher", assignmentData.getWorkloadAnalysis().getAverageClassesPerTeacher());
            }
            
            // Calculate competency matching statistics
            Map<String, Integer> competencyStats = calculateCompetencyMatching(assignmentData);
            managementData.put("competencyMatch", competencyStats);
            
            model.addAttribute("term", term);
            model.addAttribute("user", currentUser);
            model.addAttribute("assignmentData", assignmentData);
            model.addAttribute("managementData", managementData);
            model.addAttribute("pageTitle", "Strategic Teacher Assignments");
            
            return "management/teacher-assignments";
        } catch (Exception e) {
            log.error("Error loading teacher assignments", e);
            return "error/500";
        }
    }
    
    /**
     * Teacher Competency Review
     * URL: /management/teacher-competency-review
     */
    @GetMapping("/teacher-competency-review")
    @PreAuthorize("hasAuthority('TEACHER_COMPETENCY_REVIEW')")
    public String teacherCompetencyReview(@RequestParam(required = false) UUID termId,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          Model model) {
        log.info("Loading teacher competency review for user: {}", userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Resolve selected term (fallback to first planning term)
            AcademicTerm selectedTerm = null;
            try {
                selectedTerm = getSelectedTerm(termId);
            } catch (Exception ex) {
                // ignore - template can render without term-specific data
            }

            // If we have a term, fetch assignment/competency data
            if (selectedTerm != null) {
                var assignmentData = teacherLevelAssignmentService.getTeacherLevelAssignments(selectedTerm.getId());
                model.addAttribute("assignmentData", assignmentData);
                model.addAttribute("selectedTerm", selectedTerm);
                model.addAttribute("availableTerms", academicTermRepository.findPlanningTerms());
            }

            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Teacher Competency Review");

            return "management/teacher-competency-review";
        } catch (Exception e) {
            log.error("Error loading teacher competency review", e);
            model.addAttribute("error", "Failed to load competency review: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Term Preparation Analytics
     * URL: /management/analytics/term-preparation
     */
    @GetMapping("/analytics/term-preparation")
    @PreAuthorize("hasAuthority('ANALYTICS_VIEW')")
    public String termPreparationAnalytics(@AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        log.info("Loading term preparation analytics for user: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            // Choose some recent planning terms for analytics
            List<AcademicTerm> planningTerms = academicTermRepository.findPlanningTerms();
            List<UUID> termIds = planningTerms.stream().limit(3).map(AcademicTerm::getId).collect(Collectors.toList());

            // Fallback: if no planning terms available, use all terms
            if (termIds.isEmpty()) {
                termIds = academicTermRepository.findAll().stream().map(AcademicTerm::getId).limit(3).collect(Collectors.toList());
            }

            var analyticsData = crossTermAnalyticsService.getCrossTermAnalytics(termIds);

            model.addAttribute("user", currentUser);
            model.addAttribute("analyticsData", analyticsData);
            model.addAttribute("pageTitle", "Term Preparation Analytics");

            return "management/analytics/term-preparation";
        } catch (Exception e) {
            log.error("Error loading term preparation analytics", e);
            return "error/500";
        }
    }
    
    /**
     * Term Activation Approval
     * URL: /management/term-activation-approval/{termId}
     */
    @GetMapping("/term-activation-approval/{termId}")
    @PreAuthorize("hasAuthority('TERM_ACTIVATION_APPROVE')")
    public String termActivationApproval(@PathVariable UUID termId,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        log.info("Loading term activation approval for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
            // Enrich model with summary metrics useful for approval
            var assignmentData = teacherLevelAssignmentService.getTeacherLevelAssignments(termId);
            var workload = assignmentData.getWorkloadAnalysis();

            var approval = termActivationService.getOrInit(termId);
            model.addAttribute("term", term);
            model.addAttribute("user", currentUser);
            model.addAttribute("assignmentData", assignmentData);
            model.addAttribute("workload", workload);
            model.addAttribute("approval", approval);
            model.addAttribute("pageTitle", "Term Activation Approval");

            return "management/term-activation-approval";
        } catch (Exception e) {
            log.error("Error loading term activation approval", e);
            return "error/500";
        }
    }
    
    /**
     * Teacher Availability Review
     * URL: /management/teacher-availability-review/{termId}
     */
    @GetMapping("/teacher-availability-review/{termId}")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_REVIEW')")
    public String teacherAvailabilityReview(@PathVariable UUID termId,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          Model model) {
        log.info("Loading teacher availability review for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
            // Use assignment service to provide teacher summaries including availability
            var assignmentData = teacherLevelAssignmentService.getTeacherLevelAssignments(termId);
            var pendingRequests = changeRequestService.getPendingChangeRequests();

            model.addAttribute("term", term);
            model.addAttribute("user", currentUser);
            model.addAttribute("assignmentData", assignmentData);
            model.addAttribute("pendingChangeRequests", pendingRequests);
            model.addAttribute("pageTitle", "Teacher Availability Review");

            return "management/teacher-availability-review";
        } catch (Exception e) {
            log.error("Error loading teacher availability review", e);
            return "error/500";
        }
    }
    
    /**
     * Term Preparation Dashboard
     * URL: /management/term-preparation-dashboard/{termId}
     */
    @GetMapping("/term-preparation-dashboard/{termId}")
    @PreAuthorize("hasAuthority('TERM_PREPARATION_MONITOR')")
    public String termPreparationDashboard(@PathVariable UUID termId,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        log.info("Loading term preparation dashboard for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
            // Gather analytics + assignment summaries for dashboard
            var assignmentData = teacherLevelAssignmentService.getTeacherLevelAssignments(termId);
            var analytics = crossTermAnalyticsService.getCrossTermAnalytics(List.of(termId));

            // Derive simple readiness metrics
            int totalTeachers = assignmentData.getWorkloadAnalysis() != null ? assignmentData.getWorkloadAnalysis().getTotalTeachers() : 0;
            int assignedTeachers = assignmentData.getWorkloadAnalysis() != null ? assignmentData.getWorkloadAnalysis().getAssignedTeachers() : 0;
            int readinessPercent = totalTeachers > 0 ? (int) Math.round((assignedTeachers * 100.0) / totalTeachers) : 0;

            var blockers = managementDashboardService.getBlockers(termId);
            var blockerMetrics = managementDashboardService.getBlockerMetrics(termId);
            model.addAttribute("term", term);
            model.addAttribute("user", currentUser);
            model.addAttribute("assignmentData", assignmentData);
            model.addAttribute("analytics", analytics);
            model.addAttribute("teacherReadinessPercent", readinessPercent);
            model.addAttribute("blockers", blockers);
            model.addAttribute("blockerMetrics", blockerMetrics);
            model.addAttribute("pageTitle", "Term Preparation Dashboard");

            return "management/term-preparation-dashboard";
        } catch (Exception e) {
            log.error("Error loading term preparation dashboard", e);
            return "error/500";
        }
    }

    // NOTE: Removed generic POST handler for /term-preparation-dashboard/{termId} to surface
    // unintended POSTs. All dashboard-side actions must target explicit action endpoints.
    
    /**
     * Assignment Validation
     * URL: /management/assignment-validation/{termId}
     */
    @GetMapping("/assignment-validation/{termId}")
    @PreAuthorize("hasAuthority('ASSIGNMENT_VALIDATION')")
    public String assignmentValidation(@PathVariable UUID termId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading assignment validation for term: {} by user: {}", termId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
            // Provide assignments and basic validation metrics
            var assignmentData = teacherLevelAssignmentService.getTeacherLevelAssignments(termId);
            model.addAttribute("term", term);
            model.addAttribute("user", currentUser);
            model.addAttribute("assignmentData", assignmentData);
            model.addAttribute("pageTitle", "Assignment Validation");

            return "management/assignment-validation";
        } catch (Exception e) {
            log.error("Error loading assignment validation", e);
            return "error/500";
        }
    }
    
    /**
     * Helper method to calculate competency matching statistics
     */
    private Map<String, Integer> calculateCompetencyMatching(TeacherLevelAssignmentDto assignmentData) {
        Map<String, Integer> stats = new HashMap<>();
        
        if (assignmentData.getAssignments() != null) {
            int totalAssignments = assignmentData.getAssignments().size();
            long perfectMatches = assignmentData.getAssignments().stream()
                .filter(a -> a.getCompetencyLevel() != null)
                .count();
            
            stats.put("totalAssignments", totalAssignments);
            stats.put("perfectMatches", (int) perfectMatches);
            stats.put("matchPercentage", totalAssignments > 0 ? (int) ((perfectMatches * 100) / totalAssignments) : 0);
        } else {
            stats.put("totalAssignments", 0);
            stats.put("perfectMatches", 0);
            stats.put("matchPercentage", 0);
        }
        
        return stats;
    }

    // ====================== POST ACTION ENDPOINTS (NEW) ======================

    @PostMapping("/blockers/resolve")
    @PreAuthorize("hasAuthority('TERM_PREPARATION_MONITOR')")
    public String resolveBlocker(@RequestParam UUID blockerId,
                                 @RequestParam UUID termId,
                                 @RequestParam(required = false) String notes,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            var currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            managementDashboardService.resolveBlocker(blockerId, currentUser.getId(), notes);
            redirectAttributes.addFlashAttribute("success", "Blocker resolved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/management/term-preparation-dashboard/" + termId;
    }

    @PostMapping("/resource-allocation/{termId}/approve")
    @PreAuthorize("hasAuthority('RESOURCE_ALLOCATION_MANAGE')")
    public String approveResourceAllocation(@PathVariable UUID termId,
                                            @RequestParam(required = false) String notes,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            RedirectAttributes redirectAttributes) {
        try {
            var currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            resourceAllocationService.approve(termId, currentUser.getId(), notes);
            redirectAttributes.addFlashAttribute("success", "Resource allocation approved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/management/resource-allocation/" + termId;
    }

    @PostMapping("/term-activation-approval/{termId}/checklist")
    @PreAuthorize("hasAuthority('TERM_ACTIVATION_APPROVE')")
    public String updateActivationChecklist(@PathVariable UUID termId,
                                            @RequestParam(defaultValue = "false") boolean teachersAssignedConfirmed,
                                            @RequestParam(defaultValue = "false") boolean studentsEnrolledConfirmed,
                                            @RequestParam(defaultValue = "false") boolean schedulesPlottedConfirmed,
                                            @RequestParam(defaultValue = "false") boolean resourcesAllocatedConfirmed,
                                            @RequestParam(defaultValue = "false") boolean systemsReadyConfirmed) {
        termActivationService.updateChecklist(termId, teachersAssignedConfirmed, studentsEnrolledConfirmed,
                schedulesPlottedConfirmed, resourcesAllocatedConfirmed, systemsReadyConfirmed);
        return "redirect:/management/term-activation-approval/" + termId;
    }

    @PostMapping("/term-activation-approval/{termId}/approve")
    @PreAuthorize("hasAuthority('TERM_ACTIVATION_APPROVE')")
    public String approveActivation(@PathVariable UUID termId,
                                    @RequestParam(required = false) String approvalComments,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            var currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            termActivationService.approve(termId, currentUser.getId(), approvalComments);
            redirectAttributes.addFlashAttribute("success", "Term activation approved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/management/term-activation-approval/" + termId;
    }
}