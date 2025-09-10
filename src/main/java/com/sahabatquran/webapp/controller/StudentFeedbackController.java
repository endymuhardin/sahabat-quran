package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.FeedbackSubmissionDto;
import com.sahabatquran.webapp.dto.StudentFeedbackDto;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.StudentFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controller for Student Feedback functionality.
 * Handles anonymous feedback submission for teacher evaluation and other campaigns.
 */
@Controller
@RequestMapping("/student/feedback")
@RequiredArgsConstructor
@Slf4j
public class StudentFeedbackController {
    
    private final StudentFeedbackService feedbackService;
    private final UserRepository userRepository;
    
    /**
     * Display available feedback campaigns for student
     * AKH-HP-002: View Available Feedback
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public String feedbackDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Loading feedback dashboard for student: {}", userDetails.getUsername());
        
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            List<StudentFeedbackDto.CampaignSummary> activeCampaigns = 
                feedbackService.getActiveCampaignsForStudent(student.getId());
            
            List<StudentFeedbackDto.SubmissionHistory> history = 
                feedbackService.getStudentFeedbackHistory(student.getId());
            
            model.addAttribute("activeCampaigns", activeCampaigns);
            model.addAttribute("feedbackHistory", history);
            model.addAttribute("user", student);
            model.addAttribute("pageTitle", "Feedback Siswa");
            
            return "student/feedback-dashboard";
            
        } catch (Exception e) {
            log.error("Error loading feedback dashboard", e);
            model.addAttribute("error", "Failed to load feedback campaigns");
            return "error/500";
        }
    }
    
    /**
     * Start or resume feedback submission
     * AKH-HP-002: Start Feedback Session
     */
    @GetMapping("/campaign/{campaignId}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public String startFeedback(@PathVariable UUID campaignId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        log.info("Starting feedback for campaign: {} by student: {}", campaignId, userDetails.getUsername());
        
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            // Check if already submitted
            if (feedbackService.hasStudentSubmittedFeedback(campaignId, student.getId())) {
                model.addAttribute("error", "Anda sudah memberikan feedback untuk campaign ini");
                model.addAttribute("campaignId", campaignId);
                return "student/feedback-already-submitted";
            }
            
            // Get campaign details
            StudentFeedbackDto.CampaignDetails campaignDetails = 
                feedbackService.getCampaignDetails(campaignId, student.getId());
            
            // Check for saved progress
            StudentFeedbackDto.ResumeData resumeData = 
                feedbackService.resumeFeedback(campaignId, student.getId());
            
            // Generate anonymous token
            String anonymousToken = feedbackService.getOrCreateAnonymousToken(student.getId(), campaignId);
            
            model.addAttribute("campaign", campaignDetails);
            model.addAttribute("resumeData", resumeData);
            model.addAttribute("anonymousToken", anonymousToken);
            model.addAttribute("user", student);
            model.addAttribute("pageTitle", "Feedback: " + campaignDetails.getCampaignName());
            
            return "student/feedback-form";
            
        } catch (Exception e) {
            log.error("Error starting feedback", e);
            model.addAttribute("error", "Failed to load feedback form: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Submit feedback response
     * AKH-HP-002: Final Submission
     */
    @PostMapping("/campaign/{campaignId}/submit")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public String submitFeedback(@PathVariable UUID campaignId,
                                @ModelAttribute FeedbackSubmissionDto.FeedbackData feedbackData,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        log.info("Submitting feedback for campaign: {} by student: {}", campaignId, userDetails.getUsername());
        
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            // Validate submission
            FeedbackSubmissionDto.ValidationResult validation = 
                feedbackService.validateSubmission(campaignId, feedbackData);
            
            if (!validation.getIsValid()) {
                redirectAttributes.addFlashAttribute("errors", validation.getErrors());
                return "redirect:/student/feedback/campaign/" + campaignId;
            }
            
            // Submit feedback
            FeedbackSubmissionDto.SubmissionResult result = 
                feedbackService.submitFeedback(campaignId, student.getId(), feedbackData);
            
            if (result.getSuccess()) {
                redirectAttributes.addFlashAttribute("success", result.getMessage());
                redirectAttributes.addFlashAttribute("submissionId", result.getSubmissionId());
                redirectAttributes.addFlashAttribute("anonymousToken", result.getAnonymousToken());
                return "redirect:/student/feedback/confirmation/" + campaignId;
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
                return "redirect:/student/feedback/campaign/" + campaignId;
            }
            
        } catch (Exception e) {
            log.error("Error submitting feedback", e);
            redirectAttributes.addFlashAttribute("error", "Failed to submit feedback: " + e.getMessage());
            return "redirect:/student/feedback/campaign/" + campaignId;
        }
    }
    
    /**
     * Auto-save partial feedback (AJAX)
     */
    @PostMapping("/campaign/{campaignId}/autosave")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @ResponseBody
    public ResponseEntity<?> autoSaveFeedback(@PathVariable UUID campaignId,
                                             @RequestBody FeedbackSubmissionDto.PartialData partialData,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Auto-saving feedback for campaign: {}", campaignId);
        
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            feedbackService.savePartialFeedback(campaignId, student.getId(), partialData);
            
            return ResponseEntity.ok()
                .body("{\"status\":\"success\",\"message\":\"Progress saved automatically\"}");
            
        } catch (Exception e) {
            log.error("Error auto-saving feedback", e);
            return ResponseEntity.badRequest()
                .body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Show submission confirmation
     */
    @GetMapping("/confirmation/{campaignId}")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    public String showConfirmation(@PathVariable UUID campaignId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        log.info("Showing confirmation for campaign: {} by student: {}", campaignId, userDetails.getUsername());
        
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            String anonymousToken = feedbackService.getOrCreateAnonymousToken(student.getId(), campaignId);
            
            StudentFeedbackDto.SubmissionConfirmation confirmation = 
                feedbackService.getSubmissionConfirmation(campaignId, student.getId(), anonymousToken);
            
            model.addAttribute("confirmation", confirmation);
            model.addAttribute("user", student);
            model.addAttribute("pageTitle", "Feedback Berhasil Dikirim");
            
            return "student/feedback-confirmation";
            
        } catch (Exception e) {
            log.error("Error showing confirmation", e);
            model.addAttribute("error", "Failed to load confirmation");
            return "error/500";
        }
    }
    
    /**
     * Check campaign completion status (AJAX)
     */
    @GetMapping("/campaign/{campaignId}/status")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @ResponseBody
    public ResponseEntity<?> checkCompletionStatus(@PathVariable UUID campaignId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            StudentFeedbackDto.CompletionStatus status = 
                feedbackService.getCampaignCompletionStatus(campaignId, student.getId());
            
            return ResponseEntity.ok()
                .body("{\"status\":\"" + status.name() + "\"}");
            
        } catch (Exception e) {
            log.error("Error checking completion status", e);
            return ResponseEntity.badRequest()
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Handle session recovery after timeout/disconnect
     */
    @PostMapping("/recover-session")
    @PreAuthorize("hasAuthority('STUDENT_VIEW')")
    @ResponseBody
    public ResponseEntity<?> recoverSession(@RequestParam String sessionToken,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User student = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            boolean recovered = feedbackService.recoverSession(sessionToken, student.getId());
            
            if (recovered) {
                return ResponseEntity.ok()
                    .body("{\"status\":\"success\",\"message\":\"Session recovered successfully\"}");
            } else {
                return ResponseEntity.badRequest()
                    .body("{\"status\":\"error\",\"message\":\"Could not recover session\"}");
            }
            
        } catch (Exception e) {
            log.error("Error recovering session", e);
            return ResponseEntity.badRequest()
                .body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}