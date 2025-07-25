/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.jpa_repo;

import jakarta.persistence.EntityManager;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.JpaRepository;


/**
 * Persistence of {@link Step}
 * 
 * @author bkenna
 */
public class JpaStepRepository extends JpaRepository<Step> {
    
    
    /**
     * Constructs {@link Step} {@link TaskTideRepository}
     * 
     * @param backend
     * @param collectionName 
     */
    public JpaStepRepository(EntityManager backend, String collectionName) {
        super(backend, Step.class, collectionName);
    }
}
