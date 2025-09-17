package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.StudentAssessment;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentAssessmentRepository extends JpaRepository<StudentAssessment, UUID> {
    
    List<StudentAssessment> findByStudent(User student);
    
    List<StudentAssessment> findByTerm(AcademicTerm term);
    
    List<StudentAssessment> findByStudentCategory(StudentAssessment.StudentCategory studentCategory);
    
    List<StudentAssessment> findByAssessmentType(StudentAssessment.AssessmentType assessmentType);
    
    List<StudentAssessment> findByDeterminedLevel(Level determinedLevel);
    
    List<StudentAssessment> findByStudentAndTerm(User student, AcademicTerm term);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.studentCategory = :category")
    List<StudentAssessment> findByTermAndStudentCategory(
            @Param("termId") UUID termId,
            @Param("category") StudentAssessment.StudentCategory category);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.assessmentType = :type")
    List<StudentAssessment> findByTermAndAssessmentType(
            @Param("termId") UUID termId,
            @Param("type") StudentAssessment.AssessmentType type);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.isValidated = false")
    List<StudentAssessment> findUnvalidatedAssessmentsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "WHERE sa.assessmentDate BETWEEN :startDate AND :endDate")
    List<StudentAssessment> findByAssessmentDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "WHERE sa.assessmentScore BETWEEN :minScore AND :maxScore")
    List<StudentAssessment> findByScoreRange(
            @Param("minScore") Double minScore,
            @Param("maxScore") Double maxScore);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "WHERE sa.assessmentGrade = :grade AND sa.assessmentType = 'TERM_EXAM'")
    List<StudentAssessment> findByExamGrade(@Param("grade") String grade);
    
    @Query("SELECT sa FROM StudentAssessment sa " +
           "LEFT JOIN FETCH sa.student " +
           "LEFT JOIN FETCH sa.determinedLevel " +
           "WHERE sa.term.id = :termId")
    List<StudentAssessment> findByTermWithStudentAndLevel(@Param("termId") UUID termId);
    
    @Query("SELECT sa.determinedLevel, COUNT(sa) FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.isValidated = true " +
           "GROUP BY sa.determinedLevel ORDER BY COUNT(sa) DESC")
    List<Object[]> findLevelDistributionByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT sa.studentCategory, COUNT(sa) FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId " +
           "GROUP BY sa.studentCategory")
    List<Object[]> findStudentCategoryDistributionByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT sa.assessmentType, AVG(sa.assessmentScore) as avgScore FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.assessmentScore IS NOT NULL " +
           "GROUP BY sa.assessmentType")
    List<Object[]> findAverageScoresByAssessmentType(@Param("termId") UUID termId);
    
    @Query("SELECT COUNT(sa) FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.studentCategory = 'NEW' AND sa.assessmentType = 'PLACEMENT'")
    Long countNewStudentPlacementTests(@Param("termId") UUID termId);
    
    @Query("SELECT COUNT(sa) FROM StudentAssessment sa " +
           "WHERE sa.term.id = :termId AND sa.studentCategory = 'EXISTING' AND sa.assessmentType = 'MIDTERM'")
    Long countExistingStudentExams(@Param("termId") UUID termId);

    // ====================== FILTERING METHODS ======================

    /**
     * Check if student has assessments at a specific level.
     */
    boolean existsByStudentIdAndDeterminedLevelId(UUID studentId, UUID levelId);

    /**
     * Check if student has assessments in a specific term.
     */
    boolean existsByStudentIdAndTermId(UUID studentId, UUID termId);

    /**
     * Find assessments for a student in a specific term.
     */
    List<StudentAssessment> findByStudentIdAndTermId(UUID studentId, UUID termId);
}