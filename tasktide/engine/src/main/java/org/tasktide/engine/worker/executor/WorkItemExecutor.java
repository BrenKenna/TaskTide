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
package org.tasktide.engine.worker.executor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.TaskTideWorkerUnitProvider;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;

import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Class to handle the execution of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemExecutor extends TaskTideExecutor<WorkItem> {

    // Attributes
    private final int nThreads, taskThreshold;
    private final TaskTideWorkerUnitProvider unitProvider;
    
    
    /**
     * Construct {@link TaskTideExecutor} for {@link WorkItem} 
     * 
     */
    public WorkItemExecutor() {
        super(new WorkItemObserver(), LogManager.getLogger(WorkItemExecutor.class));
        this.nThreads = 4;
        this.taskThreshold = 2;
        this.unitProvider = new TaskTideWorkerUnitProvider();
    }
    
    
    /**
     * Construct {@link WorkItem} with thread and task threshold values for {@link ItemTask}
     * 
     * @param nThreads
     * @param taskThrehold 
     */
    public WorkItemExecutor(int nThreads, int taskThrehold) {
        super(new WorkItemObserver(), LogManager.getLogger(WorkItemExecutor.class));
        this.nThreads = nThreads;
        this.taskThreshold = taskThrehold;
        this.unitProvider = new TaskTideWorkerUnitProvider();
    }
    
    
    /**
     * Construct with {@link TaskTideEngineObserver} for {@link WorkItem} and 
     *     parallelism for {@link ItemTask} processing
     * 
     * @param observer
     * @param nThreads
     * @param taskThrehold
     */
    public WorkItemExecutor( TaskTideEngineObserver<WorkItem> observer, int nThreads, int taskThrehold) {
        super(observer, LogManager.getLogger(WorkItemExecutor.class));
        this.nThreads = nThreads;
        this.taskThreshold = taskThrehold;
        this.unitProvider = new TaskTideWorkerUnitProvider();
    }
    
    
    /**
     * Handle execution of {@link WorkItem}, ex no point paralllizing if one task etc.
     * 
     * Since WorkItem has workload, can track it's own
     * 
     * @param task
     * @return boolean
     * 
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    protected boolean executeTask(WorkItem task) throws IOException, InterruptedException {
        
        // Configure ItemTaskProcessor
        logger.info(
      "Configuring ItemTaskProcessor for Workload of size '{}' on thread '{}' for WorkItem:\t'{}'",
         task.getTaskCount(), Thread.currentThread().getName(), task.getId()
        );
            
        // Execute workload of processor
        logger.info("Processor configured, processing workload for WorkItem:\t'{}'", task.getId());
        TaskTideProcessor<ItemTask> subProcessor = this.provideProcessor(task);
        subProcessor.process();
            
        // Leave work item observer periodically summarize states until done
        logger.info("ExecutorObserver polling ItemTaskStateSummary for WorkItem:\t'{}'", task.getId());
            
        // Evaluate task processing
        if ( this.observer.onTaskProcessing(task) ) {
            logger.info(
          "Task processing complete on WorkItem:\t'{}'",
             task.getId()
            );
            return true;
        }
        
        // Otherwise log warning
        else {
            logger.warn(
          "Warning, Observer checks onTaskProcessing failed for WorkItem:\t'{}'", 
             task.getId()
            );
            return false;
        }
    }
    
    
    /**
     * Provide sub-processor
     * 
     * @param task
     * @return {@link TaskTideProcessor}-{@link WorkItem},{@link ItemTask}
     */
    public TaskTideProcessor<ItemTask> provideProcessor(WorkItem task) {
        
        // Initialize required variables
        TaskTideProcessor<ItemTask> processor;
        List<ItemTask> toDo;
        ExecutorService executorService;
        
        // Fetch required components
        toDo = task.fetchByStates().get(ItemState.TODO);
        executorService = TaskTideExecutorServiceProvider.itemTaskExecutorService();
        
        // Construct sub processor
        processor = unitProvider.getItemTaskProcBuilder()
            .withWorkload(toDo)
            .withExecutorService(executorService)
            .withThreshold(taskThreshold)
        .build();
        
       // Return processor
       return processor;
    }

    
    /**
     * Get number threads for sub-processor
     * 
     * @return int
     */
    public int getnThreads() {
        return nThreads;
    }

    
    /**
     * Get task threshold limit for sub-processor
     * 
     * @return int
     */
    public int getTaskThreshold() {
        return taskThreshold;
    }
}
