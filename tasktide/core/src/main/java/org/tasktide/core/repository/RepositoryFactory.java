/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;


/**
 *
 * @param <T> of {@link TaskTideModel}-{@link Workflow}, {@link Step}, {@lnik WorkItem}
 * 
 * @author bkenna
 */
public class RepositoryFactory<T extends TaskTideModel<T>> {
    
    // Attributes
    private final String collectionName;
    private final Class<T> ofModel;
    private final Object backend;
    private final RepositoryType repoType;
    
    
    /**
     * Constructed because no arguments are optional
     * 
     * @param collectionName
     * @param ofModel
     * @param backend 
     * @param repoType 
     */
    public RepositoryFactory(String collectionName, Class<T> ofModel, Object backend, RepositoryType repoType) {
        this.collectionName = collectionName;
        this.ofModel = ofModel;
        this.backend = backend;
        this.repoType = repoType;
    }
    
    
    /**
     * Make the required {@link TaskTideRepository} of type 
     *  {@link Workflow}, {@link Step}, {@link WorkItem} with
     *  required backend {@link RepositoryType}
     * 
     * @return {@link TaskTideRepository} for {@link TaskTideModel}
     */
    public TaskTideRepository<T> make() {
        return this.repoType.createRepository(this.ofModel, this.backend, this.collectionName);
    }
}