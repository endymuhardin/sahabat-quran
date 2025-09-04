package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.Session;
import com.sahabatquran.webapp.entity.TeacherAvailability;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/class-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-class-preparation-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TeacherAvailabilityRepositoryTest extends BaseIntegrationTest {
    
    @Autowired
    private TeacherAvailabilityRepository teacherAvailabilityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AcademicTermRepository academicTermRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private TestDataUtil testDataUtil;
    
    private User testTeacher1;
    private User testTeacher2;
    private AcademicTerm testTerm;
    private Session testSession1;
    private Session testSession2;
    
    @BeforeEach
    void setUp() {
        testTeacher1 = userRepository.findByUsername("TEST_TEACHER_1").orElseThrow();
        testTeacher2 = userRepository.findByUsername("TEST_TEACHER_2").orElseThrow();
        testTerm = academicTermRepository.findByTermName("TEST_TERM_2024").orElseThrow();
        
        // Get test sessions (these should be available from V002 seed data or test setup)
        List<Session> allSessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
        testSession1 = allSessions.get(0); // First available session
        testSession2 = allSessions.size() > 1 ? allSessions.get(1) : allSessions.get(0); // Second or first if only one
    }
    
    @Test
    void findByTeacherAndTerm_ShouldReturnTeacherAvailability_WhenDataExists() {
        // When
        List<TeacherAvailability> availabilities = teacherAvailabilityRepository
            .findByTeacherAndTerm(testTeacher1, testTerm);
        
        // Then
        assertThat(availabilities).isNotEmpty();
        assertThat(availabilities).allMatch(av -> av.getTeacher().getId().equals(testTeacher1.getId()));
        assertThat(availabilities).allMatch(av -> av.getTerm().getId().equals(testTerm.getId()));
    }
    
    @Test
    void findByTeacherAndTermAndDayOfWeekAndSession_ShouldReturnSpecificSlot() {
        // When - Looking for Monday session slot for teacher1 (should exist from setup)
        Optional<TeacherAvailability> availability = teacherAvailabilityRepository
            .findByTeacherAndTermAndDayOfWeekAndSession(
                testTeacher1, testTerm, TeacherAvailability.DayOfWeek.MONDAY, testSession1);
        
        // Then
        assertThat(availability).isPresent();
        assertThat(availability.get().getTeacher().getId()).isEqualTo(testTeacher1.getId());
        assertThat(availability.get().getTerm().getId()).isEqualTo(testTerm.getId());
        assertThat(availability.get().getDayOfWeek()).isEqualTo(TeacherAvailability.DayOfWeek.MONDAY);
        assertThat(availability.get().getSession().getId()).isEqualTo(testSession1.getId());
        assertThat(availability.get().getIsAvailable()).isTrue();
    }
    
    @Test
    void findAvailableSlotsByTerm_ShouldReturnOnlyAvailableSlots() {
        // Given - create an unavailable slot
        TeacherAvailability unavailableSlot = testDataUtil.createTestTeacherAvailability(testTeacher2, testTerm, testSession2);
        unavailableSlot.setIsAvailable(false);
        unavailableSlot.setDayOfWeek(TeacherAvailability.DayOfWeek.SUNDAY); // Sunday to avoid conflicts
        teacherAvailabilityRepository.save(unavailableSlot);
        
        // When
        List<TeacherAvailability> availableSlots = teacherAvailabilityRepository
            .findAvailableSlotsByTerm(testTerm.getId());
        
        // Then
        assertThat(availableSlots).isNotEmpty();
        assertThat(availableSlots).allMatch(TeacherAvailability::getIsAvailable);
        assertThat(availableSlots).noneMatch(av -> av.equals(unavailableSlot));
    }
    
    @Test
    void findAvailableTeachersByDayAndSession_ShouldReturnMatchingSlots() {
        // When - Find teachers available on Monday for testSession1
        List<TeacherAvailability> mondayTeachers = teacherAvailabilityRepository
            .findAvailableTeachersByDayAndSession(TeacherAvailability.DayOfWeek.MONDAY, testSession1);
        
        // Then
        assertThat(mondayTeachers).isNotEmpty();
        assertThat(mondayTeachers).allMatch(av -> av.getDayOfWeek().equals(TeacherAvailability.DayOfWeek.MONDAY));
        assertThat(mondayTeachers).allMatch(av -> 
            av.getSession().getId().equals(testSession1.getId()));
        assertThat(mondayTeachers).allMatch(TeacherAvailability::getIsAvailable);
    }
    
    @Test
    void countAvailableSlotsByTerm_ShouldReturnCorrectCount() {
        // When
        Long count = teacherAvailabilityRepository.countAvailableSlotsByTerm(testTerm.getId());
        
        // Then
        assertThat(count).isPositive();
        
        // Verify by manual count
        List<TeacherAvailability> availableSlots = teacherAvailabilityRepository
            .findAvailableSlotsByTerm(testTerm.getId());
        assertThat(count).isEqualTo(availableSlots.size());
    }
    
    @Test
    void findTeacherAvailabilitySummary_ShouldReturnTeacherWithSlotCounts() {
        // When
        List<Object[]> summary = teacherAvailabilityRepository
            .findTeacherAvailabilitySummary(testTerm.getId());
        
        // Then
        assertThat(summary).isNotEmpty();
        
        // Verify structure: [User teacher, Long availableSlots]
        Object[] firstEntry = summary.get(0);
        assertThat(firstEntry).hasSize(2);
        assertThat(firstEntry[0]).isInstanceOf(User.class);
        assertThat(firstEntry[1]).isInstanceOf(Long.class);
        
        User teacher = (User) firstEntry[0];
        Long slotCount = (Long) firstEntry[1];
        
        assertThat(teacher.getUsername()).startsWith("TEST_TEACHER_");
        assertThat(slotCount).isPositive();
        
        // Verify ordering (DESC by slot count)
        if (summary.size() > 1) {
            Long firstCount = (Long) summary.get(0)[1];
            Long secondCount = (Long) summary.get(1)[1];
            assertThat(firstCount).isGreaterThanOrEqualTo(secondCount);
        }
    }
    
    @Test
    void findTeachersWhoSubmittedAvailability_ShouldReturnUniqueTeachers() {
        // When
        List<User> teachersWithSubmissions = teacherAvailabilityRepository
            .findTeachersWhoSubmittedAvailability(testTerm.getId());
        
        // Then
        assertThat(teachersWithSubmissions).isNotEmpty();
        assertThat(teachersWithSubmissions).doesNotHaveDuplicates();
        assertThat(teachersWithSubmissions).contains(testTeacher1, testTeacher2);
    }
    
    @Test
    void save_ShouldHandleComplexAvailabilityPattern() {
        // Given - Create a teacher with complex availability pattern
        User newTeacher = testDataUtil.createTestUser("TEACHER");
        userRepository.save(newTeacher);
        
        // Create availability for all weekdays, morning and afternoon
        TeacherAvailability.DayOfWeek[] weekdays = {TeacherAvailability.DayOfWeek.MONDAY, TeacherAvailability.DayOfWeek.TUESDAY, 
                                                    TeacherAvailability.DayOfWeek.WEDNESDAY, TeacherAvailability.DayOfWeek.THURSDAY, TeacherAvailability.DayOfWeek.FRIDAY};
        for (int i = 0; i < weekdays.length; i++) {
            TeacherAvailability morningSlot = testDataUtil.createTestTeacherAvailability(newTeacher, testTerm, testSession1);
            morningSlot.setDayOfWeek(weekdays[i]);
            morningSlot.setIsAvailable(true);
            teacherAvailabilityRepository.save(morningSlot);
            
            TeacherAvailability afternoonSlot = testDataUtil.createTestTeacherAvailability(newTeacher, testTerm, testSession2);
            afternoonSlot.setDayOfWeek(weekdays[i]);
            afternoonSlot.setIsAvailable(i % 2 == 0); // Every other day for afternoons
            teacherAvailabilityRepository.save(afternoonSlot);
        }
        
        // When
        List<TeacherAvailability> teacherSlots = teacherAvailabilityRepository
            .findByTeacherAndTerm(newTeacher, testTerm);
        
        // Then
        assertThat(teacherSlots).hasSize(10); // 5 days × 2 sessions
        
        long session1Slots = teacherSlots.stream()
            .filter(slot -> slot.getSession().getId().equals(testSession1.getId()))
            .count();
        assertThat(session1Slots).isEqualTo(5);
        
        long availableSession2Slots = teacherSlots.stream()
            .filter(slot -> slot.getSession().getId().equals(testSession2.getId()))
            .filter(TeacherAvailability::getIsAvailable)
            .count();
        assertThat(availableSession2Slots).isEqualTo(3); // Even indices: Monday(0), Wednesday(2), Friday(4)
    }
    
    @Test
    void findAvailableSlotsByTeacherAndTerm_ShouldFilterAvailableOnly() {
        // Given - create a mix of available and unavailable slots for a new teacher
        User newTeacher = testDataUtil.createTestUser("TEACHER");
        userRepository.save(newTeacher);
        
        TeacherAvailability availableSlot = testDataUtil.createTestTeacherAvailability(newTeacher, testTerm, testSession1);
        availableSlot.setIsAvailable(true);
        availableSlot.setDayOfWeek(TeacherAvailability.DayOfWeek.SATURDAY);
        teacherAvailabilityRepository.save(availableSlot);
        
        TeacherAvailability unavailableSlot = testDataUtil.createTestTeacherAvailability(newTeacher, testTerm, testSession2);
        unavailableSlot.setIsAvailable(false);
        unavailableSlot.setDayOfWeek(TeacherAvailability.DayOfWeek.SATURDAY);
        teacherAvailabilityRepository.save(unavailableSlot);
        
        // When
        List<TeacherAvailability> availableSlots = teacherAvailabilityRepository
            .findAvailableSlotsByTeacherAndTerm(newTeacher.getId(), testTerm.getId());
        
        // Then
        assertThat(availableSlots).hasSize(1);
        TeacherAvailability foundSlot = availableSlots.get(0);
        assertThat(foundSlot.getId()).isEqualTo(availableSlot.getId());
        assertThat(foundSlot.getTeacher().getId()).isEqualTo(availableSlot.getTeacher().getId());
        assertThat(foundSlot.getTerm().getId()).isEqualTo(availableSlot.getTerm().getId());
        assertThat(foundSlot.getDayOfWeek()).isEqualTo(availableSlot.getDayOfWeek());
        assertThat(foundSlot.getSession().getId()).isEqualTo(availableSlot.getSession().getId());
        assertThat(foundSlot.getIsAvailable()).isEqualTo(availableSlot.getIsAvailable());
        assertThat(availableSlots).allMatch(TeacherAvailability::getIsAvailable);
    }
}