package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.FeedbackSubmissionDto;
import com.sahabatquran.webapp.dto.StudentFeedbackDto;
import com.sahabatquran.webapp.service.StudentFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for Anonymous Feedback functionality.
 * Handles public access to feedback forms without authentication.
 */
@Controller
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Slf4j
public class AnonymousFeedbackController {

    private final StudentFeedbackService feedbackService;

    /**
     * Anonymous feedback access for parent notifications and public feedback
     * AKH-HP-008: Parent Response to Teacher Change Notification
     */
    @GetMapping("/anonymous")
    public String anonymousFeedback(@RequestParam String token, Model model) {
        log.info("Loading anonymous feedback form for token: {}", token);

        try {
            // Validate token and get notification details
            StudentFeedbackDto.AnonymousAccess anonymousAccess =
                feedbackService.validateAnonymousToken(token);

            if (!anonymousAccess.getIsValid()) {
                model.addAttribute("error", "Token tidak valid atau sudah kedaluwarsa");
                return "error/invalid-token";
            }

            // Check notification type
            if ("TEACHER_CHANGE_NOTIFICATION".equals(anonymousAccess.getNotificationType())) {
                // Teacher change notification form
                model.addAttribute("notificationType", "TEACHER_CHANGE");
                model.addAttribute("teacherChangeInfo", anonymousAccess.getTeacherChangeInfo());
                model.addAttribute("substituteTeacherInfo", anonymousAccess.getSubstituteTeacherInfo());
                model.addAttribute("changeDuration", anonymousAccess.getChangeDuration());
                model.addAttribute("childName", anonymousAccess.getChildName());
                model.addAttribute("className", anonymousAccess.getClassName());
                model.addAttribute("anonymousToken", token);
                model.addAttribute("pageTitle", "Notifikasi Perubahan Pengajar");

                return "student/anonymous-teacher-change-notification";
            } else {
                // Generic anonymous feedback form
                model.addAttribute("anonymousAccess", anonymousAccess);
                model.addAttribute("anonymousToken", token);
                model.addAttribute("pageTitle", "Feedback Anonim");

                return "student/anonymous-feedback-form";
            }

        } catch (Exception e) {
            log.error("Error loading anonymous feedback", e);
            model.addAttribute("error", "Gagal memuat form feedback: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Submit anonymous feedback (parent notification response)
     */
    @PostMapping("/anonymous/submit")
    public String submitAnonymousFeedback(@RequestParam String token,
                                         @ModelAttribute FeedbackSubmissionDto.AnonymousFeedbackData feedbackData,
                                         RedirectAttributes redirectAttributes) {
        log.info("Submitting anonymous feedback for token: {}", token);

        try {
            // Validate token
            if (!feedbackService.validateAnonymousToken(token).getIsValid()) {
                redirectAttributes.addFlashAttribute("error", "Token tidak valid atau sudah kedaluwarsa");
                return "redirect:/feedback/anonymous?token=" + token;
            }

            // Submit anonymous feedback
            FeedbackSubmissionDto.AnonymousSubmissionResult result =
                feedbackService.submitAnonymousFeedback(token, feedbackData);

            if (result.getSuccess()) {
                redirectAttributes.addFlashAttribute("success", result.getMessage());
                redirectAttributes.addFlashAttribute("submissionId", result.getSubmissionId());
                return "redirect:/feedback/anonymous/confirmation?token=" + token;
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
                return "redirect:/feedback/anonymous?token=" + token;
            }

        } catch (Exception e) {
            log.error("Error submitting anonymous feedback", e);
            redirectAttributes.addFlashAttribute("error", "Gagal mengirim feedback: " + e.getMessage());
            return "redirect:/feedback/anonymous?token=" + token;
        }
    }

    /**
     * Show anonymous feedback confirmation
     */
    @GetMapping("/anonymous/confirmation")
    public String showAnonymousConfirmation(@RequestParam String token, Model model) {
        log.info("Showing anonymous feedback confirmation for token: {}", token);

        try {
            StudentFeedbackDto.AnonymousConfirmation confirmation =
                feedbackService.getAnonymousConfirmation(token);

            model.addAttribute("confirmation", confirmation);
            model.addAttribute("anonymousToken", token);
            model.addAttribute("pageTitle", "Feedback Berhasil Dikirim");

            return "student/anonymous-feedback-confirmation";

        } catch (Exception e) {
            log.error("Error showing anonymous confirmation", e);
            model.addAttribute("error", "Gagal memuat konfirmasi");
            return "error/500";
        }
    }
}