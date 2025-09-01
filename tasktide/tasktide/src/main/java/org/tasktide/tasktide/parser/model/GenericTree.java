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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Generic implementation of tree
 * 
 * @param <T> - Data Type of Tree 
 * @author bkenna
 */
public class GenericTree<T> {
    
    // Attributes
    private final GenericTreeNode<T> root;
    private final String delimiter;
    
    
    /**
     * Construct with delimiter
     * 
     * @param delimiter 
     */
    public GenericTree(String delimiter) {
        this.root = new GenericTreeNode<>(null, "");
        this.delimiter = delimiter;
    }
    
    
    /**
     * Get root node
     * 
     * @return GenericTreeNode-T
     */
    public GenericTreeNode<T> getRoot() {
        return root;
    }

    
    /**
     * Add child under provided address
     * 
     * @param path
     * @param data 
     */
    public void addChild(String path, T data) {
        this.recursiveAdd(root, path, data);
    }
    
    
    /**
     * Find {@link GenericTreeNode} at provided address
     * 
     * @param path
     * @return {@link GenericTreeNode}-T
     */
    public GenericTreeNode<T> findByAddress(String path) {
        return this.recursiveGetByAddress(root, path);
    }
    
    
    /**
     * Fetch data value for address
     * 
     * @param path
     * @return T
     */
    public T getDataForAddress(String path) {
        return this.findByAddress(path).getData();
    }
    
    
    /**
     * Remove address from Tree without reshaping
     * 
     * @param path
     * @return boolean
     */
    public boolean removeByAddress(String path) {
        String[] addressArr = path.split(Pattern.quote(delimiter), 2);
        return recursiveRemove(root, addressArr);
    }
    
    
    /**
     * Get map of child nodes under address
     * 
     * @param path
     * @return Map-String, {@link GenericTreeNode}-T
     */
    public Map<String, GenericTreeNode<T>> getChildrenAtAddress(String path) {
        GenericTreeNode<T> node = this.findByAddress(path);
        return node != null ? node.getChildren() : null;
    }
    
    
    /**
     * Get map of data values for children under address
     * 
     * @param path
     * @param delim
     * @return Map-String, Data
     */
    public Map<String, T> getChildrenDataAtAddress(String path, String delim) {
        GenericTreeNode<T> node = this.findByAddress(path);
        if (node == null || node.getChildren() == null) return null;
        
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, GenericTreeNode<T>> entry : node.getChildren().entrySet()) {
            result.put(entry.getKey(), entry.getValue().getData());
        }
        
