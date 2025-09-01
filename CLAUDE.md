# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Essential Commands

### Development Commands
```bash
# Start application with database
docker-compose up -d          # Start PostgreSQL container
./mvnw spring-boot:run        # Start application (http://localhost:8080)

# Build and package
./mvnw clean compile          # Compile only
./mvnw clean package          # Full build with tests

# Database management
docker-compose down           # Stop database
docker-compose up -d db       # Start database only
```

### Testing Commands
```bash
# Run all tests
./mvnw test

# Test execution patterns (enhanced structure)
./mvnw test -Dtest="functional.scenarios.**"     # Workflow tests only
./mvnw test -Dtest="functional.validation.**"    # Validation tests only
./mvnw test -Dtest="*StudentRegistration*"       # All student registration tests
./mvnw test -Dtest="*Validation*"                # All validation tests

# Playwright testing with helper script
./test-playwright.sh dev      # Visual mode with slow motion
./test-playwright.sh record   # With video recording
./test-playwright.sh debug LoginAndNavigationTest  # Debug specific test

# User Manual Documentation Generation (Indonesian)
./generate-user-manual.sh generate   # Generate complete Indonesian user documentation
./generate-user-manual.sh academic   # Generate Academic Planning docs only
./generate-user-manual.sh clean      # Clean previous documentation

# Test debugging
./mvnw test -Dtest="SomeTest" -Dplaywright.headless=false -Dplaywright.recording=true
```

## Architecture Overview

### Core Pattern
**Layered Spring Boot MVC** with clean separation:
- **Controllers** → **Services** → **Repositories** → **Entities**
- **DTOs** for data transfer between layers
- **Security** layer with RBAC (Role-Based Access Control)

### Key Domain Areas
1. **Student Registration System** - Complex multi-step registration with placement tests
2. **User Management** - RBAC with 8 permission modules, 48+ granular permissions  
3. **Academic Management** - Classes, enrollments, sessions, programs
4. **Assessment System** - Placement tests, student evaluations
5. **Audit System** - Comprehensive audit trails for critical operations

