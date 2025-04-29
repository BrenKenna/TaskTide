/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.model;

import jakarta.json.bind.JsonbBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.tasktide.core.model.builders.ItemTaskBuilder;
import org.tasktide.core.model.builders.WorkItemBuilder;
import org.tasktide.core.model.builders.WorkloadBuilder;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.task.ProcessLog;
import org.tasktide.core.model.task.TaskLogging;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;

import org.tasktide.TestCaseBuilderUtility;

/**
 * 
 * Unit tests of Model classes for Jakarta-NoSQL, and JSONb annotations, and building
 * 
 * @author bkenna
 */
public class ModelTests {
    
    private static final Logger logger = LogManager.getLogger(ModelTests.class);
    
    public ModelTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Model Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Model Tests ----------------\n";
        logger.info(msg);
    }
    
    
    @BeforeEach
    public void setUp() {
        logger.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("\n\n================ Terminating Test ================\n");
    }

    
    /**
     * Test out instantiating ProcessLog
     */
    @Test
    @Order(0)
    public void processLogTest() {
        
        // Initialize test
        boolean assertionState = true;
        logger.info("\n\n================ Process Log Test ================\n");
        
        // Construct process log
        String[] stdout = {"apples", "oragnes"};
        String[] stderr = {"pears", "pineapples"};
        ProcessLog procLog = new ProcessLog(stdout, stderr);
        logger.info("\n\nDisplaying ProcessLog string:\n" + procLog.toString() + "\n");
        logger.info("\n\nDisplaying ProcessLog json:\n" + procLog.toJsonDoc() + "\n");
        
        // Construct from JSON
        logger.info("\n\nAttempting to construct ProcessLog from JSON string\n");
        String json = procLog.toJsonString();
        ProcessLog procLogB = JsonbBuilder.create().fromJson(json, ProcessLog.class);
        logger.info("\n\nDisplaying results\n" + procLogB.toJsonDoc() + "\n");
        
        // End test
        logger.info("\n\n================ Process Log Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test out instantiating TaskLogging
     */
    @Test
    @Order(1)
    public void taskLogTest() {
    
        // Initialize test
        boolean assertionState = true;
        logger.info("\n\n================ Task Logging Test ================\n");
        
        // Construct process log
        ProcessLog procLog = TestCaseBuilderUtility.makeTestProcessLog();
        
        // Construct task log
        TaskLogging taskLog = new TaskLogging(-4L, procLog, -2L, -3L, "thread", -1L, 0);
        logger.info("\n\nDisplaying TaskLogging json:\n" + taskLog.toJsonDoc() + "\n");
        
        // Re-construct from json string
        logger.info("\n\nAttempting to construct TaskLogging from JSON string\n");
        String json = taskLog.toJsonString();
        TaskLogging taskLogB = JsonbBuilder.create().fromJson(json, TaskLogging.class);
        logger.info("\n\nDisplaying results\n" + taskLogB.toJsonDoc() + "\n");
        
        // End test
        logger.info("\n\n================ Task Logging Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test out task log builders
     */
    @Test
    @Order(2)
    public void taskLogBuilderTest() {
    
        // Initialize test
        boolean assertionState = true;
        logger.info("\n\n================ Task Log Builder Test ================\n");
        
        // Construct process log
        logger.info("\n\nAttempting to build Task Log" + "\n");
        TaskLogging taskLog = TestCaseBuilderUtility.makeTestTaskLog();
        logger.info("\n\nDisplaying Built Task Log string:\n" + taskLog.toString() + "\n");
        logger.info("\n\nDisplaying Built Task Log json:\n" + taskLog.toJsonDoc() + "\n");
        
        // End test
        logger.info("\n\n================ Task Log Builder Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test ItemTask
     */
    @Test
    @Order(3)
    public void testItemTask() {
        
        // Initialize test
        boolean assertionState = true;
        logger.info("\n\n================ Item Task Test ================\n");
        
        // Build dependant objects
        logger.info("\n\nAttempting to build Item Task" + "\n");
        ProcessLog procLog = TestCaseBuilderUtility.makeTestProcessLog();
        TaskLogging taskLog = TestCaseBuilderUtility.makeTestTaskLog();
        
        // Build item task
        ItemTask itemTask = new ItemTaskBuilder()
                                .id("gamma")
                                .taskName("My Task Name")
                                .task("My Task")
                                .taskState(TaskState.COMPLETE)
                                .taskLog(taskLog)
                                .build();
        
        // Display item task
        logger.info("\n\nDisplaying Built Item Task string:\n" + itemTask.toString() + "\n");
        logger.info("\n\nDisplaying Built Item Task json:\n" + itemTask.toJsonDoc() + "\n");
        
        
        // Re-construct from json string
        logger.info("\n\nAttempting to construct Item Task from JSON string\n");
        String json = itemTask.toJsonString();
        ItemTask itemTaskB = JsonbBuilder.create().fromJson(json, ItemTask.class);
        logger.info("\n\nDisplaying results\n" + itemTaskB.toJsonDoc() + "\n");
        
        
        // End test
        logger.info("\n\n================ Item Task Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test Workload
     */
    @Test
    @Order(4)
    public void testWorkload() {
    
        // Initialize test
        boolean assertionState = true;
        logger.info("\n\n================ Workload Test ================\n");
        
        // Build dependant objects
        logger.info("\n\nAttempting to build ItemTasks" + "\n");
        ItemTask itemTask = TestCaseBuilderUtility.makeTestItemTask();
        ItemTask itemTaskB = TestCaseBuilderUtility.makeTestItemTask();
        List<ItemTask> itemTasks = new ArrayList<>();
        itemTasks.add(itemTask); itemTasks.add(itemTaskB);
        
        // Build workload
        logger.info("\n\nAttempting to build Workload" + "\n");
        Workload workload = new WorkloadBuilder()
                                .id("MyWorklaod")
                                .workload(itemTasks)
                                .build();
        
        // Display item task
        logger.info("\n\nDisplaying Built Workload string:\n" + workload.toString() + "\n");
        logger.info("\n\nDisplaying Built Workload json:\n" + workload.toJsonDoc() + "\n");
        
        // Re-construct from json string
        logger.info("\n\nAttempting to construct Workload from JSON string\n");
        String json = workload.toJsonString();
        Workload workloadB = JsonbBuilder.create().fromJson(json, Workload.class);
        logger.info("\n\nDisplaying results\n" + workloadB.toJsonDoc() + "\n");
        
        // End test
        logger.info("\n\n================ Workload Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test WorkItem
     */
    @Test
    @Order(5)
    public void testWorkItem() {
    
        // Initialize test
        boolean assertionState = true;
        logger.info("\n\n================ Work Item Test ================\n");
        
        
        // Build workload
        logger.info("\n\nAttempting to build Work Item" + "\n");
        WorkItem workItem = new WorkItemBuilder()
                                .id("My WorkItem")
                                .itemName("My WorkItem Name")
                                .workload(TestCaseBuilderUtility.makeTestWorkload())
                                .lockId("Some random hexadecimal string")
                                .lockDate(0L)
                                .doneDate(0L)
                                .taskCount(2)
                                .taskDone(0)
                                .build();
        
        // Display item task
        logger.info("\n\nDisplaying Built Work Item string:\n" + workItem.toString() + "\n");
        logger.info("\n\nDisplaying Built Work Item json:\n" + workItem.toJsonDoc() + "\n");
        
        // Re-construct from json string
        logger.info("\n\nAttempting to construct Work Item from JSON string\n");
        String json = workItem.toJsonString();
        WorkItem workItemB = JsonbBuilder.create().fromJson(json, WorkItem.class);
        logger.info("\n\nDisplaying results\n" + workItemB.toJsonDoc() + "\n");
        
        // End test
        logger.info("\n\n================ Work Item Test ================\n");
        assertTrue(assertionState);
    }
}
