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
import static org.tasktide.itemstore.types.DbTarget.*;
import org.tasktide.itemstore.utils.ItemStoreUtils;

import org.tasktide.itemstore.strategies.ItemStoreLockStrategy;
import org.tasktide.itemstore.strategies.ItemStoreConnectionStrategy;

import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;
import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;
import org.tasktide.itemstore.operations.ItemStoreOperation;
import org.tasktide.itemstore.operations.ThrowableItemStoreOperation;


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
    protected final String storeName;
    protected final Path dbDirectory, masterDB, protoDB;
    
    
    // Owns DB lock lifecycle, and how its done
    protected final ItemStoreLockStrategy lockStrategy;

    
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
        
        String prototypeLabel = ItemStoreUtils.fetchNewProtoTypeLabel();
        this.protoDB = this.dbDirectory.resolve(prototypeLabel);
        
        if ( !ItemStoreUtils.verifyDirectories(this.dbDirectory, this.masterDB, this.protoDB) ) {
            String msg = String.format(
                "Error, cannot write to the configured paths under:\t%s",
                this.dbDirectory
            );
            throw new IllegalArgumentException(msg);
        }
        
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
     * Synchronizes lock and connection lifecycles
     *  under one operation. Allowing callers to solely
     *  own the required operation, without having to
     *  know about these distinct strategies
     * 
     * @param <R>
     * @param label
     * @param target
     * @param operation
     * 
     * @return R Operation Result
     * 
     * @throws {@link ItemStoreCheckedException} 
     */
    @SuppressWarnings("unchecked")
    protected synchronized <R> R withLockedConnection(
        String label,
        DbTarget target,
        ThrowableItemStoreOperation operation
    ) throws ItemStoreCheckedException {
        return (R) this.lockStrategy.withLock(
            label,
            () -> {
                return ItemStoreConnectionStrategy.connectionOperation(
                    target,
                    this,
                    operation
                );
            }
        );
    }
    
    
    /**
     * Synchronizes lock and connection lifecycles
     *  under one operation. Allowing callers to solely
     *  own the required operation, without having to
     *  know about these distinct strategies
     * 
     * @param <R>
     * @param label
     * @param target
     * @param operation
     * 
     * @return R Operation Result
     * 
     * @throws {@link ItemStoreUncheckedException} 
     */
    @SuppressWarnings("unchecked")
    protected synchronized <R> R withLockedConnectionUnchecked (
        String label,
        DbTarget target,
        ItemStoreOperation operation
    ) throws ItemStoreUncheckedException {
        return (R) this.lockStrategy.withLockUnchecked(
            label,
            () -> {
                return ItemStoreConnectionStrategy.connectionOperationUnchecked(
                    target,
                    this,
                    operation
                );
            }
        );
    }
    
    
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
            case MASTER -> {
                return ItemStoreConnectionStrategy
                    .openConnection(
                        DbTarget.MASTER,
                        this
                    );
            }

            case PROTOTYPE -> {
                return ItemStoreConnectionStrategy
                    .openConnection(
                        DbTarget.PROTOTYPE,
                        this
                    );
            }

            default -> {
                return ItemStoreConnectionStrategy
                    .openConnection(
                        DbTarget.BOTH,
                        this
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
            case MASTER -> {
                return ItemStoreConnectionStrategy
                    .closeConnection(
                        DbTarget.MASTER,
                        this
                    );
            }

            case PROTOTYPE -> {
                return ItemStoreConnectionStrategy
                    .closeConnection(
                        DbTarget.PROTOTYPE,
                        this
                    );
            }

            default -> {
                return ItemStoreConnectionStrategy
                    .closeConnection(
                        DbTarget.BOTH,
                        this
                    );
            }
        }
    }


    /**
     * Sync an {@link Item} to the master. Assumes with lock handle
     *  by the interface method, to prevent request duplication
     * 
     * @param item
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public void syncToMaster(Item item) throws ItemStoreCheckedException {
        this.saveItem(DbTarget.MASTER, item);
    }
    
    
    /**
     * Syncs Item list to master
     * 
     * @param items
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public void syncToMaster(List<Item> items) throws ItemStoreCheckedException {
        this.saveItems(DbTarget.MASTER, items);
    }
    
    
    /**
     * Sync all records from cache to master
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public void syncToMaster() throws ItemStoreCheckedException {
        List<Item> data = this.getAll(DbTarget.PROTOTYPE);
        this.saveItems(DbTarget.MASTER, data);
    }
    
    
    /**
     * Cache master DB to prototype
     * 
     * @return boolean
     */
    @Override
    public boolean cacheMaster() {
        try {
            Files.copy(
                this.masterDB,
                this.protoDB,
                StandardCopyOption.REPLACE_EXISTING
            );
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
            LOGGER.error(
                "Error encountered clearing prototype\n",
                ex
            );
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