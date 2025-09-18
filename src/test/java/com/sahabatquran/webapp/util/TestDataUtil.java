package com.sahabatquran.webapp.util;

import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.SessionRepository;
import com.sahabatquran.webapp.repository.TimeSlotRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Utility class for generating test data with Faker library.
 * Handles unique constraints and column length limitations.
 */
@Component
public class TestDataUtil {
    
    private final Faker faker = new Faker();
    private static final String TEST_MARKER = "TEST_DATA_";
    // Random helper not used post-refactor
    
    @Autowired(required = false)
    private TimeSlotRepository timeSlotRepository;
    
    @Autowired(required = false)
    private SessionRepository sessionRepository;
    
    /**
     * Generate a unique test identifier with length consideration
     */
    public String generateTestId(String prefix, int maxLength) {
        String baseId = TEST_MARKER + prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
        return baseId.length() > maxLength ? baseId.substring(0, maxLength) : baseId;
    }
    
    /**
     * Generate a unique username for testing
     */
    public String generateTestUsername() {
        return generateTestId("user", 40); // username column is 50, leaving some margin
    }
    
    /**
     * Generate a unique email for testing
     */
    public String generateTestEmail() {
        String localPart = generateTestId("user", 30);
        return localPart + "@test.example.com";
    }
    
    /**
     * Generate a unique class name for testing
     */
    public String generateTestClassName() {
        return generateTestId("class", 80); // class name column is 100
    }
    
    /**
     * Generate a unique term name for testing
     */
    public String generateTestTermName() {
        return generateTestId("term", 30); // term_name column is 50
    }
    
    /**
     * Generate a unique level name for testing
     */
    public String generateTestLevelName() {
        return generateTestId("level", 30); // level name column is 50
    }
    
    /**
     * Create a test User entity
     */
    public User createTestUser(String rolePrefix) {
        User user = new User();
        user.setUsername(generateTestUsername());
        user.setEmail(generateTestEmail());
        user.setFullName(faker.name().fullName());
        user.setPhoneNumber(faker.phoneNumber().cellPhone());
        user.setAddress(faker.address().fullAddress());
        user.setIsActive(true);
        return user;
    }
    
    /**
     * Create a test Level entity
     */
    public Level createTestLevel() {
        Level level = new Level();
        level.setName(generateTestLevelName());
        level.setDescription(faker.lorem().sentence(10));
        level.setOrderNumber(faker.number().numberBetween(1, 100));
        return level;
    }
    
    /**
     * Create a test AcademicTerm entity
     */
    public AcademicTerm createTestAcademicTerm() {
        AcademicTerm term = new AcademicTerm();
        term.setTermName(generateTestTermName());
        term.setStartDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
        term.setEndDate(LocalDate.now().plusDays(faker.number().numberBetween(90, 180)));
        term.setStatus(faker.options().option(AcademicTerm.TermStatus.class));
        term.setPreparationDeadline(LocalDate.now().plusDays(faker.number().numberBetween(1, 15)));
        return term;
    }
    
    /**
     * Create a test TeacherAvailability entity
     */
    public TeacherAvailability createTestTeacherAvailability(User teacher, AcademicTerm term, Session session) {
        TeacherAvailability availability = new TeacherAvailability();
        availability.setTeacher(teacher);
        availability.setTerm(term);
        // Map to a TimeSlot using provided session and a random day
        if (timeSlotRepository != null) {
            TimeSlot.DayOfWeek day = faker.options().option(TimeSlot.DayOfWeek.class);
            TimeSlot timeSlot = timeSlotRepository.findBySessionAndDayOfWeek(session, day)
                    .orElseGet(() -> {
                        TimeSlot ts = new TimeSlot();
                        ts.setSession(session);
                        ts.setDayOfWeek(day);
                        return timeSlotRepository.save(ts);
                    });
            availability.setTimeSlot(timeSlot);
        }
        availability.setIsAvailable(faker.bool().bool());
        availability.setMaxClassesPerWeek(faker.number().numberBetween(3, 8));
        availability.setPreferences(faker.lorem().sentence(20));
        return availability;
    }
    
    
    /**
     * Create a test TeacherLevelAssignment entity
     */
    public TeacherLevelAssignment createTestTeacherLevelAssignment(User teacher, Level level, AcademicTerm term) {
        TeacherLevelAssignment assignment = new TeacherLevelAssignment();
        assignment.setTeacher(teacher);
        assignment.setLevel(level);
        assignment.setTerm(term);
        assignment.setCompetencyLevel(faker.options().option(TeacherLevelAssignment.CompetencyLevel.class));
        assignment.setMaxClassesForLevel(faker.number().numberBetween(2, 5));
        assignment.setSpecialization(faker.options().option(TeacherLevelAssignment.Specialization.class));
        assignment.setNotes(faker.lorem().sentence(15));
        return assignment;
    }
    
