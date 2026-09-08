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


import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.TestEnvironment;
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
@Tag("experimental-system-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExperimentalWorkflowWorker_SystemTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TargetedEngineWorkerTests.class);
    
    private final String WORKFLOW = "EngineWorker Workflow Acquisition Test";
    private final String STEPS = "Ping,NS Lookups,Numero 3";
    private final String FILE_PATH = "src/test/resources/nested-nslookup-tasks.txt";
    
    private final AcquisitionPolicyMode POLICY_MODE = AcquisitionPolicyMode.WORKFLOW;
    
    private SeContainer container;
    private Template template;
    
    //private final ItemStoreType storeType = ItemStoreType.SQLITE;
    //private final String storeName = "TaskTideRepo/SQLITE";
    
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public ExperimentalWorkflowWorker_SystemTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Engine Worker Workflow Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        
        TestUtils.createWorkflow(this.WORKFLOW);
        for ( String elm : this.STEPS.split(",") ) {
            TestUtils.createStep(elm, this.WORKFLOW);
        }
        
        // Import workload
        LOGGER.info("Import workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEPS.split(",")[1], "|", ",");
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Engine Worker Workflow Tests----------------\n";
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
     * Tests running engine in batch mode with using the
     *  Sequential Exhaustion of targeted workflow
     * 
     */
    @Test
    @Order(0)
    public void canRunEngineBatchSequentialExhaustionMode() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Batch Sequential Exhaustive Workflow Worker =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.EXHAUST;

        // Fetch engine worker
        LOGGER.info("Configuring engine worker for:\t'{}' '{}' mode, no iteration limit", stratType, stratMode);
        TestUtils.resetWorkerContainers();
        worker = EngineWorkerTestUtils.getEngineWorker(this.POLICY_MODE, this.STEPS, stratType, stratMode);
        
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
        LOGGER.info("\n\n================ Can Run Batch Sequential Exhaustive Workflow Worker =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  Round Robin Exhaust of targeted workflow
     * 
     */
    @Test
    @Order(3)
    public void canRunEngineBatchRoundRobinExhaustMode() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Batch Round Robin Exhaust Workflow Worker =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.ROUND_ROBIN;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.EXHAUST;
        int ITERATION_LIMIT = 4;
        int RESULT_SET_LIMIT = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
        LOGGER.info("\n\n================ Can Run Batch Round Robin Exhaust Workflow Worker =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  {@link WorkflowStrategyType.SEQUENTIAL} {@link WorkflowStrategyMode.EXHAUST}
     *  of targeted workflow
     * 
     */
    @Test
    @Order(6)
    @Tag("experiemental-system-engine")
    public void canRunEngineBatchSequentialExhaustMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Batch Sequential Exhaust Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.EXHAUST;
        int ITERATION_LIMIT = 6,
            RESULT_SET_LIMIT = 1,
            WINDOW_SIZE = 4,
        POOL_SIZE = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
        LOGGER.info("\n\n================ Can Run Parallel Batch Sequential Exhaust Workflow Workers =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  {@link WorkflowStrategyType.SEQUENTIAL} {@link WorkflowStrategyMode.EXHAUST}
     *  of targeted workflow
     * 
     */
    @Test
    @Order(7)
    public void canRunEngineBatchRoundRobinExhaustMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Batch Sequential Exhaust Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.BATCH;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.EXHAUST;
        int ITERATION_LIMIT = 6,
            RESULT_SET_LIMIT = 1,
            WINDOW_SIZE = 4,
        POOL_SIZE = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
        LOGGER.info("\n\n================ Can Run Parallel Batch Sequential Exhaust Workflow Workers =================\n");
    }
    
    /**
     * Tests running engine in batch mode with using the
     *  {@link WorkflowStrategyType.SEQUENTIAL} {@link WorkflowStrategyMode.SCANNER}
     *  of targeted workflow as service. Where workflow cycling should be disabled
     *  by default
     * 
     */
    @Test
    @Order(8)
    public void canRunEngineServiceSequentialScannerMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Service Sequential Scanner Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.SERVICE;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.SCANNER;
        int ITERATION_LIMIT = 3,
            RESULT_SET_LIMIT = 1,
            WINDOW_SIZE = 4,
        POOL_SIZE = 1;
        boolean SHOULD_CYCLE_TOGGLE = true;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
            POOL_SIZE, WINDOW_SIZE, ITERATION_LIMIT, SHOULD_CYCLE_TOGGLE
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
        LOGGER.info("\n\n================ Can Run Parallel Service Sequential Scanner Workflow Workers =================\n");
    }
    
    
    /**
     * Tests running engine in batch mode with using the
     *  {@link WorkflowStrategyType.SEQUENTIAL} {@link WorkflowStrategyMode.EXHAUST}
     *  of targeted workflow as service. Where workflow cycling should be disabled
     *  by default
     * 
     */
    @Test
    @Order(9)
    public void canRunEngineServiceSequentialExhaustMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Service Sequential Exhaust Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.SERVICE;
        WorkflowStrategyType stratType = WorkflowStrategyType.SEQUENTIAL;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.EXHAUST;
        int ITERATION_LIMIT = 3,
            RESULT_SET_LIMIT = 1,
            WINDOW_SIZE = 4,
        POOL_SIZE = 1;
        boolean SHOULD_CYCLE_TOGGLE = true;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
            POOL_SIZE, WINDOW_SIZE, ITERATION_LIMIT, SHOULD_CYCLE_TOGGLE
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
        LOGGER.info("\n\n================ Can Run Parallel Service Sequential Exhaust Workflow Workers =================\n");
    }

    
    /**
     * Tests running engine in service mode with using the
     *  {@link WorkflowStrategyType.ROUND_ROBIN} {@link WorkflowStrategyMode.SCANNER}
     *  of targeted workflow
     * 
     */
    @Test
    @Order(10)
    public void canRunEngineServiceRoundRobinScannerMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Service Round Robin Scanner Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.SERVICE;
        WorkflowStrategyType stratType = WorkflowStrategyType.ROUND_ROBIN;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.SCANNER;
        int ITERATION_LIMIT = 5,
            RESULT_SET_LIMIT = 4,
            WINDOW_SIZE = 2,
        POOL_SIZE = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
        LOGGER.info("\n\n================ Can Run Parallel Service Round Robin Scanner Workflow Workers =================\n");
    }
    
    
    /**
     * Tests running engine in service mode with using the
     *  {@link WorkflowStrategyType.ROUND_ROBIN}
     *  {@link WorkflowStrategyMode.EXHAUST} of targeted workflow
     * 
     */
    @Test
    @Order(11)
    public void canRunEngineServiceRoundRobinExhaustMode_Parallel() {

        // Configure test
        LOGGER.info("\n\n================= Can Run Parallel Service Round Robin Exhaust Workflow Workers =================\n");
        TaskTideEngineWorker worker;
        WorkerExecutionPolicy executionPolicy = WorkerExecutionPolicy.SERVICE;
        WorkflowStrategyType stratType = WorkflowStrategyType.ROUND_ROBIN;
        WorkflowStrategyMode stratMode = WorkflowStrategyMode.EXHAUST;
        int ITERATION_LIMIT = 5,
            RESULT_SET_LIMIT = 4,
            WINDOW_SIZE = 2,
        POOL_SIZE = 1;
        
        // Import workload
        LOGGER.info("Importing workload");
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEPS.split(",")[0], ",");
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
        LOGGER.info("\n\n================ Can Run Parallel Service Round Robin Exhaust Workflow Workers =================\n");
    }
}