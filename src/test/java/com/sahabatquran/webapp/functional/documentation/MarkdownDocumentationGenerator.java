package com.sahabatquran.webapp.functional.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates comprehensive user documentation in Markdown format (Indonesian)
 * from captured documentation test sessions.
 */
@Slf4j
public class MarkdownDocumentationGenerator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Generate complete markdown documentation from all documentation test sessions
     */
    public void generateCompleteDocumentation(Path documentationRootDir, Path outputPath) throws IOException {
        log.info("🔄 Generating complete documentation in Indonesian...");
        
        StringBuilder markdown = new StringBuilder();
        
        // Document header
        markdown.append("# Panduan Pengguna - Workflow Lengkap Pendaftaran Siswa\n\n");
        markdown.append("*Panduan Lengkap 3 Tahap Proses Pendaftaran Siswa Baru - Yayasan Sahabat Quran*\n\n");
        markdown.append("**Tahapan Lengkap:** Siswa Daftar → Staff Assign → Guru Evaluasi → Siswa Diterima\n\n");
        markdown.append("---\n\n");
        
        // Table of contents
        markdown.append("## Daftar Isi\n\n");
        
        // Find all documentation sessions
        List<Path> sessionFiles = findDocumentationSessions(documentationRootDir);
        
        // Generate TOC
        int sectionNumber = 1;
        for (Path sessionFile : sessionFiles) {
            try {
                DocumentationCapture.DocumentationSession session = loadSession(sessionFile);
                markdown.append(String.format("%d. [%s](#%s)\n", 
                    sectionNumber++, 
                    session.testDescription, 
                    createAnchor(session.testDescription)));
                
                for (DocumentationCapture.DocumentationSection section : session.sections) {
                    markdown.append(String.format("   - [%s](#%s)\n", 
                        section.sectionName, 
                        createAnchor(section.sectionName)));
                }
            } catch (Exception e) {
                log.warn("Failed to process session file: {}", sessionFile, e);
            }
        }
        
        markdown.append("\n---\n\n");
        
        // Generate content for each session
        sectionNumber = 1;
        for (Path sessionFile : sessionFiles) {
            try {
                DocumentationCapture.DocumentationSession session = loadSession(sessionFile);
                generateSessionDocumentation(session, sessionFile.getParent(), markdown, sectionNumber++);
            } catch (Exception e) {
                log.warn("Failed to generate documentation for session: {}", sessionFile, e);
            }
        }
        
        // Footer
        markdown.append("\n---\n\n");
        markdown.append("*Dokumentasi ini dibuat secara otomatis menggunakan sistem test dokumentasi.*\n");
        markdown.append("*Terakhir diperbarui: ").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", 
            java.util.Locale.forLanguageTag("id-ID")))).append("*\n");
        
        // Save to file
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, markdown.toString().getBytes());
        
        log.info("✅ Documentation generated: {}", outputPath);
        log.info("   📄 Total size: {} KB", Files.size(outputPath) / 1024);
    }
    
    /**
     * Generate documentation for a single test session
     */
    private void generateSessionDocumentation(
            DocumentationCapture.DocumentationSession session, 
            Path sessionDir, 
            StringBuilder markdown, 
            int sectionNumber) {
        
        // Session header
        markdown.append("## ").append(sectionNumber).append(". ").append(session.testDescription).append("\n\n");
        markdown.append("**Waktu Dokumentasi:** ").append(session.timestamp).append("\n\n");
        
        // Video link if available
        if (session.videoPath != null && !session.videoPath.isEmpty()) {
            Path videoPath = Paths.get(session.videoPath);
            String relativeVideoPath;
            
            if (videoPath.isAbsolute()) {
                // Find the path relative to target/documentation/
                String videoPathStr = videoPath.toString();
                int docIndex = videoPathStr.indexOf("target/documentation/");
                if (docIndex >= 0) {
                    relativeVideoPath = videoPathStr.substring(docIndex + "target/documentation/".length());
                } else {
                    // Fallback: use the video filename
                    relativeVideoPath = videoPath.getFileName().toString();
                }
            } else {
                relativeVideoPath = videoPath.toString();
            }
            
            markdown.append("### 📹 Video Tutorial\n\n");
            markdown.append("[**Tonton Video Lengkap**](").append(relativeVideoPath).append(")\n\n");
            markdown.append("*Video ini menunjukkan seluruh proses step-by-step secara visual.*\n\n");
        }
        
        // Process each section
        for (DocumentationCapture.DocumentationSection section : session.sections) {
            generateSectionDocumentation(section, sessionDir, markdown);
        }
        
        markdown.append("\n---\n\n");
    }
    
    /**
     * Generate documentation for a single section
     */
    private void generateSectionDocumentation(
            DocumentationCapture.DocumentationSection section, 
            Path sessionDir, 
            StringBuilder markdown) {
        
        // Section header
        markdown.append("### ").append(section.sectionName).append("\n\n");
        
        if (section.description != null && !section.description.isEmpty()) {
            markdown.append(section.description).append("\n\n");
        }
        
        // Process each step
        int stepNumber = 1;
        for (DocumentationCapture.DocumentationStep step : section.steps) {
            generateStepDocumentation(step, sessionDir, markdown, stepNumber++);
        }
        
        markdown.append("\n");
    }
    
    /**
     * Generate documentation for a single step
     */
    private void generateStepDocumentation(
            DocumentationCapture.DocumentationStep step, 
            Path sessionDir, 
            StringBuilder markdown, 
            int stepNumber) {
        
        switch (step.type) {
            case "explanation":
                // Use varied prefixes for explanations to reduce repetition
                String prefix = getExplanationPrefix(step.content);
                markdown.append(prefix).append(step.content).append("\n\n");
                break;
                
            case "action":
                markdown.append("#### Langkah ").append(stepNumber).append(": ").append(step.content).append("\n\n");
                
                if (step.screenshotPath != null) {
                    Path fullScreenshotPath = sessionDir.resolve(step.screenshotPath);
                    if (Files.exists(fullScreenshotPath)) {
                        String relativeScreenshotPath;
                        String fullPathStr = fullScreenshotPath.toString();
                        
                        // Find the path relative to target/documentation/
                        int docIndex = fullPathStr.indexOf("target/documentation/");
                        if (docIndex >= 0) {
                            relativeScreenshotPath = fullPathStr.substring(docIndex + "target/documentation/".length());
                        } else {
                            // Fallback: use just the screenshot filename
                            relativeScreenshotPath = step.screenshotPath;
                        }
                        
                        markdown.append("![").append(step.content).append("](").append(relativeScreenshotPath).append(")\n\n");
                        markdown.append("*Screenshot menunjukkan: ").append(step.content).append("*\n\n");
                    }
                }
                break;
                
            case "screenshot":
                if (step.screenshotPath != null) {
                    Path fullScreenshotPath = sessionDir.resolve(step.screenshotPath);
                    if (Files.exists(fullScreenshotPath)) {
                        String relativeScreenshotPath;
                        String fullPathStr = fullScreenshotPath.toString();
                        
                        // Find the path relative to target/documentation/
                        int docIndex = fullPathStr.indexOf("target/documentation/");
                        if (docIndex >= 0) {
                            relativeScreenshotPath = fullPathStr.substring(docIndex + "target/documentation/".length());
                        } else {
                            // Fallback: use just the screenshot filename
                            relativeScreenshotPath = step.screenshotPath;
                        }
                        
                        markdown.append("![").append(step.content).append("](").append(relativeScreenshotPath).append(")\n\n");
                        markdown.append("*").append(step.content).append("*\n\n");
                    }
                }
                break;
        }
    }
    
    /**
     * Find all documentation session files in the directory tree
     */
    private List<Path> findDocumentationSessions(Path rootDir) throws IOException {
        if (!Files.exists(rootDir)) {
            return List.of();
        }
        
        return Files.walk(rootDir)
                .filter(path -> path.getFileName().toString().equals("documentation-session.json"))
                .sorted(this::compareByWorkflowOrder)
                .collect(Collectors.toList());
    }
    
    /**
     * Compare session files to ensure proper workflow ordering (Student → Staff → Teacher)
     */
    private int compareByWorkflowOrder(Path path1, Path path2) {
        String pathStr1 = path1.toString().toLowerCase();
        String pathStr2 = path2.toString().toLowerCase();
        
        int order1 = getWorkflowOrder(pathStr1);
        int order2 = getWorkflowOrder(pathStr2);
        
        return Integer.compare(order1, order2);
    }
    
    /**
     * Get workflow order number based on path content
     */
    private int getWorkflowOrder(String pathStr) {
        if (pathStr.contains("student")) return 1; // Student workflow (Tahap 1)
        if (pathStr.contains("staff")) return 2;   // Staff workflow (Tahap 2)
        if (pathStr.contains("teacher")) return 3; // Teacher workflow (Tahap 3)
        return 99; // Unknown, put at end
    }
    
    /**
     * Get varied prefix for explanations to reduce repetition
     */
    private String getExplanationPrefix(String content) {
        // Use different prefixes based on content type and context
        String lowerContent = content.toLowerCase();
        
        if (lowerContent.startsWith("**tips") || lowerContent.startsWith("tips") || lowerContent.contains("💡 tips")) {
            return "💡 "; // Just emoji for tips sections
        } else if (lowerContent.startsWith("**langkah") || lowerContent.startsWith("**cara")) {
            return "📋 **Panduan:** ";
        } else if (lowerContent.startsWith("**masalah") || lowerContent.contains("error") || lowerContent.contains("masalah")) {
            return "⚠️ **Perhatian:** ";
        } else if (lowerContent.startsWith("**yang perlu") || lowerContent.contains("perlu diperhatikan")) {
            return "📝 **Catatan:** ";
        } else if (lowerContent.startsWith("**informasi") || lowerContent.contains("menampilkan:")) {
            return "ℹ️ **Info:** ";
        } else if (lowerContent.startsWith("- ") || (content.trim().startsWith("- ") && content.trim().length() < 100)) {
            return "  "; // No prefix for bullet points, just indentation
        } else if (lowerContent.contains("setelah") && (lowerContent.contains("berhasil") || lowerContent.contains("selesai"))) {
            return "✅ **Hasil:** ";
        } else if (lowerContent.contains("pastikan") || lowerContent.contains("penting")) {
            return "⚠️ **Penting:** ";
        } else {
            return "💡 **Penjelasan:** "; // Default fallback
        }
    }
    
    /**
     * Load a documentation session from JSON file
     */
    private DocumentationCapture.DocumentationSession loadSession(Path sessionFile) throws IOException {
        return objectMapper.readValue(sessionFile.toFile(), DocumentationCapture.DocumentationSession.class);
    }
    
    /**
     * Create URL anchor from text
     */
    private String createAnchor(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("^-+|-+$", "");
    }
    
    
    /**
     * Main method for standalone execution
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java MarkdownDocumentationGenerator <documentation-root-dir> <output-file>");
            System.exit(1);
        }
        
        try {
            MarkdownDocumentationGenerator generator = new MarkdownDocumentationGenerator();
            Path rootDir = Paths.get(args[0]);
            Path outputFile = Paths.get(args[1]);
            
            generator.generateCompleteDocumentation(rootDir, outputFile);
            System.out.println("✅ Documentation generated successfully: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to generate documentation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}