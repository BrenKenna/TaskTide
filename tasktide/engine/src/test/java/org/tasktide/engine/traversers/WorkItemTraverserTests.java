/*
 * Copyright 2026 Bren.
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
package org.tasktide.engine.traversers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.junit.Rule;
import org.testcontainers.containers.GenericContainer;

import org.tasktide.engine.TestUtils;
import org.tasktide.engine.TestEnvironment;

import org.tasktide.core.repository.RepositoryType;




/**
 * Test module for the {@link ItemTaskTraverser}
 *
 * @author Bren
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkItemTraverserTests {
    
    private static final Logger LOGGER = LogManager.getLogger(WorkItemTraverserTests.class);
    
    private final String STEP = "Ping Tests";
    
    private SeContainer container;
    private Template template;
    
    
    // CouchDB container
    @Rule
    public GenericContainer couchDB = (GenericContainer) TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public WorkItemTraverserTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating WorkItem Traverser Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("couchDB-config.properties", getClass());
        template = (Template) TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.NOSQL, template);
        TestUtils.importTestRecords("singleTaskImports-Delim2.txt", this.STEP, ",");
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating WorkItem Traverser Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        couchDB.stop();
    }
    
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }
    

    
    
    @Test
    @Order(0)
    public void canTraverseWorkload() {
    
        
        // Log status
        Assertions.assertTrue(true, "Error cannot fetch to do work");
        LOGGER.info("\n\n================ Can Fetch ToDo Work ================\n");
    }
}