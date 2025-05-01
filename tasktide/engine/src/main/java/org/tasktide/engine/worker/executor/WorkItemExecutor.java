/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.observer.worker.WorkItemTimeKeeper;


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
        super(new WorkItemTimeKeeper(100000), LogManager.getLogger(ItemTaskExecutor.class));
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
     * Handle execution of {@link WorkItem}, ex no point paralllizing if one task etc
     * 
     * @param task
     * @return boolean
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    protected boolean executeTask(WorkItem task) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
