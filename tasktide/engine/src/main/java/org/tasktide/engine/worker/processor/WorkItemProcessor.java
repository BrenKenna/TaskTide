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

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.concurrent.Future;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.engine.TaskTideExecutorServiceProvider;
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
     * @param workload
     * @param threshold
     * @param executorService 
     */
    public WorkItemProcessor(List<WorkItem> workload, int threshold, ExecutorService executorService) {
        super(workload, threshold, executorService, LogManager.getLogger(WorkItemProcessor.class));
        this.worker = new WorkItemExecutor();
    }
    
    
    /**
     * Construct with all attributes
     * 
     * @param workload
     * @param threshold
     * @param executorService
     * @param executor 
     */
    public WorkItemProcessor(
        List<WorkItem> workload,
        int threshold,
        ExecutorService executorService,
        TaskTideExecutor<WorkItem> executor
    ) {
        super(workload, threshold, executorService, LogManager.getLogger(WorkItemProcessor.class));
        this.worker = (WorkItemExecutor) executor;
    }
    

    /**
     * Create a new sub processor from self
     * 
     * @param subList
     * @return {@link TaskTideProcessor}-{@link WorkItem}
     */
    @Override
    protected TaskTideProcessor<WorkItem> newSubProcessor(List<WorkItem> subList) {
        return new WorkItemProcessor(subList, this.threshold, this.executorService);
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
     * Splits workload into smaller chunks for each thread
     * 
     * @param workload
     * @return List-List-{@link WorkItem}
     */
    @Override
    protected List<List<WorkItem>> parallelChunks(List<WorkItem> workload) {
        
        // Initialize output
        List< List<WorkItem> > results = new ArrayList<>();
        
        // Initialize batch handler
        int workItemThreads = TaskTideExecutorServiceProvider.getInstance().getWorkItemThreads();
        int batchSize = workload.size() / workItemThreads;
        
        // Fetch sclies
        int start = 0, end = 0;
        while ( end < workload.size() ) {
            results.add(workload.subList(start, end));
            start = end + 1;
            end = start + batchSize;
            
            if ( end > workload.size() ) {
                results.add(workload.subList(start, workload.size()));
            }
        }
        return results;
    }
}
