package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.TeacherPreClosureDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Teacher Pre-Closure Operations
 * Handles validation and preparation tasks before semester closure
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherPreClosureService {

    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final ClassSessionRepository classSessionRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;
    private final AttendanceRepository attendanceRepository;
    private final AcademicTermRepository academicTermRepository;

    /**
     * Get comprehensive pre-closure status for a teacher
     */
    public TeacherPreClosureDto getTeacherPreClosureStatus(UUID teacherId, UUID termId) {
        log.info("Getting pre-closure status for teacher: {} and term: {}", teacherId, termId);

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        // Get all classes taught by this teacher in the term
        List<ClassGroup> teacherClasses = classGroupRepository.findByInstructorAndTerm(teacher, term);

        // Calculate overall statistics
        int totalClasses = teacherClasses.size();
        int classesWithCompleteGrades = 0;
        int classesWithVerifiedAttendance = 0;
        int completedEvaluations = 0;

        List<TeacherPreClosureDto.ClassPreClosureStatus> classStatuses = new ArrayList<>();
        List<TeacherPreClosureDto.PendingTask> pendingTasks = new ArrayList<>();

        // Process each class
        for (ClassGroup classGroup : teacherClasses) {
            TeacherPreClosureDto.ClassPreClosureStatus classStatus =
                    getClassPreClosureStatus(classGroup, teacher);
            classStatuses.add(classStatus);

            // Update counters
            if (classStatus.isGradesFinalized()) {
                classesWithCompleteGrades++;
            } else {
                // Add pending grade tasks
                if (classStatus.getStudentsWithoutGrades() > 0) {
                    pendingTasks.add(TeacherPreClosureDto.PendingTask.builder()
                            .taskType("GRADE_ENTRY")
                            .description("Complete grade entry for " +
                                       classStatus.getStudentsWithoutGrades() + " students")
                            .relatedClassId(classStatus.getClassId())
                            .relatedClassName(classStatus.getClassName())
                            .itemCount(classStatus.getStudentsWithoutGrades())
                            .urgency("HIGH")
                            .deadline(term.getEndDate())
                            .build());
                }
            }

            if (classStatus.isAttendanceVerified()) {
                classesWithVerifiedAttendance++;
            } else {
                // Add pending attendance tasks
                if (classStatus.getSessionsWithoutAttendance() > 0) {
                    pendingTasks.add(TeacherPreClosureDto.PendingTask.builder()
                            .taskType("ATTENDANCE_VERIFICATION")
                            .description("Verify attendance for " +
                                       classStatus.getSessionsWithoutAttendance() + " sessions")
                            .relatedClassId(classStatus.getClassId())
                            .relatedClassName(classStatus.getClassName())
                            .itemCount(classStatus.getSessionsWithoutAttendance())
                            .urgency("MEDIUM")
                            .deadline(term.getEndDate())
                            .build());
                }
            }

            if (classStatus.isEvaluationSubmitted()) {
                completedEvaluations++;
            } else {
                // Add pending evaluation task
                pendingTasks.add(TeacherPreClosureDto.PendingTask.builder()
                        .taskType("EVALUATION_SUBMISSION")
                        .description("Submit class evaluation")
                        .relatedClassId(classStatus.getClassId())
                        .relatedClassName(classStatus.getClassName())
                        .itemCount(1)
                        .urgency("MEDIUM")
                        .deadline(term.getEndDate())
                        .build());
            }
        }

        // Calculate completion percentages
        double gradeCompletionPercentage = totalClasses > 0 ?
                (classesWithCompleteGrades * 100.0 / totalClasses) : 0;
        double attendanceVerificationPercentage = totalClasses > 0 ?
                (classesWithVerifiedAttendance * 100.0 / totalClasses) : 0;
        double evaluationCompletionPercentage = totalClasses > 0 ?
                (completedEvaluations * 100.0 / totalClasses) : 0;
        double overallCompletionPercentage =
                (gradeCompletionPercentage + attendanceVerificationPercentage +
                 evaluationCompletionPercentage) / 3;

        // Calculate days until closure
        int daysUntilClosure = (int) ChronoUnit.DAYS.between(LocalDate.now(), term.getEndDate());

        return TeacherPreClosureDto.builder()
                .teacherId(teacherId)
                .teacherName(teacher.getFullName())
                .teacherCode(teacher.getUsername())
                .termId(termId)
                .termName(term.getTermName())
                .termEndDate(term.getEndDate())
                .daysUntilClosure(daysUntilClosure)
                .totalClasses(totalClasses)
                .classesWithCompleteGrades(classesWithCompleteGrades)
                .classesWithIncompleteGrades(totalClasses - classesWithCompleteGrades)
                .classesWithVerifiedAttendance(classesWithVerifiedAttendance)
                .classesWithUnverifiedAttendance(totalClasses - classesWithVerifiedAttendance)
                .pendingEvaluations(totalClasses - completedEvaluations)
                .completedEvaluations(completedEvaluations)
                .gradeCompletionPercentage(gradeCompletionPercentage)
                .attendanceVerificationPercentage(attendanceVerificationPercentage)
                .evaluationCompletionPercentage(evaluationCompletionPercentage)
                .overallCompletionPercentage(overallCompletionPercentage)
                .classStatuses(classStatuses)
                .pendingTasks(pendingTasks)
                .allGradesFinalized(classesWithCompleteGrades == totalClasses)
                .allAttendanceVerified(classesWithVerifiedAttendance == totalClasses)
                .allEvaluationsCompleted(completedEvaluations == totalClasses)
                .readyForClosure(classesWithCompleteGrades == totalClasses &&
                                classesWithVerifiedAttendance == totalClasses &&
                                completedEvaluations == totalClasses)
                .build();
    }

    /**
     * Get pre-closure status for a specific class
     */
    private TeacherPreClosureDto.ClassPreClosureStatus getClassPreClosureStatus(
            ClassGroup classGroup, User teacher) {

        // Get enrollments for the class
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);
        int totalStudents = enrollments.size();

        // Check grades
        int studentsWithGrades = 0;
        List<String> missingGradeStudents = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            List<StudentAssessment> assessments = studentAssessmentRepository
                    .findByStudentAndTerm(enrollment.getStudent(), classGroup.getTerm());

            if (assessments.isEmpty() || assessments.stream()
                    .noneMatch(a -> a.getAssessmentGrade() != null)) {
                missingGradeStudents.add(enrollment.getStudent().getFullName());
            } else {
                studentsWithGrades++;
            }
        }

        // Check attendance
        List<ClassSession> classSessions = classSessionRepository.findByClassGroup(classGroup);
        int totalSessions = classSessions.size();
        int sessionsWithAttendance = 0;
        List<String> missingSessions = new ArrayList<>();

        for (ClassSession session : classSessions) {
            // Check if teacher attendance exists
            boolean hasTeacherAttendance = teacherAttendanceRepository
                    .findByClassSessionAndScheduledInstructor(session, teacher).isPresent();

            // Check if student attendance records exist for this session date
            Integer attendanceCount = attendanceRepository
                    .countTotalAttendanceRecordsForDate(session.getSessionDate());

            if (hasTeacherAttendance && attendanceCount != null && attendanceCount > 0) {
                sessionsWithAttendance++;
            } else {
                missingSessions.add(session.getSessionDate().toString());
            }
        }

        // Check if grades are finalized (simplified - would need actual finalization flag)
        boolean gradesFinalized = studentsWithGrades == totalStudents && totalStudents > 0;

        // Check if attendance is verified
        boolean attendanceVerified = sessionsWithAttendance == totalSessions && totalSessions > 0;

        // Check if evaluation is submitted (simplified - would need evaluation entity)
        boolean evaluationSubmitted = false; // TODO: Implement when evaluation entity exists

        // Calculate completion percentage
        double completionPercentage = 0;
        if (gradesFinalized) completionPercentage += 33.33;
        if (attendanceVerified) completionPercentage += 33.33;
        if (evaluationSubmitted) completionPercentage += 33.34;

        // Determine overall status
        String status;
        if (completionPercentage >= 100) {
            status = "COMPLETE";
        } else if (completionPercentage > 0) {
            status = "INCOMPLETE";
        } else {
            status = "PENDING";
        }

        return TeacherPreClosureDto.ClassPreClosureStatus.builder()
                .classId(classGroup.getId())
                .className(classGroup.getName())
                .levelName(classGroup.getLevel().getName())
                .totalStudents(totalStudents)
                .studentsWithGrades(studentsWithGrades)
                .studentsWithoutGrades(totalStudents - studentsWithGrades)
                .gradesFinalized(gradesFinalized)
                .gradesFinalizedDate(gradesFinalized ? LocalDate.now() : null)
                .totalSessions(totalSessions)
                .sessionsWithAttendance(sessionsWithAttendance)
                .sessionsWithoutAttendance(totalSessions - sessionsWithAttendance)
                .attendanceVerified(attendanceVerified)
                .attendanceVerifiedDate(attendanceVerified ? LocalDate.now() : null)
                .evaluationSubmitted(evaluationSubmitted)
                .evaluationSubmittedDate(evaluationSubmitted ? LocalDate.now() : null)
                .missingGradeStudents(missingGradeStudents)
                .missingSessions(missingSessions)
                .status(status)
                .completionPercentage(completionPercentage)
                .build();
    }

    /**
     * Finalize grades for a class
     */
    @Transactional
    public void finalizeClassGrades(UUID classId, UUID teacherId) {
        log.info("Finalizing grades for class: {} by teacher: {}", classId, teacherId);

        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        // Verify teacher owns this class
        if (!classGroup.getInstructor().getId().equals(teacherId)) {
            throw new IllegalArgumentException("Teacher not authorized for this class");
        }

        // Get all enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);

        // Check all students have grades
        for (Enrollment enrollment : enrollments) {
            List<StudentAssessment> assessments = studentAssessmentRepository
                    .findByStudentAndTerm(enrollment.getStudent(), classGroup.getTerm());

            if (assessments.isEmpty() || assessments.stream()
                    .noneMatch(a -> a.getAssessmentGrade() != null)) {
                throw new IllegalStateException("Cannot finalize grades - student " +
                        enrollment.getStudent().getFullName() + " has no final grade");
            }
        }

        // Mark grades as finalized (would need actual finalization mechanism)
        // For now, we'll log the action
        log.info("Grades finalized for class: {} with {} students",
                classGroup.getName(), enrollments.size());
    }

    /**
     * Verify attendance records for a class
     */
    @Transactional
    public void verifyClassAttendance(UUID classId, UUID teacherId) {
        log.info("Verifying attendance for class: {} by teacher: {}", classId, teacherId);

        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        // Verify teacher owns this class
        if (!classGroup.getInstructor().getId().equals(teacherId)) {
            throw new IllegalArgumentException("Teacher not authorized for this class");
        }

        // Get all sessions
        List<ClassSession> sessions = classSessionRepository.findByClassGroup(classGroup);

        // Verify all sessions have attendance
        for (ClassSession session : sessions) {
            boolean hasTeacherAttendance = teacherAttendanceRepository
                    .findByClassSessionAndScheduledInstructor(session, classGroup.getInstructor()).isPresent();

            if (!hasTeacherAttendance) {
                throw new IllegalStateException("Cannot verify attendance - session on " +
                        session.getSessionDate() + " has no teacher attendance");
            }

            Integer attendanceCount = attendanceRepository.countTotalAttendanceRecordsForDate(session.getSessionDate());
            if (attendanceCount == null || attendanceCount == 0) {
                throw new IllegalStateException("Cannot verify attendance - session on " +
                        session.getSessionDate() + " has no student attendance records");
            }
        }

        // Mark attendance as verified (would need actual verification mechanism)
        log.info("Attendance verified for class: {} with {} sessions",
                classGroup.getName(), sessions.size());
    }
}