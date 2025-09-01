# User Manual Generation System

This document describes the specialized Playwright test system designed to generate high-quality screenshots and videos for user documentation and training materials.

## Overview

The User Manual Generation system is separate from regular functional tests and is specifically optimized for creating visual documentation that can be used in user guides, training materials, and help documentation. The system now includes **automated markdown generation in Indonesian** that creates complete user guides with embedded screenshots and videos.

## Key Differences from Regular Tests

| Aspect | Regular Functional Tests | User Manual Generation Tests |
|--------|-------------------------|------------------------------|
| **Purpose** | Verify functionality works | Create user documentation |
| **Speed** | Fast execution (50ms delays) | Very slow (2000ms delays) |
| **Focus** | Technical validation | User experience demonstration |
| **Output** | Test results | Screenshots + videos + **Indonesian markdown** |
| **Logging** | Technical assertions | User-friendly explanations |
| **Visual Quality** | 1280x720 | 1920x1080 high resolution |
| **Documentation** | None | **Automated markdown generation** |
| **Language** | English | **Indonesian (Bahasa Indonesia)** |

## Directory Structure

```
src/test/java/com/sahabatquran/webapp/functional/
├── scenarios/          # Regular workflow tests
├── validation/         # Regular validation tests  
└── documentation/      # 📚 USER MANUAL GENERATION
    ├── BaseDocumentationTest.java                    # Base class for documentation tests
    ├── DocumentationCapture.java                     # 📝 Structured data capture
    ├── MarkdownDocumentationGenerator.java           # 🔄 Automated markdown generation
    └── AcademicPlanningUserGuideTest.java            # 🇮🇩 Indonesian user guide test
```

## Generated Output Structure

```
target/documentation/
├── PANDUAN_PENGGUNA_LENGKAP.md           # 📄 Complete Indonesian user guide
├── PANDUAN_PERENCANAAN_AKADEMIK.md      # 📄 Academic Planning specific guide  
├── SUMMARY.md                            # 📊 Indonesian summary and overview
├── documentation-session.json           # 📝 Structured session data
└── TestClassName/
    └── methodName_timestamp/
        ├── screenshots/
        │   ├── 01_initial_state.png
        │   ├── 02_login_admin.png
        │   ├── 03_navigate_dashboard.png
        │   └── ...
        ├── video/
        │   └── TestClassName_SUCCESS.webm
        └── documentation-session.json   # 📝 Test-specific session data
```

## Usage

### Quick Start

```bash
# Generate complete Indonesian user manual documentation (recommended)
./generate-user-manual.sh generate

# Generate only Academic Planning documentation in Indonesian
./generate-user-manual.sh academic

# Generate access control documentation
./generate-user-manual.sh access

# Clean previous documentation
./generate-user-manual.sh clean

# View generated documentation folder
./generate-user-manual.sh view
```

**Output**: The system will generate:
1. **Screenshots and videos** from Playwright tests
2. **Complete Indonesian markdown documentation** (`PANDUAN_PENGGUNA_LENGKAP.md`)
3. **Summary file** in Indonesian (`SUMMARY.md`)
4. **Structured JSON data** for further processing

### Advanced Usage

```bash
# Fast generation (1000ms delays instead of 2000ms)
./generate-user-manual.sh generate --fast

# Headless mode (no visible browser)
./generate-user-manual.sh generate --headless

# Generate specific test only (with manual markdown generation)
mvn test -Dtest="AcademicPlanningUserGuideTest"

# Generate markdown documentation manually from existing test data
mvn compile exec:java \
  -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.MarkdownDocumentationGenerator" \
  -Dexec.args="target/documentation target/documentation/PANDUAN_MANUAL.md"
```

## Automated Markdown Generation System

The system includes a comprehensive **3-step automated process**:

### Step 1: Test Execution & Data Capture
- Runs specialized Playwright tests with slow, clear demonstrations
- Captures structured documentation data in JSON format
- Records high-quality screenshots (1920x1080) and videos
- Uses `DocumentationCapture` class to store explanations, actions, and file paths

### Step 2: Markdown Generation  
- Processes captured JSON data with `MarkdownDocumentationGenerator`
- Creates comprehensive Indonesian documentation with:
  - **Table of Contents** with clickable anchors
  - **Embedded Screenshots** with descriptive captions
  - **Video Links** for complete workflow demonstrations
  - **Step-by-step Instructions** in Indonesian
  - **Professional Formatting** suitable for user guides

### Step 3: Summary & Organization
- Generates Indonesian summary file (`SUMMARY.md`)
- Organizes all artifacts in logical structure
- Provides file size and content statistics
- Creates index of all generated materials

## Key Features of Documentation Tests

### 1. Automatic Screenshot Capture

```java
// Take screenshot with descriptive name and explanation
takeScreenshot("login_form", "User login form with username and password fields");

// Demonstrate user action with automatic screenshot
demonstrateAction("Click login button", () -> {
    page.click("button[type='submit']");
});
```

### 2. Indonesian User-Friendly Explanations

```java
// Add explanatory text in Indonesian that appears in logs and generated documentation
explain("Layar ini menampilkan dashboard Fondasi Asesmen dimana administrator " +
        "dapat meninjau penyelesaian asesmen siswa sebelum merencanakan kelas.");
```

### 3. Indonesian Section Organization

```java
startSection("Fase 1 - Fondasi Asesmen");
// ... test steps ...
endSection("Fase 1 - Fondasi Asesmen");
```

### 4. Structured Data Capture

