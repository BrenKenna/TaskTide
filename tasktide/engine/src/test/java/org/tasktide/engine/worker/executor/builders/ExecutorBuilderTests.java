/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.worker.executor.builders;

import org.tasktide.engine.wokerunitprovider.WorkItemExecutorBuilder;
import org.tasktide.engine.wokerunitprovider.ItemTaskExecutorBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.engine.worker.executor.TaskTideExecutor;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.tasktracker.TaskTracker;

import org.tasktide.engine.wokerunitprovider.ItemTaskObserverBuilder;
import org.tasktide.engine.wokerunitprovider.WorkItemObserverBuilder;


/**
 * Test module for {@link WorkItem}/{@link ItemTask} processing through their builders
 * 
 * @author bkenna
 */
public class ExecutorBuilderTests {
    
    private static final Logger logger = LogManager.getLogger(ExecutorBuilderTests.class);
    public ExecutorBuilderTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating ExecutorBuilder Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating ExecutorBuilder Tests ----------------\n";
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
     * Verify through {@link TaskTracker} that built {@link ItemTaskExecutor} can execute tasks
     */
    @Test
    @Order(0)
    public void canBuildItemTaskExecutor() {
    
        // Initialize test
        logger.info("\n\n================ Evaluate Building ItemTaskExecutor ================\n");
        boolean assertionState;
        ItemTaskObserverBuilder obsBuilder;
        ItemTaskExecutorBuilder execBuilder;
        TaskTideEngineObserver<ItemTask> obs;
        TaskTideExecutor<ItemTask> executor;
        TaskTracker taskTracker;
        WorkItem task;
        List<ItemTask> workload;
        
        // Configure requirements
        taskTracker = new TaskTracker();
        obsBuilder = new ItemTaskObserverBuilder();
        execBuilder = new ItemTaskExecutorBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 2);
        workload = new ArrayList<>(task.getWorkload().getWorkload().values());
        
        // Build Observer
        obs = obsBuilder
            .withWorkload(workload)
            .withTaskTracker(taskTracker)
            .withMaxTime(10000000)
        .build();
        
        // Build executor
        executor = execBuilder
            .withObserver(obs)
        .build();
        
        // Process workload
        executor.runTasks(workload);
        
        // Check task tracker
        assertionState = taskTracker.taskCount() > 0;
        logger.info(
      "Tracked '{}' ItemTasks displaying state of first:\t'{}'",
            taskTracker.taskCount(), taskTracker.get(workload.get(0).getId())
        );
        logger.info("Displaying first task after processing:\n\n{}", workload.get(0).toJsonDoc());
        
        // Evaluate test
        assertTrue(assertionState, "ItemTaskExecutor failed processing");
        logger.info("\n\n================ Evaluate Building ItemTaskExecutor ================\n");
    }
    
    
    /**
     * Verify through {@link TaskTracker} that built {@link WorkItemExecutor} can execute work
     */
    @Test
    @Order(1)
    public void canBuildWorkItemExecutor() {
    
        // Initialize requierd variables
        logger.info("\n\n================ Evaluate Building WorkItemExecutor ================\n");
        boolean assertionState;
        WorkItemObserverBuilder obsBuilder;
        WorkItemExecutorBuilder execBuilder;
        
        TaskTideEngineObserver<WorkItem> obs;
        TaskTideExecutor<WorkItem> executor;
        TaskTracker taskTracker;
        WorkItem task;
        
        // Configure requirements
        taskTracker = new TaskTracker();
        obsBuilder = new WorkItemObserverBuilder();
        execBuilder = new WorkItemExecutorBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4);
        
        // Build observers
        obs = obsBuilder
            .withTaskTracker(taskTracker)
            .withMaxTime(1000000)
        .build();

        // Build WorkItemExecutor builder
        executor = execBuilder
            .withWorkItemObserver(obs)
            .withSubTaskThreshold(2)
            .withSubThreads(2)
        .build();
        
        // Process work
        List<WorkItem> data = List.of(task); 
        executor.runTasks(data);
        try {
            TimeUnit.SECONDS.sleep(65L);
        }
        catch ( Exception ex ) {logger.warn("Interupt exception encountered while waiting for 1min");}
        
        // Evaluate processing
        assertionState = taskTracker.taskCount() > 0;
        logger.info(
      "Tracked '{}' ItemTasks displaying state of first:\t'{}'",
            taskTracker.taskCount(), taskTracker.get(task.getId())
        );
        logger.info("Displaying first task after processing:\n\n{}", task.toJsonDoc());
        
        // Evaluate test
        assertTrue(assertionState, "WorkItemExecutor failed processing");
        logger.info("\n\n================ Evaluate Building WorkItemExecutor ================\n");
    }
}
