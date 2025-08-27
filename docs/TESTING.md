# Testing Architecture Guide

## Overview

The Sahabat Quran application implements comprehensive testing strategies across multiple layers:

- **Unit Tests**: Fast, isolated component testing
- **Integration Tests**: Database and service integration
- **Functional Tests**: End-to-end user workflow testing with Selenium

## Test Structure

```
src/test/java/
├── com/sahabatquran/webapp/
│   ├── functional/          # Selenium functional tests
│   │   ├── BaseSeleniumTest.java
│   │   └── LoginAndNavigationTest.java
│   ├── integration/         # Integration tests with database
│   │   ├── BaseIntegrationTest.java
│   │   └── AuthenticationSqlIntegrationTest.java
│   ├── config/              # Test configuration
│   │   └── SeleniumContainerFactory.java
│   └── page/                # Page Object Model classes
│       ├── LoginPage.java
│       └── DashboardPage.java
```

## Configuration Management

### Test Properties (`application-test.properties`)

```properties
# Database configuration (Testcontainers PostgreSQL)
spring.datasource.url=jdbc:tc:postgresql:17:///testdb
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver

# Selenium timeout configuration
selenium.timeout.explicit.seconds=10
selenium.timeout.implicit.seconds=5
selenium.timeout.page-load.seconds=30
selenium.timeout.script.seconds=30

# Selenium debug toggles for troubleshooting
selenium.debug.vnc.enabled=false
selenium.debug.vnc.pause.seconds=5
selenium.debug.recording.enabled=false
```

### SeleniumContainerFactory Class

The `SeleniumContainerFactory` class provides optimized Selenium WebDriver creation with architecture detection:

#### Architecture Detection
- **ARM64 (Apple Silicon)**: Uses `seleniarm/standalone-chromium:latest` for M1/M2 Macs
- **x86_64 (Intel)**: Uses `selenium/standalone-chrome:4.15.0` for Intel processors
- **Automatic Detection**: Uses `System.getProperty("os.arch")` for runtime detection

#### Performance Optimizations
- **Shared Memory**: 1GB allocation for better browser performance
- **JVM Tuning**: `-Xmx768m -XX:+UseContainerSupport` for container efficiency
- **Host Access**: Enables communication with application under test
- **Startup Timeout**: 30-second limit for container initialization

#### Debug Features (System Property-Based)
- **VNC Viewer**: `selenium.debug.vnc.enabled=true` enables live browser viewing with VNC URL display and configurable pause
- **VNC Pause**: `selenium.debug.vnc.pause.seconds=5` sets pause duration to copy VNC URL (default: 5 seconds)
- **Recording**: `selenium.debug.recording.enabled=true` attempts to enable video recording to timestamped directories
  - **Note**: Recording depends on Docker image support and may not work in all environments
  - **Alternative**: Use VNC viewer to manually record or screenshot failing tests
- **Headless Toggle**: `selenium.headless=false` enables visual browser mode for debugging

**Important**: VNC and recording features automatically disable headless mode since they require a visible browser interface.

These toggles allow switching between different testing modes without code changes:
- **CI/CD Mode**: All debug toggles disabled for fast headless testing
- **Debug Mode**: VNC enabled for live troubleshooting (automatically disables headless)
- **Recording Mode**: Recording enabled for failure analysis (automatically disables headless)
- **Full Debug Mode**: Both VNC and recording enabled for comprehensive debugging

#### Container Lifecycle Management
```java
public static synchronized WebDriver createWebDriver() {
    if (container == null) {
        container = createSeleniumContainer();
        container.start();
    }
    ChromeOptions options = createChromeOptions();
    return new RemoteWebDriver(container.getSeleniumAddress(), options);
}
```

#### Timeout Management
Timeouts are now configured via properties in `BaseSeleniumTest`:
- **Explicit Timeout**: For WebDriverWait conditions (10 seconds default)
- **Implicit Timeout**: Global element wait time (5 seconds default)
- **Page Load Timeout**: Maximum page load time (30 seconds default)
- **Script Timeout**: JavaScript execution timeout (30 seconds default)

## Page Object Pattern

### Base Page Object (`BasePage`)
All page objects inherit from `BasePage` which provides:
- Common WebDriver operations
- Standardized timeout handling
- Element waiting utilities

### Page Object Implementation

#### LoginPage.java
```java
@Component
public class LoginPage extends BasePage {
    
    // ID-based element selectors (required by test standards)
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password") 
    private WebElement passwordField;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    @FindBy(id = "error-message")
    private WebElement errorMessage;
    
    // Page interaction methods
    public void login(String username, String password) {
        waitForElementVisible(usernameField);
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        loginButton.click();
    }
}
```

