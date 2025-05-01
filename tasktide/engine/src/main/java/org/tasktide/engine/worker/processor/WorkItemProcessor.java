/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.worker.WorkItemTimeKeeper;
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
    
    
    /**
     * Construct with workload
     * 
     * @param workload
     * @param threshold
     * @param executorService 
     */
    public WorkItemProcessor(List<WorkItem> workload, int threshold, ExecutorService executorService) {
        super(workload, threshold, executorService, new WorkItemTimeKeeper(100000), LogManager.getLogger(ItemTaskProcessor.class));
        this.worker = new WorkItemExecutor();
    }
    

    /**
     * Create a new sub processor from self
     * 
     * @param subList
     * @return {@link TaskTideProcessor}-{@link WorkItem}
     */
    @Override
    protected TaskTideProcessor<WorkItem> newSubProcessor(List<WorkItem> subList) {
        return new WorkItemProcessor(subList, threshold, executorService);
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
}
