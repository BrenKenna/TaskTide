/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.repository;

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

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.json_repo.JsonRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;


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
}
