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

import java.util.List;
import java.util.Map;

import org.tasktide.itemstore.types.DbTarget;

import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.itemstore.session.LinkedOperation;
import org.tasktide.itemstore.session.LinkedOperationMap;

import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;


/**
 * Interface for storing {@link Item}s into RocksDB/Sqlite
 * 
 * @author bkenna
 */
public interface ItemStore {
    
    
    /**
     * Allows a collection of {@link ItemStore} methods to be
     *  executed across an {@link ItemStore} map under one locked
     *  {@link LinkedOperation} whose lifecycle is owned
     *  ideally by the {@link AbstractItemStore} withLockedConnection
     * 
     * @param <T>
     * @param target
     * @param recipients
     * @param operations
     * 
     * @return <T> T
     * 
     * @throws {@link throws ItemStoreCheckedException}
     */
    public <T> T execute(
        DbTarget target,
        Map<String, ItemStore> recipients,
        LinkedOperationMap<T> operations
    ) throws ItemStoreCheckedException;
    
    
    /**
     * Allows a collection of {@link ItemStore} methods to be
     *  executed across {@link ItemStore} under one locked
     *  {@link LinkedOperation} whose lifecycle is owned
     *  ideally by the {@link AbstractItemStore} withLockedConnection
     * 
     * @param <T>
     * @param target
     * @param itemStore
     * @param operations
     * 
     * @return <T> T
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public <T> T execute(
        DbTarget target,
        ItemStore itemStore,
        LinkedOperation<T> operations
    ) throws ItemStoreCheckedException;
    
    
    /**
     * Allows a collection of {@link ItemStore} methods to be
     *  executed under the one lock whose lifecycle is owned
     *  ideally by the {@link AbstractItemStore} withLockedConnection
     * 
     * @param <T>
     * @param target
     * @param work
     * 
     * @return <T> T
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public <T> T execute(
        DbTarget target,
        BulkOperation<T> work
    ) throws ItemStoreCheckedException;
    
    
    /**
     * Fetch store name
     * 
     * @return String
     */
    String getStoreName();
    
    
    /**
     * Fetch file path for masterDB
     * 
     * @return String
     */
    String getMasterFilePath();
    
    
    /**
     * Fetch file path for cached DB
     * 
     * @return String
     */
    String getFilePath();
    
    
    /**
     * Get directory of ItemStore
     * 
     * @return String
     */
    String getDbDirectory();
    
    
    /**
     * Save an {@link Item} into cached DB
     * 
     * @param target
     * @param item
     * @throws {@link ItemStoreCheckedException} 
     */
    void saveItem(DbTarget target, Item item) throws ItemStoreCheckedException;
    
    
    /**
     * Save {@link Item} list into cached DB
     * 
     * @param target
     * @param items
     * @throws {@link ItemStoreCheckedException}
     */
    void saveItems(DbTarget target, List<Item> items) throws ItemStoreCheckedException;
    
    
    /**
     * Get all {@link Item}s
     * 
     * @param target
     * @return List-{@link Item}
     */
    List<Item> getAll(DbTarget target) throws ItemStoreCheckedException;
    
    
    /**
     * Get {@link Item} by Id
     * 
     * @param target
     * @param id
     * @return {@link Item}
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    Item getById(DbTarget target, String id) throws ItemStoreCheckedException;
    
    
    /**
     * Get {@link Item}s matching queried state
     * 
     * @param target
     * @param state
     * @return List-{@link Item}
     * @throws {@link ItemStoreCheckedException}
     */
    List<Item> getItemsByState(DbTarget target, String state) throws ItemStoreCheckedException;
    
    
    /**
     * Fetch the payload for an {@link Item} by an Id
     * 
     * @param target
     * @param id
     * @return String
     */
    String getPayloadById(DbTarget target, String id);
    
    
    /**
     * Drop {@link Item} from cache for {@link DbTarget}
     * 
     * @param target
     * @param item
     * @return boolean
     * @throws java.lang.Exception
     */
    boolean delete(DbTarget target, Item item) throws ItemStoreCheckedException;
    
    
    /**
     * Updates {@link Item} from cache for {@link DbTarget}
     * 
     * @param target
     * @param item
     * @return boolean
     * @throws java.lang.Exception
     */
    boolean update(DbTarget target, Item item) throws ItemStoreCheckedException;

    
    /**
     * Sync {@link ItemStore} cache to main DB
     * 
     * @param item
     * @throws {@link ItemStoreCheckedException}
     */
    void syncToMaster(Item item) throws ItemStoreCheckedException;
    
    
    /**
     * Sync list of {@link Item} to master
     * 
     * @param items
     * @throws {@link ItemStoreCheckedException}
     */
    void syncToMaster(List<Item> items) throws ItemStoreCheckedException;
    
    
    /**
     * Syncs active cache to master
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    void syncToMaster() throws ItemStoreCheckedException;
    
    
    /**
     * Copies master DB to a cached DB
     * 
     * @return boolean
     * @throws {@link ItemStoreCheckedException}
     */
    boolean cacheMaster() throws ItemStoreCheckedException;
    
    
    /**
     * Remove cached prototype
     * 
     * @return boolean 
     */
    boolean clearPrototype();
    
    
    /**
     * Close connection to target DB
     * 
     * @param target
     * @return boolean
     */
    boolean closeConn(DbTarget target);
    
    
    /**
     * Open connection to target database if closed
     * 
     * @param target
     * @return boolean
     */
    boolean openConn(DbTarget target);
    
    
    /**
     * Checks whether provided {@link DbTarget} is already opened
     * 
     * @param target
     * @return boolean
     */
    public boolean isOpen(DbTarget target);
    
    
    /**
     * Checks whether provided {@link DbTarget} is already closed
     * 
     * @param target
     * @return boolean
     */
    public boolean isClosed(DbTarget target);
    
    
    /**
     * Enforces concrete classes to implement
     *  opening connection against master DB
     * 
     * @return boolean
     */
    public abstract boolean openMaster();
    
    
    /**
     * Enforces concrete classes to implement
     *  opening connection against prototype DB
     * 
     * @return boolean
     */
    public abstract boolean openPrototoype();

    
    /**
     * Enforces concrete classes to implement
     *  closing connection against master DB
     * 
     * @return boolean
     */
    public abstract boolean closeMaster();
    
    
    /**
     * Enforces concrete classes to implement
     *  closing connection against prototype DB
     * 
     * @return boolean
     */
    public abstract boolean closePrototoype();
}