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
    echo "  generate       Generate complete system documentation (registration + term preparation)"
    echo "  clean          Clean previous documentation artifacts"
    echo "  view           Open generated documentation folder"
    echo ""
    echo "Options:"
    echo "  --fast      Use faster execution (less detailed screenshots)"
    echo "  --headless  Run in headless mode (no visible browser)"
    echo ""
    echo "Examples:"
    echo "  $0 generate           # Generate complete system documentation"
    echo "  $0 clean              # Clean previous documentation"
    echo "  $0 generate --fast    # Generate with faster execution"
}

# Generate complete user manual
generate_complete() {
    print_info "Generating complete system documentation..."
    
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
    
    print_info "Step 1/4: Running documentation tests..."
    print_info "  Mode: User Manual Generation"
    print_info "  Execution Speed: ${slowmo}ms delays"
    print_info "  Output: target/documentation/"
    
    # PART 1: Registration Workflow
    print_info "Part 1: Registration Workflow Documentation..."
    
    # Step 1: Student Registration
    print_info "  1/5: Student Registration workflow..."
    mvn test -Dtest="functional.documentation.registration.StudentRegistrationUserGuideTest" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # Step 2: Staff Assignment workflow  
    print_info "  2/5: Staff assignment workflow..."
    mvn test -Dtest="functional.documentation.registration.StaffRegistrationUserGuideTest" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # Step 3: Teacher Examination workflow
    print_info "  3/5: Teacher examination workflow..."
    mvn test -Dtest="functional.documentation.registration.TeacherRegistrationUserGuideTest" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # PART 2: Term Preparation Workflow
    print_info "Part 2: Term Preparation Workflow Documentation..."
    
    # Step 4: Complete Term Preparation workflow
    print_info "  4/5: Complete Term Preparation workflow..."
    mvn test -Dtest="functional.documentation.termpreparation.TermPreparationDocumentationTest#userGuide_completeTermPreparationWorkflow" \
        -Dplaywright.headless=${headless_mode} \
        -Dplaywright.recording=true \
        -Dplaywright.slowmo=${slowmo} \
        -Dmaven.test.failure.ignore=true
    
    # Step 5: Teacher Availability Submission
    print_info "  5/5: Teacher Availability Submission guide..."
    mvn test -Dtest="functional.documentation.termpreparation.TermPreparationDocumentationTest#userGuide_teacherAvailabilitySubmission" \
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
        
        # Step 2: Generate unified markdown documentation
        print_info "Step 2/4: Generating unified markdown documentation..."
        
        # Generate unified system documentation
        generate_unified_documentation
        
        # Step 3: Generate individual workflow documentation
        print_info "Step 3/4: Generating individual workflow documentation..."
        
        # Generate registration workflow markdown
        mvn test-compile exec:java \
            -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.MarkdownDocumentationGenerator" \
            -Dexec.args="target/documentation target/documentation/PANDUAN_PENDAFTARAN.md" \
            -Dexec.classpathScope="test" \
            -q
        
        # Generate term preparation workflow markdown
        mvn test-compile exec:java \
            -Dexec.mainClass="com.sahabatquran.webapp.functional.documentation.termpreparation.TermPreparationMarkdownGenerator" \
            -Dexec.args="target/documentation target/documentation/PANDUAN_PERSIAPAN_SEMESTER.md" \
            -Dexec.classpathScope="test" \
            -q
        
        # Step 4: Generate summary
        print_info "Step 4/4: Creating documentation summary..."
        generate_complete_summary_file
        
        print_success "Complete user manual generation finished!"
        print_info "Generated files:"
        echo "   📄 target/documentation/PANDUAN_SISTEM_LENGKAP.md - Complete system documentation"
        echo "   📄 target/documentation/PANDUAN_PENDAFTARAN.md - Registration workflow guide"
        echo "   📄 target/documentation/PANDUAN_PERSIAPAN_SEMESTER.md - Term preparation guide"
        echo "   📊 target/documentation/SUMMARY.md - Documentation summary"
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

# Generate complete summary file for unified documentation
generate_complete_summary_file() {
    cat > target/documentation/SUMMARY.md << 'EOF'
# 📚 Dokumentasi Sistem Yayasan Sahabat Quran

## 🗂️ Struktur Dokumentasi

### 📖 Dokumentasi Utama
1. **PANDUAN_SISTEM_LENGKAP.md** - Panduan lengkap sistem (pendaftaran + persiapan semester)
2. **PANDUAN_PENDAFTARAN.md** - Workflow pendaftaran siswa (3 tahap)
3. **PANDUAN_PERSIAPAN_SEMESTER.md** - Workflow persiapan semester (6 fase)
4. **SUMMARY.md** - File ringkasan ini

### 🎯 Workflow yang Didokumentasikan

#### A. Workflow Pendaftaran (Registration)
1. **Tahap 1: Pendaftaran Siswa**
   - Siswa mendaftar online
   - Upload dokumen dan audio placement test
   - Submit untuk review

2. **Tahap 2: Penugasan oleh Admin**
   - Admin akademik review pendaftaran
   - Assign ke guru untuk evaluasi
   - Monitor status penugasan

3. **Tahap 3: Evaluasi oleh Guru**
   - Guru review placement test
   - Berikan rekomendasi level
   - Approve atau reject pendaftaran

#### B. Workflow Persiapan Semester (Term Preparation)
1. **Fase 1: Assessment Foundation**
   - Monitor placement test siswa baru
   - Validasi hasil ujian siswa lama
   - Tentukan level assignment

2. **Fase 2: Level Distribution Analysis**
   - Analisis distribusi siswa per level
   - Proyeksi kebutuhan kelas
   - Capacity planning

3. **Fase 3: Teacher Availability Collection**
   - Guru submit ketersediaan jadwal
   - Admin monitor submission status
   - Validasi kelengkapan data

4. **Fase 4: Management Level Assignment**
   - Management assign guru ke level
   - Tentukan kompetensi dan spesialisasi
   - Distribusi workload

5. **Fase 5: Class Generation & Refinement**
   - Generate kelas otomatis
   - Manual refinement dan adjustment
   - Resolve konflik jadwal

6. **Fase 6: Final Review & Go-Live**
   - Review jadwal final
   - Validasi quality metrics
   - Aktivasi sistem semester baru

### 📸 Media Dokumentasi

#### Screenshot
- **Total**: 100+ screenshot HD
- **Resolusi**: 1920x1080
- **Format**: PNG
- **Struktur**: `[TestClass]/[methodName]_[timestamp]/screenshots/`

#### Video Tutorial
- **Total**: 5+ video demonstrasi
- **Resolusi**: 1920x1080 HD
- **Format**: WebM (browser-compatible)
- **Kecepatan**: 2000ms delay untuk clarity

### 🔧 Perintah Generasi

```bash
# Generate dokumentasi lengkap (registration + term preparation)
./generate-user-manual.sh generate

# Clean dokumentasi sebelumnya
./generate-user-manual.sh clean

# Buka folder dokumentasi
./generate-user-manual.sh view

# Generate dengan mode cepat (screenshot lebih sedikit)
./generate-user-manual.sh generate --fast

# Generate tanpa browser visible
./generate-user-manual.sh generate --headless
```

### 📊 Statistik Dokumentasi
- **Waktu generasi**: ~10-15 menit
- **Total ukuran**: ~50-100 MB (dengan video)
- **Bahasa**: Indonesia
- **Target pengguna**: Admin, Guru, Management, Siswa

### 🚀 Cara Penggunaan

1. **Untuk Training Staff Baru**
   - Mulai dengan PANDUAN_SISTEM_LENGKAP.md
   - Ikuti workflow step-by-step dengan screenshot
   - Tonton video untuk demonstrasi visual

2. **Untuk Referensi Cepat**
   - Gunakan panduan individual per workflow
   - Lihat screenshot untuk UI reference
   - Cek troubleshooting section

3. **Untuk System Administrator**
   - Referensi konfigurasi di appendices
   - Permission matrix untuk RBAC setup
   - Best practices untuk maintenance

---
*Dokumentasi dibuat otomatis menggunakan Playwright test framework*
*Terakhir diperbarui: $(date +'%d %B %Y, %H:%M')*
EOF

    print_info "Complete summary file created: target/documentation/SUMMARY.md"
}

# Generate summary file (for backward compatibility)
generate_summary_file() {
    generate_complete_summary_file
}


# Generate unified documentation combining all workflows
generate_unified_documentation() {
    print_info "Generating unified system documentation..."
    
    # Create unified markdown file
    cat > target/documentation/PANDUAN_SISTEM_LENGKAP.md << 'EOFMAIN'
# 📚 Panduan Lengkap Sistem Yayasan Sahabat Quran

## Sistem Manajemen Akademik Terintegrasi

### 🎯 Tentang Dokumentasi Ini

Dokumentasi ini memberikan panduan lengkap untuk mengoperasikan Sistem Manajemen Akademik Yayasan Sahabat Quran, mencakup:
- **Workflow Pendaftaran Siswa** (3 tahap)
- **Workflow Persiapan Semester** (6 fase)
- **Best Practices dan Troubleshooting**

---

## Bagian I: Workflow Pendaftaran Siswa

### Overview
Workflow pendaftaran melibatkan 3 tahap sequential:
1. Siswa mendaftar dan submit dokumen
2. Admin akademik assign ke guru
3. Guru evaluasi dan approve/reject

[Lihat panduan lengkap di PANDUAN_PENDAFTARAN.md](PANDUAN_PENDAFTARAN.md)

### Quick Start
- **Siswa**: Akses `/register` untuk memulai pendaftaran
- **Admin**: Akses `/academic/registrations` untuk manage pendaftaran
- **Guru**: Akses `/instructor/registrations` untuk evaluasi

---

## Bagian II: Workflow Persiapan Semester

### Overview
Workflow persiapan semester melibatkan 6 fase komprehensif:
1. Assessment Foundation - Validasi data assessment
2. Level Distribution - Analisis distribusi siswa
3. Teacher Availability - Pengumpulan jadwal guru
4. Management Assignment - Penugasan level guru
5. Class Generation - Generasi dan optimasi kelas
6. Final Review & Go-Live - Aktivasi semester

[Lihat panduan lengkap di PANDUAN_PERSIAPAN_SEMESTER.md](PANDUAN_PERSIAPAN_SEMESTER.md)

### Timeline Standard
- **Week 1**: Assessment completion & validation
- **Week 2**: Teacher availability & level assignment
- **Week 3**: Class generation & refinement
- **Week 4**: Final review & go-live

---

## Bagian III: Integrasi Workflow

### Alur Data Antar Workflow

```
[Pendaftaran Siswa] → [Assessment Data] → [Persiapan Semester]
        ↓                     ↓                    ↓
   Student Records      Level Assignment      Class Creation
        ↓                     ↓                    ↓
   [Active Semester] ← [Go-Live Process] ← [Schedule Final]
```

### Key Integration Points
1. **Student Registration → Term Preparation**
   - Placement test results feed into level distribution
   - New student data informs class size planning

2. **Term Preparation → Active Operations**
   - Generated classes become operational
   - Teacher assignments activate

---

## Bagian IV: Roles & Permissions

### System Roles

| Role | Registration Workflow | Term Preparation | Key Permissions |
|------|---------------------|------------------|-----------------|
| Student | Tahap 1: Submit | - | STUDENT_* |
| Academic Admin | Tahap 2: Assign | Fase 1-3, 5-6 | ACADEMIC_*, STUDENT_REG_* |
| Instructor | Tahap 3: Evaluate | Fase 3: Availability | INSTRUCTOR_*, CLASS_* |
| Management | - | Fase 4: Assignment | MANAGEMENT_*, TEACHER_ASSIGN |

---

## Bagian V: Best Practices

### For Academic Administrators
1. **Registration Management**
   - Process registrations within 48 hours
   - Assign to available teachers promptly
   - Monitor evaluation turnaround time

2. **Term Preparation**
   - Start 4 weeks before semester
   - Ensure 100% assessment completion
   - Validate all data before generation

### For Instructors
1. **Registration Evaluation**
   - Review within 72 hours of assignment
   - Provide detailed feedback
   - Use standardized evaluation criteria

2. **Availability Submission**
   - Submit complete availability early
   - Update if changes occur
   - Consider peak demand times

### For Management
1. **Strategic Planning**
   - Review historical data for trends
   - Balance teacher workload equitably
   - Plan for growth and scaling

---

## Bagian VI: Troubleshooting

### Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Registration stuck at SUBMITTED | No teacher assigned | Admin assign teacher |
| Class generation fails | Incomplete data | Validate prerequisites |
| Schedule conflicts | Overlapping assignments | Use manual refinement |
| Go-live blocked | Unresolved issues | Check validation checklist |

---

## Appendices

### A. System Configuration
See configuration details in individual workflow guides.

### B. Contact Information
- **Technical Support**: support@ysq.ac.id
- **Academic Admin**: academic@ysq.ac.id
- **Emergency**: +62 812-3456-7890

### C. Quick Links
- [Registration Guide](PANDUAN_PENDAFTARAN.md)
- [Term Preparation Guide](PANDUAN_PERSIAPAN_SEMESTER.md)
- [Screenshot Gallery](screenshots/)
- [Video Tutorials](videos/)

---

*Dokumentasi sistem lengkap Yayasan Sahabat Quran*
*Generated: $(date +'%Y-%m-%d %H:%M')*
EOFMAIN

    if [ -f "target/documentation/PANDUAN_SISTEM_LENGKAP.md" ]; then
        print_success "Unified documentation created!"
        local md_size=$(du -k target/documentation/PANDUAN_SISTEM_LENGKAP.md | cut -f1)
        print_info "  📄 PANDUAN_SISTEM_LENGKAP.md (${md_size} KB)"
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