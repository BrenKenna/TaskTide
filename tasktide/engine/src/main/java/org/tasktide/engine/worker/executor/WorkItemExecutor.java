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

import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.worker.TimeKeeper;
import org.tasktide.engine.observer.worker.executor.WorkItemExecutorObserver;
import org.tasktide.engine.observer.worker.timekeeper.WorkItemTimeKeeper;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Class to handle the execution of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemExecutor extends TaskTideExecutor<WorkItem> {

    // Attribures
    private final WorkItemExecutorObserver workItemObserver;
    
    /**
     * Construct {@link TaskTideExecutor} for {@link WorkItem} 
     * 
     */
    public WorkItemExecutor() {
        super(new WorkItemTimeKeeper(100000), LogManager.getLogger(ItemTaskExecutor.class));
        this.workItemObserver = new WorkItemExecutorObserver();
    }
    
    
    /**
     * Determine whether {@link TaskTideExecutor} should execute {@link WorkItem}
     *   Maybe consults the {@link TimeKeeper}.
     * 
     * @param task
     * @return boolean
     */
    @Override
    protected boolean shouldExecute(WorkItem task) {
        return task.getItemState() == ItemState.TODO; // Maybe consult Modifier if lock is verified etc?
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
        
        // Verify whether work item is processable
        logger.info("Thread '{}' verifying WorkItem availabilty:\t{}", Thread.currentThread().getName(), task.getItemName());
        if ( this.workItemObserver.verifyWorkItem(task) ) {
        
            // Configure ItemTaskProcessor
            logger.info(
          "Verification successful.Configuring ItemTaskProcessor for Workload of size '{}' on thread '{}' for WorkItem:\t'{}'",
                task.getTaskCount(), Thread.currentThread().getName(), task.getId()
            );
            TaskTideProcessor<ItemTask> itemTaskProcessor = this.workItemObserver.provideProcessor(task);
            
            // Execute workload of processor
            logger.info("Processor configured, processing workload for WorkItem:\t'{}'", task.getId());
            itemTaskProcessor.execute();
            
            // Leave work item observer periodically summarize states until done
            logger.info("ExecutorObserver polling ItemTaskStateSummary for WorkItem:\t'{}'", task.getId());
            this.workItemObserver.monitorUnitDone(task);
            
            // Handle post execution tasks
            this.workItemObserver.postProcess(task);
            return true;
        }
        
        // Otherwise pass
        else {
            logger.warn("Warning failed to verify availability of active WorkItem:\t'{}'",task.getId());
            return false;
        }
    }

    
    /**
     * Handle failure of {@link WorkItem} execution
     * 
     * @param task
     * @param ex 
     */
    @Override
    protected void handleFailure(WorkItem task, Exception ex) {
        task.setItemState(ItemState.ERROR); // One task enough for error state?
        logger.error(
            "Exception while executing task '{}' on thread '{}': {}",
            task.getItemName(), Thread.currentThread().getName(), ex.getMessage(), ex
        );
        if (ex instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
