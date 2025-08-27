# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ) #

Aplikasi berbasis web ini dikembangkan untuk mengelola operasional Yayasan Sahabat Quran (YSQ), sebuah lembaga pendidikan tahsin al-Quran. Aplikasi ini akan digunakan oleh siswa, pengajar, staf administrasi, staf keuangan, dan manajemen untuk mempermudah proses pendaftaran, pengelolaan kelas, pencatatan kehadiran, keuangan, dan pelaporan.

## Ringkasan Fitur ##

Aplikasi ini menyediakan sistem manajemen lengkap untuk yayasan pendidikan Al-Quran dengan fitur-fitur berikut:

### 🔐 Keamanan & Manajemen Pengguna
- **Sistem Login Aman**: Spring Security JDBC authentication dengan enkripsi BCrypt
- **Permission-Based Access Control**: 6 role pengguna dengan 48+ permission granular
- **Module-Based Authorization**: URL protection berdasarkan module (/users/**, /classes/**, /billing/**, /events/**, /reports/**)
- **Session Management**: Concurrent session control, session fixation protection
- **Manajemen Profil**: Data lengkap pengguna dengan foto dan preferensi

### 📚 Manajemen Akademik
- **Multi-Level Program**: Tahsin 1-3, Tahfidz Pemula-Lanjutan dengan silabus terstruktur
- **Manajemen Kelas**: Penjadwalan fleksibel, assignment instructor, kapasitas terkontrol
- **Sistem Enrollment**: Pendaftaran online, approval workflow, transfer kelas

### 📊 Kehadiran & Penilaian
- **Digital Attendance**: QR Code check-in, geolocation tracking, make-up sessions
- **Comprehensive Assessment**: Ujian teori/praktik, rubrik penilaian, portfolio digital
- **Automated Reporting**: Rapor otomatis, sertifikat digital, progress tracking

### 💰 Sistem Keuangan Terintegrasi
- **Smart Billing**: Recurring billing, payment plans, family discounts
- **Multi-Payment Gateway**: Online payment, receipt otomatis, refund processing
- **Payroll Management**: Gaji instructor, bonus tracking, tax calculation

### 🎯 Event & Komunikasi
- **Event Management**: Registrasi online, QR tickets, feedback collection
- **Multi-Channel Communication**: Email, SMS, WhatsApp, push notifications
- **Parent Portal**: Real-time progress monitoring, payment tracking

### 📱 Mobile-First Design
- **Student App**: Schedule, attendance, grades, payments
- **Instructor App**: Class management, grading, communication
- **Parent App**: Child monitoring, payments, teacher communication

### 📈 Analytics & Reporting
- **Real-Time Dashboard**: KPI monitoring, trend analysis, predictive insights
- **Comprehensive Reports**: Academic, financial, operational dengan export capabilities
- **Business Intelligence**: Revenue analysis, student retention, performance metrics

---

📖 **[Detail Lengkap Fitur](docs/FEATURES.md)** | 🚀 **[Progress Implementasi](docs/IMPLEMENTATION_PROGRESS.md)**

## Teknologi yang Digunakan ##

### Backend Stack
* **Framework**: Spring Boot 3.4.1
* **Security**: Spring Security 6.4 with JDBC Authentication
* **Database**: PostgreSQL 17 with Flyway migrations
* **Template Engine**: Thymeleaf 3.1 with Layout Dialect
* **Build System**: Maven 3.9+

### Frontend Stack
* **Styling**: TailwindCSS 3.4
* **JavaScript**: Alpine.js for interactivity
* **Icons**: Heroicons
* **Mobile-First**: Responsive design patterns

### Testing & QA
* **Unit Tests**: JUnit 5, Mockito
* **Integration Tests**: Testcontainers (PostgreSQL)
* **Functional Tests**: Selenium WebDriver 4.15
* **Test Configuration**: Configurable timeouts, VNC debugging, session recording
* **Page Object Pattern**: Standardized test architecture with ID-based selectors

## Persyaratan Sistem ##

Pastikan Anda memiliki perangkat lunak berikut terinstal di komputer Anda:

* Java Development Kit (JDK) 21 atau versi yang lebih baru
* Maven 3.9
* Git
* Docker (diperlukan untuk menjalankan PostgreSQL dan Testcontainers)

## Panduan Instalasi ##

### 1. Clone Repository
```bash
git clone https://github.com/your-username/sahabat-quran.git
cd sahabat-quran
```

### 2. Setup Database
```bash
# Start PostgreSQL dengan Docker Compose
docker-compose up -d

# Database akan otomatis ter-setup dengan schema dan data awal
```

### 3. Run Application
```bash
# Build dan jalankan aplikasi
./mvnw spring-boot:run
```

### 4. Akses Aplikasi
- **URL**: http://localhost:8080
- **Admin Login**: 
  - Username: `admin`
  - Password: `AdminYSQ@2024`
- **Sample Users**: Username sesuai data seed, Password: `Welcome@YSQ2024`

## Quick Start Testing ##

```bash
# Run all tests
mvn test

# Enable VNC debugging for live browser viewing
mvn test -Dselenium.debug.vnc.enabled=true

# Enable session recording for failure analysis  
mvn test -Dselenium.debug.recording.enabled=true
```

## Architecture Highlights ##

- **Security**: Spring Security JDBC with permission-based authorization
- **Testing**: Selenium with VNC debugging and session recording toggles
- **Database**: PostgreSQL with Flyway migrations
- **Frontend**: Thymeleaf + TailwindCSS + Alpine.js

## Dokumentasi ##

| Dokumen | Deskripsi |
|---------|-----------|
| 🔒 [Security Architecture](docs/SECURITY.md) | Spring Security configuration dan permission system |
| 🧪 [Testing Guide](docs/TESTING.md) | Selenium testing dengan VNC debugging dan recording |
| 📖 [Detail Fitur](docs/FEATURES.md) | Daftar lengkap fitur aplikasi dengan status implementasi |
| 🚀 [Progress Implementasi](docs/IMPLEMENTATION_PROGRESS.md) | Timeline dan milestone development |
| 🏗️ [Development Guide](docs/DEVELOPMENT.md) | Panduan untuk developer |
| 🔧 [API Documentation](docs/API.md) | REST API endpoints dan usage |

## Default User Accounts ##

Aplikasi dilengkapi dengan sample users untuk testing:

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| Admin | `admin` | `AdminYSQ@2024` | Full system access |
| Instructor | `ustadz.ahmad` | `Welcome@YSQ2024` | Teaching functions |
| Student | `siswa.ali` | `Welcome@YSQ2024` | Student portal |
| Admin Staff | `staff.admin1` | `Welcome@YSQ2024` | Administrative tasks |
| Finance Staff | `staff.finance1` | `Welcome@YSQ2024` | Financial management |
| Management | `management.director` | `Welcome@YSQ2024` | Reports and analytics |
