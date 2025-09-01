package com.sahabatquran.webapp.functional.documentation.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Model untuk dokumentasi workflow lengkap
 */
@Data
public class WorkflowDocumentation {
    
    private String workflow;
    private String title; 
    private String description;
    private List<DocumentationSection> sections;
    
    @JsonProperty("workflow")
    public String getWorkflow() {
        return workflow;
    }
    
    @JsonProperty("title") 
    public String getTitle() {
        return title;
    }
    
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    
    @JsonProperty("sections")
    public List<DocumentationSection> getSections() {
        return sections;
    }
}