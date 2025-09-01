package com.sahabatquran.webapp.functional.documentation.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Template engine untuk dokumentasi menggunakan Mustache.
 * Memungkinkan pemisahan konten dokumentasi dari kode Java.
 */
@Slf4j
public class DocumentationTemplateEngine {
    
    private final MustacheFactory mustacheFactory;
    private final ObjectMapper objectMapper;
    private final String templateBasePath;
    
    public DocumentationTemplateEngine() {
        this.mustacheFactory = new DefaultMustacheFactory("documentation-templates");
        this.objectMapper = new ObjectMapper();
        this.templateBasePath = "documentation-templates";
    }
    
    /**
     * Load template data from JSON file
     */
    public WorkflowDocumentation loadWorkflowData(String workflowType) {
        try {
            String jsonPath = String.format("/%s/%s/sections.json", templateBasePath, workflowType);
            try (InputStream inputStream = getClass().getResourceAsStream(jsonPath)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("Template file not found: " + jsonPath);
                }
                return objectMapper.readValue(inputStream, WorkflowDocumentation.class);
            }
        } catch (IOException e) {
            log.error("Failed to load workflow data for: {}", workflowType, e);
            throw new RuntimeException("Failed to load workflow template", e);
        }
    }
    
    /**
     * Render template with data
     */
    public String renderTemplate(String templateName, Object data) {
        try {
            Mustache mustache = mustacheFactory.compile(templateName);
            StringWriter writer = new StringWriter();
            mustache.execute(writer, data);
            return writer.toString();
        } catch (Exception e) {
            log.error("Failed to render template: {}", templateName, e);
            throw new RuntimeException("Failed to render template", e);
        }
    }
    
    /**
     * Get section explanation by ID
     */
    public DocumentationSection getSection(WorkflowDocumentation workflow, String sectionId) {
        return workflow.getSections().stream()
                .filter(section -> sectionId.equals(section.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));
    }
    
    /**
     * Get action explanation from section
     */
    public String getActionDescription(DocumentationSection section, String actionName) {
        return section.getActions().stream()
                .filter(action -> actionName.equals(action.getName()))
                .findFirst()
                .map(DocumentationAction::getDescription)
                .orElse("Action: " + actionName);
    }
    
    /**
     * Get all explanations for a section
     */
    public String[] getSectionExplanations(DocumentationSection section) {
        return section.getExplanations().toArray(new String[0]);
    }
    
    /**
     * Render section with custom data
     */
    public String renderSectionWithData(String workflowType, String sectionId, Map<String, Object> customData) {
        WorkflowDocumentation workflow = loadWorkflowData(workflowType);
        DocumentationSection section = getSection(workflow, sectionId);
        
        // Merge section data with custom data
        Map<String, Object> templateData = Map.of(
            "section", section,
            "workflow", workflow,
            "custom", customData
        );
        
        return renderTemplate(workflowType + "/intro.md", templateData);
    }
}