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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import org.junit.Rule;

import org.testcontainers.containers.GenericContainer;

import org.tasktide.engine.TestUtils;
import org.tasktide.engine.TestEnvironment;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.policies.WorkItemAcquisitionPolicy;



/**
 * Test module for the {@link ItemTaskTraverser}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkItemTraverserTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkItemTraverserTests.class);
    
    private final String STEP = "Ping Tests";
    
    private SeContainer container;
    private Template template;
    
    
    // CouchDB container
    @Rule
    public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkItemTraverserTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Traverser Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEP, ",");
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Traverser Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        couchDB.stop();
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
     * Fetch workload from {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @return List-{@link WorkItem}
     */
    public List<WorkItem> fetchWorkload() {
    
        // Initialize vars
        TaskTideWorkloadAcquisitionPolicy<WorkItem> policy;
        List<WorkItem> workload;
        
        // Build policy & fetch workload
        policy = WorkItemAcquisitionPolicy
            .newInstance()
            .withTarget(this.STEP)
            .withItemState(ItemState.TODO)
        ;
        workload = policy.fetchWorkload();
        
        // Return results
        return workload;
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
        TaskTideWorkloadTraverser<WorkItem> traverser;
        
        // Build acquisition policy
        LOGGER.info("Fetching workload target step:\t'{}'", this.STEP);
        workload = this.fetchWorkload();
        task = workload.get(0);
        
        // Fetch first task
        LOGGER.info("Fetching first task & configuring WorkItem Traverser");
        traverser = new WorkItemTraverser();
        
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
        
        // Build acquisition policy
        LOGGER.info("Fetching workload target step:\t'{}'", this.STEP);
        workload = this.fetchWorkload();
        
        // Traverse workload
        LOGGER.info("Performing WorkItemTraverser traversal");
        traverser = new WorkItemTraverser();
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
        ExecutorService execServ = Executors.newFixedThreadPool(3);
        TaskTideWorkloadTraverser<WorkItem> traverser;
        
        // Build acquisition policy
        LOGGER.info("Fetching workload target step:\t'{}'", this.STEP);
        workload = this.fetchWorkload();
        
        // Fetch first task
        LOGGER.info("Performing WorkItemTraverser traversal");
        traverser = new WorkItemTraverser();
        try {
            
            // Process task
            traverser.traverse(workload, execServ);
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