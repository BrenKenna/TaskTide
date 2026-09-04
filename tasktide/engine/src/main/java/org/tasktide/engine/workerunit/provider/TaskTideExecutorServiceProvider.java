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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;


/**
 * Singleton class to provide the {@link ExecutorService}
 *  for {@link WorkItem} and {@link ItemTask}
 * 
 * @author bkenna
 */
public class TaskTideExecutorServiceProvider {
 
    // There can be only one
    private static volatile TaskTideExecutorServiceProvider INSTANCE;
    
    
    // Executor services
    private final int engineWorkerThreads, itemTaskThreads;
    private final ExecutorService engineWorkerExecutorService;
    private final ExecutorService itemTaskExecutorService;
    
    
    /**
     * Construct with required thread pool size
     * 
     * @param engineWorkerThreads
     * @param itemTaskThreds 
     */
    private TaskTideExecutorServiceProvider(int engineWorkerThreads, int itemTaskThreds) {
        this.engineWorkerThreads = engineWorkerThreads;
        this.itemTaskThreads = itemTaskThreds;
        this.engineWorkerExecutorService = Executors.newFixedThreadPool(engineWorkerThreads);
        this.itemTaskExecutorService = Executors.newFixedThreadPool(itemTaskThreds);
    }

    
    /**
     * Initialize executor services for provided pool sizes
     * 
     * @param engineWorkerThreads
     * @param itemTaskThreads
     */
    public static synchronized void initialize(int engineWorkerThreads, int itemTaskThreads) {
        if ( INSTANCE != null ) {
            throw new IllegalStateException("TaskTideExecutorService already initialize");
        }
        INSTANCE = new TaskTideExecutorServiceProvider(engineWorkerThreads, itemTaskThreads);
    
    }
    
    
    /**
     * Provides the executor if initialized
     * 
     * @return TaskTideExecutorServiceProvider
     */
    public static TaskTideExecutorServiceProvider getInstance() {
        if ( INSTANCE == null ) {
            throw new IllegalStateException("TaskTideExecutorService not initialized");
        }
        else {
            return INSTANCE;
        }
    }
    
    
    /**
     * Get configured {@link ExecutorService} for {@link WorkItem} processing
     * 
     * @return {@link ExecutorService}
     */
    public ExecutorService getEngineWorkerExecutorService() {
        return this.engineWorkerExecutorService;
    }

    
    /**
     * Get configured {@link ExecutorService} for {@link ItemTask} processing
     * 
     * @return {@link ExecutorService}
     */
    public ExecutorService getItemTaskExecutorService() {
        return this.itemTaskExecutorService;
    }

    
    /**
     * Static accessor for convenience for {@link WorkItem}
     *  exectuor service
     * 
     * @return ExecutorService
     */
    public static ExecutorService engineWorkerExecutorService() {
        return getInstance().getEngineWorkerExecutorService();
    }

    
    /**
     * Static accessor for convenience for {@link ItemTask}
     *  exectuor service
     * 
     * @return ExecutorService
     */
    public static ExecutorService itemTaskExecutorService() {
        return getInstance().getItemTaskExecutorService();
    }

    
    /**
     * Thread pool size for {@link WorkItem} executor
     * 
     * @return int
     */
    public int getEngineWorkerThreads() {
        return this.engineWorkerThreads;
    }

    
    /**
     * Thread pool size for {@link ItemTask} executor
     * 
     * @return int
     */
    public int getItemTaskThreads() {
        return this.itemTaskThreads;
    }
    
    
    /**
     * Checks whether service provider is parallelized
     * 
     * @return boolean
     */
    public boolean isParallelized() {
        return this.getItemTaskThreads() > 1 || this.getEngineWorkerThreads() > 1;
    }
    
    
    /**
     * Reset the engine worker unit container for testing
     * 
     */
    public static void reset() {
        INSTANCE = null;
    }
}