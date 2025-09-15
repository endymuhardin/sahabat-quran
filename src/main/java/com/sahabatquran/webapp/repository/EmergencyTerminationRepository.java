package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.EmergencyTermination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmergencyTerminationRepository extends JpaRepository<EmergencyTermination, UUID> {

    Optional<EmergencyTermination> findBySessionId(UUID sessionId);

    List<EmergencyTermination> findByEmergencyType(EmergencyTermination.EmergencyType emergencyType);

    @Query("SELECT e FROM EmergencyTermination e WHERE e.createdAt >= :startDate AND e.createdAt <= :endDate")
    List<EmergencyTermination> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM EmergencyTermination e WHERE e.notificationsSent = false")
    List<EmergencyTermination> findPendingNotifications();

    @Query("SELECT e FROM EmergencyTermination e WHERE e.emergencyReportGenerated = false")
    List<EmergencyTermination> findPendingReports();

    long countByEmergencyType(EmergencyTermination.EmergencyType emergencyType);

    @Query("SELECT COUNT(e) FROM EmergencyTermination e WHERE e.createdAt >= :startDate")
    long countEmergenciesAfter(@Param("startDate") LocalDateTime startDate);
}