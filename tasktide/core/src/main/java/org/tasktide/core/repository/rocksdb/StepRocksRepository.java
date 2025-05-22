/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.rocksdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.tasktide.core.model.collection.Step;

import org.tasktide.core.repository.RocksDbRepository;

import org.tasktide.itemstore.ItemStore;


/**
 * {@link Step} {@link TaskTideRepository} backed by RocksDB
 * 
 * @author bkenna
 */
@ApplicationScoped
public class StepRocksRepository extends RocksDbRepository<Step> {
    
    
    /**
     * Construct {@link Step} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @throws Exception 
     */
    @Inject
    public StepRocksRepository(ItemStore itemStore) throws Exception {
        super(itemStore, Step.class);
    }
}
