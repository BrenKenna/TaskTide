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
import org.junit.jupiter.api.Tag;
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
import org.tasktide.engine.policies.AcquisitionPolicyMode;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.traversers.TaskTideWorkloadTraverser;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;


/**
 * Suite of tests for {@link WorkerUnitContainer} package
 *
 * @author Bren
 */
@Tag("unit-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkerUnitContainerTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkerUnitContainerTests.class);
    
    private final String STEP = "Nested NS Lookups";
    
    private SeContainer container;
    private Template template;
    
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkerUnitContainerTests() {
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
    public TaskTideWorkloadAcquisitionPolicy getAcquisitionPolicy() {
        return AcquisitionPolicyMode.TARGETED
            .initBuilder()
            .withTarget(this.STEP)
            .withItemState(ItemState.TODO)
        .build();
    }
    
    
    /**
     * Tests configuring {@link ProcessExecutor} through
     *  {@link WorkerContainer}
     * 
     */
    @Test
    @Order(0)
    public void canConfigureProcessExecutor() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure ProcessExecutor Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        ProcessExecutor procExec;
        boolean assertionState;
        
        // Try configure process executor
        try {
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            procExec = workerUnit.getProcessExecutor();
            assertionState = procExec != null;
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("ProcessExecutor configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure ProcessExecutor through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure ProcessExecutor");
        LOGGER.info("\n\n================ Configure ProcessExecutor Through WorkerUnitContainer ================\n");
    }
    
    
    /**
     * Tests configuring {@link ExecutorService} through
     *  {@link WorkerContainer}
     * 
     */
    @Test
    @Order(1)
    public void canConfigureExecutorServices() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure ProcessExecutor Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        ExecutorService execServ;
        int workItemThreads, itemTaskThreads;
        boolean assertionState;
        
        // Try configure process executor
        try {
            
            // Configure instance
            workItemThreads = 3;
            itemTaskThreads = 3;
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(workItemThreads, itemTaskThreads);
            execServ = workerUnit.getThreadPool(WorkerUnitModelType.WORKITEM);
            assertionState = !execServ.isShutdown();
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("ExecutorServices configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure ExecutorServices through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure ExecutorServices");
        LOGGER.info("\n\n================ Configure ExecutorServices Through WorkerUnitContainer ================\n");
    }
    
    
    /**
     * Tests configuring {@link ProcessExecutor} through
     *  {@link WorkerContainer}
     * 
     */
    @Test
    @Order(2)
    public void canConfigureItemTaskObserver() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure ItemTaskObserver Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        TaskTideEngineObserver<ItemTask> itemTaskObserver;
        boolean assertionState;
        
        // Try configure process executor
        try {
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(3, 3);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, 10000);
            itemTaskObserver = workerUnit.getEngineObserverChain(WorkerUnitModelType.ITEMTASK);
            assertionState = itemTaskObserver != null;
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("ItemTaskObserver configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure ItemTaskObserver through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure ItemTaskObserver");
        LOGGER.info("\n\n================ Configure ItemTaskObserver Through WorkerUnitContainer ================\n");
    }
    
    
    /**
     * Teste configuring {@link ItemTask} {@link TaskTideExecutor}
     * 
     */
    @Test
    @Order(3)
    public void canConfigureItemTaskExecutor() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure ItemTaskExecutor Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        TaskTideExecutor<ItemTask> itemTaskExec;
        boolean assertionState;
        
        // Try configure process executor
        try {
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(3, 3);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, 10000);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            
            itemTaskExec = workerUnit.getEngineExecutor(WorkerUnitModelType.ITEMTASK);
            assertionState = itemTaskExec != null;
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("ItemTaskExecutor configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure ItemTaskExecutor through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure ItemTaskExecutor");
        LOGGER.info("\n\n================ Configure ItemTaskExecutor Through WorkerUnitContainer ================\n");
    }
    
    
    /**
     * Tests configuring {@link ItemTask} {@link TaskTideWorkloadTraverser}
     * 
     */
    @Test
    @Order(4)
    public void canConfigureItemTaskTraverser() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure ItemTaskTraverser Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadTraverser<ItemTask> itemTaskTrav;
        boolean assertionState;
        
        // Try configure process executor
        try {
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(3, 3);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, 10000);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            itemTaskTrav = workerUnit.getEngineWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            assertionState = itemTaskTrav != null;
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("ItemTaskTraverser configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure ItemTaskTraverser through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure ItemTaskTraverser");
        LOGGER.info("\n\n================ Configure ItemTaskTraverser Through WorkerUnitContainer ================\n");
    }
    
    
    /**
     * Tests configuring {@link WorkItem} {@link TaskTideWorkloadTraverser}
     * 
     */
    @Test
    @Order(5)
    public void canConfigureWorkItemTraverser() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure WorkItemTraverser Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadTraverser<WorkItem> workItemTrav;
        boolean assertionState;
        
        // Try configure process executor
        try {
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(3, 3);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, 10000);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, 100000);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            workItemTrav = workerUnit.getEngineWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            assertionState = workItemTrav != null;
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("WorkItemTraverser configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure WorkItemTraverser through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure WorkItemTraverser");
        LOGGER.info("\n\n================ Configure WorkItemTraverser Through WorkerUnitContainer ================\n");
    }
    
    
    /**
     * Tests configuring {@link TaskTideEngineWorker}
     *  from {@link WorkerUnitContainer}
     * 
     */
    @Test
    @Order(6)
    public void canConfigureEngineWorker() {
    
        // Configure test
        LOGGER.info("\n\n================ Configure EngineWorker Through WorkerUnitContainer ================\n");
        WorkerUnitContainer workerUnit;
        TaskTideWorkloadAcquisitionPolicy acquisitionPolicy;
        TaskTideEngineWorker worker;
        boolean assertionState;
        
        // Try configure process executor
        try {
            
            workerUnit = WorkerUnitContainer.getInstance();
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(3, 3);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, 10000);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, 100000);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
            
            // Configures worker
            acquisitionPolicy = this.getAcquisitionPolicy();
            worker = new TaskTideEngineWorker(acquisitionPolicy);
            assertionState = worker.getPolicy() != null;
        }
        
        // Otherwise fail test
        catch ( TaskTideEngineUncheckedException | TaskTideEngineCheckedException ex ) {
            assertionState = false;
        }
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.error("EngineWorker configured through WorkerContainer");
        }
        else {
            LOGGER.error("Unable to configure EngineWorker through WorkerContainer");
        }
        Assertions.assertTrue(assertionState, "Error could not configure EngineWorker");
        LOGGER.info("\n\n================ Configure EngineWorker Through WorkerUnitContainer ================\n");
    }
}