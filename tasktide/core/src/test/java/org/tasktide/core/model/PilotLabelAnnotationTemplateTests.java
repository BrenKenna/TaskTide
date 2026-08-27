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
import jakarta.nosql.Template;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import org.junit.Rule;
//import org.testcontainers.containers.GenericContainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.tasktide.TestEnvironment;
import org.tasktide.core.TaskTideModel;

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
import org.tasktide.core.repository.TemplateRepository;

import org.tasktide.core.services.ServiceFactory;



/**
 * Test module for {@link CustomAnnotations} on {@link TaskTideModel}
 *  against a {@link TemplateRepository}
 * 
 * @author Brendan Kenna
 */
@Tag("integration-model")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PilotLabelAnnotationTemplateTests {
    
    private final Logger LOGGER = LogManager.getLogger(PilotLabelAnnotationTemplateTests.class);
    
    //@Rule
    //public GenericContainer<?> mongoDB = TestEnvironment.mongoDbContainer("tasktide_database");
    
    private Template template;
    private SeContainer container;
    
    
    public PilotLabelAnnotationTemplateTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Pilot Label Annotations Template Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("mongoDB-config.properties", getClass());
        template = TestEnvironment.fetchDocumentTemplate(container);
        this.initServiceManager();
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Initiating Pilot Label Annotations Template Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        // mongoDB.stop();
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
        RepositoryType repoType = RepositoryType.NOSQL;
        TaskTideService<Workflow> workflowServ = ServiceFactory.makeWorkflowService(repoType, template, "Workflow");
        TaskTideService<Step> repoStep = ServiceFactory.makeStepService(repoType, template, "Step");
        TaskTideService<WorkItem> repoWorkItem = ServiceFactory.makeWorkItemService(repoType, template, "WorkItem");
        
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
     * Tests using {@link WorkItem} annotations against a {@link TemplateRepository}
     * 
     */
    @Test
    @Order(0)
    public void canAnnotateWorkItem() {
        
        // Construct work item
        LOGGER.info("\n\n================ Can Load WorkItem Annotations into ITemplate Test ================\n");
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
        LOGGER.info("\n\n================ Can Load WorkItem Annotations into Template Test ================\n");
    }
}