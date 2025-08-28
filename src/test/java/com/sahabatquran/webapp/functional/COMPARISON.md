# Selenium vs Playwright Functional Testing Comparison

## Overview

This directory contains functional tests for the Sahabat Quran web application using two different browser automation frameworks:

- **`selenium/`** - Traditional Selenium WebDriver-based tests
- **`playwright/`** - Modern Playwright-based tests

## Framework Comparison

### Test Structure

| Aspect | Selenium | Playwright |
|--------|----------|-----------|
| **Base Test Class** | `BaseSeleniumTest extends BaseIntegrationTest` | `BasePlaywrightTest extends BaseIntegrationTest` |
| **Page Objects** | `com.sahabatquran.webapp.page.*` | `com.sahabatquran.webapp.page.playwright.*` |
| **Database Setup** | Shared `BaseIntegrationTest` with PostgreSQL TestContainer | Shared `BaseIntegrationTest` with PostgreSQL TestContainer |
| **Browser Management** | WebDriverManager + manual setup | Built-in browser management |
| **Waiting Strategy** | Explicit waits with WebDriverWait | Built-in auto-waiting |

### Key Differences

#### 1. **Setup and Configuration**

**Selenium:**
```java
@BeforeEach
void setUpWebDriver() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
    WebDriverManager.chromedriver().setup();
    webDriver = new ChromeDriver(options);
    webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
}
```

**Playwright:**
```java
@BeforeAll
static void setUpPlaywright() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(true)
            .setSlowMo(50));
}
```

#### 2. **Element Interactions**

**Selenium:**
```java
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
element.clear();
element.sendKeys("admin");
```

**Playwright:**
```java
page.fill("#username", "admin");  // Auto-waits for element to be ready
```

#### 3. **Assertions**

**Selenium:**
```java
assertTrue(loginPage.isOnLoginPage(), "Should be on login page");
assertEquals("admin", dashboardPage.getUserDisplayName(), "Should display correct username");
```

**Playwright:**
```java
// Built-in assertions with auto-retry
assertThat(page).hasURL("**/login**");
assertThat(userDisplayName).containsText("admin");
```

#### 4. **Navigation and URL Handling**

**Selenium:**
```java
webDriver.get(baseUrl + "/login");
wait.until(ExpectedConditions.urlContains("/dashboard"));
```

**Playwright:**
```java
page.navigate(baseUrl + "/login");
page.waitForURL("**/dashboard");
```

## Performance Comparison

### Speed
- **Playwright**: ~30% faster execution due to built-in waiting and modern architecture
- **Selenium**: Slower due to explicit waits and JSON Wire Protocol overhead

### Reliability  
- **Playwright**: More reliable due to auto-waiting and better element detection
- **Selenium**: Prone to flaky tests due to timing issues

### Resource Usage
- **Playwright**: Lower memory footprint, efficient browser context management
- **Selenium**: Higher resource usage, especially with multiple browser instances

## Feature Comparison

| Feature | Selenium | Playwright | Winner |
|---------|----------|------------|--------|
| **Multi-browser Support** | ✅ Chrome, Firefox, Safari, Edge | ✅ Chromium, Firefox, Safari | Tie |
| **Mobile Testing** | ✅ Via mobile emulation | ✅ Built-in device emulation | Playwright |
| **Network Interception** | ❌ Limited | ✅ Full network control | Playwright |
| **Auto-waiting** | ❌ Manual explicit waits | ✅ Built-in auto-waiting | Playwright |
| **Parallel Execution** | ✅ With TestNG/JUnit | ✅ Built-in parallelization | Playwright |
| **Debugging** | ✅ Good tooling | ✅ Excellent with traces | Playwright |
| **Community/Ecosystem** | ✅ Large, mature | ✅ Growing rapidly | Selenium |
| **Learning Curve** | 📈 Moderate | 📉 Easier | Playwright |

## Code Quality Comparison

### Lines of Code (Approximate)

| Test Type | Selenium LOC | Playwright LOC | Reduction |
|-----------|--------------|----------------|-----------|
| Login Test | 150 | 100 | 33% |
| Registration Test | 300 | 200 | 33% |
| Base Test Setup | 100 | 60 | 40% |

### Maintainability
- **Playwright**: Less boilerplate, more readable code
- **Selenium**: More verbose, requires more maintenance

## Test Coverage

Both frameworks cover identical test scenarios:

### Login and Navigation Tests
- ✅ Admin login and menu visibility
- ✅ Instructor login with limited access
- ✅ Student login with appropriate menus
- ✅ Invalid credentials handling
- ✅ Logout functionality
- ✅ Protected resource access

### Student Registration Tests  
- ✅ Complete registration workflow
- ✅ Required field validation
- ✅ Form clearing functionality
- ✅ Multi-step navigation
- ✅ Data persistence between steps

## Recommendations

### Choose Selenium When:
- Working with legacy systems
- Team has extensive Selenium expertise
- Need support for older browsers
- Using existing Selenium infrastructure

### Choose Playwright When:
- Starting new automation projects
- Need faster, more reliable tests
- Want modern debugging capabilities
- Require network interception features
- Team can invest in learning new framework

## Running Tests

### Selenium Tests
```bash
mvn test -Dtest="**/selenium/*"
```

### Playwright Tests
```bash
# First time setup - install browser binaries
npx playwright install chromium

# Basic headless execution
mvn test -Dtest="*Playwright*"

# Visual mode with slow motion (great for debugging)
mvn test -Dtest="*Playwright*" -Dplaywright.headless=false -Dplaywright.slowmo=500

# Record test execution videos
mvn test -Dtest="*Playwright*" -Dplaywright.recording=true

# Performance testing (maximum speed)
mvn test -Dtest="*Playwright*" -Dplaywright.headless=true -Dplaywright.slowmo=0
```

#### Playwright Configuration
| Property | Default | Purpose |
|----------|---------|---------|
| `playwright.headless` | `true` | Browser visibility |
| `playwright.recording` | `false` | Video recording |
| `playwright.slowmo` | `50` | Action delays (ms) |

## Conclusion

While both frameworks achieve the same testing goals, **Playwright offers significant advantages** in terms of:
- **Developer Experience**: Simpler API, better debugging
- **Reliability**: Built-in waiting, fewer flaky tests  
- **Performance**: Faster execution, lower resource usage
- **Modern Features**: Network interception, better mobile support

For new projects or teams willing to invest in learning, **Playwright is recommended**. For existing Selenium projects with working infrastructure, migration should be evaluated based on maintenance burden vs. benefits.