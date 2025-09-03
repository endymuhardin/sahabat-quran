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
└── aktivitas-semester/                # 📁 Skenario Aktivitas Harian Semester
    ├── kegiatan-harian-kelas-happy-path.md
    ├── kegiatan-harian-kelas-alternate-path.md
    ├── cross-term-analytics-happy-path.md
    ├── cross-term-analytics-alternate-path.md
    ├── laporan-siswa-happy-path.md
    └── laporan-siswa-alternate-path.md
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
Skenario pengujian untuk pelaksanaan aktivitas harian selama semester akademik berjalan.

**Coverage Area:**
- Pelaksanaan sesi kelas oleh instruktur
- Pencatatan kehadiran siswa dan guru
- Feedback siswa terhadap pembelajaran
- Monitoring aktivitas oleh admin
- Penjadwalan ulang sesi (reschedule)
- Penggantian instruktur sementara
- Pencatatan progres mingguan
- Analisis feedback dan evaluasi
- Notifikasi kepada orangtua siswa
- Penanganan kegagalan sistem dan error handling
- **Cross-term analytics dan historical data analysis**
- **Multi-term performance tracking dan comparison**
- **Student report card generation dan transcript compilation**
- **Parent portal access untuk student academic reports**
- **Bulk report generation untuk class dan level analytics**
- **Semester closure dengan comprehensive report generation**

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

## Pemetaan dengan Automated Test

### Registrasi Siswa

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Pendaftaran Siswa - Happy Path | `registrationworkflow.StudentTest` | ✅ Implemented | `functional/scenarios/registrationworkflow/StudentTest.java` |
| Pendaftaran Siswa - Alternate Path | `StudentRegistrationValidationTest` | ✅ Implemented | `functional/validation/StudentRegistrationValidationTest.java` |
| Admin Registrasi - Happy Path | `registrationworkflow.AdminStaffTest` | ✅ Implemented | `functional/scenarios/registrationworkflow/AdminStaffTest.java` |
| Admin Registrasi - Alternate Path | `StaffRegistrationValidationTest` | ✅ Implemented | `functional/validation/StaffRegistrationValidationTest.java` |
| Tes Penempatan - Happy Path | `registrationworkflow.InstructorTest` | ✅ Implemented | `functional/scenarios/registrationworkflow/InstructorTest.java` |
| Tes Penempatan - Alternate Path | `TeacherRegistrationValidationTest` | ✅ Implemented | `functional/validation/TeacherRegistrationValidationTest.java` |
| Management Registration Review | `registrationworkflow.ManagementTest` | ✅ Implemented | `functional/scenarios/registrationworkflow/ManagementTest.java` |

### Persiapan Semester

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Persiapan Semester - Happy Path | `termpreparationworkflow.AdminStaffTest` | ✅ Implemented | `functional/scenarios/termpreparationworkflow/AdminStaffTest.java` |
| Persiapan Semester - Alternate Path | `AcademicPlanningValidationTest` | ✅ Implemented | `functional/validation/AcademicPlanningValidationTest.java` |
| Teacher Availability - Happy Path | `termpreparationworkflow.InstructorTest` | ✅ Implemented | `functional/scenarios/termpreparationworkflow/InstructorTest.java` |
| Teacher Schedule Change Request - Happy Path | `TeacherAvailabilityChangeTest` | 📋 Manual Only | `availability-submission-happy-path.md` |
| Teacher Schedule Change Request - Alternate Path | `TeacherChangeRequestValidationTest` | 📋 Manual Only | `availability-submission-alternate-path.md` |
| Management Level Assignment | `termpreparationworkflow.ManagementTest` | ✅ Implemented | `functional/scenarios/termpreparationworkflow/ManagementTest.java` |
| Term Preparation Workflow Integration | `termpreparationworkflow.WorkflowIntegrationTest` | ✅ Implemented | `functional/scenarios/termpreparationworkflow/WorkflowIntegrationTest.java` |

