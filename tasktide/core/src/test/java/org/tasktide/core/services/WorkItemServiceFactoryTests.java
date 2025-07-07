/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.nosql.Template;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;

import org.tasktide.TestCaseBuilderUtility;
import org.tasktide.TestUtils;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideService;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.itemstore.ItemStore;


/**
 * Test cases for {@link TaskTideService} of {@link WorkItem} for each
 *  repository type
 *
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {Converters.class, Reflections.class, EntityConverter.class, Template.class, DocumentTemplate.class})
@AddExtensions( {ReflectionEntityMetadataExtension.class, DocumentExtension.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkItemServiceFactoryTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemServiceFactoryTests.class);
    
    public WorkItemServiceFactoryTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Service Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Service Tests ----------------\n";
        logger.info(msg);
    }
    
    @BeforeEach
    public void setUp() {
        logger.info("\n\n================ Initiating Next Test ================\n");
    }
    
    @AfterEach
    public void tearDown() {
        logger.info("\n\n================ Terminating Test ================\n");
    }

    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(0)
    public void canConstructWorkItemJsonService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-JSON From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        List<WorkItem> backend;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        repoType = RepositoryType.JSON;
        backend = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for JSON WorkItem Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = backend.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-JSON From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(1)
    public void canConstructWorkItemRocksDbService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-RocksDB From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        ItemStore backend;
        List<WorkItem> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Configure requirements
        repoType = RepositoryType.ITEMSTORE;
        String collectionName = TestUtils.resolveRocksRepoPath();
        backend = TestUtils.fetchItemStore(collectionName);
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for RocksDB-WorkItem Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        workItemService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-RocksDB From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(2)
    public void canConstructWorkItemNoSqlService() {
    
        // Initialize data
        logger.info("\n\n================ Construct WorkItemService-Template From Factory Test ================\n");
        TaskTideService<WorkItem> workItemService;
        RepositoryType repoType;
        Template backend;
        List<WorkItem> data;
        boolean assertionState ;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Configure requirements
        repoType = RepositoryType.NOSQL;
        backend = TestUtils.fetchTemplate();
        
        // Setup requirements
        logger.info("Configuring Service");
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        Map<String, String> map = workItemService.getRepo().getRepositoryMetaData();
        logger.info("Displaying meta data for Template WorkItem Service:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        workItemService.extendModel(data);
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> result = workItemService.fetchById(ref.getId());
        logger.info("\n\nDisplaying retreieved WorkItem:\n'{}'", result.toJson());
        assertionState = ref.getId().equals(result.getId());
        
        // Log test state
        logger.info("\n\n================ Construct WorkItemService-Template From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}