package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.SubstituteAssignmentDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubstituteTeacherService {
    
    private final SubstituteTeacherRepository substituteTeacherRepository;
    private final SubstituteAssignmentRepository substituteAssignmentRepository;
    private final ClassSessionRepository classSessionRepository;
    private final UserRepository userRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;
    private final ParentNotificationRepository parentNotificationRepository;
    private final SystemAlertRepository systemAlertRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    
    /**
     * Find available substitute teachers for a session
     */
    @Transactional(readOnly = true)
    public List<SubstituteAssignmentDto> findAvailableSubstitutes(UUID sessionId, boolean emergencyOnly) {
        log.info("Finding available substitutes for session: {}, emergency only: {}", sessionId, emergencyOnly);
        
        ClassSession session = classSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        List<SubstituteTeacher> availableSubstitutes;
        
        if (emergencyOnly) {
            availableSubstitutes = substituteTeacherRepository.findByIsAvailableTrueAndEmergencyAvailableTrue();
        } else {
            availableSubstitutes = substituteTeacherRepository.findByIsAvailableTrue();
        }
        
        // Sort by rating
        availableSubstitutes = availableSubstitutes.stream()
            .sorted(Comparator.comparing(SubstituteTeacher::getRating, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
        
        return availableSubstitutes.stream()
            .map(substitute -> mapToSubstituteDto(substitute, session))
            .collect(Collectors.toList());
    }
    
    /**
     * Assign substitute teacher to a session
     */
    public SubstituteAssignmentDto assignSubstitute(SubstituteAssignmentDto assignmentDto, UUID assignedBy) {
        log.info("Assigning substitute teacher for session: {}", assignmentDto.getSessionId());
        
        ClassSession session = classSessionRepository.findById(assignmentDto.getSessionId())
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        
        User originalTeacher = userRepository.findById(assignmentDto.getOriginalTeacherId())
            .orElseThrow(() -> new IllegalArgumentException("Original teacher not found"));
        
        User substituteTeacher = userRepository.findById(assignmentDto.getSubstituteTeacherId())
            .orElseThrow(() -> new IllegalArgumentException("Substitute teacher not found"));
        
        User assigner = userRepository.findById(assignedBy)
            .orElseThrow(() -> new IllegalArgumentException("Assigner not found"));
        
        // Create substitute assignment
        SubstituteAssignment assignment = new SubstituteAssignment();
        assignment.setClassSession(session);
        assignment.setOriginalTeacher(originalTeacher);
        assignment.setSubstituteTeacher(substituteTeacher);
        assignment.setAssignmentType(SubstituteAssignment.AssignmentType.valueOf(assignmentDto.getAssignmentType()));
        assignment.setAssignedBy(assigner);
        assignment.setReason(assignmentDto.getReason());
        assignment.setCompensationAmount(assignmentDto.getCompensationAmount());
        assignment.setSpecialInstructions(assignmentDto.getSpecialInstructions());
        assignment.setMaterialsShared(assignmentDto.getMaterialsShared());
        
        if (assignmentDto.getMaterialsShared()) {
            assignment.setMaterialsSharedAt(LocalDateTime.now());
        }
        
        assignment = substituteAssignmentRepository.save(assignment);
        
        // Update teacher attendance record
        TeacherAttendance attendance = teacherAttendanceRepository
            .findByClassSessionAndScheduledInstructor(session, originalTeacher)
            .orElse(new TeacherAttendance());
        
        attendance.setClassSession(session);
        attendance.setScheduledInstructor(originalTeacher);
        attendance.setActualInstructor(substituteTeacher);
        attendance.setIsPresent(false);
        attendance.setAbsenceReason(assignmentDto.getReason());
        attendance.setSubstituteArranged(true);
        attendance.setSubstituteNotes("Substitute assigned: " + substituteTeacher.getFullName());
        
        teacherAttendanceRepository.save(attendance);
        
        // Send notification to substitute teacher
        notificationService.sendSubstituteAssignmentNotification(assignment);
        
        // Notify students and parents about teacher change
        notifyStudentsAndParents(session, originalTeacher, substituteTeacher, assignmentDto.getReason());
        
        // Update substitute teacher statistics
        updateSubstituteStatistics(substituteTeacher);
        
        log.info("Substitute teacher assigned successfully: {}", assignment.getId());
        return mapToAssignmentDto(assignment);
    }
    
    /**
     * Accept substitute assignment
     */
    public SubstituteAssignmentDto acceptAssignment(UUID assignmentId, UUID substituteTeacherId) {
        log.info("Processing substitute assignment acceptance: {}", assignmentId);
        
        SubstituteAssignment assignment = substituteAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        if (!assignment.getSubstituteTeacher().getId().equals(substituteTeacherId)) {
            throw new IllegalArgumentException("Unauthorized to accept this assignment");
        }
        
        assignment.setSubstituteAccepted(true);
        assignment.setAcceptanceTime(LocalDateTime.now());
        
        substituteAssignmentRepository.save(assignment);
        
        // Update session instructor
        ClassSession session = assignment.getClassSession();
        session.setInstructor(assignment.getSubstituteTeacher());
        classSessionRepository.save(session);
        
        // Clear any related alerts
        clearSubstituteNeededAlerts(session);
        
        // Send confirmation notifications
        notificationService.sendSubstituteConfirmationNotification(assignment);
        
        log.info("Substitute assignment accepted successfully: {}", assignmentId);
        return mapToAssignmentDto(assignment);
    }
    
    /**
     * Reject substitute assignment
     */
    public void rejectAssignment(UUID assignmentId, UUID substituteTeacherId, String reason) {
        log.info("Processing substitute assignment rejection: {}", assignmentId);
        
        SubstituteAssignment assignment = substituteAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        if (!assignment.getSubstituteTeacher().getId().equals(substituteTeacherId)) {
            throw new IllegalArgumentException("Unauthorized to reject this assignment");
        }
        
        assignment.setSubstituteAccepted(false);
        assignment.setAcceptanceTime(LocalDateTime.now());
        
        substituteAssignmentRepository.save(assignment);
        
        // Create alert for finding another substitute
        createSubstituteNeededAlert(assignment.getClassSession(), "Substitute rejected assignment: " + reason);
        
        log.info("Substitute assignment rejected: {}", assignmentId);
    }
    
    /**
     * Get substitute performance metrics
     */
    @Transactional(readOnly = true)
    public List<SubstituteAssignmentDto> getSubstitutePerformance(UUID substituteTeacherId) {
        User substituteTeacher = userRepository.findById(substituteTeacherId)
            .orElseThrow(() -> new IllegalArgumentException("Substitute teacher not found"));
        
        List<SubstituteAssignment> assignments = substituteAssignmentRepository
            .findBySubstituteTeacherOrderByAssignmentDateDesc(substituteTeacher);
        
        return assignments.stream()
            .map(this::mapToAssignmentDto)
            .collect(Collectors.toList());
    }
    
    private SubstituteAssignmentDto mapToSubstituteDto(SubstituteTeacher substitute, ClassSession session) {
        SubstituteAssignmentDto dto = new SubstituteAssignmentDto();
        dto.setSessionId(session.getId());
        dto.setSubstituteTeacherId(substitute.getTeacher().getId());
        dto.setSubstituteTeacherName(substitute.getTeacher().getFullName());
        dto.setSubstituteRating(substitute.getRating());
        dto.setSubstituteContactPreference(substitute.getContactPreference() != null ? 
            substitute.getContactPreference().name() : null);
        dto.setSubstitutePhone(substitute.getTeacher().getPhoneNumber());
        dto.setSubstituteEmail(substitute.getTeacher().getEmail());
        dto.setClassName(session.getClassGroup().getName());
        dto.setSessionDate(session.getSessionDate());
        dto.setSessionTime(session.getSessionDate().toString()); // You might want to format this better
        
        return dto;
    }
    
    private SubstituteAssignmentDto mapToAssignmentDto(SubstituteAssignment assignment) {
        SubstituteAssignmentDto dto = new SubstituteAssignmentDto();
        dto.setSessionId(assignment.getClassSession().getId());
        dto.setOriginalTeacherId(assignment.getOriginalTeacher().getId());
        dto.setSubstituteTeacherId(assignment.getSubstituteTeacher().getId());
        dto.setAssignmentType(assignment.getAssignmentType().name());
        dto.setReason(assignment.getReason());
        dto.setCompensationAmount(assignment.getCompensationAmount());
        dto.setSpecialInstructions(assignment.getSpecialInstructions());
        dto.setMaterialsShared(assignment.getMaterialsShared());
        dto.setSubstituteAccepted(assignment.getSubstituteAccepted());
        dto.setAcceptanceTime(assignment.getAcceptanceTime());
        
        dto.setClassName(assignment.getClassSession().getClassGroup().getName());
        dto.setSessionDate(assignment.getClassSession().getSessionDate());
        dto.setOriginalTeacherName(assignment.getOriginalTeacher().getFullName());
        dto.setSubstituteTeacherName(assignment.getSubstituteTeacher().getFullName());
        
        return dto;
    }
    
    private void notifyStudentsAndParents(ClassSession session, User originalTeacher, 
                                         User substituteTeacher, String reason) {
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(session.getClassGroup());
        
        for (Enrollment enrollment : enrollments) {
            ParentNotification notification = new ParentNotification();
            notification.setStudent(enrollment.getStudent());
            notification.setNotificationType(ParentNotification.NotificationType.TEACHER_CHANGE);
            notification.setSubject("Perubahan Pengajar - " + session.getClassGroup().getName());
            notification.setMessage(String.format(
                "Assalamualaikum. Kami informasikan bahwa untuk kelas %s pada tanggal %s, " +
                "Ustadz/Ustadzah %s akan digantikan oleh Ustadz/Ustadzah %s. " +
                "Alasan: %s. Terima kasih atas perhatiannya.",
                session.getClassGroup().getName(),
                session.getSessionDate(),
                originalTeacher.getFullName(),
                substituteTeacher.getFullName(),
                reason
            ));
            notification.setDeliveryMethod(ParentNotification.DeliveryMethod.SMS);
            notification.setRelatedSession(session);
            
            // Try to get parent phone number from student
            if (enrollment.getStudent().getPhoneNumber() != null) {
                notification.setRecipientContact(enrollment.getStudent().getPhoneNumber());
            }
            
            parentNotificationRepository.save(notification);
        }
    }
    
    private void updateSubstituteStatistics(User substituteTeacher) {
        SubstituteTeacher substitute = substituteTeacherRepository.findByTeacher(substituteTeacher)
            .orElse(new SubstituteTeacher());
        
        substitute.setTeacher(substituteTeacher);
        substitute.setTotalSubstitutions(substitute.getTotalSubstitutions() + 1);
        substitute.setLastSubstitutionDate(LocalDateTime.now().toLocalDate());
        
        substituteTeacherRepository.save(substitute);
    }
    
    private void createSubstituteNeededAlert(ClassSession session, String message) {
        SystemAlert alert = new SystemAlert();
        alert.setAlertType(SystemAlert.AlertType.SUBSTITUTE_NEEDED);
        alert.setSeverity(SystemAlert.Severity.HIGH);
        alert.setClassSession(session);
        alert.setTeacher(session.getInstructor());
        alert.setAlertMessage(message);
        
        systemAlertRepository.save(alert);
    }
    
    private void clearSubstituteNeededAlerts(ClassSession session) {
        List<SystemAlert> alerts = systemAlertRepository
            .findAlertsByTypeAndTime(SystemAlert.AlertType.SUBSTITUTE_NEEDED, LocalDateTime.now().minusHours(24));
        
        alerts.stream()
            .filter(alert -> alert.getClassSession() != null && 
                alert.getClassSession().getId().equals(session.getId()))
            .forEach(alert -> alert.resolve(null, "Substitute assigned and accepted"));
        
        systemAlertRepository.saveAll(alerts);
    }
}