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

    
    /**
     * Construct {@link TaskTideExecutor} for {@link WorkItem} 
     * 
     */
    public WorkItemExecutor() {
        super(new WorkItemObserver(), LogManager.getLogger(ItemTaskExecutor.class));
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
        result = this.observer.onTaskProcessing(task);
            
        // Evaluate task processing
        if ( result.isSuccess() ) {
            logger.info(
          "Task processing complete on WorkItem:\t{}",
             task.getId()
            );
            return true;
        }
        
        // Otherwise log warning
        else {
            logger.warn(
          "Warning, '{}' Observer '{}' onTaskProcessing failed for WorkItem:\t'{}'", 
             result.getType(), result.getFailedObserver(), task.getId()
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
}
