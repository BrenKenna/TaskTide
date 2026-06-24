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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.tasktide.itemstore.session.BulkOperation;
import org.tasktide.itemstore.session.ItemStoreSession;
import org.tasktide.itemstore.session.LinkedOperation;
import org.tasktide.itemstore.session.LinkedOperationMap;


/**
 * Class to add support for SQLite
 * 
 * @author bkenna
 */
public class SqliteStore extends AbstractItemStore {
    
    // Attributes
    private final Logger LOGGER = LogManager.getLogger(SqliteStore.class);
    private Connection master, proto;
    
    
    public SqliteStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
        super(storeName, dbDirectory, masterDB, protoDB);
        this.initItemStore();
    }
    
    
    /**
     * Construct store with lazy master/proto connection
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     * @param isLinked 
     */
    public SqliteStore(String storeName, String dbDirectory, String masterDB, String protoDB, boolean isLinked) {
        super(storeName, dbDirectory, masterDB, protoDB);
        this.initItemStore(isLinked);
    }
    
    
    /**
     * Executes a {@link LinkedOperationMap} over an
     *  {@link ItemStore} map
     * 
     * @param <T>
     * @param target
     * @param recipients
     * @param operations
     * @return T
     */
    @Override
    public synchronized <T> T execute(
        DbTarget target,
        Map<String, ItemStore> recipients,
        LinkedOperationMap<T> operations
    ) {
        
        // Initialize vars
        Map<String, ItemStoreSession> recipientMap = new HashMap<>();
        
        // Open connections
        try {
        
            ItemStoreSession donor;
            this.openConn(target);
            for ( Entry<String, ItemStore> elm : recipients.entrySet() ) {
                String label = elm.getKey();
                RocksDbStore val = (RocksDbStore) elm.getValue();
                val.openConnNoElection(target);
                ItemStoreSession session = new SqliteSession( val.getConnection(target, Connection.class) );
                recipientMap.put(label, session);
            }
        
            // Perform operation
            donor = new SqliteSession(this.master);
            return operations.execute(donor, recipientMap);
        }
        
        // Close connections
        finally {
            for ( Entry<String, ItemStore> elm : recipients.entrySet() ) { 
                elm.getValue().closeConn(target, false);
            }
            this.closeConn(target, true);
        }
    }

    
    /**
     * Performs the {@link LinkedOperation} over two {@link SqliteStore}
     *  under one connection for both {@link ItemStore}
     * 
     * @param <T>
     * @param target
     * @param recipientStore
     * @param operations
     * @return T
     */
    @Override
    public synchronized <T> T execute(DbTarget target, ItemStore recipientStore, LinkedOperation<T> operations) {
    
        // Opens connections
        ItemStoreSession donor, recipient;
        this.openConn(target);
        ( (SqliteStore) recipientStore).openConnNoElection(target);
        
        // Execute operation
        try {
            switch ( target ) {
                case MASTER -> {
                    donor = new SqliteSession(this.master);
                    recipient = new SqliteSession( ( (AbstractItemStore) recipientStore).getConnection(target, Connection.class) );
                }
                default -> {
                    donor = new SqliteSession(this.proto);
                    recipient = new SqliteSession( ( (AbstractItemStore) recipientStore).getConnection(target, Connection.class) );
                }
            }
            return operations.execute(donor, recipient);
        }
        
        // Close connections
        finally {
            recipientStore.closeConn(target, false);
            this.closeConn(target, true);
        }
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
                    session = new SqliteSession(this.master);
                }
                default -> {
                    session = new SqliteSession(this.proto);
                }
            }
            return operations.execute(session);
        }
        finally {
            this.closeConn(target);
        }
    }
    
    
    /**
     * Get {@link Connection} get connection
     * 
     * @param target
     * @return {@link Connection}
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
     * Initialize ItemStore throwing RuntimeException
     *  if failed from delgated call to InitDatabase to
     *  both the Master & Prototype
     */
    private void initItemStore() {
        LOGGER.info("Initializing DB");
        this.openConn(DbTarget.BOTH);
        this.initDatabase(this.master);
        this.initDatabase(this.proto);
        this.closeConn(DbTarget.BOTH);
    }
    
    
    /**
     * Initialize ItemStore throwing RuntimeException
     *  if failed from delegated call to InitDatabase to
     *  both the Master & Prototype. If linked, then 
     *   election occurs for openning connection, and no
     *   closing occurs.
     * 
     * @param isLined
     */
    private void initItemStore(boolean isLinked) {
        LOGGER.info("Initializing DB under active mutex");
        if ( isLinked ) {
            this.openConnNoElection(DbTarget.BOTH);
        }
        else {
            this.openConn(DbTarget.BOTH);
        }
        this.initDatabase(this.master);
        this.initDatabase(this.proto);
        if ( !isLinked ) {
            this.closeConn(DbTarget.BOTH);
        }
    }
    
    
    /**
     * Initialize database on the provided connection.
     *  Throwing a RuntimeException if failed
     * 
     * @param conn
     * @return boolean
     */
    private boolean initDatabase(Connection conn) {
        String query = 
        """
            CREATE TABLE IF NOT EXISTS Items(
                Id TEXT UNIQUE NOT NULL,
                State TEXT NOT NULL,
                Collection TEXT NOT NULL,
                Payload JSON NOT NULL
            )
        """;
        try ( Statement stmt = conn.createStatement() ) {
            return stmt.execute(query);
        }
        catch ( SQLException ex) {
            LOGGER.error("Error initializing ItemStore displaying statck trace", ex);
            ex.printStackTrace();
            throw new RuntimeException("Sqlite-ItemStore initialization failed", ex);
        }
    }

    
    /**
     * Put {@link Item} into target database
     * 
     * @param conn
     * @param item
     * @return boolean
     */
    private boolean putItem(Connection conn, Item item) {
        String query = 
            "INSERT INTO Items (Id, State, Collection, Payload) VALUES (?, ?, ?, ?)"
        ;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getState());
            ps.setString(3, item.getCollection());
            ps.setString(4, item.getPayload());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Fetch {@link Item} from {@link ResultSet} record
     * 
     * @param rs
     * @return {@link Item}
     */
    private Item parseItem(ResultSet rs) {
        try {
            return new Item(
                rs.getString("Id"),
                rs.getString("State"),
                rs.getString("Collection"),
                rs.getString("Payload")
            );
        }
        catch ( SQLException ex ) {
            return null;
        }
    }
    
    
    /**
     * Consume result into {@link Item} array
     * 
     * @param rs
     * @return List-{@link Item}
     */
    private List<Item> consumeResultSet(ResultSet rs) {
        List<Item> output = new ArrayList<>();
        try {
            while ( rs.next() ) {
                output.add( parseItem(rs) );
            }
        }
        finally {
            return output;
        }
    }
    
    
    /**
     * Select all records from provided connection
     * 
     * @param conn
     * @return List-{@link Item}
     */
    private List<Item> selectAll(Connection conn) {
        String query = 
            "SELECT * FROM Items"
        ;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            return consumeResultSet(rs);
        }
        catch (SQLException ex) {
            return null;
        }
    }
    
    
    /**
     * Execute select query
     * 
     * @param conn
     * @param query
     * @param val
     * @return List-{@link Item}
     */
    private List<Item> selectQuery(Connection conn, String query, String val) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, val);
            ResultSet rs = ps.executeQuery();
            return this.consumeResultSet(rs);
        }
        catch (SQLException ex) {
            return null;
        }
    }
    
    
    /**
     * Deletes {@link Item} from provided database connection
     * 
     * @param conn
     * @param item
     * @return boolean
     */
    private boolean deleteItem(Connection conn, Item item) {
        String query =
            "DELETE FROM Items WHERE Id = ?"
        ;
        try ( PreparedStatement ps = conn.prepareStatement(query) ) {
            ps.setString(1, item.getId());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException ex) {
            LOGGER.error("Error deleting item displaying statck trace", ex);
            ex.printStackTrace();
            return false;
        }
    }
    
    
    /**
     * Save record to target database
     * 
     * @param target
     * @param item
     */
    @Override
    public synchronized void saveItem(DbTarget target, Item item) {
        this.openConn(target);
        switch ( target ) {
            case PROTOTYPE -> {
                this.putItem(this.proto, item);
            }
            case MASTER -> {
                this.putItem(this.master, item);
            }
            case BOTH -> {
                this.putItem(this.proto, item);
                this.putItem(this.master, item);
            }
        }
        this.closeConn(target);
    }

    
    /**
     * Save element under one commit
     * 
     * @param target
     * @param item 
     */
    public synchronized void saveItemElm(DbTarget target, Item item) {
        switch ( target ) {
            case PROTOTYPE -> {
                this.putItem(this.proto, item);
            }
            case MASTER -> {
                this.putItem(this.master, item);
            }
            case BOTH -> {
                this.putItem(this.proto, item);
                this.putItem(this.master, item);
            }
        }
        
    }
    
    /**
     * Save all records to target database
     * 
     * @param target
     * @param items
     */
    @Override
    public synchronized void saveItems(DbTarget target, List<Item> items) {
        this.openConn(target);
        for ( Item elm : items ) {
            this.saveItemElm(target, elm);
        }
        this.closeConn(target);
    }
    
    
    /**
     * Fetch all records from target database
     * 
     * @param target
     * @return List-{@link Item}
     */
    @Override
    public synchronized List<Item> getAll(DbTarget target) {
        List<Item> output;
        this.openConn(target);
        switch ( target ) {
            case PROTOTYPE -> {
                output = this.selectAll(this.proto);
            }
            default -> {
                output = this.selectAll(this.master);
            }
        }
        this.closeConn(target);
        return output;
    }

    
    /**
     * Query on Item Id
     * 
     * @param target
     * @param id
     * @return {@link Item}
     */
    @Override
    public synchronized Item getById(DbTarget target, String id) {
        List<Item> results;
        String query = "SELECT * FROM Items WHERE Id = ?";
        
        this.openConn(target);
        switch (target) {
            case PROTOTYPE -> {
                results = this.selectQuery(this.proto, query, id);
            }
            default -> {
                results = this.selectQuery(this.master, query, id);
            }
        }
        this.closeConn(target);
        
        if ( !results.isEmpty() ) {
            return results.get(0);
        }
        return null;
    }

    
    /**
     * Query on item state
     * 
     * @param target
     * @param state
     * @return List-{@link Item}
     */
    @Override
    public synchronized List<Item> getItemsByState(DbTarget target, String state) {
        List<Item> results;
        String query = "SELECT * FROM Items WHERE State = ?";

        this.openConn(target);
        switch (target) {
            case PROTOTYPE -> {
                results = this.selectQuery(this.proto, query, state);
            }
            default -> {
                results = this.selectQuery(this.master, query, state);
            }
        }
        this.closeConn(target);
        
        return results;
    }

    
    /**
     * Fetch payload for {@link Item} by Id
     * 
     * @param target
     * @param id
     * @return String
     */
    @Override
    public String getPayloadById(DbTarget target, String id) {
        Item result = this.getById(target, id);
        if ( result != null ) {
            return result.getPayload();
        }
        return null;
    }

    
    /**
     * Deletes provided {@link Item} using its Id
     * 
     * @param target
     * @param item
     * @return boolean
     */
    @Override
    public synchronized boolean delete(DbTarget target, Item item) {
        boolean status;
        this.openConn(target);
        switch (target) {
            case PROTOTYPE -> {
                status = this.deleteItem(this.proto, item);
            }
            
            case MASTER -> {
                status = this.deleteItem(this.master, item);
            }
            
            case BOTH -> {
                int counter = 0;
                if ( this.deleteItem(this.proto, item) ) counter++;
                if ( this.deleteItem(this.master, item) ) counter++;
                status = counter == 2;
            }
            
            default -> {
                status = false;
            }
        }
        this.closeConn(target);
        return status;
    }
    
    
    /**
     * Update Item by dropping, then inserting
     * 
     * @param target
     * @param item
     * @return boolean
     */
    @Override
    public synchronized boolean update(DbTarget target, Item item) {
        try {
            this.delete(target, item);
            this.saveItem(target, item);
            return true;
        }
        catch (Exception ex) {return false;}
    }
    
    
    /**
     * Close master and cache connections
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean closeConn(DbTarget target) {
        switch (target) {
            case MASTER -> {
                this.releaseLock(true);
                try {
                    if ( !this.master.isClosed() ) {
                        this.master.close();
                    }
                    return true;
                }
                catch (SQLException ex) {return false;}
            }
            case PROTOTYPE -> {
                try {
                    if ( !this.proto.isClosed() ) {
                        this.proto.close();
                    }
                    return true;
                }
                catch (SQLException ex) {return false;}
            }
            default -> {
                this.releaseLock(true);
                try {
                    if ( !this.master.isClosed() ) {
                        this.master.close();
                    }
                    if ( !this.proto.isClosed() ) {
                        this.proto.close();
                    }
                    return true;
                }
                catch (SQLException ex) {return false;}
            }
        }
    }
    
    /**
     * Close master and cache connections
     * 
     * @param target
     * @param releaseMutex
     * @return boolean
     */
    @Override
    public boolean closeConn(DbTarget target, boolean releaseMutex) {
        switch (target) {
            case MASTER -> {
                this.releaseLock(releaseMutex);
                try {
                    if ( !this.master.isClosed() ) {
                        this.master.close();
                    }
                    return true;
                }
                catch (SQLException ex) {return false;}
            }
            case PROTOTYPE -> {
                try {
                    if ( !this.proto.isClosed() ) {
                        this.proto.close();
                    }
                    return true;
                }
                catch (SQLException ex) {return false;}
            }
            default -> {
                this.releaseLock(releaseMutex);
                try {
                    if ( !this.master.isClosed() ) {
                        this.master.close();
                    }
                    if ( !this.proto.isClosed() ) {
                        this.proto.close();
                    }
                    return true;
                }
                catch (SQLException ex) {return false;}
            }
        }
    }
    
    
    /**
     * Open {@link Connection} to target database
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean openConn(DbTarget target) {
        switch (target) {
            case PROTOTYPE -> {
                try {
                    if ( this.proto == null ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                        return true;
                    }
                    if ( this.proto.isClosed() ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                    }
                    return true;
                }
                catch (SQLException ex) {
                    return false;
                }
            }
            
            case MASTER -> {
                try {
                    this.waitForLock();
                    if ( this.master == null ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                        return true;
                    }
                    if ( this.master.isClosed() ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    this.releaseLock(false);
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
            
            default -> {
                try {
                    this.waitForLock();
                    if ( this.master == null ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    if ( this.master.isClosed() ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    this.releaseLock(false);
                    if ( this.proto == null ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                    }
                    if ( this.proto.isClosed() ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                    }
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
        }
    }
    
    
    /**
     * Open {@link Connection} to target database
     * 
     * @param target
     * @return boolean
     */
    public boolean openConnNoElection(DbTarget target) {
        switch (target) {
            case PROTOTYPE -> {
                try {
                    if ( this.proto == null ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                        return true;
                    }
                    if ( this.proto.isClosed() ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                    }
                    return true;
                }
                catch (SQLException ex) {
                    return false;
                }
            }
            
            case MASTER -> {
                try {
                    if ( this.master == null ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                        return true;
                    }
                    if ( this.master.isClosed() ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    this.releaseLock(false);
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
            
            default -> {
                try {
                    if ( this.master == null ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    if ( this.master.isClosed() ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    this.releaseLock(false);
                    if ( this.proto == null ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                    }
                    if ( this.proto.isClosed() ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                    }
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
        }
    }
    
    
    /**
     * {@link ItemStoreSession} for SQLite {@link Connection}
     */
    private class SqliteSession implements ItemStoreSession {
        
        // Attributes
        private final Connection conn;

        /**
         * Construct with {@link Connection}
         * @param conn 
         */
        SqliteSession(Connection conn) {
            this.conn = conn;
        }
        
        
        @Override
        public boolean insert(Item item) {
            return putItem(conn, item);
        }

        @Override
        public Item getById(String id) {
            List<Item> results;
            String query = "SELECT * FROM Items WHERE Id = ?";
            results = selectQuery(this.conn, query, id);
            if ( !results.isEmpty() ) {
                return results.get(0);
            }
            return null;
        }

        @Override
        public boolean delete(Item item) {
            return deleteItem(this.conn, item);
        }
        
        @Override
        public List<Item> getItemsByState(String state) {
            List<Item> results;
            String query = "SELECT * FROM Items WHERE State = ?";
            results = selectQuery(this.conn, query, state);
            return results;
        }

        @Override
        public List<Item> getAll() {
            return selectAll(this.conn);
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
            LOGGER.debug(
                "Records inserted = '{}', Expected = '{}'",
                counter, items.size()
            );
            return counter == items.size();
        }
    }
}