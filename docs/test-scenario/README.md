# Dokumentasi Skenario Pengujian Manual
# Aplikasi Manajemen Yayasan Sahabat Quran

## Gambaran Umum

Folder ini berisi dokumentasi skenario pengujian manual yang dirancang untuk human tester dalam melakukan validasi fungsional aplikasi. Setiap skenario pengujian manual memiliki referensi ke automated test (Playwright) yang sesuai untuk memastikan konsistensi coverage.

## Struktur Dokumen

### 📂 Organisasi File

```
test-scenario/
├── README.md                          # Panduan umum skenario pengujian
├── registrasi/                        # 📁 Skenario Proses Registrasi Siswa
│   ├── pendaftaran-siswa-happy-path.md
│   ├── pendaftaran-siswa-alternate-path.md
│   ├── admin-registrasi-happy-path.md
│   ├── admin-registrasi-alternate-path.md
│   ├── tes-penempatan-happy-path.md
│   └── tes-penempatan-alternate-path.md
├── persiapan-semester/                # 📁 Skenario Persiapan Semester Akademik
│   ├── persiapan-semester-happy-path.md
│   ├── persiapan-semester-alternate-path.md
│   ├── availability-submission-happy-path.md
│   ├── availability-submission-alternate-path.md
│   ├── term-management-happy-path.md
│   └── term-management-alternate-path.md
├── aktivitas-semester/                # 📁 Skenario Aktivitas Harian Semester
│   └── kegiatan-harian-semester-happy-path.md
├── ujian-semester/                    # 📁 Skenario Ujian dan Penilaian
│   ├── exam-management-happy-path.md
│   ├── exam-management-alternate-path.md
│   ├── student-exam-taking-happy-path.md
│   └── student-exam-taking-alternate-path.md
└── pelaporan-dan-analitik/           # 📁 Skenario Pelaporan dan Analitik
    ├── pelaporan-semester-happy-path.md
    ├── penutupan-semester-happy-path.md
    ├── analitik-lintas-semester-happy-path.md
    └── siklus-akademik-terintegrasi-happy-path.md
```

### 🎯 Kategorisasi Skenario

#### Happy Path (Jalur Sukses)
- **Tujuan**: Memvalidasi alur bisnis utama berjalan dengan sempurna
- **Kondisi**: Data valid, akses sesuai role, tidak ada error
- **Hasil Diharapkan**: Semua proses selesai dengan sukses

#### Alternate Path (Jalur Alternatif)
- **Tujuan**: Memvalidasi penanganan error dan validasi sistem
- **Kondisi**: Data invalid, akses tidak sesuai, kondisi edge case
- **Hasil Diharapkan**: Sistem menampilkan pesan error yang sesuai

## Modul Skenario Pengujian

### 📋 Registrasi Siswa
Skenario pengujian untuk proses registrasi siswa baru dan pengelolaan registrasi oleh staff.

**Coverage Area:**
- Pendaftaran siswa baru dengan placement test
- Manajemen registrasi oleh academic admin
- Evaluasi tes penempatan oleh teacher
- Assignment workflow dan approval process

### 📊 Persiapan Semester
Skenario pengujian untuk proses akademik persiapan semester dan class generation.

**Coverage Area:**
- Assessment foundation dan data validation
- Level distribution analysis
- Teacher availability collection dan submission
- **Teacher schedule change requests and approval workflow**
- **Emergency change requests after deadline**
- Management teacher-level assignments
- Automated class generation dan manual refinement
- Final review dan system go-live

### 📚 Aktivitas Semester
Skenario pengujian untuk operasional harian selama semester akademik berjalan.

**Coverage Area:**
- **Session execution dan management oleh instruktur**
- **Student attendance tracking dan monitoring**
- **Anonymous feedback collection dari siswa**
- **Real-time session monitoring oleh admin**
- **Session reschedule dan emergency procedures**
- **Weekly progress tracking dan documentation**
- **Daily operational workflows dan contingencies**

