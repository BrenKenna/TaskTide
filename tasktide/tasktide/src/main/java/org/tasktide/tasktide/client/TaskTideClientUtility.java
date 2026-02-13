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

import org.tasktide.core.repository.itemstore_repo.ItemStoreRepositoryUtility;
import org.tasktide.core.repository.jpa_repo.JpaRepositoryUtility;
import org.tasktide.core.repository.nosql_repo.TemplateRepositoryUtility;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.ItemStoreType;

import org.tasktide.tasktide.TaskTide;
import org.tasktide.tasktide.configurer.EngineConfig;

import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.ManagerConfig;
import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;
import org.tasktide.parser.ArgumentTree;


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
     * @param flag
     * @return {@link CdiContainerProvider}
     */
    @SuppressWarnings("unchecked")
    public static CdiContainerProvider configureCdiInstance(CdiProviders cdiProvider, boolean flag) {
        
        // Initialize
        CdiContainerProvider provider = cdiProvider.createProvider();
        provider.initialize();
        
        // Add packages, and extensions
        //  Required with Netbeans, not in linux shell?
        if ( !cdiProvider.isProvider(CdiProviders.QUARKUS) ) {
            provider.addExtension(ReflectionEntityMetadataExtension.class, DocumentExtension.class);
            
            if ( flag ) {
                provider.addBeanClass(
                GlobalConfig.class, ManagerConfig.class, EngineConfig.class
                );
            }
            
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
        TemplateRepositoryUtility.initialize(dbType);
        TemplateRepositoryUtility.get().initServiceManager();
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
        LOGGER.debug("Repository FilePath is:\t'{}'", configMap.getArgTree().getGlobalArguments().getArgument("File Path").getValue());
        storeName = (String) configMap.getArgTree().getGlobalArguments().getArgument("File Path").getValue(); // Thinks boolean
        storeTypeString = (String) configMap.getArgTree().getGlobalArguments().getArgument("Repository Type").getValue();
        ItemStoreType storeType = ItemStoreType.get(storeTypeString);
        ItemStoreRepositoryUtility.initialize(storeType, storeName);
        ItemStoreRepositoryUtility.get().initServiceManager();
    }
}