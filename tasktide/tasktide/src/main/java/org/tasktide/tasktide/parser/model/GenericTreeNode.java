/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.parser.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public T getData() {
        return this.data;
    }
    
    public boolean isEdge() {
        return this.children == null;
    }
    
    public boolean hasChild(String query) {
        return this.children.containsKey(query);
    }
    
    public Set<String> listChildren() {
        return this.children.keySet();
    }
    
    public Map<String, GenericTreeNode<T>> fetchChildren() {
        return this.children;
    }
    
    
    public boolean isRoot() {
        return this.parent == null;
    }
    
    
    public void addChild(String path, String delim, String query, T data) {
        // String temp = query.toString();
        String[] addressArr = query.split(delim);
        
        if (addressArr.length == 1) {
            GenericTreeNode<T> node = new GenericTreeNode<>(data, path);
            node.parent = this;
            this.children.put(addressArr[0], node);
        }
        
        String childPath = Arrays.asList(addressArr).subList(0, addressArr.length).toString();
        GenericTreeNode<T> next = this.children.get(addressArr[1]);
        
        next.addChild(path, delim, childPath, data);
    }
    
    
    public T getChild(String delim, String query) {
        String[] addressArr = query.split(delim);
        
        if (addressArr.length == 1) {
            if ( this.address.equals(query) ) {
                 return this.data;
            }
        }
        
        String childPath = Arrays.asList(addressArr).subList(0, addressArr.length).toString();
        GenericTreeNode<T> next = this.children.get(addressArr[1]);
        
        return next.getChild(delim, childPath);
    }
}
