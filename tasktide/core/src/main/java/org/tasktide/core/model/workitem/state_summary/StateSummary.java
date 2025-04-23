/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.workitem.state_summary;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.util.Map;


/**
 * Class to (de)serialize work item state summary as JSON
 * 
 * @author bkenna
 * @param <T> of ItemState, TaskState
 */
public class StateSummary<T> {
    
    // Attributes
    @JsonbProperty("State Summary")
    private Map<T, Integer> counts;
    
    
    /**
     * Construct with item state counts
     * 
     * @param counts 
     */
    @JsonbCreator
    public StateSummary(
        @JsonbProperty("State Summary") Map<T, Integer> counts
    ) {
        this.counts = counts;
    }
    
    
    /**
     * Get item state counts
     * 
     * @return Map-ItemState, int
     */
    public Map<T, Integer> getCounts() {
        return counts;
    }

    
    /**
     * Set item state counts
     * 
     * @param counts 
     */
    public void setCounts(Map<T, Integer> counts) {
        this.counts = counts;
    }
    
    
    /**
     * Represent item state counts as formatted JSON
     * 
     * @return String-JSON Doc
     */
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }
    
    
    /**
     * Represent item state count as JSON string
     * 
     * @return String-JSON
     */
    public String toJson() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
}
