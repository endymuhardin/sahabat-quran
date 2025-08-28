# Testing Architecture Guide

## Overview

The Sahabat Quran application implements comprehensive testing strategies across multiple layers:

- **Unit Tests**: Fast, isolated component testing
- **Integration Tests**: Database and service integration
- **Functional Tests**: End-to-end user workflow testing with Playwright

## Test Structure

```
src/test/java/
├── com/sahabatquran/webapp/
│   ├── functional/          # Playwright functional tests ✅ ORGANIZED BY BUSINESS PROCESS
│   │   ├── playwright/
│   │   │   ├── BasePlaywrightTest.java
│   │   │   ├── LoginAndNavigationPlaywrightTest.java
│   │   │   └── StudentRegistrationPlaywrightTest.java      # Student registration workflow
│   ├── integration/         # Integration tests with database
│   │   ├── BaseIntegrationTest.java
│   │   ├── AuthenticationSqlIntegrationTest.java
│   │   ├── StudentRegistrationRepositoryTest.java     # Registration repository tests
│   │   └── StudentRegistrationServiceTest.java        # Registration service tests
│   ├── repository/          # Repository layer tests
│   │   ├── SessionRepositoryTest.java
│   │   ├── PlacementTestVerseRepositoryTest.java
│   │   └── StudentRegistrationRepositoryTest.java
│   └── service/             # Service layer tests
│       └── StudentRegistrationServiceTest.java
```

## Configuration Management

### Test Properties (`application-test.properties`)

```properties
# Database configuration (Testcontainers PostgreSQL)
spring.datasource.url=jdbc:tc:postgresql:17:///testdb
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver

# Playwright timeout configuration
playwright.timeout.seconds=30
playwright.debug.enabled=false
```

### BasePlaywrightTest Class

The `BasePlaywrightTest` class provides optimized Playwright setup for functional testing:

#### Browser Configuration
- **Headless Mode**: Default headless mode for CI/CD performance
- **Viewport**: Consistent 1280x720 viewport for reliable testing
- **Automatic Cleanup**: Browser and context cleanup after each test

#### Performance Features
- **Fast Execution**: Native browser automation without WebDriver overhead
- **Built-in Waiting**: Automatic waiting for elements and network requests
- **Parallel Execution**: Support for concurrent test execution

#### Debug Features
- **Debug Mode**: `playwright.debug.enabled=true` enables visual debugging
- **Slow Motion**: Configurable slow motion for debugging
- **Screenshots**: Automatic screenshot capture on test failures
- **Video Recording**: Built-in video recording capabilities

#### Timeout Management
Timeouts are configured via properties in `BasePlaywrightTest`:
- **Default Timeout**: 30 seconds for all operations
- **Navigation Timeout**: Page navigation timeout
- **Element Timeout**: Element interaction timeout

## Playwright Test Implementation Patterns

### BasePlaywrightTest Setup

The `BasePlaywrightTest` class provides common setup for all Playwright functional tests:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BasePlaywrightTest extends BaseIntegrationTest {
    
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(!isDebugMode()));
        context = browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1280, 720));
        page = context.newPage();
    }
    
    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
```

### Playwright Test Patterns

#### Login Test Example
```java
@Test
@DisplayName("Should successfully login with valid credentials")
void shouldLoginWithValidCredentials() {
    // Navigate to login page
    page.navigate(baseUrl + "/login");
    
    // Fill login form using ID selectors
    page.fill("#username", "admin");
    page.fill("#password", "AdminYSQ@2024");
    page.click("#login-button");
    
    // Wait for navigation and verify
    page.waitForURL("**/dashboard");
    assertThat(page.locator("#user-display-name")).hasText("admin");
}
```

#### Form Validation Test Example
```java
@Test
@DisplayName("Should show validation errors for invalid registration data")
void shouldValidateRegistrationForm() {
    page.navigate(baseUrl + "/student-registration");
    
    // Submit form with invalid data
    page.fill("#nama-lengkap", "");
    page.click("#submit-button");
    
    // Verify validation messages appear
    assertThat(page.locator("#nama-lengkap-error")).isVisible();
    assertThat(page.locator("#nama-lengkap-error")).hasText("Nama lengkap wajib diisi");
}
```

## Test Implementation Patterns

### BaseIntegrationTest - Shared Database Configuration

```java
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("ysqdb_test")
            .withUsername("ysquser_test")
            .withPassword("test_password");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

