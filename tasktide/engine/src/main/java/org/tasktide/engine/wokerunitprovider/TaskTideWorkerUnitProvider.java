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

import org.tasktide.engine.traversers.ItemTaskTraverserBuilder;

import org.tasktide.engine.traversers.WorkItemTraverserBuilder;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Single class to provide the builders for Observer, Processors and Executors
 * 
 * @author bkenna
 */
public class TaskTideWorkerUnitProvider {
    
    // WorkItem builder attributes
    private final WorkItemObserverBuilder workItemObsBuilder;
    private final WorkItemProcessorBuilder workItemProcBuilder;
    private final WorkItemExecutorBuilder workItemExecBuilder;
    private final WorkItemTraverserBuilder workItemTravBuilder;
    
    
    // ItemTask builder attributes
    private final ItemTaskObserverBuilder itemTaskObsBuilder;
    private final ItemTaskProcessorBuilder itemTaskProcBuilder;
    private final ItemTaskExecutorBuilder itemTaskExecBuilder;
    private final ItemTaskTraverserBuilder itemTaskTravBuilder;
    
    
    /**
     * Construct with new builders
     */
    public TaskTideWorkerUnitProvider() {
        
        // Set WorkItem builders
        this.workItemObsBuilder = new WorkItemObserverBuilder();
        this.workItemProcBuilder = new WorkItemProcessorBuilder();
        this.workItemExecBuilder = new WorkItemExecutorBuilder();
        this.workItemTravBuilder = new WorkItemTraverserBuilder();
        
        // Set ItemTask builders
        this.itemTaskObsBuilder = new ItemTaskObserverBuilder();
        this.itemTaskProcBuilder = new ItemTaskProcessorBuilder();
        this.itemTaskExecBuilder = new ItemTaskExecutorBuilder();
        this.itemTaskTravBuilder = new ItemTaskTraverserBuilder();
    }

    
    /**
     * Get builder for {@link TaskTideEngineObserver} for {@link WorkItem}
     * 
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder getWorkItemObsBuilder() {
        return this.workItemObsBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideProcessor} for {@link WorkItem}
     * 
     * @return {@link WorkItemProcessorBuilder}
     */
    public WorkItemProcessorBuilder getWorkItemProcBuilder() {
        return this.workItemProcBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideExecutor} for {@link WorkItem}
     * 
     * @return {@link WorkItemExecutorBuilder}
     */
    public WorkItemExecutorBuilder getWorkItemExecBuilder() {
        return this.workItemExecBuilder;
    }
    
    
    /**
     * Get {@link WorkItemTraverserBuilder} for {@link WorkItem}
     * 
     * @return {@link WorkItemTraverserBuilder}
     */
    public WorkItemTraverserBuilder getWorkItemTravBuilder() {
        return this.workItemTravBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideEngineObserver} for {@link ItemTask}
     * 
     * @return {@link ItemTaskObserverBuilder}
     */
    public ItemTaskObserverBuilder getItemTaskObsBuilder() {
        return this.itemTaskObsBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideProcessor} for {@link ItemTask}
     * 
     * @return {@link ItemTaskProcessorBuilder}
     */
    public ItemTaskProcessorBuilder getItemTaskProcBuilder() {
        return this.itemTaskProcBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideExecutor} for {@link ItemTask}
     * 
     * @return this.{@link ItemTaskExecutorBuilder}
     */
    public ItemTaskExecutorBuilder getItemTaskExecBuilder() {
        return this.itemTaskExecBuilder;
    }
    
    
    /**
     * Get {@link ItemTaskTraverserBuilder} for {@link ItemTask}
     * 
     * @return {@link ItemTaskTraverserBuilder}
     */
    public ItemTaskTraverserBuilder getItemTaskTravBuilder() {
        return this.itemTaskTravBuilder;
    }
}