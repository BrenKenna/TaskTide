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
package org.tasktide.engine.wokerunit.provider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.deprecated_processor.TaskTideProcessor;
import org.tasktide.engine.deprecated_processor.ItemTaskProcessor;


/**
 * Class to hold the logic for constructing {@link TaskTideProcessor} for {@link ItemTask}
 * 
 * @author bkenna
 */
public class ItemTaskProcessorBuilder {
    
    // Attributes
    private ExecutorService executorService;
    private TaskTideExecutor<ItemTask> executor;
    
    
    /**
     * Build with provided {@link ExecutorService}
     * 
     * @param executorService
     * @return {@link ItemTaskProcessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
    
    
    /**
     * Build with new {@link ExecutorService}
     * 
     * @param nThreads
     * @return {@link ItemTaskProcessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withExecutorService(int nThreads) {
        this.executorService = Executors.newFixedThreadPool(nThreads);
        return this;
    }
    
    
    /**
     * Build with provided {@link TaskTideExecutor} of {@link ItemTask}
     * 
     * @param executor
     * @return {@link ItemTaskProcessorBuilder} for {@link TaskTideProcessor} of {@link ItemTask}
     */
    public ItemTaskProcessorBuilder withSubExecutor(TaskTideExecutor<ItemTask> executor) {
        this.executor = executor;
        return this;
    }
    
    
    /**
     * Build {@link TaskTideProcessor} for {@link ItemTask}
     * 
     * @return {@link TaskTideProcessor} of {@link ItemTask}
     */
    public TaskTideProcessor<ItemTask> build() {
        return new ItemTaskProcessor(
            this.executorService,
            this.executor
        );
    }
}