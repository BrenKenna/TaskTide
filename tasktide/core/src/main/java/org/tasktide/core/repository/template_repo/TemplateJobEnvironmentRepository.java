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
package org.tasktide.core.repository.template_repo;

import jakarta.nosql.Template;

import org.tasktide.core.model.job_env.JobEnvironment;
import org.tasktide.core.repository.TemplateRepository;


/**
 * NoSQL orientated repository for {@link JobEnvironment}
 *
 * @author Brendan Kenna
 */
public class TemplateJobEnvironmentRepository extends TemplateRepository<JobEnvironment> {
    
    
    /**
     * Constructs {@link JobEnvironment} repository for the same
     * 
     * @param template
     * @param collectionName 
     */
    public TemplateJobEnvironmentRepository(
        Template template,
        String collectionName
    ) {
        super(template, JobEnvironment.class, collectionName);
    }
}