/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.engine.observer.worker.ExecutorObserver;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;


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
     * Provide sub-processor
     * 
     * @param task
     * @return {@link TaskTideProcessor}-{@link WorkItem},{@link ItemTask}
     */
    @Override
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
        
        // Handle task counts, and done time if needed
        this.handleTaskCounts(task);
        
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
        int baseDelaySeconds = 1, counter = 0, expected = task.getTaskCount();
        long sleepTime;
        StateSummary<ItemState> baseState = new StateSummary<>(task.summarizeByState());
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
        return baseState.getCount(ItemState.TODO) == stateSummary.getCount(ItemState.TODO);
    }
    
    
    /**
     * Update {@link WorkItem} progress, applying done date if completed
     * 
     * @param task 
     */
    public void handleTaskCounts(WorkItem task) {
        
        // Update task counts
        task.setTaskCounts();
        
        // Set done date if all done
        if ( task.getTaskCount() == task.getTaskDone() ) {
            long doneTime = task.getWorkload().getLatestDone();
            task.setItemState(ItemState.DONE);
            task.setDoneDate(doneTime);
        }
        
        // Otherwise unlock work item
        else {
            task.setItemState(ItemState.TODO);
            task.setLockDate(0L);
            task.setLockId("");
        }
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
     * Verify {@link WorkItem} availability before processing
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskStart(WorkItem task) {
    
        // Inititalize test params
        boolean isPending, hasOpenTasks, hasLocked;
        
        // Check that work item is still open
        isPending = task.getItemState() == ItemState.TODO;
        
        // Check work item has open tasks
        hasOpenTasks = !task.fetchByStates().get(ItemState.TODO).isEmpty();
        
        // Lock item success
        if ( isPending && hasOpenTasks ) {
            String lockId = task.getLockId();
            hasLocked = task.getLockId().equals(lockId);
        }
        else {
            hasLocked = false;
            logger.warn("Detected no open tasks, or other process lock on WorkItem:\t'{}'", task.getId());
        }

        // Check whether there are open ItemTasks
        return isPending && hasOpenTasks & hasLocked;
    }
}
