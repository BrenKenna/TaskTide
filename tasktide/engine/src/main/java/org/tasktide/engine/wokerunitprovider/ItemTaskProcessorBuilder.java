/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.wokerunitprovider;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;

import org.tasktide.engine.worker.processor.ItemTaskProcessor;


/**
 * Class to hold the logic for constructing {@link TaskTideProcessor} for {@link ItemTask}
 * 
 * @author bkenna
 */
public class ItemTaskProcessorBuilder {
    
    // Attributes
    private List<ItemTask> workload;
    private int threshold;
    private ExecutorService executorService;
    private TaskTideExecutor<ItemTask> executor;
    
    
    /**
     * Build with {@link ItemTask} workload
     * 
     * @param workload
     * @return {@link ItemTaskProcessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withWorkload(List<ItemTask> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Build with sub tasking threshold
     * 
     * @param threshold
     * @return {@link ItemTaskPkTideProcessor} of {@link ItemTask}rocessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }
    
    
    /**
     * Build with provided {@link ExecutorService}
     * 
     * @param executorService
     * @return {@link ItemTaskPkTideProcessor} of {@link ItemTask}rocessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
    
    
    /**
     * Build with new {@link ExecutorService}
     * 
     * @param nThreads
     * @return {@link ItemTaskPkTideProcessor} of {@link ItemTask}rocessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withExecutorService(int nThreads) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
        return this;
    }
    
    
    /**
     * Build with provided {@link TaskTideExecutor} of {@link ItemTask}
     * 
     * @param executor
     * @return {@link ItemTaskPkTideProcessor} of {@link ItemTask}rocessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withSubExecutor(TaskTideExecutor<ItemTask> executor) {
        this.executor = executor;
        return this;
    }
    
    
    /**
     * Build {@link TaskTideProcessor} for {@link ItemTask}
     * 
     * @return {@link TaskTideProcesor} of {@link ItemTask}
     */
    public TaskTideProcessor<ItemTask> build() {
        return new ItemTaskProcessor(this.workload, this.threshold, this.executorService, this.executor);
    }
}
