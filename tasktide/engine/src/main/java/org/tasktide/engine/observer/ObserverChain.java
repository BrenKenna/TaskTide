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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Holds each {@link WorkerObserver} in a list, with onStart, onProcessing, and onEnd
 *  methods operating iteratively where the failure of one breaks exeuction chain. To 
 *  context failure methods a {@link ObserverResult} are returned.
 * 
 * @param <T> of {@link TaskTideModel}-{@link WorkItem},{@link ItemTask}
 * @author bkenna
 */
public abstract class ObserverChain<T extends TaskTideModel<T>> implements TaskTideEngineObserver<T>{

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineObserver.class);
    protected final List<WorkerObserver<T>> observers;
    
    
    /**
     * Construct with {@link WorkerObserver} list
     * 
     * @param observers 
     */
    public ObserverChain(List<WorkerObserver<T>> observers) {
        this.observers = observers;
    }
    
    
    /**
     * Run the onTaskStart method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public boolean onTaskStart(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            ObserverResult result = obs.onTaskStart(task);
            LOGGER.info("Evaluating Observer '{}' with result '{}'", obs.getName(), task.getId());
            if ( !result.isSuccess() && !result.getType().isOptional() ) {
                if ( !result.canIgnore() ) {
                    LOGGER.warn("Task '{}' failed onTaskStart Observation '{}' check", task.getId(), obs.getName());
                    return false;
                }
                LOGGER.info("Can ignore Observer '{}' check on task '{}'", obs.getName(), task.getId());
            }
        }
        return true;
    }


    /**
     * Run the onTaskProcessing method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public boolean onTaskProcessing(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            ObserverResult result = obs.onTaskProcessing(task);
            LOGGER.info("Evaluating Observer '{}' with result '{}'", obs.getName(), task.getId());
            if ( !result.isSuccess() && !result.getType().isOptional() ) {
                if ( !result.canIgnore() ) {
                    LOGGER.warn("Task '{}' failed onTaskProcessing Observation '{}' check", task.getId(), obs.getName());
                    return false;
                }
                LOGGER.info("Can ignore Observer '{}' check on task '{}'", obs.getName(), task.getId());
            }
        }
        return true;
    }


    /**
     * Run the onTaskEnd method from each {@link WorkerObserver}.
     *  Returning {@link ObserverResult} flagging failed observer, or successful run.
     * 
     * @param task
     * @return {@link ObserverResult}
     */
    @Override
    public boolean onTaskEnd(T task) {
        for ( WorkerObserver<T> obs : this.observers ) {
            ObserverResult result = obs.onTaskEnd(task);
            if ( !result.isSuccess() && !result.getType().isOptional() ) {
                if ( !result.canIgnore() ) {
                    LOGGER.warn("Task '{}' failed onTaskEnd Observation '{}' check", task.getId(), obs.getName());
                    return false;
                }
                LOGGER.info("Can ignore Observer result '{}' check on task '{}'", obs.getName(), task.getId());
            }
        }
        return true;
    }
}