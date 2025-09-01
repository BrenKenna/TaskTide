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

import java.util.List;

import jakarta.nosql.Template;
import jakarta.persistence.EntityManager;
import org.tasktide.itemstore.ItemStore;

import org.tasktide.core.TaskTideRepository;
import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;
import org.tasktide.core.repository.jpa_repo.JpaStepRepository;
import org.tasktide.core.repository.jpa_repo.JpaWorkItemRepository;
import org.tasktide.core.repository.jpa_repo.JpaWorkflowRepository;

import org.tasktide.core.repository.nosql_repo.TemplateStepRepository;
import org.tasktide.core.repository.nosql_repo.TemplateWorkItemRepository;
import org.tasktide.core.repository.nosql_repo.TemplateWorkflowRepository;

import org.tasktide.core.repository.itemstore_repo.ItemStoreStepRepository;
import org.tasktide.core.repository.itemstore_repo.ItemStoreWorkItemRepository;
import org.tasktide.core.repository.itemstore_repo.ItemStoreWorkflowRepository;

import org.tasktide.core.repository.json_repo.JsonStepRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;
import org.tasktide.core.repository.json_repo.JsonWorkflowRepository;
import org.tasktide.itemstore.ItemStoreType;


/**
 * Enum of valid repository types
 *
 * @author bkenna
 */
public enum RepositoryType {

