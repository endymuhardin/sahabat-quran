package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Exam;
import com.sahabatquran.webapp.entity.ExamResult;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, UUID> {
    
    // Find results by exam
    List<ExamResult> findByExamOrderByCreatedAtDesc(Exam exam);
    
    // Find results by student
    List<ExamResult> findByStudentOrderByCreatedAtDesc(User student);
    
    // Find results by exam and student
    List<ExamResult> findByExamAndStudentOrderByAttemptNumberDesc(Exam exam, User student);
    
    // Find latest result for exam and student
    Optional<ExamResult> findFirstByExamAndStudentOrderByAttemptNumberDesc(Exam exam, User student);
    
    // Find results by status
    List<ExamResult> findByStatusOrderByCreatedAtDesc(ExamResult.ExamResultStatus status);
    
    // Find results requiring grading
    @Query("SELECT er FROM ExamResult er WHERE er.status = 'SUBMITTED' " +
           "ORDER BY er.completedAt ASC")
    List<ExamResult> findResultsRequiringGrading();
    
    // Find results requiring grading for specific instructor (through class group)
    @Query("SELECT er FROM ExamResult er JOIN er.exam e JOIN e.classGroup cg " +
           "JOIN TeacherLevelAssignment tla ON cg.level = tla.level " +
           "WHERE er.status = 'SUBMITTED' AND tla.teacher = :instructor " +
           "ORDER BY er.completedAt ASC")
    List<ExamResult> findResultsRequiringGradingForInstructor(@Param("instructor") User instructor);
    
    // Find graded results with pagination
    Page<ExamResult> findByStatusAndGradedByIsNotNull(
            ExamResult.ExamResultStatus status, Pageable pageable);
    
    // Count attempts for exam and student
    @Query("SELECT COUNT(er) FROM ExamResult er WHERE er.exam = :exam AND er.student = :student")
    Long countAttemptsByExamAndStudent(@Param("exam") Exam exam, @Param("student") User student);
    
    // Check if student can take exam (within attempt limits)
    @Query("SELECT CASE WHEN COUNT(er) < :maxAttempts THEN true ELSE false END " +
           "FROM ExamResult er WHERE er.exam = :exam AND er.student = :student")
    Boolean canStudentTakeExam(@Param("exam") Exam exam, 
                              @Param("student") User student, 
                              @Param("maxAttempts") Integer maxAttempts);
    
    // Find student's current in-progress exam
    Optional<ExamResult> findByStudentAndStatusIn(User student, 
                                                 List<ExamResult.ExamResultStatus> statuses);
    
    // Find best score for student and exam
    @Query("SELECT er FROM ExamResult er WHERE er.exam = :exam AND er.student = :student " +
           "AND er.status = 'GRADED' ORDER BY er.percentageScore DESC LIMIT 1")
    Optional<ExamResult> findBestScoreByExamAndStudent(@Param("exam") Exam exam, 
                                                      @Param("student") User student);
    
    // Get exam statistics
    @Query("SELECT AVG(er.percentageScore), MIN(er.percentageScore), MAX(er.percentageScore), " +
           "COUNT(er), SUM(CASE WHEN er.passed = true THEN 1 ELSE 0 END) " +
           "FROM ExamResult er WHERE er.exam = :exam AND er.status = 'GRADED'")
    Object[] getExamStatistics(@Param("exam") Exam exam);
    
    // Find results by grade
    List<ExamResult> findByGrade(ExamResult.Grade grade);
    
    // Find passing results for a student
    @Query("SELECT er FROM ExamResult er WHERE er.student = :student " +
           "AND er.passed = true AND er.status = 'GRADED' " +
           "ORDER BY er.completedAt DESC")
    List<ExamResult> findPassingResultsForStudent(@Param("student") User student);
    
    // Find results that were auto-submitted due to time expiry
    List<ExamResult> findByAutoSubmittedTrueOrderByCompletedAtDesc();
    
    // Get class performance summary
    @Query("SELECT er.grade, COUNT(er) FROM ExamResult er " +
           "WHERE er.exam = :exam AND er.status = 'GRADED' " +
           "GROUP BY er.grade ORDER BY er.grade")
    List<Object[]> getGradeDistribution(@Param("exam") Exam exam);
    
    // Find results that need instructor review (manual grading needed)
    @Query("SELECT DISTINCT er FROM ExamResult er JOIN er.examAnswers ea " +
           "WHERE er.status = 'SUBMITTED' AND ea.gradingStatus = 'PENDING_REVIEW' " +
           "ORDER BY er.completedAt ASC")
    List<ExamResult> findResultsNeedingInstructorReview();
    
    // Check if exam has any submitted results (to prevent exam modification)
    @Query("SELECT CASE WHEN COUNT(er) > 0 THEN true ELSE false END " +
           "FROM ExamResult er WHERE er.exam = :exam " +
           "AND er.status IN ('SUBMITTED', 'GRADED')")
    Boolean hasSubmittedResults(@Param("exam") Exam exam);
}