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
     * @param item
     * @throws Exception 
     */
    void saveItem(Item item) throws Exception;
    
    
    /**
     * Save an {@link Item} into master DB
     * 
     * @param item
     * @throws Exception 
     */
    void saveItemToMaster(Item item) throws Exception;
    
    
    /**
     * Save an {@link Item} into master DB
     * 
     * @param item
     * @throws Exception 
     */
    void saveItemsToMaster(List<Item> item) throws Exception;
    
    
    /**
     * Get all {@link Item}s
     * 
     * @return List-{@link Item}
     */
    List<Item> getAll();
    
    
    /**
     * Get {@link Item} by Id
     * 
     * @param id
     * @return {@link Item}
     * 
     * @throws Exception 
     */
    Item getById(String id) throws Exception;
    
    
    /**
     * Get {@link Item} by Id from Master
     * 
     * @param id
     * @return {@link Item}
     * 
     * @throws Exception 
     */
    Item getByIdFromMaster(String id) throws Exception;
    
    
    /**
     * Get {@link Item}s matching queried state
     * 
     * @param state
     * @return List-{@link Item}
     * @throws Exception 
     */
    List<Item> getItemsByState(String state) throws Exception;
    
    
    /**
     * Get {@link Item}s matching queried state from Master
     * 
     * @param state
     * @return List-{@link Item}
     * @throws Exception 
     */
    List<Item> getItemsByStateFromMaster(String state) throws Exception;
    
    
    /**
     * Fetch the payload for an {@link Item} by an Id
     * 
     * @param id
     * @return String
     */
    String getPayloadById(String id);
    
    
    
    /**
     * Fetch payload from master
     * 
     * @param id
     * @return String
     */
    String getPayloadFromMaster(String id);
    
    
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
     * @return boolean
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
}
