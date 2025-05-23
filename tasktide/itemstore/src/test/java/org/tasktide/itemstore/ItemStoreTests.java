/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.itemstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.tasktide.itemstore.stores.DbTarget;
import org.tasktide.itemstore.stores.RocksDBStore;

/**
 *
 * @author bkenna
 */
public class ItemStoreTests {
    
    private static final Logger logger = LogManager.getLogger(ItemStoreTests.class);
    
    public ItemStoreTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemStore Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemStore Tests ----------------\n";
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
     * Make a an ItemStore and query a record from it
     */
    @Test
    @Order(0)
    public void makeItemStore() {
    
        // Initialize test
        logger.info("\n\n================ Can make ItemStore Test ================\n");
        boolean assertionState;
        Item item, result;
        ItemStore itemStore;
        
        // Make itemstore
        // String storeName, String dbDirectory, String masterDB, String protoDB
        Path workDir = ItemStoreTestUtils.setWorkingDirectory("testing");
        if ( workDir != null ) {
            itemStore = ItemStoreTestUtils.makeRocksDB("mock", workDir);
            if ( itemStore != null ) {
                item = new Item<String>("myId", "state", "payload");
                try {
                    itemStore.saveItem(item);
                    result = itemStore.getById("myId");
                    itemStore.syncToMaster();
                    itemStore.closeConn(DbTarget.PROTOTYPE);
                    assertionState = item.getId().equals( result.getId() );
                    logger.info("Displaying original & queried Item:\n\n{}\n{}\n", item, result);
                    logger.info("Prototype deletion state:\t'{}'", itemStore.clearPrototype());
                    itemStore.closeConn(DbTarget.BOTH);
                }
                catch ( Exception ex ) {
                    assertionState = false;
                }
            }
            else {assertionState = false;}
        }
        else {assertionState = false;}
        
        // End test
        assertTrue(assertionState, "Unable to query from rocksDB");
        logger.info("\n\n================ Can make ItemStore Test ================\n");
    }
    
    
    
    /**
     * Make a an ItemStore and query a record from it
     */
    @Test
    @Order(1)
    public void queryItemStore() {
    
        // Initialize test
        logger.info("\n\n================ Can Requery ItemStore Test ================\n");
        boolean assertionState;
        Item item;
        List<Item> results;
        ItemStore itemStore;
        int nExpected = 2;
        
        //
        Path workDir = ItemStoreTestUtils.setWorkingDirectory("testing");
        if ( workDir != null ) {
            itemStore = ItemStoreTestUtils.makeRocksDB("mock", workDir);
            if ( itemStore != null ) {
                item = new Item<String>("myId-2", "state-2", "payload-2");
                try {
                    itemStore.saveItem(item);
                    itemStore.syncToMaster();
                    results = itemStore.getAll(true);
                    itemStore.closeConn(DbTarget.BOTH);
                    assertionState = results.size() >= nExpected;
                    logger.info("Displaying original & queried Item:\n\n{}\n{}\n", item, results.toArray());
                    logger.info("Prototype deletion state:\t'{}'", itemStore.clearPrototype());
                }
                catch ( Exception ex ) {
                    ex.printStackTrace();
                    assertionState = false;
                }
            }
            else {assertionState = false;}
        }
        else {assertionState = false;}
        
        // End test
        assertTrue(assertionState, "Unable to query from rocksDB");
        logger.info("\n\n================ Can Requery ItemStore Test ================\n");
    }
}
