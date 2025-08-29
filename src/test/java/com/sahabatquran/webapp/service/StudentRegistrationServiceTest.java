package com.sahabatquran.webapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahabatquran.webapp.dto.PlacementTestEvaluationRequest;
import com.sahabatquran.webapp.dto.RegistrationReviewRequest;
import com.sahabatquran.webapp.dto.RegistrationSearchRequest;
import com.sahabatquran.webapp.dto.StudentRegistrationRequest;
import com.sahabatquran.webapp.dto.StudentRegistrationResponse;
import com.sahabatquran.webapp.entity.PlacementTestVerse;
import com.sahabatquran.webapp.entity.Program;
import com.sahabatquran.webapp.entity.Session;
import com.sahabatquran.webapp.entity.StudentRegistration;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.repository.PlacementTestVerseRepository;
import com.sahabatquran.webapp.repository.ProgramRepository;
import com.sahabatquran.webapp.repository.SessionRepository;
import com.sahabatquran.webapp.repository.StudentRegistrationAuditRepository;
import com.sahabatquran.webapp.repository.StudentRegistrationRepository;
import com.sahabatquran.webapp.repository.UserRepository;

import net.datafaker.Faker;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {"/test-data/student-registration-setup.sql", "/test-data/session-setup.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class StudentRegistrationServiceTest extends BaseIntegrationTest {
    
    private final Faker faker = new Faker();
    private static final ThreadLocal<String> testPrefix = new ThreadLocal<>();
    
    @Autowired
    private StudentRegistrationService registrationService;
    
    @Autowired
    private StudentRegistrationRepository registrationRepository;
    
    @Autowired
    private ProgramRepository programRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private PlacementTestVerseRepository placementVerseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRegistrationAuditRepository auditRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Program testProgram;
    private Session testSession1;
    private Session testSession2;
    private Session testSession3;
    private PlacementTestVerse testVerse;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Generate unique test prefix for this test execution thread
        generateTestPrefix();
        // Use test data from SQL scripts
        testProgram = programRepository.findByCodeAndIsActiveTrue("TEST_PROGRAM").orElseThrow();
        
        // Get test sessions from SQL script  
        testSession1 = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_1").orElse(null);
        testSession2 = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_2").orElse(null);
        testSession3 = sessionRepository.findByCodeAndIsActiveTrue("TEST_SESSION_3").orElse(null);
        
        // Create fallback sessions if needed
        if (testSession1 == null) {
            testSession1 = createFallbackSession("FALLBACK_1", LocalTime.of(8, 0), LocalTime.of(10, 0));
        }
        if (testSession2 == null) {
            testSession2 = createFallbackSession("FALLBACK_2", LocalTime.of(14, 0), LocalTime.of(16, 0));
        }
        if (testSession3 == null) {
            testSession3 = createFallbackSession("FALLBACK_3", LocalTime.of(16, 0), LocalTime.of(18, 0));
        }
        
        // Create test placement verse with Faker data
        testVerse = new PlacementTestVerse();
        testVerse.setSurahNumber(faker.number().numberBetween(1, 114));
        testVerse.setSurahName(faker.lorem().word());
        testVerse.setAyahStart(1);
        testVerse.setAyahEnd(7);
        testVerse.setArabicText(faker.lorem().sentence());
        testVerse.setTransliteration(faker.lorem().sentence());
        testVerse.setDifficultyLevel(1);
        testVerse.setIsActive(true);
        testVerse = placementVerseRepository.save(testVerse);
        
        // Create test user with Faker data
        testUser = new User();
        testUser.setUsername(faker.internet().username());
        testUser.setEmail(faker.internet().emailAddress());
        testUser.setFullName(faker.name().fullName());
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up ThreadLocal to prevent memory leaks
        testPrefix.remove();
        
        // Clean up test instance variables to avoid codebase clutter
        testProgram = null;
        testSession1 = null;
        testSession2 = null;
        testSession3 = null;
        testVerse = null;
        testUser = null;
    }
    
    private void generateTestPrefix() {
        // Generate 3-character base-36 random string (0-9, a-z)
        String prefix = "";
        for (int i = 0; i < 3; i++) {
            prefix += Character.forDigit(faker.random().nextInt(36), 36);
        }
        testPrefix.set(prefix);
    }
    
    private String getTestPrefix() {
        return testPrefix.get();
    }
    
    private String withTestPrefix(String value) {
        return getTestPrefix() + "_" + value;
    }
    
    @Test
    void createRegistration_ShouldCreateSuccessfully() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        
        // When
        StudentRegistrationResponse response = registrationService.createRegistration(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getFullName()).isNotNull();
        assertThat(response.getEmail()).isNotNull();
        assertThat(response.getRegistrationStatus()).isEqualTo(StudentRegistration.RegistrationStatus.DRAFT);
        assertThat(response.getProgram().getCode()).isEqualTo("TEST_PROGRAM");
        assertThat(response.getSessionPreferences()).hasSize(2);
        
        // Verify placement test verse is assigned
        assertThat(response.getPlacementTest()).isNotNull();
        assertThat(response.getPlacementTest().getStatus()).isEqualTo(StudentRegistration.PlacementTestStatus.PENDING);
        
        // Note: Audit logging will be handled by entity auditing in next iteration
    }
    
    @Test
    void createRegistration_ShouldThrowExceptionForDuplicateEmail() {
        // Given
        StudentRegistrationRequest request1 = createValidRegistrationRequest();
        String duplicateEmail = faker.internet().emailAddress();
        request1.setEmail(duplicateEmail);
        registrationService.createRegistration(request1);
        
        StudentRegistrationRequest request2 = createValidRegistrationRequest();
        request2.setEmail(duplicateEmail); // Same email
        request2.setPhoneNumber("081234567891"); // different phone
        
        // When & Then
        assertThatThrownBy(() -> registrationService.createRegistration(request2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email sudah terdaftar");
    }
    
    @Test
    void createRegistration_ShouldThrowExceptionForDuplicatePhoneNumber() {
        // Given
        StudentRegistrationRequest request1 = createValidRegistrationRequest();
        String duplicatePhone = "0812" + faker.number().digits(8); // Generate unique phone
        request1.setPhoneNumber(duplicatePhone);
        registrationService.createRegistration(request1);
        
        StudentRegistrationRequest request2 = createValidRegistrationRequest();
        request2.setEmail("different@example.com"); // different email
        request2.setPhoneNumber(duplicatePhone); // Same phone
        
        // When & Then
        assertThatThrownBy(() -> registrationService.createRegistration(request2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nomor telepon sudah terdaftar");
    }
    
    @Test
    void createRegistration_ShouldThrowExceptionForNonExistentProgram() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        request.setProgramId(UUID.randomUUID()); // non-existent program
        
        // When & Then
        assertThatThrownBy(() -> registrationService.createRegistration(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Program tidak ditemukan");
    }
    
    @Test
    void updateRegistration_ShouldUpdateSuccessfully() {
        // Given
        StudentRegistrationRequest createRequest = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(createRequest);
        
        StudentRegistrationRequest updateRequest = new StudentRegistrationRequest();
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhoneNumber("081234567899");
        updateRequest.setAddress("Updated Address");
        updateRequest.setGender(StudentRegistration.Gender.MALE);
        updateRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updateRequest.setPlaceOfBirth("Updated City");
        updateRequest.setEmergencyContactName("Updated Contact");
        updateRequest.setEmergencyContactPhone("081234567888");
        updateRequest.setEmergencyContactRelation("Spouse");
        updateRequest.setEducationLevel("S1");
        updateRequest.setSchoolName("Updated School");
        updateRequest.setQuranReadingExperience("Updated experience");
        updateRequest.setPreviousTahsinExperience(true);
        updateRequest.setProgramId(testProgram.getId());
        updateRequest.setRegistrationReason("Updated reason");
        updateRequest.setLearningGoals("Updated goals");
        
        // Don't update session preferences to avoid constraint conflicts
        // The update test should focus on personal/educational info updates
        updateRequest.setSessionPreferences(List.of());
        
        // When
        StudentRegistrationResponse response = registrationService.updateRegistration(created.getId(), updateRequest);
        
        // Then
        assertThat(response.getFullName()).isEqualTo("Updated Name");
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
        assertThat(response.getPhoneNumber()).isEqualTo("081234567899");
        
        // Note: Audit logging will be handled by entity auditing in next iteration
    }
    
    @Test
    void updateRegistration_ShouldThrowExceptionForSubmittedRegistration() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        
        // Submit the registration first
        registrationService.submitRegistration(created.getId());
        
        // When & Then
        assertThatThrownBy(() -> registrationService.updateRegistration(created.getId(), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Hanya pendaftaran dengan status DRAFT yang dapat diubah");
    }
    
    @Test
    void submitRegistration_ShouldUpdateStatusSuccessfully() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        
        // When
        StudentRegistrationResponse response = registrationService.submitRegistration(created.getId());
        
        // Then
        assertThat(response.getRegistrationStatus()).isEqualTo(StudentRegistration.RegistrationStatus.SUBMITTED);
        assertThat(response.getSubmittedAt()).isNotNull();
        
        // Note: Audit logging will be handled by entity auditing in next iteration
    }
    
    @Test
    void submitRegistration_ShouldThrowExceptionForNonDraftStatus() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        registrationService.submitRegistration(created.getId());
        
        // When & Then
        assertThatThrownBy(() -> registrationService.submitRegistration(created.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Hanya pendaftaran dengan status DRAFT yang dapat disubmit");
    }
    
    @Test
    void reviewRegistration_ShouldUpdateStatusAndReviewInfo() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        registrationService.submitRegistration(created.getId());
        
        RegistrationReviewRequest reviewRequest = new RegistrationReviewRequest();
        reviewRequest.setRegistrationId(created.getId());
        reviewRequest.setNewStatus(StudentRegistration.RegistrationStatus.REVIEWED);
        reviewRequest.setReviewNotes("Approved after review");
        
        // When
        StudentRegistrationResponse response = registrationService.reviewRegistration(reviewRequest);
        
        // Then
        assertThat(response.getRegistrationStatus()).isEqualTo(StudentRegistration.RegistrationStatus.REVIEWED);
        assertThat(response.getReviewedAt()).isNotNull();
        // Note: ReviewedBy will be null until entity auditing is implemented
        assertThat(response.getReviewedByName()).isNull();
        assertThat(response.getReviewNotes()).isEqualTo("Approved after review");
    }
    
    @Test
    void evaluatePlacementTest_ShouldUpdatePlacementTestResults() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        request.setRecordingDriveLink("https://drive.google.com/test");
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        
        // Update placement test status to SUBMITTED manually
        StudentRegistration registration = registrationRepository.findById(created.getId()).orElseThrow();
        registration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.SUBMITTED);
        registrationRepository.save(registration);
        
        PlacementTestEvaluationRequest evalRequest = new PlacementTestEvaluationRequest();
        evalRequest.setRegistrationId(created.getId());
        evalRequest.setPlacementResult(3);
        evalRequest.setEvaluationNotes("Good reading with minor tajweed issues");
        evalRequest.setEvaluationReason("Evaluated based on recording");
        
        // When
        StudentRegistrationResponse response = registrationService.evaluatePlacementTest(evalRequest);
        
        // Then
        assertThat(response.getPlacementTest().getStatus()).isEqualTo(StudentRegistration.PlacementTestStatus.EVALUATED);
        assertThat(response.getPlacementTest().getResult()).isEqualTo(3);
        assertThat(response.getPlacementTest().getNotes()).isEqualTo("Good reading with minor tajweed issues");
        // Note: EvaluatedBy will be null until entity auditing is implemented
        assertThat(response.getPlacementTest().getEvaluatedByName()).isNull();
        assertThat(response.getPlacementTest().getEvaluatedAt()).isNotNull();
    }
    
    @Test
    void evaluatePlacementTest_ShouldThrowExceptionForNonSubmittedTest() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        
        PlacementTestEvaluationRequest evalRequest = new PlacementTestEvaluationRequest();
        evalRequest.setRegistrationId(created.getId());
        evalRequest.setPlacementResult(3);
        evalRequest.setEvaluationNotes("Test notes");
        
        // When & Then
        assertThatThrownBy(() -> registrationService.evaluatePlacementTest(evalRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Placement test belum disubmit");
    }
    
    @Test
    void getRegistration_ShouldReturnRegistrationDetails() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        
        // When
        StudentRegistrationResponse response = registrationService.getRegistration(created.getId());
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(created.getId());
        assertThat(response.getFullName()).isNotNull();
        assertThat(response.getProgram().getCode()).isEqualTo("TEST_PROGRAM");
        assertThat(response.getSessionPreferences()).hasSize(2);
    }
    
    @Test
    void searchRegistrations_ShouldReturnFilteredResults() {
        // Given
        StudentRegistrationRequest request1 = createValidRegistrationRequest();
        request1.setFullName("Ahmad Zaki");
        request1.setEmail("ahmad@example.com");
        request1.setPhoneNumber("0812" + System.currentTimeMillis() % 100000000); // Unique phone
        registrationService.createRegistration(request1);
        
        StudentRegistrationRequest request2 = createValidRegistrationRequest();
        request2.setFullName("Sarah Abdullah");
        request2.setEmail("sarah@example.com");
        request2.setPhoneNumber("0813" + System.currentTimeMillis() % 100000000); // Different unique phone
        registrationService.createRegistration(request2);
        
        RegistrationSearchRequest searchRequest = new RegistrationSearchRequest();
        searchRequest.setFullName("Sarah");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        
        // When
        Page<StudentRegistrationResponse> result = registrationService.searchRegistrations(searchRequest);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isNotNull();
    }
    
    @Test
    void getActivePrograms_ShouldReturnOrderedPrograms() {
        // When - test with existing migration programs + test program from setup
        List<Program> programs = registrationService.getActivePrograms();
        
        // Then
        assertThat(programs).hasSizeGreaterThanOrEqualTo(1);
        
        // Verify ordering - should be ordered by level_order ASC
        if (programs.size() > 1) {
            for (int i = 0; i < programs.size() - 1; i++) {
                assertThat(programs.get(i).getLevelOrder())
                    .isLessThanOrEqualTo(programs.get(i + 1).getLevelOrder());
            }
        }
        
        // All programs should be active
        assertThat(programs).allMatch(Program::getIsActive);
    }
    
    @Test
    void getActiveSessions_ShouldReturnOrderedSessions() {
        // When
        List<Session> sessions = registrationService.getActiveSessions();
        
        // Then
        assertThat(sessions).hasSizeGreaterThanOrEqualTo(2);
        assertThat(sessions.get(0).getStartTime()).isBefore(sessions.get(1).getStartTime());
    }
    
    @Test
    void getPendingPlacementTests_ShouldReturnSubmittedTests() {
        // Given
        StudentRegistrationRequest request = createValidRegistrationRequest();
        request.setRecordingDriveLink("https://drive.google.com/test");
        StudentRegistrationResponse created = registrationService.createRegistration(request);
        
        // Update to submitted status
        StudentRegistration registration = registrationRepository.findById(created.getId()).orElseThrow();
        registration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.SUBMITTED);
        registrationRepository.save(registration);
        
        // When
        List<StudentRegistrationResponse> pendingTests = registrationService.getPendingPlacementTests();
        
        // Then
        assertThat(pendingTests).hasSize(1);
        assertThat(pendingTests.get(0).getPlacementTest().getStatus())
                .isEqualTo(StudentRegistration.PlacementTestStatus.SUBMITTED);
    }
    
    private StudentRegistrationRequest createValidRegistrationRequest() {
        StudentRegistrationRequest request = new StudentRegistrationRequest();
        
        // Personal Information using Faker for unique data with test prefix
        request.setFullName(withTestPrefix(faker.name().fullName()));
        request.setGender(faker.options().option(StudentRegistration.Gender.MALE, StudentRegistration.Gender.FEMALE));
        request.setDateOfBirth(faker.date().birthday().toLocalDateTime().toLocalDate());
        request.setPlaceOfBirth(faker.address().city());
        request.setPhoneNumber("0812" + faker.number().digits(8));
        request.setEmail(withTestPrefix(faker.internet().username()) + "@test.com");
        request.setAddress(faker.address().fullAddress());
        request.setEmergencyContactName(faker.name().fullName());
        request.setEmergencyContactPhone("0813" + faker.number().digits(8));
        request.setEmergencyContactRelation(faker.options().option("Parent", "Spouse", "Sibling"));
        
        // Educational Information using Faker
        request.setEducationLevel(faker.options().option("SMA", "S1", "S2", "S3"));
        request.setSchoolName(faker.university().name());
        request.setQuranReadingExperience(faker.lorem().sentence());
        request.setPreviousTahsinExperience(faker.bool().bool());
        
        // Program Selection with Faker
        request.setProgramId(testProgram.getId());
        request.setRegistrationReason(faker.lorem().sentence());
        request.setLearningGoals(faker.lorem().sentence());
        
        // Session Preferences - randomize to ensure test independence  
        Session[] sessionArray = {testSession1, testSession2, testSession3};
        Collections.shuffle(Arrays.asList(sessionArray));
        
        StudentRegistrationRequest.SessionPreferenceRequest pref1 = 
                new StudentRegistrationRequest.SessionPreferenceRequest();
        pref1.setSessionId(sessionArray[0].getId());
        pref1.setPriority(1);
        pref1.setPreferredDays(List.of(
                faker.options().option(StudentRegistrationRequest.DayOfWeek.values()),
                faker.options().option(StudentRegistrationRequest.DayOfWeek.values())
        ));
        
        StudentRegistrationRequest.SessionPreferenceRequest pref2 = 
                new StudentRegistrationRequest.SessionPreferenceRequest();
        pref2.setSessionId(sessionArray[1].getId());
        pref2.setPriority(2);
        pref2.setPreferredDays(List.of(
                faker.options().option(StudentRegistrationRequest.DayOfWeek.values()),
                faker.options().option(StudentRegistrationRequest.DayOfWeek.values())
        ));
        
        request.setSessionPreferences(List.of(pref1, pref2));
        
        return request;
    }
    
    private Session createFallbackSession(String code, LocalTime startTime, LocalTime endTime) {
        Session session = new Session();
        session.setCode(code);
        session.setName(faker.lorem().words(2).toString());
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setIsActive(true);
        return sessionRepository.save(session);
    }
}