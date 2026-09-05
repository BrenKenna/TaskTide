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
package org.tasktide.api.security_context.service_hooks.template_repositories;

import jakarta.nosql.Template;

import org.tasktide.core.repository.TemplateRepository;

import org.tasktide.api.security_context.data_models.WorkItemResourcePermission;


/**
 * NoSQL orientated repository for {@link WorkItemResourcePermission}
 *
 * @author Brendan Kenna
 */
public class TemplateWorkItemResourcePermissionRepository extends TemplateRepository<WorkItemResourcePermission> {
    
    
    /**
     * Constructs {@link WorkItemResourcePermission} repository for the same
     * 
     * @param template
     * @param collectionName 
     */
    public TemplateWorkItemResourcePermissionRepository(
        Template template,
        String collectionName
    ) {
        super(template, WorkItemResourcePermission.class, collectionName);
    }
}