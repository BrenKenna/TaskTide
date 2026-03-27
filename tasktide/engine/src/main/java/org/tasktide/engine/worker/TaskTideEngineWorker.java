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

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;
import org.tasktide.engine.traversers.TraverserCheckedException;


/**
 * Engine worker using the {@link WorkItemAcquisitionPolicy} interface
 *  to acquire a workload to process, and process them through the
 *  {@link TaskTideWorkloadTraverser} interface. Update simplifies the
 *  TaskTide-EngineClient, and EngineUtility methods
 *
 * @author Bren
 */
public class TaskTideEngineWorker {
    
    // Worker unit container
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineWorker.class);
    private final WorkerUnitContainer engineComponents;
    private final Random RAND = new Random(); 
    private final WorkItemAcquisitionPolicy policy;

    
    /**
     * Construct with {@link WorkItemAcquisitionPolicy}
     * 
     * @param policy 
     */
    public TaskTideEngineWorker(WorkItemAcquisitionPolicy policy) {
        this.engineComponents = WorkerUnitContainer.getInstance();
        this.policy = policy;
    }
    
    
    /**
     * Get the {@link WorkItemAcquisitionPolicy}
     * 
     * @return {@link WorkItemAcquisitionPolicy}
     */
    public WorkItemAcquisitionPolicy getPolicy() {
        return policy;
    }
    
    
    /**
     * Continuously scans {@link TaskTideRepository} for
     *  work asyncronously
     */
    public void serviceOperation() {
    
        // Perhaps allow a queue like a file being written?
        int counter = 0;
        while ( true ) {
            this.fetchAndRun();
            TaskTideEngineUtility.waitSeconds(RAND.nextInt(0, 11));
            counter++;
        }
    }
    
    
    /**
     * Continuously scans {@link TaskTideRepository} for
     *  work serially
     */
    public void serviceOperationSerial() {
    
        // Perhaps allow a queue like a file being written?
        int counter = 0;
        while ( true ) {
            this.fetchAndRun();
            TaskTideEngineUtility.waitSeconds(RAND.nextInt(0, 11));
            counter++;
        }
    }
    
    
    /**
     * Fetches engine workload, checking if pilot label
     *  was used
     * 
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload() {
        List<WorkItem> workload = this.policy.fetchWorkload();
        if ( workload.size() > 1 ) {
            Collections.shuffle(workload);
        }
        return workload;
    }
    
    
    /**
     * Fetch workload from {@link WorkItemAcquisitionPolicy},
     *  and process asyncronously
     */
    public void fetchAndRun() {
        
        // Process each step in order provided
        LOGGER.info("Determing how to process workload");
        ExecutorService threadPool = this.engineComponents.getThreadPool(WorkerUnitModelType.WORKITEM);
        TaskTideWorkloadTraverser<WorkItem> traverser = this.engineComponents
            .getEngineWorkloadTraverser(WorkerUnitModelType.WORKITEM);
        
        // Fetch workload
        List<WorkItem> workload = this.fetchWorkload();
        
        // Process workload
        try {
            traverser.traverse(workload, threadPool);
            LOGGER.info("Processing complete for step:\t'{}'", this.policy.getTarget());
        }
                
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error processing workload:\n\n'{}'", ex);
        }
    }
    
    
    /**
     * Fetch workload from {@link WorkItemAcquisitionPolicy},
     *  and process serially
     */
    public void fetchAndRunSerial() {
        
        // Process each step in order provided
        LOGGER.info("Determing how to process workload");
        TaskTideWorkloadTraverser<WorkItem> traverser = this.engineComponents
            .getEngineWorkloadTraverser(WorkerUnitModelType.WORKITEM);
        
        // Fetch workload
        List<WorkItem> workload = this.fetchWorkload();
        
        // Process workload
        try {
            traverser.traverse(workload);
            LOGGER.info("Processing complete for step:\t'{}'", this.policy.getTarget());
        }
                
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error processing workload:\n\n'{}'", ex);
        }
    }
}