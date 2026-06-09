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
package org.tasktide.engine.observer.worker;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.observer.ObserverResult;
import org.tasktide.engine.workerunit.TaskTideWorkerUnit;

import org.tasktide.engine.observer.WorkerObserver;


/**
 *
 * Time keeper observer for {@link TaskTideWorkerUnit}
 *  processing of {@linkt WorkItem}, and {@link ItemTask}
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @author bkenna
 */
public abstract class TimeKeeperObserver<T extends TaskTideModel<T>> implements WorkerObserver<T>{
    
    // Attributes
    protected final Logger logger; // Lets lower classes define
    private final long maxTime;
    protected final AtomicLong startTime = new AtomicLong(0);
    protected final AtomicBoolean abort = new AtomicBoolean(true);
    protected final List<Long> executionTimes;
    private long meanDuration;
    private final ObserverType type;
    
    
    /**
     * Construct with max allowed time
     * 
     * @param maxTime 
     * @param logger 
     */
    public TimeKeeperObserver(long maxTime, Logger logger) {
        this.logger = logger;
        this.maxTime = maxTime;
        this.executionTimes = new ArrayList<>();
        this.type = ObserverType.OPTIONAL;
    }

    
    /**
     * Use {@link ObserverType} to check if TimeKeeper is optional.
     *  If not optional there is a guard against the max time LTE 1
     * 
     * @return boolean
     */
    @Override
    public boolean isOptional() {
        if ( !type.isOptional() ) {
            if ( this.maxTime <= 1 ) {
                return true;
            }
        }
        return true;
    }

    
    /**
     * Return {@link ObserverType}
     * 
     * @return {@link ObserverType}
     */
    @Override
    public ObserverType getType() {
        return this.type;
    }
    

    /**
     * Evaluate whether task can run before execution
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskStart(T task) {
        
        // Bypass optional observer
        if ( this.isOptional() ) {
            logger.debug("Skipping optional TimeKeeper evaluation");
            return ObserverResult.success();
        }
        
        // Current time in milliseconds
        long start = startTime.get();
        long now = System.currentTimeMillis();
        long elapsed = now - start;
        
        // Evaluate starting task
        boolean eval = this.evaluateStart(task, now, start, elapsed);
        logger.info("TimeKeeper evaluated time left for processing as '{}' for ItemTask:\t'{}'", eval, task.getId());
        
        // Return result
        if ( eval ) {
            return ObserverResult.success();
        }
        else {
            return ObserverResult.failure(this, this.isOptional());
        }
    }
    
    
    /**
     * Evaluate whether the next task can run
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskEnd(T task) {
        
        // Bypass optional observer
        if ( this.isOptional() ) {
            logger.debug("Skipping optional TimeKeeper evaluation");
            return ObserverResult.success();
        }
    
        // Measure processing time
        logger.info("Measuring the elapsed time of task '{}',", task.getId());
        long now = System.currentTimeMillis();
        long duration = now - startTime.get();
        
        // Evaluate duration
        logger.info("Task '{}' completed in {}ms", task.getId(), duration);
        boolean eval = this.evaluateDuration(now, duration);
        updateAbortFlag(eval);
        logger.info("TimeKeeper evaluated time left next '{}' with task:\t'{}'", eval, task.getId());
        
        // Return result
        if ( eval ) {
            return ObserverResult.success();
        }
        else {
            return ObserverResult.failure(this, this.isOptional());
        }
    }
    
    
    /**
     * Leaving out for now
     * 
     * @param task
     * @return boolean
     */
    @Override
    public ObserverResult onTaskProcessing(T task) {
        return ObserverResult.success();
    }
    
    
    /**
     * Let sub-classes handle logic
     * 
     * @param task - of {@link WorkItem}, {@link ItemTask}
     * @param flag 
     */
    public abstract void handleTaskState(T task, boolean flag);
    
    
    /**
     * Append duration from atomic start, to now.
     * Returning whether on average there's enough time for the next task to run
     * 
     * @param now
     * @param duration
     * @return boolean
     */
    private boolean measureTime(long now, long duration) {
        
        // Append
        executionTimes.add(duration);
            
        // Compute average time
        meanDuration = (long) executionTimes.stream()
            .mapToDouble(Long::doubleValue)
            .average()
        .orElse(0.0);
        
        // Return if time may exceed
        long expectedNext = meanDuration + now;
        long maxEnd = startTime.get() + maxTime;
        logger.debug("Mean duration = '{}ms', Expected Next = '{}ms', Max End '{}ms', Is time = '{}'",
         meanDuration, expectedNext, maxEnd, (expectedNext < maxEnd)
        );
        return ( meanDuration > 0 ) && ( expectedNext < maxEnd );
    }
    
    
    /**
     * Update the abort flag
     * 
     * @param flag 
     */
    private void updateAbortFlag(boolean flag) {
        abort.set(flag);
    } 
    
    
    /**
     * Evaluate starting task {@link WorkItem} or {@link ItemTask}
     * 
     * @param task
     * @param now
     * @param start
     * @param elapsed
     * @return boolean
     */
    private boolean evaluateStart(T task, long now, long start, long elapsed) {
    
        // Bypass optional observer
        if ( this.isOptional() ) {
            logger.info("TimeKeeper configured as optional, not skipping proceeding tasks");
            return true;
        }
        
        // Initialize this observers start time
        logger.info("TimeKeeper evaluating starting of task '{}'", task.getId());
        if ( start == 0 ) {
            logger.info("Atomic TimeKeeper for task '{}' set on thread '{}'",
               task.getId(), Thread.currentThread().getName()
            );
            startTime.set(now);
            return true;
        }
        
        // Evaluating whether previous task flagged an abort
        if ( !abort.get() ) {
            logger.warn("TimeKeeper detected previous task flagged an abort, determining should skip task '{}'", task.getId());
            this.handleTaskState(task, false);
            return false;
        }
        else {
            updateAbortFlag(true);
        }
        
        // Handle whether task has enough time: Breaching could unlock task
        logger.info("Evaluating whether enough time to run task '{}'", task.getId());
        startTime.set(now);
        logger.debug("Now = '{}ms', Start Time = '{}ms', Elapsed Time = '{}ms'", now, startTime.get(), elapsed);
        logger.debug("Evaluating elapsed time '{}ms' > '{}'", elapsed, maxTime);
        if ( elapsed > maxTime ) {
            logger.warn("Warning, TimeKeeper max time of '{}ms' breached by '{}'ms", maxTime, elapsed - maxTime);
            return false;
        }
        
        // Otherwise no action needed & all is good
        logger.info("TimeKeeper evaluated sufficient to run task '{}'", task.getId());
        return true;
    }
    
    
    /**
     * Evaluate task duration
     * 
     * @param now
     * @param duration
     * @return boolean
     */
    private boolean evaluateDuration(long now, long duration) {
        
        // Determine if time is breached
        if ( duration > maxTime ) {
            logger.warn("Warning task excution time '{}ms' breached by '{}ms'", maxTime, (duration-maxTime));
            this.updateAbortFlag(false);
            return false;
        }
        
        // Estimate if next task can run
        boolean canNext = measureTime(now, duration);
        if ( !canNext ) {
            logger.warn("Warning not enough estimated time for next task to run");
            this.updateAbortFlag(false);
            return false;
        }
        
        // Otherwise log next can run
        logger.info("TimeKeeper evaluated enough time left for next task");
        updateAbortFlag(true);
        return true;
    }
}