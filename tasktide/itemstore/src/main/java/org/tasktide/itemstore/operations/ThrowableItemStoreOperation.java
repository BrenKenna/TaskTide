/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.itemstore.operations;

import org.tasktide.itemstore.exceptions.ItemStoreCheckedException;


/**
 * Functional interface for defining operations
 *   against {@link ItemStore}
 * 
 * @author Bren
 * @param <R> 
 */
@FunctionalInterface
public interface ThrowableItemStoreOperation<R> {
    
    
    /**
     * Executes provided operation throwing
     *  {@link ItemStoreCheckedException}
     * 
     * @return R
     * @throws ItemStoreUncheckedException 
     */
    R execute() throws ItemStoreCheckedException;
}
