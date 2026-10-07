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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tasktide.itemstore.types.DbTarget;
import static org.tasktide.itemstore.types.DbTarget.*;

import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;
import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;
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
    
    
    /**
     * Constructs SQLite {@link ItemStore}
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     */
    public SqliteStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
        super(storeName, dbDirectory, masterDB, protoDB);
        this.initItemStore();
    }
    
    
    /**
     * Constructs SQLite {@link ItemStore}
     * 
     * @param storeName
     * @param dbDirectory
     */
    public SqliteStore(String storeName, String dbDirectory) {
        super(storeName, dbDirectory);
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
     * Constructs SQLite {@link ItemStore}
     * 
     * @param storeName
     * @param dbDirectory
     * @param isLinked
     */
    public SqliteStore(String storeName, String dbDirectory, boolean isLinked) {
        super(storeName, dbDirectory);
        this.initItemStore(isLinked);
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
        LOGGER.info("Acquiring mutex to intialize DB");
        this.withLockedConnectionUnchecked(
            "Initialize DB",
            DbTarget.BOTH,
            () -> {
                this.initDatabase(this.master);
                this.initDatabase(this.proto);
                return true;
        });
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
        if ( isLinked ) {
            LOGGER.info("Initializing DB under active mutex");
            this.openConn(DbTarget.BOTH);
            this.initDatabase(this.master);
            this.initDatabase(this.proto);
            this.closeConn(DbTarget.BOTH);
        }
        else {
            this.initItemStore();
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
            LOGGER.error("Error initializing ItemStore displaying statck trace\n\n", ex);
            throw new ItemStoreUncheckedException("Sqlite-ItemStore initialization failed", ex);
        }
    }

    
    /**
     * Fetch Connection for {@link DbTarget}
     * 
     * @param target
     * @return Connection
     */
    private Connection getFor(DbTarget target) {
        if ( target == DbTarget.MASTER ) {
            return this.master;
        }
        else if ( target == DbTarget.PROTOTYPE ) {
            return this.proto;
        }
        else {
            throw new ItemStoreUncheckedException("Target must be one of Master or Prototype");
        }
    }
    
    
    /**
     * Put {@link Item} into target database
     * 
     * @param conn
     * @param item
     * @return boolean
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    private boolean putItem(
        Connection conn,
        Item item
    ) throws ItemStoreCheckedException {
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
            LOGGER.error("Error inserting record displaying statck trace\n\n", ex);
            throw new ItemStoreCheckedException(
                "Error inserting record displaying statck trace",
                ex
            );
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
     * 
     * @throws {@link SQLException}
     */
    private List<Item> consumeResultSet(ResultSet rs) throws SQLException {
        List<Item> output = new ArrayList<>();
        try (rs) {
            while ( rs.next() ) {
                output.add( this.parseItem(rs) );
            }
            return output;
        }
    }
    
    
    /**
     * Select all records from provided connection
     * 
     * @param conn
     * @return List-{@link Item}
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    private List<Item> selectAll(Connection conn) throws ItemStoreCheckedException {
        String query = 
            "SELECT * FROM Items"
        ;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            return consumeResultSet(rs);
        }
        catch (SQLException ex) {
            LOGGER.error("Error inserting record displaying statck trace\n\n", ex);
            throw new ItemStoreCheckedException(
                "Error inserting record displaying statck trace",
                ex
            );
        }
    }
    
    
    /**
     * Execute select query
     * 
     * @param conn
     * @param query
     * @param val
     * @return List-{@link Item}
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    private List<Item> selectQuery (
        Connection conn,
        String query,
        String val
    ) throws ItemStoreCheckedException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, val);
            ResultSet rs = ps.executeQuery();
            return this.consumeResultSet(rs);
        }
        catch (SQLException ex) {
            throw new ItemStoreCheckedException("Error performing query\n\n", ex);
        }
    }
    
    
    /**
     * Execute select query
     * 
     * @param conn
     * @param query
     * @param val
     * @return List-{@link Item}
     * 
     * @throws {@link ItemStoreUncheckedException}
     */
    private List<Item> selectQueryUnchecked(
        Connection conn,
        String query,
        String val
    ) throws ItemStoreUncheckedException{
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, val);
            ResultSet rs = ps.executeQuery();
            return this.consumeResultSet(rs);
        }
        catch (SQLException ex) {
            throw new ItemStoreUncheckedException("Error performing query\n\n", ex);
        }
    }
    
    
    /**
     * Deletes {@link Item} from provided database connection
     * 
     * @param conn
     * @param item
     * @return boolean
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    private boolean deleteItem(Connection conn, Item item) throws ItemStoreCheckedException {
        String query =
            "DELETE FROM Items WHERE Id = ?"
        ;
        try ( PreparedStatement ps = conn.prepareStatement(query) ) {
            ps.setString(1, item.getId());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException ex) {
            LOGGER.error("Error deleting item displaying statck trace\n\n", ex);
            throw new ItemStoreCheckedException(
                "Error deleting item displaying statck trace",
                ex
            );
        }
    }
    
    
    /**
     * Update Item, by deleting and adding back in use. Using the
     *  SQL methods so that {@link ItemStore} interface can run
     *  this method under locked-connection lifecycle
     * 
     * @param conn
     * @param item
     * @return boolean
     * 
     * @throws {@link ItemStoreCheckedException} 
     */
    private boolean updateItem(Connection conn, Item item) throws ItemStoreCheckedException {
        if ( this.deleteItem(conn, item) ) {
            return this.putItem(conn, item);
        }
        return false;
    }
    
    
    /**
     * Save element under one commit
     * 
     * @param target
     * @param item
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    public void saveItemElm(
        DbTarget target,
        Item item
    ) throws ItemStoreCheckedException {
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
     * Save record to target database
     * 
     * @param target
     * @param item
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public void saveItem(
        DbTarget target,
        Item item
    ) throws ItemStoreCheckedException {
        this.withLockedConnection(
            "Save Item",
            target,
            () -> {
                this.saveItemElm(target, item);
                return true;
        });
    }

    
    /**
     * Save all records to target database
     * 
     * @param target
     * @param items
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public void saveItems(DbTarget target, List<Item> items) throws ItemStoreCheckedException {
        this.withLockedConnection(
            "Save Items",
            target,
            () -> {
                for (Item elm: items) {
                    this.saveItemElm(target, elm);
                }
                return true;
        }); 
    }
    
    
    /**
     * Fetch all records from target database
     * 
     * @param target
     * @return List-{@link Item}
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public List<Item> getAll(DbTarget target) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "Select All Records",
            target,
            () -> {
                switch ( target ) {
                    case PROTOTYPE -> {
                        return this.selectAll(this.proto);
                    }
                    default -> {
                        return this.selectAll(this.master);
                    }
                }
        });
    }

    
    /**
     * Query on Item Id
     * 
     * @param target
     * @param id
     * @return {@link Item}
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public Item getById(DbTarget target, String id) throws ItemStoreCheckedException {
        List<Item> results;
        String query = "SELECT * FROM Items WHERE Id = ?";
        results = this.withLockedConnection(
            "Get By Id",
            target,
            () -> {
                switch (target) {
                    case PROTOTYPE -> {
                        return this.selectQuery(this.proto, query, id);
                    }
                    default -> {
                        return this.selectQuery(this.master, query, id);
                    }
            }
        });
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
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public List<Item> getItemsByState(DbTarget target, String state) throws ItemStoreCheckedException {
        List<Item> results;
        String query = "SELECT * FROM Items WHERE State = ?";
        results = this.withLockedConnection(
            "Get Items By State",
            target,
            () -> {
                switch (target) {
                    case PROTOTYPE -> {
                        return this.selectQuery(this.proto, query, state);
                    }
                    default -> {
                        return this.selectQuery(this.master, query, state);
                    }
            }
        });
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
        Item result;
        try {
            result = this.getById(target, id);
        }
        catch (ItemStoreCheckedException ex) {
            result = null;
        }
        
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
     * 
     * @return boolean
     * 
     * @throws {@link ItemStoreCheckedException}
     */
    @Override
    public boolean delete(DbTarget target, Item item) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "Delete Item",
            target,
            () -> {
                switch (target) {
                    case PROTOTYPE -> {
                        return this.deleteItem(this.proto, item);
                    }

                    case MASTER -> {
                        return this.deleteItem(this.master, item);
                    }

                    case BOTH -> {
                        int counter = 0;
                        if ( this.deleteItem(this.proto, item) ) counter++;
                        if ( this.deleteItem(this.master, item) ) counter++;
                        return counter == 2;
                    }

                    default -> {
                        return false;
                    }
                }
        });
    }
    
    
    /**
     * Update Item by dropping, then inserting
     * 
     * @param target
     * @param item
     * @return boolean
     */
    @Override
    public boolean update(DbTarget target, Item item) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "Update Item",
            target,
            () -> {
                if ( target == DbTarget.BOTH ) {
                    return
                        this.updateItem(this.proto, item) &
                        this.updateItem(this.master, item);
                }
                else {
                    Connection conn = this.getFor(target);
                    return this.updateItem(conn, item);
                }
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
        try {
            if ( target == DbTarget.MASTER ) {
                if ( this.master == null ) {
                    return false;
                }
                return !this.master.isClosed();
            }
            if ( target == DbTarget.PROTOTYPE ) {
                if ( this.proto == null ) {
                    return false;
                }
                return !this.proto.isClosed();
            }
            return false;
        }
        catch (SQLException ex) {
            LOGGER.error("Error checking whether target DB is opened:\n\n", ex);
            throw new ItemStoreUncheckedException(ex.getMessage(), ex);
        }
    }
    
    
    /**
     * Checks if Master/Prototype are open
     * 
     * @param target
     * @return boolean
     */
    @Override
    public boolean isClosed(DbTarget target) {
        try {
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
        catch (SQLException ex) {
            LOGGER.error("Error checking whether target DB is closed:\n\n", ex);
            throw new ItemStoreUncheckedException(ex.getMessage(), ex);
        }
    }
    
    
    /**
     * Open connection master connection
     * 
     * @return boolean
     */
    @Override
    public boolean openMaster() {
        try {
            this.master = DriverManager.getConnection(
                "jdbc:sqlite:" +
                this.masterDB.resolve("master.db")
            );
            return true;
        }
        catch (SQLException ex) {
            LOGGER.error("Error opening connection to Master DB:\n", ex);
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
        catch (SQLException ex) {
            LOGGER.error("Error closing connection to Prototype DB:\n", ex);
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
            this.proto = DriverManager.getConnection(
                "jdbc:sqlite:" +
                this.protoDB.resolve("prototype.db")
            );
            return true;
        }
        catch (SQLException ex) {
            LOGGER.error("Error opening connection to prototype DB:\n", ex);
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
        catch (SQLException ex) {
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
     * 
     * @return T
     */
    @Override
    public <T> T execute(
        DbTarget target,
        BulkOperation<T> operationSet
    ) throws ItemStoreCheckedException {
        return this.withLockedConnection(
            "SQLite Operations Set",
            target,
            () -> {
                ItemStoreSession session;
                switch (target) {
                    case PROTOTYPE -> {
                        session = new SqliteSession(this.proto);
                    }
                    default -> {
                        session = new SqliteSession(this.master);
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
            "Linked SQLite Operation Set",
            target,
            () -> {
                
                // Configure sessions
                ItemStoreSession donor, recipient;
                try {
                    switch ( target ) {
                        case PROTOTYPE -> {
                            recipientStore.openConn(DbTarget.PROTOTYPE);
                            donor = new SqliteSession(this.proto);
                            recipient = new SqliteSession(
                                ( (AbstractItemStore) recipientStore).getConnection(DbTarget.PROTOTYPE, Connection.class)
                            );
                        }
                        default -> {
                            recipientStore.openConn(DbTarget.MASTER);
                            donor = new SqliteSession(this.master);
                            recipient = new SqliteSession(
                                ( (AbstractItemStore) recipientStore).getConnection(DbTarget.MASTER, Connection.class)
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
            "Map of Linked SQLite Operations",
            target,
            () -> {
                
                // Try perform operations
                try {
                    ItemStoreSession donor;
                    if ( target == PROTOTYPE ) {
                        donor = new SqliteSession(this.proto);
                    }
                    else {
                        donor = new SqliteSession(this.master);
                    }

                    // Handle recipient sessions
                    Map<String, ItemStoreSession> sessionMap = new HashMap<>();
                    for ( Entry<String, ItemStore> elm : recipients.entrySet() ) {
                        String key = elm.getKey();
                        RocksDbStore val = ( RocksDbStore ) elm.getValue();
                        val.openConn(target);
                        ItemStoreSession session = new SqliteSession(val.getConnection(target, Connection.class));
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
     * {@link ItemStoreSession} for SQLite {@link Connection}
     * 
     */
    private class SqliteSession implements ItemStoreSession {
    
        
        // Attributes
        private final Connection conn;
        
        
        /**
         * Construct {@link ItemStoreSession} for SQLite {@link Connection}
         * 
         * @param conn 
         */
        SqliteSession(Connection conn) {
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
            return putItem(conn, item);
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
            List<Item> results;
            String query = "SELECT * FROM Items WHERE Id = ?";
            results = selectQuery(this.conn, query, id);
            if ( !results.isEmpty() ) {
                return results.get(0);
            }
            return null;
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
            return deleteItem(this.conn, item);
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
            return selectAll(this.conn);
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
            List<Item> results;
            String query = "SELECT * FROM Items WHERE State = ?";
            results = selectQuery(this.conn, query, state);
            return results;
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