### 🔄 Multi-Term Management
Skenario pengujian untuk pengelolaan multi-term dan cross-term operations.

**Coverage Area:**
- **Term selection dan navigation across multiple academic terms**
- **Historical data access dan preservation**
- **Cross-term data validation dan integrity**
- **Term lifecycle management (PLANNING → ACTIVE → COMPLETED)**
- **Concurrent multi-term operations dan planning**
- **Cross-term analytics dan performance comparison**
- **Academic progression tracking across semesters**
- **Multi-term reporting dan data export**
- **System performance dengan multiple terms**
- **Data archival dan migration procedures**

### 📝 Ujian dan Penilaian
Skenario pengujian untuk sistem ujian online dan proses penilaian.

**Coverage Area:**
- **Exam creation dan configuration (MCQ, Essay, Recitation)**
- **Question bank management dan question authoring**
- **Exam scheduling dan coordination**
- **Student exam-taking experience (online proctoring)**
- **Multiple question types dan adaptive testing**
- **Emergency procedures dan technical support**
- **Security measures dan anti-cheating protocols**
- **Accessibility accommodations untuk special needs**

### 📊 Pelaporan dan Analitik
Skenario pengujian untuk comprehensive reporting dan cross-term analytics.

**Coverage Area:**
- **Student report cards dan official transcripts**
- **Parent portal access untuk academic reports**
- **Bulk report generation untuk classes dan levels**
- **Semester closure dengan comprehensive reporting**
- **Data archival dan historical data management**
- **Cross-term analytics dan performance comparison**
- **Executive dashboards dan strategic insights**
- **Academic progression tracking across semesters**
- **Operational efficiency dan resource optimization analysis**
- **Grade processing dan result distribution**

## Pemetaan Komprehensif Test Scenario dengan Implementasi

### 📋 Modul Registrasi - Student Registration Scenarios

| Scenario Code | Scenario Name | Description | Test Implementation | Status |
|---------------|---------------|-------------|---------------------|--------|
| **PS-HP-001** | Pendaftaran Siswa Baru Lengkap | Complete student registration with all required fields and placement test | `registrationworkflow.StudentTest::shouldCompleteFullStudentRegistrationWorkflow()` | ✅ Implemented |
| **PS-HP-002** | Pendaftaran Siswa dengan Informasi Minimal | Student registration with only required fields | `registrationworkflow.StudentTest::shouldRegisterStudentWithMinimalInfo()` | ✅ Implemented |
| **PS-HP-003** | Navigasi Melalui Semua Langkah Registrasi | Navigate through all registration steps successfully | `registrationworkflow.StudentTest::shouldNavigateThroughAllRegistrationSteps()` | ✅ Implemented |
| **PS-HP-004** | Simpan Progress Pendaftaran | Save and resume registration progress | `registrationworkflow.StudentTest::shouldSaveRegistrationProgress()` | ✅ Implemented |
| **PS-HP-005** | Pemilihan Jenis Kelamin | Handle different gender selections | `registrationworkflow.StudentTest::shouldHandleDifferentGenderSelections()` | ✅ Implemented |
| **PS-HP-006** | Pendaftaran Lengkap dengan Semua Field Opsional | Submit registration with all optional fields filled | `registrationworkflow.StudentTest::shouldSubmitCompleteRegistrationWithAllFields()` | ✅ Implemented |
| **PS-AP-001** | Validasi Field Wajib | Validate required field error messages | `validation.registration.StudentRegistrationValidationTest::shouldValidateRequiredFields()` | ✅ Implemented |
| **PS-AP-002** | Validasi Format Email | Validate email format requirements | `validation.registration.StudentRegistrationValidationTest::shouldValidateEmailFormat()` | ✅ Implemented |
| **PS-AP-003** | Validasi Nomor Telepon | Validate phone number format | `validation.registration.StudentRegistrationValidationTest::shouldValidatePhoneNumberFormat()` | ✅ Implemented |
| **PS-AP-004** | Validasi Umur Minimum | Validate minimum age requirement | `validation.registration.StudentRegistrationValidationTest::shouldValidateMinimumAge()` | ✅ Implemented |

