package com.sahabatquran.webapp.functional.documentation.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DocumentationTemplateEngine
 */
@DisplayName("Documentation Template Engine Tests")
class DocumentationTemplateEngineTest {
    
    private DocumentationTemplateEngine templateEngine;
    
    @BeforeEach
    void setUp() {
        templateEngine = new DocumentationTemplateEngine();
    }
    
    @Test
    @DisplayName("Should load student workflow template data successfully")
    void shouldLoadStudentWorkflowTemplate() {
        // When
        WorkflowDocumentation workflow = templateEngine.loadWorkflowData("student");
        
        // Then
        assertNotNull(workflow);
        assertEquals("Student Registration", workflow.getWorkflow());
        assertNotNull(workflow.getTitle());
        assertNotNull(workflow.getDescription());
        assertNotNull(workflow.getSections());
        assertFalse(workflow.getSections().isEmpty());
        
        System.out.println("✅ Loaded workflow: " + workflow.getTitle());
        System.out.println("   Description: " + workflow.getDescription());
        System.out.println("   Sections: " + workflow.getSections().size());
    }
    
    @Test
    @DisplayName("Should get section by ID")
    void shouldGetSectionById() {
        // Given
        WorkflowDocumentation workflow = templateEngine.loadWorkflowData("student");
        
        // When
        DocumentationSection section = templateEngine.getSection(workflow, "access_registration");
        
        // Then
        assertNotNull(section);
        assertEquals("access_registration", section.getId());
        assertEquals("Akses Halaman Pendaftaran", section.getTitle());
        assertNotNull(section.getDescription());
        
        System.out.println("✅ Found section: " + section.getTitle());
        System.out.println("   Description: " + section.getDescription());
    }
    
    @Test
    @DisplayName("Should get action description from section")
    void shouldGetActionDescription() {
        // Given
        WorkflowDocumentation workflow = templateEngine.loadWorkflowData("student");
        DocumentationSection section = templateEngine.getSection(workflow, "access_registration");
        
        // When
        String actionDescription = templateEngine.getActionDescription(section, "navigateToRegistrationPage");
        
        // Then
        assertNotNull(actionDescription);
        assertFalse(actionDescription.isEmpty());
        
        System.out.println("✅ Action description: " + actionDescription);
    }
    
    @Test
    @DisplayName("Should get section explanations")
    void shouldGetSectionExplanations() {
        // Given
        WorkflowDocumentation workflow = templateEngine.loadWorkflowData("student");
        DocumentationSection section = templateEngine.getSection(workflow, "access_registration");
        
        // When
        String[] explanations = templateEngine.getSectionExplanations(section);
        
        // Then
        assertNotNull(explanations);
        assertTrue(explanations.length > 0);
        
        System.out.println("✅ Found " + explanations.length + " explanations:");
        for (String explanation : explanations) {
            System.out.println("   - " + explanation);
        }
    }
    
    @Test
    @DisplayName("Should handle sections with tips")
    void shouldHandleSectionsWithTips() {
        // Given
        WorkflowDocumentation workflow = templateEngine.loadWorkflowData("student");
        DocumentationSection section = templateEngine.getSection(workflow, "personal_information");
        
        // When & Then
        assertNotNull(section);
        if (section.getTips() != null) {
            assertNotNull(section.getTips().getItems());
            assertFalse(section.getTips().getItems().isEmpty());
            
            System.out.println("✅ Found tips for section: " + section.getTitle());
            for (String tip : section.getTips().getItems()) {
                System.out.println("   💡 " + tip);
            }
        } else {
            System.out.println("ℹ️ No tips found for section: " + section.getTitle());
        }
    }
}