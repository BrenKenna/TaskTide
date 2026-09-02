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
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.command.commands.DeleteCommand;
import org.tasktide.core.repository.JpaRepository;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Tests the {@link DeleteCommand} {@link ManagerCommand} via
 *  {@link JpaRepository} using {@link GenericContainer}
 *
 * @author Brendan Kenna
 */
@Tag("system-core")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteManagerCommandTests {
    
    private final Logger LOGGER = LogManager.getLogger(DeleteManagerCommandTests.class);
    
    // Backend repo
    //@Rule
    //public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    // Container for fetch template/entity manager
    /**
     * private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    **/
    
    private final ItemStoreType storeType = ItemStoreType.ROCKSDB;
    private final String storeName = "TaskTide-Manager/Delete/RocksDB";
    private ItemStore itemStore;
    
    public DeleteManagerCommandTests() {
    }
    
    @BeforeAll
    public void setUpClass() {        
        String msg = "\n\n---------------- Initiating Delete Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        ItemStoreRepositoryUtility.modify(storeType, storeName);
        itemStore = ItemStoreRepositoryUtility.get().fetchItemStore(storeName, storeType);
        TestUtils.initServiceManager(RepositoryType.ITEMSTORE, itemStore);
        
        /**
         * container = TestEnvironment.startWeldContainer("jpa-config.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
        
        try {
            TestUtils.initServiceManager(RepositoryType.SQL, entityManager);
        }
        catch ( Exception ex ) {}
        **/ 
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Delete Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        //if (container != null && container.isRunning()) {
        //    container.close();
        //    LOGGER.info("CDI container shut down");
        //}
        // mariaDB.stop();
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
        //LOGGER.info("\n\n================ Records Purged For Active Tests ================\n");
    }
    
    
    @AfterEach
    public void tearDown() {
        LOGGER.info("\n\n================ Terminating Test ================\n");
    }
    
    
    /**
     * Tests deletion of {@link WorkItem}
     */
    @Test
    @Order(0)
    public void canDeleteWorkItem() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Delete WorkItem ManagerCommand Test ================\n");
        String forDeletion = "WorkItem-b30dc32b-ca06-48db-af53-d80584evd545";
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.DELETE;
        CommandSpec cmdSpec;
        DeleteCommand cmd;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records");
        TestUtils.importTestRecords("manager-cmds/import-delete-docs.json", "SequenceAlignment", "JSON");
        WorkItem preCmd = TaskTideServiceManager.fetchWorkItemService().fetchById(forDeletion);
        LOGGER.info("Displaying WorkItem for deletion:\n'{}'", preCmd.toJsonDoc());
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Item Id", forDeletion);
        cmdSpec = new CommandSpec(null, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Run command
        LOGGER.info("Constructing, and executing configured DeleteCommand:\t'{}'", action);
        cmd = (DeleteCommand) action.makeCommand(target, cmdSpec);
        assertionState = (boolean) cmd.execute();
        LOGGER.info("Execution completed with status:\t'{}'", assertionState);
        preCmd = TaskTideServiceManager.fetchWorkItemService().fetchById(forDeletion);
        LOGGER.info("Displaying WorkItem post deletion:\n'{}'", preCmd);
        
        // Log state
        LOGGER.info("\n\n================ Can Delete WorkItem ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests deletion of {@link WorkItem}
     */
    @Test
    @Order(1)
    public void canDeleteWorkItemTask() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Delete WorkItem Task ManagerCommand Test ================\n");
        String itemId = "WorkItem-df87a3j9-ce6c-456f-bdf2-662i5824d939";
        String taskId = "ItemTask-7f4fa635-17ed-4387-a9d4-0b61f168ee0d";
        String queryStr = 
            "{\"Item Id\": \"WorkItem-df87a3j9-ce6c-456f-bdf2-662i5824d939\", \"Task Id\": \"ItemTask-54384125-c65d-4e74-85fe-cdc0c1ab0hcg\"}"
        ;
        WorkItem deleted;
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.DELETE;
        CommandSpec cmdSpec;
        DeleteCommand cmd;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records");
        TestUtils.importTestRecords("manager-cmds/import-delete-docs.json", "SequenceAlignment", "JSON");
        WorkItem preCmd = TaskTideServiceManager.fetchWorkItemService().fetchById(itemId);
        LOGGER.info("Displaying WorkItem for deletion:\n'{}'", preCmd.toJsonDoc());
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        cmdSpec = new CommandSpec(null, queryStr, null);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Run command
        LOGGER.info("Constructing, and executing configured DeleteCommand:\t'{}'", action);
        cmd = (DeleteCommand) action.makeCommand(target, cmdSpec);
        deleted = (WorkItem) cmd.execute();
        assertionState = deleted != null;
        LOGGER.info("Execution completed with status:\t'{}'", assertionState);
        LOGGER.info("Displaying WorkItem post deletion:\n'{}'", deleted.toJsonDoc());
        
        // Log state
        LOGGER.info("\n\n================ Can Delete WorkItem Task ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
    
    
    /**
     * Tests dynamic deletion of a collection of WorkItems
     * 
     */
    @Test
    @Order(2)
    public void canDeleteList() {
    
        // Construct work item
        LOGGER.info("\n\n================ Can Delete List ManagerCommand Test ================\n");
        String[] forDelete = { 
            "WorkItem-b30dc32b-ca06-48db-af53-d80584evd545",
            "WorkItem-cf1ffbbe-4bc3-408f-81zd-139e029ce249"
        };
        String result = TestUtils.fetchResourcePath("manager-cmds/For-Reset.txt").toString();
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.DELETE_LIST;
        CommandSpec cmdSpec;
        DeleteCommand cmd;
        boolean assertionState;
        
        // Import test records
        LOGGER.info("Importing test records, displaying WorkItems for unlocking");
        TestUtils.importTestRecords("manager-cmds/import-delete-docs.json", "SequenceAlignment", "JSON");
        TestUtils.printEach(forDelete);
        
        // Construct command spec
        LOGGER.info("Constructing command spec");
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", ",");
        cmdSpec = new CommandSpec(result, null, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Create and run command
        LOGGER.info("Constructing, and executing configured DeleteCommand");
        cmd = (DeleteCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Display DeleteCommand for reference:\n" + cmd.toJsonDoc());
        int counter = (int) cmd.execute();
        assertionState = counter >= 0;
        
        // Evaluate test
        if ( assertionState ) {
            LOGGER.info("Records deleted");
        }
        else {
            LOGGER.error("Unable to delete test tokens");
        }
        
        // Log state
        LOGGER.info("\n\n================ Can Delete List ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
}