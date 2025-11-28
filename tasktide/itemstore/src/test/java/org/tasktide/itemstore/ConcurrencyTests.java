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

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;


/**
 * Module to test concurrent threads reading/writing
 *  to same {@link ItemStore} for both {@link RocksDBStore},
 *  and {@link SqliteStore}
 * 
 * @author Brendan Kenna
 */
public class ConcurrencyTests {
    
    private final Logger LOGGER = LogManager.getLogger(ItemTests.class);
    private final String STORE_DIRS = "./ConcurrencyTests";
    private final String SQL_STORE_DIR = STORE_DIRS + "/SqlStore"; 
    private final String ROCKS_STORE_DIR = STORE_DIRS + "/RocksStore";
    private final ItemStore ROCKS_STORE, SQL_STORE;
    
    public ConcurrencyTests() {
        ROCKS_STORE = ItemStoreUtils.getStore(ItemStoreType.ROCKSDB, ROCKS_STORE_DIR);
        SQL_STORE = ItemStoreUtils.getStore(ItemStoreType.SQLITE, SQL_STORE_DIR);
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
    public List<List<Item<String>>> makeTestSet(int nThreads) {
        List<List<Item<String>>> testSet = new ArrayList<>();
        for ( int i = 0; i < nThreads; i++ ) {
            testSet.add(ItemStoreUtils.makeMockItemCollection(10));
        }
        return testSet;
    }
    
    
    @Test
    @Order(0)
    public void testSqlItemStoreConcurrencyWrites() {
    
        // Create test data
        LOGGER.info("Creating test set for each thread");
        List<List<Item<String>>> testSet = makeTestSet(3);
    }
}
