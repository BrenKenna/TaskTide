/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.engine.worker.processor;

import org.tasktide.engine.deprecated_processor.TaskTideProcessor;
import org.tasktide.engine.deprecated_processor.WorkItemProcessor;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;

import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.Rule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.TestInstance;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.generator.ExampleGenerators;
import org.tasktide.core.manager.generator.TaskGenerator;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.EngineTestUtils;
import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;
import org.testcontainers.containers.GenericContainer;


/**
 * Test module for the {@link TaskTideProcessor} {@link TaskTideWorkerUnit}
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkItemProcessorTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemProcessorTests.class);
    private SeContainer container;
    private Template template;
    
    // CouchDB container
    @Rule
    public GenericContainer<?> couchDB = (GenericContainer<?>) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkItemProcessorTests() {}
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Processor Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Processor Tests ----------------\n";
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
     * Test that Tasks can be processed. Processing across groups in parallel
     */
    @Test
    @Order(0)
    public void canRunTasks() {
    
        // Initialize test
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
        int nTasks = 16, processed;
        boolean assertionState;
        List<WorkItem> workload;
        TaskTideProcessor<WorkItem> workItemProcessor;
        
        // Make test workload
        logger.info("Configuring workload and WorkItemProcessor");
        workload = TaskGenerator.generateExampleWorkItem(ExampleGenerators.PING, 4, 4);
        TaskTideServiceManager.fetchWorkItemService().extendModel(workload);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        workItemProcessor = new WorkItemProcessor(executorService);
        
        // Process work items
        logger.info("Processing workload of N Items = '{}'", workload.size());
        workItemProcessor.process(workload);
        EngineTestUtils.waitUntilDoneWorkItem(workload, 30, logger);
        
        
        // Evaluate test status
        processed = EngineTestUtils.countNonActive(workload);
        if ( processed == nTasks ) {
            logger.info("Processed task count '{}', matches expected '{}'", processed, nTasks);
            assertionState = true;
        }
        else {
            logger.error("Processed task count '{}', does not match expected '{}'", processed, nTasks);
            assertionState = false;
        }
        
        // Log test status
        logger.info("-------- Displaying Processed WorkItems --------");
        workload.stream().forEach( elm -> logger.info(elm.toJsonDoc()) );
        logger.info("-------- Displaying Execution Time Summary --------");
        EngineTestUtils.fetchExecutionTimesWorkItem(workload, logger);
        String tmp = String.format("Not all tasks processed correctl:\tTotal = '%d', Processed = '%d'", nTasks, processed);
        assertTrue(assertionState, tmp);
        logger.info("\n\n================ Can Process WorkItems Test ================\n");
    }
}