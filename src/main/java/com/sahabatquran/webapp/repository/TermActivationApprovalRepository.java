package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.TermActivationApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TermActivationApprovalRepository extends JpaRepository<TermActivationApproval, UUID> {
    Optional<TermActivationApproval> findByTermId(UUID termId);
}
