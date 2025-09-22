package com.sahabatquran.webapp.controller;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.nio.file.Path;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;

import com.sahabatquran.webapp.dto.ReportGenerationJobDto;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.repository.LevelRepository;
import com.sahabatquran.webapp.repository.ClassGroupRepository;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.service.ReportOrchestrationService;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simplified Student Report Controller
 *
 * Unified approach:
 * - Single generate button that processes all students in background
 * - Status dashboard showing progress/state without real-time updates
 * - Regenerate functionality for individual students
 * - All downloads fetch pre-generated PDF files
 */
@Slf4j
@Controller
@RequestMapping("/report-cards")
@RequiredArgsConstructor
public class StudentReportController {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final ClassGroupRepository classGroupRepository;
    private final AcademicTermRepository academicTermRepository;
    private final ReportOrchestrationService reportOrchestrationService;

    /**
     * Main student reports page with filters and selection
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String studentReports(@RequestParam(required = false) UUID classId,
                               @RequestParam(required = false) UUID levelId,
                               @RequestParam(required = false) UUID filterTermId,
                               Model model) {
        log.info("Accessing student reports page");

        try {
            model.addAttribute("pageTitle", "Student Reports");
            model.addAttribute("levels", levelRepository.findByOrderByOrderNumber());
            model.addAttribute("classes", classGroupRepository.findByIsActive(true));
            model.addAttribute("terms", academicTermRepository.findAllOrderByStartDateDesc());

            // Apply basic filtering for students
            List<User> students = userRepository.findStudents();
            model.addAttribute("students", students);

            // Filter values for form state
            model.addAttribute("selectedClassId", classId);
            model.addAttribute("selectedLevelId", levelId);
            model.addAttribute("selectedFilterTermId", filterTermId);

            log.info("Loaded {} students for display", students.size());
        } catch (Exception e) {
            log.error("Error loading data for student reports", e);
            model.addAttribute("levels", List.of());
            model.addAttribute("classes", List.of());
            model.addAttribute("terms", List.of());
            model.addAttribute("students", List.of());
        }

        return "reports/student-reports";
    }

    /**
     * Single Generate All Reports Button
     * Starts background generation for all students in a term
     */
    @PostMapping("/generate-all")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String generateAllReports(@RequestParam UUID termId,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            log.info("Starting generation of all student reports for term: {}", termId);

            // Get current user
            String username = request.getUserPrincipal().getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Current user not found"));

            // Get term details
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new IllegalArgumentException("Term not found: " + termId));

            // Start background generation via orchestration service
            UUID batchId = reportOrchestrationService.generateAllStudentReports(termId, currentUser.getId(), term.getTermName());

            redirectAttributes.addFlashAttribute("successMessage",
                "Report generation started for all students in " + term.getTermName() +
                ". Check the status dashboard for progress.");
            redirectAttributes.addAttribute("batchId", batchId);

            return "redirect:/report-cards/status";
        } catch (Exception e) {
            log.error("Error starting report generation for term: {}", termId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                "Failed to start report generation: " + e.getMessage());
            return "redirect:/report-cards";
        }
    }

    /**
     * Regenerate Single Student Report
     * Deletes old report and generates new one
     */
    @PostMapping("/regenerate")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String regenerateStudentReport(@RequestParam UUID studentId,
                                        @RequestParam UUID termId,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        try {
            log.info("Regenerating report for student: {} in term: {}", studentId, termId);

            // Get current user
            String username = request.getUserPrincipal().getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Current user not found"));

            // Start regeneration via orchestration service
            UUID batchId = reportOrchestrationService.regenerateStudentReport(studentId, termId, currentUser.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                "Report regeneration started. Check the status dashboard for progress.");
            redirectAttributes.addAttribute("batchId", batchId);

            return "redirect:/report-cards/status";
        } catch (Exception e) {
            log.error("Error regenerating report for student: {} in term: {}", studentId, termId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                "Failed to regenerate report: " + e.getMessage());
            return "redirect:/report-cards";
        }
    }

    /**
     * Report Generation Status Dashboard
     * Shows progress/state without real-time updates
     */
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public String reportStatus(@RequestParam(required = false) UUID batchId, Model model) {
        log.info("Accessing report generation status dashboard");

        // Get all batch statuses for display
        List<ReportGenerationJobDto> allJobs = reportOrchestrationService.getAllBatchStatuses();
        log.info("Found {} batch jobs for status dashboard", allJobs.size());

        if (allJobs.isEmpty()) {
            log.warn("No batch jobs found - this may indicate database or repository issues");
        } else {
            log.info("Batch jobs found: {}", allJobs.stream().map(job -> job.getBatchId() + " - " + job.getStatus()).toList());
        }

        model.addAttribute("reportJobs", allJobs);

        // If specific batch requested, highlight it
        if (batchId != null) {
            model.addAttribute("highlightBatchId", batchId);
        }

        model.addAttribute("pageTitle", "Report Generation Status");

        return "reports/status-dashboard";
    }

    /**
     * Download Pre-Generated PDF Report
     * Fetches pre-generated PDF files from file system
     */
    @GetMapping("/download-pdf")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public ResponseEntity<Resource> downloadPdf(@RequestParam UUID studentId,
                                              @RequestParam UUID termId) {
        try {
            log.info("Downloading PDF report for student: {} in term: {}", studentId, termId);

            // Get pre-generated report path
            Optional<Path> reportPath = reportOrchestrationService.getGeneratedReportPath(studentId, termId);

            if (reportPath.isEmpty()) {
                log.warn("Pre-generated report not found for student: {} in term: {}", studentId, termId);
                return ResponseEntity.notFound().build();
            }

            // Create resource from file
            Resource resource = new UrlResource(reportPath.get().toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("Report file not readable: {}", reportPath.get());
                return ResponseEntity.notFound().build();
            }

            // Get student and term for filename
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new IllegalArgumentException("Term not found"));

            String filename = String.format("Report_Card_%s_%s.pdf",
                    student.getFullName().replaceAll("[^a-zA-Z0-9]", "_"),
                    term.getTermName().replaceAll("[^a-zA-Z0-9]", "_"));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading PDF for student: {} in term: {}", studentId, termId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Check if report exists for download
     */
    @GetMapping("/check-report")
    @PreAuthorize("hasAuthority('REPORT_CARD_VIEW')")
    public ResponseEntity<Boolean> checkReportExists(@RequestParam UUID studentId,
                                                   @RequestParam UUID termId) {
        boolean exists = reportOrchestrationService.hasGeneratedReport(studentId, termId);
        return ResponseEntity.ok(exists);
    }
}