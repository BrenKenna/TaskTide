/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.engine.observer.ObserverResult;

import org.tasktide.engine.observer.chain.WorkItemObserver;
import org.tasktide.engine.worker.processor.TaskTideProcessor;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;


/**
 * Class to handle the execution of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemExecutor extends TaskTideExecutor<WorkItem> {

    // Attributes
    private final int nThreads, taskThreshold;
    
    
    /**
     * Construct {@link TaskTideExecutor} for {@link WorkItem} 
     * 
     */
    public WorkItemExecutor() {
        super(new WorkItemObserver(), LogManager.getLogger(ItemTaskExecutor.class));
        this.nThreads = 4;
        this.taskThreshold = 2;
    }
    
    
    /**
     * Construct {@link WorkItem} with thread and task threshold values for {@link ItemTask}
     * 
     * @param nThreads
     * @param taskThrehold 
     */
    public WorkItemExecutor(
        @ConfigProperty(name="task-tide.engine.worker.processor.thread-count.itemtask", defaultValue="2") int nThreads,
        @ConfigProperty(name="task-tide.engine.worker.processor.task-size-threshold.itemtask", defaultValue="2") int taskThrehold
    ) {
        super(new WorkItemObserver(), LogManager.getLogger(ItemTaskExecutor.class));
        this.nThreads = nThreads;
        this.taskThreshold = taskThrehold;
    }
    
    
    /**
     * Handle execution of {@link WorkItem}, ex no point paralllizing if one task etc.
     * 
     * Since WorkItem has workload, can track it's own
     * 
     * @param task
     * @return boolean
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    protected boolean executeTask(WorkItem task) throws IOException, InterruptedException {
        
        // Configure ItemTaskProcessor
        ObserverResult result;
        logger.info(
      "Configuring ItemTaskProcessor for Workload of size '{}' on thread '{}' for WorkItem:\t'{}'",
         task.getTaskCount(), Thread.currentThread().getName(), task.getId()
        );
        TaskTideProcessor<ItemTask> itemTaskProcessor = this.provideProcessor(task);
            
        // Execute workload of processor
        logger.info("Processor configured, processing workload for WorkItem:\t'{}'", task.getId());
        itemTaskProcessor.execute();
            
        // Leave work item observer periodically summarize states until done
        logger.info("ExecutorObserver polling ItemTaskStateSummary for WorkItem:\t'{}'", task.getId());
            
        // Evaluate task processing
        if ( this.observer.onTaskProcessing(task) ) {
            logger.info(
          "Task processing complete on WorkItem:\t{}",
             task.getId()
            );
            return true;
        }
        
        // Otherwise log warning
        else {
            logger.warn(
          "Warning, Observer checks onTaskProcessing failed for WorkItem:\t'{}'", 
             task.getId()
            );
            return false;
        }
    }
    
    
    /**
     * Provide sub-processor
     * 
     * @param task
     * @return {@link TaskTideProcessor}-{@link WorkItem},{@link ItemTask}
     */
    public TaskTideProcessor<ItemTask> provideProcessor(WorkItem task) {
        
        // Fetch required components
        List<ItemTask> toDo = task.fetchByStates().get(ItemState.TODO);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        
        // Construct sub processor
        TaskTideProcessor<ItemTask> itemTaskProcessor = new ItemTaskProcessor(toDo, 2, executorService);
        
       // Return processor
       return itemTaskProcessor;
    }

    
    /**
     * Get number threads for sub-processor
     * 
     * @return int
     */
    public int getnThreads() {
        return nThreads;
    }

    
    /**
     * Get task threshold limit for sub-processor
     * 
     * @return int
     */
    public int getTaskThreshold() {
        return taskThreshold;
    }
}
