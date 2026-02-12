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
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Comparator;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.mutex.exceptions.MutexUncheckedException;

import org.tasktide.mutex.orchestrator.MutexOrchestrator;
import org.tasktide.mutex.utils.DefaultMutexPaths;


/**
 * Abstract ItemStore to implement getting ItemStore attributes,  
 *  handling the locking/releasing of masterDB for updates, and
 *  caching/clearing the master prototype to another file.
 * 
 * @author bkenna
 */
public abstract class AbstractItemStore implements ItemStore {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(AbstractItemStore.class);
    private final String storeName;
    private final Path dbDirectory, masterDB, protoDB, masterLock;
    private FileChannel fileChannel;
    private FileLock fileLock;
    private final Random RANDOM;
    
    
    /**
     * Construct with all attributes. Throws IllegalArgument
     *  RunTime exception if not writable. 
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     */
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
        this.masterLock = this.dbDirectory.resolve(masterDB + ".lock");
        this.fileChannel = null;
        this.fileLock = null;
        this.RANDOM = new Random();
        
        if ( !this.verifyDirectory() ) {
            String msg = String.format("Error, cannot write to the configured path:\t%s", this.masterDB);
            throw new IllegalArgumentException(msg);
        }
        
        try {
            DefaultMutexPaths.config();
        }
        catch (MutexUncheckedException ex) {
            LOGGER.warn("Mutex already configured");
        }
    }
    
    
    /**
     * Allows a collection of {@link ItemStore} methods to be
     *  executed under the one lock
     * 
     * @param <T>
     * @param target
     * @param work
     * @return T
     */
    public abstract <T> T execute(DbTarget target, BulkOperation<T> work);

    
    /**
     * Wait for mutex to be acquired
     * 
     * @return boolean
     */
    protected boolean waitForMutex() {
    
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
     * Release mutex
     * 
     * @return boolean
     */
    protected boolean releaseMutex() {
        try {
            LOGGER.info("Releasing mutex");
            MutexOrchestrator.releaseLock();
            LOGGER.info("Released mutex");
            return true;
        }
        catch ( Exception ex ) {
            LOGGER.warn(
                "Warning unable to release mutex, displaying error:\n",
                ex
            );
            return false;
        }
    }
    
    
    /**
     * Checks whether configured directory is writable
     * 
     * @return boolean
     */
    public boolean verifyDirectory() {
        try {

            // Creates directory if not exists
            if (!Files.exists(this.dbDirectory)) {
                Files.createDirectories(this.dbDirectory);
            }

            // Returns whether directory is writable
            return Files.isDirectory(this.dbDirectory) && Files.isWritable(this.dbDirectory);
        }
        
        // Return false if directory is not usable
        catch (IOException | SecurityException e) {
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

    
    /**
     * Try process lock masterDB file
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
            LOGGER.error("Unable to acquire DB lock");
            return false;
        }
        
        // Try create a lock
        try {
            releaseLock();
            this.fileChannel = new RandomAccessFile(this.masterLock.toFile(), "rw").getChannel();
            this.fileLock = fileChannel.tryLock();
            return fileLock != null;
        }
        
        // Lock creation failed
        catch (IOException ex) { throw ex;}
    }

    
    /**
     * Creates masterDB lock file
     * 
     * @return boolean
     */
    private boolean makeMasterLockFile() {
        
        // Create masterDB lock file
        try {
            Files.createFile(masterLock);
            return true;
        }
        
        // Already exists
        catch (FileAlreadyExistsException e) {
            return true;
        }
        
        // Creation failed for another reason
        catch (IOException e) {
            return false;
        }
    }
    
    
    /**
     * Waits until master is locked
     * 
     * @throws InterruptedException 
     * @throws java.io.IOException 
     */
    protected void waitForLock() throws InterruptedException, IOException {
        
        // Initialize variables
        boolean locked;
        
        // Try locking until locked
        try {
            
            // Fetch file lock
            locked = this.tryLock();
            
            // Enter loop if not locked
            while ( !locked ) {
                
                // Wait and try again
                TimeUnit.MILLISECONDS.sleep( this.RANDOM.nextInt(200, 500) );
                locked = this.tryLock();
            }
        }
        catch (IOException ex) {throw ex;}
    }
    
    
    /**
     * Release lock on master
     * 
     * @return boolean
     */
    protected boolean releaseLock() {
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
            this.releaseMutex();
            return true;
        }
        
        catch (IOException ex) {
            this.releaseMutex();
            return false;
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
            this.saveItem(DbTarget.MASTER, item);
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
            this.saveItems(DbTarget.MASTER, items);
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
            List<Item> data = this.getAll(DbTarget.MASTER);
            this.saveItems(DbTarget.MASTER, data);
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
            this.deleteRecursively(this.protoDB);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    
    /**
     * Recursively delete all contents of folder and it
     * 
     * @param path
     * @throws IOException 
     */
    private void deleteRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(Comparator.reverseOrder()) // delete children before parents
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete: " + p, e);
                    }
                });
        }
    }
}