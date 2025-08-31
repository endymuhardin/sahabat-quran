package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Enrollment;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    
    List<Enrollment> findByStudent(User student);
    
    List<Enrollment> findByClassGroup(ClassGroup classGroup);
    
    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status);
    
    Optional<Enrollment> findByStudentAndClassGroup(User student, ClassGroup classGroup);
    
    @Query("SELECT e FROM Enrollment e WHERE e.classGroup.term.id = :termId")
    List<Enrollment> findByTermId(@Param("termId") UUID termId);
    
    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.student.id = :studentId AND e.classGroup.term.id = :termId")
    List<Enrollment> findByStudentAndTerm(@Param("studentId") UUID studentId, @Param("termId") UUID termId);
    
    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.classGroup.id = :classGroupId AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByClassGroup(@Param("classGroupId") UUID classGroupId);
    
    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.student.id = :studentId AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByStudent(@Param("studentId") UUID studentId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.classGroup.id = :classGroupId AND e.status = 'ACTIVE'")
    Long countActiveEnrollmentsByClassGroup(@Param("classGroupId") UUID classGroupId);
    
    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Enrollment> findByEnrollmentDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.student " +
           "LEFT JOIN FETCH e.classGroup c " +
           "LEFT JOIN FETCH c.level " +
           "WHERE c.id = :classGroupId")
    List<Enrollment> findByClassGroupWithStudentAndLevel(@Param("classGroupId") UUID classGroupId);
    
    @Query("SELECT e.classGroup, COUNT(e) as enrollmentCount FROM Enrollment e " +
           "WHERE e.classGroup.term.id = :termId AND e.status = 'ACTIVE' " +
           "GROUP BY e.classGroup ORDER BY enrollmentCount DESC")
    List<Object[]> findEnrollmentCountsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT e.classGroup.level, COUNT(e) as enrollmentCount FROM Enrollment e " +
           "WHERE e.classGroup.term.id = :termId AND e.status = 'ACTIVE' " +
           "GROUP BY e.classGroup.level ORDER BY enrollmentCount DESC")
    List<Object[]> findEnrollmentCountsByLevel(@Param("termId") UUID termId);
}