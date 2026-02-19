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
package org.tasktide.engine.worker.processor;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.trackers.ExecutorServiceItem;
import org.tasktide.engine.trackers.FutureTrackers;

import org.tasktide.engine.worker.executor.WorkItemExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;


/**
 * Concrete {@link ItemTask} {@link TaskTideProcessor}
 * 
 * @author bkenna
 */
public class WorkItemProcessor extends TaskTideProcessor<WorkItem> {
    
    // Attributes
    private final WorkItemExecutor worker;
    
    
    /**
     * Construct with workload
     * 
     * @param executorService 
     */
    public WorkItemProcessor(ExecutorService executorService) {
        super(executorService, LogManager.getLogger(WorkItemProcessor.class), ProcessorType.WORKITEM);
        this.worker = new WorkItemExecutor();
    }
    
    
    /**
     * Construct with all attributes
     * 
     * @param executorService
     * @param executor 
     */
    public WorkItemProcessor(
        ExecutorService executorService,
        TaskTideExecutor<WorkItem> executor
    ) {
        super(executorService, LogManager.getLogger(WorkItemProcessor.class), ProcessorType.WORKITEM);
        this.worker = (WorkItemExecutor) executor;
    }
    

    /**
     * Create a new sub processor from self
     * 
     * @return {@link TaskTideProcessor}-{@link WorkItem}
     */
    @Override
    protected TaskTideProcessor<WorkItem> newSubProcessor() {
        return new WorkItemProcessor(this.executorService);
    }

    
    /**
     * Fetch {@link WorkItemExecutor}
     * 
     * @return {@link TaskTideExecutor}-{@link WorkItem}
     */
    @Override
    protected TaskTideExecutor<WorkItem> getExecutor() {
        return this.worker;
    }

    
    /**
     * Add workload to the {@link FutureTrackers}
     * 
     * @param subList
     * @param future 
     */
    @Override
    protected void addTasksToTracker(List<WorkItem> subList, Future future) {
        for ( WorkItem task : subList ) {
            ExecutorServiceItem<WorkItem> item = new ExecutorServiceItem<>(task, future);
            FutureTrackers.WORK_ITEM_TRACKER.markTask(task.getId(), item);
        }
    }
    
    
    /**
     * Fetch records in tracker
     * 
     * @return int
     */
    @Override
    protected int fetchTrackerTaskCount() {
        return FutureTrackers.WORK_ITEM_TRACKER.taskCount();
    }
}