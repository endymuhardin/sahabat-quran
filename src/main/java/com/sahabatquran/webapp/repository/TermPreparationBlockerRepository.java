package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.TermPreparationBlocker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TermPreparationBlockerRepository extends JpaRepository<TermPreparationBlocker, UUID> {
    List<TermPreparationBlocker> findByTermId(UUID termId);
    long countByTermIdAndStatus(UUID termId, TermPreparationBlocker.BlockerStatus status);
}
