package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.AcademicTranscriptDto;
import com.sahabatquran.webapp.dto.TranscriptRequestDto;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.Enrollment;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TranscriptService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public AcademicTranscriptDto generateTranscript(TranscriptRequestDto request) {
        return generateTranscript(request.getStudentId(), request.getTranscriptFormat());
    }

    @Transactional(readOnly = true)
    public AcademicTranscriptDto generateTranscript(UUID studentId, String transcriptFormat) {
        log.info("Generating transcript for student: {} with format: {}", studentId, transcriptFormat);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByStudent(studentId);

        // Build term records from enrollments
        List<AcademicTranscriptDto.TermRecordDto> termRecords = enrollments.stream()
                .map(this::buildTermRecord)
                .collect(Collectors.toList());

        return AcademicTranscriptDto.builder()
                .studentId(studentId.toString())
                .studentName(student.getFullName())
                .studentNumber(generateStudentNumber(student))
                .dateOfBirth(null) // User entity doesn't have dateOfBirth field
                .currentLevel(getCurrentLevel(enrollments))
                .transcriptType(transcriptFormat)
                .generationDate(LocalDate.now())
                .termRecords(termRecords)
                .cumulativeGpa(calculateCumulativeGpa(enrollments))
                .totalCredits(calculateTotalCredits(enrollments))
                .academicStanding(determineAcademicStanding(enrollments))
                .officialSeal(transcriptFormat.equals("OFFICIAL_TRANSCRIPT") ? "OFFICIAL_SEAL_APPLIED" : null)
                .issuedBy("Academic Administration")
                .issuedDate(LocalDate.now())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean canGenerateTranscript(UUID studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByStudent(studentId);
        return !enrollments.isEmpty() && enrollments.stream()
                .anyMatch(enrollment -> enrollment.getStatus() != null);
    }

    @Transactional(readOnly = true)
    public String[] getAvailableFormats(UUID studentId) {
        if (canGenerateTranscript(studentId)) {
            return new String[]{"OFFICIAL_TRANSCRIPT", "UNOFFICIAL_TRANSCRIPT", "GRADE_REPORT"};
        }
        return new String[]{};
    }

    private AcademicTranscriptDto.TermRecordDto buildTermRecord(Enrollment enrollment) {
        // Build subject grades - for now using sample data
        List<AcademicTranscriptDto.SubjectGradeDto> subjects = List.of(
                AcademicTranscriptDto.SubjectGradeDto.builder()
                        .subjectName("Al-Qur'an Recitation")
                        .grade("A")
                        .score("85")
                        .credits("3")
                        .teacherName(enrollment.getClassGroup().getInstructor() != null ? enrollment.getClassGroup().getInstructor().getFullName() : "N/A")
                        .build(),
                AcademicTranscriptDto.SubjectGradeDto.builder()
                        .subjectName("Tahfidz")
                        .grade("A-")
                        .score("80")
                        .credits("4")
                        .teacherName(enrollment.getClassGroup().getInstructor() != null ? enrollment.getClassGroup().getInstructor().getFullName() : "N/A")
                        .build()
        );

        return AcademicTranscriptDto.TermRecordDto.builder()
                .termName(enrollment.getClassGroup().getTerm() != null ? enrollment.getClassGroup().getTerm().getTermName() : "Current Term")
                .level(enrollment.getClassGroup() != null ? enrollment.getClassGroup().getName() : "N/A")
                .startDate(enrollment.getCreatedAt().toLocalDate())
                .endDate(LocalDate.now()) // For demo purposes
                .subjects(subjects)
                .termGpa("3.5")
                .attendanceRate("92%")
                .status(enrollment.getStatus().toString())
                .build();
    }

    private String generateStudentNumber(User student) {
        // Generate a student number based on registration date and ID
        return "SQ" + student.getCreatedAt().getYear() +
               String.format("%06d", Math.abs(student.getId().hashCode() % 1000000));
    }

    private String getCurrentLevel(List<Enrollment> enrollments) {
        return enrollments.isEmpty() ? "N/A" :
               enrollments.get(0).getClassGroup() != null ? enrollments.get(0).getClassGroup().getName() : "N/A";
    }

    private String calculateCumulativeGpa(List<Enrollment> enrollments) {
        // Simplified GPA calculation
        return "3.45";
    }

    private String calculateTotalCredits(List<Enrollment> enrollments) {
        // Simplified credit calculation
        return String.valueOf(enrollments.size() * 7);
    }

    private String determineAcademicStanding(List<Enrollment> enrollments) {
        // Simplified academic standing
        return "Good Standing";
    }
}