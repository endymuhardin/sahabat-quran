package com.sahabatquran.webapp.functional.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures structured documentation data during test execution for automated markdown generation.
 * This class records all the steps, explanations, and screenshots taken during documentation tests.
 */
@Slf4j
public class DocumentationCapture {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DocumentationSession session = new DocumentationSession();
    private DocumentationSection currentSection = null;
    
    public void startSession(String testName, String testDescription) {
        session.testName = testName;
        session.testDescription = testDescription;
        session.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("📝 Documentation session started: {}", testName);
    }
    
    public void startSection(String sectionName, String description) {
        if (currentSection != null) {
            session.sections.add(currentSection);
        }
        
        currentSection = new DocumentationSection();
        currentSection.sectionName = sectionName;
        currentSection.description = description;
        currentSection.steps = new ArrayList<>();
        
        log.info("📖 Section started: {}", sectionName);
    }
    
    public void addExplanation(String explanation) {
        if (currentSection != null) {
            DocumentationStep step = new DocumentationStep();
            step.type = "explanation";
            step.content = explanation;
            step.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            currentSection.steps.add(step);
            log.info("💡 Explanation added: {}", explanation.substring(0, Math.min(50, explanation.length())) + "...");
        }
    }
    
    public void addAction(String actionDescription, String screenshotPath) {
        if (currentSection != null) {
            DocumentationStep step = new DocumentationStep();
            step.type = "action";
            step.content = actionDescription;
            step.screenshotPath = screenshotPath;
            step.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            currentSection.steps.add(step);
            log.info("👤 Action added: {} (Screenshot: {})", actionDescription, screenshotPath);
        }
    }
    
    public void addScreenshot(String description, String screenshotPath) {
        if (currentSection != null) {
            DocumentationStep step = new DocumentationStep();
            step.type = "screenshot";
            step.content = description;
            step.screenshotPath = screenshotPath;
            step.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            currentSection.steps.add(step);
            log.info("📸 Screenshot added: {} ({})", description, screenshotPath);
        }
    }
    
    public void endSection() {
        if (currentSection != null) {
            session.sections.add(currentSection);
            log.info("✅ Section completed: {}", currentSection.sectionName);
            currentSection = null;
        }
    }
    
    public void setVideoPath(String videoPath) {
        session.videoPath = videoPath;
        log.info("📹 Video path set: {}", videoPath);
    }
    
    public void saveToFile(Path outputPath) throws IOException {
        // Ensure current section is saved
        if (currentSection != null) {
            session.sections.add(currentSection);
        }
        
        // Create directory if it doesn't exist
        Files.createDirectories(outputPath.getParent());
        
        // Save session data as JSON
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), session);
        log.info("📁 Documentation session saved to: {}", outputPath);
    }
    
    public DocumentationSession getSession() {
        return session;
    }
    
    // Data classes for structured documentation
    @Data
    public static class DocumentationSession {
        public String testName;
        public String testDescription;
        public String timestamp;
        public String videoPath;
        public List<DocumentationSection> sections = new ArrayList<>();
    }
    
    @Data
    public static class DocumentationSection {
        public String sectionName;
        public String description;
        public List<DocumentationStep> steps = new ArrayList<>();
    }
    
    @Data
    public static class DocumentationStep {
        public String type; // "explanation", "action", "screenshot"
        public String content;
        public String screenshotPath;
        public String timestamp;
    }
}