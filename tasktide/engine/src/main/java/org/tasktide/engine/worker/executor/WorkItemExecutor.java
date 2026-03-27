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
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.wokerunitprovider.TaskTideExecutorServiceProvider;
import org.tasktide.engine.wokerunitprovider.TaskTideWorkerUnitProvider;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.observer.chain.WorkItemObserver;
import org.tasktide.engine.worker.processor.ItemTaskProcessor;

import org.tasktide.engine.worker.processor.TaskTideProcessor;


/**
 * Class to handle the execution of {@link WorkItem}
 * 
 * @author bkenna
 */
public class WorkItemExecutor extends TaskTideExecutor<WorkItem> {

    // Attributes
    private final TaskTideWorkerUnitProvider unitProvider;
    
    
    /**
     * Construct {@link TaskTideExecutor} for {@link WorkItem} 
     * 
     */
    public WorkItemExecutor() {
        super(new WorkItemObserver(), LogManager.getLogger(WorkItemExecutor.class));
        this.unitProvider = new TaskTideWorkerUnitProvider();
    }
    
    
    /**
     * Construct {@link WorkItem} with thread and task threshold values for {@link ItemTask}
     * 
     * @param taskThrehold 
     */
    public WorkItemExecutor(int taskThrehold) {
        super(new WorkItemObserver(), LogManager.getLogger(WorkItemExecutor.class));
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
    public WorkItemExecutor(TaskTideEngineObserver<WorkItem> observer, int nThreads, int taskThrehold) {
        super(observer, LogManager.getLogger(WorkItemExecutor.class));
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
    public boolean executeTask(WorkItem task) throws IOException, InterruptedException {
        
        // Configure ItemTaskProcessor
        LOGGER.info(
            "Configuring ItemTaskProcessor for Workload of size '{}' on thread '{}' for WorkItem:\t'{}'",
            task.getTaskCount(), Thread.currentThread().getName(), task.getId()
        );
            
        // Determine if any tasks are available
        boolean state;
        List<ItemTask> toDo = task.getWorkload().fetchByState().get(TaskState.PENDING);
        if ( !toDo.isEmpty() ) {
            
            // Determine if work item has multiple item tasks
            if ( toDo.size() > 1 ) {
                LOGGER.info(
                    "Configuring ItemTaskProcessor for nested workload of:\t'{}'",
                    task.getId()
                );
                TaskTideProcessor<ItemTask> subProcessor = this.provideProcessor();
                state = this.handleNestedWorkItem(task, toDo, subProcessor);
            }
            
            // Otherwise process as single task work item
            else {
                state = this.handleSingleItemTaskWorkItem(task, toDo.get(0));
            }
            
            // Return state
            return state;
        }
        
        // Otherwise skip
        else {
            LOGGER.warn("No active tasks detected for WorkItem:\t'{}'", task.getId());
            return false;
        }  
        
    }
    
    
    /**
     * Handle the execution of single {@link ItemTask} from
     *  {@link WorkItem}
     * 
     * @param workItem
     * @param itemTask
     * @return boolean
     */
    public boolean handleSingleItemTaskWorkItem(WorkItem workItem, ItemTask itemTask) {
    
        // Initialize vars
        TaskTideExecutor<ItemTask> itemTaskExecutor;
        boolean result = false;
        
        // Configure executor
        LOGGER.info(
            "Configuring ItemTaskExecutor for single task workload of:\t'{}' with '{}'",
            workItem.getId(),
            itemTask.getId()
        );
        itemTaskExecutor = new ItemTaskExecutor();
        
        // Try execute
        try {
            result = itemTaskExecutor.executeTask(itemTask);
        }
        catch ( IOException | InterruptedException ex) {
            LOGGER.error(
                "Error during execution of '{}', under '{}':\t'{}'\n\n'{}'",
                itemTask.getId(), workItem.getId(),
                ex.getMessage(), ex
            );
            result = false;
        }
        finally {
            LOGGER.info(
                "Execution of '{}', under '{}' completed with status:\t'{}'",
                itemTask.getId(), workItem.getId(), result
            );
        }
        
        
        // Return result
        return result;
    }
    
    
    /**
     * Delegate the processing of nested {@link WorkIten} processing
     *  to {@link ItemTaskProcessor}
     * 
     * @param task
     * @param toDo
     * @param subProcessor
     * @return boolean
     */
    public boolean handleNestedWorkItem(
        WorkItem task,
        List<ItemTask> toDo,
        TaskTideProcessor<ItemTask> subProcessor
    ) {
        
        // Leave work item observer periodically summarize states until done
        boolean result;
        LOGGER.info(
            "Submitting workload of size '' for WorkItem:\t'{}'",
            toDo.size(),
            task.getId()
        );
        subProcessor.process(toDo);
        
        // Evaluate task processing
        LOGGER.info("ExecutorObserver polling ItemTaskStateSummary for WorkItem:\t'{}'", task.getId());
        if ( this.observer.onTaskProcessing(task) ) {
            LOGGER.info(
                "Task processing complete on WorkItem:\t'{}'",
                task.getId()
            );
            result = true;
        }

        // Otherwise log warning
        else {
            LOGGER.warn(
                "Warning, Observer checks onTaskProcessing failed for WorkItem:\t'{}'",
                task.getId()
            );
            result = false;
        }
        
        // Return result
        return result;
    }
    
    
    /**
     * Provide sub-processor
     * 
     * @return {@link TaskTideProcessor}-{@link WorkItem},{@link ItemTask}
     */
    public TaskTideProcessor<ItemTask> provideProcessor() {
        
        // Initialize required variables
        TaskTideProcessor<ItemTask> processor;
        ExecutorService executorService;
        
        // Fetch required components
        executorService = TaskTideExecutorServiceProvider.itemTaskExecutorService();
        
        // Construct sub processor
        processor = unitProvider.getItemTaskProcBuilder()
            .withExecutorService(executorService)
        .build();
        
       // Return processor
       return processor;
    }
}