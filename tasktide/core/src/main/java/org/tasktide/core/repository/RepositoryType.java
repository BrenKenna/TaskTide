/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.tasktide.core.repository;

import java.util.List;
import jakarta.nosql.Template;


import org.tasktide.core.TaskTideRepository;

import org.tasktide.core.TaskTideModel;
import org.tasktide.core.model.collection.Workflow;
import org.tasktide.core.model.collection.Step;
import org.tasktide.core.model.workitem.WorkItem;

import org.tasktide.core.repository.nosql_repo.TemplateStepRepository;
import org.tasktide.core.repository.nosql_repo.TemplateWorkItemRepository;
import org.tasktide.core.repository.nosql_repo.TemplateWorkflowRepository;

import org.tasktide.itemstore.ItemStore;
import org.tasktide.core.repository.rocksdb.RocksDbStepRepository;
import org.tasktide.core.repository.rocksdb.RocksDbWorkItemRepository;
import org.tasktide.core.repository.rocksdb.RocksDbWorkflowRepository;

import org.tasktide.core.repository.json_repo.JsonStepRepository;
import org.tasktide.core.repository.json_repo.JsonWorkItemRepository;
import org.tasktide.core.repository.json_repo.JsonWorkflowRepository;


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
            String tmp = query.replace(" ", query).toLowerCase();
            return "itemstore".equals(tmp);
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
        public <T extends TaskTideModel<T>> RocksDbRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName) {
            ItemStore data = (ItemStore) backend;
            if (modelType.equals(Workflow.class)) {
                return (RocksDbRepository<T>) new RocksDbWorkflowRepository(data, collectionName);
            } else if (modelType.equals(Step.class)) {
                return (RocksDbRepository<T>) new RocksDbStepRepository(data, collectionName);
            } else if (modelType.equals(WorkItem.class)) {
                return (RocksDbRepository<T>) new RocksDbWorkItemRepository(data, collectionName);
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

        @Override
        public <T extends TaskTideModel<T>> TaskTideRepository<T> createRepository(Class<T> modelType, Object backend, String collectionName) {
            throw new UnsupportedOperationException("Not supported yet.");
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
     * {@link TemplateRepository}, {@link RocksDbRepository}, or
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