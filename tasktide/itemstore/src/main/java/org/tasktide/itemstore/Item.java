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
 * Base model storing records in {@link ItemStore}
 * 
 * @param <T>
 * @author bkenna
 */
public class Item<T> {
    
    // Attributes
    private String id, state, collection, payload;
    
    
    /**
     * Default constructor
     * 
     */
    public Item(){}
    
    
    /**
     * Construct with properties
     * 
     * @param id
     * @param state
     * @param collection
     * @param payload 
     */
    public Item(String id, String state, String collection, String payload) {
        this.id = id;
        this.state = state;
        this.collection = collection;
        this.payload = payload;
    }
    
    
    /**
     * Get Id
     * 
     * @return String
     */
    public String getId() {
        return id;
    }

    
    /**
     * Set id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * Get state
     * 
     * @return String
     */
    public String getState() {
        return state;
    }

    
    /**
     * Set state
     * 
     * @param state 
     */
    public void setState(String state) {
        this.state = state;
    }

    
    /**
     * Get payload
     * 
     * @return String
     */
    public String getPayload() {
        return payload;
    }

    
    /**
     * Set payload
     * 
     * @param payload 
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    
    /**
     * Get collection
     * 
     * @return 
     */
    public String getCollection() {
        return this.collection;
    }

    
    /**
     * Set step name
     * 
     * @param collection 
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Item{" +
            "id=" + id +
            ", state=" + state +
            ", collection=" + collection +
            ", payload=" + payload +
        '}';
    }
}