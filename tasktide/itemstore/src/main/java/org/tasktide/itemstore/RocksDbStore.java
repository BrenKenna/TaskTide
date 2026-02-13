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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;

import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.itemstore.session.ItemStoreSession;


/**
 * Use {@link RocksDB} as storage backend
 * 
 * @author bkenna
 */
public class RocksDbStore extends AbstractItemStore {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(RocksDbStore.class);
    private RocksDB master, proto;
    private final Options options;
    private final ObjectMapper MAPPER = new ObjectMapper();
    
    
    /**
     * Connect to DB
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB
     */
    public RocksDbStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
        super(storeName, dbDirectory, masterDB, protoDB);
        RocksDB.loadLibrary();
        this.options = new Options().setCreateIfMissing(true);
    }
    
    
    /**
     * Performs {@link BulkOperation} over {@link DbTarget}
     *  under one {@link ItemStoreSession}
     * 
     * @param <T>
     * @param target
     * @param operations
     * @return T
     */
    @Override
    public synchronized <T> T execute(DbTarget target, BulkOperation<T> operations) {
        this.openConn(target);
        try {
            ItemStoreSession session;
            switch ( target ) {
                case MASTER -> {
                    session = new RocksDbSession(this.master);
                }
                default -> {
                    session = new RocksDbSession(this.proto);
                }
            }
            return operations.execute(session);
        }
        finally {
            this.closeConn(target);
        }
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
     * Fetch iterator from db
     * 
     * @param db
     * @return RocksIterator
     */
    public RocksIterator fetchIter(RocksDB db) {
        return db.newIterator();
    }
    
    
    /**
     * Put data in target DB
     * 
     * @param db
     * @param key
     * @param value
     */
    public void putItem(RocksDB db, byte[] key, byte[] value) {
        if ( db == null ) {
            throw new IllegalStateException("Error, the provided database is null");
        }
        
        if ( key == null || value == null ) {
            throw new IllegalStateException("Error, both the key and value must be non-null");
        }
        
        try {
            LOGGER.debug("Attempting insertion into ItemStrep: '{}'", db.getName());
            db.put(key, value);
        }
        catch (RocksDBException ex) {
            LOGGER.error("Unable to insert record into ItemStore: '{}'", db.getName());
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Fetch all records from either Master (true), or Cache (False)
     * 
     * @param target
     * @return 
     */
    @Override
    public synchronized List<Item> getAll(DbTarget target) {
        
        // Initialize variables
        List<Item> output = new ArrayList<>();
        RocksIterator iter;
        
        // Handle which DB to use
        this.openConn(target);
        switch (target) {
            case MASTER -> {
                LOGGER.debug("Fetching master iter '{}' from:\t'{}'", target, this.getFilePath());
                iter = this.fetchIter(this.master);
            }
            default -> {
                LOGGER.debug("Fetching prototype iter from:\t'{}'", this.getFilePath());
                iter = this.fetchIter(this.proto);
            }
        }
        
        // Fetch all records into output
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            Item active = fetchIteratorValue(iter);
            if ( active != null ) {
                output.add(active);
            }
        }
        
        // Close connection and release lock
        iter.close();
        this.closeConn(target);
        return output;
    }

    
    /**
     * Save item to cached RocksDB
     * 
     * @param target
     * @param item
     * @throws Exception 
     */
    @Override
    public synchronized void saveItem(DbTarget target, Item item) throws Exception {
        this.openConn(target);
        switch ( target ) {
            case PROTOTYPE -> {
                this.putItem(this.proto, item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
            }
            
            case MASTER -> {
                this.putItem(this.master, item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
            }
            
            case BOTH -> {
                this.putItem(this.master, item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
                this.putItem(this.proto, item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
            }
        }
        this.closeConn(target);
    }
    
    
    /**
     * Update by dropping and inserting
     * 
     * @param target
     * @param item
     * @return boolean
     */
    @Override
    public synchronized boolean update(DbTarget target, Item item) {
        try {
            this.saveItem(target, item);
            return true;
        }
        catch (Exception ex) {
            LOGGER.error("Unable to update Item, displaying stack trace\n", ex);
            ex.printStackTrace();
            return false;
        }
    }
    

    /**
     * Import {@link Item} list to cached DB
     * 
     * @param target
     * @param items
     * @throws Exception 
     */
    @Override
    public synchronized void saveItems(DbTarget target, List<Item> items) throws Exception {
        this.openConn(target);
        try (WriteBatch batch = new WriteBatch() ) {
            for ( Item item : items ) {
                byte[] key = item.getId().getBytes();
                byte[] val = MAPPER.writeValueAsBytes(item);
                batch.put(key, val);
            }
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.setSync(true);
            
            switch ( target ) {
                case PROTOTYPE -> {
                    LOGGER.debug("Attempting batch write into prototype");
                    proto.write(writeOptions, batch);
                }
                
                case MASTER -> {
                    LOGGER.debug("Attempting batch write into prototype");
                    master.write(writeOptions, batch);
                }
                
                case BOTH -> {
                    LOGGER.debug("Attempting batch write into master & prototype");
                    master.write(writeOptions, batch);
                    proto.write(writeOptions, batch);
                }
            }
        }
        this.closeConn(target);
    }
    
    
    /**
     * Fetch the {@link Item} with provided Id from cache
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    @Override
    public synchronized Item getById(DbTarget target, String id) throws Exception {
        this.openConn(target);
        byte[] data;
        switch ( target ) {
            case PROTOTYPE -> {
                LOGGER.debug("Fetching record matching Id from Prototype:\t'{}'", id);
                data = proto.get(id.getBytes());
            }
            default -> {
                LOGGER.debug("Fetching record matching Id from Master:\t'{}'", id);
                data = master.get(id.getBytes());
            }
        }
        this.closeConn(target);
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
    public synchronized List<Item> getItemsByState(DbTarget target, String state) throws Exception {
        this.openConn(target);
        List<Item> result = new ArrayList<>();
        RocksIterator iter;
        
        switch (target) {
            case PROTOTYPE -> {
                iter = proto.newIterator();
            }
            default -> {
                iter = master.newIterator();
            }
        }
        
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            Item item = MAPPER.readValue(iter.value(), Item.class);
            if (item.getState().equals(state)) {
                result.add(item);
            }
        }
        
        iter.close();
        this.closeConn(target);
        return result;
    }

    
    /**
     * Fetch payload for queried {@link Item} from cache
     * 
     * @param id
     * @return String
     */
    @Override
    public synchronized String getPayloadById(DbTarget target, String id) {
        
        // Initialize data
        String output = null;
        Item item;
        
        // Try fetch from cache
        this.openConn(target);
        try { 
            item = this.getById(target, id);
        }
        catch (Exception ex) {
            item = null;
        }
        
        // Fetch items payload
        if ( item != null ) {
            output = item.getPayload();
        }
        
        // Return result
        this.closeConn(target);
        return output;
    }
    
    
    /**
     * Delete {@link Item} from prototype
     * 
     * @param target
     * @param item
     * @return boolean
     */
    @Override
    public synchronized boolean delete(DbTarget target, Item item) throws Exception {
        this.openConn(target);
        boolean status;
        try {
            switch (target) {
                case PROTOTYPE -> {
                    LOGGER.debug("Deleting record matching Id from Prototype");
                    this.proto.delete(item.getId().getBytes());
                }
                case MASTER -> {
                    LOGGER.debug("Deleting record matching Id from Master");
                    this.master.delete(item.getId().getBytes());
                }
                
                case BOTH -> {
                    this.master.delete(item.getId().getBytes());
                    this.proto.delete(item.getId().getBytes());
                }
            }
            status = true;
        }
        catch ( Exception ex ) {
            LOGGER.error("Unable to delete record printing stack trace\n{}", ex);
            ex.printStackTrace();
            status = false;
        }
        this.closeConn(target);
        return status;
    }

    
    /**
     * Close master and cache connections
     * 
     * @param target
     * @return boolean
     */
    @Override
    public synchronized boolean closeConn(DbTarget target) {
        LOGGER.debug("Closing connection to:\t'{}'", target);
        switch (target) {
            case MASTER -> {
                this.releaseLock(true);
                if ( this.master == null ) {
                    return true;
                }
                if ( !this.master.isClosed() ) {
                    this.master.close();
                }
                return true;
            }
            case PROTOTYPE -> {
                if ( this.proto == null ) {
                    return true;
                }
                if ( !this.proto.isClosed() ) {
                    this.proto.close();
                }
                return true;
            }
            default -> {
                this.releaseLock(true);
                if ( this.master != null ) {
                    if ( !this.master.isClosed() ) {
                        this.master.close();
                    }
                }
                
                if ( this.proto != null ) {
                    if ( !this.proto.isClosed() ) {
                        this.proto.close();
                    }
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
    public synchronized boolean openConn(DbTarget target) {
        LOGGER.debug("Openning connection to:\t'{}'", target);
        switch (target) {
            case MASTER -> {
                try {
                    LOGGER.debug("Waiting for lock");
                    this.waitForLock();
                    if ( this.master == null ) {
                        LOGGER.debug("Lock acquired. Null master, openning connection");
                        this.master = RocksDB.open(this.options, this.getMasterFilePath());
                        LOGGER.debug("Master is now '{}'", this.master);
                        LOGGER.debug("Master state of open is '{}'", !this.master.isClosed());
                        return true;
                    }
                    if (master.isClosed()) {
                        LOGGER.debug("Lock acquired. Closed mnaster, openning connection");
                        this.master = RocksDB.open(this.options, this.getMasterFilePath());
                        LOGGER.debug("Master state of open is '{}'", !this.master.isClosed());
                        return true;
                    }
                    LOGGER.debug("Lock acquired. Master is neither closed ir null");
                    return false;
                }
                catch (RocksDBException | InterruptedException | IOException ex) {
                    LOGGER.error("Error openning connection to DB:\n", ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }
            }
            
            case PROTOTYPE -> {
                try {
                    if ( this.proto == null ) {
                        this.proto = RocksDB.open(this.options, this.getFilePath());
                        return true;
                    }
                    if (proto.isClosed()) {
                        this.proto = RocksDB.open(options, this.getFilePath());
                    }
                    return true;
                }
                catch (RocksDBException ex) {
                    LOGGER.error("Error openning connection to DB:\n", ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }
            }
            
            default -> {
                try {
                    this.waitForLock();
                    
                    if ( this.master == null || this.proto == null) {
                        if ( this.master == null ) {
                            this.master = RocksDB.open(this.options, this.getMasterFilePath());
                        }
                        if ( this.proto == null ) {
                            this.proto = RocksDB.open(this.options, this.getFilePath());
                        }
                        return true;
                    }
                    
                    if (master.isClosed()) {
                        this.master = RocksDB.open(options, this.getMasterFilePath());
                    }
                    if (proto.isClosed()) {
                        this.proto = RocksDB.open(options, this.getFilePath());
                    }
                    return true;
                }
                
                catch (RocksDBException | InterruptedException | IOException ex) {
                    LOGGER.error("Error openning connection to DB:\n", ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }
            }
        }    
    }
    
    
    /**
     * {@link ItemStoreSession} for SQLite {@link RocksDB}
     */
    private class RocksDbSession implements ItemStoreSession {
    
        // Attributes
        private final RocksDB conn;
        
        
        /**
         * Construct with {@link RocksDB} connection
         * 
         * @param conn 
         */
        RocksDbSession(RocksDB conn) {
            this.conn = conn;
        }
        
        @Override
        public boolean insert(Item item) {
            try {
                putItem(this.conn, item.getId().getBytes(), MAPPER.writeValueAsBytes(item));
                return true;
            }
            catch (JsonProcessingException ex) {
                return false;
            }
        }

        @Override
        public Item getById(String id) {
            
            // Initialize vars
            byte[] data;
            
            // Fetch data point
            try {
                data = this.conn.get(id.getBytes());
                return data == null ? null : MAPPER.readValue(data, Item.class);
            }
            
            catch ( IOException | RocksDBException ex ) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean delete(Item item) {
            try {
                this.conn.delete(item.getId().getBytes());
                return true;
            }
            
            catch ( RocksDBException ex ) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        public List<Item> getAll() {
            
            // Initialize variables
            List<Item> output = new ArrayList<>();
            RocksIterator iter;
            
            // Fetch & consume iterator
            iter = fetchIter(conn);
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                Item active = fetchIteratorValue(iter);
                if ( active != null ) {
                    output.add(active);
                }
            }
        
            // Close iterator and return results
            iter.close();
            return output;
        }

        @Override
        public List<Item> getItemsByState(String state) {
            
            // Initialize vars
            List<Item> output = new ArrayList<>();
            RocksIterator iter;
            
            // Fetch & consume iterator
            iter = this.conn.newIterator();
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                try {
                    Item item = MAPPER.readValue(iter.value(), Item.class);
                    if (item.getState().equals(state)) {
                        output.add(item);
                    }
                }
                catch ( IOException ex ) { }
            }

            // Close iterator & return results
            iter.close();
            return output;
        }

        @Override
        public String getPayloadById(String id) {
            Item result = this.getById(id);
            if ( result != null ) {
                return result.getPayload();
            }
            else {
                return null;
            }
        }
        
        @Override
        public boolean importItems(List<Item> items) {
            int counter = 0;
            for ( Item item : items ) {
                if ( this.insert(item) ) {
                    counter++;
                }
            }
            return counter == items.size();
        }
    }
}