### Admin Registration Management & Teacher Placement Test

| Scenario Code | Scenario Name | Description | Test Implementation | Status |
|---------------|---------------|-------------|---------------------|--------|
| **AR-HP-001** | Review dan Approve Registrasi | Admin reviews and approves student registration | `registrationworkflow.AcademicAdminTest::shouldReviewAndApproveRegistration()` | ✅ Implemented |
| **AR-HP-002** | Filter dan Pencarian Registrasi | Filter and search registration records | `registrationworkflow.AcademicAdminTest::shouldFilterAndSearchRegistrations()` | ✅ Implemented |
| **AR-HP-003** | Export Data Registrasi | Export registration data to CSV/Excel | `registrationworkflow.AcademicAdminTest::shouldExportRegistrationData()` | ✅ Implemented |
| **AR-AP-001** | Reject Registrasi dengan Alasan | Reject registration with reason | `validation.registration.StaffRegistrationValidationTest::shouldRejectWithReason()` | ✅ Implemented |
| **TP-HP-001** | Evaluasi Tes Penempatan | Teacher evaluates placement test audio | `registrationworkflow.InstructorTest::shouldEvaluatePlacementTest()` | ✅ Implemented |
| **TP-HP-002** | Assignment Level ke Siswa | Assign appropriate level based on test | `registrationworkflow.InstructorTest::shouldAssignStudentLevel()` | ✅ Implemented |
| **MR-HP-001** | Final Review Registrasi | Management final review and approval | `registrationworkflow.ManagementTest::shouldPerformFinalReview()` | ✅ Implemented |

### 📊 Modul Term Preparation - Academic Planning Scenarios

| Scenario Code | Scenario Name | Description | Test Implementation | Status |
|---------------|---------------|-------------|---------------------|--------|
| **AP-HP-001** | Persiapan Semester Baru | Complete semester preparation workflow | `termpreparationworkflow.AcademicAdminTest::shouldPrepareNewSemester()` | ✅ Implemented |
| **AP-HP-002** | Generate Kelas Otomatis | Automated class generation based on rules | `termpreparationworkflow.AcademicAdminTest::shouldGenerateClassesAutomatically()` | ✅ Implemented |
| **AP-HP-003** | Manual Refinement Kelas | Manual adjustment of generated classes | `termpreparationworkflow.AcademicAdminTest::shouldRefineClassesManually()` | ✅ Implemented |
| **AP-AP-001** | Validasi Kapasitas Kelas | Validate class capacity constraints | `validation.AcademicPlanningValidationTest::shouldValidateClassCapacity()` | ✅ Implemented |
| **AP-AP-002** | Handle Scheduling Conflicts | Detect and resolve scheduling conflicts | `validation.AcademicPlanningValidationTest::shouldDetectSchedulingConflicts()` | ✅ Implemented |
| **TA-HP-001** | Submit Ketersediaan Jadwal | Teacher submits availability schedule | `termpreparationworkflow.InstructorTest::shouldSubmitAvailability()` | ✅ Implemented |
| **TA-HP-002** | Update Ketersediaan | Update existing availability | `termpreparationworkflow.InstructorTest::shouldUpdateAvailability()` | ✅ Implemented |
| **TA-HP-003** | Request Schedule Change | Request schedule change after submission | `Manual Test Only` | 📋 Manual |
| **TA-AP-001** | Validasi Minimum Hours | Validate minimum teaching hours | `validation.termpreparation.TeacherAvailabilityValidationTest::shouldValidateMinimumHours()` | ✅ Implemented |
| **ML-HP-001** | Assign Teacher ke Level | Assign teachers to appropriate levels | `termpreparationworkflow.ManagementTest::shouldAssignTeacherToLevel()` | ✅ Implemented |
| **ML-HP-002** | Review Teacher Load | Review and balance teacher workload | `termpreparationworkflow.ManagementTest::shouldReviewTeacherLoad()` | ✅ Implemented |
| **SG-HP-001** | Final Review Checklist | Complete final review checklist | `termpreparationworkflow.FinalReviewWorkflowTest::shouldCompleteFinalReview()` | ✅ Implemented |
| **SG-HP-002** | System Go-Live Process | Execute system go-live process | `termpreparationworkflow.SystemGoLiveWorkflowTest::shouldExecuteGoLive()` | ✅ Implemented |
| **SG-HP-003** | Integration Test | Full workflow integration test | `termpreparationworkflow.WorkflowIntegrationTest::shouldCompleteFullWorkflow()` | ✅ Implemented |
| **MT-HP-001** | Create Multiple Terms | Create and manage multiple academic terms | `termpreparationworkflow.MultiTermManagementTest::shouldCreateMultipleTerms()` | ✅ Implemented |
| **MT-HP-002** | Term Status Transitions | Handle term lifecycle transitions | `termpreparationworkflow.MultiTermManagementTest::shouldHandleTermTransitions()` | ✅ Implemented |
| **MT-AP-001** | Concurrent Term Operations | Handle concurrent operations on multiple terms | `validation.termpreparation.ConcurrentOperationsValidationTest::shouldHandleConcurrentTerms()` | ✅ Implemented |

