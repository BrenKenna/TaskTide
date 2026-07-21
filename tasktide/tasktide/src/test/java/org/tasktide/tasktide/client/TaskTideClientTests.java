/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide.client;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

// import org.junit.Rule;
// import org.tasktide.tasktide.TestEnvironment;
// import org.testcontainers.containers.GenericContainer;

import org.tasktide.core.model.workitem.ItemState;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.engine.policies.TaskTideWorkloadAcquisitionPolicy;
import org.tasktide.engine.policies.TargetedAcquisitionPolicy;

import org.tasktide.tasktide.TestUtils;

import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;


/**
 * Test module for specific implementations of {@link TaskTideClient}
 *
 * @author bkenna
 */
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
        return TargetedAcquisitionPolicy
            .newInstance()
            .withTarget(this.STEP)
            .withItemState(ItemState.TODO)
        ;
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