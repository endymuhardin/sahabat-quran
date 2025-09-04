# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ)

Aplikasi berbasis web untuk mengelola operasional Yayasan Sahabat Quran (YSQ), sebuah lembaga pendidikan tahsin al-Quran. Sistem ini mendukung complete workflow dari registrasi siswa hingga pelaporan akademik dengan automated testing dan user manual generation.

## 🎯 Status Implementasi Terkini

**Overall Progress: 65% (130/200+ features) - Production Ready untuk Core Modules**

### ✅ **Production Ready (Phase 1-3 Complete)**
- **Security & Authentication**: Spring Security 6.4 dengan 48+ granular permissions, role-based access control
- **Student Registration System**: Complete multi-step workflow dengan placement test dan approval process
- **Academic Planning Workflow**: 6-phase comprehensive semester preparation dengan automated class generation
- **Teacher Management**: Availability collection, competency tracking, workload optimization
- **Testing Infrastructure**: 4-layer testing strategy dengan Playwright automation (31% scenario coverage)
- **Indonesian Documentation**: Automated HD user manual generation system

### 🚧 **In Development (Phase 4-6 Planned)**
- **Financial Management**: Billing, payment processing, financial reporting (Q1 2025)
- **Advanced Academic Features**: Attendance tracking, assessment system, communication (Q2 2025) 
- **Mobile & Analytics**: Mobile apps, business intelligence, advanced reporting (Q3 2025)

---

📊 **[Implementation Progress](docs/IMPLEMENTATION_PROGRESS.md)** | 🔍 **[Feature Details](docs/FEATURES.md)**

## 🛠️ Tech Stack

### Backend Architecture
- **Framework**: Spring Boot 3.4.1 dengan Java 21
- **Security**: Spring Security 6.4 + JDBC authentication + BCrypt
- **Database**: PostgreSQL 17 dengan Flyway migrations
- **Templates**: Thymeleaf 3.1 dengan Security Dialect
- **Build**: Maven 3.9+ dengan Docker Compose

### Frontend Stack  
- **UI Framework**: TailwindCSS 3.4 + Alpine.js
- **Icons**: Heroicons
- **Design**: Mobile-first responsive design
- **Components**: Reusable Thymeleaf fragments

### Quality Assurance
- **Testing Strategy**: 4-layer architecture (Unit → Integration → Functional → Documentation)
- **Automation**: Playwright browser automation dengan video recording
- **Coverage**: 31% automated scenario coverage (36/116 scenarios)
- **CI/CD Ready**: Testcontainers untuk isolated database testing

## 🔧 Prerequisites

**Required:**
- **Java**: JDK 21+
- **Maven**: 3.9+  
- **Docker**: Latest stable (untuk PostgreSQL dan testing)
- **Git**: Latest

**Optional (untuk functional testing):**
- **Node.js**: Latest LTS (untuk Playwright browser dependencies)
- **Browser dependencies**: Auto-installed via `npx playwright install-deps`

## 🚀 Quick Start

### 1. Clone & Setup
```bash
git clone https://github.com/your-username/sahabat-quran.git
cd sahabat-quran

# Start PostgreSQL database
docker-compose up -d

# Run application 
./mvnw spring-boot:run
```

### 2. Access Application
- **URL**: http://localhost:8080
- **System Admin**: `sysadmin` / `SysAdmin@YSQ2024`
- **Sample Users**: See [default accounts](#-default-accounts) below

### 3. Optional: Functional Testing Setup
```bash
# Install Playwright browser dependencies (Linux/macOS)
sudo npx playwright install-deps

# Or Ubuntu/Debian specific
sudo apt-get install libnss3 libnspr4 libasound2t64
```

## 🧪 Testing & Quality

### Essential Commands
```bash
# Run all tests
./mvnw test

# Business process tests
./mvnw test -Dtest="*StudentRegistration*"      # Student registration workflow
./mvnw test -Dtest="*AcademicPlanning*"         # Academic planning workflow

# Test by type
./mvnw test -Dtest="functional.scenarios.**"     # End-to-end workflows
./mvnw test -Dtest="functional.validation.**"    # Form validation tests

# Indonesian documentation generation
./generate-user-manual.sh generate
```

### Debug & Analysis
```bash
# Visual debugging dengan Playwright Inspector
./mvnw test -Dplaywright.headless=false -Dtest="functional.**"

# Video recording untuk failure analysis
./mvnw test -Dplaywright.recording=true -Dtest="functional.**"
```

## 🏗️ Architecture

Layered Spring Boot MVC dengan clean separation:
- **Controllers** → **Services** → **Repositories** → **Entities**  
- **Security**: Permission-based RBAC dengan 48+ granular permissions
- **Database**: PostgreSQL 17 dengan UUID primary keys dan JSON support
- **Testing**: 4-layer strategy (Unit → Integration → Functional → Documentation)

## 📚 Documentation

| Guide | Description |
|-------|-------------|
| 🛠️ [**CLAUDE.md**](CLAUDE.md) | **Main development guide** - Commands, architecture, patterns |
| 📊 [Implementation Progress](docs/IMPLEMENTATION_PROGRESS.md) | Milestone status dan development timeline |
| 🔍 [Feature Details](docs/FEATURES.md) | Complete feature catalog dengan implementation status |
| 🔒 [Security Guide](docs/SECURITY.md) | Spring Security configuration dan RBAC system |
| 🧪 [Testing Guide](docs/TESTING.md) | Playwright automation dengan debugging tools |
| 📋 [Test Scenarios](docs/test-scenario/) | 116 manual testing scenarios dengan automation mapping |
| 🇮🇩 [User Manual Generation](docs/USER_MANUAL_GENERATION.md) | Automated Indonesian documentation system |
| 👥 [User Guide](docs/PANDUAN_PENGGUNA.md) | End-user guide untuk semua roles |

## 👤 Default Accounts

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| **System Admin** | `sysadmin` | `SysAdmin@YSQ2024` | Technical system administration |
| **Academic Admin** | `academic.admin1` | `Welcome@YSQ2024` | Academic operations management |
| **Instructor** | `ustadz.ahmad` | `Welcome@YSQ2024` | Teaching functions |
| **Student** | `siswa.ali` | `Welcome@YSQ2024` | Student portal access |
| **Finance Staff** | `staff.finance1` | `Welcome@YSQ2024` | Financial management |
| **Management** | `management.director` | `Welcome@YSQ2024` | Strategic oversight dan reporting |
