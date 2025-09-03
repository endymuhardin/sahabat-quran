package com.sahabatquran.webapp.functional.documentation.registration;

import com.sahabatquran.webapp.functional.documentation.BaseDocumentationTest;
import com.sahabatquran.webapp.functional.page.StudentRegistrationPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Template-based Documentation for Student Registration Workflow.
 * 
 * Uses template system to separate content from code for better maintainability.
 * Content loaded from: src/test/resources/documentation-templates/student/sections.json
 */
@Slf4j
@DisplayName("Pendaftaran Siswa - Template-based Documentation")
class StudentRegistrationUserGuideTest extends BaseDocumentationTest {

    @Test
    @DisplayName("Panduan Pengguna: Tahap 1 - Siswa Mendaftar dan Submit Rekaman")
    void userGuide_completeStudentRegistration() {
        log.info("📚 Menghasilkan Template-based Panduan Pengguna: Siswa Mendaftar dan Submit Rekaman");
        
        // Load workflow template
        loadWorkflowTemplate("student");
        
        // Create page object
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
        
        // ===== SECTION 1: ACCESS REGISTRATION PAGE =====
        startTemplateSection("access_registration");
        
        demonstrateTemplateAction("access_registration", "navigateToRegistrationPage", () -> {
            registrationPage.navigateToRegistrationPage(getBaseUrl());
        });
        
        explainFromTemplate("access_registration");
        endSection("Akses Halaman Pendaftaran");
        
        // ===== SECTION 2: PERSONAL INFORMATION =====
        startTemplateSection("personal_information");
        
        demonstrateTemplateAction("personal_information", "fillPersonalInformation", () -> {
            // Generate unique data to avoid duplicate registration errors
            long timestamp = System.currentTimeMillis();
            String uniquePhone = "0812" + String.valueOf(timestamp).substring(8);
            String uniqueEmail = "ahmad.fauzi" + timestamp + "@example.com";
            String uniqueEmergencyPhone = "0819" + String.valueOf(timestamp).substring(8);
            
            registrationPage.fillPersonalInformation(
                "Ahmad Fauzi Ramadhan", "MALE", "15032010", "Jakarta",
                uniquePhone, uniqueEmail,
                "Jl. Merdeka No. 123, RT 02/RW 05",
                "Dr. Budi Santoso", uniqueEmergencyPhone, "Orang Tua"
            );
        });
        
        explainFromTemplate("personal_information");
        
        demonstrateAction("Lanjut ke tahap berikutnya", () -> {
            registrationPage.clickNext();
        });
        
        endSection("Mengisi Informasi Pribadi dan Kontak");
        
        // ===== SECTION 3: EDUCATION INFORMATION =====
        startTemplateSection("education_information");
        
        demonstrateTemplateAction("education_information", "fillEducationInformation", () -> {
            registrationPage.fillEducationInformation("SMA/SMK", false);
        });
        
        explainFromTemplate("education_information");
        
        demonstrateAction("Lanjut ke tahap berikutnya", () -> {
            registrationPage.clickNext();
        });
        
        endSection("Mengisi Informasi Pendidikan");
        
        // ===== SECTION 4: PROGRAM SELECTION =====
        startTemplateSection("program_selection");
        
        demonstrateTemplateAction("program_selection", "selectProgram", () -> {
            registrationPage.selectFirstProgram();
        });
        
        explainFromTemplate("program_selection");
        
        demonstrateAction("Lanjut ke tahap berikutnya", () -> {
            registrationPage.clickNext();
        });
        
        endSection("Memilih Program Pembelajaran");
        
        // ===== SECTION 5: SCHEDULE PREFERENCES =====
        startTemplateSection("schedule_preferences");
        
        demonstrateTemplateAction("schedule_preferences", "fillSchedulePreferences", () -> {
            registrationPage.fillSchedulePreferences(
                "90000000-0000-0000-0000-000000000002", "1", true
            );
        });
        
        explainFromTemplate("schedule_preferences");
        
        demonstrateAction("Lanjut ke tahap berikutnya", () -> {
            registrationPage.clickNext();
        });
        
        endSection("Menentukan Preferensi Jadwal");
        
        // ===== SECTION 6: PLACEMENT TEST =====
        startTemplateSection("placement_test");
        
        demonstrateTemplateAction("placement_test", "fillPlacementTest", () -> {
            registrationPage.fillPlacementTest("https://drive.google.com/file/d/1abc123def456ghi789/view");
        });
        
        explainFromTemplate("placement_test");
        
        demonstrateTemplateAction("placement_test", "submitForm", () -> {
            registrationPage.submitForm();
        });
        
        endSection("Mengisi Link Rekaman Tes Penempatan");
        
        // ===== SECTION 7: REGISTRATION SUCCESS =====
        startTemplateSection("registration_success");
        
        // Wait for page to load after submission
        page.waitForTimeout(3000);
        
        if (page.url().contains("/register/confirmation")) {
            takeScreenshot("registration_success", "Halaman konfirmasi pendaftaran berhasil");
        } else {
            takeScreenshot("registration_form_current_state", "Status form setelah submit");
        }
        
        explainFromTemplate("registration_success");
        endSection("Konfirmasi Pendaftaran Berhasil");
        
        // ===== SECTION 8: NEXT STEPS =====
        startTemplateSection("next_steps");
        
        explainFromTemplate("next_steps");
        
        takeScreenshot("next_steps_overview", "Ringkasan timeline proses pasca pendaftaran");
        endSection("Langkah Setelah Pendaftaran");
        
        log.info("📚 Template-based dokumentasi selesai untuk Student Registration");
        log.info("   Content loaded from: documentation-templates/student/sections.json");
        log.info("   Template system memisahkan konten dari kode untuk maintenance yang lebih mudah");
    }
}