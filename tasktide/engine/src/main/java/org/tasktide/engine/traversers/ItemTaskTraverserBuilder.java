/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.traversers;

import org.tasktide.engine.processingstrategy.ItemTaskTraverser;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.executor.TaskTideExecutor;


/**
 * Building {@link ItemTaskTraverser}
 *
 * @author Bren
 */
public class ItemTaskTraverserBuilder {
 
    // Attributes
    private TaskTideExecutor<ItemTask> itemTaskExecutor;
    private TaskTideEngineObserver<ItemTask> itemTaskObserver;

    
    /**
     * Build with provided {@link TaskTideEngineObserver}
     * 
     * @param itemTaskObserver
     * @return {@link ItemTaskTraverserBuilder}
     */
    public ItemTaskTraverserBuilder
        withObserver(TaskTideEngineObserver<ItemTask> itemTaskObserver)
    {
        this.itemTaskObserver = itemTaskObserver;
        return this;
    }
        
        
    /**
     * Build with provided {@link TaskTideExecutor}
     * 
     * @param itemTaskExecutor
     * @return {@link ItemTaskTraverserBuilder}
     */
    public ItemTaskTraverserBuilder 
        withExecutor(TaskTideExecutor<ItemTask> itemTaskExecutor)
    {
        this.itemTaskExecutor = itemTaskExecutor;
        return this;
    }
        
    
    /**
     * Build configured {@link TaskTideWorkloadTraverser}
     * 
     * @return {@link TaskTideWorkloadTraverser}
     */
    public TaskTideWorkloadTraverser<ItemTask> build() {
        if ( 
            this.itemTaskObserver == null &&
            this.itemTaskExecutor == null
        ) {
            return new ItemTaskTraverser();
        }

        if ( 
            this.itemTaskObserver != null &&
            this.itemTaskExecutor != null
        ) {
            return new ItemTaskTraverser(this.itemTaskObserver, this.itemTaskExecutor);
        }
        
        else {
            return new ItemTaskTraverser(this.itemTaskObserver); 
        }
    }
}