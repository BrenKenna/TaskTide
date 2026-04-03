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
package org.tasktide.engine.workerunit.provider;

import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;


/**
 * Class to the logic for build {@link TaskTideEngineObserver} of {@link WorkItemObserver}
 * 
 * @author bkenna
 */
public class WorkItemObserverBuilder {
    
    // Attributes
    private List<WorkItem> workload;
    private int maxTime;
    
    
    /**
     * Optional to build with {@link WorkItem} workload
     * 
     * @param workload
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder withWorkload(List<WorkItem> workload) {
        this.workload = workload;
        return this;
    }

    
    /**
     * Build with {@link TimeKeeperObserver} max time
     * 
     * @param maxTime
     * @return {@link WorkItemObserverBuilder}
     */
    public WorkItemObserverBuilder withMaxTime(int maxTime) {
        this.maxTime = maxTime;
        return this;
    }
    
    
    /**
     * Build {@link WorkItem} {@link TaskTideEngineObserver}
     * 
     * @return {@link TaskTideEngineObserver} of {@link WorkItem}
     */
    public TaskTideEngineObserver<WorkItem> build() {
        if ( this.workload == null ) {
            return new WorkItemObserver(this.maxTime);
        }
        else {
            return new WorkItemObserver(this.workload, this.maxTime);
        }
    }
}