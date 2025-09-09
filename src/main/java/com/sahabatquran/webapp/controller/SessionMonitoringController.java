package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.SessionMonitoringDto;
import com.sahabatquran.webapp.service.SessionMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Slf4j
public class SessionMonitoringController {
    
    private final SessionMonitoringService sessionMonitoringService;
    
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
}