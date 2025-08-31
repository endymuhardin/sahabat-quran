package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ClassGenerationLog;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.GeneratedClassProposal;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClassGenerationLogRepository extends JpaRepository<ClassGenerationLog, UUID> {
    
    List<ClassGenerationLog> findByTerm(AcademicTerm term);
    
    List<ClassGenerationLog> findByProposal(GeneratedClassProposal proposal);
    
    List<ClassGenerationLog> findByActionType(ClassGenerationLog.ActionType actionType);
    
    List<ClassGenerationLog> findByPerformedBy(User performedBy);
    
    @Query("SELECT cgl FROM ClassGenerationLog cgl " +
           "WHERE cgl.term.id = :termId ORDER BY cgl.performedAt DESC")
    List<ClassGenerationLog> findByTermOrderByPerformedAtDesc(@Param("termId") UUID termId);
    
    @Query("SELECT cgl FROM ClassGenerationLog cgl " +
           "WHERE cgl.proposal.id = :proposalId ORDER BY cgl.performedAt DESC")
    List<ClassGenerationLog> findByProposalOrderByPerformedAtDesc(@Param("proposalId") UUID proposalId);
    
    @Query("SELECT cgl FROM ClassGenerationLog cgl " +
           "WHERE cgl.performedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY cgl.performedAt DESC")
    List<ClassGenerationLog> findByPerformedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cgl FROM ClassGenerationLog cgl " +
           "WHERE cgl.term.id = :termId AND cgl.actionType = :actionType " +
           "ORDER BY cgl.performedAt DESC")
    List<ClassGenerationLog> findByTermAndActionType(
            @Param("termId") UUID termId,
            @Param("actionType") ClassGenerationLog.ActionType actionType);
    
    @Query("SELECT cgl FROM ClassGenerationLog cgl " +
           "WHERE cgl.performedBy.id = :userId AND cgl.term.id = :termId " +
           "ORDER BY cgl.performedAt DESC")
    List<ClassGenerationLog> findByUserAndTerm(
            @Param("userId") UUID userId,
            @Param("termId") UUID termId);
    
    @Query("SELECT cgl.actionType, COUNT(cgl) FROM ClassGenerationLog cgl " +
           "WHERE cgl.term.id = :termId " +
           "GROUP BY cgl.actionType ORDER BY COUNT(cgl) DESC")
    List<Object[]> findActionTypeCountsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT cgl.performedBy, COUNT(cgl) FROM ClassGenerationLog cgl " +
           "WHERE cgl.term.id = :termId " +
           "GROUP BY cgl.performedBy ORDER BY COUNT(cgl) DESC")
    List<Object[]> findUserActivityCountsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT DATE(cgl.performedAt) as logDate, COUNT(cgl) FROM ClassGenerationLog cgl " +
           "WHERE cgl.term.id = :termId AND cgl.performedAt >= :fromDate " +
           "GROUP BY DATE(cgl.performedAt) ORDER BY logDate DESC")
    List<Object[]> findDailyActivityCountsByTerm(
            @Param("termId") UUID termId,
            @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT cgl FROM ClassGenerationLog cgl " +
           "WHERE cgl.term.id = :termId AND cgl.actionType IN ('GENERATION', 'APPROVAL') " +
           "ORDER BY cgl.performedAt DESC")
    List<ClassGenerationLog> findMajorActionsByTerm(@Param("termId") UUID termId);
}