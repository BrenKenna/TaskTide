/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.rocksdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.repository.RocksDbRepository;

import org.tasktide.itemstore.ItemStore;


/**
 * {@link Workflow} {@link TaskTideRepositor} backed by RocksDB
 * 
 * @author bkenna
 */
@ApplicationScoped
public class WorkflowRocksRepository extends RocksDbRepository<Workflow> {
    
    
    /**
     * Construct {@link Workflow} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @param collectionName
     */
    @Inject
    public WorkflowRocksRepository(
       ItemStore itemStore,
       @ConfigProperty(name = "task-tide.core.repository.rocksdb.collection.workflow.name", defaultValue = "Workflow") String collectionName
    ) {
        super(itemStore, Workflow.class, collectionName);
    }
}
