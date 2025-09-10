package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.SessionMonitoringDto;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.SessionExecutionService;
import com.sahabatquran.webapp.service.SessionMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Slf4j
public class SessionMonitoringController {
    
    private final SessionMonitoringService sessionMonitoringService;
    private final SessionExecutionService sessionExecutionService;
    private final UserRepository userRepository;
    
    /**
     * Real-time monitoring dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    public String monitoringDashboard(Model model) {
        log.info("Loading real-time monitoring dashboard");
        
        SessionMonitoringDto monitoring = sessionMonitoringService.getTodayMonitoring();
        model.addAttribute("monitoring", monitoring);
        model.addAttribute("pageTitle", "Real-time Session Monitoring");
        
        return "monitoring/dashboard";
    }
    
    /**
     * Session monitoring main page (expected by tests)
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String sessionMonitoring(Model model) {
        log.info("Loading session monitoring page");
        
        SessionMonitoringDto monitoring = sessionMonitoringService.getTodayMonitoring();
        model.addAttribute("monitoring", monitoring);
        model.addAttribute("pageTitle", "Session Monitoring");
        
        return "monitoring/sessions";
    }
    
    /**
     * Feedback Analytics dashboard
     */
    @GetMapping("/feedback-analytics")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String feedbackAnalytics(Model model) {
        log.info("Loading feedback analytics page");
        
        model.addAttribute("feedbackStats", sessionMonitoringService.getFeedbackAnalytics());
        model.addAttribute("pageTitle", "Feedback Analytics");
        
        return "monitoring/feedback-analytics";
    }
    
    /**
     * Emergency Management dashboard
     */
    @GetMapping("/emergency")
    @PreAuthorize("hasAuthority('ACADEMIC_TERM_MANAGE')")
    public String emergencyManagement(Model model) {
        log.info("Loading emergency management page");
        
        model.addAttribute("emergencyAlerts", sessionMonitoringService.getEmergencyAlerts());
        model.addAttribute("substituteRequests", sessionMonitoringService.getSubstituteRequests());
        model.addAttribute("availableSubstitutes", sessionMonitoringService.getAvailableSubstituteTeachers());
        model.addAttribute("pageTitle", "Emergency Management");
        
        return "monitoring/emergency";
    }
    
    /**
     * Live session monitoring data (AJAX endpoint)
     */
    @GetMapping("/api/live-data")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    @ResponseBody
    public ResponseEntity<SessionMonitoringDto> getLiveMonitoringData() {
        SessionMonitoringDto monitoring = sessionMonitoringService.getTodayMonitoring();
        return ResponseEntity.ok(monitoring);
    }
    
    /**
     * Session monitoring for specific date
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    public String monitoringByDate(@PathVariable String date, Model model) {
        LocalDate monitoringDate = LocalDate.parse(date);
        SessionMonitoringDto monitoring = sessionMonitoringService.getMonitoringForDate(monitoringDate);
        
        model.addAttribute("monitoring", monitoring);
        model.addAttribute("monitoringDate", monitoringDate);
        model.addAttribute("pageTitle", "Session Monitoring - " + date);
        
        return "monitoring/date-view";
    }
    
    /**
     * System alerts management
     */
    @GetMapping("/alerts")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    public String systemAlerts(Model model) {
        model.addAttribute("alerts", sessionMonitoringService.getUnresolvedAlerts());
        model.addAttribute("pageTitle", "System Alerts");
        
        return "monitoring/alerts";
    }
    
    /**
     * Resolve system alert
     */
    @PostMapping("/alerts/{alertId}/resolve")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    @ResponseBody
    public ResponseEntity<String> resolveAlert(@PathVariable UUID alertId, 
                                              @RequestParam String resolutionNotes,
                                              @RequestParam UUID resolvedBy) {
        try {
            sessionMonitoringService.resolveAlert(alertId, resolvedBy, resolutionNotes);
            return ResponseEntity.ok("Alert resolved successfully");
        } catch (Exception e) {
            log.error("Error resolving alert: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error resolving alert: " + e.getMessage());
        }
    }
    
