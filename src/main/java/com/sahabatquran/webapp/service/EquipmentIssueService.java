package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.EquipmentIssue;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.EquipmentIssueRepository;
import com.sahabatquran.webapp.repository.ClassSessionRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EquipmentIssueService {

    private final EquipmentIssueRepository equipmentIssueRepository;
    private final ClassSessionRepository classSessionRepository;
    private final UserRepository userRepository;

    /**
     * Report a new equipment issue
     */
    public EquipmentIssue reportIssue(UUID sessionId, String username,
                                    EquipmentIssue.EquipmentType equipmentType,
                                    String description, boolean isUrgent) {
        log.info("Reporting equipment issue for session {} by user {}", sessionId, username);

        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        EquipmentIssue issue = new EquipmentIssue();
        issue.setSession(session);
        issue.setReportedBy(user);
        issue.setEquipmentType(equipmentType);
        issue.setIssueDescription(description);
        issue.setIsUrgent(isUrgent);
        issue.setStatus(EquipmentIssue.IssueStatus.REPORTED);

        EquipmentIssue savedIssue = equipmentIssueRepository.save(issue);

        // Notify maintenance team
        notifyMaintenanceTeam(savedIssue);

        log.info("Equipment issue reported successfully: {}", savedIssue.getTrackingNumber());
        return savedIssue;
    }

    /**
     * Notify maintenance team about the issue
     */
    private void notifyMaintenanceTeam(EquipmentIssue issue) {
        try {
            // Update status to indicate maintenance has been notified
            issue.setStatus(EquipmentIssue.IssueStatus.MAINTENANCE_NOTIFIED);
            issue.setMaintenanceNotifiedAt(LocalDateTime.now());
            equipmentIssueRepository.save(issue);

            // Here you would typically send an email or notification
            // For now, we'll just log it
            log.info("Maintenance team notified about equipment issue: {} ({})",
                    issue.getTrackingNumber(), issue.getEquipmentType());

            if (issue.getIsUrgent()) {
                log.warn("URGENT equipment issue reported: {} - {}",
                        issue.getTrackingNumber(), issue.getIssueDescription());
            }

        } catch (Exception e) {
            log.error("Failed to notify maintenance team for issue: {}", issue.getTrackingNumber(), e);
        }
    }

    /**
     * Get all issues for a session
     */
    @Transactional(readOnly = true)
    public List<EquipmentIssue> getIssuesForSession(UUID sessionId) {
        return equipmentIssueRepository.findBySessionId(sessionId);
    }

    /**
     * Get all pending issues
     */
    @Transactional(readOnly = true)
    public List<EquipmentIssue> getPendingIssues() {
        return equipmentIssueRepository.findByStatus(EquipmentIssue.IssueStatus.REPORTED);
    }

    /**
     * Get all urgent issues
     */
    @Transactional(readOnly = true)
    public List<EquipmentIssue> getUrgentIssues() {
        return equipmentIssueRepository.findByIsUrgentTrue();
    }

    /**
     * Resolve an equipment issue
     */
    public EquipmentIssue resolveIssue(UUID issueId, String resolutionNotes) {
        EquipmentIssue issue = equipmentIssueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + issueId));

        issue.setStatus(EquipmentIssue.IssueStatus.RESOLVED);
        issue.setResolutionNotes(resolutionNotes);
        issue.setResolvedAt(LocalDateTime.now());

        EquipmentIssue resolved = equipmentIssueRepository.save(issue);
        log.info("Equipment issue resolved: {}", resolved.getTrackingNumber());

        return resolved;
    }

    /**
     * Check if maintenance team has been notified for an issue
     */
    @Transactional(readOnly = true)
    public boolean isMaintenanceNotified(UUID issueId) {
        return equipmentIssueRepository.findById(issueId)
                .map(issue -> issue.getMaintenanceNotifiedAt() != null)
                .orElse(false);
    }

    /**
     * Get tracking number for an issue
     */
    @Transactional(readOnly = true)
    public String getTrackingNumber(UUID issueId) {
        return equipmentIssueRepository.findById(issueId)
                .map(EquipmentIssue::getTrackingNumber)
                .orElse(null);
    }
}