/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.itemstore;

import org.tasktide.itemstore.AbstractItemStore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.itemstore.Item;


/**
 * Class to add support for SQLite
 * 
 * @author bkenna
 */
public class SqliteStore extends AbstractItemStore {
    
    // Attributes
    private Connection master, proto;
    
    
    /**
     * Construct store with lazy master/proto connection
     * 
     * @param storeName
     * @param dbDirectory
     * @param masterDB
     * @param protoDB 
     */
    public SqliteStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
        super(storeName, dbDirectory, masterDB, protoDB);
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
            "INSERT INTO ? VALUES (?, ?, ?)"
        ;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, this.getStoreName());
            ps.setString(2, item.getId());
            ps.setString(3, item.getState());
            ps.setString(4, item.getPayload());
            return ps.execute();
        }
        catch (SQLException ex) {
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
            "SELECT * FROM ?"
        ;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, this.getStoreName());
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
     * @return List-{@link Item}
     */
    private List<Item> selectQuery(Connection conn, String query) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, this.getStoreName());
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
            "DELETE FROM ? WHERE Id = ?"
        ;
        try ( PreparedStatement ps = conn.prepareStatement(query) ) {
            ps.setString(1, this.getStoreName());
            ps.setString(2, item.getId());
            return ps.execute();
        }
        catch (SQLException ex) {
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
    public void saveItem(DbTarget target, Item item) {
        this.openConn(target);
        switch ( target ) {
            case PROTOTYPE -> {
                this.putItem(this.proto, item);
            }
            default -> {
                this.putItem(this.master, item);
                this.putItem(this.proto, item);
            }
        }
        this.closeConn(target);
    }

    
    /**
     * Save all records to target database
     * 
     * @param target
     * @param items
     */
    @Override
    public void saveItems(DbTarget target, List<Item> items) {
        for ( Item elm : items ) {
            this.saveItem(target, elm);
        }
    }
    
    
    /**
     * Fetch all records from target database
     * 
     * @param target
     * @return List-{@link Item}
     */
    @Override
    public List<Item> getAll(DbTarget target) {
        List<Item> output;
        this.openConn(target);
        switch ( target ) {
            case MASTER -> {
                output = this.selectAll(this.master);
            }
            default -> {
                output = this.selectAll(this.proto);
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
    public Item getById(DbTarget target, String id) {
        List<Item> results;
        this.openConn(target);
        String query = "SELECT * FROM ? WHERE Id = ?";
        results = this.selectQuery(proto, query);
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
    public List<Item> getItemsByState(DbTarget target, String state) {
        List<Item> results;
        this.openConn(target);
        String query = "SELECT * FROM ? WHERE State = ?";
        results = this.selectQuery(proto, query);
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

    
    @Override
    public boolean delete(DbTarget target, Item item) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
                this.releaseLock();
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
                this.releaseLock();
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
                    this.releaseLock();
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
                        return true;
                    }
                    if ( this.master.isClosed() ) {
                        this.master = DriverManager.getConnection("jdbc:sqlite:" + this.getMasterFilePath());
                    }
                    this.releaseLock();
                    
                    if ( this.proto == null ) {
                        this.proto = DriverManager.getConnection("jdbc:sqlite:" + this.getFilePath());
                        return true;
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
    
    
    
    
}
