# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ)

Aplikasi berbasis web untuk mengelola operasional Yayasan Sahabat Quran (YSQ), sebuah lembaga pendidikan tahsin al-Quran. Aplikasi ini digunakan oleh siswa, pengajar, admin akademik, staf keuangan, dan manajemen untuk mempermudah proses pendaftaran, pengelolaan kelas, persiapan semester, dan administrasi akademik.

## Status Implementasi

### ✅ **Production Ready Features**
- **Sistem Keamanan**: Spring Security dengan RBAC (6 peran, 48+ granular permissions)
- **Student Registration System**: Multi-step registration dengan placement test dan approval workflow
- **Academic Planning Workflow**: Sistem komprehensif 6-fase persiapan semester akademik
- **Teacher Management**: Availability collection, level assignment, dan workload optimization  
- **Class Generation**: Algoritma optimisasi otomatis dengan manual refinement
- **Testing Infrastructure**: Playwright automation dengan comprehensive test coverage
- **Indonesian Documentation Generation**: Automated user manual generation system

### 🚧 **Next Phase Features**
- Advanced attendance tracking dan assessment system
- Financial management dan payment processing
- Communication system dan notifications
- Mobile applications untuk students, teachers, dan parents

---

📖 **[Detail Lengkap Fitur](docs/FEATURES.md)** | 🚀 **[Progress Implementasi](docs/IMPLEMENTATION_PROGRESS.md)**

## Teknologi yang Digunakan ##

### Tumpukan Backend
* **Framework**: Spring Boot 3.4.1
* **Keamanan**: Spring Security 6.4 dengan Otentikasi JDBC
* **Database**: PostgreSQL 17 dengan migrasi Flyway
* **Template Engine**: Thymeleaf 3.1 dengan Layout Dialect
* **Sistem Build**: Maven 3.9+

### Tumpukan Frontend
* **Styling**: TailwindCSS 3.4
* **JavaScript**: Alpine.js untuk interaktivitas
* **Ikon**: Heroicons
* **Mobile-First**: Pola desain responsif

### Testing & QA
* **Unit Tests**: JUnit 5, Mockito
* **Integration Tests**: Testcontainers (PostgreSQL)
* **Functional Tests**: Microsoft Playwright dengan video recording
* **Test Organization**: Scenarios dan validation tests dengan Page Object Model

## Persyaratan Sistem ##

Pastikan Anda memiliki perangkat lunak berikut terinstal di komputer Anda:

* Java Development Kit (JDK) 21 atau versi yang lebih baru
* Maven 3.9
* Git
* Docker (diperlukan untuk menjalankan PostgreSQL dan Testcontainers)
* Node.js dan npm (diperlukan untuk Playwright browser dependencies)

## Panduan Instalasi ##

### 1. Kloning Repository
```bash
git clone https://github.com/your-username/sahabat-quran.git
cd sahabat-quran
```

### 2. Setup Database
```bash
# Jalankan PostgreSQL dengan Docker Compose
docker-compose up -d

# Database akan otomatis ter-setup dengan schema dan data awal
```

### 3. Setup Playwright Environment (untuk Functional Tests)
```bash
# Install Playwright browser dependencies (diperlukan untuk functional tests)
sudo npx playwright install-deps

# Atau install manual dengan apt (Ubuntu/Debian)
sudo apt-get install libnss3 libnspr4 libasound2t64

# Catatan: Langkah ini opsional jika Anda hanya menjalankan unit/integration tests
# Functional tests membutuhkan browser dependencies untuk automation testing
```

### 4. Jalankan Aplikasi
```bash
# Build dan jalankan aplikasi
./mvnw spring-boot:run
```

### 5. Akses Aplikasi
- **URL**: http://localhost:8080
- **Login System Admin**: 
  - Nama Pengguna: `sysadmin`
  - Kata Sandi: `SysAdmin@YSQ2024`
- **Pengguna Sample**: Nama pengguna sesuai data seed, Kata Sandi: `Welcome@YSQ2024`

## Mulai Cepat Testing ##

### Jalankan Semua Tes
```bash
# Jalankan semua tes
./mvnw test

# Aktifkan debugging interaktif dengan Playwright Inspector
./mvnw test -Dplaywright.headless=false -Dtest="functional.**"

# Aktifkan perekaman video untuk analisis kegagalan  
./mvnw test -Dplaywright.recording=true -Dtest="functional.**"
```

### Testing Commands
```bash
# Jalankan semua tes
./mvnw test

# Tes by business process
./mvnw test -Dtest="*StudentRegistration*"
./mvnw test -Dtest="*AcademicPlanning*"

# Tes by category
./mvnw test -Dtest="functional.scenarios.**"     # Workflow tests
./mvnw test -Dtest="functional.validation.**"    # Validation tests
./mvnw test -Dtest="functional.documentation.**" # Indonesian documentation generation

# Tes dengan debugging dan recording
./mvnw test -Dtest="functional.**" -Dplaywright.recording=true
```

## Arsitektur ##

- **Backend**: Spring Boot 3.4.1 + Spring Security + PostgreSQL
- **Frontend**: Thymeleaf + TailwindCSS + Alpine.js  
- **Testing**: Playwright + Testcontainers
- **Build**: Maven + Docker Compose

## Dokumentasi

| Dokumen | Deskripsi |
|---------|-----------|
| 🛠️ [Development Guide](CLAUDE.md) | **Main development guide** - Essential commands, architecture, patterns |
| 📖 [Panduan Pengguna](docs/PANDUAN_PENGGUNA.md) | User guide untuk semua roles (siswa, staff, guru, manajemen) |
| 🔒 [Security Architecture](docs/SECURITY.md) | Spring Security configuration dan permission system |
| 🧪 [Testing Guide](docs/TESTING.md) | Playwright testing dengan debugging dan recording |
| 📊 [Features Overview](docs/FEATURES.md) | Complete feature list dengan implementation status |
| 📑 [Test Scenarios](docs/test-scenario/) | Manual testing scenarios dengan Playwright mapping |
| 🇮🇩 [Documentation Generation](docs/USER_MANUAL_GENERATION.md) | Indonesian user manual generation system |

## Akun Pengguna Default ##

Aplikasi dilengkapi dengan pengguna sample untuk testing:

| Peran | Nama Pengguna | Kata Sandi | Tingkat Akses |
|-------|---------------|------------|---------------|
| System Admin | `sysadmin` | `SysAdmin@YSQ2024` | Akses sistem teknis |
| Academic Admin | `academic.admin1` | `Welcome@YSQ2024` | Operasional akademik |
| Pengajar | `ustadz.ahmad` | `Welcome@YSQ2024` | Fungsi pengajaran |
| Siswa | `siswa.ali` | `Welcome@YSQ2024` | Portal siswa |
| Staf Keuangan | `staff.finance1` | `Welcome@YSQ2024` | Manajemen keuangan |
| Manajemen | `management.director` | `Welcome@YSQ2024` | Laporan dan analitik |
