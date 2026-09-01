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

import org.tasktide.mutex.utils.FileUtility;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Module to test concurrent threads reading/writing
 *  to same {@link ItemStore} for both {@link RocksDBStore},
 *  and {@link SqliteStore}
 * 
 * @author Brendan Kenna
 */
@Tag("experimental-itemstore")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ConcurrencyTests {
    
    private final Logger LOGGER = LogManager.getLogger(ItemTests.class);
    private final String STORE_DIRS = "./ConcurrencyTests";
    private final String SQL_STORE_DIR = STORE_DIRS + "/SqlStore"; 
    private final String ROCKS_STORE_DIR = STORE_DIRS + "/RocksStore";
    private final ItemStore ROCKS_STORE, SQL_STORE;
    private final int N_THREADS, TASKS_PER_THREAD;
    private final List<Future<?>> TASKS;
    
    public ConcurrencyTests() throws IOException {
        FileUtility.recursiveDelete(Paths.get(STORE_DIRS));
        ROCKS_STORE = ItemStoreUtils.getStore(ItemStoreType.ROCKSDB, ROCKS_STORE_DIR);
        SQL_STORE = ItemStoreUtils.getStore(ItemStoreType.SQLITE, SQL_STORE_DIR);
        N_THREADS = 3;
        TASKS_PER_THREAD = 10;
        TASKS = new ArrayList<>();
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating ItemStore Concurrency Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemStore Concurrency Tests ----------------\n";
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
     * Create a test set for each thread
     * 
     * @param nThreads
     * @return List-List-{@link Item}
     */
    public List<List<Item>> makeTestSet(int nThreads) {
        List<List<Item>> testSet = new ArrayList<>();
        for ( int i = 0; i < nThreads; i++ ) {
            String label = "thread-" + i;
            testSet.add(ItemStoreUtils.makeMockItemCollection(this.TASKS_PER_THREAD, label));
            
        }
        return testSet;
    }
    
    
    /**
     * Persist data to provided {@link ItemStore}
     * 
     * @param store
     * @param data
     * @return boolean
     */
    public boolean writeData(ItemStore store, List<Item> data) {
    
        String label = data.get(0).getState();
        try {
            
            // Write data
            store.saveItems(DbTarget.MASTER, data);
            
            // Check import size matches expected
            List<Item> imports = store.getItemsByState(DbTarget.MASTER, label);
            LOGGER.info("Imported size = '{}', Input Size = '{}'", imports.size(), data.size());
            return imports.size() == data.size();
                    
        } catch (Exception ex) {
            return false;
        }
    }
    
    
    /**
     * Wait for tasks to complete, returning whether
     *  all tasks completed successfully
     * 
     * @param tasks
     * 
     */
    public void waitFor(List<Future<?>> tasks) {

        // Wait for tasks to complete
        int successful = 0;
        for (Future<?> task : tasks) {
            try {
                boolean result = (boolean) task.get(); // waits until finished
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        LOGGER.info("N tasks = '{}' completed");
    }

    
    /**
     * Tests concurrency of {@link SqliteStore}
     * 
     */
    @Test
    @Order(0)
    public void testSqlItemStoreConcurrency() {

        // Init vars
        LOGGER.info("\n\n================ Can Cocurrently Read/Write to SQLite ItemStore ================\n");
        boolean assertionState;
        int TOTAL_TASKS = (this.N_THREADS * this.TASKS_PER_THREAD);
        
        // Create test data
        LOGGER.info("Creating test set for each thread");
        this.TASKS.clear();
        List<List<Item>> testSet = makeTestSet(this.N_THREADS);

        // Create executor service and submit tasks
        LOGGER.info("Submitting concurrency tasks with SQL backend");
        ExecutorService execServ = Executors.newFixedThreadPool(this.N_THREADS);

        // Submit work
        for (List<Item> data : testSet) {
            Future<?> task = execServ.submit(() -> {
                return this.writeData(SQL_STORE, data);
            });
            this.TASKS.add(task);
        }

        // Wait for tasks
        LOGGER.info("Tasks submitted, waiting for results");
        this.waitFor(this.TASKS);
        assertionState = SQL_STORE.getAll(DbTarget.MASTER).size() == TOTAL_TASKS;
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.info("SQLite concurrency test successful");
        }
        else {
            LOGGER.error("SQLite concurrency test unsuccessful");
        }
        assertTrue(assertionState);
        LOGGER.info("\n\n================ Can Cocurrently Read/Write to SQLite ItemStore ================\n");
    }
    
    
    
    /**
     * Tests concurrency of {@link RocksDbStore}
     * 
     */
    @Test
    @Order(1)
    public void testRocksDbItemStoreConcurrency() {

        // Init vars
        LOGGER.info("\n\n================ Can Cocurrently Read/Write to RocksDB ItemStore ================\n");
        boolean assertionState;
        int TOTAL_TASKS = (this.N_THREADS * this.TASKS_PER_THREAD);
        
        // Create test data
        LOGGER.info("Creating test set for each thread");
        this.TASKS.clear();
        List<List<Item>> testSet = makeTestSet(this.N_THREADS);

        // Create executor service and submit tasks
        LOGGER.info("Submitting concurrency tasks with SQL backend");
        ExecutorService execServ = Executors.newFixedThreadPool(this.N_THREADS);

        // Submit work
        for (List<Item> data : testSet) {
            Future<?> task = execServ.submit(() -> {
                return this.writeData(this.ROCKS_STORE, data);
            });
            this.TASKS.add(task);
        }

        // Wait for tasks
        LOGGER.info("Tasks submitted, waiting for results");
        this.waitFor(this.TASKS);
        assertionState = ROCKS_STORE.getAll(DbTarget.MASTER).size() == TOTAL_TASKS;
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.info("RocksDB concurrency test successful");
        }
        else {
            LOGGER.error("RocksDB concurrency test unsuccessful");
        }
        assertTrue(assertionState);
        LOGGER.info("\n\n================ Can Cocurrently Read/Write to RocksDB ItemStore ================\n");
    }
}