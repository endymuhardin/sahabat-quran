package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.entity.EquipmentIssue;
import com.sahabatquran.webapp.service.EquipmentIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/instructor/equipment-issue")
@RequiredArgsConstructor
@Slf4j
public class EquipmentIssueController {

    private final EquipmentIssueService equipmentIssueService;

    /**
     * Show equipment issue reporting form
     */
    @GetMapping("/report")
    @PreAuthorize("hasAnyAuthority('TEACHER_AVAILABILITY_SUBMIT', 'STUDENT_REG_VIEW', 'STUDENT_REG_REVIEW', 'STUDENT_REG_EVALUATE', 'PLACEMENT_TEST_EVALUATE')")
    public String showReportForm(Model model) {
        log.info("Equipment issue report form requested");

        // Debug: Check current user's authorities
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("Current user: {}", authentication.getName());
            log.info("User authorities: {}", authentication.getAuthorities());
        } else {
            log.warn("No authentication found");
        }

        // Add mock session data for now
        model.addAttribute("session", Map.of(
            "id", "450e8400-e29b-41d4-a716-446655440001",
            "className", "Tahsin 1 - Kelas Pagi A",
            "time", "08:00-10:00",
            "room", "Ruang A1"
        ));
        log.info("Returning template: instructor/equipment-issue-report");
        return "instructor/equipment-issue-report";
    }

    /**
     * Process equipment issue report
     */
    @PostMapping("/report")
    @PreAuthorize("hasAnyAuthority('TEACHER_AVAILABILITY_SUBMIT', 'STUDENT_REG_VIEW', 'STUDENT_REG_REVIEW', 'STUDENT_REG_EVALUATE', 'PLACEMENT_TEST_EVALUATE')")
    public String reportEquipmentIssue(
            @RequestParam(required = false) UUID sessionId,
            @RequestParam String equipmentType,
            @RequestParam String description,
            @RequestParam(defaultValue = "false") boolean isUrgent,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            log.info("Equipment issue report request: session={}, type={}, urgent={}, user={}",
                    sessionId, equipmentType, isUrgent, userDetails != null ? userDetails.getUsername() : "null");

            EquipmentIssue.EquipmentType type = EquipmentIssue.EquipmentType.valueOf(equipmentType);
            // Use a default UUID if sessionId is null (for testing)
            UUID actualSessionId = sessionId != null ? sessionId : UUID.fromString("450e8400-e29b-41d4-a716-446655440001");
            EquipmentIssue issue = equipmentIssueService.reportIssue(
                    actualSessionId, userDetails.getUsername(), type, description, isUrgent);

            // Add success message and tracking info
            model.addAttribute("issueReported", true);
            model.addAttribute("trackingNumber", issue.getTrackingNumber());
            model.addAttribute("successMessage", "Equipment issue reported successfully. Tracking: " + issue.getTrackingNumber());

            // Add session info back
            model.addAttribute("session", Map.of(
                "id", actualSessionId.toString(),
                "className", "Tahsin 1 - Kelas Pagi A",
                "time", "08:00-10:00",
                "room", "Ruang A1"
            ));

            return "instructor/equipment-issue-report";

        } catch (Exception e) {
            log.error("Failed to report equipment issue", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to report equipment issue: " + e.getMessage());
            return "redirect:/instructor/equipment-issue/report";
        }
    }
}