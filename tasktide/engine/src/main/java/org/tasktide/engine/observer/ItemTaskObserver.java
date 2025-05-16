/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.worker.StateObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;
import org.tasktide.engine.observer.worker.stateobserver.ItemTaskStateObserver;
import org.tasktide.engine.observer.worker.timekeeper.ItemTaskTimeKeeper;

import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Coordinates work across the {@link TaskTideWorkerObserver}s for the processing of {@link ItemTask}
 * 
 * @author bkenna
 */
public class ItemTaskObserver implements TaskTideWorkerObserver<ItemTask> {
    
    // Observers
    private static final Logger logger = LogManager.getLogger(ItemTaskObserver.class);
    private final StateObserver<ItemTask> stateObserver;
    private final TimeKeeperObserver<ItemTask> timeKeeperObserver;
    

    /**
     * Default constructor for simple test purposes
     */
    public ItemTaskObserver() {
        this.stateObserver = new ItemTaskStateObserver( new TaskTracker() );
        this.timeKeeperObserver = new ItemTaskTimeKeeper(100000);
    }
    
    
    /**
     * Construct with required arguments
     * 
     * @param tracker
     * @param workload
     * @param maxTime
     */
    public ItemTaskObserver(TaskTracker tracker, List<ItemTask> workload, int maxTime) {
        this.stateObserver = new ItemTaskStateObserver(tracker, workload);
        this.timeKeeperObserver = new ItemTaskTimeKeeper(maxTime);
    }

    
    /**
     * Coordinate logic across observers for whether {@link ItemTask} should be processed
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskStart(ItemTask task) {
        if ( this.stateObserver.onTaskStart(task) ) {
            if ( this.timeKeeperObserver.onTaskStart(task) ) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Coordinate logic across observers for whether {@link ItemTask} can be processed
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskProcessing(ItemTask task) {
        if ( this.stateObserver.onTaskProcessing(task) ) {
            if ( this.timeKeeperObserver.onTaskProcessing(task) ) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Coordinate wrap-up work following {@link ItemTask} processing
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskEnd(ItemTask task) {
        if ( this.stateObserver.onTaskEnd(task) ) {
            if ( this.timeKeeperObserver.onTaskEnd(task) ) {
               return true;
            }
        }
        return false;
    }
}
