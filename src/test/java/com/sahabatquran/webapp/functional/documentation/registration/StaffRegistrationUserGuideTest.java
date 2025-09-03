package com.sahabatquran.webapp.functional.documentation.registration;

import com.sahabatquran.webapp.functional.documentation.BaseDocumentationTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import com.sahabatquran.webapp.functional.page.RegistrationPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

/**
 * Template-based Documentation for Staff Assign Teacher Workflow.
 * 
 * Uses template system to separate content from code for better maintainability.
 * Content loaded from: src/test/resources/documentation-templates/staff/sections.json
 */
@Slf4j
@DisplayName("Staff Assign Teacher - Template-based Documentation")
class StaffRegistrationUserGuideTest extends BaseDocumentationTest {

    @Test
    @DisplayName("Panduan Pengguna: Tahap 2 - Staff Admin Menugaskan Guru untuk Evaluasi")
    @Sql(scripts = "/sql/staff-workflow-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/staff-workflow-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void userGuide_staffAssignTeacherWorkflow() {
        log.info("📚 Menghasilkan Template-based Panduan Pengguna: Staff Admin Menugaskan Guru untuk Evaluasi");
        
        // Load workflow template
        loadWorkflowTemplate("staff");
        
        // Test data
        final String STAFF_USERNAME = "staff.admin1";
        final String STAFF_PASSWORD = "Welcome@YSQ2024";
        final String STUDENT_NAME = "TEST_ST_Ahmad";
        final String TEACHER_ID = "20000000-0000-0000-0000-000000000001";
        final String ASSIGNMENT_NOTES = "Please review this registration and evaluate the placement test";
        
        // Create page objects
        LoginPage loginPage = new LoginPage(page);
        RegistrationPage registrationPage = new RegistrationPage(page);
        
        // ===== SECTION 1: LOGIN STAFF =====
        startTemplateSection("login_staff");
        
        demonstrateTemplateAction("login_staff", "navigateToLoginPage", () -> {
            loginPage.navigateToLoginPage(getBaseUrl());
        });
        
        demonstrateTemplateAction("login_staff", "loginAsStaff", () -> {
            loginPage.login(STAFF_USERNAME, STAFF_PASSWORD);
        });
        
        explainFromTemplate("login_staff");
        endSection("Login Admin Staff");
        
        // ===== SECTION 2: ACCESS REGISTRATIONS =====
        startTemplateSection("access_registrations");
        
        demonstrateTemplateAction("access_registrations", "navigateToRegistrations", () -> {
            registrationPage.navigateToRegistrations(getBaseUrl());
        });
        
        explainFromTemplate("access_registrations");
        
        takeScreenshot("registrations_list", "Halaman daftar registrasi siswa");
        endSection("Akses Manajemen Registrasi");
        
        // ===== SECTION 3: FILTER SUBMITTED =====
        startTemplateSection("filter_submitted");
        
        demonstrateTemplateAction("filter_submitted", "filterByStatus", () -> {
            registrationPage.filterByStatus("SUBMITTED");
        });
        
        explainFromTemplate("filter_submitted");
        
        if (registrationPage.hasResults()) {
            takeScreenshot("submitted_registrations", "Daftar registrasi dengan status SUBMITTED");
        }
        
        endSection("Filter Registrasi yang Perlu Ditugaskan");
        
        // ===== SECTION 4: SEARCH REGISTRATION =====
        startTemplateSection("search_registration");
        
        demonstrateTemplateAction("search_registration", "searchByStudentName", () -> {
            registrationPage.searchByStudentName(STUDENT_NAME);
        });
        
        explainFromTemplate("search_registration");
        endSection("Pencarian Registrasi Siswa");
        
        // ===== SECTION 5: ASSIGN TEACHER =====
        startTemplateSection("assign_teacher");
        
        // Find the first available SUBMITTED registration from test data
        if (registrationPage.hasResults()) {
            String firstTestRegistrationId = registrationPage.getFirstAvailableRegistrationId();
            
            if (firstTestRegistrationId != null) {
                log.info("📝 Working with test registration ID: {}", firstTestRegistrationId);
                
                demonstrateTemplateAction("assign_teacher", "verifyRegistrationStatus", () -> {
                    registrationPage.expectRegistrationStatus(firstTestRegistrationId, "SUBMITTED");
                });
                
                demonstrateTemplateAction("assign_teacher", "selectTeacherAndNotes", () -> {
                    // Complete the entire assignment workflow
                    registrationPage.assignTeacherToRegistration(firstTestRegistrationId, TEACHER_ID, ASSIGNMENT_NOTES);
                });
                
                takeScreenshot("assign_teacher_modal", "Modal penugasan guru dengan daftar pengajar");
                
                // Wait for status update
                page.waitForTimeout(2000);
                
                takeScreenshot("assignment_success", "Registrasi berhasil ditugaskan ke guru");
                
                demonstrateAction("Verifikasi perubahan status menjadi ASSIGNED", () -> {
                    registrationPage.expectStatusAssigned(firstTestRegistrationId);
                });
            }
        }
        
        explainFromTemplate("assign_teacher");
        endSection("Menugaskan Guru untuk Evaluasi");
        
        // ===== SECTION 6: MONITOR ASSIGNMENTS =====
        startTemplateSection("monitor_assignments");
        
        demonstrateTemplateAction("monitor_assignments", "filterAssignedRegistrations", () -> {
            registrationPage.filterByStatus("ASSIGNED");
        });
        
        explainFromTemplate("monitor_assignments");
        
        takeScreenshot("assigned_registrations", "Daftar registrasi yang sudah ditugaskan ke guru");
        endSection("Monitoring Status Penugasan");
        
        // ===== SECTION 7: SEARCH FEATURES =====
        startTemplateSection("search_features");
        
        demonstrateTemplateAction("search_features", "tryVariousSearches", () -> {
            registrationPage.searchByEmail("test@example.com");
            page.waitForTimeout(1000);
            // Clear search by searching with empty string
            registrationPage.searchByStudentName("");
        });
        
        explainFromTemplate("search_features");
        
        takeScreenshot("search_and_filter", "Berbagai opsi pencarian dan filter");
        endSection("Fitur Pencarian dan Filter Lanjutan");
        
        // ===== SECTION 8: BEST PRACTICES =====
        startTemplateSection("best_practices");
        
        explainFromTemplate("best_practices");
        endSection("Best Practices untuk Staff Admin");
        
        // ===== SECTION 9: TROUBLESHOOTING =====
        startTemplateSection("troubleshooting");
        
        explainFromTemplate("troubleshooting");
        
        takeScreenshot("troubleshooting_guide", "Panduan troubleshooting untuk staff admin");
        endSection("Troubleshooting dan Solusi Masalah");
        
        log.info("📚 Template-based dokumentasi selesai untuk Staff Assign Teacher Workflow");
        log.info("   Content loaded from: documentation-templates/staff/sections.json");
        log.info("   Screenshot dan video yang menunjukkan proses penugasan guru telah dibuat");
    }
}