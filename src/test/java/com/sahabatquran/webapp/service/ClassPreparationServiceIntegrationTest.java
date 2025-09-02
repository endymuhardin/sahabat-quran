package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.repository.*;
import com.sahabatquran.webapp.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for class preparation business process scenarios.
 * Tests inter-table data mutations and complex business workflows.
 */
@Sql(scripts = "/test-data/class-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-class-preparation-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
class ClassPreparationServiceIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private AcademicTermRepository academicTermRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LevelRepository levelRepository;
    
    @Autowired
    private TeacherAvailabilityRepository teacherAvailabilityRepository;
    
    @Autowired
    private TeacherLevelAssignmentRepository teacherLevelAssignmentRepository;
    
    @Autowired
    private StudentAssessmentRepository studentAssessmentRepository;
    
    @Autowired
    private ClassGroupRepository classGroupRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private ClassSessionRepository classSessionRepository;
    
    @Autowired
    private SessionMaterialRepository sessionMaterialRepository;
    
    @Autowired
    private ClassPreparationChecklistRepository checklistRepository;
    
    @Autowired
    private GeneratedClassProposalRepository proposalRepository;
    
    @Autowired
    private TestDataUtil testDataUtil;
    
    private AcademicTerm testTerm;
    private User testTeacher1;
    private User testTeacher2;
    private User testStudent1;
    private User testStudent2;
    private Level testLevelBasic;
    private Level testLevelIntermediate;
    
    @BeforeEach
    void setUp() {
        testTerm = academicTermRepository.findByTermName("TEST_TERM_2024").orElseThrow();
        testTeacher1 = userRepository.findByUsername("TEST_TEACHER_1").orElseThrow();
        testTeacher2 = userRepository.findByUsername("TEST_TEACHER_2").orElseThrow();
        testStudent1 = userRepository.findByUsername("TEST_STUDENT_1").orElseThrow();
        testStudent2 = userRepository.findByUsername("TEST_STUDENT_2").orElseThrow();
        testLevelBasic = levelRepository.findByName("TEST_LEVEL_BASIC").orElseThrow();
        testLevelIntermediate = levelRepository.findByName("TEST_LEVEL_INTERMEDIATE").orElseThrow();
    }
    
    @Test
    void completeClassPreparationWorkflow_ShouldCreateAllRelatedEntities() {
        // Scenario: Complete workflow from term setup to class ready
        
        // Step 1: Verify teacher availability exists (created by setup script)
        List<TeacherAvailability> existingAvailability = teacherAvailabilityRepository
            .findByTeacherAndTerm(testTeacher1, testTerm);
        assertThat(existingAvailability).isNotEmpty();
        
        // Step 2: Verify teacher level assignment exists (created by setup script)
        List<TeacherLevelAssignment> existingAssignments = teacherLevelAssignmentRepository
            .findByTeacherAndTerm(testTeacher1, testTerm);
        assertThat(existingAssignments).isNotEmpty();
        
        // Step 3: Student assessments are processed
        StudentAssessment assessment1 = testDataUtil
            .createTestStudentAssessment(testStudent1, testTerm, testLevelBasic);
        assessment1.setStudentCategory(StudentAssessment.StudentCategory.NEW);
        assessment1.setAssessmentType(StudentAssessment.AssessmentType.PLACEMENT);
        assessment1.setAssessmentScore(BigDecimal.valueOf(75.0));
        assessment1.setIsValidated(true);
        studentAssessmentRepository.save(assessment1);
        
        StudentAssessment assessment2 = testDataUtil
            .createTestStudentAssessment(testStudent2, testTerm, testLevelBasic);
        assessment2.setStudentCategory(StudentAssessment.StudentCategory.NEW);
        assessment2.setAssessmentType(StudentAssessment.AssessmentType.PLACEMENT);
        assessment2.setAssessmentScore(BigDecimal.valueOf(80.0));
        assessment2.setIsValidated(true);
        studentAssessmentRepository.save(assessment2);
        
        // Step 4: Class is generated/created
        ClassGroup testClassGroup = testDataUtil.createTestClassGroup(testLevelBasic, testTeacher1, testTerm);
        testClassGroup.setMinStudents(2);
        testClassGroup.setMaxStudents(10);
        classGroupRepository.save(testClassGroup);
        
        // Step 5: Students are enrolled
        Enrollment enrollment1 = testDataUtil.createTestEnrollment(testStudent1, testClassGroup);
        enrollment1.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment1);
        
        Enrollment enrollment2 = testDataUtil.createTestEnrollment(testStudent2, testClassGroup);
        enrollment2.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment2);
        
        // Step 6: Teacher prepares first session
        ClassSession session = testDataUtil.createTestClassSession(testClassGroup, testTeacher1);
        session.setPreparationStatus(ClassSession.PreparationStatus.IN_PROGRESS);
        classSessionRepository.save(session);
        
        // Step 7: Teacher adds materials
        SessionMaterial material1 = testDataUtil.createTestSessionMaterial(session, testTeacher1);
        material1.setMaterialType(SessionMaterial.MaterialType.TEXT);
        material1.setIsSharedWithStudents(true);
        sessionMaterialRepository.save(material1);
        
        SessionMaterial material2 = testDataUtil.createTestSessionMaterial(session, testTeacher1);
        material2.setMaterialType(SessionMaterial.MaterialType.AUDIO);
        material2.setIsSharedWithStudents(false);
        sessionMaterialRepository.save(material2);
        
        // Step 8: Teacher completes preparation checklist
        ClassPreparationChecklist checklistItem1 = testDataUtil
            .createTestPreparationChecklistItem(session);
        checklistItem1.setChecklistItem("Review student placement results");
        checklistItem1.setItemCategory(ClassPreparationChecklist.ItemCategory.PLANNING);
        checklistItem1.setIsCompleted(true);
        checklistRepository.save(checklistItem1);
        
        ClassPreparationChecklist checklistItem2 = testDataUtil
            .createTestPreparationChecklistItem(session);
        checklistItem2.setChecklistItem("Upload teaching materials");
        checklistItem2.setItemCategory(ClassPreparationChecklist.ItemCategory.MATERIALS);
        checklistItem2.setIsCompleted(true);
        checklistRepository.save(checklistItem2);
        
        // Step 9: Mark session as ready
        session.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(session);
        
        // Verification: Check all entities were created and linked correctly
        
        // Verify teacher availability
        List<TeacherAvailability> teacherSlots = teacherAvailabilityRepository
            .findByTeacherAndTerm(testTeacher1, testTerm);
        assertThat(teacherSlots).hasSizeGreaterThanOrEqualTo(1);
        assertThat(teacherSlots).anyMatch(slot -> 
            slot.getDayOfWeek().equals(TeacherAvailability.DayOfWeek.MONDAY) && 
            slot.getSessionTime().equals(TeacherAvailability.SessionTime.PAGI));
        
        // Verify level assignments
        List<TeacherLevelAssignment> assignments = teacherLevelAssignmentRepository
            .findByTeacherAndTerm(testTeacher1, testTerm);
        assertThat(assignments).hasSizeGreaterThanOrEqualTo(1);
        assertThat(assignments).anyMatch(assignment ->
            assignment.getLevel().equals(testLevelBasic));
        
        // Verify student assessments
        List<StudentAssessment> assessments = studentAssessmentRepository
            .findByTermAndStudentCategory(testTerm.getId(), StudentAssessment.StudentCategory.NEW);
        assertThat(assessments).hasSizeGreaterThanOrEqualTo(2);
        
        // Verify class and enrollments
        List<ClassGroup> termClasses = classGroupRepository.findByTerm(testTerm);
        assertThat(termClasses).contains(testClassGroup);
        
        List<Enrollment> classEnrollments = enrollmentRepository.findByClassGroup(testClassGroup);
        assertThat(classEnrollments).hasSize(2);
        assertThat(classEnrollments).extracting("student")
            .contains(testStudent1, testStudent2);
        
        // Verify session preparation
        List<ClassSession> classSessions = classSessionRepository.findByClassGroup(testClassGroup);
        assertThat(classSessions).hasSize(1);
        assertThat(classSessions.get(0).getPreparationStatus())
            .isEqualTo(ClassSession.PreparationStatus.READY);
        
        // Verify materials
        List<SessionMaterial> materials = sessionMaterialRepository.findBySession(session);
        assertThat(materials).hasSize(2);
        assertThat(materials).anyMatch(material -> 
            material.getMaterialType() == SessionMaterial.MaterialType.TEXT && 
            material.getIsSharedWithStudents());
        assertThat(materials).anyMatch(material -> 
            material.getMaterialType() == SessionMaterial.MaterialType.AUDIO && 
            !material.getIsSharedWithStudents());
        
        // Verify checklist completion
        List<ClassPreparationChecklist> checklistItems = checklistRepository.findBySession(session);
        assertThat(checklistItems).hasSize(2);
        assertThat(checklistItems).allMatch(ClassPreparationChecklist::getIsCompleted);
    }
    
    @Test
    void studentLevelProgression_ShouldHandleAssessmentAndReassignment() {
        // Scenario: Student progresses from basic to intermediate level
        
        // Step 1: Student initially assessed at basic level
        StudentAssessment initialAssessment = testDataUtil
            .createTestStudentAssessment(testStudent1, testTerm, testLevelBasic);
        initialAssessment.setStudentCategory(StudentAssessment.StudentCategory.EXISTING);
        initialAssessment.setAssessmentType(StudentAssessment.AssessmentType.MIDTERM);
        initialAssessment.setAssessmentGrade("B");
        initialAssessment.setAssessmentScore(BigDecimal.valueOf(85.0));
        studentAssessmentRepository.save(initialAssessment);
        
        // Step 2: Create basic level class and enroll student
        ClassGroup basicClassGroup = testDataUtil.createTestClassGroup(testLevelBasic, testTeacher1, testTerm);
        basicClassGroup.setName("Basic Level Class for Progression Test");
        classGroupRepository.save(basicClassGroup);
        
        Enrollment basicEnrollment = testDataUtil.createTestEnrollment(testStudent1, basicClassGroup);
        basicEnrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(basicEnrollment);
        
        // Step 3: Student performs well, gets new assessment for intermediate level
        StudentAssessment progressionAssessment = testDataUtil
            .createTestStudentAssessment(testStudent1, testTerm, testLevelIntermediate);
        progressionAssessment.setStudentCategory(StudentAssessment.StudentCategory.EXISTING);
        progressionAssessment.setAssessmentType(StudentAssessment.AssessmentType.MIDTERM);
        progressionAssessment.setAssessmentGrade("A");
        progressionAssessment.setAssessmentScore(BigDecimal.valueOf(92.0));
        progressionAssessment.setPreviousClassGroup(basicClassGroup);
        studentAssessmentRepository.save(progressionAssessment);
        
        // Step 4: Create intermediate class and enroll student
        ClassGroup intermediateClassGroup = testDataUtil.createTestClassGroup(testLevelIntermediate, testTeacher2, testTerm);
        intermediateClassGroup.setName("Intermediate Level Class for Progression Test");
        classGroupRepository.save(intermediateClassGroup);
        
        // Step 5: Complete old enrollment and create new one
        basicEnrollment.setStatus(Enrollment.EnrollmentStatus.COMPLETED);
        enrollmentRepository.save(basicEnrollment);
        
        Enrollment intermediateEnrollment = testDataUtil.createTestEnrollment(testStudent1, intermediateClassGroup);
        intermediateEnrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(intermediateEnrollment);
        
        // Verification: Check progression is correctly recorded
        
        // Verify student has assessments for both levels
        List<StudentAssessment> studentAssessments = studentAssessmentRepository
            .findByStudent(testStudent1);
        assertThat(studentAssessments).hasSize(2);
        
        StudentAssessment basicAssessment = studentAssessments.stream()
            .filter(assessment -> assessment.getDeterminedLevel().equals(testLevelBasic))
            .findFirst().orElseThrow();
        assertThat(basicAssessment.getAssessmentGrade()).isEqualTo("B");
        
        StudentAssessment intermediateAssessment = studentAssessments.stream()
            .filter(assessment -> assessment.getDeterminedLevel().equals(testLevelIntermediate))
            .findFirst().orElseThrow();
        assertThat(intermediateAssessment.getAssessmentGrade()).isEqualTo("A");
        assertThat(intermediateAssessment.getPreviousClassGroup()).isEqualTo(basicClassGroup);
        
        // Verify enrollment history
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudent(testStudent1);
        assertThat(studentEnrollments).hasSize(2);
        
        Enrollment completedEnrollment = studentEnrollments.stream()
            .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.COMPLETED)
            .findFirst().orElseThrow();
        assertThat(completedEnrollment.getClassGroup()).isEqualTo(basicClassGroup);
        
        Enrollment activeEnrollment = studentEnrollments.stream()
            .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
            .findFirst().orElseThrow();
        assertThat(activeEnrollment.getClassGroup()).isEqualTo(intermediateClassGroup);
    }
    
    @Test
    void classGenerationProposal_ShouldTrackOptimizationProcess() {
        // Scenario: Academic staff generates multiple class proposals and selects optimal one
        
        // Step 1: Create multiple class proposals with different parameters
        GeneratedClassProposal proposal1 = testDataUtil.createTestClassProposal(testTerm, testTeacher1);
        proposal1.setGenerationRun(1);
        proposal1.setOptimizationScore(BigDecimal.valueOf(75.5));
        proposal1.setConflictCount(3);
        proposal1.setIsApproved(false);
        proposalRepository.save(proposal1);
        
        GeneratedClassProposal proposal2 = testDataUtil.createTestClassProposal(testTerm, testTeacher1);
        proposal2.setGenerationRun(2);
        proposal2.setOptimizationScore(BigDecimal.valueOf(89.2));
        proposal2.setConflictCount(1);
        proposal2.setIsApproved(false);
        proposalRepository.save(proposal2);
        
        GeneratedClassProposal proposal3 = testDataUtil.createTestClassProposal(testTerm, testTeacher1);
        proposal3.setGenerationRun(3);
        proposal3.setOptimizationScore(BigDecimal.valueOf(92.8));
        proposal3.setConflictCount(0);
        proposal3.setIsApproved(true);
        proposal3.setApprovedBy(testTeacher1);
        proposalRepository.save(proposal3);
        
        // Step 2: Create classes based on approved proposal
        ClassGroup approvedClassGroup1 = testDataUtil.createTestClassGroup(testLevelBasic, testTeacher1, testTerm);
        approvedClassGroup1.setName("Approved Class 1 from Proposal 3");
        classGroupRepository.save(approvedClassGroup1);
        
        ClassGroup approvedClassGroup2 = testDataUtil.createTestClassGroup(testLevelIntermediate, testTeacher2, testTerm);
        approvedClassGroup2.setName("Approved Class 2 from Proposal 3");
        classGroupRepository.save(approvedClassGroup2);
        
        // Verification: Check proposal optimization tracking
        
        // Verify all proposals are saved
        List<GeneratedClassProposal> termProposals = proposalRepository.findByTerm(testTerm);
        assertThat(termProposals).hasSize(3);
        
        // Verify approved proposal has highest score
        var approvedProposal = proposalRepository.findApprovedProposalByTerm(testTerm.getId());
        assertThat(approvedProposal).isPresent();
        assertThat(approvedProposal.get()).isEqualTo(proposal3);
        assertThat(approvedProposal.get().getOptimizationScore())
            .isEqualTo(BigDecimal.valueOf(92.8));
        
        // Verify proposals ordered by optimization score
        List<GeneratedClassProposal> orderedProposals = proposalRepository
            .findByTermOrderByOptimizationScoreDesc(testTerm.getId());
        assertThat(orderedProposals.get(0)).isEqualTo(proposal3); // Highest score
        assertThat(orderedProposals.get(1)).isEqualTo(proposal2); // Second highest
        assertThat(orderedProposals.get(2)).isEqualTo(proposal1); // Lowest score
        
        // Verify classes were created from approved proposal
        List<ClassGroup> termClasses = classGroupRepository.findByTerm(testTerm);
        assertThat(termClasses).contains(approvedClassGroup1, approvedClassGroup2);
    }
}