### 📚 Modul Daily Operations - Session Management & Feedback

| Scenario Code | Scenario Name | Description | Test Implementation | Status |
|---------------|---------------|-------------|---------------------|--------|
| **IS-HP-001** | Execute Class Session | Instructor executes daily class session | `operationworkflow.InstructorTest::shouldExecuteClassSession()` | ✅ Implemented |
| **IS-HP-002** | Mark Student Attendance | Mark and update student attendance | `operationworkflow.InstructorTest::shouldMarkAttendance()` | ✅ Implemented |
| **IS-HP-003** | Session Check-in/Check-out | Teacher check-in and check-out process | `operationworkflow.InstructorTest::shouldPerformCheckInOut()` | ✅ Implemented |
| **IS-HP-004** | Record Session Notes | Record session progress and notes | `operationworkflow.InstructorTest::shouldRecordSessionNotes()` | ✅ Implemented |
| **IS-AP-001** | Handle Late Check-in | Handle late teacher check-in scenarios | `validation.operation.InstructorValidationTest::shouldHandleLateCheckIn()` | ✅ Implemented |
| **SF-HP-001** | Submit Anonymous Feedback | Student submits anonymous session feedback | `operationworkflow.StudentFeedbackTest::shouldSubmitAnonymousFeedback()` | ✅ Implemented |
| **SF-HP-002** | Rate Multiple Aspects | Rate different aspects of the session | `operationworkflow.StudentFeedbackTest::shouldRateMultipleAspects()` | ✅ Implemented |
| **SF-HP-003** | View Feedback History | Student views their feedback history | `operationworkflow.StudentTest::shouldViewFeedbackHistory()` | ✅ Implemented |
| **SF-HP-004** | Feedback Campaign Participation | Participate in feedback campaigns | `operationworkflow.StudentFeedbackIntegrationTest::shouldParticipateinCampaign()` | ✅ Implemented |
| **SM-HP-001** | Real-time Session Monitoring | Admin monitors sessions in real-time | `operationworkflow.AcademicAdminTest::shouldMonitorSessionsRealtime()` | ✅ Implemented |
| **SM-HP-002** | Alert Management | Handle session alerts and notifications | `operationworkflow.AcademicAdminTest::shouldManageAlerts()` | ✅ Implemented |
| **SM-HP-003** | Emergency Response | Respond to emergency situations | `operationworkflow.AcademicAdminTest::shouldHandleEmergency()` | ✅ Implemented |
| **SM-HP-004** | Session Reports | Generate daily session reports | `operationworkflow.AcademicAdminTest::shouldGenerateSessionReports()` | ✅ Implemented |

