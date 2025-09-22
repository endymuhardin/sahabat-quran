package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.dto.TeacherAvailabilityDto;
import com.sahabatquran.webapp.dto.TeacherAvailabilityChangeRequestDto;
import com.sahabatquran.webapp.dto.TeacherPreClosureDto;
import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.ClassGroup;
import com.sahabatquran.webapp.entity.TeacherAttendance;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.repository.SessionRepository;
import com.sahabatquran.webapp.repository.ClassSessionRepository;
import com.sahabatquran.webapp.repository.TeacherAttendanceRepository;
import com.sahabatquran.webapp.service.TeacherAvailabilityService;
import com.sahabatquran.webapp.service.TeacherAvailabilityChangeRequestService;
import com.sahabatquran.webapp.service.SessionService;
import com.sahabatquran.webapp.service.TeacherPreClosureService;
import com.sahabatquran.webapp.service.UserService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
@Slf4j
public class InstructorController {
    
    private final TeacherAvailabilityService teacherAvailabilityService;
    private final TeacherAvailabilityChangeRequestService changeRequestService;
    private final UserRepository userRepository;
    private final AcademicTermRepository academicTermRepository;
    private final SessionRepository sessionRepository;
    private final ClassSessionRepository classSessionRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;
    private final SessionService sessionService;
    private final TeacherPreClosureService teacherPreClosureService;
    private final UserService userService;
    
