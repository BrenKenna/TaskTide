/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.itemstore.session;

import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

// For Java Docs
import org.tasktide.itemstore.Item;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.utils.ItemStoreTestUtils;


/**
 * Test module for {@link ItemStoreSession} and supporting
 *  {@link BulkOperation}, and {@link LinkedOperation}
 *
 * @author Brendan Kenna
 */
@Tag("system-itemstore")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemStoreSessionTests {
    
    private final Logger LOGGER = LogManager.getLogger(ItemStoreSessionTests.class);
    private Path workDir;
    private ItemStore itemStore;
    
    public ItemStoreSessionTests() {}
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemStore Session Tests ----------------\n";
        this.workDir = ItemStoreTestUtils.setWorkingDirectory("rocksDB", "itemstore-session");
        this.itemStore = ItemStoreTestUtils.makeRocksDB("itemstore-session", this.workDir);
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemStore Session Tests ----------------\n";
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
     * Tests whether an {@link Item} collection can be inserted,
     *  and verified under the one bulk operation for RocksDB-{@link ItemStore}
     * 
     */
    @Test
    @Order(0)
    public void canImportAndSelectInOneOps() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Bulk Import & Select RocksDB Test ===============\n");
        boolean assertionState;
        ItemStore itemStore;
        String storeName;
        List<Item> records;
        int nRecords = 10;
        
        // Configure ItemStore & records
        LOGGER.info("Configuring RocksDB ItemStore and '{}' test records", nRecords);
        storeName = "BulkOps-RocksDB";
        itemStore = ItemStoreTestUtils.createRocksDbStore(storeName);
        records = ItemStoreTestUtils.fetchRandomItems(nRecords);
        
        // Perform import and verification as bulk operation
        LOGGER.info("Importing records & verifiying under bulk operation");

        LOGGER.info("\n\n================ Can Bulk Import & Select RocksDB Test ================\n");
    }
    
    
    /**
     * Tests whether an {@link Item} collection can be inserted,
     *  and verified under the one bulk operation for RocksDB-{@link ItemStore}
     * 
     */
    @Test
    @Order(2)
    public void canImportAndSelectInOneLinkedOpsRocksDb() {
    
        // Initialize test
        LOGGER.info("\n\n================ Linked Ops RocksDB Test ===============\n");
        boolean assertionState;
        ItemStore donor, recipient;
        String storeName;
        List<Item> records;
        int nRecords = 10;
        
        // Configure ItemStore & records
        LOGGER.info("Configuring RocksDB ItemStore and '{}' test records", nRecords);
        storeName = "LinkedOps-RocksDB";
        donor = ItemStoreTestUtils.createRocksDbStore(storeName);
        recipient = ItemStoreTestUtils.createRocksDbStore(storeName + "-recipient");
        records = ItemStoreTestUtils.fetchRandomItems(nRecords);
        
        // Perform import and verification as bulk operation
        LOGGER.info("Importing records & verifiying under bulk operation");
        
        LOGGER.info("\n\n================ Linked Ops RocksDB Test ================\n");
    }
}