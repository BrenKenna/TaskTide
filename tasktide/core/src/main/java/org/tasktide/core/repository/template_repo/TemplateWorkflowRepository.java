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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.collection.Workflow;

import org.tasktide.core.repository.TemplateRepository;

import org.tasktide.core.TaskTideModel;


/**
 * Workflow repository
 * 
 * @author bkenna
 */
public class TemplateWorkflowRepository extends TemplateRepository<Workflow> {
    
    /**
     * Construct WorkflowRepository with injectable template and configurable collection name
     * 
     * @param template
     * @param collectionName workflow.repo-name
     */
    public TemplateWorkflowRepository(
        Template template,
        String collectionName
    ) {
        super(template, Workflow.class, collectionName);
    }
    
    
    
    /**
     * Fetch WorkItem by its Id
     * 
     * @param id
     * @return WorkItem
     */
    @Override
    public Optional<Workflow> findById(String id) {
        Optional<Workflow> result = template
            .find(this.COLLECTION_CLASS, id);
        if ( result.isPresent() ) {
            Workflow value = result.get();
            value = this.hydrateWorkflow(value);
            return Optional.of(value);
        }
        return result;
    }

    
    /**
     * Insert model into DB
     * 
     * @param model
     * @return T-Model
     */
    @Override
    public Workflow insertModel(Workflow model) {
        model.deHydrateSteps();
        Workflow result = template.insert(model);
        return hydrateWorkflow(result);
    }
    
    
    /**
     * Batch import provided list of records
     * 
     * @param toAdd
     * @return boolean
     */
    @Override
    public boolean extendModel(List<Workflow> toAdd) {
        toAdd
            .parallelStream()
            .forEach(Workflow::hydrateSteps);
        Iterable<Workflow> imported = template.insert(toAdd);
        return imported != null;
    }

    
    /**
     * Update model
     * 
     * @param model
     * @return T-Model
     */
    @Override
    public Workflow updateModel(Workflow model) {
        model.deHydrateSteps();
        Workflow result = template.update(model);
        return hydrateWorkflow(result);
    }

    
    /**
     * Delete model if present
     * 
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteModel(String id) {
        template.delete(COLLECTION_CLASS, id);
        return findById(id).isEmpty();
    }

    
    /**
     * Generic method to find list of WorkItems by field equally value
     * 
     * @param field
     * @param value
     * @return List-WorkItem
     */
    @Override
    public List<Workflow> findByField(String field, Object value) {
        List<Workflow> results = template.select(COLLECTION_CLASS)
                .where(field)
                .eq(value)
            .result();
        return hydrateList(results);
    }

    
    /**
     * Filters records with provided {@link CustomAnnotation}
     * 
     * @param anno
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<Workflow> filterByAnnotation(CustomAnnotation anno) {
        List<Workflow> results = this.findAll()
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().queriedFieldsMatch(anno)
                : false
            )
        .collect(Collectors.toList());
        return hydrateList(results);
    }
    
    
    /**
     * Filters records with provided annotation key and value
     * 
     * @param key
     * @param value
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<Workflow> filterByAnnotation(String key, Object value) {
        List<Workflow> results = this.findAll()
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().getKey(key).equals(value)
                : false
            )
        .collect(Collectors.toList());
        return hydrateList(results);
    }
    
    
    /**
     * Filter records which have provided annotation key
     * 
     * @param key
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<Workflow> hasAnnotationField(String key) {
        List<Workflow> results = this.findAll()
            .stream()
            .parallel()
            .filter( elm ->
                elm.getAnnotations() != null
                ? elm.getAnnotations().hasKey(key)
                : false
            )
        .collect(Collectors.toList());
        return hydrateList(results);
    }
    
    
    /**
     * Extends collection, state query with annotation filtering
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @param annoKey
     * @param annoValue
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<Workflow> findByFieldForGroupWithAnno(
            String field, Object value, String group,
            Object groupVal, String annoKey, Object annoValue
    ) {
        List<Workflow> results = this.findByFieldForGroup(field, value, group, groupVal)
            .stream()
            .parallel()
            .filter( elm -> 
                elm.getAnnotations() != null
                ? elm.getAnnotations().getKey(annoKey).equals(annoValue)
                : false
            )
        .collect(Collectors.toList());
        return hydrateList(results);
    }
    
    
    /**
     * Extends collection, state query with annotation filtering
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @param anno
     * @return List-{@link TaskTideModel}
     */
    @Override
    public List<Workflow> findByFieldForGroupWithAnno(String field, Object value, String group, Object groupVal, CustomAnnotation anno) {
        List<Workflow> results = this.findByFieldForGroup(field, value, group, groupVal)
            .stream()
            .parallel()
            .filter( elm -> 
                elm.getAnnotations() != null
                ? elm.getAnnotations().queriedFieldsMatch(anno)
                : false
            )
        .collect(Collectors.toList());
        return hydrateList(results);
    }
    
    
    /**
     * Find {@link TaskTideModel} from backend with field and group
     *  having specified value. Step = Name, State = ToDo
     * 
     * @param field
     * @param value
     * @param group
     * @param groupVal
     * @return List-{@link TaskTideModel} 
     */
    @Override
    public List<Workflow> findByFieldForGroup(String field, Object value, String group, Object groupVal) {
        List<Workflow> results = template.select(COLLECTION_CLASS)
            .where(field)
            .eq(value)
            .and(group)
            .eq(groupVal)
        .result();
        return hydrateList(results);
    }
    
    
    /**
     * Fetch all records
     * 
     * @return List-T-Model
     */
    @Override
    public List<Workflow> findAll() {
        List<Workflow> results = template.select(COLLECTION_CLASS).result();
        return hydrateList(results);
    }
    
    
    /**
     * Hydrate {@link Workflow} collection for application
     *  view from Template backend
     * 
     * @param workflows
     * @return List-{@link Workflow}
     */
    private List<Workflow> hydrateList(List<Workflow> workflows) {
        if ( workflows != null ) {
            if ( ! workflows.isEmpty() ) {
                workflows
                    .parallelStream()
                    .forEach(Workflow::hydrateSteps);
            }
        }
        return workflows;
    }
    
    
    /**
     * Hydrate provided {@link Workflow}
     * 
     * @param workflow
     * 
     * @return {@link Workflow}
     */
    private Workflow hydrateWorkflow(Workflow workflow) {
        workflow.hydrateSteps();
        return workflow;
    }
}