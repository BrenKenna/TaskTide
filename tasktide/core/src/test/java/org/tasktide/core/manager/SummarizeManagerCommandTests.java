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
package org.tasktide.core.manager;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.testcontainers.containers.GenericContainer;

import org.tasktide.TestEnvironment;
import org.tasktide.TestUtils;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.repository.JpaRepository;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.command.commands.SummarizeCommand;
import org.tasktide.core.model.state_summary.StateSummary;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Tests {@link SummarizeCommand} {@link ManagerCommand} via
 *  {@link JpaRepository} using {@link GenericContainer}
 * 
 * @author Brendan Kenna
 */
@Tag("unit-manager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SummarizeManagerCommandTests {
    
    private static final Logger LOGGER = LogManager.getLogger(SummarizeManagerCommandTests.class);
    
    // Backend repo
    // @Rule
    // public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    // Container for fetch nosql template
    private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    
    public SummarizeManagerCommandTests() {
    }
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Summarize Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-config.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
        TestUtils.initServiceManager(RepositoryType.SQL, entityManager);
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Summarize Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        // mariaDB.stop();
    }
    
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }
    
    
    /**
     * Tests fetching {@link StateSummary} of the 'import-docs.json' workload {@link ItemState}
     */
    @Test
    @Order(0)
    public void canSummarize() {
        
        // Construct work item
        LOGGER.info("\n\n================ Can Summarize ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.SUMMARIZE;
        CommandSpec cmdSpec;
        SummarizeCommand cmd;
        StateSummary<ItemState> results;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records");
        TestUtils.importTestRecords("singleTaskImports.txt", "Arbitrary", "|");
        TestUtils.viewSteps();
        TestUtils.viewWorkItems();
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Create and run command
        LOGGER.info("Constructing, and executing configured SummarizeCommd");
        cmd = (SummarizeCommand) action.makeCommand(target, cmdSpec);
        Object output = cmd.execute();
        
        // Display results if retrieved
        if ( output != null ) {
            LOGGER.info("Casting ManagerCommand output to StateSummary-Map");
            results = (StateSummary<ItemState>) output;
            LOGGER.info("Displaying results:\n'{}'", JsonUtils.toJson(true, output));
            assertionState = true;
        }
        
        // Otherwise log failure
        else {
            LOGGER.error("Unable to retrieve StateSummary-Map");
            assertionState = false;
        }
        
        // Log state
        LOGGER.info("\n\n================ Can Summarize ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests fetching {@link StateSummary} of the 'import-docs.json' workload {@link ItemState}
     */
    @Test
    @Order(1)
    public void canSummarizeEach() {
        
        // Construct work item
        LOGGER.info("\n\n================ Can SummarizeEach ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.SUMMARIZE_EACH;
        CommandSpec cmdSpec;
        SummarizeCommand cmd;
        Map<String, StateSummary<ItemState>> results;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records");
        TestUtils.importTestRecords("singleTaskImports.txt", "Arbitrary", "|");
        TestUtils.viewSteps();
        TestUtils.viewWorkItems();
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Create and run command
        LOGGER.info("Constructing, and executing configured SummarizeCommd");
        cmd = (SummarizeCommand) action.makeCommand(target, cmdSpec);
        Object output = cmd.execute();
        
        // Display results if retrieved
        if ( output != null ) {
            LOGGER.info("Casting ManagerCommand output to StateSummary-Map");
            results = (Map<String, StateSummary<ItemState>>) output;
            LOGGER.info("Displaying results:\n'{}'", JsonUtils.toJson(true, output));
            assertionState = true;
        }
        
        // Otherwise log failure
        else {
            LOGGER.error("Unable to retrieve StateSummary-Map");
            assertionState = false;
        }
        
        // Log state
        LOGGER.info("\n\n================ Can SummarizeEach ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
}