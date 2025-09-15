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

        log.info("Session late check: sessionId={}, currentTime={}, sessionStartTime={}, lateThreshold={}, " +
                "isTimeBasedLate={}, isStatusBasedLate={}, finalIsLate={}, sessionStatus={}, schedule='{}'",
                session.getId(), currentTime, sessionStartTime, lateThreshold,
                isTimeBasedLate, isStatusBasedLate, isLate,
                session.getPreparationStatus(), classGroup.getSchedule());

        return isLate;
    }

    /**
     * Parse session start time from ClassGroup schedule field
     * Format: "Senin & Rabu, 08:00-10:00" or "Selasa & Kamis, 16:00-18:00"
     */
    public LocalTime parseSessionStartTime(ClassGroup classGroup) {
        if (classGroup == null || classGroup.getSchedule() == null) {
            return LocalTime.of(8, 0); // Default fallback
        }

        try {
            String schedule = classGroup.getSchedule();
            // Extract time portion after the comma
            if (schedule.contains(",")) {
                String timePortion = schedule.split(",")[1].trim();
                // Extract start time before the dash
                if (timePortion.contains("-")) {
                    String startTimeStr = timePortion.split("-")[0].trim();
                    return LocalTime.parse(startTimeStr);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse schedule '{}', using default time", classGroup.getSchedule());
        }

        return LocalTime.of(8, 0); // Default fallback
    }

    /**
     * Format session time range from ClassGroup schedule
     */
    public String formatSessionTime(ClassGroup classGroup) {
        if (classGroup == null || classGroup.getSchedule() == null) {
            // Fallback formatting
            LocalTime startTime = parseSessionStartTime(classGroup);
            LocalTime endTime = startTime.plusMinutes(90); // Assume 90-minute sessions
            return String.format("%02d:%02d-%02d:%02d",
                    startTime.getHour(), startTime.getMinute(),
                    endTime.getHour(), endTime.getMinute());
        }

        try {
            String schedule = classGroup.getSchedule();
            // Extract time portion after the comma
            if (schedule.contains(",")) {
                String timePortion = schedule.split(",")[1].trim();
                // Return the time range directly
                if (timePortion.contains("-")) {
                    return timePortion; // Already in format "08:00-10:00"
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse schedule '{}', using default format", classGroup.getSchedule());
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
}