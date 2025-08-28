# Functional Testing Framework

This directory contains functional tests for browser automation testing of the Sahabat Quran web application using Microsoft Playwright.

## Directory Structure

```
functional/
├── BasePlaywrightTest.java              # Base test class with common setup
├── scenarios/                           # Happy path success scenarios
│   ├── LoginAndNavigationTest.java      # Successful login and navigation workflows
│   └── StudentRegistrationTest.java     # Successful registration workflows
├── validation/                          # Validation and error scenarios
│   ├── LoginValidationTest.java         # Login error handling and validation
│   └── StudentRegistrationValidationTest.java # Registration validation and errors
└── page/                                # Page Object Model classes
    ├── LoginPage.java                   # Login form interactions
    ├── DashboardPage.java               # Dashboard and navigation elements  
    └── StudentRegistrationPage.java     # Multi-step registration form
```

## Test Organization

### Scenarios (`scenarios/`)
Contains happy path tests that verify successful user workflows:
- **LoginAndNavigationTest** - Successful user authentication and role-based navigation
- **StudentRegistrationTest** - Complete successful student registration processes

### Validation (`validation/`)  
Contains tests for error handling, form validation, and edge cases:
- **LoginValidationTest** - Invalid credentials, access control, empty field validation
- **StudentRegistrationValidationTest** - Form validation, input format validation, business rule validation

### Page Objects (`page/`)
Reusable page interaction classes following the Page Object Model pattern:
- Clean separation of test logic from UI interactions
- Centralized element selectors and actions
- Easy maintenance when UI changes

## Running Tests

### All Functional Tests
```bash
# Run all functional tests
./mvnw test -Dtest="functional.**"

# Run only scenarios (happy path tests)
./mvnw test -Dtest="functional.scenarios.**"

# Run only validation tests
./mvnw test -Dtest="functional.validation.**"
```

### Specific Test Classes
```bash
# Run specific success scenario tests
./mvnw test -Dtest="LoginAndNavigationTest"
./mvnw test -Dtest="StudentRegistrationTest"

# Run specific validation tests
./mvnw test -Dtest="LoginValidationTest"
./mvnw test -Dtest="StudentRegistrationValidationTest"
```

### Debug Mode
```bash
# Visual debugging with slow motion
./mvnw test -Dtest="LoginAndNavigationTest" -Dplaywright.headless=false -Dplaywright.slowmo=1000

# Enable video recording
./mvnw test -Dtest="functional.**" -Dplaywright.recording=true
```

## Configuration Options

Tests can be configured via system properties:

| Property | Default | Description |
|----------|---------|-------------|
| `playwright.headless` | `true` | Run browser in headless mode |
| `playwright.recording` | `false` | Enable video recording |
| `playwright.slowmo` | `50` | Delay between actions (ms) |
| `playwright.recording.dir` | `target/playwright-recordings` | Video output directory |

## Test Architecture

### BasePlaywrightTest
The base class provides:
- Browser lifecycle management (setup/teardown)
- Consistent browser context with 1280x720 viewport
- Request/response logging for debugging
- Helper methods for user authentication
- Integration with Spring Boot Test and TestContainers

### Page Object Pattern
Each page class encapsulates:
- Element locators using stable ID selectors
- User actions (click, fill, select)
- State verification methods
- Navigation helpers

### Example Test Structure
```java
@Test
@DisplayName("Should complete student registration successfully")
void shouldCompleteStudentRegistration() {
    // Given - Page objects
    StudentRegistrationPage registrationPage = new StudentRegistrationPage(page);
    
    // When - User actions
    registrationPage.navigateToRegistrationPage(getBaseUrl());
    registrationPage.completeFullRegistration();
    
    // Then - Verification
    page.waitForURL("**/register/confirmation**");
    assertTrue(registrationPage.isConfirmationDisplayed());
}
```

## Best Practices

### Test Organization
- **Scenarios**: Focus on complete user workflows and positive outcomes
- **Validation**: Focus on error cases, form validation, and edge cases
- **Descriptive names**: Use clear, behavior-focused test names
- **Single responsibility**: Each test should verify one specific behavior

### Page Objects
- **Use ID selectors**: `page.locator("#element-id")` for reliable targeting
- **Avoid complex selectors**: Keep selectors simple and maintainable
- **Encapsulate interactions**: Hide implementation details from tests
- **Return appropriate types**: Return page objects for navigation, booleans for verification

### Playwright Best Practices
- **Auto-waiting**: Playwright automatically waits for elements to be actionable
- **Built-in assertions**: Use `assertThat(page.locator("...")).isVisible()`
- **Page isolation**: Each test gets a fresh browser context
- **Network interception**: Available for API mocking if needed

## Troubleshooting

### Common Issues
- **Timeout errors**: Increase timeout or check element visibility
- **Element not found**: Verify ID selectors exist in HTML
- **Network issues**: Check request/response logs in test output
- **Flaky tests**: Usually indicates timing issues - let Playwright handle waiting

### Debug Techniques
```bash
# Step-by-step visual debugging
./mvnw test -Dtest="LoginAndNavigationTest" -Dplaywright.headless=false -Dplaywright.slowmo=2000

# Video recording for failure analysis
./mvnw test -Dtest="functional.**" -Dplaywright.recording=true

# Detailed logging
./mvnw test -Dtest="LoginAndNavigationTest" -X
```

## Adding New Tests

### For Success Scenarios
1. Create test class in `scenarios/` package
2. Extend `BasePlaywrightTest`
3. Focus on complete user workflows
4. Use descriptive test names

### For Validation Tests  
1. Create test class in `validation/` package
2. Extend `BasePlaywrightTest`
3. Focus on error conditions and validation
4. Test edge cases and boundary conditions

### For New Page Objects
1. Create page class in `page/` package
2. Use constructor injection for Playwright `Page`
3. Use stable ID selectors
4. Provide clear, focused methods for interactions