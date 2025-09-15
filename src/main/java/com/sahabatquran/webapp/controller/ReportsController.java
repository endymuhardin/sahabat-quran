package com.sahabatquran.webapp.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import com.sahabatquran.webapp.repository.EnrollmentRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reports Controller
 *
 * Handles general reports functionality and redirects for backwards compatibility.
 */
@Slf4j
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('REPORT_OPERATIONAL', 'REPORT_ACADEMIC', 'REPORT_FINANCIAL')")
    public String studentReportAdmin(@PathVariable UUID studentId, Model model) {
        log.info("Admin accessing student report for ID: {}", studentId);

        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new AccessDeniedException("Student not found"));

        model.addAttribute("studentId", studentId);
        model.addAttribute("student", student);
        model.addAttribute("pageTitle", "Student Report");

        return "reports/student-detail";
    }

    @GetMapping("/instructor/student/{studentId}")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String studentReportInstructor(@PathVariable UUID studentId, Model model, Authentication authentication) {
        log.info("Instructor attempting to access student report for ID: {}", studentId);

        var currentUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new AccessDeniedException("User not found"));

        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new AccessDeniedException("Student not found"));

        // Check if instructor teaches this student
        boolean canAccessStudent = enrollmentRepository.existsByStudentIdAndInstructorId(
            studentId, currentUser.getId());

        if (!canAccessStudent) {
            log.warn("Access denied: Instructor {} cannot access student report for ID: {}",
                authentication.getName(), studentId);
            throw new AccessDeniedException("You don't have permission to access this student's report");
        }

        model.addAttribute("studentId", studentId);
        model.addAttribute("student", student);
        model.addAttribute("pageTitle", "Student Report");

        return "reports/student-detail";
    }

    @GetMapping("/email/preview/{studentId}")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW') or hasAnyAuthority('REPORT_OPERATIONAL', 'REPORT_ACADEMIC', 'REPORT_FINANCIAL')")
    public String emailPreview(@PathVariable UUID studentId, Model model) {
        log.info("Previewing email for student ID: {}", studentId);

        StudentReportEmailDto emailData = emailService.createSampleEmailData(studentId.toString());
        String emailContent = emailService.generateEmailContent(emailData);

        model.addAttribute("emailData", emailData);
        model.addAttribute("emailContent", emailContent);
        model.addAttribute("pageTitle", "Email Preview - " + emailData.getStudentName());

        return "reports/email-preview";
    }

    @PostMapping("/email/send/{studentId}")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW') or hasAnyAuthority('REPORT_OPERATIONAL', 'REPORT_ACADEMIC', 'REPORT_FINANCIAL')")
    @ResponseBody
    public String sendEmail(@PathVariable UUID studentId,
                           @RequestParam String recipientEmail,
                           @RequestParam String recipientName) {
        log.info("Sending email for student ID: {} to: {}", studentId, recipientEmail);

        try {
            StudentReportEmailDto emailData = emailService.createSampleEmailData(studentId.toString());
            emailData.setRecipientEmail(recipientEmail);
            emailData.setRecipientName(recipientName);

            emailService.sendStudentReportEmail(emailData);

            return "success";
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return "error: " + e.getMessage();
        }
    }
}