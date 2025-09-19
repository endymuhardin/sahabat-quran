package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ResourceAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, UUID> {
    Optional<ResourceAllocation> findByTermId(UUID termId);
}
