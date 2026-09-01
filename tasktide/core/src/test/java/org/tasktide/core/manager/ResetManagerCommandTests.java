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

import org.testcontainers.containers.GenericContainer;
import org.junit.Rule;

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

import org.tasktide.TestEnvironment;
import org.tasktide.TestUtils;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.command.commands.ResetCommand;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.JpaRepository;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;


/**
 * Tests the {@link ResetCommand} {@link ManagerCommand} via
 *  {@link JpaRepository} using {@link GenericContainer}
 * 
 * @author Brendan Kenna
 */
@Tag("system-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ResetManagerCommandTests {
    
    private static final Logger LOGGER = LogManager.getLogger(ResetManagerCommandTests.class);
    
    // Backend repo
    //@Rule
    //public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    // Container for fetch template/entity manager
    private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    
    public ResetManagerCommandTests() {
    }
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Reset Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-config.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
        
        try {
            TestUtils.initServiceManager(RepositoryType.SQL, entityManager);
        }
        catch ( Exception ex ) {}
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Reset Manager Command Tests ----------------\n";
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
     * Teste resetting specific {@link WorkItem}
     */
    @Test
    @Order(0)
    public void canResetWorkItem() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Reset ManagerCommand Test ================\n");
        String forUnlock = "WorkItem-424bfad0-4041-4b57-ac4b-027507cbd811";
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.RESET_ITEM;
        CommandSpec cmdSpec;
        ResetCommand cmd;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records");
        TestUtils.importTestRecords("import-docs.json", "SequenceAlignment", "JSON");
        WorkItem preCmd = TaskTideServiceManager.fetchWorkItemService().fetchById(forUnlock);
        LOGGER.info("Displaying WorkItem for reset:\n'{}'", preCmd.toJsonDoc());
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Item Id", forUnlock);
        cmdSpec = new CommandSpec(null, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Run command
        LOGGER.info("Constructing, and executing configured ResetCommand");
        cmd = (ResetCommand) action.makeCommand(target, cmdSpec);
        assertionState = (boolean) cmd.execute();
        preCmd = TaskTideServiceManager.fetchWorkItemService().fetchById(forUnlock);
        LOGGER.info("Displaying WorkItem post reset:\n'{}'", preCmd.toJsonDoc());
        
        // Log state
        LOGGER.info("\n\n================ Can Reset ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests dynamic resetting of a collection of WorkItems
     * 
     */
    @Test
    @Order(1)
    public void canResetList() {
        
        // Construct work item
        LOGGER.info("\n\n================ Can Reset List ManagerCommand Test ================\n");
        String resetList = TestUtils.fetchResourcePath("For-Reset.txt").toString();
        String[] forUnlock = { 
            "WorkItem-424bfad0-4041-4b57-ac4b-027507cbd811",
            "WorkItem-5bf73c7a-5903-4972-88b1-5ff94389ad98",
            "WorkItem-50a47baf-0ea6-43e8-ae2c-4fcc21f9f6a5",
            "WorkItem-cf1ffbbe-4bc3-408f-81ed-139e029ce249"
        };
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.RESET_ITEMS;
        CommandSpec cmdSpec;
        ResetCommand cmd;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records, displaying WorkItems for unlocking");
        TestUtils.importTestRecords("import-docs.json", "SequenceAlignment", "JSON");
        TestUtils.printEach(forUnlock);
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", ",");
        cmdSpec = new CommandSpec(resetList, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Create and run command
        LOGGER.info("Constructing, and executing configured ResetCommand");
        cmd = (ResetCommand) action.makeCommand(target, cmdSpec);
        assertionState = (boolean) cmd.execute();
        
        // Evaluate test
        if ( assertionState ) {
            TestUtils.printEach(forUnlock);
        }
        else {
            LOGGER.error("Unable to reset test tokens");
        }
        
        // Log state
        LOGGER.info("\n\n================ Can Reset List ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
}