    NOSQL {
        @Override
        public boolean isRepository(String query) {
            return "nosql".equals(query.toLowerCase());
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return NOSQL == query;
        }

        @Override
        public String toString() {
            return "NoSQL";
        }

        /**
         * Create targeted {@link TaskTideRepository}-{@link TemplateRepository}
         * of {@link Workflow}, {@link Step}, {@link WorkItem}
         *
         * @param <T>
         * @param modelType
         * @param backend
         * @param collectionName
         *
         * @return {@link TemplateRepository}-{@link TaskTideModel of {@link Workflow}, {@link Step}, {@link WorkItem}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends TaskTideModel<T>> TemplateRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName) {
            Template template = (Template) backend;

            if (modelType.equals(Workflow.class)) {
                return (TemplateRepository<T>) new TemplateWorkflowRepository(template, collectionName);
            } else if (modelType.equals(Step.class)) {
                return (TemplateRepository<T>) new TemplateStepRepository(template, collectionName);
            } else if (modelType.equals(WorkItem.class)) {
                return (TemplateRepository<T>) new TemplateWorkItemRepository(template, collectionName);
            } else {
                throw new IllegalArgumentException("Unsupported model type for Template repository: " + modelType.getSimpleName());
            }
        }
    },
    
    JSON {
        @Override
        public boolean isRepository(String query) {
            return "json".equals(query.toLowerCase());
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return JSON == query;
        }

        @Override
        public String toString() {
            return "JSON";
        }

        /**
         * Create targeted {@link TaskTideRepository}-{@link JsonRepository} of
         * {@link Workflow}, {@link Step}, {@link WorkItem}
         *
         * @param <T>
         * @param modelType
         * @param backend
         * @param collectionName
         *
         * @return {@link JsonRepository}-{@link TaskTideModel of {@link Workflow}, {@link Step}, {@link WorkItem}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends TaskTideModel<T>> JsonRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName) {
            if (modelType.equals(Workflow.class)) {
                return (JsonRepository<T>) new JsonWorkflowRepository((List<Workflow>) backend, collectionName);
            } else if (modelType.equals(Step.class)) {
                return (JsonRepository<T>) new JsonStepRepository((List<Step>) backend, collectionName);
            } else if (modelType.equals(WorkItem.class)) {
                return (JsonRepository<T>) new JsonWorkItemRepository((List<WorkItem>) backend, collectionName);
            } else {
                throw new IllegalArgumentException("Unsupported model type for JSON repository: " + modelType.getSimpleName());
            }
        }
    },
    
    ITEMSTORE {
        @Override
        public boolean isRepository(String query) {
            return ItemStoreType.hasQuery(query);
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return ITEMSTORE == query;
        }

        @Override
        public String toString() {
            return "Item Store";
        }

        /**
         * Create targeted {@link TaskTideRepository}-{@link JsonRepository} of
         * {@link Workflow}, {@link Step}, {@link WorkItem}
         *
         * @param <T>
         * @param modelType
         * @param backend
         * @param collectionName
         *
         * @return {@link JsonRepository}-{@link TaskTideModel of {@link Workflow}, {@link Step}, {@link WorkItem}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends TaskTideModel<T>> ItemStoreRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName) {
            ItemStore data = (ItemStore) backend;
            if (modelType.equals(Workflow.class)) {
                return (ItemStoreRepository<T>) new ItemStoreWorkflowRepository(data, collectionName);
            } else if (modelType.equals(Step.class)) {
                return (ItemStoreRepository<T>) new ItemStoreStepRepository(data, collectionName);
            } else if (modelType.equals(WorkItem.class)) {
                return (ItemStoreRepository<T>) new ItemStoreWorkItemRepository(data, collectionName);
            } else {
                throw new IllegalArgumentException("Unsupported model type for RocksDb repository: " + modelType.getSimpleName());
            }
        }
    },
    
    SQL {
        @Override
        public boolean isRepository(String query) {
            return "sql".equals(query.toLowerCase());
        }

        @Override
        public boolean isRepository(RepositoryType query) {
            return SQL == query;
        }

        @Override
        public String toString() {
            return "SQL";
        }

        /**
         * Create targeted {@link TaskTideRepository}-{@link JsonRepository} of
         * {@link Workflow}, {@link Step}, {@link WorkItem}
         *
         * @param <T>
         * @param modelType
         * @param backend
         * @param collectionName
         *
         * @return {@link JpaRepository}-{@link TaskTideModel of {@link Workflow}, {@link Step}, {@link WorkItem}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends TaskTideModel<T>> TaskTideRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName) {
            EntityManager entityManager = (EntityManager) backend;

            if (modelType.equals(Workflow.class)) {
                return (JpaRepository<T>) new JpaWorkflowRepository(entityManager, collectionName);
            } else if (modelType.equals(Step.class)) {
                return (JpaRepository<T>) new JpaStepRepository(entityManager, collectionName);
            } else if (modelType.equals(WorkItem.class)) {
                return (JpaRepository<T>) new JpaWorkItemRepository(entityManager, collectionName);
            } else {
                throw new IllegalArgumentException("Unsupported model type for Template repository: " + modelType.getSimpleName());
            }
        }
    };

    /**
     * Compare to queried repository string
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isRepository(String query);

    /**
     * Compare to queried to repository
     *
     * @param query
     * @return boolean
     */
    public abstract boolean isRepository(RepositoryType query);

    /**
     * Represent value as a string
     *
     * @return String
     */
    @Override
    public abstract String toString();

    
    /**
     * Abstract method to allow each enumeration to define how to create
     * targeted {@link TaskTideRepository} of
     * {@link Workflow}, {@link Step}, {@link WorkItem}. For for
     * {@link TemplateRepository}, {@link ItemStoreRepository}, or
     * {@link JsonRepository}
     *
     * @param <T>
     * @param modelType
     * @param backend
     * @param collectionName
     *
     * @return {@link TaskTideRepository} of
     * {@link Workflow}, {@link Step}, {@link WorkItem}
     */
    public abstract <T extends TaskTideModel<T>>
            TaskTideRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName);


    /**
     * Return index of queried state
     *
     * @param query
     * @return int
     */
    public static int indexOf(String query) {

        // Initialize values
        int repoInd = -1;
        int limit = RepositoryType.values().length;
        int counter = 0;

        // Search until found
        while (counter <= limit && repoInd < 0) {
            RepositoryType repoType = RepositoryType.values()[counter];
            if (repoType.isRepository(query)) {
                repoInd = counter;
            } else {
                counter++;
            }
        }

        // Return search result
        return repoInd;
    }

    /**
     * Check if queried type exists
     *
     * @param query
     * @return boolean
     */
    public static boolean hasType(String query) {
        return indexOf(query) >= 0;
    }
    
    
    /**
     * Map query to a RepositoryType
     * 
     * @param query
     * @return RepositoryType
     */
    public static RepositoryType get(String query) {
        int ind = indexOf(query);
        if ( ind > -1 ) {
            return values()[ind];
        }
        return null;
    }
}