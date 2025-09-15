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
- **Email**: Gmail API integration with OAuth2 refresh token authentication

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
| 📧 [Gmail Integration](docs/GMAIL_INTEGRATION_SETUP.md) | Gmail API setup and configuration guide |

## 📧 Gmail Integration

The application supports sending real emails via Gmail API using OAuth2 refresh token authentication.

### Quick Setup

1. **Generate Gmail OAuth2 Refresh Token** (one-time setup):
```bash
# Run the token generator utility
./mvnw compile exec:java -Dexec.mainClass="com.sahabatquran.webapp.util.GmailTokenGenerator" \
  -Dexec.args="/path/to/your/credentials.json"

# Alternative: Run with test scope if needed
./mvnw test-compile exec:java -Dexec.mainClass="com.sahabatquran.webapp.util.GmailTokenGenerator" \
  -Dexec.args="/path/to/your/credentials.json"
```

2. **Set Environment Variables** (use values from token generator output):
```bash
export GMAIL_CLIENT_ID="your-client-id.apps.googleusercontent.com"
export GMAIL_CLIENT_SECRET="your-client-secret"
export GMAIL_REFRESH_TOKEN="your-refresh-token"
export GMAIL_NOTIFICATION_EMAIL="your-email@gmail.com"
```

3. **Run Application with Gmail Enabled**:
```bash
# Development
./mvnw spring-boot:run -Dspring.profiles.active=gmail

# Production
java -jar target/webapp.jar --spring.profiles.active=gmail
```

### Email Service Modes

| Mode | Profile | Description |
|------|---------|-------------|
| **NoopEmailService** | Default | Logs emails only, no actual sending (development/testing) |
| **GmailEmailService** | `gmail` | Sends real emails via Gmail API (production) |

### Features

- ✅ **No SMTP** - Uses Gmail API directly
- ✅ **Refresh Token** - No runtime OAuth flows needed
- ✅ **Auto-refresh** - Access tokens refresh automatically
- ✅ **Health Monitoring** - Prevents 6-month token expiration
- ✅ **Profile-based** - Easy switching between NOOP and Gmail

### Testing Email Functionality

```bash
# Test with NOOP (default - no emails sent)
./mvnw test

# Test with Gmail (requires credentials)
./mvnw test -Dspring.profiles.active=gmail

# Run specific email test
./mvnw test -Dtest="*EmailService*" -Dspring.profiles.active=gmail
```

📖 **Full Setup Guide**: See [Gmail Integration Documentation](docs/GMAIL_INTEGRATION_SETUP.md) for detailed instructions including:
- Google Cloud Console setup
- OAuth2 credentials creation
- Token generation steps
- Production deployment
- Troubleshooting guide

## 👤 Default Accounts

| Role | Username | Password | Access |
|------|----------|----------|---------|
| **System Admin** | `sysadmin` | `SysAdmin@YSQ2024` | Technical administration |
| **Academic Admin** | `academic.admin1` | `Welcome@YSQ2024` | Academic operations |
| **Instructor** | `ustadz.ahmad` | `Welcome@YSQ2024` | Teaching functions |
| **Student** | `siswa.ali` | `Welcome@YSQ2024` | Student portal |
| **Management** | `management.director` | `Welcome@YSQ2024` | Strategic oversight |