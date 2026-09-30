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
package org.tasktide.itemstore.strategies;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;
import org.tasktide.itemstore.types.DbTarget;
import org.tasktide.itemstore.utils.ItemStoreTestUtils;
import org.tasktide.itemstore.utils.ItemStoreUtils;


/**
 * Test module for {@link ItemStoreConnectionStrategy}
 *
 * @author Bren
 */
@Tag("unit-itemstore")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemStoreConnectionStrategyTests {
    
    private final Logger LOGGER = LogManager.getLogger(ItemStoreConnectionStrategyTests.class);
    private Path workDir;
    private ItemStore itemStore;
    
    public ItemStoreConnectionStrategyTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating ItemStore ConnectionStrategy Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemStore ConnectionStrategy Tests ----------------\n";
        LOGGER.info(msg);
        
    }
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
        this.workDir = ItemStoreTestUtils.setWorkingDirectory("rocksDB", "connection-strategy");
        this.itemStore = ItemStoreTestUtils.makeRocksDB("connection-strategy", this.workDir);
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
        try {
            ItemStoreUtils.deleteRecursively(this.workDir);
        }
        catch (IOException ex) {
        }
    }

    
    /**
     * Verifies opening connection
     * 
     */
    @Test
    @Order(0)
    public void canOpenConnection() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Open Connection ================\n");
        boolean assertionState;
        
        // Open connection
        LOGGER.info("Evaluating open connection handle to DB");
        assertionState = ItemStoreConnectionStrategy.openConnection(
            DbTarget.MASTER,
            this.itemStore
        );
        itemStore.closeMaster();
        
        // Evaluate test
        Assertions.assertTrue(assertionState, "Could not open connection");
        LOGGER.info("\n\n================ Can Open Connection ================\n");
    }
    
    
    /**
     * Verifies closw connection
     * 
     */
    @Test
    @Order(1)
    public void canCloseConnection() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Close Connection ================\n");
        boolean assertionState = false;
        
        // Open connection
        LOGGER.info("Closing connection if opennable");
        if ( ItemStoreConnectionStrategy.openConnection(
                DbTarget.MASTER,
                this.itemStore
        ) ) {
            LOGGER.info("Connection opened, closing");
            assertionState = itemStore.closeMaster();
        }
        else {
            LOGGER.error("Could not open connection");
        }
        
        // Evaluate test
        Assertions.assertTrue(assertionState, "Could not close connection");
        LOGGER.info("\n\n================ Can Close Connection ================\n");
    }
    
    
    /**
     * Verifies passing operation through connection lifecycle
     * 
     */
    @Test
    @Order(2)
    public void canOperateThroughConnection() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Operate Connection Lifecycle ================\n");
        boolean assertionState;
        
        // Fetch connection from within conn ops
        LOGGER.info("Attempting to perform operation under a connection lifecycle");
        try {
            assertionState = ItemStoreConnectionStrategy.connectionOperation(
                DbTarget.MASTER,
                this.itemStore,
                () -> {
                    LOGGER.info("From open connection handle");
                    return this.itemStore.isOpen(DbTarget.MASTER);
            });
        }
        catch (ItemStoreCheckedException ex) {
            assertionState = false;
        }
        LOGGER.info("Operation eval state:\t'{}'", assertionState);
        Assertions.assertTrue(assertionState);
        
        // Evaluate test
        boolean postTestState = itemStore.isClosed(DbTarget.MASTER);
        LOGGER.info(
            "Verifiying that master is closed:\t'{}'",
            postTestState
        );
        Assertions.assertTrue(
            postTestState,
            "Could not perform operation under lock strategy"
        );
        LOGGER.info("\n\n================ Can Operate Connection Lifecycle ================\n");
    }
}
