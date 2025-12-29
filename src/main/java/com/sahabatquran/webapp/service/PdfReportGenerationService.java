package com.sahabatquran.webapp.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.sahabatquran.webapp.entity.*;
import com.sahabatquran.webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating actual PDF reports using iText
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfReportGenerationService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassGroupRepository classGroupRepository;

    @Value("${app.reports.output-directory:/tmp/reports}")
    private String reportsOutputDirectory;

    @Transactional(readOnly = true)
    public String generateStudentReport(UUID studentId, UUID termId, String reportType) throws IOException {
        log.info("Generating PDF report for student: {} in term: {}", studentId, termId);

        // Fetch student data
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        // Create reports directory structure to match what ReportOrchestrationService expects
        File reportsDir = new File(reportsOutputDirectory, "students/" + studentId.toString() + "/" + termId.toString());
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Use fixed filename that matches what ReportOrchestrationService looks for
        String fileName = "report-card.pdf";
        String filePath = reportsDir.getAbsolutePath() + "/" + fileName;

        // Create PDF
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont italicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            // Add header
            document.add(new Paragraph("STUDENT ACADEMIC REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setFont(boldFont));

            document.add(new Paragraph("\n"));

            // Student Information
            document.add(new Paragraph("Student Information").setFontSize(14).setFont(boldFont));
            Table studentTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100));

            studentTable.addCell(new Cell().add(new Paragraph("Student ID:")));
            studentTable.addCell(new Cell().add(new Paragraph(student.getUsername())));
            studentTable.addCell(new Cell().add(new Paragraph("Full Name:")));
            studentTable.addCell(new Cell().add(new Paragraph(student.getFullName())));
            studentTable.addCell(new Cell().add(new Paragraph("Email:")));
            studentTable.addCell(new Cell().add(new Paragraph(student.getEmail())));

            document.add(studentTable);
            document.add(new Paragraph("\n"));

            // Enrollment Information
            List<Enrollment> enrollments = enrollmentRepository.findByStudentAndTerm(studentId, termId);
            if (!enrollments.isEmpty()) {
                document.add(new Paragraph("Enrollment Information").setFontSize(14).setFont(boldFont));

                Table enrollmentTable = new Table(UnitValue.createPercentArray(new float[]{40, 30, 30}))
                        .setWidth(UnitValue.createPercentValue(100));

                enrollmentTable.addHeaderCell(new Cell().add(new Paragraph("Class").setFont(boldFont)));
                enrollmentTable.addHeaderCell(new Cell().add(new Paragraph("Status").setFont(boldFont)));
                enrollmentTable.addHeaderCell(new Cell().add(new Paragraph("Enrolled Date").setFont(boldFont)));

                for (Enrollment enrollment : enrollments) {
                    enrollmentTable.addCell(new Cell().add(new Paragraph(enrollment.getClassGroup().getName())));
                    enrollmentTable.addCell(new Cell().add(new Paragraph(enrollment.getStatus().toString())));
                    enrollmentTable.addCell(new Cell().add(new Paragraph(
                            enrollment.getEnrollmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
                }

                document.add(enrollmentTable);
                document.add(new Paragraph("\n"));
            }

            // Assessment Results
            List<StudentAssessment> assessments = studentAssessmentRepository.findByStudentIdAndTermId(studentId, termId);
            if (!assessments.isEmpty()) {
                document.add(new Paragraph("Assessment Results").setFontSize(14).setFont(boldFont));

                Table assessmentTable = new Table(UnitValue.createPercentArray(new float[]{30, 25, 20, 25}))
                        .setWidth(UnitValue.createPercentValue(100));

                assessmentTable.addHeaderCell(new Cell().add(new Paragraph("Assessment Type").setFont(boldFont)));
                assessmentTable.addHeaderCell(new Cell().add(new Paragraph("Score").setFont(boldFont)));
                assessmentTable.addHeaderCell(new Cell().add(new Paragraph("Grade").setFont(boldFont)));
                assessmentTable.addHeaderCell(new Cell().add(new Paragraph("Date").setFont(boldFont)));

                for (StudentAssessment assessment : assessments) {
                    assessmentTable.addCell(new Cell().add(new Paragraph(assessment.getAssessmentType().toString())));
                    assessmentTable.addCell(new Cell().add(new Paragraph(
                            assessment.getAssessmentScore() != null ? assessment.getAssessmentScore().toString() : "N/A")));
                    assessmentTable.addCell(new Cell().add(new Paragraph(
                            assessment.getAssessmentGrade() != null ? assessment.getAssessmentGrade() : "N/A")));
                    assessmentTable.addCell(new Cell().add(new Paragraph(
                            assessment.getAssessmentDate() != null ?
                            assessment.getAssessmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A")));
                }

                document.add(assessmentTable);
                document.add(new Paragraph("\n"));
            }

            // Add generation timestamp
            document.add(new Paragraph("Report generated on: " +
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFont(italicFont));

        } finally {
            document.close();
        }

        log.info("Successfully generated PDF report: {}", filePath);
        return filePath;
    }

    public String generateClassSummaryReport(UUID classId, UUID termId) throws IOException {
        log.info("Generating class summary report for class: {} in term: {}", classId, termId);

        ClassGroup classGroup = classGroupRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + classId));

        // Create reports directory
        File reportsDir = new File(reportsOutputDirectory);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        String fileName = String.format("class_summary_%s_%s_%d.pdf",
                classId.toString().substring(0, 8),
                termId.toString().substring(0, 8),
                System.currentTimeMillis());
        String filePath = reportsOutputDirectory + "/" + fileName;

        // Create PDF
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont italicFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            document.add(new Paragraph("CLASS SUMMARY REPORT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setFont(boldFont));

            document.add(new Paragraph("\n"));

            // Class Information
            document.add(new Paragraph("Class Information").setFontSize(14).setFont(boldFont));
            Table classTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .setWidth(UnitValue.createPercentValue(100));

            classTable.addCell(new Cell().add(new Paragraph("Class Name:")));
            classTable.addCell(new Cell().add(new Paragraph(classGroup.getName())));
            classTable.addCell(new Cell().add(new Paragraph("Capacity:")));
            classTable.addCell(new Cell().add(new Paragraph(String.valueOf(classGroup.getCapacity()))));

            document.add(classTable);
            document.add(new Paragraph("\n"));

            // Student Statistics
            List<Enrollment> enrollments = enrollmentRepository.findByClassGroupId(classId);
            document.add(new Paragraph("Enrollment Statistics").setFontSize(14).setFont(boldFont));

            Table statsTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));

            long activeEnrollments = enrollments.stream()
                    .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                    .count();

            statsTable.addCell(new Cell().add(new Paragraph("Total Enrolled Students:")));
            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(enrollments.size()))));
            statsTable.addCell(new Cell().add(new Paragraph("Active Students:")));
            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(activeEnrollments))));

            document.add(statsTable);

            document.add(new Paragraph("Report generated on: " +
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setFont(italicFont));

        } finally {
            document.close();
        }

        log.info("Successfully generated class summary PDF: {}", filePath);
        return filePath;
    }
}