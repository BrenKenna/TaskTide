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
package org.tasktide.core.model.builders;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.workitem.Workload;
import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * Allow {@link WorkItem WorkItem} objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class WorkItemBuilder extends ModelBuilder {
    
    // Attributes
    private String id, itemName, lockId, stepName, stepId;
    private ItemType itemType;
    private ItemState itemState;
    private long lockDate, doneDate;
    private int taskCount, taskDone;
    private Workload workload;
    
    
    public WorkItemBuilder() {
        super();
    }
    
    
    /**
     * Add id field
     * 
     * @param id 
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add item name field
     * 
     * @param itemName
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder itemName(String itemName) {
        this.itemName = itemName;
        return this;
    }
    
    
    /**
     * Add lockId field
     * 
     * @param lockId
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder lockId(String lockId) {
        this.lockId = lockId;
        return this;
    }
    
    
    /**
     * Add stepId field
     * 
     * @param stepId
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder stepId(String stepId) {
        this.stepId = stepId;
        return this;
    }
    
    /**
     * Add item type field
     * 
     * @param itemType
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder itemType(ItemType itemType) {
        this.itemType = itemType;
        return this;
    }
    
    
    /**
     * Add item state field
     * 
     * @param itemState
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder itemState(ItemState itemState) {
        this.itemState = itemState;
        return this;
    }
    
    
    /**
     * Add lock date field
     * 
     * @param lockDate
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder lockDate(long lockDate) {
        this.lockDate = lockDate;
        return this;
    }
    
    
    /**
     * Add done date field
     * 
     * @param doneDate
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder doneDate(long doneDate) {
        this.doneDate = doneDate;
        return this;
    }
    
    
    /**
     * Add task count field
     * 
     * @param taskCount
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder taskCount(int taskCount) {
        this.taskCount = taskCount;
        return this;
    }
    
    
    /**
     * Add task done field
     * 
     * @param taskDone
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder taskDone(int taskDone) {
        this.taskDone = taskDone;
        return this;
    }
    
    
    /**
     * Add workload field
     * 
     * @param workload
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder workload(Workload workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Add workload field with single {@link ItemTask ItemTask}
     * 
     * @param itemTask
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder workload(ItemTask itemTask) {
        WorkloadBuilder builder = new WorkloadBuilder();
        this.workload = builder.workload(itemTask).build();
        return this;
    }
    
    
    /**
     * Add step name field
     * 
     * @param stepName
     * @return {@link WorkItemBuilder}
     */
    public WorkItemBuilder stepName(String stepName) {
        this.stepName = stepName;
        return this;
    }
    
    
    /**
     * Build {@link WorkItem} from provided fields
     * 
     * @return {@link WorkItem}
     */
    @Override
    public WorkItem build() {
        if ( stepId != null ) {
            return new WorkItem(
                id, itemName, itemType, itemState,
                lockId, lockDate, doneDate, taskCount,
                taskDone, workload, stepName, stepId
            );
        }
        return new WorkItem(
            id, itemName, itemType, itemState,
            lockId, lockDate, doneDate, taskCount,
            taskDone, workload, stepName
        );
    }
}