/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.workerunitprovider;

import org.tasktide.engine.wokerunitprovider.WorkItemObserverBuilder;
import org.tasktide.engine.wokerunitprovider.ItemTaskObserverBuilder;
import java.util.List;
import java.util.ArrayList;

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

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.tasktracker.TaskTracker;


/**
 * Test module for {@link TaskTideEngineObserver}
 * 
 * @author bkenna
 */
public class ObserverBuilderTests {
    
    private static final Logger logger = LogManager.getLogger(ObserverBuilderTests.class);
    public ObserverBuilderTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating ObserverBuilder Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating ObserverBuilder Tests ----------------\n";
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
     * Test evaluating on start task actions
     */
    @Test
    @Order(0)
    public void canBuildWorkItemObserver() {
    
        // Initialize test
        logger.info("\n\n================ Evaluate Building WorkItemObserver ================\n");
        boolean assertionState;
        WorkItemObserverBuilder obsBuilder;
        TaskTideEngineObserver<WorkItem> obs;
        TaskTracker taskTracker;
        List<WorkItem> workload;
        
        // Configure builder requirements
        taskTracker = new TaskTracker();
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 3, 4);
    
        // Build - How needed is withWorkload really?
        obsBuilder = new WorkItemObserverBuilder();
        obs = obsBuilder
            .withWorkload(workload)
            .withTaskTracker(taskTracker)
            .withMaxTime(1000000)
        .build();
        
        // Run onStart workflow
        assertionState = obs.onTaskStart(workload.get(0));
        
        // Evaluate test
        assertTrue(assertionState, "WorkItemObserver failed onTaskStart method chain");
        logger.info("\n\n================ Evaluate Building WorkItemObserver ================\n");
    }
    
    
    /**
     * Test ItemTaskObserver
     */
    @Order(1)
    @Test
    public void canBuildItemTaskObserver() {
    
        // Initialize test
        logger.info("\n\n================ Evaluate Building ItemTaskObserver ================\n");
        boolean assertionState;
        ItemTaskObserverBuilder obsBuilder;
        TaskTideEngineObserver<ItemTask> obs;
        TaskTracker taskTracker;
        WorkItem task;
        List<ItemTask> workload;
        
        // Configure builder requirements
        taskTracker = new TaskTracker();
        obsBuilder = new ItemTaskObserverBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4);
        workload = new ArrayList<>(task.getWorkload().getWorkload().values());
        
        // Build ItemTask Observer
        obs = obsBuilder
            .withWorkload(workload)
            .withTaskTracker(taskTracker)
            .withMaxTime(100000)
        .build();
        
        // Run onStart workflow
        assertionState = obs.onTaskStart(workload.get(0));
        
        // Fetch task tracker info
        logger.info(
      "TaskTracker state following Observer pipeline for ItemTask:\t'{}'",
            taskTracker.get( workload.get(0).getId() )
        );
        
        // Evaluate test
        assertTrue(assertionState, "ItemTaskObserver failed onTaskStart method chain");
        logger.info("\n\n================ Evaluate Building WorkItemObserver ================\n");
    }
}
