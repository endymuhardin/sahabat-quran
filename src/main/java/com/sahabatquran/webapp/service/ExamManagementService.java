package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.ExamDto;
import com.sahabatquran.webapp.dto.ExamQuestionDto;
import com.sahabatquran.webapp.dto.ExamResultDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamManagementService {
    
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ClassGroupRepository classGroupRepository;
    private final AcademicTermRepository academicTermRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    // Exam Creation and Management
    
    public ExamDto createExam(ExamDto examDto, String createdByUsername) {
        log.info("Creating exam: {} for class: {}", examDto.getTitle(), examDto.getClassGroupId());
        
        User createdBy = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + createdByUsername));
        
        ClassGroup classGroup = classGroupRepository.findById(examDto.getClassGroupId())
                .orElseThrow(() -> new RuntimeException("Class group not found: " + examDto.getClassGroupId()));
        
        AcademicTerm academicTerm = academicTermRepository.findById(examDto.getAcademicTermId())
                .orElseThrow(() -> new RuntimeException("Academic term not found: " + examDto.getAcademicTermId()));
        
        // Check for scheduling conflicts
        List<Exam> conflicts = examRepository.findSchedulingConflicts(
                classGroup, 
                examDto.getScheduledStart(), 
                examDto.getScheduledEnd(), 
                null
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Exam scheduling conflict detected with exam: " + 
                                     conflicts.get(0).getTitle());
        }
        
        Exam exam = new Exam();
        exam.setTitle(examDto.getTitle());
        exam.setDescription(examDto.getDescription());
        exam.setExamType(Exam.ExamType.valueOf(examDto.getExamType()));
        exam.setClassGroup(classGroup);
        exam.setAcademicTerm(academicTerm);
        exam.setScheduledStart(examDto.getScheduledStart());
        exam.setScheduledEnd(examDto.getScheduledEnd());
        exam.setDurationMinutes(examDto.getDurationMinutes());
        exam.setMaxAttempts(examDto.getMaxAttempts() != null ? examDto.getMaxAttempts() : 1);
        exam.setPassingScore(examDto.getPassingScore() != null ? examDto.getPassingScore() : 60.0);
        exam.setInstructions(examDto.getInstructions());
        exam.setAllowReview(examDto.getAllowReview() != null ? examDto.getAllowReview() : true);
        exam.setRandomizeQuestions(examDto.getRandomizeQuestions() != null ? examDto.getRandomizeQuestions() : false);
        exam.setShowResultsImmediately(examDto.getShowResultsImmediately() != null ? examDto.getShowResultsImmediately() : false);
        exam.setCreatedBy(createdBy);
        exam.setStatus(Exam.ExamStatus.DRAFT);
        
        exam = examRepository.save(exam);
        log.info("Exam created successfully with ID: {}", exam.getId());
        
        return convertToDto(exam);
    }
    
    public ExamDto updateExam(UUID examId, ExamDto examDto, String updatedByUsername) {
        log.info("Updating exam: {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        // Check if exam has submitted results (prevent modification)
        if (examResultRepository.hasSubmittedResults(exam)) {
            throw new RuntimeException("Cannot modify exam with submitted results");
        }
        
        // Check for scheduling conflicts (excluding current exam)
        List<Exam> conflicts = examRepository.findSchedulingConflicts(
                exam.getClassGroup(), 
                examDto.getScheduledStart(), 
                examDto.getScheduledEnd(), 
                examId
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Exam scheduling conflict detected");
        }
        
        exam.setTitle(examDto.getTitle());
        exam.setDescription(examDto.getDescription());
        exam.setScheduledStart(examDto.getScheduledStart());
        exam.setScheduledEnd(examDto.getScheduledEnd());
        exam.setDurationMinutes(examDto.getDurationMinutes());
        exam.setMaxAttempts(examDto.getMaxAttempts());
        exam.setPassingScore(examDto.getPassingScore());
        exam.setInstructions(examDto.getInstructions());
        exam.setAllowReview(examDto.getAllowReview());
        exam.setRandomizeQuestions(examDto.getRandomizeQuestions());
        exam.setShowResultsImmediately(examDto.getShowResultsImmediately());
        
        exam = examRepository.save(exam);
        log.info("Exam updated successfully: {}", examId);
        
        return convertToDto(exam);
    }
    
    public void deleteExam(UUID examId) {
        log.info("Deleting exam: {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        // Check if exam has submitted results
        if (examResultRepository.hasSubmittedResults(exam)) {
            throw new RuntimeException("Cannot delete exam with submitted results");
        }
        
        examRepository.delete(exam);
        log.info("Exam deleted successfully: {}", examId);
    }
    
    // Exam Scheduling and Status Management
    
    public void scheduleExam(UUID examId) {
        log.info("Scheduling exam: {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        if (exam.getQuestions().isEmpty()) {
            throw new RuntimeException("Cannot schedule exam without questions");
        }
        
        // Validate exam configuration
        validateExamConfiguration(exam);
        
        exam.setStatus(Exam.ExamStatus.SCHEDULED);
        examRepository.save(exam);
        
        log.info("Exam scheduled successfully: {}", examId);
    }
    
    public void activateExam(UUID examId) {
        log.info("Activating exam: {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        if (exam.getStatus() != Exam.ExamStatus.SCHEDULED) {
            throw new RuntimeException("Only scheduled exams can be activated");
        }
        
        exam.setStatus(Exam.ExamStatus.ACTIVE);
        examRepository.save(exam);
        
        log.info("Exam activated successfully: {}", examId);
    }
    
    public void completeExam(UUID examId) {
        log.info("Completing exam: {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        
        // Auto-submit any in-progress results
        List<ExamResult> inProgressResults = examResultRepository.findByStatusOrderByCreatedAtDesc(
                ExamResult.ExamResultStatus.IN_PROGRESS);
        
        inProgressResults.stream()
                .filter(result -> result.getExam().getId().equals(examId))
                .forEach(result -> {
                    result.setCompletedAt(LocalDateTime.now());
                    result.setStatus(ExamResult.ExamResultStatus.TIME_EXPIRED);
                    result.setAutoSubmitted(true);
                    examResultRepository.save(result);
                });
        
        exam.setStatus(Exam.ExamStatus.COMPLETED);
        examRepository.save(exam);
        
        log.info("Exam completed successfully: {}", examId);
    }
    
    // Exam Retrieval
    
    @Transactional(readOnly = true)
    public ExamDto getExamById(UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));
        return convertToDto(exam);
    }
    
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsByClassGroup(UUID classGroupId) {
        ClassGroup classGroup = classGroupRepository.findById(classGroupId)
                .orElseThrow(() -> new RuntimeException("Class group not found: " + classGroupId));
        
        return examRepository.findByClassGroupOrderByScheduledStartDesc(classGroup)
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsForStudent(String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentUsername));
        
        return examRepository.findExamsForStudent(student)
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsCreatedByInstructor(String instructorUsername) {
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found: " + instructorUsername));
        
        return examRepository.findByCreatedByOrderByCreatedAtDesc(instructor)
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ExamDto> getActiveExams() {
        return examRepository.findActiveExams(LocalDateTime.now())
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsRequiringGrading() {
        return examRepository.findExamsRequiringGrading()
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    
    // Automatic Exam Status Management (to be called by scheduled tasks)
    
    public void processExamStatusTransitions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Activate scheduled exams whose start time has arrived
        List<Exam> examsToActivate = examRepository.findExamsToActivate(now);
        for (Exam exam : examsToActivate) {
            activateExam(exam.getId());
        }
        
        // Complete active exams whose end time has passed
        List<Exam> examsToComplete = examRepository.findExamsToComplete(now);
        for (Exam exam : examsToComplete) {
            completeExam(exam.getId());
        }
    }
    
    // Utility methods
    
    private void validateExamConfiguration(Exam exam) {
        if (exam.getTitle() == null || exam.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Exam title is required");
        }
        
        if (exam.getScheduledStart() == null || exam.getScheduledEnd() == null) {
            throw new RuntimeException("Exam schedule is required");
        }
        
        if (exam.getScheduledEnd().isBefore(exam.getScheduledStart())) {
            throw new RuntimeException("Exam end time must be after start time");
        }
        
        if (exam.getDurationMinutes() == null || exam.getDurationMinutes() <= 0) {
            throw new RuntimeException("Valid exam duration is required");
        }
        
        if (exam.getQuestions().isEmpty()) {
            throw new RuntimeException("Exam must have at least one question");
        }
        
        // Validate all questions
        for (ExamQuestion question : exam.getQuestions()) {
            if (!question.isValidForQuestionType()) {
                throw new RuntimeException("Invalid question configuration: " + question.getQuestionText());
            }
        }
        
        // Calculate total points
        double totalPoints = exam.getQuestions().stream()
                .mapToDouble(ExamQuestion::getPoints)
                .sum();
        exam.setTotalPoints(totalPoints);
    }
    
    private ExamDto convertToDto(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setExamType(exam.getExamType().name());
        dto.setClassGroupId(exam.getClassGroup().getId());
        dto.setAcademicTermId(exam.getAcademicTerm().getId());
        dto.setScheduledStart(exam.getScheduledStart());
        dto.setScheduledEnd(exam.getScheduledEnd());
        dto.setDurationMinutes(exam.getDurationMinutes());
        dto.setMaxAttempts(exam.getMaxAttempts());
        dto.setPassingScore(exam.getPassingScore());
        dto.setTotalPoints(exam.getTotalPoints());
        dto.setStatus(exam.getStatus().name());
        dto.setInstructions(exam.getInstructions());
        dto.setAllowReview(exam.getAllowReview());
        dto.setRandomizeQuestions(exam.getRandomizeQuestions());
        dto.setShowResultsImmediately(exam.getShowResultsImmediately());
        dto.setCreatedBy(exam.getCreatedBy().getUsername());
        dto.setCreatedAt(exam.getCreatedAt());
        dto.setUpdatedAt(exam.getUpdatedAt());
        
        // Set class group name
        dto.setClassGroupName(exam.getClassGroup().getName());
        
        return dto;
    }
}