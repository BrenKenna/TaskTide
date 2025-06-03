/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.engine.wokerunitprovider;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.executor.ItemTaskExecutor;



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
     * @param maxTime
     * @return {@link ItemTaskExecutorBuilder} for {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public ItemTaskExecutorBuilder withObserver(List<ItemTask> workload, int maxTime) {
    
        // Set required vars
        ItemTaskObserverBuilder obsBuilder;
        
        // Build observer
        obsBuilder = new ItemTaskObserverBuilder();
        this.itemTaskObserver = obsBuilder
            .withWorkload(workload)
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build observer without workload
     * 
     * @param maxTime
     * @return {@link ItemTaskExecutorBuilder} for {@link TaskTideEngineObserver} of {@link ItemTask} 
     */
    public ItemTaskExecutorBuilder withObserver(int maxTime) {
    
        // Set required vars
        ItemTaskObserverBuilder obsBuilder;
        
        // Build observer
        obsBuilder = new ItemTaskObserverBuilder();
        this.itemTaskObserver = obsBuilder
            .withMaxTime(maxTime)
        .build();
        
        // Return builder
        return this;
    }
    
    
    /**
     * Build with provided {@link TaskTideEngineObserver} of {@link ItemTask}
     * 
     * @param observer
     * @return {@link ItemTaskExecutorBuilder} for {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public ItemTaskExecutorBuilder withObserver(TaskTideEngineObserver<ItemTask> observer) {
        this.itemTaskObserver = observer;
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
