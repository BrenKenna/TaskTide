/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine;

import org.tasktide.engine.wokerunitprovider.ItemTaskExecutorBuilder;
import org.tasktide.engine.wokerunitprovider.ItemTaskObserverBuilder;
import org.tasktide.engine.wokerunitprovider.ItemTaskProcessorBuilder;

import org.tasktide.engine.wokerunitprovider.WorkItemExecutorBuilder;
import org.tasktide.engine.wokerunitprovider.WorkItemObserverBuilder;
import org.tasktide.engine.wokerunitprovider.WorkItemProcessorBuilder;


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
    
    // ItemTask builder attributes
    private final ItemTaskObserverBuilder itemTaskObsBuilder;
    private final ItemTaskProcessorBuilder itemTaskProcBuilder;
    private final ItemTaskExecutorBuilder itemTaskExecBuilder;
    
    
    /**
     * Construct with new builders
     */
    public TaskTideWorkerUnitProvider() {
        
        // Set WorkItem builders
        this.workItemObsBuilder = new WorkItemObserverBuilder();
        this.workItemProcBuilder = new WorkItemProcessorBuilder();
        this.workItemExecBuilder = new WorkItemExecutorBuilder();
        
        // Set ItemTask builders
        this.itemTaskObsBuilder = new ItemTaskObserverBuilder();
        this.itemTaskProcBuilder = new ItemTaskProcessorBuilder();
        this.itemTaskExecBuilder = new ItemTaskExecutorBuilder();
    }

    
    /**
     * Get builder for {@link TaskTideEngineObserver} for {@link WorkItem}
     * 
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder getWorkItemObsBuilder() {
        return workItemObsBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideProcessor} for {@link WorkItem}
     * 
     * @return {@link WorkItemProcessorBuilder}
     */
    public WorkItemProcessorBuilder getWorkItemProcBuilder() {
        return workItemProcBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideEexecutor} for {@link WorkItem}
     * 
     * @return {@link WorkItemExecutorBuilder}
     */
    public WorkItemExecutorBuilder getWorkItemExecBuilder() {
        return workItemExecBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideEngineObserver} for {@link ItemTask}
     * 
     * @return {@link ItemTaskObserverBuilder}
     */
    public ItemTaskObserverBuilder getItemTaskObsBuilder() {
        return itemTaskObsBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideProcessor} for {@link ItemTask}
     * 
     * @return {@link ItemTaskProcessorBuilder}
     */
    public ItemTaskProcessorBuilder getItemTaskProcBuilder() {
        return itemTaskProcBuilder;
    }

    
    /**
     * Get builder for {@link TaskTideEexecutor} for {@link ItemTask}
     * 
     * @return {@link ItemTaskExecutorBuilder}
     */
    public ItemTaskExecutorBuilder getItemTaskExecBuilder() {
        return itemTaskExecBuilder;
    }
}
