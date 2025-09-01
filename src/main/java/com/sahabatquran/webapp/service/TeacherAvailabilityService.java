package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.TeacherAvailabilityDto;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherAvailabilityService {
    
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final AcademicTermRepository academicTermRepository;
    private final UserRepository userRepository;
    private final ClassGroupRepository classGroupRepository;
    private final ClassSessionRepository classSessionRepository;
    private final SessionMaterialRepository sessionMaterialRepository;
    private final ClassPreparationChecklistRepository checklistRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    /**
     * Get teacher's availability matrix for a specific term
     */
    public TeacherAvailabilityDto.AvailabilityMatrix getTeacherAvailabilityMatrix(UUID teacherId, UUID termId) {
        log.info("Getting availability matrix for teacher: {} and term: {}", teacherId, termId);
        
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        List<TeacherAvailability> existingAvailability = teacherAvailabilityRepository
                .findByTeacherAndTerm(teacher, term);
        
        // Build weekly matrix
        Map<Integer, Map<TeacherAvailability.SessionTime, Boolean>> weeklyMatrix = new HashMap<>();
        
        // Initialize all slots as false
        for (int day = 1; day <= 7; day++) {
            Map<TeacherAvailability.SessionTime, Boolean> daySlots = new HashMap<>();
            for (TeacherAvailability.SessionTime session : TeacherAvailability.SessionTime.values()) {
                daySlots.put(session, false);
            }
            weeklyMatrix.put(day, daySlots);
        }
        
        // Set existing availability
        existingAvailability.forEach(availability -> {
            weeklyMatrix.get(availability.getDayOfWeek())
                    .put(availability.getSessionTime(), availability.getIsAvailable());
        });
        
        // Get additional preferences
        Integer maxClasses = existingAvailability.isEmpty() ? 6 : 
                existingAvailability.get(0).getMaxClassesPerWeek();
        String preferences = existingAvailability.isEmpty() ? "" : 
                existingAvailability.get(0).getPreferences();
        
        return TeacherAvailabilityDto.AvailabilityMatrix.builder()
                .weeklyMatrix(weeklyMatrix)
                .maxClassesPerWeek(maxClasses)
                .preferences(preferences)
                .preferredLevels(new ArrayList<>()) // TODO: Implement preferred levels
                .specialConstraints("")
                .build();
    }
    
    /**
     * Submit teacher availability for a term
     */
    @Transactional
    public void submitTeacherAvailability(UUID teacherId, UUID termId, 
                                         TeacherAvailabilityDto.AvailabilityMatrix matrix,
                                         List<String> availabilitySlots) {
        log.info("Submitting availability for teacher: {} and term: {}", teacherId, termId);
        
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        // Delete existing availability for this teacher and term
        List<TeacherAvailability> existing = teacherAvailabilityRepository
                .findByTeacherAndTerm(teacher, term);
        teacherAvailabilityRepository.deleteAll(existing);
        
        // Process availability slots
        Set<String> availableSlots = new HashSet<>(availabilitySlots != null ? availabilitySlots : List.of());
        
        // Create new availability records for all possible slots
        for (int day = 1; day <= 7; day++) {
            for (TeacherAvailability.SessionTime session : TeacherAvailability.SessionTime.values()) {
                String slotKey = day + "-" + session.name();
                boolean isAvailable = availableSlots.contains(slotKey);
                
                TeacherAvailability availability = new TeacherAvailability();
                availability.setTeacher(teacher);
                availability.setTerm(term);
                availability.setDayOfWeek(day);
                availability.setSessionTime(session);
                availability.setIsAvailable(isAvailable);
                availability.setMaxClassesPerWeek(matrix.getMaxClassesPerWeek());
                availability.setPreferences(matrix.getPreferences());
                availability.setSubmittedAt(LocalDateTime.now());
                
                teacherAvailabilityRepository.save(availability);
            }
        }
        
        log.info("Successfully submitted availability for teacher: {}", teacher.getFullName());
    }
    
    /**
     * Check if teacher can submit availability for a term
     */
    public boolean canSubmitAvailability(UUID termId) {
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        // Check if term is in planning status and before preparation deadline
        return term.getStatus() == AcademicTerm.TermStatus.PLANNING && 
               (term.getPreparationDeadline() == null || 
                term.getPreparationDeadline().isAfter(java.time.LocalDate.now()));
    }
    
    /**
     * Get instructor's assigned classes for a term
     */
    public List<Object> getInstructorAssignedClasses(UUID instructorId, UUID termId) {
        log.info("Getting assigned classes for instructor: {} and term: {}", instructorId, termId);
        
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        AcademicTerm term = academicTermRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));
        
        List<ClassGroup> assignedClasses = classGroupRepository
                .findByInstructorAndTerm(instructor, term);
        
        return assignedClasses.stream()
                .map(classGroup -> {
                    // Get enrollment count
                    List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);
                    long activeEnrollments = enrollments.stream()
                            .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                            .count();
                    
                    // Get session preparation status
                    List<ClassSession> sessions = classSessionRepository.findByClassGroup(classGroup);
                    long readySessions = sessions.stream()
                            .filter(session -> session.getPreparationStatus() == ClassSession.PreparationStatus.READY)
                            .count();
                    
                    Map<String, Object> classInfo = new HashMap<>();
                    classInfo.put("id", classGroup.getId());
                    classInfo.put("name", classGroup.getName());
                    classInfo.put("level", classGroup.getLevel().getName());
                    classInfo.put("schedule", classGroup.getSchedule()); // Assuming schedule field exists
                    classInfo.put("enrollmentCount", activeEnrollments);
                    classInfo.put("maxStudents", classGroup.getMaxStudents());
                    classInfo.put("totalSessions", sessions.size());
                    classInfo.put("readySessions", readySessions);
                    classInfo.put("preparationProgress", sessions.isEmpty() ? 0 : (readySessions * 100 / sessions.size()));
                    
                    return classInfo;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Check if instructor has access to a class
     */
    public boolean hasAccessToClass(UUID instructorId, UUID classId) {
        Optional<ClassGroup> classGroup = classGroupRepository.findById(classId);
        return classGroup.isPresent() && 
               classGroup.get().getInstructor().getId().equals(instructorId);
    }
    
    /**
     * Get class preparation data for instructor
     */
    public Map<String, Object> getClassPreparationData(UUID classId, UUID instructorId) {
        log.info("Getting class preparation data for class: {} and instructor: {}", classId, instructorId);
        
        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Verify instructor access
        if (!classGroup.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Access denied to this class");
        }
        
        // Get enrollments and student information
        List<Enrollment> enrollments = enrollmentRepository.findByClassGroup(classGroup);
        List<Map<String, Object>> students = enrollments.stream()
                .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                .map(enrollment -> {
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("id", enrollment.getStudent().getId());
                    studentInfo.put("name", enrollment.getStudent().getFullName());
                    studentInfo.put("username", enrollment.getStudent().getUsername());
                    // TODO: Add assessment background, special needs, etc.
                    return studentInfo;
                })
                .collect(Collectors.toList());
        
        // Get class sessions
        List<ClassSession> sessions = classSessionRepository.findByClassGroup(classGroup);
        
        // Get materials for the most recent session
        List<SessionMaterial> materials = new ArrayList<>();
        if (!sessions.isEmpty()) {
            materials = sessionMaterialRepository.findBySession(sessions.get(0));
        }
        
        // Get preparation checklist
        List<ClassPreparationChecklist> checklist = new ArrayList<>();
        if (!sessions.isEmpty()) {
            checklist = checklistRepository.findBySession(sessions.get(0));
        }
        
        Map<String, Object> preparationData = new HashMap<>();
        preparationData.put("classGroup", classGroup);
        preparationData.put("students", students);
        preparationData.put("sessions", sessions);
        preparationData.put("materials", materials);
        preparationData.put("checklist", checklist);
        preparationData.put("canEdit", true); // TODO: Implement proper permission checking
        
        return preparationData;
    }
    
    /**
     * Update preparation checklist for a session
     */
    @Transactional
    public void updatePreparationChecklist(UUID classId, UUID instructorId, List<UUID> completedItems) {
        log.info("Updating preparation checklist for class: {} by instructor: {}", classId, instructorId);
        
        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Verify instructor access
        if (!classGroup.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Access denied to this class");
        }
        
        // Get the current session (or create one if none exists)
        List<ClassSession> sessions = classSessionRepository.findByClassGroup(classGroup);
        ClassSession currentSession;
        if (sessions.isEmpty()) {
            // Create a new session
            currentSession = new ClassSession();
            currentSession.setClassGroup(classGroup);
            currentSession.setInstructor(classGroup.getInstructor());
            currentSession.setSessionDate(java.time.LocalDate.now().plusDays(7)); // Next week
            currentSession.setPreparationStatus(ClassSession.PreparationStatus.IN_PROGRESS);
            currentSession = classSessionRepository.save(currentSession);
        } else {
            currentSession = sessions.get(0); // Get the most recent session
        }
        
        // Update checklist items
        List<ClassPreparationChecklist> allItems = checklistRepository.findBySession(currentSession);
        for (ClassPreparationChecklist item : allItems) {
            boolean shouldBeCompleted = completedItems.contains(item.getId());
            if (item.getIsCompleted() != shouldBeCompleted) {
                item.setIsCompleted(shouldBeCompleted);
                if (shouldBeCompleted) {
                    item.setCompletedAt(LocalDateTime.now());
                    item.setCompletedBy(userRepository.findById(instructorId).orElse(null));
                } else {
                    item.setCompletedAt(null);
                    item.setCompletedBy(null);
                }
                checklistRepository.save(item);
            }
        }
        
        // Check if session is ready (all checklist items completed)
        boolean allCompleted = allItems.stream().allMatch(ClassPreparationChecklist::getIsCompleted);
        if (allCompleted && currentSession.getPreparationStatus() != ClassSession.PreparationStatus.READY) {
            currentSession.setPreparationStatus(ClassSession.PreparationStatus.READY);
            classSessionRepository.save(currentSession);
        }
    }
    
    /**
     * Upload class material
     */
    @Transactional
    public void uploadClassMaterial(UUID classId, UUID instructorId, MultipartFile file, 
                                   String materialType, String materialTitle, Boolean shareWithStudents) {
        log.info("Uploading material for class: {} by instructor: {}", classId, instructorId);
        
        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Verify instructor access
        if (!classGroup.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Access denied to this class");
        }
        
        // Get or create current session
        List<ClassSession> sessions = classSessionRepository.findByClassGroup(classGroup);
        ClassSession currentSession;
        if (sessions.isEmpty()) {
            currentSession = new ClassSession();
            currentSession.setClassGroup(classGroup);
            currentSession.setInstructor(classGroup.getInstructor());
            currentSession.setSessionDate(java.time.LocalDate.now().plusDays(7));
            currentSession.setPreparationStatus(ClassSession.PreparationStatus.IN_PROGRESS);
            currentSession = classSessionRepository.save(currentSession);
        } else {
            currentSession = sessions.get(0);
        }
        
        // TODO: Implement actual file upload to storage
        String filePath = "/uploads/classes/" + classId + "/" + file.getOriginalFilename();
        
        // Create session material record
        SessionMaterial material = new SessionMaterial();
        material.setSession(currentSession);
        material.setMaterialType(SessionMaterial.MaterialType.valueOf(materialType.toUpperCase()));
        material.setFilePath(filePath);
        material.setMaterialTitle(materialTitle);
        material.setIsSharedWithStudents(shareWithStudents != null ? shareWithStudents : false);
        
        sessionMaterialRepository.save(material);
        
        log.info("Successfully uploaded material: {} for class: {}", materialTitle, classGroup.getName());
    }
}