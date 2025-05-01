/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;

import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;


/**
 * {@link ItemTask} specific time keeper
 * 
 * @author bkenna
 */
public class ItemTaskTimeKeeper extends TimeKeeper<ItemTask> {
    
    
    /**
     * Construct with max time
     * 
     * @param maxTime 
     */
    public ItemTaskTimeKeeper(long maxTime) {
        super(maxTime, LogManager.getLogger(WorkItemTimeKeeper.class));
    }

    
    /**
     * Reset {@link ItemTask} based on {@link TimeKeeper} flag
     * 
     * @param task
     * @param flag 
     */
    @Override
    public void handleTaskState(ItemTask task, boolean flag) {
        if (!flag) {
            task.setTaskState(TaskState.PENDING);
        }
    }
}
