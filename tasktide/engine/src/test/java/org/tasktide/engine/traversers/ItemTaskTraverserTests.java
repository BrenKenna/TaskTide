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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.tasktide.core.manager.TaskTideServiceManager;

// import org.junit.Rule;
// import org.testcontainers.containers.GenericContainer;

import org.tasktide.engine.TestUtils;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.engine.exceptions.TaskTideEngineCheckedException;

import org.tasktide.engine.workerunit.container.WorkerUnitContainer;
import org.tasktide.engine.workerunit.container.WorkerUnitModelType;
import org.tasktide.engine.workerunit.provider.TaskTideExecutorServiceProvider;


/**
 * Test module for the {@link ItemTaskTraverser}
 *
 * @author Bren
 */
@Tag("integration-engine")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemTaskTraverserTests {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemTaskTraverserTests.class);
    
    private final String WORKFLOW = "Traverser Tests";
    private final String STEP = "Item Task Traverser Tests";
    
    private WorkerUnitContainer workerUnit;
    
    // CouchDB container
    // @Rule
    // public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public ItemTaskTraverserTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemTask Traverser Tests ----------------\n";
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
        String msg = "\n\n---------------- Terminating ItemTask Traverser Tests ----------------\n";
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
     * Tests that the {@link ItemTaskTraverser} can process tasks
     * 
     */
    @Test
    @Order(0)
    public void traverserCanProcessAnItemTask() {
        
        // Configure test
        LOGGER.info("\n\n================ ItemTask Traverser Can Process Tasks ================\n");
        boolean assertionState;
        List<WorkItem> workload;
        List<ItemTask> tasks;
        WorkItem task;
        ItemTask itemTask;
        TaskTideWorkloadTraverser<ItemTask> traverser;
        
        // Build acquisition policy
        LOGGER.info("Fetching workload target step:\t'{}'", this.STEP);
        workload = TestUtils.fetchTargetedWorkload(this.STEP);
        task = workload.get(0);
        LOGGER.info("Displaying WorkItem being processed for reference:\n'{}'", task.toJsonDoc());
        
        // Fetch first task
        LOGGER.info("Fetching first task & configuring ItemTask Traverser");
        traverser = new ItemTaskTraverser();
        tasks = new ArrayList<>(task.getWorkload().getTaskMap().values());
        
        // Try process first task
        itemTask = tasks.get(0);
        LOGGER.info("Processing '{}' from '{}'", task.getId(), itemTask.getId());
        try {
            assertionState = traverser.processElm(itemTask);
            LOGGER.info("Processing complete with state '{}' for:\t'{}'", assertionState, task.getId());
        }
        
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error during ItemTask processing:\t'{}'\n\n{}", itemTask.getId(), ex);
            assertionState = false;
        }
        
        // Evaluate test
        task.setItemState(ItemState.DONE);
        TaskTideServiceManager.fetchWorkItemService().updateModel(task);
        Assertions.assertTrue(assertionState, "Error could not traverse ItemTask");
        LOGGER.info("\n\n================ ItemTask Traverser Can Process Tasks ================\n");
    }
    
    
    /**
     * Tests {@link ItemTaskTraverser} serial traversal
     * 
     */
    @Test
    @Order(1)
    public void canTraverseWorkloadSerial() {
    
        // Configure test
        LOGGER.info("\n\n================ Can Traverse ItemTask Serial ================\n");
        boolean assertionState;
        List<WorkItem> workload;
        List<ItemTask> tasks;
        WorkItem task;
        String resultMap;
        TaskTideWorkloadTraverser<ItemTask> traverser;
        
        // Build acquisition policy
        LOGGER.info("Fetching workload target step:\t'{}'", this.STEP);
        workload = TestUtils.fetchTargetedWorkload(this.STEP);
        task = workload.get(0);
        
        // Fetch first task
        LOGGER.info("Fetching first task & configuring ItemTask Traverser");
        traverser = new ItemTaskTraverser();
        tasks = new ArrayList<>(task.getWorkload().getTaskMap().values());
        tasks.addAll( new ArrayList<>(workload.get(1).getWorkload().getTaskMap().values()) );
        LOGGER.info("Evaluating processing for N = '{}' tasks", tasks.size());
        
        try {
            
            // Process task
            traverser.traverse(tasks);
            resultMap = JsonUtils.toJson(true, task.getWorkload().summarizeWorkload());
            LOGGER.info("Traversal complete");
            
            // Check workload
            LOGGER.info("Evaluating workload status:\t'{}'\n\n'{}'", task.getId(), resultMap);
            int tasksDone = task.getWorkload().summarizeWorkload().get(TaskState.COMPLETE);
            if ( tasksDone == task.getTaskCount() ) {
                LOGGER.info("ItemTask Traversal completed successfully");
                assertionState = true;
            }
            else {
                LOGGER.error("Error, tasks completed does not match expected. Test failed");
                assertionState = false;
            }
        }
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error during Serial ItemTask Traverseral:\n\n'{}'", ex);
            assertionState = false;
        }
        
        // Log status
        task.setItemState(ItemState.DONE);
        TaskTideServiceManager.fetchWorkItemService().updateModel(task);
        Assertions.assertTrue(assertionState, "Error could not traverse ItemTask");
        LOGGER.info("\n\n================ Can Traverse ItemTask Serial ================\n");
    }
    
    
    /**
     * Tests {@link ItemTaskTraverser} parallel traversal
     * 
     */
    @Test
    @Order(2)
    public void canTraverseWorkloadParallel() {
    
        // Configure test
        LOGGER.info("\n\n================ Can Traverse ItemTask Parallel ================\n");
        boolean assertionState;
        List<WorkItem> workload;
        List<ItemTask> tasks;
        int nWorkerThreads = 1, nItemTaskThreads = 3;
        WorkItem task;
        String resultMap;
        TaskTideWorkloadTraverser<ItemTask> traverser;
        
        // Re-configure engine worker unit container
        this.workerUnit = TestUtils.configureNewWorkerUnitContainer();
        TaskTideExecutorServiceProvider.reset();
        try {
            workerUnit.configureProcessExecutor();
            workerUnit.configureExecutorServices(nWorkerThreads, nItemTaskThreads);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.ITEMTASK, -1);
            workerUnit.configureEngineExecutor(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.ITEMTASK);
            workerUnit.configureEngineObserverChain(WorkerUnitModelType.WORKITEM, -1);
            workerUnit.configureWorkloadTraverser(WorkerUnitModelType.WORKITEM);
        }
        catch ( TaskTideEngineCheckedException ex ) {}
        
        
        // Build acquisition policy
        LOGGER.info("Fetching workload target step:\t'{}'", this.STEP);
        workload = TestUtils.fetchTargetedWorkload(this.STEP);
        task = workload.get(0);
        
        // Fetch first task
        LOGGER.info("Fetching first task & configuring ItemTask Traverser");
        traverser = new ItemTaskTraverser();
        tasks = new ArrayList<>(task.getWorkload().getTaskMap().values());
        tasks.addAll( new ArrayList<>(workload.get(1).getWorkload().getTaskMap().values()) );
        
        // Process tasks
        try {
            traverser.traverse(tasks);
            resultMap = JsonUtils.toJson(true, task.getWorkload().summarizeWorkload());
            LOGGER.info("Traversal complete");
            
            // Check workload
            LOGGER.info("Evaluating workload status:\t'{}'\n\n'{}'", task.getId(), resultMap);
            Map<TaskState, Integer> taskSummary = task.getWorkload().summarizeWorkload();
            int inActive = taskSummary.get(TaskState.ACTIVE) + taskSummary.get(TaskState.PENDING);
            if ( inActive == 0 ) {
                LOGGER.info("ItemTask Traversal completed successfully");
                assertionState = true;
            }
            else {
                LOGGER.error("Error, tasks completed does not match expected. Test failed");
                assertionState = false;
            }
        }
        catch ( TraverserCheckedException ex ) {
            LOGGER.error("Error during Serial ItemTask Traverseral:\n\n'{}'", ex);
            assertionState = false;
        }
        
        // Log status
        task.setItemState(ItemState.DONE);
        TaskTideServiceManager.fetchWorkItemService().updateModel(task);
        Assertions.assertTrue(assertionState, "Error could not traverse ItemTask");
        LOGGER.info("\n\n================ Can Traverse ItemTask Parallel ================\n");
    }
}