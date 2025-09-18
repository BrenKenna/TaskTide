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

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.engine.EngineUtility;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.TaskTideWorkerUnitProvider;
import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.executor.WorkItemExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;
import org.tasktide.engine.worker.processor.WorkItemProcessor;
import org.tasktide.tasktide.parser.model.ArgumentMap;


/**
 * Class for configuring implementing the TaskTideEngine 
 * 
 * @author bkenna
 */
public class TaskTideEngineClient extends TaskTideClient {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(TaskTideEngineClient.class);
    private final TaskTideWorkerUnitProvider unitProvider;
    private int workItemThreads, itemTaskThreads, workThreshold, taskThreshold;
    private TaskTideProcessor<WorkItem> processor;
    private TaskTideExecutor<WorkItem> workExec;
    private TaskTideEngineObserver<WorkItem> obs;
    private ArgumentMap engineArgs, globalArgs;
    private String step;
    
    
    /**
     * Construct engine client
     * 
     * @param configMap
     */
    public TaskTideEngineClient(ClientConfigMap configMap) {
        super(configMap);
        this.unitProvider = new TaskTideWorkerUnitProvider();
        this.engineArgs = this.getArgTree().getTree().getDataForAddress("engine");
        this.globalArgs = this.getArgTree().getTree().getDataForAddress("");
        this.step = (String) globalArgs.getArgument("Step Name").getValue();
    }
    
    
    /**
     * Initialize client by:
     * <br><br>
     * 1). Configuring a separate {@link ExecutorService}
     *  for {@link WorkItem}, and {@link ItemTask}.
     * <br><br>
     * 2). Configuring {@link TaskTideEngineObserver}.
     * <br><br>
     * 3). Configuring {@link TaskTideExecutor}.
     * <br><br>
     * 4). Configuring {@link TaskTideProcessor}.
     */
    @Override
    protected boolean configureClient() {
        
        // Try configure client
        try {
            // Initialize executor service 
            ExecutorService executorServ;
            executorServ = this.initializeAndConfigureExecutorServices();

            // Configure EngineObserver
            this.obs = this.configureWorkItemEngineObserverChain();

            // Configure Executor
            this.workExec = this.configureWorkItemExecutor(obs);

            // Configures processor
            this.processor = this.configureWorkItemProcessor(executorServ, workExec);
            return true;
        }
        
        // Catch and log error, return false for graceful shutdown
        catch (Exception ex) {
            LOGGER.error("Error during client configuration, displaying stack trace:\n{}", ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Processes workload through {@link TaskTideProcessor}
     * 
     */
    @Override
    protected void performClientTask() {
        
        // Fetch and run processing
        this.fetchAndRun();
        
        // Clean up - close connections etc
        this.cleanUp();
    }
    
    
    /**
     * Performs required cleanup actions
     * 
     */
    @Override
    protected void cleanUp() {
        
    }
    
    
    /**
     * Determines whether pilot label is configured
     * 
     * @return boolean 
     */
    private boolean hasPilotLabel() {
        return
            (this.engineArgs.getArgument("Pilot Label Key").getValue() != "" &&
            this.engineArgs.getArgument("Pilot Label Value").getValue() != "")
            || 
            this.engineArgs.getArgument("Pilot Label Annotation").getValue() != ""
        ;
    }
    
    
    /**
     * Fetches available work based on pilot label annotation
     * 
     * @param step
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> handlePilotLabel(String step) {
    
        if ( this.engineArgs.getArgument("Pilot Label Key").getValue() != "" &&
            this.engineArgs.getArgument("Pilot Label Value").getValue() != "" )
        {
            String key = (String) this.engineArgs.getArgument("Pilot Label Key").getValue();
            Object value = this.engineArgs.getArgument("Pilot Label Value").getValue();
            return EngineUtility.fetchToDoWorkTargetPilotLabel(step, key, value);
        }
        
        else {
            String json = (String) this.engineArgs.getArgument("Pilot Label Annotation").getValue();
            CustomAnnotation anno = JsonUtils.fromJson(json, CustomAnnotation.class);
            return EngineUtility.fetchToDoWorkTargetPilotLabel(step, anno);
        }
    }
    
    
    /**
     * Fetches engine workload, checking if pilot label
     *  was used
     * 
     * @param step
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> fetchWorkload(String step) {
        if ( this.hasPilotLabel() ) {
            LOGGER.info("Pilot label detected");
            return this.handlePilotLabel(step);
        }
        else {
            return EngineUtility.fetchToDoWorkTarget(step);
        }
    }
    
    
    /**
     * Fetch and run workloads
     * 
     */
    private void fetchAndRun() {
        
        // Process each step in order provided
        LOGGER.info("Determing how to process workload");
        if ( this.step.contains(",") ) {
            String[] steps = this.step.split(",");
            LOGGER.info("Processing in pipeline mode:\t'{}'", JsonUtils.toJson(false, steps));
            for ( String elm : steps ) {
                LOGGER.info("Processing step:\t'{}'", elm);
                List<WorkItem> workload = this.fetchWorkload(elm);
                this.processWorkload(workload);
                LOGGER.info("Processing complete for step:\t'{}'", elm);
            }
        }
        
        // Process target step
        else if ( !this.step.equalsIgnoreCase("na") ) {
            LOGGER.info("Processing single step:\t'{}'", step);
            List<WorkItem> workload = EngineUtility.fetchToDoWorkTarget(step);
            this.processWorkload(workload);
            LOGGER.info("Processing complete for step:\t'{}'", step);
        }
        
        // Process all, perhaps check a force as it could be a mistake?
        else {
            LOGGER.info("No step detected, processing unassigned under '{}'", step);
            List<WorkItem> workload = EngineUtility.fetchToDoWorkTarget(step);
            this.processWorkload(workload);
            LOGGER.info("Processing complete");
        }
    }
    
    
    /**
     * Processes provided workload
     * 
     * @param workload 
     */
    private void processWorkload(List<WorkItem> workload) {
        
        // Process if available
        if ( !workload.isEmpty() ) {
            LOGGER.info("Processing workload of size:\t'{}'", workload.size());
            this.processor.processChunks(workload);
            EngineUtility.waitOnExecutorTrackerWorkItem(workload.size(), LOGGER);
        }
        else {
            LOGGER.warn(
          "Warning, no ToDo tasks available for processing. Query below backend for more information\n\n{}\n\n",
             TaskTideServiceManager.fetchWorkItemService().getRepo().getRepositoryMetaData()
            );
        }
    }
    
    
    /**
     * Initializes and configures an {@link ExecutorService} for
     *  {@link WorkItem} and {@link ItemTask} processing through the
     *  {@link TaskTideExecutorServiceProvider}
     * 
     * @return {@link ExecutorService}
     */
    private ExecutorService initializeAndConfigureExecutorServices() {
        this.workItemThreads = (int) this.engineArgs.getArgument("WorkItem Threads").getValue();
        this.workThreshold = (int) this.engineArgs.getArgument("WorkItem SubTasking Threshold").getValue();
        this.itemTaskThreads = (int) this.engineArgs.getArgument("ItemTask Threads").getValue();
        this.taskThreshold = (int) this.engineArgs.getArgument("ItemTask SubTasking Threshold").getValue();
        TaskTideExecutorServiceProvider.initialize(this.workItemThreads, this.itemTaskThreads);
        return TaskTideExecutorServiceProvider.workItemExecutorService();
    }
    
    
    /**
     * Configures the EngineObserver for {@link WorkItem}
     * 
     * @return {@link TaskTideEngineObserver}
     */
    private TaskTideEngineObserver<WorkItem> configureWorkItemEngineObserverChain() {
        int timeKeeperObserverMaxTime = (int) this.engineArgs.getArgument("TimeKeeper Wall Time").getValue();
        return unitProvider.getWorkItemObsBuilder()
            .withMaxTime(timeKeeperObserverMaxTime)
        .build();
    } 
    
    
    /**
     * Configures {@link WorkItemExecutor}
     * 
     * @param obs
     * @return {@link TaskTideExecutor} of {@link WorkItem}
     */
    private TaskTideExecutor<WorkItem> configureWorkItemExecutor(TaskTideEngineObserver<WorkItem> obs) {
        return unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(obs)
            .withSubThreads(this.workItemThreads)
            .withSubTaskThreshold(this.workThreshold)
        .build();
    }

    
    /**
     * Configures {@link WorkItemProcessor}
     * 
     * @param execServ
     * @param subExecutor
     * @return {@link TaskTideProcessor} of {@link WorkItem}
     */
    private TaskTideProcessor<WorkItem> configureWorkItemProcessor(ExecutorService execServ, TaskTideExecutor<WorkItem> subExecutor) {
        return unitProvider.getWorkItemProcBuilder()
            .withWorkload(EngineUtility.fetchToDoWork())
            .withExecutorService(execServ)
            .withThreshold(this.workThreshold)
            .withSubExecutor(subExecutor)
        .build();
    }
}