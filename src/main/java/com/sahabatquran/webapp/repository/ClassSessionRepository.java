package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.ClassGroup;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, UUID> {
    
    List<ClassSession> findByClassGroup(ClassGroup classGroup);
    
    List<ClassSession> findByInstructor(User instructor);
    
    List<ClassSession> findByPreparationStatus(ClassSession.PreparationStatus preparationStatus);
    
    Optional<ClassSession> findByClassGroupAndSessionDate(ClassGroup classGroup, LocalDate sessionDate);
    
    @Query("SELECT cs FROM ClassSession cs WHERE cs.classGroup.id = :classGroupId ORDER BY cs.sessionDate")
    List<ClassSession> findByClassGroupOrderBySessionDate(@Param("classGroupId") UUID classGroupId);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "WHERE cs.instructor.id = :instructorId ORDER BY cs.sessionDate")
    List<ClassSession> findByInstructorOrderBySessionDate(@Param("instructorId") UUID instructorId);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "WHERE cs.sessionDate = :sessionDate AND cs.instructor.id = :instructorId")
    List<ClassSession> findBySessionDateAndInstructor(
            @Param("sessionDate") LocalDate sessionDate,
            @Param("instructorId") UUID instructorId);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "WHERE cs.sessionDate BETWEEN :startDate AND :endDate")
    List<ClassSession> findBySessionDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "WHERE cs.classGroup.term.id = :termId AND cs.preparationStatus = 'READY'")
    List<ClassSession> findReadySessionsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "WHERE cs.classGroup.term.id = :termId AND cs.preparationStatus IN ('DRAFT', 'IN_PROGRESS')")
    List<ClassSession> findIncompletePreparationByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "LEFT JOIN FETCH cs.sessionMaterials " +
           "LEFT JOIN FETCH cs.preparationChecklistItems " +
           "WHERE cs.id = :sessionId")
    Optional<ClassSession> findByIdWithMaterialsAndChecklist(@Param("sessionId") UUID sessionId);

    @Query("SELECT cs FROM ClassSession cs " +
           "LEFT JOIN FETCH cs.classGroup cg " +
           "LEFT JOIN FETCH cg.timeSlot ts " +
           "LEFT JOIN FETCH ts.session " +
           "WHERE cs.id = :sessionId")
    Optional<ClassSession> findByIdWithTimeSlot(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT cs.preparationStatus, COUNT(cs) FROM ClassSession cs " +
           "WHERE cs.classGroup.term.id = :termId " +
           "GROUP BY cs.preparationStatus")
    List<Object[]> findPreparationStatusCountsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT cs.instructor, COUNT(cs) as sessionCount, " +
           "SUM(CASE WHEN cs.preparationStatus = 'READY' THEN 1 ELSE 0 END) as readyCount FROM ClassSession cs " +
           "WHERE cs.classGroup.term.id = :termId " +
           "GROUP BY cs.instructor")
    List<Object[]> findInstructorPreparationSummary(@Param("termId") UUID termId);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "WHERE cs.sessionDate >= :fromDate AND cs.preparationStatus != 'READY'")
    List<ClassSession> findUpcomingUnpreparedSessions(@Param("fromDate") LocalDate fromDate);
    
    // Additional methods for daily operations
    List<ClassSession> findBySessionDate(LocalDate sessionDate);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "LEFT JOIN FETCH cs.classGroup cg " +
           "LEFT JOIN FETCH cg.timeSlot ts " +
           "LEFT JOIN FETCH ts.session " +
           "WHERE cs.instructor.id = :instructorId AND cs.sessionDate = :sessionDate")
    List<ClassSession> findByInstructorIdAndSessionDate(
            @Param("instructorId") UUID instructorId,
            @Param("sessionDate") LocalDate sessionDate);
    
    @Query("SELECT cs FROM ClassSession cs " +
           "LEFT JOIN FETCH cs.classGroup cg " +
           "LEFT JOIN FETCH cg.timeSlot ts " +
           "LEFT JOIN FETCH ts.session " +
           "WHERE cs.instructor.id = :instructorId AND cs.sessionDate BETWEEN :startDate AND :endDate")
    List<ClassSession> findByInstructorIdAndSessionDateBetween(
            @Param("instructorId") UUID instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}