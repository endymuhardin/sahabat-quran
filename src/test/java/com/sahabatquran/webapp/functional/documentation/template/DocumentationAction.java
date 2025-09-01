package com.sahabatquran.webapp.functional.documentation.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Model untuk action dalam dokumentasi
 */
@Data
public class DocumentationAction {
    
    private String name;
    private String description;
    private String instruction;
    
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    @JsonProperty("description") 
    public String getDescription() {
        return description;
    }
    
    @JsonProperty("instruction")
    public String getInstruction() {
        return instruction;
    }
}