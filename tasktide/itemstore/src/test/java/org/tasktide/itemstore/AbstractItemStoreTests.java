/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.itemstore;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;


/**
 * Testing of the AbstractItemStore class
 * 
 * @author bkenna
 */
public class AbstractItemStoreTests {
    
    private static final Logger logger = LogManager.getLogger(AbstractItemStoreTests.class);
    
    public AbstractItemStoreTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating AbstractItemStore Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating AbstractItemStore Tests ----------------\n";
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
     * Just displays data from getters
     */
    @Test
    @Order(0)
    public void canSummarize() {
    
        // Initialize test
        logger.info("\n\n================ Can Summarize AbstractItemStore Test ================\n");
        AbstractItemStore itemStore;
        Map<String, String> data = new HashMap<>();
        
        // Configure database
        Path workDir = ItemStoreTestUtils.setWorkingDirectory("rocksDB", "AbstractItemStore");
        itemStore = ItemStoreTestUtils.makeRocksDB("AbstractItemStore", workDir);
        
        // Fetch attributes
        data.put("Master", itemStore.getMasterFilePath());
        data.put("Prototype", itemStore.getFilePath());
        data.put("DB Directory", itemStore.getDbDirectory());
        data.put("Store Name", itemStore.getStoreName());
        logger.info("Displaying AbstractItemStore properties:\n\n{}", ItemStoreTestUtils.toJson(data));
        
        // End test
        assertTrue(!data.isEmpty(), "No attributes retreived from AbstractItemStore");
        itemStore.clearPrototype();
        logger.info("\n\n================ Can Summarize AbstractItemStore Test ================\n");
    }
    
    
    /**
     * Tests that waiter can finish
     */
    @Test
    @Order(1)
    public void crossJvmFileLockFinishes() {
    
        // Initialize test
        logger.info("\n\n================ Can Multiple JVMs Serially AbstractItemStore Test ================\n");
        boolean assertionState;
        ProcessBuilder procBuilder;
        List<Process> processes;
        int nProcesses = 2, nPassing = 0;
        
        // Fetch & processes from process builder
        logger.info("Configuring process builder for cross JVM DB access test");
        procBuilder = ItemStoreTestUtils.crossJvmFileLockerProcess();
        logger.info("Running processes");
        processes = ItemStoreTestUtils.runProcesses(procBuilder, nProcesses);
        
        // Wait for processes
        logger.info("Waiting until processes have completed");
        ItemStoreTestUtils.waitUntilDone(processes);
        
        // Summarize processes
        logger.info("Summarizing processes");
        nPassing = ItemStoreTestUtils.summarizeProcesses(processes);
        
        // End test
        logger.info("Processes Summar:\t'{}'/'{}'", nPassing, nProcesses);
        assertionState = nPassing == nProcesses;
        assertTrue(assertionState);
        logger.info("\n\n================ Can Multiple JVMs Serially AbstractItemStore Test ================\n");
    }
}