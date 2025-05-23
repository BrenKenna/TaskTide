/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.itemstore.stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

import org.tasktide.itemstore.Item;
import static org.tasktide.itemstore.stores.DbTarget.MASTER;
import static org.tasktide.itemstore.stores.DbTarget.PROTOTYPE;


/**
 * Use {@link RocksDB} as storage backend
 * 
 * @author bkenna
 */
public class RocksDBStore extends AbstractItemStore {
    
    // Attributes
    private RocksDB master, proto;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    
    /**
     * Connect to DB
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB
     * @throws RocksDBException 
     */
    public RocksDBStore(String storeName, String dbDirectory, String masterDB, String protoDB) throws RocksDBException {
        super(storeName, dbDirectory, masterDB, protoDB);
        RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(true);
        this.master = RocksDB.open(options, masterDB);
        this.proto = RocksDB.open(options, protoDB);
    }

    
    /**
     * Fetch active value from {@link RocksIterator} as an {@link Item}
     * 
     * @param iter
     * @return {@link Item}
     */
    public Item fetchIteratorValue(RocksIterator iter) {
        try {
            byte[] value = iter.value();
            return MAPPER.readValue(value, Item.class);
        } catch (IOException ex) {
            return null;
        }
    }
    
    
    /**
     * Fetch all records from either Master (true), or Cache (False)
     * 
     * @param flag
     * @return 
     */
    @Override
    public List<Item> getAll(boolean flag) {
        
        // Initialize variables
        List<Item> output = new ArrayList<>();
        RocksIterator iter;
        
        // Handle which DB to use
        if ( flag ) {
            iter = master.newIterator();
        }
        else {
            iter = proto.newIterator();
        }
        
        // Fetch all records into output
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            Item active = fetchIteratorValue(iter);
            if ( active != null ) {
                output.add(active);
            }
        }
        
