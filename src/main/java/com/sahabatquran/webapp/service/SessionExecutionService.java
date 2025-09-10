package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.SessionCheckInDto;
import com.sahabatquran.webapp.dto.SessionExecutionDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionExecutionService {
    
    private final ClassSessionRepository classSessionRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final SystemAlertRepository systemAlertRepository;
    private final UserRepository userRepository;
    private final SessionRescheduleRequestRepository rescheduleRequestRepository;
    
    /**
     * Teacher check-in for a session
     */
    public SessionCheckInDto teacherCheckIn(SessionCheckInDto checkInDto) {
        log.info("Processing teacher check-in for session: {}", checkInDto.getSessionId());
        
        ClassSession session = classSessionRepository.findById(checkInDto.getSessionId())
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        // Create or update teacher attendance record
        TeacherAttendance attendance = teacherAttendanceRepository
            .findByClassSessionAndScheduledInstructor(session, session.getInstructor())
            .orElse(new TeacherAttendance());
        
        attendance.setClassSession(session);
        attendance.setScheduledInstructor(session.getInstructor());
        attendance.setActualInstructor(session.getInstructor());
        attendance.checkIn(checkInDto.getCheckInLocation());
        attendance.setNotes(checkInDto.getNotes());
        
        teacherAttendanceRepository.save(attendance);
        
        // Update session status
        session.setSessionStatus(ClassSession.SessionStatus.IN_PROGRESS);
        classSessionRepository.save(session);
        
        // Populate response
        checkInDto.setSessionName("Session " + session.getSessionNumber());
        checkInDto.setClassName(session.getClassGroup().getName());
        checkInDto.setExpectedStudents(getExpectedStudentCount(session));
        checkInDto.setStatus("CHECKED_IN");
        
        log.info("Teacher check-in completed for session: {}", checkInDto.getSessionId());
        return checkInDto;
    }
    
    /**
     * Start session execution
     */
    public SessionExecutionDto startSession(UUID sessionId) {
        log.info("Starting session execution: {}", sessionId);
        
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        session.startSession();
        classSessionRepository.save(session);
        
        // Initialize student attendance records
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(session.getClassGroup());
        List<SessionExecutionDto.StudentAttendanceDto> studentAttendances = enrollments.stream()
            .map(enrollment -> new SessionExecutionDto.StudentAttendanceDto(
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFullName(),
                false, // Default not present
                null
            ))
            .collect(Collectors.toList());
        
        return buildSessionExecutionDto(session, studentAttendances);
    }
    
    /**
     * Mark student attendance during session
     */
    public SessionExecutionDto markStudentAttendance(UUID sessionId, List<SessionExecutionDto.StudentAttendanceDto> attendances) {
        log.info("Marking student attendance for session: {}", sessionId);
        
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        for (SessionExecutionDto.StudentAttendanceDto attendanceDto : attendances) {
            // Find or create attendance record
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndClassGroup(
                attendanceDto.getStudentId(), session.getClassGroup())
                .orElseThrow(() -> new IllegalArgumentException("Student enrollment not found"));
            
            com.sahabatquran.webapp.entity.Attendance attendance = attendanceRepository
                .findByEnrollmentAndAttendanceDate(enrollment, session.getSessionDate())
                .orElse(new com.sahabatquran.webapp.entity.Attendance());
            
            attendance.setEnrollment(enrollment);
            attendance.setAttendanceDate(session.getSessionDate());
            attendance.setIsPresent(attendanceDto.getIsPresent());
            attendance.setNotes(attendanceDto.getNotes());
            
            attendanceRepository.save(attendance);
        }
        
        // Update session attendance summary
        updateSessionAttendanceSummary(session, attendances);
        
        return buildSessionExecutionDto(session, attendances);
    }
    
    /**
     * Record session notes and objectives
     */
    public SessionExecutionDto recordSessionNotes(UUID sessionId, String notes, Map<String, Boolean> objectivesAchieved) {
        log.info("Recording session notes for session: {}", sessionId);
        
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        session.setSessionNotes(notes);
        session.setObjectivesAchieved(objectivesAchieved);
        classSessionRepository.save(session);
        
        return buildSessionExecutionDto(session, getCurrentAttendances(session));
    }
    
    /**
     * Complete session and teacher check-out
     */
    public SessionExecutionDto completeSession(UUID sessionId) {
        log.info("Completing session: {}", sessionId);
        
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        session.completeSession();
        classSessionRepository.save(session);
        
        // Teacher check-out
        teacherAttendanceRepository.findByClassSessionAndScheduledInstructor(session, session.getInstructor())
            .ifPresent(attendance -> {
                attendance.checkOut();
                teacherAttendanceRepository.save(attendance);
            });
        
        log.info("Session completed successfully: {}", sessionId);
        return buildSessionExecutionDto(session, getCurrentAttendances(session));
    }
    
    /**
     * Get session execution details
     */
    @Transactional(readOnly = true)
    public SessionExecutionDto getSessionExecution(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        return buildSessionExecutionDto(session, getCurrentAttendances(session));
    }
    
    private SessionExecutionDto buildSessionExecutionDto(ClassSession session, 
                                                         List<SessionExecutionDto.StudentAttendanceDto> attendances) {
        SessionExecutionDto dto = new SessionExecutionDto();
        dto.setSessionId(session.getId());
        dto.setSessionStatus(session.getSessionStatus().toString());
        dto.setActualStartTime(session.getActualStartTime());
        dto.setActualEndTime(session.getActualEndTime());
        dto.setSessionNotes(session.getSessionNotes());
        dto.setLearningObjectives(session.getLearningObjectives());
        dto.setObjectivesAchieved(session.getObjectivesAchieved());
        dto.setStudentAttendances(attendances);
        dto.setAttendanceSummary(session.getAttendanceSummary());
        
        return dto;
    }
    
    private List<SessionExecutionDto.StudentAttendanceDto> getCurrentAttendances(ClassSession session) {
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(session.getClassGroup());
        
        return enrollments.stream()
            .map(enrollment -> {
                Optional<com.sahabatquran.webapp.entity.Attendance> attendance = 
                    attendanceRepository.findByEnrollmentAndAttendanceDate(enrollment, session.getSessionDate());
                
                return new SessionExecutionDto.StudentAttendanceDto(
                    enrollment.getStudent().getId(),
                    enrollment.getStudent().getFullName(),
                    attendance.map(com.sahabatquran.webapp.entity.Attendance::isPresent).orElse(false),
                    attendance.map(com.sahabatquran.webapp.entity.Attendance::getNotes).orElse(null)
                );
            })
            .collect(Collectors.toList());
    }
    
    private void updateSessionAttendanceSummary(ClassSession session, List<SessionExecutionDto.StudentAttendanceDto> attendances) {
        int totalStudents = attendances.size();
        int presentStudents = (int) attendances.stream().filter(SessionExecutionDto.StudentAttendanceDto::getIsPresent).count();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalStudents", totalStudents);
        summary.put("presentStudents", presentStudents);
        summary.put("absentStudents", totalStudents - presentStudents);
        summary.put("attendanceRate", totalStudents > 0 ? (double) presentStudents / totalStudents * 100 : 0);
        summary.put("lastUpdated", LocalDateTime.now());
        
        session.setAttendanceSummary(summary);
        classSessionRepository.save(session);
        
        // Generate alerts if attendance is low
        if (totalStudents > 0 && (double) presentStudents / totalStudents < 0.7) {
            createLowAttendanceAlert(session, presentStudents, totalStudents);
        }
    }
    
    private void createLowAttendanceAlert(ClassSession session, int presentStudents, int totalStudents) {
        SystemAlert alert = new SystemAlert();
        alert.setAlertType(SystemAlert.AlertType.LOW_ATTENDANCE);
        alert.setSeverity(SystemAlert.Severity.MEDIUM);
        alert.setClassSession(session);
        alert.setTeacher(session.getInstructor());
        alert.setAlertMessage(String.format("Low attendance in %s: %d/%d students present (%.1f%%)", 
            session.getClassGroup().getName(), presentStudents, totalStudents, 
            (double) presentStudents / totalStudents * 100));
        
        systemAlertRepository.save(alert);
    }
    
    private Integer getExpectedStudentCount(ClassSession session) {
        return enrollmentRepository.countByClassGroup(session.getClassGroup());
    }
    
    // ========== ADDITIONAL METHODS FOR CONTROLLER ==========
    
    /**
     * Get today's sessions for an instructor
     */
    @Transactional(readOnly = true)
    public List<ClassSession> getTodaySessionsForInstructor(UUID instructorId) {
        LocalDate today = LocalDate.now();
        return classSessionRepository.findByInstructorIdAndSessionDate(instructorId, today);
    }
    
    /**
     * Get tomorrow's sessions for an instructor
     */
    @Transactional(readOnly = true)
    public List<ClassSession> getTomorrowSessionsForInstructor(UUID instructorId) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return classSessionRepository.findByInstructorIdAndSessionDate(instructorId, tomorrow);
    }
    
    /**
     * Check in teacher for a session
     */
    public TeacherAttendance checkInTeacher(UUID sessionId, UUID instructorId, String location) {
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // Verify instructor is assigned to this session
        if (!session.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Instructor not assigned to this session");
        }
        
        // Find or create teacher attendance record
        TeacherAttendance attendance = teacherAttendanceRepository
            .findByClassSessionAndScheduledInstructor(session, session.getInstructor())
            .orElseGet(() -> {
                TeacherAttendance newAttendance = new TeacherAttendance();
                newAttendance.setClassSession(session);
                newAttendance.setScheduledInstructor(session.getInstructor());
                newAttendance.setActualInstructor(session.getInstructor());
                return newAttendance;
            });
        
        // Perform check-in
        attendance.checkIn(location);
        teacherAttendanceRepository.save(attendance);
        
        // Update session status
        session.setSessionStatus(ClassSession.SessionStatus.IN_PROGRESS);
        session.setActualStartTime(LocalDateTime.now());
        classSessionRepository.save(session);
        
        log.info("Teacher {} checked in for session {} at {}", instructorId, sessionId, location);
        
        return attendance;
    }
    
    /**
     * Start a class session
     */
    public ClassSession startSession(UUID sessionId, UUID instructorId) {
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // Verify instructor
        if (!session.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Instructor not assigned to this session");
        }
        
        // Start session
        session.startSession();
        
        // Initialize objectives tracking if needed
        if (session.getObjectivesAchieved() == null) {
            Map<String, Boolean> objectives = new HashMap<>();
            for (String objective : session.getLearningObjectives()) {
                objectives.put(objective, false);
            }
            session.setObjectivesAchieved(objectives);
        }
        
        return classSessionRepository.save(session);
    }
    
    /**
     * End and complete a session
     */
    public ClassSession endSession(UUID sessionId, UUID instructorId, String departureTime) {
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // Verify instructor
        if (!session.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Instructor not assigned to this session");
        }
        
        // Complete session
        session.completeSession();
        classSessionRepository.save(session);
        
        // Check out teacher
        teacherAttendanceRepository.findByClassSessionAndScheduledInstructor(session, session.getInstructor())
            .ifPresent(attendance -> {
                attendance.checkOut();
                teacherAttendanceRepository.save(attendance);
            });
        
        log.info("Session {} completed by instructor {}", sessionId, instructorId);
        
        return session;
    }
    
    /**
     * Request session reschedule
     */
    public SessionRescheduleRequest requestReschedule(UUID sessionId, UUID instructorId, 
                                                      String reason, LocalDate newDate, 
                                                      String newTime, String notes) {
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // Verify instructor
        if (!session.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Instructor not assigned to this session");
        }
        
        User instructor = userRepository.findById(instructorId)
            .orElseThrow(() -> new RuntimeException("Instructor not found"));
        
        // Create reschedule request
        SessionRescheduleRequest request = new SessionRescheduleRequest();
        request.setClassSession(session);
        request.setRequestedBy(instructor);
        request.setOriginalDate(session.getSessionDate());
        request.setOriginalTime("09:00-10:30"); // Would come from session time
        request.setProposedDate(newDate);
        request.setProposedTime(newTime);
        request.setReasonDetails(notes);
        request.setStatus(SessionRescheduleRequest.RescheduleStatus.PENDING);
        
        // Set reason enum based on string
        switch (reason) {
            case "Teacher Illness":
                request.setReason(SessionRescheduleRequest.RescheduleReason.TEACHER_ILLNESS);
                request.setStatus(SessionRescheduleRequest.RescheduleStatus.APPROVED);
                request.setApprovalDate(LocalDateTime.now());
                request.setAutoApproved(true);
                
                // Update session status
                session.rescheduleSession();
                classSessionRepository.save(session);
                
                // Create new session for the new date
                ClassSession newSession = new ClassSession();
                newSession.setClassGroup(session.getClassGroup());
                newSession.setInstructor(session.getInstructor());
                newSession.setSessionDate(newDate);
                newSession.setSessionNumber(session.getSessionNumber());
                newSession.setLearningObjectives(new ArrayList<>(session.getLearningObjectives()));
                newSession.setTeachingMaterials(session.getTeachingMaterials());
                newSession.setPreparationStatus(session.getPreparationStatus());
                newSession.setSessionStatus(ClassSession.SessionStatus.SCHEDULED);
                ClassSession savedNewSession = classSessionRepository.save(newSession);
                
                request.setNewSession(savedNewSession);
                break;
            case "Emergency":
                request.setReason(SessionRescheduleRequest.RescheduleReason.EMERGENCY);
                break;
            case "Academic Event":
                request.setReason(SessionRescheduleRequest.RescheduleReason.OTHER);
                break;
            default:
                request.setReason(SessionRescheduleRequest.RescheduleReason.OTHER);
                break;
        }
        
        SessionRescheduleRequest savedRequest = rescheduleRequestRepository.save(request);
        
        log.info("Reschedule request created for session {} by instructor {}: {}", 
                sessionId, instructorId, savedRequest.getStatus());
        
        return savedRequest;
    }
}