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
package org.tasktide.core.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.ItemStoreRepository;
import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Test module for {@link CustomAnnotations} on {@link WorkItem}
 *  against a {@link ItemStoreRepository}
 * 
 * @author Brendan Kenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PilotLabelAnnotationItemStoreTests {
    
    private final Logger LOGGER = LogManager.getLogger(PilotLabelAnnotationItemStoreTests.class);

    private final ItemStoreType itemStoreType = ItemStoreType.SQLITE;
    private final String dbPath = "sqlitePilotLabel";   
    
    public PilotLabelAnnotationItemStoreTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Pilot Label Annotations ItemStore Tests ----------------\n";
        LOGGER.info(msg);
        
        this.initServiceManager();
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Initiating Pilot Label Annotations ItemStore Tests ----------------\n";
        LOGGER.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }
    
    
    /**
     * Initializes service manager
     */
    public void initServiceManager() {
        
        // Fetch services
        ItemStoreRepositoryUtility.initialize(itemStoreType, dbPath);
        ItemStoreRepositoryUtility.get().initServiceManager();
    }
    

    /**
     * Return command for task
     * 
     * @param taskName
     * @param task
     * @return {@link ManagerCommand}
     */
    public ManagerCommand makeImport(String taskName, String task) {
        
        // Initalize vars
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.ADD;
        Map<String, Object> opts;
        CommandSpec cmdSpec;
        
        // Initialize command spec
        String queryString = String.format(
           "{\"Task Name\": \"%s\", \"Task Script\": \"%s\"}",
           taskName, task
        );
        opts = new HashMap<>();
        opts.put("Step Name", "Arbitrary");
        cmdSpec = new CommandSpec(null, queryString, opts);
        
        // Return command
        return action.makeCommand(target, cmdSpec);
    }
    
    
    /**
     * Tests using {@link WorkItem} annotations against a {@link ItemStoreRepository}
     * 
     */
    @Test
    @Order(0)
    public void canAnnotateWorkItem() {
        
        // Construct work item
        LOGGER.info("\n\n================ Can Load WorkItem Annotations into ItemStore Test ================\n");
        ManagerCommand cmd;
        WorkItem record;
        CustomAnnotation anno = new CustomAnnotation();
        boolean assertionState;
        
        // Create and run import
        cmd = this.makeImport("MyTestTask", "ping google.com");
        LOGGER.info("Executing command:\n'{}'", cmd.toJsonDoc());
        LOGGER.info("Execution result:\n'{}'", cmd.execute());
        record = TaskTideServiceManager.fetchWorkItemService().viewAll().get(0);
        
        // Apply label
        LOGGER.info("Applying custom annotations to record:\t'{}'", record.getId());
        anno.add("Pilot Label", "RunsOnInstanceWithThisArg");
        anno.add("Internal Id", "Some Internal Identifier");
        record.setAnnotations(anno);
        LOGGER.info("Displaying custom annotations:\n'{}'", record.getAnntations().toJsonDoc());
        assertionState = TaskTideServiceManager.fetchWorkItemService().updateModel(record) != null;
        
        // Evaluate test
        assertTrue(assertionState, "Applying annotations to record failed");
        LOGGER.info("\n\n================ Can Load WorkItem Annotations into ItemStore Test ================\n");
    }
}