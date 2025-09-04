package com.sahabatquran.webapp.functional.documentation.termpreparation;

import com.sahabatquran.webapp.functional.documentation.BaseDocumentationTest;
import com.sahabatquran.webapp.functional.page.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;

/**
 * Documentation generator for Term Preparation Workflow.
 * 
 * Generates comprehensive Indonesian user manual for the complete
 * academic term preparation process including:
 * - Phase 1: Assessment Foundation
 * - Phase 2: Level Distribution Analysis
 * - Phase 3: Teacher Availability Collection
 * - Phase 4: Management Level Assignment
 * - Phase 5: Class Generation & Refinement
 * - Phase 6: Final Review & Go-Live
 */
@Slf4j
@DisplayName("Term Preparation Workflow - Documentation Generator")
class TermPreparationDocumentationTest extends BaseDocumentationTest {

    @Test
    @DisplayName("Panduan Lengkap: Workflow Persiapan Semester")
    @Sql(scripts = "/sql/term-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/term-preparation-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void userGuide_completeTermPreparationWorkflow() {
        log.info("📚 Menghasilkan Panduan Lengkap: Workflow Persiapan Semester");
        
        // Test data
        final String ACADEMIC_ADMIN_USERNAME = "academic.admin1";
        final String MANAGEMENT_USERNAME = "management.director";
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String PASSWORD = "Welcome@YSQ2024";
        
        // Create page objects
        LoginPage loginPage = new LoginPage(page);
        
        // ===== SECTION 1: LOGIN AS ACADEMIC ADMIN =====
        startSection("Login sebagai Admin Akademik", 
            "Admin akademik memulai proses persiapan semester dengan login ke sistem");
        
        demonstrateAction("Navigasi ke halaman login", () -> {
            loginPage.navigateToLoginPage(getBaseUrl());
        });
        
        demonstrateAction("Login sebagai Admin Akademik", () -> {
            loginPage.login(ACADEMIC_ADMIN_USERNAME, PASSWORD);
        });
        
        explain("Admin akademik memiliki akses penuh untuk mengelola persiapan semester");
        explain("Dashboard akan menampilkan menu khusus Academic Planning");
        
        endSection("Login sebagai Admin Akademik");
        
        // ===== PHASE 1: ASSESSMENT FOUNDATION =====
        startSection("Fase 1: Fondasi Assessment", 
            "Memantau dan memvalidasi kelengkapan data assessment siswa");
        
        demonstrateAction("Navigasi ke Assessment Foundation", () -> {
            page.navigate(getBaseUrl() + "/academic/assessment-foundation");
            waitForStability();
        });
        
        explain("Assessment Foundation menampilkan status lengkap dari:");
        explain("- Siswa Baru: Status placement test (sudah/belum dilakukan)");
        explain("- Siswa Lama: Status hasil ujian semester sebelumnya");
        explain("- Overall Readiness: Persentase kesiapan untuk penempatan kelas");
        
        takeScreenshot("assessment_overview", "Dashboard Assessment Foundation dengan statistik lengkap");
        
        demonstrateAction("Filter siswa yang belum melakukan assessment", () -> {
            Locator filterSelect = page.locator("#assessmentStatusFilter");
            if (filterSelect.isVisible()) {
                filterSelect.selectOption("PENDING");
            }
        });
        
        explain("Admin dapat mengidentifikasi siswa yang perlu segera dijadwalkan untuk assessment");
        explain("Sistem otomatis menghitung level recommendation berdasarkan hasil assessment");
        
        takeScreenshot("pending_assessments", "Daftar siswa yang perlu dijadwalkan assessment");
        
        endSection("Fase 1: Fondasi Assessment");
        
        // ===== PHASE 2: LEVEL DISTRIBUTION ANALYSIS =====
        startSection("Fase 2: Analisis Distribusi Level", 
            "Menganalisis distribusi siswa per level untuk perencanaan kelas");
        
        demonstrateAction("Navigasi ke Level Distribution", () -> {
            page.navigate(getBaseUrl() + "/academic/level-distribution");
            waitForStability();
        });
        
        explain("Level Distribution menampilkan:");
        explain("- Distribusi siswa per level program (Tahsin 1, 2, 3, Tahfidz)");
        explain("- Breakdown siswa baru vs siswa lama per level");
        explain("- Proyeksi kebutuhan kelas berdasarkan kapasitas standar (7-10 siswa)");
        
        takeScreenshot("level_distribution_chart", "Grafik distribusi siswa per level");
        
        demonstrateAction("Lihat detail level Tahsin 1", () -> {
            Locator tahsin1Card = page.locator(".level-card[data-level='Tahsin 1']");
            if (tahsin1Card.isVisible()) {
                tahsin1Card.click();
                page.waitForTimeout(1000);
            }
        });
        
        explain("Detail per level menunjukkan:");
        explain("- Jumlah siswa baru dari placement test");
        explain("- Jumlah siswa lama yang mengulang");
        explain("- Estimasi jumlah kelas yang diperlukan");
        explain("- Rekomendasi tipe guru (foundation specialist, remedial expert)");
        
        takeScreenshot("tahsin1_details", "Detail distribusi untuk level Tahsin 1");
        
        endSection("Fase 2: Analisis Distribusi Level");
        
        // ===== PHASE 3: TEACHER AVAILABILITY COLLECTION =====
        startSection("Fase 3: Pengumpulan Ketersediaan Guru", 
            "Mengumpulkan dan memantau jadwal ketersediaan guru");
        
        // First show admin monitoring view
        demonstrateAction("Navigasi ke Teacher Availability (Admin View)", () -> {
            page.navigate(getBaseUrl() + "/academic/availability-monitoring");
            waitForStability();
        });
        
        explain("Admin dapat memantau status submission ketersediaan dari semua guru");
        explain("Sistem menampilkan:");
        explain("- Total guru yang sudah submit ketersediaan");
        explain("- Guru yang belum submit (dengan reminder otomatis)");
        explain("- Matrix ketersediaan agregat untuk perencanaan");
        
        takeScreenshot("availability_monitoring", "Dashboard monitoring ketersediaan guru");
        
        // Now show instructor submission view
        demonstrateAction("Logout dan login sebagai Instructor", () -> {
            page.locator("#user-menu-button").click();
            page.locator("#logout-button").click();
            page.waitForTimeout(1000);
            loginPage.login(INSTRUCTOR_USERNAME, PASSWORD);
        });
        
        demonstrateAction("Instructor mengisi ketersediaan jadwal", () -> {
            page.navigate(getBaseUrl() + "/instructor/availability-submission");
            waitForStability();
        });
        
        explain("Instructor mengisi matrix ketersediaan 7x5:");
        explain("- 7 hari (Senin-Minggu)");
        explain("- 5 sesi waktu per hari (Pagi Awal, Pagi, Siang, Sore, Malam)");
        explain("- Maksimal kelas per minggu dapat disesuaikan");
        
        demonstrateAction("Pilih slot ketersediaan", () -> {
            // Demo selecting some availability slots
            Locator mondayMorning = page.locator("input[data-day='1'][data-session='SESI_2']");
            if (mondayMorning.isVisible()) {
                mondayMorning.check();
            }
            
            Locator wednesdayEvening = page.locator("input[data-day='3'][data-session='SESI_7']");
            if (wednesdayEvening.isVisible()) {
                wednesdayEvening.check();
            }
            
            Locator fridayMorning = page.locator("input[data-day='5'][data-session='SESI_1']");
            if (fridayMorning.isVisible()) {
                fridayMorning.check();
            }
        });
        
        takeScreenshot("instructor_availability_form", "Form pengisian ketersediaan guru");
        
        demonstrateAction("Submit ketersediaan", () -> {
            Locator submitBtn = page.locator("button:has-text('Submit Ketersediaan')");
            if (submitBtn.isVisible()) {
                submitBtn.click();
                page.waitForTimeout(2000);
            }
        });
        
        explain("Setelah submit, ketersediaan guru akan masuk ke sistem untuk proses penempatan");
        
        endSection("Fase 3: Pengumpulan Ketersediaan Guru");
        
        // ===== PHASE 4: MANAGEMENT LEVEL ASSIGNMENT =====
        startSection("Fase 4: Penugasan Level oleh Management", 
            "Management menentukan level dan kompetensi yang dapat diajar setiap guru");
        
        demonstrateAction("Login sebagai Management", () -> {
            page.locator("#user-menu-button").click();
            page.locator("#logout-button").click();
            page.waitForTimeout(1000);
            loginPage.login(MANAGEMENT_USERNAME, PASSWORD);
        });
        
        demonstrateAction("Navigasi ke Teacher Level Assignments", () -> {
            page.navigate(getBaseUrl() + "/management/teacher-level-assignments");
            waitForStability();
        });
        
        explain("Management menentukan:");
        explain("- Level yang dapat diajar setiap guru");
        explain("- Tingkat kompetensi (Junior, Senior, Expert)");
        explain("- Spesialisasi (Foundation, Remedial, Advanced, Mixed)");
        explain("- Maksimal kelas per level");
        
        takeScreenshot("teacher_level_assignment", "Interface penugasan level guru oleh management");
        
        demonstrateAction("Assign guru ke level Tahsin 1", () -> {
            // Simulate drag and drop or assignment action
            Locator teacherCard = page.locator(".teacher-card").first();
            Locator tahsin1Zone = page.locator(".level-zone[data-level='Tahsin 1']");
            
            if (teacherCard.isVisible() && tahsin1Zone.isVisible()) {
                // In real implementation, would use drag and drop
                teacherCard.click();
                page.waitForTimeout(500);
                tahsin1Zone.click();
            }
        });
        
        explain("Sistem menggunakan assignment ini untuk:");
        explain("- Matching guru dengan kelas sesuai kompetensi");
        explain("- Distribusi workload yang seimbang");
        explain("- Optimasi kualitas pembelajaran");
        
        takeScreenshot("level_assignment_complete", "Guru sudah di-assign ke level yang sesuai");
        
        endSection("Fase 4: Penugasan Level oleh Management");
        
        // ===== PHASE 5: CLASS GENERATION & REFINEMENT =====
        startSection("Fase 5: Generasi dan Penyesuaian Kelas", 
            "Sistem menghasilkan proposal kelas otomatis yang dapat disesuaikan manual");
        
        demonstrateAction("Login kembali sebagai Academic Admin", () -> {
            page.locator("#user-menu-button").click();
            page.locator("#logout-button").click();
            page.waitForTimeout(1000);
            loginPage.login(ACADEMIC_ADMIN_USERNAME, PASSWORD);
        });
        
        demonstrateAction("Navigasi ke Class Generation", () -> {
            page.navigate(getBaseUrl() + "/academic/generation-readiness");
            waitForStability();
        });
        
        explain("Sistem melakukan validasi prerequisite:");
        explain("- ✅ Assessment data lengkap (>80%)");
        explain("- ✅ Teacher availability submitted (100%)");
        explain("- ✅ Management level assignments complete");
        explain("- ✅ System configuration validated");
        
        takeScreenshot("generation_readiness", "Validasi kesiapan sebelum generasi kelas");
        
        demonstrateAction("Generate kelas otomatis", () -> {
            Locator generateBtn = page.locator("button:has-text('Generate Classes')");
            if (generateBtn.isVisible()) {
                generateBtn.click();
                page.waitForTimeout(3000); // Wait for generation
            }
        });
        
        explain("Algoritma generasi kelas mempertimbangkan:");
        explain("- Ukuran kelas optimal (7-10 siswa, dapat dikonfigurasi)");
        explain("- Integrasi siswa baru dan lama (target 40:60)");
        explain("- Ketersediaan jadwal guru");
        explain("- Minimalisasi konflik jadwal");
        explain("- Optimasi penggunaan ruangan");
        
        takeScreenshot("generated_classes", "Hasil generasi kelas otomatis");
        
        demonstrateAction("Navigasi ke Class Refinement", () -> {
            page.navigate(getBaseUrl() + "/academic/class-refinement");
            waitForStability();
        });
        
        explain("Admin dapat melakukan penyesuaian manual:");
        explain("- Drag & drop siswa antar kelas");
        explain("- Pindahkan jadwal kelas");
        explain("- Override ukuran kelas dengan justifikasi");
        explain("- Resolve konflik jadwal");
        
        demonstrateAction("Pindahkan siswa antar kelas", () -> {
            // Simulate student transfer
            Locator studentCard = page.locator(".student-card").first();
            Locator targetClass = page.locator(".class-container").nth(1);
            
            if (studentCard.isVisible() && targetClass.isVisible()) {
                studentCard.click();
                page.waitForTimeout(500);
                page.locator("button:has-text('Transfer')").click();
                targetClass.click();
            }
        });
        
        takeScreenshot("class_refinement", "Interface penyesuaian manual kelas");
        
        endSection("Fase 5: Generasi dan Penyesuaian Kelas");
        
        // ===== PHASE 6: FINAL REVIEW & GO-LIVE =====
        startSection("Fase 6: Review Final dan Go-Live", 
            "Review akhir dan aktivasi sistem untuk semester baru");
        
        demonstrateAction("Navigasi ke Final Schedule Review", () -> {
            page.navigate(getBaseUrl() + "/academic/final-schedule-review");
            waitForStability();
        });
        
        explain("Final Review menampilkan:");
        explain("- Grid jadwal lengkap semua kelas");
        explain("- Statistik kualitas (rata-rata ukuran kelas, workload guru)");
        explain("- Validasi akhir tanpa konflik");
        explain("- Preview komunikasi ke stakeholder");
        
        takeScreenshot("final_schedule_grid", "Grid jadwal final untuk review");
        
        demonstrateAction("Lihat detail statistik kualitas", () -> {
            Locator statsPanel = page.locator("#qualityMetrics");
            if (statsPanel.isVisible()) {
                statsPanel.scrollIntoViewIfNeeded();
            }
        });
        
        explain("Metrik kualitas yang divalidasi:");
        explain("- ✅ Rata-rata ukuran kelas: 8.5 siswa (dalam target)");
        explain("- ✅ Workload guru: 4-6 kelas (optimal)");
        explain("- ✅ Student mix: 40% baru, 60% lama (seimbang)");
        explain("- ✅ Konflik jadwal: 0 (resolved)");
        
        takeScreenshot("quality_metrics", "Metrik kualitas jadwal final");
        
        demonstrateAction("Navigasi ke System Go-Live", () -> {
            page.navigate(getBaseUrl() + "/academic/system-golive");
            waitForStability();
        });
        
        explain("System Go-Live melakukan:");
        explain("- Aktivasi database records untuk semester baru");
        explain("- Kirim notifikasi ke semua stakeholder");
        explain("- Setup sistem attendance dan grading");
        explain("- Aktivasi monitoring dan support");
        
        takeScreenshot("golive_checklist", "Checklist final sebelum go-live");
        
        demonstrateAction("Konfirmasi Go-Live (Demo Only)", () -> {
            Locator goLiveBtn = page.locator("button:has-text('Confirm Go-Live')");
            if (goLiveBtn.isVisible()) {
                // Don't actually click in demo to avoid irreversible changes
                goLiveBtn.hover();
            }
        });
        
        explain("⚠️ PERHATIAN: Go-Live adalah proses irreversible");
        explain("Setelah go-live:");
        explain("- Semester status berubah dari PLANNING ke ACTIVE");
        explain("- Semua guru dan siswa menerima notifikasi");
        explain("- Sistem siap untuk operasional semester baru");
        
        takeScreenshot("golive_confirmation", "Konfirmasi akhir sebelum aktivasi semester");
        
        endSection("Fase 6: Review Final dan Go-Live");
        
        // ===== BEST PRACTICES & TIPS =====
        startSection("Best Practices", "Tips dan praktik terbaik untuk persiapan semester");
        
        explain("📅 Timeline Ideal:");
        explain("- Week 1: Completion assessment & validasi data");
        explain("- Week 2: Collection ketersediaan guru + assignment level");
        explain("- Week 3: Generasi kelas otomatis + refinement manual");
        explain("- Week 4: Final review + go-live + komunikasi");
        
        explain("💡 Tips Sukses:");
        explain("- Pastikan 100% guru submit ketersediaan sebelum generasi");
        explain("- Review student mix (baru vs lama) per kelas");
        explain("- Validasi workload guru tidak melebihi kapasitas");
        explain("- Komunikasikan jadwal final minimal 1 minggu sebelum mulai");
        
        explain("⚠️ Common Issues:");
        explain("- Kelas undersized: Pertimbangkan merge atau justifikasi khusus");
        explain("- Konflik jadwal guru: Gunakan alternative time slots");
        explain("- Student complaints: Sediakan periode adjustment di minggu pertama");
        
        takeScreenshot("best_practices", "Rangkuman best practices");
        
        endSection("Best Practices");
        
        log.info("📚 Dokumentasi Workflow Persiapan Semester selesai");
        log.info("   Total 6 fase didokumentasikan dengan screenshot lengkap");
        log.info("   Panduan siap digunakan untuk training staff");
    }
    
