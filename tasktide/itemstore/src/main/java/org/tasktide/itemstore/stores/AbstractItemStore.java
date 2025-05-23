/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.itemstore.stores;

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
import java.util.UUID;

import org.tasktide.itemstore.Item;
import org.tasktide.itemstore.ItemStore;


/**
 * Abstract ItemStore to implement getting ItemStore attributes,  
 *  handling the locking/releasing of masterDB for updates, and
 *  caching/clearing the master prototype to another file.
 * 
 * @author bkenna
 */
public abstract class AbstractItemStore implements ItemStore {
    
    // Attributes
    private final String storeName;
    private final String dbDirectory;
    private final String masterDB;
    private final String protoDB;
    private final Path masterLock;
    private FileChannel fileChannel;
    private FileLock fileLock;
    private final Random RANDOM;
    
    
    /**
     * Construct with all attributes
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     */
    public AbstractItemStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
        this.storeName = storeName;
        this.dbDirectory = dbDirectory;
        this.masterDB = masterDB;
        this.protoDB = protoDB;
        this.masterLock = Paths.get(this.masterDB + ".lock");
        this.fileChannel = null;
        this.fileLock = null;
        this.RANDOM = new Random();
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
        return this.masterDB;
    }

    
    /**
     * Return file path prototype DB
     * 
     * @return String
     */
    @Override
    public String getFilePath() {
        return this.protoDB;
    }

    
    /**
     * Return directory of where DB is stored
     * 
     * @return String
     */
    @Override
    public String getDbDirectory() {
        return this.dbDirectory;
    }

    
    /**
     * Try process lock masterDB file
     * 
     * @return boolean
     * @throws IOException 
     */
    private boolean tryLock() throws IOException {
        
        // Create masterDB lock file if non-existent
        if ( !this.makeMasterLockFile()) {return false;}
        
        // Try create a lock
        try {
            releaseLock();
            this.fileChannel = new RandomAccessFile(this.masterLock.toFile(), "rw").getChannel();
            this.fileLock = fileChannel.tryLock();
            return fileLock != null;
        }
        
        // Lock creation failed
        catch (IOException e) { throw e;}
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
            return true;
        } catch (IOException ex) {
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
            saveItemToMaster(item);
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
            saveItemsToMaster(items);
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
            saveItemsToMaster(getAll(false));
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
            Files.copy(Paths.get(this.masterDB), Paths.get(this.protoDB), StandardCopyOption.REPLACE_EXISTING);
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
            this.deleteRecursively( Paths.get(this.protoDB) );
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