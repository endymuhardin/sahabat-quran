package com.sahabatquran.webapp.functional.documentation;

import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.TeacherRegistrationPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

/**
 * Template-based Documentation for Teacher Review Registration Workflow.
 * 
 * Uses template system to separate content from code for better maintainability.
 * Content loaded from: src/test/resources/documentation-templates/teacher/sections.json
 */
@Slf4j
@DisplayName("Teacher Review Registration - Template-based Documentation")
class TeacherRegistrationUserGuideTest extends BaseDocumentationTest {

    @Test
    @DisplayName("Panduan Pengguna: Tahap 3 - Guru Mengevaluasi Rekaman Siswa")
    @Sql(scripts = "/sql/teacher-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/teacher-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void userGuide_teacherEvaluationWorkflow() {
        log.info("📚 Menghasilkan Template-based Panduan Pengguna: Guru Mengevaluasi Rekaman Siswa");
        
        // Load workflow template
        loadWorkflowTemplate("teacher");
        
        // Test data
        final String TEACHER_USERNAME = "ustadz.ahmad";
        final String TEACHER_PASSWORD = "Welcome@YSQ2024";
        final String TEACHER_REMARKS = "Bacaan cukup baik dengan tajwid yang benar pada sebagian besar ayat. " +
                                     "Perlu perbaikan pada mad dan qalqalah. " +
                                     "Rekomendasikan untuk masuk Tahsin Level 2.";
        final String RECOMMENDED_LEVEL = "80000000-0000-0000-0000-000000000002"; // Tahsin 2
        final String PLACEMENT_RESULT = "3";
        
        // Create page objects
        LoginPage loginPage = new LoginPage(page);
        TeacherRegistrationPage teacherPage = new TeacherRegistrationPage(page);
        
        // ===== SECTION 1: LOGIN TEACHER =====
        startTemplateSection("login_teacher");
        
        demonstrateTemplateAction("login_teacher", "navigateToLoginPage", () -> {
            loginPage.navigateToLoginPage(getBaseUrl());
        });
        
        demonstrateTemplateAction("login_teacher", "loginAsTeacher", () -> {
            loginPage.login(TEACHER_USERNAME, TEACHER_PASSWORD);
        });
        
        explainFromTemplate("login_teacher");
        endSection("Login Guru/Ustadz");
        
        // ===== SECTION 2: TEACHER DASHBOARD =====
        startTemplateSection("teacher_dashboard");
        
        demonstrateTemplateAction("teacher_dashboard", "navigateToTeacherRegistrations", () -> {
            teacherPage.navigateToTeacherRegistrations(getBaseUrl());
        });
        
        explainFromTemplate("teacher_dashboard");
        
        takeScreenshot("teacher_dashboard", "Dashboard guru dengan ringkasan penugasan");
        
        // Display dashboard summary
        int totalAssignments = teacherPage.getTotalAssignments();
        int pendingCount = teacherPage.getPendingCount();
        int inReviewCount = teacherPage.getInReviewCount();
        int completedCount = teacherPage.getCompletedCount();
        
        explain(String.format("Dashboard menampilkan: %d total penugasan, %d pending, %d in review, %d completed", 
            totalAssignments, pendingCount, inReviewCount, completedCount));
        
        endSection("Dashboard Guru dan Daftar Penugasan");
        
        // ===== SECTION 3: SELECT REGISTRATION =====
        startTemplateSection("select_registration");
        
        // Find first available assigned registration from test data
        String firstAssignedRegistration = teacherPage.getFirstAssignedRegistrationId();
        
        if (firstAssignedRegistration != null && teacherPage.isAssignmentVisible(firstAssignedRegistration)) {
            log.info("📝 Working with test assignment: {}", firstAssignedRegistration);
            
            demonstrateTemplateAction("select_registration", "verifyAssignmentVisible", () -> {
                teacherPage.expectAssignmentVisible(firstAssignedRegistration);
            });
            
            demonstrateTemplateAction("select_registration", "startReview", () -> {
                teacherPage.startReview(firstAssignedRegistration);
            });
            
            takeScreenshot("evaluation_page", "Halaman evaluasi registrasi siswa");
        }
        
        explainFromTemplate("select_registration");
        endSection("Memilih Registrasi untuk Evaluasi");
        
        // ===== SECTION 4: REVIEW STUDENT INFO =====
        startTemplateSection("review_student_info");
        
        if (firstAssignedRegistration != null && teacherPage.isOnReviewPage()) {
            // Display student information
            String studentName = teacherPage.getStudentName();
            String studentEmail = teacherPage.getStudentEmail();
            String studentProgram = teacherPage.getStudentProgram();
            
            explain(String.format("**Informasi Siswa yang Sedang Dievaluasi:**"));
            explain(String.format("- Nama: %s", studentName.isEmpty() ? "[Nama siswa]" : studentName));
            explain(String.format("- Email: %s", studentEmail.isEmpty() ? "[Email siswa]" : studentEmail));
            explain(String.format("- Program: %s", studentProgram.isEmpty() ? "[Program yang dipilih]" : studentProgram));
            
            takeScreenshot("student_information", "Informasi lengkap siswa yang akan dievaluasi");
        }
        
        explainFromTemplate("review_student_info");
        endSection("Review Informasi Siswa");
        
        // ===== SECTION 5: EVALUATE RECORDING =====
        startTemplateSection("evaluate_recording");
        
        if (firstAssignedRegistration != null && teacherPage.isOnReviewPage()) {
            if (teacherPage.isRecordingAvailable()) {
                explain("**Rekaman Tersedia**: Siswa telah mengirimkan rekaman bacaan yang dapat diputar " +
                        "langsung dari sistem atau diakses melalui link eksternal (Google Drive).");
                
                takeScreenshot("recording_section", "Bagian rekaman bacaan dengan kontrol audio");
            }
        }
        
        explainFromTemplate("evaluate_recording");
        endSection("Evaluasi Rekaman Bacaan Quran");
        
        // ===== SECTION 6: FILL EVALUATION =====
        startTemplateSection("fill_evaluation");
        
        if (firstAssignedRegistration != null && teacherPage.isOnReviewPage()) {
            
            demonstrateTemplateAction("fill_evaluation", "completeEvaluation", () -> {
                teacherPage.completeReviewWithPlacementTest(
                    firstAssignedRegistration,
                    TEACHER_REMARKS,
                    RECOMMENDED_LEVEL,
                    PLACEMENT_RESULT,
                    "Evaluasi lengkap dengan rekomendasi level"
                );
            });
            
            explain("**Contoh Catatan Evaluasi:**");
            explain("\"" + TEACHER_REMARKS + "\"");
            
            takeScreenshot("evaluation_form", "Form evaluasi yang telah diisi lengkap");
            
            demonstrateTemplateAction("fill_evaluation", "submitEvaluation", () -> {
                page.waitForTimeout(2000); // Allow form to be processed
            });
        }
        
        explainFromTemplate("fill_evaluation");
        endSection("Pengisian Hasil Evaluasi");
        
        // ===== SECTION 7: EVALUATION COMPLETED =====
        startTemplateSection("evaluation_completed");
        
        if (teacherPage.isOnTeacherRegistrationsPage() || page.url().contains("registrations")) {
            takeScreenshot("evaluation_completed", "Konfirmasi evaluasi berhasil diselesaikan");
        }
        
        explainFromTemplate("evaluation_completed");
        endSection("Konfirmasi Evaluasi Selesai");
        
        // ===== SECTION 8: ADDITIONAL FEATURES =====
        startTemplateSection("additional_features");
        
        explainFromTemplate("additional_features");
        
        takeScreenshot("teacher_features", "Fitur-fitur tambahan untuk guru");
        endSection("Fitur Tambahan untuk Guru");
        
        // ===== SECTION 9: EVALUATION CRITERIA =====
        startTemplateSection("evaluation_criteria");
        
        explainFromTemplate("evaluation_criteria");
        
        takeScreenshot("evaluation_criteria", "Kriteria dan panduan evaluasi untuk guru");
        endSection("Panduan Evaluasi dan Kriteria Penilaian");
        
        // ===== SECTION 10: TROUBLESHOOTING =====
        startTemplateSection("troubleshooting");
        
        explainFromTemplate("troubleshooting");
        
        takeScreenshot("troubleshooting_teacher", "Panduan troubleshooting dan bantuan untuk guru");
        endSection("Troubleshooting dan Bantuan");
        
        log.info("📚 Template-based dokumentasi selesai untuk Teacher Evaluation Workflow");
        log.info("   Content loaded from: documentation-templates/teacher/sections.json");
        log.info("   Screenshot dan video yang menunjukkan proses evaluasi lengkap telah dibuat");
    }
}