/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.trackers.FutureTrackers;
import org.tasktide.engine.trackers.TaskTrackers;

import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.deprecated_processor.TaskTideProcessor;

import org.tasktide.engine.workerunit.provider.TaskTideExecutorServiceProvider;
import org.tasktide.engine.workerunit.provider.TaskTideWorkerUnitProvider;

/**
 *
 * @author bkenna
 */
public class InitialEngineTests {
    
    private static final Logger logger = LogManager.getLogger(InitialEngineTests.class);
    
    public InitialEngineTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItemProcessor Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItemProcessor Tests ----------------\n";
        logger.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
        logger.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("\n\n================ Terminating Test ================\n");
    }
    
    
    /**
     * Test using the {@link TaskTideWorkerUnitProvider} and {@link TaskTideExecutorServiceProvider}
     *  for deploying {@link WorkItem} processing via {@link TaskTideProcessor}
     */
    @Test
    @Order(0)
    public void canRunThroughProviders() {
    
        // Initialize test
        logger.info("\n\n================ Can Run Through Providers Test ================\n");
        int nProcessed = 0, expected = 24;
        boolean assertionState;
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Configure executor service for work items, and item tasks
        int nWorkItemThreads = 2, nItemTaskThreads = 3;
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4, 6);
        
        // Configure observer
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(1000000)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withWorkItemObserver(observer)
            .withSubThreads(2)
            .withSubTaskThreshold(3)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withExecutorService(executorService)
            .withSubExecutor(executor)
        .build();
        
        // Process tasks
        processor.process(workload);
        EngineTestUtils.waitUntilDoneWorkItem(workload, 30, logger);
        
        // Evaluate test status
        nProcessed = EngineTestUtils.countNonActive(workload);
        if ( nProcessed >= 0 ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
    }
    
    
    
    /**
     * Test using the {@link TaskTideWorkerUnitProvider} and {@link TaskTideExecutorServiceProvider}
     *  for deploying {@link WorkItem} processing via {@link TaskTideProcessor}
     * 
     * One {@link WorkItem} works fine with 2 workitem, and 3 item task threads. Even if one fails
     */
    @Test
    @Order(1)
    public void canWaitWithExecutorServiceTracker() {
    
        // Initialize test
        logger.info("\n\n================ Can Run Through Providers Test ================\n");
        int nProcessed = 0, expected = 2;
        boolean assertionState;
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Configure executor service for work items, and item tasks
        int nWorkItemThreads = 2, nItemTaskThreads = 3;
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 1, 2);
        
        // Configure observer
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(1000000)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withSubThreads(2)
            .withSubTaskThreshold(1)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withExecutorService(executorService)
            .withSubExecutor(executor)
        .build();
        
        // Process tasks
        processor.process(workload);
        
        // Wait until done:
        //  N ItemTasks = (6*4), WorkItems = 4
        //   -> ItemTasks & WorkItems are both done
        //   -> Should probably separate
        //EngineTestUtils.waitOnExecutorTracker(2, logger); 
        logger.info("Displaying ExecutorServiceTracker:\n\n'{}'\n\n",
            FutureTrackers.WORK_ITEM_TRACKER.getIds()
        );
        
        // Evaluate test status
        nProcessed = EngineTestUtils.countNonActive(workload);
        if ( nProcessed == expected ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
    }
    
    
    /**
     * Checks case where threshold is 5, and available subTasks 2.
     *  Should then enqueue all subTasks, for both {@link WorkItem}
     *  which is more likely, and {@link ItemTask} less likely.
     * 
     */
    @Test
    @Order(2)
    public void canWaitWithThresholdToWorkloadImbalance() {
    
        // Initialize test
        logger.info("\n\n================ Threshold to Workload Imbalance Test ================\n");
        int nProcessed = 0, expected = 2;
        boolean assertionState;
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Configure executor service for work items, and item tasks
        int nWorkItemThreads = 2, nItemTaskThreads = 3;
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 1, 2);
        
        // Configure observer
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(1000000)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withSubThreads(3)
            .withSubTaskThreshold(5)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withExecutorService(executorService)
            .withSubExecutor(executor)
        .build();
        
        // Process tasks
        processor.process(workload);
        
        // Wait until done:
        //  N ItemTasks = (6*4), WorkItems = 4
        //   -> ItemTasks & WorkItems are both done
        //   -> Should probably separate
        //EngineTestUtils.waitOnExecutorTracker(2, logger); 
        logger.info("Displaying ExecutorServiceTracker:\n\n'{}'\n\n",
            FutureTrackers.WORK_ITEM_TRACKER.getIds()
        );
        
        // Evaluate test status
        nProcessed = EngineTestUtils.countNonActive(workload);
        if ( nProcessed == expected ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Threshold to Workload Imbalance Test ================\n");
    }
    
    
    /**
     * Checks case where there multiple {@link WorkItem}, and threshold matches threads
     * <br><br>
     * - 5 consecutive tests of 2 WI with 2 IT all passed.
     * <br><br>
     * - 5 consecutive tests of 4 WI with 6 IT all passed (2*3 threads)
     */
    @Test
    @Order(3)
    public void canWaitWithMultipleWorkItems() {
    
        // Initialize test
        logger.info("\n\n================ Multiple WorkItems Test ================\n");
        int nProcessed = 0, workItems = 4, tasks = 6;
        boolean assertionState;
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Configure executor service for work items, and item tasks
        int nWorkItemThreads = 2, nItemTaskThreads = 3;
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, workItems, tasks);
        
        // Configure observer
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(1000000)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withSubThreads(nItemTaskThreads)
            .withSubTaskThreshold(nItemTaskThreads)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withExecutorService(executorService)
            .withSubExecutor(executor)
        .build();
        
        // Process tasks
        processor.process(workload);
        
        // Wait until done
        int expected = workItems * tasks;
        EngineTestUtils.waitOnExecutorTrackerWorkItem(workItems, logger); 
        logger.info("Displaying ExecutorServiceTracker:\n\n'{}'\n\n",
            FutureTrackers.WORK_ITEM_TRACKER.getIds()
        );
        
        // Evaluate test status
        nProcessed = EngineTestUtils.countNonActive(workload);
        if ( nProcessed == expected ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Multiple WorkItems Test ================\n");
    }
    
    
    
    /**
     * Checks the case for larger dataset 25 * 13 = 325 tasks,
     *  7 WI threads, 3 IT threads (21). WI & IT matches thread count
     * <br><br>
     *  - ___ of 5 all passed, ~__ tasks per Item errored out
     */
    @Test
    @Order(4)
    public void canWaitWithMultipleItemTaskLarger() {
    
        // Initialize test
        logger.info("\n\n================ Multiple WorkItems Test ================\n");
        int nProcessed = 0, workItems = 2, tasks = 3;
        boolean assertionState;
        TaskTideWorkerUnitProvider unitProvider;
        ExecutorService executorService;
        TaskTideEngineObserver<WorkItem> observer;
        TaskTideProcessor<WorkItem> processor;
        TaskTideExecutor<WorkItem> executor;
        List<WorkItem> workload;
        
        // Configure executor service for work items, and item tasks
        int nWorkItemThreads = 2, nItemTaskThreads = 3;
        TaskTideExecutorServiceProvider.initialize(nWorkItemThreads, nItemTaskThreads);
        executorService = TaskTideExecutorServiceProvider.workItemExecutorService();
        
        // Configure builders, task tracker and workload
        unitProvider = new TaskTideWorkerUnitProvider();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, workItems, tasks);
        
        // Configure observer
        observer = unitProvider.getWorkItemObsBuilder()
            .withMaxTime(1000000)
        .build();
        
        // Configure executor
        executor = unitProvider.getWorkItemExecBuilder()
            .withWorkItemObserver(observer)
            .withSubThreads(1)
            .withSubTaskThreshold(7)
        .build();
        
        // Configure processor
        processor = unitProvider.getWorkItemProcBuilder()
            .withExecutorService(executorService)
            .withSubExecutor(executor)
        .build();
        
        // Process tasks
        processor.process(workload);
        
        // Wait until done:
        int expected = workItems * tasks;
        EngineTestUtils.waitOnExecutorTrackerWorkItem(workItems, logger);
        logger.info("Displaying ExecutorServiceTracker:\n\n'{}'\n\n",
            FutureTrackers.WORK_ITEM_TRACKER.getIds()
        );
        
        // Evaluate test status
        nProcessed = EngineTestUtils.countNonActive(workload);
        if ( nProcessed == expected ) {
            logger.info("Processed task count '{}', matches expected '{}'", nProcessed, expected);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", nProcessed, expected);
            assertionState = false;
        }
        
        // Display trackers
        logger.info("Displaying task tracker:\n\n{}\n\n", TaskTrackers.WORK_ITEM_TRACKER.toString());
        logger.info("Displaying WorkItem tracker:\n\n{}\n\n", FutureTrackers.WORK_ITEM_TRACKER.toString());
        logger.info("Displaying ItemTask tracker:\n\n{}\n\n", FutureTrackers.ITEM_TASK_TRACKER.toString());
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String template = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", expected, nProcessed);
        assertTrue(assertionState, template);
        logger.info("\n\n================ Multiple WorkItems Test ================\n");
    }
}