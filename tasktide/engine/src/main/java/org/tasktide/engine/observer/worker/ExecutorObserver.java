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

import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.engine.observer.WorkerObserver;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Observer for the full processing life-cycle of an executor
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @param <U> subunit {@alink ItemTask} for now
 * @author bkenna
 */
public abstract class ExecutorObserver<T extends TaskTideModel<T>, U extends TaskTideModel<U>> implements WorkerObserver<T> {

    // Attributes
    protected final Logger logger;
    private final ObserverType type;

    
    /**
     * Construct embedding logger
     * 
     * @param logger
     */
    public ExecutorObserver(Logger logger) {
        this.logger = logger;
        this.type = ObserverType.CRITICAL;
    }
    
    
    /**
     * Use {@link ObserverType} to check if ExecutorObserver is optional
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
