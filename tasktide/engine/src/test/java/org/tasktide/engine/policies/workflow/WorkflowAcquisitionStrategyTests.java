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
package org.tasktide.engine.policies.workflow;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Assertions;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.TestEnvironment;
import org.tasktide.engine.TestUtils;

import org.tasktide.engine.policies.TargetedAcquisitionPolicy;
import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;


/**
 * Suite of tests for {@link WorkflowAcquisitionStrategy}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowAcquisitionStrategyTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkflowAcquisitionStrategyTests.class);
    private final String[] STEPS = { "Ping Tests", "Nslookup Tests" };
    
    
    private SeContainer container;
    private Template template;
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkflowAcquisitionStrategyTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Workflow Acquisition Strategy Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS[0], ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS[1], "|", ",");
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Workflow Acquisition Tests ----------------\n";
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
     * Tests steps in workflow can be consumed sequentially
     * 
     */
    @Test
    @Order(0)
    public void canFetchSequential() {
        
        // Initialize test
        LOGGER.info("\n\n=============== Can Fetch Workload Sequentially ================\n");
        List<WorkItem> workload;
        TaskTideWorkloadAcquisitionPolicy policy;
        WorkflowAcquisitionStrategy strat;
        WorkItem workItem;
        List<TaskTideWorkloadAcquisitionPolicy> policies = new ArrayList<>();
        
        // Initialize
        LOGGER.info("Initializing '{}' workflow strategy", WorkflowStrategyType.SEQUENTIAL);
        strat = WorkflowStrategyType.getStrategy(false);
        
        // Configure acquisition policies
        LOGGER.info("Configuring acqusition policies for steps");
        for ( String step : STEPS ) {
            policy = TargetedAcquisitionPolicy.newInstance()
                .withTarget(step)
                .withItemState(ItemState.TODO)
                .withWindowSize(100)
            .build();
            policies.add(policy);
        }
        
        // Fetch workload
        LOGGER.info("Fetching first batch");
        workload = strat.fetchWorkload(policies);
        
        // Verify that the step is expected
        LOGGER.info(
            "Examining first workload of size '{}' matches the expected target '{}'",
            workload.size(),
            policies.get(0).getTarget()
        );
        workItem = workload.get(0);
        Assertions.assertTrue(policies.get(0).getTarget().equals(workItem.getCollection()), "Error, active target and workload step do no match");
        
        // Process workload
        LOGGER.info("Marking tasks completed, to enqueue next run");
        workload
           .forEach( elm -> {
               elm.setItemState(ItemState.DONE);
               TaskTideServiceManager
                    .fetchWorkItemService()
                    .updateModel(elm);
        });
        
        // Fetch workload
        LOGGER.info("Fetching next batch");
        workload = strat.fetchWorkload(policies);
        LOGGER.info("Marking tasks completed, to enqueue next run");
        workload
           .forEach( elm -> {
               elm.setItemState(ItemState.DONE);
               TaskTideServiceManager
                    .fetchWorkItemService()
                    .updateModel(elm);
        });
        
        // Verify that the step is expected
        LOGGER.info(
            "Examining second workload of size '{}' matches the expected target '{}'",
            workload.size(),
            policies.get(1).getTarget()
        );
        workItem = workload.get(0);
        Assertions.assertTrue(policies.get(1).getTarget().equals(workItem.getCollection()), "Error, next target and workload step do no match");
        
        // Verify workflow is completed
        LOGGER.info("Verifying workflow has been fully consumed");
        workload = strat.fetchWorkload(policies);
        
        // Verify that the step is expected
        LOGGER.info("Verifying retrieved workload is empty");
        Assertions.assertTrue(workload.isEmpty(), "Error, list is not empty");
        LOGGER.info("\n\n=============== Can Fetch Workload Sequentially ================\n");
    }
    
    
    /**
     * Tests steps in workflow can be consumed round robin
     * 
     */
    @Test
    @Order(1)
    public void canRoundRobin() {
        
        // Initialize test
        LOGGER.info("\n\n=============== Can Fetch Workload Round Robin ================\n");
        List<WorkItem> workload;
        TaskTideWorkloadAcquisitionPolicy policy;
        WorkflowAcquisitionStrategy strat;
        WorkItem workItem;
        List<TaskTideWorkloadAcquisitionPolicy> policies = new ArrayList<>();
        
        // Initialize
        LOGGER.info("Initializing '{}' workflow strategy", WorkflowStrategyType.ROUND_ROBIN);
        strat = WorkflowStrategyType.getStrategy(true);
        
        // Configure acquisition policies
        LOGGER.info("Configuring acqusition policies for steps");
        for ( String step : STEPS ) {
            policy = TargetedAcquisitionPolicy.newInstance()
                .withTarget(step)
                .withItemState(ItemState.TODO)
                .withWindowSize(100)
            .build();
            policies.add(policy);
        }
        
        // Fetch workload
        LOGGER.info("Fetching first batch");
        workload = strat.fetchWorkload(policies);
        
        // Verify that the step is expected
        LOGGER.info(
            "Examining first workload of size '{}' matches the expected target '{}'",
            workload.size(),
            policies.get(0).getTarget()
        );
        workItem = workload.get(0);
        Assertions.assertTrue(policies.get(0).getTarget().equals(workItem.getCollection()), "Error, active target and workload step do no match");
        
        // Process workload
        LOGGER.info("Marking tasks completed, to enqueue next run");
        workload
           .forEach( elm -> {
               elm.setItemState(ItemState.DONE);
               TaskTideServiceManager
                    .fetchWorkItemService()
                    .updateModel(elm);
        });
        
        // Fetch workload
        LOGGER.info("Fetching next batch");
        workload = strat.fetchWorkload(policies);
        LOGGER.info("Marking tasks completed, to enqueue next run");
        workload
           .forEach( elm -> {
               elm.setItemState(ItemState.DONE);
               TaskTideServiceManager
                    .fetchWorkItemService()
                    .updateModel(elm);
        });
        
        // Verify that the step is expected
        LOGGER.info(
            "Examining second workload of size '{}' matches the expected target '{}'",
            workload.size(),
            policies.get(1).getTarget()
        );
        workItem = workload.get(0);
        Assertions.assertTrue(policies.get(1).getTarget().equals(workItem.getCollection()), "Error, next target and workload step do no match");
        
        // Verify workflow is completed
        LOGGER.info("Verifying workflow has been fully consumed");
        workload = strat.fetchWorkload(policies);
        
        // Verify that the step is expected
        LOGGER.info("Verifying retrieved workload is empty");
        Assertions.assertTrue(workload.isEmpty(), "Error, list is not empty");
        LOGGER.info("\n\n=============== Can Fetch Workload Round Robin ================\n");
    }
}
