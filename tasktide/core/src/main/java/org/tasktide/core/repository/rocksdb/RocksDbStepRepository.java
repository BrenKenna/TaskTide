/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.rocksdb;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.RocksDbRepository;
import org.tasktide.itemstore.ItemStore;


/**
 * {@link Step} {@link TaskTideRepository} backed by RocksDB
 * 
 * @author bkenna
 */
public class RocksDbStepRepository extends RocksDbRepository<Step> {
    
    /**
     * Construct {@link Step} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @param collectionName
     */
    public RocksDbStepRepository(
       ItemStore itemStore,
       String collectionName
    ) {
        super(itemStore, Step.class, collectionName);
    }
}
