/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
    String id, state, payload;
    
    
    /**
     * Default constructor
     */
    public Item(){}
    
    
    /**
     * Construct with properties
     * 
     * @param id
     * @param state
     * @param payload 
     */
    public Item(String id, String state, String payload) {
        this.id = id;
        this.state = state;
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
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "Item{" +
            "id=" + id +
            ", state=" + state +
            ", payload=" + payload +
        '}';
    }
}
