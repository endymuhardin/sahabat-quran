package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.SessionMonitoringDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionMonitoringService {
    
    private final ClassSessionRepository classSessionRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;
    private final AttendanceRepository attendanceRepository;
    private final SystemAlertRepository systemAlertRepository;
    private final UserRepository userRepository;
    private final FeedbackCampaignRepository feedbackCampaignRepository;
    private final SubstituteAssignmentRepository substituteAssignmentRepository;
    private final SubstituteTeacherRepository substituteTeacherRepository;
    
    /**
     * Get today's monitoring data
     */
    @Transactional(readOnly = true)
    public SessionMonitoringDto getTodayMonitoring() {
        return getMonitoringForDate(LocalDate.now());
    }
    
    /**
     * Get monitoring data for specific date
     */
    @Transactional(readOnly = true)
    public SessionMonitoringDto getMonitoringForDate(LocalDate date) {
        log.info("Getting monitoring data for date: {}", date);
        
        SessionMonitoringDto monitoring = new SessionMonitoringDto();
        monitoring.setMonitoringDate(date);
        monitoring.setLastUpdated(LocalDateTime.now());
        
        // Get session counts
        List<ClassSession> todaySessions = classSessionRepository.findBySessionDate(date);
        monitoring.setTotalSessionsScheduled(todaySessions.size());
        
        monitoring.setSessionsInProgress((int) todaySessions.stream()
            .filter(session -> session.getSessionStatus() == ClassSession.SessionStatus.IN_PROGRESS)
            .count());
        
        monitoring.setSessionsCompleted((int) todaySessions.stream()
            .filter(session -> session.getSessionStatus() == ClassSession.SessionStatus.COMPLETED)
            .count());
        
        monitoring.setSessionsCancelled((int) todaySessions.stream()
            .filter(session -> session.getSessionStatus() == ClassSession.SessionStatus.CANCELLED)
            .count());
        
        // Get teacher attendance
        monitoring.setTeacherCheckIns(teacherAttendanceRepository.countPresentTeachersForDate(date));
        monitoring.setTeacherAbsences(teacherAttendanceRepository.countAbsentTeachersForDate(date));
        
        // Calculate average student attendance
        Double avgAttendance = attendanceRepository.calculateAttendanceRateForDate(date);
        monitoring.setAverageStudentAttendance(avgAttendance != null ? 
            BigDecimal.valueOf(avgAttendance).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        // Get live session details
        List<SessionMonitoringDto.LiveSessionDto> liveSessions = todaySessions.stream()
            .map(this::mapToLiveSessionDto)
            .collect(Collectors.toList());
        monitoring.setLiveSessions(liveSessions);
        
        // Get alerts
        List<SystemAlert> todayAlerts = systemAlertRepository.findRecentUnresolvedAlerts(
            date.atStartOfDay());
        List<SessionMonitoringDto.SystemAlertDto> alertDtos = todayAlerts.stream()
            .map(this::mapToAlertDto)
            .collect(Collectors.toList());
        monitoring.setAlerts(alertDtos);
        
        return monitoring;
    }
    
    /**
     * Get unresolved system alerts
     */
    @Transactional(readOnly = true)
    public List<SystemAlert> getUnresolvedAlerts() {
        return systemAlertRepository.findByIsResolvedOrderByCreatedAtDesc(false);
    }
    
    /**
     * Resolve system alert
     */
    public void resolveAlert(UUID alertId, UUID resolvedBy, String resolutionNotes) {
        log.info("Resolving alert: {} by user: {}", alertId, resolvedBy);
        
        SystemAlert alert = systemAlertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found"));
        
        User resolver = userRepository.findById(resolvedBy)
            .orElseThrow(() -> new IllegalArgumentException("Resolver user not found"));
        
        alert.resolve(resolver, resolutionNotes);
        systemAlertRepository.save(alert);
        
        log.info("Alert resolved successfully: {}", alertId);
    }
    
    /**
     * Generate monitoring report
     */
    public String generateReport(LocalDate startDate, LocalDate endDate, String reportType) {
        log.info("Generating monitoring report from {} to {} type: {}", startDate, endDate, reportType);
        
        // In a real implementation, this would generate PDF/Excel reports
        // For now, just return a report ID
        String reportId = UUID.randomUUID().toString();
        
        // Generate report content based on type
        switch (reportType.toUpperCase()) {
            case "DAILY_SUMMARY":
                generateDailySummaryReport(startDate, endDate);
                break;
            case "TEACHER_PERFORMANCE":
                generateTeacherPerformanceReport(startDate, endDate);
                break;
            case "ATTENDANCE_ANALYSIS":
                generateAttendanceAnalysisReport(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
        
        return reportId;
    }
    
    /**
     * Get teacher attendance statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTeacherAttendanceStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        
        List<TeacherAttendance> weeklyAttendance = teacherAttendanceRepository
            .findByScheduledInstructorAndClassSessionSessionDateBetween(null, weekStart, today);
        
        // Calculate statistics (simplified version)
        return Map.of(
            "totalSessions", weeklyAttendance.size(),
            "presentCount", weeklyAttendance.stream().mapToInt(ta -> ta.getIsPresent() ? 1 : 0).sum(),
            "absentCount", weeklyAttendance.stream().mapToInt(ta -> !ta.getIsPresent() ? 1 : 0).sum(),
            "lateCheckIns", weeklyAttendance.stream().mapToInt(ta -> 
                ta.getCheckInTime() != null && isLateCheckIn(ta) ? 1 : 0).sum()
        );
    }
    
    /**
     * Get performance metrics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPerformanceMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        
        List<ClassSession> weeklySessions = classSessionRepository
            .findBySessionDateBetween(weekStart, today);
        
        return Map.of(
            "totalSessions", weeklySessions.size(),
            "completedSessions", weeklySessions.stream()
                .mapToInt(s -> s.getSessionStatus() == ClassSession.SessionStatus.COMPLETED ? 1 : 0).sum(),
            "cancelledSessions", weeklySessions.stream()
                .mapToInt(s -> s.getSessionStatus() == ClassSession.SessionStatus.CANCELLED ? 1 : 0).sum(),
            "averageAttendanceRate", calculateWeeklyAverageAttendance(weeklySessions)
        );
    }
    
    private SessionMonitoringDto.LiveSessionDto mapToLiveSessionDto(ClassSession session) {
        SessionMonitoringDto.LiveSessionDto dto = new SessionMonitoringDto.LiveSessionDto();
        dto.setSessionId(session.getId());
        dto.setClassName(session.getClassGroup().getName());
        dto.setTeacherName(session.getInstructor().getFullName());
        dto.setSessionStatus(session.getSessionStatus().toString());
        dto.setScheduledStartTime(session.getSessionDate().atTime(LocalTime.of(8, 0))); // Default time
        dto.setActualStartTime(session.getActualStartTime());
        
        // Check teacher check-in
        teacherAttendanceRepository.findByClassSessionAndScheduledInstructor(session, session.getInstructor())
            .ifPresent(attendance -> {
                dto.setTeacherCheckedIn(attendance.getCheckInTime() != null);
                dto.setCheckInTime(attendance.getCheckInTime());
            });
        
        // Calculate attendance (simplified)
        if (session.getAttendanceSummary() != null) {
            Object totalObj = session.getAttendanceSummary().get("totalStudents");
            Object presentObj = session.getAttendanceSummary().get("presentStudents");
            
            if (totalObj instanceof Integer && presentObj instanceof Integer) {
                dto.setTotalStudents((Integer) totalObj);
                dto.setStudentsPresent((Integer) presentObj);
                
                if ((Integer) totalObj > 0) {
                    double rate = (double) (Integer) presentObj / (Integer) totalObj * 100;
                    dto.setAttendanceRate(BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP));
                }
            }
        }
        
        return dto;
    }
    
    private SessionMonitoringDto.SystemAlertDto mapToAlertDto(SystemAlert alert) {
        SessionMonitoringDto.SystemAlertDto dto = new SessionMonitoringDto.SystemAlertDto();
        dto.setAlertId(alert.getId());
        dto.setAlertType(alert.getAlertType().toString());
        dto.setSeverity(alert.getSeverity().toString());
        dto.setMessage(alert.getAlertMessage());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setIsResolved(alert.getIsResolved());
        
        if (alert.getTeacher() != null) {
            dto.setTeacherName(alert.getTeacher().getFullName());
        }
        
        if (alert.getClassSession() != null) {
            dto.setClassName(alert.getClassSession().getClassGroup().getName());
        }
        
        return dto;
    }
    
    private void generateDailySummaryReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating daily summary report");
        // Implementation for daily summary report generation
    }
    
    private void generateTeacherPerformanceReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating teacher performance report");
        // Implementation for teacher performance report generation
    }
    
    private void generateAttendanceAnalysisReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating attendance analysis report");
        // Implementation for attendance analysis report generation
    }
    
    private boolean isLateCheckIn(TeacherAttendance attendance) {
        if (attendance.getCheckInTime() == null) return false;
        
        // Assume sessions start at 8:00 AM, late if checked in after 8:15 AM
        LocalTime expectedStart = LocalTime.of(8, 0);
        LocalTime lateThreshold = expectedStart.plusMinutes(15);
        LocalTime actualCheckIn = attendance.getCheckInTime().toLocalTime();
        
        return actualCheckIn.isAfter(lateThreshold);
    }
    
    private BigDecimal calculateWeeklyAverageAttendance(List<ClassSession> sessions) {
        if (sessions.isEmpty()) return BigDecimal.ZERO;
        
        double totalRate = sessions.stream()
            .filter(session -> session.getAttendanceSummary() != null)
            .mapToDouble(session -> {
                Object rateObj = session.getAttendanceSummary().get("attendanceRate");
                return rateObj instanceof Double ? (Double) rateObj : 0.0;
            })
            .average()
            .orElse(0.0);
        
        return BigDecimal.valueOf(totalRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get feedback analytics data
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getFeedbackAnalytics() {
        log.info("Getting feedback analytics data");
        
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();
        
        List<FeedbackCampaign> activeCampaigns = feedbackCampaignRepository.findActiveCampaigns();
        
        return Map.of(
            "totalActiveCampaigns", activeCampaigns.size(),
            "totalResponses", activeCampaigns.stream().mapToInt(FeedbackCampaign::getCurrentResponses).sum(),
            "averageRating", calculateAverageRating(activeCampaigns),
            "responseRate", calculateResponseRate(activeCampaigns),
            "campaignDetails", mapCampaignDetails(activeCampaigns)
        );
    }
    
    /**
     * Get emergency alerts and substitute requests
     */
    @Transactional(readOnly = true)
    public List<SystemAlert> getEmergencyAlerts() {
        List<SystemAlert> alerts = systemAlertRepository.findByIsResolvedAndSeverityOrderByCreatedAtDesc(
            false, SystemAlert.Severity.HIGH);
        
        // Force initialization of lazy-loaded relationships for template use
        alerts.forEach(alert -> {
            if (alert.getClassSession() != null) {
                alert.getClassSession().getId(); // Trigger lazy loading
                ClassGroup classGroup = alert.getClassSession().getClassGroup();
                if (classGroup != null) {
                    classGroup.getName(); // Trigger lazy loading of class group name
                    if (classGroup.getLevel() != null) {
                        classGroup.getLevel().getName(); // Trigger lazy loading of level name
                    }
                }
            }
            if (alert.getTeacher() != null) {
                alert.getTeacher().getFullName(); // Trigger lazy loading
                alert.getTeacher().getEmail();
                alert.getTeacher().getPhoneNumber();
            }
        });
        
        return alerts;
    }
    
    /**
     * Get active substitute requests
     */
    @Transactional(readOnly = true)
    public List<SubstituteAssignment> getSubstituteRequests() {
        return substituteAssignmentRepository.findActiveAssignments();
    }
    
    /**
     * Get available substitute teachers for emergency assignments
     */
    @Transactional(readOnly = true)
    public List<SubstituteTeacher> getAvailableSubstituteTeachers() {
        List<SubstituteTeacher> substitutes = substituteTeacherRepository.findAvailableSubstitutes();
        log.info("Found {} substitute teachers in database", substitutes.size());
        
        // Sort by teacher name
        substitutes.sort((s1, s2) -> {
            String name1 = s1.getTeacher() != null ? s1.getTeacher().getFullName() : "";
            String name2 = s2.getTeacher() != null ? s2.getTeacher().getFullName() : "";
            return name1.compareToIgnoreCase(name2);
        });
        
        // Force initialization of lazy-loaded relationships for template use
        substitutes.forEach(substitute -> {
            if (substitute.getTeacher() != null) {
                substitute.getTeacher().getFullName(); // Trigger lazy loading
                substitute.getTeacher().getEmail();
                substitute.getTeacher().getPhoneNumber();
                log.debug("Loaded substitute teacher: {}", substitute.getTeacher().getFullName());
            }
        });
        
        // For testing purposes, if no substitutes found, create mock data
        if (substitutes.isEmpty()) {
            log.warn("No substitute teachers found in database, this may be a test data setup issue");
        }
        
        return substitutes;
    }
    
    private BigDecimal calculateAverageRating(List<FeedbackCampaign> campaigns) {
        // Simplified calculation - in real implementation would aggregate from feedback_answers
        return BigDecimal.valueOf(4.2); // Mock value
    }
    
    private BigDecimal calculateResponseRate(List<FeedbackCampaign> campaigns) {
        if (campaigns.isEmpty()) return BigDecimal.ZERO;
        
        double totalRate = campaigns.stream()
            .filter(c -> c.getResponseRate() != null)
            .mapToDouble(c -> c.getResponseRate().doubleValue())
            .average()
            .orElse(0.0);
            
        return BigDecimal.valueOf(totalRate).setScale(1, RoundingMode.HALF_UP);
    }
    
    private List<Map<String, Object>> mapCampaignDetails(List<FeedbackCampaign> campaigns) {
        return campaigns.stream()
            .map(campaign -> {
                Map<String, Object> details = new HashMap<>();
                details.put("id", campaign.getId());
                details.put("name", campaign.getCampaignName());
                details.put("type", campaign.getCampaignType().toString());
                details.put("responses", campaign.getCurrentResponses());
                details.put("responseRate", campaign.getResponseRate() != null ? campaign.getResponseRate() : BigDecimal.ZERO);
                return details;
            })
            .collect(Collectors.toList());
    }
}