package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ClassGroup;
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
public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID> {
    
    Optional<ClassGroup> findByName(String name);
    
    List<ClassGroup> findByLevel(Level level);
    
    List<ClassGroup> findByInstructor(User instructor);
    
    List<ClassGroup> findByTerm(AcademicTerm term);
    
    List<ClassGroup> findByIsActive(Boolean isActive);
    
    List<ClassGroup> findByLevelAndTerm(Level level, AcademicTerm term);
    
    List<ClassGroup> findByInstructorAndTerm(User instructor, AcademicTerm term);
    
    @Query("SELECT c FROM ClassGroup c WHERE c.term.id = :termId AND c.isActive = true")
    List<ClassGroup> findActiveClassesByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT c FROM ClassGroup c " +
           "LEFT JOIN FETCH c.enrollments e " +
           "WHERE c.term.id = :termId")
    List<ClassGroup> findByTermWithEnrollments(@Param("termId") UUID termId);
    
    @Query("SELECT c FROM ClassGroup c " +
           "LEFT JOIN FETCH c.instructor " +
           "LEFT JOIN FETCH c.level " +
           "WHERE c.term.id = :termId")
    List<ClassGroup> findByTermWithInstructorAndLevel(@Param("termId") UUID termId);
    
    @Query("SELECT c FROM ClassGroup c WHERE c.classType = :classType AND c.term.id = :termId")
    List<ClassGroup> findByClassTypeAndTerm(
            @Param("classType") ClassGroup.ClassType classType,
            @Param("termId") UUID termId);
    
    @Query("SELECT c FROM ClassGroup c WHERE c.studentCategoryMix = :categoryMix AND c.term.id = :termId")
    List<ClassGroup> findByStudentCategoryMixAndTerm(
            @Param("categoryMix") ClassGroup.StudentCategoryMix categoryMix,
            @Param("termId") UUID termId);
    
    @Query("SELECT c FROM ClassGroup c " +
           "WHERE c.term.id = :termId " +
           "AND (SELECT COUNT(e) FROM Enrollment e WHERE e.classGroup = c) < c.minStudents")
    List<ClassGroup> findUndersizedClasses(@Param("termId") UUID termId);
    
    @Query("SELECT c FROM ClassGroup c " +
           "WHERE c.term.id = :termId " +
           "AND (SELECT COUNT(e) FROM Enrollment e WHERE e.classGroup = c) > c.maxStudents")
    List<ClassGroup> findOversizedClasses(@Param("termId") UUID termId);
    
    @Query("SELECT c, COUNT(e) as enrollmentCount FROM ClassGroup c " +
           "LEFT JOIN c.enrollments e " +
           "WHERE c.term.id = :termId " +
           "GROUP BY c ORDER BY enrollmentCount")
    List<Object[]> findClassEnrollmentCounts(@Param("termId") UUID termId);
    
    @Query("SELECT c.level, COUNT(c) as classCount FROM ClassGroup c " +
           "WHERE c.term.id = :termId AND c.isActive = true " +
           "GROUP BY c.level ORDER BY classCount DESC")
    List<Object[]> findClassCountsByLevel(@Param("termId") UUID termId);
    
    @Query("SELECT c.instructor, COUNT(c) as classCount FROM ClassGroup c " +
           "WHERE c.term.id = :termId AND c.isActive = true " +
           "GROUP BY c.instructor ORDER BY classCount DESC")
    List<Object[]> findClassCountsByInstructor(@Param("termId") UUID termId);
}