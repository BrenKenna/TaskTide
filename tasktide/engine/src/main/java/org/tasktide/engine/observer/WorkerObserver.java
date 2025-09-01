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
package org.tasktide.engine.observer;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.observer.worker.ObserverType;

import org.tasktide.engine.worker.TaskTideWorkerUnit;


/**
 * Observer interface for observing {@link WorkItem}, and {@link ItemTask} execution
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem}, {@link ItemTask}
 * @author bkenna
 */
public interface WorkerObserver<T extends TaskTideModel<T>> extends TaskTideWorkerUnit<T> {
    
    
    /**
     * Defines action to take on starting task execution
     * 
     * @param task
     * @return public boolean
     */
    public ObserverResult onTaskStart(T task);
    
    
    /**
     * Defines actions to take during processing
     * 
     * @param task
     * @return public boolean
     */
    public ObserverResult onTaskProcessing(T task);
    
    
    /**
     * Defines action to take when task completes
     * 
     * @param task
     * @return public boolean
     */
    public ObserverResult onTaskEnd(T task);
    
    
    /**
     * Defines whether implementing observer is optional
     * 
     * @return public boolean
     */
    public boolean isOptional();
    
    
    /**
     * Defines {@link ObserverType} of implementing Observer
     * 
     * @return {@link ObserverType}
     */
    public ObserverType getType();
    
    
    /**
     * Get observer name
     * 
     * @return String
     */
    public String getName();
}
