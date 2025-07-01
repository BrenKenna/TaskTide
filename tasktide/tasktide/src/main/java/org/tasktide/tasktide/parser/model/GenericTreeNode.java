/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.parser.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author bkenna
 */
public class GenericTreeNode<T> {
    
    // Attributes
    private final T data;
    private String address;
    private GenericTreeNode<T> parent;
    private Map<String, GenericTreeNode<T>> children;
    
    public GenericTreeNode(T data, String address) {
        this.data = data;
        this.address = address;
        this.children = new HashMap<>();
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public T getData() {
        return this.data;
    }
    
    public boolean isEdge() {
        return this.children.isEmpty();
    }
    
    public boolean hasChild(String query) {
        return this.children.containsKey(query);
    }
    
    public Set<String> listChildren() {
        return this.children.keySet();
    }
    
    public boolean removeChild(String key) {
        if (children.containsKey(key)) {
            children.remove(key);
            return true;
        }
        return false;
    }
    
    public void clear() {
        children.clear();;
    }
    
    
    
    public Map<String, GenericTreeNode<T>> fetchChildren() {
        return this.children;
    }
    
    public boolean isRoot() {
        return this.parent == null;
    }
    
    public void setParent(GenericTreeNode<T> newParent) {
        this.parent = newParent;
    }
    
    public void setPointerNode(String segmentKey, String path) {
        GenericTreeNode<T> ptrNode = new GenericTreeNode<>(null, path);
        this.children.putIfAbsent(segmentKey, ptrNode);
        ptrNode.parent = this;
    } 
    
    
    public void addChild(String path, String delim, T data) {
        this.recursiveAdd(path, delim, path, data);
    }
    
    
    public GenericTreeNode<T> findByAddress(String path, String delim) {
        return this.recursiveGetAddressData(path, delim, path);
    }
    
    public T getDataForAddress(String path, String delim) {
        return this.findByAddress(path, delim).getData();
    }
    
    
    public Map<String, GenericTreeNode<T>> getChildrenAtAddress(String path, String delim) {
        GenericTreeNode<T> node = this.findByAddress(path, delim);
        return node != null ? node.children : null;
    }
    
    
    public Map<String, T> getChildrenDataAtAddress(String path, String delim) {
        GenericTreeNode<T> node = this.findByAddress(path, delim);
        if (node == null || node.children == null) return null;
        
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, GenericTreeNode<T>> entry : node.children.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getData());
        }
        
        return result;
    }
    
    
    public GenericTreeNode<T> findByData(T query) {
        
        // Base case
        if ( this.data != null && this.data.equals(query)) {
            return this;
        }
        
        // Search depth-first
        for ( GenericTreeNode<T> child : this.children.values() ) {
            GenericTreeNode<T> result = child.findByData(query);
            if ( result != null ) {
                return result;
            }
        }
        
        // Otherwise null
        return null;
    }
    
    
    public List<GenericTreeNode<T>> getPathToRoot() {
        List<GenericTreeNode<T>> path = new ArrayList<>();
        GenericTreeNode<T> current = this;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
    
    
    public boolean removeByAddress(String address, String delim) {
        String[] addressArr = address.split(Pattern.quote(delim), 2);
        
        if ( addressArr.length == 1 ) {
            return this.removeChild(addressArr[0]);
        }
        
        // Recursive descent
        GenericTreeNode<T> next = this.children.get(addressArr[0]);
        if ( next != null ) {
            return next.removeByAddress(addressArr[1], delim);
        }
        else {
            return false;
        }
    }

    
    private GenericTreeNode<T> recursiveGetAddressData(String path, String delim, String query) {
        String[] addressArr = query.split(delim, 2);

        // Base case
        if ( addressArr.length == 1 ) {
            
            // Return data if address matches
            if ( this.address.equals(query) ) {
                return this;
            }
            
            // Otherwise null
            return null;
        }
        
        // Handle descending path
        else {
            
            // Fetch next node
            GenericTreeNode<T> next = this.children.get(addressArr[0]);
            
            // Return null if empty
            if ( next == null ) {
                return null;
            }
            
            // Proceeding query child
            else {
                return next.recursiveGetAddressData(path, delim, addressArr[1]);
            }
        }
    }
    
    
    private void recursiveAdd(String path, String delim, String query, T data) {
        
        // Base case
        String[] addressArr = query.split(delim, 2);
        if (addressArr.length == 1) {
            GenericTreeNode<T> node = new GenericTreeNode<>(data, path);
            node.parent = this;
            this.children.put(addressArr[0], node);
        }
        
        // Descend down path via query
        else {
            String currentPath = this.subStringPathToFlag(path, addressArr[0], delim);
            this.setPointerNode(addressArr[0], currentPath);
            GenericTreeNode<T> next = this.children.get(addressArr[0]);
            
            if ( next != null ) {
                next.recursiveAdd(path, delim, addressArr[1], data);
            }
        }
    }

    
    
    private String subStringPathToFlag(String path, String flag, String delim) {
        String[] pathArr = path.split(Pattern.quote(delim));
        StringBuilder result = new StringBuilder();
        
        int i = 0;
        while (i < pathArr.length && !pathArr[i].equals(flag)) {
            if (i > 0) {
                result.append(delim);
            }
            result.append(pathArr[i]);
            i++;
        }
        if (i < pathArr.length) {
            if (i > 0) {
                result.append(delim);
            }
            result.append(pathArr[i]); // add the flag itself
        }
        return result.toString();
    }
}
