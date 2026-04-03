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
package org.tasktide.itemstore;

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

import org.tasktide.mutex.utils.MutexFilesUtils;

// For Java Docs
import org.tasktide.itemstore.session.ItemStoreSession;
import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.itemstore.session.LinkedOperation;


/**
 * Suite of tests for {@link ItemStoreSession} and supporting
 *  {@link BulkOperation}, and {@link LinkedOperation}
 *
 * @author Brendan Kenna
 */
public class ItemStoreSessionTests {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemStoreSessionTests.class);
    
    public ItemStoreSessionTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemStore Session Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
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
     *  and verified under the one bulk operation for SQLite-{@link ItemStore}
     * 
     */
    @Test
    @Order(0)
    public void canImportAndSelectInOneOpsSqlite() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Bulk Import & Select SQLite Test ================\n");
        boolean assertionState;
        ItemStore itemStore;
        String storeName;
        List<Item> records;
        int nRecords = 10;
        
        // Configure ItemStore & records
        LOGGER.info("Configuring SQLite ItemStore and '{}' test records", nRecords);
        storeName = "BulkOps-Sqlite";
        itemStore = ItemStoreTestUtils.createSqliteStore(storeName);
        records = ItemStoreTestUtils.fetchRandomItems(nRecords);
        
        // Perform import and verification as bulk operation
        LOGGER.info("Importing records & verifiying under bulk operation");
        assertionState = itemStore.execute(DbTarget.MASTER, session -> {
            
            // Try import all records
            Item record;
            LOGGER.info("Inserting records");
            if ( session.importItems(records) ) {
                
                // Check that the first is present
                record = session.getById( records.get(0).getId() );
                LOGGER.info(
                    "Displaying inserted record:\n'{}'",
                    MutexFilesUtils.toJson(record)
                );
                return record != null;
            }
            
            // Otherwise fail test
            LOGGER.info("Failed to insert records");
            return false;
        });
        
        // Evaluate test
        LOGGER.info("Evaluating SQLite ItemStore Session Test");
        assertTrue(assertionState, "Error creating ItemStore");
        LOGGER.info("\n\n================ Can Bulk Import & Select SQLite Test ================\n");
    }
    
    
    /**
     * Tests whether an {@link Item} collection can be inserted,
     *  and verified under the one bulk operation for RocksDB-{@link ItemStore}
     * 
     */
    @Test
    @Order(1)
    public void canImportAndSelectInOneOpsRocksDb() {
    
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
        assertionState = itemStore.execute(DbTarget.MASTER, session -> {
            
            // Try import all records
            Item record;
            LOGGER.info("Inserting records");
            if ( session.importItems(records) ) {
                
                // Check that the first is present
                record = session.getById( records.get(0).getId() );
                LOGGER.info(
                    "Displaying inserted record:\n'{}'",
                    MutexFilesUtils.toJson(record)
                );
                return record != null;
            }
            
            // Otherwise fail test
            LOGGER.info("Failed to insert records");
            return false;
        });
        
        // Evaluate test
        LOGGER.info("Evaluating RocksDB ItemStore Session Test");
        assertTrue(assertionState, "Error creating ItemStore");
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
        assertionState = donor.execute(DbTarget.MASTER, recipient, (sessionA, sessionB) -> {
            
            // Try import all records
            int count = 0;
            Item record;
            LOGGER.info("Inserting records into sessionA");
            if ( sessionA.importItems(records) ) {
                
                // Check that the first is present
                record = sessionA.getById( records.get(0).getId() );
                LOGGER.info(
                    "Displaying inserted record:\n'{}'",
                    MutexFilesUtils.toJson(record)
                );
                if ( record != null ) {
                    count++;
                }
            }
            
            // Import into sessionB
            LOGGER.info("Inserting records into sessionB");
            record = null;
            if ( sessionB.importItems(records) ) {
                
                // Check that the first is present
                record = sessionB.getById( records.get(0).getId() );
                LOGGER.info(
                    "Displaying inserted record:\n'{}'",
                    MutexFilesUtils.toJson(record)
                );
                if ( record != null ) {
                    count++;
                }
            }
            
            // Otherwise fail test
            LOGGER.info("Check results");
            return count == 2;
        });
        
        // Evaluate test
        LOGGER.info("Evaluating RocksDB ItemStore Linked Session Test");
        assertTrue(assertionState, "Error creating ItemStore");
        LOGGER.info("\n\n================ Linked Ops RocksDB Test ================\n");
    }
    
    
    
    /**
     * Tests whether an {@link Item} collection can be inserted,
     *  and verified under the one bulk operation for SQLite-{@link ItemStore}
     * 
     */
    @Test
    @Order(2)
    public void canImportAndSelectInOneLinkedOpsSqlite() {
    
        // Initialize test
        LOGGER.info("\n\n================ Linked Ops SQLite Test ===============\n");
        boolean assertionState;
        ItemStore donor, recipient;
        String storeName;
        List<Item> records;
        int nRecords = 10;
        
        // Configure ItemStore & records
        LOGGER.info("Configuring SQLite ItemStore and '{}' test records", nRecords);
        storeName = "LinkedOps-Sqlite";
        donor = ItemStoreTestUtils.createSqliteStore(storeName);
        recipient = ItemStoreTestUtils.createSqliteStoreNoElection(storeName + "-recipient");
        records = ItemStoreTestUtils.fetchRandomItems(nRecords);
        
        // Perform import and verification as bulk operation
        LOGGER.info("Importing records & verifiying under bulk operation");
        assertionState = donor.execute(DbTarget.MASTER, recipient, (sessionA, sessionB) -> {
            
            // Try import all records
            int count = 0;
            Item record;
            LOGGER.info("Inserting records into sessionA");
            if ( sessionA.importItems(records) ) {
                
                // Check that the first is present
                record = sessionA.getById( records.get(0).getId() );
                LOGGER.info(
                    "Displaying inserted record:\n'{}'",
                    MutexFilesUtils.toJson(record)
                );
                if ( record != null ) {
                    count++;
                }
            }
            
            // Import into sessionB
            LOGGER.info("Inserting records into sessionB");
            record = null;
            if ( sessionB.importItems(records) ) {
                
                // Check that the first is present
                record = sessionB.getById( records.get(0).getId() );
                LOGGER.info(
                    "Displaying inserted record:\n'{}'",
                    MutexFilesUtils.toJson(record)
                );
                if ( record != null ) {
                    count++;
                }
            }
            
            // Otherwise fail test
            LOGGER.info("Check results");
            return count == 2;
        });
        
        // Evaluate test
        LOGGER.info("Evaluating SQLite ItemStore Linked Session Test");
        assertTrue(assertionState, "Error creating ItemStore");
        LOGGER.info("\n\n================ Linked Ops SQLite Test ================\n");
    }
}