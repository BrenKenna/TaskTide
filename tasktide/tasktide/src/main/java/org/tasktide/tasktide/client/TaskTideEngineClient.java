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
package org.tasktide.tasktide.client;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.builders.CustomAnnotationBuilder;

import org.tasktide.engine.worker.TaskTideEngineWorker;
import org.tasktide.engine.policies.WorkerExecutionPolicy;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;
import org.tasktide.engine.workerunit.container.WorkerUnitContainer;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;

import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;
import org.tasktide.engine.policies.AcquisitionPolicyMode;
import org.tasktide.parser.model.Argument;

import org.tasktide.parser.model.ArgumentMap;


/**
 * Class for configuring implementing the {@link TaskTideEngineWorker} 
 * 
 * @author bkenna
 */
public class TaskTideEngineClient extends TaskTideClient {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineClient.class);
    private final String step;
    private final WorkerUnitContainer workerContainer;
    private final ArgumentMap engineArgs, globalArgs;
    
    private TaskTideWorkloadAcquisitionPolicy acquisitionPolicy;
    private TaskTideEngineWorker worker;
    private int engineWorkerThreads, itemTaskThreads;
    private final Map<String, Object> acquisitionOpts;
    
    
    /**
     * Construct engine client
     * 
     * @param configMap
     */
    @SuppressWarnings("unchecked")
    public TaskTideEngineClient(ClientConfigMap configMap) {
        super(configMap);
        this.workerContainer = WorkerUnitContainer.getInstance();
        this.engineArgs = this.getArgTree().getTree().getDataForAddress("engine");
        this.globalArgs = this.getArgTree().getTree().getDataForAddress("");
        this.step = (String) globalArgs.getArgument("Step Name").getValue();
        this.acquisitionOpts = (Map<String, Object>) globalArgs.getArgument("Acquisition Options").getValue();
    }
    
    
    /**
     * Initialize client by:
     * <br><br>
     * 1). Configuring the {@link WorkerUnitContainer}
     * <br><br>
     * 2). Configuring an {@link TaskTideWorkloadAcquisitionPolicy}.
     * <br><br>
     * 3). Configuring the {@link TaskTideEngineWorker}
     */
    @Override
    protected boolean configureClient() {
        
        // Try configure engine client
        try {
            
            // Configure engine components
            LOGGER.info("Constructing TaskTide-Engine WorkerUnitContainer");
            this.configureWorkerContainer();
            
            // Configure the fuel for the engine
            LOGGER.info("Configuring the workload Acqusition Policy for Step:\t'{}'", this.step);
            this.acquisitionPolicy = this.configureAcquisitionPolicy();
            
            // Configure engine worker
            LOGGER.info("Constructing TaskTide-Engine Worker");
            this.worker = new TaskTideEngineWorker(this.acquisitionPolicy);
            
            // Return client state
            LOGGER.info("TaskTide-Engine client configured");
            return true;
        }
        
        // Catch and log error, return false for graceful shutdown
        catch ( TaskTideEngineCheckedException ex) {
            LOGGER.error("Error during client configuration, displaying stack trace:\n{}", ex);
            return false;
        }
    }
    
    
    /**
     * Processes workload through {@link TaskTideEngineWorker}
     * 
     */
    @Override
    protected void performClientTask() {
        
        // Determine eexecution policy
        LOGGER.info("Determining engine execution policy");
        String pol = (String) this.engineArgs.getArgument("Execution Policy").getValue();
        WorkerExecutionPolicy execPol = WorkerExecutionPolicy.get(pol);

        // Process based on policy
        LOGGER.info("Engine Worker operating in '{}' mode", execPol);
        try {
            this.worker.runEngine(execPol);
        }
        
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error("Error during workload processing by TaskTideEngineWorkre:\n\n{}", ex);
        }
        
        // Clean up - close connections etc
        this.cleanUp();
    }
    
    
    /**
     * Configures {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy}
     */
    public TaskTideWorkloadAcquisitionPolicy configureAcquisitionPolicy() {
        
        // Initialize acquisition policy
        LOGGER.info("Configuring TaskTide-Engine Workload Acqusition Policy");
        TaskTideWorkloadAcquisitionPolicy policy = AcquisitionPolicyMode.initBuilder(this.step, this.acquisitionOpts);
        LOGGER.info("TaskTide-Engine configured for:\t'{}'", policy.getPolicyMode());
        
        // Apply state
        policy = policy.withItemState(ItemState.TODO); // this can now be an argument
        
        // Apply annotation key-value
        if (
            this.engineArgs.getArgument("Pilot Label Key").getValue() != "" &&
            this.engineArgs.getArgument("Pilot Label Value").getValue() != ""
        ) {
            String key = (String) this.engineArgs.getArgument("Pilot Label Key").getValue();
            Object val = this.engineArgs.getArgument("Pilot Label Value").getValue();
            policy = policy.withAnno(key, val);
        }
        
        // Apply custom annotation
        if ( this.engineArgs.getArgument("Pilot Label Annotation").getValue() != "" ) {
            String json = (String) this.engineArgs.getArgument("Pilot Label Annotation").getValue();
            CustomAnnotation anno = CustomAnnotationBuilder.fromJsonString(json);
            policy = policy.withAnno(anno);
        }
        
        // Apply worker pool size
        policy.withPoolSize(this.engineWorkerThreads);
        
        // Apply window size
        Argument arg = this.engineArgs.getArgument("Window Size");
        int argVal = (int) arg.getValue();
        policy.withWindowSize(argVal);
        
        // Return acquisition policy
        return policy.build();
    }
    
    
    /**
     * Configures {@link WorkerUnitContainer} components
     * 
     * @throws {@link TaskTideEngineCheckedException} 
     */
    public void configureWorkerContainer() throws TaskTideEngineCheckedException {
    
        // Configure Process Executor Stream Path
        String pathString = (String) this.engineArgs.getArgument("Process Executor Stream Directory").getValue();
        this.workerContainer.configureStreamPath(pathString);
        
        // Set thread pool sizes
        LOGGER.info("Configuring taskTideEngineWorkerContainer components");
        this.engineWorkerThreads = (int) this.engineArgs.getArgument("Worker Pool Size").getValue();
        this.itemTaskThreads = (int) this.engineArgs.getArgument("ItemTask Threads").getValue();
        this.workerContainer.configureExecutorServices(this.engineWorkerThreads, this.itemTaskThreads);

        // Configure EngineObservers
        int timeKeeperObserverMaxTime = (int) this.engineArgs.getArgument("TimeKeeper Wall Time").getValue();
        this.workerContainer.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, timeKeeperObserverMaxTime);
        this.workerContainer.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, timeKeeperObserverMaxTime);
            
        // Configure Executors
        this.workerContainer.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
        this.workerContainer.configureProcessExecutor();

        // Configure traversers
        this.workerContainer.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
        this.workerContainer.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
    }
    
    
    /**
     * Performs required cleanup actions
     * 
     */
    @Override
    protected void cleanUp() {
        
    }
}