/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.engine.workerunit.provider;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;

import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.executor.ItemTaskExecutor;



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
