/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.TaskTideWorkerUnitProvider;
import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.trackers.FutureTrackers;
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
    private String step;
    
    
    /**
     * Construct engine client
     * 
     * @param configMap
     */
    public TaskTideEngineClient(ClientConfigMap configMap) {
        super(configMap);
        this.unitProvider = new TaskTideWorkerUnitProvider();
        this.step = (String) this.getArgTree().getTree().getDataForAddress("engine").getArgument("Step").getValue();
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
     * Fetch and run worklaod(s)
     */
    private void fetchAndRun() {
        
        // Process each step in order provided
        LOGGER.info("Determing how to process workload");
        if ( this.step.contains(",") ) {
            String[] steps = this.step.split(",");
            LOGGER.info("Processing in pipeline mode:\t'{}'", JsonUtils.toJson(false, steps));
            for ( String elm : steps ) {
                LOGGER.info("Processing step:\t'{}'", elm);
                List<WorkItem> workload = this.fetchToDoWorkTarget(elm);
                this.processWorkload(workload);
                LOGGER.info("Processing complete for step:\t'{}'", elm);
            }
        }
        
        // Process target step
        else if ( !this.step.equalsIgnoreCase("na") ) {
            LOGGER.info("Processing single step:\t'{}'", step);
            List<WorkItem> workload = this.fetchToDoWorkTarget(step);
            this.processWorkload(workload);
            LOGGER.info("Processing complete for step:\t'{}'", step);
        }
        
        // Process all, perhaps check a force as it could be a mistake?
        else {
            LOGGER.info("No step detected, processing unassigned under '{}'", step);
            List<WorkItem> workload = this.fetchToDoWorkTarget(step);
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
            this.waitOnExecutorTrackerWorkItem(workload.size(), LOGGER);
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
        ArgumentMap engineConf = this.getArgTree().getTree().getDataForAddress("engine");
        this.workItemThreads = (int) engineConf.getArgument("WorkItem Threads").getValue();
        this.workThreshold = (int) engineConf.getArgument("WorkItem SubTasking Threshold").getValue();
        this.itemTaskThreads = (int) engineConf.getArgument("ItemTask Threads").getValue();
        this.taskThreshold = (int) engineConf.getArgument("ItemTask SubTasking Threshold").getValue();
        TaskTideExecutorServiceProvider.initialize(this.workItemThreads, this.itemTaskThreads);
        return TaskTideExecutorServiceProvider.workItemExecutorService();
    }
    
    
    /**
     * Configures the EngineObserver for {@link WorkItem}
     * 
     * @return {@link TaskTideEngineObserver}
     */
    private TaskTideEngineObserver<WorkItem> configureWorkItemEngineObserverChain() {
        int timeKeeperObserverMaxTime = (int) this.getArgTree().getTree().getDataForAddress("engine").getArgument("TimeKeeper Wall Time").getValue();
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
            .withWorkload(this.fetchToDoWork())
            .withExecutorService(execServ)
            .withThreshold(this.workThreshold)
            .withSubExecutor(subExecutor)
        .build();
    }
    
    
    /**
     * Waits via {@link FutureTrackers}
     * 
     * @param expected
     * @param logger 
     */
    public void waitOnExecutorTrackerWorkItem(int expected, Logger logger) {
        
        // Initialize vars
        boolean done = false;
        int baseDelaySeconds = 1, counter = 0;
        long sleepTime;
        int baseCount, currentDone, nRemaining;
        
        // Wait until done
        baseCount = FutureTrackers.WORK_ITEM_TRACKER.taskCount();
        logger.info("Begining state monitoring of ExecutorServiceTracker:\tN tasks = '{}'", baseCount);
        while ( !done ) {
        
            // Measure delay capping to 512
            int waitVal = baseDelaySeconds * (int)Math.pow(2, counter - 1);
            if ( waitVal <= 5) {
                sleepTime = 10 * 1000L;
            }
            else {
                sleepTime = Math.min(waitVal, 85) * 1000L;
            }
            
            // Wait
            logger.info("Letting '{}'ms elapse for state monitoring of ExecutorServiceTracker:\t'{}'", 
                sleepTime
            );
            try {TimeUnit.MILLISECONDS.sleep(sleepTime);} catch(InterruptedException ex) {Thread.currentThread().interrupt();}
            
            // Fetch summary
            currentDone = FutureTrackers.WORK_ITEM_TRACKER.countDone();
            nRemaining = baseCount - currentDone;
            if (nRemaining < 0) {
                baseCount = FutureTrackers.WORK_ITEM_TRACKER.taskCount();
                currentDone = FutureTrackers.WORK_ITEM_TRACKER.countDone();
                nRemaining = baseCount - currentDone;
            }
            logger.info(
          "Displaying Iter-'{}' StateSummary of ExecutorServiceTracker:\n\nTotal='{}', Remaining='{}', Done='{}', Expected='{}'", 
             counter, baseCount, nRemaining, currentDone, expected
            );
            
            // Sum of touched ItemTasks, did any raise TK error, Executor have states?
            counter++;
            done = currentDone == expected;
        }
    }
    
    
    /**
     * Scan tasks for count of done
     * 
     * @param workload
     * @return int
     */
    public int countNotActive(List<ItemTask> workload) {
        return (int) workload.stream()
            .parallel()
            .filter(
                task -> 
                    task.getTaskState() == TaskState.COMPLETE 
                        || task.getTaskState() == TaskState.ERROR
            )
        .count();
    }
    
    
    /**
     * Count non-pending tasks across {@link WorkItem} list
     * 
     * @param tasks
     * @return int
     */
    public int countNonActive(List<WorkItem> tasks) {
        return (int) tasks
            .stream()
            .parallel()
            .mapToInt( elm -> {
                    Collection<ItemTask> itemTasks = elm.getWorkload().getWorkload().values();
                    return countNotActive(new ArrayList<>(itemTasks));
            })
            .sum();
    }
    
    
    /**
     * Log execution times on INFO level
     * 
     * @param workload 
     * @param logger  
     */
    public void fetchExecutionTimes(List<ItemTask> workload, Logger logger) {
        String output = "\n\n";
        for (ItemTask task : workload) {
            output += String.format(
               "Task '%s' started on Thread '%s' '%d' finished '%d' duration '%d'\n",
               task.getId(), 
               task.getTaskLog().getThreadName(),
               task.getTaskLog().getStartTime(),
               task.getTaskLog().getEndTime(),
               task.getTaskLog().getEndTime() - task.getTaskLog().getStartTime()
            );
        }
        logger.info("Displaying Execution Times Across ItemTasks:{}", output);
    }
    
    
    /**
     * Fetch execution times across all
     * 
     * @param workload
     * @param logger 
     */
    public void fetchExecutionTimesWorkItem(List<WorkItem> workload, Logger logger) {
        for ( WorkItem item : workload ) {
            System.out.println("\n\n========= Analysing WorkItem:\t'" + item.getId() + "'=============\n\n");
            fetchExecutionTimes(item.fetchByStates().get(ItemState.DONE), logger);
            fetchExecutionTimes(item.fetchByStates().get(ItemState.ERROR), logger);
            System.out.println("\n\n========= Done WorkItem:\t'" + item.getId() + "'=============\n\n");
        }
    }
    
    
    /**
     * Represent map as json string
     * 
     * @param map
     * @return String Json
     */
    public String mapToJsonString(Object map) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(map);
    }
    
    
    /**
     * Fetch todo
     * 
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> fetchToDoWork() {
        return TaskTideServiceManager.fetchWorkItemService().viewByField("itemState", ItemState.TODO);
    }
    
    
    /**
     * Fetch todo for target step
     * 
     * @return List-{@link WorkItem}
     */
    private List<WorkItem> fetchToDoWorkTarget(String stepName) {
        return TaskTideServiceManager
            .fetchWorkItemService()
            .viewByFieldForGroup("itemState", ItemState.TODO, "stepName", stepName);
    }
}