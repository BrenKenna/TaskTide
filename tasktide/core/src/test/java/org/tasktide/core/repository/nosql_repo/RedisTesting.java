/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.repository.nosql_repo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.assertTrue;


import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import jakarta.nosql.Template;

import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;
import org.eclipse.jnosql.mapping.keyvalue.spi.KeyValueExtension;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.core.repository.Tunes;



/**
 *
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {Converters.class, EntityConverter.class, Template.class, KeyValueTemplate.class})
@AddPackages(value = {Tunes.class, Reflections.class})
@AddExtensions( {ReflectionEntityMetadataExtension.class, KeyValueExtension.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisTesting {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(RedisTesting.class);
    
    private SeContainer container;
    
    private Template template;
    
    public RedisTesting() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating JNoSQL-MongoDB Tests ----------------\n";
        logger.info(msg);
        
        // Initialize CDI
        container = SeContainerInitializer.newInstance().initialize();
        logger.info(container.isRunning());
        //new DocumentTemplate();
        template = (Template) container.select(KeyValueTemplate.class).get();
        logger.info("MongoDB template state" + template == null);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating JNoSQL-MongoDB Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down.");
        }
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
    @Order(0)
    public void shouldInjectCouchDBTemplate() {
        Assertions.assertNotNull(template);
    }
    
    
    /**
     * Test manual connection
     */
    @Test
    @Order(2)
    public void shouldInsertRecord() {
    
        // Fetch document template
        logger.info("\n\n================ Injected Connection Test ================\n");
        boolean assertionState = true;
        logger.info("\n\nCreated template:\n" + template);
        
        // Make a music record
        Tunes song = new Tunes();
        song.setId("Id");
        song.setName("Imagine");
        song.setArtist("John Lennon");
        logger.info("\n\nDisplaying record for import:\n" + song);
        
        // Insert record DocumentEntity.of("Tunes");
        Tunes inserted = template.insert(song);
        logger.info("\n\nRetrieved import:\n");
        assertionState = inserted.getName().length() > 0;
        
        // Log state
        if (!assertionState) {
            assertTrue(assertionState);
            logger.info("\n\nTest successful music model recieved from inster:\n" + inserted);
        }
        else {
            assertTrue(assertionState);
            logger.error("\n\nTest failed music model recieved from inster:\n" + inserted);
        }
        logger.info("\n\n================ Injected Connection Test ================\n");
    }
}