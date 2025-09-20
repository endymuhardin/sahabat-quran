package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ReportGenerationItem;
import com.sahabatquran.webapp.entity.ReportGenerationBatch;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportGenerationItemRepository extends JpaRepository<ReportGenerationItem, UUID> {

    List<ReportGenerationItem> findByBatchOrderByPriorityAscStartedAtAsc(ReportGenerationBatch batch);

    List<ReportGenerationItem> findByBatchAndStatusOrderByPriorityAsc(
            ReportGenerationBatch batch,
            ReportGenerationItem.ItemStatus status);

    List<ReportGenerationItem> findByStudentOrderByBatchInitiatedAtDesc(User student);

    @Query("SELECT i FROM ReportGenerationItem i WHERE i.batch.id = :batchId ORDER BY i.priority ASC, i.startedAt ASC")
    List<ReportGenerationItem> findByBatchIdOrderByPriority(@Param("batchId") UUID batchId);

    @Query("SELECT i FROM ReportGenerationItem i WHERE i.batch.id = :batchId AND i.status = 'PENDING' ORDER BY i.priority ASC")
    List<ReportGenerationItem> findPendingItemsByBatch(@Param("batchId") UUID batchId);

    @Query("SELECT i FROM ReportGenerationItem i WHERE i.batch.id = :batchId AND i.status = 'FAILED' AND i.retryCount < 3")
    List<ReportGenerationItem> findRetryableFailedItems(@Param("batchId") UUID batchId);

    @Query("SELECT COUNT(i) FROM ReportGenerationItem i WHERE i.batch.id = :batchId AND i.status = :status")
    Long countByBatchAndStatus(@Param("batchId") UUID batchId, @Param("status") ReportGenerationItem.ItemStatus status);

    @Query("SELECT i FROM ReportGenerationItem i WHERE i.distributed = false AND i.status = 'COMPLETED'")
    List<ReportGenerationItem> findCompletedItemsAwaitingDistribution();

    @Query("SELECT i FROM ReportGenerationItem i WHERE i.student.id = :studentId AND i.batch.term.id = :termId ORDER BY i.batch.initiatedAt DESC")
    List<ReportGenerationItem> findByStudentAndTerm(@Param("studentId") UUID studentId, @Param("termId") UUID termId);

    // Statistics queries
    @Query("SELECT COUNT(i), i.status FROM ReportGenerationItem i WHERE i.batch.id = :batchId GROUP BY i.status")
    List<Object[]> getItemStatusStatistics(@Param("batchId") UUID batchId);

    @Query("SELECT AVG(i.processingDurationSeconds) FROM ReportGenerationItem i WHERE i.processingDurationSeconds IS NOT NULL AND i.batch.reportType = :reportType")
    Double getAverageProcessingTimeByReportType(@Param("reportType") ReportGenerationBatch.ReportType reportType);

    @Query("SELECT i FROM ReportGenerationItem i WHERE i.batch.status = 'IN_PROGRESS' AND i.status = 'PENDING' ORDER BY i.priority ASC, i.batch.initiatedAt ASC")
    List<ReportGenerationItem> findNextItemsToProcess();
}