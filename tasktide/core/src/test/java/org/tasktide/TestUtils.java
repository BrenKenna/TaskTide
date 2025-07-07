/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.nosql.Template;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

import org.tasktide.core.TaskTideModel;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.stores.RocksDBStore;


/**
 *
 * Various static methods to support development & use of TaskTide
 * 
 * @author bkenna
 */
public class TestUtils {
    
    
    /**
     * Represent map as json string
     * 
     * @param map
     * @return String Json
     */
    public static String mapToJsonString(Map map) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(map);
    }

    
    /**
     * Represent list as json string
     * 
     * @param list
     * @return String Json
     */
    public static String mapToJsonString(List list) {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
        return jsonb.toJson(list);
    }
    
    
    /**
     * Represent {@link TaskTideModel TaskTideModel} list as json doc
     * 
     * @param models
     * @return String
     */
    public static String modelToJsonString(List<? extends TaskTideModel<?>> models) {
        return models.stream()
                .map(TaskTideModel::toJson)
                .collect(Collectors.joining(",\n", "{\n", "\n]"));
    }
    
    
    /**
     * Resolve a path string for test purposes
     * 
     * @return String
     */
    public static String resolveRocksRepoPath() {
        Path cwd = Paths.get( System.getProperty("user.dir") );
        Path workDir = cwd.resolve("project-test-repos").resolve("step");
        return workDir.toString();
    }
    
    
    /**
     * Fetch a {@link RocksDBStore} with name
     * 
     * @param storeName
     * @return {@link ItemStore} of {@link RocksDBStore}
     */
    public static ItemStore fetchItemStore(String storeName) {
    
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
    public static Template fetchTemplate() {
        SeContainer container;
        container = SeContainerInitializer.newInstance().initialize();
        return container.select(DocumentTemplate.class).get();
    }
}
