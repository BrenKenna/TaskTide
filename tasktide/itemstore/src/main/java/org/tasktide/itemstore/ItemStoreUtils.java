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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Brendan Kenna
 */
public class ItemStoreUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(ItemStoreUtils.class);
    
    
    /**
     * Create required {@link ItemStore}
     * 
     * @param storeType
     * @param storeName
     * 
     * @return {@link ItemStore}
     */
    public static ItemStore getStore(ItemStoreType storeType, String storeName) {
    
        // Create file path
        Path store = Paths.get(storeName);
        try {
            Files.createDirectories(store);
            LOGGER.debug("ItemStore Directory created under:\t'{}'", storeName);
        }
        catch (IOException ex) {
            LOGGER.debug("ItemStoreDirectory already exists under:\t'{}'", storeName);
        }
        
        // Set vars
        String dbDirectory = store.toString();
        String masterDB = "master";
        String protoDB = UUID.randomUUID().toString();
        return storeType.makeItemStore(storeName, dbDirectory, masterDB, protoDB);
    }
    
    
    /**
     * Create a mock {@link Item}, Id field is random UUID
     * 
     * @return {@link Item}-String
     */
    public static Item<String> makeMockItem() {
        return new Item<>(UUID.randomUUID().toString(), "state", "step", "payload");
    }
    
    
    /**
     * Create a mock {@link Item}, Id field is random UUID
     * 
     * @param label
     * @return {@link Item}-String
     */
    public static Item<String> makeMockItem(String label) {
        return new Item<>(UUID.randomUUID().toString(), label, "step", "payload");
    }
    
    
    /**
     * Make a collection of mock {@link Item} of
     *  required size
     * 
     * @param amount
     * @return List-{@link Item}-String
     */
    public static List<Item> makeMockItemCollection(int amount) {
        List<Item> output = new ArrayList<>();
        for ( int i = 0;  i < amount; i++) {
            output.add(makeMockItem());
        }
        return output;
    }
    
    
    /**
     * Make a collection of mock {@link Item} of
     *  required size
     * 
     * @param amount
     * @param label
     * 
     * @return List-{@link Item}-String
     */
    public static List<Item> makeMockItemCollection(int amount, String label) {
        List<Item> output = new ArrayList<>();
        for ( int i = 0;  i < amount; i++) {
            output.add(makeMockItem());
        }
        return output;
    }
}