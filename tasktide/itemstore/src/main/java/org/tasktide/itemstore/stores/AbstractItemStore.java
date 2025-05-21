/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.itemstore.stores;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import java.util.List;

import org.tasktide.itemstore.Item;
import org.tasktide.itemstore.ItemStore;


/**
 * Abstract ItemStore to implement getting ItemStore attributes,  
 *  handling the locking/releasing of masterDB for updates, and
 *  caching/clearing the master prototype to another file.
 * 
 * @author bkenna
 */
public abstract class AbstractItemStore implements ItemStore {
    
    // Attributes
    protected final String storeName;
    protected final String dbDirectory;
    protected final String masterDB;
    protected final String protoDB;
    protected final Path masterLock;
    
    
    /**
     * Construct with all attributes
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     */
    public AbstractItemStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
        this.storeName = storeName;
        this.dbDirectory = dbDirectory;
        this.masterDB = masterDB;
        this.protoDB = protoDB;
        this.masterLock = Paths.get(this.masterDB + ".lock");
    }

    
    /**
     * Return store name
     * 
     * @return String
     */
    @Override
    public String getStoreName() {
        return storeName;
    }

    
    /**
     * Return full file path for master
     * 
     * @return String
     */
    @Override
    public String getMasterFilePath() {
        return this.masterDB;
    }

    
    /**
     * Return file path prototype DB
     * 
     * @return String
     */
    @Override
    public String getFilePath() {
        return this.protoDB;
    }

    
    /**
     * Return directory of where DB is stored
     * 
     * @return String
     */
    @Override
    public String getDbDirectory() {
        return this.dbDirectory;
    }

    
    /**
     * Waits until master is locked
     * 
     * @throws InterruptedException 
     */
    protected void waitForLock() throws InterruptedException {
        boolean end = false;
        while (!end) {
            try {
                Files.createFile(masterLock);
                end = true;
            }
            catch (FileAlreadyExistsException  ex) {
                TimeUnit.MILLISECONDS.sleep(500);
            }
            catch (IOException ex) {}
        }
    }
    
    
    /**
     * Release lock on master
     * 
     * @return boolean
     */
    protected boolean releaseLock() {
        try {
            Files.deleteIfExists(masterLock);
            return false;
        } catch (IOException ex) {
            return true;
        }
    }
    
    
    /**
     * Sync an {@link Item} to the master. Waits for instance to lock, then releases
     * 
     * @param item
     * @throws Exception 
     */
    @Override
    public void syncToMaster(Item item) throws Exception {
        try {
            waitForLock();
            saveItemToMaster(item);
        } 
        finally {
            releaseLock();
        }
    }
    
    
    /**
     * Syncs Item list to master
     * 
     * @param items
     */
    @Override
    public void syncToMaster(List<Item> items) throws Exception {
        try {
            waitForLock();
            saveItemsToMaster(items);
        } 
        finally {
            releaseLock();
        }
    }
    
    
    /**
     * Sync all records from cache to master
     * 
     */
    @Override
    public void syncToMaster() throws Exception {
        try {
            waitForLock();
            saveItemsToMaster(getAll());
        } 
        catch (InterruptedException ex) {}
        finally {
            releaseLock();
        }
    }
    
    
    /**
     * Cache master DB to prototype
     * 
     * @return boolean
     */
    @Override
    public boolean cacheMaster() {
        try {
            Files.copy(Paths.get(this.masterDB), Paths.get(this.protoDB), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Remove prototypeDB
     * 
     * @return 
     */
    @Override
    public boolean clearPrototype() {
        try {
            Files.deleteIfExists( Paths.get(this.protoDB) );
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}