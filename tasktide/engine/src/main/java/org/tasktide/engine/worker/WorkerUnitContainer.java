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
package org.tasktide.engine.worker;

import java.util.concurrent.ExecutorService;
import org.tasktide.core.TaskTideModel;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.TaskTideWorkerUnitProvider;
import org.tasktide.engine.TaskTideExecutorServiceProvider;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;
import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;
import org.tasktide.engine.exceptions.TaskTideEngineUncheckedException;
import org.tasktide.engine.traversers.ItemTaskTraverser;
import org.tasktide.engine.traversers.WorkItemTraverser;
import org.tasktide.engine.worker.executor.ProcessExecutor;
import org.tasktide.engine.worker.executor.TaskTideExecutor;


/**
 * Container class to hold instances of each {@link TaskTideWorkerUnit}
 *  for their use across the engine
 *
 * @author Bren
 */
public class WorkerUnitContainer {
    
    // Constructed attributes
    private static WorkerUnitContainer INSTANCE;
    private final TaskTideWorkerUnitProvider unitProvider;
    
    // Thread pools for WorkItem, ItenTask
    private boolean executorServiceConfigured = false;
    
    // Observers
    private TaskTideEngineObserver<WorkItem> workItemObserver;
    private TaskTideEngineObserver<ItemTask> itemTaskObserver;
    
    // Traversers
    private TaskTideWorkloadTraverser<WorkItem> workItemTraverser;
    private TaskTideWorkloadTraverser<ItemTask> itemTaskTraverser;
    
