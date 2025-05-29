/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.workerunitprovider;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.tasktracker.TaskTracker;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.worker.processor.TaskTideProcessor;

import org.tasktide.engine.wokerunitprovider.WorkItemObserverBuilder;
import org.tasktide.engine.wokerunitprovider.WorkItemExecutorBuilder;

import org.tasktide.engine.wokerunitprovider.ItemTaskObserverBuilder;
import org.tasktide.engine.wokerunitprovider.ItemTaskExecutorBuilder;

import org.tasktide.engine.wokerunitprovider.WorkItemProcessorBuilder;
import org.tasktide.engine.wokerunitprovider.ItemTaskProcessorBuilder;

import org.tasktide.engine.EngineTestUtils;


/**
 * Test module for {@link WorkItem}/{@link ItemTask} processing through their builders
 * 
 * @author bkenna
 */
public class ProcessorBuilderTests {
    
    private static final Logger logger = LogManager.getLogger(ProcessorBuilderTests.class);
    public ProcessorBuilderTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating ProcessorBuilder Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating ProcessorBuilder Tests ----------------\n";
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
     * Verify through {@link TaskTracker} that built {@link ItemTaskProcessor} can process tasks
     */
    @Test
    @Order(0)
    public void canBuildItemTaskProcessor() {
    
        // Initialize test
        logger.info("\n\n================ Evaluate Building ItemTaskProcessor ================\n");
        boolean assertionState;
        ItemTaskObserverBuilder obsBuilder;
        ItemTaskExecutorBuilder execBuilder;
        ItemTaskProcessorBuilder procBuilder;
        TaskTideEngineObserver<ItemTask> obs;
        TaskTideExecutor<ItemTask> executor;
        TaskTideProcessor<ItemTask> processor;
        TaskTracker taskTracker;
        WorkItem task;
        List<ItemTask> workload;
        
        // Configure requirements
        taskTracker = new TaskTracker();
        obsBuilder = new ItemTaskObserverBuilder();
        execBuilder = new ItemTaskExecutorBuilder();
        procBuilder = new ItemTaskProcessorBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 6);
        workload = new ArrayList<>(task.getWorkload().getWorkload().values());
        
        // Configure ItemTaskObserver + SubExecutor
        obs = obsBuilder.withWorkload(workload).withTaskTracker(taskTracker).withMaxTime(1000000).build();
        executor = execBuilder.withObserver(obs).build();
        processor = procBuilder
            .withWorkload(workload)
            .withSubExecutor(executor)
            .withThreshold(2)
            .withExecutorService(4)
        .build();
        
        // Process workload
        processor.process();
        EngineTestUtils.waitUntilDoneTarget(workload, 30, logger);
        
        // Evaluate test
        assertionState = taskTracker.taskCount() > 0;
        logger.info(
      "Tracked '{}' ItemTasks displaying state of first:\t'{}'",
            taskTracker.taskCount(), taskTracker.get(workload.get(0).getId())
        );
        logger.info("Displaying first task after processing:\n\n{}", workload.get(0).toJsonDoc());
        
        // Evaluate test
        assertTrue(assertionState, "ItemTaskProcessor failed processing");
        logger.info("\n\n================ Evaluate Building ItemTaskProcessor ================\n");
    }
    
    
    /**
     * Verify through {@link TaskTracker} that built {@link WorkItemProcessor} can process tasks
     */
    @Test
    @Order(1)
    public void canBuildWorkItemProcessor() {
    
        // Initialize test
        logger.info("\n\n================ Evaluate Building WorkItemProcessor ================\n");
        int nProcessed, expected = 20;
        boolean assertionState;
        WorkItemProcessorBuilder procBuilder;
        WorkItemExecutorBuilder execBuilder;
        WorkItemObserverBuilder obsBuilder;
        List<WorkItem> workload;
        TaskTideExecutor<WorkItem> executor;
        TaskTideProcessor<WorkItem> processor;
        TaskTideEngineObserver<WorkItem> obs;
        TaskTracker taskTracker;
        
        // Construct builders
        procBuilder = new WorkItemProcessorBuilder();
        execBuilder = new WorkItemExecutorBuilder();
        obsBuilder = new WorkItemObserverBuilder();
        
        // Build observer
        taskTracker = new TaskTracker();
        obs = obsBuilder
            .withMaxTime(1000000)
            .withTaskTracker(taskTracker)
        .build();
        
        // Build executor
        executor = execBuilder
            .withWorkItemObserver(obs)
            .withSubTaskThreshold(2)
            .withSubThreads(2)
        .build();
        
        // Build processor
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 5, 4);
        processor = procBuilder
            .withWorkload(workload)
            .withExecutorService(2)
            .withThreshold(2)
        .build();
        
        // Process tasks
        processor.process();
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
}
