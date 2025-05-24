/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker.timekeeper;

import org.apache.logging.log4j.LogManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.engine.observer.worker.TimeKeeperObserver;


/**
 * {@link ItemTask} specific time keeper
 * 
 * @author bkenna
 */
public class ItemTaskTimeKeeper extends TimeKeeperObserver<ItemTask> {
    
    
    /**
     * Construct with max time
     * 
     * @param maxTime 
     */
    public ItemTaskTimeKeeper(
        @ConfigProperty(name = "task-tide.engine.observer.worker.timekeep.itemtask", defaultValue = "10000") long maxTime
    ) {
        super(maxTime, LogManager.getLogger(WorkItemTimeKeeper.class));
    }

    
    /**
     * Reset {@link ItemTask} based on {@link TimeKeeperObserver} flag
     * 
     * @param task
     * @param flag 
     */
    @Override
    public void handleTaskState(ItemTask task, boolean flag) {
        if (!flag) {
            task.setTaskState(TaskState.TIME_KEEPER);
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
