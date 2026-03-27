/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.worker;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * Build a query to fetch a workload for processing
 * 
 * @author Bren
 */
public class WorkItemAcquisitionPolicy {
    
    // Attributes for which builder
    private boolean targetted, annoString, annotation;
    
    
    // Field properties
    private String target = "";
    private ItemState state = ItemState.TODO;
    private String annoKey;
    private Object annoVal;
    private CustomAnnotation anno;
    
    
    /**
     * Building an acquisition policy with collection
     * 
     * @param target
     * @return {@link WorkItemAcquisitionPolicy}
     */
    public WorkItemAcquisitionPolicy withTarget(String target) {
        this.target = target;
        this.targetted = true;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements of collection
     *  matching state
     * 
     * @param state
     * @return {@link WorkItemAcquisitionPolicy}
     */
    public WorkItemAcquisitionPolicy withItemState(ItemState state) {
        this.state = state;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements with provided
     *  annotation key-value pair
     * 
     * @param key
     * @param val
     * @return {@link WorkItemAcquisitionPolicy}
     */
    public WorkItemAcquisitionPolicy withAnno(String key, Object val) {
        this.annoKey = key;
        this.annoVal = val;
        this.annoString = true;
        return this;
    }
    
    
    /**
     * Building an acquisition policy for elements with provided
     *  annotation
     * 
     * @param anno
     * @return 
     */
    public WorkItemAcquisitionPolicy withAnno(CustomAnnotation anno) {
        this.anno = anno;
        this.annotation = true;
        return this;
    }

    
    /**
     * Build {@link WorkItem} from {@link WorkItemAcquisitionPolicy}
     * 
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload() {
    
        // Fetch by annotation
        if ( this.annotation ) {
            if ( this.targetted ) {
                return TaskTideServiceManager
                    .fetchWorkItemService()
                    .getRepo()
                .findByFieldForGroupWithAnno(
                    "itemState", this.state,
                    "stepName", this.target,
                    anno
                );
            }
        }
        
        // Fetch by annotation string
        else if ( this.annoString ) {
            if ( this.targetted ) {
                return TaskTideServiceManager
                    .fetchWorkItemService()
                    .getRepo()
                .findByFieldForGroupWithAnno(
                    "itemState", this.state,
                    "stepName", this.target,
                    this.annoKey, this.annoVal
                );
            }
        }
        
        // Fetch collection
        else {
            if ( this.targetted ) {
                return TaskTideServiceManager
                    .fetchWorkItemService()
                .viewByFieldForGroup(
                    "itemState", this.state,
                    "stepName", this.target
                );    
            }
        }
        
        // Otherwise empty list
        return new ArrayList<>();
    }
    
    
    public boolean isTargetted() {
        return targetted;
    }

    public boolean isAnnoString() {
        return annoString;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public String getTarget() {
        return target;
    }

    public ItemState getState() {
        return state;
    }

    public String getAnnoKey() {
        return annoKey;
    }

    public Object getAnnoVal() {
        return annoVal;
    }

    public CustomAnnotation getAnno() {
        return anno;
    }
}