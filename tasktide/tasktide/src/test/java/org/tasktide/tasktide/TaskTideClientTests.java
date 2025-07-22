/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.tasktide;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;

import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.tasktide.client.ClientConfigMap;
import org.tasktide.tasktide.client.TaskTideClient;
import org.tasktide.tasktide.client.TaskTideClientType;
import org.tasktide.tasktide.client.TaskTideClientUtility;

import org.tasktide.tasktide.configurer.EngineConfig;
import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.ManagerConfig;
import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.SeContainerProvider;


/**
 *
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {
    Converters.class, Reflections.class, EntityConverter.class,
    Template.class, DocumentTemplate.class,
    EngineConfig.class, GlobalConfig.class, ManagerConfig.class
})
@AddExtensions( { ReflectionEntityMetadataExtension.class, DocumentExtension.class } )
public class TaskTideClientTests {
    
    private static final Logger logger = LogManager.getLogger(ConfigureTaskTideTest.class);
    private static SeContainer container;
    private static CdiContainerProvider provider;
    
    public TaskTideClientTests() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        String msg = "\n\n---------------- Initiating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        container = SeContainerInitializer.newInstance().initialize();
        provider = new SeContainerProvider(container);
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Configuration from TaskTide-Engine-Config Tests ----------------\n";
        container.close();
        logger.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    
   
    
    /**
     * Tests importing through the manager client
     */
    @Test
    @Order(0)
    public void canImportThroughManagerClient() {
    
        // Initialize data
        logger.info("\n\n================ Tests ManagerClient-Import  ================\n");
        List<WorkItem> workload, results;
        WorkItem ref, query;
        boolean assertionState;
        
        // Fetch config
        ClientConfigMap configMap = new ClientConfigMap();
        configMap.addConfigs(provider);
        
        // Fetch service manager
        RepositoryType repoType = TaskTideClientUtility.fetchRepoType(configMap);
        TaskTideServiceManager taskTideServiceManager = TaskTideClientUtility.fetchManager(repoType, configMap);
        
        // Fetch client
        TaskTideClientType clientType = TaskTideClientType.MANAGER;
        TaskTideClient client = clientType.makeClient(taskTideServiceManager, configMap);
        
        // Import data
        logger.info("Running manager client import");
        client.runClient();
        logger.info("\n\n================ Tests ManagerClient-Import  ================\n");
    }
    
    
    /**
     * Tests processing through the engine client
     */
    @Test
    @Order(1)
    public void canProcessThroughEngineClient() {
    
        // Initialize data
        logger.info("\n\n================ Tests EngineClient  ================\n");
        List<WorkItem> workload, results;
        WorkItem ref, query;
        boolean assertionState;
        
        // Fetch config
        ClientConfigMap configMap = new ClientConfigMap();
        configMap.addConfigs(provider);
        
        // Fetch service manager
        RepositoryType repoType = TaskTideClientUtility.fetchRepoType(configMap);
        TaskTideServiceManager taskTideServiceManager = TaskTideClientUtility.fetchManager(repoType, configMap);
        
        // Fetch client
        TaskTideClientType clientType = TaskTideClientType.ENGINE;
        TaskTideClient client = clientType.makeClient(taskTideServiceManager, configMap);
        
        // Import data
        logger.info("Running manager client import");
        client.runClient();
        logger.info("\n\n================ Tests EngineClient  ================\n");
    }
}
