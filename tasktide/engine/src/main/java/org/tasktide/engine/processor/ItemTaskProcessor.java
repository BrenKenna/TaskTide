/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.processor;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.executor.ItemTaskExecutor;
import org.tasktide.engine.executor.TaskTideExecutor;


/**
 * Concrete {@link ItemTask} {@link Processor}
 * 
 * @author bkenna
 */
public class ItemTaskProcessor extends Processor<ItemTask> {

    
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
        super(workload, threshold, executorService);
        this.worker = new ItemTaskExecutor();
    }

    
    /**
     * Create a new sub processor from self
     * 
     * @param subList
     * @return {@link Processor}-{@link ItemTask}
     */
    @Override
    protected Processor<ItemTask> newSubProcessor(List<ItemTask> subList) {
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
