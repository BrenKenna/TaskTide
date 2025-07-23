/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.rocksdb;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.repository.RocksDbRepository;
import org.tasktide.itemstore.ItemStore;


/**
 * {@link WorkItem} {@link TaskTideRepository} backed by RocksDB
 * 
 * @author bkenna
 */
public class RocksDbWorkItemRepository extends RocksDbRepository<WorkItem> {
    
    
    /**
     * Construct {@link WorkItem} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @param collectionName
     */
    public RocksDbWorkItemRepository(
       ItemStore itemStore,
       @ConfigProperty(name = "task-tide.core.repository.rocksdb.collection.workitem.name", defaultValue = "WorkItem") String collectionName
    ) {
        super(itemStore, WorkItem.class, collectionName);
    }
}
