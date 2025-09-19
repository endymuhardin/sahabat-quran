package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.TermPreparationBlocker;
import com.sahabatquran.webapp.repository.TermPreparationBlockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagementDashboardService {

    private final TermPreparationBlockerRepository blockerRepository;

    public List<TermPreparationBlocker> getBlockers(UUID termId) {
        return blockerRepository.findByTermId(termId);
    }

    public Map<String, Object> getBlockerMetrics(UUID termId) {
        Map<String, Object> metrics = new HashMap<>();
        long open = blockerRepository.countByTermIdAndStatus(termId, TermPreparationBlocker.BlockerStatus.OPEN);
        long inProgress = blockerRepository.countByTermIdAndStatus(termId, TermPreparationBlocker.BlockerStatus.IN_PROGRESS);
        long resolved = blockerRepository.countByTermIdAndStatus(termId, TermPreparationBlocker.BlockerStatus.RESOLVED);
        metrics.put("open", open);
        metrics.put("inProgress", inProgress);
        metrics.put("resolved", resolved);
        long total = open + inProgress + resolved;
        metrics.put("total", total);
        metrics.put("resolutionRate", total == 0 ? 0 : Math.round((resolved * 100.0)/ total));
        return metrics;
    }

    @Transactional
    public TermPreparationBlocker resolveBlocker(UUID blockerId, UUID userId, String notes) {
        TermPreparationBlocker blocker = blockerRepository.findById(blockerId)
                .orElseThrow(() -> new IllegalArgumentException("Blocker not found"));
        blocker.setStatus(TermPreparationBlocker.BlockerStatus.RESOLVED);
        blocker.setResolutionNotes(notes);
        blocker.setResolvedBy(userId);
        blocker.setResolvedAt(Instant.now());
        return blockerRepository.save(blocker);
    }
}
