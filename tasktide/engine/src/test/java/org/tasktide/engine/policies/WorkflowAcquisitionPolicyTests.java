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

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;

import org.tasktide.engine.policies.workflow.WorkflowStrategyMode;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;


/**
 * Suite of tests for {@link WorkflowAcquisitionPolicy}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowAcquisitionPolicyTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkflowAcquisitionPolicyTests.class);
    private final String[] STEPS = { 
        "Step-1",
        "Step-2"
    };
    private final int RESULT_SET_SIZE = 2;
    
    private SeContainer container;
    private Template template;
    
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkflowAcquisitionPolicyTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Workflow Acquisition Strategy Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TaskTideServiceManager.setResultSetSize(this.RESULT_SET_SIZE);
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS[0], ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS[1], "|", ",");
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Initiating Workflow Acquisition Strategy Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        // couchDB.stop();
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
     * Tests processing steps of workflow with sequential exhaustion
     * 
     */
    @Test
    @Order(0)
    public void canSequentiallyProcessWorkflow() {
    
        // Initialize test
        LOGGER.info("\n\n============== Can Process Workflow With Sequential Exhaustion ================\n");
        List<WorkItem> workload;
        WorkItem workItem;
        TaskTideWorkloadAcquisitionPolicy workflowPolicy;
        
        // Configure acquisition policies
        LOGGER.info(
            "Configuring acqusition policies for workflow:\t'{}'",
            List.of(this.STEPS)
        );
        workflowPolicy = WorkflowAcquisitionPolicy
            .newInstance(
                List.of(this.STEPS),
                WorkflowStrategyType.SEQUENTIAL,
                WorkflowStrategyMode.EXHAUST
            )
            .withWindowSize(100)
        .build();
        
        // Fetch workload
        LOGGER.info("Examining first batch");
        int counter = 0;
        workload = workflowPolicy.fetchWorkload();
        while ( workflowPolicy.hasNext() ) {
            
            // Inspect
            LOGGER.info(
                "Examining first workload of size '{}' matches the expected target '{}'",
                workload.size(),
                this.STEPS[counter]
            );
            workItem = workload.get(0);
            Assertions.assertTrue(this.STEPS[counter].equals(workItem.getCollection()), "Error, active target and workload step do no match");
            
            // Process workload
            LOGGER.info("Marking tasks completed, to enqueue next run");
            workload
               .forEach( elm -> {
                   elm.setItemState(ItemState.DONE);
                   TaskTideServiceManager
                        .fetchWorkItemService()
                        .updateModel(elm);
            });
            
            // Increment counter and workload
            workload = workflowPolicy.fetchWorkload();
            counter++;
        }
        
        // Verify expected number of steps were processed
        Assertions.assertTrue(counter == STEPS.length, "Error, number of steps processed does not match expected");
        LOGGER.info("\n\n============== Can Process Workflow With Sequential Exhaustion ===============\n");
    }
    
    
    /**
     * Tests processing steps of workflow with scanning round robin
     * 
     */
    @Test
    @Order(1)
    public void canProcessWorkflowWithScanningRoundRobin() {
    
        // Initialize test
        LOGGER.info("\n\n=============== Can Process Workflow With Scanning Round Robin ================\n");
        List<WorkItem> workload;
        WorkItem workItem;
        TaskTideWorkloadAcquisitionPolicy workflowPolicy;
        
        // Configure acquisition policies
        LOGGER.info(
            "Configuring acqusition policies for workflow:\t'{}'",
            List.of(this.STEPS)
        );
        workflowPolicy = WorkflowAcquisitionPolicy
            .newInstance(
                List.of(this.STEPS),
                WorkflowStrategyType.ROUND_ROBIN,
                WorkflowStrategyMode.SCANNER
            )
            .withWindowSize(2)
        .build();
        
        // Fetch workload
        LOGGER.info("Examining first batch");
        int counter = 0;
        workload = workflowPolicy.fetchWorkload();
        while ( counter <= (this.STEPS.length * 2) ) {
            
            // Inspect
            int current = counter % this.STEPS.length;
            String activeStep = this.STEPS[current];
            LOGGER.info(
                "Examining workload of size '{}' matches the expected target '{}'",
                workload.size(),
                activeStep
            );
            
            // Handle workload size
            if ( !workload.isEmpty() ) {
                workItem = workload.get(0);
                Assertions.assertTrue(
                    activeStep.equals(workItem.getCollection()),
                    "Error, active target and workload step do no match"
                );
                
                // Process workload
                LOGGER.info("Marking tasks completed, to enqueue next run");
                workload
                   .forEach( elm -> {
                       elm.setItemState(ItemState.DONE);
                       TaskTideServiceManager
                            .fetchWorkItemService()
                            .updateModel(elm);
                });
            }
            
            else {
                LOGGER.info(
                    "No open tasks detected for '{}' in workflow:\t'{}'",
                    activeStep,
                    List.of(this.STEPS)
                );
                Assertions.assertTrue(true, "");
            }
            
            // Increment counter and workload
            workload = workflowPolicy.fetchWorkload();
            counter++;
        }
        
        // Verify expected number of steps were processed
        Assertions.assertTrue(
            (counter-1) == (STEPS.length * 2),
            "Error, number of steps processed does not match expected"
        );
        LOGGER.info("\n\n=============== Can Process Workflow With Scanning Round Robin ================\n");
    }
}