    // Executors
    private TaskTideExecutor<ItemTask> itemTaskExecutor;
    private ProcessExecutor processExecutor;
    
    
    /**
     * Private constructor for singleton
     */
    private WorkerUnitContainer() {
        this.unitProvider = new TaskTideWorkerUnitProvider();
    }
    
    
    /**
     * Static accessor for worker, configuring if not already
     *  configured. Method can be overloaded for parameters for
     *  dependencies
     * 
     * @return {@link WorkerUnitContainer}
     */
    public static WorkerUnitContainer getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new WorkerUnitContainer();
        }
        return INSTANCE;
    }

    
    /**
     * Configures the {@link ExecutorService} for 
     *  both {@link WorkItem}, and {@link ItemTask}
     * 
     * @param workItemThreads
     * @param itemTaskThreads
     * 
     * @throws TaskTideEngineCheckedException 
     */
    public void
        configureExecutorServices(int workItemThreads, int itemTaskThreads)
    throws TaskTideEngineCheckedException {
        if ( !this.executorServiceConfigured ) {
            TaskTideExecutorServiceProvider.initialize(workItemThreads, itemTaskThreads);
            this.executorServiceConfigured = true;
        }
        else {
            throw new TaskTideEngineCheckedException("ExecutorService already configured");
        }
    }

    
    /**
     * Configure {@link TaskTideEngineObserver} for {@link ItemTask}/{@link WorkItem}
     * 
     * @param type
     * @param timeKeeperWallTime
     * 
     * @throws org.tasktide.engine.exceptions.TaskTideEngineCheckedException
     */
    public void
        configureEngineObserverChain(WorkerUnitModelType type, int timeKeeperWallTime) 
    throws TaskTideEngineCheckedException {
        switch ( type ) {
            case ITEMTASK -> {
                if ( this.itemTaskObserver == null ) {
                    this.itemTaskObserver = this.unitProvider
                        .getItemTaskObsBuilder()
                        .withMaxTime(timeKeeperWallTime)
                    .build();
                }
                else {
                    throw new TaskTideEngineCheckedException("ItemTask Observer already configured");
                }
            }
            
            case WORKITEM -> {
                if ( this.workItemObserver == null ) {
                    this.workItemObserver = this.unitProvider
                        .getWorkItemObsBuilder()
                        .withMaxTime(timeKeeperWallTime)
                    .build();
                }
                else {
                    throw new TaskTideEngineCheckedException("WorkItem Observer already configured");
                }
            }
            
            default -> {
                throw new TaskTideEngineCheckedException("No valid observer provided");
            }
        }
    }

    
    /**
     * Configure {@link TaskTideWorkloadTraverser} for {@link ItemTask}/{@link WorkItem}.
     *  This should go through the UnitProvider builder
     * 
     * @param type
     * 
     * @throws TaskTideEngineCheckedException 
     */
    public void 
        configureWorkloadTraverser(WorkerUnitModelType type)
    throws TaskTideEngineCheckedException {
        switch( type ) {
        
            case ITEMTASK -> {
                if ( this.itemTaskTraverser == null ) {
                    this.itemTaskTraverser = new ItemTaskTraverser();
                }
                else {
                    throw new TaskTideEngineCheckedException("ItemTask Traverser already configured");
                }
            }
            
            case WORKITEM -> {
                if ( this.workItemTraverser == null ) {
                    this.workItemTraverser = new WorkItemTraverser();
                }
                else {
                    throw new TaskTideEngineCheckedException("WorkItem Traverser already configured");
                }
            }
            
            default -> {
                throw new TaskTideEngineCheckedException("No valid traverser provided");
            }
        }
    }
        
        
    /**
     * Configure {@link TaskTideExecutor} for {@link ItemTask}/{@link WorkItem}.
     * 
     * @param type
     * @throws TaskTideEngineCheckedException 
     */
    public void 
        configureEngineExecutor(WorkerUnitModelType type)
    throws TaskTideEngineCheckedException {
        switch( type ) {
        
            case ITEMTASK -> {
                if ( this.itemTaskExecutor == null ) {
                    this.itemTaskExecutor = this.unitProvider
                        .getItemTaskExecBuilder()
                    .build();
                }
                else {
                    throw new TaskTideEngineCheckedException("ItemTask Executor already configured");
                }
            }

            default -> {
                throw new TaskTideEngineCheckedException("No valid executor provided");
            }
        }
    }
    
    
    /**
     * Configures {@link ProcessExecutor}
     * 
     * @throws TaskTideEngineCheckedException 
     */
    public void configureProcessExecutor() throws TaskTideEngineCheckedException {
        if ( this.processExecutor == null ) {
            this.processExecutor = new ProcessExecutor();
        }
        else {
            throw new TaskTideEngineCheckedException("ProcessExecutor already configured"); 
        }
    }
    
    
    /**
     * Fetch required {@link TaskTideEngineObserver}
     * 
     * @param <T> of {@link TaskTideModel}
     * @param type
     * @return {@link TaskTideEngineObserver}
     * 
     * @throws TaskTideEngineUncheckedException 
     */
    @SuppressWarnings("unchecked")
    public <T extends TaskTideModel<T>> TaskTideEngineObserver<T>
        getEngineObserverChain(WorkerUnitModelType type)
    throws TaskTideEngineUncheckedException {
        return switch ( type ) {
        
            case WORKITEM -> {
                if ( this.workItemObserver != null ) {
                    yield (TaskTideEngineObserver<T>) this.workItemObserver;
                }
                else {
                    throw new TaskTideEngineUncheckedException("WorkItem Observer not configured");
                }
            }
            
            case ITEMTASK -> {
                if ( this.itemTaskObserver != null ) {
                    yield (TaskTideEngineObserver<T>)  this.itemTaskObserver;
                }
                else {
                    throw new TaskTideEngineUncheckedException("ItemTask Observer not configured");
                }
            }
            
            default -> {
                throw new TaskTideEngineUncheckedException("No valid observer provided");
            }
        };
    }
        
        
    /**
     * Fetch required {@link TaskTideWorkloadTraverser}
     * 
     * @param <T> of {@link TaskTideModel}
     * @param type
     * @return {@link TaskTideWorkloadTraverser}
     * 
     * @throws TaskTideEngineUncheckedException 
     */
    @SuppressWarnings("unchecked")
    public <T extends TaskTideModel<T>> TaskTideWorkloadTraverser<T>
        getEngineWorkloadTraverser(WorkerUnitModelType type)
    throws TaskTideEngineUncheckedException {
        return switch ( type ) {
        
            case WORKITEM -> {
                if ( this.workItemTraverser != null ) {
                    yield (TaskTideWorkloadTraverser<T>) this.workItemTraverser;
                }
                else {
                    throw new TaskTideEngineUncheckedException("WorkItem Traverser not configured");
                }
            }
            
            case ITEMTASK -> {
                if ( this.itemTaskTraverser != null ) {
                    yield (TaskTideWorkloadTraverser<T>) this.itemTaskTraverser;
                }
                else {
                    throw new TaskTideEngineUncheckedException("ItemTask Traverser not configured");
                }
            }
            
            default -> {
                throw new TaskTideEngineUncheckedException("No valid tarverser provided");
            }
        };
    }
        
    
    /**
     * Fetch configured {@link ProcessExecutor}
     * 
     * @return {@link ProcessExecutor}
     * @throws TaskTideEngineUncheckedException 
     */
    public ProcessExecutor getProcessExecutor() throws TaskTideEngineUncheckedException {
        if ( this.processExecutor != null ) {
            return this.processExecutor;
        }
        else {
            throw new TaskTideEngineUncheckedException("Process Executor not configured");
        }
    }
    
    
    
    /**
     * Fetch required {@link TaskTideExecutor}
     * 
     * @param <T> of {@link TaskTideModel}
     * @param type
     * @return {@link TaskTideExecutor}
     * 
     * @throws TaskTideEngineUncheckedException 
     */
    @SuppressWarnings("unchecked")
    public <T extends TaskTideModel<T>> TaskTideExecutor<T>
        getEngineExecutor(WorkerUnitModelType type)
    throws TaskTideEngineUncheckedException {
        return switch ( type ) {
            
            case ITEMTASK -> {
                if ( this.itemTaskExecutor != null ) {
                    yield (TaskTideExecutor<T>) this.itemTaskExecutor;
                }
                else {
                    throw new TaskTideEngineUncheckedException("ItemTask Executor not configured");
                }
            }
            
            default -> {
                throw new TaskTideEngineUncheckedException("No valid executor provided");
            }
        };
    }
}