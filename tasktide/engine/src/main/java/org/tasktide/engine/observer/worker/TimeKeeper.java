/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
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
    private final AtomicLong startTime = new AtomicLong(0);
    private final AtomicBoolean abort = new AtomicBoolean(false);
    protected final List<Long> executionTimes;
    protected double meanDuration;
    
    
    /**
     * Construct with max allowed time
     * 
     * @param maxTime 
     * @param logger 
     */
    public TimeKeeper(long maxTime, Logger logger) {
        this.logger = logger;
        this.maxTime = maxTime;
        executionTimes = new ArrayList<>();
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
        logger.debug("TimeKeepr evaluating run time for task '{}'", task.getId());
        long now = System.currentTimeMillis();
        
        // Initialize this observers start time
        logger.debug("Evaluating whether to initiate atomic couter");
        if ( startTime.get() == 0 ) {
            logger.info("Atomic TimeKeeper couter set on thread '{}'", Thread.currentThread().getName());
            startTime.set(now);
            return abort.getAndSet(true);
        }
        
        // Handle whether task has enough time: Breaching could unlock task
        logger.debug("Evaluating whether enough time to run task");
        if ( ( now - startTime.get() ) > maxTime ) {
            logger.warn("Warning, TimeKeeper max time of '{}' breached by '{}'", maxTime, now - startTime.get());
            return abort.getAndSet(false);
        }
        
        // Return method state
        return abort.get();
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
        boolean canNext = measureTime(now, duration);
        
        // Determine if time is breached
        if ( duration > maxTime ) {
            logger.warn("Warning task excution time '{}' breached by '{}'", maxTime, (duration-maxTime));
            return abort.getAndSet(false);
        }
        
        // Estimate if next task can run
        if ( !canNext ) {
            logger.warn("Warning not enough estimated time for next task to run");
            return abort.getAndSet(false);
        }
        
        // Otherwise log next can run
        logger.info("Enough time for next task to run");
        return abort.getAndSet(true);
    }
    
    
    /**
     * Append duration from atomic start, to now.
     * Returning whether on average there's enough time for the next task to run
     * 
     * @param now
     * @param duration
     * @return boolean
     */
    public boolean measureTime(long now, long duration) {
        
        // Append
        executionTimes.add(duration);
            
        // Compute average time
        meanDuration = executionTimes.stream()
            .mapToDouble(Long::doubleValue)
            .average()
        .orElse(0.0);
        
        // Return if time may exceed
        return ( meanDuration > 0 ) && (maxTime - duration) < meanDuration;
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
     * Abstract method to let concrete time keeper handle logic around
     *  managing task states. Given a boolean flag to aid this.
     * 
     * @param task - of {@link WorkItem}, {@link ItemTask}
     * @param flag
     */
    public abstract void handleTaskState(T task, boolean flag);
}