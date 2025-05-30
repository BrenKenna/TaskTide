/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.concurrent.Future;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.tasktracker.ExecutorServiceItem;
import org.tasktide.engine.tasktracker.ExecutorServiceTrackerWorkItem;

import org.tasktide.engine.worker.executor.WorkItemExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;


/**
 * Concrete {@link ItemTask} {@link TaskTideProcessor}
 * 
 * @author bkenna
 */
public class WorkItemProcessor extends TaskTideProcessor<WorkItem> {
    
    // Attributes
    private final WorkItemExecutor worker;
    private final ExecutorServiceTrackerWorkItem executorServiceTracker;
    
    
    /**
     * Construct with workload
     * 
     * @param workload
     * @param threshold
     * @param executorService 
     */
    public WorkItemProcessor(List<WorkItem> workload, int threshold, ExecutorService executorService) {
        super(workload, threshold, executorService, LogManager.getLogger(ItemTaskProcessor.class));
        this.worker = new WorkItemExecutor();
        this.executorServiceTracker = ExecutorServiceTrackerWorkItem.getInstance();
    }
    
    
    /**
     * Construct with all attributes
     * 
     * @param workload
     * @param threshold
     * @param executorService
     * @param executor 
     */
    public WorkItemProcessor(
        List<WorkItem> workload,
        int threshold,
        ExecutorService executorService,
        TaskTideExecutor<WorkItem> executor
    ) {
        super(workload, threshold, executorService, LogManager.getLogger(ItemTaskProcessor.class));
        this.worker = (WorkItemExecutor) executor;
        this.executorServiceTracker = ExecutorServiceTrackerWorkItem.getInstance();
    }
    

    /**
     * Create a new sub processor from self
     * 
     * @param subList
     * @return {@link TaskTideProcessor}-{@link WorkItem}
     */
    @Override
    protected TaskTideProcessor<WorkItem> newSubProcessor(List<WorkItem> subList) {
        return new WorkItemProcessor(subList, this.threshold, this.executorService);
    }

    
    /**
     * Fetch {@link WorkItemExecutor}
     * 
     * @return {@link TaskTideExecutor}-{@link WorkItem}
     */
    @Override
    protected TaskTideExecutor<WorkItem> getExecutor() {
        return this.worker;
    }

    
    /**
     * Add workload to the {@link ExecutorServiceTrackerWorkItem}
     * 
     * @param subList
     * @param future 
     */
    @Override
    protected void addTasksToTracker(List<WorkItem> subList, Future future) {
        for ( WorkItem task : subList ) {
            ExecutorServiceItem<WorkItem> item = new ExecutorServiceItem<>(task, future);
            this.executorServiceTracker.markTask(task.getId(), item);
        }
    }

    @Override
    protected List<List<WorkItem>> parallelChunks(List<WorkItem> workload) {
        // Initialize variables
        List<WorkItem> slize = new ArrayList<>();
        List< List<WorkItem> > results = new ArrayList<>();
        
        // Initialize batch handler
        int workItemThreads = TaskTideExecutorServiceProvider.getInstance().getWorkItemThreads();
        int batchSize = workload.size() / workItemThreads;
        
        // Fetch sclies
        int start = 0, end = 0;
        while ( end < workload.size() ) {
            results.add(workload.subList(start, end));
            start = end + 1;
            end = start + batchSize;
            
            if ( end > workload.size() ) {
                results.add(workload.subList(start, workload.size()));
            }
        }
        
        
        return results;
    }
}
