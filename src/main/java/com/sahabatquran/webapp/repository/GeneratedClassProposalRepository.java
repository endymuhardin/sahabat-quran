package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.GeneratedClassProposal;
import com.sahabatquran.webapp.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedClassProposalRepository extends JpaRepository<GeneratedClassProposal, UUID> {
    
    List<GeneratedClassProposal> findByTerm(AcademicTerm term);
    
    List<GeneratedClassProposal> findByIsApproved(Boolean isApproved);
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp WHERE gcp.term.id = :termId ORDER BY gcp.generatedAt DESC")
    List<GeneratedClassProposal> findByTermOrderByGeneratedAtDesc(@Param("termId") UUID termId);
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp " +
           "WHERE gcp.term.id = :termId AND gcp.isApproved = true")
    Optional<GeneratedClassProposal> findApprovedProposalByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp " +
           "WHERE gcp.term.id = :termId AND gcp.generationRun = :generationRun")
    List<GeneratedClassProposal> findByTermAndGenerationRun(
            @Param("termId") UUID termId, 
            @Param("generationRun") Integer generationRun);
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp " +
           "WHERE gcp.term.id = :termId ORDER BY gcp.optimizationScore DESC")
    List<GeneratedClassProposal> findByTermOrderByOptimizationScoreDesc(@Param("termId") UUID termId);
    
    @Query("SELECT MAX(gcp.generationRun) FROM GeneratedClassProposal gcp WHERE gcp.term.id = :termId")
    Optional<Integer> findMaxGenerationRunByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp " +
           "WHERE gcp.generatedAt BETWEEN :startDate AND :endDate")
    List<GeneratedClassProposal> findByGeneratedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp " +
           "WHERE gcp.conflictCount > 0 ORDER BY gcp.conflictCount DESC")
    List<GeneratedClassProposal> findProposalsWithConflicts();
    
    @Query("SELECT gcp FROM GeneratedClassProposal gcp " +
           "LEFT JOIN FETCH gcp.term " +
           "LEFT JOIN FETCH gcp.generatedBy " +
           "WHERE gcp.id = :proposalId")
    Optional<GeneratedClassProposal> findByIdWithTermAndUser(@Param("proposalId") UUID proposalId);
    
    @Query("SELECT COUNT(gcp) FROM GeneratedClassProposal gcp WHERE gcp.term.id = :termId")
    Long countProposalsByTerm(@Param("termId") UUID termId);
}