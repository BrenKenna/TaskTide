/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.trackers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.worker.TaskTideEngineUtility;


/**
 * Class to the hold the logic for how to wait on completion
 *  of tasks processing for {@link TaskTideModel} through the
 *  {@link ExecutorServiceTracker} interface.
 *
 * @param <T> of {@link TaskTideModel}
 * @author Bren
 */
public class TrackerWaiter<T extends TaskTideModel<T>> {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TrackerWaiter.class);
    private final Class<T> TYPE;
    private final String ID;
    private final List<T> TASKS, TASKS_DONE;
    private final ExecutorServiceTracker<T> TRACKER;
    
    private int failed, done, active, iterations = 0;
    private final Random rand = new Random();
    
    
    /**
     * Package private waiter for provided tasks
     * 
     * @param tasks 
     */
    TrackerWaiter(List<T> tasks, ExecutorServiceTracker<T> tracker, Class<T> type) {
        this.TYPE = type;
        this.ID = type.getSimpleName() + "-TrackerWaiter-" + UUID.randomUUID().toString();
        this.TASKS = tasks;
        this.TASKS_DONE = new ArrayList<>();
        this.TRACKER = tracker;
    }
    
    
    /**
     * Wait on workload processing to be completed.
     *  Either by completion of all tasks, or threshold
     *  of failures reached
     * 
     * @return boolean
     */
    public boolean waitForWorkload() {
        int total = this.TASKS.size();
        boolean wereKilled = false;
        
        if ( this.TRACKER.getIds().isEmpty() ) {
            LOGGER.error("Error, FutureTracker is empty");
            return false;
        }

        while ( this.TASKS_DONE.size() < total && !wereKilled ) {
            wereKilled = this.evaluateWorkload();
            TaskTideEngineUtility.waitSeconds( rand.nextInt(1, 10) );
            this.iterations++;
        }
        
        return wereKilled;
    }
    
    
    /**
     * Reset state summary of waiter workload
     */
    private void resetCounters() {
        this.active = 0;
        this.done = 0;
        this.failed = 0;
    }
    
    
    /**
     * Scan {@link ExecutorServiceTracker} for configured
     *  {@link ItemTask} workload
     * 
     */
    private void scanItems() {
    
        // Reset coutner & scan items
        this.resetCounters();
        LOGGER.info(
            "Tracker '{}' Workload size:\t'{}'",
            this.ID,
            this.TASKS.size()
        );
        for ( int i = 0; i < this.TASKS.size(); i++ ) {
            
            // Fetch data for current iter
            T task = this.TASKS.get(i);
            String taskId = task.getId();
            
            // Examine completed tasks
            if ( !this.TRACKER.isPresent(taskId) ) {
                LOGGER.info("Task not present:\t'{}'", taskId);
                continue;
            }
            
            // Handle completed task
            if ( this.TRACKER.isComplete(taskId) ) {
                
                // Transfer to do list, and fetch future
                LOGGER.info("'{}' processing completed:\t'{}'", this.TYPE.getSimpleName(), taskId);
                this.TASKS_DONE.add(task);
                Future future = this.TRACKER.get(taskId).getFuture();
                
                // Handle task event: Failed or complete
                try {
                    boolean success = (boolean) future.get();
                    if ( success ) {
                        this.done++;
                    }
                    else {
                        this.failed++;
                    }
                }
                
                // On single task WI, loose the Future but retain its alias
                //   -> Leaving processing to IT pool
                catch (InterruptedException | ExecutionException ex) {
                    LOGGER.warn(
                       "'{}' unable to fetch future for task:\t'{}'",
                       taskId,
                       this.ID
                    );
                    this.failed++;
                }
            }
            
            // Note task as active
            else {
                this.active++;
            }
        }
    }
    
    
    /**
     * Evaluate workload state killing tasks if more than half
     *  have failed. Acknowledging whether these were (true),
     *  or were not (false) cancelled
     * 
     * @return boolean
     */
    private boolean evaluateWorkload() {
        
        // Fetch current state
        LOGGER.debug(
            "Examining '{}' workload for waiter:\t'{}'",
            this.TYPE,
            this.ID
        );
        this.scanItems();
        
        // Examine results
        LOGGER.debug(
            "'{}' examining workload state for N = '{}' tasks",
            this.ID,
            this.TASKS.size()
        );
        
        // Kill if threshold is reached
        if ( this.failed >= Math.floor( this.TASKS.size() / 2.0 ) ) {
            LOGGER.warn(
                "Failed task threshold reached on waiter '{}', kill workload of size:\t'{}'",
                this.ID,
                this.TASKS.size()
            );
            this.TASKS.stream()
                .forEach( elm -> {
                    Future fut = this.TRACKER.get(elm.getId()).getFuture();
                    if ( fut != null ) {
                        fut.cancel(true);
                    }
            });
            return true;
        }
        
        // Otherwise pass
        else {
            LOGGER.info(
                "Task state within threshold on waiter '{}':\t Total = '{}', Done = '{}', Failed = '', Active = '{}'",
                this.ID,
                this.TASKS.size(),
                this.done,
                this.failed,
                this.active
            );
            return false;
        }
    }

    
    /**
     * Return count of failed
     * 
     * @return int
     */
    public int getFailed() {
        return this.failed;
    }

    
    /**
     * Return count of done
     * 
     * @return int
     */
    public int getDone() {
        return this.done;
    }

    
    /**
     * Return count of active
     * 
     * @return int
     */
    public int getActive() {
        return this.active;
    }

    
    /**
     * Fetch tasks
     * 
     * @return 
     */
    public List<T> getTasks() {
        return new ArrayList<>(this.TASKS);
    }

    
    /**
     * Fetch {@link TaskTideModel} type for waiter
     * 
     * @return Class of T
     */
    public Class<T> getType() {
        return this.TYPE;
    }

    
    /**
     * Fetch Id for waiter
     * 
     * @return 
     */
    public String getId() {
        return this.ID;
    }

    
    /**
     * Represent waiter as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "TrackerWaiter{" +
            "TYPE="     + this.TYPE +
            ", ID="     + this.ID +
            ", TASKS="  + this.TASKS +
            ", failed=" + this.failed +
            ", done="   + this.done +
            ", active=" + this.active +
        '}';
    }
}