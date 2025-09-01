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
