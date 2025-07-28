/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.itemstore;

import java.util.List;

/**
 * Interface for storing {@link Item}s into RocksDB/Sqlite
 * 
 * @author bkenna
 */
public interface ItemStore {
    
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
    
    
    /**
     * Drop {@link Item} from cache for {@link DbTarget}
     * 
     * @param target
     * @param item
     * @return boolean
     * @throws java.lang.Exception
     */
    boolean delete(DbTarget target, Item item) throws Exception ;
}
