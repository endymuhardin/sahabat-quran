# GitHub Actions Workflows

This directory contains automated workflows for the Sahabat Quran project.

## Available Workflows

### 1. User Manual Generation (`user-manual.yml`)

**Purpose:** Automatically generates comprehensive Indonesian user documentation using Playwright tests and publishes to GitHub Pages.

**Triggers:**
- Push to `main` branch (when documentation-related files change)
- Manual dispatch with options:
  - `complete` - Generate all 3 workflows (Student → Staff → Teacher)  
  - `academic` - Generate academic planning documentation only
  - `student-only` - Generate student registration workflow only
  - `staff-only` - Generate staff assignment workflow only
  - `teacher-only` - Generate teacher evaluation workflow only

**Features:**
- 🏃‍♂️ **Full E2E Testing** - Runs complete Playwright tests with real database
- 📸 **Screenshot Generation** - Captures HD screenshots for each step
- 🎥 **Video Recording** - Creates demonstration videos
- 🇮🇩 **Indonesian Documentation** - Generates professional Indonesian user manual
- 📱 **Responsive Design** - Creates mobile-friendly HTML documentation
- ⚡ **Automated Publishing** - Deploys to GitHub Pages automatically

**What it generates:**
- `PANDUAN_PENGGUNA_LENGKAP.md` - Complete 3-stage workflow guide
- `SUMMARY.md` - Documentation index and summary
- Screenshots and videos for each workflow step
- Professional HTML version for web viewing

### 2. Documentation Publishing (`publish-docs.yml`)

**Purpose:** Publishes existing documentation files to GitHub Pages with a professional interface.

**Triggers:**
- Push to `main` branch (when `docs/` or `target/documentation/` changes)
- Manual dispatch

**Features:**
- 📚 **Documentation Hub** - Creates beautiful index page with navigation
- 🔄 **Auto-conversion** - Converts Markdown to HTML with styling
- 🎨 **Professional UI** - TailwindCSS-styled documentation site
- 📖 **Multiple Sources** - Combines docs from `docs/` and `target/documentation/`

## Setup Instructions

### 1. Enable GitHub Pages

1. Go to your repository → **Settings** → **Pages**
2. Under "Source", select **GitHub Actions**
3. Save the settings

### 2. Required Permissions

The workflows are configured with appropriate permissions:
```yaml
permissions:
  contents: read
  pages: write
  id-token: write
```

### 3. Manual Workflow Execution

To manually generate documentation:

1. Go to **Actions** tab in GitHub
2. Select **"Generate and Publish User Manual"**
3. Click **"Run workflow"**
4. Choose generation type:
   - **Complete**: Full 3-stage documentation (recommended)
   - **Academic**: Academic planning docs only
   - **Student-only**: Student registration workflow
   - **Staff-only**: Staff assignment workflow  
   - **Teacher-only**: Teacher evaluation workflow
5. Click **"Run workflow"**

## Documentation Output

After successful execution, documentation will be available at:
```
https://[username].github.io/[repository-name]/
```

### Main Documentation Files

- **`index.html`** - Documentation hub with navigation
- **`PANDUAN_PENGGUNA_LENGKAP.html`** - Complete user manual
- **`README.html`** - Project documentation
- **`FEATURES.html`** - Feature documentation
- **`TESTING.html`** - Testing guide
- **Screenshots and videos** - Visual documentation assets

## Workflow Details

### Environment Setup

Both workflows include:
- ☕ **Java 21** with Maven caching
- 🐘 **PostgreSQL 17** test database
- 🎭 **Playwright** with Chromium browser
- 📦 **Node.js** dependencies for Playwright

### Test Execution

The user manual generation workflow:

1. **Database Setup** - Starts PostgreSQL service
2. **Dependency Installation** - Installs Java, Maven, Playwright
3. **Test Compilation** - Compiles test classes
4. **Documentation Generation** - Runs Playwright documentation tests
5. **Markdown Generation** - Converts test data to Indonesian documentation
6. **HTML Conversion** - Creates web-friendly version
7. **GitHub Pages Publishing** - Deploys to GitHub Pages

### Error Handling

- **Database Connection** - Waits for PostgreSQL readiness
- **Test Failures** - Continues with partial documentation if some tests fail
- **Missing Files** - Provides fallback content and error reporting
- **Verification Steps** - Checks generated files before publishing

## Monitoring and Debugging

### Workflow Logs

Check the Actions tab for detailed logs:
- Database initialization logs
- Test execution output
- Screenshot/video generation status
- Documentation file verification
- Publishing results

### Common Issues

1. **Database Connection Failures**
   - Check PostgreSQL service health
   - Verify connection parameters

2. **Playwright Browser Issues**
   - Check browser installation logs
   - Verify headless mode configuration

3. **Documentation Generation Failures**
   - Check test execution logs
   - Verify template files exist
   - Check for missing dependencies

4. **Publishing Failures**
   - Verify GitHub Pages is enabled
   - Check workflow permissions
   - Ensure artifact upload succeeded

## Local Development

To test documentation generation locally:

```bash
# Generate complete documentation
./generate-user-manual.sh generate

# Generate specific workflow
./mvnw test -Dtest="StudentRegistrationUserGuideTest" \
  -Dplaywright.headless=true \
  -Dplaywright.recording=true

# Generate markdown from test data
./mvnw test-compile exec:java \
  -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.MarkdownDocumentationGenerator" \
  -Dexec.args="target/documentation target/documentation/PANDUAN_PENGGUNA_LENGKAP.md" \
  -Dexec.classpathScope="test"
```

## Customization

### Workflow Triggers

Modify the `on:` section to change when workflows run:

```yaml
on:
  push:
    branches: [ main, develop ]  # Add more branches
    paths:
      - 'src/**'                 # Expand watched paths
  schedule:
    - cron: '0 2 * * 1'         # Weekly generation
```

### Documentation Style

Customize the HTML generation in the workflow:
- Modify CSS styles
- Change page layout
- Add custom branding
- Update color schemes

### Test Configuration

Adjust test execution parameters:
- `PLAYWRIGHT_SLOWMO` - Adjust screenshot timing
- `PLAYWRIGHT_RECORDING` - Enable/disable video recording
- Test timeout values
- Browser selection

---

**📧 Support:** For issues with GitHub Actions workflows, check the Actions logs and ensure all prerequisites are met.