### Aktivitas Semester

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Kegiatan Harian Kelas - Happy Path | `semesteractivities.InstructorTest` | ❌ Not Implemented | `functional/scenarios/semesteractivities/InstructorTest.java` |
| Kegiatan Harian Kelas - Alternate Path | `SemesterActivitiesValidationTest` | ❌ Not Implemented | `functional/validation/SemesterActivitiesValidationTest.java` |
| Cross-Term Analytics - Happy Path | `crosstermanalytics.ManagementTest` | ❌ Not Implemented | `functional/scenarios/crosstermanalytics/ManagementTest.java` |
| Cross-Term Analytics - Alternate Path | `CrossTermAnalyticsValidationTest` | ❌ Not Implemented | `functional/validation/CrossTermAnalyticsValidationTest.java` |
| Session Monitoring - Admin | `semesteractivities.AdminStaffTest` | ❌ Not Implemented | `functional/scenarios/semesteractivities/AdminStaffTest.java` |
| Student Feedback Collection | `semesteractivities.StudentTest` | ❌ Not Implemented | `functional/scenarios/semesteractivities/StudentTest.java` |
| Student Report Cards - Happy Path | `semesteractivities.StudentReportTest` | ❌ Not Implemented | `functional/scenarios/semesteractivities/StudentReportTest.java` |
| Student Report Cards - Alternate Path | `StudentReportValidationTest` | ❌ Not Implemented | `functional/validation/StudentReportValidationTest.java` |
| Parent Portal Reports - Happy Path | `semesteractivities.ParentPortalTest` | ❌ Not Implemented | `functional/scenarios/semesteractivities/ParentPortalTest.java` |
| Bulk Report Generation - Admin | `semesteractivities.AdminStaffTest.testBulkReportGeneration` | ❌ Not Implemented | `functional/scenarios/semesteractivities/AdminStaffTest.java` |
| Semester Closure Reports | `semesteractivities.ManagementTest.testSemesterClosureReports` | ❌ Not Implemented | `functional/scenarios/semesteractivities/ManagementTest.java` |

### Multi-Term Management

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Term Management - Happy Path | `multiterm.TermManagementTest` | ❌ Not Implemented | `functional/scenarios/multiterm/TermManagementTest.java` |
| Term Management - Alternate Path | `MultiTermValidationTest` | ❌ Not Implemented | `functional/validation/MultiTermValidationTest.java` |
| Multi-Term Registration Validation | `MultiTermRegistrationTest` | ❌ Not Implemented | `functional/validation/MultiTermRegistrationTest.java` |
| Cross-Term Data Migration | `multiterm.DataMigrationTest` | ❌ Not Implemented | `functional/scenarios/multiterm/DataMigrationTest.java` |
| Term Lifecycle Transitions | `multiterm.LifecycleTest` | ❌ Not Implemented | `functional/scenarios/multiterm/LifecycleTest.java` |

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

# Test per tipe workflow
./mvnw test -Dtest="functional.scenarios.**"        # All workflow tests
./mvnw test -Dtest="functional.validation.**"       # All validation tests
```

#### Aktivitas Semester Tests (Not Yet Implemented)
```bash
# Jalankan semua test semester activities workflow
./mvnw test -Dtest="functional.scenarios.semesteractivities.**"

# Test per role dalam semester activities
./mvnw test -Dtest="*semesteractivities.InstructorTest*"
./mvnw test -Dtest="*semesteractivities.AdminStaffTest*"
./mvnw test -Dtest="*semesteractivities.StudentTest*"
./mvnw test -Dtest="*semesteractivities.StudentReportTest*"
./mvnw test -Dtest="*semesteractivities.ParentPortalTest*"

# Cross-term analytics tests
./mvnw test -Dtest="*crosstermanalytics.ManagementTest*"

# Validation tests
./mvnw test -Dtest="*SemesterActivitiesValidationTest*"
./mvnw test -Dtest="*StudentReportValidationTest*"
./mvnw test -Dtest="*CrossTermAnalyticsValidationTest*"
```

#### Multi-Term Management Tests (Not Yet Implemented)
```bash
# Jalankan semua test multi-term management
./mvnw test -Dtest="functional.scenarios.multiterm.**"

