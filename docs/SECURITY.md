# Security Architecture Guide

## Overview

The Sahabat Quran application implements a comprehensive security architecture based on Spring Security 6.4 with JDBC authentication and permission-based authorization.

## Authentication System

### Spring Security JDBC Configuration

The application uses Spring Security's built-in `JdbcUserDetailsManager` with custom SQL queries:

```java
@Bean
public JdbcUserDetailsManager jdbcUserDetailsManager() {
    JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    
    // Custom user lookup query
    userDetailsManager.setUsersByUsernameQuery(
        "SELECT u.username, uc.password_hash, u.is_active " +
        "FROM users u " +
        "JOIN user_credentials uc ON u.id = uc.id_user " +
        "WHERE u.username = ?"
    );
    
    // Custom authorities query using permission codes
    userDetailsManager.setAuthoritiesByUsernameQuery(
        "SELECT u.username, p.code as authority " +
        "FROM users u " +
        "JOIN user_roles ur ON u.id = ur.id_user " +
        "JOIN role_permissions rp ON ur.id_role = rp.id_role " +
        "JOIN permissions p ON rp.id_permission = p.id " +
        "WHERE u.username = ? AND u.is_active = true"
    );
    
    return userDetailsManager;
}
```

### Password Security

- **Encryption**: BCrypt with default strength (10 rounds)
- **Storage**: Passwords stored in `user_credentials` table with `password_hash` column
- **Sample Passwords**:
  - System Admin: `SysAdmin@YSQ2024`
  - Others: `Welcome@YSQ2024`

## Authorization System

### Permission-Based Authorization

The system uses **permission-based** rather than role-based authorization for flexibility:

#### Database Schema
```sql
-- Users and credentials
users (id, username, full_name, email, is_active)
user_credentials (id, id_user, password_hash)

-- Role system
roles (id, name, description)
user_roles (id, id_user, id_role, assigned_at, id_assigned_by)

-- Permission system
permissions (id, code, name, description, module)
role_permissions (id, id_role, id_permission)
```

#### Permission Codes Structure
Permissions follow the pattern: `MODULE_ACTION`

**User Management Module:**
- `USER_VIEW`, `USER_CREATE`, `USER_EDIT`, `USER_DELETE`, `USER_ACTIVATE`

**Academic Management Module:**
- `CLASS_VIEW`, `CLASS_CREATE`, `CLASS_EDIT`, `CLASS_DELETE`, `CLASS_SCHEDULE_MANAGE`
- `ENROLLMENT_VIEW`, `ENROLLMENT_CREATE`, `ENROLLMENT_EDIT`, `ENROLLMENT_APPROVE`
- `ATTENDANCE_VIEW`, `ATTENDANCE_MARK`, `ATTENDANCE_EDIT`, `ATTENDANCE_REPORT`
- `ASSESSMENT_VIEW`, `ASSESSMENT_CREATE`, `ASSESSMENT_EDIT`, `ASSESSMENT_GRADE`

**Finance Management Module:**
- `BILLING_VIEW`, `BILLING_CREATE`, `BILLING_EDIT`
- `PAYMENT_VIEW`, `PAYMENT_VERIFY`, `PAYMENT_RECORD`
- `SALARY_VIEW`, `SALARY_CALCULATE`, `SALARY_APPROVE`

**Event Management Module:**
- `EVENT_VIEW`, `EVENT_CREATE`, `EVENT_EDIT`, `EVENT_DELETE`, `EVENT_REGISTER`

**System & Reporting Module:**
- `REPORT_OPERATIONAL`, `REPORT_FINANCIAL`, `REPORT_ACADEMIC`, `REPORT_EXPORT`
- `SYSTEM_CONFIG`, `BACKUP_RESTORE`, `AUDIT_LOG_VIEW`

### URL Protection Configuration

