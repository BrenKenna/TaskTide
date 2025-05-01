/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;


/**
 * {@link WorkItem} specific {@link TimeKeeper}
 * 
 * @author bkenna
 */
public class WorkItemTimeKeeper extends TimeKeeper<WorkItem> {
    
    /**
     * Construct with max time
     * 
     * @param maxTime 
     */
    public WorkItemTimeKeeper(long maxTime) {
        super(maxTime, LogManager.getLogger(WorkItemTimeKeeper.class));
    }

    
    /**
     * Reset {@link WorkItem} based on {@link TimeKeeper} flag
     * 
     * @param task
     * @param flag 
     */
    @Override
    public void handleTaskState(WorkItem task, boolean flag) {
        task.setItemState(ItemState.TODO);
    }
}
