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
└── persiapan-semester/                # 📁 Skenario Persiapan Semester Akademik
    ├── persiapan-semester-happy-path.md
    ├── persiapan-semester-alternate-path.md
    ├── availability-submission-happy-path.md
    └── availability-submission-alternate-path.md
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
- Manajemen registrasi oleh admin staff
- Evaluasi tes penempatan oleh teacher
- Assignment workflow dan approval process

### 📊 Persiapan Semester
Skenario pengujian untuk proses akademik persiapan semester dan class generation.

**Coverage Area:**
- Assessment foundation dan data validation
- Level distribution analysis
- Teacher availability collection dan submission
- Management teacher-level assignments
- Automated class generation dan manual refinement
- Final review dan system go-live

## Pemetaan dengan Automated Test

### Registrasi Siswa

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Pendaftaran Siswa - Happy Path | `registration-workflow.StudentTest` | ✅ Implemented | `scenarios/registration-workflow/StudentTest.java` |
| Pendaftaran Siswa - Alternate Path | `StudentRegistrationValidationTest` | ✅ Implemented | `validation/StudentRegistrationValidationTest.java` |
| Admin Registrasi - Happy Path | `registration-workflow.AdminStaffTest` | ✅ Implemented | `scenarios/registration-workflow/AdminStaffTest.java` |
| Admin Registrasi - Alternate Path | `StaffRegistrationValidationTest` | ✅ Implemented | `validation/StaffRegistrationValidationTest.java` |
| Tes Penempatan - Happy Path | `registration-workflow.InstructorTest` | ✅ Implemented | `scenarios/registration-workflow/InstructorTest.java` |
| Tes Penempatan - Alternate Path | `TeacherRegistrationValidationTest` | ✅ Implemented | `validation/TeacherRegistrationValidationTest.java` |

### Persiapan Semester

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Persiapan Semester - Happy Path | `term-preparation-workflow.AdminStaffTest` | ✅ Implemented | `scenarios/term-preparation-workflow/AdminStaffTest.java` |
| Persiapan Semester - Alternate Path | `AcademicPlanningValidationTest` | ✅ Implemented | `validation/AcademicPlanningValidationTest.java` |
| Teacher Availability - Happy Path | `term-preparation-workflow.InstructorTest` | ✅ Implemented | `scenarios/term-preparation-workflow/InstructorTest.java` |
| Management Level Assignment | `term-preparation-workflow.ManagementTest` | ✅ Implemented | `scenarios/term-preparation-workflow/ManagementTest.java` |

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
   - **Admin Staff**: `staff.admin1` / `Welcome@YSQ2024`
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
./mvnw test -Dtest="functional.scenarios.registration-workflow.**"

# Test per role dalam registration
./mvnw test -Dtest="*registration-workflow.StudentTest*"
./mvnw test -Dtest="*registration-workflow.AdminStaffTest*"
./mvnw test -Dtest="*registration-workflow.InstructorTest*"
./mvnw test -Dtest="*registration-workflow.ManagementTest*"

# Validation tests
./mvnw test -Dtest="*StudentRegistrationValidationTest*"
./mvnw test -Dtest="*StaffRegistrationValidationTest*"
./mvnw test -Dtest="*TeacherRegistrationValidationTest*"
```

#### Persiapan Semester Tests
```bash
# Jalankan semua test term preparation workflow
./mvnw test -Dtest="functional.scenarios.term-preparation-workflow.**"

# Test per role dalam term preparation
./mvnw test -Dtest="*term-preparation-workflow.AdminStaffTest*"
./mvnw test -Dtest="*term-preparation-workflow.InstructorTest*"  
./mvnw test -Dtest="*term-preparation-workflow.ManagementTest*"

# Academic planning validation tests
./mvnw test -Dtest="*AcademicPlanningValidationTest*"

# Test per tipe workflow
./mvnw test -Dtest="functional.scenarios.**"        # All workflow tests
./mvnw test -Dtest="functional.validation.**"       # All validation tests
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
| **Persiapan Semester** | ✅ Complete (4 files) | ✅ Complete | **PRODUCTION READY** |

### 📊 Coverage Statistics

| Area Testing | Happy Path | Alternate Path | Total Scenarios |
|--------------|------------|----------------|-----------------|
| **Registrasi Siswa** | 6 skenario | 8 skenario | 14 skenario |
| **Persiapan Semester** | 8 skenario | 12 skenario | 20 skenario |
| **TOTAL** | **14 skenario** | **20 skenario** | **34 skenario** |

### 🎯 Focus Area untuk Tester

#### Priority 1 - Registrasi Siswa (Ready for Testing)
- Complete end-to-end student registration workflow
- Admin staff registration management 
- Teacher placement test evaluation
- Full automated test coverage available

#### Priority 2 - Persiapan Semester (Manual Testing Phase)  
- Academic semester preparation workflow (6 phases)
- Teacher availability submission process
- Management teacher-level assignments
- Class generation dan refinement algorithms
- System go-live procedures

---

**Catatan Penting**: 
- **Registrasi Siswa**: Production-ready dengan full automated test coverage
- **Persiapan Semester**: Production-ready dengan complete automated test coverage
- **Test Structure**: Refactored dengan role-based organization (`registration-workflow/` dan `term-preparation-workflow/`)
- Dokumentasi ini adalah living document yang akan terus diperbarui seiring perkembangan aplikasi