    /**
     * Create a test Class entity
     */
    public ClassGroup createTestClassGroup(Level level, User instructor, AcademicTerm term) {
        ClassGroup classGroup = new ClassGroup();
        classGroup.setName(generateTestClassName());
        classGroup.setLevel(level);
        classGroup.setInstructor(instructor);
        classGroup.setTerm(term);
        classGroup.setCapacity(faker.number().numberBetween(10, 25));
        classGroup.setMinStudents(faker.number().numberBetween(5, 8));
        classGroup.setMaxStudents(faker.number().numberBetween(10, 15));
        // Assign a default TimeSlot if repositories are available
        if (sessionRepository != null && timeSlotRepository != null) {
            List<Session> activeSessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
            if (!activeSessions.isEmpty()) {
                Session s = activeSessions.get(0);
                TimeSlot.DayOfWeek day = faker.options().option(TimeSlot.DayOfWeek.class);
                TimeSlot ts = timeSlotRepository.findBySessionAndDayOfWeek(s, day)
                        .orElseGet(() -> {
                            TimeSlot nts = new TimeSlot();
                            nts.setSession(s);
                            nts.setDayOfWeek(day);
                            return timeSlotRepository.save(nts);
                        });
                classGroup.setTimeSlot(ts);
            }
        }
        classGroup.setLocation("Room " + faker.number().numberBetween(100, 999));
        classGroup.setIsActive(true);
        classGroup.setStudentCategoryMix(faker.options().option(ClassGroup.StudentCategoryMix.class));
        classGroup.setClassType(faker.options().option(ClassGroup.ClassType.class));
        return classGroup;
    }
    
