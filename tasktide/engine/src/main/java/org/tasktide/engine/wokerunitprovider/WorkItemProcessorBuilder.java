/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.wokerunitprovider;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;

import org.tasktide.engine.worker.processor.WorkItemProcessor;


/**
 * Class to hold the logic for constructing {@link TaskTideProcessor} for {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemProcessorBuilder {
    
    // Attributes
    private List<WorkItem> workload;
    private int threshold;
    private ExecutorService executorService;
    private TaskTideExecutor<WorkItem> executor;
    
    
    /**
     * Build with {@link WorkItem} workload
     * 
     * @param workload
     * @return {@link WorkItemProcessorBuilder} for {@link TaskTideProcessor} of {@link WorkItem}
     */
    public WorkItemProcessorBuilder withWorkload(List<WorkItem> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Build with sub tasking threshold
     * 
     * @param threshold
     * @return {@link WorkItemPkTideProcessor} of {@link WorkItem}rocessorBuilder} for {@link TaskTideProcessor} of {@link WorkItem}
     */
    public WorkItemProcessorBuilder withThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }
    
    
    /**
     * Build with provided {@link ExecutorService}
     * 
     * @param executorService
     * @return {@link WorkItemPkTideProcessor} of {@link WorkItem}rocessorBuilder} for {@link TaskTideProcessor} of {@link WorkItem}
     */
    public WorkItemProcessorBuilder withExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
    
    
    /**
     * Build with new {@link ExecutorService}
     * 
     * @param nThreads
     * @return {@link WorkItemPkTideProcessor} of {@link WorkItem}rocessorBuilder} for {@link TaskTideProcessor} of {@link WorkItem}
     */
    public WorkItemProcessorBuilder withExecutorService(int nThreads) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
        return this;
    }
    
    
    /**
     * Build with provided {@link TaskTideExecutor} of {@link WorkItem}
     * 
     * @param executor
     * @return {@link WorkItemPkTideProcessor} of {@link WorkItem}rocessorBuilder} for {@link TaskTideProcessor} of {@link WorkItem}
     */
    public WorkItemProcessorBuilder withSubExecutor(TaskTideExecutor<WorkItem> executor) {
        this.executor = executor;
        return this;
    }
    
    
    /**
     * Build {@link TaskTideProcessor} for {@link WorkItem}
     * 
     * @return {@link TaskTideProcesor} of {@link WorkItem}
     */
    public TaskTideProcessor<WorkItem> build() {
        return new WorkItemProcessor(
     this.workload,
     this.threshold,
this.executorService,
     this.executor
        );
    }
}
