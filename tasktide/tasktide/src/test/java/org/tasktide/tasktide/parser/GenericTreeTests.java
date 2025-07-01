/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide.parser;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
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
import org.tasktide.tasktide.parser.model.GenericTree;


/**
 *
 * 
 * @author bkenna
 */
public class GenericTreeTests {
    
    private static final Logger logger = LogManager.getLogger(GenericTreeTests.class);
    private static final JsonbConfig config = new JsonbConfig().withFormatting(true);
    private static final JsonbConfig configNoIndent = new JsonbConfig();
    
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
     */
    @Test
    @Order(0)
    public void canSetupTree() {
    
        // Initialize test
        logger.info("\n\n================ Can Setup Tree Test ================\n");
        boolean assertionState;
        String template;
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
        
        // Check size
        treeSize = myTree.size();
        assertionState = treeSize >= 1;
        Map<String, String> treeData = myTree.toAddressDataMap();
        Jsonb jsonb = JsonbBuilder.create(config);
        logger.info("\n\nDisplaying Tree:\n\n{}\n", jsonb.toJson(treeData));
        
        // Evaluate test
        assertTrue(assertionState, "Not all records added");
        logger.info("\n\n================ Can Setup Tree Test ================\n");
    }
}
