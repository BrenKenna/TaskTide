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

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

// import org.junit.Rule;
// import org.tasktide.tasktide.TestEnvironment;
// import org.testcontainers.containers.GenericContainer;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.engine.policies.AcquisitionPolicyMode;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;

import org.tasktide.tasktide.TestUtils;

import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;


/**
 * Test module for specific implementations of {@link TaskTideClient}
 *
 * @author bkenna
 */
@Tag("unit-client")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskTideClientTests {
    
    private static final Logger LOGGER = LogManager.getLogger(TaskTideClientTests.class);
    private static CdiContainerProvider provider;
    
    private final String STEP = "Nested NS Lookups";

    
    //@Rule
    //private static final GenericContainer<?> couchDB = TestEnvironment.couchDbContainer("tasktide_database", false);
    
    public TaskTideClientTests() {
    }
    
    
    @BeforeAll
    public static void setUpClass() {
        String msg = "\n\n---------------- Initiating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        provider = TaskTideClientUtility.configureCdiInstance(CdiProviders.WELD, true);
        LOGGER.info(msg);
    }
    
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        provider.shutdown();
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
    
        // Load
        TaskTideClientUtility.initServiceManager(repoType, configMap);
        TestUtils.importTestRecords("nested-nslookup-tasks.txt", this.STEP, "|", ",");
        
        // Return acquisition policy
        return AcquisitionPolicyMode.TARGETED.initBuilder()
            .withTarget(this.STEP)
            .withItemState(ItemState.TODO)
        .build();
    }

    
    /**
     * Tests importing through the manager client
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
        LOGGER.info("Displaying configured properties");
        for ( Entry elm : configMap.getArgTree().getGlobalArguments().getArgMap().entrySet() ) {
            LOGGER.info("Key = '{}', Value = '{}'", elm.getKey(), elm.getValue());
        }
        
        // Fetch service manager
        LOGGER.info("Initializing TaskTideServiceManager");
        RepositoryType repoType = TaskTideClientUtility.fetchRepoType(configMap);
        TaskTideClientUtility.initServiceManager(repoType, configMap);
        
        // Fetch client
        LOGGER.info("Constructing TaskTideClient");
        TaskTideClientType clientType = TaskTideClientType.MANAGER;
        TaskTideClient client = clientType.makeClient(configMap);
        
        // Import data
        LOGGER.info("Running the '{}'", clientType);
        client.runClient();
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
        
        // Fetch config
        LOGGER.info("Constructing ClientConfigMap");
        ClientConfigMap configMap = new ClientConfigMap();
        configMap.addConfigs(provider);
        
        // Fetch service manager
        LOGGER.info("Initializing TaskTideServiceManager");
        RepositoryType repoType = TaskTideClientUtility.fetchRepoType(configMap);
        try {
            this.initDocumentTemplate(repoType, configMap);
        }
        catch (IllegalStateException ex) {
            LOGGER.info("Proceeding to Engine with previously iniatied engine");
        }
        
        // Fetch client
        LOGGER.info("Fetching Client");
        TaskTideClientType clientType = TaskTideClientType.ENGINE;
        TaskTideClient client = clientType.makeClient(configMap);
        
        // Import data
        LOGGER.info("Running the '{}'", clientType);
        client.runClient();
        LOGGER.info("\n\n================ Tests EngineClient  ================\n");
    }
}