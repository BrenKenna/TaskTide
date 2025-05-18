/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.executor;

import org.apache.logging.log4j.LogManager;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.engine.observer.worker.ExecutorObserver;



/**
 * {@link TaskTideWorkerObserver} of {@link ExecutorObserver} to handle tasks around
 *  execution of {@link WorkItem} {@link Workload}
 * 
 * @author bkenna
 */
public class WorkItemExecutorObserver extends ExecutorObserver<WorkItem, ItemTask> {
    
    /**
     * Construct embedding logger
     */
    public WorkItemExecutorObserver() {
        super(LogManager.getLogger(WorkItemExecutorObserver.class));
    }
    
    
    /**
     * Monitor task until done, may want different ways later
     * 
     * @param task 
     */
    @Override
    public boolean onTaskProcessing(WorkItem task) {
        return this.pollUntilDone(task);
    }

    
    /**
     * Handle task post processing
     * 
     * @param task 
     */
    @Override
    public boolean onTaskEnd(WorkItem task) {
        
        // Handle unlocks
        int unlocked = this.handleUnlocks(task);
        logger.info("Unlocked N = '{}' sub tasks for WorkItem:\t'{}'", unlocked, task.getId());
        
        // Return whether completed
        return task.getTaskCount() == task.getTaskDone();
    }
    
    
    /**
     * Poll a {@link StateSummary} of {@link WorkItem} until done
     * 
     * @param task 
     * @return boolean
     */
    public boolean pollUntilDone(WorkItem task) {
        
        // Initialize vars
        boolean done = false;
        int baseDelaySeconds = 1, counter = 0, expected = task.getTaskCount(), totalTouched;
        long sleepTime;
        StateSummary<ItemState> stateSummary = new StateSummary<>();
        
        // Wait until done
        logger.info("Begining state monitoring of WorkItem:\t'{}'", task.getId());
        while ( !done ) {
        
            // Measure delay capping to 512
            int waitVal = baseDelaySeconds * (int)Math.pow(2, counter - 1);
            if ( waitVal <= 5) {
                sleepTime = 10 * 1000L;
            }
            else {
                sleepTime = Math.min(waitVal, 512) * 1000L;
            }
            
            // Wait
            logger.info("Letting '{}'s elapse for state monitoring of WorkItem:\t'{}'", 
                sleepTime, task.getId()
            );
            try {Thread.sleep(sleepTime);} catch(InterruptedException ex) {Thread.currentThread().interrupt();}
            
            // Fetch summary
            stateSummary = new StateSummary<>(task.summarizeByState());
            logger.info("Displaying Iter-'{}' StateSummary of WorkItem:\t'{}'\n\n{}\n\n", 
                counter, task.getId(), stateSummary.toJsonDoc()
            );
            
            // Sum of touched ItemTasks, did any raise TK error, Executor have states?
            int progress = stateSummary.getCount(ItemState.DONE) + stateSummary.getCount(ItemState.ERROR) ;
            done = progress == expected;
            
        }
        
        // Return whether any changes in states
        totalTouched = stateSummary.getCount(ItemState.DONE) + stateSummary.getCount(ItemState.ERROR);
        return totalTouched == expected;
    }
    
    
    /**
     * Handle unlocking sub task {@link ItemTask}, returning count of unlocked
     * 
     * @param task
     * @return int
     */
    public int handleUnlocks(WorkItem task) {
        List<ItemTask> forUnlock = task.fetchByStates().get(ItemState.FOR_UNLOCK);
        if ( !forUnlock.isEmpty() ) {
            for ( ItemTask elm : forUnlock ) {
                elm.setTaskState(TaskState.PENDING);
                elm.setTaskLog(null);
            }
        }
        return forUnlock.size();
    }
    
    
    /**
     * Verify {@link WorkItem} has open tasks before processing
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskStart(WorkItem task) {
    
        // Log warning and flag no open tasks
        if ( task.fetchByStates().get(ItemState.TODO).isEmpty() ) {
            logger.warn("Warning, no open tasks detected for WorkItem:\t'{}'", task.getId());
            return false;
        }

        // Return open tasks flag
        return true;
    }
}
