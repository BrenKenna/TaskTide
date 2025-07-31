/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;

import org.tasktide.core.repository.RepositoryType;
import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.repository.nosql_repo.NoSqlTemplateUtility;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;

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
                initNoSqlServiceManager(configMap);
            }
            
            case ITEMSTORE -> {
                LOGGER.info("Configuring ItemStore ServiceManager");
                initItemStoreServiceManager(configMap);
            }
            
            case SQL -> {
                LOGGER.info("Configuring SQL ServiceManager");
                initSqlServiceManager();
            }
            
            default -> {
                throw new IllegalStateException("No backend repository type detected");
            }
        }
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} for NoSQL backend
     * 
     * @param configMap
     */
    public static void initNoSqlServiceManager(ClientConfigMap configMap) {
        String dbType = (String) configMap.getArgTree().getGlobalArguments().getArgMap().get("NoSQL Database Type").getValue();
        NoSqlTemplateUtility.initialize(dbType);
        NoSqlTemplateUtility.get().initServiceManager();;
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} for SQL backend
     * 
     */
    public static void initSqlServiceManager() {
        JpaRepositoryUtility.get().initServiceManager();
    }
    
    
    /**
     * Fetch {@link TaskTideServiceManager} for {@link ItemStore} backend
     * 
     * @param configMap
     */
    public static void initItemStoreServiceManager(ClientConfigMap configMap) {
        String storeTypeString, storeName;
        storeName = (String) configMap.getArgTree().getGlobalArguments().getArgument("File Path").getValue();
        storeTypeString = (String) configMap.getArgTree().getGlobalArguments().getArgument("Repository Type").getValue();
        ItemStoreType storeType = ItemStoreType.get(storeTypeString);
        ItemStoreRepositoryUtility.initialize(storeType, storeName);
        ItemStoreRepositoryUtility.get().initServiceManager();
    }
}