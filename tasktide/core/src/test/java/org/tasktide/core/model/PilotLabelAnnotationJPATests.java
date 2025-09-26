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

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Rule;
import org.testcontainers.containers.GenericContainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.TestEnvironment;

import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.services.ServiceFactory;


/**
 * Test module for {@link CustomAnnotations} on {@link WorkItem}
 *  against a {@link JPARepository}
 * 
 * @author Brendan Kenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PilotLabelAnnotationJPATests {
    
    private final Logger LOGGER = LogManager.getLogger(PilotLabelAnnotationJPATests.class);
    
    @Rule
    public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    private EntityManager entityManager;
    private SeContainer container;
    
    
    public PilotLabelAnnotationJPATests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Pilot Label Annotations JPA Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-config.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        this.initServiceManager();
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Initiating Pilot Label Annotations JPA Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        entityManager.close();
        mariaDB.stop();
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
        RepositoryType repoType = RepositoryType.SQL;
        TaskTideService<Workflow> workflowServ = ServiceFactory.makeWorkflowService(repoType, entityManager, "Workflow");
        TaskTideService<Step> repoStep = ServiceFactory.makeStepService(repoType, entityManager, "Step");
        TaskTideService<WorkItem> repoWorkItem = ServiceFactory.makeWorkItemService(repoType, entityManager, "WorkItem");
        
        // Initialize service manager with services
        TaskTideServiceManager.initialize(repoWorkItem, repoStep, workflowServ);
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
     * Tests using {@link WorkItem} annotations against a {@link JpaRepository}
     * 
     */
    @Test
    @Order(0)
    public void canAnnotateWorkItem() {
        
        // Construct work item
        LOGGER.info("\n\n================ Can Load WorkItem Annotations into JPA Test ================\n");
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
        LOGGER.info("Displaying custom annotations:\n'{}'", record.getAnnotations().toJsonDoc());
        assertionState = TaskTideServiceManager.fetchWorkItemService().updateModel(record) != null;
        
        // Evaluate test
        assertTrue(assertionState, "Applying annotations to record failed");
        LOGGER.info("\n\n================ Can Load WorkItem Annotations into JPA Test ================\n");
    }
}