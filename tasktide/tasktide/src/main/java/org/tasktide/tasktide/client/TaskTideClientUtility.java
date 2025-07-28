/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.eclipse.jnosql.mapping.column.ColumnTemplate;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.graph.GraphTemplate;
import org.eclipse.jnosql.mapping.keyvalue.KeyValueTemplate;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.manager.ManagerTarget;
import org.tasktide.core.services.ServiceFactory;
import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.manager.TaskTideServiceManager;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;
import org.tasktide.itemstore.RocksDBStore;
import org.tasktide.tasktide.TaskTide;
import org.tasktide.tasktide.configurer.EngineConfig;

import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.ManagerConfig;
import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;
import org.tasktide.tasktide.parser.ArgumentTree;



/**
 * Utility class for {@link TaskTideClient} interface
 * 
 * @author bkenna
 */
public class TaskTideClientUtility {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTideClientUtility.class);

    
    /**
     * Fetches splash string
     * 
     * @return String
     */
    public static String fetchSplashString() {
        String output = "";
        try (InputStream is = TaskTide.class.getClassLoader().getResourceAsStream("splash.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output += "\n" + line;
            }
            return output;
        } catch (IOException e) {
            return "";
        }
    }
    
    
    /**
     * Print splash
     * 
     */
    public static void printSplash() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("splash.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        } catch (IOException e) {
            
        }
    }
    
    
    /**
     * Configures {@link CdiContainerProvider} for provided {@link CdiProviders} type
     * 
     * @param cdiProvider
     * @return {@link CdiContainerProvider}
     */
    @SuppressWarnings("unchecked")
    public static CdiContainerProvider configureCdiInstance(CdiProviders cdiProvider) {
        
        // Initialize
        CdiContainerProvider provider = cdiProvider.createProvider();
        provider.initialize();
        
        // Add packages, and extensions
        //  Required with Netbeans, not with in linux shell?
        if ( !cdiProvider.isProvider(CdiProviders.QUARKUS) ) {
            provider.addExtension(ReflectionEntityMetadataExtension.class, DocumentExtension.class);
            provider.addBeanClass(
           GlobalConfig.class, ManagerConfig.class, EngineConfig.class,
           WorkItem.class, ItemTask.class, Step.class, Workflow.class
            );
        }
        
        // Return
        LOGGER.info("Starting '{}' container", cdiProvider);
        provider.start();
        return provider;
    }

    
    /**
     * Fetch the specific client to configure and run
     * 
     * @param argTree
     * @return {@link TaskTideClientType}
     */
    public static TaskTideClientType configureClient(ArgumentTree argTree) {
        String cliString = (String) argTree.getGlobalArguments().getArgMap().get("Client").getValue();
        return TaskTideClientType.valueOf(cliString);
    }

    
    /**
     * Fetches the {@link RepositoryType} from the {@link GlobalConfig}.
     *   Returns NOSQL atm
     * 
     * @param configMap
     * @return {@link RepositoryType}
     */
    public static RepositoryType fetchRepoType(ClientConfigMap configMap) {
        String str = (String) configMap.getArgTree().getGlobalArguments().getArgMap().get("Repository Type").getValue();
        LOGGER.info("Querying configured repo type:\t'{}'", str);
        return RepositoryType.get(str);
    }
    
    
    /**
     * Fetches {@link TaskTideServiceManager} from {@link CdiContainerProvider} using
     *  the value of the {@link RepositoryType}
     * 
     * @param repoType
     * @param configMap
     */
    public static void initServiceManager(RepositoryType repoType, ClientConfigMap configMap) {
        switch ( repoType ) {
            case NOSQL -> {
                LOGGER.info("Configuring NoSQL Template ServiceManager");
                Template backend = TaskTideClientUtility.fetchTemplate(configMap);
                initServiceManager(backend);
            }
            
            case ITEMSTORE -> {
                LOGGER.info("Configuring ItemStore ServiceManager");
                Map<ManagerTarget, TaskTideRepository> repoMap = fetchItemStoreRepoMap(repoType, configMap);
                initServiceManager(repoType, repoMap);
            }
            
            case SQL -> {
                LOGGER.info("Configuring SQL ServiceManager");
                Map<ManagerTarget, TaskTideRepository> repoMap = fetchEntityManagerRepoMap(repoType, configMap);
                initServiceManager(repoType, repoMap);
            }
            
            default -> {
                throw new IllegalStateException("No backend repository type detected");
            }
        }
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} for {@link Template} backend
     * 
     * @param backend
     */
    public static void initServiceManager(Template backend) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(RepositoryType.NOSQL, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(RepositoryType.NOSQL, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(RepositoryType.NOSQL, backend, "Workflow-Service");
        
        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} from {@link TaskTideRepository} map
     * 
     * @param repoType
     * @param repoMap
     */
    public static void initServiceManager(RepositoryType repoType, Map<ManagerTarget, TaskTideRepository> repoMap) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(repoType, repoMap.get(ManagerTarget.WORKITEM), "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(repoType, repoMap.get(ManagerTarget.STEP), "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, repoMap.get(ManagerTarget.WORKFLOW), "Workflow-Service");
        
        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    /**
     * Fetch {@link TaskTideServiceManager} for the backend {@link RepositoryType}
     * 
     * @param repoType
     * @param backend
     */
    public static void initServiceManager(RepositoryType repoType, Object backend) {
        
        // Initialize vars
        TaskTideService<WorkItem> workItemService;
        TaskTideService<Step> stepService;
        TaskTideService<Workflow> workflowService;
        
        // Make services
        workItemService = ServiceFactory.makeWorkItemService(repoType, backend, "WorkItem-Service");
        stepService = ServiceFactory.makeStepService(repoType, backend, "Step-Service");
        workflowService = ServiceFactory.makeWorkflowService(repoType, backend, "Workflow-Service");

        // Return manager
        TaskTideServiceManager.initialize(workItemService, stepService, workflowService);
    }
    
    
    
    /**
     * Fetches {@link Template}
     * 
     * @param configMap
     * @return {@link Template}
     */
    @SuppressWarnings("unchecked")
    public static Template fetchTemplate(ClientConfigMap configMap) {
        Class clazz = provideTemplateClass(configMap);
        return (Template) CDI.current().select(clazz).get();
    }
    
    
    /**
     * Provides the required {@link Template} class to inject
     * 
     * @param configMap
     * @return Class
     */
    public static Class provideTemplateClass(ClientConfigMap configMap) {
        String dbType = (String) configMap.getArgTree().getGlobalArguments().getArgMap().get("NoSQL Database Type").getValue();
        dbType = dbType.strip().replace(" ", "").replace("-", "");
        LOGGER.info("Evaluating DatabaseType:\t'{}'", dbType);
        switch( dbType.toLowerCase() ) {
            case "document" -> {
                return DocumentTemplate.class;
            }
            
            case "keyvalue" -> {
                return KeyValueTemplate.class;
            }
            
            case "column" -> {
                return ColumnTemplate.class;
            }
            
            case "graph" -> {
                return GraphTemplate.class;
            }
            
            default -> {
                throw new IllegalArgumentException("NoSQL DB Type must be one of: Document, KeyValue, Column or Grpah");
            }
        }
    }
    
    
    /**
     * Fetch {@link ItemStore} for store name
     * 
     * @param storeName
     * @param storeType
     * @return {@link ItemStore}
     */
    public static ItemStore fetchItemStore(String storeName, ItemStoreType storeType) {
        
        // Resolve store locatoin
        Path store = Paths.get(storeName);
        try {
            Files.createDirectories(store);
            LOGGER.info("RocksDB ItemStore created under:\t'{}'", storeName);
        }
        catch (IOException ex) {
            LOGGER.info("RocksDB ItemStore already exists under:\t'{}'", storeName);
        }
        
        // Set vars
        String dbDirectory = store.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        return storeType.makeItemStore(storeName, dbDirectory, masterDB, protoDB);
    }
    
    
    /**
     * Fetch {@link ItemStore} map
     * 
     * @param repoType
     * @param configMap
     * @return 
     */
    public static Map<ManagerTarget, ItemStore> fetchItemStoreMap(RepositoryType repoType, ClientConfigMap configMap) {
        Map<ManagerTarget, ItemStore> output = new HashMap<>();
        String storeName = (String) configMap.getArgTree().getGlobalArguments().getArgument("File Path").getValue();
        String storeTypeString = (String) configMap.getArgTree().getGlobalArguments().getArgument("Repository Type").getValue();
        
        ItemStoreType storeType = ItemStoreType.get(storeTypeString);
        for ( ManagerTarget elm : ManagerTarget.values() ) {
           ItemStore store = fetchItemStore(storeName + "/" + elm.toString(), storeType);
           output.put(elm, store);
        }
        return output;
    }
    
    
    /**
     * Wrapper method to fetch {@link ItemStore} {@link TaskTideRepository} map
     * 
     * @param repoType
     * @param configMap
     * @return Map-{@link ManagerTarget}, {@link TaskTideRepository}
     */
    public static Map<ManagerTarget, TaskTideRepository> fetchItemStoreRepoMap(RepositoryType repoType, ClientConfigMap configMap) {
        
        // Initialize output and fetch item store map
        Map<ManagerTarget, TaskTideRepository> output = new HashMap<>();
        Map<ManagerTarget, ItemStore> itemStoreMap = fetchItemStoreMap(repoType, configMap);
        
        // Add work item repo
        ItemStore store = itemStoreMap.get(ManagerTarget.WORKITEM);
        TaskTideRepository repo = repoType.createRepository(WorkItem.class, store, store.getDbDirectory());
        output.put(ManagerTarget.WORKITEM, repo);
        
        // Add step repo
        store = itemStoreMap.get(ManagerTarget.STEP);
        repo = repoType.createRepository(Step.class, store, store.getDbDirectory());
        output.put(ManagerTarget.STEP, repo);
        
        // Add workflow repo
        store = itemStoreMap.get(ManagerTarget.WORKFLOW);
        repo = repoType.createRepository(Workflow.class, store, store.getDbDirectory());
        output.put(ManagerTarget.WORKFLOW, repo);
        
        // Return results
        return output;
    }
    
    
    /**
     * Wrapper method to construct HikariConfig
     * 
     * @param configMap
     * @return HikariConfig
     */
    public static HikariConfig fetchHikariConfig(ClientConfigMap configMap) {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl((String) configMap.getArgTree().getGlobalArguments().getArgument("Database URL").getValue());
        conf.setUsername((String) configMap.getArgTree().getGlobalArguments().getArgument("Database Username").getValue());
        conf.setPassword((String) configMap.getArgTree().getGlobalArguments().getArgument("Database Password").getValue());
        conf.setDriverClassName((String) configMap.getArgTree().getGlobalArguments().getArgument("Database Driver").getValue());
        return conf;
    }
    
    
    /**
     * Wrapper method to fetch HikariDataSource
     * 
     * @param configMap
     * @return DataSource
     */
    public static DataSource fetchDataSource(ClientConfigMap configMap) {
        HikariConfig conf = fetchHikariConfig(configMap);
        return new HikariDataSource(conf);
    }
    
    
    /**
     * Fetch configuration map for entity manager
     * 
     * @param configMap
     * @param dataSource
     * @return Map-String, Object
     */
    public static Map<String, Object> fetchEntityManagerConfig(ClientConfigMap configMap, DataSource dataSource) {
        Map<String, Object> conf = new HashMap<>();
        conf.put("jakarta.persistence.nonJtaDataSource", dataSource);
        conf.put("hibernate.hbm2ddl.auto", (String) configMap.getArgTree().getGlobalArguments().getArgument("Database DDL Update").getValue());
        conf.put("hibernate.dialect", (String) configMap.getArgTree().getGlobalArguments().getArgument("Database Dialect Driver").getValue());
        conf.put("hibernate.show_sql", (String) configMap.getArgTree().getGlobalArguments().getArgument("Database Show SQL").getValue());
        return conf;
    }
    
    
    /**
     * Fetcg entity manager factory for provided {@link ManagerTarget} using config
     *  throwing IllegalArgumentException if not Workflow, Step, or WorkItem
     * 
     * @param conf
     * @param modelType
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory fetchEntityManagerFactory(Map<String, Object> conf, ManagerTarget modelType) {
        switch (modelType) {
            case WORKFLOW -> {
                return Persistence.createEntityManagerFactory("Workflow", conf);
            }
            case STEP -> {
                return Persistence.createEntityManagerFactory("Step", conf);
            }
            case WORKITEM -> {
                return Persistence.createEntityManagerFactory("WorkItem", conf);
            }
            default -> {
                throw new IllegalArgumentException("Task TideModel Type must be one of Workflow, Step, or WorkItem");
            }
        }
    }
    
    
    /**
     * Fetch backend entity manager for {@link ManagerTarget}
     * 
     * @param repoType
     * @param configMap
     * @param modelType
     * @return EntityManager
     */
    public static EntityManager fetchEntityManager(RepositoryType repoType, ClientConfigMap configMap, ManagerTarget modelType) {
        
        // Configure dependanceis
        DataSource dataSource = fetchDataSource(configMap);
        Map<String, Object> conf = fetchEntityManagerConfig(configMap, dataSource);
        
        // Return entity manager for model type
        return fetchEntityManagerFactory(conf, modelType).createEntityManager();
    }
    
    
    /**
     * Fetch EnityManager map
     * 
     * @param repoType
     * @param configMap
     * @return Map-{@link ManagerTarget}, EntityManager
     */
    public static Map<ManagerTarget, EntityManager> fetchEntityManagerMap(RepositoryType repoType, ClientConfigMap configMap) {
        Map<ManagerTarget, EntityManager> output = new HashMap<>();
        for ( ManagerTarget elm : ManagerTarget.values() ) {
           EntityManager manager = fetchEntityManager(repoType, configMap, elm);
           output.put(elm, manager);
        }
        return output;
    }
    
    
    /**
     * Wrapper method to fetch EntityManager {@link TaskTideRepository} map
     * 
     * @param repoType
     * @param configMap
     * @return Map-{@link ManagerTarget}, {@link TaskTideRepository}
     */
    public static Map<ManagerTarget, TaskTideRepository> fetchEntityManagerRepoMap(RepositoryType repoType, ClientConfigMap configMap) {
        
        // Initialize output and fetch item store map
        Map<ManagerTarget, TaskTideRepository> output = new HashMap<>();
        Map<ManagerTarget, EntityManager> entityStoreMap = fetchEntityManagerMap(repoType, configMap);
        
        // Add work item repo
        EntityManager entity = entityStoreMap.get(ManagerTarget.WORKITEM);
        TaskTideRepository repo = repoType.createRepository(WorkItem.class, entity, "WorkItem");
        output.put(ManagerTarget.WORKITEM, repo);
        
        // Add step repo
        entity = entityStoreMap.get(ManagerTarget.STEP);
        repo = repoType.createRepository(Step.class, entity, "Step");
        output.put(ManagerTarget.STEP, repo);
        
        // Add workflow repo
        entity = entityStoreMap.get(ManagerTarget.WORKFLOW);
        repo = repoType.createRepository(Workflow.class, entity, "Workflow");
        output.put(ManagerTarget.WORKFLOW, repo);
        
        // Return results
        return output;
    }
}
