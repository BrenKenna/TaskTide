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

import java.util.List;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.workerunit.TaskTideWorkerUnit;


/**
 * Interface to decouple the {@link WorkerObserver} from their operation in chain
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @author bkenna
 */
public interface TaskTideEngineObserver<T extends TaskTideModel<T>> extends TaskTideWorkerUnit<T> {
    
    
    /**
     * Defines chained {@link WorkerObserver} actions to take on starting task
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    public boolean onTaskStart(T task);
    
    
    /**
     * Defines chained {@link WorkerObserver} actions to take on processing task
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    public boolean onTaskProcessing(T task);
    
    
    /**
     * Defines chained {@link WorkerObserver} actions to take on ending task
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    public boolean onTaskEnd(T task);
    
    
    /**
     * Get lower level observer
     * 
     * @return WorkerObserver for {@link WorkItem}/{@link ItemTask}
     */
    public List<WorkerObserver<T>> getObservers();
    
    
    /**
     * Get lower level observer by simple class name
     * 
     * @param query
     * @return 
     */
    public WorkerObserver<T> getObserver(String query);
}
