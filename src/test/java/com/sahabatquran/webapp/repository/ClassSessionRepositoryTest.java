package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/class-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-class-preparation-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ClassSessionRepositoryTest extends BaseIntegrationTest {
    
    @Autowired
    private ClassSessionRepository classSessionRepository;
    
    @Autowired
    private ClassGroupRepository classGroupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LevelRepository levelRepository;
    
    @Autowired
    private AcademicTermRepository academicTermRepository;
    
    @Autowired
    private SessionMaterialRepository sessionMaterialRepository;
    
    @Autowired
    private ClassPreparationChecklistRepository checklistRepository;
    
    @Autowired
    private TestDataUtil testDataUtil;
    
    private AcademicTerm testTerm;
    private User testTeacher;
    private Level testLevel;
    private ClassGroup testClassGroup;
    
    @BeforeEach
    void setUp() {
        testTerm = academicTermRepository.findByTermName("TEST_TERM_2024").orElseThrow();
        testTeacher = userRepository.findByUsername("TEST_TEACHER_1").orElseThrow();
        testLevel = levelRepository.findByName("TEST_LEVEL_BASIC").orElseThrow();
        
        // Create a test class group
        testClassGroup = testDataUtil.createTestClassGroup(testLevel, testTeacher, testTerm);
        classGroupRepository.save(testClassGroup);
    }
    
    @Test
    void findByClassGroupAndSessionDate_ShouldReturnUniqueSession() {
        // Given
        LocalDate sessionDate = LocalDate.now().plusDays(7);
        ClassSession session = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        session.setSessionDate(sessionDate);
        classSessionRepository.save(session);
        
        // When
        var foundSession = classSessionRepository.findByClassGroupAndSessionDate(testClassGroup, sessionDate);
        
        // Then
        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().getId()).isEqualTo(session.getId());
        assertThat(foundSession.get().getSessionNumber()).isEqualTo(session.getSessionNumber());
        assertThat(foundSession.get().getPreparationStatus()).isEqualTo(session.getPreparationStatus());
        assertThat(foundSession.get().getSessionDate()).isEqualTo(sessionDate);
        assertThat(foundSession.get().getClassGroup().getId()).isEqualTo(testClassGroup.getId());
    }
    
    @Test
    void findBySessionDateAndInstructor_ShouldReturnMultipleClassesForSameInstructor() {
        // Given - Same instructor teaching multiple classes on same date
        ClassGroup secondClassGroup = testDataUtil.createTestClassGroup(testLevel, testTeacher, testTerm);
        classGroupRepository.save(secondClassGroup);
        
        LocalDate sessionDate = LocalDate.now().plusDays(10);
        
        ClassSession session1 = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        session1.setSessionDate(sessionDate);
        classSessionRepository.save(session1);
        
        ClassSession session2 = testDataUtil.createTestClassSession(secondClassGroup, testTeacher);
        session2.setSessionDate(sessionDate);
        classSessionRepository.save(session2);
        
        // When
        List<ClassSession> instructorSessions = classSessionRepository
            .findBySessionDateAndInstructor(sessionDate, testTeacher.getId());
        
        // Then
        assertThat(instructorSessions).hasSize(2);
        assertThat(instructorSessions.stream().map(ClassSession::getId))
            .containsExactlyInAnyOrder(session1.getId(), session2.getId());
        assertThat(instructorSessions).allMatch(session -> 
            session.getSessionDate().equals(sessionDate) && 
            session.getInstructor().getId().equals(testTeacher.getId()));
    }
    
    @Test
    void findReadySessionsByTerm_ShouldFilterByPreparationStatus() {
        // Given - Mix of sessions with different preparation statuses
        ClassSession draftSession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        draftSession.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(draftSession);
        
        ClassSession inProgressSession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        inProgressSession.setSessionDate(LocalDate.now().plusDays(2));
        inProgressSession.setPreparationStatus(ClassSession.PreparationStatus.IN_PROGRESS);
        classSessionRepository.save(inProgressSession);
        
        ClassSession readySession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        readySession.setSessionDate(LocalDate.now().plusDays(3));
        readySession.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(readySession);
        
        // When
        List<ClassSession> readySessions = classSessionRepository.findReadySessionsByTerm(testTerm.getId());
        
        // Then
        assertThat(readySessions).hasSize(1);
        assertThat(readySessions.get(0).getId()).isEqualTo(readySession.getId());
        assertThat(readySessions.get(0).getPreparationStatus())
            .isEqualTo(ClassSession.PreparationStatus.READY);
    }
    
    @Test
    void findIncompletePreparationByTerm_ShouldReturnDraftAndInProgress() {
        // Given
        ClassSession draftSession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        draftSession.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(draftSession);
        
        ClassSession inProgressSession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        inProgressSession.setSessionDate(LocalDate.now().plusDays(2));
        inProgressSession.setPreparationStatus(ClassSession.PreparationStatus.IN_PROGRESS);
        classSessionRepository.save(inProgressSession);
        
        ClassSession readySession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        readySession.setSessionDate(LocalDate.now().plusDays(3));
        readySession.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(readySession);
        
        ClassSession completedSession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        completedSession.setSessionDate(LocalDate.now().plusDays(4));
        completedSession.setPreparationStatus(ClassSession.PreparationStatus.COMPLETED);
        classSessionRepository.save(completedSession);
        
        // When
        List<ClassSession> incompleteSessions = classSessionRepository
            .findIncompletePreparationByTerm(testTerm.getId());
        
        // Then
        assertThat(incompleteSessions).hasSize(2);
        assertThat(incompleteSessions.stream().map(ClassSession::getId))
            .containsExactlyInAnyOrder(draftSession.getId(), inProgressSession.getId());
        assertThat(incompleteSessions).allMatch(session -> 
            session.getPreparationStatus() == ClassSession.PreparationStatus.DRAFT ||
            session.getPreparationStatus() == ClassSession.PreparationStatus.IN_PROGRESS);
    }
    
    @Test
    void findByIdWithMaterialsAndChecklist_ShouldFetchRelatedEntities() {
        // Given - Session with materials and checklist items
        ClassSession session = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        classSessionRepository.save(session);
        
        // Add materials
        SessionMaterial material1 = testDataUtil.createTestSessionMaterial(session, testTeacher);
        sessionMaterialRepository.save(material1);
        
        SessionMaterial material2 = testDataUtil.createTestSessionMaterial(session, testTeacher);
        sessionMaterialRepository.save(material2);
        
        // Add checklist items
        ClassPreparationChecklist item1 = testDataUtil.createTestPreparationChecklistItem(session);
        checklistRepository.save(item1);
        
        ClassPreparationChecklist item2 = testDataUtil.createTestPreparationChecklistItem(session);
        checklistRepository.save(item2);
        
        // When
        var fetchedSession = classSessionRepository.findByIdWithMaterialsAndChecklist(session.getId());
        
        // Then
        assertThat(fetchedSession).isPresent();
        ClassSession retrieved = fetchedSession.get();
        
        // Verify session materials are fetched
        assertThat(retrieved.getSessionMaterials()).hasSize(2);
        assertThat(retrieved.getSessionMaterials()).contains(material1, material2);
        
        // Verify checklist items are fetched  
        assertThat(retrieved.getPreparationChecklistItems()).hasSize(2);
        assertThat(retrieved.getPreparationChecklistItems()).contains(item1, item2);
    }
    
    @Test
    void findPreparationStatusCountsByTerm_ShouldGroupByStatus() {
        // Given - Multiple sessions with different statuses
        ClassSession draftSession1 = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        draftSession1.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(draftSession1);
        
        ClassSession draftSession2 = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        draftSession2.setSessionDate(LocalDate.now().plusDays(1));
        draftSession2.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(draftSession2);
        
        ClassSession readySession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        readySession.setSessionDate(LocalDate.now().plusDays(2));
        readySession.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(readySession);
        
        // When
        List<Object[]> statusCounts = classSessionRepository.findPreparationStatusCountsByTerm(testTerm.getId());
        
        // Then
        assertThat(statusCounts).isNotEmpty();
        
        // Find DRAFT count
        Object[] draftCount = statusCounts.stream()
            .filter(row -> row[0].equals(ClassSession.PreparationStatus.DRAFT))
            .findFirst().orElse(null);
        assertThat(draftCount).isNotNull();
        assertThat(draftCount[1]).isEqualTo(2L);
        
        // Find READY count  
        Object[] readyCount = statusCounts.stream()
            .filter(row -> row[0].equals(ClassSession.PreparationStatus.READY))
            .findFirst().orElse(null);
        assertThat(readyCount).isNotNull();
        assertThat(readyCount[1]).isEqualTo(1L);
    }
    
    @Test
    void findInstructorPreparationSummary_ShouldCalculateReadyRatio() {
        // Given - Multiple sessions for instructor with different statuses
        User instructor2 = userRepository.findByUsername("TEST_TEACHER_2").orElseThrow();
        
        // Teacher 1 sessions
        ClassSession teacher1Draft = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        teacher1Draft.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(teacher1Draft);
        
        ClassSession teacher1Ready = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        teacher1Ready.setSessionDate(LocalDate.now().plusDays(1));
        teacher1Ready.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(teacher1Ready);
        
        // Teacher 2 sessions (create a class for teacher 2 first)
        ClassGroup teacher2ClassGroup = testDataUtil.createTestClassGroup(testLevel, instructor2, testTerm);
        classGroupRepository.save(teacher2ClassGroup);
        
        ClassSession teacher2Ready1 = testDataUtil.createTestClassSession(teacher2ClassGroup, instructor2);
        teacher2Ready1.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(teacher2Ready1);
        
        ClassSession teacher2Ready2 = testDataUtil.createTestClassSession(teacher2ClassGroup, instructor2);
        teacher2Ready2.setSessionDate(LocalDate.now().plusDays(2));
        teacher2Ready2.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(teacher2Ready2);
        
        // When
        List<Object[]> instructorSummary = classSessionRepository
            .findInstructorPreparationSummary(testTerm.getId());
        
        // Then
        assertThat(instructorSummary).hasSize(2);
        
        // Verify structure: [User instructor, Long sessionCount, Long readyCount]
        Object[] teacher1Summary = instructorSummary.stream()
            .filter(row -> ((User) row[0]).equals(testTeacher))
            .findFirst().orElse(null);
        assertThat(teacher1Summary).isNotNull();
        assertThat(teacher1Summary[1]).isEqualTo(2L); // Total sessions
        assertThat(teacher1Summary[2]).isEqualTo(1L); // Ready sessions
        
        Object[] teacher2Summary = instructorSummary.stream()
            .filter(row -> ((User) row[0]).equals(instructor2))
            .findFirst().orElse(null);
        assertThat(teacher2Summary).isNotNull();
        assertThat(teacher2Summary[1]).isEqualTo(2L); // Total sessions
        assertThat(teacher2Summary[2]).isEqualTo(2L); // Ready sessions (100% ready)
    }
    
    @Test
    void findUpcomingUnpreparedSessions_ShouldReturnFutureSessions() {
        // Given - Mix of past and future sessions with different preparation states
        LocalDate today = LocalDate.now();
        
        // Past session (should not be returned)
        ClassSession pastSession = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        pastSession.setSessionDate(today.minusDays(1));
        pastSession.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(pastSession);
        
        // Future unprepared session (should be returned)
        ClassSession futureUnprepared = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        futureUnprepared.setSessionDate(today.plusDays(3));
        futureUnprepared.setPreparationStatus(ClassSession.PreparationStatus.DRAFT);
        classSessionRepository.save(futureUnprepared);
        
        // Future ready session (should not be returned)
        ClassSession futureReady = testDataUtil.createTestClassSession(testClassGroup, testTeacher);
        futureReady.setSessionDate(today.plusDays(5));
        futureReady.setPreparationStatus(ClassSession.PreparationStatus.READY);
        classSessionRepository.save(futureReady);
        
        // When
        List<ClassSession> upcomingUnprepared = classSessionRepository
            .findUpcomingUnpreparedSessions(today);
        
        // Then
        assertThat(upcomingUnprepared).hasSize(1);
        assertThat(upcomingUnprepared.get(0).getId()).isEqualTo(futureUnprepared.getId());
        assertThat(upcomingUnprepared.get(0).getSessionDate()).isAfter(today.minusDays(1));
        assertThat(upcomingUnprepared.get(0).getPreparationStatus())
            .isNotEqualTo(ClassSession.PreparationStatus.READY);
    }
}