URLs are protected based on **module organization**, not roles:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/error").permitAll()
            
            // User Management Module
            .requestMatchers("/users/**")
                .hasAnyAuthority("USER_VIEW", "USER_CREATE", "USER_EDIT", "USER_DELETE", "USER_ACTIVATE")
                
            // Academic Management Module  
            .requestMatchers("/classes/**")
                .hasAnyAuthority("CLASS_VIEW", "CLASS_CREATE", "CLASS_EDIT", "CLASS_DELETE", "CLASS_SCHEDULE_MANAGE")
            .requestMatchers("/enrollments/**")
                .hasAnyAuthority("ENROLLMENT_VIEW", "ENROLLMENT_CREATE", "ENROLLMENT_EDIT", "ENROLLMENT_APPROVE", "ENROLLMENT_CANCEL")
            .requestMatchers("/attendance/**")
                .hasAnyAuthority("ATTENDANCE_VIEW", "ATTENDANCE_MARK", "ATTENDANCE_EDIT", "ATTENDANCE_REPORT")
            .requestMatchers("/assessments/**")
                .hasAnyAuthority("ASSESSMENT_VIEW", "ASSESSMENT_CREATE", "ASSESSMENT_EDIT", "ASSESSMENT_GRADE")
                
            // Finance Management Module
            .requestMatchers("/billing/**")
                .hasAnyAuthority("BILLING_VIEW", "BILLING_CREATE", "BILLING_EDIT")
            .requestMatchers("/payments/**")
                .hasAnyAuthority("PAYMENT_VIEW", "PAYMENT_VERIFY", "PAYMENT_RECORD")
            .requestMatchers("/payroll/**")
                .hasAnyAuthority("SALARY_VIEW", "SALARY_CALCULATE", "SALARY_APPROVE")
                
            // Event Management Module
            .requestMatchers("/events/**")
                .hasAnyAuthority("EVENT_VIEW", "EVENT_CREATE", "EVENT_EDIT", "EVENT_DELETE", "EVENT_REGISTER", "EVENT_MANAGE_REGISTRATION")
                
            // System & Reporting Module
            .requestMatchers("/reports/**")
                .hasAnyAuthority("REPORT_OPERATIONAL", "REPORT_FINANCIAL", "REPORT_ACADEMIC", "REPORT_EXPORT", "DASHBOARD_VIEW")
            .requestMatchers("/system/**")
                .hasAnyAuthority("SYSTEM_CONFIG", "BACKUP_RESTORE", "AUDIT_LOG_VIEW")
                
            .anyRequest().authenticated()
        );
}
```

## Frontend Security Integration

### Thymeleaf Security Integration

Navigation menus use permission-based visibility control:

```html
<!-- Academic Admin menu - requires any academic admin permission -->
<div sec:authorize="hasAnyAuthority('USER_VIEW', 'USER_CREATE', 'USER_EDIT', 'USER_DELETE', 'USER_ACTIVATE')">
    <button id="academic-admin-menu-button" @click="open = !open">
        <span>Academic Admin</span>
    </button>
</div>

<!-- Academic menu - requires any academic permission -->
<div sec:authorize="hasAnyAuthority('CLASS_VIEW', 'ENROLLMENT_VIEW', 'ATTENDANCE_VIEW', 'ASSESSMENT_VIEW')">
    <button id="academic-menu-button" @click="open = !open">
        <span>Academic</span>
    </button>
</div>

<!-- User information display -->
<span id="user-display-name" sec:authentication="name">User Name</span>
```

### Alpine.js Integration (CSP-Compliant)

The application uses **Alpine.js CSP-compliant build** (`@alpinejs/csp@3.15.2`) which requires special patterns for event handlers:

**CSP Compliance Requirements:**
- Inline JavaScript expressions in event handlers are NOT supported
- All event handlers must call methods defined in the `x-data` object
- Use `this.$el` to access element data attributes within methods

**Correct Pattern (CSP-compliant):**
```html
<div x-data="feedbackForm()">
    <!-- Click calls a method, not inline expression -->
    <button x-on:click="handleStarClick"
            :data-rating="1">★</button>
</div>

<script>
function feedbackForm() {
    return {
        handleStarClick() {
            const rating = parseInt(this.$el.dataset.rating);
            this.setRating(rating);
        },
        setRating(rating) {
            this.answers[questionId] = rating;
        }
    }
}
</script>
```

**Incorrect Pattern (NOT CSP-compliant):**
```html
<!-- This will NOT work with CSP-compliant Alpine.js -->
<button x-on:click="setRating($el.dataset.rating)">★</button>
```

JavaScript interactions respect server-side security context:

```html
<div x-data="{ open: false }" class="relative">
    <button @click="open = !open" sec:authorize="hasAuthority('REPORT_OPERATIONAL')">
        Reports
    </button>
    <div x-show="open" @click.away="open = false">
        <!-- Dropdown content -->
    </div>
