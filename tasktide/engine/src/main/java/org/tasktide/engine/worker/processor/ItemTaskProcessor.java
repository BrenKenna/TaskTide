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
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.tasktracker.ExecutorServiceItem;

import org.tasktide.engine.tasktracker.FutureTrackers;

import org.tasktide.engine.worker.executor.ItemTaskExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;


/**
 * Concrete {@link ItemTask} {@link TaskTideProcessor}
 * 
 * @author bkenna
 */
public class ItemTaskProcessor extends TaskTideProcessor<ItemTask> {

    // Attributes
    private final ItemTaskExecutor worker;;
    
    
    /**
     * Construct with workload
     * 
     * @param workload
     * @param threshold
     * @param executorService
     */
    public ItemTaskProcessor(
        List<ItemTask> workload,
        @ConfigProperty(name = "task-tide.engine.worker.processor.threshold.itemtask", defaultValue = "2") int threshold,
        ExecutorService executorService
    ) {
        super(workload, threshold, executorService, LogManager.getLogger(ItemTaskProcessor.class));
        this.worker = new ItemTaskExecutor();
    }
    
    
    /**
     * Construct with all attributes
     * 
     * @param workload
     * @param threshold
     * @param executorService
     * @param executor 
     */
    public ItemTaskProcessor(
        List<ItemTask> workload,
        @ConfigProperty(name = "task-tide.engine.worker.processor.threshold.itemtask", defaultValue = "2") int threshold,
        ExecutorService executorService,
        TaskTideExecutor<ItemTask> executor
    ) {
        super(workload, threshold, executorService, LogManager.getLogger(ItemTaskProcessor.class));
        this.worker = (ItemTaskExecutor) executor;
    }

    
    /**
     * Create a new sub processor from self
     * 
     * @param subList
     * @return {@link TaskTideProcessor}-{@link ItemTask}
     */
    @Override
    protected TaskTideProcessor<ItemTask> newSubProcessor(List<ItemTask> subList) {
        return new ItemTaskProcessor(subList, threshold, executorService);
    }

    
    /**
     * Provide {@link ItemTask} worker
     * 
     * @return {@link TaskTideExecutor}-{@link ItemTask}
     */
    @Override
    protected TaskTideExecutor<ItemTask> getExecutor() {
        return this.worker;
    }
    
    
    /**
     * Add workload to the {@link ExecutorServiceTrackerWorkItem}
     * 
     * @param subList
     * @param future 
     */
    @Override
    protected void addTasksToTracker(List<ItemTask> subList, Future future) {
        for ( ItemTask task : subList ) {
            ExecutorServiceItem<ItemTask> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.ITEM_TASK_TRACKER.markTask(task.getId(), item);
        }
    }
    
    
    @Override
    protected List<List<ItemTask>> parallelChunks(List<ItemTask> workload) {
        // Initialize variables
        List<ItemTask> slize = new ArrayList<>();
        List< List<ItemTask> > results = new ArrayList<>();
        
        // Initialize batch handler
        int itemTaskThreads = TaskTideExecutorServiceProvider.getInstance().getItemTaskThreads();
        int batchSize = workload.size() / itemTaskThreads;
        
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
