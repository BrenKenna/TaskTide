/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.workerunitprovider;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
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
import org.junit.jupiter.api.TestInstance;

import org.junit.Rule;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.testcontainers.containers.GenericContainer;
import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.supporting.JsonUtils;

import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.trackers.TaskTracker;
import org.tasktide.engine.trackers.TaskTrackers;

import org.tasktide.engine.wokerunit.provider.ItemTaskObserverBuilder;
import org.tasktide.engine.wokerunit.provider.WorkItemObserverBuilder;
import org.tasktide.engine.executor.ItemTaskExecutor;
import org.tasktide.engine.executor.WorkItemExecutor;

import org.tasktide.engine.wokerunit.provider.WorkItemExecutorBuilder;
import org.tasktide.engine.wokerunit.provider.ItemTaskExecutorBuilder;


/**
 * Test module for {@link WorkItem}/{@link ItemTask} processing through their builders
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExecutorBuilderTests {
    
    private static final Logger logger = LogManager.getLogger(ExecutorBuilderTests.class);
    private SeContainer container;
    private Template template;
    
    // CouchDB container
    @Rule
    public GenericContainer<?> couchDB = (GenericContainer<?>) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public ExecutorBuilderTests() {}
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating ExecutorBuilder Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ExecutorBuilder Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
        couchDB.stop();
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
        WorkItem task;
        List<ItemTask> workload;
        
        // Configure requirements
        obsBuilder = new ItemTaskObserverBuilder();
        execBuilder = new ItemTaskExecutorBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 2);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        logger.info("Displaying reference record:\n'{}'", JsonUtils.toJson(true, task));
        TaskTideServiceManager.fetchWorkItemService().appendModel(task);
        
        // Build Observer
        obs = obsBuilder
            .withWorkload(workload)
            .withMaxTime(10000000)
        .build();
        
        // Build executor
        executor = execBuilder
            .withObserver(obs)
        .build();
        
        // Process workload
        executor.runTasks(workload);
        
        // Check task tracker
        assertionState = TaskTrackers.ITEM_TASK_TRACKER.taskCount() > 0;
        logger.info(
      "Tracked '{}' ItemTasks displaying state of first:\t'{}'",
            TaskTrackers.ITEM_TASK_TRACKER.taskCount(), TaskTrackers.ITEM_TASK_TRACKER.get(workload.get(0).getId())
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
        WorkItem task;
        
        // Configure requirements
        obsBuilder = new WorkItemObserverBuilder();
        execBuilder = new WorkItemExecutorBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4);
        
        // Build observers
        obs = obsBuilder
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
        assertionState = TaskTrackers.ITEM_TASK_TRACKER.taskCount() > 0;
        logger.info(
      "Tracked '{}' ItemTasks displaying state of first:\t'{}'",
            TaskTrackers.ITEM_TASK_TRACKER.taskCount(), TaskTrackers.ITEM_TASK_TRACKER.get(task.getId())
        );
        logger.info("Displaying first task after processing:\n\n{}", task.toJsonDoc());
        
        // Evaluate test
        assertTrue(assertionState, "WorkItemExecutor failed processing");
        logger.info("\n\n================ Evaluate Building WorkItemExecutor ================\n");
    }
}
