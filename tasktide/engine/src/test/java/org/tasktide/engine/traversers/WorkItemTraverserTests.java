/*
 * Copyright 2026 Bren.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.engine.traversers;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import jakarta.nosql.Template;
import jakarta.enterprise.inject.se.SeContainer;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;

// import org.junit.Rule;
// import org.testcontainers.containers.GenericContainer;

import org.tasktide.engine.TestUtils;
import org.tasktide.engine.TestEnvironment;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;
import org.tasktide.engine.policies.AcquisitionPolicyMode;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;


/**
 * Test module for the {@link ItemTaskTraverser}
 *
 * @author Bren
 */
@Tag("integration-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkItemTraverserTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkItemTraverserTests.class);
    
    private String WORKFLOW = "Traverser Tests";
    private final String STEP = "WorkItem Traverser Tests";
    
    private WorkerUnitContainer workerUnit;
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkItemTraverserTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Traverser Tests ----------------\n";
        LOGGER.info(msg);
        TestUtils.initSeContainer();
        
        TestUtils.createWorkflow(this.WORKFLOW);
        TestUtils.createStep(this.STEP, this.WORKFLOW);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEP, "|", ",");
        
        this.workerUnit = TestUtils.configureNewWorkerUnitContainer();
        try {
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(1, 1);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, -1);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, -1);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
        }
        catch ( TaskTideEngineCheckedException ex ) {}
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Traverser Tests ----------------\n";
        LOGGER.info(msg);
        //couchDB.stop();
    }
    
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }
    
    
    /**
     * Tests that the {@link WorkItemTraverser} can process tasks
     * 
     */
    @Test
    @Order(0)
    public void traverserCanProcessWorkItem() {
        
        // Configure test
        LOGGER.info("\n\n================ WorkItem Traverser Can Process Tasks ================\n");
        boolean assertionState;
        List<WorkItem> workload;
        WorkItem task;
        int nWorkers = 1, nItemTaskThredas = 1;
        TaskTideWorkloadTraverser<WorkItem> traverser;
        
        // Build acquisition policy
        LOGGER.info("Configuring Travser for target step:\t'{}'", this.STEP);
        traverser = TestUtils.getTraverser(nWorkers, nItemTaskThredas);
        workload = TestUtils.fetchTargetedWorkload(this.STEP);
        task = workload.get(0);
        
        // Try process first task
        LOGGER.info("Processing workload from '{}'", task.getId());
        try {
            assertionState = traverser.processElm(task);
            LOGGER.info("Processing complete with state '{}' for:\t'{}'", assertionState, task.getId());
        }
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error during WorkItem processing:\t'{}'\n\n{}", task.getId(), ex);
            assertionState = false;
        }
        
        // Evaluate test
        Assertions.assertTrue(assertionState, "Error could not traverse WorkItem");
        LOGGER.info("\n\n================ WorkItem Traverser Can Process Tasks ================\n");
    }
    
    
    /**
     * Tests {@link WorkItemTraverser} serial traversal
     * 
     */
    @Test
    @Order(1)
    public void canTraverseWorkloadSerial() {
    
        // Configure test
        LOGGER.info("\n\n================ Can Traverse WorkItem Serial ================\n");
        boolean assertionState;
        List<WorkItem> workload;
        TaskTideWorkloadTraverser<WorkItem> traverser;
        int nWorkers = 1, nItemTaskThredas = 1;
        
        // Build acquisition policy
        LOGGER.info("Configuring Travser for target step:\t'{}'", this.STEP);
        traverser = TestUtils.getTraverser(nWorkers, nItemTaskThredas);
        workload = TestUtils.fetchTargetedWorkload(this.STEP);
        if ( workload.size() > 2 ) {
            workload = workload.subList(0, 1);
        }
        try {
            
            // Process task
            traverser.traverse(workload);
            LOGGER.info("Traversal complete");
            assertionState = true;
        }
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error during Serial WorkItem Traverseral:\n\n'{}'", ex);
            assertionState = false;
        }
        
        // Log status
        Assertions.assertTrue(assertionState, "Error could not traverse WorkItem");
        LOGGER.info("\n\n================ Can Traverse WorkItem Serial ================\n");
    }
    
    
    /**
     * Tests {@link WorkItemTraverser} parallel traversal
     * 
     */
    @Test
    @Order(2)
    public void canTraverseWorkloadParallel() {
    
        // Configure test
        LOGGER.info("\n\n================ Can Traverse WorkItem Parallel ================\n");
        boolean assertionState;
        List<WorkItem> workload;
        int nWorkers = 2, nItemTaskThredas = 2;
        TaskTideWorkloadTraverser<WorkItem> traverser;
        
        // Build acquisition policy
        LOGGER.info("Configuring Travser for target step:\t'{}'", this.STEP);
        traverser = TestUtils.getTraverser(nWorkers, nItemTaskThredas);
        workload = TestUtils.fetchTargetedWorkload(this.STEP);
        if ( workload.size() > 2 ) {
            workload = workload.subList(0, 1);
        }
        try {
            
            // Process task
            traverser.traverse(workload);
            LOGGER.info("Traversal complete");
            assertionState = true;
        }
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error during Serial WorkItem Traverseral:\n\n'{}'", ex);
            assertionState = false;
        }
        
        // Log status
        Assertions.assertTrue(assertionState, "Error could not traverse WorkItem");
        LOGGER.info("\n\n================ Can Traverse WorkItem Parallel ================\n");
    }
}