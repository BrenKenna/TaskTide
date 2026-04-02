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

import jakarta.nosql.Template;
import jakarta.enterprise.inject.se.SeContainer;
import java.util.concurrent.ExecutorService;

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
import org.tasktide.core.model.task.ItemTask;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.TestUtils;
import org.tasktide.engine.TestEnvironment;

import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;
import org.tasktide.engine.exceptions.TaskTideEngineUncheckedException;

import org.tasktide.engine.executor.ProcessExecutor;
import org.tasktide.engine.executor.TaskTideExecutor;
import org.tasktide.engine.observer.TaskTideEngineObserver;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.policies.WorkItemAcquisitionPolicy;
import org.tasktide.engine.policies.WorkerExecutionPolicy;
import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;



/**
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTideEngineWorkerTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTideEngineWorkerTests.class);
    
    private final String STEP = "Nested NS Lookups";
    
    private SeContainer container;
    private Template template;
    
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public TaskTideEngineWorkerTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Engine Worker Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords(
            "nested-nslookup-tasks.txt",
            this.STEP,
            "|",
            ","
        );
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Traverser Tests ----------------\n";
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
     * Fetch {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy} for {@link WorkItem}
     */
    public TaskTideWorkloadAcquisitionPolicy<WorkItem> getAcquisitionPolicy() {
        return WorkItemAcquisitionPolicy
            .newInstance()
            .withTarget(this.STEP)
            .withItemState(ItemState.TODO)
        ;
    }

    
    /**
     * Fetch {@link TaskTideEngineWorker} configured
     *  through {@link WorkerUnitContainer}
     * 
     * @return {@link TaskTideEngineWorker}
     */
    public TaskTideEngineWorker getEngineWorker() {
        
        // Initialize vars
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadAcquisitionPolicy<WorkItem> acquisitionPolicy;
        
        // Try configure process executor
        try {
            
            // Configure engine componenets
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(3, 3);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, 10000);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, 100000);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            // Configures worker
            acquisitionPolicy = this.getAcquisitionPolicy();
            return new TaskTideEngineWorker(acquisitionPolicy);
        }
        
        catch ( TaskTideEngineCheckedException ex ) {
            LOGGER.error("Could not instantiate WorkItemTraverser:\n\n{}", ex);
            return null;
        }
    }
    
    
    /**
     * Tests running engine in batch mode
     * 
     */
    @Test
    @Order(0)
    public void canRunEngineBatchMode() {
        
        // Configure test
        LOGGER.info("\n\n================ Can Run Engine Worker ================\n");
        TaskTideEngineWorker worker;
        
        // Fetch engine worker
        LOGGER.info("Fetching engine worker");
        worker = this.getEngineWorker();
        
        // Run engine
        LOGGER.info("Running engine in batch");
        worker.runEngine(WorkerExecutionPolicy.BATCH);
        LOGGER.info("Engine processing completed");
        
        // Log complete
        LOGGER.info("\n\n================ Can Run Engine Worker ================\n");
    }
}