package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ReportGenerationBatch;
import com.sahabatquran.webapp.entity.AcademicTerm;
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
public interface ReportGenerationBatchRepository extends JpaRepository<ReportGenerationBatch, UUID> {

    List<ReportGenerationBatch> findByTermOrderByInitiatedAtDesc(AcademicTerm term);

    List<ReportGenerationBatch> findByInitiatedByOrderByInitiatedAtDesc(User initiatedBy);

    List<ReportGenerationBatch> findByStatusOrderByInitiatedAtDesc(ReportGenerationBatch.BatchStatus status);

    List<ReportGenerationBatch> findByStatusInOrderByInitiatedAtDesc(List<ReportGenerationBatch.BatchStatus> statuses);

    @Query("SELECT b FROM ReportGenerationBatch b WHERE b.term.id = :termId ORDER BY b.initiatedAt DESC")
    List<ReportGenerationBatch> findByTermIdOrderByInitiatedAtDesc(@Param("termId") UUID termId);

    @Query("SELECT b FROM ReportGenerationBatch b WHERE b.status = :status AND b.initiatedAt >= :since")
    List<ReportGenerationBatch> findRecentBatchesByStatus(
            @Param("status") ReportGenerationBatch.BatchStatus status,
            @Param("since") LocalDateTime since);

    default List<ReportGenerationBatch> findActiveBatches() {
        return findByStatusInOrderByInitiatedAtDesc(
            List.of(ReportGenerationBatch.BatchStatus.IN_PROGRESS,
                   ReportGenerationBatch.BatchStatus.VALIDATING,
                   ReportGenerationBatch.BatchStatus.INITIATED)
        );
    }

    @Query("SELECT COUNT(b) FROM ReportGenerationBatch b WHERE b.term.id = :termId AND b.status = com.sahabatquran.webapp.entity.ReportGenerationBatch$BatchStatus.COMPLETED")
    Long countCompletedBatchesForTerm(@Param("termId") UUID termId);

    @Query("SELECT b FROM ReportGenerationBatch b WHERE b.term.id = :termId AND b.reportType = :reportType ORDER BY b.initiatedAt DESC")
    List<ReportGenerationBatch> findByTermAndReportType(
            @Param("termId") UUID termId,
            @Param("reportType") ReportGenerationBatch.ReportType reportType);

    @Query("SELECT b FROM ReportGenerationBatch b " +
           "LEFT JOIN FETCH b.reportGenerationItems " +
           "WHERE b.id = :batchId")
    Optional<ReportGenerationBatch> findByIdWithItems(@Param("batchId") UUID batchId);

    default List<ReportGenerationBatch> findCompletedBatchesAwaitingDistribution() {
        return findByStatusOrderByInitiatedAtDesc(ReportGenerationBatch.BatchStatus.COMPLETED)
                .stream()
                .filter(batch -> !batch.getDistributionCompleted())
                .toList();
    }

    // Statistics queries
    @Query("SELECT COUNT(b), b.status FROM ReportGenerationBatch b WHERE b.term.id = :termId GROUP BY b.status")
    List<Object[]> getBatchStatusStatistics(@Param("termId") UUID termId);

    @Query("SELECT AVG(b.actualDurationMinutes) FROM ReportGenerationBatch b WHERE b.actualDurationMinutes IS NOT NULL AND b.reportType = :reportType")
    Double getAverageDurationByReportType(@Param("reportType") ReportGenerationBatch.ReportType reportType);
}