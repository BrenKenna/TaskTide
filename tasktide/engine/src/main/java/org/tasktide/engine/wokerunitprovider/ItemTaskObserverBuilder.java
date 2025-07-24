/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.wokerunitprovider;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;


/**
 * Class to the logic for constructing {@link TaskTideEngineObserver} of {@link ItemTaskObserver}
 * 
 * @author bkenna
 */
public class ItemTaskObserverBuilder {
    
    // Attributes
    private List<ItemTask> workload;
    private int maxTime;
    
    
    /**
     * Optional to build with {@link ItemTask} workload
     * 
     * @param workload
     * @return {@link ItemTaskObserverBuilder}
     */
    public ItemTaskObserverBuilder withWorkload(List<ItemTask> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Build with {@link TimeKeeperObserver} max time
     * 
     * @param maxTime
     * @return {@link ItemTaskObserverBuilder}
     */
    public ItemTaskObserverBuilder withMaxTime(int maxTime) {
        this.maxTime = maxTime;
        return this;
    }
    
    
    /**
     * Build {@link ItemTask} {@link TaskTideEngineObserver}
     * 
     * @return {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public TaskTideEngineObserver<ItemTask> build() {
        if ( this.workload == null ) {
            return new ItemTaskObserver(this.maxTime);
        }
        else {
            return new ItemTaskObserver(this.workload, this.maxTime);
        }
    }
}
