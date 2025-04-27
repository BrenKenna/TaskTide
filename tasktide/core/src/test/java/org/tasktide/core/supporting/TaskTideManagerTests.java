/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.supporting;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.tasktide.TestUtils;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.ItemType;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.workitem.Workload;


/**
 *
 * @author bkenna
 */
public class TaskTideManagerTests {
    
    private static final Logger logger = LogManager.getLogger(TaskTideManagerTests.class);
    
    public TaskTideManagerTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskTide Manger Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskTide Manager Tests ----------------\n";
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
     * Should convert a task request to an ItemTask
     */
    @Test
    @Order(0)
    public void testManagerTasktoItemTask() {
    
        // Construct work item
        logger.info("\n\n================ ManagerTask to ItemTask Test ================\n");
        boolean assertionState;
        ManagerTask myTask;
        ItemTask task;
        
        // Fetch task
        myTask = new ManagerTask("Python Version", "python --version");
        task = myTask.asItemTask();
        if ( task != null ) {
            logger.info("\n\nDisplaying converted task:\n\n" + task.toJsonDoc());
            assertionState = true;
        }
        else {
            logger.error("\n\nUnable to convert request ot ItemTask");
            assertionState = false;
        }
        
        // Log state
        logger.info("\n\n================ ManagerTask to ItemTask Test ================\n");
        assertTrue(assertionState);
    }
    
    
    
    /**
     * Should convert to single task workload
     */
    @Order(1)
    @Test
    public void mapToWorkload() {
    
        // Construct workload
        logger.info("\n\n================ ManagerTask to Workload Test ================\n");
        boolean assertionState;
        ManagerTask myTask;
        Workload workload;
        
        // Fetch workload
        myTask = new ManagerTask("Python Version", "python --version");
        workload = TestUtils.Workload(myTask.asItemTask());
        if ( workload.getWorkloadType() == ItemType.SINGLE ) {
            logger.info("\n\nDisplaying converted Workload:\n\n" + workload.toJsonDoc());
            assertionState = true;
        }
        else {
            logger.error("\n\nUnable to convert request to Workload");
            assertionState = false;
        }
        
        // Log state
        logger.info("\n\n================ ManagerTask to Workload Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Should convert task to work item
     */
    @Order(2)
    @Test
    public void mapToWorkItem() {
    
        // Construct workload
        logger.info("\n\n================ ManagerTask to WorkItem Test ================\n");
        boolean assertionState;
        ManagerTask myTask;
        Workload workload;
        WorkItem work;
        
        // Fetch workload
        myTask = new ManagerTask("Python Version", "python --version");
        workload = TestUtils.Workload(myTask.asItemTask());
        work = TestUtils.makeWorkItem("Version Checks", workload, "Simple Tests");
        
        if ( work.getItemType() == ItemType.SINGLE ) {
            logger.info("\n\nDisplaying converted WorkItem:\n\n" + work.toJsonDoc());
            assertionState = true;
        }
        else {
            logger.error("\n\nUnable to convert request to WorkItem");
            assertionState = false;
        }
        
        // Log state
        logger.info("\n\n================ ManagerTask to WorkItem Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Make nested work item from collection of related tasks
     */
    @Order(3)
    @Test
    public void makeNestedWorkItem() {
    
        // Construct work item
        logger.info("\n\n================ ManagerTasks Nested WorkItem Test ================\n");
        boolean assertionState;
        List<ItemTask> myTasks = new ArrayList<>();
        Workload workload;
        WorkItem work;
        
        // Fetch work item
        myTasks.add(new ManagerTask("Python Version", "python --version").asItemTask());
        myTasks.add(new ManagerTask("Java Version", "java -version").asItemTask());
        myTasks.add(new ManagerTask("Maven Version", "mvn -version").asItemTask());
        workload = TestUtils.workload(myTasks);
        work = TestUtils.makeWorkItem("Version Checks", workload, "Simple Tests");
        
        // Evaluate work item
        if ( work.getItemType() == ItemType.NESTED ) {
            logger.info("\n\nDisplaying converted WorkItem:\n\n" + work.toJsonDoc());
            assertionState = true;
        }
        else {
            logger.error("\n\nUnable to convert request to WorkItem");
            assertionState = false;
        }
        
        // Log state
        logger.info("\n\n================ ManagerTask to WorkItem Test ================\n");
        assertTrue(assertionState);
    }
}
