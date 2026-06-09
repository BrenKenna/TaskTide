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
package org.tasktide.engine.observer.chain;

import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.ObserverChain;
import org.tasktide.engine.observer.WorkerObserver;

import org.tasktide.engine.observer.worker.timekeeper.ItemTaskTimeKeeper;
import org.tasktide.engine.observer.worker.stateobserver.ItemTaskStateObserver;

import org.tasktide.engine.observer.TaskTideEngineObserver;


/**
 * Coordinates the work across the {@link WorkerObserver}s for the processing of {@link WorkItem}
 *  through the {@link TaskTideEngineObserver} chain for {@link ItemTask}s
 * 
 * @author bkenna
 */
public class ItemTaskObserver extends ObserverChain<ItemTask> {

    
    /**
     * Default constructor for simple test purposes
     */
    public ItemTaskObserver() {
        super(
            List.of(
                new ItemTaskTimeKeeper(100000),
                new ItemTaskStateObserver()
            )
        );
    }
    
    
    /**
     * Construct with required arguments
     * 
     * @param maxTime
     */
    public ItemTaskObserver(
        @ConfigProperty(name="task-tide.engine.observer.worker.timekeeper.itemtask", defaultValue="100000") int maxTime
    ) {
        super(
            List.of(
                new ItemTaskTimeKeeper(maxTime),
                new ItemTaskStateObserver()
            )
        );
    }
    
    
    /**
     * Construct with required arguments
     * 
     * @param workload
     * @param maxTime
     */
    public ItemTaskObserver(
        List<ItemTask> workload,
        @ConfigProperty(name="task-tide.engine.observer.worker.timekeeper.itemtask", defaultValue="100000") int maxTime
    ) {
        super(
            List.of(
                new ItemTaskTimeKeeper(maxTime),
                new ItemTaskStateObserver(workload)
            )
        );
    }
    
    
    @Override
    public List<WorkerObserver<ItemTask>> getObservers() {
        return this.observers;
    }
    
    
    @Override
    public WorkerObserver<ItemTask> getObserver(String query) {
        for ( WorkerObserver<ItemTask> obs : this.observers ) {
            if ( obs.getName().equals(query) ) {
                return obs;
            }
        }
        return null;
    }
}
