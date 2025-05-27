/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.worker.executor.builders;

import org.tasktide.engine.observer.builders.ItemTaskObserverBuilder;
import java.util.List;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.executor.ItemTaskExecutor;

import org.tasktide.engine.worker.tasktracker.TaskTracker;


/**
 * Class to hold the logic for constructing {@link TaskTideExecutor} for {@link ItemTask}
 * 
 * @author bkenna
 */
public class ItemTaskExecutorBuilder {
    
    // Attributes
    private TaskTideEngineObserver<ItemTask> itemTaskObserver;
    
    
    /**
     * Build with {@link ItemTaskObserver} fields
     * 
     * @param workload
     * @param taskTracker
     * @param maxTime
     * @return {@link ItemTaskExecutorBuilder} for {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public ItemTaskExecutorBuilder withObserver(List<ItemTask> workload, TaskTracker taskTracker, int maxTime) {
    
        // Set required vars
        ItemTaskObserverBuilder obsBuilder;
        
        // Build observer
        obsBuilder = new ItemTaskObserverBuilder();
        this.itemTaskObserver = obsBuilder
            .withWorkload(workload)
            .withTaskTracker(taskTracker)
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build {@link ItemTaskExecutor}
     * 
     * @return {@link TaskTideExecutor} of {@link ItemTask}
     */
    public TaskTideExecutor<ItemTask> build() {
        return new ItemTaskExecutor((ItemTaskObserver) this.itemTaskObserver);
    }
}
