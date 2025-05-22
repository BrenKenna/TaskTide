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


/**
 * Use {@link RocksDB} as storage backend
 * 
 * @author bkenna
 */
public class RocksDBStore extends AbstractItemStore {
    
    // Attributes
    private final RocksDB master, proto;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    
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
        this.master = RocksDB.open(options, this.masterDB);
        this.proto = RocksDB.open(options, this.protoDB);
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
            return mapper.readValue(value, Item.class);
        } catch (IOException ex) {
            ex.printStackTrace();
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
            master.put(item.getId().getBytes(), mapper.writeValueAsBytes(item));
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
                byte[] val = mapper.writeValueAsBytes(item);
                batch.put(key, val);
            }
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.setSync(true);
            master.write(writeOptions, batch);
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
            proto.put(item.getId().getBytes(), mapper.writeValueAsBytes(item));
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
        for ( Item item : items ) {
            saveItem(item);
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
        return data == null ? null : mapper.readValue(data, Item.class);
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
        return data == null ? null : mapper.readValue(data, Item.class);
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
                Item item = mapper.readValue(iter.value(), Item.class);
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
                Item item = mapper.readValue(iter.value(), Item.class);
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
     */
    @Override
    public void closeConn(int flag) {
        switch (flag) {
            case 0 -> {
                this.master.close();
                this.proto.close();
            }
            case 1 -> {
                this.proto.close();
                //TimeUnit.MILLISECONDS.sleep(500);
            }
            case 2 -> {
                this.master.close();
                //TimeUnit.MILLISECONDS.sleep(500);
            }
            default -> {}
        }
    }
}