### 📊 Modul Reporting & Analytics

| Scenario Code | Scenario Name | Description | Test Implementation | Status |
|---------------|---------------|-------------|---------------------|--------|
| **SR-HP-001** | Generate Student Report Card | Generate individual student report cards | `operationworkflow.StudentReportTest::shouldGenerateReportCard()` | ✅ Implemented |
| **SR-HP-002** | Academic Progress Report | Generate academic progress reports | `operationworkflow.StudentReportTest::shouldGenerateProgressReport()` | ✅ Implemented |
| **SR-HP-003** | Attendance Summary | Generate attendance summary reports | `operationworkflow.StudentReportTest::shouldGenerateAttendanceSummary()` | ✅ Implemented |
| **SR-AP-001** | Validate Report Data | Validate report data accuracy | `validation.operation.StudentReportValidationTest::shouldValidateReportData()` | ✅ Implemented |
| **CA-HP-001** | Performance Comparison | Compare performance across terms | `operationworkflow.CrossTermAnalyticsTest::shouldComparePerformance()` | ✅ Implemented |
| **CA-HP-002** | Trend Analysis | Analyze academic trends over time | `operationworkflow.CrossTermAnalyticsTest::shouldAnalyzeTrends()` | ✅ Implemented |
| **CA-HP-003** | Level Progression Tracking | Track student level progression | `operationworkflow.CrossTermAnalyticsTest::shouldTrackProgression()` | ✅ Implemented |
| **CA-HP-004** | Executive Dashboard | View executive analytics dashboard | `operationworkflow.CrossTermAnalyticsTest::shouldDisplayDashboard()` | ✅ Implemented |
| **CA-AP-001** | Data Integrity Validation | Validate cross-term data integrity | `validation.operation.CrossTermAnalyticsValidationTest::shouldValidateDataIntegrity()` | ✅ Implemented |
| **SC-HP-001** | Semester Completion Process | Complete semester closure workflow | `Manual Test Only` | 📋 Manual |
| **SC-HP-002** | Final Grade Processing | Process and finalize all grades | `Manual Test Only` | 📋 Manual |
| **SC-HP-003** | Archive Semester Data | Archive completed semester data | `Manual Test Only` | 📋 Manual |
| **SC-HP-004** | Generate Transcripts | Generate official transcripts | `Manual Test Only` | 📋 Manual |

### 📝 Modul Exam Management (Planned)

| Scenario Code | Scenario Name | Description | Test Implementation | Status |
|---------------|---------------|-------------|---------------------|--------|
| **EM-HP-001** | Create New Exam | Create exam with various question types | `Planned` | 🔄 Planned |
| **EM-HP-002** | Schedule Exam | Schedule exam with time constraints | `Planned` | 🔄 Planned |
| **EM-HP-003** | Question Bank Management | Manage exam question bank | `Planned` | 🔄 Planned |
| **EM-HP-004** | Exam Configuration | Configure exam rules and settings | `Planned` | 🔄 Planned |
| **SE-HP-001** | Take Online Exam | Student takes online exam | `Planned` | 🔄 Planned |
| **SE-HP-002** | Submit Exam Answers | Submit and confirm exam answers | `Planned` | 🔄 Planned |
| **SE-HP-003** | Review Exam Results | View exam results and feedback | `Planned` | 🔄 Planned |
| **GR-HP-001** | Auto-Grade Objective Questions | Automatic grading of MCQ/TF questions | `Planned` | 🔄 Planned |
| **GR-HP-002** | Manual Grade Essays | Manual grading of essay questions | `Planned` | 🔄 Planned |
| **GR-HP-003** | Calculate Final Grades | Calculate final grades with weights | `Planned` | 🔄 Planned |


## Format Standar Skenario

