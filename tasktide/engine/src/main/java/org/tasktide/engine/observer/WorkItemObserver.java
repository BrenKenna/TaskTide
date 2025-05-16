/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.worker.ExecutorObserver;
import org.tasktide.engine.observer.worker.StateObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;

import org.tasktide.engine.observer.worker.executor.WorkItemExecutorObserver;
import org.tasktide.engine.observer.worker.stateobserver.WorkItemStateObserver;
import org.tasktide.engine.observer.worker.timekeeper.WorkItemTimeKeeper;

import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Coordinates work across the {@link TaskTideWorkerObserver}s for the processing of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemObserver implements TaskTideWorkerObserver<WorkItem> {
    
    // Observers
    private static final Logger logger = LogManager.getLogger(WorkItemObserver.class);
    private final ExecutorObserver<WorkItem, ItemTask> executorObserver;
    private final StateObserver<WorkItem> stateObserver;
    private final TimeKeeperObserver<WorkItem> timeKeeperObserver;

    
    /**
     * Default constructor for simple test purposes
     */
    public WorkItemObserver() {
        this.stateObserver = new WorkItemStateObserver( new TaskTracker() );
        this.executorObserver = new WorkItemExecutorObserver();
        this.timeKeeperObserver = new WorkItemTimeKeeper(100000);
    }
    
    
    /**
     * Construct with required arguments
     * 
     * @param tracker
     * @param workload
     * @param maxTime
     */
    public WorkItemObserver(TaskTracker tracker, List<WorkItem> workload, int maxTime) {
        this.stateObserver = new WorkItemStateObserver(tracker, workload);
        this.executorObserver = new WorkItemExecutorObserver();
        this.timeKeeperObserver = new WorkItemTimeKeeper(maxTime);
    }
    
    
    /**
     * Coordinate logic across observers for whether {@link WorkItem} should be processed
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskStart(WorkItem task) {
        if ( this.stateObserver.onTaskStart(task) ) {
            if ( this.executorObserver.onTaskStart(task) ) {
                if ( this.timeKeeperObserver.onTaskStart(task) ) {
                    return true;
                }
            }
        }
        return false;
    }

    
    /**
     * Coordinate logic across observers for whether {@link WorkItem} can be processed
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskProcessing(WorkItem task) {
        if ( this.stateObserver.onTaskProcessing(task) ) {
            if ( this.executorObserver.onTaskProcessing(task) ) {
                if ( this.timeKeeperObserver.onTaskProcessing(task) ) {
                    return true;
                }
            }
        }
        return false;
    }

    
    /**
     * Coordinate wrap-up work following {@link WorkItem} processing
     * 
     * @param task
     * @return boolean
     */
    @Override
    public boolean onTaskEnd(WorkItem task) {
        if ( this.stateObserver.onTaskEnd(task) ) {
            if ( this.executorObserver.onTaskEnd(task) ) {
                if ( this.timeKeeperObserver.onTaskEnd(task) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
