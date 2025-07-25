/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.jpa_repo;

import jakarta.persistence.EntityManager;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.repository.JpaRepository;


/**
 * Persistence of {@link Workflow}
 * 
 * @author bkenna
 */
public class JpaWorkflowRepository extends JpaRepository<Workflow> {
    
    
    /**
     * Constructs Workflow Repository {@link TaskTideRepository}
     * 
     * @param backend
     * @param collectionName 
     */
    public JpaWorkflowRepository(EntityManager backend, String collectionName) {
        super(backend, Workflow.class, collectionName);
    }
}
