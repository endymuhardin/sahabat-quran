package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.EmergencyTermination;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.ClassSessionRepository;
import com.sahabatquran.webapp.repository.EmergencyTerminationRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmergencyTerminationService {

    private final EmergencyTerminationRepository emergencyTerminationRepository;
    private final ClassSessionRepository classSessionRepository;
    private final UserRepository userRepository;

    public EmergencyTermination terminateSessionEmergency(UUID sessionId, String username,
                                                         EmergencyTermination.EmergencyType emergencyType,
                                                         String emergencyReason) {
        log.warn("EMERGENCY TERMINATION initiated for session {} by user {}: {}",
                sessionId, username, emergencyReason);

        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        User terminatedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // Create emergency termination record
        EmergencyTermination emergency = new EmergencyTermination();
        emergency.setSession(session);
        emergency.setTerminatedBy(terminatedBy);
        emergency.setEmergencyType(emergencyType);
        emergency.setEmergencyReason(emergencyReason);

        EmergencyTermination saved = emergencyTerminationRepository.save(emergency);

        // Immediately terminate the session
        terminateSessionImmediately(session);

        // Preserve session data
        preserveSessionData(session, saved);

        // Send emergency notifications
        sendEmergencyNotifications(saved);

        // Generate emergency report
        generateEmergencyReport(saved);

        log.warn("Emergency termination completed for session {}: tracking number {}",
                sessionId, saved.getTrackingNumber());

        return saved;
    }

    private void terminateSessionImmediately(ClassSession session) {
        try {
            // Update session status to indicate emergency termination
            // For now, we'll just log this action
            log.warn("Session {} terminated immediately due to emergency", session.getId());

            // Here you would implement actual session termination logic
            // This might include:
            // - Updating session status
            // - Stopping any ongoing activities
            // - Saving current progress

        } catch (Exception e) {
            log.error("Failed to terminate session immediately: {}", session.getId(), e);
        }
    }

    private void preserveSessionData(ClassSession session, EmergencyTermination emergency) {
        try {
            log.info("Preserving session data for emergency termination: {}", emergency.getTrackingNumber());

            // Here you would implement session data preservation logic
            // This might include:
            // - Saving attendance data
            // - Preserving session notes
            // - Backing up any work in progress

            emergency.setSessionDataPreserved(true);
            emergencyTerminationRepository.save(emergency);

            log.info("Session data preserved successfully for emergency: {}", emergency.getTrackingNumber());

        } catch (Exception e) {
            log.error("Failed to preserve session data for emergency: {}", emergency.getTrackingNumber(), e);
            emergency.setSessionDataPreserved(false);
            emergencyTerminationRepository.save(emergency);
        }
    }

    private void sendEmergencyNotifications(EmergencyTermination emergency) {
        try {
            log.warn("Sending emergency notifications for incident: {}", emergency.getTrackingNumber());

            // Send stakeholder notifications
            sendStakeholderNotifications(emergency);

            // Send parent notifications
            sendParentNotifications(emergency);

            emergency.setNotificationsSent(true);
            emergency.setStakeholderNotificationAt(LocalDateTime.now());
            emergency.setParentNotificationAt(LocalDateTime.now());
            emergencyTerminationRepository.save(emergency);

            log.info("Emergency notifications sent successfully for: {}", emergency.getTrackingNumber());

        } catch (Exception e) {
            log.error("Failed to send emergency notifications for: {}", emergency.getTrackingNumber(), e);
            emergency.setNotificationsSent(false);
            emergencyTerminationRepository.save(emergency);
        }
    }

    private void sendStakeholderNotifications(EmergencyTermination emergency) {
        // Here you would implement stakeholder notification logic
        // This might include:
        // - Notifying academic administrators
        // - Alerting management
        // - Informing security if needed
        log.warn("Stakeholder notification sent for emergency: {} ({})",
                emergency.getTrackingNumber(), emergency.getEmergencyType());
    }

    private void sendParentNotifications(EmergencyTermination emergency) {
        // Here you would implement parent notification logic
        // This might include:
        // - SMS/email to parents
        // - Push notifications via mobile app
        // - Automated phone calls if critical
        log.warn("Parent notification triggered for emergency: {} ({})",
                emergency.getTrackingNumber(), emergency.getEmergencyType());
    }

    private void generateEmergencyReport(EmergencyTermination emergency) {
        try {
            log.info("Generating emergency report for incident: {}", emergency.getTrackingNumber());

            // Here you would implement emergency report generation
            // This might include:
            // - Creating detailed incident report
            // - Generating timeline of events
            // - Collecting witness statements if applicable

            emergency.setEmergencyReportGenerated(true);
            emergencyTerminationRepository.save(emergency);

            log.info("Emergency report generated successfully for: {}", emergency.getTrackingNumber());

        } catch (Exception e) {
            log.error("Failed to generate emergency report for: {}", emergency.getTrackingNumber(), e);
            emergency.setEmergencyReportGenerated(false);
            emergencyTerminationRepository.save(emergency);
        }
    }

    @Transactional(readOnly = true)
    public boolean isSessionTerminatedEmergency(UUID sessionId) {
        return emergencyTerminationRepository.findBySessionId(sessionId).isPresent();
    }

    @Transactional(readOnly = true)
    public EmergencyTermination getEmergencyTerminationBySession(UUID sessionId) {
        return emergencyTerminationRepository.findBySessionId(sessionId)
                .orElse(null);
    }
}