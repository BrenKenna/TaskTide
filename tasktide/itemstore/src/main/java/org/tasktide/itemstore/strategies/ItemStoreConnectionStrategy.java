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
import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;
import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;

import org.tasktide.itemstore.operations.ItemStoreOperation;
import org.tasktide.itemstore.operations.ThrowableItemStoreOperation;

import org.tasktide.itemstore.types.DbTarget;


/**
 * Strategy to hold evaluation logic around whether or not
 *  to allow the opening/closing of {@link ItemStore} to take
 *  that bloat out these classes using {@link ThrowableItemStoreOperation}
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
     * @param target
     * @param itemStore
     * @return boolean
     * 
     * @throws {@link ItemStoreUncheckedException} 
     */
    public static synchronized boolean openConnection(
        DbTarget target,
        ItemStore itemStore
    ) throws ItemStoreUncheckedException {
        
        // Handles how to initilize connection
        switch (target) {
            
            // Operate on master
            case MASTER -> {
                LOGGER.info("Attempting to open connection against master ItemStore");
                if ( itemStore.isClosed(DbTarget.MASTER) ) {
                    return itemStore.openMaster();
                }
                else {
                  return false;  
                }
            }
        
            // Operate on prototype
            case PROTOTYPE -> {
                LOGGER.info("Attempting to open connection against prototype ItemStore");
                if ( itemStore.isClosed(DbTarget.PROTOTYPE) ) {
                    return itemStore.openPrototoype();
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
                    return
                        itemStore.openMaster() &
                    itemStore.openPrototoype();
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
     * @param target
     * @param itemStore
     * @return boolean
     * 
     * @throws {@link ItemStoreUncheckedException} 
     */
    public static synchronized boolean closeConnection(
        DbTarget target,
        ItemStore itemStore
    ) throws ItemStoreUncheckedException {
    
        // Handles how to initilize connection
        switch (target) {
            
            // Operate on master
            case MASTER -> {
                LOGGER.info("Attempting to close connection against master ItemStore");
                if ( itemStore.isOpen(DbTarget.MASTER) ) {
                    return itemStore.closeMaster();
                }
                else {
                  return false;  
                }
            }
        
            // Operate on prototype
            case PROTOTYPE -> {
                LOGGER.info("Attempting to close connection against prototype ItemStore");
                if ( itemStore.isOpen(DbTarget.PROTOTYPE) ) {
                    return itemStore.closePrototoype();
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
                    return
                        itemStore.closeMaster() &
                        itemStore.closePrototoype();
                }
                else {
                    return false;
                }
            }
        }
    }
    
    
    /**
     * Encase a {@link ThrowableItemStoreOperation} around
     *  standardized connection handling
     * 
     * @param <R>
     * @param target
     * @param itemStore
     * @param operation
     * 
     * @return R
     * 
     * @throws {@link ItemStoreCheckedException} 
     */
    public static synchronized <R> R connectionOperation (
        DbTarget target,
        ItemStore itemStore,
        ThrowableItemStoreOperation<R> operation
    ) throws ItemStoreCheckedException {
    
        // Handles how to initilize connection
        switch (target) {
            
            // Operate on master
            case MASTER -> {
                LOGGER.info("Attempting operation against Master ItemStore");
                if ( itemStore.isClosed(DbTarget.MASTER) ) {
                    itemStore.openMaster();
                    try {
                        return operation.execute();
                    }
                    finally {
                        itemStore.closeMaster();
                    }
                }
                else {
                    return null; 
                }
            }
        
            // Operate on prototype
            case PROTOTYPE -> {
                LOGGER.info("Attempting operation against Prototype ItemStore");
                if ( itemStore.isClosed(DbTarget.PROTOTYPE) ) {
                    itemStore.openPrototoype();
                    try {
                        return operation.execute();
                    }
                    finally {
                        itemStore.closePrototoype();
                    }
                }
                else {
                    return null;
                }
            }
        
            // Must be either master prototpye
            default -> {
                LOGGER.info("Attempting operation against Master & Prototype ItemStore");
                if ( 
                   itemStore.isClosed(DbTarget.MASTER) &&
                   itemStore.isClosed(DbTarget.PROTOTYPE)
                ) {
                    itemStore.openMaster();
                    itemStore.openPrototoype();
                    
                    try {
                        return operation.execute();
                    }
                    finally {
                        itemStore.closeMaster();
                        itemStore.closePrototoype();
                    }
                }
                else {
                    return null;
                }
            }
        }
    }
    
    
    /**
     * Encase a {@link ItemStoreOperation} around
     *  standardized connection handling
     * 
     * @param <R>
     * @param target
     * @param itemStore
     * @param operation
     * 
     * @return R
     * 
     * @throws {@link ItemStoreUncheckedException} 
     */
    public static synchronized <R> R connectionOperationUnchecked (
        DbTarget target,
        ItemStore itemStore,
        ItemStoreOperation<R> operation
    ) throws ItemStoreUncheckedException {
    
        // Handles how to initilize connection
        switch (target) {
            
            // Operate on master
            case MASTER -> {
                LOGGER.info("Attempting operation against Master ItemStore");
                if ( itemStore.isClosed(DbTarget.MASTER) ) {
                    itemStore.openMaster();
                    try {
                        return operation.execute();
                    }
                    finally {
                        itemStore.closeMaster();
                    }
                }
                else {
                    return null; 
                }
            }
        
            // Operate on prototype
            case PROTOTYPE -> {
                LOGGER.info("Attempting operation against Prototype ItemStore");
                if ( itemStore.isClosed(DbTarget.PROTOTYPE) ) {
                    itemStore.openPrototoype();
                    try {
                        return operation.execute();
                    }
                    finally {
                        itemStore.closePrototoype();
                    }
                }
                else {
                    return null;
                }
            }
        
            // Must be either master prototpye
            default -> {
                LOGGER.info("Attempting operation against Master & Prototype ItemStore");
                if ( 
                   itemStore.isClosed(DbTarget.MASTER) &&
                   itemStore.isClosed(DbTarget.PROTOTYPE)
                ) {
                    itemStore.openMaster();
                    itemStore.openPrototoype();
                    
                    try {
                        return operation.execute();
                    }
                    finally {
                        itemStore.closeMaster();
                        itemStore.closePrototoype();
                    }
                }
                else {
                    return null;
                }
            }
        }
    }
}