/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.tasktide.tasktide;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.nosql.Entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.nosql.Template;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseQualifier;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;
import org.eclipse.jnosql.mapping.document.spi.DocumentExtension;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.reflection.spi.ReflectionEntityMetadataExtension;
import org.eclipse.jnosql.mapping.semistructured.EntityConverter;

import org.tasktide.core.manager.TaskTideServiceManager;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.task.ItemTask;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RepositoryType;

import org.tasktide.tasktide.client.TaskTideClient;
import org.tasktide.tasktide.client.TaskTideClientUtility;
import org.tasktide.tasktide.client.TaskTideClients;

import org.tasktide.tasktide.configurer.GlobalConfig;
import org.tasktide.tasktide.configurer.ManagerConfig;
import org.tasktide.tasktide.configurer.TaskTideConfigurer;
import org.tasktide.tasktide.parser.ArgumentTree;

import org.tasktide.tasktide.containerprovider.CdiContainerProvider;
import org.tasktide.tasktide.containerprovider.CdiProviders;

import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.databases.couchdb.communication.CouchDBDocumentManager;

/**
 * Runs the desited {@link TaskTideClient}
 * 
 * @author bkenna
 */
public class TaskTide {
    
    // Attributes
    private static final Logger LOGGER = LogManager.getLogger(TaskTide.class);
    
    
    /**
     * Fetches splash string
     * 
     * @return String
     */
    static String fetchSplashString() {
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
    static void printSplash() {
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
     * Method to configure CDI container
     * 
     * @param cdiProvider
     * @return {@link CdiContainerProvider}
     */
    @SuppressWarnings("unchecked")
    static CdiContainerProvider configureCdiInstance(CdiProviders cdiProvider) {
        
        // Initialize
        CdiContainerProvider provider = cdiProvider.createProvider();
        provider.initialize();
        
        // Add packages, and extensions
        if ( !cdiProvider.isProvider(CdiProviders.QUARKUS) ) {
            provider.addExtension(ReflectionEntityMetadataExtension.class, DocumentExtension.class);
            provider.addBeanClass(
           GlobalConfig.class, ManagerConfig.class,
           WorkItem.class, ItemTask.class, Step.class, Workflow.class
            );
        }
        
        // Return
        LOGGER.info("Starting '{}' container", cdiProvider);
        provider.start();
        return provider;
    }
    
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Try run
        try {
            
            // Configure provider and argument tree
            //SeContainer container = SeContainerInitializer.newInstance().initialize();
            TaskTide.printSplash();
            LOGGER.info("Configuring the CDI Container Provider");
            //Template template = container.select(DocumentTemplate.class).get();
            //LOGGER.info("Created template:\t'{}'", template);
            CdiContainerProvider provider = configureCdiInstance(CdiProviders.WELD);

            // Fetch config map
            LOGGER.info("Fetching TaskTide configs");
            ArgumentTree argTree = new ArgumentTree(" ");
            Map<String, TaskTideConfigurer> configMap = TaskTideClientUtility.fetchClienConfigMap(provider, argTree, args);

            // Fetch TaskTideServiceManager
            LOGGER.info("Fetching the TaskTideServiceManager");
            RepositoryType repoType = TaskTideClientUtility.fetchRepoType(configMap.get("Global"));
            TaskTideServiceManager taskTideServiceManager = TaskTideClientUtility.fetchManager(provider, repoType);

            // Handle which client to run
            LOGGER.info("Determining which TaskTideClient to run");
            TaskTideClients val = TaskTideClientUtility.configureClient(argTree);
            TaskTideClient client = val.makeClient(taskTideServiceManager, configMap, argTree);

            // Run client
            LOGGER.info("Running 'TaskTide-{}' client with cleanup", val);
            client.runClient(args);

            // Tear down container
            LOGGER.info("TaskTideClient completed, tearing down container");
            provider.shutdown();
            System.exit(0);
        }
        
        // Otherwise show error
        catch (Exception ex) {
            LOGGER.fatal("Exiting on fatal error:\t'{}'", ex.toString());
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
}
