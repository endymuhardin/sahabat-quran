# Development Guide

## 📋 Table of Contents
1. [Project Structure](#project-structure)
2. [Development Environment](#development-environment)
3. [Database Management](#database-management)
4. [Security Implementation](#security-implementation)
5. [Coding Standards](#coding-standards)
6. [Testing Strategy](#testing-strategy)
7. [Deployment Guide](#deployment-guide)

## 🏗️ Project Structure

```
sahabat-quran/
├── docs/                          # Documentation files
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
│   └── test/                      # Test files
├── docker-compose.yml             # Docker services
├── pom.xml                        # Maven dependencies
└── README.md                      # Project documentation
```

## 🛠️ Development Environment

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

### Development Properties
Create `application-dev.properties` for development-specific configurations:
```properties
# Development Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ysqdb_dev
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf Development
spring.thymeleaf.cache=false

# Logging
logging.level.com.sahabatquran.webapp=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 🗄️ Database Management

### Migration Strategy
We use Flyway for database versioning:

```
src/main/resources/db/migration/
├── V001__create_schema.sql       # Initial schema
├── V002__initial_data_seed.sql   # Master data
├── V003__add_feature_xyz.sql     # Future migrations
└── ...
```

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

## 🔒 Security Implementation

### Authentication Flow
1. User submits credentials via login form
2. `CustomUserDetailsService` loads user from database
3. Password verified against BCrypt hash
4. `CustomUserDetails` populated with roles and permissions
5. Security context established with authorities

### Permission System
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
public void createAssessment() { ... }

// Template-level security
<div sec:authorize="hasRole('ADMIN')">
    Admin-only content
</div>
```

### Role Hierarchy
```
ADMIN > MANAGEMENT > {ADMIN_STAFF, FINANCE_STAFF} > INSTRUCTOR > STUDENT
```

### Adding New Permissions
1. Add permission to `V002__initial_data_seed.sql`
2. Assign to appropriate roles
3. Use in `@PreAuthorize` annotations or templates

## 📝 Coding Standards

### Java Code Style
- **Package naming**: `com.sahabatquran.webapp.module`
- **Class naming**: PascalCase (`UserService`)
- **Method naming**: camelCase (`findUserByUsername`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_LOGIN_ATTEMPTS`)

### Entity Design Patterns
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

### Controller Pattern
```java
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/users/{id}")
    @PreAuthorize("hasPermission('USER_VIEW')")
    public String viewUser(@PathVariable UUID id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "users/view";
    }
}
```

## 🧪 Testing Strategy

### Test Structure
```
src/test/java/com/sahabatquran/webapp/
├── controller/           # Web layer tests
├── repository/           # Data access tests  
├── service/              # Business logic tests
├── security/             # Security tests
└── integration/          # Integration tests
```

### Unit Test Example
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldFindUserById() {
        // Given
        UUID userId = UUID.randomUUID();
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        
        // When
        User actualUser = userService.findById(userId);
        
        // Then
        assertThat(actualUser).isEqualTo(expectedUser);
    }
}
```

### Integration Test Example
```java
@Transactional
class UserRepositoryIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindUserByUsername() {
        // Given
        String username = "testuser";
        
        // When
        Optional<User> user = userRepository.findByUsername(username);
        
        // Then
        assertThat(user).isPresent();
    }
}
```

### Security Test Example
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@WithMockUser(roles = "ADMIN")
class SecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldAllowAdminAccess() throws Exception {
        mockMvc.perform(get("/admin/users"))
            .andExpect(status().isOk());
    }
}
```

## 🚀 Deployment Guide

### Production Environment Setup

#### 1. Database Configuration
```bash
# Create production database
createdb -h production-db-host -U postgres ysqdb_prod

# Run migrations
./mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://production-db-host:5432/ysqdb_prod
```

#### 2. Application Properties
```properties
# Production configuration
spring.profiles.active=prod
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

# Security
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true

# Logging
logging.level.root=WARN
logging.level.com.sahabatquran.webapp=INFO
```

#### 3. Docker Deployment
```dockerfile
FROM eclipse-temurin:21-jre-alpine
COPY target/webapp-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    image: sahabat-quran-webapp:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://postgres:5432/ysqdb
    depends_on:
      - postgres
    ports:
      - "8080:8080"

  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: ysqdb
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
```

#### 4. CI/CD Pipeline (GitHub Actions)
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production
on:
  push:
    branches: [main]
jobs:
  test-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - run: ./mvnw clean test
      - run: ./mvnw clean package
      - name: Deploy to production
        run: |
          # Deployment script here
```

## 🔧 Common Development Tasks

### Adding a New Entity
1. Create entity class in `entity/` package
2. Create repository interface in `repository/` package  
3. Create service class in `service/` package
4. Create controller in `controller/` package
5. Create Thymeleaf templates
6. Add database migration if needed
7. Write unit and integration tests

### Adding a New Permission
1. Add permission to permissions insert statement in migration
2. Assign permission to appropriate roles
3. Use permission in `@PreAuthorize` or templates
4. Test with appropriate user roles

### Database Schema Changes
1. **Never modify existing migrations**
2. Create new migration file: `V00X__descriptive_name.sql`
3. Test migration on development database
4. Update entity classes if needed
5. Run tests to ensure compatibility

## 🐛 Debugging Tips

### Common Issues
1. **Migration Failures**: Check Flyway migration history table
2. **Authentication Issues**: Check user roles and permissions
3. **Database Connection**: Verify Docker container is running
4. **Template Errors**: Check Thymeleaf template syntax

### Logging Configuration
```properties
# Enable SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Enable security logging  
logging.level.org.springframework.security=DEBUG

# Enable web request logging
logging.level.org.springframework.web=DEBUG
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

**Last Updated:** 27 Agustus 2024  
**Maintained By:** Development Team