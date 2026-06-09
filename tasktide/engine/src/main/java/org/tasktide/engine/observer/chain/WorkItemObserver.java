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

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.ObserverChain;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.WorkerObserver;

import org.tasktide.engine.observer.worker.executor.WorkItemExecutorObserver;
import org.tasktide.engine.observer.worker.timekeeper.WorkItemTimeKeeper;
import org.tasktide.engine.observer.worker.stateobserver.WorkItemStateObserver;



/**
 * Coordinates the work across the {@link WorkerObserver}s for the processing of {@link WorkItem}
 *  through the {@link TaskTideEngineObserver} chain for {@link WorkItem}s
 * 
 * @author bkenna
 */
public class WorkItemObserver extends ObserverChain<WorkItem> {

    
    /**
     * Default constructor for simple test purposes
     */
    public WorkItemObserver() {
        super(
     List.of(
                new WorkItemTimeKeeper(0),
                new WorkItemStateObserver(),
                new WorkItemExecutorObserver()
            )
        );
    }
    
    
    /**
     * Construct with required arguments
     * 
     * @param maxTime 
     */
    public WorkItemObserver(
        @ConfigProperty(name="task-tide.engine.observer.worker.timekeeper.workitem", defaultValue="100000") int maxTime
    ) {
        super(
            List.of(
                new WorkItemTimeKeeper(maxTime),
                new WorkItemStateObserver(),
                new WorkItemExecutorObserver()
            )
        );
    }
    
    
    /**
     * Construct with optional workload
     * 
     * @param workload
     * @param maxTime
     */
    public WorkItemObserver(
        List<WorkItem> workload,
        @ConfigProperty(name="task-tide.engine.observer.worker.timekeeper.workitem", defaultValue="100000") int maxTime
    ) {
        super(
            List.of(
                new WorkItemTimeKeeper(maxTime),
                new WorkItemStateObserver(workload),
                new WorkItemExecutorObserver()
            )
        );
    }

    @Override
    public List<WorkerObserver<WorkItem>> getObservers() {
        return this.observers;
    }

    @Override
    public WorkerObserver<WorkItem> getObserver(String query) {
        for ( WorkerObserver<WorkItem> obs : this.observers ) {
            if ( obs.getName().equals(query) ) {
                return obs;
            }
        }
        return null;
    }
}
