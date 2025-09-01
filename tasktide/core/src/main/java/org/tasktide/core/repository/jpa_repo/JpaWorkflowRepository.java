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
