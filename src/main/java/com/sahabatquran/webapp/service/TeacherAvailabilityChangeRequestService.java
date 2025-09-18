package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.TeacherAvailabilityChangeRequestDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherAvailabilityChangeRequestService {
    
    private final TeacherAvailabilityChangeRequestRepository changeRequestRepository;
    private final TeacherAvailabilityRepository availabilityRepository;
    private final AcademicTermRepository termRepository;
        private final UserRepository userRepository;
        private final SessionRepository sessionRepository;
        private final TimeSlotRepository timeSlotRepository;
    
    /**
     * Check if teacher can submit a change request for the term
     */
    public boolean canSubmitChangeRequest(UUID teacherId, UUID termId) {
        // Check if teacher has already submitted availability
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        AcademicTerm term = termRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        List<TeacherAvailability> existingAvailability = availabilityRepository
                .findByTeacherAndTerm(teacher, term);
        
        if (existingAvailability.isEmpty()) {
            return false; // No original availability to change
        }
        
        // Check if there's already a pending request
        return changeRequestRepository
                .findPendingRequestByTeacherAndTerm(teacherId, termId)
                .isEmpty();
    }
    
    /**
     * Get teacher's current availability for comparison
     */
    public com.sahabatquran.webapp.dto.TeacherAvailabilityDto.AvailabilityMatrix getCurrentAvailability(UUID teacherId, UUID termId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        AcademicTerm term = termRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        List<TeacherAvailability> availabilities = availabilityRepository
                .findByTeacherAndTerm(teacher, term);
        
        // Convert to DTO format for display
        return com.sahabatquran.webapp.dto.TeacherAvailabilityDto.AvailabilityMatrix.builder()
                .maxClassesPerWeek(availabilities.isEmpty() ? 6 : availabilities.get(0).getMaxClassesPerWeek())
                .preferences(availabilities.isEmpty() ? "" : availabilities.get(0).getPreferences())
                .build();
    }
    
    /**
     * Submit a change request
     */
    @Transactional
    public TeacherAvailabilityChangeRequestDto submitChangeRequest(
            UUID teacherId, 
            TeacherAvailabilityChangeRequestDto.ChangeRequestFormDto formDto) {
        
        log.info("Submitting change request for teacher: {} and term: {}", teacherId, formDto.getTermId());
        
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        AcademicTerm term = termRepository.findById(formDto.getTermId())
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        // Validate that teacher can submit change request
        if (!canSubmitChangeRequest(teacherId, formDto.getTermId())) {
            throw new RuntimeException("Cannot submit change request - either no original availability or pending request exists");
        }
        
        // Get current availability for comparison
        List<TeacherAvailability> currentAvailability = availabilityRepository
                .findByTeacherAndTerm(teacher, term);
        
        Integer originalMaxClasses = currentAvailability.isEmpty() ? 6 : 
                currentAvailability.get(0).getMaxClassesPerWeek();
        
        // Build requested changes
        List<TeacherAvailabilityChangeRequest.AvailabilityChange> changes = new ArrayList<>();
        
        // Process slots to add
        if (formDto.getSlotsToAdd() != null) {
            for (String slotKey : formDto.getSlotsToAdd()) {
                String[] parts = slotKey.split("-");
                if (parts.length == 2) {
                    Session session = sessionRepository.findByCodeAndIsActiveTrue(parts[1])
                            .orElseThrow(() -> new RuntimeException("Session not found: " + parts[1]));
                    
                    TeacherAvailabilityChangeRequest.AvailabilityChange change = 
                            new TeacherAvailabilityChangeRequest.AvailabilityChange();
                    change.setChangeType("ADD");
                    change.setDayOfWeek(parts[0]);
                    change.setSessionCode(parts[1]);
                    change.setSessionName(session.getName());
                    change.setNewAvailability(true);
                    change.setDescription("Add availability for " + parts[0] + " " + session.getName());
                    changes.add(change);
                }
            }
        }
        
        // Process slots to remove
        if (formDto.getSlotsToRemove() != null) {
            for (String slotKey : formDto.getSlotsToRemove()) {
                String[] parts = slotKey.split("-");
                if (parts.length == 2) {
                    Session session = sessionRepository.findByCodeAndIsActiveTrue(parts[1])
                            .orElseThrow(() -> new RuntimeException("Session not found: " + parts[1]));
                    
                    TeacherAvailabilityChangeRequest.AvailabilityChange change = 
                            new TeacherAvailabilityChangeRequest.AvailabilityChange();
                    change.setChangeType("REMOVE");
                    change.setDayOfWeek(parts[0]);
                    change.setSessionCode(parts[1]);
                    change.setSessionName(session.getName());
                    change.setNewAvailability(false);
                    change.setDescription("Remove availability for " + parts[0] + " " + session.getName());
                    changes.add(change);
                }
            }
        }
        
        // Create change request entity
        TeacherAvailabilityChangeRequest changeRequest = new TeacherAvailabilityChangeRequest();
        changeRequest.setTeacher(teacher);
        changeRequest.setTerm(term);
        changeRequest.setReason(formDto.getReason());
        changeRequest.setRequestedChanges(changes);
        changeRequest.setOriginalMaxClasses(originalMaxClasses);
        changeRequest.setNewMaxClasses(formDto.getNewMaxClasses() != null ? 
                formDto.getNewMaxClasses() : originalMaxClasses);
        changeRequest.setStatus(TeacherAvailabilityChangeRequest.RequestStatus.PENDING);
        
        changeRequest = changeRequestRepository.save(changeRequest);
        
        log.info("Change request submitted successfully with ID: {}", changeRequest.getId());
        
        return convertToDto(changeRequest);
    }
    
    /**
     * Get change requests for a teacher
     */
    public List<TeacherAvailabilityChangeRequestDto> getChangeRequestsForTeacher(UUID teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        List<TeacherAvailabilityChangeRequest> requests = changeRequestRepository
                .findByTeacherOrderBySubmittedAtDesc(teacher);
        
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get pending change requests for admin review
     */
    public List<TeacherAvailabilityChangeRequestDto> getPendingChangeRequests() {
        List<TeacherAvailabilityChangeRequest> requests = changeRequestRepository
                .findByStatusOrderBySubmittedAtAsc(TeacherAvailabilityChangeRequest.RequestStatus.PENDING);
        
        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Approve a change request
     */
    @Transactional
    public void approveChangeRequest(UUID requestId, UUID reviewerId, String reviewComments) {
        log.info("Approving change request: {} by reviewer: {}", requestId, reviewerId);
        
        TeacherAvailabilityChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Change request not found"));
        
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        
        if (request.getStatus() != TeacherAvailabilityChangeRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be approved");
        }
        
        // Apply the changes to actual availability
        applyChangesToAvailability(request);
        
        // Update request status
        request.setStatus(TeacherAvailabilityChangeRequest.RequestStatus.APPROVED);
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewComments(reviewComments);
        
        changeRequestRepository.save(request);
        
        log.info("Change request {} approved successfully", requestId);
    }
    
    /**
     * Reject a change request
     */
    @Transactional
    public void rejectChangeRequest(UUID requestId, UUID reviewerId, String reviewComments) {
        log.info("Rejecting change request: {} by reviewer: {}", requestId, reviewerId);
        
        TeacherAvailabilityChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Change request not found"));
        
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        
        if (request.getStatus() != TeacherAvailabilityChangeRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be rejected");
        }
        
        request.setStatus(TeacherAvailabilityChangeRequest.RequestStatus.REJECTED);
        request.setReviewedBy(reviewer);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewComments(reviewComments);
        
        changeRequestRepository.save(request);
        
        log.info("Change request {} rejected", requestId);
    }
    
    /**
     * Apply approved changes to actual teacher availability
     */
    private void applyChangesToAvailability(TeacherAvailabilityChangeRequest request) {
        for (TeacherAvailabilityChangeRequest.AvailabilityChange change : request.getRequestedChanges()) {
            Session session = sessionRepository.findByCodeAndIsActiveTrue(change.getSessionCode())
                    .orElseThrow(() -> new RuntimeException("Session not found: " + change.getSessionCode()));
            
            // Determine timeslot for this change
            var tsDay = com.sahabatquran.webapp.entity.TimeSlot.DayOfWeek.valueOf(change.getDayOfWeek());
            var timeSlot = timeSlotRepository.findBySessionAndDayOfWeek(session, tsDay)
                            .orElseGet(() -> {
                                    var ts = new com.sahabatquran.webapp.entity.TimeSlot();
                                    ts.setSession(session);
                                    ts.setDayOfWeek(tsDay);
                                    return timeSlotRepository.save(ts);
                            });

            // Find existing availability record by timeslot
            List<TeacherAvailability> existingRecords = availabilityRepository
                    .findByTeacherAndTerm(request.getTeacher(), request.getTerm());
            
            TeacherAvailability targetRecord = existingRecords.stream()
                    .filter(a -> a.getTimeSlot() != null && a.getTimeSlot().equals(timeSlot))
                    .findFirst()
                    .orElse(null);
            
            if (targetRecord != null) {
                // Update existing record
                targetRecord.setIsAvailable(change.getNewAvailability());
                targetRecord.setMaxClassesPerWeek(request.getNewMaxClasses());
                targetRecord.setTimeSlot(timeSlot);
                availabilityRepository.save(targetRecord);
            } else if (change.getNewAvailability()) {
                // Create new availability record for ADD operations
                TeacherAvailability newRecord = new TeacherAvailability();
                newRecord.setTeacher(request.getTeacher());
                newRecord.setTerm(request.getTerm());
                newRecord.setIsAvailable(true);
                newRecord.setMaxClassesPerWeek(request.getNewMaxClasses());
                newRecord.setSubmittedAt(LocalDateTime.now());
                newRecord.setTimeSlot(timeSlot);
                
                availabilityRepository.save(newRecord);
            }
        }
    }
    
    /**
     * Convert entity to DTO
     */
    private TeacherAvailabilityChangeRequestDto convertToDto(TeacherAvailabilityChangeRequest entity) {
        TeacherAvailabilityChangeRequestDto dto = new TeacherAvailabilityChangeRequestDto();
        dto.setId(entity.getId());
        dto.setTeacherId(entity.getTeacher().getId());
        dto.setTeacherName(entity.getTeacher().getFullName());
        dto.setTermId(entity.getTerm().getId());
        dto.setTermName(entity.getTerm().getTermName());
        dto.setReason(entity.getReason());
        dto.setOriginalMaxClasses(entity.getOriginalMaxClasses());
        dto.setNewMaxClasses(entity.getNewMaxClasses());
        dto.setStatus(entity.getStatus());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setReviewComments(entity.getReviewComments());
        
        if (entity.getReviewedBy() != null) {
            dto.setReviewedByName(entity.getReviewedBy().getFullName());
        }
        
        // Convert requested changes
        if (entity.getRequestedChanges() != null) {
            List<TeacherAvailabilityChangeRequestDto.AvailabilityChangeDto> changeDtos = 
                    entity.getRequestedChanges().stream()
                    .map(change -> {
                        TeacherAvailabilityChangeRequestDto.AvailabilityChangeDto changeDto = 
                                new TeacherAvailabilityChangeRequestDto.AvailabilityChangeDto();
                        changeDto.setChangeType(change.getChangeType());
                        changeDto.setDayOfWeek(change.getDayOfWeek());
                        changeDto.setSessionCode(change.getSessionCode());
                        changeDto.setSessionName(change.getSessionName());
                        changeDto.setNewAvailability(change.getNewAvailability());
                        changeDto.setDescription(change.getDescription());
                        return changeDto;
                    })
                    .collect(Collectors.toList());
            dto.setRequestedChanges(changeDtos);
        }
        
        return dto;
    }
}