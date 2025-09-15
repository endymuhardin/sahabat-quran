package com.sahabatquran.webapp.controller;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;

import com.sahabatquran.webapp.dto.AcademicTranscriptDto;
import com.sahabatquran.webapp.dto.StudentReportEmailDto;
import com.sahabatquran.webapp.dto.TranscriptRequestDto;
import com.sahabatquran.webapp.repository.EnrollmentRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.repository.LevelRepository;
import com.sahabatquran.webapp.repository.ClassGroupRepository;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.service.EmailService;
import com.sahabatquran.webapp.service.TranscriptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Student Report Controller
 *
 * Handles student report card generation, transcript creation, and academic record management.
 * Provides endpoints for individual and bulk report generation.
 */
@Slf4j
@Controller
@RequestMapping("/report-cards")
@RequiredArgsConstructor
public class StudentReportController {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TranscriptService transcriptService;
    private final LevelRepository levelRepository;
    private final ClassGroupRepository classGroupRepository;
    private final AcademicTermRepository academicTermRepository;

    @GetMapping("")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String studentReports(Model model) {
        log.info("Accessing student reports page");

        // Add filter data from database
        model.addAttribute("pageTitle", "Student Reports");
        model.addAttribute("levels", levelRepository.findByOrderByOrderNumber());
        model.addAttribute("classes", classGroupRepository.findByIsActive(true));
        model.addAttribute("terms", academicTermRepository.findAllOrderByStartDateDesc());

        return "reports/student-reports";
    }

    @GetMapping("/transcripts")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String transcripts(Model model) {
        log.info("Accessing transcripts page");

        // Add any necessary model attributes
        model.addAttribute("pageTitle", "Academic Transcripts");
        model.addAttribute("levels", levelRepository.findByOrderByOrderNumber());
        model.addAttribute("terms", academicTermRepository.findAllOrderByStartDateDesc());

        return "reports/transcripts";
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW') or hasAnyAuthority('REPORT_OPERATIONAL', 'REPORT_ACADEMIC', 'REPORT_FINANCIAL')")
    public String studentReport(@PathVariable UUID studentId, Model model, Authentication authentication) {
        log.info("Attempting to access student report for ID: {}", studentId);

        // Get student entity
        var student = userRepository.findById(studentId)
            .orElseThrow(() -> new AccessDeniedException("Student not found"));

        // Check if user has admin access
        boolean hasAdminAccess = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("REPORT_OPERATIONAL") ||
                grantedAuthority.getAuthority().equals("REPORT_ACADEMIC") ||
                grantedAuthority.getAuthority().equals("REPORT_FINANCIAL"));

        // For non-admin users (instructors), check if they can access this student
        if (!hasAdminAccess) {
            var currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));

            boolean canAccessStudent = enrollmentRepository.existsByStudentIdAndInstructorId(
                studentId, currentUser.getId());

            if (!canAccessStudent) {
                log.warn("Access denied: Instructor {} cannot access student report for ID: {}",
                    authentication.getName(), studentId);
                throw new AccessDeniedException("You don't have permission to access this student's report");
            }
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

    // ====================== TRANSCRIPT ENDPOINTS ======================

    @PostMapping("/api/transcripts/generate")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    @ResponseBody
    public AcademicTranscriptDto generateTranscript(@Valid @RequestBody TranscriptRequestDto request) {
        log.info("Generating transcript for student: {} with format: {}",
                request.getStudentId(), request.getTranscriptFormat());

        try {
            return transcriptService.generateTranscript(request);
        } catch (Exception e) {
            log.error("Failed to generate transcript", e);
            throw e;
        }
    }

    @GetMapping("/api/transcripts/student/{studentId}")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    @ResponseBody
    public AcademicTranscriptDto getTranscript(@PathVariable UUID studentId,
                                              @RequestParam(defaultValue = "OFFICIAL_TRANSCRIPT") String format) {
        log.info("Retrieving transcript for student: {} with format: {}", studentId, format);

        return transcriptService.generateTranscript(studentId, format);
    }

    @GetMapping("/api/transcripts/student/{studentId}/can-generate")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    @ResponseBody
    public boolean canGenerateTranscript(@PathVariable UUID studentId) {
        return transcriptService.canGenerateTranscript(studentId);
    }

    @GetMapping("/api/transcripts/student/{studentId}/formats")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    @ResponseBody
    public String[] getAvailableFormats(@PathVariable UUID studentId) {
        return transcriptService.getAvailableFormats(studentId);
    }
}