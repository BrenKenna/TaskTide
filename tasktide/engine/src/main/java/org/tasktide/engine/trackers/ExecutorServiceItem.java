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
package org.tasktide.engine.trackers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Holds reference for the future {@link TaskTideModel} so that
 *  their processing can be tracked as a collection.
 * 
 * @param <T> {@link TaskTideModel} of type {@link WorkItem}/{@link ItemTask}
 * @author bkenna
 */
public class ExecutorServiceItem<T extends TaskTideModel<T>> {
    
    // Attributes
    private final TaskTideModel<T> task;
    private final Future<?> future;

    
    /**
     * Construct with task and future
     * 
     * @param task
     * @param future 
     */
    public ExecutorServiceItem(TaskTideModel<T> task, Future<?> future) {
        this.task = task;
        this.future = future;
    }
    
    
    /**
     * Get the model class
     * 
     * @return {@link TaskTideModel}
     */
    public TaskTideModel<T> getModel() {
        return this.task;
    }
    
    
    /**
     * Get the future
     * 
     * @return Future
     */
    public Future<?> getFuture() {
        return this.future;
    }
    
    
    /**
     * Wait on task processing to complete
     * 
     * @return boolean
     */
    public boolean waitOnTask() {
        try {
            this.future.get();
            return true;
        }
        catch ( InterruptedException | ExecutionException ex ) {
            return false;
        }
    }
}