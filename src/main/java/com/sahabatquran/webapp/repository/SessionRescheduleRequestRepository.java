package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.SessionRescheduleRequest;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRescheduleRequestRepository extends JpaRepository<SessionRescheduleRequest, UUID> {
    
    List<SessionRescheduleRequest> findByClassSession(ClassSession classSession);
    
    List<SessionRescheduleRequest> findByRequestedBy(User requestedBy);
    
    List<SessionRescheduleRequest> findByStatus(SessionRescheduleRequest.RescheduleStatus status);
    
    @Query("SELECT r FROM SessionRescheduleRequest r WHERE r.classSession.sessionDate = :sessionDate")
    List<SessionRescheduleRequest> findBySessionDate(@Param("sessionDate") LocalDate sessionDate);
    
    @Query("SELECT r FROM SessionRescheduleRequest r WHERE r.requestedBy.id = :instructorId AND r.status = 'PENDING'")
    List<SessionRescheduleRequest> findPendingRequestsByInstructor(@Param("instructorId") UUID instructorId);
    
    @Query("SELECT r FROM SessionRescheduleRequest r WHERE r.status = 'PENDING' ORDER BY r.createdAt")
    List<SessionRescheduleRequest> findPendingRequestsOrderByRequestDate();
}