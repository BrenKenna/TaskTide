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
import org.tasktide.itemstore.utils.ItemStoreTestUtils;
import org.tasktide.itemstore.utils.ItemStoreUtils;


/**
 * Test module for {@link ItemStoreLockStrategy}
 *
 * @author Bren
 */
@Tag("unit-itemstore")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemStoreLockStrategyTests {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(ItemStoreLockStrategyTests.class);
    private String storeName = "lock-strategy";
    private Path workDir;
    private ItemStore itemStore;
    
    
    public ItemStoreLockStrategyTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating ItemStore LockStrategy Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating ItemStore LockStrategy Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
        this.workDir = ItemStoreTestUtils.setWorkingDirectory("rocksDB", this.storeName);
        this.itemStore = ItemStoreTestUtils.makeRocksDB(this.storeName, this.workDir);
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
     * Tests locking through {@link ItemStoreLockStrategy}
     * 
     */
    @Test
    @Order(0)
    public void canLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Lock ItemStore ================\n");
        boolean assertionState;
        ItemStoreLockStrategy lockStrat;
        
        // Configure
        LOGGER.info("Configuring lock strategy");
        lockStrat = new ItemStoreLockStrategy(
            this.storeName,
            this.workDir
        );
        Assertions.assertTrue(
            lockStrat.getFileLock() == null,
            "LockStrategy should start in a locked state"
        );
        
        // Commence lock
        try {
            LOGGER.info("Attempting to acquire lock");
            lockStrat.waitForLock();
            LOGGER.info(
                "Displaying FileChannelLock state:\t'{}'",
                 lockStrat.getFileLock().isValid()
            );
            Assertions.assertTrue(
                lockStrat.getFileLock().isValid(),
                "File channel lock not valid"
            );
            assertionState = true;
        }
        catch (IOException | InterruptedException ex) {
            Assertions.assertTrue(false, "Failed to lock target");
            assertionState = false;
        }
        
        // Release lock on resource
        finally {
            lockStrat.releaseLock(true);
        }
        
        // Evaluate test
        Assertions.assertTrue(assertionState, "Could not lock Item");
        LOGGER.info("\n\n================ Can Lock ItemStore ================\n");
    }
    
    
    /**
     * Tests that an operation can occur under
     *  lock
     * 
     */
    @Test
    @Order(1)
    public void canOperateWithLock() {
    
        // Initialize test
        LOGGER.info("\n\n================ Can Operate With Lock ================\n");
        boolean assertionState;
        ItemStoreLockStrategy lockStrat;
        
        // Configure
        LOGGER.info("Configuring lock strategy");
        lockStrat = new ItemStoreLockStrategy(
            this.storeName,
            this.workDir
        );
        
        // Commence lock
        LOGGER.info("Verifying that operation can occur under lock");
        try {
            assertionState = lockStrat.withLock(
                "Unit Tests",
                () -> {
                    boolean value = lockStrat.getFileLock().isValid();
                    LOGGER.info(
                        "LockStrategy file channel lock state:\t'{}'",
                        value
                    );
                    return value;
            });
            LOGGER.info("Execution successful");
        }
        catch (ItemStoreCheckedException ex) {
            assertionState = false;
        }
        
        // Evaluate test
        Assertions.assertTrue(assertionState, "Could not perform operation under lock strategy");
        LOGGER.info("\n\n================ Can Operate With Lock ================\n");
    }
}
