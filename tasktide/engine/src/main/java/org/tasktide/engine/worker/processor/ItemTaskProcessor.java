/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.worker.executor.ItemTaskExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;

import org.tasktide.engine.observer.worker.timekeeper.ItemTaskTimeKeeper;


/**
 * Concrete {@link ItemTask} {@link TaskTideProcessor}
 * 
 * @author bkenna
 */
public class ItemTaskProcessor extends TaskTideProcessor<ItemTask> {

    // Attributes
    private final ItemTaskExecutor worker;
    
    
    /**
     * Construct with workload
     * 
     * @param workload
     * @param threshold
     * @param executorService
     */
    public ItemTaskProcessor(List<ItemTask> workload, int threshold, ExecutorService executorService) {
        super(workload, threshold, executorService, new ItemTaskTimeKeeper(100000), LogManager.getLogger(ItemTaskProcessor.class));
        this.worker = new ItemTaskExecutor();
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
}
