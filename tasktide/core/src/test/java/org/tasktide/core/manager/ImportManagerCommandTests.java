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
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.GenericContainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.TestEnvironment;
import org.tasktide.TestUtils;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.services.ServiceFactory;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.command.commands.ImportCommand;

import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.repository.JpaRepository;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Tests the {@link ImportCommand} {@link ManagerCommand} via
 *  {@link JpaRepository} using {@link GenericContainer}
 * 
 * @author Brendan Kenna
 */
@Tag("system-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImportManagerCommandTests {
    
    private final Logger LOGGER = LogManager.getLogger(ImportManagerCommandTests.class);
    
    // Container for fetch nosql template
    private SeContainer container;
    private EntityManager entityManager;
    //private Template template;
    
    // Backend repo
    // private final ItemStoreType storeType = ItemStoreType.SQLITE;
    // private final String storeName = "TaskTide-Manager/Import/sqlite";
    // private ItemStore itemStore;
    
    
    // Backend repos
    // @Rule
    //private final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    // Backend repo
    // @Rule
    // public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    
    public ImportManagerCommandTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Import Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        // ItemStoreRepositoryUtility.initialize(storeType, storeName);
        // itemStore = ItemStoreRepositoryUtility.get().fetchItemStore(storeName, storeType);
        // TestUtils.initServiceManager(RepositoryType.ITEMSTORE, itemStore);
        
        container = TestEnvironment.startWeldContainer("jpa-template.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        //template = TestEnvironment.fetchDocumentTemplate(container);
        
        try {
            TestUtils.initServiceManager(RepositoryType.SQL, entityManager);
        }
        catch ( Exception ex ) {
            LOGGER.error("Failed to initialize ServiceManager", ex);
            // throw ex;
        }
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Import Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        // mariaDB.stop();
        // couchDB.stop;
    }
    
    
    /**
     * Purge table records for each test
     * 
     */
    @BeforeEach
    public void setUp() {
        LOGGER.info("\n\n================ Initiating Next Test ================\n");
        //LOGGER.info("\n\n================ Purging Records For Active Tests ================\n");
        //TestUtils.clearTestTables(this.LOGGER, this.entityManager);
        LOGGER.info("\n\n================ Records Purged For Active Tests ================\n");
    }
    
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }

    
    /**
     * Fetch path for provided resource, masking error
     *  from TestUtils.fetchResource 
     * 
     * @param resource
     * @return Path
     */
    public Path fetchResourcePath(String resource) {
        try {
            return TestUtils.fetchResource(resource);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Unable to read provided resource:   " + resource);
        }
    }
    
    
    /**
     * Initialize record for test purposes
     * 
     * @return {@link WorkItem}
     */
    public WorkItem initRecord() {
        
        // Init vars
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.ADD;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        
        // Configure command
        String queryString = String.format(
           "{\"Task Name\": \"MyTestTask\", \"Task Script\": \"ping google.com\"}"
        );
        Map<String, Object> opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, queryString, opts);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        
        // Run and fetch workitem
        Object result = cmd.execute();
        LOGGER.info("Displaying output from DB init:\n'{}'\n'{}'", cmd.toJsonDoc(), result);
        
        // Fetch first work item
        WorkItem output = TaskTideServiceManager
            .fetchWorkItemService()
            .viewAll()
        .get(0);
        return output;
    }
    
    
    /**
     * Executes simply import command verbosely to check other methods
     * 
     */
    @Test
    @Order(0)
    public void canAddRecordWithImportCommand() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Add ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.ADD;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        boolean assertionState;
        
        // Configure command spec
        String queryString = String.format(
           "{\"Task Name\": \"MyTestTask\", \"Task Script\": \"ping google.com\"}"
        );
        Map<String, Object> opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, queryString, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting ImportCommand for:\t'{}'", action);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        assertionState = (boolean) cmd.runCommand();
        if (assertionState) {
            LOGGER.info("Execution successful");
        }
        else {
            LOGGER.error("Execution unsuccessful");
        }
        
        // Log state
        LOGGER.info("\n\n================ Can Add ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests appending {@link ItemTask} to {@link WorkItem}
     * 
     */
    @Test
    @Order(1)
    public void canAppendTaskWithImportCommand() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Append ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.ADD;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        boolean assertionState;
        
        
        // Configure command spec
        LOGGER.info("Adding test WorkItem");
        String queryString = String.format(
           "{\"Task Name\": \"MyTestTask-A\", \"Task Script\": \"ping google.com\"}"
        );
        Map<String, Object> opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, queryString, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting ImportCommand for:\t'{}'", action);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        assertionState = (boolean) cmd.runCommand();
        if (assertionState) {
            LOGGER.info("Execution successful");
        }
        else {
            LOGGER.error("Execution unsuccessful");
        }
        
        // Configure command spec
        action = ManagerAction.APPEND;
        WorkItem item = this.initRecord();
        queryString = String.format(
           "{\"WorkItemId\": \"%s\", \"Task Name\": \"MyTestTask-B\", \"Task Script\": \"ping facebook.com\"}",
           item.getId()
        );
        opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, queryString, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting ImportCommand for:\t'{}'", action);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        assertionState = (boolean) cmd.runCommand();
        if (assertionState) {
            LOGGER.info("Execution successful");
        }
        else {
            LOGGER.error("Execution unsuccessful");
        }
        
        // Evaluate test
        LOGGER.info("\n\n================ Can Append ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests importing the json dataset in test resources
     */
    @Test
    @Order(2)
    public void canImportJsonRecords() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Import-JSON ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.IMPORT;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        boolean assertionState;
        
        // Fetch json document
        LOGGER.info("Fetching json document");
        Path path = fetchResourcePath("manager-cmds/import-manager-docs2.json");
        String targetFile = path.toString();
        
        // Construct command
        LOGGER.info("Creating ImportCommand");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", "JSON");
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(targetFile, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting ImportCommand for:\t'{}'", action);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        assertionState = (boolean) cmd.runCommand();
        if (assertionState) {
            LOGGER.info("Execution successful");
        }
        else {
            LOGGER.error("Execution unsuccessful");
        }
        
        // Evaluate test
        LOGGER.info("\n\n================ Can Import-JSON ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests importing the single task dataset in test resources
     */
    @Test
    @Order(3)
    public void canImportSingleTaskRecords() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Import-SingleTask ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.IMPORT;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        boolean assertionState;
        
        // Fetch json document
        LOGGER.info("Fetching single task workload");
        Path path = fetchResourcePath("singleTaskImports-Seq.txt");
        String targetFile = path.toString();
        
        // Construct command
        LOGGER.info("Creating ImportCommand");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", "|");
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(targetFile, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting ImportCommand for:\t'{}'", action);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        assertionState = (boolean) cmd.runCommand();
        if (assertionState) {
            LOGGER.info("Execution successful");
        }
        else {
            LOGGER.error("Execution unsuccessful");
        }
        
        // Evaluate test
        LOGGER.info("\n\n================ Can Import-SingleTask ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests importing the nested task dataset in test resources
     */
    @Test
    @Order(4)
    public void canImportNestedTaskRecords() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Import-NestedTasks ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.IMPORT;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        boolean assertionState;
        
        // Fetch json document
        LOGGER.info("Fetching nested task workload");
        Path path = fetchResourcePath("nestedTaskImports.txt");
        String targetFile = path.toString();
        
        // Construct command
        LOGGER.info("Creating ImportCommand");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", "|");
        opts.put("Nested Delimiter", ",");
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(targetFile, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting ImportCommand for:\t'{}'", action);
        cmd = (ImportCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        assertionState = (boolean) cmd.runCommand();
        if (assertionState) {
            LOGGER.info("Execution successful");
        }
        else {
            LOGGER.error("Execution unsuccessful");
        }
        
        // Evaluate test
        LOGGER.info("\n\n================ Can Import-NestedTasks ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
}