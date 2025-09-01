package com.sahabatquran.webapp.functional.documentation.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Model untuk section dokumentasi
 */
@Data
public class DocumentationSection {
    
    private String id;
    private String title;
    private String description;
    private List<DocumentationAction> actions;
    private List<String> explanations;
    private DocumentationTips tips;
    
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }
    
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    
    @JsonProperty("actions")
    public List<DocumentationAction> getActions() {
        return actions;
    }
    
    @JsonProperty("explanations")
    public List<String> getExplanations() {
        return explanations;
    }
    
    @JsonProperty("tips")
    public DocumentationTips getTips() {
        return tips;
    }
}