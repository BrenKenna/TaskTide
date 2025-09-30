/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.tasktide.core.model.task.ItemTask;


/**
 * Class to (de)serialize work item state summary as JSON.
 * <br><br>
 * Currently constrained to {@link WorkItem WorkItem} and {@link TaskState}
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

    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "StateSummary{" + "counts=" + counts + '}';
    }
}