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
package org.tasktide.engine.policies;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import jakarta.enterprise.inject.se.SeContainer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Rule;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.testcontainers.containers.GenericContainer;

import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.repository.RepositoryType;


/**
 * Suite of tests for the {@link TaskTideWorkloadAcquisitionPolicy}
 *  interface. Namely the following tests:
 * <br>
 * 1. Fetch ToDo work for a step
 * 2). Fetch ToDo work for a step with pilot label annotation
 * 
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkItemAcquisitionTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkItemAcquisitionTests.class);
    private final String STEP = "Ping Tests";
    
    private SeContainer container;
    private Template template;
    
    
    // CouchDB container
    @Rule
    public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkItemAcquisitionTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Acquisition Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEP, ",");
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Acquisition Tests ----------------\n";
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
     * Tests whether to do work for a step can be acquired
     *  through the {@link WorkItemAcuisitionPolicy} interface
     */
    @Test
    @Order(0)
    public void canFetchToDoWork() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Fetch ToDo Work ================\n");
        String step = "Ping Tests";
        int nExpected = 4;
        boolean assertionState;
        List<WorkItem> workload;
        TaskTideWorkloadAcquisitionPolicy<WorkItem> policy;
        
        // Build acquisition policy
        LOGGER.info("Constructing policy for target step:\t'{}'", step);
        policy = WorkItemAcquisitionPolicy
            .newInstance()
            .withTarget("Ping Tests")
            .withItemState(ItemState.TODO)
        ;
        LOGGER.info("Displaying acquisition policy resource:\n\n'{}'", policy.toJsonDoc());
        
        // Fetch workload
        LOGGER.info("Fetching workload for policy, expecting N = '{}'", nExpected);
        workload = policy.fetchWorkload();
        
        // Evaluate test
        if ( nExpected == workload.size() ) {
            LOGGER.info("Test successful found '{}' of '{}' expected records", workload.size(), nExpected);
            assertionState = true;
        }
        else {
            LOGGER.error("Test failed found '{}' of '{}' expected records", workload.size(), nExpected);
            assertionState = false;
        }
        
        // Log status
        assertTrue(assertionState, "Error cannot fetch to do work");
        LOGGER.info("\n\n================ Can Fetch ToDo Work ================\n");
    }
    
    
    /**
     * Tests whether to do work for a step can be acquired
     *  through the {@link WorkItemAcuisitionPolicy} interface
     *  using Pilot Label annotation
     */
    @Test
    @Order(0)
    public void canFetchToDoWorkPilotLabel() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Fetch ToDo Work Pilot Label ================\n");
        String step = "Ping Tests";
        int nExpected = 4;
        boolean assertionState;
        List<WorkItem> workload;
        TaskTideWorkloadAcquisitionPolicy<WorkItem> policy;
        
        // Build acquisition policy
        LOGGER.info("Constructing policy for target step:\t'{}'", step);
        policy = WorkItemAcquisitionPolicy
            .newInstance()
            .withTarget("Ping Tests")
            .withItemState(ItemState.TODO)
            .withAnno(
                "Pilot Label",
                "Early Task Binding Semantics"
            )
        ;
        LOGGER.info("Displaying acquisition policy resource:\n\n'{}'", policy.toJsonDoc());
        
        // Fetch workload
        LOGGER.info("Fetching workload for policy, expecting N = '{}'", nExpected);
        workload = policy.fetchWorkload();
        
        // Evaluate test
        if ( nExpected == workload.size() ) {
            LOGGER.info("Test successful found '{}' of '{}' expected records", workload.size(), nExpected);
            assertionState = true;
        }
        else {
            LOGGER.error("Test failed found '{}' of '{}' expected records", workload.size(), nExpected);
            assertionState = false;
        }
        
        // Log status
        assertTrue(assertionState, "Error cannot fetch to do work");
        LOGGER.info("\n\n================ Can Fetch ToDo Work Pilot Label ================\n");
    }
}