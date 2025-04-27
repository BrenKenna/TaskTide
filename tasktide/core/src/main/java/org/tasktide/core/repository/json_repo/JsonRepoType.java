/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import java.util.ArrayList;
import java.util.List;

import org.tasktide.core.repository.JsonRepository;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * Enum to simplify importing file repository
 * 
 * @author bkenna
 */
public enum JsonRepoType {
    
    WORKITEM {
        @Override
        public JsonRepository<WorkItem> fetchRepo(String path) {
            List<WorkItem> data = new ArrayList<>();
            JsonWorkItemRepository repo = new JsonWorkItemRepository(data, path);
            repo.extendModel(repo.load());
            return repo;
        }
    },
    
    STEP {
        @Override
        public JsonRepository<Step> fetchRepo(String path) {
            List<Step> data = new ArrayList<>();
            JsonStepRepository repo = new JsonStepRepository(data, path);
            repo.extendModel(repo.load());
            return repo;
        }
    },
    
    WORKFLOW {
        @Override
        public JsonRepository<Workflow> fetchRepo(String path) {
            List<Workflow> data = new ArrayList<>();
            JsonWorkflowRepository repo = new JsonWorkflowRepository(data, path);
            repo.extendModel(repo.load());
            return repo;
        }
    };
    
    
    /**
     * 
     * @param path
     * @return 
     */
    public abstract JsonRepository fetchRepo(String path);
}
