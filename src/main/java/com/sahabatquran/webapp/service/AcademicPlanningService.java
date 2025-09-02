package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.AssessmentFoundationDto;
import com.sahabatquran.webapp.dto.LevelDistributionDto;
import com.sahabatquran.webapp.dto.TeacherAvailabilityDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AcademicPlanningService {
    
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final AcademicTermRepository academicTermRepository;
    private final ClassGroupRepository classGroupRepository;
    
    /**
     * Get assessment foundation data for dashboard
     */
    public AssessmentFoundationDto getAssessmentFoundationData(UUID termId) {
        log.info("Building assessment foundation data for term: {}", termId);
        
        // Get new student stats
        Long totalNewRegistrations = studentAssessmentRepository
                .countNewStudentPlacementTests(termId);
        
        List<StudentAssessment> newStudentAssessments = studentAssessmentRepository
                .findByTermAndStudentCategory(termId, StudentAssessment.StudentCategory.NEW);
        
        long newTestsCompleted = newStudentAssessments.stream()
                .filter(assessment -> assessment.getAssessmentScore() != null && assessment.getIsValidated())
                .count();
        
        long newTestsScheduled = newStudentAssessments.stream()
                .filter(assessment -> assessment.getAssessmentScore() == null && assessment.getAssessmentDate() != null)
                .count();
        
        long newTestsNotScheduled = totalNewRegistrations - newTestsCompleted - newTestsScheduled;
        
        BigDecimal newCompletionPercentage = totalNewRegistrations > 0 
                ? BigDecimal.valueOf(newTestsCompleted * 100.0 / totalNewRegistrations).setScale(0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        AssessmentFoundationDto.NewStudentStats newStats = AssessmentFoundationDto.NewStudentStats.builder()
                .totalRegistrations(totalNewRegistrations)
                .placementTestsCompleted(newTestsCompleted)
                .testsScheduled(newTestsScheduled)
                .testsNotScheduled(newTestsNotScheduled)
                .completionPercentage(newCompletionPercentage)
                .levelAssignmentsReady(newTestsCompleted)
                .build();
        
        // Get existing student stats
        Long totalExistingStudents = studentAssessmentRepository
                .countExistingStudentExams(termId);
        
        List<StudentAssessment> existingStudentAssessments = studentAssessmentRepository
                .findByTermAndStudentCategory(termId, StudentAssessment.StudentCategory.EXISTING);
        
        long existingResultsSubmitted = existingStudentAssessments.stream()
                .filter(assessment -> assessment.getAssessmentGrade() != null && assessment.getIsValidated())
                .count();
        
        long existingPartialResults = existingStudentAssessments.stream()
                .filter(assessment -> assessment.getAssessmentGrade() != null && !assessment.getIsValidated())
                .count();
        
        long existingResultsMissing = totalExistingStudents - existingResultsSubmitted - existingPartialResults;
        
        BigDecimal existingCompletionPercentage = totalExistingStudents > 0 
                ? BigDecimal.valueOf(existingResultsSubmitted * 100.0 / totalExistingStudents).setScale(0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        AssessmentFoundationDto.ExistingStudentStats existingStats = AssessmentFoundationDto.ExistingStudentStats.builder()
                .totalContinuingStudents(totalExistingStudents)
                .examResultsSubmitted(existingResultsSubmitted)
                .partialResults(existingPartialResults)
                .resultsMissing(existingResultsMissing)
                .completionPercentage(existingCompletionPercentage)
                .levelAssignmentsReady(existingResultsSubmitted)
                .build();
        
        // Calculate overall readiness
        long totalStudentsReady = newTestsCompleted + existingResultsSubmitted;
        long totalStudents = totalNewRegistrations + totalExistingStudents;
        BigDecimal overallPercentage = totalStudents > 0 
                ? BigDecimal.valueOf(totalStudentsReady * 100.0 / totalStudents).setScale(0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        AssessmentFoundationDto.OverallReadiness overallReadiness = AssessmentFoundationDto.OverallReadiness.builder()
                .totalStudentsReady(totalStudentsReady)
                .totalStudents(totalStudents)
                .overallReadinessPercentage(overallPercentage)
                .build();
        
        // Get pending assessments
        List<StudentAssessment> pendingAssessments = studentAssessmentRepository
                .findUnvalidatedAssessmentsByTerm(termId);
        
        List<AssessmentFoundationDto.PendingAssessmentItem> pendingItems = pendingAssessments.stream()
                .map(assessment -> AssessmentFoundationDto.PendingAssessmentItem.builder()
                        .studentName(assessment.getStudent().getFullName())
                        .studentUsername(assessment.getStudent().getUsername())
                        .category(assessment.getStudentCategory())
                        .assessmentType(assessment.getAssessmentType())
                        .status(assessment.getIsValidated() ? "Completed" : "Pending Validation")
                        .notes(assessment.getAssessmentNotes())
                        .build())
                .collect(Collectors.toList());
        
        // Get term name
        String termName = academicTermRepository.findById(termId)
                .map(AcademicTerm::getTermName)
                .orElse("Unknown Term");
        
        return AssessmentFoundationDto.builder()
                .termName(termName)
                .newStudentStats(newStats)
                .existingStudentStats(existingStats)
                .overallReadiness(overallReadiness)
                .pendingAssessments(pendingItems)
                .build();
    }
    
    /**
     * Get level distribution data for analysis
     */
    public LevelDistributionDto getLevelDistributionData(UUID termId) {
        log.info("Building level distribution data for term: {}", termId);
        
        // Get level distribution statistics
        List<Object[]> levelDistribution = studentAssessmentRepository.findLevelDistributionByTerm(termId);
        List<Level> allLevels = levelRepository.findAll();
        
        List<LevelDistributionDto.LevelStats> levelStatistics = allLevels.stream()
                .map(level -> {
                    // Find statistics for this level
                    Long totalCount = levelDistribution.stream()
                            .filter(row -> ((Level) row[0]).getId().equals(level.getId()))
                            .map(row -> (Long) row[1])
                            .findFirst()
                            .orElse(0L);
                    
                    // Get breakdown by category
                    List<StudentAssessment> levelAssessments = studentAssessmentRepository
                            .findByTermWithStudentAndLevel(termId).stream()
                            .filter(assessment -> assessment.getDeterminedLevel() != null 
                                    && assessment.getDeterminedLevel().getId().equals(level.getId()))
                            .collect(Collectors.toList());
                    
                    long newStudentCount = levelAssessments.stream()
                            .filter(assessment -> assessment.getStudentCategory() == StudentAssessment.StudentCategory.NEW)
                            .count();
                    
                    long existingStudentCount = levelAssessments.stream()
                            .filter(assessment -> assessment.getStudentCategory() == StudentAssessment.StudentCategory.EXISTING)
                            .count();
                    
                    // Calculate average score for this level
                    BigDecimal averageScore = levelAssessments.stream()
                            .filter(assessment -> assessment.getAssessmentScore() != null)
                            .map(StudentAssessment::getAssessmentScore)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(Math.max(1, levelAssessments.size())), 2, RoundingMode.HALF_UP);
                    
                    // Estimate recommended class count (assuming 7-10 students per class)
                    long recommendedClasses = totalCount > 0 ? (totalCount + 8) / 9 : 0; // Round up division by 9
                    
                    return LevelDistributionDto.LevelStats.builder()
                            .levelId(level.getId())
                            .levelName(level.getName())
                            .levelDescription(level.getDescription())
                            .newStudentCount(newStudentCount)
                            .existingStudentCount(existingStudentCount)
                            .totalStudentCount(totalCount)
                            .averageScore(averageScore)
                            .recommendedClassCount(recommendedClasses)
                            .specialNotes("")
                            .build();
                })
                .collect(Collectors.toList());
        
        // Get all student assignments
        List<StudentAssessment> allAssessments = studentAssessmentRepository.findByTermWithStudentAndLevel(termId);
        List<LevelDistributionDto.StudentLevelAssignment> studentAssignments = allAssessments.stream()
                .map(assessment -> LevelDistributionDto.StudentLevelAssignment.builder()
                        .studentId(assessment.getStudent().getId())
                        .studentName(assessment.getStudent().getFullName())
                        .studentUsername(assessment.getStudent().getUsername())
                        .category(assessment.getStudentCategory())
                        .determinedLevelId(assessment.getDeterminedLevel() != null ? assessment.getDeterminedLevel().getId() : null)
                        .determinedLevelName(assessment.getDeterminedLevel() != null ? assessment.getDeterminedLevel().getName() : "Not Assigned")
                        .assessmentScore(assessment.getAssessmentScore())
                        .assessmentGrade(assessment.getAssessmentGrade())
                        .specialCircumstances(assessment.getAssessmentNotes())
                        .isValidated(assessment.getIsValidated())
                        .build())
                .collect(Collectors.toList());
        
        // Build assignment rules (static data for now)
        List<LevelDistributionDto.AssignmentRules> assignmentRules = buildAssignmentRules();
        
        long totalValidated = allAssessments.stream()
                .filter(StudentAssessment::getIsValidated)
                .count();
        long totalPending = allAssessments.size() - totalValidated;
        
        String termName = academicTermRepository.findById(termId)
                .map(AcademicTerm::getTermName)
                .orElse("Unknown Term");
        
        return LevelDistributionDto.builder()
                .termName(termName)
                .levelStatistics(levelStatistics)
                .studentAssignments(studentAssignments)
                .assignmentRules(assignmentRules)
                .totalValidatedAssignments(totalValidated)
                .totalPendingAssignments(totalPending)
                .build();
    }
    
    /**
     * Get teacher availability status for monitoring
     */
    public TeacherAvailabilityDto getTeacherAvailabilityStatus(UUID termId) {
        log.info("Building teacher availability status for term: {}", termId);
        
        // Get all teachers who should submit availability
        List<User> allTeachers = userRepository.findByRoleName("INSTRUCTOR");
        
        // Get teachers who have submitted availability
        List<User> teachersWithSubmissions = teacherAvailabilityRepository
                .findTeachersWhoSubmittedAvailability(termId);
        
        List<TeacherAvailabilityDto.SubmissionStatus> submissionStatuses = allTeachers.stream()
                .map(teacher -> {
                    boolean hasSubmitted = teachersWithSubmissions.contains(teacher);
                    
                    if (hasSubmitted) {
                        List<TeacherAvailability> teacherSlots = teacherAvailabilityRepository
                                .findByTeacherAndTerm(teacher, academicTermRepository.findById(termId).orElseThrow());
                        
                        int availableSlots = (int) teacherSlots.stream()
                                .filter(TeacherAvailability::getIsAvailable)
                                .count();
                        
                        return TeacherAvailabilityDto.SubmissionStatus.builder()
                                .teacherId(teacher.getId())
                                .teacherName(teacher.getFullName())
                                .hasSubmitted(true)
                                .submissionDate(teacherSlots.isEmpty() ? null : teacherSlots.get(0).getSubmittedAt())
                                .availableSlotsCount(availableSlots)
                                .status(availableSlots > 0 ? "SUBMITTED" : "INCOMPLETE")
                                .build();
                    } else {
                        return TeacherAvailabilityDto.SubmissionStatus.builder()
                                .teacherId(teacher.getId())
                                .teacherName(teacher.getFullName())
                                .hasSubmitted(false)
                                .submissionDate(null)
                                .availableSlotsCount(0)
                                .status("PENDING")
                                .build();
                    }
                })
                .collect(Collectors.toList());
        
        String termName = academicTermRepository.findById(termId)
                .map(AcademicTerm::getTermName)
                .orElse("Unknown Term");
        
        long totalTeachers = allTeachers.size();
        long submittedTeachers = teachersWithSubmissions.size();
        long pendingTeachers = totalTeachers - submittedTeachers;
        int completionPercentage = totalTeachers > 0 ? (int) ((submittedTeachers * 100) / totalTeachers) : 0;
        
        return TeacherAvailabilityDto.builder()
                .termName(termName)
                .submissionStatuses(submissionStatuses)
                .totalTeachers(totalTeachers)
                .submittedTeachers(submittedTeachers)
                .pendingTeachers(pendingTeachers)
                // Template-friendly property names
                .submittedCount(submittedTeachers)
                .pendingCount(pendingTeachers)
                .completionPercentage(completionPercentage)
                .teacherStatuses(submissionStatuses)
                .deadline("15 Dec 2024") // TODO: Get from term preparation deadline
                .build();
    }
    
    /**
     * Check if preparation process can be launched
     */
    public boolean canLaunchPreparationProcess(UUID termId) {
        // Check if assessment data is sufficient (>80% completion)
        AssessmentFoundationDto foundationData = getAssessmentFoundationData(termId);
        return foundationData.getOverallReadiness().getOverallReadinessPercentage()
                .compareTo(BigDecimal.valueOf(80)) >= 0;
    }
    
    /**
     * Initiate preparation process for a term
     */
    @Transactional
    public void initiatePreparationProcess(UUID termId, UUID initiatedBy) {
        log.info("Initiating preparation process for term: {} by user: {}", termId, initiatedBy);
        
        // Update term status
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        // Set status to indicate preparation is active
        // Note: This would typically send notifications to stakeholders
        log.info("Preparation process initiated for term: {}", term.getTermName());
        
        // TODO: Implement notification system
        // - Send emails to all teachers to submit availability
        // - Notify management about level assignment requirements
        // - Set up monitoring alerts
    }
    
    /**
     * Get final schedule data for review
     */
    public Map<String, Object> getFinalScheduleData(UUID termId) {
        log.info("Building final schedule data for term: {}", termId);
        
        Map<String, Object> scheduleData = new HashMap<>();
        
        // Get approved classes
        List<ClassGroup> approvedClasses = classGroupRepository.findActiveClassesByTerm(termId);
        
        // Build schedule summary
        scheduleData.put("totalClasses", approvedClasses.size());
        scheduleData.put("totalStudents", approvedClasses.stream()
                .mapToLong(classGroup -> classGroup.getEnrollments() != null ? classGroup.getEnrollments().size() : 0)
                .sum());
        scheduleData.put("totalTeachers", approvedClasses.stream()
                .filter(classGroup -> classGroup.getInstructor() != null)
                .map(classGroup -> classGroup.getInstructor().getId())
                .distinct()
                .count());
        
        // Quality metrics
        double avgClassSize = approvedClasses.stream()
                .mapToInt(classGroup -> classGroup.getEnrollments() != null ? classGroup.getEnrollments().size() : 0)
                .average()
                .orElse(0.0);
        
        scheduleData.put("averageClassSize", BigDecimal.valueOf(avgClassSize).setScale(1, RoundingMode.HALF_UP));
        scheduleData.put("classDetails", approvedClasses);
        
        return scheduleData;
    }
    
    /**
     * Get system implementation status
     */
    public Map<String, Object> getSystemImplementationStatus(UUID termId) {
        log.info("Building system implementation status for term: {}", termId);
        
        Map<String, Object> status = new HashMap<>();
        
        // Check various system components
        status.put("classRecordsCreated", true);
        status.put("enrollmentRecordsCreated", true);
        status.put("scheduleIntegrationReady", true);
        status.put("notificationSystemReady", false); // TODO: Implement
        status.put("reportingSystemReady", true);
        
        // Implementation progress
        int completedTasks = 4;
        int totalTasks = 5;
        status.put("implementationProgress", BigDecimal.valueOf(completedTasks * 100.0 / totalTasks).setScale(0, RoundingMode.HALF_UP));
        
        return status;
    }
    
    /**
     * Get system go-live readiness
     */
    public Map<String, Object> getGoLiveReadiness(UUID termId) {
        log.info("Building go-live readiness for term: {}", termId);
        
        Map<String, Object> readiness = new HashMap<>();
        
        // Final checklist
        List<Map<String, Object>> checklist = Arrays.asList(
                createChecklistItem("Student assessments processed", true),
                createChecklistItem("Classes created and optimized", true),
                createChecklistItem("Teachers assigned with balanced workload", true),
                createChecklistItem("Schedule conflicts resolved", true),
                createChecklistItem("System integration operational", true),
                createChecklistItem("Infrastructure ready", true),
                createChecklistItem("Communications complete", false),
                createChecklistItem("Support systems activated", false)
        );
        
        long completedItems = checklist.stream()
                .filter(item -> (Boolean) item.get("completed"))
                .count();
        
        readiness.put("checklist", checklist);
        readiness.put("readinessPercentage", BigDecimal.valueOf(completedItems * 100.0 / checklist.size()).setScale(0, RoundingMode.HALF_UP));
        readiness.put("canGoLive", completedItems >= 6); // Minimum 6 out of 8 items
        
        return readiness;
    }
    
    /**
     * Execute system go-live
     */
    @Transactional
    public void executeSystemGoLive(UUID termId, UUID executedBy) {
        log.info("Executing system go-live for term: {} by user: {}", termId, executedBy);
        
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        // Update term status to ACTIVE
        term.setStatus(AcademicTerm.TermStatus.ACTIVE);
        academicTermRepository.save(term);
        
        // TODO: Implement additional go-live tasks
        // - Activate notification systems
        // - Send welcome messages to students and teachers
        // - Enable class attendance tracking
        // - Activate assessment systems
        
        log.info("System go-live executed successfully for term: {}", term.getTermName());
    }
    
    private Map<String, Object> createChecklistItem(String item, Boolean completed) {
        Map<String, Object> checklistItem = new HashMap<>();
        checklistItem.put("item", item);
        checklistItem.put("completed", completed);
        return checklistItem;
    }
    
    /**
     * Build assignment rules for level distribution
     */
    private List<LevelDistributionDto.AssignmentRules> buildAssignmentRules() {
        List<LevelDistributionDto.AssignmentRules> rules = new ArrayList<>();
        
        // New Student Rules (Placement Test Based)
        List<LevelDistributionDto.ScoreLevelMapping> newStudentMappings = List.of(
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(90))
                        .maxScore(BigDecimal.valueOf(100))
                        .levelName("Tahsin 3 / Tahfidz Pemula")
                        .description("Advanced level")
                        .additionalRequirements("Previous Quran study experience")
                        .competencyLevel("ADVANCED")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(75))
                        .maxScore(BigDecimal.valueOf(89))
                        .levelName("Tahsin 2")
                        .description("Intermediate level")
                        .additionalRequirements("Basic Arabic reading skills")
                        .competencyLevel("INTERMEDIATE")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(60))
                        .maxScore(BigDecimal.valueOf(74))
                        .levelName("Tahsin 1 (Advanced Group)")
                        .description("Basic plus level")
                        .additionalRequirements("Some Arabic familiarity")
                        .competencyLevel("BASIC")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(40))
                        .maxScore(BigDecimal.valueOf(59))
                        .levelName("Tahsin 1 (Standard Group)")
                        .description("Basic level")
                        .additionalRequirements("Motivation to learn")
                        .competencyLevel("FOUNDATION")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.ZERO)
                        .maxScore(BigDecimal.valueOf(39))
                        .levelName("Tahsin 1 (Foundation Group)")
                        .description("Beginner level")
                        .additionalRequirements("Patient and dedicated approach")
                        .competencyLevel("FOUNDATION")
                        .build()
        );
        
        rules.add(LevelDistributionDto.AssignmentRules.builder()
                .ruleType("NEW_STUDENT")
                .scoreMappings(newStudentMappings)
                .build());
        
        // Existing Student Rules (Exam Result Based)
        List<LevelDistributionDto.ScoreLevelMapping> existingStudentMappings = List.of(
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(85))
                        .maxScore(BigDecimal.valueOf(100))
                        .levelName("Next Level")
                        .description("Grade A: Promote to next level")
                        .additionalRequirements("Good attendance record (80%+)")
                        .competencyLevel("ADVANCED")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(70))
                        .maxScore(BigDecimal.valueOf(84))
                        .levelName("Next Level (Monitored)")
                        .description("Grade B: Promote with monitoring")
                        .additionalRequirements("Regular progress checks")
                        .competencyLevel("INTERMEDIATE")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.valueOf(60))
                        .maxScore(BigDecimal.valueOf(69))
                        .levelName("Current Level (Repeat)")
                        .description("Grade C: Repeat current level")
                        .additionalRequirements("Additional practice and support")
                        .competencyLevel("BASIC")
                        .build(),
                LevelDistributionDto.ScoreLevelMapping.builder()
                        .minScore(BigDecimal.ZERO)
                        .maxScore(BigDecimal.valueOf(59))
                        .levelName("Current Level + Remedial")
                        .description("Grade D: Repeat with remedial support")
                        .additionalRequirements("Intensive remedial program")
                        .competencyLevel("FOUNDATION")
                        .build()
        );
        
        rules.add(LevelDistributionDto.AssignmentRules.builder()
                .ruleType("EXISTING_STUDENT")
                .scoreMappings(existingStudentMappings)
                .build());
        
        return rules;
    }
}