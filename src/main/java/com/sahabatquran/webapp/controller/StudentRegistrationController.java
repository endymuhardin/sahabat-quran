package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.*;
import com.sahabatquran.webapp.entity.Program;
import com.sahabatquran.webapp.entity.Session;
import com.sahabatquran.webapp.service.StudentRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
@Slf4j
public class StudentRegistrationController {
    
    private final StudentRegistrationService registrationService;
    
    @GetMapping
    public String showRegistrationForm(Model model) {
        log.info("Displaying student registration form");
        
        StudentRegistrationRequest registrationRequest = new StudentRegistrationRequest();
        
        // Initialize with one empty session preference for form rendering
        List<StudentRegistrationRequest.SessionPreferenceRequest> sessionPrefs = new ArrayList<>();
        StudentRegistrationRequest.SessionPreferenceRequest sessionPref = new StudentRegistrationRequest.SessionPreferenceRequest();
        sessionPref.setPriority(1); // Default priority
        sessionPref.setPreferredDays(new ArrayList<>()); // Empty list for days
        sessionPrefs.add(sessionPref);
        registrationRequest.setSessionPreferences(sessionPrefs);
        
        model.addAttribute("registrationRequest", registrationRequest);
        model.addAttribute("programs", registrationService.getActivePrograms());
        model.addAttribute("sessions", registrationService.getActiveSessions());
        
        // Create simple session options for JavaScript without entity serialization
        StringBuilder sessionOptionsHtml = new StringBuilder();
        sessionOptionsHtml.append("<option value=\"\">Pilih sesi</option>");
        for (var session : registrationService.getActiveSessions()) {
            sessionOptionsHtml.append("<option value=\"")
                             .append(session.getId())
                             .append("\">")
                             .append(session.getName())
                             .append("</option>");
        }
        model.addAttribute("sessionOptionsHtml", sessionOptionsHtml.toString());
        model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
        
        return "registration/form";
    }
    
    @PostMapping
    public String submitRegistration(@Valid @ModelAttribute StudentRegistrationRequest request,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        log.info("Processing student registration submission for email: {}", request.getEmail());
        
        if (bindingResult.hasErrors()) {
            log.warn("Registration form has validation errors: {}", bindingResult.getAllErrors());
            
            // Ensure sessionPreferences is initialized if empty for form re-display
            if (request.getSessionPreferences() == null || request.getSessionPreferences().isEmpty()) {
                List<StudentRegistrationRequest.SessionPreferenceRequest> sessionPrefs = new ArrayList<>();
                StudentRegistrationRequest.SessionPreferenceRequest sessionPref = new StudentRegistrationRequest.SessionPreferenceRequest();
                sessionPref.setPriority(1);
                sessionPref.setPreferredDays(new ArrayList<>());
                sessionPrefs.add(sessionPref);
                request.setSessionPreferences(sessionPrefs);
            }
            
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            return "registration/form";
        }
        
        try {
            StudentRegistrationResponse response = registrationService.createRegistration(request);
            log.info("Student registration created successfully with ID: {}", response.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Pendaftaran berhasil disimpan! ID Pendaftaran: " + response.getId());
            redirectAttributes.addAttribute("id", response.getId());
            
            return "redirect:/register/confirmation";
            
        } catch (IllegalArgumentException e) {
            log.error("Registration validation error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            
            // Ensure sessionPreferences is initialized if empty for form re-display
            if (request.getSessionPreferences() == null || request.getSessionPreferences().isEmpty()) {
                List<StudentRegistrationRequest.SessionPreferenceRequest> sessionPrefs = new ArrayList<>();
                StudentRegistrationRequest.SessionPreferenceRequest sessionPref = new StudentRegistrationRequest.SessionPreferenceRequest();
                sessionPref.setPriority(1);
                sessionPref.setPreferredDays(new ArrayList<>());
                sessionPrefs.add(sessionPref);
                request.setSessionPreferences(sessionPrefs);
            }
            
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            return "registration/form";
            
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan sistem. Silakan coba lagi.");
            
            // Ensure sessionPreferences is initialized if empty for form re-display
            if (request.getSessionPreferences() == null || request.getSessionPreferences().isEmpty()) {
                List<StudentRegistrationRequest.SessionPreferenceRequest> sessionPrefs = new ArrayList<>();
                StudentRegistrationRequest.SessionPreferenceRequest sessionPref = new StudentRegistrationRequest.SessionPreferenceRequest();
                sessionPref.setPriority(1);
                sessionPref.setPreferredDays(new ArrayList<>());
                sessionPrefs.add(sessionPref);
                request.setSessionPreferences(sessionPrefs);
            }
            
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            return "registration/form";
        }
    }
    
    @GetMapping("/confirmation")
    public String showConfirmation(@RequestParam(required = false) UUID id, Model model) {
        log.info("Displaying registration confirmation for ID: {}", id);
        
        if (id != null) {
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
            } catch (Exception e) {
                log.error("Error fetching registration for confirmation: {}", e.getMessage());
                model.addAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            }
        }
        
        return "registration/confirmation";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Displaying edit form for registration ID: {}", id);
        
        try {
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            
            // Only allow editing of DRAFT registrations
            if (registration.getRegistrationStatus() != 
                com.sahabatquran.webapp.entity.StudentRegistration.RegistrationStatus.DRAFT) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Hanya pendaftaran dengan status DRAFT yang dapat diubah");
                return "redirect:/register/" + id;
            }
            
            // Convert response to request object for editing
            StudentRegistrationRequest request = convertToRequest(registration);
            
            model.addAttribute("registrationRequest", request);
            model.addAttribute("registration", registration);
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            
            return "registration/edit";
            
        } catch (Exception e) {
            log.error("Error loading registration for edit: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/register";
        }
    }
    
    @PostMapping("/{id}/edit")
    public String updateRegistration(@PathVariable UUID id,
                                   @Valid @ModelAttribute StudentRegistrationRequest request,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        log.info("Processing registration update for ID: {}", id);
        
        if (bindingResult.hasErrors()) {
            log.warn("Registration update form has validation errors: {}", bindingResult.getAllErrors());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
            } catch (Exception e) {
                // Registration not found, redirect to main page
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/register";
            }
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            return "registration/edit";
        }
        
        try {
            StudentRegistrationResponse response = registrationService.updateRegistration(id, request);
            log.info("Registration updated successfully: {}", response.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Pendaftaran berhasil diperbarui!");
            return "redirect:/register/" + id;
            
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Registration update error: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/register";
            }
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            return "registration/edit";
            
        } catch (Exception e) {
            log.error("Unexpected error during registration update", e);
            model.addAttribute("errorMessage", "Terjadi kesalahan sistem. Silakan coba lagi.");
            try {
                StudentRegistrationResponse registration = registrationService.getRegistration(id);
                model.addAttribute("registration", registration);
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
                return "redirect:/register";
            }
            model.addAttribute("programs", registrationService.getActivePrograms());
            model.addAttribute("sessions", registrationService.getActiveSessions());
            model.addAttribute("dayOfWeekOptions", StudentRegistrationRequest.DayOfWeek.values());
            return "registration/edit";
        }
    }
    
