package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.StudentRegistrationAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRegistrationAuditRepository extends JpaRepository<StudentRegistrationAudit, UUID> {
    
    List<StudentRegistrationAudit> findByRegistrationIdOrderByCreatedAtDesc(UUID registrationId);
    
    List<StudentRegistrationAudit> findByChangedByIdOrderByCreatedAtDesc(UUID changedById);
    
    Page<StudentRegistrationAudit> findByChangeTypeOrderByCreatedAtDesc(
            StudentRegistrationAudit.ChangeType changeType, Pageable pageable);
    
    @Query("SELECT sra FROM StudentRegistrationAudit sra WHERE sra.registration.id = :registrationId AND sra.changeType = :changeType ORDER BY sra.createdAt DESC")
    List<StudentRegistrationAudit> findByRegistrationIdAndChangeType(
            @Param("registrationId") UUID registrationId, 
            @Param("changeType") StudentRegistrationAudit.ChangeType changeType);
    
    @Query("SELECT sra FROM StudentRegistrationAudit sra WHERE sra.createdAt >= :startDate AND sra.createdAt < :endDate ORDER BY sra.createdAt DESC")
    List<StudentRegistrationAudit> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(sra) FROM StudentRegistrationAudit sra WHERE sra.changeType = :changeType AND sra.createdAt >= :startDate")
    long countByChangeTypeAndCreatedAtAfter(
            @Param("changeType") StudentRegistrationAudit.ChangeType changeType, 
            @Param("startDate") LocalDateTime startDate);
}