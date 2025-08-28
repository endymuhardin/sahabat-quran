# Playwright Testing Guide

This guide covers how to set up, configure, and run Playwright tests for the Sahabat Quran application.

## Prerequisites

### 1. Browser Installation

Playwright requires browser binaries to be installed. You have several options:

#### Option A: Manual Installation (Recommended)
```bash
# Download and install browsers manually
# This method gives you more control over the installation
npx playwright install chromium firefox webkit
```

#### Option B: Programmatic Installation
```bash
# Create a simple Java program to install browsers
# Note: This requires playwright-java CLI dependency
java -cp "target/test-classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" \
     com.microsoft.playwright.CLI install
```

#### Option C: Docker-based Testing (Alternative)
If browser installation fails, you can use Playwright with Docker containers:
```bash
# Run tests in Docker (requires Docker setup)
docker run --rm -v $(pwd):/work -w /work mcr.microsoft.com/playwright/java:v1.49.0 \
    mvn test -Dtest="*Playwright*"
```

### 2. System Requirements
- Java 21+
- Maven 3.8+
- Docker (for TestContainers)
- At least 2GB free disk space for browser binaries

## Configuration Options

Playwright tests can be configured using Java system properties:

| Property | Default | Description |
|----------|---------|-------------|
| `playwright.headless` | `true` | Run browser in headless mode |
| `playwright.recording` | `false` | Enable video recording of tests |
| `playwright.recording.dir` | `target/playwright-recordings` | Directory for video recordings |
| `playwright.slowmo` | `50` | Delay between actions (milliseconds) |

## Running Tests

### Basic Test Execution

```bash
# Run all Playwright tests (headless mode)
mvn test -Dtest="*Playwright*"

# Run specific Playwright test class
mvn test -Dtest="LoginAndNavigationPlaywrightTest"

# Run specific test method
mvn test -Dtest="LoginAndNavigationPlaywrightTest#shouldLoginAsAdminAndShowAllNavigationMenus"
```

### Visual Mode (Non-Headless)

Perfect for development and debugging:

```bash
# Run with browser window visible (great for development)
mvn test -Dtest="*Playwright*" -Dplaywright.headless=false

# Run with slower execution for better visibility
mvn test -Dtest="*Playwright*" -Dplaywright.headless=false -Dplaywright.slowmo=500

# Run specific test with visual feedback
mvn test -Dtest="LoginAndNavigationPlaywrightTest#shouldLoginAsAdminAndShowAllNavigationMenus" \
    -Dplaywright.headless=false -Dplaywright.slowmo=1000
```

### Recording Mode

Capture videos of test execution for debugging and documentation:

```bash
# Enable recording (saves videos to target/playwright-recordings/)
mvn test -Dtest="*Playwright*" -Dplaywright.recording=true

# Recording with visual mode (best for creating demos)
mvn test -Dtest="*Playwright*" \
    -Dplaywright.headless=false \
    -Dplaywright.recording=true \
    -Dplaywright.slowmo=300

# Custom recording directory
mvn test -Dtest="*Playwright*" \
    -Dplaywright.recording=true \
    -Dplaywright.recording.dir="./test-videos"

# Record only failing tests (useful for CI/CD)
mvn test -Dtest="*Playwright*" \
    -Dplaywright.recording=true \
    -Dsurefire.rerunFailingTestsCount=0
```

### Performance Testing Mode

Optimized for fastest execution:

```bash
# Maximum speed (headless, no delays, no recording)
mvn test -Dtest="*Playwright*" \
    -Dplaywright.headless=true \
    -Dplaywright.slowmo=0 \
    -Dplaywright.recording=false
```

### Debug Mode

Comprehensive debugging with all features enabled:

```bash
# Full debug mode
mvn test -Dtest="LoginAndNavigationPlaywrightTest#shouldLoginAsAdminAndShowAllNavigationMenus" \
    -Dplaywright.headless=false \
    -Dplaywright.recording=true \
    -Dplaywright.slowmo=1000 \
    -X  # Maven debug output
```

## Test Output Examples

### Configuration Display
When tests start, you'll see configuration information:
```
🎭 Playwright Configuration:
   Headless: false
   Recording: true
   Slow Motion: 500ms
📹 Recording enabled - videos will be saved to: target/playwright-recordings
```

### Network Traffic Logging
Playwright automatically logs HTTP requests and responses:
```
>> GET http://localhost:57709/login
<< 200 http://localhost:57709/login
>> GET https://cdn.tailwindcss.com/3.4.17
<< 200 https://cdn.tailwindcss.com/3.4.17
```

## File Structure After Running

