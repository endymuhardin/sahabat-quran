package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.AttendanceDiscrepancyDto;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.GuestStudent;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.ClassSessionRepository;
import com.sahabatquran.webapp.repository.EnrollmentRepository;
import com.sahabatquran.webapp.repository.GuestStudentRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AttendanceDiscrepancyService {

    private final EnrollmentRepository enrollmentRepository;
    private final GuestStudentRepository guestStudentRepository;
    private final ClassSessionRepository classSessionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AttendanceDiscrepancyDto checkAttendanceDiscrepancy(UUID sessionId, int actualPresentCount) {
        log.info("Checking attendance discrepancy for session {} with {} present students", sessionId, actualPresentCount);

        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        Long registeredStudentCountLong = enrollmentRepository.countActiveEnrollmentsByClassGroup(session.getClassGroup().getId());
        int registeredStudentCount = registeredStudentCountLong != null ? registeredStudentCountLong.intValue() : 0;

        log.info("Session {}: {} registered students, {} actually present",
                sessionId, registeredStudentCount, actualPresentCount);

        if (actualPresentCount == registeredStudentCount) {
            return AttendanceDiscrepancyDto.noDiscrepancy(sessionId, registeredStudentCount);
        } else if (actualPresentCount > registeredStudentCount) {
            return AttendanceDiscrepancyDto.extraStudents(sessionId, registeredStudentCount, actualPresentCount);
        } else {
            return AttendanceDiscrepancyDto.fewerStudents(sessionId, registeredStudentCount, actualPresentCount);
        }
    }

    public GuestStudent addGuestStudent(UUID sessionId, String guestName, String reason,
                                       GuestStudent.GuestType guestType, String addedByUsername) {
        log.info("Adding guest student {} to session {} with reason: {}", guestName, sessionId, reason);

        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        User addedBy = userRepository.findByUsername(addedByUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + addedByUsername));

        GuestStudent guestStudent = new GuestStudent();
        guestStudent.setSession(session);
        guestStudent.setGuestName(guestName);
        guestStudent.setReason(reason);
        guestStudent.setGuestType(guestType);
        guestStudent.setAddedBy(addedBy);
        guestStudent.setIsPresent(true);

        GuestStudent saved = guestStudentRepository.save(guestStudent);
        log.info("Guest student {} added successfully to session {}", guestName, sessionId);

        // Notify academic admin about guest student addition
        notifyAcademicAdminAboutGuestStudent(saved);

        return saved;
    }

    private void notifyAcademicAdminAboutGuestStudent(GuestStudent guestStudent) {
        try {
            log.info("Notifying academic admin about guest student: {} in session {}",
                    guestStudent.getGuestName(), guestStudent.getSession().getId());

            // Here you would implement actual notification logic
            // For now, just log the notification

        } catch (Exception e) {
            log.error("Failed to notify academic admin about guest student: {}",
                     guestStudent.getGuestName(), e);
        }
    }

    @Transactional(readOnly = true)
    public long getGuestStudentCountForSession(UUID sessionId) {
        return guestStudentRepository.countPresentGuestsBySession(sessionId);
    }

    @Transactional(readOnly = true)
    public int getAdjustedAttendanceCount(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        Long registeredStudentsLong = enrollmentRepository.countActiveEnrollmentsByClassGroup(session.getClassGroup().getId());
        int registeredStudents = registeredStudentsLong != null ? registeredStudentsLong.intValue() : 0;
        long guestStudents = guestStudentRepository.countPresentGuestsBySession(sessionId);

        return registeredStudents + (int) guestStudents;
    }
}