### Security Architecture
**Database-driven RBAC** with sophisticated permission model:
- **Authentication**: Custom JDBC with BCrypt, session management
- **Authorization**: Fine-grained permissions across modules (USER_*, CLASS_*, BILLING_*, etc.)
- **Schema**: Users ↔ UserRoles ↔ Roles ↔ RolePermissions ↔ Permissions
- **URL Protection**: Role-based access to endpoints (/admin/**, /management/**, etc.)

### Database Design
- **PostgreSQL 17** with **Flyway migrations** (`src/main/resources/db/migration/`)
- **UUID primary keys** throughout system for scalability
- **JSON columns** for flexible data (schedule preferences, test results)
- **Audit fields** with JPA callbacks (`@CreationTimestamp`, `@UpdateTimestamp`)

### Testing Architecture
**Four-layer testing strategy**:

1. **Integration Tests** (`BaseIntegrationTest`)
   - Testcontainers with real PostgreSQL
   - Full Spring context with actual database

2. **Functional Tests** (`BasePlaywrightTest`)
   - **Structure**: `scenarios/` (workflows) + `validation/` (form validation)
   - **Page Object Model** with dedicated page classes
   - **Intelligent Recording**: Named videos with timestamps and results
   - **Multi-user Support**: Login helpers for different roles

3. **Documentation Tests** (`BaseDocumentationTest`) - **NEW**
   - **Purpose**: Generate Indonesian user manuals with screenshots/videos
   - **Structure**: `documentation/` folder with specialized tests
   - **Features**: Automated markdown generation, structured data capture
   - **Output**: Complete Indonesian documentation with embedded media
   - **Speed**: Very slow (2000ms delays) for clear visual demonstrations

4. **Test Data** (`TestDataUtil`)
   - DataFaker for realistic test data
   - Centralized test data creation

## Development Patterns

### Entity Relationships
- All entities use **UUID** primary keys
- **JPA relationships** properly mapped with fetch strategies
- **Enum support** for status fields (`RegistrationStatus`, `SessionTimeSlot`)
- **JSON mapping** with `@JdbcTypeCode(SqlTypes.JSON)` for complex data

### Service Layer Design
- **Interface segregation** - Services implement focused interfaces
- **Transaction boundaries** properly defined
- **Business logic encapsulation** - Complex registration workflow in `StudentRegistrationService`

### Controller Patterns
- **RegistrationController** (22KB) - Main registration workflows
- **StudentRegistrationController** (18KB) - Specialized student registration  
- **DTOs** for request/response mapping
- **Validation** with Bean Validation annotations

### Authentication & Authorization
```java
// Method-level security throughout
@PreAuthorize("hasPermission(null, 'STUDENT_REG_REVIEW')")
@PreAuthorize("hasPermission(null, 'USER_CREATE')")

// Role-based URL protection in SecurityConfig
"/admin/**" -> ADMIN, MANAGEMENT
"/student-registration/**" -> ADMIN, ADMIN_STAFF  
"/api/registrations/**" -> ADMIN, MANAGEMENT
```

## Important Configuration

### Database Connection
- **Local**: PostgreSQL on port 54321 (non-standard to avoid conflicts)
- **Test**: Testcontainers with PostgreSQL 17
- **Migrations**: Flyway with versioned SQL files

### Security Configuration
- **Session Management**: Single concurrent session per user
- **Password Policy**: BCrypt with secure defaults
- **CSRF Protection**: Enabled for all forms
- **Session Fixation**: Protection enabled

### File Upload Limits
- **Max File Size**: 10MB
- **Max Request Size**: 10MB
- Used for placement test audio recordings

## Common Development Tasks

### Adding New Tests
1. **Functional Tests**: Extend `BasePlaywrightTest`
   - Use `scenarios/` for complete workflows
   - Use `validation/` for form/business rule validation
   - Follow naming: `[BusinessProcess][TestType]Test`

2. **Integration Tests**: Extend `BaseIntegrationTest`
   - Testcontainers provides isolated database
   - Use `@Transactional` for test isolation

### Working with Student Registration
- **Complex Entity**: 25+ fields including JSON preferences
- **Workflow States**: DRAFT → SUBMITTED → APPROVED/REJECTED
- **Placement Test Integration**: Audio recording submission
- **Audit Trail**: All changes tracked in `StudentRegistrationAudit`

### Database Schema Changes
1. Create new migration file: `V###__description.sql`
2. Update corresponding JPA entities
3. Test with both unit tests and functional tests
4. Consider data migration for existing records

## Login Credentials (Development)
- **Admin**: admin / AdminYSQ@2024
- **Staff**: staff.admin1 / Welcome@YSQ2024  
- **Finance**: staff.finance1 / Welcome@YSQ2024
- **Student**: siswa.ali / Welcome@YSQ2024

## Key Dependencies
- **Spring Boot 3.4.1** (Java 21)
- **Spring Security 6.4** with JDBC authentication
- **Thymeleaf 3.1** with security dialect
- **PostgreSQL 17** with Flyway migrations
- **Playwright 1.49** for functional testing
- **Testcontainers** for integration testing
- **Lombok** for boilerplate reduction

## Project Structure

```
sahabat-quran/
├── docs/                          # Documentation files
├── generate-user-manual.sh       # 🇮🇩 Indonesian documentation generator script
├── src/
│   ├── main/
│   │   ├── java/com/sahabatquran/webapp/
│   │   │   ├── config/            # Configuration classes
│   │   │   ├── controller/        # REST controllers
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── entity/            # JPA entities
│   │   │   ├── exception/         # Custom exceptions
│   │   │   ├── repository/        # JPA repositories
│   │   │   ├── security/          # Security configurations
│   │   │   ├── service/           # Business logic
│   │   │   └── util/              # Utility classes
│   │   └── resources/
│   │       ├── db/migration/      # Flyway migrations
│   │       ├── static/            # CSS, JS, images
│   │       └── templates/         # Thymeleaf templates
│   └── test/
│       └── java/com/sahabatquran/webapp/functional/
│           ├── scenarios/         # Workflow tests
│           ├── validation/        # Validation tests
│           └── documentation/     # 🇮🇩 Indonesian user manual generation
│               ├── BaseDocumentationTest.java
│               ├── DocumentationCapture.java
│               ├── MarkdownDocumentationGenerator.java
│               └── AcademicPlanningUserGuideTest.java
├── target/documentation/          # 🇮🇩 Generated Indonesian documentation
│   ├── PANDUAN_PENGGUNA_LENGKAP.md
│   └── SUMMARY.md
├── docker-compose.yml             # Docker services
├── pom.xml                        # Maven dependencies
└── README.md                      # Project documentation
```

## Development Environment Setup

### Prerequisites
- **Java**: JDK 21+
- **Maven**: 3.9+
- **Docker**: Latest stable version
- **IDE**: IntelliJ IDEA or VS Code with Java extensions

### Environment Setup
```bash
# Clone repository
git clone https://github.com/your-username/sahabat-quran.git
cd sahabat-quran

# Start development services
docker-compose up -d

# Install dependencies and run
./mvnw clean install
./mvnw spring-boot:run
```

## Database Management

### Migration Best Practices
1. **Never modify existing migrations** - Create new ones
2. **Use descriptive names** - `V003__add_user_profile_photo.sql`
3. **Test migrations** on development database first
4. **Include rollback scripts** in comments when possible

### Database Naming Conventions
- **Tables**: snake_case (e.g., `user_roles`)
- **Columns**: snake_case (e.g., `created_at`)
- **Foreign Keys**: `id_tablename` (e.g., `id_user`)
- **Indexes**: `idx_table_column` (e.g., `idx_users_email`)

## Coding Standards

### Java Code Style
- **Package naming**: `com.sahabatquran.webapp.module`
- **Class naming**: PascalCase (`UserService`)
- **Method naming**: camelCase (`findUserByUsername`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_LOGIN_ATTEMPTS`)

### Entity Design Pattern
```java
@Entity
@Table(name = "table_name")
@Data
@EqualsAndHashCode(exclude = {"lazyLoadedFields"})
@ToString(exclude = {"sensitiveFields"})
public class EntityName {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    
    @Column(name = "column_name")
    private String fieldName;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Service Layer Pattern
```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
```

## Indonesian User Documentation Generation

### Purpose
The system includes automated **Indonesian user manual generation** using specialized Playwright tests. This creates professional documentation suitable for:
- Staff training materials
- User onboarding guides  
- Official product documentation
- Help system integration

### Quick Commands
```bash
# Generate complete Indonesian user manual (recommended)
./generate-user-manual.sh generate

# Generate Academic Planning documentation only
./generate-user-manual.sh academic

# Clean previous documentation
./generate-user-manual.sh clean
```

### Generated Output
- **PANDUAN_PENGGUNA_LENGKAP.md** - Complete Indonesian user guide with screenshots
- **SUMMARY.md** - Indonesian summary and file index
- **Screenshots** - High-resolution (1920x1080) step-by-step images
- **Videos** - Clear demonstration videos with 2000ms delays
- **JSON Data** - Structured documentation data for further processing

### Key Features
- **Fully Automated**: 3-step process (test → generate → organize)
- **Indonesian Language**: Professional Indonesian documentation  
- **High Quality**: HD screenshots and videos optimized for documentation
- **Ready-to-Use**: No additional editing required
- **Structured Output**: Professional formatting with TOC, embedded media

### Implementation Details
- **Base Class**: `BaseDocumentationTest` - Specialized for documentation generation
- **Data Capture**: `DocumentationCapture` - Structured JSON data recording
- **Markdown Generator**: `MarkdownDocumentationGenerator` - Automated Indonesian documentation
- **Test Example**: `AcademicPlanningUserGuideTest` - Complete Indonesian workflow documentation

See `docs/USER_MANUAL_GENERATION.md` for complete documentation.