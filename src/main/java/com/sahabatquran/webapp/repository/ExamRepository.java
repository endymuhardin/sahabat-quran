package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.ClassGroup;
import com.sahabatquran.webapp.entity.Exam;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    
    // Find exams by class group
    List<Exam> findByClassGroupOrderByScheduledStartDesc(ClassGroup classGroup);
    
    // Find exams by academic term
    List<Exam> findByAcademicTermOrderByScheduledStartDesc(AcademicTerm academicTerm);
    
    // Find exams by status
    List<Exam> findByStatusOrderByScheduledStartDesc(Exam.ExamStatus status);
    
    // Find exams by type and class group
    List<Exam> findByExamTypeAndClassGroupOrderByScheduledStartDesc(
            Exam.ExamType examType, ClassGroup classGroup);
    
    // Find active exams (scheduled and active status)
    @Query("SELECT e FROM Exam e WHERE e.status IN ('SCHEDULED', 'ACTIVE') " +
           "AND e.scheduledStart <= :now AND e.scheduledEnd >= :now " +
           "ORDER BY e.scheduledStart ASC")
    List<Exam> findActiveExams(@Param("now") LocalDateTime now);
    
    // Find upcoming exams for a class group
    @Query("SELECT e FROM Exam e WHERE e.classGroup = :classGroup " +
           "AND e.status = 'SCHEDULED' AND e.scheduledStart > :now " +
           "ORDER BY e.scheduledStart ASC")
    List<Exam> findUpcomingExamsForClass(@Param("classGroup") ClassGroup classGroup, 
                                        @Param("now") LocalDateTime now);
    
    // Find exams created by instructor
    List<Exam> findByCreatedByOrderByCreatedAtDesc(User createdBy);
    
    // Find exams by class group with pagination
    Page<Exam> findByClassGroup(ClassGroup classGroup, Pageable pageable);
    
    // Find exams scheduled between dates
    @Query("SELECT e FROM Exam e WHERE e.scheduledStart >= :startDate " +
           "AND e.scheduledEnd <= :endDate ORDER BY e.scheduledStart ASC")
    List<Exam> findExamsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    // Find exams that need to be automatically transitioned to active
    @Query("SELECT e FROM Exam e WHERE e.status = 'SCHEDULED' " +
           "AND e.scheduledStart <= :now")
    List<Exam> findExamsToActivate(@Param("now") LocalDateTime now);
    
    // Find exams that need to be automatically completed
    @Query("SELECT e FROM Exam e WHERE e.status = 'ACTIVE' " +
           "AND e.scheduledEnd <= :now")
    List<Exam> findExamsToComplete(@Param("now") LocalDateTime now);
    
    // Find exams for a specific student (through class enrollment)
    @Query("SELECT e FROM Exam e JOIN Enrollment en ON e.classGroup = en.classGroup " +
           "WHERE en.student = :student AND e.status IN ('SCHEDULED', 'ACTIVE') " +
           "ORDER BY e.scheduledStart ASC")
    List<Exam> findExamsForStudent(@Param("student") User student);
    
    // Find completed exams for a student with results
    @Query("SELECT e FROM Exam e JOIN ExamResult er ON e = er.exam " +
           "WHERE er.student = :student AND er.status = 'GRADED' " +
           "ORDER BY e.scheduledStart DESC")
    List<Exam> findCompletedExamsForStudent(@Param("student") User student);
    
    // Count exams by status for reporting
    @Query("SELECT e.status, COUNT(e) FROM Exam e GROUP BY e.status")
    List<Object[]> countExamsByStatus();
    
    // Find exams requiring grading
    @Query("SELECT DISTINCT e FROM Exam e JOIN ExamResult er ON e = er.exam " +
           "WHERE er.status = 'SUBMITTED' AND e.status = 'ACTIVE'")
    List<Exam> findExamsRequiringGrading();
    
    // Check for scheduling conflicts
    @Query("SELECT e FROM Exam e WHERE e.classGroup = :classGroup " +
           "AND e.status IN ('SCHEDULED', 'ACTIVE') " +
           "AND ((e.scheduledStart <= :endTime AND e.scheduledEnd >= :startTime)) " +
           "AND (:examId IS NULL OR e.id != :examId)")
    List<Exam> findSchedulingConflicts(@Param("classGroup") ClassGroup classGroup,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("examId") UUID examId);
}