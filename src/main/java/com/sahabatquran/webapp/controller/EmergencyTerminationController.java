package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.entity.EmergencyTermination;
import com.sahabatquran.webapp.service.EmergencyTerminationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/emergency-termination")
@RequiredArgsConstructor
@Slf4j
public class EmergencyTerminationController {

    private final EmergencyTerminationService emergencyTerminationService;

    @PostMapping("/terminate")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Map<String, Object>> terminateSessionEmergency(
            @RequestParam UUID sessionId,
            @RequestParam String emergencyType,
            @RequestParam String emergencyReason,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            log.warn("Emergency termination request: session={}, type={}, user={}",
                    sessionId, emergencyType, userDetails.getUsername());

            EmergencyTermination.EmergencyType type = EmergencyTermination.EmergencyType.valueOf(emergencyType);
            EmergencyTermination emergency = emergencyTerminationService.terminateSessionEmergency(
                    sessionId, userDetails.getUsername(), type, emergencyReason);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Session terminated due to emergency",
                    "trackingNumber", emergency.getTrackingNumber(),
                    "emergencyId", emergency.getId(),
                    "notificationsSent", emergency.getNotificationsSent(),
                    "sessionDataPreserved", emergency.getSessionDataPreserved(),
                    "emergencyReportGenerated", emergency.getEmergencyReportGenerated()
            ));

        } catch (Exception e) {
            log.error("Failed to terminate session due to emergency", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to terminate session: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/session/{sessionId}/status")
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ACADEMIC_ADMIN', 'MANAGEMENT')")
    public ResponseEntity<Map<String, Object>> getEmergencyTerminationStatus(@PathVariable UUID sessionId) {
        boolean isTerminated = emergencyTerminationService.isSessionTerminatedEmergency(sessionId);
        EmergencyTermination emergency = emergencyTerminationService.getEmergencyTerminationBySession(sessionId);

        if (emergency != null) {
            return ResponseEntity.ok(Map.of(
                    "isEmergencyTerminated", true,
                    "trackingNumber", emergency.getTrackingNumber(),
                    "emergencyType", emergency.getEmergencyType().toString(),
                    "notificationsSent", emergency.getNotificationsSent(),
                    "sessionDataPreserved", emergency.getSessionDataPreserved(),
                    "emergencyReportGenerated", emergency.getEmergencyReportGenerated()
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "isEmergencyTerminated", false
            ));
        }
    }
}