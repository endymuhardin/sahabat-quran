package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.ResourceAllocation;
import com.sahabatquran.webapp.repository.ResourceAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceAllocationService {

    private final ResourceAllocationRepository allocationRepository;

    public ResourceAllocation getOrCreateDraft(UUID termId, UUID userId) {
        return allocationRepository.findByTermId(termId)
                .orElseGet(() -> allocationRepository.save(ResourceAllocation.builder()
                        .termId(termId)
                        .status(ResourceAllocation.AllocationStatus.DRAFT)
                        .createdBy(userId)
                        .build()));
    }

    @Transactional
    public ResourceAllocation updateDraft(UUID termId, Integer a, Integer b, Double materials, Double tech, Double misc) {
        ResourceAllocation allocation = allocationRepository.findByTermId(termId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found"));
        if (allocation.getStatus() == ResourceAllocation.AllocationStatus.APPROVED) {
            throw new IllegalStateException("Cannot modify approved allocation");
        }
        allocation.setClassroomsBuildingA(a);
        allocation.setClassroomsBuildingB(b);
        allocation.setMaterialsBudget(materials);
        allocation.setTechnologyBudget(tech);
        allocation.setMiscellaneousBudget(misc);
        return allocationRepository.save(allocation);
    }

    @Transactional
    public ResourceAllocation approve(UUID termId, UUID approverId, String notes) {
        ResourceAllocation allocation = allocationRepository.findByTermId(termId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found"));
        allocation.setStatus(ResourceAllocation.AllocationStatus.APPROVED);
        allocation.setApprovedBy(approverId);
        allocation.setApprovedAt(Instant.now());
        allocation.setApprovalNotes(notes);
        return allocationRepository.save(allocation);
    }
}
