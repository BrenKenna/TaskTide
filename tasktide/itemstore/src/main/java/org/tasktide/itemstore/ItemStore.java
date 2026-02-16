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

import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.itemstore.session.LinkedOperation;


/**
 * Interface for storing {@link Item}s into RocksDB/Sqlite
 * 
 * @author bkenna
 */
public interface ItemStore {
    
    
    /**
     * Allows a collection of {@link ItemStore} methods to be
     *  executed across {@link ItemStore} under one locked
     *  {@link LinkedOperation}
     * 
     * @param <T>
     * @param target
     * @param itemStore
     * @param operations
     * @return T
     */
    <T> T execute(DbTarget target, ItemStore itemStore, LinkedOperation<T> operations);
    
    
    /**
     * Allows a collection of {@link ItemStore} methods to be
     *  executed under the one lock
     * 
     * @param <T>
     * @param target
     * @param work
     * @return T
     */
    <T> T execute(DbTarget target, BulkOperation<T> work);
    
    
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
     * @throws Exception 
     */
    void saveItem(DbTarget target, Item item) throws Exception;
    
    
    /**
     * Save {@link Item} list into cached DB
     * 
     * @param target
     * @param items
     * @throws Exception 
     */
    void saveItems(DbTarget target, List<Item> items) throws Exception;
    
    
    /**
     * Get all {@link Item}s
     * 
     * @param target
     * @return List-{@link Item}
     */
    List<Item> getAll(DbTarget target);
    
    
    /**
     * Get {@link Item} by Id
     * 
     * @param target
     * @param id
     * @return {@link Item}
     * 
     * @throws Exception 
     */
    Item getById(DbTarget target, String id) throws Exception;
    
    
    /**
     * Get {@link Item}s matching queried state
     * 
     * @param target
     * @param state
     * @return List-{@link Item}
     * @throws Exception 
     */
    List<Item> getItemsByState(DbTarget target, String state) throws Exception;
    
    
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
    boolean delete(DbTarget target, Item item) throws Exception;
    
    
    /**
     * Updates {@link Item} from cache for {@link DbTarget}
     * 
     * @param target
     * @param item
     * @return boolean
     * @throws java.lang.Exception
     */
    boolean update(DbTarget target, Item item) throws Exception;

    
    /**
     * Sync {@link ItemStore} cache to main DB
     * 
     * @param item
     * @throws Exception 
     */
    void syncToMaster(Item item) throws Exception;
    
    
    /**
     * Sync list of {@link Item} to master
     * 
     * @param items
     * @throws Exception 
     */
    void syncToMaster(List<Item> items) throws Exception;
    
    
    /**
     * Syncs active cache to master
     * 
     * @throws Exception 
     */
    void syncToMaster() throws Exception;
    
    
    /**
     * Copies master DB to a cached DB
     * 
     * @return boolean
     * @throws Exception 
     */
    boolean cacheMaster() throws Exception;
    
    
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
}