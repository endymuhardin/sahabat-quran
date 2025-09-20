package com.sahabatquran.webapp.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PdfReportGenerationService to verify actual PDF content generation.
 *
 * This test verifies that the backend implementation generates real PDF content
 * instead of placeholder functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PDF Content Generation Tests")
class PdfReportGenerationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentAssessmentRepository studentAssessmentRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ClassGroupRepository classGroupRepository;

    @InjectMocks
    private PdfReportGenerationService pdfReportGenerationService;

    @TempDir
    Path tempDir;

    private User testStudent;
    private User testTeacher;
    private ClassGroup testClassGroup;
    private AcademicTerm testTerm;
    private Level testLevel;
    private Enrollment testEnrollment;
    private StudentAssessment testAssessment;

    @BeforeEach
    void setUp() {
        // Set the temp directory for report output
        ReflectionTestUtils.setField(pdfReportGenerationService, "reportsOutputDirectory", tempDir.toString());

        // Create test entities
        testStudent = createTestStudent();
        testTeacher = createTestTeacher();
        testLevel = createTestLevel();
        testTerm = createTestTerm();
        testClassGroup = createTestClassGroup();
        testEnrollment = createTestEnrollment();
        testAssessment = createTestAssessment();
    }

    @Test
    @DisplayName("Should generate student report with actual PDF content")
    void shouldGenerateStudentReportWithActualContent() throws IOException {
        // Arrange
        UUID studentId = testStudent.getId();
        UUID termId = testTerm.getId();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(enrollmentRepository.findByStudentAndTerm(studentId, termId)).thenReturn(List.of(testEnrollment));
        when(studentAssessmentRepository.findByStudentIdAndTermId(studentId, termId)).thenReturn(List.of(testAssessment));

        // Act
        String filePath = pdfReportGenerationService.generateStudentReport(studentId, termId, "STANDARD");

        // Assert
        assertNotNull(filePath, "File path should not be null");

        File pdfFile = new File(filePath);
        assertTrue(pdfFile.exists(), "PDF file should be created");
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty");

        // Verify PDF content
        String pdfContent = extractPdfText(filePath);

        // Verify header content
        assertTrue(pdfContent.contains("STUDENT ACADEMIC REPORT"),
            "PDF should contain report header");

        // Verify student information
        assertTrue(pdfContent.contains("Student Information"),
            "PDF should contain student information section");
        assertTrue(pdfContent.contains(testStudent.getUsername()),
            "PDF should contain student username");
        assertTrue(pdfContent.contains(testStudent.getFullName()),
            "PDF should contain student full name");
        assertTrue(pdfContent.contains(testStudent.getEmail()),
            "PDF should contain student email");

        // Verify enrollment information
        assertTrue(pdfContent.contains("Enrollment Information"),
            "PDF should contain enrollment information section");
        assertTrue(pdfContent.contains(testClassGroup.getName()),
            "PDF should contain class name");
        assertTrue(pdfContent.contains("ACTIVE"),
            "PDF should contain enrollment status");

        // Verify assessment information
        assertTrue(pdfContent.contains("Assessment Results"),
            "PDF should contain assessment results section");
        assertTrue(pdfContent.contains("PLACEMENT"),
            "PDF should contain assessment type");
        assertTrue(pdfContent.contains("85"),
            "PDF should contain assessment score");
        assertTrue(pdfContent.contains("A"),
            "PDF should contain assessment grade");

        // Verify timestamp
        assertTrue(pdfContent.contains("Report generated on:"),
            "PDF should contain generation timestamp");

        System.out.println("✅ Student PDF Content Verification:");
        System.out.println("📄 File: " + filePath);
        System.out.println("📏 Size: " + pdfFile.length() + " bytes");
        System.out.println("📝 Content verified: Student info, enrollments, assessments");
    }

    @Test
    @DisplayName("Should generate class summary report with actual PDF content")
    void shouldGenerateClassSummaryReportWithActualContent() throws IOException {
        // Arrange
        UUID classId = testClassGroup.getId();
        UUID termId = testTerm.getId();

        when(classGroupRepository.findById(classId)).thenReturn(Optional.of(testClassGroup));
        when(enrollmentRepository.findByClassGroupId(classId)).thenReturn(List.of(testEnrollment));

        // Act
        String filePath = pdfReportGenerationService.generateClassSummaryReport(classId, termId);

        // Assert
        assertNotNull(filePath, "File path should not be null");

        File pdfFile = new File(filePath);
        assertTrue(pdfFile.exists(), "PDF file should be created");
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty");

        // Verify PDF content
        String pdfContent = extractPdfText(filePath);

        // Verify header content
        assertTrue(pdfContent.contains("CLASS SUMMARY REPORT"),
            "PDF should contain report header");

        // Verify class information
        assertTrue(pdfContent.contains("Class Information"),
            "PDF should contain class information section");
        assertTrue(pdfContent.contains(testClassGroup.getName()),
            "PDF should contain class name");
        assertTrue(pdfContent.contains("25"),
            "PDF should contain class capacity");

        // Verify enrollment statistics
        assertTrue(pdfContent.contains("Enrollment Statistics"),
            "PDF should contain enrollment statistics section");
        assertTrue(pdfContent.contains("Total Enrolled Students"),
            "PDF should contain total enrollment count");
        assertTrue(pdfContent.contains("Active Students"),
            "PDF should contain active enrollment count");

        // Verify timestamp
        assertTrue(pdfContent.contains("Report generated on:"),
            "PDF should contain generation timestamp");

        System.out.println("✅ Class Summary PDF Content Verification:");
        System.out.println("📄 File: " + filePath);
        System.out.println("📏 Size: " + pdfFile.length() + " bytes");
        System.out.println("📝 Content verified: Class info, enrollment statistics");
    }

    @Test
    @DisplayName("Should handle missing data gracefully in PDF generation")
    void shouldHandleMissingDataGracefullyInPdfGeneration() throws IOException {
        // Arrange
        UUID studentId = testStudent.getId();
        UUID termId = testTerm.getId();

        when(userRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(enrollmentRepository.findByStudentAndTerm(studentId, termId)).thenReturn(List.of()); // No enrollments
        when(studentAssessmentRepository.findByStudentIdAndTermId(studentId, termId)).thenReturn(List.of()); // No assessments

        // Act
        String filePath = pdfReportGenerationService.generateStudentReport(studentId, termId, "STANDARD");

        // Assert
        assertNotNull(filePath, "File path should not be null");

        File pdfFile = new File(filePath);
        assertTrue(pdfFile.exists(), "PDF file should be created even with missing data");
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty");

        // Verify PDF content
        String pdfContent = extractPdfText(filePath);

        // Should still contain basic structure
        assertTrue(pdfContent.contains("STUDENT ACADEMIC REPORT"),
            "PDF should contain report header");
        assertTrue(pdfContent.contains("Student Information"),
            "PDF should contain student information section");
        assertTrue(pdfContent.contains(testStudent.getFullName()),
            "PDF should contain student name");

        // Should NOT contain enrollment or assessment sections since data is missing
        assertFalse(pdfContent.contains("Enrollment Information"),
            "PDF should not contain enrollment section when no enrollments exist");
        assertFalse(pdfContent.contains("Assessment Results"),
            "PDF should not contain assessment section when no assessments exist");

        System.out.println("✅ Missing Data Handling Verification:");
        System.out.println("📄 File: " + filePath);
        System.out.println("📏 Size: " + pdfFile.length() + " bytes");
        System.out.println("📝 Content verified: Graceful handling of missing enrollment/assessment data");
    }

    // Helper method to extract text from PDF for content verification
    private String extractPdfText(String filePath) throws IOException {
        try (PdfReader reader = new PdfReader(filePath);
             PdfDocument pdfDoc = new PdfDocument(reader)) {

            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
            }
            return text.toString();
        }
    }

    // Test data creation methods
    private User createTestStudent() {
        User student = new User();
        student.setId(UUID.randomUUID());
        student.setUsername("test.student");
        student.setFullName("Test Student Name");
        student.setEmail("test.student@example.com");
        return student;
    }

    private User createTestTeacher() {
        User teacher = new User();
        teacher.setId(UUID.randomUUID());
        teacher.setUsername("test.teacher");
        teacher.setFullName("Test Teacher Name");
        teacher.setEmail("test.teacher@example.com");
        return teacher;
    }

    private Level createTestLevel() {
        Level level = new Level();
        level.setId(UUID.randomUUID());
        level.setName("Level 1");
        level.setDescription("Beginner Level");
        level.setOrderNumber(1);
        level.setCompetencyLevel(Level.CompetencyLevel.FOUNDATION);
        return level;
    }

    private AcademicTerm createTestTerm() {
        AcademicTerm term = new AcademicTerm();
        term.setId(UUID.randomUUID());
        term.setTermName("Test Semester 2024");
        term.setStartDate(LocalDate.now().minusMonths(3));
        term.setEndDate(LocalDate.now().plusMonths(3));
        return term;
    }

    private ClassGroup createTestClassGroup() {
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(UUID.randomUUID());
        classGroup.setName("Test Class 1A");
        classGroup.setCapacity(25);
        classGroup.setLevel(testLevel);
        classGroup.setInstructor(testTeacher);
        classGroup.setTerm(testTerm);
        return classGroup;
    }

    private Enrollment createTestEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(UUID.randomUUID());
        enrollment.setStudent(testStudent);
        enrollment.setClassGroup(testClassGroup);
        enrollment.setEnrollmentDate(LocalDate.now().minusMonths(2));
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollment.setCreatedAt(LocalDateTime.now().minusMonths(2));
        return enrollment;
    }

    private StudentAssessment createTestAssessment() {
        StudentAssessment assessment = new StudentAssessment();
        assessment.setId(UUID.randomUUID());
        assessment.setStudent(testStudent);
        assessment.setTerm(testTerm);
        assessment.setStudentCategory(StudentAssessment.StudentCategory.NEW);
        assessment.setAssessmentType(StudentAssessment.AssessmentType.PLACEMENT);
        assessment.setAssessmentScore(new BigDecimal("85.00"));
        assessment.setAssessmentGrade("A");
        assessment.setAssessmentDate(LocalDate.now().minusMonths(1));
        assessment.setIsValidated(true);
        assessment.setCreatedAt(LocalDateTime.now().minusMonths(1));
        return assessment;
    }
}