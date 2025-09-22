package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.ClassGroup;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.TeacherAttendance;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.ClassSessionRepository;
import com.sahabatquran.webapp.repository.TeacherAttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SessionService {

    private final ClassSessionRepository classSessionRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;

    /**
     * Get today's sessions for an instructor
     */
    @Transactional(readOnly = true)
    public List<ClassSession> getTodaySessionsForInstructor(User instructor, LocalDate date) {
        return classSessionRepository.findByInstructorIdAndSessionDate(instructor.getId(), date);
    }

    /**
     * Get tomorrow's sessions for an instructor
     */
    @Transactional(readOnly = true)
    public List<ClassSession> getTomorrowSessionsForInstructor(User instructor, LocalDate date) {
        return classSessionRepository.findByInstructorIdAndSessionDate(instructor.getId(), date);
    }

    /**
     * Check if a session is late based on current time and schedule
     */
    public boolean isSessionLate(ClassSession session) {
        if (session == null || session.getClassGroup() == null) {
            log.warn("Session late check failed: session or classGroup is null");
            return false;
        }

        ClassGroup classGroup = session.getClassGroup();
        LocalTime currentTime = LocalTime.now();
    LocalTime sessionStartTime = parseSessionStartTime(classGroup);
        LocalTime lateThreshold = sessionStartTime.plusMinutes(15);

        // Check time-based lateness
        boolean isTimeBasedLate = currentTime.isAfter(lateThreshold);

        // Check status-based lateness (IN_PROGRESS indicates a session that should have started but hasn't)
        boolean isStatusBasedLate = session.getPreparationStatus() == ClassSession.PreparationStatus.IN_PROGRESS;

        // Session is late if EITHER condition is true
        boolean isLate = isTimeBasedLate || isStatusBasedLate;

        // Use formatSessionTime to safely get TimeSlot information with proper eager loading
        String timeSlotStr = formatSessionTime(classGroup);

        log.info("Session late check: sessionId={}, currentTime={}, sessionStartTime={}, lateThreshold={}, " +
                "isTimeBasedLate={}, isStatusBasedLate={}, finalIsLate={}, sessionStatus={}, timeSlot='{}'",
                session.getId(), currentTime, sessionStartTime, lateThreshold,
                isTimeBasedLate, isStatusBasedLate, isLate,
                session.getPreparationStatus(), timeSlotStr);

        return isLate;
    }

    /**
     * Parse session start time from ClassGroup TimeSlot
     * Assumes ClassGroup is loaded with proper eager fetching for TimeSlot relationships
     */
    public LocalTime parseSessionStartTime(ClassGroup classGroup) {
        if (classGroup == null) {
            return LocalTime.of(8, 0); // Default fallback
        }

        // With proper eager loading, these relationships should be available
        if (classGroup.getTimeSlot() != null && classGroup.getTimeSlot().getSession() != null) {
            return classGroup.getTimeSlot().getSession().getStartTime();
        }

        return LocalTime.of(8, 0); // Default fallback
    }

    /**
     * Format session time range from ClassGroup TimeSlot
     * Assumes ClassGroup is loaded with proper eager fetching for TimeSlot relationships
     */
    public String formatSessionTime(ClassGroup classGroup) {
        if (classGroup == null) {
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = startTime.plusMinutes(90); // Assume 90-minute sessions
            return String.format("%02d:%02d-%02d:%02d",
                    startTime.getHour(), startTime.getMinute(),
                    endTime.getHour(), endTime.getMinute());
        }

        // With proper eager loading, these relationships should be available
        if (classGroup.getTimeSlot() != null && classGroup.getTimeSlot().getSession() != null) {
            var session = classGroup.getTimeSlot().getSession();
            return String.format("%02d:%02d-%02d:%02d",
                    session.getStartTime().getHour(), session.getStartTime().getMinute(),
                    session.getEndTime().getHour(), session.getEndTime().getMinute());
        }

        // Fallback formatting
        LocalTime startTime = parseSessionStartTime(classGroup);
        LocalTime endTime = startTime.plusMinutes(90); // Assume 90-minute sessions
        return String.format("%02d:%02d-%02d:%02d",
                startTime.getHour(), startTime.getMinute(),
                endTime.getHour(), endTime.getMinute());
    }

    /**
     * Get teacher attendance record for a session
     */
    @Transactional(readOnly = true)
    public Optional<TeacherAttendance> getTeacherAttendance(ClassSession session, User instructor) {
        if (session == null || instructor == null) {
            return Optional.empty();
        }
        return teacherAttendanceRepository.findByClassSessionAndScheduledInstructor(session, instructor);
    }

    /**
     * Build session data map for template rendering
     */
    public Map<String, Object> buildSessionData(ClassSession session, User instructor) {
        if (session == null || session.getClassGroup() == null) {
            return new HashMap<>();
        }

        ClassGroup classGroup = session.getClassGroup();
        boolean isLate = isSessionLate(session);

        Optional<TeacherAttendance> attendanceOpt = getTeacherAttendance(session, instructor);
        boolean isCheckedIn = attendanceOpt.isPresent() && attendanceOpt.get().getArrivalTime() != null;

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("id", session.getId());
        sessionData.put("className", classGroup.getName());
        sessionData.put("time", formatSessionTime(classGroup));
        sessionData.put("room", classGroup.getLocation());
        sessionData.put("isLate", isLate);
        sessionData.put("isCheckedIn", isCheckedIn);
        sessionData.put("preparationStatus", session.getPreparationStatus());

        return sessionData;
    }

    /**
     * Build simple session data map for tomorrow's session (no lateness check needed)
     */
    public Map<String, Object> buildSimpleSessionData(ClassSession session) {
        if (session == null || session.getClassGroup() == null) {
            return new HashMap<>();
        }

        ClassGroup classGroup = session.getClassGroup();

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("id", session.getId());
        sessionData.put("className", classGroup.getName());
        sessionData.put("time", formatSessionTime(classGroup));
        sessionData.put("room", classGroup.getLocation());

        return sessionData;
    }

    /**
     * Check in instructor for a session
     */
    @Transactional
    public TeacherAttendance checkInInstructor(ClassSession session, User instructor, String location, String lateReason) {
        if (session == null || instructor == null || location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Session, instructor, and location are required for check-in");
        }

        // Get or create teacher attendance record
        TeacherAttendance attendance = getTeacherAttendance(session, instructor)
            .orElseGet(() -> {
                TeacherAttendance newAttendance = new TeacherAttendance();
                newAttendance.setClassSession(session);
                newAttendance.setScheduledInstructor(instructor);
                newAttendance.setActualInstructor(instructor);
                return newAttendance;
            });

        // Perform check-in
        attendance.checkIn(location.trim());

        // Handle late check-in
        boolean isLate = isSessionLate(session);
        if (isLate) {
            if (lateReason == null || lateReason.trim().isEmpty()) {
                throw new IllegalArgumentException("Late reason is required for late check-in");
            }

            // Add late check-in information to notes
            String lateNote = String.format("LATE CHECK-IN: %s (Reason: %s)",
                LocalDateTime.now().toString(), lateReason.trim());

            String existingNotes = attendance.getNotes();
            if (existingNotes == null || existingNotes.trim().isEmpty()) {
                attendance.setNotes(lateNote);
            } else {
                attendance.setNotes(existingNotes + "\n" + lateNote);
            }
        }

        // Save and return
        return teacherAttendanceRepository.save(attendance);
    }
}