    @Test
    @DisplayName("Panduan Singkat: Teacher Availability Submission")
    @Sql(scripts = "/sql/term-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/term-preparation-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void userGuide_teacherAvailabilitySubmission() {
        log.info("📚 Menghasilkan Panduan: Teacher Availability Submission");
        
        final String INSTRUCTOR_USERNAME = "ustadz.ahmad";
        final String PASSWORD = "Welcome@YSQ2024";
        
        LoginPage loginPage = new LoginPage(page);
        
        startSection("Pengisian Ketersediaan Jadwal Guru",
            "Panduan lengkap untuk guru mengisi ketersediaan jadwal mengajar");
        
        demonstrateAction("Login sebagai Instructor", () -> {
            loginPage.navigateToLoginPage(getBaseUrl());
            loginPage.login(INSTRUCTOR_USERNAME, PASSWORD);
        });
        
        demonstrateAction("Navigasi ke halaman Availability Submission", () -> {
            page.navigate(getBaseUrl() + "/instructor/availability-submission");
            waitForStability();
        });
        
        demonstrateAction("Pilih term aktif dengan deadline yang masih berlaku", () -> {
            // Check if term selector exists and select the correct term
            Locator termSelector = page.locator("#termSelector");
            if (termSelector.isVisible()) {
                // Debug: Print all available options
                log.info("🔍 DEBUG: Available terms in dropdown:");
                var options = termSelector.locator("option").all();
                if (options.isEmpty()) {
                    throw new AssertionError("❌ No terms available in dropdown! Check if planning terms exist in database.");
                }
                
                String testTermId = "11111111-1111-1111-1111-111111111111";
                boolean foundTestTerm = false;
                
                for (var option : options) {
                    String value = option.getAttribute("value");
                    String text = option.textContent();
                    log.info("  - Value: '{}', Text: '{}'", value, text);
                    if (testTermId.equals(value)) {
                        foundTestTerm = true;
                        log.info("✅ Found our test term!");
                    }
                }
                
                if (foundTestTerm) {
                    // Select our test term specifically
                    log.info("🔄 Selecting test term with ID: {}", testTermId);
                    termSelector.selectOption(new SelectOption().setValue(testTermId));
                    waitForStability();
                    page.waitForURL("**/instructor/availability-submission?termId=" + testTermId);
                    waitForStability();
                } else {
                    // Select the first available term as fallback
                    String firstTermValue = options.get(0).getAttribute("value");
                    String firstTermText = options.get(0).textContent();
                    log.info("⚠️ Test term not found, selecting first available: {} ({})", firstTermText, firstTermValue);
                    termSelector.selectOption(new SelectOption().setValue(firstTermValue));
                    waitForStability();
                    page.waitForURL("**/instructor/availability-submission?termId=" + firstTermValue);
                    waitForStability();
                }
            }
        });
        
        demonstrateAction("Verifikasi submission tersedia untuk term yang dipilih", () -> {
            // Fail fast if submission is closed for this term
            Locator submissionClosedAlert = page.locator("#submission-closed-alert");
            if (submissionClosedAlert.isVisible()) {
                // Get current term info for debugging
                String currentTerm = page.locator("#termSelector option:checked").textContent();
                String alertText = submissionClosedAlert.textContent();
                
                throw new AssertionError(
                    "❌ SUBMISSION CLOSED ERROR DETECTED!\n" +
                    "Current Term: " + currentTerm + "\n" +
                    "Alert Message: " + alertText + "\n" +
                    "Expected: Term should allow availability submission\n" +
                    "Actual: Term has closed submission or deadline has passed\n" +
                    "Check: 1) Term status should be PLANNING, 2) preparation_deadline should be > current date\n" +
                    "Debug: Check term-preparation-setup.sql for correct deadline setting"
                );
            }
        });
        
        explain("Halaman ini menampilkan matrix jadwal 7 hari x N sesi waktu");
        explain("Guru dapat memilih slot waktu yang tersedia untuk mengajar");
        explain("Term aktif dipilih untuk memastikan submission dapat dilakukan");
        
        takeScreenshot("availability_matrix_empty", "Matrix ketersediaan kosong");
        
        demonstrateAction("Pilih ketersediaan hari Senin", () -> {
            // Wait for the availability grid to be visible
            page.waitForSelector("#availabilityGrid");
            waitForStability();
            
            // Click Monday slots using the correct data attributes
            page.locator("[data-day='MONDAY'][data-session='SESI_1']").click();
            waitForStability();
            page.locator("[data-day='MONDAY'][data-session='SESI_2']").click();
            waitForStability();
            page.locator("[data-day='MONDAY'][data-session='SESI_7']").click();
            waitForStability();
        });
        
        demonstrateAction("Pilih ketersediaan hari Rabu", () -> {
            // Click Wednesday slots using the correct data attributes
            page.locator("[data-day='WEDNESDAY'][data-session='SESI_2']").click();
            waitForStability();
            page.locator("[data-day='WEDNESDAY'][data-session='SESI_5']").click();
            waitForStability();
            page.locator("[data-day='WEDNESDAY'][data-session='SESI_7']").click();
            waitForStability();
        });
        
        demonstrateAction("Pilih ketersediaan hari Jumat", () -> {
            // Click Friday slots using the correct data attributes
            page.locator("[data-day='FRIDAY'][data-session='SESI_1']").click();
            waitForStability();
            page.locator("[data-day='FRIDAY'][data-session='SESI_5']").click();
            waitForStability();
        });
        
        takeScreenshot("availability_filled", "Matrix ketersediaan sudah diisi");
        
        demonstrateAction("Set maksimal kelas per minggu", () -> {
            Locator maxClassesInput = page.locator("#maxClassesPerWeek");
            if (maxClassesInput.isVisible()) {
                maxClassesInput.selectOption("6");
            }
        });
        
        demonstrateAction("Tambahkan catatan khusus", () -> {
            Locator notesTextarea = page.locator("#preferences");
            if (notesTextarea.isVisible()) {
                notesTextarea.fill("Preferensi untuk kelas Tahsin 1 dan 2. Tidak tersedia untuk kelas weekend pagi karena komitmen keluarga.");
            }
        });
        
        takeScreenshot("availability_complete", "Form ketersediaan lengkap dengan preferensi");
        
        demonstrateAction("Submit ketersediaan", () -> {
            page.locator("button:has-text('Submit Availability')").click();
            page.waitForTimeout(2000);
        });
        
        explain("✅ Ketersediaan berhasil disimpan");
        explain("Admin akademik dapat melihat ketersediaan ini untuk perencanaan kelas");
        explain("Guru dapat update ketersediaan sampai batas waktu yang ditentukan");
        
        takeScreenshot("submission_success", "Konfirmasi ketersediaan berhasil disimpan");
        
        endSection("Pengisian Ketersediaan Jadwal Guru");
        
        log.info("📚 Dokumentasi Teacher Availability Submission selesai");
    }
    
}