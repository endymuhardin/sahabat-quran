package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/student-registration-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class StudentRegistrationRepositoryTest extends BaseIntegrationTest {
    
    @Autowired
    private StudentRegistrationRepository registrationRepository;
    
    @Autowired
    private ProgramRepository programRepository;
    
    @Autowired
    private PlacementTestVerseRepository placementVerseRepository;
    
    private final Faker faker = new Faker();
    private Program testProgram;
    private PlacementTestVerse testVerse;
    private StudentRegistration testRegistration;
    
    @BeforeEach
    void setUp() {
        // Use the test program created by SQL script
        testProgram = programRepository.findByCodeAndIsActiveTrue("TEST_PROGRAM").orElseThrow();
        
        // Create test placement verse using Faker
        testVerse = new PlacementTestVerse();
        testVerse.setSurahNumber(faker.number().numberBetween(1, 114));
        testVerse.setSurahName(faker.lorem().word());
        testVerse.setAyahStart(1);
        testVerse.setAyahEnd(7);
        testVerse.setArabicText(faker.lorem().sentence());
        testVerse.setDifficultyLevel(1);
        testVerse.setIsActive(true);
        testVerse = placementVerseRepository.save(testVerse);
        
        // Test data loaded via SQL script, get the existing one
        testRegistration = registrationRepository.findByEmailAndRegistrationStatusNot(
                "test@example.com", StudentRegistration.RegistrationStatus.REJECTED).orElse(null);
        if (testRegistration == null) {
            // Fallback: create a new one if not found
            testRegistration = createTestRegistrationWithFaker();
            testRegistration = registrationRepository.save(testRegistration);
        }
    }
    
    @Test
    void findByRegistrationStatusOrderByCreatedAtDesc_ShouldReturnRegistrationsByStatus() {
        // Given - create unique registration using Faker
        StudentRegistration submittedRegistration = createTestRegistrationWithFaker();
        submittedRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
        submittedRegistration.setSubmittedAt(LocalDateTime.now());
        registrationRepository.save(submittedRegistration);
        
        // When
        Page<StudentRegistration> result = registrationRepository.findByRegistrationStatusOrderByCreatedAtDesc(
                StudentRegistration.RegistrationStatus.SUBMITTED, PageRequest.of(0, 10));
        
        // Then
        assertThat(result.getContent()).hasSize(3); // Include migration data + setup data + test data
        assertThat(result.getContent())
                .extracting(StudentRegistration::getRegistrationStatus)
                .containsOnly(StudentRegistration.RegistrationStatus.SUBMITTED);
    }
    
    @Test
    void findByRegistrationStatusInOrderByCreatedAtDesc_ShouldReturnRegistrationsByMultipleStatuses() {
        // Given - create unique registrations using Faker
        StudentRegistration submittedRegistration = createTestRegistrationWithFaker();
        submittedRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
        registrationRepository.save(submittedRegistration);
        
        StudentRegistration reviewedRegistration = createTestRegistrationWithFaker();
        reviewedRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.ASSIGNED);
        registrationRepository.save(reviewedRegistration);
        
        List<StudentRegistration.RegistrationStatus> statuses = List.of(
                StudentRegistration.RegistrationStatus.SUBMITTED,
                StudentRegistration.RegistrationStatus.ASSIGNED
        );
        
        // When
        Page<StudentRegistration> result = registrationRepository.findByRegistrationStatusInOrderByCreatedAtDesc(
                statuses, PageRequest.of(0, 10));
        
        // Then - should have at least 2 (including setup data)
        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(2);
        // Verify our test data is included
        assertThat(result.getContent())
                .extracting(StudentRegistration::getRegistrationStatus)
                .contains(
                        StudentRegistration.RegistrationStatus.SUBMITTED,
                        StudentRegistration.RegistrationStatus.ASSIGNED
                );
    }
    
    @Test
    void findByStatusAndProgram_ShouldReturnRegistrationsByStatusAndProgram() {
        // Given
        StudentRegistration registration = createTestRegistration();
        registration.setEmail("program@example.com");
        registration.setPhoneNumber("081234567894");
        registration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
        registrationRepository.save(registration);
        
        // When
        Page<StudentRegistration> result = registrationRepository.findByStatusAndProgram(
                StudentRegistration.RegistrationStatus.REVIEWED, 
                testProgram.getId(), 
                PageRequest.of(0, 10)
        );
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRegistrationStatus())
                .isEqualTo(StudentRegistration.RegistrationStatus.REVIEWED);
        assertThat(result.getContent().get(0).getProgram().getId()).isEqualTo(testProgram.getId());
    }
    
    @Test
    void findByPlacementTestStatus_ShouldReturnRegistrationsByPlacementStatus() {
        // Given
        testRegistration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.SUBMITTED);
        testRegistration.setRecordingDriveLink("https://drive.google.com/test");
        testRegistration.setSubmittedAt(LocalDateTime.now().minusHours(1));
        registrationRepository.save(testRegistration);
        
        StudentRegistration anotherRegistration = createTestRegistration();
        anotherRegistration.setEmail("placement@example.com");
        anotherRegistration.setPhoneNumber("081234567895");
        anotherRegistration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.SUBMITTED);
        anotherRegistration.setSubmittedAt(LocalDateTime.now());
        registrationRepository.save(anotherRegistration);
        
        // When
        List<StudentRegistration> result = registrationRepository.findByPlacementTestStatus(
                StudentRegistration.PlacementTestStatus.SUBMITTED);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(StudentRegistration::getPlacementTestStatus)
                .containsOnly(StudentRegistration.PlacementTestStatus.SUBMITTED);
        // Should be ordered by submittedAt ASC
        assertThat(result.get(0).getSubmittedAt()).isBefore(result.get(1).getSubmittedAt());
    }
    
    @Test
    void findByEmailAndRegistrationStatusNot_ShouldReturnRegistrationWithDifferentStatus() {
        // Given
        testRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
        registrationRepository.save(testRegistration);
        
        // When
        Optional<StudentRegistration> result = registrationRepository.findByEmailAndRegistrationStatusNot(
                testRegistration.getEmail(), StudentRegistration.RegistrationStatus.REJECTED);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(testRegistration.getEmail());
        assertThat(result.get().getRegistrationStatus()).isNotEqualTo(StudentRegistration.RegistrationStatus.REJECTED);
    }
    
    @Test
    void findByEmailAndRegistrationStatusNot_ShouldNotReturnRejectedRegistration() {
        // Given
        testRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REJECTED);
        registrationRepository.save(testRegistration);
        
        // When
        Optional<StudentRegistration> result = registrationRepository.findByEmailAndRegistrationStatusNot(
                testRegistration.getEmail(), StudentRegistration.RegistrationStatus.REJECTED);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void countByRegistrationStatus_ShouldReturnCorrectCount() {
        // Given
        StudentRegistration registration1 = createTestRegistration();
        registration1.setEmail("count1@example.com");
        registration1.setPhoneNumber("081234567896");
        registration1.setRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
        registrationRepository.save(registration1);
        
        StudentRegistration registration2 = createTestRegistration();
        registration2.setEmail("count2@example.com");
        registration2.setPhoneNumber("081234567897");
        registration2.setRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
        registrationRepository.save(registration2);
        
        // When
        long count = registrationRepository.countByRegistrationStatus(StudentRegistration.RegistrationStatus.SUBMITTED);
        
        // Then - include setup data (1 setup + 2 test = 3 SUBMITTED) 
        assertThat(count).isGreaterThanOrEqualTo(2);
    }
    
    @Test
    void countByPlacementTestStatus_ShouldReturnCorrectCount() {
        // Given
        testRegistration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.EVALUATED);
        testRegistration.setPlacementResult(3);
        registrationRepository.save(testRegistration);
        
        // When
        long count = registrationRepository.countByPlacementTestStatus(StudentRegistration.PlacementTestStatus.EVALUATED);
        
        // Then
        assertThat(count).isEqualTo(1);
    }
    
    @Test
    void searchRegistrations_ShouldReturnFilteredResults() {
        // Given
        StudentRegistration registration1 = createTestRegistration();
        registration1.setFullName("Ahmad Zaki");
        registration1.setEmail("ahmad@example.com");
        registration1.setPhoneNumber("081234567898");
        registration1.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
        registrationRepository.save(registration1);
        
        StudentRegistration registration2 = createTestRegistration();
        registration2.setFullName("Sarah Abdullah");
        registration2.setEmail("sarah@example.com");
        registration2.setPhoneNumber("081234567899");
        registration2.setRegistrationStatus(StudentRegistration.RegistrationStatus.REJECTED);
        registrationRepository.save(registration2);
        
        // When - search by name
        Page<StudentRegistration> nameResult = registrationRepository.searchRegistrations(
                "Ahmad", null, null, null, null, PageRequest.of(0, 10));
        
        // Then - filter for test-specific Ahmad
        assertThat(nameResult.getContent()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(nameResult.getContent())
            .anyMatch(reg -> reg.getFullName().equals("Ahmad Zaki"));
        
        // Get the specific test record
        StudentRegistration testRecord = nameResult.getContent().stream()
            .filter(reg -> reg.getFullName().equals("Ahmad Zaki"))
            .findFirst().orElseThrow();
        assertThat(testRecord.getFullName()).contains("Ahmad");
        
        // When - search by email
        Page<StudentRegistration> emailResult = registrationRepository.searchRegistrations(
                null, "sarah", null, null, null, PageRequest.of(0, 10));
        
        // Then
        assertThat(emailResult.getContent()).hasSize(1);
        assertThat(emailResult.getContent().get(0).getEmail()).contains("sarah");
        
        // When - search by status
        Page<StudentRegistration> statusResult = registrationRepository.searchRegistrations(
                null, null, null, StudentRegistration.RegistrationStatus.REVIEWED, null, PageRequest.of(0, 10));
        
        // Then
        assertThat(statusResult.getContent()).hasSize(1);
        assertThat(statusResult.getContent().get(0).getRegistrationStatus())
                .isEqualTo(StudentRegistration.RegistrationStatus.REVIEWED);
    }
    
    @Test
    void existsByEmailAndRegistrationStatusNot_ShouldReturnCorrectBoolean() {
        // Given
        testRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
        registrationRepository.save(testRegistration);
        
        // When - check for non-rejected status
        boolean exists = registrationRepository.existsByEmailAndRegistrationStatusNot(
                testRegistration.getEmail(), StudentRegistration.RegistrationStatus.REJECTED);
        
        // Then
        assertThat(exists).isTrue();
        
        // When - check with rejected registration
        testRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REJECTED);
        registrationRepository.save(testRegistration);
        
        boolean notExists = registrationRepository.existsByEmailAndRegistrationStatusNot(
                testRegistration.getEmail(), StudentRegistration.RegistrationStatus.REJECTED);
        
        // Then
        assertThat(notExists).isFalse();
    }
    
    @Test
    void existsByPhoneNumberAndRegistrationStatusNot_ShouldReturnCorrectBoolean() {
        // Given
        testRegistration.setRegistrationStatus(StudentRegistration.RegistrationStatus.REVIEWED);
        registrationRepository.save(testRegistration);
        
        // When
        boolean exists = registrationRepository.existsByPhoneNumberAndRegistrationStatusNot(
                testRegistration.getPhoneNumber(), StudentRegistration.RegistrationStatus.REJECTED);
        
        // Then
        assertThat(exists).isTrue();
    }
    
    private StudentRegistration createTestRegistration() {
        StudentRegistration registration = new StudentRegistration();
        
        // Personal Information
        registration.setFullName("Test Student");
        registration.setGender(StudentRegistration.Gender.MALE);
        registration.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registration.setPlaceOfBirth("Jakarta");
        registration.setPhoneNumber("081234567890");
        registration.setEmail("test@example.com");
        registration.setAddress("Test Address");
        registration.setEmergencyContactName("Test Emergency Contact");
        registration.setEmergencyContactPhone("081234567800");
        registration.setEmergencyContactRelation("Parent");
        
        // Educational Information
        registration.setEducationLevel("S1");
        registration.setSchoolName("Test University");
        registration.setQuranReadingExperience("Basic reading ability");
        registration.setPreviousTahsinExperience(false);
        
        // Program Selection
        registration.setProgram(testProgram);
        registration.setRegistrationReason("Want to improve Quran reading");
        registration.setLearningGoals("Master tajweed rules");
        
        // Schedule Preferences JSON
        registration.setSchedulePreferences("[{\"sessionId\":\"test-session-id\",\"priority\":1,\"days\":[\"MONDAY\",\"WEDNESDAY\"]}]");
        
        // Placement Test
        registration.setPlacementVerse(testVerse);
        registration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.PENDING);
        
        // Status
        registration.setRegistrationStatus(StudentRegistration.RegistrationStatus.DRAFT);
        
        return registration;
    }
    
    private StudentRegistration createTestRegistrationWithFaker() {
        StudentRegistration registration = new StudentRegistration();
        
        // Personal Information with Faker
        registration.setFullName(faker.name().fullName());
        registration.setGender(faker.options().option(StudentRegistration.Gender.MALE, StudentRegistration.Gender.FEMALE));
        registration.setDateOfBirth(faker.date().birthday().toLocalDateTime().toLocalDate());
        registration.setPlaceOfBirth(faker.address().city());
        registration.setPhoneNumber("0812" + faker.number().digits(8));
        registration.setEmail(faker.internet().emailAddress());
        registration.setAddress(faker.address().fullAddress());
        registration.setEmergencyContactName(faker.name().fullName());
        registration.setEmergencyContactPhone("0813" + faker.number().digits(8));
        registration.setEmergencyContactRelation(faker.options().option("Parent", "Spouse", "Sibling"));
        
        // Educational Information with Faker
        registration.setEducationLevel(faker.options().option("SMA", "S1", "S2", "S3"));
        registration.setSchoolName(faker.university().name());
        registration.setQuranReadingExperience(faker.lorem().sentence());
        registration.setPreviousTahsinExperience(faker.bool().bool());
        
        // Program Selection
        registration.setProgram(testProgram);
        registration.setRegistrationReason(faker.lorem().sentence());
        registration.setLearningGoals(faker.lorem().sentence());
        
        // Schedule Preferences JSON
        registration.setSchedulePreferences("[{\"sessionId\":\"" + faker.internet().uuid() + "\",\"priority\":1,\"days\":[\"MONDAY\",\"WEDNESDAY\"]}]");
        
        // Placement Test
        registration.setPlacementVerse(testVerse);
        registration.setPlacementTestStatus(StudentRegistration.PlacementTestStatus.PENDING);
        
        // Status
        registration.setRegistrationStatus(StudentRegistration.RegistrationStatus.DRAFT);
        
        return registration;
    }
}