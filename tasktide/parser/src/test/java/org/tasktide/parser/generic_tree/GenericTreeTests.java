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
package org.tasktide.parser.generic_tree;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;


/**
 * Tests the GenericTree module
 * 
 * @author bkenna
 */
@Tag("unit-parser")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenericTreeTests {
    
    private static final Logger logger = LogManager.getLogger(GenericTreeTests.class);
    private static final Jsonb PRETTY_JSON = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private static final Jsonb JSON = JsonbBuilder.create(new JsonbConfig());
    
    public GenericTreeTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Generic Tree Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Generic Tree Tests ----------------\n";
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
     * Test that records can be added to the tree, and display in log
     * 
     */
    @Test
    @Order(0)
    public void canSetupTree() {
    
        // Initialize test
        logger.info("\n\n================ Can Setup Tree Test ================\n");
        boolean assertionState;
        int treeSize;
        GenericTree<String> myTree;
        
        // Create tree and add some records
        myTree = new GenericTree<>("/");
        myTree.addChild("red/apple", "Apple");
        myTree.addChild("red/cherry", "Cherry");
        myTree.addChild("red/strawberry", "Strawberry");

        myTree.addChild("yellow/banana", "Banana");
        myTree.addChild("yellow/lemon", "Lemon");
        myTree.addChild("yellow/pineapple", "Pineapple");

        myTree.addChild("green/lime", "Lime");
        myTree.addChild("green/kiwi", "Kiwi");
        myTree.addChild("green/grape", "Grape");
        
        myTree.addChild("secondary/multicolor/large", "papya");
        
        // Check size
        treeSize = myTree.size();
        assertionState = treeSize == 10;
        logger.info("Tree size is:\t'{}'", treeSize);
        logger.info("\n\nDisplaying Tree:\n\n{}\n", PRETTY_JSON.toJson(myTree.toAddressDataMap()));
        
        // Evaluate test
        assertTrue(assertionState, "Not all records added");
        logger.info("\n\n================ Can Setup Tree Test ================\n");
    }
    
    
    /**
     * Test find/removing a datapoint
     * 
     */
    @Test
    @Order(1)
    public void canFindAndRemove() {
    
        // Initialize test
        logger.info("\n\n================ Can Add/Remove to Tree Test ================\n");
        boolean assertionState = false;
        GenericTree<String> myTree;
        
        // Create tree and add some records
        myTree = new GenericTree<>("/");
        myTree.addChild("red/apple", "Apple");
        myTree.addChild("yellow/lemon", "Lemon");
        myTree.addChild("green/grape", "Grape");
        
        // Find by address, and value
        String value = myTree.getDataForAddress("red/apple");
        if ( value != null ) {
            logger.info("Successfully found address:\t\"{}\"", "red/apple");
            if ( myTree.containsData(value) ) {
                logger.info("Successfully queried value:\t\"{}\"", "Apple");
                assertionState = myTree.removeByAddress("red/apple");
            }
        }
        logger.info(
            "\n\nDisplaying Tree After Test:\n\n{}\n",
            PRETTY_JSON.toJson(myTree.toAddressDataMap())
        );
        
        // Evaluate test
        assertTrue(assertionState, "Unable to query/remove data");
        logger.info("\n\n================ Can Add/Remove to Tree Test ================\n");
    }
    
    
    
    /**
     * Test that records can be added to the tree under an unexisting branch
     * 
     */
    @Test
    @Order(2)
    public void canAddUnderNonExistingBranch() {
    
        // Initialize test
        logger.info("\n\n================ Can Add Under Unexting Branch Test ================\n");
        boolean assertionState;
        String testData = "papya", testAddress = "secondary/multicolor/large";
        GenericTree<String> myTree;
        
        // Create tree and add some records
        myTree = new GenericTree<>("/");
        myTree.addChild("red/apple", "Apple");
        myTree.addChild(testAddress, testData);
        
        // Check size
        assertionState = myTree.containsData("papya");
        logger.info("\n\nDisplaying Tree:\n\n{}\n", PRETTY_JSON.toJson(myTree.toAddressDataMap()));
        
        // Evaluate test
        assertTrue(assertionState, "Not all records added");
        logger.info("\n\n================ Can Add Under Unexting Branch Test ================\n");
    }
}
