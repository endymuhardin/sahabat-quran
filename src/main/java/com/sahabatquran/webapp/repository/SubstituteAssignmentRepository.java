package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.SubstituteAssignment;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubstituteAssignmentRepository extends JpaRepository<SubstituteAssignment, UUID> {
    
    Optional<SubstituteAssignment> findByClassSession(ClassSession classSession);
    
    List<SubstituteAssignment> findBySubstituteTeacherOrderByAssignmentDateDesc(User substituteTeacher);
    
    List<SubstituteAssignment> findByOriginalTeacher(User originalTeacher);
    
    List<SubstituteAssignment> findByAssignmentType(SubstituteAssignment.AssignmentType assignmentType);
    
    @Query("SELECT sa FROM SubstituteAssignment sa WHERE sa.substituteAccepted IS NULL ORDER BY sa.assignmentDate DESC")
    List<SubstituteAssignment> findPendingAssignments();
    
    @Query("SELECT sa FROM SubstituteAssignment sa WHERE sa.substituteTeacher = :teacher AND sa.substituteAccepted IS NULL ORDER BY sa.assignmentDate DESC")
    List<SubstituteAssignment> findPendingAssignmentsByTeacher(@Param("teacher") User teacher);
    
    @Query("SELECT sa FROM SubstituteAssignment sa WHERE sa.assignmentDate >= :since AND sa.substituteAccepted = true")
    List<SubstituteAssignment> findAcceptedAssignmentsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(sa) FROM SubstituteAssignment sa WHERE sa.substituteTeacher = :teacher AND sa.substituteAccepted = true")
    Long countAcceptedAssignmentsByTeacher(@Param("teacher") User teacher);
    
    @Query("SELECT AVG(sa.performanceRating) FROM SubstituteAssignment sa WHERE sa.substituteTeacher = :teacher AND sa.performanceRating IS NOT NULL")
    java.math.BigDecimal calculateAverageRatingForTeacher(@Param("teacher") User teacher);
    
    // Method for monitoring service to find active assignments
    default List<SubstituteAssignment> findActiveAssignments() {
        return findPendingAssignments();
    }
}