```java
// Data is automatically captured and structured for markdown generation
// Each explain(), demonstrateAction(), and section creates JSON data
docCapture.addExplanation("Penjelasan dalam bahasa Indonesia");
docCapture.addAction("Aksi yang dilakukan pengguna", "screenshots/01_action.png");
```

### 5. High-Quality Video Recording

- 1920x1080 resolution
- 2000ms delays between actions for clear visibility  
- Automatic video naming with test results
- Videos linked in generated markdown documentation
- Organized output structure

## Creating New Documentation Tests

### 1. Extend BaseDocumentationTest

```java
@DisplayName("Nama Fitur - Dokumentasi Panduan Pengguna")
class FeatureUserGuideTest extends BaseDocumentationTest {
    
    @Test 
    @DisplayName("Panduan Pengguna: Workflow Fitur Lengkap")
    void userGuide_completeFeatureWorkflow() {
        // Test dokumentasi Anda di sini dalam bahasa Indonesia
    }
}
```

### 2. Use Documentation-Specific Methods

```java
// Start a workflow section in Indonesian
startSection("Proses Pendaftaran Pengguna");

// Explain what users will see in Indonesian
explain("Form pendaftaran memungkinkan pengguna baru untuk membuat akun...");

// Demonstrate user actions in Indonesian  
demonstrateAction("Isi detail pengguna", () -> {
    page.fill("#name", "John Doe");
    page.fill("#email", "john@example.com");
});

// End the section
endSection("Proses Pendaftaran Pengguna");
```

**Important**: All explanations, section names, and action descriptions should be in **Indonesian** to match the generated documentation.

### 3. Focus on User Experience

- Use real user scenarios, not edge cases
- Include explanations in Indonesian of why users perform actions
- Show complete workflows from start to finish
- Include error states that users might encounter
- **Write all content in Indonesian** for consistency with generated documentation

## Best Practices

### For Screenshot Quality
- Use descriptive filenames and explanations
- Take screenshots at key decision points
- Capture both success and error states
- Ensure UI elements are clearly visible

### For Video Quality  
- Keep actions slow and deliberate (2000ms delays)
- Complete entire workflows in single test methods
- Avoid technical test assertions in documentation tests
- Focus on user-visible outcomes

### For Indonesian Documentation Content
- Write explanations in Indonesian as if for end users, not developers
- Include context about why actions are performed
- Show alternative paths (e.g., access denied scenarios)
- Organize content logically by user workflow
- Use consistent Indonesian terminology throughout
- Ensure cultural appropriateness for Indonesian users

## Integration with Existing Tests

The documentation tests are completely separate from regular functional tests:

- **Run regular tests**: `./test-playwright.sh test`
- **Run validation tests**: `mvn test -Dtest="*Validation*"`  
- **Run documentation tests**: `./generate-user-manual.sh generate`

This separation ensures that:
- Documentation generation doesn't slow down regular testing
- Documentation tests can be optimized for visual quality
- Different teams can work on functional vs. documentation tests
- Output is organized appropriately for each purpose

## Example Output

When you run documentation tests, you'll get:

1. **High-quality screenshots** showing each step of user workflows
2. **Smooth video recordings** demonstrating complete processes  
3. **Complete Indonesian markdown documentation** ready for immediate use
4. **Organized file structure** ready for integration into documentation
5. **User-friendly naming** that maps to documentation sections
6. **Professional formatting** suitable for official documentation

The generated artifacts can be directly used in:
- User training materials (Indonesian)
- Online help documentation  
- Video tutorials with Indonesian captions
- Step-by-step guides for Indonesian users
- Onboarding materials for Indonesian staff
- **Complete user manuals in Indonesian**

## Maintenance

Documentation tests should be updated when:
- UI changes significantly
- New features are added
- User workflows change
- Access control requirements change

The documentation system is designed to be maintainable and can be extended easily for new features as they're developed.

## Generated Indonesian Documentation Features

### Comprehensive Content Structure

The generated `PANDUAN_PENGGUNA_LENGKAP.md` includes:

1. **Complete Table of Contents** with clickable navigation
2. **Professional Headers** in Indonesian (`# Panduan Pengguna - Sistem Perencanaan Akademik`)
3. **Step-by-step Instructions** with numbered workflows
4. **Embedded Screenshots** with Indonesian captions
5. **Video Integration** with descriptive links
6. **Professional Footer** with generation timestamp in Indonesian format

### Sample Generated Content

```markdown
## 1. Login Admin dan Navigasi

**Waktu Dokumentasi:** 2025-01-07 10:30:45

### 📹 Video Tutorial

[**Tonton Video Lengkap**](video/AcademicPlanningUserGuideTest_SUCCESS.webm)

#### Langkah 1: Navigasi ke halaman login

![Navigasi ke halaman login](screenshots/01_navigasi_ke_halaman_login.png)

*Screenshot menunjukkan: Navigasi ke halaman login*

💡 **Penjelasan:** Panduan ini menunjukkan bagaimana administrator dapat mengakses dan menggunakan sistem Perencanaan Akademik...
```

### Automatic Features

- **Indonesian Date Formatting**: Uses Indonesian locale for timestamps
- **Professional Language**: Formal Indonesian suitable for official documentation  
- **Consistent Terminology**: Uses standardized Indonesian technical terms
- **Cultural Appropriateness**: Content structure matches Indonesian documentation standards
- **Ready-to-Use**: No additional editing required for publication

### Integration Ready

The generated documentation is immediately ready for:
- **Internal Training**: Staff onboarding and training programs
- **User Manuals**: Official product documentation
- **Help Systems**: Integration into web-based help systems
- **PDF Generation**: Can be converted to PDF using pandoc or similar tools
- **Web Publishing**: Ready for publishing on documentation websites