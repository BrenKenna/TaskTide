/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.nosql.Template;
import jakarta.persistence.EntityManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.supporting.JsonUtils;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.RocksDBStore;


/**
 * Test module for {@link RepositoryFactory} for {@link Workflow}
 *  across the {@link RepositoryType}
 * 
 * @author bkenna
 */
@EnableAutoWeld
@AddPackages(value = {
    Converters.class, EntityConverter.class, Template.class, DocumentTemplate.class,
    EntityManager.class
})
@AddPackages(value = {Tunes.class, Reflections.class})
@AddExtensions( {ReflectionEntityMetadataExtension.class, DocumentExtension.class} )
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkItemRepositoryFactoryTests {
    
    private static final Logger logger = LogManager.getLogger(WorkItemRepositoryFactoryTests.class);
    
    public WorkItemRepositoryFactoryTests() {}
    
    @BeforeAll
    public static void setUpClass() {        
        String msg = "\n\n---------------- Initiating Repository Tests ----------------\n";
        logger.info(msg);
    }
    
    @AfterAll
    public static void tearDownClass() {
        String msg = "\n\n---------------- Terminating Repository Tests ----------------\n";
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
     * Resolve a path string for test purposes
     * 
     * @return String
     */
    public String resolveRocksRepoPath() {
        Path cwd = Paths.get( System.getProperty("user.dir") );
        Path workDir = cwd.resolve("project-test-repos").resolve("work-item");
        return workDir.toString();
    }
    
    
    /**
     * Fetch a {@link RocksDBStore} with name
     * 
     * @param storeName
     * @return {@link ItemStore} of {@link RocksDBStore}
     */
    public ItemStore fetchItemStore(String storeName) {
    
        // Resolve store name location to a Path
        Path targetPath = Paths.get(storeName);
        try {
            
            // Create path if required
            Files.createDirectories(targetPath);
            
            // Set required properites
            String dbDirectory = targetPath.toString();
            String masterDB = "master";
            String protoDB = UUID.randomUUID().toString();
            RocksDBStore itemStore = new RocksDBStore(storeName, dbDirectory, masterDB, protoDB);
            
            // Return ItemStore
            return itemStore;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    
    /**
     * Fetch Jakarta NoSQL backend database from container
     * 
     * @return {@link Template}
     */
    public Template fetchTemplate() {
        logger.info("Initializing container");
        SeContainer container;
        container = SeContainerInitializer.newInstance().initialize();
        logger.info("Weld container running:\t'{}'", container.isRunning());
        return container.select(DocumentTemplate.class).get();
    }
    
    
    public EntityManager fetchManager() {
        return CDI.current().select(EntityManager.class).get();
    }
    
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(0)
    public void canConstructWorkItemJsonRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct JSON Repositories From Factory Test ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType;
        List<WorkItem> backend;
        WorkItem record;
        boolean assertionState = false;
        
        // Generate data
        logger.info("Generating data for testing");
        repoType = RepositoryType.JSON;
        backend = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Setup requirements
        logger.info("Configuring repository");
        workItemRepoFactory = new RepositoryFactory<>("Test-Json-WorkItem", WorkItem.class, backend, repoType);
        workItemRepo = workItemRepoFactory.make();
        Map<String, String> map = workItemRepo.getRepositoryMetaData();
        logger.info("Displaying meta data for WorkItemRepository:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Check that records can be queried
        logger.info("Verifying records can be retrieved");
        TaskTideModel<WorkItem> ref = backend.get(0);
        assertionState = workItemRepo.findById(ref.getId()).get() != null;
        
        // Log test state
        logger.info("\n\n================ Construct JSON Repositories From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(1)
    public void canConstructWorkItemRocksDbRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct ItemStore WorkItem Repository From Factory Test ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType;
        ItemStore backend;
        List<WorkItem> data;
        WorkItem record;
        boolean assertionState = false;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Configure requirements
        repoType = RepositoryType.ITEMSTORE;
        String collectionName = resolveRocksRepoPath();
        backend = this.fetchItemStore(collectionName);
        
        // Configure repository
        logger.info("\nConfiguring repository");
        workItemRepoFactory = new RepositoryFactory<>("Test-Rocks-WorkItem", WorkItem.class, backend, repoType);
        workItemRepo = workItemRepoFactory.make();
        Map<String, String> map = workItemRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for WorkItemRepository:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        data.stream()
            .forEach( elm -> workItemRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        assertionState = !workItemRepo.findById(ref.getId()).isEmpty();
        logger.info("\nDisplayling all records:\n\n{}", TestUtils.modelToJsonString(workItemRepo.findAll()));
        
        // Log test state
        logger.info("\n\n================ Construct ItemStore WorkItem Repository From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(2)
    public void canConstructWorkItemNoSqlRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct Template WorkItem Repository From Factory Test ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType;
        Template backend;
        List<WorkItem> data;
        WorkItem record;
        boolean assertionState = false;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Fetch backend instance
        repoType = RepositoryType.NOSQL;
        backend = this.fetchTemplate();
        logger.info("Backend template:\t'{}'", backend);
        
        // Configure repository
        logger.info("\nConfiguring repository");
        workItemRepoFactory = new RepositoryFactory<>("Test-Template-WorkItem", WorkItem.class, backend, repoType);
        workItemRepo = workItemRepoFactory.make();
        Map<String, String> map = workItemRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for WorkItemRepository:\n'{}'", TestUtils.mapToJsonString(map));
        
        // Add records
        data.stream()
            .forEach( elm -> workItemRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        assertionState = !workItemRepo.findById(ref.getId()).isEmpty();
        logger.info("\nDisplayling all records:\n\n{}", TestUtils.modelToJsonString(workItemRepo.findAll()));
        
        // Log test state
        logger.info("\n\n================ Construct Template WorkItem Repository From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
    
    
    /**
     * Test that a work item can be fetched 
     */
    @Test
    @Order(3)
    public void canConstructWorkItemSqlRepository() {
    
        // Initialize data
        logger.info("\n\n================ Construct JPA WorkItem Repository From Factory Test ================\n");
        TaskTideRepository<WorkItem> workItemRepo;
        RepositoryFactory<WorkItem> workItemRepoFactory;
        RepositoryType repoType;
        EntityManager backend;
        List<WorkItem> data;
        WorkItem record;
        boolean assertionState;
        
        // Generate data
        logger.info("Generating data for testing");
        data = List.of(
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem(),
            TestCaseBuilderUtility.makeTestWorkItem()
        );
        
        // Fetch backend instance
        repoType = RepositoryType.SQL;
        backend = JpaRepositoryUtility.getInstance().fetchEntityManager();
        logger.info("Backend Entity:\t'{}'", backend);
        
        // Configure repository
        logger.info("\nConfiguring repository");
        workItemRepoFactory = new RepositoryFactory<>("Test-JPA-WorkItem", WorkItem.class, backend, repoType);
        workItemRepo = workItemRepoFactory.make();
        Map<String, String> map = workItemRepo.getRepositoryMetaData();
        logger.info("\nDisplaying meta data for WorkItemRepository:\n'{}'", JsonUtils.toJson(true, map));
        
        // Add records: Inserts then finds by id
        logger.info("Inserting records");
        data.stream()
            .forEach( elm -> workItemRepo.insertModel(elm));
        
        // Check that records can be queried
        logger.info("\nVerifying records can be retrieved");
        TaskTideModel<WorkItem> ref = data.get(0);
        TaskTideModel<WorkItem> res = workItemRepo.findById(ref.getId()).get();
        assertionState = res != null;
        logger.info("\nDisplayling all records:\n\n{}", JsonUtils.toJson(true, res));
        
        // Log test state
        logger.info("\n\n================ Construct JPA WorkItem Repository From Factory Test ================\n");
        assertTrue(assertionState, "Reference record could not be retrieved from backend repository");
    }
}