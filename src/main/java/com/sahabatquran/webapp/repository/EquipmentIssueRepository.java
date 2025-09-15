package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.EquipmentIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentIssueRepository extends JpaRepository<EquipmentIssue, UUID> {

    List<EquipmentIssue> findBySessionId(UUID sessionId);

    List<EquipmentIssue> findByStatus(EquipmentIssue.IssueStatus status);

    List<EquipmentIssue> findByIsUrgentTrue();

    @Query("SELECT e FROM EquipmentIssue e WHERE e.createdAt >= :startDate AND e.createdAt <= :endDate")
    List<EquipmentIssue> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM EquipmentIssue e WHERE e.status = :status AND e.createdAt >= :startDate")
    List<EquipmentIssue> findByStatusAndCreatedAfter(@Param("status") EquipmentIssue.IssueStatus status,
                                                   @Param("startDate") LocalDateTime startDate);

    long countByStatus(EquipmentIssue.IssueStatus status);

    long countByEquipmentType(EquipmentIssue.EquipmentType equipmentType);
}