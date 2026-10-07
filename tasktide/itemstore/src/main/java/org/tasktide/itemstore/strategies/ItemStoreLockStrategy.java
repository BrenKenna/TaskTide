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

import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.file.Path;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.tasktide.mutex.model.Mutex;
import org.tasktide.mutex.exceptions.MutexCheckedException;
import org.tasktide.mutex.exceptions.MutexUncheckedException;

import org.tasktide.mutex.utils.DefaultMutexPaths;
import org.tasktide.mutex.orchestrator.MutexOrchestrator;

import org.tasktide.itemstore.AbstractItemStore;
import org.tasktide.itemstore.ItemStore;
import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;
import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;

import org.tasktide.itemstore.operations.ItemStoreOperation;
import org.tasktide.itemstore.operations.ThrowableItemStoreOperation;
import org.tasktide.itemstore.utils.ItemStoreUtils;


/**
 * Class responsible for flexibly owning the {@link Mutex} lifecycle
 *  for an {@link ItemStore} as required. Using the same
 *  configured database, hence is tied but a child of
 *  {@link AbstractItemStore}. Providing an withLock
 *  method handle, and explicit lock/release methods
 *
 * @author Bren
 */
public class ItemStoreLockStrategy {

    // Attributes
    private final Logger LOGGER = LogManager.getLogger(ItemStoreLockStrategy.class);
    private final String storeName;
    private final Path dbDirectory, masterDB, masterLock;
    private final Random RANDOM;
    
