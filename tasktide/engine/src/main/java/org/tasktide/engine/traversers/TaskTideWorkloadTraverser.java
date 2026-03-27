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
package org.tasktide.engine.traversers;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.tasktide.core.TaskTideModel;

import org.tasktide.engine.workerunit.TaskTideWorkerUnit;


/**
 * Class to template process of processing a list of
 *  {@link TaskTideModel}
 *
 * @param <T> of {@link TaskTideModel}
 * @author Bren
 */
public interface TaskTideWorkloadTraverser<T extends TaskTideModel<T>> extends TaskTideWorkerUnit<T>{
 
    /**
     * Traverses workload processing each element
     * 
     * @param workload
     * @throws TraverserCheckedException 
     */
    public void traverse(List<T> workload) throws TraverserCheckedException;
    
    
    /**
     * Traverses workload processing each element
     * 
     * @param workload
     * @param threadPool
     * @throws TraverserCheckedException 
     */
    public void traverse(List<T> workload, ExecutorService threadPool) throws TraverserCheckedException;
    
    
    /**
     * Processes an element of workload  
     * 
     * @param elm
     * @return boolean
     * 
     * @throws TraverserCheckedException 
     */
    public boolean processElm(T elm) throws TraverserCheckedException;
}