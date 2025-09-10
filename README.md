# Aplikasi Manajemen Yayasan Sahabat Quran (YSQ)

**Islamic Education Management System** dengan comprehensive academic workflow dari student registration hingga cross-term analytics.

## 🎯 Current Status

**Overall Progress: 75%** - Production ready untuk core academic modules

### ✅ **Production Ready**
- **Student Registration**: Complete workflow dengan placement test integration
- **Semester Preparation**: 6-phase comprehensive academic planning 
- **Session Management**: Real-time monitoring dan execution
- **Student Feedback**: Anonymous feedback system dengan analytics
- **Security**: Enterprise RBAC dengan 48+ granular permissions

### 🔄 **Major Development Needed**  
- **Cross-term Analytics**: Service implementation required
- **Report Generation**: PDF export dan transcript system
- **Exam Management**: Complete workflow needed
- **Semester Closure**: Data archival system required

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.4.1 (Java 21), Spring Security 6.4, PostgreSQL 17
- **Frontend**: Thymeleaf + Bootstrap 5, responsive design  
- **Testing**: Playwright (functional), Testcontainers (integration)
- **Architecture**: Layered MVC dengan clean separation, UUID-based entities

## 🚀 Quick Start

```bash
# Clone dan setup database
git clone https://github.com/your-username/sahabat-quran.git
cd sahabat-quran
docker-compose up -d

# Run application
./mvnw spring-boot:run
```

**Access**: http://localhost:8080  
**Admin**: `sysadmin` / `SysAdmin@YSQ2024`

## 🧪 Essential Commands

```bash
# Development
./mvnw spring-boot:run                    # Start application
./mvnw test                              # Run all tests
./mvnw test -Dtest="*StudentRegistration*" # Business process tests

# Documentation  
./generate-user-manual.sh generate       # Generate Indonesian user manual

# Debugging
./mvnw test -Dplaywright.headless=false  # Visual test debugging
```

## 📚 Documentation

| Guide | Description |
|-------|-------------|
| 🛠️ [**CLAUDE.md**](CLAUDE.md) | **Primary development guide** - Architecture, commands, patterns |
| 📊 [Project Overview](docs/PROJECT_OVERVIEW.md) | Implementation status dan development priorities |
| 🔒 [Security Guide](docs/SECURITY.md) | Spring Security configuration dan RBAC |
| 🧪 [Testing Guide](docs/TESTING.md) | Playwright automation dan debugging |
| 📋 [Test Scenarios](docs/test-scenario/) | Comprehensive testing scenarios |
| 🇮🇩 [User Manual](docs/USER_MANUAL_GENERATION.md) | Automated Indonesian documentation |

## 👤 Default Accounts

| Role | Username | Password | Access |
|------|----------|----------|---------|
| **System Admin** | `sysadmin` | `SysAdmin@YSQ2024` | Technical administration |
| **Academic Admin** | `academic.admin1` | `Welcome@YSQ2024` | Academic operations |
| **Instructor** | `ustadz.ahmad` | `Welcome@YSQ2024` | Teaching functions |
| **Student** | `siswa.ali` | `Welcome@YSQ2024` | Student portal |
| **Management** | `management.director` | `Welcome@YSQ2024` | Strategic oversight |