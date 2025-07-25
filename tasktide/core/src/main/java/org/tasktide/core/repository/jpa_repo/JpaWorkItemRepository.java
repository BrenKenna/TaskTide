/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.jpa_repo;

import jakarta.persistence.EntityManager;

import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.JpaRepository;


/**
 * Persistence of {@link WorkItem}
 * 
 * @author bkenna
 */
public class JpaWorkItemRepository extends JpaRepository<WorkItem> {
    
    
    /**
     * Constructs {@link WorkItem} {@link TaskTideRepository}
     * 
     * @param backend
     * @param collectionName 
     */
    public JpaWorkItemRepository(EntityManager backend, String collectionName) {
        super(backend, WorkItem.class, collectionName);
    }
}