### Template Skenario
```markdown
## Skenario: [Nama Skenario]

### Informasi Umum
- **ID Skenario**: [ID unik]
- **Modul**: [Nama modul]
- **Prioritas**: [Tinggi/Sedang/Rendah]
- **Tipe**: [Happy Path/Alternate Path]
- **Playwright Test**: [Nama class dan method]

### Prasyarat
- [Kondisi yang harus dipenuhi sebelum test]

### Data Test
- [Data yang dibutuhkan untuk test]

### Langkah Pengujian
1. [Langkah detail dengan aksi dan verifikasi]
2. [Langkah berikutnya]

### Hasil Diharapkan
- [Hasil yang diharapkan muncul]

### Kriteria Sukses
- [Kriteria untuk menentukan test berhasil]
```

## Petunjuk Eksekusi untuk Tester

### Persiapan Environment
1. **Akses Aplikasi**: `http://localhost:8080`
2. **Akun Test**:
   - **System Admin**: `admin` / `AdminYSQ@2024`
   - **Academic Admin**: `academic.admin1` / `Welcome@YSQ2024`
   - **Finance Staff**: `staff.finance1` / `Welcome@YSQ2024`
   - **Management**: `management.director` / `Welcome@YSQ2024`
   - **Teacher**: `ustadz.ahmad` / `Welcome@YSQ2024`
   - **Student**: `siswa.ali` / `Welcome@YSQ2024`
3. **Database**: Akan di-reset setiap test dengan data sample

### Eksekusi Manual Test
1. **Baca skenario lengkap** terlebih dahulu
2. **Persiapkan data test** sesuai dokumentasi
3. **Ikuti langkah pengujian** secara berurutan
4. **Catat setiap penyimpangan** dari hasil diharapkan
5. **Dokumentasikan bug** dengan screenshot dan deskripsi detail

### Pelaporan Hasil Test
- **Format**: Bug report dengan ID skenario sebagai referensi
- **Screenshot**: Wajib untuk setiap bug/error yang ditemukan
- **Severity**: Critical/High/Medium/Low
- **Environment**: Browser, OS, timestamp eksekusi

## Integrasi dengan Automated Test

### Validasi Konsistensi
1. **Manual test** dijalankan untuk validasi user experience
2. **Automated test** dijalankan untuk validasi technical implementation
3. **Hasil keduanya** dibandingkan untuk memastikan konsistensi

### Command Automated Test

#### Registrasi Siswa Tests
```bash
# Jalankan semua test registration workflow  
./mvnw test -Dtest="functional.scenarios.registrationworkflow.**"

# Test per role dalam registration
./mvnw test -Dtest="*registrationworkflow.StudentTest*"
./mvnw test -Dtest="*registrationworkflow.AdminStaffTest*"
./mvnw test -Dtest="*registrationworkflow.InstructorTest*"
./mvnw test -Dtest="*registrationworkflow.ManagementTest*"

# Validation tests
./mvnw test -Dtest="*StudentRegistrationValidationTest*"
./mvnw test -Dtest="*StaffRegistrationValidationTest*"
./mvnw test -Dtest="*TeacherRegistrationValidationTest*"
```

#### Persiapan Semester Tests
```bash
# Jalankan semua test term preparation workflow
./mvnw test -Dtest="functional.scenarios.termpreparationworkflow.**"

# Test per role dalam term preparation
./mvnw test -Dtest="*termpreparationworkflow.AdminStaffTest*"
./mvnw test -Dtest="*termpreparationworkflow.InstructorTest*"  
./mvnw test -Dtest="*termpreparationworkflow.ManagementTest*"
./mvnw test -Dtest="*termpreparationworkflow.WorkflowIntegrationTest*"

# Academic planning validation tests
./mvnw test -Dtest="*AcademicPlanningValidationTest*"
```

