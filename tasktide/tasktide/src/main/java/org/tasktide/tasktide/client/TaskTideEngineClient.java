/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;
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
        this.step = (String) engineArgs.getArgument("Step").getValue();
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
                List<WorkItem> workload = EngineUtility.fetchToDoWorkTarget(elm);
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