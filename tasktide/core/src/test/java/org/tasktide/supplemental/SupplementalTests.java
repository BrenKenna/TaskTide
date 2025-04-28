/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.supplemental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Unit tests on supplemental things like printing splash
 * 
 * @author bkenna
 */
public class SupplementalTests {
    
    private static final Logger logger = LogManager.getLogger(SupplementalTests.class);
    
    public SupplementalTests() {}
    
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Supplemental Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Supplemental Tests ----------------\n";
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
     * Print splash
     * 
     * @throws IOException 
     */
    public void printSplash() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("splash.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        } catch (IOException e) {
            throw e;
        }
    }
    
    
    /**
     * Fetches splash string
     * 
     * @return String
     * @throws IOException 
     */
    public String fetchSplashString() throws IOException {
        String output = "";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("splash.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output += "\n" + line;
            }
            return output;
        } catch (IOException e) {
            throw e;
        }
    }
    
    
    /**
     * Test printing the splash resource
     */
    @Order(0)
    @Test
    public void testSplash() {
    
        // Initialize test
        boolean assertionState;
        logger.info("\n\n================ Print Splash Test ================\n");
        
        
        // Try pring splash
        try {
            logger.info("\n\nDisplaying splash string:" + fetchSplashString() + "\n\n");
            assertionState = true;
        } catch (IOException ex) {
            assertionState = false;
            logger.error("\n\nError printing splash:\n" + ex);
        }
        
        // Log status
        logger.info("\n\n================ Print Splash Test ================\n");
        assertTrue(assertionState);
    }
}