</div>
```

## Session Management

### Session Security Configuration

```java
.sessionManagement(session -> session
    .maximumSessions(1)                    // One session per user
    .maxSessionsPreventsLogin(false)       // Allow new login to invalidate old session
)
.sessionManagement(session -> session
    .sessionFixation().migrateSession()    // Protection against session fixation attacks
)
```

### Login/Logout Configuration

```java
.formLogin(form -> form
    .loginPage("/login")
    .loginProcessingUrl("/login")
    .defaultSuccessUrl("/dashboard", true)
    .failureUrl("/login?error=true")
    .permitAll()
)
.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login?logout=true")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .permitAll()
)
```

## Role-Permission Mapping

### Default Role Assignments

| Role | Permissions | Access Level |
|------|-------------|--------------|
| **System Administrator** | SYSTEM_*, AUDIT_*, USER_* (system level), VIEW permissions | Technical system administration |
| **Academic Admin** | USER_*, CLASS_*, ENROLLMENT_*, ASSESSMENT_*, ACADEMIC_PLANNING_*, EVENT_* | Academic operations |
| **Instructor** | CLASS_*, ENROLLMENT_VIEW, ATTENDANCE_*, ASSESSMENT_* | Teaching functions |
| **Student** | CLASS_VIEW, ENROLLMENT_VIEW, ASSESSMENT_VIEW, PAYMENT_VIEW, EVENT_REGISTER | Student portal access |
| **Finance Staff** | BILLING_*, PAYMENT_*, SALARY_*, REPORT_FINANCIAL | Financial management |
| **Management** | REPORT_*, DASHBOARD_VIEW, ACADEMIC_PLANNING_*, TEACHER_LEVEL_ASSIGN | Strategic oversight |

### Permission Assignment Strategy

1. **Module-Based Grouping**: Permissions grouped by functional modules
2. **Action-Based Naming**: Clear action verbs (VIEW, CREATE, EDIT, DELETE, etc.)
3. **Granular Control**: Fine-grained permissions for specific operations
4. **Role Flexibility**: Roles can be modified without code changes

## Security Testing

### Authentication Tests

Tests verify proper login behavior and error handling:

```java
@Test
@DisplayName("Should successfully login with academic admin credentials and show all navigation menus")
void shouldLoginAsAcademicAdminAndShowAllNavigationMenus() {
    LoginPage loginPage = new LoginPage(webDriver, timeout);
    DashboardPage dashboardPage = new DashboardPage(webDriver, timeout);
    
    loginPage.navigateToLoginPage(baseUrl);
    loginPage.login("academic.admin1", "Welcome@YSQ2024");
    
    // Verify successful authentication
    assertTrue(dashboardPage.isOnDashboard());
    assertEquals("academic.admin1", dashboardPage.getUserDisplayName());
    
    // Verify navigation menu visibility based on permissions
    testNavigationMenuVisibility("Academic Admin", dashboardPage::isAcademicAdminMenuVisible, true);
    testNavigationMenuVisibility("Academic", dashboardPage::isAcademicMenuVisible, true);
}
```

### SQL Query Testing

Integration tests verify authentication queries work correctly:

```java
@Test
void shouldAuthenticateUserWithCorrectCredentials() {
    // Test user lookup query
    String userQuery = "SELECT u.username, uc.password_hash, u.is_active " +
                      "FROM users u JOIN user_credentials uc ON u.id = uc.id_user " +
                      "WHERE u.username = ?";
    
    List<Map<String, Object>> userResult = jdbcTemplate.queryForList(userQuery, "academic.admin1");
    assertFalse(userResult.isEmpty());
    
    // Test authority lookup query
    String authQuery = "SELECT u.username, p.code as authority " +
                      "FROM users u JOIN user_roles ur ON u.id = ur.id_user " +
                      "JOIN role_permissions rp ON ur.id_role = rp.id_role " +
                      "JOIN permissions p ON rp.id_permission = p.id " +
                      "WHERE u.username = ? AND u.is_active = true";
    
    List<Map<String, Object>> authResult = jdbcTemplate.queryForList(authQuery, "academic.admin1");
    assertFalse(authResult.isEmpty());
}
```

## Security Best Practices

### Password Management
- Use strong default passwords
- Enforce password complexity rules
- Implement password expiration policies
- Provide secure password reset functionality

### Permission Design
- Follow principle of least privilege
- Use descriptive permission names
- Group permissions by functional modules
- Regular permission audits

### Session Security
- Implement session timeout
- Use HTTPS in production
- Secure session cookies
- Monitor concurrent sessions

### Input Validation
- Validate all user inputs
- Use parameterized SQL queries
- Implement CSRF protection
- Sanitize output data

### Monitoring and Auditing
- Log authentication attempts
- Track permission changes
- Monitor suspicious activities
- Implement audit trails

This security architecture provides a robust, flexible, and maintainable security system for the Sahabat Quran application.