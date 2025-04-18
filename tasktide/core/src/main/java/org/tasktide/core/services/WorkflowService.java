/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.repository.ModelRepository;
import org.tasktide.core.supporting.Utils;


/**
 *
 * Service to provide WorkflowService interactions to backend DB
 * 
 * @author bkenna
 */
@Dependent
public class WorkflowService {
    
    // Attributes
    private final ModelRepository<Workflow> repo;
    private final Utils utils;
    
    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    @Inject
    public WorkflowService(ModelRepository<Workflow> repo) {
        this.repo = repo;
        this.utils = new Utils();
    }

    
    public ModelRepository<Workflow> getRepo() {
        return repo;
    }
}
