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
package org.tasktide.engine;


/**
 * Entry-point class for the TaskTide Engine
 * 
 * @author bkenna
 */
public class Engine {
    
    // Attributes
    private final TaskTideExecutorServiceProvider execProv;
    private final TaskTideWorkerUnitProvider workerProv;
    
    
    /**
     * Construct with required arguments
     * 
     * @param workItemThreads
     * @param itemTaskThreads 
     */
    public Engine(int workItemThreads, int itemTaskThreads) {
        TaskTideExecutorServiceProvider.initialize(workItemThreads, itemTaskThreads);
        this.execProv = TaskTideExecutorServiceProvider.getInstance();
        this.workerProv = new TaskTideWorkerUnitProvider();
    }
}
