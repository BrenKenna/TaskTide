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
package org.tasktide.tasktide.parser.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;


/**
 * Datapoint of {@link GenericTree}
 * 
 * @author bkenna
 */
public class GenericTreeNode<T> {
    
    // Attributes
    private T data;
    private final String address;
    private GenericTreeNode<T> parent;
    private Map<String, GenericTreeNode<T>> children = new HashMap<>();
    
    
    /**
     * Construct tree node
     * 
     * @param data
     * @param address 
     */
    public GenericTreeNode(T data, String address) {
        this.data = data;
        this.address = address;
    }

    
    /**
     * Get address of node
     * 
     * @return String
     */
    public String getAddress() {
        return address;
    }
    
    
    /**
     * Get value of node
     * 
     * @return T
     */
    public T getData() {
        return data;
    }
    
    
    /**
     * Set value of node
     * 
     * @param data 
     */
    public void setData(T data) {
        if ( this.data == null ) {
            this.data = data;
        }
    }
    
    
    /**
     * Get the children of current node
     * 
     * @return Map-String, {@link GenericTreeNode}
     */
    public Map<String, GenericTreeNode<T>> getChildren() {
        return children;
    }
    
    
    /**
     * Get parent of current node
     * 
     * @return 
     */
    public GenericTreeNode<T> getParent() {
        return this.parent;
    }
    
    
    /**
     * Set the parent of current node
     * 
     * @param parent 
     */
    public void setParent(GenericTreeNode<T> parent) {
        this.parent = parent;
    }
    
    
    /**
     * Check whether current node has children/is leaf node
     * 
     * @return boolean
     */
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
    
    
    /**
     * Check whether current node has queried child
     * 
     * @param query
     * @return boolean
     */
    public boolean hasChild(String query) {
        return this.children.containsKey(query);
    }
    
    
    /**
     * Fetch names of current nodes children
     * 
     * @return Set-String
     */
    public Set<String> listChildren() {
        return this.children.keySet();
    }
    
    
    /**
     * Remove provided key from children
     * 
     * @param key
     * @return boolean
     */
    public boolean removeChild(String key) {
        if (this.children.containsKey(key)) {
            this.children.remove(key);
            return true;
        }
        return false;
    }
    
    
    /**
     * Clear all child nodes
     * 
     */
    public void clearChildren() {
        this.children.clear();;
    }
}
