/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.itemstore.operations;

import org.tasktide.itemstore.exceptions.ItemStoreUncheckedException;


/**
 * Functional interface for defining operations
 *   against {@link ItemStore} throwing {@link ItemStoreUncheckedException}
 * 
 * @author Bren
 * @param <R> 
 */
@FunctionalInterface
public interface ItemStoreOperation<R> {
    
    
    /**
     * Executes provided operation throwing
     *  {@link ItemStoreUncheckedException}
     * 
     * @return R
     * @throws ItemStoreUncheckedException 
     */
    R execute() throws ItemStoreUncheckedException;
}
