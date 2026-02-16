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


/**
 * Enum to support operations over {@link ItemStore} based on the
 *  type value, and strategic factory
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
            return new RocksDbStore(storeName, dbDirectory, masterDB, protoDB);
        }
        
        @Override
        public ItemStore makeItemStoreNoElection(String storeName, String dbDirectory, String masterDB, String protoDB) {
            return makeItemStore(storeName, dbDirectory, masterDB, protoDB);
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
        public ItemStore makeItemStoreNoElection(String storeName, String dbDirectory, String masterDB, String protoDB) {
            return new SqliteStore(storeName, dbDirectory, masterDB, protoDB, true);
        }

        @Override
        public String toString() {
            return name();
        }
    };

    public abstract boolean isItemStoreType(String query);
    public abstract boolean isItemStoreType(ItemStoreType query);
    public abstract ItemStore makeItemStore(String storeName, String dbDirectory, String masterDB, String protoDB);
    public abstract ItemStore makeItemStoreNoElection(String storeName, String dbDirectory, String masterDB, String protoDB);
    
    
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