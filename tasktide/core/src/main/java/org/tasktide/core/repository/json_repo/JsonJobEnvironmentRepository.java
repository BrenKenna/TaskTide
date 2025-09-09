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

import java.util.List;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.repository.JsonRepository;


/**
 * JSON orientated repository for {@link JobEnvironment}
 *
 * @author Brendan Kenna
 */
public class JsonJobEnvironmentRepository extends JsonRepository<JobEnvironment> {
    
    
    /**
     * Constructs {@link JobEnvironment} repository for the same
     * 
     * @param modelCollection
     * @param collectionName 
     */
    public JsonJobEnvironmentRepository(
        List<JobEnvironment> modelCollection,
        String collectionName
    ) {
        super(modelCollection, JobEnvironment.class, collectionName);
    }
}