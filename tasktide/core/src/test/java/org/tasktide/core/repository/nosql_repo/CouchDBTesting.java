/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;

import jakarta.enterprise.inject.se.SeContainer;

import jakarta.nosql.Template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.TestEnvironment;

import org.testcontainers.containers.GenericContainer;


/**
 * Test module for reading/writing records to couchDB docker container
 * 
 * @author bkenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouchDBTesting {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(CouchDBTesting.class);
    
    // CouchDB container
    @Rule
    public GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    // Container for fetch nosql template
    private SeContainer container;
    private Template template;
    
    
    public CouchDBTesting() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating JNoSQL-CouchDB Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = TestEnvironment.fetchDocumentTemplate(container);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating JNoSQL-CouchDB Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
        couchDB.stop();
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
     * Tests fetching document template
     */
    @Test
    @Order(0)
    public void shouldInjectCouchDBTemplate() {
        logger.info("\n\n================ Tests DocumentTemplate Injection ================\n");
        Assertions.assertNotNull(template);
        logger.info("\n\n================ Tests DocumentTemplate Injection ================\n");
    }
    
    
    /**
     * Tests running couchDB container
     */
    @Test
    @Order(1)
    public void containerShouldBeRunning() {
        logger.info("\n\n================ Tests CouchDB Container Spinup ================\n");
        Assertions.assertTrue(couchDB.isRunning());
        logger.info("Displaying CouchDB Container status:\t'{}'", couchDB.getContainerInfo());
        logger.info("\n\n================ Tests CouchDB Container Spinup ================\n");
    }
    
    
    /**
     * Tests insertion to container through template
     */
    @Test
    @Order(2)
    public void shouldInsertRecord() {
    
        // Fetch document template
        logger.info("\n\n================ Tests CouchDB Insertion ================\n");
        boolean assertionState;
        logger.info("\n\nCreated template:\n" + template);
        
        // Make a music record
        Tunes song = new Tunes();
        song.setId("Id");
        song.setName("Imagine");
        song.setArtist("John Lennon");
        logger.info("\n\nDisplaying record for import:\n" + song);
        
        // Insert record DocumentEntity.of("Tunes");
        Tunes inserted = template.insert(song);
        assertionState = inserted != null;
        
        // Log state
        if (!assertionState) {
            assertTrue(assertionState);
            logger.info("\n\nTest successful music model recieved from inster:\n" + inserted);
        }
        else {
            assertTrue(assertionState);
            logger.error("\n\nTest failed music model recieved from inster:\n" + inserted);
        }
        logger.info("\n\n================ Injected CouchDB Insertion Test ================\n");
    }
}