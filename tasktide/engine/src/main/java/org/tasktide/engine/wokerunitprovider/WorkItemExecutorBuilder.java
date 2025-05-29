/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.wokerunitprovider;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.executor.WorkItemExecutor;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;

import org.tasktide.engine.tasktracker.TaskTracker;

import org.tasktide.engine.wokerunitprovider.WorkItemObserverBuilder;


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
     * @param taskTracker
     * @param maxTime
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public WorkItemExecutorBuilder withWorkItemObserver(List<WorkItem> workload, TaskTracker taskTracker, int maxTime) {
    
        // Set required vars
        WorkItemObserverBuilder obsBuilder;
        
        // Build with work item observer
        obsBuilder = new WorkItemObserverBuilder();
        this.observer = obsBuilder
            .withWorkload(workload)
            .withTaskTracker(taskTracker)
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build with {@link WorkItemObserver}
     * 
     * @param taskTracker
     * @param maxTime
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public WorkItemExecutorBuilder withWorkItemObserver(TaskTracker taskTracker, int maxTime) {
    
        // Set required vars
        WorkItemObserverBuilder obsBuilder;
        
        // Build with work item observer
        obsBuilder = new WorkItemObserverBuilder();
        this.observer = obsBuilder
            .withTaskTracker(taskTracker)
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
            return new WorkItemExecutor(this.nThreads, this.threshold);
        }
        return new WorkItemExecutor(this.observer, this.nThreads, this.threshold);
    }
}
