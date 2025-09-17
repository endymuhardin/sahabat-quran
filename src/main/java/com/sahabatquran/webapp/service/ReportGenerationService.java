package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.dto.*;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating student reports with validation and data completeness checking.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReportGenerationService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final AcademicTermRepository academicTermRepository;
    private final ClassGroupRepository classGroupRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * Validates student enrollment and data completeness for report generation.
     */
    public ReportValidationResultDto validateReportGeneration(StudentReportRequestDto request) {
        log.info("Validating report generation for student: {} in term: {}", request.getStudentId(), request.getTermId());

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        AcademicTerm term = academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        ReportValidationResultDto.ReportValidationResultDtoBuilder resultBuilder = ReportValidationResultDto.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .validationErrors(new ArrayList<>())
                .warnings(new ArrayList<>());

        // Check enrollment
        boolean isEnrolled = checkStudentEnrollment(student, term, resultBuilder);
        resultBuilder.isEnrolledInTerm(isEnrolled);

        if (!isEnrolled) {
            resultBuilder.isValid(false);
            List<String> availableTerms = getAvailableTermsForStudent(student);
            resultBuilder.availableTerms(availableTerms);
            if (!availableTerms.isEmpty()) {
                resultBuilder.suggestedTerm(availableTerms.get(0)); // Most recent
            }
        }

        // Check data completeness
        ReportValidationResultDto.DataCompletenessDto dataCompleteness = checkDataCompleteness(student, term);
        resultBuilder.dataCompleteness(dataCompleteness);

        boolean isValid = isEnrolled && (dataCompleteness.isHasCompleteData() || request.isGeneratePartialReport());
        resultBuilder.isValid(isValid);

        ReportValidationResultDto result = resultBuilder.build();
        log.info("Validation result for student {}: valid={}, enrolled={}, complete={}",
                student.getFullName(), result.isValid(), isEnrolled, dataCompleteness.isHasCompleteData());

        return result;
    }

    /**
     * Generates a student report.
     */
    public StudentReportResponseDto generateStudentReport(StudentReportRequestDto request) {
        log.info("Generating report for student: {} in term: {}", request.getStudentId(), request.getTermId());

        // Validate first
        ReportValidationResultDto validation = validateReportGeneration(request);
        if (!validation.isValid() && !request.isGeneratePartialReport()) {
            throw new IllegalStateException("Cannot generate report: validation failed");
        }

        User student = userRepository.findById(request.getStudentId()).orElseThrow();
        AcademicTerm term = academicTermRepository.findById(request.getTermId()).orElseThrow();

        // Get enrollment to find class and level
        Enrollment enrollment = enrollmentRepository.findByStudentAndClassGroup_Term(student, term)
                .orElse(null);

        StudentReportResponseDto.StudentReportResponseDtoBuilder reportBuilder = StudentReportResponseDto.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .termName(term.getTermName())
                .reportType(request.getReportType())
                .generatedDate(LocalDate.now())
                .isPartialReport(!validation.getDataCompleteness().isHasCompleteData())
                .hasDisclaimers(request.isIncludeDisclaimers());

        if (enrollment != null) {
            ClassGroup classGroup = enrollment.getClassGroup();
            reportBuilder.className(classGroup.getName())
                        .level(classGroup.getLevel() != null ? classGroup.getLevel().getName() : "Unknown");
        }

        // Get assessments and calculate grades
        List<StudentAssessment> assessments = studentAssessmentRepository.findByStudentAndTerm(student, term);
        List<StudentReportResponseDto.AssessmentComponentDto> assessmentComponents = buildAssessmentComponents(assessments);
        reportBuilder.assessmentComponents(assessmentComponents);

        // Calculate final grade
        BigDecimal finalGrade = calculateFinalGrade(assessmentComponents);
        reportBuilder.finalGrade(finalGrade)
                    .finalLetterGrade(convertToLetterGrade(finalGrade));

        // Get attendance
        BigDecimal attendanceRate = calculateAttendanceRate(student, term);
        reportBuilder.attendanceRate(attendanceRate);

        // Add missing data warnings if applicable
        if (validation.getDataCompleteness() != null) {
            reportBuilder.hasCompleteData(validation.getDataCompleteness().isHasCompleteData())
                        .missingDataWarnings(validation.getDataCompleteness().getMissingAssessments())
                        .incompleteDataFields(validation.getDataCompleteness().getIncompleteFields());
        }

        // Get teacher feedback (simplified - could be from assessments or separate feedback table)
        String teacherFeedback = getTeacherFeedback(student, term);
        reportBuilder.teacherFeedback(teacherFeedback);

        StudentReportResponseDto report = reportBuilder.build();
        log.info("Generated report for student {} with final grade: {}", student.getFullName(), finalGrade);

        return report;
    }

    /**
     * Validates bulk report generation and categorizes students by data quality.
     */
    public Map<String, List<ReportValidationResultDto>> validateBulkReportGeneration(BulkReportRequestDto request) {
        log.info("Validating bulk report generation for term: {}", request.getTermId());

        AcademicTerm term = academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new IllegalArgumentException("Term not found"));

        List<User> students;
        if (request.getClassId() != null) {
            // Get students from specific class
            ClassGroup classGroup = classGroupRepository.findById(request.getClassId())
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));
            students = enrollmentRepository.findByClassGroup(classGroup)
                    .stream()
                    .map(Enrollment::getStudent)
                    .collect(Collectors.toList());
        } else if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            // Get specific students
            students = userRepository.findAllById(request.getStudentIds());
        } else {
            // Get all students in the term
            students = enrollmentRepository.findByClassGroup_Term(term)
                    .stream()
                    .map(Enrollment::getStudent)
                    .collect(Collectors.toList());
        }

        Map<String, List<ReportValidationResultDto>> categorizedResults = new HashMap<>();
        categorizedResults.put("COMPLETE", new ArrayList<>());
        categorizedResults.put("INCOMPLETE", new ArrayList<>());
        categorizedResults.put("MISSING", new ArrayList<>());

        for (User student : students) {
            StudentReportRequestDto individualRequest = new StudentReportRequestDto();
            individualRequest.setStudentId(student.getId());
            individualRequest.setTermId(request.getTermId());
            individualRequest.setReportType(request.getReportType());

            ReportValidationResultDto validation = validateReportGeneration(individualRequest);

            String category;
            if (validation.getDataCompleteness().isHasCompleteData()) {
                category = "COMPLETE";
            } else if (validation.getDataCompleteness().getCompletionPercentage() > 50) {
                category = "INCOMPLETE";
            } else {
                category = "MISSING";
            }

            categorizedResults.get(category).add(validation);
        }

        log.info("Bulk validation completed. Complete: {}, Incomplete: {}, Missing: {}",
                categorizedResults.get("COMPLETE").size(),
                categorizedResults.get("INCOMPLETE").size(),
                categorizedResults.get("MISSING").size());

        return categorizedResults;
    }

    private boolean checkStudentEnrollment(User student, AcademicTerm term,
                                         ReportValidationResultDto.ReportValidationResultDtoBuilder resultBuilder) {
        log.info("Checking enrollment for student {} (ID: {}) in term {} (ID: {})",
                student.getFullName(), student.getId(), term.getTermName(), term.getId());

        Optional<Enrollment> enrollment = enrollmentRepository.findByStudentAndClassGroup_Term(student, term);
        log.info("Enrollment found: {}", enrollment.isPresent());

        if (enrollment.isEmpty()) {
            // Check if student has any assessment data for this term, which implies enrollment
            boolean hasAssessmentData = studentAssessmentRepository.existsByStudentIdAndTermId(student.getId(), term.getId());
            log.info("Assessment data found for student {} in term {}: {}", student.getId(), term.getId(), hasAssessmentData);

            if (hasAssessmentData) {
                resultBuilder.warnings(Collections.singletonList("Student has assessment data but no formal enrollment record"));
                return true; // Consider enrolled if assessment data exists
            }

            resultBuilder.validationErrors(Collections.singletonList("Student is not enrolled in the selected term"));
            return false;
        }

        if (enrollment.get().getStatus() != Enrollment.EnrollmentStatus.ACTIVE) {
            resultBuilder.warnings(Collections.singletonList("Student enrollment status is " + enrollment.get().getStatus()));
        }

        return true;
    }

    private List<String> getAvailableTermsForStudent(User student) {
        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(enrollment -> enrollment.getClassGroup().getTerm().getTermName())
                .distinct()
                .sorted(Collections.reverseOrder()) // Most recent first
                .collect(Collectors.toList());
    }

    private ReportValidationResultDto.DataCompletenessDto checkDataCompleteness(User student, AcademicTerm term) {
        List<StudentAssessment> assessments = studentAssessmentRepository.findByStudentAndTerm(student, term);

        List<String> requiredAssessments = Arrays.asList("PLACEMENT", "MIDTERM", "FINAL");
        List<String> missingAssessments = new ArrayList<>();
        List<String> incompleteFields = new ArrayList<>();

        Set<String> availableAssessments = assessments.stream()
                .map(a -> a.getAssessmentType().name())
                .collect(Collectors.toSet());

        for (String required : requiredAssessments) {
            if (!availableAssessments.contains(required)) {
                missingAssessments.add(required);
            }
        }

        // Check for incomplete assessment data
        for (StudentAssessment assessment : assessments) {
            if (assessment.getAssessmentScore() == null && assessment.getAssessmentGrade() == null) {
                incompleteFields.add(assessment.getAssessmentType().name() + " score/grade");
            }
        }

        int totalRequired = requiredAssessments.size();
        int available = totalRequired - missingAssessments.size();
        int completionPercentage = totalRequired > 0 ? (available * 100 / totalRequired) : 0;

        String completionStatus;
        if (completionPercentage == 100 && incompleteFields.isEmpty()) {
            completionStatus = "COMPLETE";
        } else if (completionPercentage >= 50) {
            completionStatus = "PARTIAL";
        } else {
            completionStatus = "MISSING";
        }

        return ReportValidationResultDto.DataCompletenessDto.builder()
                .hasCompleteData(missingAssessments.isEmpty() && incompleteFields.isEmpty())
                .missingAssessments(missingAssessments)
                .incompleteFields(incompleteFields)
                .completionPercentage(completionPercentage)
                .completionStatus(completionStatus)
                .build();
    }

    private List<StudentReportResponseDto.AssessmentComponentDto> buildAssessmentComponents(List<StudentAssessment> assessments) {
        Map<String, BigDecimal> weights = Map.of(
                "PLACEMENT", BigDecimal.valueOf(20),
                "MIDTERM", BigDecimal.valueOf(30),
                "FINAL", BigDecimal.valueOf(30)
        );

        List<String> requiredAssessments = Arrays.asList("PLACEMENT", "MIDTERM", "FINAL");
        List<StudentReportResponseDto.AssessmentComponentDto> components = new ArrayList<>();

        for (String assessmentType : requiredAssessments) {
            Optional<StudentAssessment> assessment = assessments.stream()
                    .filter(a -> a.getAssessmentType().name().equals(assessmentType))
                    .findFirst();

            StudentReportResponseDto.AssessmentComponentDto.AssessmentComponentDtoBuilder componentBuilder =
                    StudentReportResponseDto.AssessmentComponentDto.builder()
                    .componentName(assessmentType)
                    .weight(weights.get(assessmentType))
                    .maxScore(BigDecimal.valueOf(100));

            if (assessment.isPresent()) {
                StudentAssessment a = assessment.get();
                componentBuilder.score(a.getAssessmentScore() != null ? a.getAssessmentScore() : BigDecimal.ZERO)
                              .letterGrade(a.getAssessmentGrade())
                              .isMissing(false);
            } else {
                componentBuilder.score(BigDecimal.ZERO)
                              .letterGrade("N/A")
                              .isMissing(true);
            }

            components.add(componentBuilder.build());
        }

        return components;
    }

    private BigDecimal calculateFinalGrade(List<StudentReportResponseDto.AssessmentComponentDto> components) {
        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (StudentReportResponseDto.AssessmentComponentDto component : components) {
            if (!component.isMissing() && component.getScore() != null) {
                BigDecimal weightedScore = component.getScore()
                        .multiply(component.getWeight())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalWeightedScore = totalWeightedScore.add(weightedScore);
                totalWeight = totalWeight.add(component.getWeight());
            }
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalWeightedScore.multiply(BigDecimal.valueOf(100))
                .divide(totalWeight, 2, RoundingMode.HALF_UP);
    }

    private String convertToLetterGrade(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) return "A";
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "B";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "C";
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) return "D";
        return "F";
    }

    private BigDecimal calculateAttendanceRate(User student, AcademicTerm term) {
        // Simplified attendance calculation
        // In a real implementation, you would query attendance records
        return BigDecimal.valueOf(92.0); // Mock attendance rate
    }

    private String getTeacherFeedback(User student, AcademicTerm term) {
        // Simplified teacher feedback
        // In a real implementation, you would query feedback records
        return "Menunjukkan kemajuan konsisten dalam hafalan. Perlu lebih fokus pada tajwid.";
    }
}