#### DashboardPage.java
```java
@Component
public class DashboardPage extends BasePage {
    
    // Navigation menu elements with IDs
    @FindBy(id = "admin-menu-button")
    private WebElement adminMenuButton;
    
    @FindBy(id = "user-display-name")
    private WebElement userDisplayName;
    
    // Permission-based navigation testing
    public boolean isAdminMenuVisible() {
        return isElementVisible(adminMenuButton);
    }
    
    public String getUserDisplayName() {
        waitForElementVisible(userDisplayName);
        return userDisplayName.getText();
    }
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

### BaseSeleniumTest Setup

The `BaseSeleniumTest` extends `BaseIntegrationTest` to inherit database configuration while adding Selenium-specific functionality:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class BaseSeleniumTest extends BaseIntegrationTest {
    
    private WebDriver webDriver;
    
    // Timeout configurations from properties
    @Value("${selenium.timeout.explicit.seconds:10}")
    private int explicitTimeoutSeconds;
    
    @Value("${selenium.timeout.implicit.seconds:5}")
    private int implicitTimeoutSeconds;
    
    @Value("${selenium.timeout.page-load.seconds:30}")
    private int pageLoadTimeoutSeconds;
    
    @Value("${selenium.timeout.script.seconds:30}")
    private int scriptTimeoutSeconds;
    
    @BeforeEach
    void setUp() {
        // Create WebDriver instance using factory
        webDriver = SeleniumContainerFactory.createWebDriver();
        
        // Maximize window for consistent viewport
        webDriver.manage().window().maximize();
        
        // Apply configured timeouts from properties
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeoutSeconds));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeoutSeconds));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(scriptTimeoutSeconds));
    }
}
```

### Test Method Patterns

#### Authentication Test Pattern
```java
@Test
@DisplayName("Should successfully login with admin credentials and show all navigation menus")
void shouldLoginAsAdminAndShowAllNavigationMenus() {
    // Given
    LoginPage loginPage = new LoginPage(webDriver, getExplicitTimeout());
    DashboardPage dashboardPage = new DashboardPage(webDriver, timeout);
    
    // When
    loginPage.navigateToLoginPage(baseUrl);
    loginPage.login("admin", "AdminYSQ@2024");
    
    // Wait for navigation
    WebDriverWait wait = new WebDriverWait(webDriver, getExplicitTimeout());
    wait.until(ExpectedConditions.urlContains("/dashboard"));
    
    // Then
    assertTrue(dashboardPage.isOnDashboard());
    assertEquals("admin", dashboardPage.getUserDisplayName());
    
    // Test navigation menu visibility
    testNavigationMenuVisibility("Admin", dashboardPage::isAdminMenuVisible, true);
}
```

#### Navigation Menu Testing Pattern
```java
private void testNavigationMenuVisibility(String menuName, 
                                        Supplier<Boolean> visibilityCheck, 
                                        boolean shouldBeVisible) {
    try {
        boolean isVisible = visibilityCheck.get();
        
        if (shouldBeVisible) {
            if (isVisible) {
                System.out.println("  ✅ " + menuName + " menu is visible (expected)");
            } else {
                System.out.println("  ❌ " + menuName + " menu is not visible (should be visible)");
            }
            assertTrue(isVisible, menuName + " menu should be visible for this role");
        } else {
            if (!isVisible) {
                System.out.println("  ✅ " + menuName + " menu is hidden (expected)");
            } else {
                System.out.println("  ❌ " + menuName + " menu is visible (should be hidden)");
            }
            assertFalse(isVisible, menuName + " menu should be hidden for this role");
        }
    } catch (Exception e) {
        if (shouldBeVisible) {
            fail(menuName + " menu should exist but was not found");
        } else {
            System.out.println("  ✅ " + menuName + " menu not found (expected to be hidden)");
        }
    }
}
```

## Running Tests

### Standard Test Execution
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=LoginAndNavigationTest

# With Spring test profile
mvn test -Dspring.profiles.active=test
```

### Debug Mode Testing

#### Enable VNC Viewer Toggle
```bash
mvn test -Dselenium.debug.vnc.enabled=true
```
- **Automatic Headless Disable**: Automatically disables headless mode (no manual configuration needed)
- **VNC URL Display**: Shows VNC URL like `vnc://localhost:32768` in console output
- **Configurable Pause**: Pauses 5 seconds (default) for URL copying - adjust with `selenium.debug.vnc.pause.seconds=N`
- **Live Browser Viewing**: Connect via VNC client to watch tests execute in real-time
- **Debug Benefits**: See actual browser interactions, element selections, and failures

**Console Output Example:**
```
🔍 VNC/Recording enabled - automatically disabling headless mode
🔍 VNC Viewer Available at: vnc://localhost:32768
📋 Copy the URL above to connect with VNC viewer for debugging
⏸️  Pausing for 5 seconds to allow VNC setup...
▶️  Continuing with test execution...
```

#### Enable Session Recording Toggle
```bash
mvn test -Dselenium.debug.recording.enabled=true
```
- **Automatic Headless Disable**: Automatically disables headless mode for proper recording
- **Video Output**: Records videos to timestamped directories like `target/selenium-recordings/2025-08-27_14-30-15/`
- **Automatic Management**: Starts/stops recording per test execution
- **Failure Analysis**: Essential for debugging CI/CD pipeline failures
- **Format**: Standard MP4 videos viewable in any media player

