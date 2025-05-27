/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor.builders;

import org.tasktide.engine.observer.builders.ItemTaskObserverBuilder;
import org.tasktide.engine.observer.builders.WorkItemObserverBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.worker.processor.builders.ItemTaskProcessorBuilder;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.executor.WorkItemExecutor;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;

import org.tasktide.engine.worker.processor.TaskTideProcessor;

import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Class to hold the logic for constructing {@link TaskTideExecutor} for {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemExecutorBuilder {
    
    // Attributes
    private TaskTideEngineObserver<WorkItem> observer;
    private TaskTideProcessor<ItemTask> subProcessor;
    private TaskTideEngineObserver<ItemTask> subObserver;
    
    
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
     * Build with a new {@link ItemTaskObserver}
     * 
     * @param workload
     * @param taskTracker
     * @param maxTime
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public WorkItemExecutorBuilder withSubObserver(List<ItemTask> workload, TaskTracker taskTracker, int maxTime) {
        
        // Set required vars
        ItemTaskObserverBuilder obsBuilder;
        
        // Build observer
        obsBuilder = new ItemTaskObserverBuilder();
        this.subObserver = obsBuilder
            .withWorkload(workload)
            .withTaskTracker(taskTracker)
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build with specific {@link ItemTaskObserver}
     * 
     * @param subObserver
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public WorkItemExecutorBuilder withSubObserver(TaskTideEngineObserver<ItemTask> subObserver) {
        this.subObserver = subObserver;
        return this;
    }
    
    
    /**
     * Build with {@link ItemTaskProcessor} processor using pre-configured dependencies
     * 
     * @param workload
     * @param threshold
     * @param executor
     * @param executorService
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public WorkItemExecutorBuilder withSubProcessor(List<ItemTask> workload, int threshold, TaskTideExecutor<ItemTask> executor, ExecutorService executorService) {
    
        // Set required vars
        ItemTaskProcessorBuilder procBuilder;
        
        // Build ItemTaskProcessor
        procBuilder = new ItemTaskProcessorBuilder();
        this.subProcessor = procBuilder
            .withWorkload(workload)
            .withThreshold(threshold)
            .withSubExecutor(executor)
            .withExecutorService(executorService)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build with {@link ItemTaskProcessor} processor
     * 
     * @param subProcessor
     * @return {@link WorkItemExecutorBuilder} with {@link TaskTideProcessor} of {@link ItemTask}
     */
    public WorkItemExecutorBuilder withSubProcessor(TaskTideProcessor<ItemTask> subProcessor) {
        this.subProcessor = subProcessor;
        return this;
    }
    
    
    /**
     * Build {@link WorkItemExecutor} with default values for {@link ItemTaskProcessor}
     * 
     * @return {@link TaskTideExecutor} of {@link WorkItem}
     */
    public TaskTideExecutor<WorkItem> build() {
        return new WorkItemExecutor(this.observer, 2, 2, this.subProcessor, subObserver);
    }
    
    
    /**
     * Build {@link WorkItemExecutor} with values for {@link ItemTaskProcessor}
     * 
     * @param nSubThreads
     * @param nSubTaskThreshold
     * @return {@link TaskTideExecutor} of {@link WorkItem}
     */
    public TaskTideExecutor<WorkItem> build(int nSubThreads, int nSubTaskThreshold) {
        return new WorkItemExecutor(
    this.observer,
         nSubThreads,
      nSubTaskThreshold,
      this.subProcessor,
       this.subObserver
        );
    }
}
