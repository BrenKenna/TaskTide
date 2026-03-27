/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.workerunitprovider;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
import org.tasktide.engine.wokerunit.provider.WorkItemObserverBuilder;
import org.tasktide.engine.wokerunit.provider.ItemTaskObserverBuilder;

import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;

import org.tasktide.engine.observer.TaskTideEngineObserver;
import org.tasktide.engine.trackers.TaskTrackers;
import org.testcontainers.containers.GenericContainer;


/**
 * Test module for {@link TaskTideEngineObserver}
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ObserverBuilderTests {
    
    private static final Logger logger = LogManager.getLogger(ObserverBuilderTests.class);
    private SeContainer container;
    private Template template;
    
    // CouchDB container
    @Rule
    public GenericContainer<?> couchDB = (GenericContainer<?>) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public ObserverBuilderTests() {}
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating ObserverBuilder Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ObserverBuilder Tests ----------------\n";
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
        List<WorkItem> workload;
        
        // Configure builder requirements
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 3, 4);
        TaskTideServiceManager.fetchWorkItemService().extendModel(workload);
    
        // Build - How needed is withWorkload really?
        obsBuilder = new WorkItemObserverBuilder();
        obs = obsBuilder
            .withWorkload(workload)
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
    @Test
    @Order(1)
    public void canBuildItemTaskObserver() {
    
        // Initialize test
        logger.info("\n\n================ Evaluate Building ItemTaskObserver ================\n");
        boolean assertionState;
        ItemTaskObserverBuilder obsBuilder;
        TaskTideEngineObserver<ItemTask> obs;
        WorkItem task;
        List<ItemTask> workload;
        
        // Configure builder requirements
        obsBuilder = new ItemTaskObserverBuilder();
        task = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4);
        workload = new ArrayList<>(task.getWorkload().getTaskMap().values());
        
        // Build ItemTask Observer
        obs = obsBuilder
            .withWorkload(workload)
            .withMaxTime(100000)
        .build();
        
        // Run onStart workflow
        assertionState = obs.onTaskStart(workload.get(0));
        
        // Fetch task tracker info
        logger.info(
      "TaskTracker state following Observer pipeline for ItemTask:\t'{}'",
            TaskTrackers.WORK_ITEM_TRACKER.get( workload.get(0).getId() )
        );
        
        // Evaluate test
        assertTrue(assertionState, "ItemTaskObserver failed onTaskStart method chain");
        logger.info("\n\n================ Evaluate Building WorkItemObserver ================\n");
    }
}
