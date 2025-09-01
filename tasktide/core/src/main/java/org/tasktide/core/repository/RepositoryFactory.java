/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.core.repository;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.TaskTideRepository;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.workitem.WorkItem;


/**
 *
 * @param <T> of {@link TaskTideModel}-{@link Workflow}, {@link Step}, {@link WorkItem}
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