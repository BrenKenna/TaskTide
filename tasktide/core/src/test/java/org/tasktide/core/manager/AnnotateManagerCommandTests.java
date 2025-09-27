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
import org.tasktide.TestUtils;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.manager.command.CommandSpec;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerCommand;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.command.commands.AnnotateCommand;
import org.tasktide.core.manager.command.commands.ImportCommand;
import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.supporting.JsonUtils;


/**
 * Tests {@link CustomAnnotation} through the {@link ManagerCommand} interface
 *
 * @author Brendan Kenna
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnnotateManagerCommandTests {
    
    private static final Logger LOGGER = LogManager.getLogger(AnnotateManagerCommandTests.class);
    
    // Container for fetch nosql template
    private SeContainer container;
    private EntityManager entityManager;
    private Template template;
    
    // Backend repos
    // @Rule
    //private final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    // Backend repo
    @Rule
    public GenericContainer<?> mariaDB = TestEnvironment.mariaDbContainer("tasktide_database");
    
    public AnnotateManagerCommandTests() {
    }
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Annotation Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        container = TestEnvironment.startWeldContainer("jpa-template.properties", getClass());
        entityManager = JpaRepositoryUtility.get().fetchEntityManager();
        template = TestEnvironment.fetchDocumentTemplate(container);
        this.initServiceManager();
    }
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Import Manager Command Tests ----------------\n";
        LOGGER.info(msg);
        if (container != null && container.isRunning()) {
            container.close();
            LOGGER.info("CDI container shut down");
        }
        mariaDB.stop();
        // couchDB.stop();
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
            throw new IllegalArgumentException("Unable to read provided resource;\t" + resource);
        }
    }
    
    
    /**
     * Initialize record for test purposes
     * 
     * @return {@link WorkItem}
     */
    public boolean initRecord() {
        
        // Init vars
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.IMPORT;
        CommandSpec cmdSpec;
        ImportCommand cmd;
        
        // Configure command
        Path path = fetchResourcePath("import-docs.json");
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
        return (boolean) cmd.runCommand();
    }
    
    
    /**
     * Tests annotating records
     * 
     */
    @Test
    @Order(0)
    public void canAnnotateRecord() {
        
        // Annotate recird
        LOGGER.info("\n\n================ Can Annotate ManagerCommand Test ================\n");
        ManagerTarget target = ManagerTarget.WORKITEM;
        ManagerAction action = ManagerAction.ANNOTATION;
        CommandSpec cmdSpec;
        AnnotateCommand cmd;
        boolean assertionState;
        //LOGGER.info("Displaying init record state:\t'{}'", initRecord());
        
        // Fetching records annotation
        LOGGER.info("Fetching record for annoations");
        Path path = fetchResourcePath("forAnno.txt");
        String targetFile = path.toString();
        
        // Construct command
        LOGGER.info("Creating AnnotationCommand");
        String queryString = String.format(
           "{\"Pilot Label\": \"Unit-Tests-Are-Awesome\", \"Unit Test\": \"Annotations\"}"
        );
        Map<String, Object> opts = new HashMap<>();
        opts.put("Delimiter", ",");
        cmdSpec = new CommandSpec(targetFile, queryString, opts);
        LOGGER.info("Displaying configured command spec:\n'{}'", cmdSpec.toJsonDoc());
        
        // Construct manager command
        LOGGER.info("Consutrcting AnnotationCommand for:\t'{}'", action);
        cmd = (AnnotateCommand) action.makeCommand(target, cmdSpec);
        LOGGER.info("Displaying constructed command:\n'{}'", cmd.toJsonDoc());
        
        // Perform action
        LOGGER.info("Executing command");
        int nRecords = (int) cmd.runCommand();
        assertionState = nRecords >= 1;
        if (assertionState) {
            LOGGER.info("Execution successful, N annotated = '{}'", nRecords);
            LOGGER.info(
                "Displaying records for reference:\n'{}'",
                JsonUtils.toJson(true, TaskTideServiceManager.fetchWorkItemService().fetchById("WorkItem-cf1ffbbe-4bc3-408f-81ed-139e029ce249"))
            );
        }
        else {
            LOGGER.error("Execution unsuccessful, N annotated = '{}'", nRecords);
        }
        
        // Evaluate test
        LOGGER.info("\n\n================ Can Annotate ManagerCommand Test ================\n");
        assertTrue(assertionState);
    }
}