        return result;
    }
    
    
    
    /**
     * Find first node matching query depth-first
     * 
     * @param query
     * @return {@link GenericTreeNode}-t
     */
    public GenericTreeNode<T> findByData(T query) {
        
        // Base case
        if ( this.root.listChildren().isEmpty() ) return null;
        
        // Search depth-first
        return recursiveFindByData(root, query);
    }
    
    
    /**
     * Check whether tree contains queried value
     * 
     * @param query
     * @return query
     */
    public boolean containsData(T query) {
        return findByData(query) != null;
    }
    
    
    /**
     * Return size of tree
     * 
     * @return int
     */
    public int size() {
        return sizeRecursive(root);
    }
    
    
    /**
     * Get path to the root node of the tree, from current node
     * 
     * @param node
     * @return List-{@link GenericTreeNode}-T
     */
    public List<GenericTreeNode<T>> getPathToRoot(GenericTreeNode<T> node) {
        List<GenericTreeNode<T>> path = new ArrayList<>();
        GenericTreeNode<T> current = node;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }
    
    
    /**
     * Convert tree to a {@link LinkedHashMap} of address-node value pairs
     * 
     * @return Map-String, Value
     */
    public Map<String, T> toAddressDataMap() {
        Map<String, T> result = new LinkedHashMap<>();
        collectAddressData(root, result);
        return result;
    }
    
    
    /**
     * Recursively tree depth-first collecting the address and data of each node
     * 
     * @param current
     * @param map 
     */
    private void collectAddressData(GenericTreeNode<T> current, Map<String, T> map) {
        if ( current == null ) return;
        if ( current.getData() != null ) {
            map.put(current.getAddress(), current.getData());
        }
        for ( GenericTreeNode<T> child : current.getChildren().values() ) {
            collectAddressData(child, map);
        }
    }
    
    
    /**
     * Recursively calculate size of tree depth-first
     * 
     * @param current
     * @return int
     */
    private int sizeRecursive(GenericTreeNode<T> current) {
        if ( current == null ) return 0;
        int count = 1;
        for ( GenericTreeNode<T> child : current.getChildren().values() ) {
            count += sizeRecursive(child);
        }
        return count;
    }
    
    
    /**
     * Recursively traverse tree depth first to find node value matching query
     * 
     * @param current
     * @param query
     * @return {@link GenericTreeNode}-T
     */
    private GenericTreeNode<T> recursiveFindByData(GenericTreeNode<T> current, T query) {
        if ( current == null ) return null;
        if ( query.equals(current.getData()) ) return current;
        
        for ( GenericTreeNode<T> child : current.getChildren().values() ) {
            GenericTreeNode<T> found = recursiveFindByData(child, query);
            if ( found != null ) {
                return found;
            }
        }
        return null;
    }

    
    /**
     * Recursively descend provided path to remove child at terminal address
     * 
     * @param current
     * @param parts
     * @return boolean
     */
    private boolean recursiveRemove(GenericTreeNode<T> current, String[] parts) {
        if (parts.length == 0) return false;
        GenericTreeNode<T> child = current.getChildren().get(parts[0]);
        if (child == null) return false;

        if (parts.length == 1) {
            // Remove child
            return current.removeChild(parts[0]);
        } else {
            return recursiveRemove(child, new String[]{parts[1]});
        }
    }
    
    
    /**
     * Recursively descend provided path to find child at terminal address
     * 
     * @param current
     * @param path
     * @return {@link GenericTreeNode}-T
     */
    private GenericTreeNode<T> recursiveGetByAddress(GenericTreeNode<T> current, String path) {
        
        // Handle 
        if (path.isEmpty()) {
            return current;
        }
        
        String[] addressArr = path.split(Pattern.quote(this.delimiter), 2);
        GenericTreeNode<T> child = current.getChildren().get(addressArr[0]);
        
        if ( child == null ) return null;
        if ( addressArr.length == 1 ) return child;
        return recursiveGetByAddress(child, addressArr[1]);
    }
    
    
    /**
     * Recursively descend provided path to add a new child at terminal address
     * 
     * @param current
     * @param path
     * @param data
     */
    private void recursiveAdd(GenericTreeNode<T> current, String path, T data) {
        
        // Base case
        String[] addressArr = path.split(Pattern.quote(this.delimiter), 2);
        String key = addressArr[0];
        
        // Set current address
        String currentAddress = current.getAddress().isEmpty() ? key : current.getAddress() + delimiter + key;
        current.getChildren().putIfAbsent(key, new GenericTreeNode<>(null, currentAddress) );
        GenericTreeNode<T> child = current.getChildren().get(key);
        child.setParent(current);
        
        // 
        if (addressArr.length == 1) {
            child.setData(data);
        }
        
        // Descend down path via query
        else {
            recursiveAdd(child, addressArr[1], data);
        }
    }

    
    /**
     * Substring provided path up to a flag using the delimiter of the tree
     * 
     * @param path
     * @param flag
     * @return String
     */
    public String subStringPathToFlag(String path, String flag) {
        String[] pathArr = path.split(Pattern.quote(this.delimiter));
        StringBuilder result = new StringBuilder();
        
        int i = 0;
        while (i < pathArr.length && !pathArr[i].equals(flag)) {
            if (i > 0) {
                result.append(this.delimiter);
            }
            result.append(pathArr[i]);
            i++;
        }
        if (i < pathArr.length) {
            if (i > 0) {
                result.append(this.delimiter);
            }
            result.append(pathArr[i]); // add the flag itself
        }
        return result.toString();
    }
}
