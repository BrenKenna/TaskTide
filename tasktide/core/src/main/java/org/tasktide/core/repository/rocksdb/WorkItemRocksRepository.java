/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.rocksdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.RocksDbRepository;

import org.tasktide.itemstore.ItemStore;


/**
 * {@link WorkItem} {@link TaskTideRepository} backed by RocksDB
 * 
 * @author bkenna
 */
@ApplicationScoped
public class WorkItemRocksRepository extends RocksDbRepository<WorkItem> {
    
    
    /**
     * Construct {@link WorkItem} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @throws Exception 
     */
    @Inject
    public WorkItemRocksRepository(ItemStore itemStore) throws Exception {
        super(itemStore, WorkItem.class);
    }
}
