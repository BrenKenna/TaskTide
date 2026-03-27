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
package org.tasktide.engine.observer.worker;


import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.worker.TaskTideEngineUtility;
import org.tasktide.engine.observer.WorkerObserver;
import org.tasktide.engine.trackers.TaskTrackers;


/**
 * Abstract class for handling the logic of {@link WorkItem}/{@link ItemTask} processing based on states.
 * <br><br>
 * Would be useful to work towards embedding a {@link TaskTideService}, so progress logged.
 * 
 * @author bkenna
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 */
public abstract class StateObserver<T extends TaskTideModel<T>> implements WorkerObserver<T> {

    // Tracker attribute
    private final ObserverType type;
    protected final String JOB_ENV_ID;
            
    
    /**
     * Construct with {@link TaskTrackers}
     * 
     */
    public StateObserver() {
        this.type = ObserverType.CRITICAL;
        this.JOB_ENV_ID = TaskTideEngineUtility.getJobEnvId();
    }

        
    /**
     * Use {@link ObserverType} to check if StateObserver is optional
     * 
     * @return boolean
     */
    @Override
    public boolean isOptional() {
        return type.isOptional();
    }

    
    /**
     * Return {@link ObserverType}
     * 
     * @return {@link ObserverType}
     */
    @Override
    public ObserverType getType() {
        return this.type;
    }
}
