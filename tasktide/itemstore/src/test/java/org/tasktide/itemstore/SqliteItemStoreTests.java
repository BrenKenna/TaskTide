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
package org.tasktide.itemstore;

import java.nio.file.Path;
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


/**
 * Unit tests for SQLite backed ItemStore
 * 
 * @author bkenna
 */
public class SqliteItemStoreTests {
    
    private static final Logger LOGGER = LogManager.getLogger(SqliteItemStoreTests.class);
    
    public SqliteItemStoreTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating SQLite-Store Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating SQLite-Store Tests ----------------\n";
        LOGGER.info(msg);
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
     * Wrapper method for later tests to fetch ItemStore
     * 
     * @param storeName
     * @return {@link ItemStore}
     */
    public ItemStore createItemStore(String storeName) {
        Path workDir;
        String flag = "sqlite", proto;
        workDir = ItemStoreTestUtils.setWorkingDirectory(flag, storeName);
        proto = UUID.randomUUID().toString();
        return ItemStoreType.SQLITE.makeItemStore(storeName, workDir.toString(), "master", proto);
    }

    
    /**
     * Tests creation of ItemStore
     */
    @Order(0)
    @Test
    public void canMakeItemStore() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can make ItemStore Test ================\n");
        boolean assertionState;
        ItemStore itemStore;
        
        // Setup SQLite store
        LOGGER.info("Creating SQLite ItemStore");
        Path workDir;
        String flag = "sqlite", storeName = "WorkItem", proto;
        workDir = ItemStoreTestUtils.setWorkingDirectory(flag, storeName);
        proto = UUID.randomUUID().toString();
        itemStore = ItemStoreType.SQLITE.makeItemStore(storeName, workDir.toString(), "master", proto);
        LOGGER.info("ItemStore created");
        
        // Evaluate ItemStore
        LOGGER.info("Evaluating ItemStore");
        assertionState = !itemStore.getDbDirectory().isEmpty();
        assertTrue(assertionState, "Error creating ItemStore");
        LOGGER.info("\n\n================ Can make ItemStore Test ================\n");
    }
    
    
    /**
     * Fetches random item
     * 
     * @return {@link Item}
     */
    public Item fetchRandomItem() {
        return new Item<String>(UUID.randomUUID().toString(), "State", "Step", UUID.randomUUID().toString());
    }
    
    
    /**
     * Tests insertions into item store
     */
    @Order(1)
    @Test
    public void canInsertIntoItemStore() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Insert ItemStore Test ================\n");
        boolean assertionState;
        Item item, result;
        ItemStore itemStore;
    
        // Fetch item store and create mock record
        itemStore = createItemStore("InsertionTest");
        item = fetchRandomItem();
        
        // Insert record
        LOGGER.info("Displaying record for import:\n{}\n", item);
        try {
            itemStore.saveItem(DbTarget.MASTER, item);
            LOGGER.info("Record inserted, verifying get");
            result = itemStore.getById(DbTarget.MASTER, item.getId());
            LOGGER.info("Displaying queried result:\n{}\n", result);
            assertionState = result != null;
        }
        catch (Exception ex) {
            assertionState = false;
        }
        
        // Evaluate test
        assertTrue(assertionState, "Error inserting record");
        LOGGER.info("\n\n================ Can Insert ItemStore Test ================\n");
    }
}