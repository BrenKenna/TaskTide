/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.processingstrategy;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;
import org.tasktide.engine.traversers.TraverserCheckedException;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;



/**
 * Handles processing {@link WorkItem} as Nested/Un-Nested task
 *
 * @author Bren
 */
public class WorkItemProcessingStrategy implements ProcessingStrategy<WorkItem> {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemProcessingStrategy.class);
    private final WorkerUnitContainer workerUnits;

    private final TaskTideWorkloadTraverser<ItemTask> itemTaskTraverser;
    
    
    /**
     * Construct {@link ProcessingStrategy} for {@link WorkItem}
     * 
     */
    public WorkItemProcessingStrategy() {
        this.workerUnits        = WorkerUnitContainer.getInstance();
        this.itemTaskTraverser  = this.workerUnits.getEngineWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
    }
    
    
    /**
     * Process {@link WorkItem}
     * 
     * @param task
     * @return boolean
     * 
     * @throws TraverserCheckedException 
     */
    @Override
    public boolean processTask(WorkItem task) throws TraverserCheckedException {
        
        // Pass if no active tasks
        List<ItemTask> toDo = task.getWorkload().fetchByState().get(TaskState.PENDING);
        if ( toDo.isEmpty() ) {
            LOGGER.warn("Warning, no active tasks under WorkItem:\t'{}'", task.getId());
            return false;
        }
        
        // Determine if work item has multiple item tasks
        else {
            LOGGER.info(
                "Proceeding with processing '{}' tasks under '{}'",
                toDo.size(),
                task.getId()
            );
            return this.processWorkload(task, toDo);
        }
    }
    
    
    /**
     * Process {@link WorkItem} as nested workload
     * 
     * @param task
     * @param toDo
     * 
     * @return boolean
     */
    private boolean processWorkload(WorkItem task, List<ItemTask> toDo) {
    
        // Try process task
        LOGGER.info("Delegating WorkItem processing to ItemTaskTraverser:\t'{}'", task.getId());
        try {
            this.itemTaskTraverser.traverse(toDo);
            LOGGER.info("Execution completed");
            return true;
        }

        // Otherwise fail
        catch (TraverserCheckedException ex) {
            LOGGER.error("Error during WorkItem processing:\t'{}'\n\n{}", task.getId(), ex);
            return false;
        }
    }
}