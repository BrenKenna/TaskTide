/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.repository.JsonRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;

import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.task.TaskState;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.TestCaseBuilderUtility;


/**
 * Test module for {@link WorkItemService WorkItemService}. Focuses on the {@link JsonRepository JsonRepository}
 * 
 * <ul>
 *  <li>TestUtils shows what can go into different classes.</li>
 *  <li>Builder pattern adds varied means of constructing model classes.</li>
 *  <li>Adjusted StateSummary to be generic, with type as enum. Should consider validation.</li>
 * </ul>
 * 
 * @author bkenna
 */
public class WorkItemServiceTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemServiceTests.class);
    
    public WorkItemServiceTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Service Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Service Tests ----------------\n";
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
     * Test whether WorkItem service can be constructed
     */
    @Test
    @Order(0)
    public void canConstructWorkItemService() {
    
        // Setup repository
        logger.info("\n\n================ Service Setup Test ================\n");
        boolean assertionState;
        JsonRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
        
        // Generate data
        logger.info("Generating data for testing");
        data.add(TestCaseBuilderUtility.makeTestWorkItem());
        data.add(TestCaseBuilderUtility.makeTestWorkItem());
        data.add(TestCaseBuilderUtility.makeTestWorkItem());
        repo = new JsonWorkItemRepository(data, "myData");
        
        // Construct work item service
        TaskTideService<WorkItem> serv = new WorkItemService(repo, 4);
        assertionState = serv.toString() != null;
        
        // Handle test state
        if (assertionState) {
            logger.info("\n\nDisplaying WorkItemService as String:\n" + serv.toString() + "\n");
        }
        else {
            logger.error("\n\nError WorkItemService as String:\n" + serv.toString() + "\n");
        }
        
        // Log test state
        logger.info("\n\n================ Service Setup Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test whether duplicate task can be added
     */
    @Test
    @Order(1)
    public void cannotAddDuplicateTask() {
    
        // Fetch service
        logger.info("\n\n================ Duplicate WorkItem Test ================\n");
        boolean assertionState = true;
        TaskTideRepository<WorkItem> repo = TestCaseBuilderUtility.createWorkItemJsonRepo();
        WorkItemService serv = new WorkItemService(repo, 4);
        
        // Append task to a work item
        ItemTask task = TestCaseBuilderUtility.makeTestItemTask();
        WorkItem item = serv.appendTask(serv.viewAll().get(0), task);
        
        // Check data
        if ( item == null ) {
            logger.info("Test successful cannot add duplicate task");
            assertionState = true;
        }
        else {
            assertionState = false;
            logger.warn("Test unsuccessful duplicate task added");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Duplicate WorkItem Test ================\n");
    }
    
    
    /**
     * Test locking a work item
     */
    @Test
    @Order(2)
    public void canLockItem() {
    
        // Fetch service
        logger.info("\n\n================ Locking WorkItem Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo = TestCaseBuilderUtility.createWorkItemJsonRepo();
        WorkItemService serv = new WorkItemService(repo, 4);
        
        // Lock work Item
        logger.info("\n\nLocking first WorkItem\n");
        WorkItem toLock = serv.viewAll().get(0);
        WorkItem locked = serv.lockItem(toLock);
        
        // Handle process output
        if ( locked != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying locked item:\n" + locked.getLockId() + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable to lock item:\n" + toLock.toJsonDoc() + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Locking WorkItem Test ================\n");
    }
    
    
    /**
     * Test whether WorkItem can be marked as done
     */
    @Test
    @Order(3)
    public void canMarkAsDone() {
    
        // Fetch service
        logger.info("\n\n================ Marking as Done Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo = TestCaseBuilderUtility.createWorkItemJsonRepo();
        WorkItemService serv = new WorkItemService(repo, 4);
        
        // Lock work Item
        logger.info("\n\nCompleting first WorkItem\n");
        WorkItem pendDone = serv.viewAll().get(0);
        pendDone.getWorkload().getWorkload().values().stream()
                  .forEach(elm -> elm.setTaskState(TaskState.COMPLETE));
        WorkItem done = serv.markAsDone(pendDone);
        
        // Handle process output
        if ( done != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying completed item:\n" + done.toJsonDoc() + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable to mark as done item:\n" + pendDone.toJsonDoc() + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Marking as Done Test ================\n");
    }
    
    
    /**
     * Test finding an item by field value
     */
    @Test
    @Order(4)
    public void canFindByField() {
        
        // Fetch service
        logger.info("\n\n================ View by Field Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo = TestCaseBuilderUtility.createWorkItemJsonRepo();
        WorkItemService serv = new WorkItemService(repo, 4);
        
        // Fetch 
        List<WorkItem> results = serv.viewByField("itemState", ItemState.TODO);
        if ( results != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying first queried item:\n" + results.get(0).toJsonDoc() + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable query via service:\n" + results + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ View by Field Test ================\n");
    }
    
    
    /**
     * Test summarizing work items by state, using WorkItem & Workload summary methods
     */
    @Test
    @Order(5)
    public void canSummarizeByState() {
        
        // Fetch service
        logger.info("\n\n================ Summarize By State Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo = TestCaseBuilderUtility.createWorkItemJsonRepo();
        WorkItemService serv = new WorkItemService(repo, 4);
    
        // Fetch
        logger.info("Fetching summary count of item states");
        StateSummary<ItemState> stateSummary = serv.fetchCountByState(true);
        assertionState = stateSummary.getCounts().get(ItemState.TODO) == serv.viewItemsByState(ItemState.TODO).size();
        if ( assertionState ) {
            logger.info("\n\nTest successful displaying retreived StateSummary:\n\n" + stateSummary.toJsonDoc() + "\n");
        }
        else {
            logger.warn(
               "\n\nTest failed displaying retreived StateSummary:\n" 
                   + stateSummary.toJsonDoc() +
               "\n\nExpected TODO count = " + serv.viewItemsByState(ItemState.TODO).size() +
               "\n"
            );
        }

        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Summarize By State Test ================\n");
    }
}
