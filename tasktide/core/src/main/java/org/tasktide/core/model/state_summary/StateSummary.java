/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.state_summary;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Class to (de)serialize work item state summary as JSON.
 * <br><br>
 * Currently constrained to {@link WorkItem WorkItem} and {@link TaskState TaskState}
 * implement the {@link StateSummaryType} interface.
 *
 * @author bkenna
 * @param <T> of {@link StateSummaryType}
 */
public class StateSummary<T extends StateSummaryType> {
    
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
     * Null constructor
     */
    @JsonbCreator
    public StateSummary() {
        this.counts = new HashMap<>();
    }
    
    
    /**
     * Get item state counts
     * 
     * @return Map-State, int
     */
    public Map<T, Integer> getCounts() {
        return counts;
    }
    
    
    /**
     * Get count for provided state
     * 
     * @param state
     * @return int
     */
    public int getCount(T state) {
        return (int) counts.get(state);
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
     * Set state summary from state map
     * 
     * @param <E> - of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
     * @param stateMap 
     */
    public <E extends TaskTideModel<E>> void setFromStateMap(Map<T, List<E>> stateMap) {
        counts = new HashMap<>();
        for ( Entry<T, List<E>> elm : stateMap.entrySet() ) {
            counts.put(elm.getKey(), elm.getValue().size());
        }
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