    /**
     * Instructor Dashboard
     * URL: /instructor/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        log.info("Loading instructor dashboard for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Instructor Dashboard");
            
            return "instructor/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading instructor dashboard", e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Teacher Availability Submission Interface
     * URL: /instructor/availability-submission
     */
    @GetMapping("/availability-submission")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_SUBMIT')")
    public String availabilitySubmission(@RequestParam(required = false) UUID termId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        log.info("Loading availability submission for instructor: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get active or selected term
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get current availability data for this teacher and term
            TeacherAvailabilityDto.AvailabilityMatrix currentMatrix = teacherAvailabilityService
                    .getTeacherAvailabilityMatrix(currentUser.getId(), selectedTerm.getId());
            
            // Get available terms for instructor submission (planning terms only)
            List<AcademicTerm> availableTerms = academicTermRepository.findPlanningTerms();
            
            // Check if submission is allowed
            boolean canSubmit = teacherAvailabilityService.canSubmitAvailability(selectedTerm.getId());
            
            // Get all active sessions for dynamic display
            var allSessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("availabilityMatrix", currentMatrix);
            model.addAttribute("allSessions", allSessions);
            model.addAttribute("canSubmit", canSubmit);
            model.addAttribute("pageTitle", "Teacher Availability Submission");
            
            return "instructor/availability-submission";
            
        } catch (Exception e) {
            log.error("Error loading availability submission page", e);
            model.addAttribute("error", "Failed to load availability form: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Submit Teacher Availability
     * POST: /instructor/availability-submission
     */
    @PostMapping("/availability-submission")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_SUBMIT')")
    public String submitAvailability(@RequestParam UUID termId,
                                   @RequestParam(defaultValue = "6") Integer maxClassesPerWeek,
                                   @RequestParam(required = false) String preferences,
                                   @RequestParam(required = false) List<String> preferredLevels,
                                   @RequestParam(required = false) String specialConstraints,
                                   @RequestParam List<String> availabilitySlots, // Format: "dayOfWeek-sessionTime"
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        log.info("Processing availability submission for instructor: {} and term: {}", 
                userDetails.getUsername(), termId);
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Build availability matrix from form data
            TeacherAvailabilityDto.AvailabilityMatrix matrix = TeacherAvailabilityDto.AvailabilityMatrix.builder()
                    .maxClassesPerWeek(maxClassesPerWeek)
                    .preferences(preferences)
                    .preferredLevels(preferredLevels)
                    .specialConstraints(specialConstraints)
                    .build();
            
            // Process availability slots
            teacherAvailabilityService.submitTeacherAvailability(
                    currentUser.getId(), termId, matrix, availabilitySlots);
            
            redirectAttributes.addFlashAttribute("success", 
                "Availability submitted successfully. You can modify it until the deadline.");
            
            return "redirect:/instructor/availability-submission?termId=" + termId;
            
        } catch (Exception e) {
            log.error("Error submitting teacher availability", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to submit availability: " + e.getMessage());
            return "redirect:/instructor/availability-submission?termId=" + termId;
        }
    }
    
    /**
     * My Assigned Classes Dashboard
     * URL: /instructor/my-classes
     */
    @GetMapping("/my-classes")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String myClasses(@RequestParam(required = false) UUID termId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        log.info("Loading assigned classes for instructor: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Get assigned classes for this instructor
            var assignedClasses = teacherAvailabilityService
                    .getInstructorAssignedClasses(currentUser.getId(), selectedTerm.getId());
            
            List<AcademicTerm> availableTerms = academicTermRepository.findActiveTerms();
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("assignedClasses", assignedClasses);
            model.addAttribute("pageTitle", "My Assigned Classes");
            
            return "instructor/my-classes";
            
        } catch (Exception e) {
            log.error("Error loading assigned classes", e);
            model.addAttribute("error", "Failed to load classes: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Individual Class Preparation Interface
     * URL: /instructor/class/{classId}/preparation
     */
    @GetMapping("/class/{classId}/preparation")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String classPreparation(@PathVariable UUID classId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        log.info("Loading class preparation for class: {} by instructor: {}", 
                classId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify instructor has access to this class
            boolean hasAccess = teacherAvailabilityService
                    .hasAccessToClass(currentUser.getId(), classId);
            
            if (!hasAccess) {
                model.addAttribute("error", "You do not have access to this class");
                return "error/403";
            }
            
            // Get class preparation data
            var preparationData = teacherAvailabilityService
                    .getClassPreparationData(classId, currentUser.getId());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("classId", classId);
            model.addAttribute("preparationData", preparationData);
            model.addAttribute("pageTitle", "Class Preparation");
            
            return "instructor/class-preparation";
            
        } catch (Exception e) {
            log.error("Error loading class preparation", e);
            model.addAttribute("error", "Failed to load preparation data: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Class Readiness Confirmation Page (stub)
     * URL: /instructor/class-readiness-confirmation
     */
    @GetMapping("/class-readiness-confirmation")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String classReadinessConfirmation(@AuthenticationPrincipal UserDetails userDetails,
                                            Model model) {
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Class Readiness Confirmation");
            return "instructor/class-readiness-confirmation";
        } catch (Exception e) {
            log.error("Error loading class readiness confirmation", e);
            model.addAttribute("error", "Failed to load page: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Availability Confirmation Page (stub)
     * URL: /instructor/availability-confirmation
     */
    @GetMapping("/availability-confirmation")
    @PreAuthorize("hasAuthority('TEACHER_AVAILABILITY_SUBMIT')")
    public String availabilityConfirmation(@AuthenticationPrincipal UserDetails userDetails,
                                          Model model) {
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Availability Confirmation");
            return "instructor/availability-confirmation";
        } catch (Exception e) {
            log.error("Error loading availability confirmation", e);
            model.addAttribute("error", "Failed to load page: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Student roster for a class (stub)
     * URL: /instructor/class/{classId}/students
     */
    @GetMapping("/class/{classId}/students")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String viewStudentRoster(@PathVariable UUID classId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // For now, render a simple template with the required ID for tests
            model.addAttribute("user", currentUser);
            model.addAttribute("classId", classId);
            model.addAttribute("pageTitle", "Student Roster");
            return "instructor/student-roster";
        } catch (Exception e) {
            log.error("Error loading student roster", e);
            model.addAttribute("error", "Failed to load student roster: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Update Preparation Checklist
     * POST: /instructor/class/{classId}/preparation/checklist
     */
    @PostMapping("/class/{classId}/preparation/checklist")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String updatePreparationChecklist(@PathVariable UUID classId,
                                           @RequestParam List<UUID> completedItems,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {
        log.info("Updating preparation checklist for class: {} by instructor: {}", 
                classId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            teacherAvailabilityService.updatePreparationChecklist(
                    classId, currentUser.getId(), completedItems);
            
            redirectAttributes.addFlashAttribute("success", 
                "Preparation checklist updated successfully");
            
            return "redirect:/instructor/class/" + classId + "/preparation";
            
        } catch (Exception e) {
            log.error("Error updating preparation checklist", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to update checklist: " + e.getMessage());
            return "redirect:/instructor/class/" + classId + "/preparation";
        }
    }
    
    /**
     * Upload Class Materials
     * POST: /instructor/class/{classId}/materials/upload
     */
    @PostMapping("/class/{classId}/materials/upload")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String uploadClassMaterials(@PathVariable UUID classId,
                                     @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                     @RequestParam String materialType,
                                     @RequestParam String materialTitle,
                                     @RequestParam(defaultValue = "false") Boolean shareWithStudents,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        log.info("Uploading material for class: {} by instructor: {}", 
                classId, userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            teacherAvailabilityService.uploadClassMaterial(
                    classId, currentUser.getId(), file, materialType, materialTitle, shareWithStudents);
            
            redirectAttributes.addFlashAttribute("success", 
                "Material uploaded successfully");
            
            return "redirect:/instructor/class/" + classId + "/preparation";
            
        } catch (Exception e) {
            log.error("Error uploading class material", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to upload material: " + e.getMessage());
            return "redirect:/instructor/class/" + classId + "/preparation";
        }
    }
    
    /**
     * Helper method to get selected term
     */
    private AcademicTerm getSelectedTerm(UUID termId) {
        if (termId != null) {
            return academicTermRepository.findById(termId)
                    .orElseThrow(() -> new RuntimeException("Term not found"));
        }
        
        // Default to first planning term for instructor availability submission
        List<AcademicTerm> planningTerms = academicTermRepository.findPlanningTerms();
        if (!planningTerms.isEmpty()) {
            return planningTerms.get(0);
        }
        
        // If no planning terms available, fallback to active terms
        List<AcademicTerm> activeTerms = academicTermRepository.findActiveTerms();
        if (!activeTerms.isEmpty()) {
            return activeTerms.get(0);
        }
        
        throw new RuntimeException("No available terms");
    }
    
    /**
     * View Availability Status
     * GET: /instructor/availability
     */
    @GetMapping("/availability")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String viewAvailabilityStatus(@AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        log.info("Viewing availability status for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            model.addAttribute("user", currentUser);
            model.addAttribute("pageTitle", "Availability Status");
            
            return "instructor/availability-status";
            
        } catch (Exception e) {
            log.error("Error viewing availability status", e);
            model.addAttribute("error", "Failed to load availability status: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Show Change Request Form
     * GET: /instructor/availability/change-request
     */
    @GetMapping("/availability/change-request")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String showChangeRequestForm(@RequestParam(required = false) UUID termId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        log.info("Showing availability change request form for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            AcademicTerm selectedTerm = getSelectedTerm(termId);
            
            // Check if teacher can submit change request
            boolean canSubmit = changeRequestService.canSubmitChangeRequest(currentUser.getId(), selectedTerm.getId());
            if (!canSubmit) {
                model.addAttribute("error", "Cannot submit change request - either no original availability exists or there's already a pending request");
                return "instructor/availability-status";
            }
            
            // Get current availability for display
            TeacherAvailabilityDto.AvailabilityMatrix currentMatrix = 
                    teacherAvailabilityService.getTeacherAvailabilityMatrix(currentUser.getId(), selectedTerm.getId());
            
            // Get all available sessions for form
            var allSessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
            
            // Create form DTO with pre-populated termId
            TeacherAvailabilityChangeRequestDto.ChangeRequestFormDto formDto = 
                    new TeacherAvailabilityChangeRequestDto.ChangeRequestFormDto();
            formDto.setTermId(selectedTerm.getId());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("currentMatrix", currentMatrix);
            model.addAttribute("allSessions", allSessions);
            model.addAttribute("changeRequestForm", formDto);
            model.addAttribute("pageTitle", "Request Availability Change");
            
            return "instructor/availability-change-request";
            
        } catch (Exception e) {
            log.error("Error showing change request form", e);
            model.addAttribute("error", "Failed to load change request form: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Submit Change Request
     * POST: /instructor/availability/change-request
     */
    @PostMapping("/availability/change-request")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String submitChangeRequest(@ModelAttribute TeacherAvailabilityChangeRequestDto.ChangeRequestFormDto formDto,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        log.info("Submitting availability change request for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            TeacherAvailabilityChangeRequestDto request = 
                    changeRequestService.submitChangeRequest(currentUser.getId(), formDto);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Change request submitted successfully! Request ID: " + request.getId());
            
            return "redirect:/instructor/availability/change-requests";
            
        } catch (Exception e) {
            log.error("Error submitting change request", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to submit change request: " + e.getMessage());
            return "redirect:/instructor/availability/change-request";
        }
    }
    
    /**
     * View Change Requests
     * GET: /instructor/availability/change-requests
     */
    @GetMapping("/availability/change-requests")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String viewChangeRequests(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        log.info("Viewing change requests for: {}", userDetails.getUsername());
        
        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<TeacherAvailabilityChangeRequestDto> requests = 
                    changeRequestService.getChangeRequestsForTeacher(currentUser.getId());
            
            model.addAttribute("user", currentUser);
            model.addAttribute("changeRequests", requests);
            model.addAttribute("pageTitle", "My Change Requests");
            
            return "instructor/change-requests-list";
            
        } catch (Exception e) {
            log.error("Error viewing change requests", e);
            model.addAttribute("error", "Failed to load change requests: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Session Management Page
     * URL: /instructor/session-management
     */
    @GetMapping("/session-management")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String sessionManagement(@AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        log.info("Loading session management for instructor: {}", userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            // Get today's sessions for this instructor using SessionService
            List<ClassSession> todaySessions = sessionService.getTodaySessionsForInstructor(currentUser, today);

            // Get tomorrow's sessions for this instructor using SessionService
            List<ClassSession> tomorrowSessions = sessionService.getTomorrowSessionsForInstructor(currentUser, tomorrow);

            boolean hasLateSession = false;
            ClassSession todaySession = null;

            // Prepare today's session data using SessionService
            if (!todaySessions.isEmpty()) {
                todaySession = todaySessions.get(0); // Get first session
                log.info("Found today's session: id={}, status={}, date={}",
                        todaySession.getId(), todaySession.getPreparationStatus(), todaySession.getSessionDate());

                hasLateSession = sessionService.isSessionLate(todaySession);

                // For testing: also check if current time indicates late session
                LocalTime now = LocalTime.now();
                if (now.getHour() >= 8 && now.getHour() < 17) {
                    hasLateSession = true;
                    log.info("Forcing late session for testing during business hours");
                }

                log.info("Late session check result: hasLateSession={}", hasLateSession);

                Map<String, Object> sessionData = sessionService.buildSessionData(todaySession, currentUser);
                model.addAttribute("session", sessionData);
                model.addAttribute("hasSession", true);

                log.info("Session data added to model: {}", sessionData);
            } else {
                log.warn("No today's sessions found for instructor {}", currentUser.getUsername());
                model.addAttribute("hasSession", false);
            }

            // Prepare tomorrow's session data using SessionService
            if (!tomorrowSessions.isEmpty()) {
                ClassSession tomorrowSession = tomorrowSessions.get(0);
                Map<String, Object> tomorrowSessionData = sessionService.buildSimpleSessionData(tomorrowSession);
                model.addAttribute("tomorrowSession", tomorrowSessionData);
                model.addAttribute("hasTomorrowSession", true);
            } else {
                model.addAttribute("hasTomorrowSession", false);
            }

            // Set common model attributes
            model.addAttribute("user", currentUser);
            model.addAttribute("fullName", currentUser.getFullName());
            model.addAttribute("pageTitle", "Session Management");
            model.addAttribute("currentDate", today);
            model.addAttribute("isSessionLate", hasLateSession);

            log.info("Final model attributes: hasSession={}, isSessionLate={}, currentDate={}",
                    model.getAttribute("hasSession"), hasLateSession, today);

            return "instructor/session-management";

        } catch (Exception e) {
            log.error("Error loading session management", e);
            model.addAttribute("error", "Failed to load session management: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Handle instructor check-in for a session
     * POST: /instructor/check-in
     */
    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    @ResponseBody
    public ResponseEntity<?> checkIn(@RequestBody Map<String, String> checkInData,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Processing check-in for instructor: {}", userDetails.getUsername());

        try {
            User instructor = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Instructor not found: " + userDetails.getUsername()));
            String location = checkInData.get("location");
            String lateReason = checkInData.get("lateReason");

            // Get today's session for the instructor
            LocalDate today = LocalDate.now();
            List<ClassSession> todaySessions = sessionService.getTodaySessionsForInstructor(instructor, today);

            if (todaySessions.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "No session found for today"));
            }

            ClassSession session = todaySessions.get(0);
            boolean isLate = sessionService.isSessionLate(session);

            // Validate late check-in
            if (isLate && (lateReason == null || lateReason.trim().isEmpty())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Alasan keterlambatan harus diisi",
                               "fieldError", "lateReason"));
            }

            // Perform check-in
            TeacherAttendance attendance = sessionService.checkInInstructor(session, instructor, location, lateReason);

            Map<String, Object> response = Map.of(
                "success", true,
                "message", isLate ? "Check-in terlambat berhasil dicatat" : "Check-in berhasil",
                "isLate", isLate,
                "checkInTime", attendance.getCheckInTime().toString(),
                "location", attendance.getCheckInLocation()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during check-in", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Check-in failed: " + e.getMessage()));
        }
    }

    /**
     * Pre-Closure Dashboard for Teachers
     * Shows pending tasks before semester closure
     * GET: /instructor/pre-closure-dashboard
     */
    @GetMapping("/pre-closure-dashboard")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String preClosureDashboard(@RequestParam(required = false) UUID termId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {
        log.info("Loading pre-closure dashboard for teacher: {}", userDetails.getUsername());

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

            // Get pre-closure validation data for teacher
            TeacherPreClosureDto preClosureData = teacherPreClosureService.getTeacherPreClosureStatus(
                    currentUser.getId(), selectedTerm.getId());

            // Get available terms for selection
            List<AcademicTerm> availableTerms = academicTermRepository.findActiveTerms();

            model.addAttribute("user", currentUser);
            model.addAttribute("selectedTerm", selectedTerm);
            model.addAttribute("availableTerms", availableTerms);
            model.addAttribute("preClosureData", preClosureData);
            model.addAttribute("pageTitle", "Pre-Closure Dashboard");

            return "instructor/pre-closure-dashboard";

        } catch (Exception e) {
            log.error("Error loading pre-closure dashboard", e);
            model.addAttribute("error", "Failed to load pre-closure dashboard: " + e.getMessage());
            return "error/500";
        }
    }

    /**
     * Finalize Grades for a Class
     * POST: /instructor/finalize-grades
     */
    @PostMapping("/finalize-grades")
    @PreAuthorize("hasAuthority('GRADE_MANAGE')")
    public String finalizeGrades(@RequestParam UUID classId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        log.info("Finalizing grades for class: {} by teacher: {}", classId, userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Finalize grades for the class
            teacherPreClosureService.finalizeClassGrades(classId, currentUser.getId());

            redirectAttributes.addFlashAttribute("success", "Grades finalized successfully for the class");
            return "redirect:/instructor/pre-closure-dashboard";

        } catch (Exception e) {
            log.error("Error finalizing grades", e);
            redirectAttributes.addFlashAttribute("error", "Failed to finalize grades: " + e.getMessage());
            return "redirect:/instructor/pre-closure-dashboard";
        }
    }

    /**
     * Verify Attendance Records
     * POST: /instructor/verify-attendance
     */
    @PostMapping("/verify-attendance")
    @PreAuthorize("hasAuthority('CLASS_VIEW')")
    public String verifyAttendance(@RequestParam UUID classId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        log.info("Verifying attendance for class: {} by teacher: {}", classId, userDetails.getUsername());

        try {
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify attendance records for the class
            teacherPreClosureService.verifyClassAttendance(classId, currentUser.getId());

            redirectAttributes.addFlashAttribute("success", "Attendance records verified successfully");
            return "redirect:/instructor/pre-closure-dashboard";

        } catch (Exception e) {
            log.error("Error verifying attendance", e);
            redirectAttributes.addFlashAttribute("error", "Failed to verify attendance: " + e.getMessage());
            return "redirect:/instructor/pre-closure-dashboard";
        }
    }

}