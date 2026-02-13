/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.itemstore.session;

import java.util.List;

import org.tasktide.itemstore.Item;


/**
 * Operations supported under an {@link ItemStore} session, ultimately
 *  these are pushed back to the DB just bypassing the Open/Close
 *  connection aspects until done.
 *
 * @author Brendan Kenna
 */
public interface ItemStoreSession {
    
    /**
     * Insert provided {@link Iten}
     * 
     * @param item 
     * @return  
     */
    boolean insert(Item item);
    
    
    /**
     * Fetch {@link Item} matching Id
     * 
     * @param id
     * @return 
     */
    Item getById(String id);
    
    
    /**
     * Remove provided {@link Item}
     * 
     * @param item 
     * @return  
     */
    boolean delete(Item item);
    
    
    /**
     * Fetch all records
     * 
     * @return 
     */
    List<Item> getAll();
    
    
    /**
     * Fetch {@link Item} collection matching provided state
     * 
     * @param state
     * @return List-{@link Item}
     */
    public List<Item> getItemsByState(String state);
    
    
    /**
     * Fetch payload of {@link Item} by its Id
     * 
     * @param id
     * @return String
     */
    public String getPayloadById(String id);
}