### Student Registration Test Example

```java
@Test
@DisplayName("Should complete student registration workflow successfully")
void shouldCompleteStudentRegistrationWorkflow() {
    // Navigate to registration page
    page.navigate(baseUrl + "/student-registration");
    
    // Fill registration form
    page.fill("#nama-lengkap", "Ahmad Fauzi");
    page.fill("#email", "ahmad.fauzi@example.com");
    page.selectOption("#jenis-kelamin", "L");
    page.fill("#tanggal-lahir", "1995-05-15");
    page.fill("#nomor-telepon", "081234567890");
    page.fill("#alamat", "Jl. Merdeka No. 123, Jakarta");
    
    // Submit form
    page.click("#submit-button");
    
    // Verify success message
    assertThat(page.locator("#success-message")).isVisible();
    assertThat(page.locator("#success-message"))
        .hasText("Pendaftaran berhasil! Silakan tunggu konfirmasi dari admin.");
}

## Running Tests

### Standard Test Execution
```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=LoginAndNavigationPlaywrightTest

# With Spring test profile
./mvnw test -Dspring.profiles.active=test

# Run only Playwright tests
./mvnw test -Dtest="*Playwright*"
```

### Playwright Test Execution

```bash
# Run all functional tests
./mvnw test -Dtest="functional.playwright.*"

# Run specific test classes
./mvnw test -Dtest=StudentRegistrationPlaywrightTest
./mvnw test -Dtest=LoginAndNavigationPlaywrightTest

# Run tests with debug mode enabled
./mvnw test -Dtest="*Playwright*" -Dplaywright.debug.enabled=true
```

### Test Script Execution

```bash
# Use the dedicated Playwright test script
./test-playwright.sh

# Run specific test with script
./test-playwright.sh StudentRegistrationPlaywrightTest
```

### Debug Mode Testing

#### Enable Playwright Debug Mode
```bash
./mvnw test -Dplaywright.debug.enabled=true -Dtest="*Playwright*"
```
- **Visual Browser Mode**: Automatically disables headless mode for visual debugging
- **Inspector**: Opens Playwright Inspector for step-by-step execution
- **Element Highlighting**: Visual highlighting of elements being interacted with
- **Network Inspection**: View network requests and responses
- **Console Logs**: Access browser console logs for debugging

#### Built-in Recording and Screenshots
```bash
# Enable video recording (automatic on test failures)
./mvnw test -Dplaywright.video.enabled=true

# Enable screenshots on failures
./mvnw test -Dplaywright.screenshots.enabled=true
```
- **Automatic Videos**: Records videos for failed tests automatically
- **Screenshots**: Captures screenshots at failure points
- **Output Location**: Saved to `test-results/` directory
- **Multiple Formats**: Support for various video and image formats

### Advanced Configuration
```bash
# Custom timeout values
./mvnw test -Dplaywright.timeout.seconds=60 -Dtest="*Playwright*"

# Enable full debug mode with recording
./mvnw test -Dplaywright.debug.enabled=true -Dplaywright.video.enabled=true -Dtest="*Playwright*"

# Profile-based configuration
./mvnw test -Dspring.profiles.active=test,debug