        // Return output
        return output;
    }
    
    
    /**
     * Save {@link Item} to master RocksDB
     * 
     * @param item
     * @throws JsonProcessingException
     * @throws RocksDBException 
     * @throws java.lang.InterruptedException 
     */
    @Override
    public void saveItemToMaster(Item item) throws JsonProcessingException, RocksDBException, InterruptedException, IOException {
        this.waitForLock();
        try {
            master.put(item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
            proto.put(item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
        }
        finally {
            this.releaseLock();
        }
    }
    
    
    /**
     * Save {@link Item} list to master RocksDB
     * 
     * @param items
     * @throws JsonProcessingException
     * @throws RocksDBException 
     * @throws java.lang.InterruptedException 
     */
    @Override
    public void saveItemsToMaster(List<Item> items) throws JsonProcessingException, RocksDBException, InterruptedException, IOException {
        this.waitForLock();
        try (WriteBatch batch = new WriteBatch() ) {
            for ( Item item : items ) {
                byte[] key = item.getId().getBytes();
                byte[] val = MAPPER.writeValueAsBytes(item);
                batch.put(key, val);
            }
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.setSync(true);
            master.write(writeOptions, batch);
            proto.write(writeOptions, batch);
        }
        
        finally {
            this.releaseLock();
        }
    }

    
    /**
     * Save item to cached RocksDB
     * 
     * @param item
     * @throws Exception 
     */
    @Override
    public void saveItem(Item item) throws Exception {
        try {
            proto.put(item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
        }
        catch ( JsonProcessingException | RocksDBException ex ) {}
    }
    

    /**
     * Import {@link Item} list to cached DB
     * 
     * @param items
     * @throws Exception 
     */
    @Override
    public void saveItems(List<Item> items) throws Exception {
        try (WriteBatch batch = new WriteBatch() ) {
            for ( Item item : items ) {
                byte[] key = item.getId().getBytes();
                byte[] val = MAPPER.writeValueAsBytes(item);
                batch.put(key, val);
            }
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.setSync(true);
            proto.write(writeOptions, batch);
        }
    }
    
    
    /**
     * Fetch the {@link Item} with provided Id from cache
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    @Override
    public Item getById(String id) throws Exception {
        byte[] data = proto.get(id.getBytes());
        return data == null ? null : MAPPER.readValue(data, Item.class);
    }

    
    /**
     * Fetch the {@link Item} with provided Id from master
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    @Override
    public Item getByIdFromMaster(String id) throws Exception {
        byte[] data = master.get(id.getBytes());
        return data == null ? null : MAPPER.readValue(data, Item.class);
    }

    
    /**
     * Fetch item by state from master
     * 
     * @param state
     * @return List-{@link Item}
     * @throws Exception 
     */
    @Override
    public List<Item> getItemsByState(String state) throws Exception {
        List<Item> result = new ArrayList<>();
        try (RocksIterator iter = proto.newIterator()) {
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                Item item = MAPPER.readValue(iter.value(), Item.class);
                if (item.getState().equals(state)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    
    /**
     * Fetch item by state from master
     * 
     * @param state
     * @return List-{@link Item}
     * @throws Exception 
     */
    @Override
    public List<Item> getItemsByStateFromMaster(String state) throws Exception {
        List<Item> result = new ArrayList<>();
        try (RocksIterator iter = master.newIterator()) {
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                Item item = MAPPER.readValue(iter.value(), Item.class);
                if (item.getState().equals(state)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    
    /**
     * Fetch payload for queried {@link Item} from cache
     * 
     * @param id
     * @return String
     */
    @Override
    public String getPayloadById(String id) {
        
        // Initialize data
        String output = null;
        Item item;
        
        // Try fetch from cache
        try { 
            item = this.getById(id);
        }
        catch (Exception ex) {
            item = null;
        }
        
        // Fetch items payload
        if ( item != null ) {
            output = item.getPayload();
        }
        
        // Return result
        return output;
    }

    
    /**
     * Fetch payload for queried {@link Item} from master
     * 
     * @param id
     * @return String 
     */
    @Override
    public String getPayloadFromMaster(String id) {
        
        // Initialize data
        String output = null;
        Item item;
        
        // Try fetch from cache
        try { 
            item = this.getByIdFromMaster(id);
        }
        catch (Exception ex) {
            item = null;
        }
        
        // Fetch items payload
        if ( item != null ) {
            output = item.getPayload();
        }
        
        // Return result
        return output;
    }

    
    /**
     * Close master & cache connections
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean closeConn(DbTarget target) {
        switch (target) {
            case MASTER -> {
                this.releaseLock();
                if ( !this.master.isClosed() ) {
                    this.master.close();
                }
                return true;
            }
            case PROTOTYPE -> {
                if ( !this.proto.isClosed() ) {
                    this.proto.close();
                }
                return true;
            }
            default -> {
                this.releaseLock();
                if ( !this.master.isClosed() ) {
                    this.master.close();
                }
                if ( !this.proto.isClosed() ) {
                    this.proto.close();
                }
                return true;
            }
        }
    }
    
    
    /**
     * Open connection to target database if closed
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean openConn(DbTarget target) {
        Options options = new Options().setCreateIfMissing(true);
        switch (target) {
            case MASTER -> {
                try {
                    if (master.isClosed()) {
                        this.master = RocksDB.open(options, this.getMasterFilePath());
                    }
                    return true;
                }
                catch (RocksDBException ex) {
                    return false;
                }
            }
            
            case PROTOTYPE -> {
                try {
                    if (proto.isClosed()) {
                        this.proto = RocksDB.open(options, this.getFilePath());
                    }
                    return true;
                }
                catch (RocksDBException ex) {
                    return false;
                }
            }
            
            default -> {
                try {
                    if (master.isClosed()) {
                        this.master = RocksDB.open(options, this.getMasterFilePath());
                    }
                    if (proto.isClosed()) {
                        this.proto = RocksDB.open(options, this.getFilePath());
                    }
                    return true;
                }
                
                catch (RocksDBException ex) {
                    return false;
                }
            }
        }    
    }

    
    /**
     * Delete {@link Item} from prototype
     * 
     * @param item
     * @return boolean
     */
    @Override
    public boolean delete(Item item) throws Exception {
        try {
            this.proto.delete(item.getId().getBytes());
            return true;
        }
        catch ( RocksDBException ex ) {
            return false;
        }
    }

    
    /**
     * Delete {@link Item} from store
     * 
     * @param item
     * @return boolean
     * @throws Exception 
     */
    @Override
    public boolean deleteFromMaster(Item item) throws Exception {
        this.waitForLock();
        try {
            this.delete(item);
            this.master.delete(item.getId().getBytes());
            return true;
        }
        finally {
            this.releaseLock();
        }
    }
}
