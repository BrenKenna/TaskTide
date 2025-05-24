/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.timekeeper;

import org.apache.logging.log4j.LogManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.worker.TimeKeeperObserver;


/**
 * {@link WorkItem} specific {@link TimeKeeperObserver}
 * 
 * @author bkenna
 */
public class WorkItemTimeKeeper extends TimeKeeperObserver<WorkItem> {
    
    
    /**
     * Construct with max time
     * 
     * @param maxTime 
     */
    public WorkItemTimeKeeper(
        @ConfigProperty(name = "task-tide.engine.observer.worker.timekeep.workitem", defaultValue = "10000") long maxTime
    ) {
        super(maxTime, LogManager.getLogger(WorkItemTimeKeeper.class));
    }

    
    /**
     * Reset {@link WorkItem} based on {@link TimeKeeperObserver} flag
     * 
     * @param task
     * @param flag 
    */
    @Override
    public void handleTaskState(WorkItem task, boolean flag) {
        if (!flag) {
            task.setItemState(ItemState.FOR_UNLOCK);
        }
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