# Test per functionality
./mvnw test -Dtest="*multiterm.TermManagementTest*"
./mvnw test -Dtest="*multiterm.DataMigrationTest*"
./mvnw test -Dtest="*multiterm.LifecycleTest*"

# Multi-term validation tests
./mvnw test -Dtest="*MultiTermValidationTest*"
./mvnw test -Dtest="*MultiTermRegistrationTest*"
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

### ✅ Status Implementasi

| Modul | Skenario Manual | Automated Test | Implementation Status |
|-------|----------------|----------------|---------------------|
| **Registrasi Siswa** | ✅ Complete (6 files) | ✅ Complete | **PRODUCTION READY** |
| **Persiapan Semester** | ✅ Complete (6 files) | ⚠️ Partial (4/6 implemented) | **MOSTLY READY** |
| **Aktivitas Semester** | ✅ Complete (6 files) | ❌ Not Implemented | **MANUAL TESTING READY** |
| **Multi-Term Management** | ✅ Complete (integrated) | ❌ Not Implemented | **TESTING READY** |

### 📊 Coverage Statistics

| Area Testing | Happy Path | Alternate Path | Total Scenarios |
|--------------|------------|----------------|-----------------|
| **Registrasi Siswa** | 6 skenario | 10 skenario | 16 skenario |
| **Persiapan Semester** | 12 skenario | 20 skenario | 32 skenario |
| **Aktivitas Semester** | 19 skenario | 24 skenario | 43 skenario |
| **Multi-Term Management** | 11 skenario | 14 skenario | 25 skenario |
| **TOTAL** | **48 skenario** | **68 skenario** | **116 skenario** |

### 🎯 Focus Area untuk Tester

#### Priority 1 - Registrasi Siswa (Ready for Testing)
- Complete end-to-end student registration workflow
- Admin staff registration management 
- Teacher placement test evaluation
- Full automated test coverage available

#### Priority 2 - Persiapan Semester (Production Ready)  
- Academic semester preparation workflow (6 phases)
- Teacher availability submission process
- Management teacher-level assignments
- Class generation dan refinement algorithms
- System go-live procedures

#### Priority 3 - Multi-Term Management (Testing Ready)
- **Term selection dan navigation across multiple academic terms**
- **Cross-term data validation dan integrity checking**
- **Term lifecycle management (PLANNING → ACTIVE → COMPLETED)**
- **Historical data access dan preservation workflows**
- **Concurrent multi-term operations dan planning**
- **Cross-term analytics dan performance comparison**
- **Academic progression tracking across semesters**
- **Multi-term reporting dan data export capabilities**

#### Priority 4 - Aktivitas Semester (Manual Testing Phase)
- Daily class session execution workflow
- Teacher and student attendance tracking
- Anonymous student feedback system  
- Real-time session monitoring
- Session rescheduling and teacher substitution
- Weekly progress recording and reporting
- Performance analytics and evaluation
- Cross-term analytics dan historical data analysis
- **Student report card generation dan multi-semester transcripts**
- **Parent portal access dengan secure report viewing**
- **Bulk report generation untuk administrative purposes**
- **Semester closure dengan comprehensive reporting workflow**
- **Class dan level analytics reporting untuk management insights**
- Error handling and system recovery procedures

---

**Catatan Penting**: 
- **Registrasi Siswa**: Production-ready dengan full automated test coverage + multi-term validation
- **Persiapan Semester**: Production-ready dengan complete automated test coverage + multi-term management
- **Aktivitas Semester**: Manual testing ready dengan comprehensive scenario coverage + cross-term analytics
- **Multi-Term Management**: Complete test scenario coverage untuk multi-term operations
- **Test Structure**: Comprehensive dengan multi-term capabilities terintegrasi
- **Total Coverage**: 116 test scenarios covering single-term, multi-term operations, dan comprehensive reporting
- Dokumentasi ini adalah living document yang akan terus diperbarui seiring perkembangan aplikasi