### Advanced Configuration with Toggles
```bash
# Custom timeout values via system properties
mvn test -Dselenium.timeout.explicit.seconds=15

# Enable both debug toggles simultaneously
mvn test -Dselenium.debug.vnc.enabled=true -Dselenium.debug.recording.enabled=true

# Custom VNC pause duration
mvn test -Dselenium.debug.vnc.enabled=true -Dselenium.debug.vnc.pause.seconds=10

# Profile-based toggle configuration
mvn test -Dspring.profiles.active=test,debug
```

#### Toggle Combination Effects
- **Both Toggles Disabled**: Fast headless Chrome testing (default)
- **VNC Only**: Live browser viewing for interactive debugging
- **Recording Only**: Video capture for later analysis (still uses non-headless mode)
- **Both Enabled**: Full debug mode with live viewing and recording

### Practical Debugging Scenarios

#### Scenario 1: Test is Failing - Need Real-Time Debugging
```bash
# Enable VNC with custom pause for setup time
mvn test -Dtest=LoginAndNavigationTest -Dselenium.debug.vnc.enabled=true -Dselenium.debug.vnc.pause.seconds=10
```
1. Run the command above
2. Copy the VNC URL from console output
3. Open VNC viewer (built-in on macOS, or download VNC Viewer)
4. Connect to the URL and watch test execution live
5. Identify exactly where/why the test fails

#### Scenario 2: CI/CD Pipeline Failures - Need Video Evidence  
```bash
# Try enabling recording (may not work in all environments)
mvn test -Dselenium.debug.recording.enabled=true

# Alternative: Use VNC for manual recording/screenshots
mvn test -Dselenium.debug.vnc.enabled=true
```
1. **If recording works**: Videos saved to timestamped directories in `target/selenium-recordings/`
2. **If recording fails**: Use VNC viewer to manually capture screenshots or screen recordings
3. Share visual evidence with team for collaborative debugging

#### Scenario 3: Comprehensive Debugging Session
```bash
# Enable both VNC and recording with custom timeouts
mvn test -Dselenium.debug.vnc.enabled=true -Dselenium.debug.recording.enabled=true -Dselenium.timeout.explicit.seconds=15
```

## Best Practices

### Element Selection Strategy
- **Always use ID selectors**: `@FindBy(id = "element-id")`
- **Avoid XPath/CSS selectors**: Fragile and maintenance-heavy
- **Meaningful ID names**: `login-button`, `user-display-name`, `admin-menu-button`

### Wait Strategies
- **Explicit waits over implicit**: `WebDriverWait` with `ExpectedConditions`
- **Never use Thread.sleep()**: Unreliable and slow
- **Wait for specific conditions**: Element visibility, URL changes, text presence

### Test Data Management
- **Use application-test.properties**: Environment-specific configuration
- **Testcontainers for isolation**: Fresh database per test run
- **Seed data via Flyway**: Consistent test data setup

### Error Handling
- **Informative assertions**: Clear failure messages
- **Comprehensive logging**: Expected vs actual behavior
- **Screenshot on failure**: Automatic capture for debugging

### Test Organization
- **Descriptive test names**: Clear intent and expected behavior
- **Group related tests**: Use nested classes or method grouping
- **Parameterized tests**: Cover multiple scenarios efficiently

## Troubleshooting

### Common Issues

#### Authentication Failures
- Verify password hashes in database
- Check SQL query syntax in SecurityConfig
- Ensure user accounts are active

#### Element Not Found
- Verify ID attributes exist in HTML
- Check element visibility timing
- Use appropriate wait conditions

#### Timeout Issues
- Adjust timeout values in properties
- Check network connectivity
- Verify application startup time

#### VNC Connection Issues
- Ensure Docker is running
- Check firewall settings
- Copy VNC URL within pause window

### Debug Techniques

#### SQL Query Testing
```java
@Test
void debugAuthenticationQueries() {
    // Test user lookup query
    String userQuery = "SELECT u.username, uc.password_hash, u.is_active " +
                      "FROM users u JOIN user_credentials uc ON u.id = uc.id_user " +
                      "WHERE u.username = ?";
    
    // Test authority lookup query  
    String authQuery = "SELECT u.username, p.code as authority " +
                      "FROM users u JOIN user_roles ur ON u.id = ur.id_user " +
                      "JOIN role_permissions rp ON ur.id_role = rp.id_role " +
                      "JOIN permissions p ON rp.id_permission = p.id " +
                      "WHERE u.username = ? AND u.is_active = true";
}
```

#### Browser Console Access
```java
// Execute JavaScript for debugging
JavascriptExecutor js = (JavascriptExecutor) webDriver;
String consoleLog = js.executeScript("return console.log").toString();
```

This testing architecture ensures comprehensive coverage, maintainability, and debugging capabilities for the Sahabat Quran application.