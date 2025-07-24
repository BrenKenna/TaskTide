/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.TaskTideManagerUtility;
import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.services.ServiceFactory;

import org.tasktide.engine.TaskTideExecutorServiceProvider;
import org.tasktide.engine.TaskTideWorkerUnitProvider;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.trackers.FutureTrackers;
import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;

import org.tasktide.tasktide.configurer.EngineConfig;
import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.ManagerConfig;
import org.tasktide.tasktide.configurer.TaskTideConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;


/**
 * Verifies that the TaskTide-Engine can be configured and used from
 *  configurations
 * 
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {
    Converters.class, Reflections.class, EntityConverter.class,
    Template.class, DocumentTemplate.class,
    EngineConfig.class, GlobalConfig.class
})
@AddExtensions( { ReflectionEntityMetadataExtension.class, DocumentExtension.class } )
public class ConfigureTaskTideTest {
    
    private static final Logger logger = LogManager.getLogger(ConfigureTaskTideTest.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private static final Jsonb JSON = JsonbBuilder.create(new JsonbConfig());
  
    private static SeContainer container;
    
    public ConfigureTaskTideTest() {}
    
    
    @BeforeAll
    public static void setUpClass() {
        String msg = "\n\n---------------- Initiating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        container = SeContainerInitializer.newInstance().initialize();
        logger.info(msg);
    }
    
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        container.close();
        logger.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
    /**
     * Waits via {@link ExecutorServiceTrackerWorkItem}
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
     * Fetch {@link DocumentTemplate} for {@link Template}
     * 
     * @return {@link Template}
     */
    public Template fetchDocumentTemplate() {
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(DocumentTemplate.class).get();
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} for the 
     * 
     * @param backend
     */
    public void fetchManager(Template backend) {
        
        // Initialize vars
        TaskTideServiceManager output;
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.NOSQL, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.NOSQL, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.NOSQL, backend, "Workflow-Service");
        
        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    
    /**
     * Tests that the engine can be configured from micro-profile config
     * 
     */
    @Test
    @Order(0)
    public void canConfigureEngineClient() {
    
        // Initialize data
        logger.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfigurer engineConfig;
        
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Fetch engine parameters into argument tree
        argTree = new ArgumentTree(" ");
        engineConfig = CDI.current().select(EngineConfig.class).get();
        engineConfig.initConfig(argTree);
        
        // Fetch configure values
        int workItemThreads, workItemThreshold, itemTaskThreads, itemTaskThreshold;
        workItemThreads = (int) argTree.getTree().getDataForAddress("engine").getArgument("WorkItem Threads").getValue();
        workItemThreshold = (int) argTree.getTree().getDataForAddress("engine").getArgument("WorkItem SubTasking Threshold").getValue();
        itemTaskThreads = (int) argTree.getTree().getDataForAddress("engine").getArgument("ItemTask Threads").getValue();
        itemTaskThreshold = (int) argTree.getTree().getDataForAddress("engine").getArgument("ItemTask SubTasking Threshold").getValue();
        
        // Configure executor service for work items, and item tasks
        TaskTideExecutorServiceProvider.initialize(workItemThreads, itemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        int nWorkItems = 4, nItemTask = 3;
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, nWorkItems, nItemTask);
        
        // Configure observer
        int timeKeeperObserverMaxTime = (int) argTree.getTree().getDataForAddress("engine").getArgument("TimeKeeper Wall Time").getValue();
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(timeKeeperObserverMaxTime)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withSubThreads(workItemThreads)
            .withSubTaskThreshold(workItemThreshold)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withWorkload(workload)
            .withExecutorService(executorService)
            .withThreshold(workItemThreshold)
            .withSubExecutor(executor)
        .build();
        
        // Process work
        processor.process();
        this.waitOnExecutorTrackerWorkItem(nWorkItems, logger);
        
        // Evaluate test status
        boolean assertionState;
        int expected = nWorkItems * nItemTask;
        int nProcessed = countNonActive(workload);
        if ( nProcessed == expected ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        
        // Process evaluation
        logger.info("\n-------- Displaying Execution Time Summary --------\n");
        fetchExecutionTimesWorkItem(workload, logger);
        logger.info("\n-------- Displaying Execution Time Summary --------\n");
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Tests Running EngineClient Through Config  ================\n");
    }
    
    
    @Test
    @Order(1)
    public void canConfigurerManagerClient() {
    
        // Initialize data
        logger.info("\n\n================ Tests Running ManagerClient Through Config  ================\n");
        ArgumentTree argTree;
        TaskTideConfigurer globalConfig, managerConfig;
        TaskTideServiceManager taskTideManager;
        Template backend;
        List<WorkItem> workload;
        TaskTideModel<WorkItem> result;
        boolean assertionState ;
        
        
        // Initialize configuration
        argTree = new ArgumentTree(" ");
        globalConfig = CDI.current().select(GlobalConfig.class).get();
        globalConfig.initConfig(argTree);
        managerConfig = CDI.current().select(ManagerConfig.class).get();
        managerConfig.initConfig(argTree);
        
        // Configure requirements
        String provider = (String) argTree.getTree().getDataForAddress("").getArgument("NoSQL Provider").getValue();
        logger.info("Fetching Service Using '{}' Backend", provider);
        backend = fetchDocumentTemplate();
        Map<String, String> map = TaskTideServiceManager.fetchWorkItemService().getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for Template WorkItem Service:\n'{}'", mapToJsonString(map));
        
        // Add records
        logger.info("Verifying that records can be added");
        try {
            workload = TaskTideManagerUtility.importTasks("TestData", "nestedTaskImports.txt", "|", ",");
            assertionState = TaskTideServiceManager.fetchWorkItemService().extendModel(workload);
            result = TaskTideServiceManager.fetchWorkItemService().fetchById( workload.get(0).getId());
            logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        }
        catch (Exception ex) {
            logger.error("Unable to parse the 'nestedTaskImports.txt' from test resources");
            assertionState = false;
        }
        
        // Log test state
        logger.info("\n\n================ Tests Running ManagerClient Through Config ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}