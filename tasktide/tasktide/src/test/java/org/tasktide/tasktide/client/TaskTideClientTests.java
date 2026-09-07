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
package org.tasktide.tasktide.client;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.manager.command.ManagerAction;
import org.tasktide.core.manager.command.ManagerTarget;
import org.tasktide.core.manager.generator.ExampleGenerators;

// import org.junit.Rule;
// import org.tasktide.tasktide.TestEnvironment;
// import org.testcontainers.containers.GenericContainer;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.engine.policies.AcquisitionPolicyMode;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.parser.configuration.TaskTideConfig;
import org.tasktide.parser.model.Argument;
import org.tasktide.parser.model.ArgumentMap;

import org.tasktide.tasktide.TestUtils;

import org.tasktide.tasktide.containerprovider.CdiContainerProvider;


/**
 * Test module for specific implementations of {@link TaskTideClient}
 *
 * @author bkenna
 */
@Tag("system-tasktide")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("unchecked")
public class TaskTideClientTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTideClientTests.class);
    private static CdiContainerProvider provider;
    
    private final String WORKFLOW = "TaskTide Client Tests";
    private final String STEP = "Nested NS Lookups";

    
    //@Rule
    //private static final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public TaskTideClientTests() {
    }
    
    
    @BeforeAll
    public void setUpClass() {
        String msg = "\n\n---------------- Initiating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        TestUtils.initSeContainer();
        this.provider = TestUtils.fetchCdiProvider();
        
        TestUtils.createWorkflow(this.WORKFLOW);
        TestUtils.createStep(this.STEP, this.WORKFLOW);
        LOGGER.info(msg);
    }
    
    
    @AfterAll
    public void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        LOGGER.info(msg);
        //couchDB.stop();
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
    /**
     * Initialize document backend providing {@link TaskTideWorkloadAcquisitionPolicy}
     * 
     * @param repoType
     * @param configMap
     * 
     * @return {@link TaskTideWorkloadAcquisitionPolicy} of {@link WorkItem}
     */
    public TaskTideWorkloadAcquisitionPolicy
        initDocumentTemplate(RepositoryType repoType, ClientConfigMap configMap)
    {
        // Return acquisition policy
        TestUtils.registerWorkItemTasks(ExampleGenerators.NSLOOKUPS, this.STEP, 3);
        return AcquisitionPolicyMode.TARGETED.initBuilder()
            .withTarget(this.STEP)
            .withItemState(ItemState.TODO)
        .build();
    }

    
    /**
     * Tests importing through the manager client
     *   No errors means good
     */
    @Test
    @Order(0)
    public void canImportThroughManagerClient() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests ManagerClient-Import  ================\n");
        
        // Fetch config
        LOGGER.info("Creating ClientConfigMap");
        ClientConfigMap configMap = new ClientConfigMap();
        configMap.addConfigs(provider);
        
        // Fetch client
        LOGGER.info("Initialize TaskTideClient Config");
        TaskTideClientType clientType = TaskTideClientType.MANAGER;
        TaskTideConfig mgrConf = configMap.getConfig(clientType);
        ArgumentMap argMap = mgrConf.getArgumentMap();
        
        // Adjust values for test
        Argument<String> targetArg = (Argument<String>) argMap.getArgMap().get("Target");
        targetArg.setValue(ManagerTarget.WORKITEM.toString());
        Argument<String> action = (Argument<String>) argMap.getArgMap().get("Method");
        action.setValue(ManagerAction.SUMMARIZE.toString());
        Argument<String> targetFile = (Argument<String>) argMap.getArgMap().get("Target File");
        targetFile.setValue("./summarize-manager-command.txt");
        
        ArgumentMap globalArgMap = configMap
            .getConfig(TaskTideClientType.GLOBAL)
        .getArgumentMap();
        Argument<String> stepArg = (Argument<String>) globalArgMap.getArgument("Step Name");
        stepArg.setValue(this.STEP);
                
        // Create client
        LOGGER.info("Initialize TaskTideClient");
        TaskTideClient client = clientType.makeClient(configMap);
        LOGGER.info("Displaying configured properties");
        for ( Entry elm : client.getArgTree().getTree().getDataForAddress("manager").getArgMap().entrySet() ) {
            LOGGER.info("Key = '{}', Value = '{}'", elm.getKey(), elm.getValue());
        }
        
        // Import data
        LOGGER.info("Running the '{}'", clientType);
        client.runClient();
        Assertions.assertTrue(
            Files.exists(Paths.get("summarize-manager-command.txt")),
            "Results from Summarize Manager not written to 'summarize-manager-command.txt'"
        );
        LOGGER.info("\n\n================ Tests ManagerClient-Import  ================\n");
    }
    
    
    /**
     * Tests processing through the engine client
     */
    @Test
    @Order(1)
    public void canProcessThroughEngineClient() {
    
        // Initialize data
        LOGGER.info("\n\n================ Tests EngineClient  ================\n");
        int nTasks = 2, tasksPerItem = 2;
        ClientConfigMap configMap;
        TaskTideClientType clientType;
        TaskTideClient client;
        
        // Fetch config
        LOGGER.info("Constructing ClientConfigMap");
        configMap = new ClientConfigMap();
        configMap.addConfigs(provider);
        
        // Apply step name for this tests
        LOGGER.info("Apply config and register tasks");
        ArgumentMap globalArgMap = configMap
            .getConfig(TaskTideClientType.GLOBAL)
        .getArgumentMap();
        Argument<String> stepArg = (Argument<String>) globalArgMap.getArgument("Step Name");
        stepArg.setValue(this.STEP);
        List<WorkItem> tasks = TestUtils.registerWorkItemCollection(
            ExampleGenerators.NSLOOKUPS,
            this.STEP,
            tasksPerItem,
            nTasks
        ); // Could check Id instead, but count is fine this instance
        
        // Fetch client
        LOGGER.info("Fetching Client");
        clientType = TaskTideClientType.ENGINE;
        client = clientType.makeClient(configMap);
        
        // Import data
        LOGGER.info("Running the '{}'", clientType);
        client.runClient();
        
        List<WorkItem> tasksDone = TaskTideServiceManager
            .fetchWorkItemService()
            .viewByFieldForGroup(
                "StepName",
                this.STEP,
                "ItemState",
                ItemState.DONE
        );
        Assertions.assertTrue(
            tasksDone.size() == nTasks,
            "Expected n = '" + nTasks + "' tasks completed, detected '" + tasksDone.size() + "' from ServiceManager" 
        );
        
        LOGGER.info("\n\n================ Tests EngineClient  ================\n");
    }
}