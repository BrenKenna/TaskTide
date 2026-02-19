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
package org.tasktide.engine.wokerunitprovider;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.executor.WorkItemExecutor;


/**
 * Class to hold the logic for constructing {@link TaskTideExecutor} for {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemExecutorBuilder {
    
    // Attributes
    private TaskTideEngineObserver<WorkItem> observer;
    private int threshold, nThreads;
    
    
    /**
     * Build with {@link WorkItemObserver}
     * 
     * @param workload
     * @param maxTime
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public WorkItemExecutorBuilder withWorkItemObserver(List<WorkItem> workload, int maxTime) {
    
        // Set required vars
        WorkItemObserverBuilder obsBuilder;
        
        // Build with work item observer
        obsBuilder = new WorkItemObserverBuilder();
        this.observer = obsBuilder
            .withWorkload(workload)
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build with {@link WorkItemObserver}
     * 
     * @param maxTime
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public WorkItemExecutorBuilder withWorkItemObserver(int maxTime) {
    
        // Set required vars
        WorkItemObserverBuilder obsBuilder;
        
        // Build with work item observer
        obsBuilder = new WorkItemObserverBuilder();
        this.observer = obsBuilder
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build with specific {@link WorkItemObserver}
     * 
     * @param obs
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public WorkItemExecutorBuilder withWorkItemObserver(TaskTideEngineObserver<WorkItem> obs) {
        this.observer = obs;
        return this;
    }
    
    
    /**
     * Build sub-tasking threshold for {@link ItemTask}
     * 
     * @param subTaskThreshold
     * @return {@link WorkItemExecutorBuilder} with subTaskThreshold
     */
    public WorkItemExecutorBuilder withSubTaskThreshold(int subTaskThreshold) {
        this.threshold = subTaskThreshold;
        return this;
    }

    
    
    /**
     * Build thread pool size for {@link ItemTask}
     * 
     * @param nSubThreads
     * @return {@link WorkItemExecutorBuilder} with nSubThreads
     */
    public WorkItemExecutorBuilder withSubThreads(int nSubThreads) {
        this.nThreads = nSubThreads;
        return this;
    }
    
    
    /**
     * Build {@link WorkItemExecutor} with or without a {@link WorkItemObserver}
     * 
     * @return {@link TaskTideExecutor} of {@link WorkItem}
     */
    public TaskTideExecutor<WorkItem> build() {
        if ( this.observer == null ) {
            return new WorkItemExecutor();
        }
        return new WorkItemExecutor(this.observer, this.nThreads, this.threshold);
    }
}
