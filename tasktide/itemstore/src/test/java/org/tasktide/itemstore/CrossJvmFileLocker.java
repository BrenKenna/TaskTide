/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.tasktide.itemstore;

import java.io.IOException;
import java.nio.file.Path;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Class to add random {@link Item} to single master, for testing
 *  that multiple JVMs can safely/are queued accessing {@link ItemStore}.
 * 
 * @author bkenna
 */
public class CrossJvmFileLocker {

    // Attributes for class
    private static final Logger logger = LogManager.getLogger(CrossJvmFileLocker.class);
    private static final String jvmFlag = UUID.randomUUID().toString();
           
    
    /**
     * Provide {@link RocksDbStore}
     * 
     * @return {@link RocksDbStore}
     */
    private static AbstractItemStore provideDB() {
        
        // Initialize required vars
        AbstractItemStore itemStore;
        Path workDir;
        
        // Configure database
        workDir = ItemStoreTestUtils.setWorkingDirectory("rocksDB", "AbstractItemStore-MultiJVM");
        itemStore = ItemStoreTestUtils.makeRocksDB("AbstractItemStore-MultiJVM", workDir);
        
        // Provide ItemStore
        return itemStore;
    }
    
    
    /**
     * Writes jvm flag
     * 
     * @param workDir
     * @param phase
     * @return boolean
     */
    public static boolean writeFlag(Path workDir, String phase) {
        Path flagPath = workDir.resolve(phase + "_" + jvmFlag);
        try {
            return flagPath.toFile().createNewFile();
        }
        catch ( IOException ex ) {
            return false;
        }
    }
    
    
    /**
     * Get a random {@link Item} using a random {@link UUID} for each property
     * 
     * @return {@link Item}
     */
    private static Item getRandomItem() {
        
        // Initialize vars
        String id, state, payload;
        Item output;
        
        // Create item
        id = UUID.randomUUID().toString();
        state = UUID.randomUUID().toString();
        payload = UUID.randomUUID().toString();
        output = new Item(id, state, payload);
        
        // Return results
        return output;
    }
    
    
    /**
     * Fetch {@link RocksDbStore} and add a random {@link Item} to master
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Initialize variables
        AbstractItemStore itemStore;
        Item randomItem;
        Path workDir = ItemStoreTestUtils.setWorkingDirectory("rocksDB", "AbstractItemStore-MultiJVM");
        
        // Fetch DB and random Item
        String flag = UUID.randomUUID().toString();
        //writeFlag(workDir, "Start");
        logger.info("Configuring Database & RandomItem for JVM at:\t'{}'", System.currentTimeMillis());
        itemStore = provideDB();
        //writeFlag(workDir, "DB-Created");
        randomItem = getRandomItem();
        //writeFlag(workDir, "Make-Item");
        
        // Try insert record
        try {
            itemStore.saveItem(DbTarget.MASTER, randomItem);
            writeFlag(workDir, "Item-Imported");
            
        } catch (Exception ex) {
            logger.error(
          "Error inserting record:\n\nDatabase:\t'{}'\n\nRecord:\t'{}'",
             itemStore.getMasterFilePath(), randomItem.toString()
            );
            writeFlag(workDir, "Item-Failed-Import");
            ex.printStackTrace();
        }
        finally {
            itemStore.closeConn(DbTarget.BOTH);
            //writeFlag(workDir, "Connections-Closed");
            itemStore.clearPrototype();
            //writeFlag(workDir, "Prototype-Cleared");
        }
        // writeFlag(workDir, "Done");
        logger.info("Finished Database & RandomItem for JVM at:\t'{}'", System.currentTimeMillis());
    }
    
}
