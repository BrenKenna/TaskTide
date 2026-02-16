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

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.TemplateRepository;


/**
 * Step repository
 * 
 * @author bkenna
 */
public class TemplateStepRepository extends TemplateRepository<Step> {
    
    /**
     * Construct StepRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName step.repo-name
     */
    public TemplateStepRepository(Template template,
       String collectionName
    ) {
        super(template, Step.class, collectionName);
    }
}