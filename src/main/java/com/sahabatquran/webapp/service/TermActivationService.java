package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.TermActivationApproval;
import com.sahabatquran.webapp.repository.TermActivationApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermActivationService {

    private final TermActivationApprovalRepository approvalRepository;

    public TermActivationApproval getOrInit(UUID termId) {
        return approvalRepository.findByTermId(termId)
                .orElseGet(() -> approvalRepository.save(TermActivationApproval.builder()
                        .termId(termId)
                        .status(TermActivationApproval.ActivationStatus.PENDING)
                        .build()));
    }

    @Transactional
    public TermActivationApproval updateChecklist(UUID termId,
                                                  boolean teachers,
                                                  boolean students,
                                                  boolean schedules,
                                                  boolean resources,
                                                  boolean systems) {
        TermActivationApproval approval = getOrInit(termId);
        if (approval.getStatus() == TermActivationApproval.ActivationStatus.APPROVED) {
            return approval; // immutable after approval
        }
        approval.setTeachersAssignedConfirmed(teachers);
        approval.setStudentsEnrolledConfirmed(students);
        approval.setSchedulesPlottedConfirmed(schedules);
        approval.setResourcesAllocatedConfirmed(resources);
        approval.setSystemsReadyConfirmed(systems);
        return approvalRepository.save(approval);
    }

    @Transactional
    public TermActivationApproval approve(UUID termId, UUID approverId, String comments) {
        TermActivationApproval approval = getOrInit(termId);
        if (!(approval.isTeachersAssignedConfirmed() && approval.isStudentsEnrolledConfirmed() &&
              approval.isSchedulesPlottedConfirmed() && approval.isResourcesAllocatedConfirmed() &&
              approval.isSystemsReadyConfirmed())) {
            throw new IllegalStateException("Checklist incomplete; cannot approve");
        }
        approval.setStatus(TermActivationApproval.ActivationStatus.APPROVED);
        approval.setApprovedBy(approverId);
        approval.setApprovedAt(Instant.now());
        approval.setApprovalComments(comments);
        return approvalRepository.save(approval);
    }
}
