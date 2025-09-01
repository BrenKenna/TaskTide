/*
 * Copyright 2025 Brendan Kenna.
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
package org.tasktide.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.repository.JsonRepository;
import org.tasktide.core.repository.json_repo.JsonStepRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.model.state_summary.StateSummary;

import org.tasktide.TestUtils;
import org.tasktide.TestCaseBuilderUtility;


/**
 * Test module for {@link StepService StepService} through {@link JsonRepository JsonRepository}
 * 
 * @author bkenna
 */
public class StepServiceTests {
    
    private static final Logger logger = LogManager.getLogger(StepServiceTests.class);
    
    public StepServiceTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Step Service Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Step Service Tests ----------------\n";
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
     * Test construction of {@link StepService StepService}
     */
    @Test
    @Order(0)
    public void canConstruct() {
    
        // Setup repository
        logger.info("\n\n================ StepService Setup Test ================\n");
        boolean assertionState;
        JsonRepository<Step> repo;
        
        // Generate test data
        logger.info("Generating data for testing");
        repo = new JsonStepRepository(TestCaseBuilderUtility.makeTestStepList(), "myData");
        
        // Construct step service
        TaskTideService<Step> serv = new StepService(repo);
        assertionState = serv.toString() != null;
        
        // Handle test state
        if (assertionState) {
            logger.info("\n\nDisplaying StepService as String:\n" + serv.toString() + "\n");
        }
        else {
            logger.error("\n\nError StepService as String:\n" + serv.toString() + "\n");
        }
        
        // Log test state
        logger.info("\n\n================ StepService Setup Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test finding an item by field value
     */
    @Test
    @Order(1)
    public void canFindByField() {
    
        // Fetch service
        logger.info("\n\n================ StepService View by Field Test ================\n");
        boolean assertionState;
        TaskTideRepository<Step> repo = TestCaseBuilderUtility.createStepJsonRepo();
        TaskTideService<Step> serv = new StepService(repo);
        
        // Fetch 
        List<Step> results = serv.viewByField("stepName", "myFirstStep");
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
    @Order(2)
    public void canSummarize() {
    
        // Fetch service
        logger.info("\n\n================ StepService Summarize Progress Across Steps Test ================\n");
        boolean assertionState;
        TaskTideRepository<Step> repo = TestCaseBuilderUtility.createStepJsonRepo();
        StepService serv = new StepService(repo);
        
        // Fetch 
        Map<String, StateSummary<ItemState>> summary = serv.viewSummary();
        if ( summary != null ) {
            assertionState = true;
            logger.info("\n\nDisplaying step summary:\n" + TestUtils.mapToJsonString(summary) + "\n");
        }
        else {
            assertionState = false;
            logger.warn("\n\nUnable query step summary:\n" + summary + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ StepService Summarize Progress Across Steps Test ================\n");
    }
    
    
    /**
     * Test summarizing progress across all {@link StepService StepService}
     */
    @Test
    @Order(3)
    public void canSave() {
    
        // Fetch service
        logger.info("\n\n================ Save Steps Test ================\n");
        boolean assertionState;
        TaskTideRepository<Step> repo = TestCaseBuilderUtility.createStepJsonRepo();
        StepService serv = new StepService(repo);
        
        // Fetch 
        int nRecords = serv.save();
        if ( nRecords > 1 ) {
            assertionState = true;
            logger.info("Saved Step Service with new records. Displaying total saved:\t" + nRecords + "\n");
        }
        else {
            assertionState = false;
            logger.warn("Unable save Step Service, none saved:\t" + nRecords + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ Save Steps Test ================\n");
    }
    
    
    /**
     * Test view all 
     */
    @Test
    @Order(4)
    public void canViewAll() {
    
        // Fetch service
        logger.info("\n\n================ View All Steps Test ================\n");
        boolean assertionState;
        TaskTideRepository<Step> repo = TestCaseBuilderUtility.createStepJsonRepo();
        StepService serv = new StepService(repo);
        
        // View all
        List<Step> output = serv.viewAll();
        if ( output.size() > 0 ) {
            assertionState = true;
            logger.info("Displaying retrieved data count:\t" + output.size() + "\n");
        }
        else {
            assertionState = false;
            logger.warn("Unable to retrieve data count:\t" + output.size() + "\n");
        }
        
        // Log test status
        assertTrue(assertionState);
        logger.info("\n\n================ View All Steps Test ================\n");
    }
    
    
    /**
     * Test mapping to lower class
     */
    @Test
    @Order(5)
    public void canMapToWorkItem() {
    
        // Fetch service
        logger.info("\n\n================ Map Step to WorkItem Test ================\n");
        boolean assertionState;
        TaskTideRepository<Step> repo = TestCaseBuilderUtility.createStepJsonRepo();
        StepService serv = new StepService(repo);
        
        // Make work item repo
        logger.info("Making JsonWorkItemRepository");
        List<WorkItem> data = new ArrayList<>();
        data.add(TestCaseBuilderUtility.makeTestWorkItem("myFirstStep"));
        data.add(TestCaseBuilderUtility.makeTestWorkItem("myFirstStep"));
        data.add(TestCaseBuilderUtility.makeTestWorkItem("myFirstStep"));
        TaskTideRepository<WorkItem> workRepo = new JsonWorkItemRepository(data, "myData");
        TaskTideService<WorkItem> workServ = new WorkItemService(workRepo, 4);
        
        // Map step to workItem
        logger.info("Mapping 'myFirstStep' to WorkItems");
        Step step = serv.viewByField("stepName", "myFirstStep").get(0);
        List<WorkItem> output = serv.getThroughLink(workServ, step);
        assertionState = output.size() == data.size();
        
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
        logger.info("\n\n================ Map Step to WorkItem Test ================\n");
    }
}
