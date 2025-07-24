/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.observer.worker;


import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.WorkerObserver;
import org.tasktide.engine.trackers.TaskTrackers;


/**
 * Abstract class for handling the logic of {@link WorkItem}/{@link ItemTask} processing based on states.
 * <br><br>
 * Would be useful to work towards embedding a {@link TaskTideService}, so progress logged.
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public abstract class StateObserver<T extends TaskTideModel<T>> implements WorkerObserver<T> {

    // Tracker attribute
    private final ObserverType type;
            
    
    /**
     * Construct with {@link TaskTrackers}
     * 
     */
    public StateObserver() {
        this.type = ObserverType.CRITICAL;
    }

        
    /**
     * Use {@link ObserverType} to check if StateObserver is optional
     * 
     * @return boolean
     */
    @Override
    public boolean isOptional() {
        return type.isOptional();
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
}
