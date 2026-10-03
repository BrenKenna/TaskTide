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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;
import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;
import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.itemstore.session.ItemStoreSession;
import org.tasktide.itemstore.session.LinkedOperation;
import org.tasktide.itemstore.session.LinkedOperationMap;

import org.tasktide.itemstore.types.DbTarget;
import static org.tasktide.itemstore.types.DbTarget.*;
import org.tasktide.itemstore.utils.ItemStoreUtils;


/**
 * Use {@link RocksDB} as storage backend
 * 
 * @author bkenna
 */
public class RocksDbStore extends AbstractItemStore {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(RocksDbStore.class);
    protected RocksDB master, proto;
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
     * Connect to DB
     * 
     * @param storeName
     * @param dbDirectory
     */
    public RocksDbStore(String storeName, String dbDirectory) {
        super(storeName, dbDirectory);
        RocksDB.loadLibrary();
        this.options = new Options().setCreateIfMissing(true);
    }
    
    
    /**
     * Get {@link RocksDB} get connection
     * 
     * @param target
     * @return {@link RocksDB}
     */
    @Override
    protected <T> T getConnection(DbTarget target, Class<T> type) {
        switch ( target ) {
            case MASTER -> {
                return type.cast(this.master);
            }
            default -> {
                return type.cast(this.proto);
            }
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
            return this.MAPPER.readValue(value, Item.class);
        }
        catch (IOException ex) {
            LOGGER.error(ex);
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
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public void putItem(RocksDB db, byte[] key, byte[] value) throws ItemStoreCheckedException {
        if ( db == null ) {
            throw new IllegalStateException("Error, the provided database is null");
        }
        
        if ( key == null || value == null ) {
            throw new IllegalStateException("Error, both the key and value must be non-null");
        }
        
        try {
            db.put(key, value);
        }
        
        catch (RocksDBException ex) {
            LOGGER.error(
                "Unable to insert record into ItemStore:\t'{}'\n",
                db.getName(),
                ex
            );
            throw new ItemStoreCheckedException("Unable to insert record to RcoskDB-ItemStore", ex);
        }
    }
    
    
    /**
     * Fetch all records from either Master (true), or Cache (False)
     * 
     * @param target
     * @return List-{@link Item}
     * 
     * @throws {@link ItemStoreUncheckedException}
     */
    @Override
    public synchronized List<Item> getAll(DbTarget target) throws ItemStoreUncheckedException {
        
        // Initialize variables
        List<Item> output = new ArrayList<>();

        // Consume iterable under locked-connection lifecycle
        this.withLockedConnectionUnchecked(
            "Get All Records",
            target,
            () -> {
                RocksIterator iter;
                switch (target) {
                    case MASTER -> {
                        iter = this.fetchIter(this.master);
                    }
                    default -> {
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
                iter.close();
                return true;
        });

        // Return results
        return output;
    }

    
    /**
     * Save item to cached RocksDB, connection and lock
     *  lifecycles orchestrated by {@link AbstractItemStore}
     * 
     * @param target
     * @param item
     * 
     * @throws {@link ItemStoreCheckedException} 
     */
    @Override
    public void saveItem(DbTarget target, Item item) throws ItemStoreCheckedException {
        
        // Initialize vars
        byte[] key, value;
        
        // Serialize item
        key = item.getId().getBytes();
        value = ItemStoreUtils.serializeItemToByteArray(MAPPER, item);
        
        // Add record with AbstractItemStore
        this.withLockedConnection(
            "Save Item RocksDB",
            target,
            () -> {
                switch ( target ) {
                    case DbTarget.PROTOTYPE -> {
                        this.putItem(this.proto, key, value);
                        return true;
                    }
                    case DbTarget.MASTER -> {
                        this.putItem(this.master, key, value);
                        return true;
                    }
                    case DbTarget.BOTH -> {
                        this.putItem(this.master, key, value);
                        this.putItem(this.proto, key, value);
                        return true;
                    }
                }
                return false;
            }
        );
    }
    
    
    /**
     * Update by dropping and inserting
     * 
     * @param target
     * @param item
     * @return boolean
     */
    @Override
    public boolean update(DbTarget target, Item item) {
        try {
            LOGGER.debug("Updating record through save operations");
            this.saveItem(target, item);
            return true;
        }
        catch (ItemStoreCheckedException ex) {
            LOGGER.error("Unable to update Item, displaying stack trace\n", ex);
            return false;
        }
    }
    

    /**
     * Import {@link Item} list to cached DB
     * 
     * @param target
     * @param items
     * @throws {@link ItemStoreCheckedException} 
     */
    @Override
    public void saveItems(DbTarget target, List<Item> items) throws ItemStoreCheckedException {
        try (
            WriteBatch batch = new WriteBatch();
            WriteOptions writeOptions = new WriteOptions();
        ) {
            for ( Item item : items ) {
                byte[] key = item.getId().getBytes();
                byte[] val = ItemStoreUtils.serializeItemToByteArray(MAPPER, item);
                batch.put(key, val);
            }
            writeOptions.setSync(true);
            
            this.withLockedConnection(
                "Save record collection",
                target,
                () -> {
                    switch ( target ) {
                        case PROTOTYPE -> {
                            ItemStoreUtils.writeBatch(this.proto, batch, writeOptions);
                            return true;
                        }

                        case MASTER -> {
                            ItemStoreUtils.writeBatch(this.master, batch, writeOptions);
                            return true;
                        }
                        case BOTH -> {
                            ItemStoreUtils.writeBatch(this.master, batch, writeOptions);
                            ItemStoreUtils.writeBatch(this.proto, batch, writeOptions);
                            return true;
                        }
                    }
                    return false;
            });
        }
        
        catch (RocksDBException | ItemStoreCheckedException ex) {
            throw new ItemStoreCheckedException("Error batch writing records", ex);
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
    public Item getById(DbTarget target, String id) throws ItemStoreCheckedException {
        byte[] data;
        data = this.withLockedConnection(
            "Get By Id",
            target,
            () -> {
                switch ( target ) {
                    case PROTOTYPE -> {
                        return ItemStoreUtils.getId(this.proto, id);
                    }
                    default -> {
                        return ItemStoreUtils.getId(this.master, id);
                    }
                }
        });
        return data == null ? null : ItemStoreUtils.marshallItemFromByteArray(MAPPER, data);
    }

    
    /**
     * Fetch item by state from master
     * 
     * @param state
     * @return List-{@link Item}
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public List<Item> getItemsByState(
        DbTarget target,
        String state)
    throws ItemStoreCheckedException {
        List<Item> result = new ArrayList<>();
        
        this.withLockedConnection(
            "Get By ItemState",
            target,
            () -> {
                RocksIterator iter;
                switch (target) {
                    case PROTOTYPE -> {
                        iter = this.proto.newIterator();
                    }
                    default -> {
                        iter = this.master.newIterator();
                    }
                }

                for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                    Item item = ItemStoreUtils
                        .marshallItemFromByteArray(
                            this.MAPPER,
                            iter.value()
                    );
                    if (item.getState().equals(state)) {
                        result.add(item);
                    }
                }
                iter.close();
                return null;
        });
        return result;
    }

    
    /**
     * Fetch payload for queried {@link Item} from cache
     * 
     * @param id
     * @return String
     */
    @Override
    public String getPayloadById(DbTarget target, String id) {
        
        // Initialize data
        String output = null;
        Item item;
        
        // Try fetch from cache
        try { 
            item = this.getById(target, id);
        }
        catch (ItemStoreCheckedException ex) {
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
     * Delete {@link Item} from prototype
     * 
     * @param target
     * @param item
     * 
     * @return boolean
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public boolean delete(DbTarget target, Item item) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "Delete record",
            target,
            () -> {
                switch (target) {
                    case PROTOTYPE -> {
                        ItemStoreUtils.delete(this.proto, item);
                    }
                    case MASTER -> {
                        ItemStoreUtils.delete(this.master, item);
                    }
                    case BOTH -> {
                        ItemStoreUtils.delete(this.master, item);
                        ItemStoreUtils.delete(this.proto, item);
                    }
                }
                return true;
        });
    }

    
    /**
     * Checks if Master/Prototype are open
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean isOpen(DbTarget target) {
        if ( target == DbTarget.MASTER ) {
            if ( this.master != null ) {
                return !this.master.isClosed();
            }
            return false;
        }
    
        if ( target == DbTarget.PROTOTYPE ) {
            if ( this.proto != null ) {
                return !this.proto.isClosed();
            }
            return false;
        }
        return false;
    }
    
    
    /**
     * Checks if Master/Prototype are open
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean isClosed(DbTarget target) {
        if ( target == DbTarget.MASTER ) {
            if ( this.master == null ) {
                return true;
            }
            return this.master.isClosed();
        }
    
        if ( target == DbTarget.PROTOTYPE ) {
            if ( this.proto == null ) {
                return true;
            }
            return this.proto.isClosed();
        }
        return false;
    }
    
    
    /**
     * Open connection master connection
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean openMaster() {
        try {
            this.master = RocksDB.open(this.options, this.getMasterFilePath());
            return true;
        }
        catch (RocksDBException ex) {
            LOGGER.error("Error openning connection to Master DB:\n", ex.getMessage());
            throw new ItemStoreUncheckedException(ex.getMessage(), ex);
        }
    }
    
    
    /**
     * Closes connection against master DB
     * 
     * @return boolean
     */
    @Override
    public boolean closeMaster() {
        try {
            this.master.close();
            return true;
        }
        
        // This is documentaed as such
        catch (Exception ex) {
           LOGGER.error("Error openning connection to Prototype DB:\n", ex.getMessage());
            throw new ItemStoreUncheckedException(ex.getMessage(), ex);
        }
    }
    
    
    /**
     * Open connection prototype connection
     * 
     * @return boolean
     */
    @Override
    public boolean openPrototoype() {
        try {
            this.proto = RocksDB.open(this.options, this.getFilePath());
            return true;
        }
        catch (RocksDBException ex) {
            LOGGER.error("Error closing connection to Master DB:\n", ex.getMessage());
            throw new ItemStoreUncheckedException(ex.getMessage(), ex);
        }
    }

    
    /**
     * Closes connection against prototype DB
     * 
     * @return boolean
     */
    @Override
    public boolean closePrototoype() {
        try {
            this.proto.close();
            return true;
        }
        
        // This is documentaed as such
        catch (Exception ex) {
           LOGGER.error("Error closing connection to Prototype DB:\n", ex.getMessage());
            throw new ItemStoreUncheckedException(ex.getMessage(), ex);
        }
    }
    
    
    /**
     * Performs a set of operations over an
     *  {@link ItemStoreSession}
     * 
     * @param <T>
     * @param target
     * @param ops
     * 
     * @return T
     */
    @Override
    public <T> T execute(
        DbTarget target,
        BulkOperation<T> operationSet
    ) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "RocksDB Operations Set",
            target,
            () -> {
                ItemStoreSession session;
                switch (target) {
                    case PROTOTYPE -> {
                        session = new RocksDbSession(this.proto);
                    }
                    default -> {
                        session = new RocksDbSession(this.master);
                    }
                }
                return operationSet.execute(session);
        });
    }
    
    
    /**
     * Performs a set of {@link LinkedOperation}
     *  over active, and recipient {@link ItemStore}.
     *  Under the mutex of the calling {@link ItemStore},
     *   hence provided {@link ItemStore} referred to as
     *   a recipient of the stores mutex
     * 
     * @param <T>
     * @param target
     * @param recipientStore
     * @param linkedOperations
     * 
     * @return <T> of T
     * 
     * @throws ItemStoreCheckedException 
     */
    @Override
    public <T> T execute(
        DbTarget target,
        ItemStore recipientStore,
        LinkedOperation<T> linkedOperations
    ) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "Linked RocksDB Operation Set",
            target,
            () -> {
                
                // Configure sessions
                ItemStoreSession donor, recipient;
                try {
                    switch ( target ) {
                        case PROTOTYPE -> {
                            recipientStore.openConn(DbTarget.PROTOTYPE);
                            donor = new RocksDbSession(this.proto);
                            recipient = new RocksDbSession(
                                ( (AbstractItemStore) recipientStore).getConnection(DbTarget.PROTOTYPE, RocksDB.class)
                            );
                        }
                        default -> {
                            recipientStore.openConn(DbTarget.MASTER);
                            donor = new RocksDbSession(this.master);
                            recipient = new RocksDbSession(
                                ( (AbstractItemStore) recipientStore).getConnection(DbTarget.MASTER, RocksDB.class)
                            );
                        }
                    }

                    // Perform operation
                    return linkedOperations.execute(donor, recipient);
                }
                
                // Ensures recipient is closed
                finally {
                    recipientStore.closeConn(target);
                }
        });
    }
    
    
    /**
     * Donates active {@link ItemStore} mutex acquisition
     *  to provided recipients for provided operation
     *  set
     * 
     * @param <T>
     * @param target
     * @param recipients
     * @param operations
     * 
     * @return <T> of T
     * 
     * @throws {@link ItemStoreCheckedException} 
     */
    @Override
    public <T> T execute(
        DbTarget target,
        Map<String, ItemStore> recipients,
        LinkedOperationMap<T> operations
    ) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "Map of Linked RocksDB Operations",
            target,
            () -> {
                
                // Try perform operations
                try {
                    ItemStoreSession donor;
                    if ( target == PROTOTYPE ) {
                        donor = new RocksDbSession(this.proto);
                    }
                    else {
                        donor = new RocksDbSession(this.master);
                    }

                    // Handle recipient sessions
                    Map<String, ItemStoreSession> sessionMap = new HashMap<>();
                    for ( Entry<String, ItemStore> elm : recipients.entrySet() ) {
                        String key = elm.getKey();
                        RocksDbStore val = ( RocksDbStore ) elm.getValue();
                        val.openConn(target);
                        ItemStoreSession session = new RocksDbSession(val.getConnection(target, RocksDB.class));
                        sessionMap.put(key, session);
                    }

                    // Perform operation
                    return operations.execute(donor, sessionMap);
                }
                
                // Ensures recipient connections are closed
                finally {
                    for ( Entry<String, ItemStore> elm : recipients.entrySet() ) { 
                        elm.getValue().closeConn(target);
                    }
                }
        });
    }
    
    
    /**
     * {@link ItemStoreSession} for {@link RocksDB}
     * 
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

        
        /**
         * Insert {@link Item}
         * 
         * @param item
         * @return boolean
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public boolean insert(Item item) throws ItemStoreCheckedException {
            RocksDbStore.this.putItem(
                this.conn,
                item.getId().getBytes(),
                ItemStoreUtils.serializeItemToByteArray(RocksDbStore.this.MAPPER, item)
            );
            return true;
        }
        

        /**
         * Import record set
         * 
         * @param items
         * 
         * @return boolean
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public boolean importItems(List<Item> items) throws ItemStoreCheckedException {
            int counter = 0;
            for ( Item elm : items ) {
                if ( this.insert(elm) ) {
                    counter++;
                }
            }
            return counter == items.size();
        }

        
        /**
         * Fetch {@link Item} for Id
         * 
         * @param id
         * 
         * @return {@link Item}
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public Item getById(String id) throws ItemStoreCheckedException {
            byte[] data;
            try {
                data = this.conn.get(id.getBytes());
            }
            catch ( RocksDBException ex ) {
                RocksDbStore.this.LOGGER.error(ex.getMessage(), ex);
                throw new ItemStoreCheckedException(ex.getMessage(), ex);
            }
            
            return data == null 
                ? null :
                ItemStoreUtils.marshallItemFromByteArray(
                    RocksDbStore.this.MAPPER,
                    data
            );
        }

        
        /**
         * Drop provided record from {@link ItemStore}
         * 
         * @param item
         * 
         * @return boolean
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public boolean delete(Item item) throws ItemStoreCheckedException {
            try {
                this.conn.delete(item.getId().getBytes());
                return true;
            }
            catch ( RocksDBException ex ) {
                RocksDbStore.this.LOGGER.error(ex.getMessage(), ex);
                throw new ItemStoreCheckedException(ex.getMessage(), ex);
            }
        }

        
        /**
         * Fetch all records from {@link ItemStore}
         * 
         * @return List-{@link Item}
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public List<Item> getAll() throws ItemStoreCheckedException {
            
            // Initialize variables
            List<Item> output = new ArrayList<>();
            RocksIterator iter;
            
            // Fetch & consume iterator
            iter = RocksDbStore.this.fetchIter(this.conn);
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                Item active = RocksDbStore.this.fetchIteratorValue(iter);
                if ( active != null ) {
                    output.add(active);
                }
            }
        
            // Close iterator and return results
            iter.close();
            return output;
        }

        
        /**
         * Fetch {@link Item} via collection state
         * 
         * @param state
         * 
         * @return List-{@link Item}
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public List<Item> getItemsByState(String state) throws ItemStoreCheckedException {
            
            // Initialize variables
            List<Item> output = new ArrayList<>();
            RocksIterator iter;
            
            // Fetch & consume iterator
            iter = RocksDbStore.this.fetchIter(this.conn);
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                Item active = RocksDbStore.this.fetchIteratorValue(iter);
                if ( active.getState().equals(state)) {
                    output.add(active);
                }
            }
        
            // Close iterator and return results
            iter.close();
            return output;
        }

        
        /**
         * Fetch payload for {@link Item} by its Id
         * 
         * @param id
         * 
         * @return String
         * 
         * @throws {@link ItemStoreCheckedException} 
         */
        @Override
        public String getPayloadById(String id) throws ItemStoreCheckedException {
            Item result = this.getById(id);
            if ( result != null ) {
                return result.getPayload();
            }
            else {
                return null;
            }
        }
    }
}