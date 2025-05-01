/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideWorkerObserver;


/**
 *
 * Time keeper observer for {@link TaskTideWorker}
 *  processing of {@linkt WorkItem}, and {@link ItemTask}
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @author bkenna
 */
public abstract class TimeKeeper<T extends TaskTideModel<T>> implements TaskTideWorkerObserver<T> {
    
    // Attributes
    protected final Logger logger; // Lets lower classes define
    private final long maxTime;
    protected final AtomicLong startTime = new AtomicLong(0);
    protected final AtomicBoolean abort = new AtomicBoolean(true);
    protected final List<Long> executionTimes;
    private long meanDuration;
    
    
    /**
     * Construct with max allowed time
     * 
     * @param maxTime 
     * @param logger 
     */
    public TimeKeeper(long maxTime, Logger logger) {
        this.logger = logger;
        this.maxTime = maxTime;
        this.executionTimes = new ArrayList<>();
    }
    
    
    /**
     * Evaluate whether task can run before execution
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskStart(T task) {
        
        // Current time in milliseconds
        long start = startTime.get();
        long now = System.currentTimeMillis();
        long elapsed = now - start;
        
        // Evaluate starting task
        boolean eval = evaluateStart(task, now, start, elapsed);
        return eval;
    }
    
    
    /**
     * Evaluate whether the next task can run
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskEnd(T task) {
    
        // Measure processing time
        logger.info("Measuring the elapsed time of task '{}',", task.getId());
        long now = System.currentTimeMillis();
        long duration = now - startTime.get();
        
        // Evaluate duration
        logger.info("Task '{}' completed in {}ms", task.getId(), duration);
        boolean eval = evaluateDuration(task, now, duration);
        updateAbortFlag(eval);
        return eval;
    }
    
    
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
            logger.warn("TimeKeeper detected previous task flagged an abort, should skip task '{}'", task.getId());
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
        logger.info("TimeKeeper evaluated time for successful run of task '{}'", task.getId());
        return true;
    }
    
    
    /**
     * Evaluate task duration
     * 
     * @param task
     * @param now
     * @param duration
     * @return boolean
     */
    private boolean evaluateDuration(T task, long now, long duration) {
        
        // Determine if time is breached
        if ( duration > maxTime ) {
            logger.warn("Warning task excution time '{}ms' breached by '{}ms'", maxTime, (duration-maxTime));
            updateAbortFlag(false);
            return false;
        }
        
        // Estimate if next task can run
        boolean canNext = measureTime(now, duration);
        if ( !canNext ) {
            logger.warn("Warning not enough estimated time for next task to run");
            updateAbortFlag(false);
            return false;
        }
        
        // Otherwise log next can run
        logger.info("TimeKeeper evaluated enough time left for next task");
        updateAbortFlag(true);
        return true;
    }
    
    
    /**
     * 
     * @param subList
     * @return 
     */
    @Override
    public boolean onSubTasking(List<T> subList) {
        return true;
    }
    
    
    /**
     * Let sub-classes handle logic
     * 
     * @param task - of {@link WorkItem}, {@link ItemTask}
     * @param flag 
     */
    public abstract void handleTaskState(T task, boolean flag);
}