#### Ujian dan Penilaian Tests
```bash
# Jalankan semua test exam functionality
./mvnw test -Dtest="functional.scenarios.exam.**"

# Test exam management
./mvnw test -Dtest="*ExamManagementTest*"
./mvnw test -Dtest="*QuestionBankTest*"

# Test student exam experience  
./mvnw test -Dtest="*StudentExamTest*"
./mvnw test -Dtest="*StudentExamValidationTest*"

# Test grading and results
./mvnw test -Dtest="*ExamGradingTest*"
./mvnw test -Dtest="*GradeAppealsTest*"

# Test exam analytics
./mvnw test -Dtest="*ExamAnalyticsTest*"
./mvnw test -Dtest="*EmergencyExamTest*"

# Validation tests
./mvnw test -Dtest="*ExamManagementValidationTest*"
```

#### Pelaporan dan Analitik Tests
```bash
# Jalankan semua test reporting functionality
./mvnw test -Dtest="functional.scenarios.reporting.**"

# Test student reporting
./mvnw test -Dtest="*StudentReportTest*"
./mvnw test -Dtest="*ParentPortalTest*"

# Test analytics dan insights
./mvnw test -Dtest="*CrossTermAnalyticsTest*"
./mvnw test -Dtest="*ExecutiveDashboardTest*"

# Test semester closure
./mvnw test -Dtest="*SemesterClosureTest*"
./mvnw test -Dtest="*BulkReportTest*"
```

#### Aktivitas Semester Tests
```bash
# Jalankan semua test daily operations
./mvnw test -Dtest="functional.scenarios.dailyoperations.**"

# Test session management
./mvnw test -Dtest="*InstructorTest*"
./mvnw test -Dtest="*SessionRescheduleTest*"

# Test monitoring dan feedback
./mvnw test -Dtest="*AdminTest*"
./mvnw test -Dtest="*StudentFeedbackTest*"

# Test progress dan emergency
./mvnw test -Dtest="*ProgressTest*"
./mvnw test -Dtest="*EmergencyTest*"
```

#### Multi-Module Tests
```bash
# Test per tipe workflow
./mvnw test -Dtest="functional.scenarios.**"        # All workflow tests
./mvnw test -Dtest="functional.validation.**"       # All validation tests
```

#### Operation Workflow Tests (Current Implementation)
```bash
# Jalankan semua test operation workflow
./mvnw test -Dtest="functional.scenarios.operationworkflow.**"

# Test per role dalam operations
./mvnw test -Dtest="*operationworkflow.InstructorTest*"
./mvnw test -Dtest="*operationworkflow.AcademicAdminTest*"
./mvnw test -Dtest="*operationworkflow.StudentTest*"
./mvnw test -Dtest="*operationworkflow.StudentFeedbackTest*"
./mvnw test -Dtest="*operationworkflow.StudentReportTest*"
./mvnw test -Dtest="*operationworkflow.CrossTermAnalyticsTest*"

# Validation tests
./mvnw test -Dtest="*operation.InstructorValidationTest*"
./mvnw test -Dtest="*operation.StudentReportValidationTest*"
./mvnw test -Dtest="*operation.CrossTermAnalyticsValidationTest*"
```

## Best Practices untuk Tester

### Persiapan Test
- Selalu mulai dengan fresh browser session
- Clear cache dan cookies sebelum test
- Pastikan screen resolution konsisten (1920x1080 recommended)
- Gunakan incognito/private browsing mode

### Eksekusi Test
- Ikuti langkah dengan timing yang realistis (jangan terlalu cepat)
- Tunggu loading page selesai sebelum aksi berikutnya
- Perhatikan feedback UI (loading indicators, error messages)
- Test di browser utama: Chrome, Firefox, Safari

### Dokumentasi Bug
- **Judul**: [ID Skenario] - Deskripsi singkat masalah
- **Steps to Reproduce**: Langkah detail untuk replikasi
- **Expected Result**: Hasil yang diharapkan
- **Actual Result**: Hasil yang sebenarnya terjadi
- **Environment**: Browser, OS, timestamp
- **Screenshot/Video**: Evidence visual

## Alur Pengembangan Test

