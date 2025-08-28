package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.Session;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/session-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SessionRepositoryTest extends BaseIntegrationTest {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    private final Faker faker = new Faker();
    private Session morningSession;
    private Session afternoonSession;
    private Session eveningSession;
    
    @BeforeEach
    void setUp() {
        // Use test sessions created by SQL script
        morningSession = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_1").orElse(null);
        afternoonSession = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_2").orElse(null);
        eveningSession = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_3").orElse(null);
        
        // If not found, create fallback sessions
        if (morningSession == null) {
            morningSession = createTestSession("TEST_SESSION_1_FALLBACK", "Test Session 1", 
                    LocalTime.of(8, 0), LocalTime.of(10, 0));
            morningSession = sessionRepository.save(morningSession);
        }
        if (afternoonSession == null) {
            afternoonSession = createTestSession("TEST_SESSION_2_FALLBACK", "Test Session 2", 
                    LocalTime.of(14, 0), LocalTime.of(16, 0));
            afternoonSession = sessionRepository.save(afternoonSession);
        }
    }
    
    @Test
    void findByIsActiveTrueOrderByStartTime_ShouldReturnSessionsOrderedByTime() {
        // When
        List<Session> result = sessionRepository.findByIsActiveTrueOrderByStartTime();
        
        // Then - should have at least 3 sessions (including migration data)
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        
        // Verify they are ordered chronologically
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getStartTime())
                    .isBeforeOrEqualTo(result.get(i + 1).getStartTime());
        }
        
        // Verify our test sessions are present
        assertThat(result).anyMatch(s -> s.getCode().equals("TEST_SESSION_1"));
        assertThat(result).anyMatch(s -> s.getCode().equals("TEST_SESSION_2"));
        assertThat(result).anyMatch(s -> s.getCode().equals("TEST_SESSION_3"));
    }
    
    @Test
    void findByCodeAndIsActiveTrue_ShouldReturnSessionByCode() {
        // When
        Optional<Session> result = sessionRepository.findByCodeAndIsActiveTrue("SESI_1");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("SESI_1");
        assertThat(result.get().getName()).isEqualTo("Sesi 1 (07-09)");
        assertThat(result.get().getStartTime()).isEqualTo(LocalTime.of(7, 0));
        assertThat(result.get().getEndTime()).isEqualTo(LocalTime.of(9, 0));
    }
    
    @Test
    void findByCodeAndIsActiveTrue_ShouldReturnEmptyForInactiveSession() {
        // Given - use TEST_SESSION_1 instead of SESI_1
        morningSession.setIsActive(false);
        sessionRepository.save(morningSession);
        
        // When
        Optional<Session> result = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_1");
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void findActiveByTimeRange_ShouldReturnSessionsWithinTimeRange() {
        // When - find sessions between 12:00 and 16:00
        List<Session> result = sessionRepository.findActiveByTimeRange(
                LocalTime.of(12, 0), LocalTime.of(16, 0));
        
        // Then - should include afternoon sessions (migration data + test data)
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        // Should include our test session and migration sessions
        assertThat(result).anyMatch(s -> s.getCode().equals("TEST_SESSION_3"));
        assertThat(result).anyMatch(s -> s.getCode().equals("SESI_5") || s.getCode().equals("SESI_6") || s.getCode().equals("SESI_7"));
        
        // Verify all sessions start within the range
        for (Session session : result) {
            assertThat(session.getStartTime())
                    .isBetween(LocalTime.of(12, 0), LocalTime.of(16, 0));
        }
    }
    
    @Test
    void findMorningSessions_ShouldReturnSessionsBeforeAfternoon() {
        // When - find morning sessions (before 13:00)
        List<Session> result = sessionRepository.findMorningSessions(
                LocalTime.of(6, 0), LocalTime.of(13, 0));
        
        // Then - should include morning sessions from both migration and test data
        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        
        // Verify all sessions start before 13:00
        for (Session session : result) {
            assertThat(session.getStartTime()).isBefore(LocalTime.of(13, 0));
        }
        
        // Should include migration morning sessions
        assertThat(result).anyMatch(s -> s.getCode().equals("SESI_1") || s.getCode().equals("SESI_2") || s.getCode().equals("SESI_3") || s.getCode().equals("SESI_4"));
    }
    
    @Test
    void findAfternoonSessions_ShouldReturnSessionsFromAfternoonOnwards() {
        // When - find afternoon/evening sessions (from 13:00 onwards)
        List<Session> result = sessionRepository.findAfternoonSessions(LocalTime.of(13, 0));
        
        // Then - should include afternoon sessions from both migration and test data
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        
        // Verify all sessions start from 13:00 onwards
        for (Session session : result) {
            assertThat(session.getStartTime())
                    .isAfterOrEqualTo(LocalTime.of(13, 0));
        }
        
        // Should include afternoon sessions (migration and test data)
        assertThat(result).anyMatch(s -> s.getCode().equals("SESI_5") || s.getCode().equals("SESI_6") || s.getCode().equals("SESI_7"));
        assertThat(result).anyMatch(s -> s.getCode().equals("TEST_SESSION_3"));
    }
    
    @Test
    void existsByCodeAndIsActiveTrue_ShouldReturnTrueForActiveSession() {
        // When
        boolean exists = sessionRepository.existsByCodeAndIsActiveTrue("SESI_5");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    void existsByCodeAndIsActiveTrue_ShouldReturnFalseForInactiveSession() {
        // Given - use TEST_SESSION_2 which we control
        afternoonSession.setIsActive(false);
        sessionRepository.save(afternoonSession);
        
        // When
        boolean exists = sessionRepository.existsByCodeAndIsActiveTrue("TEST_SESSION_2");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    void existsByCodeAndIsActiveTrue_ShouldReturnFalseForNonExistentSession() {
        // When
        boolean exists = sessionRepository.existsByCodeAndIsActiveTrue("NON_EXISTENT");
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    void shouldNotReturnInactiveSessions() {
        // Given
        Session inactiveSession = createTestSession("INACTIVE", "Inactive Session", 
                LocalTime.of(10, 0), LocalTime.of(12, 0));
        inactiveSession.setIsActive(false);
        sessionRepository.save(inactiveSession);
        
        // When
        List<Session> activeSessions = sessionRepository.findByIsActiveTrueOrderByStartTime();
        
        // Then - should have at least the original active sessions (migration + test)
        assertThat(activeSessions).hasSizeGreaterThanOrEqualTo(3);
        assertThat(activeSessions)
                .extracting(Session::getCode)
                .doesNotContain("INACTIVE");
    }
    
    @Test
    void findActiveByTimeRange_ShouldReturnEmptyForNoMatchingRange() {
        // When - search for sessions in a time range with no sessions
        List<Session> result = sessionRepository.findActiveByTimeRange(
                LocalTime.of(18, 0), LocalTime.of(20, 0));
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldHandleEdgeCasesForTimeRanges() {
        // Given - create a session that starts exactly at boundary
        Session boundarySession = createTestSession("BOUNDARY", "Boundary Session", 
                LocalTime.of(13, 0), LocalTime.of(14, 0));
        sessionRepository.save(boundarySession);
        
        // When - search with exact start time as boundary
        List<Session> result = sessionRepository.findActiveByTimeRange(
                LocalTime.of(13, 0), LocalTime.of(14, 0));
        
        // Then - should include boundary session plus any migration sessions in that range
        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        assertThat(result).anyMatch(s -> s.getCode().equals("BOUNDARY"));
    }
    
    private Session createTestSession(String code, String name, LocalTime startTime, LocalTime endTime) {
        Session session = new Session();
        session.setCode(code);
        session.setName(name);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setIsActive(true);
        return session;
    }
}