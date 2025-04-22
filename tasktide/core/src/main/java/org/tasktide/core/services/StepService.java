/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.tasktide.core.model.collection.Step;

import org.tasktide.core.supporting.Utils;
import org.tasktide.core.repository.TaskTideRepository;


/**
 *
 * Service to provide Step interactions to backend DB
 * 
 * @author bkenna
 */
@Dependent
public class StepService {
    
    // Attributes
    private final TaskTideRepository<Step> repo;
    private final Utils utils;
    
    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    @Inject
    public StepService(TaskTideRepository<Step> repo) {
        this.repo = repo;
        this.utils = new Utils();
    }
}