```
project-root/
├── target/
│   ├── playwright-recordings/     # Video recordings (if enabled)
│   │   ├── test-1-LoginTest.webm
│   │   └── test-2-RegistrationTest.webm
│   ├── surefire-reports/          # Test reports
│   └── test-classes/               # Compiled test classes
└── src/test/
    └── java/
        └── functional/playwright/  # Test source code
```

## Browser Support

Playwright supports multiple browsers. You can configure which browser to use:

```java
// In BasePlaywrightTest.java, you can change the browser:

// Chromium (default)
browser = playwright.chromium().launch(launchOptions);

// Firefox
browser = playwright.firefox().launch(launchOptions);

// WebKit (Safari engine)
browser = playwright.webkit().launch(launchOptions);
```

To test with different browsers:
```bash
# You would need to modify BasePlaywrightTest to support browser selection
# For now, tests run with Chromium by default
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Browser Not Found
```
Error: Browser executable not found
```
**Solution:** Install browsers using `npx playwright install chromium`

#### 2. Permission Denied on macOS
```
Error: Permission denied when launching browser
```
**Solution:** 
```bash
# Allow unsigned executables (macOS only)
sudo spctl --master-disable
# Or specifically allow Playwright browsers
sudo xattr -d com.apple.quarantine /path/to/playwright/browsers/*
```

#### 3. Out of Memory During Recording
```
Error: OutOfMemoryError during video recording
```
**Solution:**
```bash
# Increase JVM memory
mvn test -Dtest="*Playwright*" -Dplaywright.recording=true -Xmx2g
```

#### 4. Tests Fail in CI/CD
```bash
# CI/CD optimized command
mvn test -Dtest="*Playwright*" \
    -Dplaywright.headless=true \
    -Dplaywright.recording=false \
    -Dplaywright.slowmo=0
```

#### 5. Port Conflicts
```
Error: Address already in use
```
**Solution:** Spring Boot automatically assigns random ports, but if issues persist:
```bash
# Force specific port range
mvn test -Dtest="*Playwright*" -Dserver.port=0
```

## Best Practices

### 1. Development Workflow
```bash
# During development (visual debugging)
mvn test -Dtest="YourNewTest" \
    -Dplaywright.headless=false \
    -Dplaywright.slowmo=800

# Before committing (fast validation)
mvn test -Dtest="*Playwright*"

# For creating documentation videos
mvn test -Dtest="*Playwright*" \
    -Dplaywright.headless=false \
    -Dplaywright.recording=true \
    -Dplaywright.slowmo=300
```

### 2. CI/CD Pipeline
```yaml
# Example GitHub Actions configuration
- name: Run Playwright Tests
  run: |
    mvn test -Dtest="*Playwright*" \
      -Dplaywright.headless=true \
      -Dplaywright.recording=false

- name: Upload Test Videos (on failure)
  if: failure()
  uses: actions/upload-artifact@v3
  with:
    name: playwright-videos
    path: target/playwright-recordings/
```

### 3. Local Development
```bash
# Create aliases for common commands
alias pw-dev='mvn test -Dtest="*Playwright*" -Dplaywright.headless=false -Dplaywright.slowmo=500'
alias pw-record='mvn test -Dtest="*Playwright*" -Dplaywright.recording=true -Dplaywright.headless=false'
alias pw-fast='mvn test -Dtest="*Playwright*" -Dplaywright.slowmo=0'
```

## Comparison with Selenium

| Feature | Selenium | Playwright |
|---------|----------|------------|
| **Setup Command** | `mvn test -Dtest="*selenium*"` | `mvn test -Dtest="*Playwright*"` |
| **Visual Mode** | Always visible (unless configured) | `mvn test -Dplaywright.headless=false` |
| **Recording** | External tools required | `mvn test -Dplaywright.recording=true` |
| **Speed Control** | Not built-in | `mvn test -Dplaywright.slowmo=500` |
| **Network Logging** | Manual setup required | Built-in request/response logging |

## Integration with IDEs

### IntelliJ IDEA
1. Install Playwright plugin
2. Configure run configurations with system properties:
   ```
   VM Options: -Dplaywright.headless=false -Dplaywright.slowmo=500
   ```

### VS Code
1. Install Playwright extension
2. Use integrated terminal with Maven commands

## Summary

Playwright offers powerful configuration options for different testing scenarios:

- **🏃‍♂️ Fast CI/CD**: `mvn test -Dtest="*Playwright*"`
- **👀 Visual Development**: `mvn test -Dplaywright.headless=false -Dplaywright.slowmo=500`
- **📹 Documentation**: `mvn test -Dplaywright.recording=true -Dplaywright.headless=false`
- **🔍 Debugging**: All options combined with `-X` for Maven debug output

The framework automatically handles browser lifecycle, provides detailed logging, and supports video recording out of the box, making it significantly easier to debug and document test behavior compared to traditional Selenium setups.