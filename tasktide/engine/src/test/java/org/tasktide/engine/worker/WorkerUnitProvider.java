/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;
import org.tasktide.engine.worker.processor.WorkItemProcessor;

/**
 *
 * @author bkenna
 */
public class WorkerUnitProvider {
    
    // Attributes
    private final TaskTideEngineObserver<WorkItem> workItemObserver;
    private final TaskTideEngineObserver<ItemTask> itemTaskObserver;
    // private final Logger logger = LogManager.getLogger(WorkerUnitProvider.class);
    
    
    /**
     * Construct with fields
     * 
     * @param workItemObserver
     * @param itemTaskObserver 
     */
    public WorkerUnitProvider(
        TaskTideEngineObserver<WorkItem> workItemObserver,
        TaskTideEngineObserver<ItemTask> itemTaskObserver
    ) {
        this.workItemObserver = workItemObserver;
        this.itemTaskObserver = itemTaskObserver;
    }
    
    
    /**
     * Creates a {@link WorkItemProcessor}
     * 
     * @param workload
     * @param threshold
     * @param nThreadPools
     * @return TaskTideProcessor of {@link Worktem}
     */
    public TaskTideProcessor<WorkItem> fetchWorkItemProcessor(List<WorkItem> workload, int threshold, int nThreadPools) {
    
        // Initialize required variables
        TaskTideProcessor<WorkItem> workItemProcessor;
        ExecutorService executorService;
        
        // Configure processor
        executorService = Executors.newFixedThreadPool(nThreadPools);
        workItemProcessor = new WorkItemProcessor(workload, threshold, executorService);
        
        // Return processor
        return workItemProcessor;
    }
    
    
    /**
     * Creates a {@link ItemTaskProcessor}
     * 
     * @param workload
     * @param threshold
     * @param nThreadPools
     * @return TaskTideProcessor of {@link ItemTask}
     */
    public TaskTideProcessor<ItemTask> fetchItemTaskProcessor(List<ItemTask> workload, int threshold, int nThreadPools) {

        // Initialize required variables
        TaskTideProcessor<ItemTask> itemTaskProcessor;
        ExecutorService executorService;
        
        // Configure processor
        executorService = Executors.newFixedThreadPool(nThreadPools);
        itemTaskProcessor = new ItemTaskProcessor(workload, threshold, executorService);
        
        // Return processor
        return itemTaskProcessor;
    }
    
    
    
    public TaskTideExecutor<WorkItem> fetchWorkItemExecutor() {
    
        // Initialize required variables
        TaskTideExecutor<WorkItem> workItemExecutor;
        
        // Return results
        return null;
    }
}