    /**
     * Generate monitoring report
     */
    @PostMapping("/reports/generate")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    @ResponseBody
    public ResponseEntity<String> generateMonitoringReport(@RequestParam String startDate,
                                                          @RequestParam String endDate,
                                                          @RequestParam String reportType) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            String reportId = sessionMonitoringService.generateReport(start, end, reportType);
            return ResponseEntity.ok(reportId);
        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error generating report: " + e.getMessage());
        }
    }
    
    /**
     * Teacher attendance overview
     */
    @GetMapping("/teachers")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    public String teacherAttendanceOverview(Model model) {
        model.addAttribute("teacherStats", sessionMonitoringService.getTeacherAttendanceStats());
        model.addAttribute("pageTitle", "Teacher Attendance Overview");
        
        return "monitoring/teacher-attendance";
    }
    
    /**
     * Session performance metrics
     */
    @GetMapping("/performance")
    @PreAuthorize("hasAuthority('ACADEMIC_MONITORING')")
    public String sessionPerformanceMetrics(Model model) {
        model.addAttribute("performanceStats", sessionMonitoringService.getPerformanceMetrics());
        model.addAttribute("pageTitle", "Session Performance Metrics");
        
        return "monitoring/performance";
    }
    
    // ========== INSTRUCTOR ENDPOINTS ==========
    
    /**
     * Instructor Dashboard - redirects to session management
     */
    @GetMapping("/instructor/dashboard")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String instructorDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading instructor dashboard for: {}", userDetails.getUsername());
        
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("fullName", user.getFullName());
        
        // Get today's sessions
        var todaySessions = sessionExecutionService.getTodaySessionsForInstructor(user.getId());
        var tomorrowSessions = sessionExecutionService.getTomorrowSessionsForInstructor(user.getId());
        
        model.addAttribute("todaySessions", todaySessions);
        model.addAttribute("tomorrowSessions", tomorrowSessions);
        
        return "instructor/session-management";
    }
    
    /**
     * Session Management page for instructors
     */
    @GetMapping("/instructor/session-management")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String sessionManagement(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        return instructorDashboard(userDetails, model);
    }
    
    /**
     * Weekly Progress Recording
     */
    @GetMapping("/instructor/weekly-progress")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String weeklyProgress(@RequestParam(defaultValue = "5") Integer week,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        log.info("Loading weekly progress for week: {} by: {}", week, userDetails.getUsername());
        
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("selectedWeek", week);
        
        return "instructor/weekly-progress";
    }
    
    /**
     * Session Check-in
     */
    @PostMapping("/instructor/session/{sessionId}/check-in")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    @ResponseBody
    public ResponseEntity<String> checkIn(@PathVariable UUID sessionId,
                         @RequestParam String location,
                         @RequestParam String arrivalTime,
                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            sessionExecutionService.checkInTeacher(sessionId, user.getId(), location);
            
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Check-in berhasil\"}");
        } catch (Exception e) {
            log.error("Check-in failed", e);
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Start Session
     */
    @PostMapping("/instructor/session/{sessionId}/start")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    @ResponseBody
    public ResponseEntity<String> startSession(@PathVariable UUID sessionId,
                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            sessionExecutionService.startSession(sessionId, user.getId());
            
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Session started successfully\"}");
        } catch (Exception e) {
            log.error("Start session failed", e);
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Submit Session
     */
    @PostMapping("/instructor/session/{sessionId}/submit")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    @ResponseBody
    public ResponseEntity<String> submitSession(@PathVariable UUID sessionId,
                              @RequestParam String notes,
                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            sessionExecutionService.endSession(sessionId, user.getId(), null);
            
            return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Session submitted successfully\"}");
        } catch (Exception e) {
            log.error("Submit session failed", e);
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Request Session Reschedule
     */
    @PostMapping("/instructor/session/{sessionId}/reschedule")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    @ResponseBody
    public ResponseEntity<String> rescheduleSession(@PathVariable UUID sessionId,
                                  @RequestParam String reason,
                                  @RequestParam String newDate,
                                  @RequestParam String newTime,
                                  @RequestParam(required = false) String notes,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            var request = sessionExecutionService.requestReschedule(sessionId, user.getId(), 
                reason, LocalDate.parse(newDate), newTime, notes);
            
            return ResponseEntity.ok("{\"status\":\"success\",\"requestId\":\"" + request.getId() + "\",\"approved\":" + 
                (request.getStatus().name().equals("APPROVED") ? "true" : "false") + "}");
        } catch (Exception e) {
            log.error("Reschedule request failed", e);
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}