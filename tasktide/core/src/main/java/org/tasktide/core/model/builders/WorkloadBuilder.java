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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.workitem.Workload;


/**
 *
 * Allow ProcessLog objects to be built where all fields are optional
 * 
 * @author bkenna
 */
public class WorkloadBuilder extends ModelBuilder<Workload> {
    
    // Attributes
    private String id;
    private Map<String, ItemTask> workload;
    private ItemState workloadState;
    private ItemType workloadType;
    
    
    public WorkloadBuilder() {
        super();
    }
    
    
    /**
     * Add Id field
     * 
     * @param id
     * @return WorkloadBuilder 
     */
    public WorkloadBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add workload field
     * 
     * @param workload
     * @return WorkloadBuilder
     */
    public WorkloadBuilder withWorkload(Map<String, ItemTask> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Add workload field from list of tasks
     * 
     * @param tasks
     * @return WorkloadBuilder
     */
    public WorkloadBuilder withWorkload(List<ItemTask> tasks) {
        this.workload = new HashMap<>();
        for ( ItemTask task : tasks ) {
            this.workload.put(task.getTaskName(), task);
        }
        return this;
    }
    
    
    /**
     * Builder {@link Workload Workload} from single {@link ItemTask ItemTask}
     * 
     * @param task
     * @return {@link WorkloadBuilder WorkloadBuilder}
     */
    public WorkloadBuilder withWorkload(ItemTask task) {
        this.workload = new HashMap<>();
        this.workload.put(task.getTaskName(), task);
        return this;
    }
    
    
    /**
     * Add workload state field
     * 
     * @param workloadState
     * @return WorkloadBuilder
     */
    public WorkloadBuilder withWorkloadState(ItemState workloadState) {
        this.workloadState = workloadState;
        return this;
    }
    
    
    /**
     * Add workload type field
     * 
     * @param workloadType
     * @return WorkloadBuilder
     */
    public WorkloadBuilder withWorkloadType(ItemType workloadType) {
        this.workloadType = workloadType;
        return this;
    }
    
    
    /**
     * Build workload from provided fields
     * 
     * @return Workload
     */
    @Override
    public Workload build() {
        return new Workload(id, workload, workloadType);
    }
}
