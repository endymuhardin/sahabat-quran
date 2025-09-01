package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.TeacherLevelAssignmentDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherLevelAssignmentService {
    
    private final TeacherLevelAssignmentRepository teacherLevelAssignmentRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final AcademicTermRepository academicTermRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
    
    /**
     * Get comprehensive teacher level assignment data
     */
    public TeacherLevelAssignmentDto getTeacherLevelAssignments(UUID termId) {
        log.info("Building teacher level assignment data for term: {}", termId);
        
        // Get existing assignments
        List<TeacherLevelAssignment> existingAssignments = teacherLevelAssignmentRepository
                .findByTermWithTeacherAndLevel(termId);
        
        // Convert to assignment DTOs
        List<TeacherLevelAssignmentDto.Assignment> assignments = existingAssignments.stream()
                .map(assignment -> TeacherLevelAssignmentDto.Assignment.builder()
                        .assignmentId(assignment.getId())
                        .teacherId(assignment.getTeacher().getId())
                        .teacherName(assignment.getTeacher().getFullName())
                        .teacherUsername(assignment.getTeacher().getUsername())
                        .levelId(assignment.getLevel().getId())
                        .levelName(assignment.getLevel().getName())
                        .levelDescription(assignment.getLevel().getDescription())
                        .competencyLevel(assignment.getCompetencyLevel())
                        .maxClassesForLevel(assignment.getMaxClassesForLevel())
                        .specialization(assignment.getSpecialization())
                        .assignedByName(assignment.getAssignedBy() != null ? assignment.getAssignedBy().getFullName() : "System")
                        .assignedAt(assignment.getAssignedAt())
                        .canEdit(true) // TODO: Implement proper permission checking
                        .build())
                .collect(Collectors.toList());
        
        // Build teacher summaries
        List<TeacherLevelAssignmentDto.TeacherSummary> teacherSummaries = buildTeacherSummaries(termId, existingAssignments);
        
        // Build level summaries
        List<TeacherLevelAssignmentDto.LevelSummary> levelSummaries = buildLevelSummaries(termId, existingAssignments);
        
        // Build workload analysis
        TeacherLevelAssignmentDto.WorkloadAnalysis workloadAnalysis = buildWorkloadAnalysis(termId, existingAssignments);
        
        // Find unassigned teachers
        List<TeacherLevelAssignmentDto.UnassignedTeacher> unassignedTeachers = findUnassignedTeachers(termId, existingAssignments);
        
        // Get available options for dropdowns
        List<TeacherLevelAssignmentDto.TeacherOption> availableTeachers = buildTeacherOptions();
        List<TeacherLevelAssignmentDto.LevelOption> availableLevels = buildLevelOptions();
        
        String termName = academicTermRepository.findById(termId)
                .map(AcademicTerm::getTermName)
                .orElse("Unknown Term");
        
        return TeacherLevelAssignmentDto.builder()
                .termName(termName)
                .assignments(assignments)
                .teacherSummaries(teacherSummaries)
                .levelSummaries(levelSummaries)
                .workloadAnalysis(workloadAnalysis)
                .unassignedTeachers(unassignedTeachers)
                .availableTeachers(availableTeachers)
                .availableLevels(availableLevels)
                .build();
    }
    
    /**
     * Assign teacher to level
     */
    @Transactional
    public void assignTeacherToLevel(UUID teacherId, UUID levelId, UUID termId, 
                                   String competencyLevel, Integer maxClassesForLevel,
                                   String specialization, UUID assignedById) {
        log.info("Assigning teacher: {} to level: {} for term: {}", teacherId, levelId, termId);
        
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Level not found"));
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        User assignedBy = userRepository.findById(assignedById)
                .orElseThrow(() -> new RuntimeException("Assigned by user not found"));
        
        // Check if assignment already exists
        Optional<TeacherLevelAssignment> existing = teacherLevelAssignmentRepository
                .findByTeacherAndLevelAndTerm(teacher, level, term);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Teacher is already assigned to this level for this term");
        }
        
        // Create new assignment
        TeacherLevelAssignment assignment = new TeacherLevelAssignment();
        assignment.setTeacher(teacher);
        assignment.setLevel(level);
        assignment.setTerm(term);
        assignment.setCompetencyLevel(TeacherLevelAssignment.CompetencyLevel.valueOf(competencyLevel));
        assignment.setMaxClassesForLevel(maxClassesForLevel);
        assignment.setSpecialization(specialization != null ? 
                TeacherLevelAssignment.Specialization.valueOf(specialization) : null);
        assignment.setAssignedBy(assignedBy);
        assignment.setAssignedAt(LocalDateTime.now());
        
        teacherLevelAssignmentRepository.save(assignment);
        
        log.info("Successfully assigned teacher: {} to level: {}", teacher.getFullName(), level.getName());
    }
    
    /**
     * Update existing teacher assignment
     */
    @Transactional
    public void updateTeacherAssignment(UUID assignmentId, String competencyLevel, 
                                      Integer maxClassesForLevel, String specialization, UUID updatedById) {
        log.info("Updating teacher assignment: {}", assignmentId);
        
        TeacherLevelAssignment assignment = teacherLevelAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        assignment.setCompetencyLevel(TeacherLevelAssignment.CompetencyLevel.valueOf(competencyLevel));
        assignment.setMaxClassesForLevel(maxClassesForLevel);
        assignment.setSpecialization(specialization != null ? 
                TeacherLevelAssignment.Specialization.valueOf(specialization) : null);
        
        teacherLevelAssignmentRepository.save(assignment);
        
        log.info("Successfully updated assignment for teacher: {} to level: {}", 
                assignment.getTeacher().getFullName(), assignment.getLevel().getName());
    }
    
    /**
     * Delete teacher assignment
     */
    @Transactional
    public void deleteTeacherAssignment(UUID assignmentId) {
        log.info("Deleting teacher assignment: {}", assignmentId);
        
        TeacherLevelAssignment assignment = teacherLevelAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        teacherLevelAssignmentRepository.delete(assignment);
        
        log.info("Successfully deleted assignment for teacher: {} from level: {}", 
                assignment.getTeacher().getFullName(), assignment.getLevel().getName());
    }
    
    /**
     * Auto-assign teachers to levels based on availability and competency
     */
    @Transactional
    public int autoAssignTeachersToLevels(UUID termId, Boolean overrideExisting, UUID assignedById) {
        log.info("Auto-assigning teachers for term: {}", termId);
        
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        User assignedBy = userRepository.findById(assignedById)
                .orElseThrow(() -> new RuntimeException("Assigned by user not found"));
        
        // Get all teachers with availability
        List<User> teachersWithAvailability = teacherAvailabilityRepository
                .findTeachersWhoSubmittedAvailability(termId);
        
        // Get all levels
        List<Level> allLevels = levelRepository.findAll();
        
        // Get existing assignments
        List<TeacherLevelAssignment> existingAssignments = overrideExisting ? 
                new ArrayList<>() : teacherLevelAssignmentRepository.findByTermId(termId);
        
        Set<String> existingPairs = existingAssignments.stream()
                .map(assignment -> assignment.getTeacher().getId() + "-" + assignment.getLevel().getId())
                .collect(Collectors.toSet());
        
        int assignmentsCreated = 0;
        
        // Simple auto-assignment logic
        for (User teacher : teachersWithAvailability) {
            // Get teacher's available slots count
            List<TeacherAvailability> availability = teacherAvailabilityRepository
                    .findAvailableSlotsByTeacherAndTerm(teacher.getId(), termId);
            
            int availableSlots = availability.size();
            
            // Assign to levels based on available slots
            List<Level> targetLevels = determineTargetLevels(teacher, availableSlots);
            
            for (Level level : targetLevels) {
                String pairKey = teacher.getId() + "-" + level.getId();
                if (!existingPairs.contains(pairKey)) {
                    TeacherLevelAssignment assignment = new TeacherLevelAssignment();
                    assignment.setTeacher(teacher);
                    assignment.setLevel(level);
                    assignment.setTerm(term);
                    assignment.setCompetencyLevel(determineCompetencyLevel(teacher, level));
                    assignment.setMaxClassesForLevel(Math.min(4, availableSlots / targetLevels.size()));
                    assignment.setSpecialization(determineSpecialization(teacher, level));
                    assignment.setAssignedBy(assignedBy);
                    assignment.setAssignedAt(LocalDateTime.now());
                    
                    teacherLevelAssignmentRepository.save(assignment);
                    assignmentsCreated++;
                    
                    existingPairs.add(pairKey);
                }
            }
        }
        
        log.info("Auto-assignment completed. Created {} assignments", assignmentsCreated);
        return assignmentsCreated;
    }
    
    /**
     * Get teacher workload analysis
     */
    public Map<String, Object> getTeacherWorkloadAnalysis(UUID termId) {
        log.info("Building teacher workload analysis for term: {}", termId);
        
        List<TeacherLevelAssignment> assignments = teacherLevelAssignmentRepository.findByTermId(termId);
        List<User> allTeachers = userRepository.findByRoleName("INSTRUCTOR");
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalTeachers", allTeachers.size());
        analysis.put("assignedTeachers", assignments.stream()
                .map(assignment -> assignment.getTeacher().getId())
                .collect(Collectors.toSet()).size());
        analysis.put("assignments", assignments.size());
        
        // Calculate workload distribution
        Map<UUID, Integer> teacherClassCounts = assignments.stream()
                .collect(Collectors.groupingBy(
                        assignment -> assignment.getTeacher().getId(),
                        Collectors.summingInt(assignment -> assignment.getMaxClassesForLevel() != null ? 
                                assignment.getMaxClassesForLevel() : 0)
                ));
        
        Map<String, Long> workloadDistribution = teacherClassCounts.values().stream()
                .collect(Collectors.groupingBy(this::categorizeWorkload, Collectors.counting()));
        
        analysis.put("workloadDistribution", workloadDistribution);
        
        return analysis;
    }
    
    // Helper methods
    
    private List<TeacherLevelAssignmentDto.TeacherSummary> buildTeacherSummaries(UUID termId, 
            List<TeacherLevelAssignment> existingAssignments) {
        
        List<User> allTeachers = userRepository.findByRoleName("INSTRUCTOR");
        Map<UUID, List<TeacherLevelAssignment>> assignmentsByTeacher = existingAssignments.stream()
                .collect(Collectors.groupingBy(assignment -> assignment.getTeacher().getId()));
        
        return allTeachers.stream()
                .map(teacher -> {
                    List<TeacherLevelAssignment> teacherAssignments = assignmentsByTeacher
                            .getOrDefault(teacher.getId(), new ArrayList<>());
                    
                    int totalMaxClasses = teacherAssignments.stream()
                            .mapToInt(assignment -> assignment.getMaxClassesForLevel() != null ? 
                                    assignment.getMaxClassesForLevel() : 0)
                            .sum();
                    
                    List<String> levelNames = teacherAssignments.stream()
                            .map(assignment -> assignment.getLevel().getName())
                            .collect(Collectors.toList());
                    
                    List<TeacherLevelAssignment.Specialization> specializations = teacherAssignments.stream()
                            .map(TeacherLevelAssignment::getSpecialization)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                    
                    // Check availability submission
                    List<User> teachersWithAvailability = teacherAvailabilityRepository
                            .findTeachersWhoSubmittedAvailability(termId);
                    boolean hasAvailability = teachersWithAvailability.contains(teacher);
                    
                    int availableSlots = 0;
                    if (hasAvailability) {
                        availableSlots = teacherAvailabilityRepository
                                .findAvailableSlotsByTeacherAndTerm(teacher.getId(), termId).size();
                    }
                    
                    return TeacherLevelAssignmentDto.TeacherSummary.builder()
                            .teacherId(teacher.getId())
                            .teacherName(teacher.getFullName())
                            .teacherUsername(teacher.getUsername())
                            .totalLevelsAssigned(teacherAssignments.size())
                            .totalMaxClasses(totalMaxClasses)
                            .assignedLevelNames(levelNames)
                            .specializations(specializations)
                            .hasAvailabilitySubmitted(hasAvailability)
                            .availableSlotsCount(availableSlots)
                            .workloadStatus(categorizeWorkload(totalMaxClasses))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private List<TeacherLevelAssignmentDto.LevelSummary> buildLevelSummaries(UUID termId,
            List<TeacherLevelAssignment> existingAssignments) {
        
        List<Level> allLevels = levelRepository.findAll();
        Map<UUID, List<TeacherLevelAssignment>> assignmentsByLevel = existingAssignments.stream()
                .collect(Collectors.groupingBy(assignment -> assignment.getLevel().getId()));
        
        return allLevels.stream()
                .map(level -> {
                    List<TeacherLevelAssignment> levelAssignments = assignmentsByLevel
                            .getOrDefault(level.getId(), new ArrayList<>());
                    
                    // Estimate required teachers based on student count
                    List<Object[]> levelDistribution = studentAssessmentRepository.findLevelDistributionByTerm(termId);
                    Long studentCount = levelDistribution.stream()
                            .filter(row -> ((Level) row[0]).getId().equals(level.getId()))
                            .map(row -> (Long) row[1])
                            .findFirst()
                            .orElse(0L);
                    
                    int requiredTeachers = Math.max(1, (int) Math.ceil(studentCount / 9.0)); // Assuming 9 students per class
                    
                    List<TeacherLevelAssignment.CompetencyLevel> competencyDistribution = levelAssignments.stream()
                            .map(TeacherLevelAssignment::getCompetencyLevel)
                            .collect(Collectors.toList());
                    
                    String coverageStatus = levelAssignments.size() >= requiredTeachers ? "ADEQUATE" : "INSUFFICIENT";
                    
                    return TeacherLevelAssignmentDto.LevelSummary.builder()
                            .levelId(level.getId())
                            .levelName(level.getName())
                            .levelDescription(level.getDescription())
                            .assignedTeachers(levelAssignments.size())
                            .requiredTeachers(requiredTeachers)
                            .competencyDistribution(competencyDistribution)
                            .coverageStatus(coverageStatus)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private TeacherLevelAssignmentDto.WorkloadAnalysis buildWorkloadAnalysis(UUID termId,
            List<TeacherLevelAssignment> existingAssignments) {
        
        List<User> allTeachers = userRepository.findByRoleName("INSTRUCTOR");
        Set<UUID> assignedTeachers = existingAssignments.stream()
                .map(assignment -> assignment.getTeacher().getId())
                .collect(Collectors.toSet());
        
        int totalClassCapacity = existingAssignments.stream()
                .mapToInt(assignment -> assignment.getMaxClassesForLevel() != null ? 
                        assignment.getMaxClassesForLevel() : 0)
                .sum();
        
        double averageClasses = assignedTeachers.isEmpty() ? 0.0 : 
                (double) totalClassCapacity / assignedTeachers.size();
        
        Map<UUID, Integer> teacherClassCounts = existingAssignments.stream()
                .collect(Collectors.groupingBy(
                        assignment -> assignment.getTeacher().getId(),
                        Collectors.summingInt(assignment -> assignment.getMaxClassesForLevel() != null ? 
                                assignment.getMaxClassesForLevel() : 0)
                ));
        
        Map<String, Integer> workloadDistribution = new HashMap<>();
        teacherClassCounts.values().forEach(classCount -> {
            String category = categorizeWorkload(classCount);
            workloadDistribution.merge(category, 1, Integer::sum);
        });
        
        List<String> recommendations = generateRecommendations(allTeachers.size(), assignedTeachers.size(), 
                workloadDistribution, averageClasses);
        
        return TeacherLevelAssignmentDto.WorkloadAnalysis.builder()
                .totalTeachers(allTeachers.size())
                .assignedTeachers(assignedTeachers.size())
                .unassignedTeachers(allTeachers.size() - assignedTeachers.size())
                .averageClassesPerTeacher(averageClasses)
                .totalClassCapacity(totalClassCapacity)
                .workloadDistribution(workloadDistribution)
                .recommendations(recommendations)
                .build();
    }
    
    private List<TeacherLevelAssignmentDto.UnassignedTeacher> findUnassignedTeachers(UUID termId,
            List<TeacherLevelAssignment> existingAssignments) {
        
        List<User> allTeachers = userRepository.findByRoleName("INSTRUCTOR");
        Set<UUID> assignedTeacherIds = existingAssignments.stream()
                .map(assignment -> assignment.getTeacher().getId())
                .collect(Collectors.toSet());
        
        return allTeachers.stream()
                .filter(teacher -> !assignedTeacherIds.contains(teacher.getId()))
                .map(teacher -> {
                    List<User> teachersWithAvailability = teacherAvailabilityRepository
                            .findTeachersWhoSubmittedAvailability(termId);
                    boolean hasAvailability = teachersWithAvailability.contains(teacher);
                    
                    int availableSlots = hasAvailability ? 
                            teacherAvailabilityRepository
                                    .findAvailableSlotsByTeacherAndTerm(teacher.getId(), termId).size() : 0;
                    
                    String reason = hasAvailability ? "Available for assignment" : "No availability submitted";
                    
                    return TeacherLevelAssignmentDto.UnassignedTeacher.builder()
                            .teacherId(teacher.getId())
                            .teacherName(teacher.getFullName())
                            .teacherUsername(teacher.getUsername())
                            .hasAvailabilitySubmitted(hasAvailability)
                            .availableSlotsCount(availableSlots)
                            .suggestedLevels(new ArrayList<>()) // TODO: Implement level suggestions
                            .reason(reason)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private List<TeacherLevelAssignmentDto.TeacherOption> buildTeacherOptions() {
        return userRepository.findByRoleName("INSTRUCTOR").stream()
                .map(teacher -> TeacherLevelAssignmentDto.TeacherOption.builder()
                        .teacherId(teacher.getId())
                        .teacherName(teacher.getFullName())
                        .teacherUsername(teacher.getUsername())
                        .isActive(teacher.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<TeacherLevelAssignmentDto.LevelOption> buildLevelOptions() {
        return levelRepository.findAll().stream()
                .map(level -> TeacherLevelAssignmentDto.LevelOption.builder()
                        .levelId(level.getId())
                        .levelName(level.getName())
                        .levelDescription(level.getDescription())
                        .isActive(true) // Assuming levels are always active
                        .build())
                .collect(Collectors.toList());
    }
    
    private String categorizeWorkload(int classCount) {
        if (classCount < 3) return "UNDERLOADED";
        if (classCount <= 6) return "OPTIMAL";
        return "OVERLOADED";
    }
    
    private List<Level> determineTargetLevels(User teacher, int availableSlots) {
        // Simple logic: assign to basic levels first
        List<Level> allLevels = levelRepository.findAll();
        return allLevels.stream()
                .filter(level -> level.getName().contains("Tahsin"))
                .limit(Math.max(1, availableSlots / 5)) // Assign to multiple levels if many slots available
                .collect(Collectors.toList());
    }
    
    private TeacherLevelAssignment.CompetencyLevel determineCompetencyLevel(User teacher, Level level) {
        // Simple logic: new teachers get JUNIOR, experienced get SENIOR
        return TeacherLevelAssignment.CompetencyLevel.SENIOR; // Default for now
    }
    
    private TeacherLevelAssignment.Specialization determineSpecialization(User teacher, Level level) {
        // Simple logic based on level name
        if (level.getName().contains("1")) return TeacherLevelAssignment.Specialization.FOUNDATION;
        if (level.getName().contains("Advanced")) return TeacherLevelAssignment.Specialization.ADVANCED;
        return TeacherLevelAssignment.Specialization.MIXED;
    }
    
    private List<String> generateRecommendations(int totalTeachers, int assignedTeachers, 
            Map<String, Integer> workloadDistribution, double averageClasses) {
        List<String> recommendations = new ArrayList<>();
        
        if (assignedTeachers < totalTeachers * 0.7) {
            recommendations.add("Consider assigning more teachers to increase coverage");
        }
        
        if (averageClasses > 7) {
            recommendations.add("Average workload is high - consider redistributing classes");
        }
        
        if (workloadDistribution.getOrDefault("OVERLOADED", 0) > 0) {
            recommendations.add("Some teachers are overloaded - redistribute classes for better balance");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Teacher assignments are well balanced");
        }
        
        return recommendations;
    }
}