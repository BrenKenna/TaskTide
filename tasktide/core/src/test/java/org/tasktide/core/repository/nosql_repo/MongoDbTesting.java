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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.TestEnvironment;

import org.testcontainers.containers.GenericContainer;


/**
 * Test module for reading/writing records to mongoDB docker container
 * 
 * @author bkenna
 */
@Tag("unit-repo-nosql")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoDbTesting {
    
    // Logger for tests
    private static final Logger logger = LogManager.getLogger(MongoDbTesting.class);
    
    // CouchDB container
    @Rule
    public GenericContainer<?> mongoDB = TestEnvironment.mongoDbContainer("tasktide_database");
    
    // Container for fetch nosql template
    private SeContainer container;
    private Template template;
    
    
    public MongoDbTesting() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating JNoSQL-MongoDB Testing Tests ----------------\n";
        logger.info(msg);
        container = TestEnvironment.startWeldContainer("mongoDB-config.properties", getClass());
        template = TestEnvironment.fetchDocumentTemplate(container);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating JNoSQL-MongoDB Testing Tests ----------------\n";
        logger.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            logger.info("CDI container shut down");
        }
        mongoDB.stop();
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
     * Tests running mongoDB container
     */
    @Test
    @Order(1)
    public void containerShouldBeRunning() {
        logger.info("\n\n================ Tests MongoDB Container Spinup ================\n");
        Assertions.assertTrue(mongoDB.isRunning());
        logger.info("Displaying MongoDB Container status:\t'{}'", mongoDB.getContainerInfo());
        logger.info("\n\n================ Tests MongoDB Container Spinup ================\n");
    }
    
    
    /**
     * Tests insertion to container through template
     */
    @Test
    @Order(2)
    public void shouldInsertRecord() {
    
        // Fetch document template
        logger.info("\n\n================ Tests MongoDB Insertion ================\n");
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
        logger.info("\n\n================ Injected MongoDB Insertion Test ================\n");
    }
}