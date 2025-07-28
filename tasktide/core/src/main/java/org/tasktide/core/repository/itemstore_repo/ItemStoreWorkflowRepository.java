/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.itemstore_repo;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.repository.ItemStoreRepository;
import org.tasktide.itemstore.ItemStore;


/**
 * {@link Workflow} {@link TaskTideRepository} backed by RocksDB
 * 
 * @author bkenna
 */
public class ItemStoreWorkflowRepository extends ItemStoreRepository<Workflow> {
    
    /**
     * Construct {@link Workflow} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @param collectionName
     */
    public ItemStoreWorkflowRepository(
       ItemStore itemStore,
       @ConfigProperty(name = "task-tide.core.repository.rocksdb.collection.workflow.name", defaultValue = "Workflow") String collectionName
    ) {
        super(itemStore, Workflow.class, collectionName);
    }
}
