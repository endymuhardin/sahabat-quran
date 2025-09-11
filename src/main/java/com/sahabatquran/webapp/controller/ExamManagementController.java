package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.ExamDto;
import com.sahabatquran.webapp.dto.ExamQuestionDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.service.ExamManagementService;
import com.sahabatquran.webapp.service.ExamQuestionService;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.ClassGroupRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/exams")
@RequiredArgsConstructor
@Slf4j
public class ExamManagementController {
    
    private final ExamManagementService examManagementService;
    private final ExamQuestionService examQuestionService;
    private final ClassGroupRepository classGroupRepository;
    private final AcademicTermRepository academicTermRepository;
    
    /**
     * Exam Management Dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("Loading exam management dashboard for: {}", userDetails.getUsername());
        
        try {
            // Get exams created by current user (instructor)
            List<ExamDto> myExams = examManagementService.getExamsCreatedByInstructor(userDetails.getUsername());
            
            // Get active exams
            List<ExamDto> activeExams = examManagementService.getActiveExams();
            
            // Get exams requiring grading
            List<ExamDto> examsRequiringGrading = examManagementService.getExamsRequiringGrading();
            
            model.addAttribute("myExams", myExams);
            model.addAttribute("activeExams", activeExams);
            model.addAttribute("examsRequiringGrading", examsRequiringGrading);
            model.addAttribute("pageTitle", "Exam Management Dashboard");
            
            return "exams/dashboard";
        } catch (Exception e) {
            log.error("Error loading exam dashboard: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading exam dashboard: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * List all exams
     */
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public String listExams(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(required = false) UUID classGroupId,
                           Model model) {
        log.info("Listing exams for user: {}, classGroup: {}", userDetails.getUsername(), classGroupId);
        
        try {
            List<ExamDto> exams;
            
            if (classGroupId != null) {
                exams = examManagementService.getExamsByClassGroup(classGroupId);
            } else {
                exams = examManagementService.getExamsCreatedByInstructor(userDetails.getUsername());
            }
            
            // Get available class groups for filtering
            var availableClasses = classGroupRepository.findAll();
            var activeTerm = academicTermRepository.findByStatus(AcademicTerm.TermStatus.ACTIVE);
            
            model.addAttribute("exams", exams);
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("selectedClassGroupId", classGroupId);
            model.addAttribute("currentTerm", !activeTerm.isEmpty() ? activeTerm.get(0) : null);
            model.addAttribute("pageTitle", "Exam Management");
            
            return "exams/list";
        } catch (Exception e) {
            log.error("Error listing exams: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading exams: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Show create exam form
     */
    @GetMapping("/create")
    @PreAuthorize("hasAuthority('EXAM_CREATE')")
    public String showCreateForm(Model model) {
        log.info("Showing create exam form");
        
        try {
            ExamDto examDto = new ExamDto();
            
            // Get available class groups and current academic term
            var availableClasses = classGroupRepository.findAll();
            var activeTerm = academicTermRepository.findByStatus(AcademicTerm.TermStatus.ACTIVE);
            
            model.addAttribute("examDto", examDto);
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("currentTerm", !activeTerm.isEmpty() ? activeTerm.get(0) : null);
            model.addAttribute("examTypes", List.of("MIDTERM", "FINAL", "QUIZ", "PRACTICE"));
            model.addAttribute("pageTitle", "Create New Exam");
            
            return "exams/create";
        } catch (Exception e) {
            log.error("Error showing create exam form: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading create form: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Process create exam form
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('EXAM_CREATE')")
    public String createExam(@Valid @ModelAttribute("examDto") ExamDto examDto,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Creating exam: {} by user: {}", examDto.getTitle(), userDetails.getUsername());
        
        if (bindingResult.hasErrors()) {
            log.warn("Exam creation validation errors: {}", bindingResult.getAllErrors());
            
            // Reload form data
            var availableClasses = classGroupRepository.findAll();
            var activeTerm = academicTermRepository.findByStatus(AcademicTerm.TermStatus.ACTIVE);
            
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("currentTerm", !activeTerm.isEmpty() ? activeTerm.get(0) : null);
            model.addAttribute("examTypes", List.of("MIDTERM", "FINAL", "QUIZ", "PRACTICE"));
            model.addAttribute("pageTitle", "Create New Exam");
            
            return "exams/create";
        }
        
        try {
            ExamDto createdExam = examManagementService.createExam(examDto, userDetails.getUsername());
            
            log.info("Exam created successfully with ID: {}", createdExam.getId());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Exam '" + createdExam.getTitle() + "' created successfully!");
            
            return "redirect:/exams/" + createdExam.getId() + "/edit";
        } catch (Exception e) {
            log.error("Error creating exam: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error creating exam: " + e.getMessage());
            
            // Reload form data
            var availableClasses = classGroupRepository.findAll();
            var activeTerm = academicTermRepository.findByStatus(AcademicTerm.TermStatus.ACTIVE);
            
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("currentTerm", !activeTerm.isEmpty() ? activeTerm.get(0) : null);
            model.addAttribute("examTypes", List.of("MIDTERM", "FINAL", "QUIZ", "PRACTICE"));
            model.addAttribute("pageTitle", "Create New Exam");
            
            return "exams/create";
        }
    }
    
    /**
     * Show exam details
     */
    @GetMapping("/{examId}")
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public String showExamDetails(@PathVariable UUID examId, Model model) {
        log.info("Showing exam details: {}", examId);
        
        try {
            ExamDto exam = examManagementService.getExamById(examId);
            List<ExamQuestionDto> questions = examQuestionService.getQuestionsByExamId(examId);
            
            model.addAttribute("exam", exam);
            model.addAttribute("questions", questions);
            model.addAttribute("pageTitle", "Exam: " + exam.getTitle());
            
            return "exams/details";
        } catch (Exception e) {
            log.error("Error showing exam details: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading exam: " + e.getMessage());
            return "error/404";
        }
    }
    
    /**
     * Show edit exam form
     */
    @GetMapping("/{examId}/edit")
    @PreAuthorize("hasAuthority('EXAM_EDIT')")
    public String showEditForm(@PathVariable UUID examId, Model model) {
        log.info("Showing edit exam form: {}", examId);
        
        try {
            ExamDto exam = examManagementService.getExamById(examId);
            
            // Check if exam is editable
            if (!exam.isEditable()) {
                model.addAttribute("errorMessage", "This exam cannot be edited in its current status.");
                return "redirect:/exams/" + examId;
            }
            
            List<ExamQuestionDto> questions = examQuestionService.getQuestionsByExamId(examId);
            var availableClasses = classGroupRepository.findAll();
            
            model.addAttribute("examDto", exam);
            model.addAttribute("questions", questions);
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("examTypes", List.of("MIDTERM", "FINAL", "QUIZ", "PRACTICE"));
            model.addAttribute("pageTitle", "Edit Exam: " + exam.getTitle());
            
            return "exams/edit";
        } catch (Exception e) {
            log.error("Error showing edit exam form: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading exam: " + e.getMessage());
            return "error/404";
        }
    }
    
    /**
     * Process edit exam form
     */
    @PostMapping("/{examId}/edit")
    @PreAuthorize("hasAuthority('EXAM_EDIT')")
    public String updateExam(@PathVariable UUID examId,
                            @Valid @ModelAttribute("examDto") ExamDto examDto,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Updating exam: {} by user: {}", examId, userDetails.getUsername());
        
        if (bindingResult.hasErrors()) {
            log.warn("Exam update validation errors: {}", bindingResult.getAllErrors());
            
            // Reload form data
            List<ExamQuestionDto> questions = examQuestionService.getQuestionsByExamId(examId);
            var availableClasses = classGroupRepository.findAll();
            
            model.addAttribute("questions", questions);
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("examTypes", List.of("MIDTERM", "FINAL", "QUIZ", "PRACTICE"));
            model.addAttribute("pageTitle", "Edit Exam: " + examDto.getTitle());
            
            return "exams/edit";
        }
        
        try {
            examDto.setId(examId);
            ExamDto updatedExam = examManagementService.updateExam(examId, examDto, userDetails.getUsername());
            
            log.info("Exam updated successfully: {}", examId);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Exam '" + updatedExam.getTitle() + "' updated successfully!");
            
            return "redirect:/exams/" + examId;
        } catch (Exception e) {
            log.error("Error updating exam: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error updating exam: " + e.getMessage());
            
            // Reload form data
            List<ExamQuestionDto> questions = examQuestionService.getQuestionsByExamId(examId);
            var availableClasses = classGroupRepository.findAll();
            
            model.addAttribute("questions", questions);
            model.addAttribute("availableClasses", availableClasses);
            model.addAttribute("examTypes", List.of("MIDTERM", "FINAL", "QUIZ", "PRACTICE"));
            model.addAttribute("pageTitle", "Edit Exam: " + examDto.getTitle());
            
            return "exams/edit";
        }
    }
    
    /**
     * Schedule exam (change status from DRAFT to SCHEDULED)
     */
    @PostMapping("/{examId}/schedule")
    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    public String scheduleExam(@PathVariable UUID examId, 
                              RedirectAttributes redirectAttributes) {
        log.info("Scheduling exam: {}", examId);
        
        try {
            examManagementService.scheduleExam(examId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Exam scheduled successfully!");
            return "redirect:/exams/" + examId;
        } catch (Exception e) {
            log.error("Error scheduling exam: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error scheduling exam: " + e.getMessage());
            return "redirect:/exams/" + examId;
        }
    }
    
    /**
     * Activate exam (change status from SCHEDULED to ACTIVE)
     */
    @PostMapping("/{examId}/activate")
    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    public String activateExam(@PathVariable UUID examId, 
                              RedirectAttributes redirectAttributes) {
        log.info("Activating exam: {}", examId);
        
        try {
            examManagementService.activateExam(examId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Exam activated successfully!");
            return "redirect:/exams/" + examId;
        } catch (Exception e) {
            log.error("Error activating exam: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error activating exam: " + e.getMessage());
            return "redirect:/exams/" + examId;
        }
    }
    
    /**
     * Complete exam (change status to COMPLETED)
     */
    @PostMapping("/{examId}/complete")
    @PreAuthorize("hasAuthority('EXAM_MANAGE')")
    public String completeExam(@PathVariable UUID examId, 
                              RedirectAttributes redirectAttributes) {
        log.info("Completing exam: {}", examId);
        
        try {
            examManagementService.completeExam(examId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Exam completed successfully!");
            return "redirect:/exams/" + examId;
        } catch (Exception e) {
            log.error("Error completing exam: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error completing exam: " + e.getMessage());
            return "redirect:/exams/" + examId;
        }
    }
    
    /**
     * Delete exam
     */
    @PostMapping("/{examId}/delete")
    @PreAuthorize("hasAuthority('EXAM_DELETE')")
    public String deleteExam(@PathVariable UUID examId, 
                            RedirectAttributes redirectAttributes) {
        log.info("Deleting exam: {}", examId);
        
        try {
            examManagementService.deleteExam(examId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Exam deleted successfully!");
            return "redirect:/exams";
        } catch (Exception e) {
            log.error("Error deleting exam: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting exam: " + e.getMessage());
            return "redirect:/exams/" + examId;
        }
    }
    
    /**
     * AJAX endpoint to get exams for a class group
     */
    @GetMapping("/api/class/{classGroupId}")
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    @ResponseBody
    public ResponseEntity<List<ExamDto>> getExamsByClassGroup(@PathVariable UUID classGroupId) {
        try {
            List<ExamDto> exams = examManagementService.getExamsByClassGroup(classGroupId);
            return ResponseEntity.ok(exams);
        } catch (Exception e) {
            log.error("Error getting exams for class group: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}