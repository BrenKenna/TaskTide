/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.json_repo.JsonRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;

import org.tasktide.TestUtils;


/**
 * Tests of the JsonRepository
 * 
 * @author bkenna
 */
public class JsonRepositoryTests {
    
    
    private static final Logger logger = LogManager.getLogger(JsonRepositoryTests.class);
    
    public JsonRepositoryTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating JSON Repository Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating JSON Repository Tests ----------------\n";
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
     * Tests construction of each model repository
     * 
     */
    @Test
    @Order(0)
    public void repoNotNull() {
        
        // Initialize data
        logger.info("\n\n================ Construct JSON Repository Test ================\n");
        String msg;
        boolean assertionState;
        JsonRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry");
        repo = new JsonWorkItemRepository(data, "myData");
        
        // Represent repo as string
        msg = repo.toString();
        assertionState = msg != null;
        
        // Handle test state
        if (assertionState) {
            logger.info("\n\nDisplaying Repository as String:\n" + msg + "\n");
        }
        else {
            logger.error("\n\nError displaying repository:\n" + msg + "\n");
        }
        
        // Log test state
        logger.info("\n\n================ Construct JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Can write repository
     */
    @Test
    @Order(1)
    public void canWriteRepository() {
        
        // Initialize data
        logger.info("\n\n================ Writing JSON Repository Test ================\n");
        String msg;
        boolean assertionState;
        JsonRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry");
        repo = new JsonWorkItemRepository(data, "myData");
        
        // Represent repo as string
        int nRecords = repo.save();
        assertionState = nRecords >= 0;
        
        // Handle test state
        if (assertionState) {
            logger.info("\n\nNumber of records writen >=0:\t" + nRecords + "\n");
        }
        else {
            logger.error("\n\nError number of records <0:\t" + nRecords + "\n");
        }
        
        // Log test state
        logger.info("\n\n================ Writing JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Can read repository
     */
    @Test
    @Order(2)
    public void canReadRepository() {
        
        // Initialize data
        logger.info("\n\n================ Reading JSON Repository Test ================\n");
        String msg;
        boolean assertionState;
        JsonRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry");
        repo = new JsonWorkItemRepository(data, "myData");
        int nRecords = repo.save();
        
        // Represent repo as string
        msg = repo.listToJson(repo.load()) ;
        assertionState = msg != null;
        
        // Handle test state
        if (assertionState) {
            logger.info("\n\nData read from myData.json.gz:\t" + msg + "\n");
        }
        else {
            logger.error("\n\nError reading data from myData.json.gz:\t" + msg + "\n");
        }
        
        // Log test state
        logger.info("\n\n================ Read JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test the find all and find by id methods
     */
    @Test
    @Order(3)
    public void canQuery() {
        
        // Initialize data
        logger.info("\n\n================ Query JSON Repository Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry");
        repo = new JsonWorkItemRepository(data, "myData");
        
        // Represent repo as string
        logger.info("Verifying simple repository queries from interface level");
        if ( repo.findAll().size() == 2 ) {
            logger.info("Data size matches expexted");
            if ( repo.findById("My WorkItem").isPresent() ) {
                logger.info("Repository findById successful");
                assertionState = true;
            }
            else {
                logger.warn("Repository failed to retrieve by Id");
                assertionState = false;
            }
        }
        else {
            logger.warn("Repository findAll count did not match expected");
            assertionState = false;
        }
        
        // Handle test state
        if (assertionState) {
            logger.info("Can query test sucessful, test state:\t" + assertionState + "\n\n");
        }
        else {
            logger.error("Can query test unsucessful, test state:\t" + assertionState + "\n\n");
        }
        
        // Log test state
        logger.info("\n\n================ Query JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test the find by field having value on Repository interface level 
     */
    @Test
    @Order(4)
    public void canFindValueByField() {
    
        // Initialize data
        logger.info("\n\n================ Find by Field JSON Repository Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry of 2 of the same WorkItems");
        repo = new JsonWorkItemRepository(data, "myData");
        String reference = data.get(0).getItemName();
        logger.info("Fetching count of records where itemName = '" + reference + "'\n");
        int matching = repo.findByField("itemName", reference).size();
        
        // Represent repo as string
        logger.info("Verifying findByField results are 2 WorkItems");
        if ( matching == 2 ) {
            logger.info("Data size matches expexted");
            assertionState = true;
        }
        else {
            logger.warn("Repository findByField count did not match expected");
            assertionState = false;
        }
        
        // Handle test state
        if (assertionState) {
            logger.info("Find by Field test sucessful, test state:\t" + assertionState + "\n\n");
        }
        else {
            logger.error("Find by Field test unsucessful, test state:\t" + assertionState + "\n\n");
        }
        
        // Log test state
        logger.info("\n\n================ Find By Field JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test models can be inserted
     */
    @Test
    @Order(5)
    public void canInsert() {
    
        // Initialize data
        logger.info("\n\n================ Insert Model to JSON Repository Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry of 2 of the same WorkItems");
        repo = new JsonWorkItemRepository(data, "myData");
        TaskTideModel rec = repo.insertModel(TestUtils.makeTestWorkItem());
        
        // Represent repo as string
        logger.info("Verifying second model inserted, and matches first Id");
        if ( repo.findAll().size() == 2 && rec.getId().equals(data.get(0).getId())) {
            logger.info("Data size matches expexted");
            assertionState = true;
        }
        else {
            logger.warn("Repository inserted count Ids did not match expected\n");
            logger.warn("\n\nInserted record:\n" + rec.getId() + "\n\nReferenced record:\n" + data.get(0).getId() + "\n\n");
            assertionState = false;
        }
        
        // Handle test state
        if (assertionState) {
            logger.info("Insert model test sucessful, test state:\t" + assertionState + "\n\n");
        }
        else {
            logger.error("Insert model test unsucessful, test state:\t" + assertionState + "\n\n");
        }
        
        // Log test state
        logger.info("\n\n================ Insert Model to JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test models can be deleted
     */
    @Test
    @Order(5)
    public void canDelete() {
    
        // Initialize data
        logger.info("\n\n================ Delete Model from JSON Repository Test ================\n");
        boolean assertionState;
        TaskTideRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        
        // Construct repo
        logger.info("Creating repoisotry of 2 of the same WorkItems");
        repo = new JsonWorkItemRepository(data, "myData");
        TaskTideModel tmp = data.get(0);
        
        // Represent repo as string
        logger.info("Verifying second model inserted, and matches first Id");
        if ( repo.deleteModel(tmp.getId()) ) {
            logger.info("Datapoint deleted");
            assertionState = true;
        }
        else {
            logger.warn("Datapoint not deleted\n");
            assertionState = false;
        }
        
        // Handle test state
        if (assertionState) {
            logger.info("Delete model test sucessful, test state:\t" + assertionState + "\n\n");
        }
        else {
            logger.error("Delete test unsucessful, test state:\t" + assertionState + "\n\n");
        }
        
        // Log test state
        logger.info("\n\n================ Delete Model from JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Test that models can be updated
     */
    @Test
    @Order(6)
    public void canUpdate() {
    
        // Initialize data
        logger.info("\n\n================ Update Model from JSON Repository Test ================\n");
        boolean assertionState;
        TaskTideModel ref, query;
        TaskTideRepository<WorkItem> repo;
        List<WorkItem> data = new ArrayList<>();
    
        // Generate data
        logger.info("\n\nGenerating data for testing\n");
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        data.add(TestUtils.makeTestWorkItem());
        ref = data.get(0);
        ((WorkItem) ref).setItemName("New Item Name");
        
        // Construct repo
        logger.info("Creating repoisotry of 3 of the same WorkItems");
        repo = new JsonWorkItemRepository(data, "myData");
        query = repo.updateModel( (WorkItem) ref );
        
        // Represent repo as string
        logger.info("Verifying model can be updated");
        if ( query != null ) {
            logger.info("Datapoint updated\n\nNew data point:\n" + query.toJson());
            assertionState = true;
        }
        else {
            logger.warn("Datapoint not updated\n");
            assertionState = false;
        }
        
        // Handle test state
        if (assertionState) {
            logger.info("Update model test sucessful, test state:\t" + assertionState + "\n\n");
        }
        else {
            logger.error("Update test unsucessful, test state:\t" + assertionState + "\n\n");
        }
        
        // Log test state
        logger.info("\n\n================ Update Model from JSON Repository Test ================\n");
        assertTrue(assertionState);
    }
}