    /**
     * Create a test Enrollment entity
     */
    public Enrollment createTestEnrollment(User student, ClassGroup classGroup) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClassGroup(classGroup);
        enrollment.setEnrollmentDate(LocalDate.now().minusDays(faker.number().numberBetween(1, 30)));
        enrollment.setStatus(faker.options().option(Enrollment.EnrollmentStatus.class));
        return enrollment;
    }
    
    /**
     * Create a test StudentAssessment entity
     */
    public StudentAssessment createTestStudentAssessment(User student, AcademicTerm term, Level determinedLevel) {
        StudentAssessment assessment = new StudentAssessment();
        assessment.setStudent(student);
        assessment.setTerm(term);
        assessment.setStudentCategory(faker.options().option(StudentAssessment.StudentCategory.class));
        assessment.setAssessmentType(faker.options().option(StudentAssessment.AssessmentType.class));
        assessment.setAssessmentScore(BigDecimal.valueOf(faker.number().randomDouble(2, 40, 100)));
        
        // Set grade only for term exams
        if (assessment.getAssessmentType() == StudentAssessment.AssessmentType.MIDTERM || 
            assessment.getAssessmentType() == StudentAssessment.AssessmentType.FINAL) {
            assessment.setAssessmentGrade(faker.options().option("A", "B", "C", "D"));
        }
        
        assessment.setDeterminedLevel(determinedLevel);
        assessment.setAssessmentDate(LocalDate.now().minusDays(faker.number().numberBetween(1, 30)));
        assessment.setAssessmentNotes(faker.lorem().sentence(20));
        assessment.setIsValidated(faker.bool().bool());
        return assessment;
    }
    
    /**
     * Create a test ClassSession entity
     */
    public ClassSession createTestClassSession(ClassGroup classGroup, User instructor) {
        ClassSession session = new ClassSession();
        session.setClassGroup(classGroup);
        session.setInstructor(instructor);
        session.setSessionDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
        session.setSessionNumber(faker.number().numberBetween(1, 20));
        
        // Add some learning objectives
        List<String> objectives = new ArrayList<>();
        for (int i = 0; i < faker.number().numberBetween(2, 5); i++) {
            objectives.add(faker.lorem().sentence(8));
        }
        session.setLearningObjectives(objectives);
        
        session.setPreparationStatus(faker.options().option(ClassSession.PreparationStatus.class));
        
        // Add teaching materials as JSON
        Map<String, Object> materials = new HashMap<>();
        materials.put("textbook", "Chapter " + faker.number().numberBetween(1, 20));
        materials.put("exercises", faker.number().numberBetween(5, 15) + " practice items");
        session.setTeachingMaterials(materials);
        
        return session;
    }
    
    /**
     * Create a test SessionMaterial entity
     */
    public SessionMaterial createTestSessionMaterial(ClassSession session, User uploadedBy) {
        SessionMaterial material = new SessionMaterial();
        material.setSession(session);
        material.setMaterialType(faker.options().option(SessionMaterial.MaterialType.class));
        material.setFilePath("/test/materials/" + UUID.randomUUID().toString());
        material.setMaterialTitle(faker.lorem().words(3).toString().replace("[", "").replace("]", ""));
        material.setMaterialDescription(faker.lorem().sentence(15));
        material.setFileSize((long) faker.number().numberBetween(1000, 50000000));
        material.setMimeType(generateMimeType(material.getMaterialType()));
        material.setIsSharedWithStudents(faker.bool().bool());
        material.setUploadedBy(uploadedBy);
        return material;
    }
    
    /**
     * Create a test ClassPreparationChecklist entity
     */
    public ClassPreparationChecklist createTestPreparationChecklistItem(ClassSession session) {
        ClassPreparationChecklist item = new ClassPreparationChecklist();
        item.setSession(session);
        item.setChecklistItem(faker.lorem().sentence(8));
        item.setItemCategory(faker.options().option(ClassPreparationChecklist.ItemCategory.class));
        item.setIsCompleted(faker.bool().bool());
        item.setDisplayOrder(faker.number().numberBetween(1, 10));
        item.setNotes(faker.lorem().sentence(10));
        return item;
    }
    
    /**
     * Create a test ClassSizeConfiguration entity
     */
    public ClassSizeConfiguration createTestClassSizeConfiguration(Level level) {
        ClassSizeConfiguration config = new ClassSizeConfiguration();
        String configKey = level != null ? 
            level.getName().toLowerCase().replace(" ", "_") + "." + faker.options().option("min", "max") :
            "test." + faker.options().option("min", "max");
        config.setConfigKey(configKey);
        config.setConfigValue(faker.number().numberBetween(5, 20));
        config.setLevel(level);
        config.setDescription(faker.lorem().sentence(10));
        return config;
    }
    
    /**
     * Create a test GeneratedClassProposal entity
     */
    public GeneratedClassProposal createTestClassProposal(AcademicTerm term, User generatedBy) {
        GeneratedClassProposal proposal = new GeneratedClassProposal();
        proposal.setTerm(term);
        proposal.setGenerationRun(faker.number().numberBetween(1, 10));
        
        // Create proposal data as JSON
        Map<String, Object> proposalData = new HashMap<>();
        proposalData.put("totalClasses", faker.number().numberBetween(10, 25));
        proposalData.put("totalStudents", faker.number().numberBetween(100, 300));
        proposalData.put("algorithm", "test-algorithm-v1");
        proposal.setProposalData(proposalData);
        
        proposal.setOptimizationScore(BigDecimal.valueOf(faker.number().randomDouble(2, 60, 100)));
        proposal.setConflictCount(faker.number().numberBetween(0, 5));
        proposal.setGeneratedBy(generatedBy);
        proposal.setIsApproved(faker.bool().bool());
        return proposal;
    }
    
    // Removed schedule generator; TimeSlot is now the single source of scheduling truth
    
    /**
     * Generate appropriate MIME type based on material type
     */
    private String generateMimeType(SessionMaterial.MaterialType materialType) {
        return switch (materialType) {
            case AUDIO -> faker.options().option("audio/mp3", "audio/wav", "audio/m4a");
            case VIDEO -> faker.options().option("video/mp4", "video/avi", "video/mov");
            case TEXT -> faker.options().option("text/plain", "text/html", "application/rtf");
            case WORKSHEET, PRESENTATION -> faker.options().option("application/pdf", "application/msword", "application/vnd.ms-powerpoint");
            default -> "application/octet-stream";
        };
    }
    
    /**
     * Check if an entity has test marker (for cleanup purposes)
     */
    public static boolean hasTestMarker(String value) {
        return value != null && value.contains(TEST_MARKER);
    }
    
    /**
     * Get test marker prefix
     */
    public static String getTestMarker() {
        return TEST_MARKER;
    }
}