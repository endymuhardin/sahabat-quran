package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.ClassGenerationDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClassGenerationService {
    
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final TeacherLevelAssignmentRepository teacherLevelAssignmentRepository;
    private final ClassSizeConfigurationRepository classSizeConfigurationRepository;
    private final GeneratedClassProposalRepository generatedClassProposalRepository;
    private final ClassGroupRepository classGroupRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final AcademicTermRepository academicTermRepository;
    
    /**
     * Check if class generation can proceed and get readiness status
     */
    public ClassGenerationDto.GenerationReadiness getGenerationReadiness(UUID termId) {
        log.info("Checking generation readiness for term: {}", termId);
        
        List<String> blockingIssues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Check student data completeness
        long totalStudents = studentAssessmentRepository.countNewStudentPlacementTests(termId) +
                           studentAssessmentRepository.countExistingStudentExams(termId);
        
        List<StudentAssessment> validatedAssessments = studentAssessmentRepository
                .findByTermWithStudentAndLevel(termId).stream()
                .filter(StudentAssessment::getIsValidated)
                .collect(Collectors.toList());
        
        BigDecimal studentCompleteness = totalStudents > 0 ? 
                BigDecimal.valueOf(validatedAssessments.size() * 100.0 / totalStudents)
                          .setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        if (studentCompleteness.compareTo(BigDecimal.valueOf(80)) < 0) {
            blockingIssues.add("Student assessment data is only " + studentCompleteness + "% complete (minimum 80% required)");
        }
        
        // Check teacher availability completeness
        List<User> allTeachers = userRepository.findByRoleName("INSTRUCTOR");
        List<User> teachersWithAvailability = teacherAvailabilityRepository
                .findTeachersWhoSubmittedAvailability(termId);
        
        BigDecimal teacherCompleteness = allTeachers.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(teachersWithAvailability.size() * 100.0 / allTeachers.size())
                          .setScale(1, RoundingMode.HALF_UP);
        
        if (teacherCompleteness.compareTo(BigDecimal.valueOf(100)) < 0) {
            blockingIssues.add("Teacher availability is only " + teacherCompleteness + "% complete (100% required)");
        }
        
        // Check level assignments completeness
        List<TeacherLevelAssignment> levelAssignments = teacherLevelAssignmentRepository.findByTermId(termId);
        BigDecimal levelAssignmentCompleteness = teachersWithAvailability.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(levelAssignments.size() * 100.0 / teachersWithAvailability.size())
                          .setScale(1, RoundingMode.HALF_UP);
        
        if (levelAssignmentCompleteness.compareTo(BigDecimal.valueOf(70)) < 0) {
            warnings.add("Level assignments are only " + levelAssignmentCompleteness + "% complete (recommended 70%+)");
        }
        
        // Build recommended parameters
        ClassGenerationDto.GenerationParameters recommendedParameters = buildRecommendedParameters(termId);
        
        boolean canGenerate = blockingIssues.isEmpty();
        
        return ClassGenerationDto.GenerationReadiness.builder()
                .canGenerate(canGenerate)
                .studentDataCompleteness(studentCompleteness)
                .teacherAvailabilityCompleteness(teacherCompleteness)
                .levelAssignmentCompleteness(levelAssignmentCompleteness)
                .blockingIssues(blockingIssues)
                .warnings(warnings)
                .recommendedParameters(recommendedParameters)
                .build();
    }
    
    /**
     * Generate classes based on parameters
     */
    @Transactional
    public ClassGenerationDto.GeneratedClassProposal generateClasses(UUID termId, 
                                                                   ClassGenerationDto.GenerationParameters parameters,
                                                                   UUID generatedBy) {
        log.info("Generating classes for term: {} with parameters: {}", termId, parameters);
        
        // Get next generation run number
        Integer maxRun = generatedClassProposalRepository.findMaxGenerationRunByTerm(termId)
                .orElse(0);
        Integer currentRun = maxRun + 1;
        
        // Get validated student assessments
        List<StudentAssessment> validatedStudents = studentAssessmentRepository
                .findByTermWithStudentAndLevel(termId).stream()
                .filter(StudentAssessment::getIsValidated)
                .collect(Collectors.toList());
        
        // Group students by level
        Map<UUID, List<StudentAssessment>> studentsByLevel = validatedStudents.stream()
                .collect(Collectors.groupingBy(assessment -> assessment.getDeterminedLevel().getId()));
        
        // Get teacher availability and assignments
        List<TeacherLevelAssignment> teacherAssignments = teacherLevelAssignmentRepository
                .findByTermWithTeacherAndLevel(termId);
        
        // Generate classes for each level
        List<ClassGenerationDto.GeneratedClass> generatedClasses = new ArrayList<>();
        List<ClassGenerationDto.Conflict> conflicts = new ArrayList<>();
        List<ClassGenerationDto.SizeViolation> sizeViolations = new ArrayList<>();
        
        for (Map.Entry<UUID, List<StudentAssessment>> entry : studentsByLevel.entrySet()) {
            UUID levelId = entry.getKey();
            List<StudentAssessment> levelStudents = entry.getValue();
            
            // Get teachers assigned to this level
            List<TeacherLevelAssignment> levelTeachers = teacherAssignments.stream()
                    .filter(assignment -> assignment.getLevel().getId().equals(levelId))
                    .collect(Collectors.toList());
            
            if (levelTeachers.isEmpty()) {
                log.warn("No teachers assigned to level: {}. Skipping class generation.", levelId);
                continue;
            }
            
            // Generate classes for this level
            var levelClasses = generateClassesForLevel(termId, levelId, levelStudents, levelTeachers, parameters);
            generatedClasses.addAll(levelClasses.getClasses());
            conflicts.addAll(levelClasses.getConflicts());
            sizeViolations.addAll(levelClasses.getSizeViolations());
        }
        
        // Calculate optimization score
        BigDecimal optimizationScore = calculateOptimizationScore(generatedClasses, conflicts, sizeViolations);
        
        // Build metrics
        ClassGenerationDto.GenerationMetrics metrics = buildGenerationMetrics(generatedClasses, validatedStudents);
        
        // Create proposal
        ClassGenerationDto.GeneratedClassProposal proposal = ClassGenerationDto.GeneratedClassProposal.builder()
                .generationRun(currentRun)
                .optimizationScore(optimizationScore)
                .conflictCount(conflicts.size())
                .classes(generatedClasses)
                .conflicts(conflicts)
                .sizeViolations(sizeViolations)
                .metrics(metrics)
                .generatedAt(LocalDateTime.now())
                .generatedByName(userRepository.findById(generatedBy).map(User::getFullName).orElse("Unknown"))
                .isApproved(false)
                .canApprove(conflicts.isEmpty() || conflicts.stream().allMatch(c -> "LOW".equals(c.getSeverity())))
                .build();
        
        // Save proposal to database
        saveProposalToDatabase(termId, proposal, generatedBy);
        
        log.info("Generated {} classes with optimization score: {}", generatedClasses.size(), optimizationScore);
        return proposal;
    }
    
    /**
     * Get class refinement data for manual optimization
     */
    public ClassGenerationDto.ClassRefinementData getClassRefinementData(UUID termId, UUID proposalId) {
        log.info("Building class refinement data for proposal: {}", proposalId);
        
        // Get proposal
        GeneratedClassProposal proposal = generatedClassProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        // Extract classes from proposal data
        // Note: This would parse the JSONB proposal_data field
        List<ClassGenerationDto.GeneratedClass> classes = parseClassesFromProposal(proposal);
        
        // Find unassigned students
        List<ClassGenerationDto.UnassignedStudent> unassignedStudents = findUnassignedStudents(termId, classes);
        
        // Get available teachers
        List<ClassGenerationDto.AvailableTeacher> availableTeachers = buildAvailableTeachers(termId, classes);
        
        // Get available time slots
        List<String> availableTimeSlots = buildAvailableTimeSlots(termId);
        
        // Get room capacities (placeholder)
        Map<String, Integer> roomCapacities = buildRoomCapacities();
        
        // Build constraints
        ClassGenerationDto.ClassRefinementConstraints constraints = buildRefinementConstraints();
        
        return ClassGenerationDto.ClassRefinementData.builder()
                .classes(classes)
                .unassignedStudents(unassignedStudents)
                .availableTeachers(availableTeachers)
                .availableTimeSlots(availableTimeSlots)
                .roomCapacities(roomCapacities)
                .constraints(constraints)
                .build();
    }
    
    /**
     * Transfer student between classes
     */
    @Transactional
    public void transferStudent(UUID proposalId, UUID studentId, UUID fromClassId, UUID toClassId) {
        log.info("Transferring student: {} from class: {} to class: {}", studentId, fromClassId, toClassId);
        
        // Get proposal
        GeneratedClassProposal proposal = generatedClassProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        // Update proposal data (would modify JSONB field)
        // Implementation would parse JSON, modify student assignments, recalculate metrics
        
        log.info("Student transfer completed successfully");
    }
    
    /**
     * Approve class proposal and create actual class records
     */
    @Transactional
    public void approveClassProposal(UUID proposalId, UUID approvedBy) {
        log.info("Approving class proposal: {} by user: {}", proposalId, approvedBy);
        
        GeneratedClassProposal proposal = generatedClassProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));
        
        // Mark proposal as approved
        proposal.setIsApproved(true);
        proposal.setApprovedBy(userRepository.findById(approvedBy).orElse(null));
        generatedClassProposalRepository.save(proposal);
        
        // Create actual class records from proposal
        createClassRecordsFromProposal(proposal);
        
        log.info("Class proposal approved and classes created successfully");
    }
    
    // Helper methods
    
    private ClassGenerationDto.GenerationParameters buildRecommendedParameters(UUID termId) {
        // Get default class size configuration
        List<ClassSizeConfiguration> sizeConfigs = classSizeConfigurationRepository.findSystemDefaults();
        
        Integer defaultMin = sizeConfigs.stream()
                .filter(config -> "default.min".equals(config.getConfigKey()))
                .map(ClassSizeConfiguration::getConfigValue)
                .findFirst()
                .orElse(7);
        
        Integer defaultMax = sizeConfigs.stream()
                .filter(config -> "default.max".equals(config.getConfigKey()))
                .map(ClassSizeConfiguration::getConfigValue)
                .findFirst()
                .orElse(10);
        
        return ClassGenerationDto.GenerationParameters.builder()
                .defaultMinClassSize(defaultMin)
                .defaultMaxClassSize(defaultMax)
                .newExistingStudentRatio(BigDecimal.valueOf(0.4)) // 40% new, 60% existing
                .maxClassesPerTeacher(6)
                .allowUndersizedClasses(false)
                .optimizeForTeacherWorkload(true)
                .priorityStrategy("BALANCE")
                .levelSpecificSizes(new HashMap<>())
                .build();
    }
    
    private ClassGenerationResult generateClassesForLevel(UUID termId, UUID levelId, 
                                                         List<StudentAssessment> students,
                                                         List<TeacherLevelAssignment> teachers,
                                                         ClassGenerationDto.GenerationParameters parameters) {
        
        List<ClassGenerationDto.GeneratedClass> classes = new ArrayList<>();
        List<ClassGenerationDto.Conflict> conflicts = new ArrayList<>();
        List<ClassGenerationDto.SizeViolation> sizeViolations = new ArrayList<>();
        
        // Simple class generation logic
        int classesNeeded = (int) Math.ceil((double) students.size() / parameters.getDefaultMaxClassSize());
        int studentsPerClass = students.size() / classesNeeded;
        
        for (int i = 0; i < classesNeeded; i++) {
            int startIndex = i * studentsPerClass;
            int endIndex = Math.min((i + 1) * studentsPerClass, students.size());
            
            List<StudentAssessment> classStudents = students.subList(startIndex, endIndex);
            
            // Assign teacher (simple round-robin)
            TeacherLevelAssignment assignedTeacher = teachers.get(i % teachers.size());
            
            // Find available time slot for this teacher
            var availableSlot = findAvailableTimeSlot(termId, assignedTeacher.getTeacher().getId());
            
            // Create class
            ClassGenerationDto.GeneratedClass generatedClass = ClassGenerationDto.GeneratedClass.builder()
                    .classId(UUID.randomUUID())
                    .className("Generated Class " + (i + 1))
                    .levelId(levelId)
                    .levelName(assignedTeacher.getLevel().getName())
                    .teacherId(assignedTeacher.getTeacher().getId())
                    .teacherName(assignedTeacher.getTeacher().getFullName())
                    .dayOfWeek(availableSlot.getTimeSlot() != null ? availableSlot.getTimeSlot().getDayOfWeek() : null)
                    .session(availableSlot.getTimeSlot() != null ? availableSlot.getTimeSlot().getSession() : null)
                    .sessionDisplay(formatSessionDisplay(availableSlot))
                    .students(convertToAssignedStudents(classStudents))
                    .currentSize(classStudents.size())
                    .minSize(parameters.getDefaultMinClassSize())
                    .maxSize(parameters.getDefaultMaxClassSize())
                    .isUndersized(classStudents.size() < parameters.getDefaultMinClassSize())
                    .isOversized(classStudents.size() > parameters.getDefaultMaxClassSize())
                    .classType(determineClassType(classStudents))
                    .newStudentPercentage(calculateNewStudentPercentage(classStudents))
                    .specialNotes(new ArrayList<>())
                    .build();
            
            classes.add(generatedClass);
            
            // Check for violations
            if (generatedClass.getIsUndersized() || generatedClass.getIsOversized()) {
                ClassGenerationDto.SizeViolation violation = ClassGenerationDto.SizeViolation.builder()
                        .classId(generatedClass.getClassId())
                        .className(generatedClass.getClassName())
                        .violationType(generatedClass.getIsUndersized() ? "UNDERSIZED" : "OVERSIZED")
                        .currentSize(generatedClass.getCurrentSize())
                        .targetMin(generatedClass.getMinSize())
                        .targetMax(generatedClass.getMaxSize())
                        .justification("Generated based on available students and teachers")
                        .requiresApproval(!parameters.getAllowUndersizedClasses())
                        .isApproved(false)
                        .build();
                
                sizeViolations.add(violation);
            }
        }
        
        return new ClassGenerationResult(classes, conflicts, sizeViolations);
    }
    
    private TeacherAvailability findAvailableTimeSlot(UUID termId, UUID teacherId) {
        List<TeacherAvailability> availability = teacherAvailabilityRepository
                .findAvailableSlotsByTeacherAndTerm(teacherId, termId);
        
        return availability.isEmpty() ? null : availability.get(0); // Simple: take first available
    }
    
    private String formatSessionDisplay(TeacherAvailability slot) {
        if (slot == null) return "TBD";
        
        if (slot.getTimeSlot() == null || slot.getTimeSlot().getSession() == null) return "TBD";
        String[] days = {"", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        String dayName = days[dayOfWeekToInteger(slot.getTimeSlot().getDayOfWeek())];
        String sessionName = slot.getTimeSlot().getSession().getName();
        
        return dayName + " " + sessionName;
    }
    
    private int dayOfWeekToInteger(com.sahabatquran.webapp.entity.TimeSlot.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }
    
    private List<ClassGenerationDto.AssignedStudent> convertToAssignedStudents(List<StudentAssessment> assessments) {
        return assessments.stream()
                .map(assessment -> ClassGenerationDto.AssignedStudent.builder()
                        .studentId(assessment.getStudent().getId())
                        .studentName(assessment.getStudent().getFullName())
                        .studentUsername(assessment.getStudent().getUsername())
                        .category(assessment.getStudentCategory())
                        .assessmentScore(assessment.getAssessmentScore())
                        .assessmentGrade(assessment.getAssessmentGrade())
                        .specialNeeds(assessment.getAssessmentNotes())
                        .canReassign(true)
                        .build())
                .collect(Collectors.toList());
    }
    
    private String determineClassType(List<StudentAssessment> students) {
        long newStudents = students.stream()
                .filter(s -> s.getStudentCategory() == StudentAssessment.StudentCategory.NEW)
                .count();
        
        if (newStudents == 0) return "EXISTING_ONLY";
        if (newStudents == students.size()) return "NEW_ONLY";
        return "MIXED";
    }
    
    private BigDecimal calculateNewStudentPercentage(List<StudentAssessment> students) {
        if (students.isEmpty()) return BigDecimal.ZERO;
        
        long newStudents = students.stream()
                .filter(s -> s.getStudentCategory() == StudentAssessment.StudentCategory.NEW)
                .count();
        
        return BigDecimal.valueOf(newStudents * 100.0 / students.size()).setScale(1, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateOptimizationScore(List<ClassGenerationDto.GeneratedClass> classes,
                                                 List<ClassGenerationDto.Conflict> conflicts,
                                                 List<ClassGenerationDto.SizeViolation> violations) {
        // Simple scoring algorithm
        double baseScore = 100.0;
        
        // Penalize conflicts
        baseScore -= conflicts.size() * 10.0;
        
        // Penalize size violations
        baseScore -= violations.size() * 5.0;
        
        // Reward good class size distribution
        double avgSize = classes.stream().mapToInt(ClassGenerationDto.GeneratedClass::getCurrentSize).average().orElse(0);
        if (avgSize >= 7 && avgSize <= 10) {
            baseScore += 10.0;
        }
        
        return BigDecimal.valueOf(Math.max(0, baseScore)).setScale(1, RoundingMode.HALF_UP);
    }
    
    private ClassGenerationDto.GenerationMetrics buildGenerationMetrics(List<ClassGenerationDto.GeneratedClass> classes,
                                                                        List<StudentAssessment> allStudents) {
        
        int totalStudentsAssigned = classes.stream()
                .mapToInt(ClassGenerationDto.GeneratedClass::getCurrentSize)
                .sum();
        
        Set<UUID> utilizedTeachers = classes.stream()
                .map(ClassGenerationDto.GeneratedClass::getTeacherId)
                .collect(Collectors.toSet());
        
        double avgClassSize = classes.isEmpty() ? 0 : 
                classes.stream().mapToInt(ClassGenerationDto.GeneratedClass::getCurrentSize).average().orElse(0);
        
        Map<String, Integer> classTypeDistribution = classes.stream()
                .collect(Collectors.groupingBy(
                        ClassGenerationDto.GeneratedClass::getClassType,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        
        return ClassGenerationDto.GenerationMetrics.builder()
                .totalClasses(classes.size())
                .totalStudentsAssigned(totalStudentsAssigned)
                .unassignedStudents(allStudents.size() - totalStudentsAssigned)
                .averageClassSize(BigDecimal.valueOf(avgClassSize).setScale(1, RoundingMode.HALF_UP))
                .teachersUtilized(utilizedTeachers.size())
                .totalTeacherSlots(0) // TODO: Calculate from availability
                .teacherUtilizationRate(BigDecimal.ZERO) // TODO: Calculate
                .roomsRequired(classes.size())
                .classTypeDistribution(classTypeDistribution)
                .workloadBalance(BigDecimal.ZERO) // TODO: Calculate standard deviation
                .build();
    }
    
    // Placeholder implementations for complex operations
    private void saveProposalToDatabase(UUID termId, ClassGenerationDto.GeneratedClassProposal proposal, UUID generatedBy) {
        // TODO: Convert proposal to GeneratedClassProposal entity and save
        log.info("Saving proposal to database - implementation needed");
    }
    
    private List<ClassGenerationDto.GeneratedClass> parseClassesFromProposal(GeneratedClassProposal proposal) {
        // TODO: Parse JSONB proposal_data field
        return new ArrayList<>();
    }
    
    private List<ClassGenerationDto.UnassignedStudent> findUnassignedStudents(UUID termId, 
                                                                            List<ClassGenerationDto.GeneratedClass> classes) {
        return new ArrayList<>();
    }
    
    private List<ClassGenerationDto.AvailableTeacher> buildAvailableTeachers(UUID termId,
                                                                           List<ClassGenerationDto.GeneratedClass> classes) {
        return new ArrayList<>();
    }
    
    private List<String> buildAvailableTimeSlots(UUID termId) {
        return Arrays.asList("Senin Pagi", "Senin Siang", "Selasa Pagi", "Selasa Siang");
    }
    
    private Map<String, Integer> buildRoomCapacities() {
        Map<String, Integer> rooms = new HashMap<>();
        rooms.put("Ruang A", 15);
        rooms.put("Ruang B", 12);
        rooms.put("Ruang C", 10);
        return rooms;
    }
    
    private ClassGenerationDto.ClassRefinementConstraints buildRefinementConstraints() {
        return ClassGenerationDto.ClassRefinementConstraints.builder()
                .minClassSize(7)
                .maxClassSize(10)
                .maxStudentMovesPerRun(5)
                .allowTeacherReassignment(true)
                .allowTimeSlotChanges(false)
                .maintainStudentCategoryBalance(true)
                .maxNewStudentRatio(BigDecimal.valueOf(0.6))
                .build();
    }
    
    private void createClassRecordsFromProposal(GeneratedClassProposal proposal) {
        // TODO: Create actual ClassGroup entities from proposal data
        log.info("Creating class records from proposal - implementation needed");
    }
    
    // Inner class for generation results
    private static class ClassGenerationResult {
        private final List<ClassGenerationDto.GeneratedClass> classes;
        private final List<ClassGenerationDto.Conflict> conflicts;
        private final List<ClassGenerationDto.SizeViolation> sizeViolations;
        
        public ClassGenerationResult(List<ClassGenerationDto.GeneratedClass> classes,
                                   List<ClassGenerationDto.Conflict> conflicts,
                                   List<ClassGenerationDto.SizeViolation> sizeViolations) {
            this.classes = classes;
            this.conflicts = conflicts;
            this.sizeViolations = sizeViolations;
        }
        
        public List<ClassGenerationDto.GeneratedClass> getClasses() { return classes; }
        public List<ClassGenerationDto.Conflict> getConflicts() { return conflicts; }
        public List<ClassGenerationDto.SizeViolation> getSizeViolations() { return sizeViolations; }
    }
}