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
package org.tasktide.engine.worker;

import org.tasktide.engine.EngineWorkerTestUtils;
import jakarta.enterprise.inject.se.SeContainer;

import jakarta.nosql.Template;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.engine.TestUtils;

import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;

import org.tasktide.engine.policies.AcquisitionPolicyMode;
import org.tasktide.engine.policies.WorkerExecutionPolicy;

import org.tasktide.engine.policies.workflow.WorkflowAcquisitionStrategy;
import org.tasktide.engine.policies.workflow.WorkflowStrategyMode;
import org.tasktide.engine.policies.workflow.WorkflowStrategyType;


/**
 * Suite of tests against {@link TaskTideEngineWorker} specifically
 *  towards {@link WorkflowAcquisitionStrategy}
 *
 * @author Bren
 */
@Tag("system-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkflowEngineWorkerTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TargetedEngineWorkerTests.class);
    
    private final String WORKFLOW = "EngineWorker Workflow Acquisition Test";
    private final String STEPS = "Dig,NS Lookups,Numero 3";
    
    private final AcquisitionPolicyMode POLICY_MODE = AcquisitionPolicyMode.WORKFLOW;

    //private final ItemStoreType storeType = ItemStoreType.SQLITE;
    //private final String storeName = "TaskTideRepo/SQLITE";
    
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkflowEngineWorkerTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Engine Worker Workflow Tests ----------------\n";
        LOGGER.info(msg);
        TestUtils.initSeContainer();
        
        TestUtils.createWorkflow(this.WORKFLOW);
        for ( String elm : this.STEPS.split(",") ) {
            TestUtils.createStep(elm, this.WORKFLOW);
        }
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Engine Worker Workflow Tests----------------\n";
        LOGGER.info(msg);
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
     * Tests running engine in batch mode with using the
     *  Sequential Scanner of targeted workflow
     * 
     */
    @Test
    @Order(0)
    public void canRunEngineBatchSequentialScannerMode() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Batch Sequential Scanner Workflow Worker =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.SCANNER;
        int ITERATION_LIMIT = 1;
        int RESULT_SET_LIMIT = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("nested-dig-tasks.txt", this.STEPS.split(",")[0], "|", ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS.split(",")[1], "|", ",");
        
        // Fetch engine worker
        LOGGER.info(
            "Configuring engine worker for:\t'{}' '{}' mode, with iteration limit",
            stratType,
            stratMode
        );
        LOGGER.info(
            "Scanner Iteration Limit = '{}', TaskTide-Service-Manager Result Set Limit = '{}'",
            ITERATION_LIMIT, RESULT_SET_LIMIT
        );
        TaskTideServiceManager.setResultSetSize(RESULT_SET_LIMIT);
        TestUtils.resetWorkerContainers();
        worker = EngineWorkerTestUtils.getEngineWorker(this.POLICY_MODE, this.STEPS, stratType, stratMode, ITERATION_LIMIT);
        
        // Run engine
        LOGGER.info(
            "Running '{}' '{}' of workflow in '{}' mode",
            stratType, stratMode, executionPolicy
        );
        try {
            worker.runEngine(executionPolicy);
        }
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error(
                "Error running '{}' '{}' of workflow in '{}' mode:\n\n'{}'",
                stratType, stratMode, executionPolicy, ex
            );
        }
        LOGGER.info("Engine processing completed");
        
        // Log complete
        LOGGER.info("\n\n================ Can Run Batch Sequential Scanner Workflow Worker =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  Round Robin Scanner of targeted workflow
     * 
     */
    @Test
    @Order(1)
    public void canRunEngineBatchRoundRobinScannerMode() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Batch Round Robin Scanner Workflow Worker =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.ROUND_ROBIN;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.SCANNER;
        int ITERATION_LIMIT = 4;
        int RESULT_SET_LIMIT = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("nested-dig-tasks.txt", this.STEPS.split(",")[0], "|", ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS.split(",")[1], "|", ",");
        
        // Fetch engine worker
        LOGGER.info(
            "Configuring engine worker for:\t'{}' '{}' mode, with iteration limit",
            stratType,
            stratMode
        );
        LOGGER.info(
            "Scanner Iteration Limit = '{}', TaskTide-Service-Manager Result Set Limit = '{}'",
            ITERATION_LIMIT, RESULT_SET_LIMIT
        );
        TestUtils.resetWorkerContainers();
        TaskTideServiceManager.setResultSetSize(RESULT_SET_LIMIT);
        worker = EngineWorkerTestUtils.getEngineWorker(this.POLICY_MODE, this.STEPS, stratType, stratMode, ITERATION_LIMIT);
        
        // Run engine
        LOGGER.info(
            "Running '{}' '{}' of workflow in '{}' mode",
            stratType, stratMode, executionPolicy
        );
        try {
            worker.runEngine(executionPolicy);
        }
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error(
                "Error running '{}' '{}' of workflow in '{}' mode:\n\n'{}'",
                stratType, stratMode, executionPolicy, ex
            );
        }
        LOGGER.info("Engine processing completed");
        
        // Log complete
        LOGGER.info("\n\n================ Can Run Batch Round Robin Scanner Workflow Worker =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  {@link WorkflowStrategyType.ROUND_ROBIN} {@link WorkflowStrategyMode.SCANNER}
     *  of targeted workflow
     * 
     */
    @Test
    @Order(2)
    public void canRunEngineBatchRoundRobinScannerMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Batch Round Robin Scanner Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.ROUND_ROBIN;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.SCANNER;
        int ITERATION_LIMIT = 4,
            RESULT_SET_LIMIT = 1,
            WINDOW_SIZE = 4,
        POOL_SIZE = 2;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("nested-dig-tasks.txt", this.STEPS.split(",")[0], "|", ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS.split(",")[1], "|", ",");
        
        // Fetch engine worker
        LOGGER.info(
            "Configuring engine worker for:\t'{}' '{}' mode, with iteration limit",
            stratType,
            stratMode
        );
        LOGGER.info(
            "Scanner Iteration Limit = '{}', TaskTide-Service-Manager Result Set Limit = '{}'",
            ITERATION_LIMIT, RESULT_SET_LIMIT
        );
        TestUtils.resetWorkerContainers();
        TaskTideServiceManager.setResultSetSize(RESULT_SET_LIMIT);
        worker = EngineWorkerTestUtils.getEngineWorker(
            this.POLICY_MODE, this.STEPS, stratType, stratMode,
            POOL_SIZE, WINDOW_SIZE, ITERATION_LIMIT
        );
        
        // Run engine
        LOGGER.info(
            "Running '{}' '{}' of workflow in '{}' mode",
            stratType, stratMode, executionPolicy
        );
        try {
            worker.runEngine(executionPolicy);
        }
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error(
                "Error running '{}' '{}' of workflow in '{}' mode:\n\n'{}'",
                stratType, stratMode, executionPolicy, ex
            );
        }
        LOGGER.info("Engine processing completed");
        
        // Log complete
        LOGGER.info("\n\n================ Can Run Parallel Batch Round Robin Scanner Workflow Workers =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  {@link WorkflowStrategyType.SEQUENTIAL} {@link WorkflowStrategyMode.SCANNER}
     *  of targeted workflow
     * 
     */
    @Test
    @Order(3)
    public void canRunEngineBatchSequentialScannerMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Batch Sequential Scanner Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.SCANNER;
        int ITERATION_LIMIT = 6,
            RESULT_SET_LIMIT = 1,
            WINDOW_SIZE = 4,
        POOL_SIZE = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("nested-dig-tasks.txt", this.STEPS.split(",")[0], "|", ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS.split(",")[1], "|", ",");
        
        // Fetch engine worker
        LOGGER.info(
            "Configuring engine worker for:\t'{}' '{}' mode, with iteration limit",
            stratType,
            stratMode
        );
        LOGGER.info(
            "Scanner Iteration Limit = '{}', TaskTide-Service-Manager Result Set Limit = '{}'",
            ITERATION_LIMIT, RESULT_SET_LIMIT
        );
        TestUtils.resetWorkerContainers();
        TaskTideServiceManager.setResultSetSize(RESULT_SET_LIMIT);
        worker = EngineWorkerTestUtils.getEngineWorker(
            this.POLICY_MODE, this.STEPS, stratType, stratMode,
            POOL_SIZE, WINDOW_SIZE, ITERATION_LIMIT
        );
        
        // Run engine
        LOGGER.info(
            "Running '{}' '{}' of workflow in '{}' mode",
            stratType, stratMode, executionPolicy
        );
        try {
            worker.runEngine(executionPolicy);
        }
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error(
                "Error running '{}' '{}' of workflow in '{}' mode:\n\n'{}'",
                stratType, stratMode, executionPolicy, ex
            );
        }
        LOGGER.info("Engine processing completed");
        
        // Log complete
        LOGGER.info("\n\n================ Can Run Parallel Batch Sequential Scanner Workflow Workers =================\n");
    }
}