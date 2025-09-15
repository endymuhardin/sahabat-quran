package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.AttendanceDiscrepancyDto;
import com.sahabatquran.webapp.entity.GuestStudent;
import com.sahabatquran.webapp.service.AttendanceDiscrepancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance-discrepancy")
@RequiredArgsConstructor
@Slf4j
public class AttendanceDiscrepancyController {

    private final AttendanceDiscrepancyService attendanceDiscrepancyService;

    @PostMapping("/check")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<AttendanceDiscrepancyDto> checkAttendanceDiscrepancy(
            @RequestParam UUID sessionId,
            @RequestParam int actualPresentCount) {

        try {
            log.info("Checking attendance discrepancy: session={}, presentCount={}",
                    sessionId, actualPresentCount);

            AttendanceDiscrepancyDto discrepancy = attendanceDiscrepancyService
                    .checkAttendanceDiscrepancy(sessionId, actualPresentCount);

            return ResponseEntity.ok(discrepancy);

        } catch (Exception e) {
            log.error("Failed to check attendance discrepancy", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add-guest-student")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Map<String, Object>> addGuestStudent(
            @RequestParam UUID sessionId,
            @RequestParam String guestName,
            @RequestParam String reason,
            @RequestParam String guestType,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            log.info("Adding guest student: session={}, name={}, type={}, user={}",
                    sessionId, guestName, guestType, userDetails.getUsername());

            GuestStudent.GuestType type = GuestStudent.GuestType.valueOf(guestType);
            GuestStudent guestStudent = attendanceDiscrepancyService.addGuestStudent(
                    sessionId, guestName, reason, type, userDetails.getUsername());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Guest student added successfully",
                    "guestStudentId", guestStudent.getId(),
                    "guestName", guestStudent.getGuestName(),
                    "adminNotified", true
            ));

        } catch (Exception e) {
            log.error("Failed to add guest student", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to add guest student: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/session/{sessionId}/guest-count")
    @PreAuthorize("hasAnyAuthority('INSTRUCTOR', 'ACADEMIC_ADMIN', 'MANAGEMENT')")
    public ResponseEntity<Map<String, Object>> getGuestStudentCount(@PathVariable UUID sessionId) {
        long guestCount = attendanceDiscrepancyService.getGuestStudentCountForSession(sessionId);
        int adjustedTotal = attendanceDiscrepancyService.getAdjustedAttendanceCount(sessionId);

        return ResponseEntity.ok(Map.of(
                "guestStudentCount", guestCount,
                "adjustedTotalAttendance", adjustedTotal
        ));
    }
}