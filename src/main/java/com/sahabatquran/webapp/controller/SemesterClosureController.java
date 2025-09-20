package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.BulkReportGenerationDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.ReportGenerationBatch;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.ReportGenerationBatchRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.BulkReportGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for semester closure and report generation operations
 */
@Controller
@RequestMapping("/academic/semester-closure")
@RequiredArgsConstructor
@Slf4j
public class SemesterClosureController {

    private final BulkReportGenerationService bulkReportGenerationService;
    private final ReportGenerationBatchRepository batchRepository;
    private final AcademicTermRepository academicTermRepository;
    private final UserRepository userRepository;

    /**
     * Semester Closure Dashboard
     * GET: /academic/semester-closure
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    public String semesterClosureDashboard(@RequestParam(required = false) UUID termId,
                                         @RequestParam(required = false) UUID batchStarted,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        log.info("🎯 CONTROLLER REACHED: Loading semester closure dashboard for user: {}", userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get active term if not specified
            AcademicTerm selectedTerm;
            if (termId != null) {
                selectedTerm = academicTermRepository.findById(termId)
                        .orElseThrow(() -> new RuntimeException("Term not found"));
            } else {
                List<AcademicTerm> activeTerms = academicTermRepository.findActiveTerms();
                if (activeTerms.isEmpty()) {
                    model.addAttribute("error", "No active term found");
                    return "error/400";
                }
                selectedTerm = activeTerms.get(0);
            }

            // Get available terms
            List<AcademicTerm> availableTerms = academicTermRepository.findActiveTerms();

            // Get existing batches for the term
            List<ReportGenerationBatch> existingBatches = batchRepository.findByTermIdOrderByInitiatedAtDesc(selectedTerm.getId());

            // Get active batches
            List<ReportGenerationBatch> activeBatches = batchRepository.findActiveBatches();

            // Calculate batch counts to avoid complex Thymeleaf expressions
            long completedBatchCount = existingBatches.stream()
                    .filter(batch -> batch.getStatus() == ReportGenerationBatch.BatchStatus.COMPLETED)
                    .count();
            long failedBatchCount = existingBatches.stream()
                    .filter(batch -> batch.getStatus() == ReportGenerationBatch.BatchStatus.FAILED)
                    .count();

            // Handle success message for newly started batch
            if (batchStarted != null) {
                model.addAttribute("successMessage",
                    "Report generation has been started successfully. You can monitor progress below.");
                model.addAttribute("newBatchId", batchStarted);
            }

            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("existingBatches", existingBatches);
            model.addAttribute("activeBatches", activeBatches);
            model.addAttribute("completedBatchCount", completedBatchCount);
            model.addAttribute("failedBatchCount", failedBatchCount);
            model.addAttribute("pageTitle", "Semester Closure");

            return "academic/semester-closure-dashboard";

        } catch (Exception e) {
            log.error("Error loading semester closure dashboard", e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Validate Bulk Report Generation
     * POST: /academic/semester-closure/validate
     */
    @PostMapping("/validate")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    @ResponseBody
    public ResponseEntity<BulkReportGenerationDto> validateBulkReportGeneration(
            @RequestParam UUID termId,
            @RequestBody BulkReportGenerationDto.ReportConfiguration config) {
        log.info("Validating bulk report generation for term: {}", termId);

        try {
            BulkReportGenerationDto result = bulkReportGenerationService
                    .validateBulkReportGeneration(termId, config);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error validating bulk report generation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Initiate Bulk Report Generation
     * POST: /academic/semester-closure/generate
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> initiateBulkReportGeneration(
            @RequestParam UUID termId,
            @RequestBody BulkReportGenerationDto.ReportConfiguration config,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Initiating bulk report generation for term: {} by user: {}", termId, userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UUID batchId = bulkReportGenerationService.initiateBulkReportGeneration(
                    termId, config, currentUser.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("batchId", batchId);
            response.put("message", "Bulk report generation initiated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error initiating bulk report generation", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to initiate report generation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get Batch Status
     * GET: /academic/semester-closure/batch/{batchId}/status
     */
    @GetMapping("/batch/{batchId}/status")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    @ResponseBody
    public ResponseEntity<BulkReportGenerationDto> getBatchStatus(@PathVariable UUID batchId) {
        log.info("Getting batch status for: {}", batchId);

        try {
            BulkReportGenerationDto result = bulkReportGenerationService.getBatchStatus(batchId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error getting batch status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cancel Batch
     * POST: /academic/semester-closure/batch/{batchId}/cancel
     */
    @PostMapping("/batch/{batchId}/cancel")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelBatch(@PathVariable UUID batchId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Cancelling batch: {} by user: {}", batchId, userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean cancelled = bulkReportGenerationService.cancelBatch(batchId, currentUser.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", cancelled);
            response.put("message", cancelled ? "Batch cancelled successfully" : "Batch cannot be cancelled");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error cancelling batch", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to cancel batch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Pre-Closure Validation Dashboard
     * GET: /academic/semester-closure/pre-validation/{termId}
     */
    @GetMapping("/pre-validation/{termId}")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    public String preClosureValidation(@PathVariable UUID termId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading pre-closure validation for term: {} by user: {}", termId, userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            // Create default configuration for validation
            BulkReportGenerationDto.ReportConfiguration defaultConfig =
                    BulkReportGenerationDto.ReportConfiguration.builder()
                            .includeStudentReports(true)
                            .includeClassSummaries(true)
                            .includeTeacherEvaluations(true)
                            .includeParentNotifications(true)
                            .includeManagementSummary(true)
                            .autoDistribute(false)
                            .reportQuality("STANDARD")
                            .includeCharts(true)
                            .language("id")
                            .build();

            // Run validation
            BulkReportGenerationDto validationResult = bulkReportGenerationService
                    .validateBulkReportGeneration(termId, defaultConfig);

            model.addAttribute("user", currentUser);
            model.addAttribute("term", term);
            model.addAttribute("bulkReportData", validationResult);
            model.addAttribute("pageTitle", "Pre-Closure Validation");

            return "academic/pre-closure-validation";

        } catch (Exception e) {
            log.error("Error loading pre-closure validation", e);
            model.addAttribute("error", "Failed to load validation: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Batch Details View
     * GET: /academic/semester-closure/batch/{batchId}
     */
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    public String batchDetails(@PathVariable UUID batchId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        log.info("Loading batch details for: {} by user: {}", batchId, userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BulkReportGenerationDto batchStatus = bulkReportGenerationService.getBatchStatus(batchId);

            ReportGenerationBatch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new RuntimeException("Batch not found"));

            model.addAttribute("user", currentUser);
            model.addAttribute("batch", batch);
            model.addAttribute("batchStatus", batchStatus);
            model.addAttribute("pageTitle", "Batch Details");

            return "academic/batch-details";

        } catch (Exception e) {
            log.error("Error loading batch details", e);
            model.addAttribute("error", "Failed to load batch details: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Execute Term Closure
     * POST: /academic/semester-closure/execute/{termId}
     */
    @PostMapping("/execute/{termId}")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_EXECUTE')")
    public String executeTermClosure(@PathVariable UUID termId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        log.info("Executing term closure for: {} by user: {}", termId, userDetails.getUsername());

        try {
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            // Check if all reports are generated
            Long completedBatches = batchRepository.countCompletedBatchesForTerm(termId);
            if (completedBatches == 0) {
                redirectAttributes.addFlashAttribute("error",
                    "Cannot close term: No completed report generation batches found");
                return "redirect:/academic/semester-closure?termId=" + termId;
            }

            // Execute term closure
            term.setStatus(AcademicTerm.TermStatus.COMPLETED);
            academicTermRepository.save(term);

            redirectAttributes.addFlashAttribute("success",
                "Term " + term.getTermName() + " has been successfully closed");

            log.info("Term closure completed for: {} by user: {}", termId, userDetails.getUsername());

        } catch (Exception e) {
            log.error("Error executing term closure", e);
            redirectAttributes.addFlashAttribute("error",
                "Failed to execute term closure: " + e.getMessage());
        }

        return "redirect:/academic/semester-closure?termId=" + termId;
    }

    /**
     * Generate Quick Status Report
     * GET: /academic/semester-closure/quick-status/{termId}
     */
    @GetMapping("/quick-status/{termId}")
    @PreAuthorize("hasAuthority('TERM_CLOSURE_MANAGE')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getQuickStatus(@PathVariable UUID termId) {
        try {
            AcademicTerm term = academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));

            List<ReportGenerationBatch> activeBatches = batchRepository.findActiveBatches()
                    .stream()
                    .filter(batch -> batch.getTerm().getId().equals(termId))
                    .toList();

            Long completedBatches = batchRepository.countCompletedBatchesForTerm(termId);

            Map<String, Object> status = new HashMap<>();
            status.put("termName", term.getTermName());
            status.put("termStatus", term.getStatus().name());
            status.put("activeBatchesCount", activeBatches.size());
            status.put("completedBatchesCount", completedBatches);
            status.put("canBeClosed", completedBatches > 0 && activeBatches.isEmpty());

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error getting quick status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}