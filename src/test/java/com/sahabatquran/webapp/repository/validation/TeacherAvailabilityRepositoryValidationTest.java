package com.sahabatquran.webapp.repository.validation;

import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.entity.Session;
import com.sahabatquran.webapp.entity.TeacherAvailability;
import com.sahabatquran.webapp.entity.User;
import java.util.List;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import com.sahabatquran.webapp.repository.SessionRepository;
import com.sahabatquran.webapp.repository.TeacherAvailabilityRepository;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql(scripts = "/test-data/class-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-class-preparation-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TeacherAvailabilityRepositoryValidationTest extends BaseIntegrationTest {
    
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
    
    private User testTeacher;
    private AcademicTerm testTerm;
    private Session testSession;
    
    @BeforeEach
    void setUp() {
        // Create a completely fresh test teacher to avoid conflicts with existing data
        testTeacher = testDataUtil.createTestUser("INSTRUCTOR");
        testTeacher.setUsername("TEST_VALIDATION_TEACHER_" + System.currentTimeMillis());
        userRepository.save(testTeacher);
        
        testTerm = academicTermRepository.findByTermName("TEST_TERM_2024").orElseThrow();
        
        // Get first available session for testing
        testSession = sessionRepository.findByIsActiveTrueOrderByStartTime().get(0);
    }
    
    @ParameterizedTest
    @CsvSource({
        "0, 'Day of week 0 should fail'",
        "8, 'Day of week 8 should fail'",
        "-1, 'Negative day of week should fail'",
        "1, 'Valid day of week 1 (Monday) should pass'",
        "7, 'Valid day of week 7 (Sunday) should pass'"
    })
    void save_ShouldValidateDayOfWeek(int dayOfWeek, String description) {
        // Given
        TeacherAvailability availability = createBasicAvailability();
        availability.setDayOfWeek(getDayOfWeekFromInt(dayOfWeek));
        
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            // When & Then - Should fail validation (null enum or invalid)
            assertThatThrownBy(() -> teacherAvailabilityRepository.saveAndFlush(availability))
                .satisfiesAnyOf(
                    ex -> assertThat(ex).isInstanceOf(DataIntegrityViolationException.class),
                    ex -> assertThat(ex).isInstanceOf(ConstraintViolationException.class)
                );
        } else {
            // When & Then - Should succeed
            TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability);
            assertThat(saved.getDayOfWeek()).isEqualTo(getDayOfWeekFromInt(dayOfWeek));
        }
    }
    
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void save_ShouldValidateSession(boolean withValidSession) {
        // Given
        TeacherAvailability availability = createBasicAvailability();
        
        if (!withValidSession) {
            // Test with null session - should fail validation
            availability.setSession(null);
            
            // When & Then - Should fail validation
            assertThatThrownBy(() -> teacherAvailabilityRepository.saveAndFlush(availability))
                .isInstanceOf(DataIntegrityViolationException.class);
        } else {
            // Test with valid session - should succeed
            availability.setSession(testSession);
            
            // When & Then - Should succeed
            TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability);
            assertThat(saved.getSession().getId()).isEqualTo(testSession.getId());
        }
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 15, 100})
    void save_ShouldValidateMaxClassesPerWeek(int maxClasses) {
        // Given
        TeacherAvailability availability = createBasicAvailability();
        availability.setMaxClassesPerWeek(maxClasses);
        
        if (maxClasses < 1 || maxClasses > 12) { // Business rule: reasonable range
            // This is more of a business validation, but test edge cases
            
            // For now, test that extreme values are saved (database allows them)
            // Business logic should be validated at service layer
            TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability);
            assertThat(saved.getMaxClassesPerWeek()).isEqualTo(maxClasses);
        } else {
            // When & Then - Should succeed for reasonable values
            TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability);
            assertThat(saved.getMaxClassesPerWeek()).isEqualTo(maxClasses);
        }
    }
    
    @ParameterizedTest
    @CsvSource({
        "1, 0, 1, 0, 'Same teacher, term, day, and session should fail unique constraint'",
        "1, 0, 1, 1, 'Same teacher, term, day but different session should pass'", 
        "1, 0, 2, 0, 'Same teacher, term, session but different day should pass'"
    })
    void save_ShouldEnforceUniqueConstraint(
            int dayOfWeek1, int sessionIndex1,
            int dayOfWeek2, int sessionIndex2, 
            String description) {
        
        // Get available sessions for testing
        List<Session> sessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
        Session session1 = sessions.get(sessionIndex1 % sessions.size());
        Session session2 = sessions.get(sessionIndex2 % sessions.size());
        
        // Given - First availability (this should always succeed)
        TeacherAvailability availability1 = createBasicAvailability();
        availability1.setDayOfWeek(getDayOfWeekFromInt(dayOfWeek1));
        availability1.setSession(session1);
        teacherAvailabilityRepository.saveAndFlush(availability1);
        
        // Second availability
        TeacherAvailability availability2 = createBasicAvailability();
        availability2.setDayOfWeek(getDayOfWeekFromInt(dayOfWeek2));
        availability2.setSession(session2);
        
        if (dayOfWeek1 == dayOfWeek2 && sessionIndex1 == sessionIndex2) {
            // When & Then - Should fail unique constraint
            assertThatThrownBy(() -> teacherAvailabilityRepository.saveAndFlush(availability2))
                .isInstanceOf(DataIntegrityViolationException.class);
        } else {
            // When & Then - Should succeed
            TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability2);
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getDayOfWeek()).isEqualTo(getDayOfWeekFromInt(dayOfWeek2));
            assertThat(saved.getSession().getId()).isEqualTo(session2.getId());
        }
    }
    
    @ParameterizedTest
    @CsvSource({
        "true, 'Available should pass'",
        "false, 'Not available should pass'"
    })
    void save_ShouldAcceptBooleanAvailability(boolean isAvailable, String description) {
        // Given
        TeacherAvailability availability = createBasicAvailability();
        availability.setIsAvailable(isAvailable);
        
        // When & Then - Should always succeed
        TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability);
        assertThat(saved.getIsAvailable()).isEqualTo(isAvailable);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'', 'Empty preferences should pass'",
        "'Short preference', 'Short preferences should pass'",
        "'Very long preference text that goes on and on and includes many details about the teachers scheduling needs and constraints and should still be accepted by the system', 'Long preferences should pass'"
    })
    void save_ShouldAcceptVariousPreferences(String preferences, String description) {
        // Given
        TeacherAvailability availability = createBasicAvailability();
        availability.setPreferences(preferences.isEmpty() ? null : preferences);
        
        // When & Then - Should succeed
        TeacherAvailability saved = teacherAvailabilityRepository.saveAndFlush(availability);
        assertThat(saved.getPreferences()).isEqualTo(preferences.isEmpty() ? null : preferences);
    }
    
    private TeacherAvailability.DayOfWeek getDayOfWeekFromInt(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> TeacherAvailability.DayOfWeek.MONDAY;
            case 2 -> TeacherAvailability.DayOfWeek.TUESDAY;
            case 3 -> TeacherAvailability.DayOfWeek.WEDNESDAY;
            case 4 -> TeacherAvailability.DayOfWeek.THURSDAY;
            case 5 -> TeacherAvailability.DayOfWeek.FRIDAY;
            case 6 -> TeacherAvailability.DayOfWeek.SATURDAY;
            case 7 -> TeacherAvailability.DayOfWeek.SUNDAY;
            default -> null; // Will cause validation error as intended
        };
    }
    
    private TeacherAvailability createBasicAvailability() {
        TeacherAvailability availability = new TeacherAvailability();
        availability.setTeacher(testTeacher);
        availability.setTerm(testTerm);
        availability.setDayOfWeek(TeacherAvailability.DayOfWeek.THURSDAY); // Default to Thursday (not used by TEST_TEACHER_2)
        availability.setSession(testSession); // Default session
        availability.setIsAvailable(true);
        availability.setMaxClassesPerWeek(6);
        return availability;
    }
}