    // Dynamically lock master once acquired
    private FileChannel fileChannel;
    private FileLock fileLock;

    
    /**
     * Targeted constructor using store name under provided
     *  directory
     * 
     * @param storeName
     * @param dbDirectory 
     */
    public ItemStoreLockStrategy(String storeName, Path dbDirectory) {
        this.storeName = storeName;
        this.dbDirectory = dbDirectory;
        
        // Infer master
        this.masterDB = this.dbDirectory.resolve("master");
        this.masterLock = this.masterDB.resolve("master.lock");

        // Active locks
        this.fileChannel = null;
        this.fileLock = null;
        this.RANDOM = new Random();

        // Configures mutex
        try {
            DefaultMutexPaths.config();
        }
        catch (MutexUncheckedException ex) {
            LOGGER.warn("MutexOrchestrator already configured");
        }
    }
    
    
    /**
     * {@link AbstractItemStore} compatible constructor
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     */
    public ItemStoreLockStrategy(
        String storeName,
        Path dbDirectory,
        Path masterDB
    ) {
        this.storeName = storeName;
        this.dbDirectory = dbDirectory;
        this.masterDB = masterDB;
        this.masterLock = this.masterDB.resolve("master.lock");
        this.RANDOM = new Random();
        
        this.fileChannel = null;
        this.fileLock = null;
        
        
        try {
            DefaultMutexPaths.config();
        }
        catch (MutexUncheckedException ex) {
            LOGGER.warn("MutexOrchestrator already configured");
        }
    }

    
    /**
     * Waits until master is locked
     * 
     * @throws InterruptedException 
     * @throws java.io.IOException 
     */
    public void waitForLock() throws InterruptedException, IOException {
        
        // Wait for lock until acquired
        boolean locked;
        locked = this.tryLock();
        while ( !locked ) {
            TimeUnit.MILLISECONDS.sleep(
                this.RANDOM.nextInt(200, 500)
            );
            locked = this.tryLock();
        }
    }
    
    
    /**
     * Internal {@link ItemStore} method to force database
     *  operations under standard lock release pipeline
     * 
     * @param <R>
     * @param operation
     * @param label
     * @return <R> of operation result
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public synchronized <R> R withLock (
        String label,
        ThrowableItemStoreOperation<R> operation
    ) throws ItemStoreCheckedException {
        
        // Try acquire lock on target,
        //  and perform operation
        try {
            this.waitForLock();
            return operation.execute();
        }
        
        // Otherwise log traceably throw error from mutex lib
        catch (InterruptedException | IOException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.error(ex);
            String msg = String.format(
                "Error ecountered obtaining lock for operation '%s':\t'%s'",
                label, ex.getMessage()
            );
            throw new ItemStoreCheckedException(msg, ex);
        }
        
        // Otherwise from the actual operation itself
        catch ( ItemStoreCheckedException ex ) {
            LOGGER.error(ex);
            String msg = String.format(
                "Error ecountered during operation '%s':\t'%s'",
                label, ex.getMessage()
            );
            throw new ItemStoreCheckedException(msg, ex);
        }
        
        // Release lock on resource
        finally {
            this.releaseLock(true);
        }
    }
    
    
    /**
     * Internal {@link ItemStore} method to force database
     *  operations under standard lock release pipeline
     * 
     * @param <R>
     * @param operation
     * @param label
     * @return <R> of operation result
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public synchronized <R> R withLockUnchecked (
        String label,
        ItemStoreOperation<R> operation
    ) throws ItemStoreUncheckedException {
        
        // Try acquire lock on target,
        //  and perform operation
        try {
            this.waitForLock();
            return operation.execute();
        }
        
        // Otherwise log traceably throw error from mutex lib
        catch (InterruptedException | IOException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.error(ex);
            String msg = String.format(
                "Error ecountered obtaining lock for operation '%s':\t'%s'",
                label, ex.getMessage()
            );
            throw new ItemStoreUncheckedException(msg, ex);
        }
        
        // Otherwise from the actual operation itself
        catch ( Exception ex ) {
            LOGGER.error(ex);
            String msg = String.format(
                "Error ecountered during operation '%s':\t'%s'",
                label, ex.getMessage()
            );
            throw new ItemStoreUncheckedException(msg, ex);
        }
        
        // Release lock on resource
        finally {
            this.releaseLock(true);
        }
    }
    
    
    /**
     * Non-synchronized {@link ItemStore} method to force database
     *  operations under standard lock release pipeline
     * 
     * @param <R>
     * @param operation
     * @param label
     * @return <R> of operation result
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public <R> R withLockNonSynced (
        String label,
        ThrowableItemStoreOperation<R> operation
    ) throws ItemStoreCheckedException {
        
        // Try acquire lock on target,
        //  and perform operation
        try {
            this.waitForLock();
            return operation.execute();
        }
        
        // Otherwise log traceably throw error from mutex lib
        catch (InterruptedException | IOException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.error(ex);
            String msg = String.format(
                "Error ecountered obtaining lock for operation '%s':\t'%s'",
                label, ex.getMessage()
            );
            throw new ItemStoreCheckedException(msg, ex);
        }
        
        // Otherwise from the actual operation itself
        catch ( ItemStoreCheckedException ex ) {
            LOGGER.error(ex);
            String msg = String.format(
                "Error ecountered during operation '%s':\t'%s'",
                label, ex.getMessage()
            );
            throw new ItemStoreCheckedException(msg, ex);
        }
        
        // Release lock on resource
        finally {
            this.releaseLock(true);
        }
    }
    
    
    /**
     * Release lock on master
     * 
     * @param releaseMutex
     * @return boolean
     */
    public boolean releaseLock(boolean releaseMutex) {
        try {
            
            // Clear lock
            if ( this.fileLock != null && this.fileLock.isValid() ) {
                fileLock.release();
            }
            
            // Close file channel
            if ( this.fileChannel != null && this.fileChannel.isOpen() ) {
                fileChannel.close();
            }
            
            // Release mutex
            if ( releaseMutex ) {
                this.releaseMutex();
            }
            return true;
        }
        
        catch (IOException ex) {
            this.releaseMutex();
            return false;
        }
    }
    
    
    /**
     * Release mutex
     * 
     * @return boolean
     */
    private boolean releaseMutex() {
        try {
            LOGGER.info("Releasing mutex");
            MutexOrchestrator.releaseLock();
            LOGGER.info("Released mutex");
            return true;
        }
        catch ( MutexCheckedException ex ) {
            LOGGER.warn(
                "Warning unable to release mutex, displaying error:\n",
                ex
            );
            return false;
        }
    }
    
    
    /**
     * Wait for mutex to be acquired
     * 
     * @return boolean
     */
    private boolean waitForMutex() {
    
        try {
            LOGGER.info("Acquiring mutex");
            MutexOrchestrator.tryAcquireUntilSuccess();
            LOGGER.info("Mutex acquired");
            return true;
        }
        catch ( Exception ex ) {
            LOGGER.warn(
                "Warning unable to acquire mutex, displaying error:\n'{}'",
                ex
            );
            return false;
        }
    }
    
    
    /**
     * Try process lock masterDB file after obtaining
     *  the mutex
     * 
     * @return boolean
     * @throws IOException 
     */
    private boolean tryLock() throws IOException {
        
        // Try acquire lock
        if ( !this.waitForMutex() ) {
            LOGGER.error("Unable to acquire mutex for DB lock");
            return false;
        }
        
        // Create masterDB lock file if non-existent
        if ( !this.makeMasterLockFile()) {
            LOGGER.error("Unable to acquire DB lock, releasing mutex");
            this.releaseMutex();
            return false;
        }
        
        // Try create a lock
        try {
            this.releaseLock(false); // Does not clear mutex
            this.fileChannel = new RandomAccessFile(
                this.masterLock.toFile(),
                "rw"
            ).getChannel();
            this.fileLock = fileChannel.tryLock();
            if ( fileLock != null ) {
                return true;
            }
            else {
                LOGGER.error("Unable to verify master lock, releasing mutex");
                this.releaseMutex();
                return false;
            }
        }
        
        // Lock creation failed
        catch (IOException ex) {
            this.releaseLock(true);
            throw ex;
        }
    }
    
    
    /**
     * Locks the configured master lock file
     * 
     * @return boolean
     */
    private boolean makeMasterLockFile() {
        return ItemStoreUtils.makeTargetLockFile(this.masterLock);
    }

    
    /**
     * Fetch configured store name
     * 
     * @return String
     */
    public String getStoreName() {
        return this.storeName;
    }

    
    /**
     * Fetch configured DB directory
     * 
     * @return Path
     */
    public Path getDbDirectory() {
        return this.dbDirectory;
    }

    
    /**
     * Fetch configured master DB
     *  directory
     * 
     * @return Path
     */
    public Path getMasterDB() {
        return this.masterDB;
    }

    
    /**
     * Fetch configured master lock
     *  directory
     * 
     * @return Path
     */
    public Path getMasterLock() {
        return this.masterLock;
    }

    
    /**
     * Fetch active file channel
     * 
     * @return FileChannel
     */
    public FileChannel getFileChannel() {
        return this.fileChannel;
    }

    
    /**
     * Fetch active OS file lock
     * 
     * @return FileLock
     */
    public FileLock getFileLock() {
        return this.fileLock;
    }
}