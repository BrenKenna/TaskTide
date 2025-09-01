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
package org.tasktide.engine.observer.worker.timekeeper;

import org.apache.logging.log4j.LogManager;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;

import org.tasktide.engine.observer.worker.TimeKeeperObserver;


/**
 * {@link ItemTask} specific time keeper
 * 
 * @author bkenna
 */
public class ItemTaskTimeKeeper extends TimeKeeperObserver<ItemTask> {
    
    
    /**
     * Construct with max time
     * 
     * @param maxTime 
     */
    public ItemTaskTimeKeeper(
        @ConfigProperty(name = "task-tide.engine.observer.worker.timekeep.itemtask", defaultValue = "10000") long maxTime
    ) {
        super(maxTime, LogManager.getLogger(WorkItemTimeKeeper.class));
    }

    
    /**
     * Reset {@link ItemTask} based on {@link TimeKeeperObserver} flag
     * 
     * @param task
     * @param flag 
     */
    @Override
    public void handleTaskState(ItemTask task, boolean flag) {
        if (!flag) {
            task.setTaskState(TaskState.TIME_KEEPER);
        }
    }
    
    
    /**
     * Return observer name
     * 
     * @return 
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
