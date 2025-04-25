/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import java.util.List;
import java.util.Map;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.repository.JsonRepository;
import org.tasktide.core.repository.json_repo.JsonWorkflowRepository;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.model.workitem.ItemState;

import org.tasktide.TestUtils;


/**
 * Test module for {@link StepService StepService} through {@link JsonRepository JsonRepository}
 * 
 * @author bkenna
 */
public class WorkflowServiceTests {
    
    private static final Logger logger = LogManager.getLogger(WorkflowServiceTests.class);
    
    public WorkflowServiceTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Workflow Service Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Workflow Service Tests ----------------\n";
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
     * Test construction of {@link Workflow Workflow}
     */
    @Test
    @Order(0)
    public void canConstruct() {
    
        // Setup repository
        logger.info("\n\n================ WorkflowService Setup Test ================\n");
        boolean assertionState;
        TaskTideRepository<Workflow> repo;
        TaskTideService<Workflow> serv;
        
        // 
        logger.info("Generating data for testing");
        repo = new JsonWorkflowRepository(TestUtils.makeTestWorkflows(), "myData");
        serv = new WorkflowService(repo);
        assertionState = serv.toString() != null;
        
        
        // Handle test state
        if (assertionState) {
            logger.info("\n\nDisplaying WorkflowService as String:\n" + serv.toString() + "\n");
        }
        else {
            logger.error("\n\nError WorkflowService as String:\n" + serv.toString() + "\n");
        }
        
        // Log test state
        logger.info("\n\n================ WorkflowService Setup Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test finding by field
     */
    @Test
    @Order(2)
    public void canViewAll() {
    
        // Fetch service
        logger.info("\n\n================ View All Workflows Test ================\n");
        boolean assertionState;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        WorkflowService serv = new WorkflowService(repo);
        
        // View all
        List<Workflow> output = serv.viewAll();
        if ( !output.isEmpty() ) {
            assertionState = true;
            logger.info("Displaying retrieved data:\n" + TestUtils.mapToJsonString(output) + "\n");
        }
        else {
            assertionState = false;
            logger.warn("Unable to retrieve data:\n" + output.size() + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ View All Workflows Test ================\n");
    }
    
    
    /**
     * Test finding by field
     */
    @Test
    @Order(3)
    public void canFindByField() {
    
        // Fetch service
        logger.info("\n\n================ StepService View by Field Test ================\n");
        boolean assertionState;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        TaskTideService<Workflow> serv = new WorkflowService(repo);
        
        // Fetch 
        List<Workflow> results = serv.viewByField("workflowName", "myFirstWorkflow");
        if ( results != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying first queried item:\n" + TestUtils.modelToJsonString(results) + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable query via service:\n" + TestUtils.modelToJsonString(results) + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ StepService View by Field Test ================\n");
    }
    
    
    
    /**
     * Test summarizing progress across all {@link StepService StepService}
     */
    @Test
    @Order(4)
    public void canSummarize() {
    
        // Fetch service
        logger.info("\n\n================ WorkflowService Summarize Progress Across Steps Test ================\n");
        boolean assertionState;
        Map<String, Map<String, StateSummary<ItemState>>> summary;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        TaskTideService<Workflow> serv = new WorkflowService(repo);
        
        // Fetch 
        summary = ((WorkflowService) serv).summarizeWorkflow();
        if ( summary != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying Workflow summary:\n" + TestUtils.mapToJsonString(summary) + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable query Workflow summary:\n" + summary + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ WorkflowService Summarize Progress Across Steps Test ================\n");
    }
    
    
    /**
     * Test whether a {@link Workflow Workflow} can be mapped down to {@link Step Step}
     */
    @Test
    @Order(5)
    public void canMapToStep() {
    
        // Fetch service
        logger.info("\n\n================ Map Workflow to Step Test ================\n");
        boolean assertionState;
        List<Step> steps;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        WorkflowService serv = new WorkflowService(repo);
        TaskTideRepository<Step> stepRepo;
        TaskTideService<Step> stepServ;
        
        // Make work item repo
        logger.info("Making JsonStepRepository");
        stepRepo = TestUtils.createStepJsonRepo();
        stepServ = new StepService(stepRepo);
        
        // Map workflow to step
        logger.info("Mapping 'myFirstWorkflow' to Steps");
        Workflow workflow = serv.viewByField("workflowName", "myFirstWorkflow").get(0);
        List<Step> output = serv.getThroughLink(stepServ, workflow);
        assertionState = output.size() >= 1;
        
        // Evaluate test
        if ( assertionState ) {
            assertionState = true;
            logger.info("\n\nDisplaying mapped data:\n" + TestUtils.modelToJsonString(output) + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable to map data:\n" + TestUtils.modelToJsonString(output) + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Map Workflow to Step Test ================\n");
    }
    
    
    /**
     * Test inserting a record
     */
    @Test
    @Order(6)
    public void canAppend() {
    
        // Fetch service
        logger.info("\n\n================ Add Workflow Test ================\n");
        boolean assertionState;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        TaskTideService<Workflow> serv = new WorkflowService(repo);
        
        // Add a workflow to collection
        logger.info("Creating new workflow for insertion");
        Workflow myWorkflow = TestUtils.makeTestWorkflow(TestUtils.makeTestStepList(), "workflow4", "myFourthWorkflow");
        Workflow updated = serv.appendModel(myWorkflow);
        if ( updated != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying inserted data:\n" + updated.toJsonDoc() + "\n");
        }
        else {
            assertionState = false;
            logger.info("\n\nUnable to insert data, displaying for reference:\n" + updated.toJsonDoc() + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Add Workflow Test ================\n");
    }
    
    
    /**
     * Test adding {@link Step Step} {@link Workflow Workflow}
     */
    @Test
    @Order(7)
    public void canAddStep() {
    
        // Fetch service
        logger.info("\n\n================ Add Step to Workflow Test ================\n");
        boolean assertionState;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        TaskTideService<Workflow> serv = new WorkflowService(repo);
        
        // Add a workflow to collection
        logger.info("Creating new Step to append to Workflow");
        Step step = TestUtils.makeTestStep("newStep", "My Super Cool New Step");
        Workflow workflow = serv.viewAll().get(0);
        Workflow updated = ((WorkflowService) serv).addStepToWorkflow(workflow, step);
        if ( updated != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying uppated Workflow with new Step:\n" + updated.toJsonDoc() + "\n");
        }
        else {
            assertionState = false;
            logger.info("\n\nUnable to insert data, displaying for reference:\n" + updated.toJsonDoc() + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Add Step to Workflow Test ================\n");
    }
    
    
    /**
     * Test saving a {@link Workflow Workflow}
     */
    @Test
    @Order(8)
    public void canSaveWorkflow() {
    
        // Fetch service
        logger.info("\n\n================ Save Workflows Test ================\n");
        boolean assertionState;
        TaskTideRepository<Workflow> repo = TestUtils.createWorkflowJsonRepo();
        WorkflowService serv = new WorkflowService(repo);
        
        // View all
        if ( serv.save() > 0 ) {
            assertionState = true;
            logger.info("Displaying retrieved data:\n" + TestUtils.mapToJsonString(serv.summarizeWorkflow()) + "\n");
        }
        else {
            assertionState = false;
            logger.warn("Unable to retrieve data:\n" + TestUtils.mapToJsonString(serv.summarizeWorkflow()) + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Save Workflows Test ================\n");
    }
}
