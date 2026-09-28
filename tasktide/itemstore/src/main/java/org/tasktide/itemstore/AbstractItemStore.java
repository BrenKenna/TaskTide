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
package org.tasktide.itemstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.itemstore.types.DbTarget;
import org.tasktide.itemstore.utils.ItemStoreUtils;

import org.tasktide.itemstore.strategies.ItemStoreLockStrategy;
import org.tasktide.itemstore.strategies.ItemStoreConnectionStrategy;

import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;


/**
 * Abstract {@link ItemStore} to implement getting ItemStore attributes,  
 *  handling the locking/releasing of masterDB for updates, and
 *  caching/clearing the master prototype to another file.
 * 
 * @author bkenna
 */
public abstract class AbstractItemStore implements ItemStore {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(AbstractItemStore.class);
    private final String storeName;
    private final Path dbDirectory, masterDB, protoDB;
    
    // Owns DB lock lifecycle, and how its done
    private final ItemStoreLockStrategy lockStrategy;

    /**
     * Construct with all attributes. Throws IllegalArgument
     *  RunTime exception if not writable. 
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     */
    @Deprecated
    public AbstractItemStore(
        String storeName,
        String dbDirectory,
        String masterDB,
        String protoDB
    ) {
        this.storeName = storeName;
        this.dbDirectory = Paths.get(dbDirectory);
        this.masterDB = this.dbDirectory.resolve(masterDB);
        this.protoDB = this.dbDirectory.resolve(protoDB);
        
        if ( !ItemStoreUtils.verifyDirectory(this.dbDirectory) ) {
            String msg = String.format("Error, cannot write to the configured path:\t%s", this.masterDB);
            throw new IllegalArgumentException(msg);
        }
        
        this.lockStrategy = new ItemStoreLockStrategy(
            this.storeName,
            this.dbDirectory,
            this.masterDB
        );
    }
    
    
    /**
     * Preferred constructor moving forward
     * 
     * @param storeName
     * @param dbDirectory 
     */
    public AbstractItemStore(String storeName, String dbDirectory) {
        this.storeName = storeName;
        this.dbDirectory = Paths.get(dbDirectory);
        this.masterDB = this.dbDirectory.resolve("master");
        
        if ( !ItemStoreUtils.verifyDirectory(this.dbDirectory) ) {
            String msg = String.format("Error, cannot write to the configured path:\t%s", this.masterDB);
            throw new IllegalArgumentException(msg);
        }
        
        String prototypeLabel = ItemStoreUtils.fetchNewProtoTypeLabel();
        this.protoDB = this.dbDirectory.resolve(prototypeLabel);
        
        this.lockStrategy = new ItemStoreLockStrategy(
            this.storeName,
            this.dbDirectory,
            this.masterDB
        );
    }
    

