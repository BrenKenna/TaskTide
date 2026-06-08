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

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.executor.TaskTideExecutor;


/**
 * Building {@link WorkItemTraverser}
 * 
 * @author Bren
 */
public class WorkItemTraverserBuilder {
 
    // Attributes
    private TaskTideExecutor<ItemTask> itemTaskExecutor;
    private TaskTideEngineObserver<WorkItem> workItemObserver;

    
    /**
     * Build with provided {@link TaskTideEngineObserver}
     * 
     * @param workItemObserver
     * @return {@link WorkItemTraverserBuilder}
     */
    public WorkItemTraverserBuilder
        withObserver(TaskTideEngineObserver<WorkItem> workItemObserver)
    {
        this.workItemObserver = workItemObserver;
        return this;
    }
        
        
    /**
     * Build with provided {@link TaskTideExecutor}
     * 
     * @param itemTaskExecutor
     * @return {@link WorkItemTraverserBuilder}
     */
    public WorkItemTraverserBuilder 
        withExecutor(TaskTideExecutor<ItemTask> itemTaskExecutor)
    {
        this.itemTaskExecutor = itemTaskExecutor;
        return this;
    }
        
    
    /**
     * Build configured {@link WorkItemTraverser}
     * 
     * @return {@link TaskTideWorkloadTraverser}
     */
    public TaskTideWorkloadTraverser<WorkItem> build() {
        
        // Default construction
        if ( 
            this.workItemObserver == null &&
            this.itemTaskExecutor == null
        ) {
            return new WorkItemTraverser();
        }
        
        // With all arguments
        if ( 
            this.workItemObserver != null &&
            this.itemTaskExecutor != null
        ) {
            return new WorkItemTraverser(this.workItemObserver, this.itemTaskExecutor); 
        }
        
        // With observer and traverser
        else if (
            this.workItemObserver != null
        ) {
            return new WorkItemTraverser(this.workItemObserver); 
        }
        
        // With observer
        else {
            return new WorkItemTraverser(this.workItemObserver); 
        }
    }
}