    @GetMapping("/{id}")
    public String viewRegistration(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Displaying registration details for ID: {}", id);
        
        try {
            StudentRegistrationResponse registration = registrationService.getRegistration(id);
            model.addAttribute("registration", registration);
            
            return "registration/detail";
            
        } catch (Exception e) {
            log.error("Error fetching registration details: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Pendaftaran tidak ditemukan");
            return "redirect:/register";
        }
    }
    
    @PostMapping("/{id}/submit")
    public String submitForReview(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        log.info("Submitting registration for review: {}", id);
        
        try {
            StudentRegistrationResponse response = registrationService.submitRegistration(id);
            log.info("Registration submitted successfully: {}", response.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Pendaftaran berhasil disubmit untuk review!");
            
            return "redirect:/register/" + id;
            
        } catch (IllegalStateException e) {
            log.error("Submit registration error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register/" + id;
            
        } catch (Exception e) {
            log.error("Unexpected error during registration submission", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Terjadi kesalahan sistem. Silakan coba lagi.");
            return "redirect:/register/" + id;
        }
    }
    
    // AJAX endpoint for session preferences
    @GetMapping("/api/sessions")
    @ResponseBody
    public ResponseEntity<List<Session>> getActiveSessions() {
        List<Session> sessions = registrationService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }
    
    // AJAX endpoint for programs
    @GetMapping("/api/programs")
    @ResponseBody
    public ResponseEntity<List<Program>> getActivePrograms() {
        List<Program> programs = registrationService.getActivePrograms();
        return ResponseEntity.ok(programs);
    }
    
    // Helper method to convert response to request for editing
    private StudentRegistrationRequest convertToRequest(StudentRegistrationResponse response) {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        
        // Personal Information
        request.setFullName(response.getFullName());
        request.setGender(response.getGender());
        request.setDateOfBirth(response.getDateOfBirth());
        request.setPlaceOfBirth(response.getPlaceOfBirth());
        request.setPhoneNumber(response.getPhoneNumber());
        request.setEmail(response.getEmail());
        request.setAddress(response.getAddress());
        request.setEmergencyContactName(response.getEmergencyContactName());
        request.setEmergencyContactPhone(response.getEmergencyContactPhone());
        request.setEmergencyContactRelation(response.getEmergencyContactRelation());
        
        // Educational Information
        request.setEducationLevel(response.getEducationLevel());
        request.setSchoolName(response.getSchoolName());
        request.setQuranReadingExperience(response.getQuranReadingExperience());
        request.setPreviousTahsinExperience(response.getPreviousTahsinExperience());
        request.setPreviousTahsinDetails(response.getPreviousTahsinDetails());
        
        // Program Selection
        if (response.getProgram() != null) {
            request.setProgramId(response.getProgram().getId());
        }
        request.setRegistrationReason(response.getRegistrationReason());
        request.setLearningGoals(response.getLearningGoals());
        
        // Session Preferences
        if (response.getSessionPreferences() != null) {
            List<StudentRegistrationRequest.SessionPreferenceRequest> sessionPrefs = 
                response.getSessionPreferences().stream().map(pref -> {
                    StudentRegistrationRequest.SessionPreferenceRequest sessionPref = 
                        new StudentRegistrationRequest.SessionPreferenceRequest();
                    sessionPref.setSessionId(pref.getSession().getId());
                    sessionPref.setPriority(pref.getPriority());
                    
                    // Convert string days to enum
                    List<StudentRegistrationRequest.DayOfWeek> days = pref.getPreferredDays().stream()
                        .map(day -> StudentRegistrationRequest.DayOfWeek.valueOf(day))
                        .toList();
                    sessionPref.setPreferredDays(days);
                    
                    return sessionPref;
                }).toList();
            
            request.setSessionPreferences(sessionPrefs);
        }
        
        // Placement Test
        if (response.getPlacementTest() != null) {
            request.setRecordingDriveLink(response.getPlacementTest().getRecordingDriveLink());
        }
        
        return request;
    }
}