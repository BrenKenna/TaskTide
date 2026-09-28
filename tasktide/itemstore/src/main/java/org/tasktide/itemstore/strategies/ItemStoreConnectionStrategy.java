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
package org.tasktide.itemstore.strategies;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.tasktide.itemstore.ItemStore;

import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;
import org.tasktide.itemstore.operations.ItemStoreOperation;

import org.tasktide.itemstore.types.DbTarget;


/**
 * Strategy to hold evaluation logic around whether or not
 *  to allow the opening/closing of {@link ItemStore} to take
 *  that bloat out these classes using {@link ItemStoreThrowableOperation}
 *
 * @author Bren
 */
public class ItemStoreConnectionStrategy {

    // Logging
    private static final Logger LOGGER = LogManager.getLogger(ItemStoreConnectionStrategy.class);
    
    
    /**
     * Handle to evaluate opening connection
     *  against configured {@link ItemStore}
     * 
     * @param <R>
     * @param target
     * @param itemStore
     * @param operation
     * @return boolean
     * 
     * @throws {@link ItemStoreUncheckedException} 
     */
    public static synchronized <R> boolean openConnection(
        DbTarget target,
        ItemStore itemStore,
        ItemStoreOperation<R> operation
    ) throws ItemStoreUncheckedException {
        
        // Handles how to initilize connection
        switch (target) {
            
            // Operate on master
            case DbTarget.MASTER -> {
                LOGGER.info("Attempting to open connection against master ItemStore");
                if ( itemStore.isClosed(DbTarget.MASTER) ) {
                    operation.execute();
                    return true;
                }
                else {
                  return false;  
                }
            }
        
            // Operate on prototype
            case DbTarget.PROTOTYPE -> {
                LOGGER.info("Attempting to open connection against prototype ItemStore");
                if ( itemStore.isClosed(DbTarget.PROTOTYPE) ) {
                    operation.execute();
                    return true;
                }
                else {
                  return false;  
                }
            }
        
            // Must be either master prototpye
            default -> {
                LOGGER.info("Attempting to open connection against prototype ItemStore");
                if ( 
                   itemStore.isClosed(DbTarget.MASTER) &&
                   itemStore.isClosed(DbTarget.PROTOTYPE)
                ) {
                    operation.execute();
                    return true;
                }
                else {
                    return false;
                }
            }
        }
    }
    
    
    /**
     * Handle to evaluate opening connection
     *  against configured {@link ItemStore}
     * 
     * @param <R>
     * @param target
     * @param itemStore
     * @param operation
     * @return boolean
     * 
     * @throws {@link ItemStoreUncheckedException} 
     */
    public static synchronized <R> boolean closeConnection(
        DbTarget target,
        ItemStore itemStore,
        ItemStoreOperation<R> operation
    ) throws ItemStoreUncheckedException {
    
        // Handles how to initilize connection
        switch (target) {
            
            // Operate on master
            case DbTarget.MASTER -> {
                LOGGER.info("Attempting to close connection against master ItemStore");
                if ( itemStore.isOpen(DbTarget.MASTER) ) {
                    operation.execute();
                    return true;
                }
                else {
                  return false;  
                }
            }
        
            // Operate on prototype
            case DbTarget.PROTOTYPE -> {
                LOGGER.info("Attempting to close connection against prototype ItemStore");
                if ( itemStore.isOpen(DbTarget.PROTOTYPE) ) {
                    operation.execute();
                    return true;
                }
                else {
                  return false;  
                }
            }
        
            // Must be either master prototpye
            default -> {
                LOGGER.info("Attempting to close connection against prototype ItemStore");
                if ( 
                   itemStore.isOpen(DbTarget.MASTER) &&
                   itemStore.isOpen(DbTarget.PROTOTYPE)
                ) {
                    operation.execute();
                    return true;
                }
                else {
                    return false;
                }
            }
        }
    }
}