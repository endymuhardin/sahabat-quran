package com.sahabatquran.webapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahabatquran.webapp.dto.*;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentRegistrationService {
    
    private final StudentRegistrationRepository registrationRepository;
    private final StudentSessionPreferenceRepository sessionPreferenceRepository;
    private final ProgramRepository programRepository;
    private final LevelRepository levelRepository;
    private final SessionRepository sessionRepository;
    private final PlacementTestVerseRepository placementVerseRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public StudentRegistrationResponse createRegistration(StudentRegistrationRequest request) {
        log.info("Creating new student registration for email: {}", request.getEmail());
        
        // Validate email and phone uniqueness
        validateUniqueConstraints(request.getEmail(), request.getPhoneNumber(), null);
        
        // Get program
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new IllegalArgumentException("Program tidak ditemukan"));
        
        // Create registration entity
        StudentRegistration registration = mapToEntity(request, program);
        
        // Assign random placement test verse
        assignPlacementTestVerse(registration);
        
        // Save registration
        registration = registrationRepository.save(registration);
        
    // Save session preferences (convert day-of-week + session to TimeSlot)
    saveSessionPreferences(registration, request.getSessionPreferences());
        
        log.info("Student registration created successfully with ID: {}", registration.getId());
        return mapToResponse(registration);
    }
    
    @Transactional
    public StudentRegistrationResponse updateRegistration(UUID registrationId, StudentRegistrationRequest request) {
        log.info("Updating student registration with ID: {}", registrationId);
        
        StudentRegistration registration = getRegistrationById(registrationId);
        
        // Only allow updates for DRAFT registrations
        if (registration.getRegistrationStatus() != StudentRegistration.RegistrationStatus.DRAFT) {
            throw new IllegalStateException("Hanya pendaftaran dengan status DRAFT yang dapat diubah");
        }
        
        // Validate email and phone uniqueness (excluding current registration)
        validateUniqueConstraints(request.getEmail(), request.getPhoneNumber(), registrationId);
        
        // Update fields
        updateRegistrationFields(registration, request);
        
        // Update session preferences
        sessionPreferenceRepository.deleteByRegistrationId(registrationId);
    saveSessionPreferences(registration, request.getSessionPreferences());
        
        registration = registrationRepository.save(registration);
        
        log.info("Student registration updated successfully");
        return mapToResponse(registration);
    }
    
    @Transactional
    public StudentRegistrationResponse submitRegistration(UUID registrationId) {
        log.info("Submitting student registration with ID: {}", registrationId);
        
        StudentRegistration registration = getRegistrationById(registrationId);
        
        if (registration.getRegistrationStatus() != StudentRegistration.RegistrationStatus.DRAFT) {
            throw new IllegalStateException("Hanya pendaftaran dengan status DRAFT yang dapat disubmit");
        }
        
        // Update status
        registration.setRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
        registration.setSubmittedAt(LocalDateTime.now());
        
        registration = registrationRepository.save(registration);
        
        log.info("Student registration submitted successfully");
        return mapToResponse(registration);
    }
    
    @Transactional
    public StudentRegistrationResponse reviewRegistration(RegistrationReviewRequest request) {
        log.info("Reviewing student registration with ID: {}", request.getRegistrationId());
        
        StudentRegistration registration = getRegistrationById(request.getRegistrationId());
        
        // Update review information
        registration.setRegistrationStatus(request.getNewStatus());
        registration.setReviewedAt(LocalDateTime.now());
        registration.setReviewedBy(null); // Will be handled by entity auditing later
        registration.setReviewNotes(request.getReviewNotes());
        
        registration = registrationRepository.save(registration);
        
        log.info("Student registration reviewed successfully");
        return mapToResponse(registration);
    }
    
    @Transactional
    public StudentRegistrationResponse evaluatePlacementTest(PlacementTestEvaluationRequest request) {
        log.info("Evaluating placement test for registration ID: {}", request.getRegistrationId());
        
        StudentRegistration registration = getRegistrationById(request.getRegistrationId());
        
        if (registration.getPlacementTestStatus() != StudentRegistration.PlacementTestStatus.SUBMITTED) {
            throw new IllegalStateException("Placement test belum disubmit");
        }
        
        // Update placement test results
        registration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.EVALUATED);
        registration.setPlacementResult(request.getPlacementResult());
        registration.setPlacementNotes(request.getEvaluationNotes());
        registration.setPlacementEvaluatedBy(null); // Will be handled by entity auditing later
        registration.setPlacementEvaluatedAt(LocalDateTime.now());
        
        registration = registrationRepository.save(registration);
        
        log.info("Placement test evaluated successfully");
        return mapToResponse(registration);
    }
    
    public StudentRegistrationResponse getRegistration(UUID registrationId) {
        StudentRegistration registration = getRegistrationById(registrationId);
        return mapToResponse(registration);
    }
    
    public Page<StudentRegistrationResponse> searchRegistrations(RegistrationSearchRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<StudentRegistration> registrations = registrationRepository.searchRegistrations(
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getRegistrationStatus(),
                request.getProgramId(),
                pageable
        );
        
        return registrations.map(this::mapToResponse);
    }
    
    public List<Program> getActivePrograms() {
        return programRepository.findByIsActiveTrue();
    }
    
    public List<Session> getActiveSessions() {
        return sessionRepository.findByIsActiveTrueOrderByStartTime();
    }
    
    public List<StudentRegistrationResponse> getPendingPlacementTests() {
        List<StudentRegistration> registrations = registrationRepository.findByPlacementTestStatus(
                StudentRegistration.PlacementTestStatus.SUBMITTED);
        return registrations.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    // Private helper methods
    
    private StudentRegistration getRegistrationById(UUID registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registrasi tidak ditemukan"));
    }
    
    private void validateUniqueConstraints(String email, String phoneNumber, UUID excludeId) {
        if (excludeId == null) {
            if (registrationRepository.existsByEmailAndRegistrationStatusNot(
                    email, StudentRegistration.RegistrationStatus.REJECTED)) {
                throw new IllegalArgumentException("Email sudah terdaftar");
            }
            if (registrationRepository.existsByPhoneNumberAndRegistrationStatusNot(
                    phoneNumber, StudentRegistration.RegistrationStatus.REJECTED)) {
                throw new IllegalArgumentException("Nomor telepon sudah terdaftar");
            }
        } else {
            Optional<StudentRegistration> existingByEmail = registrationRepository
                    .findByEmailAndRegistrationStatusNot(email, StudentRegistration.RegistrationStatus.REJECTED);
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(excludeId)) {
                throw new IllegalArgumentException("Email sudah terdaftar");
            }
            
            Optional<StudentRegistration> existingByPhone = registrationRepository
                    .findByPhoneNumberAndRegistrationStatusNot(phoneNumber, StudentRegistration.RegistrationStatus.REJECTED);
            if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(excludeId)) {
                throw new IllegalArgumentException("Nomor telepon sudah terdaftar");
            }
        }
    }
    
    private StudentRegistration mapToEntity(StudentRegistrationRequest request, Program program) {
        StudentRegistration registration = new StudentRegistration();
        
        // Personal Information
        registration.setFullName(request.getFullName());
        registration.setGender(request.getGender());
        registration.setDateOfBirth(request.getDateOfBirth());
        registration.setPlaceOfBirth(request.getPlaceOfBirth());
        registration.setPhoneNumber(request.getPhoneNumber());
        registration.setEmail(request.getEmail());
        registration.setAddress(request.getAddress());
        registration.setEmergencyContactName(request.getEmergencyContactName());
        registration.setEmergencyContactPhone(request.getEmergencyContactPhone());
        registration.setEmergencyContactRelation(request.getEmergencyContactRelation());
        
        // Educational Information
        registration.setEducationLevel(request.getEducationLevel());
        registration.setSchoolName(request.getSchoolName());
        registration.setQuranReadingExperience(request.getQuranReadingExperience());
        registration.setPreviousTahsinExperience(request.getPreviousTahsinExperience());
        registration.setPreviousTahsinDetails(request.getPreviousTahsinDetails());
        
        // Program Selection
        registration.setProgram(program);
        registration.setRegistrationReason(request.getRegistrationReason());
        registration.setLearningGoals(request.getLearningGoals());
        
        // Schedule Preferences (JSON)
        try {
            registration.setSchedulePreferences(objectMapper.writeValueAsString(request.getSessionPreferences()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing schedule preferences", e);
        }
        
        // Placement Test
        registration.setRecordingDriveLink(request.getRecordingDriveLink());
        
        return registration;
    }
    
    private void updateRegistrationFields(StudentRegistration registration, StudentRegistrationRequest request) {
        // Update personal information
        registration.setFullName(request.getFullName());
        registration.setGender(request.getGender());
        registration.setDateOfBirth(request.getDateOfBirth());
        registration.setPlaceOfBirth(request.getPlaceOfBirth());
        registration.setPhoneNumber(request.getPhoneNumber());
        registration.setEmail(request.getEmail());
        registration.setAddress(request.getAddress());
        registration.setEmergencyContactName(request.getEmergencyContactName());
        registration.setEmergencyContactPhone(request.getEmergencyContactPhone());
        registration.setEmergencyContactRelation(request.getEmergencyContactRelation());
        
        // Update educational information
        registration.setEducationLevel(request.getEducationLevel());
        registration.setSchoolName(request.getSchoolName());
        registration.setQuranReadingExperience(request.getQuranReadingExperience());
        registration.setPreviousTahsinExperience(request.getPreviousTahsinExperience());
        registration.setPreviousTahsinDetails(request.getPreviousTahsinDetails());
        
        // Update program if changed
        if (!registration.getProgram().getId().equals(request.getProgramId())) {
            Program program = programRepository.findById(request.getProgramId())
                    .orElseThrow(() -> new IllegalArgumentException("Program tidak ditemukan"));
            registration.setProgram(program);
        }
        
        // Update other fields
        registration.setRegistrationReason(request.getRegistrationReason());
        registration.setLearningGoals(request.getLearningGoals());
        registration.setRecordingDriveLink(request.getRecordingDriveLink());
        
        // Update schedule preferences JSON
        try {
            registration.setSchedulePreferences(objectMapper.writeValueAsString(request.getSessionPreferences()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing schedule preferences", e);
        }
    }
    
    private void assignPlacementTestVerse(StudentRegistration registration) {
        Optional<PlacementTestVerse> randomVerse = placementVerseRepository.findRandomVerse();
        randomVerse.ifPresent(registration::setPlacementVerse);
    }
    
    private void saveSessionPreferences(StudentRegistration registration,
                                       List<StudentRegistrationRequest.SessionPreferenceRequest> preferences) {
        for (StudentRegistrationRequest.SessionPreferenceRequest pref : preferences) {
            Session session = sessionRepository.findById(pref.getSessionId())
                    .orElseThrow(() -> new IllegalArgumentException("Session tidak ditemukan"));

            // For each preferred day, create a separate TimeSlot preference with the same priority order by listing sequence
            int basePriority = pref.getPriority();
            int offset = 0;
            for (StudentRegistrationRequest.DayOfWeek day : pref.getPreferredDays()) {
                TimeSlot.DayOfWeek tsDay = TimeSlot.DayOfWeek.valueOf(day.name());
                TimeSlot timeSlot = timeSlotRepository.findBySessionAndDayOfWeek(session, tsDay)
                        .orElseGet(() -> {
                            TimeSlot ts = new TimeSlot();
                            ts.setSession(session);
                            ts.setDayOfWeek(tsDay);
                            return timeSlotRepository.save(ts);
                        });

                StudentSessionPreference slotPref = new StudentSessionPreference();
                slotPref.setRegistration(registration);
                slotPref.setTimeSlot(timeSlot);
                slotPref.setPreferencePriority(basePriority + offset);
                sessionPreferenceRepository.save(slotPref);
                offset++;
            }
        }
    }
    
    private StudentRegistrationResponse mapToResponse(StudentRegistration registration) {
        StudentRegistrationResponse response = new StudentRegistrationResponse();
        
        response.setId(registration.getId());
        response.setFullName(registration.getFullName());
        response.setGender(registration.getGender());
        response.setDateOfBirth(registration.getDateOfBirth());
        response.setPlaceOfBirth(registration.getPlaceOfBirth());
        response.setPhoneNumber(registration.getPhoneNumber());
        response.setEmail(registration.getEmail());
        response.setAddress(registration.getAddress());
        response.setEmergencyContactName(registration.getEmergencyContactName());
        response.setEmergencyContactPhone(registration.getEmergencyContactPhone());
        response.setEmergencyContactRelation(registration.getEmergencyContactRelation());
        response.setEducationLevel(registration.getEducationLevel());
        response.setSchoolName(registration.getSchoolName());
        response.setQuranReadingExperience(registration.getQuranReadingExperience());
        response.setPreviousTahsinExperience(registration.getPreviousTahsinExperience());
        response.setPreviousTahsinDetails(registration.getPreviousTahsinDetails());
        response.setRegistrationReason(registration.getRegistrationReason());
        response.setLearningGoals(registration.getLearningGoals());
        response.setRegistrationStatus(registration.getRegistrationStatus());
        response.setSubmittedAt(registration.getSubmittedAt());
        response.setReviewedAt(registration.getReviewedAt());
        response.setReviewNotes(registration.getReviewNotes());
        response.setCreatedAt(registration.getCreatedAt());
        response.setUpdatedAt(registration.getUpdatedAt());
        
        // Map program info
        if (registration.getProgram() != null) {
            StudentRegistrationResponse.ProgramInfo programInfo = new StudentRegistrationResponse.ProgramInfo();
            programInfo.setId(registration.getProgram().getId());
            programInfo.setCode(registration.getProgram().getCode());
            programInfo.setName(registration.getProgram().getName());
            programInfo.setDescription(registration.getProgram().getDescription());
            if (registration.getProgram().getLevel() != null) {
                programInfo.setLevelOrder(registration.getProgram().getLevel().getOrderNumber());
            }
            response.setProgram(programInfo);
        }
        
        // Map session preferences
    List<StudentSessionPreference> preferences = sessionPreferenceRepository
        .findByRegistrationIdOrderByPreferencePriority(registration.getId());
    response.setSessionPreferences(preferences.stream().map(this::mapSessionPreference).collect(Collectors.toList()));
        
        // Map placement test info
        if (registration.getPlacementVerse() != null) {
            response.setPlacementTest(mapPlacementTestInfo(registration));
        }
        
        // Map reviewed by name
        if (registration.getReviewedBy() != null) {
            response.setReviewedByName(registration.getReviewedBy().getFullName());
        }
        
        // Map teacher assignment fields
        if (registration.getAssignedTeacher() != null) {
            response.setAssignedTeacherId(registration.getAssignedTeacher().getId());
            response.setAssignedTeacherName(registration.getAssignedTeacher().getFullName());
        }
        response.setAssignedAt(registration.getAssignedAt());
        if (registration.getAssignedBy() != null) {
            response.setAssignedByName(registration.getAssignedBy().getFullName());
        }
        response.setTeacherReviewStatus(registration.getTeacherReviewStatus());
        response.setTeacherRemarks(registration.getTeacherRemarks());
        
        return response;
    }
    
    private StudentRegistrationResponse.SessionPreferenceInfo mapSessionPreference(StudentSessionPreference pref) {
        StudentRegistrationResponse.SessionPreferenceInfo info = new StudentRegistrationResponse.SessionPreferenceInfo();
        info.setId(pref.getId());
        info.setPriority(pref.getPreferencePriority());

        // Map session info via TimeSlot
        if (pref.getTimeSlot() != null && pref.getTimeSlot().getSession() != null) {
            StudentRegistrationResponse.SessionInfo sessionInfo = new StudentRegistrationResponse.SessionInfo();
            sessionInfo.setId(pref.getTimeSlot().getSession().getId());
            sessionInfo.setCode(pref.getTimeSlot().getSession().getCode());
            sessionInfo.setName(pref.getTimeSlot().getSession().getName());
            sessionInfo.setStartTime(pref.getTimeSlot().getSession().getStartTime().toString());
            sessionInfo.setEndTime(pref.getTimeSlot().getSession().getEndTime().toString());
            info.setSession(sessionInfo);
            info.setPreferredDays(List.of(pref.getTimeSlot().getDayOfWeek().name()));
        }

        return info;
    }
    
    private StudentRegistrationResponse.PlacementTestInfo mapPlacementTestInfo(StudentRegistration registration) {
        StudentRegistrationResponse.PlacementTestInfo testInfo = new StudentRegistrationResponse.PlacementTestInfo();
        
        PlacementTestVerse verse = registration.getPlacementVerse();
        testInfo.setVerseId(verse.getId());
        testInfo.setSurahName(verse.getSurahName());
        testInfo.setSurahNumber(verse.getSurahNumber());
        testInfo.setAyahStart(verse.getAyahStart());
        testInfo.setAyahEnd(verse.getAyahEnd());
        testInfo.setArabicText(verse.getArabicText());
        testInfo.setDifficultyLevel(verse.getDifficultyLevel());
        
        testInfo.setRecordingDriveLink(registration.getRecordingDriveLink());
        testInfo.setStatus(registration.getPlacementTestStatus());
        testInfo.setResult(registration.getPlacementResult());
        testInfo.setNotes(registration.getPlacementNotes());
        testInfo.setEvaluatedAt(registration.getPlacementEvaluatedAt());
        
        if (registration.getPlacementEvaluatedBy() != null) {
            testInfo.setEvaluatedByName(registration.getPlacementEvaluatedBy().getFullName());
        }
        
        return testInfo;
    }
    
    // New methods for teacher assignment workflow
    
    @Transactional
    public StudentRegistrationResponse assignTeacherToRegistration(TeacherAssignmentRequest request, User assignedBy) {
        log.info("Assigning teacher {} to registration {} by user {}", request.getTeacherId(), request.getRegistrationId(), assignedBy.getId());
        
        StudentRegistration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("Registrasi tidak ditemukan"));
        
        if (registration.getRegistrationStatus() != StudentRegistration.RegistrationStatus.SUBMITTED) {
            throw new IllegalStateException("Hanya registrasi dengan status SUBMITTED yang dapat ditugaskan");
        }
        
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Guru tidak ditemukan"));
        
        registration.setAssignedTeacher(teacher);
        registration.setAssignedAt(LocalDateTime.now());
        registration.setAssignedBy(assignedBy);
        registration.setRegistrationStatus(StudentRegistration.RegistrationStatus.ASSIGNED);
        registration.setTeacherReviewStatus(StudentRegistration.TeacherReviewStatus.PENDING);
        
        StudentRegistration saved = registrationRepository.save(registration);
        return mapToResponse(saved);
    }
    
    @Transactional
    public StudentRegistrationResponse submitTeacherReview(TeacherReviewRequest request) {
        log.info("Teacher {} submitting review for registration {}", request.getTeacherId(), request.getRegistrationId());
        
        StudentRegistration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("Registrasi tidak ditemukan"));
        
        if (!registration.getAssignedTeacher().getId().equals(request.getTeacherId())) {
            throw new IllegalStateException("Hanya guru yang ditugaskan yang dapat mereview registrasi ini");
        }
        
        registration.setTeacherReviewStatus(request.getReviewStatus());
        registration.setTeacherRemarks(request.getTeacherRemarks());
        
        if (request.getRecommendedLevelId() != null) {
            Level recommendedLevel = levelRepository.findById(request.getRecommendedLevelId())
                    .orElseThrow(() -> new IllegalArgumentException("Level tidak ditemukan"));
            registration.setRecommendedLevel(recommendedLevel);
        }
        
        if (request.getReviewStatus() == StudentRegistration.TeacherReviewStatus.COMPLETED) {
            registration.setTeacherEvaluatedAt(LocalDateTime.now());
            registration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
            
            // Also update placement test if provided
            if (request.getPlacementTestResult() != null) {
                registration.setPlacementResult(request.getPlacementTestResult());
                registration.setPlacementNotes(request.getPlacementNotes());
                registration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.EVALUATED);
                registration.setPlacementEvaluatedBy(registration.getAssignedTeacher());
                registration.setPlacementEvaluatedAt(LocalDateTime.now());
            }
        }
        
        StudentRegistration saved = registrationRepository.save(registration);
        return mapToResponse(saved);
    }
    
    public List<StudentRegistrationResponse> getRegistrationsAssignedToTeacher(UUID teacherId) {
        log.info("Getting registrations assigned to teacher {}", teacherId);
        
        List<StudentRegistration> registrations = registrationRepository.findByAssignedTeacher_IdOrderByAssignedAtDesc(teacherId);
        return registrations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
}