#!/bin/bash

# Test script to preview documentation styling locally

set -e

echo "📚 Testing documentation styling locally..."

# Create a test directory
TEST_DIR="test-documentation-preview"
mkdir -p $TEST_DIR

# Create a sample markdown file with typical documentation content
cat > $TEST_DIR/sample.md << 'EOF'
# 📚 Panduan Lengkap: Workflow Persiapan Semester

## Sistem Manajemen Persiapan Akademik - Yayasan Sahabat Quran

*Panduan Komprehensif untuk Persiapan Semester Baru dari Assessment hingga Go-Live*

### 🎯 Tujuan Dokumen

Dokumen ini memberikan panduan lengkap untuk seluruh proses persiapan semester, mencakup:
- Workflow 6 fase dari assessment hingga go-live
- Peran dan tanggung jawab setiap stakeholder
- Best practices dan tips untuk optimasi
- Troubleshooting dan solusi masalah umum

## 📖 Daftar Isi

### Bagian I: Overview Workflow
1. [Pengantar Workflow Persiapan Semester](#pengantar)
2. [Timeline dan Milestone](#timeline)
3. [Stakeholder dan Peran](#stakeholder)

### Bagian II: Fase-Fase Workflow

#### Fase 1: Assessment Foundation
- [Monitoring Placement Test](#placement-test)
- [Validasi Hasil Ujian](#hasil-ujian)
- [Level Assignment](#level-assignment)

## 🚀 Workflow Persiapan Semester

### Pengantar

Workflow persiapan semester adalah proses komprehensif yang memastikan kesiapan optimal untuk semester akademik baru.

**Prinsip Utama:**
- **Data-Driven**: Keputusan berdasarkan data assessment yang valid
- **Collaborative**: Melibatkan semua stakeholder secara aktif
- **Optimized**: Algoritma cerdas untuk distribusi optimal
- **Flexible**: Penyesuaian manual untuk kasus khusus

### Timeline dan Milestone

| Minggu | Fase | Aktivitas Utama | Deliverable |
|--------|------|-----------------|-------------|
| Week 1 | Assessment & Validation | Complete placement tests, validate exam results | 100% assessment data |
| Week 2 | Collection & Assignment | Teacher availability, level assignments | Availability matrix, competency map |
| Week 3 | Generation & Refinement | Auto-generate classes, manual adjustments | Optimized class schedule |
| Week 4 | Review & Go-Live | Final validation, system activation | Active semester, notifications sent |

### Code Example

```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TermPreparationService {
    
    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    
    public TermPreparationResult prepareNewTerm(TermConfig config) {
        // Validate prerequisites
        validateAssessmentCompletion();
        
        // Generate optimal class distribution
        List<ClassSchedule> schedules = generateSchedules(config);
        
        // Apply manual refinements
        applyRefinements(schedules);
        
        return TermPreparationResult.success(schedules);
    }
}
```

> 💡 **Tips:** Mulai persiapan minimal 4 minggu sebelum semester dimulai untuk memastikan semua tahapan dapat diselesaikan dengan baik.

> ⚠️ **Perhatian:** Pastikan 100% assessment selesai sebelum generasi kelas untuk hasil optimal.

### Best Practices

1. **Timeline Management**
   - Mulai persiapan minimal 4 minggu sebelum semester dimulai
   - Set deadline ketersediaan guru 2 minggu sebelum generasi kelas
   - Sisakan 1 minggu untuk adjustment dan komunikasi final

2. **Data Quality**
   - Pastikan 100% assessment selesai sebelum generasi kelas
   - Validasi data siswa baru vs lama untuk optimal mixing
   - Review historical performance untuk teacher assignment

### Konfigurasi Sistem

```properties
# Class Size Configuration
class.size.default.min=7
class.size.default.max=10
class.size.tahsin1.min=8
class.size.tahsin1.max=12

# Student Mix Ratios
class.mix.new.target=0.4
class.mix.existing.target=0.6

# Teacher Workload
teacher.workload.optimal.min=4
teacher.workload.optimal.max=6
```

---

*Dokumentasi ini dibuat secara otomatis menggunakan sistem dokumentasi terintegrasi.*
*Terakhir diperbarui: 04 September 2025, 14:30*
EOF

# Copy CSS and template files
cp scripts/documentation-template.css $TEST_DIR/
cp scripts/pandoc-template.html $TEST_DIR/ 2>/dev/null || true

# Check if pandoc is installed
if ! command -v pandoc &> /dev/null; then
    echo "❌ Pandoc is not installed. Please install pandoc to test documentation generation."
    echo "   On macOS: brew install pandoc"
    echo "   On Ubuntu: sudo apt-get install pandoc"
    exit 1
fi

# Convert markdown to HTML using custom CSS
echo "🔄 Converting markdown to HTML with custom styling..."
cd $TEST_DIR

pandoc sample.md -o sample.html \
    --standalone \
    --template="pandoc-template.html" \
    --css="documentation-template.css" \
    --toc \
    --toc-depth=3 \
    --metadata title="Sahabat Quran Documentation Preview"

cd ..

echo "✅ Documentation preview generated successfully!"
echo ""
echo "📂 Output location: $TEST_DIR/sample.html"
echo ""
echo "🌐 To view the documentation:"
echo "   open $TEST_DIR/sample.html"
echo ""
echo "📊 Features in the new styling:"
echo "   - Wide layout (max-width: 1400px on large screens)"
echo "   - Modern gradient backgrounds"
echo "   - Beautiful typography with Inter font"
echo "   - Enhanced tables and code blocks"
echo "   - Smooth animations and transitions"
echo "   - Responsive design for all devices"
echo ""
echo "🎨 The documentation will now:"
echo "   - Use more horizontal space (90% or up to 1400px)"
echo "   - Look modern and visually appealing"
echo "   - Be easier to read with better typography"
echo "   - Have improved visual hierarchy"