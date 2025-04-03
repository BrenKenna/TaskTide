/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.model.tasklogging;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


/**
 *
 * @author bkenna
 */
public class TaskLoggingTest {
    
    private static final Logger logger = LogManager.getLogger(TaskLoggingTest.class);
    
    public TaskLoggingTest() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating TaskLogging Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating TaskLogging Tests ----------------\n";
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

    
    @Test
    //@Order(1)
    public void processLogTest() {
        boolean assertionState = true;
        logger.info("\n\n================ Process Log Test ================\n");
        
        System.out.println("hello");
        
        logger.info("\n\n================ Process Log Test ================\n");
        assertTrue(assertionState);
    }
}