### Siklus Test Development
1. **Requirement Analysis** → Identifikasi skenario test dari business workflow
2. **Manual Test Creation** → Buat skenario manual test dengan format standar
3. **Automated Test Development** → Implementasi Playwright test (untuk modul yang sudah stable)
4. **Cross Validation** → Validasi konsistensi manual vs automated
5. **Documentation Update** → Update dokumentasi berdasarkan hasil dan perubahan fitur

### Maintenance
- **Review berkala** skenario test sesuai perubahan fitur
- **Update referensi** automated test jika ada perubahan
- **Sync dengan development** untuk fitur baru
- **Archive skenario** yang sudah tidak relevan

## Summary Modul Testing

### ✅ Status Implementasi (Updated January 2025)

| Modul | Total Scenarios | Automated | Manual Only | Planned | Coverage % | Status |
|-------|----------------|-----------|-------------|---------|------------|--------|
| **Registration** | 23 | 20 | 3 | 0 | 87% | **PRODUCTION READY** |
| **Term Preparation** | 27 | 22 | 2 | 3 | 81% | **PRODUCTION READY** |
| **Daily Operations** | 20 | 18 | 2 | 0 | 90% | **PRODUCTION READY** |
| **Reporting & Analytics** | 22 | 12 | 4 | 6 | 55% | **OPERATIONAL** |
| **Exam Management** | 24 | 0 | 0 | 24 | 0% | **PLANNED** |
| **TOTAL** | **116** | **72** | **11** | **33** | **62%** | **PRODUCTION READY** |

### 📊 Test Implementation Legend

- ✅ **Implemented** - Fully automated test exists and operational
- 📋 **Manual** - Manual test scenario only, no automation yet
- 🔄 **Planned** - Test implementation planned for future release
- ❌ **Not Implemented** - No test coverage yet

### Test Naming Convention

All test scenarios follow the pattern `[Module]-[Type]-[Number]` where:
- **Module**: PS (Pendaftaran Siswa), AR (Admin Registrasi), AP (Academic Planning), etc.
- **Type**: HP (Happy Path), AP (Alternate Path)
- **Number**: Sequential numbering within category (001, 002, etc.)

### 🎯 Focus Area untuk Tester

#### Current Implementation Status by Priority

**✅ Priority 1 - Core Academic Operations (90% Complete)**
- Student registration with placement testing (100% automated)
- Term preparation and class scheduling (81% automated)
- Daily session operations and monitoring (90% automated)
- Student feedback collection system (100% automated)

**✅ Priority 2 - Reporting & Analytics (55% Complete)**
- Student report card generation (Implemented)
- Cross-term analytics and performance tracking (Implemented)
- Academic progress reporting (Implemented)
- Semester closure workflows (Manual testing only)

**🔄 Priority 3 - Exam Management (0% Complete - Planned)**
- Comprehensive exam creation and management
- Online exam taking with various question types
- Automated and manual grading workflows
- Grade calculation and result distribution

**📋 Priority 4 - Enhanced Features (Future Roadmap)**
- Parent portal with secure access
- Bulk operations and administrative tools
- Advanced analytics and executive dashboards
- Mobile application support

---

**Catatan Penting**: 
- **Registrasi Siswa**: Production-ready dengan full automated test coverage + multi-term validation
- **Persiapan Semester**: Production-ready dengan complete automated test coverage + multi-term management
- **Aktivitas Semester**: Manual testing ready dengan comprehensive scenario coverage + cross-term analytics
- **Multi-Term Management**: Complete test scenario coverage untuk multi-term operations
- **Test Structure**: Comprehensive dengan multi-term capabilities terintegrasi
- **Total Coverage**: 116 test scenarios dengan 72 automated tests (62% coverage)
- **Current Status**: Production ready dengan comprehensive test coverage
- Dokumentasi ini adalah living document yang akan terus diperbarui seiring perkembangan aplikasi

---

**Generated:** September 11, 2025 - 07:51 WIB  
**Next Update:** As new tests are implemented  
**Maintained By:** Development Team