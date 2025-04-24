/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.services;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.List;


import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideService;
import org.tasktide.core.TaskTideMapper;

import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Service to provide WorkflowService interactions to backend DB
 * 
 * @author bkenna
 */
@Dependent
public class WorkflowService implements TaskTideMapper<Workflow, Step>, TaskTideService {
    
    // Attributes
    private final TaskTideRepository<Workflow> repo;
    private final Utils utils;
    
    
    /**
     * Construct with repo for testing
     * 
     * @param repo 
     */
    @Inject
    public WorkflowService(TaskTideRepository<Workflow> repo) {
        this.repo = repo;
        this.utils = new Utils();
    }

    @Override
    public List<Step> getThroughLink(TaskTideRepository<Step> mappingRepo, Workflow model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