    /**
     * Get connection
     * 
     * @param <T>
     * @param target
     * @param type SqliteStore | RocksDbStore
     * @return T 
     */
    protected abstract <T> T getConnection(DbTarget target, Class<T> type);
    
    
    /**
     * Standardizes how concrete {@link ItemStore} open connections,
     *  using the evaluation logic from {@link ItemStoreConnectionStrategy},
     *  and targeted {@link AbstractItemStore} methods. Allowing
     *  concrete classes to only own opening appropriate {@link DbTarget}
     * 
     * @param target
     * @return boolean
     */
    @Override
    public synchronized boolean openConn(DbTarget target) {
        switch (target) {
            case DbTarget.MASTER -> {
                return ItemStoreConnectionStrategy.openConnection(
                    target,
                    this,
                    () -> { return this.openMaster(); }
                );
            }
            
            case DbTarget.PROTOTYPE -> {
                return ItemStoreConnectionStrategy.openConnection(
                    target,
                    this,
                    () -> { return this.openPrototoype(); }
                );
            }
            
            default -> {
                return ItemStoreConnectionStrategy.openConnection(
                    target,
                    this,
                    () -> { return
                        this.openMaster() &&
                        this.openPrototoype()
                    ;}
                );
            }
        }
    }
    
    
    /**
     * Standardizes how concrete {@link ItemStore} close connections,
     *  using the evaluation logic from {@link ItemStoreConnectionStrategy},
     *  and targeted {@link AbstractItemStore} methods. Allowing
     *  concrete classes to only own opening appropriate {@link DbTarget}
     * 
     * @param target
     * @return boolean
     */
    @Override
    public synchronized boolean closeConn(DbTarget target) {
        switch (target) {
            case DbTarget.MASTER -> {
                return ItemStoreConnectionStrategy.closeConnection(
                    target,
                    this,
                    () -> { return this.closeMaster(); }
                );
            }
            
            case DbTarget.PROTOTYPE -> {
                return ItemStoreConnectionStrategy.closeConnection(
                    target,
                    this,
                    () -> { return this.closePrototoype(); }
                );
            }
            
            default -> {
                return ItemStoreConnectionStrategy.closeConnection(
                    target,
                    this,
                    () -> { return
                        this.closeMaster() &&
                        this.closePrototoype()
                    ;}
                );
            }
        }
    }
    
    
    /**
     * Enforces concrete classes to implement
     *  opening connection against master DB
     * 
     * @return boolean
     */
    protected abstract boolean openMaster();
    
    
    /**
     * Enforces concrete classes to implement
     *  opening connection against prototype DB
     * 
     * @return boolean
     */
    protected abstract boolean openPrototoype();

    
    /**
     * Enforces concrete classes to implement
     *  closing connection against master DB
     * 
     * @return boolean
     */
    protected abstract boolean closeMaster();
    
    
    /**
     * Enforces concrete classes to implement
     *  closing connection against prototype DB
     * 
     * @return boolean
     */
    protected abstract boolean closePrototoype();


    /**
     * Sync an {@link Item} to the master. Waits for instance to lock, then releases
     * 
     * @param item
     * @throws Exception 
     */
    @Override
    public void syncToMaster(Item item) throws ItemStoreCheckedException {
        try {
            this.itemStoreTransactionNoReturn("Sync record to Master", () -> {
                try {
                    this.saveItem(DbTarget.MASTER, item);
                }
                catch (Exception ex) {
                    //throw new ItemStoreCheckedException("Error syncing to master", ex);
                }
            });
        }
        
        catch (Exception ex) {
            throw new ItemStoreCheckedException("Error syncing record to master", ex);
        }
    }
    
    
    /**
     * Syncs Item list to master
     * 
     * @param items
     */
    @Override
    public void syncToMaster(List<Item> items) throws ItemStoreCheckedException {
        
        try {
            this.itemStoreTransactionNoReturn("Sync records to Master", () -> {
                try {
                    this.saveItems(DbTarget.MASTER, items);
                }
                catch (Exception ex) {
                    //throw new ItemStoreCheckedException("Error syncing to master", ex);
                }
            });
        }
        
        catch (Exception ex) {
            throw new ItemStoreCheckedException("Error syncing records to master", ex);
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
            List<Item> data = this.getAll(DbTarget.MASTER);
            this.saveItems(DbTarget.MASTER, data);
        } 
        catch (InterruptedException ex) {}
        finally {
            releaseLock(true);
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
            Files.copy(this.masterDB, this.protoDB, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Remove prototypeDB
     * 
     * @return boolean
     */
    @Override
    public boolean clearPrototype() {
        try {
            ItemStoreUtils.deleteRecursively(this.protoDB);
            return true;
        } catch (IOException ex) {
            return false;
        }
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
        return this.masterDB.toString();
    }

    
    /**
     * Return file path prototype DB
     * 
     * @return String
     */
    @Override
    public String getFilePath() {
        return this.protoDB.toString();
    }

    
    /**
     * Return directory of where DB is stored
     * 
     * @return String
     */
    @Override
    public String getDbDirectory() {
        return this.dbDirectory.toString();
    }
}