/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.model.state_summary;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;


/**
 * JSON-B compatible representation of {@link StateSummaryType}
 *  maps
 * 
 * <br>
 * Resolves yasson-3.0.>=4 bump where generic key does not seem supported
 * <br> PR = https://github.com/BrenKenna/TaskTide/pull/15
 * <br> Commit = https://github.com/BrenKenna/TaskTide/commit/5a4894d3421c9d7a926fa54f7bb9988ef244a60d
 * <br>
 *
 * @author Bren
 */
public abstract class StateSummary<T extends Enum<T>> {

    
    // Attributes
    @JsonbProperty("State Summary")
    protected final Map<String, Integer> summaryMap;
    
    @JsonbProperty("State Type")
    private final StateSummaryType type;
    
    @JsonbTransient
    private final Class<T> classRef;
            
    
    /**
     * Initialize with {@link StateSummaryType}, required 
     *  class
     * 
     * @param type 
     */
    public StateSummary(StateSummaryType type, Class<T> classRef) {
        this.type = type;
        this.classRef = classRef;
        this.summaryMap = new HashMap<>();
    }
    
    
    /**
     * Initialize with {@link StateSummaryType} and summary map
     * 
     * @param type
     * @param classRef
     * @param summaryMap 
     */
    public StateSummary(StateSummaryType type, Class<T> classRef, Map<String, Integer> summaryMap) {
        this.type = type;
        this.classRef = classRef;
        this.summaryMap = summaryMap;
    }

    
    /**
     * Get {@link StateSummaryType}
     * 
     * @return {@link StateSummaryType}
     */
    public StateSummaryType getType() {
        return type;
    }

    
    /**
     * Get class reference
     * 
     * @return 
     */
    public Class<T> getClassRef() {
        return this.classRef;
    }
    
    
    /**
     * Get summary map
     * 
     * @return Map-String, Integer
     */
    public Map<String, Integer> getSummaryMap() {
        return summaryMap;
    }
    
    
    /**
     * Add summary value for {@link StateSummaryType} instance
     * 
     * @param key
     * @param value 
     */
    public void addElement(String key, int value) {
        if ( !this.summaryMap.containsKey(key) ) {
            this.summaryMap.put(key, value);
        }
    }
    
    
    /**
     * Fetch entry for queried key string
     * 
     * @param key
     * @return Entry-String, Integer
     */
    public Entry<String, Integer> getEntry(String key) {
        for ( Entry<String, Integer> elm : this.summaryMap.entrySet() ) {
            if ( elm.getKey().equals(key) ) {
                return elm;
            }
        }
        return null;
    }
    
    
    /**
     * Get the value for the specified key
     * 
     * @param key
     * @return int
     */
    public int getValueFor(String key) {
        if ( this.summaryMap.containsKey(key) ) {
            return this.summaryMap.get(key);
        }
        return -1;
    }
    
    
    /**
     * Check if {@link StateSummary} has provided key
     * 
     * @param key
     * @return boolean
     */
    public boolean hasKey(String key) {
        return this.summaryMap.containsKey(key);
    }
    
    
    /**
     * Map queried state to its state string
     * 
     * @param query
     * 
     * @return String 
     */
    public String mapQueryToStateString(T query) {
        return query.name();
    }
    

    /**
     * Map queried string to state
     * 
     * @param query
     * @return T
     */
    public abstract T mapQueryToState(String query);

    
    /**
     * Check whether state map has queried key
     * 
     * @param query
     * @return boolean
     */
    public boolean hasKey(T query) {
        String key = this.mapQueryToStateString(query);
        return this.hasKey(key);
    }
    
    
    /**
     * Adds element to state summary
     * 
     * @param key
     * @param value 
     */
    public void addElement(T state, int value) {
        String key = this.mapQueryToStateString(state);
        this.addElement(key, value);
    }
    
    
    /**
     * Get value for queried state
     * 
     * @param query
     * @return int
     */
    public int getValueFor(T query) {
        String key = this.mapQueryToStateString(query);
        return this.getValueFor(key);
    }
    
    
    /**
     * Get entry for query
     * 
     * @param query
     * @return Entry-String, int
     */
    public Entry<String, Integer> getEntry(T query) {
        String key = this.mapQueryToStateString(query);
        return this.getEntry(key);
    }
}