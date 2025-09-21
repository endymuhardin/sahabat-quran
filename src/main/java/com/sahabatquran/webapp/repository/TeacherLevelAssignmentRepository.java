package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.TeacherLevelAssignment;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.Level;
import com.sahabatquran.webapp.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherLevelAssignmentRepository extends JpaRepository<TeacherLevelAssignment, UUID> {
    
    List<TeacherLevelAssignment> findByTeacher(User teacher);
    
    List<TeacherLevelAssignment> findByLevel(Level level);
    
    List<TeacherLevelAssignment> findByTerm(AcademicTerm term);
    
    List<TeacherLevelAssignment> findByTeacherAndTerm(User teacher, AcademicTerm term);
    
    Optional<TeacherLevelAssignment> findByTeacherAndLevelAndTerm(User teacher, Level level, AcademicTerm term);
    
    @Query("SELECT tla FROM TeacherLevelAssignment tla WHERE tla.term.id = :termId")
    List<TeacherLevelAssignment> findByTermId(@Param("termId") UUID termId);
    
    @Query("SELECT tla FROM TeacherLevelAssignment tla " +
           "WHERE tla.level.id = :levelId AND tla.term.id = :termId")
    List<TeacherLevelAssignment> findByLevelAndTerm(@Param("levelId") UUID levelId, @Param("termId") UUID termId);
    
    @Query("SELECT tla FROM TeacherLevelAssignment tla " +
           "WHERE tla.competencyLevel = :competencyLevel AND tla.term.id = :termId")
    List<TeacherLevelAssignment> findByCompetencyLevelAndTerm(
            @Param("competencyLevel") TeacherLevelAssignment.CompetencyLevel competencyLevel,
            @Param("termId") UUID termId);
    
    @Query("SELECT tla FROM TeacherLevelAssignment tla " +
           "WHERE tla.specialization = :specialization AND tla.term.id = :termId")
    List<TeacherLevelAssignment> findBySpecializationAndTerm(
            @Param("specialization") TeacherLevelAssignment.Specialization specialization,
            @Param("termId") UUID termId);
    
    @Query("SELECT tla FROM TeacherLevelAssignment tla " +
           "LEFT JOIN FETCH tla.teacher " +
           "LEFT JOIN FETCH tla.level " +
           "WHERE tla.term.id = :termId")
    List<TeacherLevelAssignment> findByTermWithTeacherAndLevel(@Param("termId") UUID termId);
    
    @Query("SELECT tla.level, COUNT(tla) as teacherCount FROM TeacherLevelAssignment tla " +
           "WHERE tla.term.id = :termId " +
           "GROUP BY tla.level ORDER BY teacherCount DESC")
    List<Object[]> findLevelTeacherCounts(@Param("termId") UUID termId);
    
    @Query("SELECT tla.teacher, COUNT(tla) as levelCount FROM TeacherLevelAssignment tla " +
           "WHERE tla.term.id = :termId " +
           "GROUP BY tla.teacher ORDER BY levelCount DESC")
    List<Object[]> findTeacherLevelCounts(@Param("termId") UUID termId);
    
    @Query("SELECT COUNT(DISTINCT tla.teacher) FROM TeacherLevelAssignment tla WHERE tla.term.id = :termId")
    Long countDistinctTeachersByTermId(@Param("termId") UUID termId);

    // Count assignments by term
    long countByTermId(UUID termId);
}