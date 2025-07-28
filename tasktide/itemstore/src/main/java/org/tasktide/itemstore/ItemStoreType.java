/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

package org.tasktide.itemstore;


/**
 * Enum to support operations over {@link ItemStore}, and construction
 * 
 * @author bkenna
 */
public enum ItemStoreType {

    ROCKSDB {

        @Override
        public boolean isItemStoreType(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isItemStoreType(ItemStoreType query) {
            return this == query;
        }
        
        @Override
        public ItemStore makeItemStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
            return new RocksDBStore(storeName, dbDirectory, masterDB, protoDB);
        }

        @Override
        public String toString() {
            return name();
        }
    },

    SQLITE {
        @Override
        public boolean isItemStoreType(String query) {
            return this.toString().equalsIgnoreCase(query);
        }

        @Override
        public boolean isItemStoreType(ItemStoreType query) {
            return this == query;
        }
        
        @Override
        public ItemStore makeItemStore(String storeName, String dbDirectory, String masterDB, String protoDB) {
            return new SqliteStore(storeName, dbDirectory, masterDB, protoDB);
        }

        @Override
        public String toString() {
            return name();
        }
    };

    public abstract boolean isItemStoreType(String query);
    public abstract boolean isItemStoreType(ItemStoreType query);
    public abstract ItemStore makeItemStore(String storeName, String dbDirectory, String masterDB, String protoDB);
    
    
    /**
     * Fetch index of query
     * 
     * @param name
     * @return int
     */
    public static int indexOf(String name) {
        for (ItemStoreType itemstoretype : values() ) {
            if (itemstoretype.isItemStoreType(name)) {
                return itemstoretype.ordinal();
            }
        }
        return -1;
    }

    
    /**
     * Checks if enum has value
     * 
     * @param query
     * @return boolean
     */
    public static boolean hasQuery(String query) {
        if (query == null) {
            return false;
        }
        for (ItemStoreType itemstoretype : values()) {
            if (itemstoretype.isItemStoreType(query)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Fetches value for query
     * 
     * @param query
     * @return ItemStoreType
     */
    public static ItemStoreType get(String query) {
        int ind = indexOf(query);
        if (ind >= 0) {
            return values()[ind];
        }
        return null;
    }
}
