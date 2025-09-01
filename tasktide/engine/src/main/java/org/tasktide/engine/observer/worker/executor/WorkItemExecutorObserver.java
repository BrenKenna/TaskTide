/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.engine.observer.worker.executor;

import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;
import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.engine.observer.ObserverResult;
import org.tasktide.engine.observer.worker.ExecutorObserver;
import org.tasktide.engine.observer.WorkerObserver;


/**
 * {@link WorkerObserver} of {@link ExecutorObserver} to handle tasks around
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
    public ObserverResult onTaskProcessing(WorkItem task) {
        if ( this.pollUntilDone(task) ) {
            return ObserverResult.success();
        }
        else {
            return ObserverResult.failure(this, true);
        }
    }

    
    /**
     * Handle task post processing
     * 
     * @param task 
     */
    @Override
    public ObserverResult onTaskEnd(WorkItem task) {
        
        // Handle unlocks
        int unlocked = this.handleUnlocks(task);
        logger.info("Unlocked N = '{}' sub tasks for WorkItem:\t'{}'", unlocked, task.getId());
        
        // Return whether completed
        if ( task.getTaskCount() == task.getTaskDone() ) {
            return ObserverResult.success();
        }
        else {
            return ObserverResult.failure(this, true);
        }
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
            logger.info("Letting '{}'ms elapse for state monitoring of WorkItem:\t'{}'", 
                sleepTime, task.getId()
            );
            try {TimeUnit.MILLISECONDS.sleep(sleepTime);} catch(InterruptedException ex) {Thread.currentThread().interrupt();}
            
            // Fetch summary
            stateSummary = new StateSummary<>(task.summarizeByState());
            logger.info("Displaying Iter-'{}' StateSummary of WorkItem:\t'{}'\n\n{}\n\n", 
                counter, task.getId(), stateSummary.toJsonDoc()
            );
            
            // Sum of touched ItemTasks, did any raise TK error, Executor have states?
            int progress = stateSummary.getCount(ItemState.DONE) + stateSummary.getCount(ItemState.ERROR) ;
            counter++;
            done = progress == expected;
        }
        
        // Return whether any changes in states
        totalTouched = stateSummary.getCount(ItemState.DONE) + stateSummary.getCount(ItemState.ERROR);
        logger.info("Completed processing of '{}' WorkItems with '{}'", totalTouched, expected);
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
    public ObserverResult onTaskStart(WorkItem task) {
    
        // Log warning and flag no open tasks
        if ( task.fetchByStates().get(ItemState.TODO).isEmpty() ) {
            logger.warn("Warning, no open tasks detected for WorkItem:\t'{}'", task.getId());
            return ObserverResult.failure(this, false);
        }

        // Return open tasks flag
        return ObserverResult.success();
    }
    
    
    /**
     * Return observer name
     * 
     * @return 
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
