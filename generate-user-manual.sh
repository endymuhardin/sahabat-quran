#!/bin/bash

# User Manual Generation Script
# Runs specialized Playwright tests designed to create high-quality screenshots and videos
# for user documentation and training materials

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
print_info() {
    echo -e "${BLUE}📚 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Print usage information
usage() {
    echo "📚 User Manual Generation Tool"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  generate       Generate complete end-to-end registration workflow documentation"
    echo "  clean          Clean previous documentation artifacts"
    echo "  view           Open generated documentation folder"
    echo ""
    echo "Options:"
    echo "  --fast      Use faster execution (less detailed screenshots)"
    echo "  --headless  Run in headless mode (no visible browser)"
    echo ""
    echo "Examples:"
    echo "  $0 generate           # Generate complete registration workflow documentation"
    echo "  $0 clean              # Clean previous documentation"
    echo "  $0 generate --fast    # Generate with faster execution"
}

# Generate complete user manual
generate_complete() {
    print_info "Generating complete user manual documentation..."
    
    # Determine execution mode
    local headless_mode="false"
    local slowmo="2000"
    
    if [[ "$1" == "--fast" ]]; then
        slowmo="1000"
        print_info "Using fast generation mode (1000ms delays)"
    fi
    
    if [[ "$1" == "--headless" ]] || [[ "$2" == "--headless" ]]; then
        headless_mode="true"
        print_info "Using headless mode"
    fi
    
    # Create documentation output directory
    mkdir -p target/documentation
    
    print_info "Step 1/3: Running documentation tests..."
    print_info "  Mode: User Manual Generation"
    print_info "  Execution Speed: ${slowmo}ms delays"
    print_info "  Output: target/documentation/"
    
    # Run registration workflow tests in correct order: Student → Staff → Teacher
    print_info "Running complete registration workflow in order..."
    
    # Step 1: Student Registration
    print_info "  1/3: Student Registration workflow..."
    mvn test -Dtest="StudentRegistrationUserGuideTest" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # Step 2: Staff Assignment workflow  
    print_info "  2/3: Staff assignment workflow..."
    mvn test -Dtest="StaffRegistrationUserGuideTest" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # Step 3: Teacher Examination workflow
    print_info "  3/3: Teacher examination workflow..."
    mvn test -Dtest="TeacherRegistrationUserGuideTest" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # Check test results
    if [ -d "target/documentation" ]; then
        # Count generated files
        local screenshot_count=$(find target/documentation -name "*.png" | wc -l)
        local video_count=$(find target/documentation -name "*.webm" | wc -l)
        
        print_success "Test documentation artifacts generated!"
        print_info "  📸 Screenshots: ${screenshot_count}"
        print_info "  📹 Videos: ${video_count}"
        
        # Step 2: Generate markdown documentation
        print_info "Step 2/3: Generating markdown documentation..."
        
        # Compile and run markdown generator
        mvn test-compile exec:java \
            -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.MarkdownDocumentationGenerator" \
            -Dexec.args="target/documentation target/documentation/PANDUAN_PENGGUNA_LENGKAP.md" \
            -Dexec.classpathScope="test" \
            -q
        
        if [ -f "target/documentation/PANDUAN_PENGGUNA_LENGKAP.md" ]; then
            print_success "Markdown documentation generated!"
            local md_size=$(du -k target/documentation/PANDUAN_PENGGUNA_LENGKAP.md | cut -f1)
            print_info "  📄 Size: ${md_size} KB"
        else
            print_warning "Markdown generation may have failed"
        fi
        
        # Step 3: Generate summary
        print_info "Step 3/3: Creating documentation summary..."
        generate_summary_file
        
        print_success "Complete user manual generation finished!"
        print_info "Generated files:"
        echo "   📄 target/documentation/PANDUAN_PENGGUNA_LENGKAP.md"
        echo "   📊 target/documentation/SUMMARY.md"
        find target/documentation -name "*.png" -o -name "*.webm" | head -10 | while read file; do
            echo "   📁 $file"
        done
        
        if [ $(find target/documentation -name "*.png" -o -name "*.webm" | wc -l) -gt 10 ]; then
            echo "   ... and $(( $(find target/documentation -name "*.png" -o -name "*.webm" | wc -l) - 10 )) more files"
        fi
        
    else
        print_error "Documentation generation failed - no output directory found"
    fi
}

# Generate summary file
generate_summary_file() {
    cat > target/documentation/SUMMARY.md << 'EOF'
# Ringkasan Dokumentasi Pengguna

## 📋 Daftar File yang Dihasilkan

### 📄 Dokumentasi Utama
- **PANDUAN_PENGGUNA_LENGKAP.md** - Panduan lengkap dengan screenshot dan video
- **SUMMARY.md** - File ringkasan ini

### 📸 Screenshot
Semua screenshot disimpan dalam folder `screenshots/` untuk setiap test:
- Format: `XX_description.png`  
- Resolusi: 1920x1080 (HD)
- Menampilkan setiap langkah workflow

### 📹 Video Tutorial  
Video demonstrasi lengkap disimpan dalam folder `video/`:
- Format: WebM (dapat diputar di browser)
- Resolusi: 1920x1080 (HD)  
- Kecepatan: Lambat untuk pembelajaran (2000ms delay)

## 🎯 Cara Menggunakan Dokumentasi

1. **Baca panduan lengkap**: Buka `PANDUAN_PENGGUNA_LENGKAP.md`
2. **Lihat screenshot**: Setiap langkah dilengkapi gambar
3. **Tonton video**: Video menunjukkan proses lengkap secara visual
4. **Ikuti step-by-step**: Panduan disusun berurutan dari login hingga selesai

## 🔄 Memperbarui Dokumentasi

Untuk memperbarui dokumentasi:
```bash
# Generate ulang dokumentasi lengkap
./generate-user-manual.sh generate
```

---
*Dokumentasi dibuat otomatis dari test Playwright*
EOF

    print_info "Summary file created: target/documentation/SUMMARY.md"
}


# Generate Student Registration documentation only
generate_student() {
    print_info "Generating Student Registration workflow documentation..."
    
    mkdir -p target/documentation
    
    print_info "Running Student Registration documentation tests..."
    mvn test -Dtest="StudentRegistrationUserGuideTest" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=2000 \
        -Dmaven.test.failure.ignore=true
    
    if [ -d "target/documentation" ]; then
        print_info "Generating markdown documentation..."
        mvn test-compile exec:java \
            -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.MarkdownDocumentationGenerator" \
            -Dexec.args="target/documentation target/documentation/PANDUAN_PENDAFTARAN_SISWA.md" \
            -Dexec.classpathScope="test" \
            -q
        
        generate_summary_file
        print_success "Student Registration documentation generated!"
        print_info "  📄 target/documentation/PANDUAN_PENDAFTARAN_SISWA.md"
    else
        print_error "Student Registration documentation generation failed"
    fi
}



# Generate all registration workflows documentation
generate_registration() {
    print_info "Generating all Registration workflows documentation..."
    
    mkdir -p target/documentation
    
    # Run registration workflow tests in correct order: Student → Staff → Teacher
    print_info "Running registration workflow in sequential order..."
    
    print_info "  1/3: Student Registration..."
    mvn test -Dtest="StudentRegistrationUserGuideTest" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=2000 \
        -Dmaven.test.failure.ignore=true
    
    print_info "  2/3: Staff Assignment..."
    mvn test -Dtest="StaffRegistrationUserGuideTest" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=2000 \
        -Dmaven.test.failure.ignore=true
    
    print_info "  3/3: Teacher Examination..."
    mvn test -Dtest="TeacherRegistrationUserGuideTest" \
        -Dplaywright.headless=false \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=2000 \
        -Dmaven.test.failure.ignore=true
    
    if [ -d "target/documentation" ]; then
        local screenshot_count=$(find target/documentation -name "*.png" | wc -l)
        local video_count=$(find target/documentation -name "*.webm" | wc -l)
        
        print_success "Registration documentation artifacts generated!"
        print_info "  📸 Screenshots: ${screenshot_count}"
        print_info "  📹 Videos: ${video_count}"
        
        print_info "Generating markdown documentation..."
        mvn test-compile exec:java \
            -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.MarkdownDocumentationGenerator" \
            -Dexec.args="target/documentation target/documentation/PANDUAN_PENDAFTARAN_LENGKAP.md" \
            -Dexec.classpathScope="test" \
            -q
        
        generate_summary_file
        print_success "All Registration workflows documentation generated!"
        print_info "  📄 target/documentation/PANDUAN_PENDAFTARAN_LENGKAP.md"
    else
        print_error "Registration workflows documentation generation failed"
    fi
}

# Clean documentation artifacts
clean() {
    print_info "Cleaning documentation artifacts..."
    rm -rf target/documentation/
    rm -rf target/surefire-reports/
    mvn clean -q
    print_success "Documentation cleanup completed"
}

# View generated documentation
view_docs() {
    if [ -d "target/documentation" ]; then
        print_info "Opening documentation folder..."
        if command -v open &> /dev/null; then
            open target/documentation  # macOS
        elif command -v xdg-open &> /dev/null; then
            xdg-open target/documentation  # Linux
        elif command -v explorer &> /dev/null; then
            explorer target/documentation  # Windows
        else
            print_info "Documentation location: $(pwd)/target/documentation"
        fi
    else
        print_warning "No documentation found. Run 'generate' command first."
    fi
}

# Main script logic
case "${1:-}" in
    generate)
        generate_complete "$2" "$3"
        ;;
    student)
        generate_student
        ;;
    registration)
        generate_registration
        ;;
    clean)
        clean
        ;;
    view)
        view_docs
        ;;
    help|--help|-h)
        usage
        ;;
    "")
        generate_complete "$2" "$3"
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        usage
        exit 1
        ;;
esac

print_info "User manual generation script completed!"