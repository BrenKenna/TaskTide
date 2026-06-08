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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.executor.ItemTaskExecutor;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.traversers.TraverserCheckedException;
import org.tasktide.engine.traversers.WorkItemTraverser;


/**
 *
 * @author Bren
 */
public class ItemTaskProcessingStrategy implements ProcessingStrategy<ItemTask> {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(WorkItemTraverser.class);
    private final TaskTideEngineObserver<ItemTask> observer;
    private final ItemTaskExecutor executor;
    
    
    /**
     * Construct with observer and executor
     * 
     * @param observer
     * @param executor 
     */
    ItemTaskProcessingStrategy(TaskTideEngineObserver<ItemTask> observer, ItemTaskExecutor executor) {
        this.observer = observer;
        this.executor = executor;
    }
    
    
    /**
     * Processes provided task, entry point for passing to
     *  {@link ExecutorService}
     * 
     * @param elm
     * @return boolean
     * 
     * @throws TraverserCheckedException 
     */
    @Override
    public boolean processTask(ItemTask elm) throws TraverserCheckedException {
        
        // Pass if preprocessing fails
        if ( ! this.observer.onTaskProcessing(elm) ) {
            LOGGER.warn("Warning, preprocessing failed for task:\t'{}'", elm.getId());
            return false;
        }
        
        // Otherwise process
        else {
            try {
                if ( this.executor.executeTask(elm) ) {
                    LOGGER.info("Processing sucessful for task:\t'{}'", elm.getId());
                    return true;
                }
                else {
                    LOGGER.info("Processing unsuccessful for task:\t'{}'", elm.getId());
                    return false;
                }
            }
            catch ( IOException | InterruptedException ex ) {
                LOGGER.error(
                    "Error processing task:\t'{}'\n\n{}",
                    elm.getId(), ex
                );
                return false;
            }
        }
    }
}