# Combine with specific test execution
./mvnw test -Dtest=StudentRegistrationPlaywrightTest -Dplaywright.debug.enabled=true
```

### Practical Debugging Scenarios

#### Scenario 1: Test is Failing - Interactive Debugging
```bash
# Enable debug mode for step-by-step execution
./mvnw test -Dtest=LoginAndNavigationPlaywrightTest -Dplaywright.debug.enabled=true
```
1. Run the command above
2. Playwright Inspector opens automatically
3. Step through test execution line by line
4. Inspect elements, network requests, and console logs
5. Identify the exact failure point

#### Scenario 2: CI/CD Pipeline Failures - Video Analysis
```bash
# Enable automatic video recording for failures
./mvnw test -Dplaywright.video.enabled=true -Dtest="*Playwright*"
```
1. **Automatic Recording**: Videos saved to `test-results/` directory for failed tests
2. **Timestamps**: Each test gets its own timestamped recording
3. **Analysis**: Review video to understand failure sequence
4. **Sharing**: Share video files with team for collaborative debugging

#### Scenario 3: Comprehensive Test Analysis
```bash
# Enable full debugging with videos and screenshots
./mvnw test -Dplaywright.debug.enabled=true -Dplaywright.video.enabled=true -Dplaywright.screenshots.enabled=true -Dtest=StudentRegistrationPlaywrightTest
```

## Best Practices

### Element Selection Strategy
- **Prefer ID selectors**: `page.locator("#element-id")` for reliable targeting
- **Use data-testid attributes**: `page.locator("[data-testid='submit-button']")` for test-specific selectors
- **Avoid complex selectors**: Keep selectors simple and maintainable
- **Meaningful selector names**: `#login-button`, `#user-display-name`, `#admin-menu-button`

### Playwright-Specific Best Practices
- **Auto-waiting**: Playwright automatically waits for elements to be actionable
- **Built-in assertions**: Use `assertThat(page.locator("...")).hasText("...")` for better error messages
- **Page isolation**: Each test gets a fresh browser context for complete isolation
- **Network interception**: Use `page.route()` to mock external API calls

### Test Data Management
- **Use application-test.properties**: Environment-specific configuration
- **Testcontainers for isolation**: Fresh database per test run
- **Seed data via Flyway**: Consistent test data setup
- **Clean state**: Each test starts with a clean browser context

### Error Handling
- **Informative assertions**: Use Playwright's built-in assertion messages
- **Automatic screenshots**: Screenshots captured automatically on failures
- **Video recordings**: Enable for CI/CD pipeline debugging
- **Network logs**: Access network activity for API-related issues

### Test Organization
- **Descriptive test names**: Clear intent and expected behavior
- **Page Object Model**: Organize page interactions in dedicated classes
- **Parallel execution**: Playwright supports concurrent test execution
- **Test isolation**: Each test runs in its own browser context

## Troubleshooting

### Common Issues

#### Authentication Failures
- Verify password hashes in database
- Check SQL query syntax in SecurityConfig
- Ensure user accounts are active
- Use Playwright's network tab to inspect authentication requests

#### Element Not Found
- Verify ID attributes exist in HTML using browser dev tools
- Use `page.locator().isVisible()` to check element visibility
- Playwright automatically waits for elements, no manual waits needed
- Check browser console for JavaScript errors

#### Timeout Issues
- Adjust `playwright.timeout.seconds` property
- Check network connectivity
- Verify application startup time
- Use `page.waitForLoadState()` for page readiness

#### Browser Launch Issues
- Ensure sufficient system resources
- Check for conflicting browser processes
- Verify Playwright browser installation: `npx playwright install`

### Debug Techniques

#### Network Monitoring
```java
@Test
void debugNetworkRequests() {
    // Monitor all network requests
    page.onRequest(request -> 
        System.out.println(">> " + request.method() + " " + request.url()));
    
    page.onResponse(response -> 
        System.out.println("<< " + response.status() + " " + response.url()));
    
    // Your test actions here
    page.navigate(baseUrl + "/login");
}
```

#### Console Logs Access
```java
@Test
void debugConsoleErrors() {
    // Listen for console messages
    page.onConsoleMessage(msg -> 
        System.out.println("Console: " + msg.type() + " - " + msg.text()));
    
    // Navigate and perform actions
    page.navigate(baseUrl + "/student-registration");
}
```

#### Element State Debugging
```java
@Test
void debugElementStates() {
    Locator element = page.locator("#submit-button");
    
    System.out.println("Visible: " + element.isVisible());
    System.out.println("Enabled: " + element.isEnabled());
    System.out.println("Text: " + element.textContent());
    
    // Wait for specific state
    element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
}
```

This testing architecture ensures comprehensive coverage, maintainability, and debugging capabilities for the Sahabat Quran application using modern Playwright testing framework.