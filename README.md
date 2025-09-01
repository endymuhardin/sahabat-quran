# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ) #

Aplikasi berbasis web ini dikembangkan untuk mengelola operasional Yayasan Sahabat Quran (YSQ), sebuah lembaga pendidikan tahsin al-Quran. Aplikasi ini akan digunakan oleh siswa, pengajar, staf administrasi, staf keuangan, dan manajemen untuk mempermudah proses pendaftaran, pengelolaan kelas, pencatatan kehadiran, keuangan, dan pelaporan.

## Fitur Utama ##

### ✅ **Sudah Diimplementasi**
- **Sistem Keamanan**: Spring Security dengan RBAC (6 peran, 48+ izin)
- **Pendaftaran Siswa Lengkap**: Formulir multi-bagian dengan tes penempatan
- **Manajemen Program**: 6 level (Tahsin 1-3, Tahfidz Pemula-Lanjutan)
- **Testing Komprehensif**: Playwright automation dengan video recording
- **🆕 Workflow Persiapan Kelas**: Sistem komprehensif 6-fase dari assessment hingga go-live
- **🆕 Manajemen Ketersediaan Guru**: Sistem pengumpulan ketersediaan dan assignment otomatis
- **🆕 Generasi Kelas Otomatis**: Algoritma optimisasi dengan refinement manual

### 🚧 **Dalam Pengembangan**
- Sistem kehadiran dan penilaian
- Modul keuangan dan pembayaran
- Komunikasi dan notifikasi
- Functional testing untuk workflow persiapan kelas

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
- **Login Admin**: 
  - Nama Pengguna: `admin`
  - Kata Sandi: `AdminYSQ@2024`
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

# Tes berdasarkan proses bisnis
./mvnw test -Dtest="*StudentRegistration*"

# Tes fungsional dengan video recording
./mvnw test -Dtest="functional.**" -Dplaywright.recording=true
```

## Arsitektur ##

- **Backend**: Spring Boot 3.4.1 + Spring Security + PostgreSQL
- **Frontend**: Thymeleaf + TailwindCSS + Alpine.js  
- **Testing**: Playwright + Testcontainers
- **Build**: Maven + Docker Compose

## Dokumentasi ##

| Dokumen | Deskripsi |
|---------|-----------|
| 📖 [Panduan Pengguna](docs/PANDUAN_PENGGUNA.md) | Panduan lengkap untuk siswa, staff, guru, dan manajemen |
| 🔒 [Arsitektur Keamanan](docs/SECURITY.md) | Konfigurasi Spring Security dan sistem izin |
| 🧪 [Panduan Testing](docs/TESTING.md) | Testing Playwright dengan debugging dan perekaman |
| 📖 [Detail Fitur](docs/FEATURES.md) | Daftar lengkap fitur dengan status implementasi |
| 🚀 [Progress Implementasi](docs/IMPLEMENTATION_PROGRESS.md) | Timeline dan milestone pengembangan |

## Akun Pengguna Default ##

Aplikasi dilengkapi dengan pengguna sample untuk testing:

| Peran | Nama Pengguna | Kata Sandi | Tingkat Akses |
|-------|---------------|------------|---------------|
| Admin | `admin` | `AdminYSQ@2024` | Akses sistem penuh |
| Pengajar | `ustadz.ahmad` | `Welcome@YSQ2024` | Fungsi pengajaran |
| Siswa | `siswa.ali` | `Welcome@YSQ2024` | Portal siswa |
| Staf Admin | `staff.admin1` | `Welcome@YSQ2024` | Tugas administratif |
| Staf Keuangan | `staff.finance1` | `Welcome@YSQ2024` | Manajemen keuangan |
| Manajemen | `management.director` | `Welcome@YSQ2024` | Laporan dan analitik |
