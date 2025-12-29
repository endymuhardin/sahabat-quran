package com.sahabatquran.webapp.functional.documentation.termpreparation;

import tools.jackson.databind.json.JsonMapper;
import com.sahabatquran.webapp.functional.documentation.DocumentationCapture;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Specialized Markdown generator for Term Preparation Workflow documentation.
 * 
 * Generates comprehensive Indonesian user manual specific to the academic
 * term preparation process with proper phase organization and navigation.
 */
@Slf4j
public class TermPreparationMarkdownGenerator {
    
    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
        "dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID")
    );
    
    /**
     * Generate complete term preparation workflow documentation
     */
    public void generateTermPreparationDocumentation(Path documentationRootDir, Path outputPath) throws IOException {
        log.info("🔄 Generating Term Preparation Workflow documentation in Indonesian...");
        
        StringBuilder markdown = new StringBuilder();
        
        // Document header
        generateHeader(markdown);
        
        // Table of contents
        generateTableOfContents(markdown, documentationRootDir);
        
        // Process workflow sections
        List<Path> sessionFiles = findDocumentationSessions(documentationRootDir);
        generateWorkflowContent(sessionFiles, markdown);
        
        // Appendices
        generateAppendices(markdown);
        
        // Footer
        generateFooter(markdown);
        
        // Save to file
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, markdown.toString().getBytes());
        
        log.info("✅ Term Preparation documentation generated: {}", outputPath);
        log.info("   📄 Total size: {} KB", Files.size(outputPath) / 1024);
    }
    
    private void generateHeader(StringBuilder markdown) {
        markdown.append("# 📚 Panduan Lengkap: Workflow Persiapan Semester\n\n");
        markdown.append("## Sistem Manajemen Persiapan Akademik - Yayasan Sahabat Quran\n\n");
        markdown.append("*Panduan Komprehensif untuk Persiapan Semester Baru dari Assessment hingga Go-Live*\n\n");
        
        markdown.append("### 🎯 Tujuan Dokumen\n");
        markdown.append("Dokumen ini memberikan panduan lengkap untuk seluruh proses persiapan semester, mencakup:\n");
        markdown.append("- Workflow 6 fase dari assessment hingga go-live\n");
        markdown.append("- Peran dan tanggung jawab setiap stakeholder\n");
        markdown.append("- Best practices dan tips untuk optimasi\n");
        markdown.append("- Troubleshooting dan solusi masalah umum\n\n");
        
        markdown.append("### 👥 Target Pengguna\n");
        markdown.append("- **Admin Akademik**: Koordinator utama persiapan semester\n");
        markdown.append("- **Management**: Pengambil keputusan strategis\n");
        markdown.append("- **Instructor**: Pengisi ketersediaan dan persiapan kelas\n");
        markdown.append("- **System Administrator**: Support teknis\n\n");
        
        markdown.append("---\n\n");
    }
    
    private void generateTableOfContents(StringBuilder markdown, Path documentationRootDir) throws IOException {
        markdown.append("## 📖 Daftar Isi\n\n");
        
        markdown.append("### Bagian I: Overview Workflow\n");
        markdown.append("1. [Pengantar Workflow Persiapan Semester](#pengantar)\n");
        markdown.append("2. [Timeline dan Milestone](#timeline)\n");
        markdown.append("3. [Stakeholder dan Peran](#stakeholder)\n\n");
        
        markdown.append("### Bagian II: Fase-Fase Workflow\n");
        markdown.append("#### Fase 1: Assessment Foundation\n");
        markdown.append("- [Monitoring Placement Test](#placement-test)\n");
        markdown.append("- [Validasi Hasil Ujian](#hasil-ujian)\n");
        markdown.append("- [Level Assignment](#level-assignment)\n\n");
        
        markdown.append("#### Fase 2: Level Distribution Analysis\n");
        markdown.append("- [Analisis Distribusi Siswa](#distribusi-siswa)\n");
        markdown.append("- [Proyeksi Kebutuhan Kelas](#proyeksi-kelas)\n");
        markdown.append("- [Capacity Planning](#capacity-planning)\n\n");
        
        markdown.append("#### Fase 3: Teacher Availability Collection\n");
        markdown.append("- [Admin: Monitoring Submission](#monitoring-submission)\n");
        markdown.append("- [Instructor: Input Ketersediaan](#input-ketersediaan)\n");
        markdown.append("- [Validasi dan Follow-up](#validasi-availability)\n\n");
        
        markdown.append("#### Fase 4: Management Level Assignment\n");
        markdown.append("- [Penugasan Level Guru](#penugasan-level)\n");
        markdown.append("- [Kompetensi dan Spesialisasi](#kompetensi)\n");
        markdown.append("- [Workload Distribution](#workload)\n\n");
        
        markdown.append("#### Fase 5: Class Generation & Refinement\n");
        markdown.append("- [Prerequisite Validation](#prerequisite)\n");
        markdown.append("- [Automated Generation](#generation)\n");
        markdown.append("- [Manual Refinement](#refinement)\n");
        markdown.append("- [Conflict Resolution](#conflict)\n\n");
        
        markdown.append("#### Fase 6: Final Review & Go-Live\n");
        markdown.append("- [Schedule Review](#schedule-review)\n");
        markdown.append("- [Quality Metrics](#quality-metrics)\n");
        markdown.append("- [System Activation](#activation)\n");
        markdown.append("- [Stakeholder Communication](#communication)\n\n");
        
        markdown.append("### Bagian III: Panduan Operasional\n");
        markdown.append("- [Best Practices](#best-practices)\n");
        markdown.append("- [Common Issues & Solutions](#troubleshooting)\n");
        markdown.append("- [Tips Optimasi](#optimization)\n");
        markdown.append("- [Checklist Go-Live](#checklist)\n\n");
        
        markdown.append("### Bagian IV: Referensi\n");
        markdown.append("- [Konfigurasi Sistem](#configuration)\n");
        markdown.append("- [Permission Matrix](#permissions)\n");
        markdown.append("- [API Documentation](#api)\n");
        markdown.append("- [Contact Support](#support)\n\n");
        
        markdown.append("---\n\n");
    }
    
    private void generateWorkflowContent(List<Path> sessionFiles, StringBuilder markdown) {
        markdown.append("## 🚀 Workflow Persiapan Semester\n\n");
        
        // Introduction section
        markdown.append("### <a name=\"pengantar\"></a>Pengantar\n\n");
        markdown.append("Workflow persiapan semester adalah proses komprehensif yang memastikan ");
        markdown.append("kesiapan optimal untuk semester akademik baru. Proses ini melibatkan ");
        markdown.append("koordinasi antara berbagai stakeholder dan sistem otomasi untuk ");
        markdown.append("menghasilkan jadwal kelas yang optimal.\n\n");
        
        markdown.append("**Prinsip Utama:**\n");
        markdown.append("- **Data-Driven**: Keputusan berdasarkan data assessment yang valid\n");
        markdown.append("- **Collaborative**: Melibatkan semua stakeholder secara aktif\n");
        markdown.append("- **Optimized**: Algoritma cerdas untuk distribusi optimal\n");
        markdown.append("- **Flexible**: Penyesuaian manual untuk kasus khusus\n\n");
        
        // Timeline section
        markdown.append("### <a name=\"timeline\"></a>Timeline dan Milestone\n\n");
        markdown.append("| Minggu | Fase | Aktivitas Utama | Deliverable |\n");
        markdown.append("|--------|------|-----------------|-------------|\n");
        markdown.append("| Week 1 | Assessment & Validation | Complete placement tests, validate exam results | 100% assessment data |\n");
        markdown.append("| Week 2 | Collection & Assignment | Teacher availability, level assignments | Availability matrix, competency map |\n");
        markdown.append("| Week 3 | Generation & Refinement | Auto-generate classes, manual adjustments | Optimized class schedule |\n");
        markdown.append("| Week 4 | Review & Go-Live | Final validation, system activation | Active semester, notifications sent |\n\n");
        
        // Process each documentation session
        for (Path sessionFile : sessionFiles) {
            try {
                DocumentationCapture.DocumentationSession session = loadSession(sessionFile);
                generateSessionContent(session, sessionFile.getParent(), markdown);
            } catch (Exception e) {
                log.warn("Failed to process session: {}", sessionFile, e);
            }
        }
    }
    
    private void generateSessionContent(
            DocumentationCapture.DocumentationSession session,
            Path sessionDir,
            StringBuilder markdown) {
        
        // Generate content based on session type
        String sessionType = detectSessionType(session);
        
        switch (sessionType) {
            case "COMPLETE_WORKFLOW":
                generateCompleteWorkflowSection(session, sessionDir, markdown);
                break;
            case "TEACHER_AVAILABILITY":
                generateTeacherAvailabilitySection(session, sessionDir, markdown);
                break;
            default:
                generateGenericSection(session, sessionDir, markdown);
        }
    }
    
    private void generateCompleteWorkflowSection(
            DocumentationCapture.DocumentationSession session,
            Path sessionDir,
            StringBuilder markdown) {
        
        markdown.append("## 📋 Fase-Fase Workflow Persiapan Semester\n\n");
        
        for (DocumentationCapture.DocumentationSection section : session.sections) {
            String phaseNumber = extractPhaseNumber(section.sectionName);
            String phaseTitle = section.sectionName;
            
            markdown.append("### ").append(phaseTitle).append("\n\n");
            
            if (section.description != null && !section.description.isEmpty()) {
                markdown.append("**Deskripsi:** ").append(section.description).append("\n\n");
            }
            
            // Process steps with proper formatting
            processWorkflowSteps(section, sessionDir, markdown);
            
            markdown.append("\n---\n\n");
        }
    }
    
    private void generateTeacherAvailabilitySection(
            DocumentationCapture.DocumentationSession session,
            Path sessionDir,
            StringBuilder markdown) {
        
        markdown.append("## 👨‍🏫 Panduan Khusus: Pengisian Ketersediaan Guru\n\n");
        
        for (DocumentationCapture.DocumentationSection section : session.sections) {
            markdown.append("### ").append(section.sectionName).append("\n\n");
            
            processTeacherSteps(section, sessionDir, markdown);
        }
    }
    
    private void processWorkflowSteps(
            DocumentationCapture.DocumentationSection section,
            Path sessionDir,
            StringBuilder markdown) {
        
        int stepNumber = 1;
        for (DocumentationCapture.DocumentationStep step : section.steps) {
            switch (step.type) {
                case "action":
                    markdown.append("**Langkah ").append(stepNumber++).append(": ")
                           .append(step.content).append("**\n\n");
                    
                    if (step.screenshotPath != null) {
                        addScreenshot(step, sessionDir, markdown);
                    }
                    break;
                    
                case "explanation":
                    // Format explanations based on content
                    if (step.content.startsWith("- ")) {
                        markdown.append(step.content).append("\n");
                    } else if (step.content.contains("Tips:") || step.content.contains("💡")) {
                        markdown.append("\n> 💡 **Tips:** ").append(
                            step.content.replace("💡", "").replace("Tips:", "")
                        ).append("\n\n");
                    } else if (step.content.contains("PERHATIAN") || step.content.contains("⚠️")) {
                        markdown.append("\n> ⚠️ **Perhatian:** ").append(
                            step.content.replace("⚠️", "").replace("PERHATIAN:", "")
                        ).append("\n\n");
                    } else {
                        markdown.append(step.content).append("\n\n");
                    }
                    break;
                    
                case "screenshot":
                    if (step.screenshotPath != null) {
                        addScreenshot(step, sessionDir, markdown);
                    }
                    break;
            }
        }
    }
    
    private void processTeacherSteps(
            DocumentationCapture.DocumentationSection section,
            Path sessionDir,
            StringBuilder markdown) {
        
        for (DocumentationCapture.DocumentationStep step : section.steps) {
            if (step.type.equals("action")) {
                markdown.append("#### ").append(step.content).append("\n\n");
                
                if (step.screenshotPath != null) {
                    addScreenshot(step, sessionDir, markdown);
                }
            } else if (step.type.equals("explanation")) {
                markdown.append(step.content).append("\n\n");
            }
        }
    }
    
    private void addScreenshot(
            DocumentationCapture.DocumentationStep step,
            Path sessionDir,
            StringBuilder markdown) {
        
        Path fullPath = sessionDir.resolve(step.screenshotPath);
        if (Files.exists(fullPath)) {
            String relativePath = calculateRelativePath(fullPath);
            markdown.append("![").append(step.content).append("](")
                   .append(relativePath).append(")\n\n");
            markdown.append("*").append(step.content).append("*\n\n");
        }
    }
    
    private void generateAppendices(StringBuilder markdown) {
        markdown.append("## 📚 Lampiran\n\n");
        
        // Best Practices
        markdown.append("### <a name=\"best-practices\"></a>Best Practices\n\n");
        markdown.append("#### Timeline Management\n");
        markdown.append("- Mulai persiapan minimal 4 minggu sebelum semester dimulai\n");
        markdown.append("- Set deadline ketersediaan guru 2 minggu sebelum generasi kelas\n");
        markdown.append("- Sisakan 1 minggu untuk adjustment dan komunikasi final\n\n");
        
        markdown.append("#### Data Quality\n");
        markdown.append("- Pastikan 100% assessment selesai sebelum generasi kelas\n");
        markdown.append("- Validasi data siswa baru vs lama untuk optimal mixing\n");
        markdown.append("- Review historical performance untuk teacher assignment\n\n");
        
        markdown.append("#### Communication\n");
        markdown.append("- Kirim reminder otomatis untuk guru yang belum submit ketersediaan\n");
        markdown.append("- Komunikasikan jadwal final minimal 1 minggu sebelum mulai\n");
        markdown.append("- Sediakan FAQ dan contact person untuk pertanyaan\n\n");
        
        // Troubleshooting
        markdown.append("### <a name=\"troubleshooting\"></a>Troubleshooting\n\n");
        markdown.append("| Masalah | Penyebab | Solusi |\n");
        markdown.append("|---------|----------|--------|\n");
        markdown.append("| Kelas undersized (<7 siswa) | Distribusi tidak merata | Merge dengan kelas sejenis atau justifikasi khusus |\n");
        markdown.append("| Konflik jadwal guru | Overlapping assignment | Gunakan alternative time slots atau reassign guru |\n");
        markdown.append("| Assessment incomplete | Siswa tidak hadir test | Schedule makeup test atau gunakan historical data |\n");
        markdown.append("| Teacher overload | Terlalu banyak assignment | Redistribute ke guru lain atau recruit guru baru |\n\n");
        
        // Configuration
        markdown.append("### <a name=\"configuration\"></a>Konfigurasi Sistem\n\n");
        markdown.append("```properties\n");
        markdown.append("# Class Size Configuration\n");
        markdown.append("class.size.default.min=7\n");
        markdown.append("class.size.default.max=10\n");
        markdown.append("class.size.tahsin1.min=8\n");
        markdown.append("class.size.tahsin1.max=12\n");
        markdown.append("class.size.tahfidz.min=4\n");
        markdown.append("class.size.tahfidz.max=8\n\n");
        markdown.append("# Student Mix Ratios\n");
        markdown.append("class.mix.new.target=0.4\n");
        markdown.append("class.mix.existing.target=0.6\n\n");
        markdown.append("# Teacher Workload\n");
        markdown.append("teacher.workload.optimal.min=4\n");
        markdown.append("teacher.workload.optimal.max=6\n");
        markdown.append("teacher.workload.absolute.max=8\n");
        markdown.append("```\n\n");
    }
    
    private void generateFooter(StringBuilder markdown) {
        markdown.append("---\n\n");
        markdown.append("## 📞 Informasi Kontak\n\n");
        markdown.append("**Technical Support:**\n");
        markdown.append("- Email: support@ysq.ac.id\n");
        markdown.append("- Phone: (021) 1234-5678\n");
        markdown.append("- WhatsApp: 0812-3456-7890\n\n");
        
        markdown.append("**Academic Coordination:**\n");
        markdown.append("- Email: academic@ysq.ac.id\n");
        markdown.append("- Phone: (021) 1234-5679\n\n");
        
        markdown.append("---\n\n");
        markdown.append("*Dokumentasi ini dibuat secara otomatis menggunakan sistem dokumentasi terintegrasi.*\n");
        markdown.append("*Terakhir diperbarui: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("*\n");
        markdown.append("*Versi: 1.0.0*\n");
    }
    
    // Helper methods
    private List<Path> findDocumentationSessions(Path rootDir) throws IOException {
        if (!Files.exists(rootDir)) {
            return List.of();
        }
        
        return Files.walk(rootDir)
                .filter(path -> path.getFileName().toString().equals("documentation-session.json"))
                .sorted(this::compareByWorkflowOrder)
                .collect(Collectors.toList());
    }
    
    private int compareByWorkflowOrder(Path path1, Path path2) {
        String pathStr1 = path1.toString().toLowerCase();
        String pathStr2 = path2.toString().toLowerCase();
        
        // Prioritize complete workflow over specific guides
        if (pathStr1.contains("complete") && !pathStr2.contains("complete")) return -1;
        if (!pathStr1.contains("complete") && pathStr2.contains("complete")) return 1;
        
        return pathStr1.compareTo(pathStr2);
    }
    
    private DocumentationCapture.DocumentationSession loadSession(Path sessionFile) throws IOException {
        return jsonMapper.readValue(sessionFile.toFile(), DocumentationCapture.DocumentationSession.class);
    }
    
    private String detectSessionType(DocumentationCapture.DocumentationSession session) {
        if (session.testName != null) {
            if (session.testName.contains("completeTermPreparation")) {
                return "COMPLETE_WORKFLOW";
            } else if (session.testName.contains("teacherAvailability")) {
                return "TEACHER_AVAILABILITY";
            }
        }
        return "GENERIC";
    }
    
    private String extractPhaseNumber(String sectionName) {
        if (sectionName.contains("Fase 1")) return "1";
        if (sectionName.contains("Fase 2")) return "2";
        if (sectionName.contains("Fase 3")) return "3";
        if (sectionName.contains("Fase 4")) return "4";
        if (sectionName.contains("Fase 5")) return "5";
        if (sectionName.contains("Fase 6")) return "6";
        return "";
    }
    
    private String calculateRelativePath(Path fullPath) {
        String fullPathStr = fullPath.toString();
        int docIndex = fullPathStr.indexOf("target/documentation/");
        if (docIndex >= 0) {
            return fullPathStr.substring(docIndex + "target/documentation/".length());
        }
        return fullPath.getFileName().toString();
    }
    
    private void generateGenericSection(
            DocumentationCapture.DocumentationSession session,
            Path sessionDir,
            StringBuilder markdown) {
        
        for (DocumentationCapture.DocumentationSection section : session.sections) {
            markdown.append("### ").append(section.sectionName).append("\n\n");
            
            if (section.description != null && !section.description.isEmpty()) {
                markdown.append(section.description).append("\n\n");
            }
            
            processWorkflowSteps(section, sessionDir, markdown);
        }
    }
    
    /**
     * Main method for standalone execution
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TermPreparationMarkdownGenerator <documentation-root-dir> <output-file>");
            System.exit(1);
        }
        
        try {
            TermPreparationMarkdownGenerator generator = new TermPreparationMarkdownGenerator();
            Path rootDir = Paths.get(args[0]);
            Path outputFile = Paths.get(args[1]);
            
            generator.generateTermPreparationDocumentation(rootDir, outputFile);
            System.out.println("✅ Term Preparation documentation generated successfully: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to generate documentation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}