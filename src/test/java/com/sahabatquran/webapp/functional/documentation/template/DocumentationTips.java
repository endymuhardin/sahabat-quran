package com.sahabatquran.webapp.functional.documentation.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Model untuk tips dalam dokumentasi
 */
@Data
public class DocumentationTips {
    
    private List<String> items;
    
    @JsonProperty("items")
    public List<String> getItems() {
        return items;
    }
}