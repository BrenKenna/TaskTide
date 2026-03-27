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
package org.tasktide.engine.wokerunit.provider;

import java.util.List;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.ItemTaskObserver;
import org.tasktide.engine.observer.worker.TimeKeeperObserver;


/**
 * Class to the logic for constructing {@link TaskTideEngineObserver} of {@link ItemTaskObserver}
 * 
 * @author bkenna
 */
public class ItemTaskObserverBuilder {
    
    // Attributes
    private List<ItemTask> workload;
    private int maxTime;
    
    
    /**
     * Optional to build with {@link ItemTask} workload
     * 
     * @param workload
     * @return {@link ItemTaskObserverBuilder}
     */
    public ItemTaskObserverBuilder withWorkload(List<ItemTask> workload) {
        this.workload = workload;
        return this;
    }
    
    
    /**
     * Build with {@link TimeKeeperObserver} max time
     * 
     * @param maxTime
     * @return {@link ItemTaskObserverBuilder}
     */
    public ItemTaskObserverBuilder withMaxTime(int maxTime) {
        this.maxTime = maxTime;
        return this;
    }
    
    
    /**
     * Build {@link ItemTask} {@link TaskTideEngineObserver}
     * 
     * @return {@link TaskTideEngineObserver} of {@link ItemTask}
     */
    public TaskTideEngineObserver<ItemTask> build() {
        if ( this.workload == null ) {
            return new ItemTaskObserver(this.maxTime);
        }
        else {
            return new ItemTaskObserver(this.workload, this.maxTime);
        }
    }
}
