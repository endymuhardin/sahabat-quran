# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ) #

Aplikasi berbasis web ini dikembangkan untuk mengelola operasional Yayasan Sahabat Quran (YSQ), sebuah lembaga pendidikan tahsin al-Quran. Aplikasi ini akan digunakan oleh siswa, pengajar, staf administrasi, staf keuangan, dan manajemen untuk mempermudah proses pendaftaran, pengelolaan kelas, pencatatan kehadiran, keuangan, dan pelaporan.

## Ringkasan Fitur ##

Aplikasi ini menyediakan sistem manajemen lengkap untuk yayasan pendidikan Al-Quran dengan fitur-fitur berikut:

### 🔐 Keamanan & Manajemen Pengguna
- **Sistem Login Aman**: Otentikasi Spring Security JDBC dengan enkripsi BCrypt
- **Kontrol Akses Berbasis Izin**: 6 peran pengguna dengan 48+ izin granular
- **Otorisasi Berbasis Modul**: Perlindungan URL berdasarkan modul (/users/**, /classes/**, /billing/**, /events/**, /reports/**)
- **Manajemen Sesi**: Kontrol sesi bersamaan, perlindungan fiksasi sesi
- **Manajemen Profil**: Data lengkap pengguna dengan foto dan preferensi

### 📚 Manajemen Akademik
- **Program Multi-Level**: Tahsin 1-3, Tahfidz Pemula-Lanjutan dengan silabus terstruktur
- **Manajemen Kelas**: Penjadwalan fleksibel, penugasan pengajar, kapasitas terkontrol
- **Sistem Pendaftaran Siswa**: ✅ **SUDAH DIIMPLEMENTASI**
  - Formulir pendaftaran multi-bagian (Pribadi, Pendidikan, Program, Jadwal, Tes Penempatan)
  - Pemilihan program Tahsin/Tahfidz dengan referensi database
  - Sistem preferensi jadwal (7 sesi waktu dengan pilihan hari)
  - Tes penempatan dengan ayat Al-Quran dan rekaman bacaan
  - Alur persetujuan: DRAFT → DIAJUKAN → DISETUJUI/DITOLAK
  - Panel admin untuk tinjauan dan evaluasi tes penempatan

### 📊 Kehadiran & Penilaian
- **Kehadiran Digital**: Check-in QR Code, pelacakan geolokasi, sesi make-up
- **Penilaian Komprehensif**: Ujian teori/praktik, rubrik penilaian, portofolio digital
- **Pelaporan Otomatis**: Rapor otomatis, sertifikat digital, pelacakan progres

### 💰 Sistem Keuangan Terintegrasi
- **Tagihan Pintar**: Tagihan berulang, rencana pembayaran, diskon keluarga
- **Gateway Pembayaran Multi**: Pembayaran online, tanda terima otomatis, pemrosesan pengembalian dana
- **Manajemen Penggajian**: Gaji pengajar, pelacakan bonus, perhitungan pajak

### 🎯 Acara & Komunikasi
- **Manajemen Acara**: Pendaftaran online, tiket QR, pengumpulan umpan balik
- **Komunikasi Multi-Saluran**: Email, SMS, WhatsApp, notifikasi push
- **Portal Orang Tua**: Pemantauan progres real-time, pelacakan pembayaran

### 📱 Desain Mobile-First
- **Aplikasi Siswa**: Jadwal, kehadiran, nilai, pembayaran
- **Aplikasi Pengajar**: Manajemen kelas, penilaian, komunikasi
- **Aplikasi Orang Tua**: Pemantauan anak, pembayaran, komunikasi dengan guru

### 📈 Analitik & Pelaporan
- **Dashboard Real-Time**: Pemantauan KPI, analisis tren, wawasan prediktif
- **Laporan Komprehensif**: Akademik, keuangan, operasional dengan kemampuan ekspor
- **Business Intelligence**: Analisis pendapatan, retensi siswa, metrik kinerja

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
* **Functional Tests**: Microsoft Playwright ✅ **DIORGANISIR BERDASARKAN PROSES BISNIS**
  - **Pola**: `[ProsesBisnis][TipeTes]Test` untuk eksekusi selektif
  - **StudentRegistration**: Tes alur pendaftaran siswa & validasi
  - **StaffRegistration**: Tes alur kerja staff & penugasan guru
  - **TeacherRegistration**: Tes alur kerja evaluasi guru & aturan bisnis
* **Konfigurasi Tes**: Timeout yang dapat dikonfigurasi, debugging VNC, perekaman sesi
* **Pola Page Object**: Arsitektur tes standar dengan selektor berbasis ID

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

### Eksekusi Tes Selektif ✅ **FITUR BARU**
```bash
# Jalankan tes berdasarkan proses bisnis
./mvnw test -Dtest="*StudentRegistration*"
./mvnw test -Dtest="*StaffRegistration*"
./mvnw test -Dtest="*TeacherRegistration*"

# Jalankan tes berdasarkan tipe
./mvnw test -Dtest="*Workflow*"
./mvnw test -Dtest="*Validation*"

# Jalankan kombinasi spesifik
./mvnw test -Dtest="*StaffRegistration*Workflow*"
./mvnw test -Dtest="*TeacherRegistration*Validation*"
```

## Sorotan Arsitektur ##

- **Keamanan**: Spring Security JDBC dengan otorisasi berbasis izin
- **Testing**: Playwright dengan debugging interaktif dan perekaman otomatis
- **Database**: PostgreSQL dengan migrasi Flyway
- **Frontend**: Thymeleaf + TailwindCSS + Alpine.js

## Dokumentasi ##

| Dokumen | Deskripsi |
|---------|-----------|
| 📖 [Panduan Pengguna](docs/PANDUAN_PENGGUNA.md) | ✅ **LENGKAP** - Panduan lengkap untuk siswa, staff, guru, dan manajemen |
| 📋 [Skenario Tes Manual](docs/test-scenario/README.md) | ✅ **LENGKAP** - Skenario pengujian manual untuk tester manusia |
| 🔒 [Arsitektur Keamanan](docs/SECURITY.md) | Konfigurasi Spring Security dan sistem izin |
| 🧪 [Panduan Testing](docs/TESTING.md) | Testing Playwright dengan debugging interaktif dan perekaman |
| 📖 [Detail Fitur](docs/FEATURES.md) | Daftar lengkap fitur aplikasi dengan status implementasi |
| 🚀 [Progress Implementasi](docs/IMPLEMENTATION_PROGRESS.md) | Timeline dan milestone pengembangan |
| 🏗️ [Panduan Pengembangan](docs/DEVELOPMENT.md) | Panduan untuk pengembang |

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
